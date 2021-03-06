package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayMidnightWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.ExcessOverTimeWorkMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.VacationClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workingstyle.flex.SettingOfFlexWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.AttendanceItemDictionaryForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryOccurrenceSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.flexset.CoreTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeDailyAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * ??????????????????????????????
 * @author keisuke_hoshina
 */
@Getter
public class ExcessOfStatutoryTimeOfDaily {
	//?????????????????????
	@Setter
	private ExcessOfStatutoryMidNightTime excessOfStatutoryMidNightTime;
	//????????????
	private Optional<OverTimeOfDaily> overTimeWork;
	//????????????
	private Optional<HolidayWorkTimeOfDaily> workHolidayTime;
	
	
	/**
	 * Constructor
	 * @param excessOfStatutoryMidNightTime
	 * @param overTimeWork
	 * @param workHolidayTime
	 */
	public ExcessOfStatutoryTimeOfDaily(
			ExcessOfStatutoryMidNightTime excessOfStatutoryMidNightTime,
			Optional<OverTimeOfDaily> overTimeWork,
			Optional<HolidayWorkTimeOfDaily> workHolidayTime) {
		super();
		this.excessOfStatutoryMidNightTime = excessOfStatutoryMidNightTime;
		this.overTimeWork = overTimeWork;
		this.workHolidayTime = workHolidayTime;
	}
	
	public void updateOverTime(OverTimeOfDaily ot){
		this.overTimeWork = Optional.ofNullable(ot);
	}
	
	public void updateHoliday(HolidayWorkTimeOfDaily ht){
		this.workHolidayTime = Optional.ofNullable(ht);
	}
	
	/**
	 * ??????????????????????????????????????????
	 * ???????????????????????????????????????????????????
	 * @param recordReget ??????
	 * @param workType ????????????
	 * @param flexCalcMethod ??????????????????????????????
	 * @param vacationClass ???????????????
	 * @param siftCode ????????????????????????
	 * @param workTimeDailyAtr ??????????????????
	 * @param eachCompanyTimeSet ???????????????????????????
	 * @param conditionItem ??????????????????
	 * @param predetermineTimeSetByPersonInfo ???????????????????????????????????????
	 * @param coreTimeSetting ??????????????????????????????
	 * @param declareResult ???????????????????????????
	 * @return ??????????????????????????????
	 */
	public static ExcessOfStatutoryTimeOfDaily calculationExcessTime(
			ManageReGetClass recordReget,
			WorkType workType,
			Optional<SettingOfFlexWork> flexCalcMethod,
			VacationClass vacationClass,
			Optional<WorkTimeCode> siftCode,
			Optional<WorkTimeDailyAtr> workTimeDailyAtr,
			List<CompensatoryOccurrenceSetting> eachCompanyTimeSet,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			Optional<CoreTimeSetting> coreTimeSetting,
			DeclareTimezoneResult declareResult) {
		
		//?????????????????????????????????????????????
		AttendanceTime beforeApplicationTime = AttendanceTime.ZERO;
		if(recordReget.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance().isPresent()) {
			beforeApplicationTime = recordReget.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getExcessOfStatutoryMidNightTime().getBeforeApplicationTime();
		}
		//?????????????????????
		AttendanceTime flexPreAppTime = AttendanceTime.ZERO;
		if(recordReget.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance().isPresent()
				&& recordReget.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().isPresent()
				&& recordReget.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime() != null) {
			flexPreAppTime = recordReget.getIntegrationOfDaily().getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily().getOverTimeWork().get().getFlexTime().getBeforeApplicationTime();
		}
		
		//????????????
		val overTime = calculationOverTime(
				recordReget,
				workType,
				flexCalcMethod,
				vacationClass,
				siftCode,
				flexPreAppTime,conditionItem,predetermineTimeSetByPersonInfo,coreTimeSetting,beforeApplicationTime,
				declareResult);
		
		//????????????
		val workHolidayTime = calculationHolidayTime(
				recordReget,
				recordReget.getIntegrationOfDaily().getCalAttr().getHolidayTimeSetting().getRestTime(),
				workType,
				siftCode.map(x -> x.v()),
				recordReget.getIntegrationOfDaily(),beforeApplicationTime,
				declareResult);
		
		//???????????????
		val excessOfStatutoryMidNightTime = ExcessOfStatutoryMidNightTime.calcExcessTime(Optional.of(overTime),Optional.of(workHolidayTime));
		
		return new ExcessOfStatutoryTimeOfDaily(excessOfStatutoryMidNightTime, Optional.of(overTime), Optional.of(workHolidayTime));
	}
	


	/**
	 * ?????????????????????
	 * @param oneDay ??????
	 * @param workType ????????????
	 * @param flexCalcMethod ??????????????????????????????
	 * @param vacationClass ???????????????
	 * @param siftCode ???????????????
	 * @param eachWorkTimeSet ????????????????????????????????????
	 * @param eachCompanyTimeSet ???????????????????????????
	 * @param flexPreAppTime ??????????????????
	 * @param conditionItem ??????????????????
	 * @param predetermineTimeSetByPersonInfo ???????????????????????????????????????
	 * @param coreTimeSetting ??????????????????????????????
	 * @param beforeApplicationTime ??????????????????
	 * @param declareResult ???????????????????????????
	 * @return ???????????????????????????
	 */
	private static OverTimeOfDaily calculationOverTime(
			ManageReGetClass oneDay,
			WorkType workType,
			Optional<SettingOfFlexWork> flexCalcMethod,
			VacationClass vacationClass,
			Optional<WorkTimeCode> siftCode,
			AttendanceTime flexPreAppTime,
			WorkingConditionItem conditionItem,
			Optional<PredetermineTimeSetForCalc> predetermineTimeSetByPersonInfo,
			Optional<CoreTimeSetting> coreTimeSetting,
			AttendanceTime beforeApplicationTime,
			DeclareTimezoneResult declareResult) {
		
		if(oneDay.getCalculationRangeOfOneDay() != null && oneDay.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().isPresent()) {
			if(oneDay.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get().getOverTimeWorkSheet().isPresent()) {
				return OverTimeOfDaily.calculationTime(
						oneDay,
						workType,
						flexCalcMethod,
						vacationClass,
						siftCode,
						flexPreAppTime,
						conditionItem,
						predetermineTimeSetByPersonInfo,
						coreTimeSetting,
						beforeApplicationTime,
						declareResult);
			}
		}
		//??????????????????????????????????????????????????????????????????
		List<OverTimeFrameTime> calcOverTimeWorkTimeList = new ArrayList<>();
		return new OverTimeOfDaily(
				Collections.emptyList(),
				calcOverTimeWorkTimeList,
				Finally.of(new ExcessOverTimeWorkMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)))),
				new AttendanceTime(0),
				new FlexTime(TimeDivergenceWithCalculationMinusExist.sameTime(new AttendanceTimeOfExistMinus(0)), new AttendanceTime(0)),
				new AttendanceTime(0));
	}
	
	/**
	 * ?????????????????????
	 * ????????????????????????????????????????????????
	 * @param recordReget ??????
	 * @param autoCalSetting ????????????????????????????????????
	 * @param workType ????????????
	 * @param eachWorkTimeSet ????????????????????????????????????
	 * @param eachCompanyTimeSet ???????????????????????????
	 * @param integrationOfDaily ????????????(Work)
	 * @param beforeApplicationTime ??????????????????
	 * @param declareResult ???????????????????????????
	 * @return ???????????????????????????
	 */
	private static HolidayWorkTimeOfDaily calculationHolidayTime(
			ManageReGetClass recordReget,
			AutoCalSetting autoCalSetting,
			WorkType workType,
			Optional<String> workTimeCode,
			IntegrationOfDaily integrationOfDaily,
			AttendanceTime beforeApplicationTime,
			DeclareTimezoneResult declareResult) {
		
		if(recordReget.getCalculatable() && recordReget.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().isPresent()) {
			if(recordReget.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get().getHolidayWorkTimeSheet().isPresent()) {
				return HolidayWorkTimeOfDaily.calculationTime(
						recordReget,
						recordReget.getCalculationRangeOfOneDay().getOutsideWorkTimeSheet().get().getHolidayWorkTimeSheet().get(),
						autoCalSetting,
						workType,
						workTimeCode,
						integrationOfDaily,
						beforeApplicationTime,
						recordReget.getIntegrationOfDaily().getCalAttr().getHolidayTimeSetting().getLateNightTime(),
						declareResult);
			}
		}
		
		//????????????????????????????????????????????????????????????????????????
		List<HolidayWorkFrameTime> calcHolidayTimeWorkTimeList = new ArrayList<>();
		List<HolidayWorkMidNightTime> addList = new ArrayList<>();
		addList.add(new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork));
		addList.add(new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), StaturoryAtrOfHolidayWork.PublicHolidayWork));
		addList.add(new HolidayWorkMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)), StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork));
		return new HolidayWorkTimeOfDaily(Collections.emptyList(),
										calcHolidayTimeWorkTimeList,
										Finally.of(new HolidayMidnightWork(addList)),
										new AttendanceTime(0));
	}
	
	/**
	 * ?????????????????? 
	 */
	public List<EmployeeDailyPerError> checkOverTimeExcess(String employeeId,
														   GeneralDate targetDate,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getOverTimeWork().isPresent())
			returnErrorItem = this.getOverTimeWork().get().checkOverTimeExcess(employeeId,targetDate, attendanceItemDictionary,errorCode);
		return returnErrorItem;
	}
	
	/**
	 * ???????????????????????? 
	 */
	public List<EmployeeDailyPerError> checkPreOverTimeExcess(String employeeId,
														   GeneralDate targetDate,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getOverTimeWork().isPresent())
			returnErrorItem = this.getOverTimeWork().get().checkPreOverTimeExcess(employeeId,targetDate, attendanceItemDictionary, errorCode);
		return returnErrorItem;
	}
	/**
	 * ?????????????????? 
	 */
	public List<EmployeeDailyPerError> checkFlexTimeExcess(String employeeId,
			   											   GeneralDate targetDate,
														   String searchWord,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getOverTimeWork().isPresent())
			returnErrorItem = this.getOverTimeWork().get().checkFlexTimeExcess(employeeId,targetDate,searchWord, attendanceItemDictionary, errorCode);
		return returnErrorItem;
	}
	
	/**
	 * ?????????????????? 
	 */
	public List<EmployeeDailyPerError> checkPreFlexTimeExcess(String employeeId,
			   											   GeneralDate targetDate,
														   String searchWord,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getOverTimeWork().isPresent())
			returnErrorItem = this.getOverTimeWork().get().checkPreFlexTimeExcess(employeeId,targetDate,searchWord, attendanceItemDictionary, errorCode);
		return returnErrorItem;
	}
	
	/**
	 * ?????????????????? 
	 */
	public List<EmployeeDailyPerError> checkHolidayWorkTimeExcess(String employeeId,
														   		  GeneralDate targetDate,
																   AttendanceItemDictionaryForCalc attendanceItemDictionary,
																   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getWorkHolidayTime().isPresent())
			returnErrorItem = this.getWorkHolidayTime().get().checkHolidayWorkExcess(employeeId,targetDate, attendanceItemDictionary, errorCode);
		return returnErrorItem;
	}
	
	/**
	 * ???????????????????????? 
	 */
	public List<EmployeeDailyPerError> checkPreHolidayWorkTimeExcess(String employeeId,
														   		  GeneralDate targetDate,
																   AttendanceItemDictionaryForCalc attendanceItemDictionary,
																   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorItem = new ArrayList<>();
		if(this.getWorkHolidayTime().isPresent())
			returnErrorItem = this.getWorkHolidayTime().get().checkPreHolidayWorkExcess(employeeId,targetDate, attendanceItemDictionary, errorCode);
		return returnErrorItem;
	}
	
	/**
	 * ?????????????????????????????????
	 */
	public List<EmployeeDailyPerError> checkMidNightExcess(String employeeId,
			   											   GeneralDate targetDate,
														   String searchWord,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if(this.getExcessOfStatutoryMidNightTime().isOverLimitDivergenceTime()) {
			val itemId = attendanceItemDictionary.findId(searchWord);
			if(itemId.isPresent())
				returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId.get()));
		}
		//????????????
		if(this.getOverTimeWork().isPresent()) {
			returnErrorList.addAll(this.getOverTimeWork().get().checkNightTimeExcess(employeeId,targetDate, "????????????????????????", attendanceItemDictionary, errorCode));
		}
		//????????????
		if(this.getWorkHolidayTime().isPresent()) {
			returnErrorList.addAll(this.getWorkHolidayTime().get().checkNightTimeExcess(employeeId,targetDate, attendanceItemDictionary, errorCode));
		}
		return returnErrorList;
	}
	
	/**
	 * ??????????????????????????????????????? 
	 */
	public List<EmployeeDailyPerError> checkPreMidNightExcess(String employeeId,
			   											   GeneralDate targetDate,
														   String searchWord,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if(this.getExcessOfStatutoryMidNightTime().isPreOverLimitDivergenceTime()) {
			val itemId = attendanceItemDictionary.findId(searchWord);
			if(itemId.isPresent())
				returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId.get()));
		}
		return returnErrorList;
	}
	
	/**
	 * ???????????????????????????
	 * @return
	 */
	public ExcessOfStatutoryTimeOfDaily calcDiverGenceTime() {
		Optional<OverTimeOfDaily> overtime = this.overTimeWork.isPresent()?Optional.of(this.overTimeWork.get().calcDiverGenceTime()):Optional.empty();
		Optional<HolidayWorkTimeOfDaily> holiday = this.workHolidayTime.isPresent()?Optional.of(this.workHolidayTime.get().calcDiverGenceTime()):Optional.empty();
		ExcessOfStatutoryMidNightTime excessOfStatutoryMidNightTime = this.excessOfStatutoryMidNightTime!=null?this.excessOfStatutoryMidNightTime.calcDiverGenceTime():this.excessOfStatutoryMidNightTime;
		return new ExcessOfStatutoryTimeOfDaily(excessOfStatutoryMidNightTime,overtime,holiday); 
	}
	
	/**
	 *???????????????????????????????????????????????????
	 * @param diffHolidayWorkTime ????????????????????????????????????
	 * @param diffOverTime ????????????????????????????????????
	 */
	public void reCalcMidNightTime() {
		ExcessOverTimeWorkMidNightTime overMidTime = new ExcessOverTimeWorkMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0)));
		TimeDivergenceWithCalculation holidayMidTime = TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0));
		if(this.getOverTimeWork().isPresent()
			&& this.getOverTimeWork().get().getExcessOverTimeWorkMidNightTime().isPresent()) {
			overMidTime = this.getOverTimeWork().get().getExcessOverTimeWorkMidNightTime().get();
		}
		if(this.getWorkHolidayTime().isPresent()
			&& this.getWorkHolidayTime().get().getHolidayMidNightWork().isPresent()) {
			holidayMidTime = this.getWorkHolidayTime().get().getHolidayMidNightWork().get().calcAllMidTime();
		}
		this.excessOfStatutoryMidNightTime = new ExcessOfStatutoryMidNightTime(TimeDivergenceWithCalculation.createTimeWithCalculation(overMidTime.getTime().getTime().addMinutes(holidayMidTime.getTime().valueAsMinutes()),
																																	   overMidTime.getTime().getCalcTime().addMinutes(holidayMidTime.getCalcTime().valueAsMinutes())),
																			   this.excessOfStatutoryMidNightTime.getBeforeApplicationTime());
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param upperTime ????????????
	 */
	public void controlMidTimeUpper(AttendanceTime upperTime) {
		this.excessOfStatutoryMidNightTime.controlUpperTime(upperTime);
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????????????????????
	 * @return??????????????????????????????????????????????????????
	 */
	public AttendanceTime calcOverTime() {
		if(this.overTimeWork.isPresent()) {
			return new AttendanceTime(this.getOverTimeWork().get().calcTotalFrameTime() .v()+ this.getOverTimeWork().get().calcTransTotalFrameTime().v());
		}
		return new AttendanceTime(0);
	}
	
	/**
	 * ????????????????????????????????????????????????????????????????????????
	 * @return????????????????????????????????????????????????
	 */
	public AttendanceTime calcWorkHolidayTime() {
		if(this.workHolidayTime.isPresent()) {
			return new AttendanceTime(this.getWorkHolidayTime().get().calcTotalFrameTime().v() + this.getWorkHolidayTime().get().calcTransTotalFrameTime().v());
		}
		return new AttendanceTime(0);
	}
}
