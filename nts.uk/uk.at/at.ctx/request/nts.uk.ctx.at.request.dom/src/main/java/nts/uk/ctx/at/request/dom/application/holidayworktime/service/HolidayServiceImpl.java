package nts.uk.ctx.at.request.dom.application.holidayworktime.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.CommonOvertimeHoliday;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.PreActualColorCheck;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.User;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.CheckBeforeRegisMultiEmpOutput;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.PreAppContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.OverTimeWorkHoursOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.commonalgorithm.ICommonAlgorithmHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.AppHdWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutputMulti;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdSelectWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdWorkDetailOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdWorkDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HolidayWorkCalculationResult;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.application.overtime.ExcessState;
import nts.uk.ctx.at.request.dom.application.overtime.OverStateOutput;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.ICommonAlgorithmOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoNoBaseDate;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.QuotaOuput;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.ReasonDissociationOutput;
import nts.uk.ctx.at.request.dom.application.overtime.service.OverTimeContent;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkContent;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkHours;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkInfo;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.CalcStampMiss;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.OverrideSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.Time36AgreeCheckRegister;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.workrecord.dailyrecordprocess.dailycreationwork.BreakTimeZoneSetting;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;
import nts.uk.ctx.at.shared.dom.scherec.application.timeleaveapplication.TimeLeaveApplicationDetailShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWork;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWorkRepository;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.NotUseAtr;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class HolidayServiceImpl implements HolidayService {
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private CommonOvertimeHoliday commonOverTimeHoliday;
	
	@Inject
	private ICommonAlgorithmHolidayWork commonHolidayWorkAlgorithm;
	
	@Inject
	private ICommonAlgorithmOverTime commonOverTimeAlgorithm;
	
	@Inject
	private WorkdayoffFrameRepository workdayoffFrameRepository;
	
	@Inject
	private PreActualColorCheck preActualColorCheck;
	
	@Inject
	private NewBeforeRegister processBeforeRegister;
	
	@Inject
	private DetailBeforeUpdate detailBeforeProcessRegisterService;
	
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;
	
	@Inject
	private HolidayWorkAppSetRepository holidayWorkAppSetRepository;
	
	@Inject
	private AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Override
	public AppHdWorkDispInfoOutput startA(String companyId, Optional<List<String>> empList,
			Optional<List<GeneralDate>> dateList, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = new AppHdWorkDispInfoOutput();
		appHdWorkDispInfoOutput.setAppDispInfoStartupOutput(appDispInfoStartupOutput);
		
		//	休出申請の設定を取得する
		HolidayWorkAppSet holidayWorkSetting = commonHolidayWorkAlgorithm.getHolidayWorkSetting(companyId);
		AppReflectOtHdWork hdWorkOvertimeReflect = commonHolidayWorkAlgorithm.getHdWorkOvertimeReflect(companyId);
		appHdWorkDispInfoOutput.setHolidayWorkAppSet(holidayWorkSetting);
		appHdWorkDispInfoOutput.setHdWorkOvertimeReflect(hdWorkOvertimeReflect);
		
		//01-02_時間外労働を取得
		String employeeId = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid();
		Optional<OverTimeWorkHoursOutput> agreeOvertimeOutput = commonOverTimeHoliday.getAgreementTime(companyId, employeeId,
				holidayWorkSetting.getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr(),
				holidayWorkSetting.getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr());
		appHdWorkDispInfoOutput.setOtWorkHoursForApplication(agreeOvertimeOutput);
		
		//1-1.休日出勤申請（新規）起動時初期データを取得する
		String applicantEmployeeId = empList.isPresent() ? (!empList.get().isEmpty() ? empList.get().get(0) : "") : "";
		Optional<GeneralDate> applicationDate = dateList.isPresent() ? Optional.of(dateList.get().get(0)) : Optional.empty();
		GeneralDate baseDate = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate();
		PrePostInitAtr prePostAtr = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getPrePostAtr();
		AppEmploymentSet employmentSet = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet().orElse(null);
		List<WorkTimeSetting> workTimeList = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(new ArrayList<>());
		List<ActualContentDisplay> actualContentDisplayList = 
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(new ArrayList<>());
		  
		HdWorkDispInfoWithDateOutput hdWorkDispInfoWithDateOutput = commonHolidayWorkAlgorithm.getHdWorkDispInfoWithDateOutput(companyId, applicantEmployeeId, 
				applicationDate, baseDate, prePostAtr, employmentSet, workTimeList, holidayWorkSetting, hdWorkOvertimeReflect, actualContentDisplayList);
		
		appHdWorkDispInfoOutput.setHdWorkDispInfoWithDateOutput(hdWorkDispInfoWithDateOutput);
		
		//01-03_休出時間枠を取得
		List<WorkdayoffFrame> workdayoffFrameList = workdayoffFrameRepository.findByUseAtr(companyId, 1);
		appHdWorkDispInfoOutput.setWorkdayoffFrameList(workdayoffFrameList);
		
		//	指定社員の申請残業枠を取得する 
		InfoNoBaseDate infoNoBaseDate= commonOverTimeAlgorithm.getInfoNoBaseDate(companyId,
				employeeId,
				OvertimeAppAtr.EARLY_NORMAL_OVERTIME);
		QuotaOuput quotaOutput = commonOverTimeAlgorithm.getOvertimeQuotaSetUse(companyId, employeeId, baseDate, 
				OvertimeAppAtr.EARLY_NORMAL_OVERTIME, infoNoBaseDate.getOverTimeAppSet());
		appHdWorkDispInfoOutput.setDispFlexTime(NotUseAtr.valueOf(quotaOutput.getFlexTimeClf() ? 1 : 0));
		appHdWorkDispInfoOutput.setOvertimeFrameList(quotaOutput.getOverTimeQuotaList());
		
		//	乖離理由の表示区分を取得する
		ReasonDissociationOutput reasonDissociationOutput = commonOverTimeAlgorithm.getInfoNoBaseDate(companyId, ApplicationType.HOLIDAY_WORK_APPLICATION, 
				null, holidayWorkSetting.getOvertimeLeaveAppCommonSet());
		if(reasonDissociationOutput != null) {
			appHdWorkDispInfoOutput.setDivergenceTimeRoots(reasonDissociationOutput.getDivergenceTimeRoots());
			appHdWorkDispInfoOutput.setDivergenceReasonInputMethod(reasonDissociationOutput.getDivergenceReasonInputMethod());
		}
		
		return appHdWorkDispInfoOutput;
	}

	@Override
	public HolidayWorkCalculationResult calculate(
			String companyId,
			String employeeId,
			Optional<GeneralDate> date,
			PrePostInitAtr prePostAtr,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet,
			ApplicationTime preApplicationTime,
			ApplicationTime actualApplicationTime,
			WorkContent workContent,
			Boolean agent) {
		//INPUTをチェックする
		if(!date.isPresent() || !workContent.getWorkTypeCode().isPresent() || !workContent.getWorkTimeCode().isPresent() || workContent.getTimeZones().isEmpty()) {
			return null;
		}
		//06_計算処理
		List<ApplicationTime> applicationTimes = commonOverTimeHoliday.calculator(companyId, employeeId, date.get() , workContent.getWorkTypeCode(), workContent.getWorkTimeCode(), 
				workContent.getTimeZones(), workContent.getBreakTimes());
		Optional<OverStateOutput> overStateOutputOP = Optional.empty();
		if (!agent) {
			//	事前申請・実績の時間超過をチェックする
			OverStateOutput overStateOutput = overtimeLeaveAppCommonSet.checkPreApplication(
					EnumAdaptor.valueOf(prePostAtr.value, PrePostInitAtr.class), 
					Optional.ofNullable(preApplicationTime), 
					!applicationTimes.isEmpty() ? Optional.ofNullable(applicationTimes.get(0)) : Optional.empty(), 
							Optional.ofNullable(actualApplicationTime));
			
			overStateOutputOP = Optional.ofNullable(overStateOutput);
			
		}
		
		
		return new HolidayWorkCalculationResult(overStateOutputOP, !applicationTimes.isEmpty() ? applicationTimes.get(0) : null, CalculatedFlag.CALCULATED);
	}

	@Override
	public AppHdWorkDispInfoOutput changeAppDate(
			String companyId,
			List<GeneralDate> dateList,
			ApplicationType applicationType,
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput,
			Boolean isAgent) {
		AppDispInfoStartupOutput appDispInfoStartupOutput = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput();
		
		//	申請日を変更する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.changeAppDateProcess(companyId, dateList, applicationType, appDispInfoStartupOutput.getAppDispInfoNoDateOutput(), 
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput(), Optional.empty());
		appDispInfoStartupOutput.setAppDispInfoWithDateOutput(appDispInfoWithDateOutput);
		appHdWorkDispInfoOutput.setAppDispInfoStartupOutput(appDispInfoStartupOutput);
		
		//	1-1.休日出勤申請（新規）起動時初期データを取得する
		String employeeId = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid();
		
		HdWorkDispInfoWithDateOutput hdWorkDispInfoWithDateOutput = commonHolidayWorkAlgorithm.getHdWorkDispInfoWithDateOutput(companyId, employeeId, 
				Optional.of(dateList.get(0)), appDispInfoWithDateOutput.getBaseDate(), 
				appDispInfoWithDateOutput.getPrePostAtr(), appDispInfoWithDateOutput.getOpEmploymentSet().orElse(null), 
				appDispInfoWithDateOutput.getOpWorkTimeLst().orElse(Collections.emptyList()), 
				appHdWorkDispInfoOutput.getHolidayWorkAppSet(), appHdWorkDispInfoOutput.getHdWorkOvertimeReflect(), 
				appDispInfoWithDateOutput.getOpActualContentDisplayLst().orElse(Collections.emptyList()));
		appHdWorkDispInfoOutput.setHdWorkDispInfoWithDateOutput(hdWorkDispInfoWithDateOutput);
		
		// 休憩時間帯を取得する
		List<ActualContentDisplay> opActualContentDisplayLst = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput()
				.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList());
		Optional<AchievementDetail> achievementDetail = !opActualContentDisplayLst.isEmpty() ? 
				opActualContentDisplayLst.get(0).getOpAchievementDetail() : Optional.empty();
		BreakTimeZoneSetting breakTimeZoneSettingList = commonOverTimeAlgorithm.selectWorkTypeAndTime(
				companyId, 
				hdWorkDispInfoWithDateOutput.getInitWorkType().orElse(null), 
				hdWorkDispInfoWithDateOutput.getInitWorkTime().orElse(null), 
				hdWorkDispInfoWithDateOutput.getWorkHours()==null ? Optional.empty() : hdWorkDispInfoWithDateOutput.getWorkHours().getStartTimeOp1(), 
				hdWorkDispInfoWithDateOutput.getWorkHours()==null ? Optional.empty() : hdWorkDispInfoWithDateOutput.getWorkHours().getEndTimeOp1(), 
				achievementDetail);
		// 勤務内容.休憩時間帯を再セットする
		appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().setBreakTimeZoneSettingList(Optional.ofNullable(breakTimeZoneSettingList));
		
		// 	計算（従業員）
		WorkContent workContent = commonHolidayWorkAlgorithm.getWorkContent(hdWorkDispInfoWithDateOutput);
		List<PreAppContentDisplay> preAppContentDisplayList = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput()
				.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
		Optional<AppHolidayWork> appHolidayWork = !preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty();
		
		HolidayWorkCalculationResult calculationResult = this.calculate(
				companyId,
				employeeId,
				Optional.ofNullable(dateList.get(0)), 
				appDispInfoWithDateOutput.getPrePostAtr(),
				appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet(), 
				appHolidayWork.isPresent() ? appHolidayWork.get().getApplicationTime() : null, 
				hdWorkDispInfoWithDateOutput.getActualApplicationTime().orElse(null),
				workContent,
				isAgent);
		appHdWorkDispInfoOutput.setCalculationResult(Optional.ofNullable(calculationResult));
		
		return appHdWorkDispInfoOutput;
	}

	@Override
	public HdSelectWorkDispInfoOutput selectWork(String companyId, GeneralDate date, WorkTypeCode workType,
			WorkTimeCode workTime, List<ActualContentDisplay> actualContentDisplayList,
			AppDispInfoStartupOutput appDispInfoStartupOutput, HolidayWorkAppSet holidayWorkAppSet) {
		HdSelectWorkDispInfoOutput hdSelectWorkDispInfoOutput = new HdSelectWorkDispInfoOutput();
		
		//	初期表示する出退勤時刻を取得する
		OverTimeContent overTimeContent = commonHolidayWorkAlgorithm.getOverTimeContent(Optional.of(workType), Optional.of(workTime), 
				actualContentDisplayList);
		Optional<WorkHours> workHours = commonOverTimeAlgorithm.initAttendanceTime(companyId, Optional.ofNullable(date), overTimeContent, 
				holidayWorkAppSet.getApplicationDetailSetting());
		hdSelectWorkDispInfoOutput.setWorkHours(workHours.orElse(new WorkHours()));
		
		//	休憩時間帯を取得する
		Optional<AchievementDetail> achievementDetail = !actualContentDisplayList.isEmpty() ? 
				actualContentDisplayList.get(0).getOpAchievementDetail() : Optional.empty();
		BreakTimeZoneSetting breakTimeZoneSettingList = commonOverTimeAlgorithm.selectWorkTypeAndTime(companyId, workType, 
				workTime, 
				workHours.isPresent() ? workHours.get().getStartTimeOp1() : Optional.empty(), 
				workHours.isPresent() ? workHours.get().getEndTimeOp1() : Optional.empty(), 
				achievementDetail);
		hdSelectWorkDispInfoOutput.setBreakTimeZoneSettingList(Optional.ofNullable(breakTimeZoneSettingList));
		
		//07-02_実績取得・状態チェック
		ApplicationTime actualApplicationTime = new ApplicationTime();
		if(appDispInfoStartupOutput.getAppDetailScreenInfo().isPresent() 
				&& (appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.APPROVER) 
						|| appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.OTHER))) {
			actualApplicationTime = preActualColorCheck.checkStatus(companyId, appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), 
					date, ApplicationType.HOLIDAY_WORK_APPLICATION, workType, workTime, OverrideSet.TIME_OUT_PRIORITY, 
					Optional.of(CalcStampMiss.CAN_NOT_REGIS), breakTimeZoneSettingList.getTimeZones(), 
					!actualContentDisplayList.isEmpty() ? Optional.of(actualContentDisplayList.get(0)): Optional.empty());
		}
		if(!appDispInfoStartupOutput.getAppDetailScreenInfo().isPresent() 
				|| (appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.APPLICANT) 
						|| appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.APPLICANT_APPROVER))) {
			actualApplicationTime = preActualColorCheck.checkStatus(companyId, appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), 
					date, ApplicationType.HOLIDAY_WORK_APPLICATION, workType, workTime, holidayWorkAppSet.getOvertimeLeaveAppCommonSet().getOverrideSet(), 
					Optional.of(holidayWorkAppSet.getCalcStampMiss()), breakTimeZoneSettingList.getTimeZones(), 
					!actualContentDisplayList.isEmpty() ? Optional.of(actualContentDisplayList.get(0)): Optional.empty());
		}
		hdSelectWorkDispInfoOutput.setActualApplicationTime(actualApplicationTime);
		
		return hdSelectWorkDispInfoOutput;
	}

	@Override
	public CheckBeforeOutput checkBeforeRegister(boolean require, String companyId, AppHdWorkDispInfoOutput appHdWorkDispInfoOutput,
			AppHolidayWork appHolidayWork, boolean isProxy) {
		CheckBeforeOutput checkBeforeOutput = new CheckBeforeOutput();
		
		//2-1.新規画面登録前の処理
		int totalOverTime = 0;
        totalOverTime = appHolidayWork.getApplicationTime().getApplicationTime().stream()
                .map(x -> x.getApplicationTime().v())
                .mapToInt(Integer::intValue)
                .sum();
        totalOverTime += appHolidayWork.getApplicationTime().getOverTimeShiftNight().isPresent() 
                && appHolidayWork.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight() != null ? 
                appHolidayWork.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight().v() : 0;
        totalOverTime += appHolidayWork.getApplicationTime().getFlexOverTime().map(AttendanceTimeOfExistMinus::v).orElse(0);
        TimeDigestionParam timeDigestionParam = new TimeDigestionParam(
                0, 
                0, 
                0, 
                0, 
                0, 
                totalOverTime, 
                new ArrayList<TimeLeaveApplicationDetailShare>());
		List<ConfirmMsgOutput> confirmMsgOutputs = processBeforeRegister.processBeforeRegister_New(companyId, 
				EmploymentRootAtr.APPLICATION,
				isProxy, 
				appHolidayWork.getApplication(), 
				null,
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
				Collections.emptyList(), 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput(), 
				Arrays.asList(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
				Optional.of(timeDigestionParam), 
				appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v), false);
		
		//3.個別エラーチェック
		checkBeforeOutput = commonHolidayWorkAlgorithm.individualErrorCheck(require, companyId, appHdWorkDispInfoOutput, appHolidayWork, 0);	//mode new = 0
		
		checkBeforeOutput.getConfirmMsgOutputs().addAll(confirmMsgOutputs);
		return checkBeforeOutput;
	}
	

	@Override
	public CheckBeforeOutputMulti checkBeforeRegisterMulti(boolean require, String companyId, List<String> empList,
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput, AppHolidayWork appHolidayWork) {
		CheckBeforeOutputMulti checkBeforeOutputMulti = new CheckBeforeOutputMulti();
		
		//2-1.新規画面登録前の処理(複数人) 
		CheckBeforeRegisMultiEmpOutput checkBeforeRegisMultiEmpOutput = processBeforeRegister.processBeforeRegisterMultiEmp(companyId, 
				empList, appHolidayWork.getApplication(), 
				null, appHdWorkDispInfoOutput.getAppDispInfoStartupOutput());
		checkBeforeOutputMulti.setApprovalRootContentMap(checkBeforeRegisMultiEmpOutput.getMapEmpContentAppr());
		checkBeforeOutputMulti.setErrorEmpBusinessName(checkBeforeRegisMultiEmpOutput.getEmpErrorName());
		
		//3.個別エラーチェック(複数人)
		checkBeforeOutputMulti.setConfirmMsgOutputMap(
				commonHolidayWorkAlgorithm.individualErrorCheckMulti(require, companyId, empList, appHdWorkDispInfoOutput, appHolidayWork)
				);
		
		return checkBeforeOutputMulti;
	}

	@Override
	public HdWorkDetailOutput getDetail(
			String companyId,
			String applicationId,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		HdWorkDetailOutput hdWorkDetailOutput = new HdWorkDetailOutput();
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = new AppHdWorkDispInfoOutput();
		appHdWorkDispInfoOutput.setAppDispInfoStartupOutput(appDispInfoStartupOutput);
		hdWorkDetailOutput.setAppHdWorkDispInfoOutput(appHdWorkDispInfoOutput);
		
		//	ドメインモデル「休日出勤申請」を取得する
		Optional<AppHolidayWork> appHolidayWorkOp = appHolidayWorkRepository.find(companyId, applicationId);
		if(!appHolidayWorkOp.isPresent()) {
			return null;
		}
		AppHolidayWork appHolidayWork = appHolidayWorkOp.get();
//		Application application = appDispInfoStartupOutput.getAppDetailScreenInfo().map(x -> x.getApplication()).orElse(null);
		Optional<Application> applicationOp = applicationRepository.findByID(companyId, applicationId);
		appHolidayWork.setApplication(applicationOp.orElse(appDispInfoStartupOutput.getAppDetailScreenInfo().map(x -> x.getApplication()).orElse(null)));
		Application application = appHolidayWork.getApplication();
		hdWorkDetailOutput.setAppHolidayWork(appHolidayWork);
		
		//	休出申請の設定を取得する
		Optional<HolidayWorkAppSet> holidayWorkAppSet = holidayWorkAppSetRepository.findSettingByCompany(companyId);
		appHdWorkDispInfoOutput.setHolidayWorkAppSet(holidayWorkAppSet.orElse(null));
		Optional<AppReflectOtHdWork> appReflectOtHdWork = appReflectOtHdWorkRepository.findByCompanyId(companyId);
		appHdWorkDispInfoOutput.setHdWorkOvertimeReflect(appReflectOtHdWork.orElse(null));
		
		//	申請表示情報(基準日関係あり)．事前事後区分=取得した「申請」．事前事後区分
		appDispInfoStartupOutput.getAppDispInfoWithDateOutput().setPrePostAtr(EnumAdaptor.valueOf(application.getPrePostAtr().value, PrePostInitAtr.class));
		
		HdWorkDispInfoWithDateOutput hdWorkDispInfoWithDateOutput = new HdWorkDispInfoWithDateOutput();
		
		//	#113397
		//	休日出勤申請起動時の表示情報(申請対象日関係あり)．初期選択勤務種類、初期選択就業時間帯＝取得した「休日出勤申請」．勤務情報．勤務種類コード、就業時間帯コード
		hdWorkDispInfoWithDateOutput.setInitWorkType(Optional.ofNullable(appHolidayWork.getWorkInformation().getWorkTypeCode()));
		hdWorkDispInfoWithDateOutput.setInitWorkTime(Optional.ofNullable(appHolidayWork.getWorkInformation().getWorkTimeCode()));
		
		//	#113397
		//	ドメインモデル「勤務種類」を取得 
		Optional<WorkType> workType = workTypeRepository.findByPK(companyId, appHolidayWork.getWorkInformation().getWorkTypeCode().v());
		hdWorkDispInfoWithDateOutput.setInitWorkTypeName(workType.isPresent() ? Optional.of(workType.get().getName()) : Optional.empty());
		
		//	#113397
		//	ドメインモデル「就業時間帯」を取得
		Optional<WorkTimeSetting> workTime = workTimeSettingRepository.findByCode(companyId, appHolidayWork.getWorkInformation().getWorkTimeCode().v());
		hdWorkDispInfoWithDateOutput.setInitWorkTimeName(workTime.isPresent() ? Optional.of(workTime.get().getWorkTimeDisplayName().getWorkTimeName()) : Optional.empty());
		
		//	01-03_休出時間枠を取得
		List<WorkdayoffFrame> workdayoffFrameList = workdayoffFrameRepository.findByUseAtr(companyId, 1);
		appHdWorkDispInfoOutput.setWorkdayoffFrameList(workdayoffFrameList);
		
//		指定社員の申請残業枠を取得する 
		InfoNoBaseDate infoNoBaseDate= commonOverTimeAlgorithm.getInfoNoBaseDate(companyId,
				application.getEmployeeID(),
				OvertimeAppAtr.EARLY_NORMAL_OVERTIME);
		QuotaOuput quotaOutput = commonOverTimeAlgorithm.getOvertimeQuotaSetUse(companyId, application.getEmployeeID(), appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate(), 
				OvertimeAppAtr.EARLY_NORMAL_OVERTIME, infoNoBaseDate.getOverTimeAppSet());
		appHdWorkDispInfoOutput.setDispFlexTime(NotUseAtr.valueOf(quotaOutput.getFlexTimeClf() ? 1 : 0));
		appHdWorkDispInfoOutput.setOvertimeFrameList(quotaOutput.getOverTimeQuotaList());
		
//		乖離理由の表示区分を取得する
		ReasonDissociationOutput reasonDissociationOutput = commonOverTimeAlgorithm.getInfoNoBaseDate(companyId, ApplicationType.HOLIDAY_WORK_APPLICATION, 
				null, appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet());
		if(reasonDissociationOutput != null) {
			appHdWorkDispInfoOutput.setDivergenceTimeRoots(reasonDissociationOutput.getDivergenceTimeRoots());
			appHdWorkDispInfoOutput.setDivergenceReasonInputMethod(reasonDissociationOutput.getDivergenceReasonInputMethod());
		}
		
		//1-2.起動時勤務種類リストを取得する
		List<WorkType> workTypeList = commonHolidayWorkAlgorithm.getWorkTypeList(companyId, appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet().orElse(null));
		hdWorkDispInfoWithDateOutput.setWorkTypeList(Optional.of(workTypeList));
		
		//01-02_時間外労働を取得
		String employeeId = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid();
		Optional<OverTimeWorkHoursOutput> agreeOvertimeOutput = commonOverTimeHoliday.getAgreementTime(companyId, employeeId,
				holidayWorkAppSet.isPresent() ? holidayWorkAppSet.get().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr() : Time36AgreeCheckRegister.NOT_CHECK,
				holidayWorkAppSet.isPresent() ? holidayWorkAppSet.get().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr() : nts.uk.shr.com.enumcommon.NotUseAtr.NOT_USE);
		appHdWorkDispInfoOutput.setOtWorkHoursForApplication(agreeOvertimeOutput);
		
		//	取得した「休日出勤申請」．「申請」．事前事後区分をチェックする
		if(application.getPrePostAtr().value == PrePostInitAtr.POSTERIOR.value) {
			//07-02_実績取得・状態チェック
			ApplicationTime actualApplicationTime = new ApplicationTime();
			List<ActualContentDisplay> actualContentDisplayList = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList());
			List<DeductionTime> deductionTimeList = appHolidayWork.getBreakTimeList().orElse(Collections.emptyList()).stream()
					.map(breakTime -> new DeductionTime(breakTime.getTimeZone().getStartTime(), breakTime.getTimeZone().getEndTime())).collect(Collectors.toList());
			
			if(appDispInfoStartupOutput.getAppDetailScreenInfo().isPresent() 
					&& (appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.APPROVER) 
							|| appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.OTHER))) {
				actualApplicationTime = preActualColorCheck.checkStatus(companyId, appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), 
						application.getAppDate().getApplicationDate(), ApplicationType.HOLIDAY_WORK_APPLICATION, appHolidayWork.getWorkInformation().getWorkTypeCode(), 
						appHolidayWork.getWorkInformation().getWorkTimeCode(), OverrideSet.TIME_OUT_PRIORITY, 
						Optional.of(CalcStampMiss.CAN_NOT_REGIS), deductionTimeList, 
						!actualContentDisplayList.isEmpty() ? Optional.of(actualContentDisplayList.get(0)): Optional.empty());
			}
			if(!appDispInfoStartupOutput.getAppDetailScreenInfo().isPresent() 
					|| (appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.APPLICANT) 
							|| appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser().equals(User.APPLICANT_APPROVER))) {
				actualApplicationTime = preActualColorCheck.checkStatus(companyId, appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(), 
						application.getAppDate().getApplicationDate(), ApplicationType.HOLIDAY_WORK_APPLICATION, appHolidayWork.getWorkInformation().getWorkTypeCode(), 
						appHolidayWork.getWorkInformation().getWorkTimeCode(), appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getOverrideSet(), 
						Optional.of(appHdWorkDispInfoOutput.getHolidayWorkAppSet().getCalcStampMiss()), deductionTimeList, 
						!actualContentDisplayList.isEmpty() ? Optional.of(actualContentDisplayList.get(0)): Optional.empty());
			}
			hdWorkDispInfoWithDateOutput.setActualApplicationTime(Optional.ofNullable(actualApplicationTime));
			
			//	事前申請・実績の時間超過をチェックする
			List<PreAppContentDisplay> preAppContentDisplayList = appDispInfoStartupOutput.getAppDispInfoWithDateOutput()
					.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
			Optional<AppHolidayWork> appHolidayWorkPre = !preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty();
			
			OverStateOutput overStateOutput = 
				appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().checkPreApplication(
					EnumAdaptor.valueOf(appHolidayWork.getApplication().getPrePostAtr().value, PrePostInitAtr.class), 
					Optional.ofNullable(appHolidayWorkPre.isPresent() ? appHolidayWorkPre.get().getApplicationTime() : null), 
					Optional.ofNullable(appHolidayWork.getApplicationTime()),
					Optional.ofNullable(actualApplicationTime)
				);
			if(appHdWorkDispInfoOutput.getCalculationResult().isPresent()) {  
				appHdWorkDispInfoOutput.getCalculationResult().get().setActualOvertimeStatus(Optional.ofNullable(overStateOutput));
				appHdWorkDispInfoOutput.getCalculationResult().get().setCalculatedFlag(CalculatedFlag.CALCULATED);
			} else {
				HolidayWorkCalculationResult calculationResult = new HolidayWorkCalculationResult();
				calculationResult.setActualOvertimeStatus(Optional.ofNullable(overStateOutput));
				calculationResult.setCalculatedFlag(CalculatedFlag.CALCULATED);
				appHdWorkDispInfoOutput.setCalculationResult(Optional.of(calculationResult));
			}
		}
		// 申請中の勤務情報をセットする
		appHdWorkDispInfoOutput.setWorkInfo(Optional.of(new WorkInfo(
				Optional.ofNullable(appHolidayWork.getWorkInformation().getWorkTypeCode()).map(x -> x.v()).orElse(null),
				appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(x -> x.v()).orElse(null))));
		
		
		
		appHdWorkDispInfoOutput.setHdWorkDispInfoWithDateOutput(hdWorkDispInfoWithDateOutput);
		
		return hdWorkDetailOutput;
	}

	@Override
	public CheckBeforeOutput checkBeforeUpdate(boolean require, String companyId,
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput, AppHolidayWork appHolidayWork) {
		CheckBeforeOutput checkBeforeOutput = new CheckBeforeOutput();
		String workTypeCode = Optional.ofNullable(appHolidayWork.getWorkInformation().getWorkTypeCode()).map(x -> x.v()).orElse(null);
		String workTimeCode = appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(x -> x.v()).orElse(null);
		if (appHdWorkDispInfoOutput.getWorkInfo().isPresent()) {
			workTypeCode = !workTypeCode.equals(appHdWorkDispInfoOutput.getWorkInfo().get().getWorkType()) ? workTypeCode : null;
			workTimeCode = !workTimeCode.equals(appHdWorkDispInfoOutput.getWorkInfo().get().getWorkTime()) ? workTimeCode : null;			
		}
		//	4-1.詳細画面登録前の処理
	      int totalOverTime = 0;
	        totalOverTime = appHolidayWork.getApplicationTime().getApplicationTime().stream()
	                .map(x -> x.getApplicationTime().v())
	                .mapToInt(Integer::intValue)
	                .sum();
	        totalOverTime += appHolidayWork.getApplicationTime().getOverTimeShiftNight().isPresent() 
	                && appHolidayWork.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight() != null ? 
	                appHolidayWork.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight().v() : 0;
	        totalOverTime += appHolidayWork.getApplicationTime().getFlexOverTime().map(AttendanceTimeOfExistMinus::v).orElse(0);
	        TimeDigestionParam timeDigestionParam = new TimeDigestionParam(
	                0, 
	                0, 
	                0, 
	                0, 
	                0, 
	                totalOverTime, 
	                new ArrayList<TimeLeaveApplicationDetailShare>());
		detailBeforeProcessRegisterService.processBeforeDetailScreenRegistration(companyId, appHolidayWork.getApplication().getEmployeeID(), 
				appHolidayWork.getAppDate().getApplicationDate(), 
				EmploymentRootAtr.APPLICATION.value, 
				appHolidayWork.getAppID(), 
				appHolidayWork.getApplication().getPrePostAtr(), 
				appHolidayWork.getApplication().getVersion(), 
				workTypeCode, 
				workTimeCode, 
				appHdWorkDispInfoOutput.getAppDispInfoStartupOutput(), 
				new ArrayList<String>(), 
				Optional.of(timeDigestionParam), 
				false, 
				Optional.of(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
				appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v));
		
		//3.個別エラーチェック
		checkBeforeOutput = commonHolidayWorkAlgorithm.individualErrorCheck(require, companyId, appHdWorkDispInfoOutput, appHolidayWork, 1);	//mode update = 1

		return checkBeforeOutput;
	}

	@Override
	public AppHdWorkDispInfoOutputMobile getStartMobile(Boolean mode, String companyId, Optional<String> employeeId,
			Optional<GeneralDate> appDate, Optional<AppHdWorkDispInfoOutput> appHdWorkDispInfo,
			Optional<AppHolidayWork> appHolidayWork, AppDispInfoStartupOutput appDispInfoStartupOutput) {
		AppHdWorkDispInfoOutputMobile appHdWorkDispInfoOutputMobile = new AppHdWorkDispInfoOutputMobile();
		if (!mode) { // 修正モード
			// INPUT「休日出勤申請起動時の表示情報」と「休日出勤申請」を返す
			appHdWorkDispInfoOutputMobile.setAppHdWorkDispInfo(appHdWorkDispInfo.get());
			appHdWorkDispInfoOutputMobile.setAppHolidayWork(appHolidayWork);
			return appHdWorkDispInfoOutputMobile;
		}
		AppHdWorkDispInfoOutput appHdWorkDispInfoOutput = this.startA(companyId, 
				employeeId.isPresent() ? Optional.of(Arrays.asList(employeeId.get())) : Optional.empty(), 
				appDate.isPresent() ? Optional.of(Arrays.asList(appDate.get())) : Optional.empty(), 
				appDispInfoStartupOutput);
		appHdWorkDispInfoOutputMobile.setAppHdWorkDispInfo(appHdWorkDispInfoOutput);
		return appHdWorkDispInfoOutputMobile;
	}

	@Override
	public AppHdWorkDispInfoOutput changeDateMobile(String companyId, GeneralDate appDate,
			AppHdWorkDispInfoOutput appHdWorkDispInfo) {
		//	申請表示情報(基準日関係あり)を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.getAppDispInfoWithDate(
				companyId,
				ApplicationType.HOLIDAY_WORK_APPLICATION,
				Arrays.asList(appDate),
				appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput(),
				true,	// true = 新規モード
				Optional.empty());
		//	休日出勤申請起動時の表示情報．申請表示情報．申請表示情報(基準日関係あり) = 取得した「申請表示情報(基準日関係あり) 」
		appHdWorkDispInfo.getAppDispInfoStartupOutput().setAppDispInfoWithDateOutput(appDispInfoWithDateOutput);
		//	1-1.休日出勤申請（新規）起動時初期データを取得する
		List<EmployeeInfoImport> empList = appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst();
		HdWorkDispInfoWithDateOutput hdWorkDispInfoWithDateOutput = commonHolidayWorkAlgorithm.getHdWorkDispInfoWithDateOutput(companyId, 
										!empList.isEmpty() ? empList.get(0).getSid() : null, 
										Optional.ofNullable(appDate), 
										appDispInfoWithDateOutput.getBaseDate(), 
										appDispInfoWithDateOutput.getPrePostAtr(), 
										appDispInfoWithDateOutput.getOpEmploymentSet().orElse(null), 
										appDispInfoWithDateOutput.getOpWorkTimeLst().orElse(Collections.emptyList()), 
										appHdWorkDispInfo.getHolidayWorkAppSet(), 
										appHdWorkDispInfo.getHdWorkOvertimeReflect(), 
										appDispInfoWithDateOutput.getOpActualContentDisplayLst().orElse(Collections.emptyList()));
		//	休日出勤申請起動時の表示情報．休日出勤申請起動時の表示情報(申請対象日関係あり)=上記取得した「休日出勤申請起動時の表示情報(申請対象日関係あり)」
		appHdWorkDispInfo.setHdWorkDispInfoWithDateOutput(hdWorkDispInfoWithDateOutput);
		return appHdWorkDispInfo;
	}

	@Override
	public AppHdWorkDispInfoOutput calculateMobile(
			String companyId,
			AppHdWorkDispInfoOutput appHdWorkDispInfo,
			AppHolidayWork appHolidayWork,
			Boolean mode,
			String employeeId,
			Optional<GeneralDate> appDate,
			Boolean isAgent) {
		//	勤務情報の申請内容をチェックする
		commonHolidayWorkAlgorithm.checkContentApp(
				companyId,
				appHdWorkDispInfo,
				appHolidayWork,
				mode);
		//	休日出勤申請起動時の表示情報．休出申請設定．申請詳細設定．時刻計算利用区分をチェックする
		if(appHdWorkDispInfo.getHolidayWorkAppSet().getApplicationDetailSetting().getTimeCalUse().equals(nts.uk.shr.com.enumcommon.NotUseAtr.USE)) {
			BreakTimeZoneSetting breakTimeZoneSettingList = new BreakTimeZoneSetting();
			List<DeductionTime> timeZones = appHolidayWork.getBreakTimeList().isPresent() ? appHolidayWork.getBreakTimeList().get().stream()
												.map(breakTime -> new DeductionTime(breakTime.getTimeZone().getStartTime(), breakTime.getTimeZone().getEndTime()))
												.collect(Collectors.toList())
												: Collections.emptyList();
			breakTimeZoneSettingList.setTimeZones(timeZones);
			appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().setBreakTimeZoneSettingList(Optional.ofNullable(breakTimeZoneSettingList));
			WorkHours workHours = new WorkHours();
			if(appHolidayWork.getWorkingTimeList().isPresent()) {
				List<TimeZoneWithWorkNo> workingTimeList = appHolidayWork.getWorkingTimeList().get();
				workingTimeList.stream().forEach(workingTime -> {
					if(workingTime.getWorkNo().v() == 1) {
						workHours.setStartTimeOp1(Optional.ofNullable(workingTime.getTimeZone().getStartTime()));
						workHours.setEndTimeOp1(Optional.ofNullable(workingTime.getTimeZone().getEndTime()));
					}
					if(workingTime.getWorkNo().v() == 2) {
						workHours.setStartTimeOp2(Optional.ofNullable(workingTime.getTimeZone().getStartTime()));
						workHours.setEndTimeOp2(Optional.ofNullable(workingTime.getTimeZone().getEndTime()));
					}
				});
			}
			appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().setWorkHours(workHours);
			WorkContent workContent = commonHolidayWorkAlgorithm.getWorkContent(appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput());
			
			List<PreAppContentDisplay> preAppContentDisplayList = appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput()
					.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
			Optional<AppHolidayWork> appHolidayWorkPre = !preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty();
			List<EmployeeInfoImport> empList = appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst();
			//	6.計算する(従業員)
			HolidayWorkCalculationResult calculationResult = this.calculate(
					companyId,
					!empList.isEmpty() ? empList.get(0).getSid() : employeeId,
					appDate,
					appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getPrePostAtr(),
					appHdWorkDispInfo.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet(),
					appHolidayWorkPre.isPresent() ? appHolidayWorkPre.get().getApplicationTime() : null,
					appHdWorkDispInfo.getHdWorkDispInfoWithDateOutput().getActualApplicationTime().orElse(null),
					workContent,
					isAgent);
			//	休日出勤申請起動時の表示情報．計算結果＝取得した「休日出勤の計算結果(従業員)」
			appHdWorkDispInfo.setCalculationResult(Optional.ofNullable(calculationResult));
			if(calculationResult != null 
					&& calculationResult.getActualOvertimeStatus()
										.map(x -> x.getAchivementExcess().equals(ExcessState.EXCESS_ERROR))
										.orElse(false)) {
				throw new BusinessException("Msg_1556");
			}
		}
		return appHdWorkDispInfo;
	}
}
