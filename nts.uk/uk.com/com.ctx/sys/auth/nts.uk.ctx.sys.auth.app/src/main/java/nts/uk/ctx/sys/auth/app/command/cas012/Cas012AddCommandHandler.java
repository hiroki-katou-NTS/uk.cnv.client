package nts.uk.ctx.sys.auth.app.command.cas012;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.GrantSystemAdminRoleService;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrant;
import nts.uk.ctx.sys.auth.dom.grant.roleindividual.RoleIndividualGrantRepository;
import nts.uk.ctx.sys.auth.dom.role.Role;
import nts.uk.ctx.sys.auth.dom.role.RoleRepository;
import nts.uk.ctx.sys.auth.dom.role.RoleType;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Stateless
public class Cas012AddCommandHandler extends CommandHandler<Cas012AddOrUpdateCommand> {
    @Inject
    private RoleIndividualGrantRepository roleIndividualGrantRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserRepository userRepo;

    @Override
    protected void handle(CommandHandlerContext<Cas012AddOrUpdateCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        DatePeriod validPeriod = new DatePeriod(command.getStartDate(),command.getEndDate());
        RequireImpl require = new RequireImpl(roleIndividualGrantRepository, roleRepository, userRepo);
        AtomTask task = GrantSystemAdminRoleService.grant(require, command.getUId(),validPeriod);
        transaction.execute(task::run);
    }

    @AllArgsConstructor
    public class RequireImpl implements GrantSystemAdminRoleService.Require {
        private RoleIndividualGrantRepository roleIndividualGrantRepository;
        private RoleRepository roleRepository;
        private UserRepository userRepo;

        @Override
        public Optional<RoleIndividualGrant> getGrantInfoByRoleTypeOfUser(String userId, RoleType roleType) {
            val listDomain = roleIndividualGrantRepository.findByUserAndRole(userId,roleType.value);
            if(listDomain.isEmpty()){
                return Optional.empty();
            }else {
                return Optional.of(listDomain.get(0));
            }

        }

        @Override
        public List<RoleIndividualGrant> getGrantInfoByRoleType(RoleType roleType) {
            return roleIndividualGrantRepository.findByRoleType(roleType.value);
        }

        @Override
        public void registerGrantInfo(RoleIndividualGrant roleIndividualGrant) {
            roleIndividualGrantRepository.add(roleIndividualGrant);
        }

        @Override
        public void updateGrantInfo(RoleIndividualGrant roleIndividualGrant) {
            roleIndividualGrantRepository.update(roleIndividualGrant);
        }

        @Override
        public void deleteGrantInfo(String userId, String companyId, RoleType roleType) {
            roleIndividualGrantRepository.remove(userId, companyId, roleType.value);
        }

        @Override
        public Role getRoleByRoleType(RoleType roleType) {
            val listRole = roleRepository.findByType(roleType.value);
            if (listRole.isEmpty()) {
                return null;
            }
            return listRole.get(0);
        }

        @Override
        public Optional<User> getUser(String userId) {
            return userRepo.getByUserID(userId);
        }
    }
}
