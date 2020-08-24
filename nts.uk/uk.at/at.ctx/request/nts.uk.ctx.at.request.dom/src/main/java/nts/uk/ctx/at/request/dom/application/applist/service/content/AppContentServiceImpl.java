package nts.uk.ctx.at.request.dom.application.applist.service.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.i18n.I18NText;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.AppListExtractCondition;
import nts.uk.ctx.at.request.dom.application.applist.extractcondition.ApplicationListAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.ApplicationTypeDisplay;
import nts.uk.ctx.at.request.dom.application.applist.service.datacreate.StampAppOutputTmp;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppContentDetailCMM045;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.AppStampDataOutput;
import nts.uk.ctx.at.request.dom.application.applist.service.detail.ScreenAtr;
import nts.uk.ctx.at.request.dom.application.applist.service.param.ListOfApplication;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalBehaviorAtrImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.approvallistsetting.ApprovalListDisplaySetting;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandard;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandardRepository;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.ReasonForFixedForm;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.ReasonTypeItem;
import nts.uk.ctx.at.shared.dom.attendance.AttendanceItem;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
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
	
	private static final int PC = 0;
	private static final int MOBILE = 1;
	
	@Inject
	private AppReasonStandardRepository appReasonStandardRepository;
	
	@Inject
	private AppContentDetailCMM045 appContentDetailCMM045;

	@Override
	public String getArrivedLateLeaveEarlyContent(AppReason appReason, DisplayAtr appReasonDisAtr, ScreenAtr screenAtr, List<ArrivedLateLeaveEarlyItemContent> itemContentLst,
			ApplicationType appType, AppStandardReasonCode appStandardReasonCD) {
		String result = Strings.EMPTY;
		String paramString = Strings.EMPTY;
		if(screenAtr == ScreenAtr.KAF018 || screenAtr == ScreenAtr.CMM045) {
			// @＝改行
			paramString = "\n";
		} else {
			// @＝”　”
			paramString = "	";
		}
		// ・<List>（項目名、勤務NO、区分（遅刻早退）、時刻、取消）
		if(!CollectionUtil.isEmpty(itemContentLst)) {
			for(int i = 0; i < itemContentLst.size(); i++) {
				ArrivedLateLeaveEarlyItemContent item = itemContentLst.get(0);
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
			result += appReasonContent;
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
			return opReasonTypeItem.get().getOpReasonForFixedForm().orElse(null);
		}
		return null;
	}

	@Override
	public String getAppReasonContent(DisplayAtr appReasonDisAtr, AppReason appReason, ScreenAtr screenAtr,
			AppStandardReasonCode appStandardReasonCD, ApplicationType appType,
			Optional<HolidayAppType> opHolidayAppType) {
		// 申請理由内容　＝　String.Empty
		String result = Strings.EMPTY;
		if(!(screenAtr != ScreenAtr.KAF018 && appReason!= null && appReasonDisAtr == DisplayAtr.DISPLAY)) {
			return result;
		}
		// アルゴリズム「申請内容定型理由取得」を実行する
		ReasonForFixedForm reasonForFixedForm = this.getAppStandardReasonContent(appType, appStandardReasonCD, opHolidayAppType);
		if(reasonForFixedForm!=null) {
			if(screenAtr == ScreenAtr.KDL030) {
				// 申請理由内容　+＝　”申請理由：”を改行
				result += "申請理由：  " + "\n";
				// 申請理由内容　+＝　定型理由＋改行＋Input．申請理由
				result += reasonForFixedForm.v() + "\n" + appReason.v();
			} else {
				// 申請理由内容　+＝　定型理由＋’　’＋Input．申請理由
				result += reasonForFixedForm.v() + " " + appReason.v();
			}
		} else {
			if(screenAtr == ScreenAtr.KDL030) {
				// 申請理由内容　+＝　”申請理由：”を改行
				result += "申請理由：  " + "\n";
				// 申請理由内容　+＝　Input．申請理由
				result += appReason.v();
			} else {
				// 申請理由内容　+＝　Input．申請理由
				result += appReason.v();
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
		if(screenAtr == ScreenAtr.KAF018 || screenAtr == ScreenAtr.CMM045) {
			// @＝改行
			paramString = "\n";
		} else {
			// @＝”　”
			paramString = "	";
		}
		if(!CollectionUtil.isEmpty(stampAppOutputTmpLst)) {
			for(int i = 0; i < stampAppOutputTmpLst.size(); i++) {
				StampAppOutputTmp item = stampAppOutputTmpLst.get(0);
				if(i > 0) {
					// 申請内容＋＝@
					result += paramString;
				}
				// 打刻申請出力用Tmp.取消
				if(!item.isCancel()) {
					// 申請内容＋＝$.項目名＋'　'＋$.開始時刻＋#CMM045_100(～)＋$.終了時刻
					result += item.getOpItemName().get() + item.getOpStartTime().get().getFullText() + I18NText.getText("CMM045_100") + item.getOpEndTime().get().getFullText();
				} else {
					// 申請内容＋＝$.項目名＋'　'＋#CMM045_292(取消)
					result += item.getOpItemName().get() + I18NText.getText("CMM045_292");
				}
			}
		}
		// アルゴリズム「申請内容の申請理由」を実行する
		String appReasonContent = this.getAppReasonContent(appReasonDisAtr, appReason, screenAtr, appStandardReasonCD, appType, Optional.empty());
		if(Strings.isNotBlank(appReasonContent)) {
			result += appReasonContent;
		}
		return result;
	}

	@Override
	public String getWorkChangeGoBackContent(ApplicationType appType, String workTypeName, String workTimeName,
			NotUseAtr goWorkAtr1, TimeWithDayAttr workTimeStart1, NotUseAtr goBackAtr1, TimeWithDayAttr workTimeEnd1,
			TimeWithDayAttr breakTimeStart1, TimeWithDayAttr breakTimeEnd1, DisplayAtr appReasonDisAtr,
			AppReason appReason, Application application) {
		// 申請内容　＝　String.Empty ( Nội dung application = 　String.Empty)
		String result = Strings.EMPTY;
		if(appType == ApplicationType.WORK_CHANGE_APPLICATION) {
			// 申請内容　＝　Input．勤務種類名称 ( Nội dung = Input. Work type name)
			result = workTypeName;
			// 申請内容　+＝’　’　＋　Input．就業時間帯名称  ( Nội dung +＝　 Input.Working hour name)
			result += " " + workTimeName;
		}
		// Input．．勤務直行1をチェック ( Check Input．．đi làm thẳng 1
		if(goWorkAtr1 == NotUseAtr.NOT_USE) {
			// 申請内容　+＝　Input．勤務時間開始1
			result += workTimeStart1.getInDayTimeWithFormat();
		} else {
			if(appType == ApplicationType.WORK_CHANGE_APPLICATION) {
				// 申請内容　+＝’　’＋#CMM045_252
				result += " " + I18NText.getText("CMM045_252");
				// 申請内容　+＝　Input．勤務時間開始1
				result += workTimeStart1.getInDayTimeWithFormat();
			} else if(appType == ApplicationType.GO_RETURN_DIRECTLY_APPLICATION) {
				// 申請内容　+＝#CMM045_259＋’　’ ( Nội dung đơn xin　+＝#CMM045_259)
				result += I18NText.getText("CMM045_259");
			}
		}
		// Input．勤務直帰1をチェック
		if(goBackAtr1 == NotUseAtr.NOT_USE) {
			// 申請内容　+＝　#CMM045_100 
			result += I18NText.getText("CMM045_100");
			// 申請内容　+＝　Input．勤務時間終了1
			result += workTimeEnd1.getInDayTimeWithFormat();
		} else {
			if(appType == ApplicationType.WORK_CHANGE_APPLICATION) {
				// 申請内容　+＝　#CMM045_100　+　#CMM045_252
				result += I18NText.getText("CMM045_100") + I18NText.getText("CMM045_252");
				// 申請内容　+＝　Input．勤務時間終了1
				result += workTimeEnd1.getInDayTimeWithFormat();
			} else if(appType == ApplicationType.GO_RETURN_DIRECTLY_APPLICATION) {
				// 申請内容　+＝　#CMM045_260
				result += I18NText.getText("CMM045_260");
			}
		}
		if(appType == ApplicationType.WORK_CHANGE_APPLICATION) {
			// 
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
			result += appReasonContent;
		}
		return result;
	}

	@Override
	public ListOfApplication createEachAppData(Application application, String companyID, List<WorkTimeSetting> lstWkTime,
			List<WorkType> lstWkType, List<AttendanceItem> attendanceItemLst, ApplicationListAtr mode, ApprovalListDisplaySetting approvalListDisplaySetting,
			List<ListOfApplication> appLst, Map<String, List<ApprovalPhaseStateImport_New>> mapApproval, int device,
			AppListExtractCondition appListExtractCondition) {
		ListOfApplication result = new ListOfApplication();
		if(device == PC) {
			// ドメインモデル「申請」．申請種類をチェック (Check Domain「Application.ApplicationType
			switch (application.getAppType()) {
			case GO_RETURN_DIRECTLY_APPLICATION:
				// 直行直帰申請データを作成 ( Tạo dữ liệu đơn xin đi làm, về nhà thẳng)
				String contentGoBack = appContentDetailCMM045.getContentGoBack(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						lstWkTime, 
						lstWkType, 
						ScreenAtr.CMM045);
				result.setAppContent(contentGoBack);
				break;
			case STAMP_APPLICATION:
				// 打刻申請データを作成(tạo data của打刻申請 )
				AppStampDataOutput appStampDataOutput = appContentDetailCMM045.createAppStampData(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID, 
						null);
				result.setAppContent(appStampDataOutput.getAppContent());
				// 申請一覧.申請種類表示＝取得した申請種類表示(ApplicationList.AppTypeDisplay= AppTypeDisplay đã get)
				result.setOpAppTypeDisplay(appStampDataOutput.getOpAppTypeDisplay());
				break;
			case EARLY_LEAVE_CANCEL_APPLICATION:
				// 遅刻早退取消申請データを作成(tạo data của 遅刻早退取消申請)
				String contentArrivedLateLeaveEarly = appContentDetailCMM045.createArrivedLateLeaveEarlyData(
						application, 
						approvalListDisplaySetting.getAppReasonDisAtr(), 
						ScreenAtr.CMM045, 
						companyID);
				result.setAppContent(contentArrivedLateLeaveEarly);
				break;
			default:
				result.setAppContent("-1");
				break;
			}
		}
		// 承認フェーズList　＝　Input．Map＜ルートインスタンスID、承認フェーズList＞を取得(ApprovalPhaseList= Input．Map＜get RootInstanceID, ApprovalPhaseList>)
		result.setOpApprovalPhaseLst(Optional.of(mapApproval.get(application.getAppID())));
		// 申請一覧．承認状況照会　＝　承認状況照会内容(AppList.ApproveStatusRefer =ApproveStatusReferContents )
		result.setOpApprovalStatusInquiry(Optional.of(this.getApprovalStatusInquiryContent(result.getOpApprovalPhaseLst().get())));
		// アルゴリズム「反映状態を取得する」を実行する(Thực hiện thuật toán [lấy trạng thái phản ánh])
		ReflectedState reflectedState = application.getAppReflectedState();
		
		ApprovalBehaviorAtrImport_New phaseAtr = ApprovalBehaviorAtrImport_New.UNAPPROVED;
		ApprovalBehaviorAtrImport_New frameAtr = ApprovalBehaviorAtrImport_New.UNAPPROVED;
		if(mode == ApplicationListAtr.APPROVER) {
//			String loginID = AppContexts.user().employeeId();
//			Optional<ApprovalPhaseStateImport_New> phaseLogin = result.getOpApprovalPhaseLst().get()
//					.stream().filter(x -> {
//						List<ApprovalFrameImport_New> x.getListApprovalFrame().stream().filter(y -> { 
//							return y.getApproverID().equals(loginID);
//						}).findAny();
//					}).findAny();
//			
//			
//			
//			for(ApprovalPhaseStateImport_New phase : result.getOpApprovalPhaseLst().get()) {
//				for(ApprovalFrameImport_New frame : phase.getListApprovalFrame()) {
//					
//					
//					
//					for(ApproverStateImport_New approver : frame.getListApprover()) {
//						// 承認枠　＝　ロープの承認枠(ApprovalFrame= ApproveFrame dang loop)
//						// 承認フェーズ　＝　ロープの承認フェーズ(ApprovalPhase = ApprovalPhase dang loop)
//						// 承認枠の承認状態　＝　承認枠．承認区分(ApprovalStatus của ApprovalFrame = ApprovalFrame. ApprovalAtr)
//						phaseAtr = phase.getApprovalAtr();
//						frameAtr = approver
//					}
//				}
//			}
		}
		// 申請一覧．反映状態　＝　申請の反映状態(ApplicationList. trạng thái phản ánh = trạng thái phản ánh của đơn xin)
		result.setReflectionStatus(reflectedState.name);
		return result;
	}

	@Override
	public String getApprovalStatusInquiryContent(List<ApprovalPhaseStateImport_New> approvalPhaseLst) {
		// 承認状況照会　＝　String.Empty (tham khảo tình trạng approval)
		String result = Strings.EMPTY;
		for(ApprovalPhaseStateImport_New phase : approvalPhaseLst) {
			if(phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.UNAPPROVED || phase.getApprovalAtr() == ApprovalBehaviorAtrImport_New.REMAND) {
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
			ApprovalBehaviorAtrImport_New phaseAtr, ApprovalBehaviorAtrImport_New frameAtr) {
		// TODO Auto-generated method stub
		return null;
	}

}
