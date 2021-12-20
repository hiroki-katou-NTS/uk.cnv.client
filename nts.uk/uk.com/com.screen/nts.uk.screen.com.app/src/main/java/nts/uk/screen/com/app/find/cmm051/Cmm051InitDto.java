package nts.uk.screen.com.app.find.cmm051;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.portal.dom.notice.adapter.WorkplaceInfoImport;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Cmm051InitDto {
    private EmployeeInformationDto employeeInformation;
    private WorkplaceInfoImport workplaceInfoImport;
}
