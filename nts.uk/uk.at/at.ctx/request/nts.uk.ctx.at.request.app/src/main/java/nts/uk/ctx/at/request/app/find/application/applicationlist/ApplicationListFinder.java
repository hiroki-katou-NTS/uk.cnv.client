package nts.uk.ctx.at.request.app.find.application.applicationlist;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.dom.application.Application_New;
import nts.uk.ctx.at.request.dom.application.applicationlist.extractcondition.AppListExtractCondition;
import nts.uk.ctx.at.request.dom.application.applicationlist.service.ApplicationListInitialRepository;
import nts.uk.ctx.at.request.dom.setting.company.request.RequestSetting;
import nts.uk.ctx.at.request.dom.setting.company.request.RequestSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.request.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.shr.com.context.AppContexts;

/**
 * 11 - 申請一覧初期処理
 * @author hoatt
 *
 */
@Stateless
public class ApplicationListFinder {

	@Inject
	private ApplicationListInitialRepository repoAppListInit;
	@Inject
	private RequestSettingRepository repoRequestSet;
	
	public ApplicationListDto getAppList(AppListExtractConditionDto param){
		String companyId = AppContexts.user().companyId();
		//ドメインモデル「承認一覧表示設定」を取得する-(Lấy domain Approval List display Setting)
		Optional<RequestSetting> requestSet = repoRequestSet.findByCompany(companyId);
		ApprovalListDisplaySetting appDisplaySet = null;
		if(requestSet.isPresent()){
			appDisplaySet = requestSet.get().getApprovalListDisplaySetting();
		}
		//URパラメータが存在する-(Check param)
		if(param == null){//存在する場合
			// TODO Auto-generated method stub
			
		}else{//存在しない場合
			// TODO Auto-generated method stub
		}
		//ドメインモデル「申請一覧共通設定フォーマット.表の列幅」を取得-(Lấy 表の列幅)//xu ly o ui
		//アルゴリズム「申請一覧リスト取得」を実行する-(Thực hiện thuật toán Application List get): 1-申請一覧リスト取得
		AppListExtractCondition appListExCon = param.convertDtotoDomain(param);
		List<Application_New> lstApp = repoAppListInit.getApplicationList(appListExCon, appDisplaySet);
		return new ApplicationListDto();
	}
}
