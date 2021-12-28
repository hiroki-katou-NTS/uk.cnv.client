package nts.uk.ctx.at.request.dom.application.common.service.smartphone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.EnvAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.dto.MailServerSetImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ErrorFlagImport;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.DetailScreenBefore;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.InitMode;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.BeforePreBootMode;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailedScreenBeforeStartOutput;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.init.AppDetailScreenInfo;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.DetailScreenAppData;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.OutputMode;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.init.CollectApprovalRootPatternService;
import nts.uk.ctx.at.request.dom.application.common.service.other.CollectAchievement;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.PreAppContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoNoDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.MsgErrorOutput;
import nts.uk.ctx.at.request.dom.application.common.service.smartphone.output.AppReasonOutput;
import nts.uk.ctx.at.request.dom.application.common.service.smartphone.output.DeadlineLimitCurrentMonth;
import nts.uk.ctx.at.request.dom.application.common.service.smartphone.output.RequestMsgInfoOutput;
import nts.uk.ctx.at.request.dom.application.holidayshipment.HolidayShipmentService;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.setting.DisplayAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReason;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.DisplayReasonRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppTypeSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.OTAppBeforeAccepRestric;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.ReceptionRestrictionSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.service.checkpostappaccept.PostAppAcceptLimit;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.service.checkpreappaccept.PreAppAcceptLimit;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service.AppDeadlineSettingGet;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandard;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandardRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSetRepository;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApplicationUseSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApprovalFunctionSet;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.UseATR;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultiple;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultipleRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@Stateless
public class CommonAlgorithmMobileImpl implements CommonAlgorithmMobile {
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private ApplicationSettingRepository applicationSettingRepository;
	
	@Inject
	private DisplayReasonRepository displayReasonRepository;
	
	@Inject
	private AppReasonStandardRepository appReasonStandardRepository;
	
	@Inject
	private EnvAdapter envAdapter;
	
	@Inject
	private WorkManagementMultipleRepository workManagementMultipleRepository;
	
	@Inject
	private HolidayShipmentService holidayShipmentService;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject
	private EmployeeRequestAdapter employeeAdaptor;
	
	@Inject
	private CollectApprovalRootPatternService collectApprovalRootPatternService;
	
	@Inject
	private CollectAchievement collectAchievement;
	
	@Inject
	private AppDeadlineSettingGet appDeadlineSettingGet;
	
	@Inject
	private AppEmploymentSetRepository appEmploymentSetRepository;
	
	@Inject
	private DetailScreenBefore detailScreenBefore;
	
	@Inject
	private BeforePreBootMode beforePreBootMode;
	
	@Inject
	private InitMode initMode;
	
	@Inject
	private ClosureEmploymentRepository closureEmpRepo;

	@Override
	public AppDispInfoStartupOutput appCommonStartProcess(boolean mode, String companyID, String employeeID,
			ApplicationType appType, Optional<HolidayAppType> opHolidayAppType, List<GeneralDate> dateLst,
			Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		// 申請共通設定情報を取得する
		AppDispInfoNoDateOutput appDispInfoNoDateOutput = this.getAppCommonSetInfo(companyID, employeeID, appType, opHolidayAppType);
		// 基準日に関係する申請設定情報を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = this.getAppSetInfoRelatedBaseDate(mode, companyID, employeeID,
				dateLst, appType, appDispInfoNoDateOutput.getApplicationSetting(), opOvertimeAppAtr);
		// 取得した内容を返す
		return new AppDispInfoStartupOutput(appDispInfoNoDateOutput, appDispInfoWithDateOutput);
	}

	@Override
	public AppDispInfoNoDateOutput getAppCommonSetInfo(String companyID, String employeeID,
		ApplicationType appType, Optional<HolidayAppType> opHolidayAppType) {
		// 申請者情報を取得する
		List<EmployeeInfoImport> employeeInfoLst = commonAlgorithm.getEmployeeInfoLst(Arrays.asList(employeeID));
		// 申請別の申請設定の取得
		ApplicationSetting applicationSetting = applicationSettingRepository.findByAppType(companyID, appType);
		// 申請理由を取得する
		AppReasonOutput appReasonOutput = this.getAppReasonDisplay(companyID, appType, opHolidayAppType);
		// メールサーバを設定したかチェックする
		MailServerSetImport mailServerSetImport = envAdapter.checkMailServerSet(companyID);
		// 複数回勤務を取得
		Optional<WorkManagementMultiple> opWorkManagementMultiple = workManagementMultipleRepository.findByCode(companyID);
		// 取得した内容を返す
		return new AppDispInfoNoDateOutput(
				mailServerSetImport.isMailServerSet(), 
				NotUseAtr.NOT_USE, 
				employeeInfoLst, 
				applicationSetting, 
				Collections.emptyList(), 
				appReasonOutput.getDisplayAppReason(), 
				appReasonOutput.getDisplayStandardReason(), 
				appReasonOutput.getReasonTypeItemLst(), 
				opWorkManagementMultiple.map(x -> x.getUseATR()==UseATR.use).orElse(false));
	}

	@Override
	public AppReasonOutput getAppReasonDisplay(String companyID, ApplicationType appType,
			Optional<HolidayAppType> opHolidayAppType) {
		// INPUT．「申請種類」をチェックする
		if(appType == ApplicationType.ABSENCE_APPLICATION) {
			// INPUT．「休暇申請の種類」をチェックする
			if(!opHolidayAppType.isPresent()) {
				// OUTPUTを返す
				return new AppReasonOutput(
						DisplayAtr.NOT_DISPLAY, 
						DisplayAtr.NOT_DISPLAY, 
						Collections.emptyList());
			}
			// ドメインモデル「申請定型理由」を取得する
			Optional<DisplayReason> opDisplayReason = displayReasonRepository.findByHolidayAppType(companyID, opHolidayAppType.get());
			// 取得できたかチェックする
			if(!opDisplayReason.isPresent()) {
				// OUTPUTを返す
				return new AppReasonOutput(
						DisplayAtr.NOT_DISPLAY, 
						DisplayAtr.NOT_DISPLAY, 
						Collections.emptyList());
			}
			// 取得した「申請理由表示．定型理由の表示」をチェックする
			if(opDisplayReason.get().getDisplayFixedReason() == DisplayAtr.NOT_DISPLAY) {
				// OUTPUTを返す
				return new AppReasonOutput(
						opDisplayReason.get().getDisplayFixedReason(), 
						opDisplayReason.get().getDisplayAppReason(), 
						Collections.emptyList());
			}
			// ドメインモデル「申請定型理由」を取得する
			Optional<AppReasonStandard> opAppReasonStandard = appReasonStandardRepository.findByHolidayAppType(companyID, opHolidayAppType.get());
			// OUTPUTを返す
			return new AppReasonOutput(
					opDisplayReason.get().getDisplayFixedReason(), 
					opDisplayReason.get().getDisplayAppReason(), 
					opAppReasonStandard.map(x -> x.getReasonTypeItemLst()).orElse(Collections.emptyList()));
			
		}
		// ドメインモデル「申請定型理由」を取得する
		Optional<DisplayReason> opDisplayReason = displayReasonRepository.findByAppType(companyID, appType);
		// 取得できたかチェックする
		if(!opDisplayReason.isPresent()) {
			// OUTPUTを返す
			return new AppReasonOutput(
					DisplayAtr.NOT_DISPLAY, 
					DisplayAtr.NOT_DISPLAY, 
					Collections.emptyList());
		}
		// 取得した「申請理由表示．定型理由の表示」をチェックする
		if(opDisplayReason.get().getDisplayFixedReason() == DisplayAtr.NOT_DISPLAY) {
			// OUTPUTを返す
			return new AppReasonOutput(
					opDisplayReason.get().getDisplayFixedReason(), 
					opDisplayReason.get().getDisplayAppReason(), 
					Collections.emptyList());
		}
		// ドメインモデル「申請定型理由」を取得する
		Optional<AppReasonStandard> opAppReasonStandard = appReasonStandardRepository.findByAppType(companyID, appType);
		// OUTPUTを返す
		return new AppReasonOutput(
				opDisplayReason.get().getDisplayFixedReason(), 
				opDisplayReason.get().getDisplayAppReason(), 
				opAppReasonStandard.map(x -> x.getReasonTypeItemLst()).orElse(Collections.emptyList()));
	}

	@Override
	public AppDispInfoWithDateOutput getAppSetInfoRelatedBaseDate(boolean mode, String companyID, String employeeID,
			List<GeneralDate> appDateLst, ApplicationType appType, ApplicationSetting applicationSetting,
			Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		List<MsgErrorOutput> msgErrorLst = new ArrayList<>();
		// 基準日として扱う日の取得
		GeneralDate baseDate = this.getBaseDate(applicationSetting, appType, appDateLst);
		// 社員IDから申請承認設定情報の取得
		ApprovalFunctionSet approvalFunctionSet = this.commonAlgorithm.getApprovalFunctionSet(
				companyID, 
				employeeID, 
				baseDate, 
				appType);
		// 取得した「利用区分」をチェックする
		// chuyển đến UI
		// INPUT．「申請種類」をチェックする
		Optional<List<WorkTimeSetting>> opWorkTimeSettingLst = Optional.empty();
		if(appType != ApplicationType.STAMP_APPLICATION && appType != ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION 
				&& appType != ApplicationType.OPTIONAL_ITEM_APPLICATION) {
			List<WorkTimeSetting> workTimeSettingLst = otherCommonAlgorithm.getWorkingHoursByWorkplace(companyID, employeeID, baseDate);
			opWorkTimeSettingLst = Optional.of(workTimeSettingLst);
		}
		// 社員所属雇用履歴を取得する
		SEmpHistImport empHistImport = employeeAdaptor.getEmpHist(companyID, employeeID, baseDate);
		if(empHistImport==null || empHistImport.getEmploymentCode()==null){
			// エラーメッセージ(Msg_426)を返す
			throw new BusinessException("Msg_426");
		}
		// 雇用別申請承認設定を取得する
		Optional<AppEmploymentSet> opAppEmploymentSet = appEmploymentSetRepository.findByCompanyIDAndEmploymentCD(companyID, empHistImport.getEmploymentCode());
		// INPUT．「起動モード」を確認する
		Optional<List<ApprovalPhaseStateImport_New>> opListApprovalPhaseState = Optional.empty();
		Optional<ErrorFlagImport> opErrorFlag = Optional.empty();
		if(mode) {
			// 1-4.新規画面起動時の承認ルート取得パターン
			ApprovalRootContentImport_New approvalRootContentImport_New = collectApprovalRootPatternService.getApprovalRootPatternNew(
					companyID, 
					employeeID, 
					EmploymentRootAtr.APPLICATION, 
					appType, 
					baseDate);
			opListApprovalPhaseState = Optional.of(approvalRootContentImport_New.getApprovalRootState().getListApprovalPhaseState());
			opErrorFlag = Optional.of(approvalRootContentImport_New.getErrorFlag());
		}
		// 事前事後の初期選択状態を取得する
		// TODO: 申請設定 domain has changed!
		Optional<AppTypeSetting> opAppTypeSetting = applicationSetting.getAppTypeSettings().stream().filter(x -> x.getAppType()==appType).findAny();
		Optional<ReceptionRestrictionSetting> opReceptionRestrictionSetting = applicationSetting.getReceptionRestrictionSettings().stream().filter(x -> x.getAppType()==appType).findAny();
		PrePostInitAtr prePostInitAtr = this.getPrePostInitAtr(
				appDateLst.stream().findFirst(), 
				appType, 
				applicationSetting.getAppDisplaySetting().getPrePostDisplayAtr(),
				opAppTypeSetting.map(x -> x.getDisplayInitialSegment().orElse(null)).orElse(null), 
				opOvertimeAppAtr,
				opReceptionRestrictionSetting.map(x -> x.getOtAppBeforeAccepRestric().orElse(null)).orElse(null));
		// INPUT．「申請種類」をチェックする
		Optional<List<ActualContentDisplay>> opActualContentDisplayLst = Optional.empty();
		Optional<List<PreAppContentDisplay>> opPreAppContentDisplayLst = Optional.empty();
		if(appType == ApplicationType.OVER_TIME_APPLICATION &&
				appType == ApplicationType.HOLIDAY_WORK_APPLICATION &&
				appType == ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION &&
				appType == ApplicationType.STAMP_APPLICATION &&
				appType == ApplicationType.ANNUAL_HOLIDAY_APPLICATION) {
			// 実績内容の取得
			List<ActualContentDisplay> actualContentDisplayLst = collectAchievement.getAchievementContents(
					companyID, 
					employeeID, 
					appDateLst, 
					appType);
			opActualContentDisplayLst = Optional.of(actualContentDisplayLst);
			// 事前内容の取得
			List<PreAppContentDisplay> preAppContentDisplayLst = collectAchievement.getPreAppContents(
					companyID, 
					employeeID, 
					appDateLst, 
					appType,
					opOvertimeAppAtr
			);
			opPreAppContentDisplayLst = Optional.of(preAppContentDisplayLst);
		}
		// 取得した内容を返す
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = new AppDispInfoWithDateOutput(
				approvalFunctionSet, 
				prePostInitAtr, 
				baseDate, 
				empHistImport, 
				NotUseAtr.NOT_USE);
		appDispInfoWithDateOutput.setOpEmploymentSet(opAppEmploymentSet);
		appDispInfoWithDateOutput.setOpListApprovalPhaseState(opListApprovalPhaseState);
		if(opErrorFlag.isPresent()) {
			switch (opErrorFlag.get()) {
			case NO_CONFIRM_PERSON:
				msgErrorLst.add(new MsgErrorOutput("Msg_238", Collections.emptyList()));
				break;
			case APPROVER_UP_10:
				msgErrorLst.add(new MsgErrorOutput("Msg_237", Collections.emptyList()));
				break;
			case NO_APPROVER:
				msgErrorLst.add(new MsgErrorOutput("Msg_324", Collections.emptyList()));
				break;
			default:
				break;
			}
		}
		appDispInfoWithDateOutput.setOpMsgErrorLst(Optional.of(msgErrorLst));
		appDispInfoWithDateOutput.setOpActualContentDisplayLst(opActualContentDisplayLst);
		appDispInfoWithDateOutput.setOpPreAppContentDisplayLst(opPreAppContentDisplayLst);
		appDispInfoWithDateOutput.setOpWorkTimeLst(opWorkTimeSettingLst);
		return appDispInfoWithDateOutput;
	}

	@Override
	public GeneralDate getBaseDate(ApplicationSetting applicationSetting, ApplicationType appType,
			List<GeneralDate> appDateLst) {
		Optional<GeneralDate> refDate = Optional.empty();
		// INPUT．申請種類をチェックする
		if(appType == ApplicationType.COMPLEMENT_LEAVE_APPLICATION) {
			// 基準申請日の決定
			GeneralDate recDate = appDateLst.size() >= 1 ? appDateLst.get(0) : null;
			GeneralDate absDate = appDateLst.size() >= 2 ? appDateLst.get(1) : null;
			refDate = holidayShipmentService.detRefDate(Optional.ofNullable(recDate), Optional.ofNullable(absDate));
		} else {
			// 申請対象日リストから基準日を取得する
			refDate = CollectionUtil.isEmpty(appDateLst) ? Optional.empty() : Optional.of(appDateLst.get(0));
		}
		// 基準日として扱う日の取得
		return applicationSetting.getBaseDate(refDate);
	}

	@Override
	public PrePostInitAtr getPrePostInitAtr(Optional<GeneralDate> opAppDate, ApplicationType appType, DisplayAtr prePostDisplayAtr,
			PrePostInitAtr displayInitialSegment, Optional<OvertimeAppAtr> opOvertimeAppAtr, OTAppBeforeAccepRestric otAppBeforeAccepRestric) {
		// INPUT．事前事後区分表示をチェックする(check INPUT. hiển thị phân loại xin trước xin sau)
		if(prePostDisplayAtr == DisplayAtr.DISPLAY) {
			// OUTPUT．「事前事後区分」=INPUT．事前事後区分の初期表示 (OUTPUT. [phan loại xin trước xin sau]= INPUT. hiển thị khởi tạo của phân loại xin trước xin sau)
			return displayInitialSegment;
		}
		// INPUT．申請対象日リストをチェックする(Check INPUT. ApplicationTargerDateList)
		if(!opAppDate.isPresent()) {
			// OUTPUT．「事前事後区分」=事前(OUTPUT. [phân loại xin trước xin sau]= xin trước)
			return PrePostInitAtr.PREDICT;
		}
		// 3.事前事後の判断処理(事前事後非表示する場合)
		PrePostAtr prePostAtr = otherCommonAlgorithm.preliminaryJudgmentProcessing(
				appType,
				opAppDate.get(), 
				opOvertimeAppAtr.orElse(null),
				otAppBeforeAccepRestric);
		return EnumAdaptor.valueOf(prePostAtr.value, PrePostInitAtr.class);
	}

	@Override
	public RequestMsgInfoOutput getRequestMsgInfoOutputMobile(String companyID, String employeeID, String employmentCD,
			ApplicationUseSetting applicationUseSetting, ReceptionRestrictionSetting receptionRestrictionSetting,
			Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		// 雇用に紐づく締めを取得する
		int closureID = closureEmpRepo.findByEmploymentCD(companyID, employmentCD).get().getClosureId();
		// 申請締切設定を取得する
		DeadlineLimitCurrentMonth deadlineLimitCurrentMonth = appDeadlineSettingGet.getApplicationDeadline(companyID, employeeID, closureID);
		// 事前申請がいつから受付可能か確認する
		PreAppAcceptLimit preAppAcceptLimit = receptionRestrictionSetting.checkWhenPreAppCanBeAccepted(opOvertimeAppAtr);
		// 事後申請がいつから受付可能か確認する
		PostAppAcceptLimit postAppAcceptLimit = receptionRestrictionSetting.checkWhenPostAppCanBeAccepted();
		// OUTPUTをセットして返す
		return new RequestMsgInfoOutput(
				applicationUseSetting, 
				deadlineLimitCurrentMonth, 
				preAppAcceptLimit, 
				postAppAcceptLimit);
	}

	@Override
	public AppDispInfoStartupOutput getDetailMob(String companyID, String appID) {
		// 15.詳細画面申請データを取得する
		DetailScreenAppData detailScreenAppData = detailScreenBefore.getDetailScreenAppData(appID);
		// 申請共通起動処理
		List<GeneralDate> dateLst = new ArrayList<>();
		Application application = detailScreenAppData.getApplication();
		if(application.getOpAppStartDate().isPresent() && application.getOpAppEndDate().isPresent()) {
			DatePeriod datePeriod = new DatePeriod(
					application.getOpAppStartDate().get().getApplicationDate(), 
					application.getOpAppEndDate().get().getApplicationDate());
			dateLst = datePeriod.datesBetween();
		} else {
			dateLst.add(application.getAppDate().getApplicationDate());
		}
		AppDispInfoStartupOutput appDispInfoStartupOutput = this.appCommonStartProcess(
				false, 
				companyID, 
				application.getEmployeeID(), 
				application.getAppType(), 
				Optional.empty(), 
				dateLst, 
				Optional.empty());
		// 入力者の社員情報を取得する
		Optional<EmployeeInfoImport> opEmployeeInfoImport = commonAlgorithm.getEnterPersonInfor(
				application.getEmployeeID(), 
				application.getEnteredPersonID());
		// 14-2.詳細画面起動前モードの判断
		DetailedScreenBeforeStartOutput detailedScreenPreBootModeOutput = 
			beforePreBootMode.judgmentDetailScreenMode(
				companyID, 
				AppContexts.user().employeeId(), 
				application, 
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate());
		// 14-3.詳細画面の初期モード
		OutputMode outputMode = initMode.getDetailScreenInitMode(
				detailedScreenPreBootModeOutput.getUser(),  
				detailedScreenPreBootModeOutput.getReflectPlanState().value);
		
		// 取得した「申請表示情報」を更新する(Update [thông tin hiển thị đơn xin]đã lấy)
		AppDetailScreenInfo appDetailScreenInfo = new AppDetailScreenInfo(
				application, 
				detailScreenAppData.getDetailScreenApprovalData().getApprovalLst(), 
				detailScreenAppData.getDetailScreenApprovalData().getAuthorComment(), 
				detailedScreenPreBootModeOutput.getUser(), 
				detailedScreenPreBootModeOutput.getReflectPlanState(), 
				outputMode);
		appDetailScreenInfo.setPastApp(detailedScreenPreBootModeOutput.isPastApp());
		appDetailScreenInfo.setAuthorizableFlags(Optional.of(detailedScreenPreBootModeOutput.isAuthorizableFlags()));
		appDetailScreenInfo.setApprovalATR(Optional.of(detailedScreenPreBootModeOutput.getApprovalATR()));
		appDetailScreenInfo.setAlternateExpiration(Optional.of(detailedScreenPreBootModeOutput.isAlternateExpiration()));
		appDispInfoStartupOutput.getAppDispInfoNoDateOutput().setOpEmployeeInfo(opEmployeeInfoImport);
		appDispInfoStartupOutput.setAppDetailScreenInfo(Optional.of(appDetailScreenInfo));
		// 更新した「申請表示情報」を返す(Trả về [thông tin hiển thị đơn xin] đã update)
		return appDispInfoStartupOutput;
	}

}
