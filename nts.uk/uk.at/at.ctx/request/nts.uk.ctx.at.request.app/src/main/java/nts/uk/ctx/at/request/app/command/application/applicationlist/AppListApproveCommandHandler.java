package nts.uk.ctx.at.request.app.command.application.applicationlist;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.task.AsyncTask;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.application.common.ApproveAppHandler;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ApprovalDevice;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplyActionContent;
import nts.uk.ctx.at.request.dom.application.applist.service.ApprovalListService;
import nts.uk.ctx.at.request.dom.application.applist.service.ListOfAppTypes;
import nts.uk.ctx.at.request.dom.application.applist.service.WorkMotionData;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.EnvAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.dto.MailServerSetImport;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.after.ApprovalProcessParam;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ApproveProcessResult;
import nts.uk.ctx.at.request.dom.applicationreflect.service.AppReflectManagerFromRecord;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDispSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.shr.com.context.AppContexts;

/**
 * refactor 4
 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧承認登録ver4.申請一覧承認登録ver4
 * @author Doan Duy Hung
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AppListApproveCommandHandler extends CommandHandlerWithResult<AppListApproveCommand, AppListApproveResult>{
	
	
	@Inject
	private ApproveAppHandler approveAppHandler;
	
	@Inject
	private ManagedParallelWithContext parallel;
	
	@Inject
	private ApprovalListService approvalListService;
	
	@Inject
	private ApprovalListDispSetRepository approvalListDispSetRepository;
	
	@Inject
	private AppReflectManagerFromRecord appReflectManager;
	
	@Resource
	private ManagedExecutorService executerService;
	
	@Inject
	private ApplicationSettingRepository applicationSettingRepository;
	
	@Inject
	private EnvAdapter envAdapter;
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧承認登録ver4.申請一覧承認登録ver4
	 */
	@Override
	protected AppListApproveResult handle(CommandHandlerContext<AppListApproveCommand> context) {
		String companyID = AppContexts.user().companyId();
		AppListApproveResult result = new AppListApproveResult(new HashMap<String, String>(), new HashMap<String, String>());
		AppListApproveCommand command = context.getCommand();
		List<ListOfApplicationCmd> listOfApplicationCmds = command.getListOfApplicationCmds();
		if(CollectionUtil.isEmpty(listOfApplicationCmds)) {
			return result;
		}
//		List<ListOfAppTypes> listOfAppTypes =  command.getListOfAppTypes().stream().map(x -> x.toDomain()).collect(Collectors.toList());
		// ドメインモデル「承認一覧表示設定」を取得する (Lấy domain Approval List display Setting)
		ApprovalListDisplaySetting approvalListDisplaySetting = approvalListDispSetRepository.findByCID(companyID).get();
		// 承認一覧表示設定より作業動作データをセット (set dữ liệu chạy công việc từ 承認一覧表示設定)
		WorkMotionData workMotionData = new WorkMotionData();
		if(approvalListDisplaySetting.getAdvanceExcessMessDisAtr()==DisplayAtr.DISPLAY) {
			workMotionData.setPreAppConfirm(ApplyActionContent.CONFIRM);
		} else {
			workMotionData.setPreAppConfirm(ApplyActionContent.NOT_CONFIRM);
		}
		if(approvalListDisplaySetting.getActualExcessMessDisAtr()==DisplayAtr.DISPLAY) {
			workMotionData.setPreAppConfirm(ApplyActionContent.CONFIRM);
		} else {
			workMotionData.setPreAppConfirm(ApplyActionContent.NOT_CONFIRM);
		}
		// 表示されている申請一覧リストの1行目より最終行まで繰返し (Vong lap tu don day tien den don cuoi cung dang hien thi tren man hinh)
		listOfApplicationCmds.forEach(listOfApplicationCmd -> {
			// 対象の申請が未承認の申請の場合
			// xử lý trên UI
			// INPUT「一括承認」＝True
			// xử lý trên UI
			// デバイス＝スマホ
			if(command.getDevice()==ApprovalDevice.PC.value) {
				// アルゴリズム「申請一覧承認登録チェックver4」を実行する　-　15
				boolean error = approvalListService.checkErrorComfirm(
						approvalListDisplaySetting, 
						workMotionData, 
						EnumAdaptor.valueOf(listOfApplicationCmd.getAppType(), ApplicationType.class));
				if(error) {
					result.getSuccessMap().put(listOfApplicationCmd.getAppID(), "");
				} else {
					result.getSuccessMap().put(listOfApplicationCmd.getAppID(), "");
				}
				return;
			}
			// アルゴリズム「申請一覧の承認登録」を実行する
//			Pair<Boolean, String> pair = this.approveSingleApp(companyID, listOfApplicationCmd, Collections.emptyList());
//			if(pair.getLeft()) {
//				result.getSuccessMap().put(listOfApplicationCmd.getAppID(), pair.getRight());
//			} else {
//				result.getFailMap().put(listOfApplicationCmd.getAppID(), pair.getRight());
//			}
			result.getSuccessMap().put(listOfApplicationCmd.getAppID(), "");
		});
		return result;
	}
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧の承認登録.申請一覧の承認登録
	 * @param companyID
	 * @param listOfApplicationCmd
	 * @return
	 */
	public Pair<Boolean, String> approveSingleApp(String companyID, ListOfApplicationCmd listOfApplicationCmd, List<ListOfAppTypes> listOfAppTypes) {
		try {
			Application application = listOfApplicationCmd.toDomain().getApplication();
			// ドメインモデル「申請設定」を取得する
			ApplicationSetting applicationSetting = applicationSettingRepository.findByCompanyId(companyID).get();
			// アルゴリズム「メールサーバを設定したかチェックする」を実行する
			MailServerSetImport mailServerSetImport = envAdapter.checkMailServerSet(companyID);
			// 「承認時の設定パラメータ」を作成する
			ApprovalProcessParam approvalProcessParam = new ApprovalProcessParam(
					mailServerSetImport.isMailServerSet(),
					applicationSetting.getAppTypeSettings().stream().filter(x -> x.getAppType()==application.getAppType()).findAny().orElse(null));
			// アルゴリズム「承認する」を実行する
			ApproveProcessResult approveProcessResult = approveAppHandler.approve(companyID, application.getAppID(), application, approvalProcessParam, 
					"", listOfAppTypes, true);
			if(approveProcessResult.isProcessDone()) {
				return Pair.of(true, "");
			} else {
				return Pair.of(false, "");
			}
		} catch (Exception e) {
			return Pair.of(false, e.getMessage());
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public AppListApproveResult approverAfterConfirm(List<ListOfApplicationCmd> listOfApplicationCmds, List<ListOfAppTypes> listOfAppTypes) {
		String companyID = AppContexts.user().companyId();
		AppListApproveResult result = new AppListApproveResult(new HashMap<String, String>(), new HashMap<String, String>());
		if(CollectionUtil.isEmpty(listOfApplicationCmds)) {
			return result;
		}
		listOfApplicationCmds.forEach(listOfApplicationCmd -> {
			Pair<Boolean, String> pair = this.approveSingleApp(companyID, listOfApplicationCmd, listOfAppTypes);
			if(pair.getLeft()) {
				result.getSuccessMap().put(listOfApplicationCmd.getAppID(), pair.getRight());
			} else {
				result.getFailMap().put(listOfApplicationCmd.getAppID(), pair.getRight());
			}
		});
		
		// reflect app
		AsyncTask task = AsyncTask.builder().keepsTrack(false).threadName(this.getClass().getName() + ".reflect-app: ")
				.build(() -> {
					appReflectManager
							.reflectApplication(result.getSuccessMap().keySet().stream().collect(Collectors.toList()));
				});
		this.executerService.submit(task);
		return result;
	}

}
