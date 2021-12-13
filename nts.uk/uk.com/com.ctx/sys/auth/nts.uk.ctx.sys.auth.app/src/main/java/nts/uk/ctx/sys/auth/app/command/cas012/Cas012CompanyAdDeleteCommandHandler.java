package nts.uk.ctx.sys.auth.app.command.cas012;


import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class Cas012CompanyAdDeleteCommandHandler extends CommandHandler<Cas012CompanyAdDeleteCommand> {
    @Inject
    private RoleIndividualGrantRepository roleIndividualGrantRepo;
    @Override
    protected void handle(CommandHandlerContext<Cas012CompanyAdDeleteCommand> commandHandlerContext) {
        Cas012CompanyAdDeleteCommand command = commandHandlerContext.getCommand();
        if(command.getRoleType() == RoleType.COMPANY_MANAGER.value)
        roleIndividualGrantRepo.remove(command.getUId(),command.getCId(),command.getRoleType());
    }
}
