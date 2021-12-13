package nts.uk.screen.com.app.find.cmm051;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.screen.com.app.find.user.information.employee.data.management.information.EmployeeDataMngInfoDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeInfoAndWorkplaceInfoDto {
   private List<WorkplaceInforParam> workplaceInfors;
   private List<EmployeeDataMngInfoDto> listEmployee;
   private List<PersonDto> personList;
}
