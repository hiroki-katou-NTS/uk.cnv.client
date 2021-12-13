package nts.uk.ctx.sys.auth.app.command.cas012;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;


@Stateless
public class Cas012CompanyAdRegisterCommandHandler extends CommandHandler<Cas012CompanyAdRegisterOrUpdateCommand> {

    @Inject
    private RoleIndividualGrantRepository roleIndividualGrantRepo;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserRepository userRepo;
    @Override
    protected void handle(CommandHandlerContext<Cas012CompanyAdRegisterOrUpdateCommand> commandHandlerContext) {
        Cas012CompanyAdRegisterOrUpdateCommand command = commandHandlerContext.getCommand();
        Optional<RoleIndividualGrant> domainOld =
                roleIndividualGrantRepo.findByUserCompanyRoleType(command.getUId(),command.getCId(),command.getRoleType());
        if(domainOld.isPresent()){
            throw new BusinessException("Msg_61","Com_User");
        }
        DatePeriod validPeriod = new DatePeriod(command.getStartDate(),command.getEndDate());
        RequireImpl require = new RequireImpl(roleRepository,userRepo);
        RoleIndividualGrant domain = RoleIndividualGrant.createGrantInfoOfCompanyManager(require,command.getUId(),command.getCId(),validPeriod);
        roleIndividualGrantRepo.add(domain);

    }
    @AllArgsConstructor
    public class RequireImpl implements RoleIndividualGrant.Require{

        private RoleRepository roleRepository;
        private UserRepository userRepo;
        @Override
        public Role getRoleByRoleType(RoleType roleType) {
            val listRole = roleRepository.findByType(roleType.value);
            if(listRole.isEmpty()){
                return null;
            }else {
                return listRole.get(0);
            }
        }

        @Override
        public Optional<User> getUser(String userId) {
            return userRepo.getByUserID(userId);
        }
    }
}
