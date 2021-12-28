package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutputMulti;
import nts.uk.ctx.at.request.dom.application.overtime.*;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.CheckBeforeOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSet;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import org.apache.commons.lang3.tuple.Pair;

public interface OvertimeService {
	/**
	 * 02_残業区分チェック 
	 * @param url
	 * @return
	 */
	public int checkOvertimeAtr(String url);
	
	
	
	/**
	 * Refactor5 計算を実行する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.ユースケース
	 * @param companyId
	 * @param employeeId
	 * @param dateOp
	 * @param overtimeAppAtr
	 * @param overtimeLeaveAppCommonSet
	 * @param advanceApplicationTime
	 * @param achieveApplicationTime
	 * @param workContent
	 * @param agent
	 * @return 残業申請の表示情報
	 */
	DisplayInfoOverTime calculate(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			OvertimeAppAtr overtimeAppAtr,
			PrePostInitAtr prePostInitAtr,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet,
			ApplicationTime advanceApplicationTime, 
			ApplicationTime achieveApplicationTime,
			WorkContent workContent,
			OvertimeAppSet overtimeAppSet,
			Boolean agent,
			List<OvertimeHour> overtimeHours,
			List<OvertimeReason> overtimeReasons,
			Optional<AchievementDetail> opAchievementDetail
	);
	/**
	 * Refactor5 19_計算処理
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.19_計算処理
	 * @param companyId
	 * @param employeeId
	 * @param dateOp
	 * @param overtimeAppAtr
	 * @param overtimeLeaveAppCommonSet
	 * @param advanceApplicationTime
	 * @param achieveApplicationTime
	 * @param workContent
	 * @return
	 */
	public CaculationOutput getCalculation(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp, 
			PrePostInitAtr prePostInitAtr,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet,
			ApplicationTime advanceApplicationTime, 
			ApplicationTime achieveApplicationTime,
			WorkContent workContent,
			OvertimeAppSet overtimeAppSet,
			Boolean agent);
	
	/**
	 * Refactor5 01_初期起動の処理
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.01_初期起動の処理
	 * @param companyId
	 * @param employeeId
	 * @param overtimeAppAtr
	 * @return
	 */
	public DisplayInfoOverTime getInitData(
			String companyId,
			Optional<GeneralDate> dateOp,
			OvertimeAppAtr overtimeAppAtr,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			Boolean agent
			);
	
	/**
	 * Refactor5 16_勤務種類・就業時間帯を選択する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.16_勤務種類・就業時間帯を選択する
	 * @param companyId
	 * @param employeeId
	 * @param dateOp
	 * @param workTypeCode
	 * @param workTimeCode
	 * @param startTimeSPR
	 * @param endTimeSPR
	 * @param actualContentDisplay
	 * @param overtimeAppSet
	 * @return
	 */
	public SelectWorkOutput selectWork(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			WorkTypeCode workTypeCode,
			WorkTimeCode workTimeCode,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			Optional<ActualContentDisplay> actualContentDisplay,
			OvertimeAppSet overtimeAppSet
			);
	
	
	/**
	 * Refactor5 申請時の乖離時間をチェックする
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.設定.会社別.申請承認設定.残業休出申請共通設定.申請時の乖離時間をチェックする
	 * @param require
	 * @param appType
	 * @param appOverOptional
	 * @param appHolidayOptional
	 */
	public void checkDivergenceTime(
			Boolean require,
			ApplicationType appType,
			Optional<AppOverTime> appOverOptional,
			Optional<AppHolidayWork> appHolidayOptional,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet
			);
	/**
	 * Refactor5 03_登録前エラーチェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.03_登録前エラーチェック
	 * @param require
	 * @param companyId
	 * @param displayInfoOverTime
	 * @param appOverTime
	 * @return
	 */
	public CheckBeforeOutput checkErrorRegister(
			Boolean require,
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime
			);
	/**
	 * Refactor5 登録前のエラーチェック(複数人版)
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.登録前のエラーチェック(複数人版)
	 * @param require
	 * @param companyId
	 * @param displayInfoOverTime
	 * @param appOverTime
	 * @return
	 */
	public CheckBeforeOutputMulti checkErrorRegisterMultiple(
			Boolean require,
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime
			);
	
	/**
	 * Refactor5 01_詳細画面起動の処理
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.B：残業申請（詳細・照会）.アルゴリズム.01_詳細画面起動の処理
	 * @param companyId
	 * @param appId
	 * @param appDispInfoStartupOutput
	 * @return
	 */
	public DetailOutput getDetailData(
			String companyId,
			String appId,
			AppDispInfoStartupOutput appDispInfoStartupOutput);
	/**
	 * Refactor5
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.B：残業申請（詳細・照会）.ユースケース
	 * @param companyId
	 * @param appId
	 * @param appDispInfoStartupOutput
	 * @return
	 */
	public DetailOutput initDetailScreen(
			String companyId,
			String appId,
			AppDispInfoStartupOutput appDispInfoStartupOutput);
	/**
	 * Refactor5 15_詳細画面の登録前エラーチェック
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.B：残業申請（詳細・照会）.アルゴリズム.15_詳細画面の登録前エラーチェック
	 * @param require
	 * @param companyId
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @return
	 */
	public CheckBeforeOutput checkBeforeUpdate(
			Boolean require,
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime);
	/**
	 * Refactor5 
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.ユースケース
	 * @param companyId
	 * @param dateOp
	 * @param overtimeAppAtr
	 * @param appDispInfoStartupOutput
	 * @param startTimeSPR
	 * @param endTimeSPR
	 * @param isProxy
	 * @return
	 */
	public DisplayInfoOverTime startA(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			OvertimeAppAtr overtimeAppAtr,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			Boolean agent
			);
	/**
	 * Refactor5 UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.ユースケース
	 * @param companyId
	 * @param employeeId
	 * @param dateOp
	 * @param startTimeSPR
	 * @param endTimeSPR
	 * @param appDispInfoStartupOutput
	 * @param overtimeAppSet
	 * @return
	 */
	public DisplayInfoOverTime selectWorkInfo(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			WorkTypeCode workTypeCode,
			WorkTimeCode workTimeCode,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			OvertimeAppSet overtimeAppSet,
			PrePostInitAtr prePost,
			Boolean agent
			);
	

	/**
	 * Refactor5 UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.ユースケース
	 * @param companyId
	 * @param employeeId
	 * @param dateOp
	 * @param overtimeAppAtr
	 * @param appDispInfoStartupOutput
	 * @param startTimeSPR
	 * @param endTimeSPR
	 * @param overtimeAppSet
	 * @param worktypes
	 * @param prePost
	 * @param displayInfoOverTime
	 * @return
	 */
	public DisplayInfoOverTime changeDate(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			OvertimeAppAtr overtimeAppAtr,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			OvertimeAppSet overtimeAppSet,
			List<WorkType> worktypes,
			PrePostInitAtr prePost,
			DisplayInfoOverTime displayInfoOverTime,
			Boolean agent
			);
	
	// Mobile //
	/**
	 * Refactor5 
	 * @param mode
	 * @param companyId
	 * @param employeeIdOptional
	 * @param dateOptional
	 * @param disOptional
	 * @param appOptional
	 * @param appDispInfoStartupOutput
	 * @param overtimeAppAtr
	 * @return
	 */
	public DisplayInfoOverTimeMobile startMobile(
			Boolean mode,
			String companyId,
			Optional<String> employeeIdOptional,
			Optional<GeneralDate> dateOptional,
			Optional<DisplayInfoOverTime> disOptional,
			Optional<AppOverTime> appOptional,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			OvertimeAppAtr overtimeAppAtr,
			Boolean agent);
	/**
	 * Refactor5 UKDesign.UniversalK.就業.KAF_申請.KAFS05_残業申請(スマホ).A：残業申請(新規).アルゴリズム.申請日を変更する
	 * 申請日を変更する
	 * @param companyId
	 * @param date
	 * @param displayInfoOverTime
	 * @return
	 */
	public DisplayInfoOverTime changeDateMobile(
			String companyId,
			GeneralDate date,
			DisplayInfoOverTime displayInfoOverTime
			);
	/**
	 * Refactor5 UKDesign.UniversalK.就業.KAF_申請.KAFS05_残業申請(スマホ).A：残業申請(新規).アルゴリズム.申請時間の申請内容をチェックする
	 * 申請時間の申請内容をチェックする
	 * @param require
	 * @param companyId
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @return
	 */
	public List<ConfirmMsgOutput> checkBeforeInsert(
			Boolean require,
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime
			);
	/**
	 * Refactor5 申請時間に移動する前の個別チェック処理
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS05_残業申請(スマホ).A：残業申請(新規).アルゴリズム.申請時間に移動する前の個別チェック処理
	 * @param companyId
	 * @param mode
	 * @param displayInfoOverTime
	 * @param appOverTime
	 */
	public void checkBeforeMovetoAppTime(
			String companyId,
			Boolean mode,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime);

	/**
	 * Refactor5 勤務情報の申請内容をチェックする
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS05_残業申請(スマホ).A：残業申請(新規).アルゴリズム.勤務情報の申請内容をチェックする
	 * @param companyId
	 * @param displayInfoOverTime
	 * @param appOverTime
	 * @param mode
	 */
	public void checkContentApp(
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime,
			Boolean mode
			);
	/**
	 * Refactor5
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS05_残業申請(スマホ).A：残業申請(新規).ユースケース
	 * @param companyId
	 * @param displayInfoOverTime
	 * @param appOverTime
	 * @param mode
	 * @param employeeId
	 * @param dateOp
	 * @return
	 */
	public DisplayInfoOverTime calculateMobile(
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime,
			Boolean mode,
			String employeeId,
			Optional<GeneralDate> dateOp,
			Boolean agent
			);
	/**
	 * Refactor5 個別登録前チェッ処理（複数人版）
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.個別登録前チェッ処理（複数人版）
	 * @param companyId
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @return
	 */
	public Map<String, List<ConfirmMsgOutput>> checkIndividualMultiple(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime
			);
	/**
	 * Refactor5 残業申請の共通部分をチェックする
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.個別登録前チェッ処理（複数人版）.残業申請の共通部分をチェックする
	 * @param appOverTime
	 * @param displayInfoOverTime
	 */
	public void checkCommonOverTime(
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime
			);
	
	/**
	 * Refactor5 申請対象者の情報を再取得する
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.個別登録前チェッ処理（複数人版）.申請対象者の情報を再取得する
	 * @param companyId
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @return
	 */
	public DisplayInfoOverTime reacquireInfoEmploy(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime
			);
	
	/**
	 * Refactor5 申請者ごとの申請内容をチェックする
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.個別登録前チェッ処理（複数人版）.申請者ごとの申請内容をチェックする
	 * @param companyId
	 * @param appOverTime
	 * @param displayInfoOverTime
	 * @return
	 */
	public List<ConfirmMsgOutput> checkEachEmployee(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime);

	/**
	 * UKDesign.UniversalK.就業.KAF_申請.KAF005_残業申請.A：残業申請（新規登録）.アルゴリズム.残業申請の新規登録（複数人版）
	 * 残業時間を計算のために勤務時間、休憩時間帯を判断する
	 * @param companyId
	 * @param employeeId
	 * @param appDate
	 * @param overtimeHours
	 * @param overtimeReasons
	 * @param workingHours
	 * @param workInformation
	 * @param achievementDetail
	 * @return
	 */
	Pair<List<TimeZoneWithWorkNo>, List<BreakTimeSheet>> getWorkingHoursAndBreakHours(
			String companyId,
			String employeeId,
			GeneralDate appDate,
			List<OvertimeHour> overtimeHours,
			List<OvertimeReason> overtimeReasons,
			List<TimeZoneWithWorkNo> workingHours,
			WorkInformation workInformation,
			Optional<AchievementDetail> achievementDetail
	);
}
