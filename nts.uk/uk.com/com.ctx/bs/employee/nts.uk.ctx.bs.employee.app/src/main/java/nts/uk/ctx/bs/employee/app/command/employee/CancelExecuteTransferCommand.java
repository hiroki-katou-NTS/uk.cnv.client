package nts.uk.ctx.bs.employee.app.command.employee;

import java.util.List;

import lombok.Getter;
import nts.uk.ctx.bs.employee.app.command.jobtitle.affiliate.DeleteAffJobTitleMainCommand;
import nts.uk.ctx.bs.employee.app.command.workplace.affiliate.DeleteAffWorkplaceHistoryCommand;

@Getter
public class CancelExecuteTransferCommand {
	private List<DeleteAffWorkplaceHistoryCommand> affWorkplaces;
	private List<DeleteAffJobTitleMainCommand> affJobTitles;
}
