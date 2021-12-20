package nts.uk.screen.com.app.find.cas014;

import lombok.*;
import nts.uk.ctx.bs.employee.pub.employee.EmployeeInfoExport;
import nts.uk.ctx.sys.portal.dom.adapter.roleset.RoleSetDto;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cas014Dto {
    private List<RoleSetGrantedPersonDto> grantedPersonList;
    private List<RoleSetDto> roleSetList;
    private List<EmployeeInfoExport> employeeInfoExportList;
}
