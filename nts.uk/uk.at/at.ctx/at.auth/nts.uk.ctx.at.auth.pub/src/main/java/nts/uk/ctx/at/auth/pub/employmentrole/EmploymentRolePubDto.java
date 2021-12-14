package nts.uk.ctx.at.auth.pub.employmentrole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
@Value
@AllArgsConstructor
public class EmploymentRolePubDto {
	/**
	 * 会社ID
	 */
	private String companyId;
	/**
	 * ロールID
	 */
	private String roleId;
	/**
	 * スケジュール画面社員参照
	 */
	private int scheduleEmployeeRef;
	/**
	 * 予約画面社員参照
	 */
	private int bookEmployeeRef;
	/**
	 * 代行者指定時社員参照
	 */
	private  int employeeRefSpecAgent;
	/**
	 * 在席照会社員参照
	 */
	private int presentInqEmployeeRef;
	/**
	 * 未来日参照許可 FUTURE_DATE_REF_PERMIT
	 */
	private int futureDateRefPermit;

}
