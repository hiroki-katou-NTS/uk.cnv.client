package nts.uk.ctx.sys.shared.dom.user.builtin;

import java.util.Arrays;
import java.util.List;

import nts.uk.shr.com.context.loginuser.LoginUserContextManager;

/**
 * ビルトインユーザ
 */
public class BuiltInUser {
	
	// 仮
	private final String employeeCode = "system";
	
	/** システム管理者のロールID（初期データに合わせる） */
	private static final String ROLE_ID_SYSTEM_ADMIN = "00000000-0000-0000-00000000000000000";
	
	/** 会社管理者のロールID（０契約０会社データに合わせる） */
	private static final String ROLE_ID_COMPANY_ADMIN = "00000000-0000-0000-00000000000000001";
	
	/** 個人情報担当者のロールID（０契約０会社データに合わせる） */
	private static final String ROLE_ID_PERSONAL_INFO = "ROLE_ID_8_000000000000-0000";
	
	private static final String ROLE_ID_DUMMY = "BuiltInUser-RoleID";
	
	/** ビルトインユーザのユーザIDかどうかの判定に使えるようにpublic */
	public static final String USER_ID = "BuiltInUser-UserID";

	/** ビルトインユーザーの社員IDか以下略 */
	public static final String EMPLOYEE_ID = "BuiltInUser-EmployeeID";
	
	public boolean authenticate(String employeeCode, String password) {

		// 一旦system/kinjirou
		return this.employeeCode.equals(employeeCode) && password.equals("kinjirou");
	}
	
	public static List<String> allRoleIds() {
		return Arrays.asList(ROLE_ID_SYSTEM_ADMIN, ROLE_ID_COMPANY_ADMIN);
	}

	public void setupLoginUserContext(
			LoginUserContextManager manager,
			String tenantCode,
			String companyId,
			String companyCode) {
		
		manager.loggedInAsEmployee(
				USER_ID,
				"BuiltInUser-PersonID",
				tenantCode,
				companyId,
				companyCode,
				EMPLOYEE_ID,
				employeeCode);
		
		manager.roleIdSetter()
			.forAttendance(ROLE_ID_DUMMY)
			.forPayroll(ROLE_ID_DUMMY)
			.forPersonnel(ROLE_ID_DUMMY)
			.forPersonalInfo(ROLE_ID_PERSONAL_INFO)
			.forOfficeHelper(ROLE_ID_DUMMY)
			.forSystemAdmin(ROLE_ID_SYSTEM_ADMIN)
			.forCompanyAdmin(ROLE_ID_COMPANY_ADMIN)
			.forGroupCompaniesAdmin(ROLE_ID_DUMMY)
			.isInChargeAttendance(false)
			.isInChargePayroll(false)
			.isInChargePersonnel(false)
			.isInChargePersonalInfo(true); // 個人情報の登録だけは触れる
	}
}
