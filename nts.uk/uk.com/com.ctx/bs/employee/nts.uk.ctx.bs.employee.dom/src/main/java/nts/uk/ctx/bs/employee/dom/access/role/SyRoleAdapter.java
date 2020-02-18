/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.dom.access.role;

import java.util.List;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceInfo;

/**
 * The Interface SyRoleWorkplaceAdapter.
 */
public interface SyRoleAdapter {
	
	/**
	 * Find list wkp id by role id.
	 *
	 * @param systemType the system type
	 * @param baseDate the base date
	 * @return the list
	 */
	WorkplaceIDImport findListWkpIdByRoleId(Integer systemType, GeneralDate baseDate);
	

	/**
	 *
	 * @param systemType the system type
	 * @return the list
	 */
	WorkplaceIDImport findListWkpId(Integer systemType);
	
	
	/**
	 * [No.560]職場IDから職場の情報をすべて取得する
	 * 
	 * @param companyId
	 * @param listWorkplaceId
	 * @param baseDate
	 * @return
	 */
	public List<WorkplaceInfo> getWorkplaceInforByWkpIds(String companyId, List<String> listWorkplaceId,
			GeneralDate baseDate);
}
