package nts.uk.ctx.at.request.dom.application.overtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;

import java.util.Optional;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.残業休出申請.残業申請
 * 残業理由
 */

@Getter
@AllArgsConstructor
public class OvertimeReason {
    // 残業回数
    private OvertimeNumber overtimeNumber;

    // 定型理由
    private Optional<AppStandardReasonCode> fixedReasonCode;

    // 申請理由
    private Optional<AppReason> applyReason;
}
