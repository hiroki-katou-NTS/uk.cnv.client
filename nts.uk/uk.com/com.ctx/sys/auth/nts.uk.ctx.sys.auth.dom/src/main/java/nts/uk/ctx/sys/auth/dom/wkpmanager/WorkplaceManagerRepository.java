package nts.uk.ctx.sys.auth.dom.wkpmanager;

import java.util.List;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.export.wkpmanager.WorkPlaceSelectionExportData;
import nts.uk.ctx.sys.auth.dom.wplmanagementauthority.WorkPlaceFunction;

public interface WorkplaceManagerRepository {
	
	/**
	 * IDを指定して職場管理者を取得する
	 * @param id ID
	 * @return
	 */
	Optional<WorkplaceManager> getWorkplaceManagerByID(String id);
	
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
	
	/**
	 * 職場IDと社員IDを指定して職場管理者を取得する
	 * @param workplaceId 職場ID
	 * @param sid 社員ID
	 * @return List<WorkplaceManager>
	 */
	List<WorkplaceManager> getWkpManagerByWorkplaceIdAndSid(String workplaceId, String sid);
	
	/**
	 * 社員IDを指定して職場管理者を取得する
	 * @param sid 社員ID
	 * @return List<WorkplaceManager>
	 */
	List<WorkplaceManager> getWkpManagerListBySid(String sid);
	
	/**
	 * 職場IDと社員IDを指定して職場管理者を削除する
	 * @param workplaceId 職場ID
	 * @param sid 社員ID
	 */
	void deleteByWorkplaceIdAndSid(String workplaceId, String sid);

}
