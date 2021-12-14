package nts.uk.ctx.sys.log.dom.logbasicinfo;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDateTime;
import nts.uk.shr.com.security.audittrail.basic.LogBasicInformation;

/**
 * 
 * @author HungTT
 *
 */

public interface LogBasicInfoRepository {

	Optional<LogBasicInformation> getLogBasicInfo(String companyId, String operationId);
	
	List<LogBasicInformation> findByOperatorsAndDate(String companyId, List<String> listEmployeeId, GeneralDateTime start, GeneralDateTime end);
	
	List<LogBasicInformation> getLogBasicInfo(String companyId, List<String> operationIds);
	
}
