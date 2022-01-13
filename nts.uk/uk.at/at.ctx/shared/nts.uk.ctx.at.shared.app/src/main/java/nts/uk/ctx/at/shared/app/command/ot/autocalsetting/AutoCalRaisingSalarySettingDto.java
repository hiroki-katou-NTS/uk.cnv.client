package nts.uk.ctx.at.shared.app.command.ot.autocalsetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;

/**
 * The Class AutoCalRaisingSalarySettingDto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoCalRaisingSalarySettingDto {

	/** The specific raising salary calc atr. */
	private boolean specificRaisingSalaryCalcAtr;

	/** The raising salary calc atr. */
	private boolean raisingSalaryCalcAtr;

	public static AutoCalRaisingSalarySettingDto fromDomain(AutoCalRaisingSalarySetting domain) {
		return new AutoCalRaisingSalarySettingDto(domain.isSpecificRaisingSalaryCalcAtr(),
				domain.isRaisingSalaryCalcAtr());
	}
}
