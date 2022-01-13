package nts.uk.ctx.at.request.dom.application.applist.service.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.request.dom.application.overtime.*;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.i18n.I18NText;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ApprovalDevice;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.AppListExtractCondition;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.ApplicationListAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplicationTypeDisplay;
import nts.uk.ctx.at.request.dom.application.applist.service.datacreate.StampAppOutputTmp;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppContentDetailCMM045;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppDetailInfoRepository;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppHolidayWorkDataOutput;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppOvertimeDataOutput;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppStampDataOutput;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.CompLeaveAppDataOutput;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.ScreenAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.param.AttendanceNameItem;
import nts.uk.ctx.at.request.dom.application.applist.service.param.ListOfApplication;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementExcessInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.standardtime.AgreementMonthSettingAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.standardtime.AgreementMonthSettingOutput;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalBehaviorAtrImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalFrameImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.PreActualColorCheck;
import nts.uk.ctx.at.request.dom.application.common.service.other.CollectAchievement;
import nts.uk.ctx.at.request.dom.application.common.service.other.PreAppContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationDetail;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.CalcStampMiss;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.optionalitemappsetting.OptionalItemApplicationTypeName;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.ApplicationDetailSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSet;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandard;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandardRepository;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.ReasonForFixedForm;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.ReasonTypeItem;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrame;
import nts.uk.ctx.at.shared.dom.ot.frame.OvertimeWorkFrameRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ScheRecAtr;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@Stateless
public class AppContentServiceImpl implements AppContentService {
	
	@Inject
	private AppReasonStandardRepository appReasonStandardRepository;
	
	@Inject
	private AppContentDetailCMM045 appContentDetailCMM045;
	
	@Inject
	private PreActualColorCheck preActualColorCheck;
	
	@Inject
	private OvertimeAppSetRepository overtimeAppSetRepository;
	
	@Inject
	private HolidayWorkAppSetRepository holidayWorkAppSetRepository;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
	private AppOverTimeRepository appOverTimeRepository;
	
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;

	@Inject
	private OvertimeWorkFrameRepository overtimeWorkFrameRepository;
	
	@Inject
	private WorkdayoffFrameRepository workdayoffFrameRepository;

	@Inject
	private AppOverTimeRepository appOverTimeRepo;
	
	@Inject
	private CollectAchievement collectAchievement;
	
	private AgreementTimeAdapter agreementTimeAdapter;
	
	@Inject
	private AgreementMonthSettingAdapter agreementMonthSettingAdapter;
	
	@Inject
	private AppDetailInfoRepository appDetailInfoRepo;

	@Override
	public String getArrivedLateLeaveEarlyContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, List<ArrivedLateLeaveEarlyItemContent> itemContentLst,
			ApplicationType appType, AppStandardReasonCode appStandardReasonCD) {
		String result = Strings.EMPTY;
		String paramString = Strings.EMPTY;
		if(screenAtr != ScreenAtr.KAF018 && screenAtr != ScreenAtr.CMM045) {
			// @＝改行
			paramString = "\n";
		} else {
			// @＝”　”
			paramString = "	";
		}
		// ・<List>（項目名、勤務NO、区分（遅刻早退）、時刻、取消）
		if(!CollectionUtil.isEmpty(itemContentLst)) {
			for(int i = 0; i < itemContentLst.size(); i++) {
				ArrivedLateLeaveEarlyItemContent item = itemContentLst.get(i);
				if(i > 0) {
					// 申請内容＋＝@
					result += paramString;
				}
				// <List>.取消
				if(!item.isCancellation()) {
					// 申請内容＋＝項目名＋勤務NO＋'　'＋時刻
					result += item.getItemName() + item.getWorkNo() + " " + item.getOpTimeWithDayAttr().get().getFullText();
				} else {
					// 申請内容＋＝項目名＋勤務NO＋'　'＋#CMM045_292(取消)
					result += item.getItemName() + item.getWorkNo() + "	" + I18NText.getText("CMM045_292");
				}
			}
		}
		// アルゴリズム「申請内容の申請理由」を実行する
		String appReasonContent = this.getAppReasonContent(appReasonDisAtr, appReason, screenAtr, appStandardReasonCD, appType, Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public ReasonForFixedForm getAppStandardReasonContent(ApplicationType appType, AppStandardReasonCode appStandardReasonCD,
			Optional<HolidayAppType> opHolidayAppType) {
		String companyID = AppContexts.user().companyId();
		// 定型理由コード(FormReasonCode)
		if(appStandardReasonCD == null) {
			return null;
		} 
		// ドメインモデル「申請定型理由」を取得する(Get domain 「ApplicationFormReason」)
		Optional<AppReasonStandard> opAppReasonStandard = Optional.empty();
		if(!opHolidayAppType.isPresent()) {
			opAppReasonStandard = appReasonStandardRepository.findByAppType(companyID, appType);
		} else {
			opAppReasonStandard = appReasonStandardRepository.findByHolidayAppType(companyID, opHolidayAppType.get());
		}
		if(!opAppReasonStandard.isPresent()) {
			return null;
		}
		// 定型理由＝定型理由項目.定型理由(FormReason = FormReasonItem. FormReason)
		List<ReasonTypeItem> reasonTypeItemLst = opAppReasonStandard.get().getReasonTypeItemLst();
		Optional<ReasonTypeItem> opReasonTypeItem = reasonTypeItemLst.stream().filter(x -> x.getAppStandardReasonCD().equals(appStandardReasonCD)).findAny();
		if(opReasonTypeItem.isPresent()) {
			return opReasonTypeItem.get().getReasonForFixedForm();
		}
		return null;
	}

	@Override
	public String getAppReasonContent(DisplayAtr appReasonDisAtr, AppReason appReason, ScreenAtr screenAtr,
			AppStandardReasonCode appStandardReasonCD, ApplicationType appType,
			Optional<HolidayAppType> opHolidayAppType) {
		// 申請理由内容　＝　String.Empty
		String result = Strings.EMPTY;
		if(!(screenAtr != ScreenAtr.KAF018 && 
				((appReason!= null && Strings.isNotBlank(appReason.v())) || (appStandardReasonCD != null))  && 
				appReasonDisAtr == DisplayAtr.DISPLAY)) {
			return result;
		}
		// アルゴリズム「申請内容定型理由取得」を実行する
		ReasonForFixedForm reasonForFixedForm = this.getAppStandardReasonContent(appType, appStandardReasonCD, opHolidayAppType);
		if(reasonForFixedForm!=null) {
			if(screenAtr == ScreenAtr.KDL030) {
				// 申請理由内容　+＝　”申請理由：”を改行
				result += "申請理由：  " + "\n";
				// 申請理由内容　+＝　定型理由＋改行＋Input．申請理由
				result += reasonForFixedForm.v();
				if(appReason!=null) {
					result += "\n" + appReason.v();
				}
			} else {
				// 申請理由内容　+＝　定型理由＋’　’＋Input．申請理由
				result += reasonForFixedForm.v();
				if(appReason!=null) {
					result += " " + appReason.v().replaceAll("\n", " ");	
				}
			}
		} else {
			if(screenAtr == ScreenAtr.KDL030) {
				// 申請理由内容　+＝　”申請理由：”を改行
				result += "申請理由：  " + "\n";
				// 申請理由内容　+＝　Input．申請理由
				if(appReason!=null) {
					result += appReason.v();	
				}
			} else {
				// 申請理由内容　+＝　Input．申請理由
				if(appReason!=null) {
					result += appReason.v().replaceAll("\n", " ");	
				}
			}
		}
		return result;
	}

	@Override
	public List<AppTypeMapProgramID> getListProgramIDOfAppType() {
		List<AppTypeMapProgramID> result = new ArrayList<>();
		// 残業申請＝KAF005、0
		result.add(new AppTypeMapProgramID(ApplicationType.OVER_TIME_APPLICATION, "KAF005", ApplicationTypeDisplay.EARLY_OVERTIME));
		// 残業申請＝KAF005、1
		result.add(new AppTypeMapProgramID(ApplicationType.OVER_TIME_APPLICATION, "KAF005", ApplicationTypeDisplay.NORMAL_OVERTIME));
		// 残業申請＝KAF005、2
		result.add(new AppTypeMapProgramID(ApplicationType.OVER_TIME_APPLICATION, "KAF005", ApplicationTypeDisplay.EARLY_NORMAL_OVERTIME));
		//・残業申請＝KAF005、3
		result.add(new AppTypeMapProgramID(ApplicationType.OVER_TIME_APPLICATION, "KAF005", ApplicationTypeDisplay.OVERTIME_MULTIPLE_TIME));
		// 休暇申請＝KAF006
		result.add(new AppTypeMapProgramID(ApplicationType.ABSENCE_APPLICATION, "KAF006", null));
		// 勤務変更申請＝KAF007
		result.add(new AppTypeMapProgramID(ApplicationType.WORK_CHANGE_APPLICATION, "KAF007", null));
		// 出張申請＝KAF008
		result.add(new AppTypeMapProgramID(ApplicationType.BUSINESS_TRIP_APPLICATION, "KAF008", null));
		// 直行直帰申請＝KAF009
		result.add(new AppTypeMapProgramID(ApplicationType.GO_RETURN_DIRECTLY_APPLICATION, "KAF009", null));
		// 休出時間申請＝KAF010
		result.add(new AppTypeMapProgramID(ApplicationType.HOLIDAY_WORK_APPLICATION, "KAF010", null));
		// 打刻申請＝KAF002、0
		result.add(new AppTypeMapProgramID(ApplicationType.STAMP_APPLICATION, "KAF002", ApplicationTypeDisplay.STAMP_ADDITIONAL));
		// 打刻申請＝KAF002、1
		result.add(new AppTypeMapProgramID(ApplicationType.STAMP_APPLICATION, "KAF002", ApplicationTypeDisplay.STAMP_ONLINE_RECORD));
		// 時間年休申請＝KAF012
		result.add(new AppTypeMapProgramID(ApplicationType.ANNUAL_HOLIDAY_APPLICATION, "KAF012", null));
		// 遅刻早退取消申請＝KAF004
		result.add(new AppTypeMapProgramID(ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION, "KAF004", null));
		// 振休振出申請＝KAF011
		result.add(new AppTypeMapProgramID(ApplicationType.COMPLEMENT_LEAVE_APPLICATION, "KAF011", null));
		// 任意申請＝KAF020
		result.add(new AppTypeMapProgramID(ApplicationType.OPTIONAL_ITEM_APPLICATION, "KAF020", null));
		// 対象の申請種類に対しプログラムIDを返す
		return result;
	}

	@Override
	public String getAppStampContent(DisplayAtr appReasonDisAtr, AppReason appReason, ScreenAtr screenAtr,
			List<StampAppOutputTmp> stampAppOutputTmpLst, ApplicationType appType, AppStandardReasonCode appStandardReasonCD) {
		String result = Strings.EMPTY;
		String paramString = Strings.EMPTY;
		if(screenAtr != ScreenAtr.KAF018 && screenAtr != ScreenAtr.CMM045) {
			// @＝改行
			paramString = "\n";
		} else {
			// @＝”　”
			paramString = "	";
		}
		if(!CollectionUtil.isEmpty(stampAppOutputTmpLst)) {
			for(int i = 0; i < stampAppOutputTmpLst.size(); i++) {
				StampAppOutputTmp item = stampAppOutputTmpLst.get(i);
				if(i > 0) {
					// 申請内容＋＝@
					result += paramString;
				}
				// 打刻申請出力用Tmp.取消
				if(!item.isCancel()) {
					// 申請内容＋＝$.項目名＋'　'＋$.開始時刻＋#CMM045_100(～)＋$.終了時刻
					result += item.getOpItemName().orElse("") + " " +
							item.getOpStartTime().map(x -> x.getFullText()).orElse("") + I18NText.getText("CMM045_100") + item.getOpEndTime().map(x -> x.getFullText()).orElse("");
				} else {
					// 申請内容＋＝$.項目名＋'　'＋#CMM045_292(取消)
					result += item.getOpItemName().orElse("") + " " + I18NText.getText("CMM045_292");
				}
			}
		}
		// アルゴリズム「申請内容の申請理由」を実行する
		String appReasonContent = this.getAppReasonContent(appReasonDisAtr, appReason, screenAtr, appStandardReasonCD, appType, Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public String getWorkChangeGoBackContent(ApplicationType appType, String workTypeName, String workTimeName, NotUseAtr goWorkAtr1, TimeWithDayAttr workTimeStart1, 
			NotUseAtr goBackAtr1, TimeWithDayAttr workTimeEnd1, TimeWithDayAttr workTimeStart2, TimeWithDayAttr workTimeEnd2,
			TimeWithDayAttr breakTimeStart1, TimeWithDayAttr breakTimeEnd1, DisplayAtr appReasonDisAtr, AppReason appReason, Application application) {
		// 申請内容　＝　String.Empty ( Nội dung application = 　String.Empty)
		String result = Strings.EMPTY;
		if(appType == ApplicationType.WORK_CHANGE_APPLICATION) {
			// 申請内容　＝　Input．勤務種類名称 ( Nội dung = Input. Work type name)
			result = workTypeName;
			// 申請内容　+＝’　’　＋　Input．就業時間帯名称  ( Nội dung +＝　 Input.Working hour name)
			result += " " + workTimeName;
			// Input．．勤務直行1をチェック ( Check Input．．đi làm thẳng 1
			if(goWorkAtr1 == NotUseAtr.USE) {
				// 申請内容　+＝’　’＋#CMM045_259
				result += " " + I18NText.getText("CMM045_259");
			} else {
				// 申請内容　+＝’　’
				result += " ";
			}
			// 申請内容　+＝　Input．勤務時間開始1
			result += workTimeStart1 == null ? "" : workTimeStart1.getInDayTimeWithFormat();
			// Input．勤務直帰1をチェック
			if(workTimeStart1!=null || workTimeEnd1!=null) {
				result += I18NText.getText("CMM045_100");
			}
			if(goBackAtr1 == NotUseAtr.USE) {
				// 申請内容　+＝　#CMM045_100　+　#CMM045_260
				result += I18NText.getText("CMM045_260");
			}
			// 申請内容　+＝　Input．勤務時間終了1
			result += workTimeEnd1 == null ? "" : workTimeEnd1.getInDayTimeWithFormat();
		} else {
			// Input．．勤務直行1をチェック ( Check Input．．đi làm thẳng 1
			if(goWorkAtr1 == NotUseAtr.USE) {
				// 申請内容　+＝#CMM045_259＋’　’ ( Nội dung đơn xin　+＝#CMM045_259)
				result += I18NText.getText("CMM045_259") + " ";
			}
			// Input．勤務直帰1をチェック
			if(goBackAtr1 == NotUseAtr.USE) {
				// 申請内容　+＝　#CMM045_260
				result += I18NText.getText("CMM045_260");
			}
		}
		if(appType == ApplicationType.WORK_CHANGE_APPLICATION) {
			if(workTimeStart2!=null || workTimeEnd2!=null) {
				// 申請内容　+＝　’　’＋Input．勤務時間開始2＋#CMM045_100＋Input．勤務時間終了2
				result += " ";
				result += workTimeStart2 == null ? "" : workTimeStart2.getInDayTimeWithFormat();
				result += I18NText.getText("CMM045_100");
				result += workTimeEnd2 == null ? "" : workTimeEnd2.getInDayTimeWithFormat();
			}
			if(!(breakTimeStart1==null || breakTimeStart1.v()==0 || breakTimeEnd1 == null || breakTimeEnd1.v()==0)) {
				result += " ";
				result += I18NText.getText("CMM045_251");
				result += breakTimeStart1 == null ? "" : breakTimeStart1.getInDayTimeWithFormat();
				result += I18NText.getText("CMM045_100");
				result += breakTimeEnd1 == null ? "" : breakTimeEnd1.getInDayTimeWithFormat();
			}
		}
		// 申請理由内容　＝　申請内容の申請理由
		String appReasonContent = this.getAppReasonContent(
				appReasonDisAtr, 
				appReason, 
				null, 
				application.getOpAppStandardReasonCD().orElse(null), 
				appType, 
				Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public ListOfApplication createEachAppData(Application application, String companyID, List<WorkTimeSetting> lstWkTime,
			List<WorkType> lstWkType, List<AttendanceNameItem> attendanceNameItemLst, ApplicationListAtr mode, ApprovalListDisplaySetting approvalListDisplaySetting,
			ListOfApplication listOfApp, Map<String, List<ApprovalPhaseStateImport_New>> mapApproval, int device,
			AppListExtractCondition appListExtractCondition, List<String> agentLst, Map<String, Pair<Integer, Integer>> cacheTime36) {
		if(device == ApprovalDevice.PC.value) {
			// ドメインモデル「申請」．申請種類をチェック (Check Domain「Application.ApplicationType
			switch (application.getAppType()) {
			case COMPLEMENT_LEAVE_APPLICATION:
				// 振休振出申請データを作成( Tạo data application nghỉ bù làm bù)
				CompLeaveAppDataOutput compLeaveAppDataOutput = appContentDetailCMM045.getContentComplementLeave(
						application, 
						companyID, 
						lstWkType, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045);
				listOfApp.setAppContent(compLeaveAppDataOutput.getContent());
				listOfApp.setOpComplementLeaveApp(Optional.of(compLeaveAppDataOutput.getComplementLeaveAppLink()));
				break;
			case ABSENCE_APPLICATION:
				// 申請一覧リスト取得休暇 (Ngày nghỉ lấy  Application list)
				String contentApplyForLeave = appContentDetailCMM045.getContentApplyForLeave(
						application, 
						companyID, 
						lstWkType, 
						approvalListDisplaySetting.getAppReasonDisAtr(),
						ScreenAtr.CMM045);
				listOfApp.setAppContent(contentApplyForLeave);
				break;
			case GO_RETURN_DIRECTLY_APPLICATION:
				// 直行直帰申請データを作成 ( Tạo dữ liệu đơn xin đi làm, về nhà thẳng)
				String contentGoBack = appContentDetailCMM045.getContentGoBack(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						lstWkTime, 
						lstWkType, 
						ScreenAtr.CMM045);
				listOfApp.setAppContent(contentGoBack);
				break;
			case WORK_CHANGE_APPLICATION:
				// 勤務変更申請データを作成
				String contentWorkChange = appContentDetailCMM045.getContentWorkChange(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						lstWkTime, 
						lstWkType, 
						companyID);
				listOfApp.setAppContent(contentWorkChange);
				break;
			case OVER_TIME_APPLICATION:
				// 残業申請データを作成
				AppOvertimeDataOutput appOvertimeDataOutput = appContentDetailCMM045.createOvertimeContent(
						application, 
						lstWkType, 
						lstWkTime, 
						attendanceNameItemLst, 
						appListExtractCondition.getAppListAtr(), 
						approvalListDisplaySetting, 
						companyID, 
						cacheTime36,
						ScreenAtr.CMM045);
				listOfApp.setAppContent(appOvertimeDataOutput.getAppContent());
				// 申請一覧.申請種類表示＝取得した申請種類表示(ApplicationList. AppTypeDisplay = AppTypeDisplay đã get)
				listOfApp.setOpAppTypeDisplay(appOvertimeDataOutput.getOpAppTypeDisplay());
				// 申請一覧．背景色　＝　取得した背景色(ApplicationList.màu nền = màu nền đã get)
				listOfApp.setOpBackgroundColor(Optional.ofNullable(appOvertimeDataOutput.getBackgroundColor()));
				break;
			case HOLIDAY_WORK_APPLICATION:
				// 休出時間申請データを作成
				AppHolidayWorkDataOutput appHolidayWorkDataOutput = appContentDetailCMM045.createHolidayWorkContent(
						application, 
						lstWkType, 
						lstWkTime, 
						attendanceNameItemLst, 
						appListExtractCondition.getAppListAtr(), 
						approvalListDisplaySetting, 
						companyID,
						cacheTime36,
						ScreenAtr.CMM045);
				listOfApp.setAppContent(appHolidayWorkDataOutput.getAppContent());
				// 申請一覧．背景色　＝　取得した背景色(ApplicationList.màu nền = màu nền đã get)
				listOfApp.setOpBackgroundColor(Optional.ofNullable(appHolidayWorkDataOutput.getBackgroundColor()));
				break;
			case BUSINESS_TRIP_APPLICATION:
				// 出張申請データを作成(Tạo data của 出張申請 )
				String contentBusinessTrip = appContentDetailCMM045.createBusinessTripData(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID);
				listOfApp.setAppContent(contentBusinessTrip);
				break;
			case OPTIONAL_ITEM_APPLICATION:
				// 任意申請データを作成(tạo data của任意申請 )
				String contentOptionalItemApp = appContentDetailCMM045.createOptionalItemApp(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID);
				listOfApp.setAppContent(contentOptionalItemApp);
				break;
			case STAMP_APPLICATION:
				// 打刻申請データを作成(tạo data của打刻申請 )
				AppStampDataOutput appStampDataOutput = appContentDetailCMM045.createAppStampData(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID, 
						null);
				listOfApp.setAppContent(appStampDataOutput.getAppContent());
				// 申請一覧.申請種類表示＝取得した申請種類表示(ApplicationList.AppTypeDisplay= AppTypeDisplay đã get)
				listOfApp.setOpAppTypeDisplay(appStampDataOutput.getOpAppTypeDisplay());
				break;
			case ANNUAL_HOLIDAY_APPLICATION:
				// 時間休暇申請データを作成(tạo data của時間休暇申請 )
				String contentAnnualHoliday = appContentDetailCMM045.createAnnualHolidayData(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID);
				listOfApp.setAppContent(contentAnnualHoliday);
				break;
			case EARLY_LEAVE_CANCEL_APPLICATION:
				// 遅刻早退取消申請データを作成(tạo data của 遅刻早退取消申請)
				String contentArrivedLateLeaveEarly = appContentDetailCMM045.createArrivedLateLeaveEarlyData(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID);
				listOfApp.setAppContent(contentArrivedLateLeaveEarly);
				break;
			default:
				listOfApp.setAppContent("-1");
				break;
			}
		} else { 
			if (application.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION) {
				
				// 振休振出申請データを作成( Tạo data application nghỉ bù làm bù)
				CompLeaveAppDataOutput compLeaveAppDataOutput = appContentDetailCMM045.getContentComplementLeave(
						application, 
						companyID, 
						lstWkType, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045);
				listOfApp.setAppContent(compLeaveAppDataOutput.getContent());
				listOfApp.setOpComplementLeaveApp(Optional.of(compLeaveAppDataOutput.getComplementLeaveAppLink()));
			}
			
			// アルゴリズム「各申請データを作成（スマホ）」を実行する
			Optional<ApplicationTypeDisplay> opAppDisplay = this.getAppDisplayByMobile(application, listOfApp);
			listOfApp.setOpAppTypeDisplay(opAppDisplay);
		}
		// 承認フェーズList　＝　Input．Map＜ルートインスタンスID、承認フェーズList＞を取得(ApprovalPhaseList= Input．Map＜get RootInstanceID, ApprovalPhaseList>)
		listOfApp.setOpApprovalPhaseLst(Optional.of(mapApproval.get(application.getAppID())));
		// 申請一覧．承認状況照会　＝　承認状況照会内容(AppList.ApproveStatusRefer =ApproveStatusReferContents )
		listOfApp.setOpApprovalStatusInquiry(Optional.of(this.getApprovalStatusInquiryContent(listOfApp.getOpApprovalPhaseLst().get())));
		
		// アルゴリズム「反映状態を取得する」を実行する(Thực hiện thuật toán [lấy trạng thái phản ánh])
		ReflectedState reflectedState = application.getAppReflectedState();
		String reflectedStateString = reflectedState.name;
		if(mode == ApplicationListAtr.APPLICATION) {
			switch (reflectedState) {
			case NOTREFLECTED:
				if(device==ApprovalDevice.PC.value) {
					reflectedStateString = "CMM045_62";
				} else {
					reflectedStateString = "CMMS45_7";
				}
				break;
			case WAITREFLECTION:
				if(device==ApprovalDevice.PC.value) {
					reflectedStateString = "CMM045_63";
				} else {
					reflectedStateString = "CMMS45_8";
				}
				break;
			case REFLECTED:
				if(device==ApprovalDevice.PC.value) {
					reflectedStateString = "CMM045_64";
				} else {
					reflectedStateString = "CMMS45_9";
				}
				break;
			case DENIAL:
				if(device==ApprovalDevice.PC.value) {
					reflectedStateString = "CMM045_65";
				} else {
					reflectedStateString = "CMMS45_11";
				}
				break;
			case REMAND:
				if(device==ApprovalDevice.PC.value) {
					reflectedStateString = "CMM045_66";
				} else {
					reflectedStateString = "CMMS45_36";
				}
				break;
			case CANCELED:
				if(device==ApprovalDevice.PC.value) {
					reflectedStateString = "CMM045_67";
				} else {
					reflectedStateString = "CMMS45_10";
				}
				break;
			default:
				break;
			}
		}
		if(mode == ApplicationListAtr.APPROVER) {
			ApprovalBehaviorAtrImport_New phaseAtr = null;
			ApprovalBehaviorAtrImport_New frameAtr = null;
			String loginID = AppContexts.user().employeeId();
			List<ApprovalPhaseStateImport_New> listPhase = listOfApp.getOpApprovalPhaseLst().orElse(Collections.emptyList());
			boolean isBreak = false;
			for(ApprovalPhaseStateImport_New phase : listPhase) {
				if(isBreak) {
					break;
				}
				for(ApprovalFrameImport_New frame : phase.getListApprovalFrame()) {
					if(isBreak) {
						break;
					}
					for(ApproverStateImport_New approver : frame.getListApprover()) {
						boolean isMapWithApprover = false;
						boolean isMapWithAgent = false;
						if(phase.getApprovalAtr()==ApprovalBehaviorAtrImport_New.REMAND ||
							(phase.getApprovalAtr()==ApprovalBehaviorAtrImport_New.UNAPPROVED && approver.getApprovalAtr()==ApprovalBehaviorAtrImport_New.UNAPPROVED)) {
							isMapWithApprover = approver.getApproverID().equals(loginID);
							isMapWithAgent = agentLst.contains(approver.getApproverID());
						} else {
							isMapWithApprover = approver.getApproverID().equals(loginID);
							isMapWithAgent = (Strings.isNotBlank(approver.getAgentID()) && approver.getAgentID().equals(loginID));
						}
						if (!isMapWithApprover && !isMapWithAgent) {
							continue;
						}
						Optional<ApprovalPhaseStateImport_New> opPhaseBeforeNotApproved = Optional.empty();
						if(listPhase.size()!=1) {
							opPhaseBeforeNotApproved = listPhase.stream()
									.filter(x -> x.getPhaseOrder() > phase.getPhaseOrder())
									.filter(x -> x.getApprovalAtr()!=ApprovalBehaviorAtrImport_New.APPROVED).findAny();
						}
						if(opPhaseBeforeNotApproved.isPresent()) {
							continue;
						}
						// 承認枠　＝　ロープの承認枠(ApprovalFrame= ApproveFrame dang loop)
						// 承認フェーズ　＝　ロープの承認フェーズ(ApprovalPhase = ApprovalPhase dang loop)
						// 承認枠の承認状態　＝　承認枠．承認区分(ApprovalStatus của ApprovalFrame = ApprovalFrame. ApprovalAtr)
						phaseAtr = phase.getApprovalAtr();
						frameAtr = approver.getApprovalAtr();
						isBreak = true;
						listOfApp.setOpApprovalFrameStatus(Optional.of(frameAtr.value));
						// 反映状態　＝　反映状態（承認一覧モード）//Trạng thái phản ánh= trạng thái phản ánh(mode danh sách approve)
						reflectedStateString = this.getReflectStatusApprovalListMode(reflectedState, phaseAtr, frameAtr, device);
					}
				}
			}
		}
		// 申請一覧．反映状態　＝　申請の反映状態(ApplicationList. trạng thái phản ánh = trạng thái phản ánh của đơn xin)
		listOfApp.setReflectionStatus(reflectedStateString);
		return listOfApp;
	}

	@Override
	public String getApprovalStatusInquiryContent(List<ApprovalPhaseStateImport_New> approvalPhaseLst) {
		// 承認状況照会　＝　String.Empty (tham khảo tình trạng approval)
		String result = Strings.EMPTY;
		for(ApprovalPhaseStateImport_New phase : approvalPhaseLst) {
			if(phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.UNAPPROVED || 
					phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.REMAND ||
					phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.ORIGINAL_REMAND) {
				// 承認状況照会　+＝　”－”  // (tham khảo tình trạng approval)
				result += "－";
			}
			if(phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.APPROVED) {
				// 承認状況照会　+＝　”〇” // (tham khảo tình trạng approval)
				result += "〇";
			}
			if(phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.DENIAL) {
				// 承認状況照会　+＝　”×” // (tham khảo tình trạng approval)
				result += "×";
			}
		}
		return result;
	}

	@Override
	public String getReflectStatusApprovalListMode(ReflectedState reflectedState,
			ApprovalBehaviorAtrImport_New phaseAtr, ApprovalBehaviorAtrImport_New frameAtr, int device) {
		String result = Strings.EMPTY;
		// 反映状態(trạng thái phản ánh)　＝　PC：#CMM045_62スマホ：#CMMS45_7
		if(device==ApprovalDevice.PC.value) {
			result = "CMM045_62";
		} else {
			result = "CMMS45_7";
		}
		// 反映状態(trạng thái phản ánh)　＝　PC：#CMM045_64スマホ：#CMMS45_9
		if(reflectedState==ReflectedState.REFLECTED) {
			if(device==ApprovalDevice.PC.value) {
				result = "CMM045_64";
			} else {
				result = "CMMS45_9";
			}
			return result;
		}
		// 反映状態　＝　PC：#CMM045_65スマホ：#CMMS45_11
		if(reflectedState==ReflectedState.DENIAL) {
			if(device==ApprovalDevice.PC.value) {
				result = "CMM045_65";
			} else {
				result = "CMMS45_11";
			}
			return result;
		}
		// 反映状態　＝　PC：#CMM045_66スマホ：#CMMS45_36
		if(reflectedState==ReflectedState.NOTREFLECTED && phaseAtr==ApprovalBehaviorAtrImport_New.REMAND) {
			if(device==ApprovalDevice.PC.value) {
				result = "CMM045_66";
			} else {
				result = "CMMS45_36";
			}
			return result;
		}
		// 反映状態　＝　PC：#CMM045_67スマホ：#CMMS45_10
		if(reflectedState==ReflectedState.CANCELED) {
			if(device==ApprovalDevice.PC.value) {
				result = "CMM045_67";
			} else {
				result = "CMMS45_10";
			}
			return result;
		}
		// 反映状態(trạng thái phản ánh)　＝　PC：#CMM045_63スマホ：#CMMS45_8
		boolean condition1 = 
				(reflectedState==ReflectedState.WAITREFLECTION) ||
				(phaseAtr==ApprovalBehaviorAtrImport_New.APPROVED) ||
				(frameAtr==ApprovalBehaviorAtrImport_New.APPROVED);
		if(condition1) {
			if(device==ApprovalDevice.PC.value) {
				result = "CMM045_63";
			} else {
				result = "CMMS45_8";
			}
			return result;
		}
		return result;
	}

	@Override
	public String getOptionalItemAppContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			OptionalItemApplicationTypeName optionalItemApplicationTypeName, List<OptionalItemOutput> optionalItemOutputLst, 
			ApplicationType appType, AppStandardReasonCode appStandardReasonCD) {
		String result = Strings.EMPTY;
		String paramString = Strings.EMPTY;
		// ScreenID
		if(screenAtr != ScreenAtr.KAF018 && screenAtr != ScreenAtr.CMM045) {
			// @＝改行
			paramString = "\n";
		} else {
			// @＝”　”
			paramString = "";
		}
		// 申請内容＝任意申請種類名
		result = optionalItemApplicationTypeName.v();
		for(OptionalItemOutput optionalItemOutput : optionalItemOutputLst) {
			// 申請内容＋＝@＋”　”＋<List>任意項目名称＋”　”＋<List>値＋<List>単位
			result += paramString + " " + optionalItemOutput.getOptionalItemName().v() + " ";
			if(optionalItemOutput.getAnyItemValue().getTime().isPresent()) {
				result += new TimeWithDayAttr(optionalItemOutput.getAnyItemValue().getTime().get().v()).getRawTimeWithFormat() + optionalItemOutput.getUnit().v();
				continue;
			}
			if(optionalItemOutput.getAnyItemValue().getTimes().isPresent()) {
				result += optionalItemOutput.getAnyItemValue().getTimes().get().v() + optionalItemOutput.getUnit().v();
				continue;
			}
			if(optionalItemOutput.getAnyItemValue().getAmount().isPresent()) {
				result += optionalItemOutput.getAnyItemValue().getAmount().get().v() + optionalItemOutput.getUnit().v();
				continue;
			}
		}
		// アルゴリズム「申請内容の申請理由」を実行する
		String appReasonContent = this.getAppReasonContent(appReasonDisAtr, appReason, screenAtr, appStandardReasonCD, appType, Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public String getOvertimeHolidayWorkContent(AppOverTimeData appOverTimeData, AppHolidayWorkData appHolidayWorkData,
			ApplicationType appType, PrePostAtr prePostAtr, ApplicationListAtr applicationListAtr, AppReason appReason, 
			DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, boolean actualStatus, Application application) {
		// 申請内容　＝　String．Empty
		String result = "";
		if(prePostAtr==PrePostAtr.POSTERIOR && applicationListAtr==ApplicationListAtr.APPROVER) {
			// 申請内容　+＝　#CMM045_272
			result += I18NText.getText("CMM045_272");
		}
		// 申請内容　+＝「申請の内容（事前、事後内容）」を取得
		result += this.getDetailApplicationPrePost(appType, appOverTimeData, appHolidayWorkData);
		if(!(applicationListAtr==ApplicationListAtr.APPROVER && prePostAtr==PrePostAtr.POSTERIOR)) {
			Integer excessTime = 0;
			Integer excessTimeNumber = 0;
			if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
				if(appHolidayWorkData.getExcessTime()!=null) {
					excessTime = appHolidayWorkData.getExcessTime();
				}
				if(appHolidayWorkData.getExcessTimeNumber()!=null) {
					excessTimeNumber = appHolidayWorkData.getExcessTimeNumber();
				}
			} else {
				if(appOverTimeData.getExcessTime()!=null) {
					excessTime = appOverTimeData.getExcessTime();
				}
				if(appOverTimeData.getExcessTimeNumber()!=null) {
					excessTimeNumber = appOverTimeData.getExcessTimeNumber();
				}
			}
			if(excessTime > 0) {
				// 申請内容を改行(thêm kí tự xuống dòng)
				result += "\n";
				// 申請内容　+＝　#CMM045_282 +　”　”　+　時間外時間データ．「実績時間 + 申請時間」　+　#CMM045_283　+　{0}回
				result += I18NText.getText("CMM045_282") + new TimeWithDayAttr(excessTime).getRawTimeWithFormat() + " " + 
						I18NText.getText("CMM045_283") + I18NText.getText("CMM045_284", excessTimeNumber.toString());
			}
		} else {
			if((appType==ApplicationType.OVER_TIME_APPLICATION && appOverTimeData.getOpPreAppData().isPresent()) ||
				(appType==ApplicationType.HOLIDAY_WORK_APPLICATION && appHolidayWorkData.getOpPreAppData().isPresent())){
				// 申請内容　を改行(thêm ký tự xuống dòng)
				result += "\n";
				// 申請内容　+＝　#CMM045_273 (nội dung đơn xin)
				result += I18NText.getText("CMM045_273");
				// 申請内容　+＝　「申請の内容（事前、事後内容）」を取得
				result += this.getDetailApplicationPrePost(
						appType, 
						appOverTimeData==null ? null : appOverTimeData.getOpPreAppData().orElse(null), 
						appHolidayWorkData==null ? null : appHolidayWorkData.getOpPreAppData().orElse(null));
			} else {
				// 申請内容　＋＝CMM045_273＋CMM045_306
				result += "\n" + I18NText.getText("CMM045_273") + I18NText.getText("CMM045_306");
			}
			// Input実績状態
			if(!actualStatus) {
				// 申請内容　＋＝　#CMM045_274
				result += "\n" + I18NText.getText("CMM045_274");
//				if(actualStatus==ActualStatus.NO_ACTUAL) {
//					// 申請内容　＋＝#CMM045_306
//					result += I18NText.getText("CMM045_306");
//				} else {
//					// 申請内容　＋＝#CMM045_305
//					result += I18NText.getText("CMM045_305");
//				}
				// 申請内容　＋＝#CMM045_306
				result += I18NText.getText("CMM045_306");
			} else {
				// 申請内容　+＝　「事後申請の実績データの内容」を取得
				if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
					result += this.getContentActualStatusCheckResult(appHolidayWorkData.getOpPostAppData().orElse(null), ApplicationType.HOLIDAY_WORK_APPLICATION);
				} else {
					result += this.getContentActualStatusCheckResult(appOverTimeData.getOpPostAppData().orElse(null), ApplicationType.OVER_TIME_APPLICATION);
				}
				// 申請内容　+＝　「勤怠項目の内容」を取得
				List<AppTimeFrameData> appTimeFrameDataLst = Collections.emptyList();
				if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
					appTimeFrameDataLst = appHolidayWorkData.getOpPostAppData().map(x -> x.getAppTimeFrameDataLst()).orElse(Collections.emptyList());
				} else {
					appTimeFrameDataLst = appOverTimeData.getOpPostAppData().map(x -> x.getAppTimeFrameDataLst()).orElse(Collections.emptyList());
				}
				result += this.getDisplayFrame(appType, appTimeFrameDataLst);
			}
			String excessTime = "";
			String excessTimeNumber = "";
			if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
				if(appHolidayWorkData.getExcessTime() > 0) {
					// 申請内容　+＝　#CMM045_282 +　”　”　+　時間外時間データ．「実績時間 + 申請時間」　+　#CMM045_283　+　{0}回
					if(appHolidayWorkData.getExcessTime()!=null) {
						excessTime = new TimeWithDayAttr(appHolidayWorkData.getExcessTime()).getRawTimeWithFormat();
					}
					if(appHolidayWorkData.getExcessTimeNumber()!=null) {
						excessTimeNumber = appHolidayWorkData.getExcessTimeNumber().toString();
					}
					result += I18NText.getText("CMM045_282") + excessTime + " " + I18NText.getText("CMM045_283") + I18NText.getText("CMM045_284", excessTimeNumber);
				}
			} else {
				if(appOverTimeData.getExcessTime() > 0) {
					// 申請内容　+＝　#CMM045_282 +　”　”　+　時間外時間データ．「実績時間 + 申請時間」　+　#CMM045_283　+　{0}回
					if(appOverTimeData.getExcessTime()!=null) {
						excessTime = new TimeWithDayAttr(appOverTimeData.getExcessTime()).getRawTimeWithFormat();
					}
					if(appOverTimeData.getExcessTimeNumber()!=null) {
						excessTimeNumber = appOverTimeData.getExcessTimeNumber().toString();
					}
					result += I18NText.getText("CMM045_282") + excessTime + " " + I18NText.getText("CMM045_283") + I18NText.getText("CMM045_284", excessTimeNumber);
				}
			}
		}
		//  複数回残業申請を追加 2021/10　申請⑧EA4134
		//・申請種類＝残業申請
		//・申請データ(残業申請).残業区分＝複数回残業
		if(appType == ApplicationType.OVER_TIME_APPLICATION && appOverTimeData.getOvertimeAtr() == ApplicationTypeDisplay.OVERTIME_MULTIPLE_TIME.value ){
			result = this.getOvertimeApplicationDataMultiTime(
					appReasonDisAtr,
					appOverTimeData,
					screenAtr,
					result);
		}else {
			// 申請理由内容　＝　申請内容の申請理由
			String appReasonContent = this.getAppReasonContent(
					appReasonDisAtr,
					appReason,
					screenAtr,
					application.getOpAppStandardReasonCD().orElse(null),
					appType,
					Optional.empty());
			// 申請内容を改行(xuống dòng nội dung đơn xin)
			if(Strings.isNotBlank(appReasonContent)) {
				result += "\n" + appReasonContent;
			}
		}


		return result;
	}
	
	/**
	 * 申請の内容（事前、事後内容）
	 * @return
	 */
	private String getDetailApplicationPrePost(ApplicationType appType, AppOverTimeData appOverTimeData, AppHolidayWorkData appHolidayWorkData) {
		String companyID = AppContexts.user().companyId();
		// 申請内容　＝　String．Empty
		String result = "";
		if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
			// 申請内容　+＝　申請データ．勤務種類名称
			result += appHolidayWorkData.getOpWorkTypeName().orElse("");
			// 申請内容　+＝　申請データ．就業時間帯名称
			result += appHolidayWorkData.getOpWorkTimeName().orElse("");
			if(Strings.isNotBlank(result)) {
				result += " ";
			}
		}
		if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
			// 申請内容　+＝　申請データ．勤務開始時間
			String startTime = appHolidayWorkData.getStartTime()==null ? "" : new TimeWithDayAttr(appHolidayWorkData.getStartTime()).getFullText();
			result += startTime;
			if(Strings.isNotBlank(result)) {
				result += " ";
			}
			// 申請内容　+＝　#CMM045_100+　申請データ．勤務終了時間
			String endTime = appHolidayWorkData.getEndTime()==null ? "" : new TimeWithDayAttr(appHolidayWorkData.getEndTime()).getFullText();
			if(Strings.isNotBlank(startTime) && Strings.isNotBlank(endTime)) {
				result += I18NText.getText("CMM045_100");
			}
			result += endTime;
			if(Strings.isNotBlank(result)) {
				result += " ";
			}
		} else {
			// 申請内容　+＝　申請データ．勤務開始時間
			String startTime = appOverTimeData.getStartTime()==null ? "" : new TimeWithDayAttr(appOverTimeData.getStartTime()).getFullText();
			result += startTime;
			if(Strings.isNotBlank(result)) {
				result += " ";
			}
			// 申請内容　+＝　#CMM045_100+　申請データ．勤務終了時間
			String endTime = appOverTimeData.getEndTime()==null ? "" : new TimeWithDayAttr(appOverTimeData.getEndTime()).getFullText();
			if(Strings.isNotBlank(startTime) && Strings.isNotBlank(endTime)) {
				result += I18NText.getText("CMM045_100");
			}
			result += endTime;
			if(Strings.isNotBlank(result)) {
				result += " ";
			}
		}
		// 勤怠項目の内容(frameTime)
		List<AppTimeFrameData> appTimeFrameDataLst = Collections.emptyList();
		if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
			appTimeFrameDataLst = appHolidayWorkData.getAppTimeFrameDataLst();
		} else {
			appTimeFrameDataLst = appOverTimeData.getAppTimeFrameDataLst();
		}
		result += this.getDisplayFrame(appType, appTimeFrameDataLst);
		if(Strings.isNotBlank(result)) {
			result += " ";
		}
		return result;
	}
	
	/**
	 * 勤怠項目の内容
	 * @param appType
	 * @param appHolidayWork
	 * @param appOverTime
	 * @return
	 */
	private String getDisplayFrame(ApplicationType appType, List<AppTimeFrameData> appTimeFrameDataLst) {
		if(CollectionUtil.isEmpty(appTimeFrameDataLst)) {
			return "";
		}
		// 勤怠項目の内容　＝　String.Empty(nội dung của AttendanceItem =String.Empty)
		String result = "";
		result = appTimeFrameDataLst.stream().sorted(Comparator.comparing((AppTimeFrameData x) -> {
			String frameNoStr = String.format("%02d", x.getAttendanceNo());
			return String.valueOf(x.getAttendanceType().value) + frameNoStr;
		})).map(x -> {
			// 勤怠項目の内容　+＝申請時間データ．勤怠名称　+　申請時間データ．申請時間 (nội dung AttendanceItem +＝ ApplicationTimeData. AttendanceName 　+　ApplicationTimedata. ApplicationTime)
			return x.getAttendanceName() + new TimeWithDayAttr(x.getApplicationTime()).getRawTimeWithFormat();
		}).collect(Collectors.joining(" "));
		return result;
	}
	
	/**
	 * 事後申請の実績データの内容
	 * @param actualStatusCheckResult
	 * @return
	 */
	private String getContentActualStatusCheckResult(PostAppData postAppData, ApplicationType appType) {
		// 実績内容　＝　#CMM045_274 (nội dung thực tế ＝　#CMM045_274)
		String result = "\n" + I18NText.getText("CMM045_274");
		if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
			// 実績内容　+＝　事後申請の実績データ．勤務種類名称 (nội dung thực tế +＝　data thực tế của đơn xin sau . WorktypeName)
			result += postAppData.getWorkTypeName() + " ";
			// 実績内容　+＝　事後申請の実績データ．就業時間帯名称 (nội dung thực tế　+＝ data thực tế của đơn xin sau . WorkTimeName )
			result += postAppData.getOpWorkTimeName().orElse("") + " "; 
		}
		// 実績内容　+＝　事後申請の実績データ．開始時間 (Nội dung thực tế +＝ Data thực tế của đơn xin sau . StartTime)
		String startTime = postAppData.getStartTime()==null ? "" : new TimeWithDayAttr(postAppData.getStartTime()).getFullText();
		result += startTime;
		if(Strings.isNotBlank(result)) {
			result += " ";
		}
		// 実績内容　+＝　#CMM045_100　+　事後申請の実績データ．終了時間 (Nội dung thực tế +＝ 　#CMM045_100　+　Data thực tế của đơn xin sau . EndTime)
		String endTime = postAppData.getEndTime()==null ? "" : new TimeWithDayAttr(postAppData.getEndTime()).getFullText();
		if(Strings.isNotBlank(startTime) && Strings.isNotBlank(endTime)) {
			result += I18NText.getText("CMM045_100");
		}
		result += endTime;
		if(Strings.isNotBlank(result)) {
			result += " ";
		}
		return result;
	}

	@Override
	public OvertimeHolidayWorkActual getOvertimeHolidayWorkActual(String companyID, Application application, 
			List<WorkType> workTypeLst, List<WorkTimeSetting> workTimeSettingLst, List<AttendanceNameItem> attendanceNameItemLst,
			AppOverTime appOverTime, AppHolidayWork appHolidayWork, WorkTypeCode workType, WorkTimeCode workTime) {
		// 申請.申請種類(Application.AppType)
		if(application.getAppType()!=ApplicationType.OVER_TIME_APPLICATION && application.getAppType()!=ApplicationType.HOLIDAY_WORK_APPLICATION) {
			return null;
		}
		OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet = null;
		ApplicationDetailSetting applicationDetailSetting = null;
		Optional<CalcStampMiss> calStampMiss = Optional.empty();
		if(application.getAppType()==ApplicationType.OVER_TIME_APPLICATION) {
			// ドメインモデル「残業申請設定」を取得する
			OvertimeAppSet overtimeAppSet = overtimeAppSetRepository.findSettingByCompanyId(companyID).get();
			overtimeLeaveAppCommonSet = overtimeAppSet.getOvertimeLeaveAppCommonSet();
			applicationDetailSetting = overtimeAppSet.getApplicationDetailSetting();
		} else {
			// ドメインモデル「休日出勤申請設定」を取得する
			HolidayWorkAppSet holidayWorkAppSet = holidayWorkAppSetRepository.findSettingByCompany(companyID).get();
			overtimeLeaveAppCommonSet = holidayWorkAppSet.getOvertimeLeaveAppCommonSet();
			applicationDetailSetting = holidayWorkAppSet.getApplicationDetailSetting();
			calStampMiss = Optional.of(holidayWorkAppSet.getCalcStampMiss());
		}
		// アルゴリズム「事前内容の取得」を実行する
		PreAppContentDisplay preAppContentDisplay = new PreAppContentDisplay(application.getAppDate().getApplicationDate(), Optional.empty(), Optional.empty());
		List<PreAppContentDisplay> preAppContentDisplayLst = collectAchievement.getPreAppContents(
				companyID, 
				application.getEmployeeID(), 
				Arrays.asList(application.getAppDate().getApplicationDate()), 
				application.getAppType(),
				Optional.ofNullable(appOverTime != null ? appOverTime.getOverTimeClf() : null)
		);
		if(!CollectionUtil.isEmpty(preAppContentDisplayLst)) {
			preAppContentDisplay = preAppContentDisplayLst.get(0);
		}
		AppOverTimeData appOverTimeData = null;
		AppHolidayWorkData appHolidayWorkData = null;
		if(preAppContentDisplay.getApOptional().isPresent()) {
			AppOverTime appOverTimePre = preAppContentDisplay.getApOptional().get();
			appOverTimeData = new AppOverTimeData(
					appOverTimePre.getWorkHoursOp().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getStartTime().v()).orElse(null)).orElse(null), 
					appOverTimePre.getOverTimeClf().value, 
					appOverTimePre.getWorkHoursOp().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getEndTime().v()).orElse(null)).orElse(null), 
					appOverTimePre.getAppID(),
					null,
					null,
					appOverTimePre.getApplicationTime().getFlexOverTime().map(x -> x.v()), 
					appOverTimePre.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()), 
					workTypeLst.stream().filter(x -> x.getWorkTypeCode().equals(appOverTimePre.getWorkInfoOp().map(y -> y.getWorkTypeCode()).orElse(null)))
						.findAny().map(x -> x.getName().v()), 
					Optional.empty(), 
					Optional.empty(), 
					appOverTimePre.getApplicationTime().getOverTimeShiftNight().map(x -> x.getOverTimeMidNight().v()), 
					appOverTimePre.getWorkInfoOp().map(x -> x.getWorkTimeCodeNotNull().map(y -> y.v())).orElse(Optional.empty()), 
					workTimeSettingLst.stream().filter(x -> x.getWorktimeCode().equals(appOverTimePre.getWorkInfoOp().map(y -> y.getWorkTimeCodeNotNull().orElse(null)).orElse(null)))
						.findAny().map(x -> x.getWorkTimeDisplayName().getWorkTimeName().v()), 
					Optional.empty(), 
					appOverTimePre.getApplicationTime().getApplicationTime().stream().map(x -> new AppTimeFrameData(
							null, 
							x.getFrameNo().v(), 
							x.getAttendanceType(), 
							attendanceNameItemLst.stream().filter(y -> y.getAttendanceNo()==x.getFrameNo().v()&&y.getAttendanceType()==x.getAttendanceType())
								.findAny().map(y -> y.getAttendanceName()).orElse(""), 
							null, 
							x.getApplicationTime().v())).collect(Collectors.toList()));
		}
		if(preAppContentDisplay.getAppHolidayWork().isPresent()) {
			AppHolidayWork appHolidayWorkPre = preAppContentDisplay.getAppHolidayWork().get();
			appHolidayWorkData = new AppHolidayWorkData(
					appHolidayWorkPre.getWorkingTimeList().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getStartTime().v()).orElse(null)).orElse(null), 
					appHolidayWorkPre.getWorkingTimeList().map(x -> x.stream().filter(y -> y.getWorkNo().v()==1).findAny().map(y -> y.getTimeZone().getEndTime().v()).orElse(null)).orElse(null), 
					appHolidayWorkPre.getAppID(),
					null,
					null,
					appHolidayWorkPre.getApplicationTime().getApplicationTime().stream().map(x -> new AppTimeFrameData(
							null, 
							x.getFrameNo().v(), 
							x.getAttendanceType(), 
							attendanceNameItemLst.stream().filter(y -> y.getAttendanceNo()==x.getFrameNo().v()&&y.getAttendanceType()==x.getAttendanceType())
								.findAny().map(y -> y.getAttendanceName()).orElse(""), 
							null, 
							x.getApplicationTime().v())).collect(Collectors.toList()), 
					Optional.of(appHolidayWorkPre.getWorkInformation().getWorkTypeCode().v()), 
					workTypeLst.stream().filter(x -> x.getWorkTypeCode().equals(appHolidayWorkPre.getWorkInformation().getWorkTypeCode()))
						.findAny().map(x -> x.getName().v()), 
					Optional.empty(), 
					Optional.empty(), 
					appHolidayWorkPre.getWorkInformation().getWorkTimeCodeNotNull().map(y -> y.v()), 
					workTimeSettingLst.stream().filter(x -> x.getWorktimeCode().equals(appHolidayWorkPre.getWorkInformation().getWorkTimeCodeNotNull().orElse(null)))
						.findAny().map(x -> x.getWorkTimeDisplayName().getWorkTimeName().v()), 
					Optional.empty());
		}
		// アルゴリズム「実績内容の取得」を実行する
		Optional<ActualContentDisplay> actualContentDisplay = Optional.empty();
		List<ActualContentDisplay> actualContentDisplayLst = collectAchievement.getAchievementContents(
				companyID, 
				application.getEmployeeID(), 
				Arrays.asList(application.getAppDate().getApplicationDate()), 
				application.getAppType());
		if(!CollectionUtil.isEmpty(actualContentDisplayLst)) {
			actualContentDisplay = Optional.of(actualContentDisplayLst.get(0));
		}
		// アルゴリズム「07-02_実績取得・状態チェック」を実行する(thực hiện thuật toán「07-02_get thực tế・check trạng thái」 )
		List<DeductionTime> breakTimes = Collections.emptyList();
		if(application.getAppType()==ApplicationType.OVER_TIME_APPLICATION) {
			if(preAppContentDisplay.getApOptional().isPresent()) {
				breakTimes = preAppContentDisplay.getApOptional().get().getBreakTimeOp()
						.map(x -> x.stream().map(y -> new DeductionTime(y.getTimeZone().getStartTime(), y.getTimeZone().getEndTime())).collect(Collectors.toList()))
						.orElse(Collections.emptyList());
			}
		} else {
			if(preAppContentDisplay.getAppHolidayWork().isPresent()) {
				breakTimes = preAppContentDisplay.getAppHolidayWork().get().getBreakTimeList()
						.map(x -> x.stream().map(y -> new DeductionTime(y.getTimeZone().getStartTime(), y.getTimeZone().getEndTime())).collect(Collectors.toList()))
						.orElse(Collections.emptyList());
			}
		}
		ApplicationTime achiveOp = preActualColorCheck.checkStatus(
				companyID, 
				application.getEmployeeID(), 
				application.getAppDate().getApplicationDate(), 
				application.getAppType(),
				workType, 
				workTime, 
				overtimeLeaveAppCommonSet.getOverrideSet(),
				Optional.empty(), 
				breakTimes,
				actualContentDisplay);
		PostAppData postAppData = null;
		if(actualContentDisplay.isPresent()) {
			if(actualContentDisplay.get().getOpAchievementDetail().isPresent()) {
				AchievementDetail achievementDetail = actualContentDisplay.get().getOpAchievementDetail().get();
				Integer calculationMidnightOutsideWork = null;
				if(application.getAppType()==ApplicationType.OVER_TIME_APPLICATION) {
					calculationMidnightOutsideWork = achievementDetail.getOpOvertimeMidnightTime().map(x -> x.v()).orElse(null);
				} else {
					if(achievementDetail.getOpInlawHolidayMidnightTime().isPresent() ||
							achievementDetail.getOpOutlawHolidayMidnightTime().isPresent() ||
							achievementDetail.getOpPublicHolidayMidnightTime().isPresent()) {
						calculationMidnightOutsideWork = 0;
						if(achievementDetail.getOpInlawHolidayMidnightTime().isPresent()) {
							calculationMidnightOutsideWork += achievementDetail.getOpInlawHolidayMidnightTime().map(x -> x.v()).orElse(0);
						}
						if(achievementDetail.getOpOutlawHolidayMidnightTime().isPresent()) {
							calculationMidnightOutsideWork += achievementDetail.getOpOutlawHolidayMidnightTime().map(x -> x.v()).orElse(0);
						}
						if(achievementDetail.getOpPublicHolidayMidnightTime().isPresent()) {
							calculationMidnightOutsideWork += achievementDetail.getOpPublicHolidayMidnightTime().map(x -> x.v()).orElse(0);
						}
					}
				}
				postAppData = new PostAppData(
						achievementDetail.getOpWorkTime().orElse(null), 
						achievementDetail.getWorkTypeCD(), 
						achievementDetail.getOpWorkTypeName().orElse(null), 
						achievementDetail.getOpFlexTime().map(x -> x.v()).orElse(null), 
						calculationMidnightOutsideWork, 
						achievementDetail.getOpLeaveTime().orElse(null), 
						Optional.ofNullable(achievementDetail.getWorkTimeCD()), 
						achievementDetail.getOpWorkTimeName(), 
						achiveOp==null ? Collections.emptyList() : achiveOp.getApplicationTime().stream().map(x -> new AppTimeFrameData(
								null, 
								x.getFrameNo().v(), 
								x.getAttendanceType(), 
								attendanceNameItemLst.stream().filter(y -> y.getAttendanceNo()==x.getFrameNo().v()&&y.getAttendanceType()==x.getAttendanceType())
									.findAny().map(y -> y.getAttendanceName()).orElse(""), 
								null, 
								x.getApplicationTime().v())).collect(Collectors.toList()));
			}
		}
		// アルゴリズム「事前申請・実績の時間超過をチェックする」を実行する
		Optional<ApplicationTime> advanceOp = Optional.empty();
		if(application.getAppType()==ApplicationType.OVER_TIME_APPLICATION) {
			if(preAppContentDisplay.getApOptional().isPresent()) {
				advanceOp = Optional.of(preAppContentDisplay.getApOptional().get().getApplicationTime());
			}
		} else {
			if(preAppContentDisplay.getAppHolidayWork().isPresent()) {
				advanceOp = Optional.of(preAppContentDisplay.getAppHolidayWork().get().getApplicationTime());
			}
		}
		ApplicationTime subsequentOp = application.getAppType()==ApplicationType.OVER_TIME_APPLICATION
				? appOverTime.getApplicationTime() : appHolidayWork.getApplicationTime();
		OverStateOutput overStateOutput = overtimeLeaveAppCommonSet.checkPreApplication(
				EnumAdaptor.valueOf(application.getPrePostAtr().value, PrePostInitAtr.class), 
				advanceOp, 
				Optional.of(subsequentOp), 
				Optional.ofNullable(achiveOp));
		boolean actualStatus = false;
		String backgroundColor = "";
		if(actualContentDisplay.isPresent()) {
			actualStatus = true;
		} else {
			actualStatus = false;
		}
		if(!actualStatus) {
			backgroundColor = "bg-workinh-result-excess";
		} else {
			if(overStateOutput.getAdvanceExcess().isAdvanceExcess() || overStateOutput.getAdvanceExcess().isAdvanceExcessError()) {
				backgroundColor = "bg-pre-application-excess";
			}
			if(overStateOutput.getAchivementExcess().isAdvanceExcess() || overStateOutput.getAchivementExcess().isAdvanceExcessError()) {
				backgroundColor = "bg-workinh-result-excess";
			}
		}
		return new OvertimeHolidayWorkActual(
				appOverTimeData, 
				appHolidayWorkData, 
				postAppData, 
				actualStatus, 
				backgroundColor);
	}

	@Override
	public Optional<ApplicationTypeDisplay> getAppDisplayByMobile(Application application, ListOfApplication listOfApplication) {
		Optional<ApplicationTypeDisplay> result = Optional.empty();	
		String companyId = AppContexts.user().companyId();
		// 申請.申請種類
		if (application.getAppType() == ApplicationType.OVER_TIME_APPLICATION) { // 残業申請の場合
			// ドメインモデル「残業申請」を取得する
			Optional<AppOverTime> apOptional = appOverTimeRepo.find(companyId, application.getAppID());
			if (apOptional.isPresent()) {
				// 申請種類表示＝残業申請.残業区分
				 return result = Optional.of(EnumAdaptor.valueOf(apOptional.get().getOverTimeClf().value, ApplicationTypeDisplay.class));				
			}
		} else if (application.getAppType() == ApplicationType.STAMP_APPLICATION) {
			// 申請.打刻申請モードをチェック
			if (application.getOpStampRequestMode().isPresent()) {
				if (application.getOpStampRequestMode().get() == StampRequestMode.STAMP_ADDITIONAL) {
					return result = Optional.of(ApplicationTypeDisplay.STAMP_ADDITIONAL);
				} else {
					return result = Optional.of(ApplicationTypeDisplay.STAMP_ONLINE_RECORD);
				}
			}
		}
		return result;
	}

	public List<AttendanceNameItem> getAttendanceNameItemLst(String companyID) {
		List<AttendanceNameItem> result = new ArrayList<>();
		// ドメインモデル「残業枠」を取得 ( Lấy domain model 「残業枠」)
		List<OvertimeWorkFrame> overtimeWorkFrameLst = overtimeWorkFrameRepository.getAllOvertimeWorkFrame(companyID);
		for(OvertimeWorkFrame overtimeWorkFrame : overtimeWorkFrameLst) {
			result.add(new AttendanceNameItem(
					overtimeWorkFrame.getOvertimeWorkFrNo().v().intValue(), 
					overtimeWorkFrame.getOvertimeWorkFrName().v(), 
					AttendanceType_Update.NORMALOVERTIME));
		}
		// ドメインモデル「休出枠」を取得(Lấy domain model 「休出枠」)
		List<WorkdayoffFrame> workdayoffFrameLst = workdayoffFrameRepository.getAllWorkdayoffFrame(companyID);
		for(WorkdayoffFrame workdayoffFrame : workdayoffFrameLst) {
			result.add(new AttendanceNameItem(
					workdayoffFrame.getWorkdayoffFrNo().v().intValue(), 
					workdayoffFrame.getWorkdayoffFrName().v(), 
					AttendanceType_Update.BREAKTIME));
		}
		// ドメインモデル「加給時間項目」を取得 (Lấy domain model 「加給時間項目」)
		
		// ドメインモデル「特定加給時間項目」を取得 ( Lấy domain model 「特定加給時間項目」)
		
		return result;
	}

	@Override
	public Pair<Integer, Integer> getAgreementTime36(String employeeID, YearMonth yearMonth, Map<String, Pair<Integer, Integer>> cache) {
		Integer excessTime = null;
		Integer excessTimeNumber = null;
		// 社員ID＋年月で保持されたキャッシュが存在する
		if(cache.containsKey(employeeID+yearMonth.v().toString())) {
			return cache.get(employeeID+yearMonth.v().toString());
		}
		// 【NO.333】36協定時間の取得
		AgreementTimeOfManagePeriod agreementTimeOfManagePeriod = agreementTimeAdapter.getAgreementTimeOfManagePeriod(
				employeeID, 
				yearMonth, 
				new ArrayList<IntegrationOfDaily>(), 
				GeneralDate.today(), 
				ScheRecAtr.SCHEDULE);
		// [NO.708]社員と年月を指定して３６協定年月設定を取得する
		AgreementMonthSettingOutput agreementMonthSettingOutput = agreementMonthSettingAdapter.getData(employeeID, yearMonth);
		if(agreementMonthSettingOutput.getIsExist()) {
			// 超過時間＝管理期間の36協定時間.法定上限対象時間.対象時間
			excessTime = agreementTimeOfManagePeriod.getLegalMaxTime().getAgreementTime().v();
		} else {
			// 超過時間＝管理期間の36協定時間.３６協定対象時間.対象時間
			excessTime = agreementTimeOfManagePeriod.getAgreementTime().getAgreementTime().v();
		}
		// [No.605]年月を指定して年間超過回数の取得
		AgreementExcessInfoImport agreementExcessInfoImport = agreementMonthSettingAdapter.getDataRq605(employeeID, yearMonth);
		excessTimeNumber = agreementExcessInfoImport.getExcessTimes();
		// 社員ID＋年月で保持する(キャッシュ)
		cache.put(employeeID+yearMonth.v().toString(), Pair.of(excessTime, excessTimeNumber));
		return Pair.of(excessTime, excessTimeNumber);
	}
	
	@Override
	public String getComplementLeaveContent(AbsenceLeaveApp absenceLeaveApp, RecruitmentApp recruitmentApp,
			DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, ComplementLeaveAppLink complementLeaveAppLink,
			Application application, List<WorkType> workTypeLst) {
		// 申請内容　＝　String.Empty (Nội dung application = String.Empty)
		String result = "";
		if(Strings.isNotBlank(complementLeaveAppLink.getLinkAppID())) {
			// 申請内容　＝　#CMM045_262　+　”　”　+　振出申請データ．勤務種類名称　+　”　”　+　振出申請データ．開始時刻　+　#CMM045_100　+　　振出申請データ．終了時刻1
			String recruitmentAppWorkTypeName = appDetailInfoRepo.findWorkTypeName(workTypeLst, recruitmentApp.getWorkInformation().getWorkTypeCode().v());
			result += I18NText.getText("CMM045_262") + " " + recruitmentAppWorkTypeName + " ";
			String recStartTime = recruitmentApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getStartTime().getFullText()).orElse("");
			String recEndTime= recruitmentApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getEndTime().getFullText()).orElse("");
			if(Strings.isNotBlank(recStartTime) || Strings.isNotBlank(recEndTime)) {
				result += recStartTime + I18NText.getText("CMM045_100") + recEndTime;
			}
			// 申請内容を改行(xuống dòng nội dung đơn xin)
			result += "\n";
			// 申請内容　+＝　#CMM045_263　+　”　”　+　振休申請データ．勤務種類名称　+　”　”　　+　振休申請データ．開始時刻　+　#CMM045_100　+　振休申請データ．終了時刻
			String absenceLeaveAppWorkTypeName = appDetailInfoRepo.findWorkTypeName(workTypeLst, absenceLeaveApp.getWorkInformation().getWorkTypeCode().v());
			result += I18NText.getText("CMM045_263") + " " + absenceLeaveAppWorkTypeName + " ";
			String absStartTime = absenceLeaveApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getStartTime().getFullText()).orElse("");
			String absEndTime = absenceLeaveApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getEndTime().getFullText()).orElse("");
			if(Strings.isNotBlank(absStartTime) || Strings.isNotBlank(absEndTime)) {
				result += absStartTime + I18NText.getText("CMM045_100") + absEndTime;
			}
			// 振休申請.変更元の振休日=empty(Đơn xin nghỉ bù. ngày nghỉ bù của nguồn thay đổi)
			if(absenceLeaveApp.getChangeSourceHoliday().isPresent()) {
				// 申請内容　＋＝"　"＋「値」(Nội dung đơn xin ＋＝"　"＋「value」)
				result += " " + I18NText.getText("CMM045_304", absenceLeaveApp.getChangeSourceHoliday().get().toString("yyyy/MM/dd"));
			}
		} else {
			if(absenceLeaveApp!=null) {
				// 申請内容　＝　#CMM045_263　+　”　”　+　振休申請データ．勤務種類名称
				String absenceLeaveAppWorkTypeName = appDetailInfoRepo.findWorkTypeName(workTypeLst, absenceLeaveApp.getWorkInformation().getWorkTypeCode().v());
				result += I18NText.getText("CMM045_263") + " " + absenceLeaveAppWorkTypeName;
				// 申請内容　+＝　”　”　+　振休申請データ．開始時刻　+　#CMM045_100　+　　振休申請データ．終了時刻
				String absStartTime = absenceLeaveApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getStartTime().getFullText()).orElse("");
				String absEndTime = absenceLeaveApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getEndTime().getFullText()).orElse("");
				if(Strings.isNotBlank(absStartTime) || Strings.isNotBlank(absEndTime)) {
					result += " " + absStartTime + I18NText.getText("CMM045_100") + absEndTime;
				}
				// 振休申請.変更元の振休日=empty(đơn xin nghỉ bù. Ngày nghỉ bù của nguồn thay đổi)
				if(absenceLeaveApp.getChangeSourceHoliday().isPresent()) {
					// 申請内容　＋＝"　"＋「値」(Nội dung đơn xin ＋＝"　"＋「value」)
					result += " " + I18NText.getText("CMM045_304", absenceLeaveApp.getChangeSourceHoliday().get().toString("yyyy/MM/dd"));
				}
			} if(recruitmentApp != null) {
				// 申請内容　＝　#CMM045_262　+　”　”　+　振出申請データ．勤務種類名称
				String recruitmentAppWorkTypeName = appDetailInfoRepo.findWorkTypeName(workTypeLst, recruitmentApp.getWorkInformation().getWorkTypeCode().v());
				result += I18NText.getText("CMM045_262") + " " + recruitmentAppWorkTypeName;
				// 申請内容　+＝　”　”　+　振出申請データ．開始時刻　+　#CMM045_100　+　　振出申請データ．終了時刻
				String recStartTime = recruitmentApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getStartTime().getFullText()).orElse("");
				String recEndTime = recruitmentApp.getWorkingHours().stream().filter(x -> x.getWorkNo().v()==1).findAny().map(x -> x.getTimeZone().getEndTime().getFullText()).orElse("");
				if(Strings.isNotBlank(recStartTime) || Strings.isNotBlank(recEndTime)) {
					result += " " + recStartTime + I18NText.getText("CMM045_100") + recEndTime;
				}
			}
		}
		// 申請理由内容　＝　申請内容の申請理由(Nội dung đơn xin = Lý do đơn xin của nội dung đơn xin)
		String appReasonContent = this.getAppReasonContent(
				appReasonDisAtr, 
				application.getOpAppReason().orElse(null),
				screenAtr, 
				application.getOpAppStandardReasonCD().orElse(null), 
				application.getAppType(), 
				Optional.empty());
		// 申請内容を改行 (Xuống dòng nội dung đơn xin)
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public String getAnnualHolidayContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr,
			List<TimeLeaveApplicationDetail> leaveApplicationDetails, AppStandardReasonCode appStandardReasonCD) {
		String paramStr = "";
		// ScreenID
		if(screenAtr==ScreenAtr.KAF018 || screenAtr==ScreenAtr.CMM045) {
			// @＝”　”
			paramStr = " ";
		} else {
			// @＝改行
			paramStr = "\n";
		}
		// <List>項目名、時間休種類、時間代休、時間年休、子の看護時間、介護時間、60H超休、時間特別休暇
		String result = leaveApplicationDetails.stream().map(x -> {
			String resultLoop = "";
			// 申請内容＋＝<List>項目名
			switch (x.getAppTimeType()) {
			case ATWORK:
				resultLoop += I18NText.getText("CMM045_294");
				break;
			case OFFWORK:
				resultLoop += I18NText.getText("CMM045_295");
				break;
			case ATWORK2:
				resultLoop += I18NText.getText("CMM045_294") + "2";
				break;
			case OFFWORK2:
				resultLoop += I18NText.getText("CMM045_295") + "2";
				break;
			case PRIVATE:
				resultLoop += I18NText.getText("CMM045_296");
				break;
			case UNION:
				resultLoop += I18NText.getText("CMM045_297");
				break;
			default:
				break;
			}
			// 申請内容＋＝'　'＋#CMM045_298（時間代休）＋'　'＋<List> 時間代休
			if(x.getTimeDigestApplication().getTimeOff() != null && x.getTimeDigestApplication().getTimeOff().valueAsMinutes()>0) {
				resultLoop += " " + I18NText.getText("CMM045_298") + " " + 
								new TimeWithDayAttr(x.getTimeDigestApplication().getTimeOff().valueAsMinutes()).getRawTimeWithFormat();
			}
			// 申請内容＋＝'　'＋#CMM045_300（時間年休）＋'　'＋<List> 時間年休
			if(x.getTimeDigestApplication().getTimeAnnualLeave() != null && x.getTimeDigestApplication().getTimeAnnualLeave().valueAsMinutes()>0) {
				resultLoop += " " + I18NText.getText("CMM045_300") + " " + 
								new TimeWithDayAttr(x.getTimeDigestApplication().getTimeAnnualLeave().valueAsMinutes()).getRawTimeWithFormat();
			}
			// 申請内容＋＝'　'＋#CMM045_302（子看護）＋'　'＋<List> 子の看護時間
			if(x.getTimeDigestApplication().getChildTime() != null && x.getTimeDigestApplication().getChildTime().valueAsMinutes()>0) {
				resultLoop += " " + I18NText.getText("CMM045_302") + " " + 
								new TimeWithDayAttr(x.getTimeDigestApplication().getChildTime().valueAsMinutes()).getRawTimeWithFormat();
			}
			// 申請内容＋＝'　'＋#CMM045_303（介護）＋'　'＋<List> 介護時間
			if(x.getTimeDigestApplication().getNursingTime() != null && x.getTimeDigestApplication().getNursingTime().valueAsMinutes()>0) {
				resultLoop += " " + I18NText.getText("CMM045_303") + " " + 
								new TimeWithDayAttr(x.getTimeDigestApplication().getNursingTime().valueAsMinutes()).getRawTimeWithFormat();
			}
			// 申請内容＋＝'　'＋#CMM045_299（60H超休）＋'　'＋<List> 60H超休
			if(x.getTimeDigestApplication().getOvertime60H() != null && x.getTimeDigestApplication().getOvertime60H().valueAsMinutes()>0) {
				resultLoop += " " + I18NText.getText("CMM045_299") + " " + 
								new TimeWithDayAttr(x.getTimeDigestApplication().getOvertime60H().valueAsMinutes()).getRawTimeWithFormat();
			}
			// 申請内容＋＝'　'＋#CMM045_301（時間特休）＋'　'＋<List> 時間特別休暇
			if(x.getTimeDigestApplication().getTimeSpecialVacation() != null && x.getTimeDigestApplication().getTimeSpecialVacation().valueAsMinutes()>0) {
				resultLoop += " " + I18NText.getText("CMM045_301") + " " + 
								new TimeWithDayAttr(x.getTimeDigestApplication().getTimeSpecialVacation().valueAsMinutes()).getRawTimeWithFormat();
			}
			return resultLoop;
		}).collect(Collectors.joining(paramStr));
		// アルゴリズム「申請内容の申請理由」を実行する
		String appReasonContent = this.getAppReasonContent(appReasonDisAtr, appReason, screenAtr, appStandardReasonCD, ApplicationType.ANNUAL_HOLIDAY_APPLICATION, Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			result += "\n" + appReasonContent;
		}
		return result;
	}

	@Override
	public String getOvertimeApplicationDataMultiTime(DisplayAtr appReasonDisAtr, AppOverTimeData appOverTimeData,
													  ScreenAtr screenAtr, String appReasonContent) {
		String companyId = AppContexts.user().companyId();
		//　・申請理由内容 => appReasonContent
		String reasonContent = "";
		//変数的に利用
		// $SV = empty;
		String SV = "";
		// $複数残業 = empty
		StringBuilder multipleOverTime = new StringBuilder(Strings.EMPTY);
		ReasonForFixedForm reasonForFixedForm;

		Optional<AppOverTime> apOptional = appOverTimeRepo.find(companyId,appOverTimeData.getAppID());
		if(apOptional.isPresent()){
			Optional<OvertimeWorkMultipleTimes> multipleTimesOp = apOptional.get().getMultipleTimesOp();
			if(multipleTimesOp.isPresent()){
				List<OvertimeReason> overtimeReasons = multipleTimesOp.get().getOvertimeReasons();
				List<OvertimeHour> overtimeHours = multipleTimesOp.get().getOvertimeHours()
						.stream()
						.sorted(Comparator.comparing(OvertimeHour::getOvertimeNumber)).collect(Collectors.toList());
				//残業申請.複数回残業内容.残業時間帯を残業回数昇順で１～ループ
				for (int i = 0; i <overtimeHours.size() ; i++) {
					val overtime =  overtimeHours.get(i);
					Optional<OvertimeReason> overtimeReasonOptional =
							overtimeReasons.stream().filter(e->e.getOvertimeNumber().equals(overtime.getOvertimeNumber())).findFirst();
					TimeSpanForCalc timeSpanForCalc = overtime.getOvertimeHours();
					AppStandardReasonCode appStandardReasonCode = null;
					if(overtimeReasonOptional.isPresent()){
						Optional<AppStandardReasonCode> fixedReasonCodeOpt = overtimeReasonOptional.get().getFixedReasonCode();
						if(fixedReasonCodeOpt.isPresent()){
							appStandardReasonCode = fixedReasonCodeOpt.get();
						}
					}
					//ScreenID
					//CMM045の場合
					if(i>=1){
						if(screenAtr == ScreenAtr.CMM045 ||screenAtr == ScreenAtr.KAF018){
							SV = I18NText.getText("CMM045_308");
						}else {
							SV = "\n";
						}
					}
					if (screenAtr == ScreenAtr.CMM045 && appReasonDisAtr == DisplayAtr.NOT_DISPLAY ) {
						//申請理由表示区分 => 表示しないの場合
							// 申請の理由 ＝ Empty
							reasonContent = "";
					}else {
						reasonForFixedForm = this.getAppStandardReasonContent(
								ApplicationType.OVER_TIME_APPLICATION, appStandardReasonCode, Optional.empty());
						//申請の理由 ＝ 上記取得定型理由＋複数回残表内容.残業理由.申請理由
						//複数回残表内容.残業理由.申請理由
						String appReason = "";
						if (overtimeReasonOptional.isPresent()) {
							Optional<AppReason> applyReason = overtimeReasonOptional.get().getApplyReason();
							if (applyReason.isPresent()) {
								appReason = applyReason.get().v();
							}
						}
						reasonContent = reasonForFixedForm == null ? appReason : (reasonForFixedForm.v() + appReason);
					}
					//$複数残業 ＋＝ $SV＋ #CMM045_307※パラメータへのセットはノート参照
					List<String> params = new ArrayList<>();
					if(timeSpanForCalc !=null){
						TimeWithDayAttr start = timeSpanForCalc.getStart();
						TimeWithDayAttr end = timeSpanForCalc.getEnd();

						params.add(start.getFullText());
						params.add(end.getFullText());
						params.add(reasonContent);
					}

					multipleOverTime.append(SV).append(I18NText.getText("CMM045_307", params));
				}
				return appReasonContent  + "\n" + multipleOverTime;

			}
		}
			return appReasonContent;
	}
}
