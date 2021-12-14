package nts.uk.ctx.bs.employee.dom.workplace;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeCode;

/*
 * TP_社員の所属組織
 */

@RequiredArgsConstructor
@Getter
public class EmployeeAffiliation {

	// 社員ID
	private final String employeeID;

	// 社員コード
	private final Optional<EmployeeCode> employeeCode;

	// ビジネスネーム
	private final Optional<String> businessName;

	// 職場ID
	private final String workplaceID;

	// 職場グループID
	private final Optional<String> workplaceGroupID;

	/**
	 * [C-1] 追加する
	 * @param employeeID
	 * @param employeeCode
	 * @param businessName
	 * @param workplaceID
	 * @param workplaceGroupID
	 * @return
	 */
	public static EmployeeAffiliation add(String employeeID, EmployeeCode employeeCode, String businessName,
			String workplaceID, String workplaceGroupID) {
		return new EmployeeAffiliation(employeeID, Optional.ofNullable(employeeCode), Optional.ofNullable(businessName),
				workplaceID, Optional.ofNullable(workplaceGroupID));
	}

	/**
	 * [C-2] 表示情報なしで作成する
	 * @param employeeID
	 * @param workplaceID
	 * @param workplaceGroupID
	 * @return
	 */
	public static EmployeeAffiliation createWithoutInfo(String employeeID, String workplaceID,
			String workplaceGroupID) {
		return new EmployeeAffiliation(employeeID, Optional.empty(), Optional.empty(), workplaceID,
				Optional.ofNullable(workplaceGroupID));
	}

	/**
	 * [C-3] 職場グループなしで作成する
	 * @param employeeID
	 * @param employeeCode
	 * @param businessName
	 * @param workplaceID
	 * @return
	 */
	public static EmployeeAffiliation createWithoutWG(String employeeID, EmployeeCode employeeCode, String businessName,
			String workplaceID) {
		return new EmployeeAffiliation(employeeID, Optional.ofNullable(employeeCode), Optional.ofNullable(businessName),
				workplaceID, Optional.empty());
	}

	/**
	 * [C-4] 表示情報と職場グループなしで作成する
	 * @param employeeID
	 * @param workplaceID
	 * @return
	 */
	public static EmployeeAffiliation createWithoutInfoAndWG(String employeeID, String workplaceID) {
		return new EmployeeAffiliation(employeeID, Optional.empty(), Optional.empty(), workplaceID, Optional.empty());
	}
}
