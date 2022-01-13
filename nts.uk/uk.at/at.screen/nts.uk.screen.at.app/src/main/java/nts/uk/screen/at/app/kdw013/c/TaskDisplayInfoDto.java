package nts.uk.screen.at.app.kdw013.c;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Getter
public class TaskDisplayInfoDto {
    /**
     * 名称
     */
    private String taskName;

    /**
     * 略名
     */
    private String taskAbName;

    /**
     * 作業色
     */
    private String color;

    /**
     * 備考
     */
    private String taskNote;
}
