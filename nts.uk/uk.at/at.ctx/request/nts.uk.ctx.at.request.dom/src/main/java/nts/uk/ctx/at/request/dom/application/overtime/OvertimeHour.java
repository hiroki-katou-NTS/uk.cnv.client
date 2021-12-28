package nts.uk.ctx.at.request.dom.application.overtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.残業休出申請.残業申請
 * 残業時間帯
 */

@Getter
@AllArgsConstructor
public class OvertimeHour {
    // 残業回数
    private OvertimeNumber overtimeNumber;

    // 残業時間帯
    private TimeSpanForCalc overtimeHours;
}
