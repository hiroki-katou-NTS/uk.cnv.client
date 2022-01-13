package nts.uk.ctx.at.function.dom.adapter.child;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.YearMonth;




/**
 * 子の看護休暇当月未来状況
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ChildNursingLeaveThisMonthFutureSituation {

    private YearMonth ym;
    //付与前使用日数←[*1]．本年．集計期間の使用数．日数
    private Double numberOfDaysUsedBeforeGrant;
    //付与前使用時間←[*1]．本年．集計期間の使用数．時間
    private Integer usageTimeBeforeGrant;
    //付与後使用日数←[*1]．翌年．集計期間の使用数．日数(翌年データがある場合にセット)
    private Double daysOfUseAfterGrant;
    // 付与後使用時間←[*1]．翌年．集計期間の使用数．時間(翌年データがある場合にセット)
    private Integer usageTimeAfterGrant;

    //付与前残日数←[*2]．本年．残数．日数
    private Double remainingDaysBeforeGrant;
    //付与前残時間←[*2]．本年．残数．時間
    private Integer remainingTimesBeforeGrant;
    //付与後残日数←[*2]．翌年．残数．日数(翌年データがある場合にセット)
    private Double remainingDaysAfterGrant;
    //付与後残時間←[*2]．翌年．残数．時間(翌年データがある場合にセット)
    private Integer remainingTimesAfterGrant;
    //付与前上限日数←[*2]．本年．上限日数
    private Integer maxNumberOfDaysBeforeGrant;
    //付与後上限日数←[*2]．翌年．上限日数(翌年データがある場合にセット)
    private Integer maxNumberOfDaysAfterGrant;
}
