package nts.uk.ctx.pr.core.app.insurance.social.pension.command;

import lombok.Data;
import nts.uk.ctx.pr.core.app.insurance.social.pensionrate.find.PensionRateDto;

@Data
public class RegisterPensionCommand {
	PensionRateDto pensionRate;
}
