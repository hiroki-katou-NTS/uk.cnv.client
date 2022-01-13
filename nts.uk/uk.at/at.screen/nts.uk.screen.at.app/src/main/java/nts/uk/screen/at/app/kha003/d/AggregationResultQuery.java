package nts.uk.screen.at.app.kha003.d;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.MasterNameInformation;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.WorkDetailData;
import nts.uk.screen.at.app.kha003.b.ManHourPeriod;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class AggregationResultQuery {
    private String code;
    private MasterNameInformation masterNameInfo;
    private List<WorkDetailDataDto> workDetailList;
    private ManHourPeriod period;

    public List<WorkDetailData> toWorkDetailData() {
        return workDetailList.stream().map(x -> new WorkDetailData(
                x.getEmployeeId(),
                GeneralDate.fromString(x.getDate(), "yyyy/MM/dd"),
                x.getSupportWorkFrameNo(),
                x.getAffWorkplaceId(),
                x.getWorkplaceId(),
                x.getWorkCode1(),
                x.getWorkCode2(),
                x.getWorkCode3(),
                x.getWorkCode4(),
                x.getWorkCode5(),
                x.getTotalWorkingHours()
        )).collect(Collectors.toList());
    }
}

@Getter
@AllArgsConstructor
class WorkDetailDataDto {
    /** 社員ID */
    private String employeeId;
    /** 年月日 */
    private String date;
    /** 応援勤務枠NO */
    private int supportWorkFrameNo;
    /** 所属職場ID */
    private String affWorkplaceId;
    /** 勤務職場ID */
    private String workplaceId;
    /** 作業コード1 */
    private String workCode1;
    /** 作業コード2 */
    private String workCode2;
    /** 作業コード3 */
    private String workCode3;
    /** 作業コード4 */
    private String workCode4;
    /** 作業コード5 */
    private String workCode5;
    /** 総労働時間 */
    private int totalWorkingHours;
}
