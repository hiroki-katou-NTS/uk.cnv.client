package nts.uk.ctx.at.shared.pub.workmanagement.workmaster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.calendar.period.DatePeriod;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskPubExport {

    // コード
    private final String code;

    // 作業枠NO
    private final Integer taskFrameNo;

    // 外部連携情報
    private ExternalCorporationInfoPubExport cooperationInfo;

    // 子作業一覧
    private List<String> childTaskList;

    // 有効期限
    private DatePeriod expirationDate;

    // 表示情報 : 作業表示情報
    private TaskDisplayInfoPubExport displayInfo;
}
