package nts.uk.ctx.at.function.dom.adapter.holidayover60h;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AggrResultOfHolidayOver60hImport {
    /** 使用回数 */
    private UsedTimes usedTimes;
    /** 60H超休エラー情報 */
    private List<Integer> holidayOver60hErrors;
    /** 60H超休情報（期間終了日時点） */
    private HolidayOver60hInfoImport asOfPeriodEnd;
    /** 60H超休情報（期間終了日の翌日開始時点） */
    private HolidayOver60hInfoImport asOfStartNextDayOfPeriodEnd;
    /** 60H超休情報（消滅） */
    private HolidayOver60hInfoImport lapsed;
}
