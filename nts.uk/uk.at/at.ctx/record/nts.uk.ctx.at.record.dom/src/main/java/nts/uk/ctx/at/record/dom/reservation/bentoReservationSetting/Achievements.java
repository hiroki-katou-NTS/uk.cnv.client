package nts.uk.ctx.at.record.dom.reservation.bentoReservationSetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.DomainObject;

/**
 * 実績集計方法
 */
@Getter
@AllArgsConstructor
public class Achievements extends DomainObject{


    /**
     *基準時間
     */
    // Reference time.
    private ReferenceTime referenceTime;

    /**
     *日別実績の集計
     */
    // Aggregation of daily results.
    private AchievementMethod dailyResults;

    /**
     *月別実績の集計
     */
    // Aggregation of monthly results.
    private AchievementMethod monthlyResults;
}
