package nts.uk.ctx.sys.auth.dom.wkpmanager;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.export.wkpmanager.WorkPlaceSelectionExportData;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.WorkPlaceFunction;

public interface WorkplaceManagerRepository {
	// Get workplace manager list by workplace id
	List<WorkplaceManager> getWkpManagerListByWkpId(String workplaceId);
	
	// Get workplace manager list by workplace id and employee id
	List<WorkplaceManager> getWkpManagerBySIdWkpId(String companyId, String workplaceId);
	
	// add new workplace manager
	void add(WorkplaceManager workplaceManager);
	
	// update workplace manager
	void update(WorkplaceManager workplaceManager);
	
	// delete workplace manager
	void delete(String wkpManagerId);
	
	List<WorkplaceManager> findListWkpManagerByEmpIdAndBaseDate(String employeeId, GeneralDate baseDate);
	
	List<WorkplaceManager> findByWkpDateAndManager(String wkpID, GeneralDate baseDate, List<String> wkpManagerIDLst);
	
	List<WorkplaceManager> findListWkpManagerByWkpIdsAndBaseDate(List<String> wkpIDLst, GeneralDate baseDate);

	//Export Data
	List<WorkPlaceSelectionExportData> findAllWorkPlaceSelection(String companyId, List<WorkPlaceFunction> functionNo);

	List<WorkplaceManager> findByPeriodAndWkpIds(List<String> wkpIds, DatePeriod datePeriod);

	List<WorkplaceManager> findByPeriodAndBaseDate(String wkpId, GeneralDate baseDate);

}
