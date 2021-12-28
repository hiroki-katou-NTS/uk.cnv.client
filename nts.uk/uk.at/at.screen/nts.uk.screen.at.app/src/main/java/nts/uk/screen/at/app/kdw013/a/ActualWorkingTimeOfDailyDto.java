package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.ConstraintTimeDto;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.TotalWorkingTimeDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.ActualWorkingTimeOfDaily;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ActualWorkingTimeOfDailyDto {
	// 割増時間
		private PremiumTimeOfDailyPerformanceDto premiumTimeOfDailyPerformance;

		// 拘束差異時間
		private Integer constraintDifferenceTime;

		// 拘束時間
		private ConstraintTimeDto constraintTime;

		// 時差勤務時間
		private Integer timeDifferenceWorkingHours;

		// 総労働時間
		private TotalWorkingTimeDto totalWorkingTime;

		// //代休発生情報
		// private SubHolOccurrenceInfo subHolOccurrenceInfo;

		// 乖離時間
		private DivergenceTimeOfDailyDto divTime;

		public static ActualWorkingTimeOfDailyDto fromDomain(ActualWorkingTimeOfDaily domain) {

		return new ActualWorkingTimeOfDailyDto(
				PremiumTimeOfDailyPerformanceDto.fromDomain(domain.getPremiumTimeOfDailyPerformance()),
				domain.getConstraintDifferenceTime().v(), 
				ConstraintTimeDto.fromDomain(domain.getConstraintTime()),
				domain.getTimeDifferenceWorkingHours().v(), 
				TotalWorkingTimeDto.fromTotalWorkingTime(domain.getTotalWorkingTime()),
				DivergenceTimeOfDailyDto.fromDomain(domain.getDivTime()));
		}
}
