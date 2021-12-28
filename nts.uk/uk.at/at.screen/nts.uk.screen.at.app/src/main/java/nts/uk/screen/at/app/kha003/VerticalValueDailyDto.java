package nts.uk.screen.at.app.kha003;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerticalValueDailyDto {
    /** 作業時間 */
    private String workingHours;
    /** 年月 */
    private String yearMonth;
    /** 年月日 */
    private String date;
}
