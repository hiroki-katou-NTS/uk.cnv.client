package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * 振休確認表の帳票表示内容
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HolidayConfirmationTableContent {
    // 社員コード
    private String employeeCode;

    // 社員名
    private String employeeName;

    // 職場コード
    private String workplaceCode;

    // 職場名称
    private String workplaceName;

    // 階層コード
    private String hierarchicalCode;

    // 振休発生取得情報
    private Optional<HolidayAcquisitionInfo> holidayAcquisitionInfo;
}
