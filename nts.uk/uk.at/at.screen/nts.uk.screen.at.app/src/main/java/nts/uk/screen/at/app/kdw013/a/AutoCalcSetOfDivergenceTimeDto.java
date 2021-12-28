package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.deviationtime.AutoCalcSetOfDivergenceTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AutoCalcSetOfDivergenceTimeDto {
	/** 乖離: boolean */
	private Integer divergenceTime;

	public static AutoCalcSetOfDivergenceTimeDto fromDomain(AutoCalcSetOfDivergenceTime domain) {
		return new AutoCalcSetOfDivergenceTimeDto(domain.getDivergenceTime().value);
	}
}
