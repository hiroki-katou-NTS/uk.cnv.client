/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.pub.role;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import nts.arc.time.GeneralDate;
import nts.uk.shr.com.context.loginuser.role.LoginUserRoles;

/**
 * The Interface RoleExportRepo.
 */
public interface RoleExportRepo {
	
	/**
	 * Find by list role id.
	 *
	 * @param companyId the company id
	 * @param lstRoleId the lst role id
	 * @return the list
	 */
	List<RoleExport> findByListRoleId(String companyId,List<String> lstRoleId);
		
	
	/**
	 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".システム.権限管理.Export.[RQ151]ロールIDから参照可能な職場リストを取得する
	 *
	 * @param systemType the system type
	 * @param baseDate the base date
	 * @return the workplace id export
	 */
	WorkplaceIdExport findWorkPlaceIdByRoleId(Integer systemType, GeneralDate baseDate);
	
 	/**
	 * Find by id.
	 *
	 * @param roleId the role id
	 * @return the list
	 */
	List<RoleExport> findById(String roleId);
	
	/**
	 * Find work place id by role id.
	 *
	 * @param systemType the system type
	 * @return the workplace id export
	 */
	WorkplaceIdExport findWorkPlaceIdNoRole(Integer systemType);
	
	/**
	 * Gets the work place id by employee reference range.
	 *
	 * @param baseDate the base date
	 * @param employeeReferenceRange the employee reference range
	 * @return the work place id by employee reference range
	 */
	// Request #159 
	// 指定条件から参照可能な職場リストを取得する
	List<String> getWorkPlaceIdByEmployeeReferenceRange(GeneralDate baseDate, Integer employeeReferenceRange);
	
	/**
	 * Find role id by system type.
	 * @param systemType the system type
	 * @return the string
	 */
	//Get RoleId 
	String findRoleIdBySystemType(Integer systemType);
	
	/**
	 * RequestList50
	 * @return
	 */
	RoleWhetherLoginPubExport  getWhetherLoginerCharge();
	
	RoleWhetherLoginPubExport  getWhetherLoginerCharge(LoginUserRoles roles);
	
	/**
	 * RequestList325
	 * @return
	 */
	OperableSystemExport  getOperableSystem();
	/**
	 * 社員参照範囲を取得する
	 */
	OptionalInt findEmpRangeByRoleID(String roleID);
	
	
	/**
	 * Find by role id.
	 *
	 * @param roleId the role id
	 * @return the optional
	 */
	//	RequestList84  ロールを取得する
	Optional<RoleExport> findByRoleId(String roleId);
	
	/**
	 * Gets the current loginer role.
	 *
	 * @return the current loginer role
	 */
	//	RequestList525
	RoleWhetherLoginPubExport  getCurrentLoginerRole();

	
	/**
	 * 	đối ứng cho bug 109119- CLI003
	 *  getNameLstByRoleIds
	 * @param companyId the company id
	 * @param lstRoleId the lst role id
	 * @return the list
	 */
	Map<String, String> getNameLstByRoleIds(String cid, List<String> roleIds);
	
	
	/**
	 * [No.XXX]ユーザIDからロールを区分を含めて取得する
	 * @param userId  ユーザID：ユーザID
	 * @param rollType ロール種類：ロール種類
	 * @param baseDate 基準日：年月日
	 * @param companyId 会社ID：会社ID
	 * @return ・ロール情報：
				　　├　担当ロールか：boolean
				　　└　ロールID：ロールID
	 */
	RollInformationExport getRoleIncludCategoryFromUserID(String userId, int roleType, GeneralDate baseDate, String companyId);
	
	/**
	 * [No.XXX]ユーザIDからロールセットを取得する
	 * @param userId  ユーザID：ユーザID
	 * @param baseDate 基準日：年月日
	 * @return ロールセット
	 */
	Optional<RoleSetExport> getRoleSetFromUserId(String userId, GeneralDate baseDate);

}
