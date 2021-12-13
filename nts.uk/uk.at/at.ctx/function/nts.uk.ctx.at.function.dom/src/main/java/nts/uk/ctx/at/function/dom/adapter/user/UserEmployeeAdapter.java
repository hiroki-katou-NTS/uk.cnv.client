package nts.uk.ctx.at.function.dom.adapter.user;

import java.util.Optional;

public interface UserEmployeeAdapter {
    Optional<String> getUserIDByEmpID(String employeeID);
}
