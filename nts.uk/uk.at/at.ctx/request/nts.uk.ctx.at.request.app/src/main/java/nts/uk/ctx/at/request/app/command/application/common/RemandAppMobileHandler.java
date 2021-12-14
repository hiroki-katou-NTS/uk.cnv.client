package nts.uk.ctx.at.request.app.command.application.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.DetailAfterRemand;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.RemandCommand;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.MailResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRec;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRecRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class RemandAppMobileHandler extends CommandHandlerWithResult<RemandCommand, ProcessResult> {
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Inject
	private DetailAfterRemand detailAfterRemand;
	
	@Inject
	private AppHdsubRecRepository appHdsubRecRepository;
	
	@Override
	protected ProcessResult handle(CommandHandlerContext<RemandCommand> context) {
		String companyID =  AppContexts.user().companyId();
		RemandCommand remandCommand = context.getCommand();
		List<String> lstAppID = remandCommand.getAppID();
		Optional<AppHdsubRec> opAppHdsubRec = appHdsubRecRepository.findByAppId(remandCommand.getAppID().stream().findFirst().orElse(""));
		if(opAppHdsubRec.isPresent()) {
			lstAppID = new ArrayList<>();
			lstAppID.add(opAppHdsubRec.get().getRecAppID());
			lstAppID.add(opAppHdsubRec.get().getAbsenceLeaveAppID());
			remandCommand.setAppID(lstAppID);
		}
		for (String appId : lstAppID) {
			//共通アルゴリズム「詳細画面差し戻し前の処理」を実行する-(THực hiện xử lý màn hình chi tiết trước khi refer back )
			// 11-1.詳細画面差し戻し前の処理
			detailBeforeUpdate.exclusiveCheck(companyID, appId, remandCommand.getVersion());
		}
		//共通アルゴリズム「詳細画面差し戻し後の処理」を実行する(xử lý màn hình chi tiết sau khi refer back)
		//11-2.詳細画面差し戻し後の処理
		MailResult mailResult = detailAfterRemand.doRemand(companyID, remandCommand);
		ProcessResult processResult = new ProcessResult();
		processResult.setAutoSuccessMail(mailResult.getSuccessList());
		processResult.setAutoFailMail(mailResult.getFailList());
		processResult.setAutoFailServer(mailResult.getFailServerList());
		return processResult;
	}
}
