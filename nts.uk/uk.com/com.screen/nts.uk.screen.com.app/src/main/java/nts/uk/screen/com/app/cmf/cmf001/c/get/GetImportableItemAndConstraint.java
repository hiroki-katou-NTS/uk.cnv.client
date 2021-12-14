package nts.uk.screen.com.app.cmf.cmf001.c.get;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
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
	
	public ImportableItemDto get(String settingCode, int itemNo) {
		
		String companyId = AppContexts.user().companyId();
		val setting = settingRepo.get(companyId, new ExternalImportCode(settingCode)).get();
		
		val item = itemRepo.get(setting.getExternalImportDomainId(), itemNo).get();
		
		return ImportableItemDto.of(item);
		
	}
}
