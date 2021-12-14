package nts.uk.ctx.at.request.app.command.application.common;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.service.application.IApplicationContentService;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.AfterProcessDelete;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.MailResult;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.shr.com.context.AppContexts;
@Stateless
@Transactional
public class DeleteAppHandler extends CommandHandlerWithResult<AppDetailBehaviorCmd, ProcessResult> {
	
	@Inject
	private AfterProcessDelete afterProcessDelete;
	
	@Inject
	private DetailBeforeUpdate beforeRegisterRepo;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject
	private IApplicationContentService applicationContentService;

	@Override
	protected ProcessResult handle(CommandHandlerContext<AppDetailBehaviorCmd> context) {
		String companyID = AppContexts.user().companyId();
		ProcessResult processResult = new ProcessResult();
		AppDetailBehaviorCmd cmd = context.getCommand();
		Application application = cmd.getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication().toDomain();
		AppDispInfoStartupOutput appDispInfoStartupOutput = cmd.getAppDispInfoStartupOutput().toDomain();
		String appID = application.getAppID();
		int version = application.getVersion();
		
		beforeRegisterRepo.exclusiveCheck(companyID, appID, version);
		String content = applicationContentService.getApplicationContent(application);
		// 共通アルゴリズム「詳細画面削除後の処理」を実行する(thực hiện xử lý 「詳細画面削除後の処理」)
		List<String> destinationLst = afterProcessDelete.screenAfterDelete(
				appID, 
				application, 
				appDispInfoStartupOutput, 
				cmd.getHdsubRecLinkData() != null ? Optional.of(cmd.getHdsubRecLinkData().toDomain()) : Optional.empty());
		processResult.setProcessDone(true);
		// IF文を参照
		AppTypeSetting appTypeSetting = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getApplicationSetting().getAppTypeSettings()
				.stream().filter(x -> x.getAppType() == application.getAppType()).findAny().get();
		boolean condition = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().isMailServerSet() && 
				appTypeSetting.isSendMailWhenRegister();
		if(condition) {
			processResult.setAutoSendMail(true);
			// 送信先リストに項目がいるかチェックする(kiểm tra danh sách người xác nhận có mục nào hay không)
			if(!CollectionUtil.isEmpty(destinationLst)){
				// 送信先リストにメールを送信する(gửi mail cho danh sách người xác nhận)
				MailResult mailResult = otherCommonAlgorithm.sendMailApproverDelete(destinationLst, application, content);
				processResult.setAutoSuccessMail(mailResult.getSuccessList());
				processResult.setAutoFailMail(mailResult.getFailList());
				processResult.setAutoFailServer(mailResult.getFailServerList());
			}
		}
		processResult.setAppIDLst(Arrays.asList(appID));
		return processResult;
	}
}
