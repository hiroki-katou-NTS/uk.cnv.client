package nts.uk.ctx.at.schedule.app.find.schedule.workplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterWorkScheduleOutput {
    
    private String employeeCode;
    
    private String employeename;
    
    private String date;
    
    private int errorItemId;
    
    private String errorMessage;
}
