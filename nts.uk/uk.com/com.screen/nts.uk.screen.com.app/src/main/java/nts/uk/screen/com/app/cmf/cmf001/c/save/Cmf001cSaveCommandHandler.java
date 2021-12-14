package nts.uk.screen.com.app.cmf.cmf001.c.save;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSetting;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
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
	
	@Override
	protected void handle(CommandHandlerContext<Cmf001cSaveCommand> context) {
		
		String companyId = AppContexts.user().companyId();
		val command = context.getCommand();
		
		val setting = settingRepo.get(companyId, command.getExternalImportCode())
				.orElseThrow(() -> new RuntimeException("not found: " + command.getSettingCode()));
		
		updateSetting(companyId, command, setting);
		updateReviseItem(companyId, command, setting);
	}

	private void updateSetting(String companyId, Cmf001cSaveCommand command, ExternalImportSetting setting) {
		
		val mapping = setting.getAssembly().getMapping();
		int itemNo = command.getItemNo();
		
		if (command.isFixedValue()) {
			mapping.setFixedValue(itemNo, StringifiedValue.of(command.getFixedValue()));
		} else if (command.isCsvMapping()) {
			mapping.setCsvMapping(itemNo);
		} else {
			mapping.setNoSetting(itemNo);
		}
		
		settingRepo.update(setting);
	}

	private void updateReviseItem(String companyId, Cmf001cSaveCommand command, ExternalImportSetting setting) {

		val importableItem = importableItemRepo.get(setting.getExternalImportDomainId(), command.getItemNo())
				.orElseThrow(() -> new RuntimeException("not found: " + command.getSettingCode() + ", " + command.getItemNo()));

		command.toDomainReviseItem(companyId, importableItem.getItemType())
			.ifPresent(reviseItem -> {
				reviseItemRepo.persist(reviseItem);
			});
	}
}
