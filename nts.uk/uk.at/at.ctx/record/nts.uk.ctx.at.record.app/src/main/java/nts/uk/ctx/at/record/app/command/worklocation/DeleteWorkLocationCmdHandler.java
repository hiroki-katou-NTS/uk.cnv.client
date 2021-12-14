package nts.uk.ctx.at.record.app.command.worklocation;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocation;
import nts.uk.ctx.at.record.dom.stampmanagement.workplace.WorkLocationRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 勤務場所を削除する
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM040_勤務場所の登録.A：勤務場所の登録.メニュー別OCD.勤務場所を削除する
 * @author tutk
 *
 */
@Stateless
public class DeleteWorkLocationCmdHandler  extends CommandHandler<String>{

	@Inject
	private WorkLocationRepository repo;
	
	@Override
	protected void handle(CommandHandlerContext<String> context) {
		String workLocationCode = context.getCommand();
		String contractCode = AppContexts.user().contractCode();
		//1:
		Optional<WorkLocation> optWorkLocation = repo.findByCode(contractCode, workLocationCode);
		//2:
		if(!optWorkLocation.isPresent()) {
			//2.2:
			throw new BusinessException("Msg_1969");
		}
		//3:
		repo.deleteByWorkLocationCd(contractCode, workLocationCode, AppContexts.user().companyId());
		repo.deleteWorkLocation(contractCode, workLocationCode);
	}

}
