package nts.uk.screen.com.app.find.cas014;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleSetGrantedPersonDto {
    private String roleSetCd;

    private String employeeID;

    private GeneralDate startDate;

    private GeneralDate endDate;
}
