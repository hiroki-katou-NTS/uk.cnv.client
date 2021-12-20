package nts.uk.ctx.at.schedule.app.command.task.taskpalette;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskpalette.TaskPaletteOrganizationRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;

/**
 * 
 * @author quytb
 *
 */

@Stateless
public class TaskPaletteDeleteCommandHandler extends CommandHandler<TaskPaletteCommand> {
	@Inject
	private TaskPaletteOrganizationRepository repository;
	
	@Override
	protected void handle(CommandHandlerContext<TaskPaletteCommand> context) {
		TaskPaletteCommand command = context.getCommand();		
		
		TargetOrgIdenInfor targetOrg = TargetOrgIdenInfor
				.createFromTargetUnit(TargetOrganizationUnit.valueOf(command.getUnit()), command.getTargetId());
		
		this.repository.delete(targetOrg, command.getPage());		
	}

}
