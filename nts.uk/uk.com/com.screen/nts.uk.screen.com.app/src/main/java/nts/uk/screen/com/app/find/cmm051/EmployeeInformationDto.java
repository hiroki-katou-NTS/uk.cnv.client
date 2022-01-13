package nts.uk.screen.com.app.find.cmm051;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.screen.com.app.find.user.information.employee.data.management.information.EmployeeDataMngInfoDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeInformationDto {
    List<WorkplaceManagerDto> workplaceManagerList;
    List<EmployeeDataMngInfoDto> listEmployee;
    List<PersonDto> personList;
}
