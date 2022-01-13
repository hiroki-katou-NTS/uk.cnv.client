package nts.uk.screen.com.app.cmf.cmf001.c.get;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.file.storage.FileStorage;
import nts.uk.ctx.exio.app.input.setting.FromCsvBaseSettingToDomainRequireImpl;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.importableitem.ImportableItemsRepository;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GetImportableItemAndConstraint {
	
	@Inject
	private ExternalImportSettingRepository settingRepo;
	
	@Inject
	private ImportableItemsRepository itemRepo;

	@Inject
	private FileStorage fileStorage;
	
	public ImportableItemDto get(String settingCode, ImportingDomainId domainId, int itemNo) {
		
		String companyId = AppContexts.user().companyId();
		val require = new FromCsvBaseSettingToDomainRequireImpl(fileStorage);
		val setting = settingRepo.get(Optional.of(require), companyId, new ExternalImportCode(settingCode)).get();
		
		val item = itemRepo.get(domainId, itemNo).get();
		
		return ImportableItemDto.of(item);
		
	}
}
