package nts.uk.ctx.at.schedule.app.command.schedule.workplace;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.schedule.app.find.schedule.workplace.ScheduleRegisterDto;

/**
 * @author anhnm
 *
 */
@Getter
@AllArgsConstructor
public class ScheduleRegisterCommand {

    List<ScheduleRegisterTargetCommand> targets;
    
    boolean overwrite;
    
    public ScheduleRegisterDto toDomain() {
        return new ScheduleRegisterDto(
                targets.stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
                this.overwrite);
    }
}
