package nts.uk.ctx.sys.auth.dom.algorithm;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AcquireUserIDFromEmpIDService {

	Optional<String> getUserIDByEmpID(String employeeID);
	Map<String,String> getUserIDByEmpID(List<String> listPid);
}
