package nts.uk.file.at.infra.schedule.personalschedulebydate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HourMinuteInfo {
    private HourMinuteValue start;
    private HourMinuteValue end;
}

@AllArgsConstructor
@Getter
class HourMinuteValue {
    private Integer hour;
    private Integer minute;
}
