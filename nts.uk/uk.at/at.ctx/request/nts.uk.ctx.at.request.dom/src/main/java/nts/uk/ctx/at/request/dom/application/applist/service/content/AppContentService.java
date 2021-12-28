package nts.uk.ctx.at.request.dom.application.applist.service.content;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.time.YearMonth;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.AppListExtractCondition;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.ApplicationListAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplicationTypeDisplay;
import nts.uk.ctx.at.request.dom.application.applist.service.datacreate.StampAppOutputTmp;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.ScreenAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.param.AttendanceNameItem;
import nts.uk.ctx.at.request.dom.application.applist.service.param.ListOfApplication;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalBehaviorAtrImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationDetail;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemApplicationTypeName;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.ReasonForFixedForm;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * refactor 4
 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請内容ver4
 * @author Doan Duy Hung
 *
 */
public interface AppContentService {
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請内容ver4.申請内容（遅刻早退取消）.申請内容（遅刻早退取消）
	 * @param appReason 申請理由
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param screenID ScreenID
	 * @param itemContentLst <List>（項目名、勤務NO、区分（遅刻早退）、時刻、取消）
	 * @param appType 申請種類
	 * @param appStandardReasonCD 定型理由コード
	 * @return
	 */
	public String getArrivedLateLeaveEarlyContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, List<ArrivedLateLeaveEarlyItemContent> itemContentLst,
			ApplicationType appType, AppStandardReasonCode appStandardReasonCD);
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.各申請データを作成.申請内容.申請内容定型理由取得.申請内容定型理由取得
	 * @param appType 申請種類
	 * @param appStandardReasonCD 定型理由コード
	 * @param opHolidayAppType 休暇申請の種類(Optional)
	 * @return
	 */
	public ReasonForFixedForm getAppStandardReasonContent(ApplicationType appType, AppStandardReasonCode appStandardReasonCD, Optional<HolidayAppType> opHolidayAppType);
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.各申請データを作成.申請内容.申請内容（残業申請、休日出勤申請）.申請内容の申請理由.申請内容の申請理由
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param appReason 申請理由
	 * @param screenID ScreenID
	 * @param appStandardReasonCD 定型理由コード
	 * @param appType 申請種類
	 * @param opHolidayAppType 休暇申請の種類(Optional)
	 * @return
	 */
	public String getAppReasonContent(DisplayAtr appReasonDisAtr, AppReason appReason, ScreenAtr screenAtr, AppStandardReasonCode appStandardReasonCD,
			ApplicationType appType, Optional<HolidayAppType> opHolidayAppType);
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧の申請名称を取得する.申請一覧申請種類のプログラムID.申請一覧申請種類のプログラムID
	 * @return
	 */
	public List<AppTypeMapProgramID> getListProgramIDOfAppType(); 
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請内容ver4.申請内容（打刻申請）.申請内容（打刻申請）
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param appReason 申請理由
	 * @param screenAtr ScreenID
	 * @param stampAppOutputTmpLst 打刻申請出力用Tmp
	 * @param appType 申請種類
	 * @param appStandardReasonCD 定型理由コード
	 * @return
	 */
	public String getAppStampContent(DisplayAtr appReasonDisAtr, AppReason appReason, ScreenAtr screenAtr, List<StampAppOutputTmp> stampAppOutputTmpLst,
			ApplicationType appType, AppStandardReasonCode appStandardReasonCD);
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.各申請データを作成.申請内容.申請内容（勤務変更申請、直行直帰申請）.申請内容（勤務変更申請、直行直帰申請）
	 * @param appType 申請種類
	 * @param workTypeName 勤務種類名称
	 * @param workTimeName 就業時間帯名称
	 * @param goWorkAtr1 勤務直行1
	 * @param workTimeStart1 勤務時間開始1
	 * @param goBackAtr1 勤務直帰1
	 * @param workTimeEnd1 勤務時間終了1
	 * @param breakTimeStart1 休憩時間開始1
	 * @param breakTimeEnd1 休憩時間終了1
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param appReason 申請理由
	 * @param application 申請
	 * @return
	 */
	public String getWorkChangeGoBackContent(ApplicationType appType, String workTypeName, String workTimeName, NotUseAtr goWorkAtr1, TimeWithDayAttr workTimeStart1, 
			NotUseAtr goBackAtr1, TimeWithDayAttr workTimeEnd1, TimeWithDayAttr workTimeStart2, TimeWithDayAttr workTimeEnd2,
			TimeWithDayAttr breakTimeStart1, TimeWithDayAttr breakTimeEnd1, DisplayAtr appReasonDisAtr, AppReason appReason, Application application);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.各申請データを作成.各申請データを作成
	 * @param application 申請
	 * @param companyID 会社ID
	 * @param lstWkTime 就業時間帯リスト
	 * @param lstWkType 勤務種類リスト
	 * @param attendanceNameItemLst 勤怠項目リスト
	 * @param mode モード
	 * @param approvalListDisplaySetting 承認一覧表示設定
	 * @param listOfApp 申請一覧
	 * @param mapApproval Map＜ルートインスタンスID、承認フェーズList＞
	 * @param device デバイス：PC or スマートフォン
	 * @param appListExtractCondition 申請一覧抽出条件
	 * @return
	 */
	public ListOfApplication createEachAppData(Application application, String companyID, List<WorkTimeSetting> lstWkTime, List<WorkType> lstWkType, 
			List<AttendanceNameItem> attendanceNameItemLst, ApplicationListAtr mode, ApprovalListDisplaySetting approvalListDisplaySetting, ListOfApplication listOfApp, 
			Map<String,List<ApprovalPhaseStateImport_New>> mapApproval, int device, AppListExtractCondition appListExtractCondition, List<String> agentLst,
			Map<String, Pair<Integer, Integer>> cacheTime36);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.各申請データを作成.承認状況照会内容.承認状況照会内容
	 * @param approvalPhaseLst 承認フェーズList 
	 * @return
	 */
	public String getApprovalStatusInquiryContent(List<ApprovalPhaseStateImport_New> approvalPhaseLst);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面.アルゴリズム.各申請データを作成.反映状態（承認一覧モード）.反映状態（承認一覧モード）
	 * @param reflectedState 申請の実績反映状態
	 * @param phaseAtr 承認フェーズの承認区分
	 * @param frameAtr 承認枠の承認区分
	 * @return
	 */
	public String getReflectStatusApprovalListMode(ReflectedState reflectedState, ApprovalBehaviorAtrImport_New phaseAtr, 
			ApprovalBehaviorAtrImport_New frameAtr, int device);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請内容ver4.申請内容（任意項目申請）.申請内容（任意項目申請）
	 * @param appReason 申請理由
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param screenAtr ScreenID
	 * @param optionalItemApplicationTypeName 任意申請種類名
	 * @param optionalItemOutputLst <List>（任意項目名称、値、属性、単位）
	 * @param appType 申請種類
	 * @param appStandardReasonCD 定型理由コード
	 * @return
	 */
	public String getOptionalItemAppContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, 
			OptionalItemApplicationTypeName optionalItemApplicationTypeName, List<OptionalItemOutput> optionalItemOutputLst,
			ApplicationType appType, AppStandardReasonCode appStandardReasonCD);
	
	/**
	 * 申請内容（残業申請、休日出勤申請）
	 * @param appType
	 * @param prePostAtr
	 * @param applicationListAtr
	 * @param appReason
	 * @param appReasonDisAtr
	 * @param screenAtr
	 * @param actualStatus
	 * @param application
	 * @return
	 */
	public String getOvertimeHolidayWorkContent(AppOverTimeData appOverTimeData, AppHolidayWorkData appHolidayWorkData,
			ApplicationType appType, PrePostAtr prePostAtr, ApplicationListAtr applicationListAtr, AppReason appReason, 
			DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, boolean actualStatus, Application application);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧リスト取得実績.申請一覧リスト取得実績
	 * @param companyID
	 * @param application
	 * @return
	 */
	public OvertimeHolidayWorkActual getOvertimeHolidayWorkActual(String companyID, Application application, 
			List<WorkType> workTypeLst, List<WorkTimeSetting> workTimeSettingLst, List<AttendanceNameItem> attendanceNameItemLst,
			AppOverTime appOverTime, AppHolidayWork appHolidayWork, WorkTypeCode workType, WorkTimeCode workTime);
	
	/**
	 * Refactor4  各申請データを作成（スマホ）
	 * UKDesign.UniversalK.就業.KAF_申請.CMMS45_申請一覧・承認一覧（スマホ）.A：申請一覧.アルゴリズム.各申請データを作成（スマホ）
	 * @param application
	 * @param listOfApplication
	 * @return
	 */
	public Optional<ApplicationTypeDisplay> getAppDisplayByMobile(Application application, ListOfApplication listOfApplication);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧リストのデータを作成.勤怠名称を取得.勤怠名称を取得
	 * @param companyID
	 * @return
	 */
	public List<AttendanceNameItem> getAttendanceNameItemLst(String companyID);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.各申請データを作成.申請一覧36協定時間の取得.申請一覧36協定時間の取得
	 * @param employeeID
	 * @param yearMonth
	 * @param cache
	 * @return
	 * pair left: 超過時間
	 * pair right: 超過回数
	 */
	public Pair<Integer, Integer> getAgreementTime36(String employeeID, YearMonth yearMonth, Map<String, Pair<Integer, Integer>> cache);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.各申請データを作成.振休振出申請データを作成.振休振出申請内容.振休振出申請内容
	 * @param absenceLeaveApp 振休申請データ
	 * @param recruitmentApp 振出申請データ
	 * @param appReasonDisAtr 申請理由内容区分
	 * @param screenAtr ScreenID
	 * @param complementLeaveAppLink 振休振出申請紐付け
	 * @param application 申請
	 */
	public String getComplementLeaveContent(AbsenceLeaveApp absenceLeaveApp, RecruitmentApp recruitmentApp, DisplayAtr appReasonDisAtr,
			ScreenAtr screenAtr, ComplementLeaveAppLink complementLeaveAppLink, Application application, List<WorkType> workTypeLst);
	
	/**
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請内容ver4.申請内容（時間年休申請）.申請内容（時間年休申請）
	 * @param appReason 申請理由
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param screenAtr ScreenID
	 * @param leaveApplicationDetails <List>項目名、時間休種類、時間代休、時間年休、子の看護時間、介護時間、60H超休、時間特別休暇
	 * @param appStandardReasonCD 定型理由コード
	 * @return
	 */
	public String getAnnualHolidayContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			List<TimeLeaveApplicationDetail> leaveApplicationDetails, AppStandardReasonCode appStandardReasonCD);

	/**
	 * UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.各申請データを作成.残業申請データを作成.残業申請複数回データを作成.残業申請複数回データを作成
	 * @param appReasonDisAtr 申請理由表示区分
	 * @param appHolidayWorkData 申請データ(残業申請）
	 * @param screenAtr ScreenID
	 * @param result 申請理由内容
	 * @return 申請理由内容
	 */

	public String getOvertimeApplicationDataMultiTime(DisplayAtr appReasonDisAtr,AppOverTimeData appOverTimeData,ScreenAtr screenAtr,String appReasonContent);
}
