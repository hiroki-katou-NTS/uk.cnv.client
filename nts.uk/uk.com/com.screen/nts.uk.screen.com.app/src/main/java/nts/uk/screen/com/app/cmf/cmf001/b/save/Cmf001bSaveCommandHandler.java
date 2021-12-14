package nts.uk.screen.com.app.cmf.cmf001.b.save;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportCode;
import nts.uk.ctx.exio.dom.input.setting.ExternalImportSettingRepository;
import nts.uk.ctx.exio.dom.input.setting.assembly.revise.ReviseItemRepository;
import nts.uk.screen.com.app.cmf.cmf001.b.get.ExternalImportSettingDto;
import nts.uk.shr.com.context.AppContexts;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class Cmf001bSaveCommandHandler extends CommandHandler<Cmf001bSaveCommand>{

	@Inject
	private ExternalImportSettingRepository externalImportSettingRepo;
	
	@Inject
	private ReviseItemRepository reviseItemRepo;

	@Override
	protected void handle(CommandHandlerContext<Cmf001bSaveCommand> context) {

		val command = context.getCommand();
		String companyId = AppContexts.user().companyId();

		//新規モード
		if(command.isNew()) {
			//コードの重複チェック
			if(externalImportSettingRepo.exist(companyId, command.getCode())) {
				throw new BusinessException("コードが存在しています。");
			}
			val domain = command.toDomain();
			externalImportSettingRepo.insert(domain);
		} else {
			
			val setting = externalImportSettingRepo.get(companyId, command.getCode()).get();
			
			val require = createRequire(companyId);
			command.getSetting().merge(require, setting);
			externalImportSettingRepo.update(setting);
		}
	}

	private ExternalImportSettingDto.RequireMerge createRequire(String companyId) {
		
		return new ExternalImportSettingDto.RequireMerge() {
			
			@Override
			public void deleteReviseItems(ExternalImportCode settingCode) {
				reviseItemRepo.delete(companyId, settingCode);
			}
			
			@Override
			public void deleteReviseItems(ExternalImportCode settingCode, List<Integer> itemNos) {
				reviseItemRepo.delete(companyId, settingCode, itemNos);
			}
		};
	}

}
