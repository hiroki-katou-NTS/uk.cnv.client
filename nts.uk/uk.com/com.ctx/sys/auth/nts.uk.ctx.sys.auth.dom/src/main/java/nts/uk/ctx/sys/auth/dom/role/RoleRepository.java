/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.dom.role;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RoleRepository {

	/**
	 * Find by id.
	 *
	 * @param lstRoleId the lst role id
	 * @return Role
	 */
	List<Role> findByListId(List<String> lstRoleId);

	/**
	 * Find by id
	 * 
	 * @param roleId
	 * @return
	 */
	Optional<Role> findByRoleId(String roleId);

	/**
	 * Find by list role id.
	 *
	 * @param companyId
	 *            the company id
	 * @param lstRoleId
	 *            the lst role id
	 * @return the list
	 */
	List<Role> findByListRoleId(String companyId, List<String> lstRoleId);
	
	/**
	 * 
	 * @param contractCD
	 * @param RoleType
	 * @param companyID
	 * @return
	 */
	Optional<Role> findByContractCDRoleTypeAndCompanyID(String contractCD, int roleType, String companyID);

	/**
	 * insert new role
	 * 
	 * @param role
	 */
	void insert(Role role);

	/**
	 * update role
	 * 
	 * @param role
	 */
	void update(Role role);

	/**
	 * remove role
	 * 
	 * @param roleId
	 */
	void remove(String roleId);

	/**
	 * find by role type
	 * 
	 * @param companyId
	 * @param roleType
	 * @return Role
	 */
	List<Role> findByType(String companyId, int roleType);
	
	/**
	 * get by role type and filter
	 * 
	 * @param companyId
	 * @param roleType
	 * @param isInchargeOnly
	 * @param isGeneralOnly
	 * @return
	 */
	List<Role> findByTypeAndRoleAtr(String companyId, int roleType, int roleAtr);
	
	/**
	 * find by role type, RoleAtr
	 * 
	 * @param companyId
	 * @param roleType
	 * @param RoleAtr
	 * @return Role
	 */
	List<Role> findByTypeAtr(String companyId, int roleType, int RoleAtr);

	/**
	 * find by role type
	 * 
	 * @param roleType
	 * @return Role
	 */
	List<Role> findByType(int roleType);
	
	/**
	 * Find by id.
	 *
	 * @param roleId the role id
	 * @return the list
	 */
	default List<Role> findById(String roleId) {
		return this.findByListId(Arrays.asList(roleId));
	}
	
	/**
	 * ?????i ???ng bug 108119- CLI003
	 * findRoleIdAndNameByListRoleId
	 * @param cid
	 * @param roleIds
	 * @return
	 */
	Map<String, String> findRoleIdAndNameByListRoleId(String cid, List<String> roleIds);

	/**
	 * exists(??????ID, ???????????????, ??????????????????, ??????????????????)
	 * @param cid ??????ID
	 * @param roleType ???????????????
	 * @param assignAtr ????????????
	 * @param roleCode ??????????????????
	 * @return
	 */
	boolean exists(String cid, RoleType roleType, RoleAtr assignAtr, RoleCode roleCode);
	
	/**
	 * [2] ???????????????????????????????????????????????????
	 * @param cid
	 * @return ?????????List
	 */
	List<Role> obtainRoleWorks(String cid);
	
	/**
	 * [3] ???????????????ID?????????????????????????????????????????????????????????
	 * @param cid
	 * @param eplRoleId ???????????????ID
	 * @return ?????????
	 */
	Optional<Role> getRoleWorks(String cid, String eplRoleId);

	/**
	 * find by company
	 *
	 * @param companyId
	 * @return Role
	 */
	List<Role> findByCompanyId(String companyId);
}
