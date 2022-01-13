package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUndigestNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeave;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveMaxRemainingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.HalfDayAnnualLeave;

import java.util.Optional;


@AllArgsConstructor
@Getter
public class AnnualLeaveRemainingKdr {
    /** 年休（マイナスなし） */
    private AnnualLeave annualLeaveNoMinus;
    /** 年休（マイナスあり） */
    private AnnualLeave annualLeaveWithMinus;
    /** 半日年休（マイナスなし） */
    private Optional<HalfDayAnnualLeave> halfDayAnnualLeaveNoMinus;
    /** 半日年休（マイナスあり） */
    private Optional<HalfDayAnnualLeave> halfDayAnnualLeaveWithMinus;
    /** 時間年休（マイナスなし） */
    private Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveNoMinus;
    /** 時間年休（マイナスあり） */
    private Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveWithMinus;
    /** 年休未消化数 */
    private Optional<AnnualLeaveUndigestNumber> annualLeaveUndigestNumber;
}
