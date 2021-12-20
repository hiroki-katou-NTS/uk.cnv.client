package nts.uk.ctx.at.function.dom.adapter.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffAtWorkplaceExport {
    // 社員ID
    private String employeeId;

    /** The workplace id. */
    // 職場ID
    private String workplaceId;

    // 履歴ID
    private String historyID;

//    //通常職場ID
//    private String normalWorkplaceID;
}
