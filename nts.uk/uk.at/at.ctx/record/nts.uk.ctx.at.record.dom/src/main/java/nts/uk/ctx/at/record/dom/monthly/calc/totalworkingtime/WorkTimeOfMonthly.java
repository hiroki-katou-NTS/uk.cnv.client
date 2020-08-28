package nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.byperiod.FlexTimeByPeriod;
import nts.uk.ctx.at.record.dom.monthly.calc.actualworkingtime.RegularAndIrregularTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.calc.flex.FlexTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime.hdwkandcompleave.HolidayWorkTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime.overtime.OverTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.premiumtarget.getvacationaddtime.AddSet;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.timeseries.WorkTimeOfTimeSeries;
import nts.uk.ctx.at.record.dom.weekly.RegAndIrgTimeOfWeekly;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculation;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.other.WithinStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailycalprocess.calculation.other.WithinStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 月別実績の就業時間
 * @author shuichi_ishida
 */
@Getter
public class WorkTimeOfMonthly implements Cloneable, Serializable {

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** 就業時間 */
	@Setter
	private AttendanceTimeMonth workTime;
	/** 所定内割増時間 */
	@Setter
	private AttendanceTimeMonth withinPrescribedPremiumTime;
	/** 実働就業時間 */
	@Setter
	private AttendanceTimeMonth actualWorkTime;
	
	/** 時系列ワーク */
	private Map<GeneralDate, WorkTimeOfTimeSeries> timeSeriesWorks;
	
	/**
	 * コンストラクタ
	 */
	public WorkTimeOfMonthly(){
		
		this.workTime = new AttendanceTimeMonth(0);
		this.withinPrescribedPremiumTime = new AttendanceTimeMonth(0);
		this.actualWorkTime = new AttendanceTimeMonth(0);
		this.timeSeriesWorks = new HashMap<>();
	}

	/**
	 * ファクトリー
	 * @param workTime 就業時間
	 * @param withinPrescribedPremiumTime 所定内割増時間
	 * @param actualWorkTime 実働就業時間
	 * @return 月別実績の就業時間
	 */
	public static WorkTimeOfMonthly of(
			AttendanceTimeMonth workTime,
			AttendanceTimeMonth withinPrescribedPremiumTime,
			AttendanceTimeMonth actualWorkTime){
		
		val domain = new WorkTimeOfMonthly();
		domain.workTime = workTime;
		domain.withinPrescribedPremiumTime = withinPrescribedPremiumTime;
		domain.actualWorkTime = actualWorkTime;
		return domain;
	}
	
	@Override
	public WorkTimeOfMonthly clone() {
		WorkTimeOfMonthly cloned = new WorkTimeOfMonthly();
		try {
			cloned.workTime = new AttendanceTimeMonth(this.workTime.v());
			cloned.withinPrescribedPremiumTime = new AttendanceTimeMonth(this.withinPrescribedPremiumTime.v());
			cloned.actualWorkTime = new AttendanceTimeMonth(this.actualWorkTime.v());
			// ※　Shallow Copy.
			cloned.timeSeriesWorks = this.timeSeriesWorks;
		}
		catch (Exception e){
			throw new RuntimeException("WorkTimeOfMonthly clone error.");
		}
		return cloned;
	}
	
	/**
	 * 就業時間を確認する
	 * @param datePeriod 期間
	 * @param attendanceTimeOfDailyMap 日別実績の勤怠時間リスト
	 * @param workInformationOfDailyMap 日別実績の勤務情報リスト
	 * @param companySets 月別集計で必要な会社別設定
	 */
	public void confirm(RequireM1 require, DatePeriod datePeriod,
			Map<GeneralDate, AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyMap,
			Map<GeneralDate, WorkInfoOfDailyPerformance> workInformationOfDailyMap,
			MonAggrCompanySettings companySets){
		
		for (val attendanceTimeOfDaily : attendanceTimeOfDailyMap.values()) {
			val ymd = attendanceTimeOfDaily.getYmd();
			
			// 期間外はスキップする
			if (!datePeriod.contains(ymd)) continue;
			
			// ドメインモデル「日別実績の所定内時間」を取得する
			val actualWorkingTimeOfDaily = attendanceTimeOfDaily.getTime().getActualWorkingTimeOfDaily();
			val totalWorkingTime = actualWorkingTimeOfDaily.getTotalWorkingTime();
			WithinStatutoryTimeOfDaily withinPrescribedTimeOfDaily = totalWorkingTime.getWithinStatutoryTimeOfDaily();
			if (withinPrescribedTimeOfDaily == null){
				withinPrescribedTimeOfDaily = WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(
						new AttendanceTime(0),
						new AttendanceTime(0),
						new AttendanceTime(0),
						new WithinStatutoryMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))),
						new AttendanceTime(0));
			}
	
			// 取得した就業時間・所定内割増時間を確認する
			AttendanceTime workTime = new AttendanceTime(withinPrescribedTimeOfDaily.getWorkTime().v());
			val withinPrescribedPremiumTime = withinPrescribedTimeOfDaily.getWithinPrescribedPremiumTime();
			
			// ドメインモデル「日別実績の残業時間」を取得する
			val illegalTimeOfDaily = totalWorkingTime.getExcessOfStatutoryTimeOfDaily();
			if (illegalTimeOfDaily.getOverTimeWork().isPresent()){
				val overTimeOfDaily = illegalTimeOfDaily.getOverTimeWork().get();
				
				// 変形法定内残業を就業時間に加算
				workTime = workTime.addMinutes(overTimeOfDaily.getIrregularWithinPrescribedOverTimeWork().v());
			}
			
			// 「日別実績の総労働時間．休暇加算時間」を取得する
			val vacationAddTime = totalWorkingTime.getVacationAddTime();
			
			// 勤務種類を確認する
			WorkType workType = null;
			if (workInformationOfDailyMap.containsKey(ymd)) {
				if (workInformationOfDailyMap.get(ymd).getWorkInformation().getRecordInfo() != null) {
					val record = workInformationOfDailyMap.get(ymd).getWorkInformation().getRecordInfo();
					if (record.getWorkTypeCode() != null) {
						String workTypeCode = record.getWorkTypeCode().v();
						workType = companySets.getWorkTypeMap(require, workTypeCode);
					}
				}
			}
	
			// 時系列ワークに追加
			val workTimeOfTimeSeries = WorkTimeOfTimeSeries.of(ymd,
					WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(
							workTime,
							withinPrescribedTimeOfDaily.getActualWorkTime(),
							withinPrescribedPremiumTime,
							withinPrescribedTimeOfDaily.getWithinStatutoryMidNightTime(),
							withinPrescribedTimeOfDaily.getVacationAddTime()),
					vacationAddTime,
					workType
					);
			this.timeSeriesWorks.putIfAbsent(ymd, workTimeOfTimeSeries);
		}
	}
	
	/**
	 * 時系列合計法定内時間を取得する
	 * @param datePeriod 期間
	 * @return 時系列合計法定内時間
	 */
	public AttendanceTimeMonth getTimeSeriesTotalLegalTime(DatePeriod datePeriod){
		
		AttendanceTimeMonth returnTime = new AttendanceTimeMonth(0);
		for (val timeSeriesWork : this.timeSeriesWorks.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			returnTime = returnTime.addMinutes(timeSeriesWork.getLegalTime().getWorkTime().v());
		}
		return returnTime;
	}
	
	/**
	 * 時系列合計法定内実働時間を取得する
	 * @param datePeriod 期間
	 * @return 時系列合計法定内実働時間
	 */
	public AttendanceTimeMonth getTimeSeriesTotalLegalActualTime(DatePeriod datePeriod){
		
		AttendanceTimeMonth returnTime = new AttendanceTimeMonth(0);
		for (val timeSeriesWork : this.timeSeriesWorks.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			returnTime = returnTime.addMinutes(timeSeriesWork.getLegalTime().getActualWorkTime().v());
		}
		return returnTime;
	}
	
	/**
	 * 集計対象時間を取得
	 * @param datePeriod 期間
	 * @param addSet 加算設定
	 * @return 集計対象時間
	 */
	public AttendanceTimeMonth getAggregateTargetTime(DatePeriod datePeriod, AddSet addSet){
		
		AttendanceTimeMonth result = new AttendanceTimeMonth(0);
		for (val timeSeriesWork : this.timeSeriesWorks.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			result = result.addMinutes(timeSeriesWork.getAggregateTargetTime(addSet).v());
		}
		return result;
	}
	
	/**
	 * 就業時間を集計する
	 * @param datePeriod 期間
	 * @param workingSystem 労働制
	 * @param actualWorkingTime 実働時間
	 * @param flexTime フレックス時間
	 * @param overTime 残業時間
	 * @param holidayWorkTime 休出時間
	 */
	public void aggregate(
			DatePeriod datePeriod,
			WorkingSystem workingSystem,
			RegularAndIrregularTimeOfMonthly actualWorkingTime,
			FlexTimeOfMonthly flexTime,
			OverTimeOfMonthly overTime,
			HolidayWorkTimeOfMonthly holidayWorkTime){

		// 就業時間の合計処理
		this.totalizeWorkTime(datePeriod);
		
		// 就業時間に法定内残業時間を加算する
		this.workTime = this.workTime.addMinutes(overTime.getLegalOverTime(datePeriod).v());
		
		// 就業時間に法定内休出時間を加算する
		this.workTime = this.workTime.addMinutes(holidayWorkTime.getLegalHolidayWorkTime(datePeriod).v());
		
		// 就業時間に「加算した休暇使用時間」を加算する
		// Redmine#102512
//		this.workTime = this.workTime.addMinutes(
//				actualWorkingTime.getAddedVacationUseTime().getAddTimePerMonth().v());
//		this.workTime = this.workTime.addMinutes(
//				flexTime.getAddedVacationUseTime().getAddTimePerMonth().v());
		
		// 就業時間から週割増合計時間・月割増合計時間を引く
		this.workTime = this.workTime.minusMinutes(actualWorkingTime.getWeeklyTotalPremiumTime().v());
		this.workTime = this.workTime.minusMinutes(actualWorkingTime.getMonthlyTotalPremiumTime().v());
	}
	
	/**
	 * 就業時間を集計する　（週別集計用）
	 * @param datePeriod 期間
	 * @param workingSystem 労働制
	 * @param actualWorkingTime 実働時間
	 * @param flexTime フレックス時間
	 * @param overTime 残業時間
	 * @param holidayWorkTime 休出時間
	 */
	public void aggregateForWeek(
			DatePeriod datePeriod,
			WorkingSystem workingSystem,
			RegAndIrgTimeOfWeekly actualWorkingTime,
			FlexTimeByPeriod flexTime,
			OverTimeOfMonthly overTime,
			HolidayWorkTimeOfMonthly holidayWorkTime){

		// 就業時間の合計処理
		this.totalizeWorkTime(datePeriod);
		
		// 就業時間に法定内残業時間を加算する
		this.workTime = this.workTime.addMinutes(overTime.getLegalOverTime(datePeriod).v());
		
		// 就業時間に法定内休出時間を加算する
		this.workTime = this.workTime.addMinutes(holidayWorkTime.getLegalHolidayWorkTime(datePeriod).v());
		
		// 就業時間から週割増合計時間を引く
		this.workTime = this.workTime.minusMinutes(actualWorkingTime.getWeeklyTotalPremiumTime().v());
	}
	
	/**
	 * 就業時間を集計する　（任意期間別集計用）
	 * @param datePeriod 期間
	 * @param attendanceTimeOfDailyMap 日別実績の勤怠時間リスト
	 * @param workInfoOfDailyMap 日別実績の勤務情報リスト
	 * @param companySets 月別集計で必要な会社別設定
	 */
	public void aggregateForByPeriod(RequireM1 require, DatePeriod datePeriod,
			Map<GeneralDate, AttendanceTimeOfDailyPerformance> attendanceTimeOfDailyMap,
			Map<GeneralDate, WorkInfoOfDailyPerformance> workInfoOfDailyMap,
			MonAggrCompanySettings companySets){
		
		for (val attendanceTimeOfDaily : attendanceTimeOfDailyMap.values()) {
			val ymd = attendanceTimeOfDaily.getYmd();
			
			// 期間外はスキップする
			if (!datePeriod.contains(ymd)) continue;
			
			// ドメインモデル「日別実績の所定内時間」を取得する
			val actualWorkingTimeOfDaily = attendanceTimeOfDaily.getTime().getActualWorkingTimeOfDaily();
			val totalWorkingTime = actualWorkingTimeOfDaily.getTotalWorkingTime();
			WithinStatutoryTimeOfDaily withinPrescribedTimeOfDaily = totalWorkingTime.getWithinStatutoryTimeOfDaily();
			if (withinPrescribedTimeOfDaily == null){
				withinPrescribedTimeOfDaily = WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(
						new AttendanceTime(0),
						new AttendanceTime(0),
						new AttendanceTime(0),
						new WithinStatutoryMidNightTime(TimeDivergenceWithCalculation.sameTime(new AttendanceTime(0))),
						new AttendanceTime(0));
			}
			
			// 勤務種類を確認する
			WorkType workType = null;
			if (workInfoOfDailyMap.containsKey(ymd)) {
				if (workInfoOfDailyMap.get(ymd).getWorkInformation().getRecordInfo() != null) {
					val record = workInfoOfDailyMap.get(ymd).getWorkInformation().getRecordInfo();
					if (record.getWorkTypeCode() != null) {
						String workTypeCode = record.getWorkTypeCode().v();
						workType = companySets.getWorkTypeMap(require, workTypeCode);
					}
				}
			}
	
			// 時系列ワークに追加
			val workTimeOfTimeSeries = WorkTimeOfTimeSeries.of(ymd,
					WithinStatutoryTimeOfDaily.createWithinStatutoryTimeOfDaily(
							withinPrescribedTimeOfDaily.getWorkTime(),
							withinPrescribedTimeOfDaily.getActualWorkTime(),
							withinPrescribedTimeOfDaily.getWithinPrescribedPremiumTime(),
							withinPrescribedTimeOfDaily.getWithinStatutoryMidNightTime(),
							withinPrescribedTimeOfDaily.getVacationAddTime()),
					new AttendanceTime(0),
					workType);
			this.timeSeriesWorks.putIfAbsent(ymd, workTimeOfTimeSeries);
		}
	}
	
	/**
	 * 就業時間の合計処理
	 * @param datePeriod 期間
	 */
	public void totalizeWorkTime(DatePeriod datePeriod){
		
		this.workTime = new AttendanceTimeMonth(0);
		this.withinPrescribedPremiumTime = new AttendanceTimeMonth(0);
		this.actualWorkTime = new AttendanceTimeMonth(0);
		for (val timeSeriesWork : this.timeSeriesWorks.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			val legalTime = timeSeriesWork.getLegalTime();
			this.workTime = this.workTime.addMinutes(legalTime.getWorkTime().v());
			this.withinPrescribedPremiumTime = this.withinPrescribedPremiumTime.addMinutes(
					legalTime.getWithinPrescribedPremiumTime().v());
			this.actualWorkTime = this.actualWorkTime.addMinutes(
					legalTime.getActualWorkTime().v());
		}
	}
	
	/**
	 * 総労働対象時間の取得
	 * @return 総労働対象時間
	 */
	public AttendanceTimeMonth getTotalWorkingTargetTime(){
		
		return new AttendanceTimeMonth(this.workTime.v() + this.withinPrescribedPremiumTime.v());
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(WorkTimeOfMonthly target){
		
		this.workTime = this.workTime.addMinutes(target.workTime.v());
		this.withinPrescribedPremiumTime = this.withinPrescribedPremiumTime.addMinutes(
				target.withinPrescribedPremiumTime.v());
		this.actualWorkTime = this.actualWorkTime.addMinutes(target.actualWorkTime.v());
	}
	
	public static interface RequireM1 extends MonAggrCompanySettings.RequireM4 {

	}
}