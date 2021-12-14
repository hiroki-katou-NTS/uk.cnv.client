package nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.gul.util.value.Finally;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUnUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffDayTimeUse;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainCarryForward;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.DayOffRemainDayAndTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.DayOffError;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.RemainUnDigestedDayTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantDay;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.MonthVacationGrantTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.daynumber.ReserveLeaveRemainingDayNumber;

/**
 * @author ThanhNX
 *
 *         代休の集計結果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubstituteHolidayAggrResult {

	// 休出代休明細
	private VacationDetails vacationDetails;

	//代休残数
	private DayOffRemainDayAndTimes remain;
	// 残日数
	public LeaveRemainingDayNumber getRemainDay() {
		return remain.getDay();
	}
	// 残時間
	public RemainingMinutes getRemainTime() {
		return remain.getTime().map(x -> new RemainingMinutes(x.v())).orElse(new RemainingMinutes(0));
	}

	//代休使用数
	private DayOffDayTimeUse use;
	// 使用日数
	public ReserveLeaveRemainingDayNumber getDayUse() {
		return new ReserveLeaveRemainingDayNumber(use.getDay().v());
	}
	// 使用時間
	public RemainingMinutes getTimeUse() {
		return use.getTime().map(x -> new RemainingMinutes(x.v())).orElse(new RemainingMinutes(0));
	}

	//代休発生数
	private DayOffDayAndTimes oocr;
	// 発生日数
	public ReserveLeaveRemainingDayNumber getOccurrenceDay() {
		return new ReserveLeaveRemainingDayNumber(oocr.getDay().v());
	}
	// 発生時間
	public RemainingMinutes getOccurrenceTime() {
		return oocr.getTime().map(x -> new RemainingMinutes(x.v())).orElse(new RemainingMinutes(0));
	}

	//代休繰越数
	private DayOffRemainCarryForward carryForward;
	// 繰越日数
	public ReserveLeaveRemainingDayNumber getCarryoverDay() {
		return new ReserveLeaveRemainingDayNumber(carryForward.getDay().v());
	}
	// 繰越時間
	public RemainingMinutes getCarryoverTime() {
		return carryForward.getTime().map(x -> new RemainingMinutes(x.v())).orElse(new RemainingMinutes(0));
	}

	//代休未消化数
	private DayOffDayTimeUnUse unUsed;
	// 未消化日数
	public ReserveLeaveRemainingDayNumber getUnusedDay() {
		return new ReserveLeaveRemainingDayNumber(unUsed.getDay().v());
	}
	// 未消化時間
	public RemainingMinutes getUnusedTime() {
		return unUsed.getTime().map(x -> new RemainingMinutes(x.v())).orElse(new RemainingMinutes(0));
	}

	// エラー情報
	private List<DayOffError> dayOffErrors;

	// 前回集計期間の翌日
	private Finally<GeneralDate> nextDay;

	// 逐次休暇の紐付け情報
	private List<SeqVacationAssociationInfo> lstSeqVacation;

	public void setCalcNumberOccurUses(RemainUnDigestedDayTimes data) {
		this.use = new DayOffDayTimeUse(new LeaveUsedDayNumber(data.getUnDigestedDays()),
				Optional.of(new LeaveUsedTime(data.getUnDigestedTimes())));
		this.oocr = new DayOffDayAndTimes(new MonthVacationGrantDay(data.getRemainDays()),
				Optional.of(new MonthVacationGrantTime(data.getRemainTimes())));
	}

	public void removeAllDayValue() {
		this.remain.setDay(new LeaveRemainingDayNumber(0.0));
		this.use.setDay(new LeaveUsedDayNumber(0.0));
		this.oocr.setDay(new MonthVacationGrantDay(0.0));
		this.carryForward.setDay(new LeaveRemainingDayNumber(0.0));
	}
	
	public void removeAllTimeValue() {
		this.remain.setTime(Optional.empty());
		this.use.setTime(Optional.empty());
		this.oocr.setTime(Optional.empty());
		this.carryForward.setTime(Optional.empty());
	}
}
