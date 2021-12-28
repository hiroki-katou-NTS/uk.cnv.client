package nts.uk.ctx.at.request.dom.application.holidayworktime.commonalgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BundledBusinessException;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.EmployeeInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.employee.EmploymentAdapterRQ;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.CommonOvertimeHoliday;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.PreActualColorCheck;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.CollectAchievement;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.other.PreAppContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.Time36UpperLimitCheck;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.AppHdWorkDispInfoOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdWorkBreakTimeSetOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.HdWorkDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.InitWorkTypeWorkTime;
import nts.uk.ctx.at.request.dom.application.overtime.ApplicationTime;
import nts.uk.ctx.at.request.dom.application.overtime.AttendanceType_Update;
import nts.uk.ctx.at.request.dom.application.overtime.ExcessState;
import nts.uk.ctx.at.request.dom.application.overtime.OutDateApplication;
import nts.uk.ctx.at.request.dom.application.overtime.OverStateOutput;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.ICommonAlgorithmOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.service.CheckWorkingInfoResult;
import nts.uk.ctx.at.request.dom.application.overtime.service.OverTimeContent;
import nts.uk.ctx.at.request.dom.application.overtime.service.OvertimeService;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkContent;
import nts.uk.ctx.at.request.dom.application.overtime.service.WorkHours;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.HolidayWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.workrecord.dailyrecordprocess.dailycreationwork.BreakTimeZoneSetting;
import nts.uk.ctx.at.request.dom.workrecord.remainmanagement.InterimRemainDataMngCheckRegisterRequest;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.AppRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.PrePostAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.require.RemainNumberTempRequireService;
import nts.uk.ctx.at.shared.dom.scherec.application.timeleaveapplication.TimeLeaveApplicationDetailShare;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWork;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.overtimeholidaywork.AppReflectOtHdWorkRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.Time36ErrorInforList;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcessCommon;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SubstitutionHolidayOutput;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkTypeByIndividualWorkDay;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.DeprecateClassification;
import nts.uk.ctx.at.shared.dom.worktype.HolidayAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.ctx.at.shared.dom.worktype.service.JudgmentOneDayHoliday;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * Refactor5
 * @author huylq
 *
 */
@Stateless
public class CommonAlgorithmHolidayWorkImpl implements ICommonAlgorithmHolidayWork{
	
	@Inject
	private HolidayWorkAppSetRepository holidayWorkAppSetRepository;
	
	@Inject
	private AppReflectOtHdWorkRepository appReflectOtHdWorkRepository;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;
	
	@Inject
	private JudgmentOneDayHoliday judgmentOneDayHoliday;
	
	@Inject
	private ICommonAlgorithmOverTime commonAlgorithmOverTime;
	
	@Inject
	private CommonOvertimeHoliday commonOvertimeHoliday;
	
	@Inject
	private PreActualColorCheck preActualColorCheck;
	
	@Inject
	private AbsenceTenProcessCommon absenceTenProcessCommon;
	
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	
	@Inject
	private OvertimeService overtimeService;
	
	@Inject
	private RemainNumberTempRequireService requireService;
	
	@Inject
	private InterimRemainDataMngCheckRegisterRequest checkRegister;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private CollectAchievement collectAchievement;
	
	@Inject
	private Time36UpperLimitCheck time36UpperLimitCheck;
	
	@Inject
	private EmploymentAdapterRQ employmentAdapter;
	
	@Inject
	private NewBeforeRegister processBeforeRegister;
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Override
	public HolidayWorkAppSet getHolidayWorkSetting(String companyId) {
		Optional<HolidayWorkAppSet> holidayWorkSetting = holidayWorkAppSetRepository.findSettingByCompany(companyId);
		return holidayWorkSetting.orElse(null);
	}

	@Override
	public AppReflectOtHdWork getHdWorkOvertimeReflect(String companyId) {
		Optional<AppReflectOtHdWork> hdWorkOvertimeReflect = appReflectOtHdWorkRepository.findByCompanyId(companyId);
		return hdWorkOvertimeReflect.orElse(null);
	}

	@Override
	public HdWorkDispInfoWithDateOutput getHdWorkDispInfoWithDateOutput(String companyId, String employeeId,
			Optional<GeneralDate> date, GeneralDate baseDate, PrePostInitAtr prePostAtr, AppEmploymentSet employmentSet,
			List<WorkTimeSetting> workTimeList, HolidayWorkAppSet holidayWorkSetting,
			AppReflectOtHdWork hdWorkOvertimeReflect, List<ActualContentDisplay> actualContentDisplayList) {
		HdWorkDispInfoWithDateOutput hdWorkDispInfoWithDateOutput = new HdWorkDispInfoWithDateOutput();
		
		//1-2.起動時勤務種類リストを取得する
		List<WorkType> workTypeList = this.getWorkTypeList(companyId, employmentSet);
		hdWorkDispInfoWithDateOutput.setWorkTypeList(Optional.of(workTypeList));
		
		//1-3.起動時勤務種類・就業時間帯の初期選択		
		InitWorkTypeWorkTime initWork = this.initWork(companyId, employeeId, baseDate, workTypeList, workTimeList, actualContentDisplayList);
		
		hdWorkDispInfoWithDateOutput.setInitWorkType(initWork.getInitWorkTypeCd());
		hdWorkDispInfoWithDateOutput.setInitWorkTime(initWork.getInitWorkTimeCd());
		hdWorkDispInfoWithDateOutput.setInitWorkTypeName(Optional.ofNullable(initWork.getInitWorkType().isPresent() ? initWork.getInitWorkType().get().getName() : null));
		hdWorkDispInfoWithDateOutput.setInitWorkTimeName(Optional.ofNullable(initWork.getInitWorkTime().isPresent() ? 
				initWork.getInitWorkTime().get().getWorkTimeDisplayName().getWorkTimeName() : null));
		
		//	初期表示する出退勤時刻を取得する
		OverTimeContent overTimeContent = this.getOverTimeContent(initWork.getInitWorkTypeCd(), initWork.getInitWorkTimeCd(), actualContentDisplayList);
		
		Optional<WorkHours> workHours = commonAlgorithmOverTime.initAttendanceTime(companyId, date, overTimeContent, holidayWorkSetting.getApplicationDetailSetting());
		hdWorkDispInfoWithDateOutput.setWorkHours(workHours.orElse(null));
		
		//01-01_休憩時間を取得する
		HdWorkBreakTimeSetOutput hdWorkBreakTimeSetOutput = this.getBreakTime(companyId, ApplicationType.HOLIDAY_WORK_APPLICATION, 
				initWork.getInitWorkTypeCd().isPresent() ? initWork.getInitWorkTypeCd().get().v() : null, 
				initWork.getInitWorkTimeCd().isPresent() ? initWork.getInitWorkTimeCd().get().v() : null, 
				workHours.isPresent() ? workHours.get().getStartTimeOp1() : Optional.empty(), 
				workHours.isPresent() ? workHours.get().getEndTimeOp1() : Optional.empty(), 
				UseAtr.toEnum(holidayWorkSetting.getApplicationDetailSetting().getTimeCalUse().value), 
				hdWorkOvertimeReflect.getHolidayWorkAppReflect().getAfter().getBreakLeaveApplication().getBreakReflectAtr().value==0 ? new Boolean(false) :new Boolean(true));
		hdWorkDispInfoWithDateOutput.setBreakTimeZoneSettingList(Optional.of(new BreakTimeZoneSetting(hdWorkBreakTimeSetOutput.getDeductionTimeLst())));
		
		//07-02_実績取得・状態チェック
		ApplicationTime applicationTime = preActualColorCheck.checkStatus(companyId, employeeId, date.orElse(null), ApplicationType.HOLIDAY_WORK_APPLICATION, 
				initWork.getInitWorkTypeCd().orElse(null), initWork.getInitWorkTimeCd().orElse(null), holidayWorkSetting.getOvertimeLeaveAppCommonSet().getOverrideSet(), 
				Optional.of(holidayWorkSetting.getCalcStampMiss()), hdWorkBreakTimeSetOutput.getDeductionTimeLst(), 
				!actualContentDisplayList.isEmpty() ? Optional.of(actualContentDisplayList.get(0)): Optional.empty());
		hdWorkDispInfoWithDateOutput.setActualApplicationTime(Optional.ofNullable(applicationTime));
		
		//10-2.代休の設定を取得する
		SubstitutionHolidayOutput subHolidayOutput = absenceTenProcessCommon.getSettingForSubstituteHoliday(companyId, employeeId, baseDate);
		if(subHolidayOutput != null) {
			hdWorkDispInfoWithDateOutput.setSubHdManage(subHolidayOutput.isSubstitutionFlg());
		}
		
		return hdWorkDispInfoWithDateOutput;
	}

	@Override
	public HdWorkBreakTimeSetOutput getBreakTime(String companyID, ApplicationType appType, String workTypeCD,
			String workTimeCD, Optional<TimeWithDayAttr> startTime, Optional<TimeWithDayAttr> endTime,
			UseAtr timeCalUse, Boolean breakTimeDisp) {
		HdWorkBreakTimeSetOutput result = new HdWorkBreakTimeSetOutput(false, Collections.emptyList());
		// 01-17_休憩時間帯を表示するか判断
		boolean displayRestTime = commonOvertimeHoliday.getRestTime(
				companyID,
				timeCalUse,
				breakTimeDisp,
				ApplicationType.HOLIDAY_WORK_APPLICATION);
		result.setDisplayRestTime(displayRestTime);
		//	休憩時間帯表示区分をチェック 
		if(displayRestTime) {
			//	休憩時間帯を取得する
			List<DeductionTime> deductionTimeLst = commonOvertimeHoliday.getBreakTimes(companyID, workTypeCD, workTimeCD, startTime, endTime);
			result.setDeductionTimeLst(deductionTimeLst);
		}
		return result;
	}
	
	@Override
	public CheckBeforeOutput individualErrorCheck(
			boolean require,
			String companyId,
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput,
			AppHolidayWork appHolidayWork,
			Integer mode) {
		CheckBeforeOutput checkBeforeOutput = new CheckBeforeOutput();
		String workTypeCode = Optional.ofNullable(appHolidayWork.getWorkInformation().getWorkTypeCode()).map(x -> x.v()).orElse(null);
		String workTimeCode = appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(x -> x.v()).orElse(null);
		if (appHdWorkDispInfoOutput.getWorkInfo().isPresent()) {
			workTypeCode = !workTypeCode.equals(appHdWorkDispInfoOutput.getWorkInfo().get().getWorkType()) ? workTypeCode : null;
			workTimeCode = !workTimeCode.equals(appHdWorkDispInfoOutput.getWorkInfo().get().getWorkTime()) ? workTimeCode : null;			
		}
		//	勤務種類、就業時間帯チェックのメッセージを表示
		this.checkWorkMessageDisp(
				workTypeCode,
				workTimeCode);
		
		//	計算ボタン未クリックチェック
		commonOvertimeHoliday.calculateButtonCheck(appHdWorkDispInfoOutput.getCalculationResult().isPresent() ? 
				appHdWorkDispInfoOutput.getCalculationResult().get().getCalculatedFlag() : CalculatedFlag.UNCALCULATED, 
				EnumAdaptor.valueOf(appHdWorkDispInfoOutput.getHolidayWorkAppSet().getApplicationDetailSetting().getTimeCalUse().value, UseAtr.class));
		
		//	休出時間のチェック
		this.checkHdWorkTime(appHolidayWork.getApplicationTime());
		
		//	事前申請が必須か確認する
		List<PreAppContentDisplay> preAppContentDisplayList = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput()
				.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
		appHdWorkDispInfoOutput.getHolidayWorkAppSet().getApplicationDetailSetting().checkAdvanceApp(ApplicationType.HOLIDAY_WORK_APPLICATION, 
				appHolidayWork.getApplication().getPrePostAtr(), Optional.empty(), 
				!preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty());
		
		//	事前申請・実績超過チェック
		List<ConfirmMsgOutput> confirmMsgOutputs = this.checkExcess(appHdWorkDispInfoOutput, appHolidayWork);
		
		//	申請時の乖離時間をチェックする
		overtimeService.checkDivergenceTime(require, ApplicationType.HOLIDAY_WORK_APPLICATION, 
				Optional.empty(), Optional.of(appHolidayWork), appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet());
		
		//	社員に対応する締め期間を取得する
		val requireM3 = requireService.createRequire();
        val cacheCarrier = new CacheCarrier();
        DatePeriod closingPeriod = ClosureService.findClosurePeriod(requireM3, cacheCarrier, appHolidayWork.getApplication().getEmployeeID(), 
        		appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getBaseDate());
        
        List<AppRemainCreateInfor> appDatas = new ArrayList<AppRemainCreateInfor>();
        
        List<GeneralDate> lstAppDate = new ArrayList<GeneralDate>();
        lstAppDate.add(appHolidayWork.getAppDate().getApplicationDate());
        ApplicationTime applicationTime = appHolidayWork.getApplicationTime();
        
        Integer breakTimeTotal = 
        		applicationTime.getApplicationTime()
							   .stream()
							   .filter(x -> x.getAttendanceType() == AttendanceType_Update.BREAKTIME)
							   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
							   .sum()
        		+ applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getMidNightOutSide())).map(x -> x.v()).orElse(0)
        		+ applicationTime.getOverTimeShiftNight().map(x -> x.getMidNightHolidayTimes()
        															.stream()
        															.mapToInt(y -> y.getAttendanceTime().v())
        															.sum()
        				).orElse(0);
        									   
        Integer overTimeTotal = 
        		applicationTime.getApplicationTime()
				   .stream()
				   .filter(x -> x.getAttendanceType() != AttendanceType_Update.BREAKTIME)
				   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
				   .sum()
			   + applicationTime.getFlexOverTime().map(x -> x.v()).orElse(0)
			   + applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getOverTimeMidNight())).map(x -> x.v()).orElse(0);
			   
        
        
        AppRemainCreateInfor appData = AppRemainCreateInfor.builder()
        		.sid(appHolidayWork.getEmployeeID())
        		.appId(appHolidayWork.getAppID())
        		.inputDate(appHolidayWork.getInputDate())
        		.appDate(appHolidayWork.getAppDate().getApplicationDate())
        		.prePosAtr(EnumAdaptor.valueOf(appHolidayWork.getPrePostAtr().value, PrePostAtr.class))
        		.appType(EnumAdaptor.valueOf(appHolidayWork.getAppType().value, nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType.class))
        		.workTypeCode(Optional.of(appHolidayWork.getWorkInformation().getWorkTimeCode().v()))
        		.workTimeCode(appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().flatMap(x -> Optional.of(x.v())))
        		.vacationTimes(Collections.emptyList())
        		.appBreakTimeTotal(breakTimeTotal > 0 ? Optional.of(breakTimeTotal) : Optional.empty())
        		.appOvertimeTimeTotal(overTimeTotal > 0 ? Optional.of(overTimeTotal) : Optional.empty())
        		.startDate(appHolidayWork.getOpAppStartDate().flatMap(x -> Optional.of(x.getApplicationDate())))
        		.endDate(appHolidayWork.getOpAppEndDate().flatMap(x -> Optional.of(x.getApplicationDate())))
        		.lstAppDate(lstAppDate)
        		.timeDigestionUsageInfor(Optional.empty()) 
        		.build();
        
        appDatas.add(appData);
        
		// 登録時の残数チェック  #118506
//        InterimRemainCheckInputParam checkRegisterParam =
//        		InterimRemainCheckInputParam.builder()
//        			.cid(companyId)
//        			.sid(appHolidayWork.getApplication().getEmployeeID())
//        			.datePeriod(new DatePeriod(
//        				closingPeriod.start(),
//        				closingPeriod.end().addYears(1).addDays(-1)
//					))
//        			.mode(false)
//        			.baseDate(appHolidayWork.getApplication().getAppDate().getApplicationDate())
//        			.registerDate(new DatePeriod(
//        				appHolidayWork.getApplication()
//        							  .getAppDate()
//        							  .getApplicationDate(),
//        				appHolidayWork.getApplication()
//        							  .getAppDate()
//        							  .getApplicationDate()
//					 ))
//        			.chkRegister(true)
//        			.recordData(Collections.emptyList())
//        			.scheData(Collections.emptyList())
//        			.appData(appDatas)
//        			.chkSubHoliday(true)
//        			.chkPause(false)
//        			.chkAnnual(false)
//        			.chkFundingAnnual(false)
//        			.chkSpecial(false)
//        			.chkPublicHoliday(false)
//        			.chkSuperBreak(false)
//        			.chkChildNursing(false)
//        			.chkLongTermCare(false)
//        			.build();
//        
//        EarchInterimRemainCheck earchInterimRemainCheck = checkRegister.checkRegister(checkRegisterParam);
//        if(earchInterimRemainCheck.isChkSubHoliday()) {
//        	confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1409", Arrays.asList("代休不足区分"))); //missing param
//        }
        
        BundledBusinessException bundledBusinessExceptions = BundledBusinessException.newInstance();
        //18.３６時間の上限チェック(新規登録)_NEW
        Time36ErrorInforList time36UpperLimitCheckResult = time36UpperLimitCheck.checkRegister(companyId, 
        		appHolidayWork.getApplication().getEmployeeID(), 
        		appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(), 
        		appHolidayWork.getApplication(),
        		Optional.empty(), 
        		Optional.of(appHolidayWork),
        		appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr(), 
        		appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr());
        if(!time36UpperLimitCheckResult.getTime36AgreementErrorLst().isEmpty()) {
        	time36UpperLimitCheckResult.getTime36AgreementErrorLst().forEach(error -> {
            	switch(error.getTime36AgreementErrorAtr()) {
    	        	case MONTH_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_1535", 
        						this.convertTime_Short_HM(error.getAgreementTime()), 
        						this.convertTime_Short_HM(error.getThreshold())
    	        				
    	        				);
    	        		break;
    	        	case YEAR_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_1536", 
        						this.convertTime_Short_HM(error.getAgreementTime()),
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        	case MAX_MONTH_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_1537", 
        						this.convertTime_Short_HM(error.getAgreementTime()),
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        	case MAX_YEAR_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_2056", 
        						this.convertTime_Short_HM(error.getAgreementTime()),
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        	case MAX_MONTH_AVERAGE_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_1538", 
    	        				error.getOpYearMonthPeriod().map(x -> x.start().year() + "/" + x.start().month()).orElse(""),
    							error.getOpYearMonthPeriod().map(x -> x.end().year() + "/" + x.end().month()).orElse(""),
    							this.convertTime_Short_HM(error.getAgreementTime()), 
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        		default: break;
            	}
            });
        }
        
        if (!CollectionUtil.isEmpty(bundledBusinessExceptions.getMessageId())) {
        	throw bundledBusinessExceptions;
        }
        
    	//	申請の矛盾チェック
    	commonAlgorithm.appConflictCheck(companyId, appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0), 
    			Arrays.asList(appHolidayWork.getApplication().getAppDate().getApplicationDate()), 
    			Arrays.asList(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
    			appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()));
        
        checkBeforeOutput.setConfirmMsgOutputs(confirmMsgOutputs);
		return checkBeforeOutput;
	}

	@Override
	public Map<String, List<ConfirmMsgOutput>> individualErrorCheckMulti(boolean require, String companyId, List<String> empList,
			AppHdWorkDispInfoOutput appHdWorkDispInfoOutput, AppHolidayWork appHolidayWork) {
		Map<String, List<ConfirmMsgOutput>> confirmMsgOutputMap = new HashMap<String, List<ConfirmMsgOutput>>();
		
		//12.マスタ勤務種類、就業時間帯データをチェック
//		CheckWorkingInfoResult checkWorkingInfoResult = 
		otherCommonAlgorithm.checkWorkingInfo(companyId,
					appHolidayWork.getWorkInformation().getWorkTypeCode().v(), 
					appHolidayWork.getWorkInformation().getWorkTimeCode().v());
		
		//03-06_計算ボタンチェック
		commonOvertimeHoliday.calculateButtonCheck(appHdWorkDispInfoOutput.getCalculationResult().isPresent() ? 
				appHdWorkDispInfoOutput.getCalculationResult().get().getCalculatedFlag() : CalculatedFlag.UNCALCULATED, 
				EnumAdaptor.valueOf(appHdWorkDispInfoOutput.getHolidayWorkAppSet().getApplicationDetailSetting().getTimeCalUse().value, UseAtr.class));

		//	休出時間のチェック
		this.checkHdWorkTime(appHolidayWork.getApplicationTime());
		
		//INPUT．申請者リストをループする
		empList.forEach(empId -> {
			AppHolidayWork empAppHolidayWork = appHolidayWork;
			AppHdWorkDispInfoOutput empAppHdWorkDispInfoOutput = appHdWorkDispInfoOutput;
			empAppHolidayWork.getApplication().setEmployeeID(empId);
			Optional<EmployeeInfoImport> employeeInfo = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().stream()
			.filter(empInfo -> empInfo.getSid().equals(empId)).findFirst();
			
			//	事前内容の取得
			List<PreAppContentDisplay> preAppContentDisplayLst = collectAchievement.getPreAppContents(companyId, empId,
					Arrays.asList(empAppHolidayWork.getApplication().getAppDate().getApplicationDate()), empAppHolidayWork.getApplication().getAppType(), Optional.empty());
			//	実績内容の取得
			List<ActualContentDisplay> actualContentDisplayLst = collectAchievement.getAchievementContents(companyId, empId, 
					Arrays.asList(empAppHolidayWork.getApplication().getAppDate().getApplicationDate()), empAppHolidayWork.getApplication().getAppType());
			
			//07-02_実績取得・状態チェック
			ApplicationTime applicationTime = preActualColorCheck.checkStatus(companyId, empId, empAppHolidayWork.getApplication().getAppDate().getApplicationDate(), 
					ApplicationType.HOLIDAY_WORK_APPLICATION, 
					empAppHolidayWork.getWorkInformation().getWorkTypeCode(), 
					empAppHolidayWork.getWorkInformation().getWorkTimeCode(), 
					empAppHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getOverrideSet(), 
					Optional.of(empAppHdWorkDispInfoOutput.getHolidayWorkAppSet().getCalcStampMiss()), 
					empAppHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().getBreakTimeZoneSettingList().isPresent() ? 
							empAppHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().getBreakTimeZoneSettingList().get().getTimeZones() : Collections.emptyList(), 
					Optional.ofNullable(!actualContentDisplayLst.isEmpty() ? actualContentDisplayLst.get(0) : null));
			
			//	ループする社員の休日出勤申請起動時の表示情報 = INPUT．休日出勤申請起動時の表示情報
			empAppHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().setOpPreAppContentDisplayLst(Optional.of(preAppContentDisplayLst));
			empAppHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().setOpActualContentDisplayLst(Optional.of(actualContentDisplayLst));
			empAppHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().setActualApplicationTime(Optional.ofNullable(applicationTime));

			//	事前申請・実績超過チェック
			List<ConfirmMsgOutput> confirmMsgOutputs = this.checkExcess(empAppHdWorkDispInfoOutput, empAppHolidayWork);
			if(employeeInfo.isPresent() && confirmMsgOutputs.size() > 0) {
				confirmMsgOutputMap.put(employeeInfo.get().getBussinessName(), this.toMultiMessage(confirmMsgOutputs));
			}
			
			//	申請時の乖離時間をチェックする
			overtimeService.checkDivergenceTime(require, ApplicationType.HOLIDAY_WORK_APPLICATION, 
					Optional.empty(), Optional.of(empAppHolidayWork), empAppHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet());
			
			//	社員に対応する締め期間を取得する
			val requireM3 = requireService.createRequire();
	        val cacheCarrier = new CacheCarrier();
	        DatePeriod closingPeriod = ClosureService.findClosurePeriod(requireM3, cacheCarrier, empAppHolidayWork.getApplication().getEmployeeID(), 
	        		empAppHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getBaseDate());
	        
	        List<AppRemainCreateInfor> appDatas = new ArrayList<AppRemainCreateInfor>();
	        
	        List<GeneralDate> lstAppDate = new ArrayList<GeneralDate>();
	        lstAppDate.add(empAppHolidayWork.getAppDate().getApplicationDate());
	        ApplicationTime applicationTimeRemain = empAppHolidayWork.getApplicationTime();
	        Integer breakTimeTotal = 
	        		applicationTimeRemain.getApplicationTime()
								   .stream()
								   .filter(x -> x.getAttendanceType() == AttendanceType_Update.BREAKTIME)
								   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
								   .sum()
	        		+ applicationTimeRemain.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getMidNightOutSide())).map(x -> x.v()).orElse(0)
	        		+ applicationTimeRemain.getOverTimeShiftNight().map(x -> x.getMidNightHolidayTimes()
	        															.stream()
	        															.mapToInt(y -> y.getAttendanceTime().v())
	        															.sum()
	        				).orElse(0);
	        									   
	        Integer overTimeTotal = 
	        		applicationTimeRemain.getApplicationTime()
					   .stream()
					   .filter(x -> x.getAttendanceType() != AttendanceType_Update.BREAKTIME)
					   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
					   .sum()
				   + applicationTimeRemain.getFlexOverTime().map(x -> x.v()).orElse(0)
				   + applicationTimeRemain.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getOverTimeMidNight())).map(x -> x.v()).orElse(0);
				   
	        	   
	        
	        
	        AppRemainCreateInfor appData = AppRemainCreateInfor.builder()
	        		.sid(empAppHolidayWork.getEmployeeID())
	        		.appId(empAppHolidayWork.getAppID())
	        		.inputDate(empAppHolidayWork.getInputDate())
	        		.appDate(empAppHolidayWork.getAppDate().getApplicationDate())
	        		.prePosAtr(EnumAdaptor.valueOf(empAppHolidayWork.getPrePostAtr().value, PrePostAtr.class))
	        		.appType(EnumAdaptor.valueOf(empAppHolidayWork.getAppType().value, nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType.class))
	        		.workTypeCode(Optional.of(empAppHolidayWork.getWorkInformation().getWorkTimeCode().v()))
	        		.workTimeCode(empAppHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().flatMap(x -> Optional.of(x.v())))
	        		.vacationTimes(Collections.emptyList())
	        		.appBreakTimeTotal(breakTimeTotal > 0 ? Optional.of(breakTimeTotal) : Optional.empty())
	        		.appOvertimeTimeTotal(overTimeTotal > 0 ? Optional.of(overTimeTotal) : Optional.empty())
	        		.startDate(empAppHolidayWork.getOpAppStartDate().flatMap(x -> Optional.of(x.getApplicationDate())))
	        		.endDate(empAppHolidayWork.getOpAppEndDate().flatMap(x -> Optional.of(x.getApplicationDate())))
	        		.lstAppDate(lstAppDate)
	        		.timeDigestionUsageInfor(Optional.empty()) 
	        		.build();
	        
	        appDatas.add(appData);
	        
	        //	登録時の残数チェック   #118506
//	        InterimRemainCheckInputParam checkRegisterParam =
//	        		InterimRemainCheckInputParam.builder()
//	        			.cid(companyId)
//	        			.sid(appHolidayWork.getApplication().getEmployeeID())
//	        			.datePeriod(new DatePeriod(
//	        				closingPeriod.start(),
//	        				closingPeriod.end().addYears(1).addDays(-1)
//						))
//	        			.mode(false)
//	        			.baseDate(appHolidayWork.getApplication().getAppDate().getApplicationDate())
//	        			.registerDate(new DatePeriod(
//	        				appHolidayWork.getApplication()
//	        							  .getAppDate()
//	        							  .getApplicationDate(),
//	        				appHolidayWork.getApplication()
//	        							  .getAppDate()
//	        							  .getApplicationDate()
//						 ))
//	        			.chkRegister(true)
//	        			.recordData(Collections.emptyList())
//	        			.scheData(Collections.emptyList())
//	        			.appData(appDatas)
//	        			.chkSubHoliday(true)
//	        			.chkPause(false)
//	        			.chkAnnual(false)
//	        			.chkFundingAnnual(false)
//	        			.chkSpecial(false)
//	        			.chkPublicHoliday(false)
//	        			.chkSuperBreak(false)
//	        			.chkChildNursing(false)
//	        			.chkLongTermCare(false)
//	        			.build();
//	        EarchInterimRemainCheck earchInterimRemainCheck = checkRegister.checkRegister(checkRegisterParam);
//	        if(earchInterimRemainCheck.isChkSubHoliday()) {
//				confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1409", Arrays.asList("代休不足区分"))); //missing param
//	        }
	        
	        //	社員IDと基準日から社員の雇用コードを取得
	        Optional<EmploymentHistoryImported> empHist = employmentAdapter.getEmpHistBySid(companyId, empId, 
	        		empAppHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getBaseDate());
	        
	        //18.３６時間の上限チェック(新規登録)_NEW
	        Time36ErrorInforList time36UpperLimitCheckResult = time36UpperLimitCheck.checkRegister(companyId, 
	        		appHolidayWork.getApplication().getEmployeeID(), 
	        		appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(), 
	        		appHolidayWork.getApplication(),
	        		Optional.empty(), 
	        		Optional.of(appHolidayWork),
	        		appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr(), 
	        		appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr());
	        
	        BundledBusinessException bundledBusinessExceptions = BundledBusinessException.newInstance();
	        if(!time36UpperLimitCheckResult.getTime36AgreementErrorLst().isEmpty()) {
	        	time36UpperLimitCheckResult.getTime36AgreementErrorLst().forEach(error -> {
	        		switch(error.getTime36AgreementErrorAtr()) {
    	        	case MONTH_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_2012", 
    	        				employeeInfo.map(x -> x.getBussinessName()).orElse(""),
        						this.convertTime_Short_HM(error.getAgreementTime()), 
        						this.convertTime_Short_HM(error.getThreshold())
    	        				
    	        				);
    	        		break;
    	        	case YEAR_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_2013", 
    	        				employeeInfo.map(x -> x.getBussinessName()).orElse(""),
        						this.convertTime_Short_HM(error.getAgreementTime()),
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        	case MAX_MONTH_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_2014", 
    	        				employeeInfo.map(x -> x.getBussinessName()).orElse(""),
        						this.convertTime_Short_HM(error.getAgreementTime()),
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        	case MAX_YEAR_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_2057", 
    	        				employeeInfo.map(x -> x.getBussinessName()).orElse(""),
        						this.convertTime_Short_HM(error.getAgreementTime()),
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        	case MAX_MONTH_AVERAGE_ERROR:
    	        		bundledBusinessExceptions.addMessage(
    	        				"Msg_2015", 
    	        				employeeInfo.map(x -> x.getBussinessName()).orElse(""),
    	        				error.getOpYearMonthPeriod().map(x -> x.start().year() + "/" + x.start().month()).orElse(""),
    							error.getOpYearMonthPeriod().map(x -> x.end().year() + "/" + x.end().month()).orElse(""),
        						this.convertTime_Short_HM(error.getAgreementTime()), 
        						this.convertTime_Short_HM(error.getThreshold())
    	        		);
    	        		break;
    	        		default: break;
            	}
	            });
	        }
	        
	        if (!CollectionUtil.isEmpty(bundledBusinessExceptions.getMessageId())) {
	        	throw bundledBusinessExceptions;
	        }
	        
	    	//	申請の矛盾チェック
	    	commonAlgorithm.appConflictCheck(companyId, employeeInfo.orElse(null), 
	    			Arrays.asList(empAppHolidayWork.getApplication().getAppDate().getApplicationDate()), 
	    			Arrays.asList(empAppHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
	    			empAppHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()));
	        
		});
		return confirmMsgOutputMap;
	}
	
	private String convertTime_Short_HM(int time) {
		return (time / 60 + ":" + (time % 60 < 10 ? "0" + time % 60 : time % 60));
	}
	
	private List<ConfirmMsgOutput> toMultiMessage(List<ConfirmMsgOutput> confirmMsgOutputs){
		List<ConfirmMsgOutput> confirmMsgOutputsMulti = confirmMsgOutputs.stream().map(confirmMsg -> {
				switch(confirmMsg.getMsgID()) {
					case "Msg_235": confirmMsg.setMsgID("Msg_1995"); break;
					case "Msg_391": confirmMsg.setMsgID("Msg_1996"); break;
					case "Msg_323": confirmMsg.setMsgID("Msg_1997"); break;
					case "Msg_1134": confirmMsg.setMsgID("Msg_1998"); break;
					case "Msg_1518": confirmMsg.setMsgID("Msg_1999"); break;
					case "Msg_236": confirmMsg.setMsgID("Msg_2000"); break;
					case "Msg_327": confirmMsg.setMsgID("Msg_2001"); break;
					case "Msg_448": confirmMsg.setMsgID("Msg_2002"); break;
					case "Msg_449": confirmMsg.setMsgID("Msg_2003"); break;
					case "Msg_450": confirmMsg.setMsgID("Msg_2004"); break;
					case "Msg_451": confirmMsg.setMsgID("Msg_2005"); break;
					case "Msg_324": confirmMsg.setMsgID("Msg_2008"); break;
					case "Msg_237": confirmMsg.setMsgID("Msg_2009"); break;
					case "Msg_238": confirmMsg.setMsgID("Msg_2010"); break;
					case "Msg_1409": confirmMsg.setMsgID("Msg_2011"); break;
					case "Msg_1535": confirmMsg.setMsgID("Msg_2012"); break;
					case "Msg_1536": confirmMsg.setMsgID("Msg_2013"); break;
					case "Msg_1537": confirmMsg.setMsgID("Msg_2014"); break;
					case "Msg_1538": confirmMsg.setMsgID("Msg_2015"); break;
					case "Msg_1508": confirmMsg.setMsgID("Msg_2019"); break;
					case "Msg_2056": confirmMsg.setMsgID("Msg_2057"); break;
					default:
						confirmMsg.setMsgID(confirmMsg.getMsgID());
						break;
				}
				return confirmMsg;
			}).collect(Collectors.toList());
		return confirmMsgOutputsMulti;
	}
	
	@Override
	public void checkWorkMessageDisp(String workTypeCode, String workTimeCode) {
		String companyId = AppContexts.user().companyId();
		CheckWorkingInfoResult checkWorkingInfoResult = otherCommonAlgorithm.checkWorkingInfo(companyId, workTypeCode, workTimeCode);
		if(checkWorkingInfoResult.isWkTypeError() && checkWorkingInfoResult.isWkTimeError()) {
			throw new BusinessException("Msg_1530", "勤務種類コード"+workTypeCode+","
					+"就業時間帯コード"+workTimeCode);
		}else {
			if(checkWorkingInfoResult.isWkTypeError()) {
				throw new BusinessException("Msg_1530", "勤務種類コード"+workTypeCode);
			}
			if(checkWorkingInfoResult.isWkTimeError()) {
				throw new BusinessException("Msg_1530", "就業時間帯コード"+workTimeCode);
			}
		}
	}
	
	@Override
	public void checkHdWorkTime(ApplicationTime applicationTime) {
		int totalTime = 0;
		totalTime += applicationTime.getApplicationTime().stream().mapToInt(appTime -> appTime.getApplicationTime().v()).sum();
		if(applicationTime.getOverTimeShiftNight().isPresent()) {
			if(applicationTime.getOverTimeShiftNight().get().getOverTimeMidNight() != null) {
				totalTime += applicationTime.getOverTimeShiftNight().get().getOverTimeMidNight().v();
			}
			totalTime += applicationTime.getOverTimeShiftNight().get().getMidNightHolidayTimes().stream()
								.filter(midNightHolidayTime -> midNightHolidayTime.getAttendanceTime() != null)
								.mapToInt(midNightHolidayTime -> midNightHolidayTime.getAttendanceTime().v()).sum();
		}
		if(totalTime <= 0) {
			throw new BusinessException("Msg_1654");
		}
	}

	@Override
	public List<ConfirmMsgOutput> checkExcess(AppHdWorkDispInfoOutput appHdWorkDispInfoOutput, AppHolidayWork appHolidayWork){
		List<ConfirmMsgOutput> confirmMsgOutputs = new ArrayList<ConfirmMsgOutput>();
		
		List<PreAppContentDisplay> preAppContentDisplayList = appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput()
				.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
		Optional<AppHolidayWork> appHolidayWorkOp = !preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty();
		
		OverStateOutput overStateOutput = appHdWorkDispInfoOutput.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet()
				.checkPreApplication(EnumAdaptor.valueOf(appHolidayWork.getApplication().getPrePostAtr().value, PrePostInitAtr.class), 
						Optional.ofNullable(appHolidayWorkOp.isPresent() ? appHolidayWorkOp.get().getApplicationTime() : null), 
						Optional.ofNullable(appHolidayWork.getApplicationTime()), 
						appHdWorkDispInfoOutput.getHdWorkDispInfoWithDateOutput().getActualApplicationTime());
		if(overStateOutput.getIsExistApp()) {
			confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1508", Collections.emptyList()));
		} else {
			OutDateApplication outDateApplication = overStateOutput.getAdvanceExcess();
			ConfirmMsgOutput confirmMsgOutput = 
					this.checkExcessWithMessage(outDateApplication, appHdWorkDispInfoOutput, "Msg_424", ExcessState.EXCESS_ALARM, false);
			if(confirmMsgOutput != null) {
				confirmMsgOutputs.add(confirmMsgOutput);
			}
		}
		switch(overStateOutput.getAchivementStatus()) {
			case EXCESS_ERROR:
				throw new BusinessException("Msg_1746", 
						appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getBussinessName(), 
						appHolidayWork.getApplication().getAppDate().getApplicationDate().toString());
			case EXCESS_ALARM:
				confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1745", 
						Arrays.asList(appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getBussinessName(), 
								appHolidayWork.getApplication().getAppDate().getApplicationDate().toString())));
				break;
			case NO_EXCESS:
				OutDateApplication outDateApplication = overStateOutput.getAchivementExcess();
				
				this.checkExcessWithMessage(outDateApplication, appHdWorkDispInfoOutput, "Msg_1748", ExcessState.EXCESS_ERROR, true);
				
				ConfirmMsgOutput confirmMsgOutput = 
						this.checkExcessWithMessage(outDateApplication, appHdWorkDispInfoOutput, "Msg_1747", ExcessState.EXCESS_ALARM, false);
				if(confirmMsgOutput != null) {
					confirmMsgOutputs.add(confirmMsgOutput);
				}
				break;
			default: break;
		}
		return confirmMsgOutputs;
	}
	
	public ConfirmMsgOutput checkExcessWithMessage(OutDateApplication outDateApplication, AppHdWorkDispInfoOutput appHdWorkDispInfoOutput,
			String messageId, ExcessState excessState, boolean isErrorMessage) {
		List<String> messageContentList = new ArrayList<String>();
		
		List<Integer> overtimeFrameNoList = outDateApplication.getExcessStateDetail().stream()
							.filter(excessStateDetail -> AttendanceType_Update.NORMALOVERTIME.equals(excessStateDetail.getType()) 
									&& excessState.equals(excessStateDetail.getExcessState()))
							.map(excessStateDetail -> excessStateDetail.getFrame().v()).collect(Collectors.toList());
		List<Integer> workdayoffFrameNoList = outDateApplication.getExcessStateDetail().stream()
							.filter(excessStateDetail -> AttendanceType_Update.BREAKTIME.equals(excessStateDetail.getType()) && 
									excessState.equals(excessStateDetail.getExcessState()))
							.map(excessStateDetail -> excessStateDetail.getFrame().v()).collect(Collectors.toList());
		
		appHdWorkDispInfoOutput.getOvertimeFrameList().stream()
		.filter(overtimeFrame -> overtimeFrameNoList.contains(overtimeFrame.getOvertimeWorkFrNo().v().intValue()))
		.forEach(overtimeFrame -> {
			messageContentList.add(overtimeFrame.getOvertimeWorkFrName().v());
		});
		appHdWorkDispInfoOutput.getWorkdayoffFrameList().stream()
		.filter(workdayoffFrame -> workdayoffFrameNoList.contains(workdayoffFrame.getWorkdayoffFrNo().v().intValue()))
		.forEach(workdayoffFrame -> {
			messageContentList.add(workdayoffFrame.getWorkdayoffFrName().v());
		});
		
		outDateApplication.getExcessStateMidnight().stream()
		.filter(excessStateMidnight -> excessStateMidnight.getExcessState().equals(excessState))
		.forEach(excessStateMidnight -> {
			switch(excessStateMidnight.getLegalCfl()) {
				case WithinPrescribedHolidayWork:
					messageContentList.add(TextResource.localize("KAF005_341"));
					break;
				case ExcessOfStatutoryHolidayWork:
					messageContentList.add(TextResource.localize("KAF005_342"));
					break;
				case PublicHolidayWork:
					messageContentList.add(TextResource.localize("KAF005_343"));
					break;
					default: break;
			}
		});
		if(excessState.equals(outDateApplication.getFlex())) {
			messageContentList.add(TextResource.localize("KAF005_65"));
		}
		
		if(excessState.equals(outDateApplication.getOverTimeLate())) {
			messageContentList.add(TextResource.localize("KAF005_63"));
		}
		
		if(!messageContentList.isEmpty()) {
			String messageContent = messageContentList.stream().collect(Collectors.joining(", "));
			if(isErrorMessage) {
				throw new BusinessException(messageId, 
						appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getBussinessName(),
						messageContent);
			} else {
				return new ConfirmMsgOutput(messageId, 
						Arrays.asList(appHdWorkDispInfoOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getBussinessName(), 
								messageContent));
			}
		} else {
			return null;
		}
	}
	
	@Override
	public List<WorkType> getWorkTypeList(String companyId, AppEmploymentSet employmentSet){
		List<WorkType> workTypeList = new ArrayList<WorkType>();
		if(employmentSet != null && employmentSet.getTargetWorkTypeByAppLst() != null && !employmentSet.getTargetWorkTypeByAppLst().isEmpty()) {
			List<String> workTypeCodeList = employmentSet.getTargetWorkTypeByAppLst().stream()
				.filter(workTypeByApp -> workTypeByApp.getAppType().equals(ApplicationType.HOLIDAY_WORK_APPLICATION))
				.map(workTypeByApp -> workTypeByApp.getWorkTypeLst())
				.flatMap(List::stream)
				.distinct()
				.collect(Collectors.toList());
			workTypeList = workTypeRepository.findWorkTypeByCodes(companyId, workTypeCodeList, DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value);
			
		} 
		if(workTypeList.isEmpty()) {
			workTypeList = workTypeRepository.findWorkOneDay(companyId, DeprecateClassification.NotDeprecated.value, WorkTypeUnit.OneDay.value, WorkTypeClassification.HolidayWork.value);
		}
		workTypeList.sort(Comparator.comparing(WorkType::getWorkTypeCode));
		return workTypeList;
	}
	
	
	@Override
	public InitWorkTypeWorkTime initWork(
			String companyId,
			String employeeId,
			GeneralDate baseDate, 
			List<WorkType> workTypeList,
			List<WorkTimeSetting> workTimeList,
			List<ActualContentDisplay> actualContentDisplayList) {
		
		// 社員の労働条件を取得する
		Optional<WorkingConditionItem> workingConditionItem =
				workingConditionItemRepository.getBySidAndStandardDate(employeeId, baseDate);
		Optional<WorkTypeCode> initWorkTypeCd = Optional.empty();
		Optional<WorkTimeCode> initWorkTimeCd = Optional.empty();
		
		boolean isArchivement = !CollectionUtil.isEmpty(actualContentDisplayList) ? 
				actualContentDisplayList.get(0).getOpAchievementDetail().isPresent()
				: false;
		
		if(isArchivement) { // Input．表示する実績内容をチェックする
			//	休出の法定区分を取得
			Optional<HolidayAtr> holidayAtr = 
					judgmentOneDayHoliday.getHolidayAtr(
							companyId,
							actualContentDisplayList.get(0).getOpAchievementDetail().map(x -> x.getWorkTypeCD()).orElse(null)
					);
			//	休日日の勤務種類・就業時間帯の初期選択
			WorkHolidayInfo workHolidayInfo = this.initWorkHoliday(
					companyId,
					employeeId,
					workTypeList,
					workTimeList,
					holidayAtr,
					workingConditionItem);
			// Output．初期選択勤務種類=取得した初期選択勤務種類、Output．初期選択就業時間帯=取得した初期選択就業時間帯
			initWorkTypeCd = workHolidayInfo.getInitWorkTypeCd();
			initWorkTimeCd = workHolidayInfo.getInitWorkTimeCd();
			
		} else {
			// Output.初期選択勤務種類=「労働条件項目.区分別勤務」．勤務種類.休日出勤時．勤務種類コード
			initWorkTypeCd = workingConditionItem.isPresent() ? 
					Optional.of(workingConditionItem.get().getWorkCategory().getWorkType().getHolidayWorkWTypeCode()) : Optional.empty();
			
			// Output.初期選択就業時間帯=「労働条件項目.区分別勤務」.勤務時間帯．休日出勤時．就業時間帯コード
			initWorkTimeCd = workingConditionItem.isPresent() ? 
					workingConditionItem.get().getWorkCategory().getWorkTime().getHolidayWork().getWorkTimeCode() : Optional.empty();
		}
		
		// ドメインモデル「勤務種類」を取得
		Optional<WorkType> initWorkType = workTypeRepository.findByPK(companyId, initWorkTypeCd.isPresent() ? initWorkTypeCd.get().v() : null);
		
		// ドメインモデル「就業時間帯」を取得
		Optional<WorkTimeSetting> initWorkTime = workTimeSettingRepository.findByCode(companyId, initWorkTimeCd.isPresent() ? initWorkTimeCd.get().v() : null);
		
		InitWorkTypeWorkTime initWork = new InitWorkTypeWorkTime(
				initWorkTypeCd,
				initWorkTimeCd,
				initWorkType,
				initWorkTime);
		
		return initWork;
	}
	
	@Override
	public WorkHolidayInfo initWorkHoliday(
			String companyId,
			String employeeId,
			List<WorkType> workTypeList,
			List<WorkTimeSetting> workTimeList,
			Optional<HolidayAtr> holidayAtrOp,
			Optional<WorkingConditionItem> workingConditionItemOp
			) {
		WorkHolidayInfo output =new WorkHolidayInfo();
		if (holidayAtrOp.isPresent() && workingConditionItemOp.isPresent()) {
			HolidayAtr holidayAtr = holidayAtrOp.get();
			WorkingConditionItem workingConditionItem = workingConditionItemOp.get();
			// Output．初期選択就業時間帯=Input．ドメインモデル「労働条件項目」.区分別勤務．勤務時間帯．休日出勤時．就業時間帯コード
			output.setInitWorkTimeCd(
					workingConditionItem.getWorkCategory()
										.getWorkTime()
										.getHolidayWork()
										.getWorkTimeCode());
			boolean hasSetting = false;
			WorkTypeByIndividualWorkDay workTypeByIndividualWorkDay  = workingConditionItem.getWorkCategory().getWorkType();
			switch(holidayAtr) {
				case STATUTORY_HOLIDAYS: 
					// Input．ドメインモデル「労働条件項目」．区分別勤務.勤務種類．法内休出時をチェックする
					if (workTypeByIndividualWorkDay.getInLawBreakTimeWTypeCode().isPresent()) {
						hasSetting = true;
						// Output．初期選択勤務種類=Input．ドメインモデル「労働条件項目」．区分別勤務．勤務種類.法内休出時
						output.setInitWorkTypeCd(workTypeByIndividualWorkDay.getInLawBreakTimeWTypeCode());
					}
					
					break;
				case NON_STATUTORY_HOLIDAYS:
					// Input．ドメインモデル「労働条件項目」．区分別勤務.勤務種類．法外休出時をチェックする
					if (workTypeByIndividualWorkDay.getOutsideLawBreakTimeWTypeCode().isPresent()) {
						hasSetting = true;
						// Output．初期選択勤務種類=Input．ドメインモデル「労働条件項目」．区分別勤務．勤務種類.法外休出時
						output.setInitWorkTypeCd(workTypeByIndividualWorkDay.getOutsideLawBreakTimeWTypeCode());
					}
					break;
				case PUBLIC_HOLIDAY:
					// Input．ドメインモデル「労働条件項目」．区分別勤務.勤務種類．祝日休出時をチェックする
					if (workTypeByIndividualWorkDay.getHolidayAttendanceTimeWTypeCode().isPresent()) {
						hasSetting = true;
						// Output．初期選択勤務種類=Input．ドメインモデル「労働条件項目」．区分別勤務．勤務種類.祝日休出時
						output.setInitWorkTypeCd(workTypeByIndividualWorkDay.getHolidayAttendanceTimeWTypeCode());
						
					}
					break;
				default: 
					break;
			}
			
			if (!hasSetting) {
				
				// Output．初期選択勤務種類=Input．ドメインモデル「労働条件項目」．区分別勤務．勤務種類.休日出勤時
				WorkTypeCode holidayWorkWTypeCode = workTypeByIndividualWorkDay.getHolidayWorkWTypeCode();
				output.setInitWorkTypeCd(Optional.ofNullable(workTypeByIndividualWorkDay.getHolidayWorkWTypeCode()));
				
				// 勤務種類の法定区分を取得
				Optional<HolidayAtr> noneSettingHolidayAtr = 
						judgmentOneDayHoliday.getHolidayAtr(
													companyId,
													holidayWorkWTypeCode.v()
								);
				
				// Input．休日区分と取得した休日出勤時の休日区分をチェックする
				if (noneSettingHolidayAtr.isPresent()) { // 
					if (!noneSettingHolidayAtr.get().equals(holidayAtr)) { // Input．休日区分==取得した休日出勤時の休日区分 が false
						
						// 指定する勤務種類リストから指定する休日区分の勤務種類を取得する
						Optional<WorkType> specifiedHdWorkType = Optional.empty(); 
						specifiedHdWorkType =
								workTypeList.stream()
											.filter(workType -> workType.getWorkTypeSetList() != null && 
														!workType.getWorkTypeSetList().isEmpty() ? 
														workType.getWorkTypeSetList().get(0).getHolidayAtr().equals(holidayAtr) : false)
											.findFirst();
						if(specifiedHdWorkType.isPresent()) {
							output.setInitWorkTypeCd(Optional.of(specifiedHdWorkType.get().getWorkTypeCode()));
						}
						
						
						
					}
				}
				
			}
			
			
			
		}
		
		// Input．勤務種類リストにOutput．初期選択勤務種類が存在するかチェックする
		String wType = output.getInitWorkTypeCd().map(y -> y.v()).orElse(null);
		boolean isWorkType = workTypeList.stream()
					.anyMatch(
							x -> x.getWorkTypeCode().v().equals(wType)
					);
		if (!isWorkType) {
			output.setInitWorkTypeCd(Optional.ofNullable(!CollectionUtil.isEmpty(workTypeList) ? workTypeList.get(0).getWorkTypeCode() : null));
		}
		
		// Input．就業時間帯リストにOutput．初期選択就業時間帯が存在するかチェックする
		String wTime = output.getInitWorkTimeCd().map(x -> x.v()).orElse(null);
		boolean isWorkTime = 
				workTimeList.stream()
							.anyMatch(x -> x.getWorktimeCode().v().equals(wTime));
		
		if (!isWorkTime) {
			output.setInitWorkTimeCd(Optional.ofNullable(!CollectionUtil.isEmpty(workTimeList) ? workTimeList.get(0).getWorktimeCode() : null));
		}
		
		return output;
		
	}
	
	public WorkContent getWorkContent(HdWorkDispInfoWithDateOutput hdWorkDispInfoWithDateOutput) {
		WorkContent workContent = new WorkContent();
		workContent.setWorkTypeCode(hdWorkDispInfoWithDateOutput.getInitWorkType().isPresent()
				? Optional.of(hdWorkDispInfoWithDateOutput.getInitWorkType().get().v())
				: Optional.empty());
		workContent.setWorkTimeCode(hdWorkDispInfoWithDateOutput.getInitWorkTime().isPresent()
				? Optional.of(hdWorkDispInfoWithDateOutput.getInitWorkTime().get().v())
				: Optional.empty());
		WorkHours workHours = hdWorkDispInfoWithDateOutput.getWorkHours();
		TimeZone timeZoneNo1 = new TimeZone();
		TimeZone timeZoneNo2 = new TimeZone();
		if(workHours != null) {
			timeZoneNo1 = new TimeZone(hdWorkDispInfoWithDateOutput.getWorkHours().getStartTimeOp1().orElse(null),
					hdWorkDispInfoWithDateOutput.getWorkHours().getEndTimeOp1().orElse(null));
			if(hdWorkDispInfoWithDateOutput.getWorkHours().getStartTimeOp2().isPresent() && 
					hdWorkDispInfoWithDateOutput.getWorkHours().getEndTimeOp2().isPresent()) {
				timeZoneNo2 = new TimeZone(hdWorkDispInfoWithDateOutput.getWorkHours().getStartTimeOp2().get(),
						hdWorkDispInfoWithDateOutput.getWorkHours().getEndTimeOp2().get());
			}	
		}
		List<TimeZone> timeZones = new ArrayList<TimeZone>();
		if(timeZoneNo1.getStart() != null && timeZoneNo1.getEnd() != null) {
			timeZones.add(timeZoneNo1);
		}
		if(timeZoneNo2.getStart() != null && timeZoneNo2.getEnd() != null) {
			timeZones.add(timeZoneNo2);
		}
		workContent.setTimeZones(timeZones);

		List<BreakTimeSheet> breakTimes = new ArrayList<BreakTimeSheet>();
		if (hdWorkDispInfoWithDateOutput.getBreakTimeZoneSettingList().isPresent()) {
			List<DeductionTime> timeZoneList = hdWorkDispInfoWithDateOutput.getBreakTimeZoneSettingList().get()
					.getTimeZones();
			for (int i = 1; i < timeZoneList.size() + 1 && i <= 10; i++) {
				TimeZone timeZone = timeZoneList.get(i - 1);
				BreakTimeSheet breakTimeSheet = new BreakTimeSheet(new BreakFrameNo(i), timeZone.getStart(),
						timeZone.getEnd());
				breakTimes.add(breakTimeSheet);
			}
		}
		workContent.setBreakTimes(breakTimes);
		return workContent;
	}

	public OverTimeContent getOverTimeContent(Optional<WorkTypeCode> workTypeCode, Optional<WorkTimeCode> workTimeCode, List<ActualContentDisplay> actualContentDisplayList) {
		OverTimeContent overTimeContent = new OverTimeContent();
		WorkHours workHoursSPR = new WorkHours();
		workHoursSPR.setStartTimeOp1(Optional.empty());
		workHoursSPR.setStartTimeOp2(Optional.empty());
		workHoursSPR.setEndTimeOp1(Optional.empty());
		workHoursSPR.setEndTimeOp2(Optional.empty());
		WorkHours workHoursActual = new WorkHours();
		if(!actualContentDisplayList.isEmpty()) {
			ActualContentDisplay actualContentDisplay = actualContentDisplayList.get(0);
			if (actualContentDisplay.getOpAchievementDetail().isPresent()) {
				AchievementDetail achievementDetail = actualContentDisplay.getOpAchievementDetail().get();
				if (achievementDetail.getOpWorkTime().isPresent()) {
					workHoursActual.setStartTimeOp1(Optional.of(new TimeWithDayAttr(achievementDetail.getOpWorkTime().get())));
				}
				if (achievementDetail.getOpLeaveTime().isPresent()) {
					workHoursActual.setEndTimeOp1(Optional.of(new TimeWithDayAttr(achievementDetail.getOpLeaveTime().get())));
				}
				if (achievementDetail.getOpWorkTime2().isPresent()) {
					workHoursActual.setStartTimeOp2(Optional.of(new TimeWithDayAttr(achievementDetail.getOpWorkTime2().get())));
				}
				if (achievementDetail.getOpDepartureTime2().isPresent()) {
					workHoursActual.setEndTimeOp2(Optional.of(new TimeWithDayAttr(achievementDetail.getOpDepartureTime2().get())));
				}
			}
		}
		
		overTimeContent.setWorkTypeCode(workTypeCode);
		overTimeContent.setWorkTimeCode(workTimeCode);
		overTimeContent.setSPRTime(Optional.of(workHoursSPR));
		overTimeContent.setActualTime(Optional.of(workHoursActual));
		return overTimeContent;
	}

	@Override
	public void checkContentApp(String companyId, AppHdWorkDispInfoOutput appHdWorkDispInfo,
			AppHolidayWork appHolidayWork, Boolean mode) {
	    int totalOverTime = 0;
	    if (appHolidayWork.getApplicationTime() != null) {
	        totalOverTime = appHolidayWork.getApplicationTime().getApplicationTime().stream()
	                .map(x -> x.getApplicationTime().v())
	                .mapToInt(Integer::intValue)
	                .sum();
	        totalOverTime += appHolidayWork.getApplicationTime().getOverTimeShiftNight().isPresent() 
	                && appHolidayWork.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight() != null ? 
	                appHolidayWork.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight().v() : 0;
	                totalOverTime += appHolidayWork.getApplicationTime().getFlexOverTime().map(AttendanceTimeOfExistMinus::v).orElse(0);
	    }
        TimeDigestionParam timeDigestionParam = new TimeDigestionParam(
                0, 
                0, 
                0, 
                0, 
                0, 
                totalOverTime, 
                new ArrayList<TimeLeaveApplicationDetailShare>());
		if (mode) { // 新規モード　の場合
			//2-1.新規画面登録前の処理
			processBeforeRegister.processBeforeRegister_New(
					companyId,
					EmploymentRootAtr.APPLICATION,
					false,
					appHolidayWork.getApplication(),
					null,
					appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
					Collections.emptyList(), 
					appHdWorkDispInfo.getAppDispInfoStartupOutput(), 
					Arrays.asList(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
					Optional.ofNullable(timeDigestionParam), 
					appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v), 
					false);
			
		} else { // 詳細・照会モード　の場合
			//4-1.詳細画面登録前の処理
			detailBeforeUpdate.processBeforeDetailScreenRegistration(
					companyId,
					appHolidayWork.getEmployeeID(),
					appHolidayWork.getAppDate().getApplicationDate(),
					EmploymentRootAtr.APPLICATION.value,
					appHolidayWork.getAppID(),
					appHolidayWork.getPrePostAtr(),
					appHolidayWork.getVersion(),
					appHolidayWork.getWorkInformation().getWorkTypeCode().v(),
					appHolidayWork.getWorkInformation().getWorkTimeCode().v(),
					appHdWorkDispInfo.getAppDispInfoStartupOutput(), 
					Arrays.asList(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
                    Optional.ofNullable(timeDigestionParam), 
                    false, 
                    Optional.of(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
                    appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().map(WorkTimeCode::v));
		}
		//	遷移する前のエラーチェック
		this.checkBeforeMoveToAppTime(companyId, appHdWorkDispInfo, appHolidayWork);
	}

	@Override
	public void checkBeforeMoveToAppTime(String companyId, AppHdWorkDispInfoOutput appHdWorkDispInfo,
			AppHolidayWork appHolidayWork) {
		//	勤務種類、就業時間帯チェックのメッセージを表示
		this.checkWorkMessageDisp(appHolidayWork.getWorkInformation().getWorkTypeCode().v(), 
				appHolidayWork.getWorkInformation().getWorkTimeCode().v());
		
		//	事前申請が必須か確認する
		List<PreAppContentDisplay> preAppContentDisplayList = appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput()
				.getOpPreAppContentDisplayLst().orElse(Collections.emptyList());
		appHdWorkDispInfo.getHolidayWorkAppSet().getApplicationDetailSetting().checkAdvanceApp(ApplicationType.HOLIDAY_WORK_APPLICATION, 
				appHolidayWork.getApplication().getPrePostAtr(), Optional.empty(), 
				!preAppContentDisplayList.isEmpty() ? preAppContentDisplayList.get(0).getAppHolidayWork() : Optional.empty());
		
		//	申請の矛盾チェック
		List<EmployeeInfoImport> empList = appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput().getEmployeeInfoLst();
    	commonAlgorithm.appConflictCheck(companyId, !empList.isEmpty() ? empList.get(0) : null, 
    			Arrays.asList(appHolidayWork.getApplication().getAppDate().getApplicationDate()), 
    			Arrays.asList(appHolidayWork.getWorkInformation().getWorkTypeCode().v()), 
    			appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()));
    	
//    	//    	社員に対応する締め期間を取得する
//		val requireM3 = requireService.createRequire();
//        val cacheCarrier = new CacheCarrier();
//        DatePeriod closingPeriod = ClosureService.findClosurePeriod(requireM3, cacheCarrier, appHolidayWork.getApplication().getEmployeeID(), 
//        		appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getBaseDate());
//        
//        //	登録時の残数チェック 
//        
//        List<AppRemainCreateInfor> appDatas = new ArrayList<AppRemainCreateInfor>();
//        
//        List<GeneralDate> lstAppDate = new ArrayList<GeneralDate>();
//        lstAppDate.add(appHolidayWork.getAppDate().getApplicationDate());
//        ApplicationTime applicationTime = appHolidayWork.getApplicationTime();
//        Integer breakTimeTotal = 
//        		applicationTime.getApplicationTime()
//							   .stream()
//							   .filter(x -> x.getAttendanceType() == AttendanceType_Update.BREAKTIME)
//							   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
//							   .sum()
//        		+ applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getMidNightOutSide())).map(x -> x.v()).orElse(0)
//        		+ applicationTime.getOverTimeShiftNight().map(x -> x.getMidNightHolidayTimes()
//        															.stream()
//        															.mapToInt(y -> y.getAttendanceTime().v())
//        															.sum()
//        				).orElse(0);
//        									   
//        Integer overTimeTotal = 
//        		applicationTime.getApplicationTime()
//				   .stream()
//				   .filter(x -> x.getAttendanceType() != AttendanceType_Update.BREAKTIME)
//				   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
//				   .sum()
//			   + applicationTime.getFlexOverTime().map(x -> x.v()).orElse(0)
//			   + applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getOverTimeMidNight())).map(x -> x.v()).orElse(0);
//			   
//        
//			   
//        
//        
//        AppRemainCreateInfor appData = AppRemainCreateInfor.builder()
//        		.sid(appHolidayWork.getEmployeeID())
//        		.appId(appHolidayWork.getAppID())
//        		.inputDate(appHolidayWork.getInputDate())
//        		.appDate(appHolidayWork.getAppDate().getApplicationDate())
//        		.prePosAtr(EnumAdaptor.valueOf(appHolidayWork.getPrePostAtr().value, PrePostAtr.class))
//        		.appType(EnumAdaptor.valueOf(appHolidayWork.getAppType().value, nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType.class))
//        		.workTypeCode(Optional.of(appHolidayWork.getWorkInformation().getWorkTimeCode().v()))
//        		.workTimeCode(appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().flatMap(x -> Optional.of(x.v())))
//        		.vacationTimes(Collections.emptyList())
//        		.appBreakTimeTotal(breakTimeTotal > 0 ? Optional.of(breakTimeTotal) : Optional.empty())
//        		.appOvertimeTimeTotal(overTimeTotal > 0 ? Optional.of(overTimeTotal) : Optional.empty())
//        		.startDate(appHolidayWork.getOpAppStartDate().flatMap(x -> Optional.of(x.getApplicationDate())))
//        		.endDate(appHolidayWork.getOpAppEndDate().flatMap(x -> Optional.of(x.getApplicationDate())))
//        		.lstAppDate(lstAppDate)
//        		.timeDigestionUsageInfor(Optional.empty()) 
//        		.build();
//        
//        appDatas.add(appData);
//        
//        //	登録時の残数チェック   
//        InterimRemainCheckInputParam checkRegisterParam =
//        		InterimRemainCheckInputParam.builder()
//        			.cid(companyId)
//        			.sid(appHolidayWork.getApplication().getEmployeeID())
//        			.datePeriod(new DatePeriod(
//        				closingPeriod.start(),
//        				closingPeriod.end().addYears(1).addDays(-1)
//					))
//        			.mode(false)
//        			.baseDate(appHolidayWork.getApplication().getAppDate().getApplicationDate())
//        			.registerDate(new DatePeriod(
//        				appHolidayWork.getApplication()
//        							  .getAppDate()
//        							  .getApplicationDate(),
//        				appHolidayWork.getApplication()
//        							  .getAppDate()
//        							  .getApplicationDate()
//					 ))
//        			.chkRegister(true)
//        			.recordData(Collections.emptyList())
//        			.scheData(Collections.emptyList())
//        			.appData(appDatas)
//        			.chkSubHoliday(true)
//        			.chkPause(false)
//        			.chkAnnual(false)
//        			.chkFundingAnnual(false)
//        			.chkSpecial(false)
//        			.chkPublicHoliday(false)
//        			.chkSuperBreak(false)
//        			.chkChildNursing(false)
//        			.chkLongTermCare(false)
//        			.build();
//        List<ConfirmMsgOutput> confirmMsgOutputs = new ArrayList<ConfirmMsgOutput>();
//        EarchInterimRemainCheck earchInterimRemainCheck = checkRegister.checkRegister(checkRegisterParam);
//        if(earchInterimRemainCheck.isChkSubHoliday()) {
//        	confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1409", Arrays.asList("代休不足区分"))); //missing param
//        }
//        
//        // 36
//        return confirmMsgOutputs;
	}
	
	@Override
	public List<ConfirmMsgOutput> checkAfterMoveToAppTime(boolean require, String companyId, AppHdWorkDispInfoOutput appHdWorkDispInfo,
			AppHolidayWork appHolidayWork) {

			//	休出時間のチェック
			this.checkHdWorkTime(appHolidayWork.getApplicationTime());
			
			//	事前申請・実績超過チェック
			List<ConfirmMsgOutput> confirmMsgOutputs = this.checkExcess(appHdWorkDispInfo, appHolidayWork);
			
			//	申請時の乖離時間をチェックする
			overtimeService.checkDivergenceTime(require, ApplicationType.HOLIDAY_WORK_APPLICATION, 
					Optional.empty(), Optional.of(appHolidayWork), appHdWorkDispInfo.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet());
			
			//	社員に対応する締め期間を取得する
			val requireM3 = requireService.createRequire();
	        val cacheCarrier = new CacheCarrier();
	        DatePeriod closingPeriod = ClosureService.findClosurePeriod(requireM3, cacheCarrier, appHolidayWork.getApplication().getEmployeeID(), 
	        		appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getBaseDate());
	        
	        //	登録時の残数チェック 
	        
	        List<AppRemainCreateInfor> appDatas = new ArrayList<AppRemainCreateInfor>();
	        
	        List<GeneralDate> lstAppDate = new ArrayList<GeneralDate>();
	        lstAppDate.add(appHolidayWork.getAppDate().getApplicationDate());
	        ApplicationTime applicationTime = appHolidayWork.getApplicationTime();
	        Integer breakTimeTotal = 
	        		applicationTime.getApplicationTime()
								   .stream()
								   .filter(x -> x.getAttendanceType() == AttendanceType_Update.BREAKTIME)
								   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
								   .sum()
	        		+ applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getMidNightOutSide())).map(x -> x.v()).orElse(0)
	        		+ applicationTime.getOverTimeShiftNight().map(x -> x.getMidNightHolidayTimes()
	        															.stream()
	        															.mapToInt(y -> y.getAttendanceTime().v())
	        															.sum()
	        				).orElse(0);
	        									   
	        Integer overTimeTotal = 
	        		applicationTime.getApplicationTime()
					   .stream()
					   .filter(x -> x.getAttendanceType() != AttendanceType_Update.BREAKTIME)
					   .mapToInt(x -> Optional.ofNullable(x.getApplicationTime()).map(y -> y.v()).orElse(0))
					   .sum()
				   + applicationTime.getFlexOverTime().map(x -> x.v()).orElse(0)
				   + applicationTime.getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getOverTimeMidNight())).map(x -> x.v()).orElse(0);
				   
	        
				   
	        
	        
	        AppRemainCreateInfor appData = AppRemainCreateInfor.builder()
	        		.sid(appHolidayWork.getEmployeeID())
	        		.appId(appHolidayWork.getAppID())
	        		.inputDate(appHolidayWork.getInputDate())
	        		.appDate(appHolidayWork.getAppDate().getApplicationDate())
	        		.prePosAtr(EnumAdaptor.valueOf(appHolidayWork.getPrePostAtr().value, PrePostAtr.class))
	        		.appType(EnumAdaptor.valueOf(appHolidayWork.getAppType().value, nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType.class))
	        		.workTypeCode(Optional.of(appHolidayWork.getWorkInformation().getWorkTimeCode().v()))
	        		.workTimeCode(appHolidayWork.getWorkInformation().getWorkTimeCodeNotNull().flatMap(x -> Optional.of(x.v())))
	        		.vacationTimes(Collections.emptyList())
	        		.appBreakTimeTotal(breakTimeTotal > 0 ? Optional.of(breakTimeTotal) : Optional.empty())
	        		.appOvertimeTimeTotal(overTimeTotal > 0 ? Optional.of(overTimeTotal) : Optional.empty())
	        		.startDate(appHolidayWork.getOpAppStartDate().flatMap(x -> Optional.of(x.getApplicationDate())))
	        		.endDate(appHolidayWork.getOpAppEndDate().flatMap(x -> Optional.of(x.getApplicationDate())))
	        		.lstAppDate(lstAppDate)
	        		.timeDigestionUsageInfor(Optional.empty()) 
	        		.build();
	        
	        appDatas.add(appData);
	        
	        //	登録時の残数チェック   #118506
//	        InterimRemainCheckInputParam checkRegisterParam =
//	        		InterimRemainCheckInputParam.builder()
//	        			.cid(companyId)
//	        			.sid(appHolidayWork.getApplication().getEmployeeID())
//	        			.datePeriod(new DatePeriod(
//	        				closingPeriod.start(),
//	        				closingPeriod.end().addYears(1).addDays(-1)
//						))
//	        			.mode(false)
//	        			.baseDate(appHolidayWork.getApplication().getAppDate().getApplicationDate())
//	        			.registerDate(new DatePeriod(
//	        				appHolidayWork.getApplication()
//	        							  .getAppDate()
//	        							  .getApplicationDate(),
//	        				appHolidayWork.getApplication()
//	        							  .getAppDate()
//	        							  .getApplicationDate()
//						 ))
//	        			.chkRegister(true)
//	        			.recordData(Collections.emptyList())
//	        			.scheData(Collections.emptyList())
//	        			.appData(appDatas)
//	        			.chkSubHoliday(true)
//	        			.chkPause(false)
//	        			.chkAnnual(false)
//	        			.chkFundingAnnual(false)
//	        			.chkSpecial(false)
//	        			.chkPublicHoliday(false)
//	        			.chkSuperBreak(false)
//	        			.chkChildNursing(false)
//	        			.chkLongTermCare(false)
//	        			.build();
//	        EarchInterimRemainCheck earchInterimRemainCheck = checkRegister.checkRegister(checkRegisterParam);
//	        if(earchInterimRemainCheck.isChkSubHoliday()) {
//	        	confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1409", Arrays.asList("代休不足区分"))); //missing param
//	        }

	        
	        // 18.３６時間の上限チェック(新規登録)_NEW
	        Time36ErrorInforList time36UpperLimitCheckResult = time36UpperLimitCheck.checkRegister(companyId, 
	        		appHolidayWork.getApplication().getEmployeeID(), 
	        		appHdWorkDispInfo.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(), 
	        		appHolidayWork.getApplication(),
	        		Optional.empty(), 
	        		Optional.of(appHolidayWork),
	        		appHdWorkDispInfo.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getExtratimeExcessAtr(), 
	        		appHdWorkDispInfo.getHolidayWorkAppSet().getOvertimeLeaveAppCommonSet().getExtratimeDisplayAtr());
	        if(!time36UpperLimitCheckResult.getTime36AgreementErrorLst().isEmpty()) {
	        	time36UpperLimitCheckResult.getTime36AgreementErrorLst().forEach(error -> {
	            	switch(error.getTime36AgreementErrorAtr()) {
	    	        	case MONTH_ERROR:
	    	        		confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1535", 
	    	        				Arrays.asList(this.convertTime(error.getAgreementTime()), this.convertTime(error.getThreshold()))));
	    	        		break;
	    	        	case YEAR_ERROR:
	    	        		confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1536", 
	    	        				Arrays.asList(this.convertTime(error.getAgreementTime()), this.convertTime(error.getThreshold()))));
	    	        		break;
	    	        	case MAX_MONTH_ERROR:
	    	        		confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1537", 
	    	        				Arrays.asList(this.convertTime(error.getAgreementTime()), this.convertTime(error.getThreshold()))));
	    	        		break;
	    	        	case MAX_YEAR_ERROR:
	    	        		confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_2056", 
	    	        				Arrays.asList(this.convertTime(error.getAgreementTime()), this.convertTime(error.getThreshold()))));
	    	        		break;
	    	        	case MAX_MONTH_AVERAGE_ERROR:
	    	        		confirmMsgOutputs.add(new ConfirmMsgOutput("Msg_1538", 
	    	        				Arrays.asList(
	    	        						this.convertTime(error.getOpYearMonthPeriod().isPresent() ? error.getOpYearMonthPeriod().get().start().v() : null), 
	    	        						this.convertTime(error.getOpYearMonthPeriod().isPresent() ? error.getOpYearMonthPeriod().get().end().v() : null),
	    	        						this.convertTime(error.getAgreementTime()), 
	    	        						this.convertTime(error.getThreshold()))));
	    	        		break;
	    	        		default: break;
	            	}
	            });
	        }
		return confirmMsgOutputs;
	}
	
	private String convertTime(Integer time) {
		if (time == null) {
			return "";
		}
		TimeWithDayAttr timeConvert = new TimeWithDayAttr(time);
		return timeConvert.getInDayTimeWithFormat();
	}
}
