package nts.uk.ctx.sys.auth.dom.algorithm;

import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.role.Role;

public interface EmpReferenceRangeService {
	Optional<Role>  getByUserIDAndReferenceDate(String UserID, int roleType, GeneralDate referenceDate); 
}
