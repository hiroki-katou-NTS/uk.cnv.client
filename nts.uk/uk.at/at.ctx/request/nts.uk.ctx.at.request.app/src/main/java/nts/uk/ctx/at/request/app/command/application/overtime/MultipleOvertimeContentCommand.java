package nts.uk.ctx.at.request.app.command.application.overtime;

import lombok.Data;

@Data
public class MultipleOvertimeContentCommand {
    private int frameNo;
    private Integer startTime;
    private Integer endTime;
    private Integer fixedReasonCode;
    private String appReason;
}
