package nts.uk.ctx.sys.auth.app.query;


import lombok.AllArgsConstructor;
import lombok.val;
import nts.uk.ctx.sys.auth.dom.GetPersonalEmployeeInfoByUserIdService;
import nts.uk.ctx.sys.auth.dom.adapter.employee.EmployeeAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.PersonalEmployeeInfoImport;
import nts.uk.ctx.sys.shared.dom.user.User;
import nts.uk.ctx.sys.shared.dom.user.UserRepository;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

@Stateless
public class GetEmployeeIDFromUserIDQuery {
    @Inject
    private UserRepository userRepo;
    @Inject
    private EmployeeAdapter employeeAdapter;

    public Optional<PersonalEmployeeInfoImport> getEmployeeIDFromUserID(String uid){
        RequireImpl require = new RequireImpl(userRepo,employeeAdapter);
        return GetPersonalEmployeeInfoByUserIdService.get(require,uid);
    }

    @AllArgsConstructor
    public class RequireImpl implements GetPersonalEmployeeInfoByUserIdService.Require{
        private UserRepository userRepo;
        private EmployeeAdapter employeeAdapter;
        @Override
        public Optional<User> getUser(String userId) {
            return userRepo.getByUserID(userId);
        }
        @Override
        public Optional<PersonalEmployeeInfoImport> getPersonalEmployeeInfo(String personId) {
            val rs =   employeeAdapter.getPersonalEmployeeInfo(Arrays.asList(personId));
            if(rs.isEmpty()){
                return Optional.empty();
            }
            else {
                return Optional.of(rs.get(0));
            }
        }
    }
}
