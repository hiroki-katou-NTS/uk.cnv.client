package nts.uk.ctx.at.request.app.command.application.kdl035;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 振出データ
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubstituteWorkData {
    // データ種類
    private int dataType;

    // 使用期限日
    private GeneralDate expirationDate;

    // 当月期限切れか
    private boolean expiringThisMonth;

    // 振出日
    private GeneralDate substituteWorkDate;

    // 残数
    private double remainingNumber;
}
