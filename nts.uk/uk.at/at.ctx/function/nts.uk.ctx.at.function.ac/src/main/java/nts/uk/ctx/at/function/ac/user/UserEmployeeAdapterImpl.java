package nts.uk.ctx.at.function.ac.user;

import nts.uk.ctx.at.function.dom.adapter.user.UserEmployeeAdapter;
import nts.uk.ctx.sys.auth.pub.user.UserPublisher;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Optional;

@Stateless
public class UserEmployeeAdapterImpl implements UserEmployeeAdapter {
    @Inject
    private UserPublisher userPublisher;

    @Override
    public Optional<String> getUserIDByEmpID(String employeeID) {
        return userPublisher.getUserIDByEmpID(employeeID);
    }
}
