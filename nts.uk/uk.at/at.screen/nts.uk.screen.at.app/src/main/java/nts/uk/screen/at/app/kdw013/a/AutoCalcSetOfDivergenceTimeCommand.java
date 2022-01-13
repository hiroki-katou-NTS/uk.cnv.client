package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.calculationattribute.enums.DivergenceTimeAttr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.deviationtime.AutoCalcSetOfDivergenceTime;

@AllArgsConstructor
@Getter
public class AutoCalcSetOfDivergenceTimeCommand {
	/** 乖離: boolean */
	private Integer divergenceTime;

	public AutoCalcSetOfDivergenceTime toDomain() {

		return new AutoCalcSetOfDivergenceTime(EnumAdaptor.valueOf(this.getDivergenceTime(), DivergenceTimeAttr.class));
	}
}
