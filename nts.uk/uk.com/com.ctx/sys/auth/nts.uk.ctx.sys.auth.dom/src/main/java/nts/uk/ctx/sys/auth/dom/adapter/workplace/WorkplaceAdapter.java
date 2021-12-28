/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.adapter.workplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;

/**
 * The Interface WorkplaceAdapter.
 */
public interface WorkplaceAdapter {

	/**
	 * Find list wkp id by base date.
	 *
	 * @param baseDate the base date
	 * @return the list
	 */
	List<String> findListWkpIdByBaseDate(GeneralDate baseDate);

	/**
	 * Find wkp by base date and employee id.
	 *
	 * @param baseDate the base date
	 * @param employeeId the employee id
	 * @return the aff workplace hist import
	 */
	Optional<AffWorkplaceHistImport> findWkpByBaseDateAndEmployeeId(GeneralDate baseDate, String employeeId);
	//NEW 
	Optional<AffWorkplaceHistImport> findWkpByBaseDateAndSIdNEW(GeneralDate baseDate, String employeeId);
	
	/**
	 * Find list workplace id by cid and wkp id and base date.
	 *
	 * @param companyId the company id
	 * @param workplaceId the workplace id
	 * @param baseDate the base date
	 * @return the list
	 */
	List<String> findListWorkplaceIdByCidAndWkpIdAndBaseDate(String companyId, String workplaceId,
			GeneralDate baseDate);
	
	/**
	 * 
	 * @param workplaceId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<AffWorkplaceImport> findListSIdByCidAndWkpIdAndPeriod(String workplaceId, GeneralDate startDate, GeneralDate endDate);

	
	List<AffiliationWorkplace> findByListEmpIDAndDate (List<String> listEmployeeID , GeneralDate baseDate);
	
	//find list employeeId by list wkpId (req120 ver2)
	List<AffWorkplaceImport> findListSIdWkpIdAndPeriod(List<String> lstWkpId, GeneralDate startDate, GeneralDate endDate);
	
	// RequestList613: [RQ613]指定社員の職場管理者の職場リストを取得する（配下含む）
	List<String> getWorkplaceId(GeneralDate baseDate, String employeeID);
	/**
	 * Lay workplace cap duoi
	 * @param companyId
	 * @param baseDate
	 * @param parentWorkplaceId
	 * @return
	 */
	public List<String> getAllChildrenOfWkpIdNEW(String companyId, GeneralDate baseDate, String parentWorkplaceId);
	
	/**
	 * [No.573]職場の下位職場を基準職場を含めて取得する
	 *
	 * @param companyId
	 * @param baseDate
	 * @param workplaceId
	 * @return
	 */
	public List<String> getWorkplaceIdAndChildren(String companyId, GeneralDate baseDate, String workplaceId);
	/**
	 * @name 所属職場を取得するAdapter
	 * @param employeeID 	社員ID
	 * @param date 年月日
	 * @return 	Map<社員ID,職場ID> 所属情報
	 */
	public Map<String, String> getAWorkplace(String employeeID, GeneralDate date);

	/**
	 * @name 	所属職場リストを取得するAdapter
	 * @param employeeID  	List<職場ID>
	 * @param date 年月日
	 * @return 	社員一覧	Map<社員ID,職場ID>
	 */
	public Map<String, String> getByListIds(List<String> workPlaceIds, GeneralDate baseDate);
}
