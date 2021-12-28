package nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskImport {
    /** 作業コード */
    private final String code;

    /** 作業枠NO */
    private final Integer taskFrameNo;

    /** 作業名称 */
    private final String taskName;

}
