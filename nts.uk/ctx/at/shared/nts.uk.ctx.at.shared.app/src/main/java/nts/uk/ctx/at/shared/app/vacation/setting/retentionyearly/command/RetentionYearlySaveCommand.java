package nts.uk.ctx.at.shared.app.vacation.setting.retentionyearly.command;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.app.vacation.setting.retentionyearly.command.dto.RetentionYearlyDto;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;

@Getter
@Setter
public class RetentionYearlySaveCommand {

	private RetentionYearlyDto retentionYearly;
	public RetentionYearlySetting toDomain(String companyCode) {
		return this.retentionYearly.toDomain(companyCode);
	}
}
