package nts.uk.ctx.at.request.app.command.application.kdl036;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.app.find.application.common.service.other.output.ActualContentDisplayDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.subhdmana.dto.LeaveComDayOffManaDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Kdl036InputData {
    // 社員ID
    private String employeeId;

    // 申請期間
    private GeneralDate startDate;

    // 申請期間
    private GeneralDate endDate;

    // 日数単位（1 / 0.5） ItemDays
    private double daysUnit;

    // 対象選択区分（自動 / 申請 / 手動） TargetSelectionAtr
    private int targetSelectionAtr;

    // List<表示する実績内容>
    private List<ActualContentDisplayDto> actualContentDisplayList;

    // List<休出代休紐付け管理>
    private List<LeaveComDayOffManaDto> managementData;
}
