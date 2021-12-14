package nts.uk.ctx.at.request.dom.application.businesstrip;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.WorkInformation;
// 出張勤務情報
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessTripInfo {

    // 勤務情報
    private WorkInformation workInformation;

    // 年月日
    private GeneralDate date;

    // 勤務時間帯
    private List<BusinessTripWorkHour> workingHours;

}
