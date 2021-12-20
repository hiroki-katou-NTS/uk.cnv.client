package nts.uk.ctx.bs.employee.dom.workplace;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.uk.ctx.bs.employee.dom.employeeinfo.EmployeeCode;

/**
 * 社員の所属組織
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.基幹.社員.職場.社員の所属組織
 */
@RequiredArgsConstructor
@Getter
public class EmployeeAffiliation {

	/** 社員ID **/
	private final String employeeID;
	/** 社員コード **/
	private final Optional<EmployeeCode> employeeCode;
	/** ビジネスネーム **/
	private final Optional<String> businessName;
	/** 職場ID **/
	private final String workplaceID;
	/** 職場グループID **/
	private final Optional<String> workplaceGroupID;


	/**
	 * 作成する
	 * @param employeeID 社員ID
	 * @param employeeCode 社員コード
	 * @param businessName ビジネスネーム
	 * @param workplaceID 職場ID
	 * @param workplaceGroupID 職場グループID
	 * @return 社員の所属組織
	 */
	public static EmployeeAffiliation create(
			String employeeID, EmployeeCode employeeCode, String businessName
		,	String workplaceID, String workplaceGroupID
	) {
		return new EmployeeAffiliation(
						employeeID
					,	Optional.of(employeeCode)
					,	Optional.of(businessName)
					,	workplaceID
					,	Optional.of(workplaceGroupID)
				);
	}

	/**
	 * 表示情報なしで作成する
	 * @param employeeID 社員ID
	 * @param workplaceID 職場ID
	 * @param workplaceGroupID 職場グループID
	 * @return 社員の所属組織
	 */
	public static EmployeeAffiliation createWithoutInfo(String employeeID, String workplaceID, String workplaceGroupID) {

		return new EmployeeAffiliation(employeeID, Optional.empty(), Optional.empty(), workplaceID, Optional.of(workplaceGroupID));

	}

	/**
	 * 職場グループなしで作成する
	 * @param employeeID 社員ID
	 * @param employeeCode 社員コード
	 * @param businessName ビジネスネーム
	 * @param workplaceID 職場ID
	 * @return 社員の所属組織
	 */
	public static EmployeeAffiliation createWithoutWG(String employeeID, EmployeeCode employeeCode, String businessName, String workplaceID) {

		return new EmployeeAffiliation(employeeID, Optional.of(employeeCode), Optional.of(businessName), workplaceID, Optional.empty());

	}

	/**
	 * 表示情報と職場グループなしで作成する
	 * @param employeeID 社員ID
	 * @param workplaceID 職場ID
	 * @return 社員の所属組織
	 */
	public static EmployeeAffiliation createWithoutInfoAndWG(String employeeID, String workplaceID) {

		return new EmployeeAffiliation(employeeID, Optional.empty(), Optional.empty(), workplaceID, Optional.empty());

	}

}
