package nts.uk.ctx.sys.gateway.dom.login.password.authenticate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.sys.gateway.dom.login.password.InspectionResult;
import nts.uk.ctx.sys.shared.dom.employee.GetAnEmployeeImported;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author chungnt
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InspectionResultDto {

	// 検証成功
	private boolean verificationSuccess;

	// 社員情報
	private GetAnEmployeeImportedDto employeeInformation;

	// 検証失敗メッセージ
	private String verificationFailureMessage;

	public static InspectionResultDto toInspectionResultDto(InspectionResult param,String password) {

		InspectionResultDto result = new InspectionResultDto();

		result.setVerificationSuccess(param.isVerificationSuccess());
		result.setVerificationFailureMessage(param.getVerificationFailureMessage().map(m -> m).orElse(null));

		if (param.getEmployeeInformation().isPresent()) {

			GetAnEmployeeImported emp = param.getEmployeeInformation().get();
			result.setEmployeeInformation(new GetAnEmployeeImportedDto(emp.getCid(), emp.getPersonalId(), emp.getSid(),
					emp.getEmployeeCode(), password, AppContexts.user().companyCode()));
		}

		return result;
	}
}

@AllArgsConstructor
@Data
class GetAnEmployeeImportedDto {
	// 会社ID
	private String companyId;

	// 個人ID
	private String personalId;

	// 社員ID
	private String employeeId;

	// 社員コード
	private String employeeCode;
	
	private String password;
	
	private String companyCode;
}
