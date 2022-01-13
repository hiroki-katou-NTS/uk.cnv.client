package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.AutoCalRaisingSalarySetting;

@AllArgsConstructor
@Getter
public class AutoCalRaisingSalarySettingCommand {
	private boolean specificRaisingSalaryCalcAtr;
	private boolean raisingSalaryCalcAtr;

	public AutoCalRaisingSalarySetting toDomain() {
		return new AutoCalRaisingSalarySetting(specificRaisingSalaryCalcAtr, raisingSalaryCalcAtr);
	}
}
