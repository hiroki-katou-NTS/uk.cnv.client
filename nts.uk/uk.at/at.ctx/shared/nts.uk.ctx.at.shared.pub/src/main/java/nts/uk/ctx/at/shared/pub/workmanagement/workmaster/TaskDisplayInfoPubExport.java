package nts.uk.ctx.at.shared.pub.workmanagement.workmaster;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskDisplayInfoPubExport {
    private String taskName;

    /** 略名　*/
    private String taskAbName;

    /** 作業色　*/
    private String color;

    /** 備考　*/
    private String taskNote;
}
