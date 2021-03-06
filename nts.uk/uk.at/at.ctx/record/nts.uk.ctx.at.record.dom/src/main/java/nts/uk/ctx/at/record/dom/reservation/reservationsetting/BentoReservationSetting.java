package nts.uk.ctx.at.record.dom.reservation.reservationsetting;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * 弁当予約設定
 * @author Nguyen Huy Quang
 *
 */

@Getter
public class BentoReservationSetting extends AggregateRoot {

    /**
     * 会社ID
     */
    // The company id.
    private String companyId;

    /**
     * 予約の運用区別
     */
    // operation Distinction.
    private OperationDistinction operationDistinction;

    /**
     * 予約修正内容
     */
    // correction Content.
    private CorrectionContent correctionContent;

    /**
     * 実績集計
     */
    // achievements.
    private Achievements achievements;

    public BentoReservationSetting(
            String companyId, OperationDistinction operationDistinction,
            CorrectionContent correctionContent, Achievements achievements)
    {
        this.companyId = companyId;
        this.operationDistinction = operationDistinction;
        this.correctionContent = correctionContent;
        this.achievements = achievements;
    }

}
