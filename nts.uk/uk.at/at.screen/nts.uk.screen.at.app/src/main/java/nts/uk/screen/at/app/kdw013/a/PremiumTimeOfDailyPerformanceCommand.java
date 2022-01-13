package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTimeOfDailyPerformance;

@AllArgsConstructor
@Getter
public class PremiumTimeOfDailyPerformanceCommand {
	/** 割増時間 **/
	private List<PremiumTimeCommand> premiumTimes;

	/** 割増金額合計 **/
	private Integer totalAmount;

	/** 割増労働時間合計 **/
	private Integer totalWorkingTime;

	public PremiumTimeOfDailyPerformance toDomain() {

		return new PremiumTimeOfDailyPerformance(
				this.getPremiumTimes().stream().map(x -> x.toDomain()).collect(Collectors.toList()),
				new AttendanceAmountDaily(this.getTotalAmount()),
				new AttendanceTime(this.getTotalWorkingTime()));
	}
}
