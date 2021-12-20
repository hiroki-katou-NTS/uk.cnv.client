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
	 * đối ứng bug 108119- CLI003
	 * findRoleIdAndNameByListRoleId
	 * @param cid
	 * @param roleIds
	 * @return
	 */
	Map<String, String> findRoleIdAndNameByListRoleId(String cid, List<String> roleIds);

	/**
	 * exists(会社ID, ロール種類, 担当区分毎に, ロールコード)
	 * @param cid 会社ID
	 * @param roleType ロール種類
	 * @param assignAtr 担当区分
	 * @param roleCode ロールコード
	 * @return
	 */
	boolean exists(String cid, RoleType roleType, RoleAtr assignAtr, RoleCode roleCode);
	
	/**
	 * [2] 承認権限がある就業ロールを取得する
	 * @param cid
	 * @return ロールList
	 */
	List<Role> obtainRoleWorks(String cid);
	
	/**
	 * [3] 就業ロールIDから承認権限がある就業ロールを取得する
	 * @param cid
	 * @param eplRoleId 就業ロールID
	 * @return ロール
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
