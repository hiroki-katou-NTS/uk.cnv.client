package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author chungnt
 *
 */

@NoArgsConstructor
@Getter
@Setter
public class GetStartupProcessingInformationDto extends IndividualDisplayControlDto{

	public String employeeId; // 社員ID
	public String employeeCode; // 社員コード
	public String businessName; // ビジネスネーム
	public int yearMonth; //年月
	public GetStartupProcessingInformationDto(IndividualDisplayControlDto individualDisplayControl,
			String employeeId, String employeeCode, String businessName, int yearMonth) {
		super(individualDisplayControl.scheModifyAuthCtrlCommon, individualDisplayControl.scheModifyAuthCtrlByPerson, individualDisplayControl.scheFunctionControl);
		this.employeeId = employeeId;
		this.employeeCode = employeeCode;
		this.businessName = businessName;
		this.yearMonth = yearMonth;
	}
	
}
