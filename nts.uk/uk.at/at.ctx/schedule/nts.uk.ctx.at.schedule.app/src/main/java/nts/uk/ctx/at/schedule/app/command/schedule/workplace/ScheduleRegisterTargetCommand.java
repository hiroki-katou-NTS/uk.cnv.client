package nts.uk.ctx.at.schedule.app.command.schedule.workplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.find.schedule.workplace.ScheduleRegisterTarget;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ScheduleRegisterTargetCommand {
    
    String employeeId;
    
    String date;
    
    String importCode;
    
    public ScheduleRegisterTarget toDomain() {
        return new ScheduleRegisterTarget(employeeId, GeneralDate.fromString(date, "yyyy/MM/dd"), new ShiftMasterImportCode(importCode));
    }
}
