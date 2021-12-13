package nts.uk.ctx.at.function.dom.adapter.workplace;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

public interface WorkplaceAdapter {
	Optional<WorkplaceImport> getWorlkplaceHistory(String employeeId, GeneralDate baseDate);

	List<WorkplaceImport> findWkpByWkpIdList(String companyId, GeneralDate baseDate, List<String> wkpIds);
	
	List<WorkplaceIdName> findWkpByWkpId(String companyId, GeneralDate baseDate, List<String> wkpIds);
	
	List<WorkplaceImport> getWorlkplaceHistory(List<String> employeeId, GeneralDate baseDate);
	
	List<WorkplaceImport> getWorlkplaceHistoryByIDs(List<String> employeeIds);
	
	List<WorkPlaceInforExport> getWorkplaceInforByWkpIds(String companyId, List<String> listWorkplaceId, GeneralDate baseDate);

	/**
	 * [No.597]職場の所属社員を取得する
	 */
	List<EmployeeInfoImported> getLstEmpByWorkplaceIdsAndPeriod(List<String> workplaceIds, DatePeriod period);
}
