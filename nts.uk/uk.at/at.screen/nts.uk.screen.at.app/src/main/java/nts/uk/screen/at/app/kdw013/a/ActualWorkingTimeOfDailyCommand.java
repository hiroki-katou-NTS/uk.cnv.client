package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;

@AllArgsConstructor
@Getter
public class ActualWorkingTimeOfDailyCommand {

	// 割増時間
	private PremiumTimeOfDailyPerformanceCommand premiumTimeOfDailyPerformance;

	// 拘束差異時間
	private Integer constraintDifferenceTime;

	// 拘束時間
	private ConstraintTimeCommand constraintTime;

	// 時差勤務時間
	private Integer timeDifferenceWorkingHours;

	// 総労働時間
	//private TotalWorkingTimeCommand totalWorkingTime;

	// //代休発生情報
	// private SubHolOccurrenceInfo subHolOccurrenceInfo;

	// 乖離時間
	private DivergenceTimeOfDailyCommand divTime;

	public ActualWorkingTimeOfDaily toDomain() {

		return new ActualWorkingTimeOfDaily(
				this.getPremiumTimeOfDailyPerformance().toDomain(),
				new AttendanceTime(this.getConstraintDifferenceTime()), 
				this.getConstraintTime().toDomain(), 
				new AttendanceTime(this.getTimeDifferenceWorkingHours()) , 
				null, 
				this.getDivTime().toDomain());
	}

}
