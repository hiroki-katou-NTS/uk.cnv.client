package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureDateDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PersonalScheduleByWkpQuery {
    // 対象組織: 対象組織識別情報
    private int orgUnit;
    private String orgId;

    // 対象期間: 対象期間
    private GeneralDate periodStart;
    private GeneralDate periodEnd;

    // 社員IDリスト: List(社員ID)
    private List<String> employeeIds;

    // 出力設定コード
    private String outputSettingCode;

    // コメント欄
    private String comment;

    // Excel形式で出力か
    private boolean excel;

    private boolean preview;

    // 締め日
    private ClosureDateDto closureDate;
}
