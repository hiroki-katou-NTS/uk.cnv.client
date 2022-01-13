package nts.uk.ctx.at.function.app.query.holidayconfirmationtable;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

/**
 * Temporary: 代休確認表の帳票表示内容
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DisplayContentsOfSubLeaveConfirmationTable {
    /**
     *  社員コード
     */
    private String employeeCode;

    /**
     * 社員名
     */
    private String employeeName;
    /**
     * 職場コード
     */
    private String workplaceCode;

    /**
     * 職場名称
     */
    private String workplaceName;

    /**
     * 階層コード
     */
    private String hierarchyCode;

    /**
     * 代休発生取得情報
     */
    private Optional<SubstituteHolidayOccurrenceInfo> observationOfExitLeave;

}
