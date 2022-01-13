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
	 * 未来日参照許可 FUTURE_DATE_REF_PERMIT
	 */
	private int futureDateRefPermit;
}
