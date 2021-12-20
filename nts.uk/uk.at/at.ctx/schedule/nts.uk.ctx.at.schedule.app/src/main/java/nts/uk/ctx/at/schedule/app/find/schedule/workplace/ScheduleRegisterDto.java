package nts.uk.ctx.at.schedule.app.find.schedule.workplace;

import java.util.List;

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
public class ScheduleRegisterDto {

    private List<ScheduleRegisterTarget> targets;
    
    private boolean overWrite;
}
