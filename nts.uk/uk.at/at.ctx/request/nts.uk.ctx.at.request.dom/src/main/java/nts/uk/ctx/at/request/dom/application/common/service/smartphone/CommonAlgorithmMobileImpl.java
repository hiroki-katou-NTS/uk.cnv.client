package nts.uk.ctx.at.request.dom.application.common.service.smartphone;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ApplicationType_Old;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.PrePostAtr_Old;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.EnvAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.sys.dto.MailServerSetImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ErrorFlagImport;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.init.CollectApprovalRootPatternService;
import nts.uk.ctx.at.request.dom.application.common.service.other.AppDetailContent;
import nts.uk.ctx.at.request.dom.application.common.service.other.CollectAchievement;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoNoDateOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
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
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.ReceptionRestrictionSetting;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.service.checkpostappaccept.PostAppAcceptLimit;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.service.checkpreappaccept.PreAppAcceptLimit;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.service.BaseDateGet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.service.AppDeadlineSettingGet;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandard;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppReasonStandardRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSetting;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSettingRepository;
import nts.uk.ctx.at.request.dom.setting.workplace.ApprovalFunctionSetting;
import nts.uk.ctx.at.request.dom.setting.workplace.appuseset.ApplicationUseSetting;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultiple;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultipleRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
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
	private BaseDateGet baseDateGet;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject
	private EmployeeRequestAdapter employeeAdaptor;
	
	@Inject
	private AppEmploymentSettingRepository appEmploymentSetting;
	
	@Inject
	private CollectApprovalRootPatternService collectApprovalRootPatternService;
	
	@Inject
	private CollectAchievement collectAchievement;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;
	
	@Inject
	private AppDeadlineSettingGet appDeadlineSettingGet;

	@Override
	public AppDispInfoStartupOutput appCommonStartProcess(boolean mode, String companyID, String employeeID,
			ApplicationType appType, Optional<HolidayAppType> opHolidayAppType, List<GeneralDate> dateLst,
			Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		// 申請共通設定情報を取得する
		AppDispInfoNoDateOutput appDispInfoNoDateOutput = this.getAppCommonSetInfo(mode, companyID, employeeID, appType, opHolidayAppType);
		// 基準日に関係する申請設定情報を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = this.getAppSetInfoRelatedBaseDate(mode, companyID, employeeID,
				dateLst, appType, appDispInfoNoDateOutput.getApplicationSetting(), opOvertimeAppAtr);
		// 取得した内容を返す
		return new AppDispInfoStartupOutput(appDispInfoNoDateOutput, appDispInfoWithDateOutput);
	}

	@Override
	public AppDispInfoNoDateOutput getAppCommonSetInfo(boolean mode, String companyID, String employeeID,
			ApplicationType appType, Optional<HolidayAppType> opHolidayAppType) {
		List<EmployeeInfoImport> employeeInfoLst = Collections.emptyList();
		// INPUT．「起動モード」をチェックする
		if(!mode) {
			// 申請者情報を取得する
			employeeInfoLst = commonAlgorithm.getEmployeeInfoLst(Arrays.asList(employeeID));
		}
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
				opWorkManagementMultiple.isPresent());
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
			AppReasonStandard appReasonStandard = appReasonStandardRepository.findByHolidayAppType(companyID, opHolidayAppType.get());
			// OUTPUTを返す
			return new AppReasonOutput(
					opDisplayReason.get().getDisplayFixedReason(), 
					opDisplayReason.get().getDisplayAppReason(), 
					appReasonStandard.getReasonTypeItemLst());
			
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
		AppReasonStandard appReasonStandard = appReasonStandardRepository.findByAppType(companyID, appType);
		// OUTPUTを返す
		return new AppReasonOutput(
				opDisplayReason.get().getDisplayFixedReason(), 
				opDisplayReason.get().getDisplayAppReason(), 
				appReasonStandard.getReasonTypeItemLst());
	}

	@Override
	public AppDispInfoWithDateOutput getAppSetInfoRelatedBaseDate(boolean mode, String companyID, String employeeID,
			List<GeneralDate> appDateLst, ApplicationType appType, ApplicationSetting applicationSetting,
			Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		// 基準日として扱う日の取得
		GeneralDate baseDate = this.getBaseDate(applicationSetting, appType, appDateLst);
		// 社員IDから申請承認設定情報の取得
		ApprovalFunctionSetting approvalFunctionSet = this.commonAlgorithm.getApprovalFunctionSet(
				companyID, 
				employeeID, 
				baseDate, 
				EnumAdaptor.valueOf(appType.value, ApplicationType_Old.class));
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
		// 雇用別申請承認設定を取得する
		Optional<AppEmploymentSetting> opAppEmploymentSetting = appEmploymentSetting.getEmploymentSetting(companyID, empHistImport.getEmploymentCode(), appType.value);
		// INPUT．「起動モード」を確認する
		Optional<List<ApprovalPhaseStateImport_New>> opListApprovalPhaseState = Optional.empty();
		Optional<ErrorFlagImport> opErrorFlag = Optional.empty();
		if(mode) {
			// 1-4.新規画面起動時の承認ルート取得パターン
			ApprovalRootContentImport_New approvalRootContentImport_New = collectApprovalRootPatternService.getgetApprovalRootPatternNew(
					companyID, 
					employeeID, 
					EmploymentRootAtr.APPLICATION, 
					EnumAdaptor.valueOf(appType.value, ApplicationType_Old.class), 
					baseDate);
			opListApprovalPhaseState = Optional.of(approvalRootContentImport_New.getApprovalRootState().getListApprovalPhaseState());
			opErrorFlag = Optional.of(approvalRootContentImport_New.getErrorFlag());
		}
		// 事前事後の初期選択状態を取得する
		PrePostInitAtr prePostInitAtr = this.getPrePostInitAtr(
				appDateLst.get(0), 
				appType, 
				applicationSetting.getAppDisplaySetting().getPrePostDisplayAtr(),
				applicationSetting.getAppTypeSetting().getDisplayInitialSegment(), 
				opOvertimeAppAtr.get());
		// INPUT．「申請種類」をチェックする
		Optional<List<AchievementOutput>> opAchievementOutputLst = Optional.empty();
		Optional<List<AppDetailContent>> opAppDetailContentLst = Optional.empty();
		if(appType == ApplicationType.OVER_TIME_APPLICATION &&
				appType == ApplicationType.LEAVE_TIME_APPLICATION &&
				appType == ApplicationType.EARLY_LEAVE_CANCEL_APPLICATION &&
				appType == ApplicationType.STAMP_APPLICATION &&
				appType == ApplicationType.ANNUAL_HOLIDAY_APPLICATION) {
			// 実績内容の取得
			List<AchievementOutput> achievementOutputLst = collectAchievement.getAchievementContents(
					companyID, 
					employeeID, 
					appDateLst, 
					EnumAdaptor.valueOf(appType.value, ApplicationType_Old.class));
			opAchievementOutputLst = Optional.of(achievementOutputLst);
			// 事前内容の取得
			List<AppDetailContent> appDetailContentLst = collectAchievement.getPreAppContents(
					companyID, 
					employeeID, 
					appDateLst, 
					EnumAdaptor.valueOf(appType.value, ApplicationType_Old.class));
			opAppDetailContentLst = Optional.of(appDetailContentLst);
		}
		// 取得した内容を返す
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = new AppDispInfoWithDateOutput(
				approvalFunctionSet, 
				prePostInitAtr, 
				baseDate, 
				empHistImport, 
				NotUseAtr.NOT_USE);
		appDispInfoWithDateOutput.setOpEmploymentSet(opAppEmploymentSetting);
		appDispInfoWithDateOutput.setOpListApprovalPhaseState(opListApprovalPhaseState);
		appDispInfoWithDateOutput.setOpErrorFlag(opErrorFlag);
		appDispInfoWithDateOutput.setOpAchievementOutputLst(opAchievementOutputLst);
		appDispInfoWithDateOutput.setOpAppDetailContentLst(opAppDetailContentLst);
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
			refDate = Optional.of(holidayShipmentService.detRefDate(recDate, absDate));
		} else {
			// 申請対象日リストから基準日を取得する
			refDate = CollectionUtil.isEmpty(appDateLst) ? Optional.empty() : Optional.of(appDateLst.get(0));
		}
		// 基準日として扱う日の取得
		return baseDateGet.getBaseDate(refDate, applicationSetting.getRecordDate());
	}

	@Override
	public PrePostInitAtr getPrePostInitAtr(GeneralDate appDate, ApplicationType appType, DisplayAtr prePostDisplayAtr,
			PrePostInitAtr displayInitialSegment, OvertimeAppAtr overtimeAppAtr) {
		// INPUT．事前事後区分表示をチェックする(check INPUT. hiển thị phân loại xin trước xin sau)
		if(prePostDisplayAtr == DisplayAtr.DISPLAY) {
			// OUTPUT．「事前事後区分」=INPUT．事前事後区分の初期表示 (OUTPUT. [phan loại xin trước xin sau]= INPUT. hiển thị khởi tạo của phân loại xin trước xin sau)
			return displayInitialSegment;
		}
		// INPUT．申請対象日リストをチェックする(Check INPUT. ApplicationTargerDateList)
		if(appDate==null) {
			// OUTPUT．「事前事後区分」=事前(OUTPUT. [phân loại xin trước xin sau]= xin trước)
			return PrePostInitAtr.PREDICT;
		}
		// 3.事前事後の判断処理(事前事後非表示する場合)
		PrePostAtr_Old prePostAtr_Old = otherCommonAlgorithm.preliminaryJudgmentProcessing(
				EnumAdaptor.valueOf(appType.value, ApplicationType_Old.class),
				appDate, 
				overtimeAppAtr.value);
		return EnumAdaptor.valueOf(prePostAtr_Old.value, PrePostInitAtr.class);
	}

	@Override
	public RequestMsgInfoOutput getRequestMsgInfoOutputMobile(String companyID, String employeeID, String employmentCD,
			ApplicationUseSetting applicationUseSetting, ReceptionRestrictionSetting receptionRestrictionSetting,
			Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		// 雇用に紐づく締めを取得する
		Optional<ClosureEmployment> closureEmpOtp = closureEmploymentRepository.findByEmploymentCD(companyID,
				employmentCD);
		// 申請締切設定を取得する
		DeadlineLimitCurrentMonth deadlineLimitCurrentMonth = appDeadlineSettingGet.getApplicationDeadline(companyID, employeeID, closureEmpOtp.get().getClosureId());
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

}
