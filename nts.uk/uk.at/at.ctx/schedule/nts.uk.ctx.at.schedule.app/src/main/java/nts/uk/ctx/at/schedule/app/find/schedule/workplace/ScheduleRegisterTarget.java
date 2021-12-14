package nts.uk.ctx.at.schedule.app.find.schedule.workplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ScheduleRegisterTarget {
    
    private String employeeId;
    
    private GeneralDate date;
    
    private ShiftMasterImportCode importCode;
}
