package nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm;

import java.util.List;
import java.util.Optional;


import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.overtime.*;
import nts.uk.ctx.at.request.dom.application.overtime.service.DisplayInfoOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.OverTimeContent;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkHours;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.ApplicationDetailSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.AtWorkAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.workrecord.dailyrecordprocess.dailycreationwork.BreakTimeZoneSetting;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.time.TimeWithDayAttr;


public interface ICommonAlgorithmOverTime {
	/**
	 * Refactor5 指定社員の申請残業枠を取得する
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.設定.会社別.申請承認設定.残業申請設定.アルゴリズム.指定社員の申請残業枠を取得する
	 * @param companyId
	 * @param employeeId
	 * @param date
	 * @param overTimeAtr
	 * @return 利用する残業枠
	 */
	public QuotaOuput getOvertimeQuotaSetUse(
			String companyId,
			String employeeId,
			GeneralDate date,
			OvertimeAppAtr overTimeAtr,
			OvertimeAppSet overtimeAppSet);
	/**
	 * Refactor5
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.07_勤務種類取得
	 * @param appEmploymentSetting
	 * @return 勤務種類(List)
	 */
	public List<WorkType> getWorkType(Optional<AppEmploymentSet> appEmploymentSetting);
	/**
	 * Refactor5  基準日に関する情報を取得する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.基準日に関する情報を取得する
	 * @param companyId
	 * @param employeeId
	 * @param date
	 * @param overTimeAtr
	 * @param workTime
	 * @param appEmploymentSetting
	 * @return 基準日に関する情報
	 */
	public InfoBaseDateOutput getInfoBaseDate(
			String companyId,
			String employeeId,
			GeneralDate date,
			OvertimeAppAtr overTimeAtr,
			List<WorkTimeSetting> workTime,
			Optional<AppEmploymentSet> appEmploymentSetting,
			OvertimeAppSet overtimeAppSet
			);
	/**
	 * Refactor5 申請日に関する情報を取得する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.申請日に関する情報を取得する
	 * @param companyId
	 * @param dateOp
	 * @param startTimeSPR
	 * @param endTimeSPR
	 * @param workTypeLst
	 * @param appDispInfoStartupOutput
	 * @param overtimeAppSet
	 */
	public InfoWithDateApplication getInfoAppDate(
			String companyId,
			Optional<GeneralDate> dateOp,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			List<WorkType> workTypeLst,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			OvertimeAppSet overtimeAppSet
			);
	/**
	 * Refactor5 利用する乖離理由を取得する
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.設定.会社別.申請承認設定.残業休出申請共通設定.アルゴリズム.利用する乖離理由のを取得する
	 * @param companyId 会社ID
	 * @param appType 申請種類
	 * @param ovetTimeAtr 残業申請区分<Optional>
	 */
	public ReasonDissociationOutput getInfoNoBaseDate(
			String companyId,
			ApplicationType appType,
			Optional<OvertimeAppAtr> ovetTimeAtr,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet);
	
	/**
	 * Refactor5  基準日に関係ない情報を取得する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.基準日に関係ない情報を取得する
	 * @param companyId
	 * @param employeeId
	 * @param overtimeAppAtr
	 * @return 基準日に関係しない情報
	 */
	public InfoNoBaseDate getInfoNoBaseDate(
			String companyId,
			String employeeId,
			OvertimeAppAtr overtimeAppAtr);
	
	/**
	 * Refactor5 休憩時間帯を取得する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.休憩時間帯を取得する
	 * @param companyId
	 * @param workTypeCode
	 * @param workTimeCode
	 * @param startTime
	 * @param endTime
	 * @param achievementDetail
	 * @return 休憩時間帯設定
	 */
	public BreakTimeZoneSetting selectWorkTypeAndTime(
			String companyId,
			WorkTypeCode workTypeCode,
			WorkTimeCode workTimeCode,
			Optional<TimeWithDayAttr> startTime,
			Optional<TimeWithDayAttr> endTime,
			Optional<AchievementDetail> achievementDetail
			);
	/**
	 * Refactor5 勤務時間外の休憩時間を除く
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.勤務時間外の休憩時間を除く
	 * @param startTime
	 * @param endTime
	 * @param breakTimeZoneSetting
	 * @return
	 */
	public BreakTimeZoneSetting createBreakTime(
			Optional<TimeWithDayAttr> startTime,
			Optional<TimeWithDayAttr> endTime,
			BreakTimeZoneSetting breakTimeZoneSetting
			);
	/**
	 * Refactor5 初期表示する出退勤時刻を取得する
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.設定.会社別.申請承認設定.残業休出申請共通設定.アルゴリズム.初期表示する出退勤時刻を取得する
	 * @param companyId
	 * @param dateOp
	 * @param overTimeContent
	 * @return
	 */
	public Optional<WorkHours> initAttendanceTime(
			String companyId,
			Optional<GeneralDate> dateOp,
			OverTimeContent overTimeContent,
			ApplicationDetailSetting applicationDetailSetting);
	/**
	 * Refactor5 勤務時間を取得する
	 * @param companyId
	 * @param overTimeContent
	 * @param atworkTimeBeginDisp
	 * @return
	 */
	public Optional<WorkHours> getWorkHours(
			String companyId,
			OverTimeContent overTimeContent,
			AtWorkAtr atworkTimeBeginDisp,
			ApplicationDetailSetting applicationDetailSetting
			);
	/**
	 * Refactor5 申請する残業時間をチェックする
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.申請する残業時間をチェックする
	 * @param applicationTime
	 * @return
	 */
	public void checkOverTime(ApplicationTime applicationTime);
	
	/**
	 * Refactor5 事前申請・実績超過チェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.事前申請・実績超過チェック
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @return
	 */
	public List<ConfirmMsgOutput> checkExcess(AppOverTime appOverTime, DisplayInfoOverTime displayInfoOverTime);
	/**
	 * Refactor5 ３６上限チェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.３６上限チェック
	 * @param companyId
	 * @param appOverTime
	 * @param isProxy
	 * @param mode
	 * @return
	 */
	public void check36Limit(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime
			);
	/**
	 * Refactor5 申請日の矛盾チェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.申請日の矛盾チェック
	 * @param companyId
	 * @param displayInfoOverTime
	 * @param appOverTime
	 * @param mode
	 */
	public void commonAlgorithmAB(
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime,
			Integer mode
			);
	/**
	 * Refactor5 残業申請の個別登録前チェッ処理
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.AB画面の共通アルゴリズム.残業申請の個別登録前チェッ処理
	 * @param require
	 * @param companyId
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @param mode
	 * @return
	 */
	public CheckBeforeOutput checkBeforeOverTime(
			Boolean require,
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime,
			Integer mode
			);
	/**
	 * Refactor5 申請日変更時処理
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.申請日変更時処理.申請日変更時処理
	 * @param companyId
	 * @param date
	 * @param displayInfoOverTime
	 */
	public DisplayInfoOverTime changeApplicationDate(
			String companyId,
			GeneralDate date,
			DisplayInfoOverTime displayInfoOverTime);
}
