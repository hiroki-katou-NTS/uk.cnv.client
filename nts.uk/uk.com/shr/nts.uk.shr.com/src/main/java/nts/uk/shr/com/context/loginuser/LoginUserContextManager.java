package nts.uk.shr.com.context.loginuser;

import nts.arc.security.ticket.DataTicket;
import nts.uk.shr.com.context.loginuser.role.LoginUserRoles;

import java.util.Optional;

/**
 * The class to update LoginUserContext
 * 
 * usage:
 * @Inject LoginUserContextManager manager;
 * ...
 *
 * this.manager.loggedInAsEmployee(... parameters ...);
 * 
 * this.manager.roleIdSetter();
 *     .forAttendance(...);
 *     .forPayroll(...);
 *     .forPersonnel(...);
 *     .forPersonalInfo(...);
 * ...
 */
public interface LoginUserContextManager {
	
	/**
	 * User logged in as empoyee.
	 * 
	 * @param userId
	 * @param personId
	 * @param contractCode
	 * @param companyId
	 * @param companyCode
	 * @param employeeId
	 * @param employeeCode
	 */
	void loggedInAsEmployee(
			String userId,
			String personId,
			String contractCode,
			String companyId,
			String companyCode,
			String employeeId,
			String employeeCode);
	
	/**
	 * User logged in as user.
	 * 
	 * @param userId
	 * @param personId
	 * @param contractCode
	 * @param companyId
	 * @param companyCode
	 */
	void loggedInAsUser(
			String userId,
			String personId,
			String contractCode,
			String companyId,
			String companyCode);
	
	/**
	 * Change company.
	 * @param userId
	 * @param personId
	 * @param contractCode
	 * @param companyId
	 * @param companyCode
	 * @param employeeId
	 * @param employeeCode
	 */
	void changeCompany(
			String userId,
			String personId,
			String contractCode,
			String companyId,
			String companyCode,
			String employeeId,
			String employeeCode,
			boolean isEmployee);
	
	/**
	 * Returns RoleIdSetter to set role IDs of the user.
	 * @return RoleIdSetter
	 */
	RoleIdSetter roleIdSetter();
	
	void roleSet(LoginUserRoles roles);

	/**
	 * set language
	 * @param basic basic
	 * @param forPersonName for person name
	 */
	void setLanguage(String basic, String forPersonName);
	
	DataTicket toTicket();
	
	void restore(DataTicket ticket);
	
	Optional<String> toBase64();
	
	void restoreBase64(String base64);
	
	void loggedOut();
	
	public static interface RoleIdSetter {

		RoleIdSetter forAttendance(String roleId);
		RoleIdSetter forPayroll(String roleId);
		RoleIdSetter forPersonnel(String roleId);
		RoleIdSetter forPersonalInfo(String roleId);
		RoleIdSetter forOfficeHelper(String roleId);
		RoleIdSetter forSystemAdmin(String roleId);
		RoleIdSetter forCompanyAdmin(String roleId);
		RoleIdSetter forGroupCompaniesAdmin(String roleId);
		RoleIdSetter isInChargeAttendance(boolean isInCharge);
		RoleIdSetter isInChargePayroll(boolean isInCharge);
		RoleIdSetter isInChargePersonnel(boolean isInCharge);
		RoleIdSetter isInChargePersonalInfo(boolean isInCharge);
	}
}
