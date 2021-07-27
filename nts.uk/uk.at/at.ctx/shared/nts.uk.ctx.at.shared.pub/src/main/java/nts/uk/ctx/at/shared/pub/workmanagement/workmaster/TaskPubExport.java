package nts.uk.ctx.at.shared.pub.workmanagement.workmaster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskPubExport {

    private String code;

    // 作業枠NO
    private Integer taskFrameNo;

    private String name;
}
