package nts.uk.ctx.bs.employee.pub.employee.export.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonEmpBasicInfoDto {
	
	//個人ID
	private String personId;
	
	//社員ID
	private String employeeId;
	
	//ビジネスネーム
	private String businessName;

	//性別
	private int gender;

	//生年月日
	private GeneralDate birthday;

	//社員コード
	private String employeeCode;
	
	//入社年月日
	private GeneralDate jobEntryDate;

	//退職年月日
	private GeneralDate retirementDate;

}
