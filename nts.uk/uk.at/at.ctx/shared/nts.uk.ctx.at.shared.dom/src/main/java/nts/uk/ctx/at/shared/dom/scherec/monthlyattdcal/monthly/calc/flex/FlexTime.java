package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.flex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeDivergenceWithCalculationMinusExist;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.AttendanceTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.timeseries.FlexTimeOfTimeSeries;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.TimeMonthWithCalculationAndMinus;

/**
 * フレックス時間
 * @author shuichi_ishida
 */
@Getter
public class FlexTime implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;

	/** フレックス時間 */
	@Setter
	private TimeMonthWithCalculationAndMinus flexTime;
	/** 事前フレックス時間 */
	@Setter
	private AttendanceTimeMonth beforeFlexTime;
	/** 法定内フレックス時間 */
	@Setter
	private AttendanceTimeMonthWithMinus legalFlexTime;
	/** 法定外フレックス時間 */
	@Setter
	private AttendanceTimeMonthWithMinus illegalFlexTime;
	/** 当月フレックス時間 */
	@Setter
	private FlexTimeCurrentMonth flexTimeCurrentMonth;
	
	/** 時系列ワーク */
	private Map<GeneralDate, FlexTimeOfTimeSeries> timeSeriesWorks;
	
	/**
	 * コンストラクタ
	 */
	public FlexTime(){
		
		this.flexTime = TimeMonthWithCalculationAndMinus.ofSameTime(0);
		this.beforeFlexTime = new AttendanceTimeMonth(0);
		this.legalFlexTime = new AttendanceTimeMonthWithMinus(0);
		this.illegalFlexTime = new AttendanceTimeMonthWithMinus(0);
		this.flexTimeCurrentMonth = new FlexTimeCurrentMonth();
		this.timeSeriesWorks = new HashMap<>();
	}

	/**
	 * ファクトリー
	 * @param flexTime フレックス時間
	 * @param beforeFlexTime 事前フレックス時間
	 * @param legalFlexTime 法定内フレックス時間
	 * @param illegalFlexTime 法定外フレックス時間
	 * @param flexTimeCurrentMonth 当月フレックス時間
	 * @return フレックス時間
	 */
	public static FlexTime of(
			TimeMonthWithCalculationAndMinus flexTime,
			AttendanceTimeMonth beforeFlexTime,
			AttendanceTimeMonthWithMinus legalFlexTime,
			AttendanceTimeMonthWithMinus illegalFlexTime,
			FlexTimeCurrentMonth flexTimeCurrentMonth){

		val domain = new FlexTime();
		domain.flexTime = flexTime;
		domain.beforeFlexTime = beforeFlexTime;
		domain.legalFlexTime = legalFlexTime;
		domain.illegalFlexTime = illegalFlexTime;
		domain.flexTimeCurrentMonth = flexTimeCurrentMonth;
		return domain;
	}
	
	/**
	 * フレックス時間（時系列ワーク）に加算する
	 * @param ymd 年月日
	 * @param timeAsMinutes フレックス時間（分）
	 * @param calcTimeAsMinutes 計算フレックス時間（分）
	 * @param beforeApplicationTimeAsMinutes 事前申請時間（分）
	 */
	private void addFlexTimeInTimeSeriesWork(GeneralDate ymd,
			Integer timeAsMinutes, Integer calcTimeAsMinutes, Integer beforeApplicationTimeAsMinutes){
		
		this.timeSeriesWorks.putIfAbsent(ymd, new FlexTimeOfTimeSeries(ymd));
		val targetFlexTime = this.timeSeriesWorks.get(ymd);
		val flexTimeSrc = targetFlexTime.getFlexTime();
		targetFlexTime.setFlexTime(
				new nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime(
						TimeDivergenceWithCalculationMinusExist.createTimeWithCalculation(
								flexTimeSrc.getFlexTime().getTime().addMinutes(timeAsMinutes),
								flexTimeSrc.getFlexTime().getCalcTime().addMinutes(calcTimeAsMinutes)),
						flexTimeSrc.getBeforeApplicationTime().addMinutes(beforeApplicationTimeAsMinutes)
				));
	}
	
	/**
	 * 集計する
	 * @param attendanceTimeOfDaily 日別実績の勤怠時間
	 */
	public void aggregate(GeneralDate ymd, AttendanceTimeOfDailyAttendance attendanceTimeOfDaily){

		// 「フレックス時間」を取得する
		val actualWorkingTimeOfDaily = attendanceTimeOfDaily.getActualWorkingTimeOfDaily();
		val totalWorkingTime = actualWorkingTimeOfDaily.getTotalWorkingTime();
		val excessPrescribedTimeOfDaily = totalWorkingTime.getExcessOfStatutoryTimeOfDaily();
		val overTimeOfDaily = excessPrescribedTimeOfDaily.getOverTimeWork();
		// 残業時間がない時、集計しない
		if (!overTimeOfDaily.isPresent()) return;
		val flexTimeSrc = overTimeOfDaily.get().getFlexTime();
		
		// 取得したフレックス時間を「フレックス時間」に加算する
		this.addFlexTimeInTimeSeriesWork(ymd, flexTimeSrc.getFlexTime().getTime().v(),
				flexTimeSrc.getFlexTime().getCalcTime().v(),
				flexTimeSrc.getBeforeApplicationTime().v());
	}
	
	/**
	 * 残業枠時間をフレックス時間に入れる
	 * @param ymd 年月日
	 * @param overTimeFrameTime 残業枠時間
	 */
	public void addOverTimeFrameTime(GeneralDate ymd, OverTimeFrameTime overTimeFrameTime){
		
		this.addFlexTimeInTimeSeriesWork(ymd,
				overTimeFrameTime.getOverTimeWork().getTime().v() +
				overTimeFrameTime.getTransferTime().getTime().v(),
				overTimeFrameTime.getOverTimeWork().getCalcTime().v() +
				overTimeFrameTime.getTransferTime().getCalcTime().v(),
				overTimeFrameTime.getBeforeApplicationTime().v());
	}
	
	/**
	 * 休出枠時間をフレックス時間に入れる
	 * @param ymd 年月日
	 * @param holidayWorkFrameTime 休出枠時間
	 */
	public void addHolidayWorkTimeFrameTime(GeneralDate ymd, HolidayWorkFrameTime holidayWorkFrameTime){
		
		this.addFlexTimeInTimeSeriesWork(ymd,
				holidayWorkFrameTime.getHolidayWorkTime().get().getTime().v() +
				holidayWorkFrameTime.getTransferTime().get().getTime().v(),
				holidayWorkFrameTime.getHolidayWorkTime().get().getCalcTime().v() +
				holidayWorkFrameTime.getTransferTime().get().getCalcTime().v(),
				holidayWorkFrameTime.getBeforeApplicationTime().get().v());
	}
	
	/**
	 * 時系列合計フレックス時間を取得する
	 * @param datePeriod 期間
	 * @param exceptMinus マイナス値を除く
	 * @return 時系列合計フレックス時間
	 */
	public AttendanceTimeMonthWithMinus getTimeSeriesTotalFlexTime(DatePeriod datePeriod, boolean exceptMinus){
		
		int returnMinutes = 0;
		for (val timeSeriesWork : this.timeSeriesWorks.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			val flexMinutes = timeSeriesWork.getFlexTime().getFlexTime().getTime().v();
			if (exceptMinus && flexMinutes <= 0) continue;
			returnMinutes += flexMinutes;
		}
		return new AttendanceTimeMonthWithMinus(returnMinutes);
	}
	
	/**
	 * 時系列合計計算フレックス時間を取得する
	 * @param datePeriod 期間
	 * @param exceptMinus マイナス値を除く
	 * @return 時系列合計計算フレックス時間
	 */
	public AttendanceTimeMonthWithMinus getTimeSeriesTotalCalcFlexTime(DatePeriod datePeriod, boolean exceptMinus){
		
		int returnMinutes = 0;
		for (val timeSeriesWork : this.timeSeriesWorks.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			val calcFlexMinutes = timeSeriesWork.getFlexTime().getFlexTime().getCalcTime().v();
			if (exceptMinus && calcFlexMinutes <= 0) continue;
			returnMinutes += calcFlexMinutes;
		}
		return new AttendanceTimeMonthWithMinus(returnMinutes);
	}
	
	/**
	 * 合算する
	 * @param target 加算対象
	 */
	public void sum(FlexTime target){
		
		this.flexTime = this.flexTime.addMinutes(
				target.flexTime.getTime().v(), target.flexTime.getCalcTime().v());
		this.beforeFlexTime = this.beforeFlexTime.addMinutes(target.beforeFlexTime.v());
		this.legalFlexTime = this.legalFlexTime.addMinutes(target.legalFlexTime.v());
		this.illegalFlexTime = this.illegalFlexTime.addMinutes(target.illegalFlexTime.v());
		this.flexTimeCurrentMonth.sum(target.flexTimeCurrentMonth);
	}
	
	/** 日単位のフレックス超過時間を合計する */
	public AttendanceTimeMonth getPlusFlexTime () {
		
		return new AttendanceTimeMonth(this.timeSeriesWorks.entrySet()
						.stream().map(c -> c.getValue())
						.mapToInt(c -> c.getFlexTime().getFlexOverTime().v())
						.sum());
	}
}
