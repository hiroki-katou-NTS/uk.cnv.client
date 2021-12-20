package nts.uk.screen.com.app.cmf.cmf001.c.save;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.app.input.setting.FromCsvBaseSettingToDomainRequireImpl;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.ImportSettingBaseType;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class Cmf001cSaveCommandHandler extends CommandHandler<Cmf001cSaveCommand>{

	@Inject
	private ExternalImportSettingRepository settingRepo;
	
	@Inject
	private ImportableItemsRepository importableItemRepo;
	
	@Inject
	private ReviseItemRepository reviseItemRepo;

	@Inject
	private FileStorage fileStorage;
	
	@Override
	protected void handle(CommandHandlerContext<Cmf001cSaveCommand> context) {
		
		String companyId = AppContexts.user().companyId();
		val command = context.getCommand();

		val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		val setting = settingRepo.get(Optional.of(require), companyId, command.getExternalImportCode())
				.orElseThrow(() -> new RuntimeException("not found: " + command.getSettingCode()));
		
		updateSetting(companyId, command, setting);
		updateReviseItem(companyId, command, setting);
	}

	private void updateSetting(String companyId, Cmf001cSaveCommand command, ExternalImportSetting setting) {
		
		val mapping = setting.getAssembly(ImportingDomainId.valueOf(command.getDomainId())).getMapping();
		int itemNo = command.getItemNo();
		
		boolean withReset = (setting.getBaseType() != ImportSettingBaseType.CSV_BASE);
		if (command.isFixedValue()) {
			mapping.setFixedValue(itemNo, StringifiedValue.of(command.getFixedValue()), withReset);
		} else if (command.isCsvMapping()) {
			mapping.setCsvMapping(itemNo, withReset);
		} else {
			mapping.setNoSetting(itemNo, withReset);
		}
		
		settingRepo.update(setting);
	}

	private void updateReviseItem(String companyId, Cmf001cSaveCommand command, ExternalImportSetting setting) {

		val domainId = ImportingDomainId.valueOf(command.getDomainId());
		val importableItem = importableItemRepo.get(domainId, command.getItemNo())
				.orElseThrow(() -> new RuntimeException("not found: " + command.getSettingCode() + ", " + command.getItemNo()));

		val reviseItem = command.toDomainReviseItem(companyId, importableItem.getItemType());
		if (reviseItem.isPresent()) {
			reviseItemRepo.persist(reviseItem.get());
		}
		else {
			reviseItemRepo.delete(companyId,  new ExternalImportCode(command.getSettingCode()), domainId, command.getItemNo());
		}
	}
}
