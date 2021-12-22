package nts.uk.ctx.at.request.app.command.application.kdl036;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 休日出勤データ
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HolidayWorkData {
    // データ種類
    private int dataType;

    // 使用期限日
    private GeneralDate expirationDate;

    // 当月期限切れか
    private boolean expiringThisMonth;

    // 休出日
    private GeneralDate holidayWorkDate;

    // 残数
    private double remainingNumber;
}
