package nts.uk.ctx.at.function.dom.holidaysremaining.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.YearMonth;

/**
 * 特別休暇過去状況
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SpecialVacationPastSituation {
    /**	社員ID */
    private String sid;
    /**	 年月*/
    private YearMonth ym;
    /**	特別休暇コード */
    private int specialHolidayCd;
    //使用日数←[*1]．使用数．合計．　使用日数
    private double useDays;
    //使用時間←[*1]．使用数．合計．　使用時間
    private int useTimes;
    //残日数　←[*1]．残数．　付与後．日数
    private double remainDays;
    //残時間　←[*1]．残数．　付与後．時間
    private int remainTimes;

   // [*1]　特別休暇月別残数データ．実特別休暇
}
