package nts.uk.ctx.sys.shared.dom.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//社員 <imported>

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAnEmployeeImported {

	// 会社ID
	private String cid;

	// 個人ID
	private String personalId;

	// 社員ID
	private String sid;

	// 社員コード
	private String employeeCode;

}
