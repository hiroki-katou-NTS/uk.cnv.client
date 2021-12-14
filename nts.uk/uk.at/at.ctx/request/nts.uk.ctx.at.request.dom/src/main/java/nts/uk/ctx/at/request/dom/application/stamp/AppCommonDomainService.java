package nts.uk.ctx.at.request.dom.application.stamp;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.StampRecordOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.stamp.output.AppStampOutput;
import nts.uk.ctx.at.request.dom.application.stamp.output.ErrorStampInfo;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.stampapplication.StampAppReflect;

public interface AppCommonDomainService {
	
	/**
	 * Refactor4
	 * 打刻申請（新規）起動前処理
	 *UKDesign.UniversalK.就業.KAF_申請.KAF002_打刻申請.B：打刻申請（新規）→A・B画面.打刻申請（新規）共通アルゴリズム.打刻申請（新規）起動前処理
	 * @param companyId
	 * @param dates
	 * @param appDispInfoStartupOutput
	 * @param recoderFlag
	 * @return 打刻申請起動時の表示情報打刻申請（新規）起動前処理 
	 */
	public AppStampOutput getDataCommon(String companyId, Optional<GeneralDate> dates, AppDispInfoStartupOutput appDispInfoStartupOutput, Boolean recoderFlag);
	
	/**Refactor4
	 * 打刻申請の設定チェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF002_打刻申請.B：打刻申請（新規）→A・B画面.打刻申請（新規）共通アルゴリズム.打刻申請の設定チェック
	 * @param appStampReflect
	 * @param temporaryWorkUseManage
	 */
	public void checkAppStampSetting(StampAppReflect appStampReflect, Boolean temporaryWorkUseManage);
	
	/**Refactor4
	 * 実績の打刻のチェック
	 * @param stampRecordOutput
	 * @return 打刻エラー情報
	 */
	public List<ErrorStampInfo> getErrorStampList(StampRecordOutput stampRecordOutput, Optional<String> workTypeCd);
	
	/**
	 * Refactor4
	 * 打刻申請登録前のエラーチェック処理（新規）
	 * @param companyId
	 * @param agentAtr
	 * @param application
	 * @param appStampOutput
	 * @return
	 */
	public List<ConfirmMsgOutput> checkBeforeRegister(String companyId, Boolean agentAtr, Application application, AppStampOutput appStampOutput);
	
	/**
	 * Refactor4
	 * 打刻申請登録前のエラーチェック処理（詳細）
	 * @param companyId
	 * @param agentAtr
	 * @param application
	 * @param appStampOutput
	 * @return
	 */
	public List<ConfirmMsgOutput> checkBeforeUpdate(String companyId, Boolean agentAtr, Application application, AppStampOutput appStampOutput);
	
	
	/**Refactor4
	 * 打刻申請（詳細）起動前処理
	 * @param companyId
	 * @param appId
	 * @param appDispInfoStartupOutput
	 * @return 打刻申請起動時の表示情報打刻申請（新規）起動前処理 
	 */
	public AppStampOutput getDataDetailCommon(String companyId, String appId, AppDispInfoStartupOutput appDispInfoStartupOutput, Boolean recoderFlag);
	
}
