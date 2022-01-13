package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.primitive.PrimitiveValueBase;
import nts.uk.ctx.at.request.dom.adapter.CalculationParams;
import nts.uk.ctx.at.request.dom.adapter.OneDayAttendanceTimeTempCalcAdapter;
import nts.uk.ctx.at.request.dom.application.overtime.*;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.EmploymentRootAtr;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeaveRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect.GetApplicationReflectionResultAdapter;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.CommonOvertimeHoliday;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.PreActualColorCheck;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.before.DetailBeforeUpdate;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.output.User;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.before.NewBeforeRegister;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.CheckBeforeRegisMultiEmpOutput;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.CollectAchievement;
import nts.uk.ctx.at.request.dom.application.common.service.other.PreAppContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.WorkInfoListOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CalculatedFlag;
import nts.uk.ctx.at.request.dom.application.holidayworktime.service.dto.CheckBeforeOutputMulti;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarlyRepository;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.CheckBeforeOutput;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.ICommonAlgorithmOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoBaseDateOutput;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoNoBaseDate;
import nts.uk.ctx.at.request.dom.application.overtime.CommonAlgorithm.InfoWithDateApplication;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImage;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImageRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampRepository;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.algorithm.obtainappreflect.ObtainAppReflectResultProcess;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.PrePostInitAtr;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.appovertime.OvertimeAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdworkapplicationsetting.OverrideSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeLeaveAppCommonSet;
import nts.uk.ctx.at.request.dom.workrecord.dailyrecordprocess.dailycreationwork.BreakTimeZoneSetting;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionParam;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.application.timeleaveapplication.TimeLeaveApplicationDetailShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrame;
import nts.uk.ctx.at.shared.dom.workdayoff.frame.WorkdayoffFrameRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.DeductionTime;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

@Stateless
public class OvertimeServiceImpl implements OvertimeService {
	
	@Inject
	private EmployeeRequestAdapter employeeRequestAdapter;
	
	@Inject
	private CollectAchievement collectAchievement;
	
	
	@Inject 
	private CommonOvertimeHoliday commonOvertimeHoliday;
	
	@Inject
	private WorkdayoffFrameRepository workdayoffFrameRepository;
	
	@Inject
	private ICommonAlgorithmOverTime commonAlgorithmOverTime;
	
	@Inject
	private PreActualColorCheck preActualColorCheck;
	
	@Inject
	private DetailBeforeUpdate detailBeforeUpdate;
	
	@Inject
	private NewBeforeRegister processBeforeRegister;
	
	@Inject
	private CommonAlgorithm commonAlgorithmImpl;
	
	@Inject
	private GoBackDirectlyRepository repoGoBack;
	@Inject
	private BusinessTripRepository repoBusTrip;
	@Inject
	private ArrivedLateLeaveEarlyRepository repoLateLeave;
	@Inject
	private AppStampRepository repoStamp;
	@Inject
	private AppWorkChangeRepository repoWorkChange;
	@Inject
	private AppRecordImageRepository repoRecordImg;
	@Inject
	private ApplicationRepository applicationRepository;
	@Inject
	private GetApplicationReflectionResultAdapter getApplicationReflectionResultAdapter;
	@Inject
	private TimeLeaveApplicationRepository timeLeaveApplicationRepo;
	@Inject
	private ApplyForLeaveRepository applyForLeaveRepository;
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;
	@Inject
	private AbsenceLeaveAppRepository absenceLeaveAppRepository;
	@Inject
	private RecruitmentAppRepository recruitmentAppRepository;
	@Inject
	private OptionalItemApplicationRepository optionalItemApplicationRepository;

	@Inject
	private WorkTimeSettingRepository worktimeSetRepo;

	@Inject
	private OneDayAttendanceTimeTempCalcAdapter attendanceCalcAdapter;

	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSetRepo;
	
	
	@Override
	public int checkOvertimeAtr(String url) {
		if(url == null){
			return OverTimeAtr.ALL.value;
		}
		switch(url){
		case "0":
			return OverTimeAtr.PREOVERTIME.value;
		case "1":
			return OverTimeAtr.REGULAROVERTIME.value;
		case "2":
			return OverTimeAtr.ALL.value;
		default:
			return OverTimeAtr.ALL.value;
			
		}
	}


	@Override
	public DisplayInfoOverTime calculate(
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
			Optional<AchievementDetail> opAchievementDetail) {
		DisplayInfoOverTime output = new DisplayInfoOverTime();
		output.setCalculatedFlag(CalculatedFlag.UNCALCULATED);

		if (overtimeAppAtr == OvertimeAppAtr.MULTIPLE_OVERTIME && dateOp.isPresent()) {
			List<TimeZoneWithWorkNo> workHourList = new ArrayList<>();
			for (int i = 0; i < workContent.getTimeZones().size(); i++) {
				workHourList.add(new TimeZoneWithWorkNo(
						i + 1,
						workContent.getTimeZones().get(i).getStart().v(),
						workContent.getTimeZones().get(i).getEnd().v()
				));
			}
			Pair<List<TimeZoneWithWorkNo>, List<BreakTimeSheet>> result = this.getWorkingHoursAndBreakHours(
					companyId,
					employeeId,
					dateOp.get(),
					overtimeHours,
					overtimeReasons,
					workHourList,
					new WorkInformation(workContent.getWorkTypeCode().orElse(null), workContent.getWorkTimeCode().orElse(null)),
					opAchievementDetail
			);
			workContent.setTimeZones(result.getLeft().stream().map(i -> new TimeZone(i.getTimeZone().getStartTime(), i.getTimeZone().getEndTime())).collect(Collectors.toList()));
			workContent.setBreakTimes(result.getRight());
		}

		// 計算処理
		CaculationOutput caculationOutput = this.getCalculation(
				companyId,
				employeeId,
				dateOp,
				prePostInitAtr,
				overtimeLeaveAppCommonSet,
				advanceApplicationTime,
				achieveApplicationTime,
				workContent,
				overtimeAppSet,
				agent);
		// 取得した「休出枠<List>」を「残業申請の表示情報」にセットする
		if (caculationOutput != null) {
			output.setWorkdayoffFrames(caculationOutput.getWorkdayoffFrames());
			output.setCalculationResultOp(Optional.ofNullable(caculationOutput.getCalculationResult()));			
			output.setCalculatedFlag(caculationOutput.getCalculatedFlag());
		}
		output.setCalculatedBreakTimes(workContent.getBreakTimes());
		output.setCalculatedWorkTimes(workContent.getTimeZones());
		// 残業時間帯の値と背景色をセット
		return output;
	}

	@Override
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
			Boolean agent) {
		CaculationOutput ouput = new CaculationOutput();
		// INPUTをチェックする
		if (	!dateOp.isPresent() 
				|| !workContent.getWorkTypeCode().isPresent()
				|| !workContent.getWorkTimeCode().isPresent() 
				|| CollectionUtil.isEmpty(workContent.getTimeZones())
				|| overtimeAppSet.getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.NOT_USE
				) return null;
		// 06_計算処理
		List<ApplicationTime> applicationTimes = commonOvertimeHoliday.calculator(
				companyId,
				employeeId,
				dateOp.orElse(null),
				workContent.getWorkTypeCode(),
				workContent.getWorkTimeCode(),
				workContent.getTimeZones(),
				workContent.getBreakTimes());
		OverStateOutput overStateOutput = null;
		if (!agent) {
			// 事前申請・実績の時間超過をチェックする
			overStateOutput = overtimeLeaveAppCommonSet.checkPreApplication(
					prePostInitAtr,
					Optional.ofNullable(advanceApplicationTime),
					CollectionUtil.isEmpty(applicationTimes) ? Optional.empty() : Optional.of(applicationTimes.get(0)),
							Optional.ofNullable(achieveApplicationTime));			
		}
		// 【チェック内容】
		// 取得したList「申請時間．type」 = 休出時間　がある場合
		if (!CollectionUtil.isEmpty(applicationTimes)) {
			List<Integer> noList = new ArrayList<Integer>();
			applicationTimes.get(0).getApplicationTime().forEach(item -> {
				// 休出時間をチェックする
				if (item.getAttendanceType() == AttendanceType_Update.BREAKTIME) {
					noList.add(item.getFrameNo().v());
				}
			});			
			List<WorkdayoffFrame> workdayoffFrames = workdayoffFrameRepository.getWorkdayoffFrameBy(
					companyId,
					noList);
			ouput.setWorkdayoffFrames(workdayoffFrames);
		}
		// OUTPUT「計算結果」をセットして取得した「休出枠」と一緒に返す
		CalculationResult calculationResult = new CalculationResult();
		calculationResult.setOverStateOutput(Optional.ofNullable(overStateOutput));
		calculationResult.setApplicationTimes(applicationTimes);
		ouput.setCalculatedFlag(CalculatedFlag.CALCULATED);
		ouput.setCalculationResult(calculationResult);
		
		return ouput;
	}

	@Override
	public DisplayInfoOverTime getInitData(
			String companyId,
			Optional<GeneralDate> dateOp,
			OvertimeAppAtr overtimeAppAtr,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			Boolean agent) {
		GeneralDate baseDate = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate();
		// get employeeId
		String employeeId = appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid();
		DisplayInfoOverTime output = new DisplayInfoOverTime();
		// 基準日に関係ない情報を取得する 
		InfoNoBaseDate infoNoBaseDate= commonAlgorithmOverTime.getInfoNoBaseDate(
				companyId,
				employeeId,
				overtimeAppAtr);
		// 基準日に関する情報を取得する
		InfoBaseDateOutput infoBaseDateOutput = commonAlgorithmOverTime.getInfoBaseDate(
				companyId,
				employeeId,
				baseDate,
				overtimeAppAtr,
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(Collections.emptyList()),
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet(),
				infoNoBaseDate.getOverTimeAppSet());
		if (!CollectionUtil.isEmpty(infoBaseDateOutput.getWorktypes())) {
			// 申請日に関する情報を取得する
			InfoWithDateApplication infoWithDateApplication = commonAlgorithmOverTime.getInfoAppDate(
					companyId,
					dateOp,
					startTimeSPR,
					endTimeSPR,
					infoBaseDateOutput.getWorktypes(),
					appDispInfoStartupOutput,
					infoNoBaseDate.getOverTimeAppSet());
			
			output.setInfoWithDateApplicationOp(Optional.of(infoWithDateApplication));
		}
		// 取得した情報をOUTPUT「勤務変更申請の表示情報」にセットしてを返す
		output.setAppDispInfoStartup(appDispInfoStartupOutput);
		output.setInfoBaseDateOutput(infoBaseDateOutput);
		output.setInfoNoBaseDate(infoNoBaseDate);
		output.setCalculatedFlag(CalculatedFlag.UNCALCULATED);
		output.setOvertimeAppAtr(overtimeAppAtr);
		// 勤務種類リストと就業時間帯リストがない場合エラーを返す UI
		return output;
	}



	@Override
	public SelectWorkOutput selectWork(
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			WorkTypeCode workTypeCode,
			WorkTimeCode workTimeCode,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			Optional<ActualContentDisplay> actualContentDisplay,
			OvertimeAppSet overtimeAppSet) {
		SelectWorkOutput output = new SelectWorkOutput();
		OverTimeContent overTimeContent = new OverTimeContent();
		Optional<WorkTypeCode> workTypeCodeOp = Optional.ofNullable(workTypeCode);
		Optional<WorkTimeCode> workTimeCodeOp = Optional.ofNullable(workTimeCode);
		WorkHours workHoursSPR = new WorkHours();
		if (startTimeSPR.isPresent()) {
			workHoursSPR.setStartTimeOp1(startTimeSPR.flatMap(x -> Optional.of(new TimeWithDayAttr(x))));
		}
		if (endTimeSPR.isPresent()) {
			workHoursSPR.setEndTimeOp1(endTimeSPR.flatMap(x -> Optional.of(new TimeWithDayAttr(x))));
		}
		WorkHours workHoursActual = new WorkHours();
		if (actualContentDisplay.isPresent()) {
			
			if (actualContentDisplay.get().getOpAchievementDetail().isPresent()) {
				AchievementDetail achievementDetail = actualContentDisplay.get().getOpAchievementDetail().get();
				if (achievementDetail.getOpWorkTime().isPresent()) {
					workHoursActual.setStartTimeOp1(achievementDetail.getOpWorkTime().flatMap(x -> Optional.of(new TimeWithDayAttr(x))));
				}
				if (achievementDetail.getOpLeaveTime().isPresent()) {
					workHoursActual.setEndTimeOp1(achievementDetail.getOpLeaveTime().flatMap(x -> Optional.of(new TimeWithDayAttr(x))));
				}
				if (achievementDetail.getOpWorkTime2().isPresent()) {
					workHoursActual.setStartTimeOp2(achievementDetail.getOpWorkTime2().flatMap(x -> Optional.of(new TimeWithDayAttr(x))));
				}
				if (achievementDetail.getOpDepartureTime2().isPresent()) {
					workHoursActual.setEndTimeOp2(achievementDetail.getOpDepartureTime2().flatMap(x -> Optional.of(new TimeWithDayAttr(x))));
				}
				
			}
		}
		overTimeContent.setWorkTypeCode(workTypeCodeOp);
		overTimeContent.setWorkTimeCode(workTimeCodeOp);
		overTimeContent.setSPRTime(Optional.of(workHoursSPR));
		overTimeContent.setActualTime(Optional.of(workHoursActual));
		// 初期表示する出退勤時刻を取得する
		Optional<WorkHours> workHoursOp = commonAlgorithmOverTime.initAttendanceTime(
				companyId,
				dateOp,
				overTimeContent,
				overtimeAppSet.getApplicationDetailSetting());
		
		// 休憩時間帯を取得する
		BreakTimeZoneSetting breakTimes = commonAlgorithmOverTime.selectWorkTypeAndTime(
				companyId,
				workTypeCode,
				workTimeCode,
				workHoursOp.flatMap(x -> x.getStartTimeOp1()),
				workHoursOp.flatMap(x -> x.getEndTimeOp1()),
				actualContentDisplay.map(x -> x.getOpAchievementDetail()).orElse(Optional.empty()));
		// 07-02_実績取得・状態チェック
		ApplicationTime applicationTime = preActualColorCheck.checkStatus(
				companyId,
				employeeId, // QA 112492
				dateOp.orElse(null),
				ApplicationType.OVER_TIME_APPLICATION,
				workTypeCode,
				workTimeCode,
				overtimeAppSet.getOvertimeLeaveAppCommonSet().getOverrideSet(),
				Optional.empty(),
				breakTimes.getTimeZones(),
				actualContentDisplay);
		output.setWorkHours(workHoursOp);
		output.setBreakTimeZoneSetting(breakTimes);
		output.setApplicationTime(applicationTime);
		return output;
	}

	// pending
	@Override
	public void checkDivergenceTime(
			Boolean require,
			ApplicationType appType,
			Optional<AppOverTime> appOverOptional,
			Optional<AppHolidayWork> appHolidayOptional,
			OvertimeLeaveAppCommonSet overtimeLeaveAppCommonSet) {
		// 「@登録時の乖離時間チェック」をチェックする
		if (overtimeLeaveAppCommonSet.getCheckDeviationRegister() == nts.uk.shr.com.enumcommon.NotUseAtr.NOT_USE) return;
		// 日別実績への申請反映結果を取得 not done
		String companyID = AppContexts.user().companyId();
		String employeeID = "";
		GeneralDate baseDate = null; 
		Optional<Application> application = Optional.empty();
		if(appType==ApplicationType.OVER_TIME_APPLICATION) {
			employeeID = appOverOptional.get().getEmployeeID();
			baseDate = appOverOptional.get().getAppDate().getApplicationDate();
			application = Optional.of((Application) appOverOptional.get());
		} else {
			employeeID = appHolidayOptional.get().getEmployeeID();
			baseDate = appHolidayOptional.get().getAppDate().getApplicationDate();
			application = Optional.of((Application) appHolidayOptional.get());
		}
		Optional<IntegrationOfDaily> opIntegrationOfDaily = ObtainAppReflectResultProcess.process(
				this.createObtainAppReflectResultProcessRequire(), companyID, employeeID, baseDate, application);
		// 取得した「日別勤怠(Work)」から「日別勤怠(Work)．エラー一覧」を取得する
		Optional<List<ReasonDivergence>> reasonDissociation = Optional.empty();
		if(appType==ApplicationType.OVER_TIME_APPLICATION) {
			if(appOverOptional.isPresent()) {
				if(appOverOptional.get().getApplicationTime().getReasonDissociation().isPresent()) {
					reasonDissociation = appOverOptional.get().getApplicationTime().getReasonDissociation();
				}
			}
		} else {
			if(appHolidayOptional.isPresent()) {
				if(appHolidayOptional.get().getApplicationTime().getReasonDissociation().isPresent()) {
					reasonDissociation = appHolidayOptional.get().getApplicationTime().getReasonDissociation();
				}
			}
		}
		String errorMessage = "";
		if(opIntegrationOfDaily.isPresent()) {
			// エラーメッセージを表示する
			if(opIntegrationOfDaily.isPresent()) {
				List<EmployeeDailyPerError> employeeDailyPerErrorLst = new ArrayList<>();
				if(appType==ApplicationType.HOLIDAY_WORK_APPLICATION) {
					employeeDailyPerErrorLst = opIntegrationOfDaily.get().getEmployeeError().stream()
						.filter(x -> x.getErrorAlarmWorkRecordCode().equals(new ErrorAlarmWorkRecordCode("D005")))
						.collect(Collectors.toList());
				}
				if(appType==ApplicationType.OVER_TIME_APPLICATION) {
					if(appOverOptional.isPresent()) {
						if(appOverOptional.get().getOverTimeClf()==OvertimeAppAtr.EARLY_OVERTIME) {
							employeeDailyPerErrorLst = opIntegrationOfDaily.get().getEmployeeError().stream()
								.filter(x -> x.getErrorAlarmWorkRecordCode().equals(new ErrorAlarmWorkRecordCode("D001")))
								.collect(Collectors.toList());
						}
					}
				}
				if(appType==ApplicationType.OVER_TIME_APPLICATION) {
					if(appOverOptional.isPresent()) {
						if(appOverOptional.get().getOverTimeClf()==OvertimeAppAtr.NORMAL_OVERTIME) {
							employeeDailyPerErrorLst = opIntegrationOfDaily.get().getEmployeeError().stream()
								.filter(x -> x.getErrorAlarmWorkRecordCode().equals(new ErrorAlarmWorkRecordCode("D003")))
								.collect(Collectors.toList());
						}
					}
				}
				if(appType==ApplicationType.OVER_TIME_APPLICATION) {
					if(appOverOptional.isPresent()) {
						if(appOverOptional.get().getOverTimeClf()==OvertimeAppAtr.EARLY_NORMAL_OVERTIME) {
							employeeDailyPerErrorLst = opIntegrationOfDaily.get().getEmployeeError().stream()
								.filter(x -> x.getErrorAlarmWorkRecordCode().equals(new ErrorAlarmWorkRecordCode("D001")) 
										|| x.getErrorAlarmWorkRecordCode().equals(new ErrorAlarmWorkRecordCode("D003")))
								.collect(Collectors.toList());
						}
					}
				}
				if(!CollectionUtil.isEmpty(employeeDailyPerErrorLst)) {
					if(employeeDailyPerErrorLst.get(0).getErrorAlarmMessage().isPresent()) {
						errorMessage = employeeDailyPerErrorLst.get(0).getErrorAlarmMessage().get().v();
					}
				}
			}
			if(Strings.isNotBlank(errorMessage)) {
				throw new BusinessException(new RawErrorMessage(errorMessage));
			}
		}
//		if(Strings.isBlank(errorMessage) && reasonDissociation.isPresent()) {
//			// エラーメッセージ（Msg_1298）を表示する
//			throw new BusinessException("Msg_1298");
//		}
	}
	
	private ObtainAppReflectResultProcess.Require createObtainAppReflectResultProcessRequire() {
		return new ObtainAppReflectResultProcess.Require() {
			
			@Override
			public Optional<GoBackDirectly> findGoBack(String companyId, String appID, Application app) {
				return repoGoBack.find(companyId, appID,app);
			}
			
			@Override
			public Optional<BusinessTrip> findBusinessTripApp(String companyId, String appID, Application app) {
				return repoBusTrip.findByAppId(companyId, appID,app);
			}
			
			@Override
			public Optional<ArrivedLateLeaveEarly> findArrivedLateLeaveEarly(String companyId, String appID,
					Application application) {
				ArrivedLateLeaveEarly app = repoLateLeave.getLateEarlyApp(companyId, appID, application);
				return app == null ? Optional.empty() : Optional.of(app);
			}
			
			@Override
			public Optional<AppWorkChange> findAppWorkCg(String companyId, String appID, Application app) {
				return repoWorkChange.findbyID(companyId, appID, app);
			}
			
			@Override
			public Optional<AppStamp> findAppStamp(String companyId, String appID, Application app) {
				return repoStamp.findByAppID(companyId, appID, app);
			}
			
			@Override
			public Optional<IntegrationOfDaily> getAppReflectResult(String cid, ApplicationShare application,
					GeneralDate baseDate, Optional<IntegrationOfDaily> dailyData) {
				return getApplicationReflectionResultAdapter.getApp(cid, application, baseDate, dailyData);
			}
			
			@Override
			public List<Application> getAppForReflect(String sid, GeneralDate dateData, List<Integer> recordStatus,
					List<Integer> scheStatus, List<Integer> appType) {
				DatePeriod period = new DatePeriod(dateData, dateData);
				return applicationRepository.getAppForReflect(sid, period, recordStatus, scheStatus, appType);
			}

			@Override
			public Optional<AppRecordImage> findAppRecordImage(String companyId, String appID, Application app) {
				return repoRecordImg.findByAppID(companyId, appID, app);
			}

			@Override
			public Optional<TimeLeaveApplication> findTimeLeavById(String companyId, String appId) {
				return timeLeaveApplicationRepo.findById(companyId, appId);
			}
			@Override
			public Optional<AppOverTime> findOvertime(String companyId, String appId) {
				return appOverTimeRepository.find(companyId, appId);
			}

			@Override
			public Optional<ApplyForLeave> findApplyForLeave(String CID, String appId) {
				return applyForLeaveRepository.findApplyForLeave(CID, appId);
			}

			@Override
			public Optional<AppHolidayWork> findAppHolidayWork(String companyId, String appId) {
				return appHolidayWorkRepository.find(companyId, appId);
			}
			
			@Override
			public Optional<AbsenceLeaveApp> findAbsenceByID(String applicationID) {
				return absenceLeaveAppRepository.findByAppId(applicationID);
			}

			@Override
			public Optional<RecruitmentApp> findRecruitmentByID(String applicationID) {
				return recruitmentAppRepository.findByAppId(applicationID);
			}

			@Override
			public Optional<OptionalItemApplication> getOptionalByAppId(String companyId, String appId) {
				return optionalItemApplicationRepository.getByAppId(companyId, appId);
			}
		};
	}

	@Override
	public CheckBeforeOutput checkErrorRegister(
			Boolean require,
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime) {
		CheckBeforeOutput output = new CheckBeforeOutput();
		// 2-1.新規画面登録前の処理
		int totalOverTime = 0;
		totalOverTime = appOverTime.getApplicationTime().getApplicationTime().stream()
		        .map(x -> x.getApplicationTime().v())
		        .mapToInt(Integer::intValue)
		        .sum();
		totalOverTime +=
		        appOverTime.getApplicationTime().getOverTimeShiftNight().flatMap(x -> Optional.ofNullable(x.getOverTimeMidNight())).map(x -> x.v()).orElse(0);
		totalOverTime += appOverTime.getApplicationTime().getFlexOverTime().map(AttendanceTimeOfExistMinus::v).orElse(0);
		TimeDigestionParam timeDigestionParam = new TimeDigestionParam(
		        0, 
		        0, 
		        0, 
		        0, 
		        0, 
		        totalOverTime, 
		        new ArrayList<TimeLeaveApplicationDetailShare>());
		List<ConfirmMsgOutput> confirmMsgOutputs = processBeforeRegister.processBeforeRegister_New(
				companyId,
				EmploymentRootAtr.APPLICATION, // QA 112515 done
				false,
				appOverTime.getApplication(),
				appOverTime.getOverTimeClf(),
				displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
				Collections.emptyList(), 
				displayInfoOverTime.getAppDispInfoStartup(), 
				appOverTime.getWorkInfoOp().isPresent() ? Arrays.asList(appOverTime.getWorkInfoOp().get().getWorkTypeCode().v()) : new ArrayList<String>(), 
				Optional.of(timeDigestionParam), 
				appOverTime.getWorkInfoOp().isPresent() ? appOverTime.getWorkInfoOp().get().getWorkTimeCodeNotNull().map(WorkTimeCode::v) : Optional.empty(), 
				false);
		// 残業申請の個別登録前チェッ処理
		output = commonAlgorithmOverTime.checkBeforeOverTime(
				require,
				companyId,
				appOverTime,
				displayInfoOverTime,
				1); // QA input 112515 done
		// 取得した「確認メッセージリスト」と「残業申請」を返す
		output.getConfirmMsgOutputs().addAll(confirmMsgOutputs);
		
		return output;
	}
	@Inject
	private AppOverTimeRepository appOverTimeRepository;
	@Override
	public DetailOutput getDetailData(
			String companyId,
			String appId,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		DetailOutput output = new DetailOutput();
		DisplayInfoOverTime displayInfoOverTime = new DisplayInfoOverTime();
		// 申請日に関係する情報
		Optional<InfoWithDateApplication> infoOptional = Optional.empty();
		// ドメインモデル「残業申請」を取得する
		Optional<AppOverTime> appOverTimeOp = appOverTimeRepository.find(companyId, appId);
		if (!appOverTimeOp.isPresent()) return null;
		AppOverTime appOverTime = appOverTimeOp.get();
		Application application = appDispInfoStartupOutput.getAppDetailScreenInfo().map(x -> x.getApplication()).orElse(null);
		appOverTime.setApplication(application);
		// 基準日に関係ない情報を取得する
		InfoNoBaseDate infoNoBaseDate = commonAlgorithmOverTime.getInfoNoBaseDate(
				companyId,
				appOverTime.getEmployeeID(),
				appOverTime.getOverTimeClf());
		// 基準日に関する情報を取得する
		InfoBaseDateOutput infoBaseDateOutput = commonAlgorithmOverTime.getInfoBaseDate(
				companyId,
				appOverTime.getEmployeeID(),
				appOverTime.getAppDate().getApplicationDate(),
				appOverTime.getOverTimeClf(),
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(Collections.emptyList()),
				appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpEmploymentSet(),
				infoNoBaseDate.getOverTimeAppSet());
		// 取得した「残業申請」をチェックする
		if (appOverTime.getPrePostAtr() == PrePostAtr.POSTERIOR) {
			// 07-02_実績取得・状態チェック
			/**
			 *  退勤時刻優先設定 = 
　				INPUT．「申請表示情報．申請詳細画面情報．利用者」 = 申請本人 OR 申請本人&承認者 ⇒ 取得した「基準日に関係しない情報．残業申請設定．残業休出申請共通設定．実績超過打刻優先設定」
　				INPUT．「申請表示情報．申請詳細画面情報．利用者」 <> 承認者 OR その他 ⇒ 退勤時刻優先

			 */
			OverrideSet overrideSet = OverrideSet.SYSTEM_TIME_PRIORITY;
			if (appDispInfoStartupOutput.getAppDetailScreenInfo().isPresent()) {
				if (appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser() == User.APPLICANT 
						|| appDispInfoStartupOutput.getAppDetailScreenInfo().get().getUser() == User.APPLICANT_APPROVER) {
					overrideSet = infoNoBaseDate.getOverTimeAppSet().getOvertimeLeaveAppCommonSet().getOverrideSet();
				} else {
					overrideSet = OverrideSet.TIME_OUT_PRIORITY;
				}
			}
			ApplicationTime archievement = preActualColorCheck.checkStatus(
					companyId,
					appOverTime.getEmployeeID(),
					appOverTime.getAppDate().getApplicationDate(),
					ApplicationType.OVER_TIME_APPLICATION,
					appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode()).orElse(null),
					appOverTime.getWorkInfoOp().map(x -> x.getWorkTimeCode()).orElse(null),
					overrideSet, // 退勤時刻優先設定
					Optional.empty(),
					appOverTime.getBreakTimeOp()
							   .orElse(Collections.emptyList())
							   .stream()
							   .map(x -> new DeductionTime(x.getTimeZone().getStartTime(), x.getTimeZone().getEndTime()))
							   .collect(Collectors.toList()),
					appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().map(x -> x.get(0)));
			
			// 「申請日に関係する情報」を作成する #113175
			InfoWithDateApplication infoWithDateApplication = new InfoWithDateApplication();
			infoWithDateApplication.setApplicationTime(Optional.ofNullable(archievement));
			infoOptional = Optional.of(infoWithDateApplication);
		}
		// 取得した「残業申請」をチェックする
		Optional<OvertimeApplicationSetting> resultOp = appOverTime.getApplicationTime().getApplicationTime().stream().filter(x -> x.getAttendanceType() == AttendanceType_Update.BREAKTIME).findFirst();
		if (resultOp.isPresent()) {
			// ドメインモデル「休出枠」を取得する
			List<Integer> noList = new ArrayList<Integer>();
			appOverTime.getApplicationTime().getApplicationTime().stream().forEach(item -> {
				// 休出時間をチェックする
				if (item.getAttendanceType() == AttendanceType_Update.BREAKTIME) {
					noList.add(item.getFrameNo().v());
				}
			});			
			List<WorkdayoffFrame> workdayoffFrames = workdayoffFrameRepository.getWorkdayoffFrameBy(
					companyId,
					noList);
			displayInfoOverTime.setWorkdayoffFrames(workdayoffFrames);
		}
		// 事前申請・実績の時間超過をチェックする
		OverStateOutput overStateOutput = infoNoBaseDate.getOverTimeAppSet()
				.getOvertimeLeaveAppCommonSet()
				.checkPreApplication(
					EnumAdaptor.valueOf(appOverTime.getPrePostAtr().value, PrePostInitAtr.class),
					appDispInfoStartupOutput
							.getAppDispInfoWithDateOutput()
							.getOpPreAppContentDisplayLst()
							.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0)))
							.flatMap(y -> y.getApOptional())
							.flatMap(z -> Optional.of(z.getApplicationTime())),
					Optional.of(appOverTime.getApplicationTime()),
					infoOptional.flatMap(x -> x.getApplicationTime()));
		
		Optional<WorkInfoListOutput> workInfo = Optional.empty();
		if (infoNoBaseDate.getOverTimeAppSet().getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.USE) { // 取得した「基準日に関係しない情報．残業申請設定．残業休出申請共通設定．時刻計算利用区分」をチェックする
			// 申請中の勤務情報を取得する
			WorkInfoListOutput workInfoOp = commonAlgorithmImpl.getWorkInfoList(
					companyId,
					appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()).orElse(null),
					appOverTime.getWorkInfoOp().flatMap(x -> x.getWorkTimeCodeNotNull()).flatMap(x -> Optional.ofNullable(x.v())),
					infoBaseDateOutput.getWorktypes(),
					appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().orElse(Collections.emptyList()));
			workInfo = Optional.ofNullable(workInfoOp);
		}
		
		if (workInfo.isPresent()) {
			infoBaseDateOutput.setWorktypes(workInfo.get().getWorkTypes());
			appDispInfoStartupOutput.getAppDispInfoWithDateOutput().setOpWorkTimeLst(Optional.ofNullable(workInfo.get().getWorkTimes()));
			displayInfoOverTime.setWorkInfo(
					Optional.of(new WorkInfo(appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()).orElse(null),
							appOverTime.getWorkInfoOp().flatMap(x -> x.getWorkTimeCodeNotNull()).map(x -> x.v()).orElse(null))));
		}
		
		// OUTPUT「残業申請の表示情報」をセットして取得した「残業申請」と一緒に返す
		displayInfoOverTime.setAppDispInfoStartup(appDispInfoStartupOutput);
		displayInfoOverTime.setInfoNoBaseDate(infoNoBaseDate);
		displayInfoOverTime.setInfoBaseDateOutput(infoBaseDateOutput);
		displayInfoOverTime.setInfoWithDateApplicationOp(infoOptional);
		displayInfoOverTime.setOvertimeAppAtr(appOverTime.getOverTimeClf());
		displayInfoOverTime.setCalculatedFlag(CalculatedFlag.CALCULATED);
		CalculationResult calculationResult = new CalculationResult();
		calculationResult.setOverStateOutput(Optional.ofNullable(overStateOutput));
		displayInfoOverTime.setCalculationResultOp(Optional.of(calculationResult));
		output.setDisplayInfoOverTime(displayInfoOverTime);
		output.setAppOverTime(appOverTime);
		return output;
	}

	@Override
	public DetailOutput initDetailScreen(String companyId, String appId,
			AppDispInfoStartupOutput appDispInfoStartupOutput) {
		// 詳細画面起動前申請共通設定を取得する (get info from UI )
		// 詳細画面起動の処理
		DetailOutput output = this.getDetailData(companyId, appId, appDispInfoStartupOutput);
		return output;
	}

	@Override
	public CheckBeforeOutput checkBeforeUpdate(
			Boolean require,
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime) {
		CheckBeforeOutput output = new CheckBeforeOutput();
		
		String workTypeCode = appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null);
		String workTimeCode = appOverTime.getWorkInfoOp().flatMap(x -> x.getWorkTimeCodeNotNull()).map(x -> x.v()).orElse(null);
		
		if (displayInfoOverTime.getWorkInfo().isPresent()) {
			workTypeCode = !workTypeCode.equals(displayInfoOverTime.getWorkInfo().get().getWorkType()) ? workTypeCode : null;
			workTimeCode = !workTimeCode.equals(displayInfoOverTime.getWorkInfo().get().getWorkTime()) ? workTimeCode : null;			
		}
		
		// 4-1.詳細画面登録前の処理
		int totalOverTime = 0;
        totalOverTime = appOverTime.getApplicationTime().getApplicationTime().stream()
                .map(x -> x.getApplicationTime().v())
                .mapToInt(Integer::intValue)
                .sum();
        totalOverTime += appOverTime.getApplicationTime().getOverTimeShiftNight().isPresent() 
                && appOverTime.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight() != null ? 
                appOverTime.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight().v() : 0;
        totalOverTime += appOverTime.getApplicationTime().getFlexOverTime().map(AttendanceTimeOfExistMinus::v).orElse(0);
        TimeDigestionParam timeDigestionParam = new TimeDigestionParam(
                0, 
                0, 
                0, 
                0, 
                0, 
                totalOverTime, 
                new ArrayList<TimeLeaveApplicationDetailShare>());
		detailBeforeUpdate.processBeforeDetailScreenRegistration(
				companyId,
				appOverTime.getEmployeeID(),
				appOverTime.getAppDate().getApplicationDate(),
				EmploymentRootAtr.APPLICATION.value,
				appOverTime.getAppID(),
				appOverTime.getPrePostAtr(),
				appOverTime.getVersion(),
				workTypeCode,
				workTimeCode,
				displayInfoOverTime.getAppDispInfoStartup(), 
				new ArrayList<String>(), 
				Optional.of(timeDigestionParam), 
				false,
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()), 
				appOverTime.getWorkInfoOp().isPresent() ? appOverTime.getWorkInfoOp().get().getWorkTimeCodeNotNull().map(WorkTimeCode::v) : Optional.empty());
		// 残業申請の個別登録前チェッ処理
		output = commonAlgorithmOverTime.checkBeforeOverTime(
				require,
				companyId,
				appOverTime,
				displayInfoOverTime,
				1); // 詳細・照会モード
		// 取得した「確認メッセージリスト」と「残業申請」を返す
		return output;
	}

	@Override
	public DisplayInfoOverTime startA( // output is not needed to add error list parameter, because just check error msg on UI // 114330
			String companyId,
			String employeeId,
			Optional<GeneralDate> dateOp,
			OvertimeAppAtr overtimeAppAtr,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			Optional<Integer> startTimeSPR,
			Optional<Integer> endTimeSPR,
			Boolean agent
			) {
		DisplayInfoOverTime output;
		// 15_初期起動の処理
		output = this.getInitData(
				companyId,
				dateOp,
				overtimeAppAtr,
				appDispInfoStartupOutput,
				startTimeSPR,
				endTimeSPR,
				agent);

		if (output.getInfoNoBaseDate().getOverTimeAppSet().getApplicationDetailSetting().getTimeCalUse() == NotUseAtr.NOT_USE && overtimeAppAtr == OvertimeAppAtr.MULTIPLE_OVERTIME)
			throw new BusinessException("Msg_3237");

		Integer prePost = output.getAppDispInfoStartup()
			.getAppDispInfoWithDateOutput()
			.getPrePostAtr().value;

		if (overtimeAppAtr == OvertimeAppAtr.MULTIPLE_OVERTIME && dateOp.isPresent()) {
            output.setLatestMultipleOvertimeApp(appOverTimeRepository.findLatestMultipleOvertimeApp(employeeId, dateOp.get(), EnumAdaptor.valueOf(prePost, PrePostAtr.class)));
		}

		WorkContent workContent = new WorkContent();
		if (output.getLatestMultipleOvertimeApp().isPresent()) {
			if (output.getLatestMultipleOvertimeApp().get().getWorkInfoOp().isPresent()) {
				workContent.setWorkTypeCode(Optional.of(output.getLatestMultipleOvertimeApp().get().getWorkInfoOp().get().getWorkTypeCode().v()));
				workContent.setWorkTimeCode(output.getLatestMultipleOvertimeApp().get().getWorkInfoOp().get().getWorkTimeCodeNotNull().map(PrimitiveValueBase::v));
			}
			if (output.getLatestMultipleOvertimeApp().get().getWorkHoursOp().isPresent()) {
				workContent.setTimeZones(output.getLatestMultipleOvertimeApp().get().getWorkHoursOp().get()
						.stream().map(i -> new TimeZone(
								i.getTimeZone().getStartTime(),
								i.getTimeZone().getEndTime()
						)).collect(Collectors.toList()));
			}
			if (output.getLatestMultipleOvertimeApp().get().getBreakTimeOp().isPresent()) {
				workContent.setBreakTimes(output.getLatestMultipleOvertimeApp().get().getBreakTimeOp().get()
						.stream().map(i -> new BreakTimeSheet(
								new BreakFrameNo(i.getWorkNo().v()),
								i.getTimeZone().getStartTime(),
								i.getTimeZone().getEndTime()
						)).collect(Collectors.toList()));
			}
		} else {
			workContent.setWorkTypeCode(output.getInfoWithDateApplicationOp()
					.map(x -> x.getWorkTypeCD())
					.orElse(Optional.empty()));
			workContent.setWorkTimeCode(
					output.getInfoWithDateApplicationOp()
							.map(x -> x.getWorkTimeCD())
							.orElse(Optional.empty()));
			List<TimeZone> timeZones = new ArrayList<TimeZone>();
			List<BreakTimeSheet> breakTimes = new ArrayList<BreakTimeSheet>();
			if (output.getInfoWithDateApplicationOp().isPresent()) {
				Optional<WorkHours> workHours = output.getInfoWithDateApplicationOp().get().getWorkHours();
				if (workHours.isPresent()) {
					if (workHours.get().getStartTimeOp1().isPresent() || workHours.get().getEndTimeOp1().isPresent()) {
						TimeZone timeZone = new TimeZone(
								workHours.get().getStartTimeOp1().orElse(null),
								workHours.get().getEndTimeOp1().orElse(null));
						timeZones.add(timeZone);
					}
					if (workHours.get().getStartTimeOp2().isPresent() || workHours.get().getEndTimeOp2().isPresent()) {
						TimeZone timeZone = new TimeZone(
								workHours.get().getStartTimeOp2().orElse(null),
								workHours.get().getEndTimeOp2().orElse(null));
						timeZones.add(timeZone);
					}
				}
				Optional<BreakTimeZoneSetting> breakTime = output.getInfoWithDateApplicationOp().get().getBreakTime();
				if (breakTime.isPresent()) {
					List<DeductionTime> breakTimeZones = breakTime.get().getTimeZones();
					breakTimes = IntStream.range(1, (int) breakTimeZones.stream().count())
							.mapToObj(i -> new BreakTimeSheet(
									new BreakFrameNo(i),
									breakTimeZones.get(i).getStart(),
									breakTimeZones.get(i).getEnd()))
							.collect(Collectors.toList());
				}
			}
			workContent.setTimeZones(timeZones);
			workContent.setBreakTimes(breakTimes);
		}
		if (!(CollectionUtil.isEmpty(output.getInfoBaseDateOutput().getWorktypes())
			|| !output.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpWorkTimeLst().isPresent())) {
			Optional<AchievementDetail> opAchievementDetail = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent()
					? appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail()
					: Optional.empty();
			// 計算を実行する
			DisplayInfoOverTime temp = this.calculate(
					companyId,
					output.getAppDispInfoStartup().getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(),
					dateOp,
					overtimeAppAtr,
					EnumAdaptor.valueOf(prePost, PrePostInitAtr.class),
					output.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet(),
					output.getAppDispInfoStartup()
					.getAppDispInfoWithDateOutput()
					.getOpPreAppContentDisplayLst()
					.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0)))
					.flatMap(y -> y.getApOptional())
					.map(z -> z.getApplicationTime())
					.orElse(null),
					output.getInfoWithDateApplicationOp()
					.map(x -> x.getApplicationTime().orElse(null))
					.orElse(null),
					workContent,
					output.getInfoNoBaseDate().getOverTimeAppSet(),
					agent,
					output.getLatestMultipleOvertimeApp().isPresent() && output.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().isPresent() ? output.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().get().getOvertimeHours() : new ArrayList<>(),
					output.getLatestMultipleOvertimeApp().isPresent() && output.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().isPresent() ? output.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().get().getOvertimeReasons() : new ArrayList<>(),
					opAchievementDetail
			);
			output.setWorkdayoffFrames(temp.getWorkdayoffFrames());
			output.setCalculationResultOp(temp.getCalculationResultOp());
			output.setCalculatedFlag(temp.getCalculatedFlag());
			output.setCalculatedBreakTimes(temp.getCalculatedBreakTimes());
			output.setCalculatedWorkTimes(temp.getCalculatedWorkTimes());
		} else {
			output.setCalculatedFlag(CalculatedFlag.UNCALCULATED);
		}
		
		return output;
	}

	@Override
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
			) {
		// 「残業申請の表示情報」を更新する
		displayInfoOverTime.setAppDispInfoStartup(appDispInfoStartupOutput);
		// 申請日変更時処理
		commonAlgorithmOverTime.changeApplicationDate(companyId, dateOp.orElse(null), displayInfoOverTime);

		if (overtimeAppAtr == OvertimeAppAtr.MULTIPLE_OVERTIME && dateOp.isPresent()) {
			Optional<AppOverTime> app = appOverTimeRepository.findLatestMultipleOvertimeApp(employeeId, dateOp.get(), EnumAdaptor.valueOf(prePost.value, PrePostAtr.class));
            displayInfoOverTime.setLatestMultipleOvertimeApp(app);
		}

		WorkContent workContent = new WorkContent();
		if (displayInfoOverTime.getLatestMultipleOvertimeApp().isPresent()) {
			if (displayInfoOverTime.getLatestMultipleOvertimeApp().get().getWorkInfoOp().isPresent()) {
				workContent.setWorkTypeCode(Optional.of(displayInfoOverTime.getLatestMultipleOvertimeApp().get().getWorkInfoOp().get().getWorkTypeCode().v()));
				workContent.setWorkTimeCode(displayInfoOverTime.getLatestMultipleOvertimeApp().get().getWorkInfoOp().get().getWorkTimeCodeNotNull().map(PrimitiveValueBase::v));
			}
			if (displayInfoOverTime.getLatestMultipleOvertimeApp().get().getWorkHoursOp().isPresent()) {
				workContent.setTimeZones(displayInfoOverTime.getLatestMultipleOvertimeApp().get().getWorkHoursOp().get()
						.stream().map(i -> new TimeZone(
								i.getTimeZone().getStartTime(),
								i.getTimeZone().getEndTime()
						)).collect(Collectors.toList()));
			}
			if (displayInfoOverTime.getLatestMultipleOvertimeApp().get().getBreakTimeOp().isPresent()) {
				workContent.setBreakTimes(displayInfoOverTime.getLatestMultipleOvertimeApp().get().getBreakTimeOp().get()
						.stream().map(i -> new BreakTimeSheet(
								new BreakFrameNo(i.getWorkNo().v()),
								i.getTimeZone().getStartTime(),
								i.getTimeZone().getEndTime()
						)).collect(Collectors.toList()));
			}
		} else {
			workContent.setWorkTypeCode(displayInfoOverTime.getInfoWithDateApplicationOp()
					.map(x -> x.getWorkTypeCD())
					.orElse(Optional.empty()));
			workContent.setWorkTimeCode(
					displayInfoOverTime.getInfoWithDateApplicationOp()
							.map(x -> x.getWorkTimeCD())
							.orElse(Optional.empty()));
			List<TimeZone> timeZones = new ArrayList<TimeZone>();
			List<BreakTimeSheet> breakTimes = new ArrayList<BreakTimeSheet>();
			if (displayInfoOverTime.getInfoWithDateApplicationOp().isPresent()) {
				Optional<WorkHours> workHours = displayInfoOverTime.getInfoWithDateApplicationOp().get().getWorkHours();
				if (workHours.isPresent()) {
					if (workHours.get().getStartTimeOp1().isPresent() || workHours.get().getEndTimeOp1().isPresent()) {
						TimeZone timeZone = new TimeZone(
								workHours.get().getStartTimeOp1().orElse(null),
								workHours.get().getEndTimeOp1().orElse(null));
						timeZones.add(timeZone);
					}
					if (workHours.get().getStartTimeOp2().isPresent() || workHours.get().getEndTimeOp2().isPresent()) {
						TimeZone timeZone = new TimeZone(
								workHours.get().getStartTimeOp2().orElse(null),
								workHours.get().getEndTimeOp2().orElse(null));
						timeZones.add(timeZone);
					}
				}
				Optional<BreakTimeZoneSetting> breakTime = displayInfoOverTime.getInfoWithDateApplicationOp().get().getBreakTime();
				if (breakTime.isPresent()) {
					List<DeductionTime> breakTimeZones = breakTime.get().getTimeZones();
					if(!CollectionUtil.isEmpty(breakTimeZones)) {
						breakTimes = IntStream.range(0, (int) breakTimeZones.stream().count())
								.mapToObj(i -> new BreakTimeSheet(
										new BreakFrameNo(i+1),
										breakTimeZones.get(i).getStart(),
										breakTimeZones.get(i).getEnd()))
								.collect(Collectors.toList());
					}
				}
			}
			workContent.setTimeZones(timeZones);
			workContent.setBreakTimes(breakTimes);
		}
		Optional<AchievementDetail> opAchievementDetail = displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent()
				? displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail()
				: Optional.empty();
		// 計算を実行する
		DisplayInfoOverTime displayInfoOverTimeTemp = this.calculate(
				companyId,
				employeeId,
				dateOp,
				overtimeAppAtr,
				prePost,
				overtimeAppSet.getOvertimeLeaveAppCommonSet(),
				displayInfoOverTime.getAppDispInfoStartup()
					.getAppDispInfoWithDateOutput()
					.getOpPreAppContentDisplayLst()
					.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0)))
					.flatMap(y -> y.getApOptional())
					.map(z -> z.getApplicationTime())
					.orElse(null),
				displayInfoOverTime.getInfoWithDateApplicationOp()
					.map(x -> x.getApplicationTime().orElse(null))
					.orElse(null),
				workContent,
				overtimeAppSet,
				agent,
				displayInfoOverTime.getLatestMultipleOvertimeApp().isPresent() && displayInfoOverTime.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().isPresent() ? displayInfoOverTime.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().get().getOvertimeHours() : new ArrayList<>(),
				displayInfoOverTime.getLatestMultipleOvertimeApp().isPresent() && displayInfoOverTime.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().isPresent() ? displayInfoOverTime.getLatestMultipleOvertimeApp().get().getMultipleTimesOp().get().getOvertimeReasons() : new ArrayList<>(),
				opAchievementDetail
		);
		displayInfoOverTime.setCalculationResultOp(displayInfoOverTimeTemp.getCalculationResultOp());
		displayInfoOverTime.setWorkdayoffFrames(displayInfoOverTimeTemp.getWorkdayoffFrames());
		displayInfoOverTime.setCalculatedFlag(displayInfoOverTimeTemp.getCalculatedFlag());
		displayInfoOverTime.setCalculatedBreakTimes(displayInfoOverTimeTemp.getCalculatedBreakTimes());
		displayInfoOverTime.setCalculatedWorkTimes(displayInfoOverTimeTemp.getCalculatedWorkTimes());
		return displayInfoOverTime;
	}


	@Override
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
			) {
		// 16_勤務種類・就業時間帯を選択する
		SelectWorkOutput selectWorkOutput = this.selectWork(
													companyId,
													employeeId,
													dateOp,
													workTypeCode,
													workTimeCode,
													startTimeSPR,
													endTimeSPR,
													appDispInfoStartupOutput.getAppDispInfoWithDateOutput()
														.getOpActualContentDisplayLst()
														.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0))),
													overtimeAppSet);
		
		
		WorkContent workContent = new WorkContent();
		workContent.setWorkTypeCode(Optional.ofNullable(workTypeCode).flatMap(x -> Optional.of(x.v())));
		workContent.setWorkTimeCode(Optional.ofNullable(workTimeCode).flatMap(x -> Optional.of(x.v())));
		List<TimeZone> timeZones = new ArrayList<TimeZone>();
		List<BreakTimeSheet> breakTimes = new ArrayList<BreakTimeSheet>();
		Optional<WorkHours> workHours = selectWorkOutput.getWorkHours();
		if (workHours.isPresent()) {
			if (workHours.get().getStartTimeOp1().isPresent() || workHours.get().getEndTimeOp1().isPresent()) {
				TimeZone timeZone = new TimeZone(
						workHours.get().getStartTimeOp1().orElse(null),
						workHours.get().getEndTimeOp1().orElse(null));
				timeZones.add(timeZone);
			}
			if (workHours.get().getStartTimeOp2().isPresent() || workHours.get().getEndTimeOp2().isPresent()) {
				TimeZone timeZone = new TimeZone(
						workHours.get().getStartTimeOp2().orElse(null),
						workHours.get().getEndTimeOp2().orElse(null));
				timeZones.add(timeZone);
			}
		}
		Optional<BreakTimeZoneSetting> breakTime = Optional.ofNullable(selectWorkOutput.getBreakTimeZoneSetting());
		if (breakTime.isPresent()) {
			List<DeductionTime> breakTimeZones = breakTime.get().getTimeZones();
			breakTimes = IntStream.range(1, (int) breakTimeZones.stream().count())
						.mapToObj(i -> new BreakTimeSheet(
								new BreakFrameNo(i),
								breakTimeZones.get(i).getStart(),
								breakTimeZones.get(i).getEnd()))
						.collect(Collectors.toList());
		}
		workContent.setTimeZones(timeZones);
		workContent.setBreakTimes(breakTimes);

		Optional<AchievementDetail> opAchievementDetail = appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent()
				? appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail()
				: Optional.empty();
		// 計算を実行する
		DisplayInfoOverTime displayInfoOverTimeTemp = this.calculate(
				companyId,
				employeeId,
				dateOp,
				OvertimeAppAtr.MULTIPLE_OVERTIME,
				prePost,
				overtimeAppSet.getOvertimeLeaveAppCommonSet(),
				appDispInfoStartupOutput
					.getAppDispInfoWithDateOutput()
					.getOpPreAppContentDisplayLst()
					.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0)))
					.flatMap(y -> y.getApOptional())
					.map(z -> z.getApplicationTime())
					.orElse(null),
					selectWorkOutput.getApplicationTime(),
				workContent,
				overtimeAppSet,
				agent,
				new ArrayList<>(),
				new ArrayList<>(),
				opAchievementDetail
		);
		displayInfoOverTimeTemp.setAppDispInfoStartup(appDispInfoStartupOutput);
		InfoWithDateApplication infoWithDateApplication = new InfoWithDateApplication();
		infoWithDateApplication.setApplicationTime(Optional.ofNullable(selectWorkOutput.getApplicationTime()));
		infoWithDateApplication.setWorkHours(workHours);
		infoWithDateApplication.setBreakTime(breakTime);
		displayInfoOverTimeTemp.setInfoWithDateApplicationOp(Optional.of(infoWithDateApplication));
		displayInfoOverTimeTemp.setCalculatedFlag(displayInfoOverTimeTemp.getCalculatedFlag());
		return displayInfoOverTimeTemp;
	}

	@Override
	public DisplayInfoOverTimeMobile startMobile(
			Boolean mode,
			String companyId,
			Optional<String> employeeIdOptional,
			Optional<GeneralDate> dateOptional,
			Optional<DisplayInfoOverTime> disOptional,
			Optional<AppOverTime> appOptional,
			AppDispInfoStartupOutput appDispInfoStartupOutput,
			OvertimeAppAtr overtimeAppAtr,
			Boolean agent
			) {
		DisplayInfoOverTimeMobile output = new DisplayInfoOverTimeMobile();
		DisplayInfoOverTime displayInfoOverTime;
		if (!mode) { // 修正モード
			// INPUT「残業申請の表示情報」と「残業申請」を返す
			output.setAppOverTimeOp(appOptional);
			output.setDisplayInfoOverTime(disOptional.get());
			return output;
		}
		// PCのアルゴリズム「01_初期起動の処理」を実行する
		displayInfoOverTime = this.getInitData(
				companyId,
				dateOptional,
				overtimeAppAtr,
				appDispInfoStartupOutput,
				Optional.empty(),
				Optional.empty(),
				false);
		output.setDisplayInfoOverTime(displayInfoOverTime);	
		return output;
	}

	@Override
	public DisplayInfoOverTime changeDateMobile(
			String companyId,
			GeneralDate date,
			DisplayInfoOverTime displayInfoOverTime) {
		
		List<GeneralDate> dates = new ArrayList<>();
		dates.add(date);
		
		// 申請表示情報(基準日関係あり)を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithmImpl.getAppDispInfoWithDate(
				companyId,
				ApplicationType.OVER_TIME_APPLICATION,
				dates,
				displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoNoDateOutput(),
				true,
				Optional.of(displayInfoOverTime.getOvertimeAppAtr()));
		// 「残業申請の表示情報」を更新する
		displayInfoOverTime.getAppDispInfoStartup().setAppDispInfoWithDateOutput(appDispInfoWithDateOutput);
		// 申請日に関する情報を取得する
		commonAlgorithmOverTime.changeApplicationDate(companyId, date, displayInfoOverTime);
		
		return displayInfoOverTime;
	}

	@Override
	public List<ConfirmMsgOutput> checkBeforeInsert(
			Boolean require,
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime) {
		List<ConfirmMsgOutput> outputs = new ArrayList<ConfirmMsgOutput>();
		// 申請する残業時間をチェックする
		commonAlgorithmOverTime.checkOverTime(appOverTime.getApplicationTime());
		// 事前申請・実績超過チェック
		List<ConfirmMsgOutput> checkExcessList = commonAlgorithmOverTime.checkExcess(appOverTime, displayInfoOverTime);
		outputs.addAll(checkExcessList);
		// 登録時の乖離時間チェックを行う
		this.checkDivergenceTime(
				require,
				ApplicationType.OVER_TIME_APPLICATION,
				Optional.of(appOverTime),
				Optional.empty(),
				displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet());
		// ３６上限チェック
		
		// 取得した「確認メッセージリスト」と「残業申請」を返す
		return outputs;
	}

	@Override
	public void checkBeforeMovetoAppTime(
			String companyId,
			Boolean mode,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime) {
		// 勤務種類、就業時間帯チェックのメッセージを表示
		detailBeforeUpdate.displayWorkingHourCheck(
				companyId,
				appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null),
				appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTimeCode())).map(x -> x.v()).orElse(null));
		// 事前申請が必須か確認する
		displayInfoOverTime.getInfoNoBaseDate()
					       .getOverTimeAppSet()
					       .getApplicationDetailSetting()
					       .checkAdvanceApp(
					    		   ApplicationType.OVER_TIME_APPLICATION,
					    		   appOverTime.getPrePostAtr(),
					    		   displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpPreAppContentDisplayLst().map(x -> x.get(0).getApOptional()).orElse(Optional.empty()),
					    		   Optional.empty());
		// 申請日の矛盾チェック
		commonAlgorithmOverTime.commonAlgorithmAB(
				companyId,
				displayInfoOverTime,
				appOverTime,
				BooleanUtils.toInteger(mode));
	}

	@Override
	public void checkContentApp(String companyId, DisplayInfoOverTime displayInfoOverTime, AppOverTime appOverTime,
			Boolean mode) {
	    int totalOverTime = 0;
	    if (appOverTime.getApplicationTime() != null) {
	        totalOverTime = appOverTime.getApplicationTime().getApplicationTime().stream()
	                .map(x -> x.getApplicationTime().v())
	                .mapToInt(Integer::intValue)
	                .sum();
	        totalOverTime += appOverTime.getApplicationTime().getOverTimeShiftNight().isPresent() 
                    && appOverTime.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight() != null ? 
	                appOverTime.getApplicationTime().getOverTimeShiftNight().get().getOverTimeMidNight().v() : 0;
	                totalOverTime += appOverTime.getApplicationTime().getFlexOverTime().map(AttendanceTimeOfExistMinus::v).orElse(0);
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
			// 2-1.新規画面登録前の処理
			List<ConfirmMsgOutput> confirmMsgOutputs = processBeforeRegister.processBeforeRegister_New(
					companyId,
					EmploymentRootAtr.APPLICATION, // QA 112515 done
					false,
					appOverTime.getApplication(),
					appOverTime.getOverTimeClf(),
					displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpMsgErrorLst().orElse(Collections.emptyList()),
					Collections.emptyList(), 
					displayInfoOverTime.getAppDispInfoStartup(),
					appOverTime.getWorkInfoOp().isPresent() ? Arrays.asList(appOverTime.getWorkInfoOp().get().getWorkTypeCode().v()) : new ArrayList<String>(), 
			        Optional.of(timeDigestionParam), 
			        appOverTime.getWorkInfoOp().isPresent() ? appOverTime.getWorkInfoOp().get().getWorkTimeCodeNotNull().map(WorkTimeCode::v) : Optional.empty(), 
			        false);
			
		} else { // 詳細・照会モード　の場合
			// 4-1.詳細画面登録前の処理
			detailBeforeUpdate.processBeforeDetailScreenRegistration(
					companyId,
					appOverTime.getEmployeeID(),
					appOverTime.getAppDate().getApplicationDate(),
					EmploymentRootAtr.APPLICATION.value,
					appOverTime.getAppID(),
					appOverTime.getPrePostAtr(),
					appOverTime.getVersion(),
					appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null),
					appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTimeCode())).map(x -> x.v()).orElse(null),
					displayInfoOverTime.getAppDispInfoStartup(), 
					new ArrayList<String>(), 
	                Optional.of(timeDigestionParam), 
	                false,
	                appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode().v()), 
	                appOverTime.getWorkInfoOp().isPresent() ? appOverTime.getWorkInfoOp().get().getWorkTimeCodeNotNull().map(WorkTimeCode::v) : Optional.empty());
			
		}
		// 申請時間に移動する前の個別チェック処理
		this.checkBeforeMovetoAppTime(
				companyId,
				mode,
				displayInfoOverTime,
				appOverTime);
		
	}

	@Override
	public DisplayInfoOverTime calculateMobile(
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime,
			Boolean mode,
			String employeeId,
			Optional<GeneralDate> dateOp,
			Boolean agent) {
		
		// 勤務情報の申請内容をチェックする
		this.checkContentApp(
				companyId,
				displayInfoOverTime,
				appOverTime,
				mode);
		if (displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getApplicationDetailSetting().getTimeCalUse() == nts.uk.shr.com.enumcommon.NotUseAtr.USE) {
			Integer prePost = appOverTime.getApplication().getPrePostAtr().value;
				WorkContent workContent = new WorkContent();
				if (appOverTime.getWorkInfoOp().isPresent()) {
					workContent.setWorkTypeCode(appOverTime.getWorkInfoOp()
							.flatMap(x -> Optional.ofNullable(x.getWorkTypeCode()))
							.flatMap(x -> Optional.ofNullable(x.v())));

					workContent.setWorkTimeCode(appOverTime.getWorkInfoOp()
							.flatMap(x -> x.getWorkTimeCodeNotNull())
							.flatMap(x -> Optional.ofNullable(x.v())));
				}
				List<TimeZone> timeZones = new ArrayList<TimeZone>();
				List<BreakTimeSheet> breakTimes = new ArrayList<BreakTimeSheet>();
				if (appOverTime.getWorkHoursOp().isPresent()) {
					appOverTime.getWorkHoursOp()
							   .get()
							   .stream()
							   .forEach(x -> {
									TimeWithDayAttr start = x.getTimeZone().getStartTime();
									TimeWithDayAttr end = x.getTimeZone().getEndTime();
									timeZones.add(new TimeZone(start, end));
							    });
				}
				if (appOverTime.getBreakTimeOp().isPresent()) {
					appOverTime.getBreakTimeOp().get()
							   .stream()
							   .forEach(x -> {
									TimeWithDayAttr start = x.getTimeZone().getStartTime();
									TimeWithDayAttr end = x.getTimeZone().getEndTime();
									breakTimes.add(new BreakTimeSheet(
											new BreakFrameNo(x.getWorkNo().v()),
											start,
											end));
							   });
				}
				
				
				workContent.setTimeZones(timeZones);
				workContent.setBreakTimes(breakTimes);
			Optional<AchievementDetail> opAchievementDetail = displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent()
					? displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail()
					: Optional.empty();
			// 計算処理を実行する
			DisplayInfoOverTime temp = this.calculate(
					companyId,
					employeeId,
					dateOp,
					displayInfoOverTime.getOvertimeAppAtr(),
					EnumAdaptor.valueOf(prePost, PrePostInitAtr.class),
					displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet(),
					displayInfoOverTime.getAppDispInfoStartup()
									.getAppDispInfoWithDateOutput()
									.getOpPreAppContentDisplayLst()
									.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(x.get(0)))
									.flatMap(y -> y.getApOptional())
									.map(z -> z.getApplicationTime())
									.orElse(null),
					displayInfoOverTime.getInfoWithDateApplicationOp()
						.map(x -> x.getApplicationTime().orElse(null))
						.orElse(null),
					workContent,
					displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet(),
					agent,
					new ArrayList<>(),
					new ArrayList<>(),
					opAchievementDetail
			);
			displayInfoOverTime.setCalculationResultOp(temp.getCalculationResultOp());
			displayInfoOverTime.setWorkdayoffFrames(temp.getWorkdayoffFrames());
		}
		
		return displayInfoOverTime;
	}

	@Override
	public CheckBeforeOutputMulti checkErrorRegisterMultiple(
			Boolean require,
			String companyId,
			DisplayInfoOverTime displayInfoOverTime,
			AppOverTime appOverTime) {
		CheckBeforeOutputMulti checkBeforeOutputMulti = new CheckBeforeOutputMulti();
		// 2-1.新規画面登録前の処理(複数人)
		CheckBeforeRegisMultiEmpOutput checkBeforeRegisMultiEmpOutput = processBeforeRegister.processBeforeRegisterMultiEmp(
				companyId,
				displayInfoOverTime.getAppDispInfoStartup()
								   .getAppDispInfoNoDateOutput()
								   .getEmployeeInfoLst()
								   .stream()
								   .map(x -> x.getSid())
								   .collect(Collectors.toList()),
			   appOverTime.getApplication(),
			   appOverTime.getOverTimeClf(),
			   displayInfoOverTime.getAppDispInfoStartup());
		checkBeforeOutputMulti.setApprovalRootContentMap(checkBeforeRegisMultiEmpOutput.getMapEmpContentAppr());
		checkBeforeOutputMulti.setErrorEmpBusinessName(checkBeforeRegisMultiEmpOutput.getEmpErrorName());
		// 個別登録前チェッ処理（複数人版）
		Map<String, List<ConfirmMsgOutput>> confirmMsgOutputMap = this.checkIndividualMultiple(companyId, appOverTime, displayInfoOverTime);
		checkBeforeOutputMulti.setConfirmMsgOutputMap(confirmMsgOutputMap);
		
		return checkBeforeOutputMulti;
	}

	@Override
	public Map<String, List<ConfirmMsgOutput>> checkIndividualMultiple(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime) {
		Map<String, List<ConfirmMsgOutput>> confirmMsgOutputMap = new HashMap<String, List<ConfirmMsgOutput>>();
		// 残業申請の共通部分をチェックする
		this.checkCommonOverTime(appOverTime, displayInfoOverTime);
		
		// 残業申請の表示情報．申請表示情報．申請表示情報(基準日関係なし)．社員情報リスト」をループする
		List<String> employeeList = displayInfoOverTime.getAppDispInfoStartup()
				   .getAppDispInfoNoDateOutput()
				   .getEmployeeInfoLst()
				   .stream()
				   .map(x -> x.getSid())
				   .collect(Collectors.toList());
		for (String el : employeeList) {
			// INPUT「残業申請」の申請者を置き替える
			appOverTime.setEmployeeID(el);
			// 申請対象者の情報を再取得する
			this.reacquireInfoEmploy(
					companyId,
					appOverTime,
					displayInfoOverTime);
			// 申請者ごとの申請内容をチェックする
			List<ConfirmMsgOutput> confirms = this.checkEachEmployee(
					companyId,
					appOverTime,
					displayInfoOverTime);
			// OUTPUTのList<社員名、確認メッセージリスト>に追加する
			confirmMsgOutputMap.put(el, confirms);
			
			
		}
		
		return confirmMsgOutputMap;
	}

	@Override
	public void checkCommonOverTime(AppOverTime appOverTime, DisplayInfoOverTime displayInfoOverTime) {
		// 03-06_計算ボタンチェック
		commonOvertimeHoliday.calculateButtonCheck(
				displayInfoOverTime.getCalculatedFlag(),
				EnumAdaptor.valueOf(displayInfoOverTime.getInfoNoBaseDate()
						.getOverTimeAppSet()
						.getApplicationDetailSetting()
						.getTimeCalUse().value, UseAtr.class));
		
		// 勤務種類、就業時間帯チェックのメッセージを表示
		detailBeforeUpdate.displayWorkingHourCheck(
				AppContexts.user().companyId(),
				appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null),
				appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTimeCode())).map(x -> x.v()).orElse(null));
		
		
		// 申請する残業時間をチェックする
		commonAlgorithmOverTime.checkOverTime(appOverTime.getApplicationTime());
		
	}

	@Override
	public DisplayInfoOverTime reacquireInfoEmploy(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime) {
		List<GeneralDate> appDates = new ArrayList<>();
		appDates.add(appOverTime.getApplication().getAppDate().getApplicationDate());
		// 事前内容の取得
		List<PreAppContentDisplay> preAppContentDisplayLst = collectAchievement.getPreAppContents(
				companyId,
				appOverTime.getApplication().getEmployeeID(),
				appDates,
				ApplicationType.OVER_TIME_APPLICATION,
                Optional.of(appOverTime.getOverTimeClf())
        );
		// 実績内容の取得
		List<ActualContentDisplay> actualContentDisplayLst = collectAchievement.getAchievementContents(
				companyId,
				appOverTime.getApplication().getEmployeeID(),
				appDates,
				ApplicationType.OVER_TIME_APPLICATION);
		
		// 07-02_実績取得・状態チェック
		
		ApplicationTime applicationTime = preActualColorCheck.checkStatus(
				companyId,
				appOverTime.getApplication().getEmployeeID(),
				appOverTime.getApplication().getAppDate().getApplicationDate(),
				ApplicationType.OVER_TIME_APPLICATION,
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTypeCode()).orElse(null),
				appOverTime.getWorkInfoOp().map(x -> x.getWorkTimeCode()).orElse(null),
				displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet().getOverrideSet(),
				Optional.empty(),
				appOverTime.getBreakTimeOp().map(y -> y.stream().map(x -> new DeductionTime(x.getTimeZone().getStartTime(), x.getTimeZone().getEndTime())).collect(Collectors.toList())).orElse(Collections.emptyList()),
				CollectionUtil.isEmpty(actualContentDisplayLst) ? Optional.empty() : Optional.of(actualContentDisplayLst.get(0)));
		// [RQ31]社員所属雇用履歴を取得
		SEmpHistImport sEmpHistExport = employeeRequestAdapter.getEmpHist(
				companyId,
				appOverTime.getApplication().getEmployeeID(),
				displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getBaseDate());
		
		if (sEmpHistExport == null) { // 終了状態：社員所属雇用履歴取得失敗
			// エラーメッセージ(Msg_2148)を表示する
			
			/**
			 * {0}：INPUT．「残業申請の表示情報．申請表示情報．申請表示情報(基準日関係なし)．社員情報リスト．3社員名」←条件：社員ID＝INPUT「残業申請．申請．申請者」
			 */
			throw new BusinessException(
					"Msg_2148", 
					displayInfoOverTime.getAppDispInfoStartup()
									   .getAppDispInfoNoDateOutput()
									   .getEmployeeInfoLst()
									   .stream()
									   .filter(x -> x.getSid().equals(appOverTime.getApplication().getEmployeeID()))
									   .findFirst()
									   .map(x -> x.getBussinessName())
									   .orElse("")
					);
		}
		
		// INPUT「残業申請の表示情報」を更新して返す
		displayInfoOverTime.getAppDispInfoStartup()
						   .getAppDispInfoWithDateOutput()
						   .setOpPreAppContentDisplayLst(CollectionUtil.isEmpty(preAppContentDisplayLst) ? Optional.empty() : Optional.of(preAppContentDisplayLst));
		
		
		displayInfoOverTime.getAppDispInfoStartup()
						   .getAppDispInfoWithDateOutput()
						   .setOpActualContentDisplayLst(CollectionUtil.isEmpty(actualContentDisplayLst) ? Optional.empty() : Optional.of(actualContentDisplayLst));
		
		displayInfoOverTime.getAppDispInfoStartup()
		   				   .getAppDispInfoWithDateOutput()
		   				   .setEmpHistImport(sEmpHistExport);
		if (Optional.ofNullable(applicationTime).isPresent()) {
			if (!displayInfoOverTime.getInfoWithDateApplicationOp().isPresent()) {
				displayInfoOverTime.setInfoWithDateApplicationOp(Optional.of(new InfoWithDateApplication()));
			}
			displayInfoOverTime.getInfoWithDateApplicationOp().get().setApplicationTime(Optional.of(applicationTime));
		}
		
		
		return displayInfoOverTime;
	}

	@Override
	public List<ConfirmMsgOutput> checkEachEmployee(
			String companyId,
			AppOverTime appOverTime,
			DisplayInfoOverTime displayInfoOverTime) {
		List<ConfirmMsgOutput> output = new ArrayList<>();
		Optional<BusinessException> businessException = Optional.empty();
		try {
			// 事前申請が必須か確認する
			displayInfoOverTime.getInfoNoBaseDate()
			.getOverTimeAppSet()
			.getApplicationDetailSetting()
			.checkAdvanceApp(
					ApplicationType.OVER_TIME_APPLICATION,
					appOverTime.getPrePostAtr(),
					displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpPreAppContentDisplayLst().map(x -> x.get(0).getApOptional()).orElse(Optional.empty()),
					Optional.empty());
			
		} catch (Exception e) {
			
			businessException = Optional.of(new BusinessException(
					"Msg_2081", 
					displayInfoOverTime.getAppDispInfoStartup()
					.getAppDispInfoNoDateOutput()
					.getEmployeeInfoLst()
					.stream()
					.filter(x -> x.getSid().equals(appOverTime.getEmployeeID()))
					.findFirst()
					.map(x -> x.getBussinessName())
					.orElse(""),
					e.getMessage()  
					));
			
			
		}	
			// 事前申請・実績超過チェック
			List<ConfirmMsgOutput> checkExcessList = commonAlgorithmOverTime.checkExcess(appOverTime, displayInfoOverTime);
				
			this.toMultiMessage(checkExcessList);
			output = checkExcessList;
		try {
			// 申請時の乖離時間をチェックする
			this.checkDivergenceTime(
					true,
					ApplicationType.OVER_TIME_APPLICATION,
					Optional.of(appOverTime),
					Optional.empty(),
					displayInfoOverTime.getInfoNoBaseDate().getOverTimeAppSet().getOvertimeLeaveAppCommonSet());
			// ３６上限チェック
			commonAlgorithmOverTime.check36Limit(companyId, appOverTime, displayInfoOverTime);		
		} catch (Exception e) {
						
			businessException = Optional.of(new BusinessException(
					"Msg_2081", 
					displayInfoOverTime.getAppDispInfoStartup()
					.getAppDispInfoNoDateOutput()
					.getEmployeeInfoLst()
					.stream()
					.filter(x -> x.getSid().equals(appOverTime.getEmployeeID()))
					.findFirst()
					.map(x -> x.getBussinessName())
					.orElse(""),
					e.getMessage()  
					));
			
			
		}
		if (businessException.isPresent()) {
			throw businessException.get();
		}
		// 申請の矛盾チェック
		String workType = appOverTime.getWorkInfoOp().flatMap(x -> Optional.ofNullable(x.getWorkTypeCode())).map(x -> x.v()).orElse(null);
		GeneralDate date = appOverTime.getApplication().getAppDate().getApplicationDate();
		commonAlgorithmImpl.appConflictCheck(
				companyId,
				displayInfoOverTime.getAppDispInfoStartup()
				   .getAppDispInfoNoDateOutput()
				   .getEmployeeInfoLst()
				   .stream()
				   .filter(x -> x.getSid().equals(appOverTime.getApplication().getEmployeeID()))
				   .findFirst()
				   .orElse(null),
				Optional.ofNullable(date).isPresent() ? Arrays.asList(date) : Collections.emptyList(), 
    			StringUtils.isBlank(workType) ? Collections.emptyList() : Arrays.asList(workType),
				displayInfoOverTime.getAppDispInfoStartup().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().orElse(Collections.emptyList()));
		
		
		return output;
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
					case "Msg_1508": confirmMsg.setMsgID("Msg_2019"); break;
					default:
						confirmMsg.setMsgID(confirmMsg.getMsgID());
						break;
				}
				return confirmMsg;
			}).collect(Collectors.toList());
		return confirmMsgOutputsMulti;
	}

	@Override
	public Pair<List<TimeZoneWithWorkNo>, List<BreakTimeSheet>> getWorkingHoursAndBreakHours(String companyId,
																							 String employeeId,
																							 GeneralDate appDate,
																							 List<OvertimeHour> overtimeHours,
																							 List<OvertimeReason> overtimeReasons,
																							 List<TimeZoneWithWorkNo> workingHours,
																							 WorkInformation workInformation,
																							 Optional<AchievementDetail> achievementDetail) {
		// 複数回残業内容を作成する
		OvertimeWorkMultipleTimes content = OvertimeWorkMultipleTimes.create(overtimeHours, overtimeReasons);

		// 残業時間を計算のために勤務時間を判断する
		workingHours = content.getWorkingHoursToCalculateOvertime(workingHours);

		// 休憩時間帯を取得する
		BreakTimeZoneSetting breakTimeZoneSetting = commonAlgorithmOverTime.selectWorkTypeAndTime(
				companyId,
				workInformation.getWorkTypeCode(),
				workInformation.getWorkTimeCode(),
				workingHours.isEmpty() ? Optional.empty() : Optional.of(workingHours.get(0).getTimeZone().getStartTime()),
				workingHours.isEmpty() ? Optional.empty() : Optional.of(workingHours.get(0).getTimeZone().getEndTime()),
				achievementDetail
		);
		List<BreakTimeSheet> breakTimeSheets = new ArrayList<>();
		for (int i = 0; i < breakTimeZoneSetting.getTimeZones().size(); i++) {
			breakTimeSheets.add(new BreakTimeSheet(
					new BreakFrameNo(i + 1),
					breakTimeZoneSetting.getTimeZones().get(i).getStart(),
					breakTimeZoneSetting.getTimeZones().get(i).getEnd()
			));
		}

		List<BreakTimeSheet> breakTimes = content.getBreakTimeToCalculateOvertime(
				new OvertimeWorkMultipleTimes.Require() {
					@Override
					public Optional<WorkTimeSetting> getWorkTimeSetting(String companyId, String code) {
						return worktimeSetRepo.findByCode(companyId, code);
					}
					@Override
					public IntegrationOfDaily tempCalculateOneDayAttendanceTime(CalculationParams params) {
						return attendanceCalcAdapter.calculate(params);
					}
					@Override
					public Optional<PredetemineTimeSetting> getPredetemineTimeSetting(String companyId, String workTimeCode) {
						return predetemineTimeSetRepo.findByWorkTimeCode(companyId, workTimeCode);
					}
				},
				companyId,
				employeeId,
				appDate,
				workInformation,
				workingHours,
				breakTimeSheets
		);

		return Pair.of(workingHours, breakTimes);
	}
	
}
