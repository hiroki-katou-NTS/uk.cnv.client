package nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;

/**
 * 
 * 4-1.詳細画面登録前の処理
 *
 */
public interface DetailBeforeUpdate {
	/**
	 * 4-1.詳細画面登録前の処理
	 * @param companyID
	 * @param employeeID
	 * @param appDate
	 * @param employeeRouteAtr
	 * @param appID
	 * @param postAtr
	 * @param version
	 * @param wkTypeCD
	 * @param wkTimeCd
	 * @param appDispInfoStartupOutput
	 */
	public void processBeforeDetailScreenRegistration(String companyID, String employeeID, GeneralDate appDate, int employeeRouteAtr, String appID, 
			PrePostAtr postAtr, int version,String wkTypeCD, String wkTimeCd, AppDispInfoStartupOutput appDispInfoStartupOutput, List<String> workTypeCds, 
            Optional<TimeDigestionParam> timeDigestionUsageInfo, boolean flag, Optional<String> workTypeRemainChk, Optional<String> workTimeRemainChk);
	
	/**
	 * 1.排他チェック
	 */
	public void exclusiveCheck(String companyID, String appID, int version);
	/**
	 * 4-1.詳細画面登録前の処理 (CMM045)
	 * @param companyID 会社ID 
	 * @param employeeID 社員ID（申請本人の社員ID）
	 * @param appDate 申請対象日
	 * @param employeeRouteAtr 就業ルート区分
	 * @param targetApp 対象申請
	 * @param postAtr 事前事後区分
	 */
	public boolean processBefDetailScreenReg(String companyID, String employeeID, GeneralDate appDate, int employeeRouteAtr, String appID, 
			PrePostAtr postAtr, int version, AppDispInfoStartupOutput appDispInfoStartupOutput);
	/**
	 * 1.排他チェック (CMM045)
	 * @param companyID
	 * @param appID
	 * @param version
	 * @return
	 */
	public boolean exclusiveCheckErr(String companyID, String appID, int version);

	/**
	 * 勤務種類、就業時間帯チェックのメッセージを表示
	 * 
	 * @param companyID
	 * @param wkTypeCode
	 * @param wkTimeCode
	 */
	
	void displayWorkingHourCheck(String companyID, String wkTypeCode, String wkTimeCode);
}

