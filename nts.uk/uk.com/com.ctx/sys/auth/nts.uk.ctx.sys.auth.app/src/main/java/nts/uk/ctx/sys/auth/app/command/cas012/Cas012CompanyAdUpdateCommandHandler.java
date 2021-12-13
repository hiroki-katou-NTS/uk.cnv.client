package nts.uk.ctx.sys.auth.app.command.cas012;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class Cas012CompanyAdUpdateCommandHandler extends CommandHandler<Cas012CompanyAdRegisterOrUpdateCommand> {
    @Inject
    private RoleIndividualGrantRepository roleIndividualGrantRepo;
    @Override
    protected void handle(CommandHandlerContext<Cas012CompanyAdRegisterOrUpdateCommand> commandHandlerContext) {
        Cas012CompanyAdRegisterOrUpdateCommand command = commandHandlerContext.getCommand();
        DatePeriod validPeriod = new DatePeriod(command.getStartDate(),command.getEndDate());
        Optional<RoleIndividualGrant> domainOldOpt =
                roleIndividualGrantRepo.findByUserCompanyRoleType(command.getUId(),command.getCId(),command.getRoleType());
        if(domainOldOpt.isPresent()){
            RoleIndividualGrant domainOld = domainOldOpt.get();
            domainOld.setValidPeriod(validPeriod);
            roleIndividualGrantRepo.update(domainOld);
        }
    }
}
