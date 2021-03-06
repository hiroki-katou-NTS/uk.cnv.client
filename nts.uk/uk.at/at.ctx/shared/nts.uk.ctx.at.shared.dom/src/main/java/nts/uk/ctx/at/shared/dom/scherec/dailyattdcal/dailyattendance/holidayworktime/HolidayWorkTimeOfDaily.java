package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer.SubsTransferProcessMode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.BonusPayAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.calcategory.CalAttrOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.BonusPayTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.AttendanceItemDictionaryForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkFrameTimeSheetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.HolidayWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManageReGetClass;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.OutsideWorkTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareCalcRange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.declare.DeclareTimezoneResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.CalculationRangeOfOneDay;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.declare.DeclareFrameSet;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.StaturoryAtrOfHolidayWork;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * ???????????????????????????
 * @author keisuke_hoshina
 *
 */
@Getter
@Setter
public class HolidayWorkTimeOfDaily implements Cloneable{
	//??????????????????
	private List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet;
	//???????????????
	private List<HolidayWorkFrameTime> holidayWorkFrameTime;
	//????????????
	private Finally<HolidayMidnightWork> holidayMidNightWork;
	//??????????????????
	private AttendanceTime holidayTimeSpentAtWork;
	
	/**
	 * Constructor 
	 */
	public HolidayWorkTimeOfDaily(List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheet,List<HolidayWorkFrameTime> holidayWorkFrameTime,
								   Finally<HolidayMidnightWork> holidayMidNightWork, AttendanceTime holidayTimeSpentAtWork) {
		this.holidayWorkFrameTimeSheet = holidayWorkFrameTimeSheet;
		this.holidayWorkFrameTime = holidayWorkFrameTime;
		this.holidayMidNightWork = holidayMidNightWork;
		this.holidayTimeSpentAtWork = holidayTimeSpentAtWork;
	}
	

	/**
	 * ?????????????????????????????????????????????????????????
	 * ????????????????????????????????????????????????
	 * @param recordReGet ??????
	 * @param holidayWorkTimeSheet ?????????????????????
	 * @param holidayAutoCalcSetting ????????????????????????????????????
	 * @param workType ????????????
	 * @param eachWorkTimeSet ????????????????????????????????????
	 * @param eachCompanyTimeSet ???????????????????????????
	 * @param integrationOfDaily ????????????(Work)
	 * @param beforeApplicationTime ??????????????????
	 * @param holidayLateNightAutoCalSetting ??????????????????????????????????????????
	 * @param declareResult ???????????????????????????
	 * @return ???????????????????????????
	 */
	public static HolidayWorkTimeOfDaily calculationTime(
			ManageReGetClass recordReGet,
			HolidayWorkTimeSheet holidayWorkTimeSheet,
			AutoCalSetting holidayAutoCalcSetting,
			WorkType workType,
			Optional<String> workTimeCode,
			IntegrationOfDaily integrationOfDaily,
			AttendanceTime beforeApplicationTime,
			AutoCalSetting holidayLateNightAutoCalSetting,
			DeclareTimezoneResult declareResult) {
		
		//???????????????????????????
		val holidayWorkFrameTimeSheet = holidayWorkTimeSheet.changeHolidayWorkTimeFrameTimeSheet(
				recordReGet.getPersonDailySetting().getOverTimeSheetReq(),
				workType.getCompanyId(),
				holidayAutoCalcSetting,
				workType,
				workTimeCode,
				integrationOfDaily,
				true);
		//?????????????????????
		val holidayWorkFrameTime = holidayWorkTimeSheet.collectHolidayWorkTime(
				recordReGet.getPersonDailySetting().getOverTimeSheetReq(),
				workType.getCompanyId(),
				holidayAutoCalcSetting,
				workType,
				workTimeCode,
				integrationOfDaily,
				declareResult,
				true);
		
		//?????????????????????????????????
		val holidayMidnightWork = Finally.of(calcMidNightTimeIncludeHolidayWorkTime(
				holidayWorkTimeSheet,
				beforeApplicationTime,
				holidayLateNightAutoCalSetting,
				declareResult));
		//????????????
		val holidayTimeSpentTime = new AttendanceTime(0);
		return new HolidayWorkTimeOfDaily(holidayWorkFrameTimeSheet, holidayWorkFrameTime, holidayMidnightWork, holidayTimeSpentTime);
	}
	
	/**
	 * ???????????????????????????????????????????????????????????????
	 * @return????????????????????????
	 */
	public List<BonusPayTime> calcBonusPay(AutoCalRaisingSalarySetting bonusPayAutoCalcSet,BonusPayAtr bonusPayAtr,CalAttrOfDailyAttd calcAtrOfDaily){
		List<BonusPayTime> bonusPayList = new ArrayList<>();
//		for(HolidayWorkFrameTimeSheet frameTimeSheet: holidayWorkFrameTimeSheet) {
//			//bonusPayList.addAll(frameTimeSheet.calcBonusPay(ActualWorkTimeSheetAtr.HolidayWork,bonusPayAutoCalcSet,calcAtrOfDaily));
//		}
		return bonusPayList;
	}
	
	/**
	 * ????????????????????????????????????????????????????????????????????????
	 * @return????????????????????????
	 */
	public List<BonusPayTime> calcSpecifiedBonusPay(AutoCalRaisingSalarySetting bonusPayAutoCalcSet,BonusPayAtr bonusPayAtr,CalAttrOfDailyAttd calcAtrOfDaily){
		List<BonusPayTime> bonusPayList = new ArrayList<>();
//		for(HolidayWorkFrameTimeSheet frameTimeSheet: holidayWorkFrameTimeSheet) {
//			//bonusPayList.addAll(frameTimeSheet.calcSpacifiedBonusPay(ActualWorkTimeSheetAtr.HolidayWork,bonusPayAutoCalcSet,calcAtrOfDaily));
//		}
		return bonusPayList;
	}
	
	/**
	 * ???????????????????????????????????????????????????
	 * ?????????????????????????????????????????????????????????????????????????????????
	 * ??????????????????????????????????????????
	 * @param holidayWorkTimeSheet ?????????????????????
	 * @param beforeApplicationTime ??????????????????
	 * @param holidayLateNightAutoCalSetting ??????????????????
	 * @param declareResult ???????????????????????????
	 * @return ????????????
	 */
	public static HolidayMidnightWork calcMidNightTimeIncludeHolidayWorkTime(
			HolidayWorkTimeSheet holidayWorkTimeSheet,
			AttendanceTime beforeApplicationTime,
			AutoCalSetting holidayLateNightAutoCalSetting,
			DeclareTimezoneResult declareResult) {
		
		EachStatutoryHolidayWorkTime eachTime = new EachStatutoryHolidayWorkTime();
		for(HolidayWorkFrameTimeSheetForCalc  frameTime : holidayWorkTimeSheet.getWorkHolidayTime()) {
			eachTime.addTime(frameTime.getStatutoryAtr().get(), frameTime.calcMidNightTime(holidayLateNightAutoCalSetting));
		}
		List<HolidayWorkMidNightTime> holidayWorkList = new ArrayList<>();
		holidayWorkList.add(new HolidayWorkMidNightTime(eachTime.getStatutory(), StaturoryAtrOfHolidayWork.WithinPrescribedHolidayWork));
		holidayWorkList.add(new HolidayWorkMidNightTime(eachTime.getExcess(), StaturoryAtrOfHolidayWork.ExcessOfStatutoryHolidayWork));
		holidayWorkList.add(new HolidayWorkMidNightTime(eachTime.getPublicholiday(), StaturoryAtrOfHolidayWork.PublicHolidayWork));
		
		//??????????????????????????????
		if(holidayLateNightAutoCalSetting.getUpLimitORtSet()==TimeLimitUpperLimitSetting.LIMITNUMBERAPPLICATION){
			//??????????????????
			for(HolidayWorkMidNightTime holidayWorkMidNightTime:holidayWorkList) {
				if(holidayWorkMidNightTime.getTime().getTime().greaterThanOrEqualTo(beforeApplicationTime.valueAsMinutes())) {
					TimeDivergenceWithCalculation time = TimeDivergenceWithCalculation.createTimeWithCalculation(beforeApplicationTime, holidayWorkMidNightTime.getTime().getCalcTime());
					holidayWorkMidNightTime.reCreate(time);
				}
			}
		}
		if (declareResult.getCalcRangeOfOneDay().isPresent()){
			// ?????????????????????????????????
			HolidayWorkTimeOfDaily.calcDeclareHolidayWorkMidnightTime(
					holidayWorkList, holidayLateNightAutoCalSetting, declareResult);
		}
		return new HolidayMidnightWork(holidayWorkList);
	}
	
	/**
	 * ?????????????????????????????????
	 * @param recordList ??????????????????
	 * @param holidayLateNightAutoCalSetting ??????????????????
	 * @param declareResult ???????????????????????????
	 * @return ????????????????????????
	 */
	private static void calcDeclareHolidayWorkMidnightTime(
			List<HolidayWorkMidNightTime> recordList,
			AutoCalSetting holidayLateNightAutoCalSetting,
			DeclareTimezoneResult declareResult) {

		if (!declareResult.getCalcRangeOfOneDay().isPresent()) return;
		CalculationRangeOfOneDay declareCalcRange = declareResult.getCalcRangeOfOneDay().get();
		if (!declareResult.getDeclareCalcRange().isPresent()) return;
		DeclareCalcRange calcRange = declareResult.getDeclareCalcRange().get();
		
		EachStatutoryHolidayWorkTime eachTime = new EachStatutoryHolidayWorkTime();
		// ????????????????????????
		if (calcRange.getDeclareSet().getFrameSet() == DeclareFrameSet.WORKTIME_SET){
			// ????????????????????????????????????????????????????????????
			OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
			if (declareOutsideWork.getHolidayWorkTimeSheet().isPresent()){
				HolidayWorkTimeSheet declareSheet = declareOutsideWork.getHolidayWorkTimeSheet().get();
				for(HolidayWorkFrameTimeSheetForCalc frameTime : declareSheet.getWorkHolidayTime()) {
					AttendanceTime declareTime = frameTime.calcMidNightTime(holidayLateNightAutoCalSetting).getCalcTime();
					if (declareTime.valueAsMinutes() > 0){
						eachTime.addTime(frameTime.getStatutoryAtr().get(), TimeDivergenceWithCalculation.sameTime(declareTime));
						// ?????????????????????????????????????????????????????????????????????
						calcRange.getEditState().getHolidayWorkMn().add(frameTime.getStatutoryAtr().get());
					}
				}
			}
		}
		else{
			// ?????????????????????????????????
			{
				// ?????????????????????
				if (calcRange.getWorkTypeOpt().isPresent()){
					// ???????????????????????????
					if (calcRange.getWorkTypeOpt().get().isHolidayWork()){
						// ?????????????????????????????????????????????????????????????????????
						AttendanceTime declareTime = calcRange.getCalcTime().getHolidayWorkMn();
						if (declareTime.valueAsMinutes() > 0){
							// ?????????????????????????????????
							OutsideWorkTimeSheet declareOutsideWork = declareCalcRange.getOutsideWorkTimeSheet().get();
							if (declareOutsideWork.getHolidayWorkTimeSheet().isPresent()){
								HolidayWorkTimeSheet declareSheet = declareOutsideWork.getHolidayWorkTimeSheet().get();
								if (declareSheet.getWorkHolidayTime().size() > 0){
									eachTime.addTime(
											declareSheet.getWorkHolidayTime().get(0).getStatutoryAtr().get(), TimeDivergenceWithCalculation.sameTime(declareTime));
									// ?????????????????????????????????????????????????????????????????????
									calcRange.getEditState().getHolidayWorkMn().add(
											declareSheet.getWorkHolidayTime().get(0).getStatutoryAtr().get());
								}
							}
						}
					}
				}
			}
		}
		// ???????????????????????????
		{
			for (HolidayWorkMidNightTime record : recordList){
				switch(record.getStatutoryAtr()){
				case WithinPrescribedHolidayWork:
					record.getTime().replaceTimeWithCalc(eachTime.getStatutory().getTime());
					break;
				case ExcessOfStatutoryHolidayWork:
					record.getTime().replaceTimeWithCalc(eachTime.getExcess().getTime());
					break;
				case PublicHolidayWork:
					record.getTime().replaceTimeWithCalc(eachTime.getPublicholiday().getTime());
					break;
				}
			}
		}
	}
	
	/**
	 * ???????????????????????????????????????
	 * @return???????????????
	 */
	public AttendanceTime calcTotalFrameTime() {
		int sumHdTime = this.holidayWorkFrameTime.stream().filter(x -> x.getHolidayWorkTime().isPresent())
				.collect(Collectors.summingInt(x -> x.getHolidayWorkTime().get().getTime().v()));
		return new AttendanceTime(sumHdTime);
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * @return???????????????
	 */
	public AttendanceTime calcTransTotalFrameTime() {
		int sumHdTranferTime = this.holidayWorkFrameTime.stream().filter(x -> x.getTransferTime().isPresent())
				.collect(Collectors.summingInt(x -> x.getTransferTime().get().getTime().v()));
		return new AttendanceTime(sumHdTranferTime);
	}
	
   //?????????????????????????????????????????????
	public AttendanceTime calcTotalAppTime() {
		int sumApp = this.holidayWorkFrameTime.stream().filter(x -> x.getBeforeApplicationTime().isPresent())
				.collect(Collectors.summingInt(x -> x.getBeforeApplicationTime().get().v()));
		return new AttendanceTime(sumApp);
	}
	
	/**
	 * ???????????? ????????????????????????
	 * @return???????????????????????????
	 */
	public List<EmployeeDailyPerError> checkHolidayWorkExcess(String employeeId,
															  GeneralDate targetDate,
															  AttendanceItemDictionaryForCalc attendanceItemDictionary,
															  ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		for(HolidayWorkFrameTime frameTime:this.getHolidayWorkFrameTime()) {
			if(frameTime.isOverLimitDivergenceTime()) {
				//????????????
				attendanceItemDictionary.findId("????????????"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
				);
//				//????????????
//				attendanceItemDictionary.findId("????????????"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
//						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
//				);
			}
		}
		return returnErrorList;
	}
	
	/**
	 * ?????????????????????????????????
	 * @return???????????????????????????
	 */
	public List<EmployeeDailyPerError> checkPreHolidayWorkExcess(String employeeId,
			  												  GeneralDate targetDate,
															  AttendanceItemDictionaryForCalc attendanceItemDictionary,
			  												  ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		for(HolidayWorkFrameTime frameTime:this.getHolidayWorkFrameTime()) {
			if(frameTime.isPreOverLimitDivergenceTime()) {
				//????????????
				attendanceItemDictionary.findId("????????????"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
				);
//				//????????????
//				attendanceItemDictionary.findId("????????????"+frameTime.getHolidayFrameNo().v()).ifPresent( itemId -> 
//						returnErrorList.add(new EmployeeDailyPerError(AppContexts.user().companyCode(), employeeId, targetDate, errorCode, itemId))
//				);
			}
		}
		return returnErrorList;
	}
	
	/**
	 *??????????????????????????????
	 * @return
	 */
	public List<EmployeeDailyPerError> checkNightTimeExcess(String employeeId,
														   GeneralDate targetDate,
														   AttendanceItemDictionaryForCalc attendanceItemDictionary,
														   ErrorAlarmWorkRecordCode errorCode) {
		List<EmployeeDailyPerError> returnErrorList = new ArrayList<>();
		if(this.getHolidayMidNightWork().isPresent()) {
			returnErrorList.addAll(this.getHolidayMidNightWork().get().getErrorList(employeeId, targetDate, attendanceItemDictionary, errorCode));
		}
		return returnErrorList;
	}
	
	/**
	 * ????????????????????????????????????????????????
	 * @return
	 */
	public HolidayWorkTimeOfDaily calcDiverGenceTime() {
		List<HolidayWorkFrameTime> list = new ArrayList<>();
		for(HolidayWorkFrameTime holidayworkFrameTime:this.holidayWorkFrameTime) {
			holidayworkFrameTime.calcDiverGenceTime();
			list.add(holidayworkFrameTime);
		}
		Finally<HolidayMidnightWork> holidayMidnight = this.holidayMidNightWork.isPresent()?Finally.of(this.holidayMidNightWork.get().calcDiverGenceTime()):this.holidayMidNightWork;
		return new HolidayWorkTimeOfDaily(this.holidayWorkFrameTimeSheet,list,holidayMidnight,this.holidayTimeSpentAtWork);
	}

	/**
	 * ??????????????????????????????0?????????
	 * @param holidayWorkFrameTimeList ????????????????????????
	 */
	public static void divergenceMinusValueToZero(
			List<HolidayWorkFrameTime> holidayWorkFrameTimeList){
		
		//????????????????????????
		if (AppContexts.optionLicense().customize().ootsuka() == false) return;
		
		//??????????????????????????????0?????????
		for (val holidayWorkFrameTime : holidayWorkFrameTimeList){
			holidayWorkFrameTime.getHolidayWorkTime().get().divergenceMinusValueToZero();
			holidayWorkFrameTime.getTransferTime().get().divergenceMinusValueToZero();
		}
	}
	
	//PC??????????????????????????????????????????????????????????????????(?????????????????????)
	public void setPCLogOnValue(Map<HolidayWorkFrameNo, HolidayWorkFrameTime> map) {
		Map<HolidayWorkFrameNo,HolidayWorkFrameTime> changeList = convertHolMap(this.getHolidayWorkFrameTime());
		
		for(int frameNo = 1 ; frameNo<=10 ; frameNo++) {
			//?????????
			if(changeList.containsKey(new HolidayWorkFrameNo(frameNo))) {
				val getframe = changeList.get(new HolidayWorkFrameNo(frameNo)); 
				if(map.containsKey(new HolidayWorkFrameNo(frameNo))) {
					//???????????????????????????
					getframe.getHolidayWorkTime().get().replaceTimeAndCalcDiv(map.get(new HolidayWorkFrameNo(frameNo)).getHolidayWorkTime().get().getCalcTime());
					//???????????????????????????
					getframe.getTransferTime().get().replaceTimeAndCalcDiv(map.get(new HolidayWorkFrameNo(frameNo)).getTransferTime().get().getCalcTime());
				}
				else {
					//???????????????????????????
					getframe.getHolidayWorkTime().get().replaceTimeAndCalcDiv(new AttendanceTime(0));
					//???????????????????????????
					getframe.getTransferTime().get().replaceTimeAndCalcDiv(new AttendanceTime(0));
				}
				changeList.remove(new HolidayWorkFrameNo(frameNo));
				changeList.put(new HolidayWorkFrameNo(frameNo), getframe);
			}
			//??????????????????
			else {
				if(map.containsKey(new HolidayWorkFrameNo(frameNo))) {
					changeList.put(new HolidayWorkFrameNo(frameNo),
							   	   new HolidayWorkFrameTime(new HolidayWorkFrameNo(frameNo),
							   			   				 Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),map.get(new HolidayWorkFrameNo(frameNo)).getHolidayWorkTime().get().getCalcTime())),
							   			   				 Finally.of(TimeDivergenceWithCalculation.createTimeWithCalculation(new AttendanceTime(0),map.get(new HolidayWorkFrameNo(frameNo)).getTransferTime().get().getCalcTime())),
							   			   				 Finally.of(new AttendanceTime(0))));
				}
			}
		}
		
		this.holidayWorkFrameTime = new ArrayList<>(changeList.values());
	}
	
	private Map<HolidayWorkFrameNo,HolidayWorkFrameTime> convertHolMap(List<HolidayWorkFrameTime> holidayWorkFrameTime) {
		Map<HolidayWorkFrameNo,HolidayWorkFrameTime> map= new HashMap<>();
		for(HolidayWorkFrameTime hol : holidayWorkFrameTime) {
			map.put(hol.getHolidayFrameNo(), hol);
		}
		return map;
	}
	
	//???????????????????????????????????????????????????
	public static HolidayWorkTimeOfDaily createDefaultBeforeApp(List<Integer> lstNo) {
		List<HolidayWorkFrameTime> workFrameTime = lstNo.stream().map(x -> {
			return new HolidayWorkFrameTime(new HolidayWorkFrameNo(x), Finally.of(TimeDivergenceWithCalculation.emptyTime()), Finally.of(TimeDivergenceWithCalculation.emptyTime()),
					Finally.of(new AttendanceTime(0)));
		}).collect(Collectors.toList());
		return new HolidayWorkTimeOfDaily(new ArrayList<>(), 
				workFrameTime, 
				Finally.of(new HolidayMidnightWork(new ArrayList<>())), 
				new AttendanceTime(0));
	}

	@Override
	public HolidayWorkTimeOfDaily clone() {

		// ??????????????????
		List<HolidayWorkFrameTimeSheet> holidayWorkFrameTimeSheetClone = this.holidayWorkFrameTimeSheet.stream().map(x -> {
			return x.clone();
		}).collect(Collectors.toList());

		// ???????????????
		List<HolidayWorkFrameTime> holidayWorkFrameTimeClone = this.holidayWorkFrameTime.stream().map(x -> {
			return x.clone();
		}).collect(Collectors.toList());

		// ????????????
		Finally<HolidayMidnightWork> holidayMidNightWorkClone = holidayMidNightWork.isPresent()
				? Finally.of(holidayMidNightWork.get().clone())
				: Finally.empty();

		// ??????????????????
		AttendanceTime holidayTimeSpentAtWorkClone = new AttendanceTime(this.holidayTimeSpentAtWork.v());

		return new HolidayWorkTimeOfDaily(holidayWorkFrameTimeSheetClone, holidayWorkFrameTimeClone,
				holidayMidNightWorkClone, holidayTimeSpentAtWorkClone);
	}
	
	// ????????????????????????????????????????????????????????????
	public boolean tranferHdWorkCompenCall(SubsTransferProcessMode processMode) {
		AttendanceTime sumHdTime = calcTotalFrameTime();
		AttendanceTime sumHdTranferTime = calcTransTotalFrameTime();
		AttendanceTime sumApp = calcTotalAppTime();
		if ((sumHdTime.valueAsMinutes() + sumHdTranferTime.valueAsMinutes()) <= 0
				&& processMode == SubsTransferProcessMode.DAILY && sumApp.valueAsMinutes() > 0) {
			return true;
		}
		return false;
	}
}
