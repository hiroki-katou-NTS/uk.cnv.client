package nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.excessoutside;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.monthly.calc.actualworkingtime.RegularAndIrregularTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.calc.flex.FlexTimeOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime.AggregateTotalWorkingTime;
import nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime.hdwkandcompleave.AggregateHolidayWorkTime;
import nts.uk.ctx.at.record.dom.monthly.calc.totalworkingtime.overtime.AggregateOverTime;
import nts.uk.ctx.at.record.dom.monthly.roundingset.RoundingSetOfMonthly;
import nts.uk.ctx.at.record.dom.monthlyaggrmethod.flex.AggrSettingMonthlyOfFlx;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.timeseries.FlexTimeOfTimeSeries;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.timeseries.MonthlyPremiumTimeOfTimeSeries;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.timeseries.WeeklyPremiumTimeOfTimeSeries;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.work.timeseries.WorkTimeOfTimeSeries;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonthWithMinus;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * 時間外超過明細
 * @author shuichu_ishida
 */
@Getter
public class ExcessOutsideWorkDetail {

	/** 就業時間 */
	private Map<GeneralDate, WorkTimeOfTimeSeries> workTime;
	/** 残業時間 */
	private Map<OverTimeFrameNo, AggregateOverTime> overTime;
	/** 休出時間 */
	private Map<HolidayWorkFrameNo, AggregateHolidayWorkTime> holidayWorkTime;
	/** フレックス超過時間 */
	private Map<GeneralDate, FlexTimeOfTimeSeries> flexExcessTime;
	/** 週割増時間 */
	private Map<GeneralDate, WeeklyPremiumTimeOfTimeSeries> weeklyPremiumTime;
	/** 月割増時間 */
	private Map<GeneralDate, MonthlyPremiumTimeOfTimeSeries> monthlyPremiumTime;
	/** 丸め後合計時間 */
	private TotalTime totalTimeAfterRound;
	
	/**
	 * コンストラクタ
	 */
	public ExcessOutsideWorkDetail(){

		this.workTime = new HashMap<>();
		this.overTime = new HashMap<>();
		this.holidayWorkTime = new HashMap<>();
		this.flexExcessTime = new HashMap<>();
		this.weeklyPremiumTime = new HashMap<>();
		this.monthlyPremiumTime = new HashMap<>();
		this.totalTimeAfterRound = new TotalTime();
	}
	
	/**
	 * フレックス超過時間合計を取得する
	 * @param datePeriod 期間
	 * @return フレックス超過時間合計
	 */
	public AttendanceTimeMonthWithMinus getTotalFlexExcessTime(DatePeriod datePeriod){
		
		AttendanceTimeMonthWithMinus totalTime = new AttendanceTimeMonthWithMinus(0);
		for (val timeSeriesWork : this.flexExcessTime.values()){
			if (!datePeriod.contains(timeSeriesWork.getYmd())) continue;
			val flexTime = timeSeriesWork.getFlexTime().getFlexTime().getTime();
			totalTime = totalTime.addMinutes(flexTime.v());
		}
		return totalTime;
	}
	
	/**
	 * 丸め後合計時間に移送する
	 * @param aggregateTotalWorkingTime 集計総労働時間
	 * @param regAndIrgTimeOfMonthly 月別実績の通常変形時間
	 * @param flexTimeOfMonthly 月別実績のフレックス時間
	 * @param aggrSetOfFlex フレックス時間勤務の月の集計設定
	 * @param roundingSet 月別実績の丸め設定
	 */
	public void setTotalTimeAfterRound(
			AggregateTotalWorkingTime aggregateTotalWorkingTime,
			RegularAndIrregularTimeOfMonthly regAndIrgTimeOfMonthly,
			FlexTimeOfMonthly flexTimeOfMonthly,
			AggrSettingMonthlyOfFlx aggrSetOfFlex,
			RoundingSetOfMonthly roundingSet){
		
		// 丸め前合計時間にコピーする
		TotalTimeBeforeRound totalTimeBeforeRound = new TotalTimeBeforeRound();
		totalTimeBeforeRound.copyValues(aggregateTotalWorkingTime,
				regAndIrgTimeOfMonthly, flexTimeOfMonthly, aggrSetOfFlex);
		
		// 各合計時間を丸める
		this.totalTimeAfterRound.setTotalTimeAfterRound(totalTimeBeforeRound, roundingSet);
	}
}
