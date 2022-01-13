package nts.uk.screen.at.app.ktgwidget.find.dto;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import nts.uk.ctx.at.record.app.find.monthly.root.AgreementTimeOfManagePeriodDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;


@Data
@Builder
public class AcquisitionOfOvertimeHoursOfEmployeesDto {
	
	//	配下社員の個人情報
	private List<PersonEmpBasicInfoImport> personalInformationOfSubordinateEmployees;
	
	//	配下社員の時間外時間
	private List<AgreementTimeOfManagePeriodDto> overtimeOfSubordinateEmployees;
}
