package nts.uk.file.at.app.export.holidayconfirmationtable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 紐付け情報
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LinkingInfo {
    // 発生日
    private GeneralDate occurrenceDate;

    // 使用日
    private GeneralDate useDate;

    // 使用日数
    private double useDateNumber;
}
