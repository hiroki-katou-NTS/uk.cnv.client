package nts.uk.ctx.at.shared.dom.adapter.workplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface SharedAffWorkPlaceHisAdapter {
	
	Optional<SharedAffWorkPlaceHisImport> getAffWorkPlaceHis(String employeeId, GeneralDate processingDate);

	List<String> findParentWpkIdsByWkpId(String companyId, String workplaceId, GeneralDate date);
	
	List<String> findAffiliatedWorkPlaceIdsToRoot(String companyId, String employeeId, GeneralDate baseDate);
	
	List<String> findAffiliatedWorkPlaceIdsToRootRequire(CacheCarrier cacheCarrier, String companyId, String employeeId, GeneralDate baseDate);

	List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId);
	
	Map<GeneralDate, Map<String, Optional<SharedAffWorkPlaceHisImport>>> getAffWorkPlaceHisClones(String companyId, List<String> employeeId, DatePeriod baseDate);

	List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate baseDate);

}
