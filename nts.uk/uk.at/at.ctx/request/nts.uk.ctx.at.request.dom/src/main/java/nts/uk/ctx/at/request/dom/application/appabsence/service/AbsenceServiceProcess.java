package nts.uk.ctx.at.request.dom.application.appabsence.service;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.appabsence.apptimedigest.TimeDigestApplication;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.AbsenceCheckRegisterOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.AppAbsenceStartInfoOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.AppForLeaveStartOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.SpecAbsenceDispInfo;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.VacationCheckOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.VacationLinkManageInfo;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ProcessResult;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.AppliedDate;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.vacationapplicationsetting.HolidayApplicationSetting;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

public interface AbsenceServiceProcess {

	/**
	 * @author hoatt
	 * 13.計画年休上限チェック
	 * @param cID: 会社ID
	 * @param sID: 社員ID
	 * @param workTypeCD: 勤務種類コード
	 * @param sDate: 申請開始日
	 * @param eDate: 申請終了日
	 * @param lstDateIsHoliday: 休日の申請日
	 */
	public void checkLimitAbsencePlan(String cID, String sID, String workTypeCD,
			GeneralDate sDate, GeneralDate eDate, List<GeneralDate> lstDateIsHoliday);
	/**
	 * @author hoatt
	 * 14.休暇種類表示チェック
	 * @param companyID 会社ID
	 * @param sID 社員ID
	 * @param baseDate 基準日
	 * @return
	 */
	public CheckDispHolidayType checkDisplayAppHdType(String companyID, String sID, GeneralDate baseDate);

	/**
	 * 代休振休優先消化チェック
	 * @param mode 画面モード  新規モード: true/更新モード: false
	 * @param hdAppSet 休暇申請設定
	 * @param employmentSet 雇用別申請承認設定
	 * @param subVacaManage 振休管理区分
	 * @param subHdManage 代休管理区分
	 * @param subVacaRemain 振休残数
	 * @param subHdRemain 代休残数
	 * @return
	 */
//	public List<ConfirmMsgOutput> checkDigestPriorityHd(boolean mode, HolidayApplicationSetting hdAppSet, AppEmploymentSetting employmentSet, boolean subVacaManage,
//														boolean subHdManage, Double subVacaRemain, Double subHdRemain);
	/**
	 * @author hoatt
	 * 振休代休優先チェック
	 * @param pridigCheck - 休暇申請設定．年休より優先消化チェック区分 - HdAppSet
	 * @param isSubVacaManage - 振休管理設定．管理区分
	 * @param subVacaTypeUseFlg - 休暇申請対象勤務種類．休暇種類を利用しない（振休） - AppEmploymentSetting
	 * @param isSubHdManage - 代休管理設定．管理区分
	 * @param subHdTypeUseFlg - 休暇申請対象勤務種類．休暇種類を利用しない（代休） - AppEmploymentSetting
	 * @param numberSubHd - 代休残数
	 * @param numberSubVaca - 振休残数
	 * @return エラーメッセージ - 確認メッセージ
	 */
	public List<ConfirmMsgOutput> checkPriorityHoliday(AppliedDate pridigCheck,
			boolean isSubVacaManage, boolean subVacaTypeUseFlg, boolean isSubHdManage, boolean subHdTypeUseFlg,
			int numberSubHd, int numberSubVaca);

	/**
	 * 残数取得する
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param baseDate 基準日
	 * @param yearManage 年休管理区分
	 * @param subHdManage 代休管理区分
	 * @param subVacaManage 積休管理区分
	 * @param retentionManage 振休管理区分
	 * @return
	 */
	public NumberOfRemainOutput getNumberOfRemaining(String companyID, String employeeID, GeneralDate baseDate,
            ManageDistinct annualLeaveManageDistinct, ManageDistinct accumulatedManage, ManageDistinct substituteLeaveManagement,
            ManageDistinct holidayManagement, ManageDistinct overrest60HManagement, ManageDistinct childNursingManagement,
            ManageDistinct longTermCareManagement);

	/**
	 * 休暇申請設定を取得する
	 * @param companyID 会社ID
	 * @return
	 */
	public HolidayRequestSetOutput getHolidayRequestSet(String companyID);

	/**
	 * 休暇残数情報を取得する
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param date 基準日
	 * @return
	 */
	public RemainVacationInfo getRemainVacationInfo(String companyID, String employeeID, GeneralDate date);

	/**
	 * 特別休暇の上限情報取得する
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param workTypeCD 勤務種類コード<Optional>
	 * @return
	 */
	public AppAbsenceStartInfoOutput getSpecAbsenceUpperLimit(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput, Optional<String> workTypeCD);

	/**
	 * 就業時間帯変更時処理
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param workTypeCD 勤務種類コード
	 * @param workTimeCD 就業時間帯コード<Optional>
	 * @return
	 */
	public AppAbsenceStartInfoOutput workTimesChangeProcess(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput, String workTypeCD,
			Optional<String> workTimeCD);

	/**
	 * 勤務種類変更時処理
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param holidayType 休暇種類
	 * @param workTypeCD 勤務種類コード<Optional>
	 * @return
	 */
	public AppAbsenceStartInfoOutput workTypeChangeProcess(String companyID, List<String> appDates, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput, HolidayAppType holidayType,
			Optional<String> workTypeCD);

	/**
	 * 休暇種類変更時処理
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param displayHalfDayValue 勤務種類組み合わせ全表示チェック
	 * @param alldayHalfDay 終日半日休暇区分
	 * @param holidayType 休暇種類
	 * @return
	 */
	public AppAbsenceStartInfoOutput holidayTypeChangeProcess(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput,
			List<String> appDates, HolidayAppType holidayType);

	/**
	 * 終日半日休暇変更時処理
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param displayHalfDayValue 勤務種類組み合わせ全表示チェック
	 * @param alldayHalfDay 終日半日休暇区分
	 * @param holidayType 休暇種類<Optional>
	 * @return
	 */
	public AppAbsenceStartInfoOutput allHalfDayChangeProcess(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput,
			boolean displayHalfDayValue, Integer alldayHalfDay, Optional<HolidayAppType> holidayType);

	/**
	 * A画面 - 登録前のエラーチェック処理
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param agentAtr 代行申請区分
	 * @return
	 */
	public AbsenceCheckRegisterOutput checkBeforeRegister(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput,
			ApplyForLeave appAbsence, boolean agentAtr, boolean isEmptyLeaveList, boolean isEmptyPayoutList);

	/**
	 * 申請日の矛盾チェック
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param startDate 申請日開始日
	 * @param endDate 申請日終了日
	 * @param alldayHalfDay 終日半日休暇区分
	 * @param hdAppSet 休暇申請設定
	 * @return
	 */
	public List<ConfirmMsgOutput> inconsistencyCheck(String companyID, String employeeID, GeneralDate startDate, GeneralDate endDate,
			Integer alldayHalfDay, HolidayApplicationSetting hdAppSet, boolean mode);


	/**
	 * 休暇種類共通エラーチェック
	 * @param companyID 会社ID
	 * @param closureStartDate 締め開始日
	 * @param appAbsence 休暇申請
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 */
	public List<ConfirmMsgOutput> holidayCommonCheck(String companyID, GeneralDate closureStartDate, ApplyForLeave appAbsence,
			AppAbsenceStartInfoOutput appAbsenceStartInfoOutput, List<GeneralDate> lstHolidayDate, Optional<TimeDigestApplication> timeDigestApplication);

	/**
	 * 年休のチェック処理
	 * @param mode 画面モード
	 * @param companyID 会社ID
	 * @param employeeID 社員ID
	 * @param startDate 申請開始日
	 * @param endDate 申請終了日
	 * @param workTypeCD 勤務種類コード
	 * @param lstDateIsHoliday 休日の申請日<List>
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @return
	 */
	public List<ConfirmMsgOutput> annualLeaveCheck(boolean mode, String companyID, String employeeID, GeneralDate startDate, GeneralDate endDate,
			String workTypeCD, List<GeneralDate> lstDateIsHoliday, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput);

	/**
	 * 特別休暇の上限チェック
	 * @param companyID 会社ID
	 * @param startDate 申請開始日
	 * @param endDate  申請終了日
	 * @param mournerAtr 上限日数
	 * @param specAbsenceDispInfo 特別休暇表示情報
	 * @param lstDateIsHoliday 休日の申請日<List>
	 */
	public void checkSpecHoliday(String companyID, GeneralDate startDate, GeneralDate endDate, Boolean mournerAtr,
			SpecAbsenceDispInfo specAbsenceDispInfo, List<GeneralDate> lstDateIsHoliday);

	/**
	 * 特別休暇のチェック処理
	 * @param companyID 会社ID
	 * @param startDate 申請開始日
	 * @param endDate 申請終了日
	 * @param holidayDateLst 休日の申請日<List>
	 * @param mournerAtr 喪主区分
	 */
	/**
	 * 特別休暇のチェック処理
	 * @param companyID 会社ID
	 * @param startDate 申請開始日
	 * @param endDate 申請終了日
	 * @param holidayDateLst 休日の申請日<List>
	 * @param specAbsenceDispInfo 特別休暇表示情報
	 * @param mournerAtr 喪主区分
	 */
	public void checkSpecLeaveProcess(String companyID, GeneralDate startDate, GeneralDate endDate, List<GeneralDate> holidayDateLst,
			SpecAbsenceDispInfo specAbsenceDispInfo, Boolean mournerAtr);

	/**
	 * 時間消化のチェック処理
	 * @param startDate
	 * @param endDate
	 */
	public void checkTimeDigestProcess(String companyID, TimeDigestApplication timeDigestApplicationGeneralDate
			, RemainVacationInfo remainVacationInfo, String employeeId, GeneralDate baseDate, Optional<AttendanceTime> requiredTime );

	/**
	 * 休暇種類別エラーチェック
	 * @param mode 画面モード
	 * @param companyID 会社ID
	 * @param appAbScene 休暇申請
	 * @param holidayDateLst 休日の申請日<List>
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @return
	 */
	public List<ConfirmMsgOutput> errorCheckByHolidayType(boolean mode, String companyID, ApplyForLeave appAbScene
			, List<GeneralDate> lstDate, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput);

	/**
	 * C画面 - 登録前のエラーチェック処理
	 * @param mode 画面モード
	 * @param companyID 会社ID
	 * @param appAbsence 申請
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param holidayType 休暇種類
	 * @param workTypeCD 勤務種類コード
	 * @param alldayHalfDay 終日半日休暇区分
	 * @param holidayDateLst 休日の申請日<List>
	 * @param mournerAtr 喪主区分<Optional>
	 * @return
	 */
	public AbsenceCheckRegisterOutput checkAppAbsenceRegister(boolean mode, String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput,ApplyForLeave appBeforeChange,ApplyForLeave appAfterChange);

	/**
	 * 休暇申請登録時チェック処理
	 * @param mode 画面モード
	 * @param companyID　会社ID
	 * @param appAbscene　休暇申請
	 * @param appAbsenceStartInfoOutput　休暇申請起動時の表示情報
	 * @param lstHolidayDate　休日リスト
	 * @return
	 */
	public List<ConfirmMsgOutput> checkAbsenceWhenRegister(boolean mode, String companyID, ApplyForLeave appAbscene, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput, List<GeneralDate> lstHolidayDate);

	/**
	 * 勤務種類・就業時間帯情報を取得する
	 * @param companyID 会社ID
	 * @param appAbsence 休暇申請
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @return
	 */
	public AppAbsenceStartInfoOutput getWorkTypeWorkTimeInfo(String companyID, ApplyForLeave appAbsence, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput);

	/**
	 * 更新前のエラーチェック処理
	 * @param companyID 会社ID
	 * @param appAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 * @param application 申請
	 * @param appAbsence 休暇申請
	 * @param alldayHalfDay 終日半日休暇区分
	 * @param agentAtr 代行申請区分
	 * @param mourningAtr 喪主区分<Optional>
	 * @return
	 */
	/*public AbsenceCheckRegisterOutput checkBeforeUpdate(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput, Application application,
			AppAbsence appAbsence, Integer alldayHalfDay, boolean agentAtr, Optional<Boolean> mourningAtr);*/

	/**
	 * 1.休暇申請（新規）起動処理
	 * @param String companyID 会社ID
	 * @param AppDispInfoStartupOutput appDispInfoStartupOutput 申請表示情報
	 * @return AppAbsenceStartInfoOutput 休暇申請起動時の表示情報
	 */
	public AppAbsenceStartInfoOutput getVacationActivation(String companyID, AppDispInfoStartupOutput appDispInfoStartupOutput);

	/**
	 * 休暇紐付管理をチェックする
	 * @param WorkType workType before 変更前の勤務種類
	 * @param WorkType workType after 変更後の勤務種類
	 * @param List<LeaveComDayOffManagement> list leaveManage 休出代休紐付け管理<List>
	 * @param List<PayoutSubofHDManagement> list 振出振休紐付け管理<List>
	 * @return VacationCheckOutput
	 *         代休紐付管理をクリアする(boolean)
	 *         振休紐付管理をクリアする(boolean)
	 */
	public VacationCheckOutput checkVacationTyingManage(WorkType wtBefore, WorkType wtAfter, List<LeaveComDayOffManagement> leaveComDayOffMana, List<PayoutSubofHDManagement> payoutSubofHDManagements);

	/**
	 *     休暇申請（新規）登録処理
	 * @param applyForLeave
	 * @param appDates
	 * @param leaveComDayOffMana
	 * @param payoutSubofHDManagements
	 * @param mailServerSet
	 * @param approvalRoot
	 */
	public ProcessResult registerAppAbsence(ApplyForLeave applyForLeave, List<String> appDates, List<LeaveComDayOffManagement> leaveComDayOffMana, List<PayoutSubofHDManagement> payoutSubofHDManagements, boolean mailServerSet, List<ApprovalPhaseStateImport_New> approvalRoot, AppTypeSetting applicationSetting);


	/**
	 * 5.休暇申請（詳細）起動処理
	 * @param companyID
	 * @param appID
	 * @param appDetailScreenInfo
	 * @return
	 */
	public AppForLeaveStartOutput getAppForLeaveStartB(String companyID, String appID, AppDispInfoStartupOutput appDispInfoStartupOutput);


	/**
	 *     休暇紐付管理情報を取得する
	 * @param employeeID
	 * @param appStartDate
	 * @param appEndDate
	 * @param workType
	 * @param actualContentDisplayLst
	 */
	public VacationLinkManageInfo getVacationLinkManageInfo(String employeeID, String appStartDate, String appEndDate, WorkType workType, List<ActualContentDisplay> actualContentDisplayLst);

	/**
	 *     登録前のエラーチェック処理
	 * @param companyID
	 * @param appAbsenceStartInfoOutput
	 * @param appAbsence
	 * @param agentAtr
	 * @return
	 */
	public AbsenceCheckRegisterOutput checkBeforeUpdate(String companyID, AppAbsenceStartInfoOutput appAbsenceStartInfoOutput,
            ApplyForLeave appAbsence, boolean agentAtr, boolean isEmptyLeaveList, boolean isEmptyPayoutList);


    /**
         * 休暇紐付け管理を登録する
     * @param leaveComDayOffMana
     * @param payoutSubofHDManagements
     */
    public void registerVacationLinkManage(List<LeaveComDayOffManagement> leaveComDayOffMana,
            List<PayoutSubofHDManagement> payoutSubofHDManagements);

    /**
         * 指定する勤務種類に必要な休暇時間を算出する
     * @param employeeID
     * @param date
     * @param workTypeCode
     * @param workTimeCode
     * @param scheduleToWork
     * @param workingCondition
     * @return
     */
    public AttendanceTime calculateTimeRequired(String employeeID,
            Optional<GeneralDate> date,
            Optional<String> workTypeCode,
            Optional<String> workTimeCode,
            Optional<WorkInfoOfDailyAttendance> workInfoDaily,
            Optional<ScBasicScheduleImport> scheduleToWork,
            Optional<WorkingConditionItem> workingCondition);

    /**
         * 休暇申請（詳細）更新処理
     * @param applyForLeave
     * @param holidayAppDates
     * @param oldLeaveComDayOffMana
     * @param oldPayoutSubofHDManagements
     * @param leaveComDayOffMana
     * @param payoutSubofHDManagements
     * @return
     */
    public ProcessResult updateApplyForLeave(
            ApplyForLeave applyForLeave,
            List<String> holidayAppDates,
            List<LeaveComDayOffManagement> leaveComDayOffMana,
            List<PayoutSubofHDManagement> payoutSubofHDManagements,
            AppDispInfoStartupOutput appDispInfoStartupOutput);

    /**
            *  代休日を変更する
     * @param companyID
     * @param holidayDates
     * @param appAbsenceStartInfoDto
     * @return
     */
    public AppAbsenceStartInfoOutput getChangeHolidayDates(String companyID, List<GeneralDate> holidayDates, AppAbsenceStartInfoOutput appAbsenceStartInfoDto);

    /**
         * 休暇申請を登録する
     * @param companyID
     * @param newApplyForLeave
     * @param originApplyForLeave
     * @param holidayDates
     * @param appAbsenceStartInfoDto
     */
    public ProcessResult registerHolidayDates(String companyID, ApplyForLeave newApplyForLeave, ApplyForLeave originApplyForLeave, List<GeneralDate> holidayDates, AppAbsenceStartInfoOutput appAbsenceStartInfoDto);

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF011_振休振出申請.振休振出申請共通アルゴリズム.勤務種類・就業時間帯関連.勤務時間初期値の取得.勤務時間初期値の取得
     * @param companyID
     * @param workTypeCode
     * @param workTimeCode
     * @return
     */

    public List<TimeZone> initWorktimeCode(String companyID, String workTypeCode, String workTimeCode);

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF006_休暇申請.アルゴリズム.休暇に必要な時間をチェックする.休暇に必要な時間をチェックする
     * @param timeDigestApplication 時間消化申請
     * @param requiredVacationTime 必要休暇時間
     */
    public void checkVacationTimeRequire(TimeDigestApplication timeDigestApplication, AttendanceTime requiredVacationTime);
    
    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF006_休暇申請.アルゴリズム.休暇申請登録時チェック処理.休暇種類共通エラーチェック.休暇残数チェック
     * @param companyID
     * @param application
     * @param datePeriod
     * @param vacationType
     */
//    public void checkRemainVacation(String companyID, ApplyForLeave application, GeneralDate date, HolidayAppType vacationType, Optional<TimeDigestApplication> timeDigestApplication);
}
