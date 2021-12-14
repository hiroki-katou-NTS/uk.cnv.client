package nts.uk.ctx.at.request.dom.application.common.ovetimeholiday;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.OverTimeWorkHoursOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.AppDateContradictionAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.Time36AgreeCheckRegister;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.timeitem.BonusPayTimeItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

public interface CommonOvertimeHoliday {
	
	/**
	 * 01-01_休憩時間帯を取得する
	 * @param companyID 会社ID
	 * @param workTypeCode 勤務種類コード
	 * @param workTimeCode 就業時間帯コード
	 * @param opStartTime Optional＜開始時刻＞
	 * @param opEndTime Optional＜終了時刻＞
	 * @return
	 */
	public List<DeductionTime> getBreakTimes(String companyID, String workTypeCode, String workTimeCode, 
			Optional<TimeWithDayAttr> opStartTime, Optional<TimeWithDayAttr> opEndTime);
	
	/**
	 * 01-02_時間外労働を取得
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param appType 時間外表示区分
	 * @return
	 */
	public Optional<OverTimeWorkHoursOutput> getAgreementTime(
			String companyID,
			String employeeID,
			Time36AgreeCheckRegister extratimeExcessAtr,
			NotUseAtr extratimeDisplayAtr);

	/**
	 * 01-04_加給時間を取得
	 * @param companyID 会社ID
	 * @param employeeID 申請者
	 * @param appDate 申請日
	 * @param bonusTimeDisplayAtr 加給時間表示区分
	 * @return
	 */
	public List<BonusPayTimeItem> getBonusTime(String companyID, String employeeID, GeneralDate appDate, UseAtr bonusTimeDisplayAtr);
	
	/**
	 * 01-07_乖離理由を取得
	 * @param prePostAtr 事前事後区分
	 * @param divergenceReasonInputAtr 乖離理由入力区分
	 * @return
	 */
	public boolean displayDivergenceReasonInput(PrePostAtr prePostAtr, UseAtr divergenceReasonInputAtr);
	
	
	/**
	 * 01-17_休憩時間取得
	 * @param companyID 会社ID
	 * @param timeCalUse 時刻計算利用区分
	 * @param breakInputFieldDisp 休憩入力欄を表示する
	 * @param appType 申請種類
	 * @return
	 */
	public boolean getRestTime(String companyID, UseAtr timeCalUse, Boolean breakInputFieldDisp, ApplicationType appType);
	
	/**
	 * 03-08_申請日の矛盾チェック
	 * @param companyID 会社ID
	 * @param employeeID 申請者の社員ID
	 * @param appDate 申請日
	 * @param appType 申請種類
	 * @param appDateContradictionAtr 申請日矛盾区分
	 * @return
	 */
	public List<ConfirmMsgOutput> inconsistencyCheck(String companyID, String employeeID, GeneralDate appDate, ApplicationType appType, AppDateContradictionAtr appDateContradictionAtr);
	

	
	/**
	 * 
	 * @param 計算フラグ:CalculateFlg(0,1)
	 * @param companyID
	 * @param employeeID
	 * @param rootAtr
	 * @param targetApp
	 * @param appDate
	 */
	/**
	 * 03-06_計算ボタンチェック
	 * @param calculateFlg
	 * @param timeCalUse 時刻計算利用区分
	 */
	void calculateButtonCheck(CalculatedFlag calculateFlg, UseAtr timeCalUse);
	
	/**
	 * Refactor5 06_計算処理
	 * UKDesign.UniversalK.就業.KAF_申請.共通アルゴリズム(残業・休出).06_計算処理
	 * @param companyId
	 * @param employeeId
	 * @param date
	 * @param workTypeCode
	 * @param workTimeCode
	 * @param timeZones
	 * @param breakTimes
	 * @return 申請時間 List
	 */
	public List<ApplicationTime> calculator(
			String companyId,
			String employeeId,
			GeneralDate date,
			Optional<String> workTypeCode,
			Optional<String> workTimeCode,
			List<TimeZone> timeZones,
			List<BreakTimeSheet> breakTimes);
	/**
	 * 03-01-1_チェック条件
	 * @param prePostAtr 事前事後区分
	 * @param preExcessDisplaySetting 事前超過表示設定
	 * @return
	 */
	public UseAtr preAppSetCheck(PrePostAtr prePostAtr, UseAtr preExcessDisplaySetting);
	
	/**
	 * 03-02-1_チェック条件
	 * @param performanceExcessAtr 実績超過区分
	 * @param prePostAtr 事前事後区分
	 * @return
	 */
	public AppDateContradictionAtr actualSetCheck(AppDateContradictionAtr performanceExcessAtr, PrePostAtr prePostAtr);
	
	
	
}
