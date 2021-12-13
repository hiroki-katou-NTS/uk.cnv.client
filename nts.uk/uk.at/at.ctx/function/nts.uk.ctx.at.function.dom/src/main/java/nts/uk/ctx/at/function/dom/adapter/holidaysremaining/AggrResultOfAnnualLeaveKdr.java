package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public class AggrResultOfAnnualLeaveKdr {
    /** 年休情報（期間終了日時点） */
    private AnnualLeaveInfoKdr asOfPeriodEnd;
    /** 年休情報（期間終了日の翌日開始時点） */
    private AnnualLeaveInfoKdr asOfStartNextDayOfPeriodEnd;
    /** 年休情報（付与時点） */
    private List<AnnualLeaveInfoKdr> asOfGrant;
    /** 年休情報（消滅） */
    private List<AnnualLeaveInfoKdr> lapsed;
}
