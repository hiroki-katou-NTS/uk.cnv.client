package nts.uk.ctx.at.shared.app.query.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskDto {

    private String baseDate;

    private Integer taskFrameNo;
    
    private String sid;
    
    private String taskCode;
}

