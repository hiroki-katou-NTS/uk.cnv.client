package nts.uk.ctx.at.record.app.command.workrecord.worktype;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.workingtype.WorkingTypeChangedByEmpRepo;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class DeleteEmploymentSettingCommandHandler extends CommandHandler<DeleteEmploymentSettingCommand>{

	@Inject
	private WorkingTypeChangedByEmpRepo workRep;
	
	@Override
	protected void handle(CommandHandlerContext<DeleteEmploymentSettingCommand> context) {
		DeleteEmploymentSettingCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		String employmentCode = command.getEmploymentCode();
		
		workRep.deleteEmploymentSetting(companyId, employmentCode);
	}
}
