package nts.uk.ctx.at.function.dom.adapter.holidayover60h;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HolidayOver60hRemainingNumberImport {
    /** 60H超休（マイナスあり）. 使用時間 */
    private AnnualLeaveUsedTime usedTimeWithMinus;

    /** 60H超休（マイナスあり）. 残時間 */
    private AnnualLeaveRemainingTime remainingTimeWithMinus;

    /** 60H超休（マイナスなし）. 使用時間 */
    private AnnualLeaveUsedTime usedTimeNoMinus;

    /** 60H超休（マイナスなし）. 残時間 */
    private AnnualLeaveRemainingTime remainingTimeNoMinus;
    /**
     * 繰越時間
     */
    private AnnualLeaveRemainingTime carryForwardTimes;
    /**
     * 未消化数
     */
    private AnnualLeaveRemainingTime holidayOver60hUndigestNumber;

}
