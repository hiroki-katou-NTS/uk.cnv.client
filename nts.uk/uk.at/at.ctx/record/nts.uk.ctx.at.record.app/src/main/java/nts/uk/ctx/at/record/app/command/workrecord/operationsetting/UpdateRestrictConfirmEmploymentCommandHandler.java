package nts.uk.ctx.at.record.app.command.workrecord.operationsetting;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmployment;
import nts.uk.ctx.at.record.dom.confirmemployment.RestrictConfirmEmploymentRepository;

@Stateless
@Transactional
public class UpdateRestrictConfirmEmploymentCommandHandler extends CommandHandler<RestrictConfirmEmploymentCommand>{

	@Inject
	RestrictConfirmEmploymentRepository repository;
	
	@Override
    protected void handle(CommandHandlerContext<RestrictConfirmEmploymentCommand> context) {
		RestrictConfirmEmploymentCommand updateCommand = context.getCommand();
        repository.update(new RestrictConfirmEmployment(updateCommand.getCompanyID(), updateCommand.isConfirmEmployment()));
    }
}
