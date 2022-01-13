package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemValueOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.export.attdstatus.AttendanceStatusList;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonthlyCalculatingDailys;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.WorkTypeDaysCountTable;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workamount.WorkAmountOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workclock.WorkClockOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.workdays.WorkDaysOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.WorkTimeOfMonthlyVT;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.shr.com.context.AppContexts;

/**
 * 期間別の縦計
 * @author shuichi_ishida
 */
@Getter
public class VerticalTotalOfMonthly implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;
	
	/** 勤務日数 */
	private WorkDaysOfMonthly workDays;
	/** 勤務時間 */
	private WorkTimeOfMonthlyVT workTime;
	/** 勤務時刻 */
	private WorkClockOfMonthly workClock;
	/** 勤務金額 */
	private WorkAmountOfMonthly workAmount;
	
	/**
	 * コンストラクタ
	 */
	public VerticalTotalOfMonthly(){
		
		this.workDays = new WorkDaysOfMonthly();
		this.workTime = new WorkTimeOfMonthlyVT();
		this.workClock = new WorkClockOfMonthly();
		this.workAmount = new WorkAmountOfMonthly();
	}
	
	/**
	 * ファクトリー
	 * @param workDays 勤務日数
	 * @param workTime 勤務時間
	 * @param workClock 勤務時刻
	 * @return 月別実績の縦計
	 */
	public static VerticalTotalOfMonthly of(
			WorkDaysOfMonthly workDays,
			WorkTimeOfMonthlyVT workTime,
			WorkClockOfMonthly workClock,
			WorkAmountOfMonthly workAmount){
		
		val domain = new VerticalTotalOfMonthly();
		domain.workDays = workDays;
		domain.workTime = workTime;
		domain.workClock = workClock;
		domain.workAmount = workAmount;
		return domain;
	}
	
	/**
	 * 縦計
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param datePeriod 期間
	 * @param workingSystem 労働制
	 * @param companySets 月別集計で必要な会社別設定
	 * @param employeeSets 月別集計で必要な社員別設定
	 * @param monthlyCalcDailys 月の計算中の日別実績データ
	 */
	public void verticalTotal(
			RequireM1 require,
			String companyId,
			String employeeId,
			DatePeriod datePeriod,
			WorkingSystem workingSystem,
			MonAggrCompanySettings companySets,
			MonAggrEmployeeSettings employeeSets,
			MonthlyCalculatingDailys monthlyCalcDailys){
		
		// 集計結果の初期化
		this.workTime = new WorkTimeOfMonthlyVT();
		this.workDays = new WorkDaysOfMonthly();
		this.workClock = new WorkClockOfMonthly();
		this.workAmount = new WorkAmountOfMonthly();
		
		// 日別実績の勤務情報　取得
		Map<GeneralDate, WorkInfoOfDailyAttendance> workInfoOfDailyMap =
				monthlyCalcDailys.getWorkInfoOfDailyMap();
		
		// 出勤状態を取得する
		AttendanceStatusList attendanceStatusList = new AttendanceStatusList(
				monthlyCalcDailys.getAttendanceTimeOfDailyMap(),
				monthlyCalcDailys.getTimeLeaveOfDailyMap());
		
		// 乖離フラグの集計
		this.workTime.aggregateDivergenceAtr(monthlyCalcDailys.getEmployeeDailyPerErrorList());
		
		// 任意項目を取得する
		Map<GeneralDate, AnyItemValueOfDailyAttd> anyItemValueOfDailyMap = new HashMap<>();
		for (val anyItem : monthlyCalcDailys.getAnyItemValueOfDailyList().entrySet()) {
			anyItemValueOfDailyMap.putIfAbsent(anyItem.getKey(), anyItem.getValue());
		}
		
		// 日ごとのループ
		GeneralDate procYmd = datePeriod.start();
		while (procYmd.beforeOrEquals(datePeriod.end())){
			
			// 勤務情報・勤務種類・日数カウント表・所定時間設定を確認する
			WorkInfoOfDailyAttendance workInfoOfDaily = workInfoOfDailyMap.get(procYmd);
			WorkType workType = null;
			WorkTypeDaysCountTable workTypeDaysCountTable = null;
			PredetemineTimeSetting predetermineTimeSet = null;
			PredetermineTimeSetForCalc predTimeSetForCalc = null;
			if (workInfoOfDaily != null){
				val recordWorkInfo = workInfoOfDaily.getRecordInfo();
				val workTypeCode = recordWorkInfo.getWorkTypeCode();
				val workTimeCode = recordWorkInfo.getWorkTimeCode();
				if (workTypeCode != null){
					workType = companySets.getWorkTypeMap(require,workTypeCode.v());
					if (workType != null){
						
						// 勤務種類を判断しカウント数を取得する
						workTypeDaysCountTable = new WorkTypeDaysCountTable(workType,
								companySets.getVacationAddSet(),
								Optional.of(companySets.getVerticalTotalMethod()));
					}
					else {
						workInfoOfDaily = null;
					}
				}
				else {
					workInfoOfDaily = null;
				}
				
				// 計算用所定時間設定を確認する
				if (workTimeCode != null){
					predetermineTimeSet = companySets.getPredetemineTimeSetMap(require, workTimeCode.v());
					if (predetermineTimeSet != null){
						predTimeSetForCalc = PredetermineTimeSetForCalc.convertMastarToCalc(predetermineTimeSet);
					}
				}
			}
			
			// 平日時就業時間帯を取得
//			PredetemineTimeSetting predTimeSetOnWeekday = null;
//			val workConditionItemOpt = employeeSets.getWorkingConditionItem(procYmd);
//			if (workConditionItemOpt.isPresent()) {
//				val workCategory = workConditionItemOpt.get().getWorkCategory();
//				if (workCategory != null) {
//					val weekdayTime = workCategory.getWorkTime().getWeekdayTime();
//					if (weekdayTime != null) {
//						if (weekdayTime.getWorkTimeCode().isPresent()) {
//							predTimeSetOnWeekday = companySets.getPredetemineTimeSetMap(
//									require, weekdayTime.getWorkTimeCode().get().v());
//						}
//					}
//				}
//			}
			
			// 必要なマスタがない時、その日をスキップする
			boolean isSkip = false;
			if (workType == null) isSkip = true;
			if (isSkip){
				procYmd = procYmd.addDays(1);
				continue;
			}
			
			// 勤怠時間を確認する
			val attendanceTimeOfDaily = monthlyCalcDailys.getAttendanceTimeOfDailyMap().get(procYmd);
			
			// 出退勤を確認する
			val timeLeavingOfDaily = monthlyCalcDailys.getTimeLeaveOfDailyMap().get(procYmd);
			
			// 特定日区分を確認する
			val specificDateAttrOfDaily = monthlyCalcDailys.getSpecificDateAttrOfDailyMap().get(procYmd);
			
			// 出勤状態・2回目打刻有無を確認する
			val isAttendanceDay = attendanceStatusList.isAttendanceDay(procYmd);
			val isTwoTimesStampExists = attendanceStatusList.isTwoTimesStampExists(procYmd);
			
			// PCログオン情報　取得
			val pcLogonInfoOpt = Optional.ofNullable(monthlyCalcDailys.getPcLogonInfoMap().get(procYmd));
			
			// 任意項目を確認する
			Optional<AnyItemValueOfDailyAttd> anyItemValueOpt = Optional.ofNullable(anyItemValueOfDailyMap.get(procYmd));
		
			// 勤務日数集計
			this.workDays.aggregate(require, employeeId, companyId, procYmd, workingSystem, workType, attendanceTimeOfDaily,
					specificDateAttrOfDaily, workTypeDaysCountTable, workInfoOfDaily, 
					predetermineTimeSet, isAttendanceDay, isTwoTimesStampExists);
			
			// 勤務時間集計
			this.workTime.aggregate(require, employeeId, procYmd, workType, attendanceTimeOfDaily, companyId);
			
			if (AppContexts.optionLicense().customize().ootsuka()) {
				// 勤務時刻集計
				this.workClock.aggregate(workType, pcLogonInfoOpt, attendanceTimeOfDaily, timeLeavingOfDaily,
						predTimeSetForCalc, anyItemValueOpt);
			}
			
			/** 就業時間金額の集計 */
			this.workAmount.aggregate(attendanceTimeOfDaily);
			
			procYmd = procYmd.addDays(1);
		}
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(VerticalTotalOfMonthly target){

		this.workDays.sum(target.workDays);
		this.workTime.sum(target.workTime);
		this.workClock.sum(target.workClock);
		this.workAmount.sum(target.workAmount);
	}
	
	public static interface RequireM1 extends MonAggrCompanySettings.RequireM4, 
												MonAggrCompanySettings.RequireM2, 
												WorkDaysOfMonthly.RequireM1,
												WorkTimeOfMonthlyVT.RequireM1 {

	}
}