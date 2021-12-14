package nts.uk.ctx.sys.auth.dom.algorithm;

import java.util.Optional;

public interface AcquireUserIDFromEmpIDService {

	Optional<String> getUserIDByEmpID(String employeeID);
}
