package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.aggregation.dom.scheduletable.OneDayEmployeeAttendanceInfo;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTableOutputSetting;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTablePersonalInfo;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonalScheduleByWkpDataSource<T> {
    private int orgUnit;

    private DatePeriod period;

    // 会社情報
    private String companyName;

    // スケジュール表の出力設定
    private ScheduleTableOutputSetting outputSetting;

    // 組織の表示情報
    private DisplayInfoOrganization organizationDisplayInfo;

    // List<年月日情報>
    private List<DateInformation> dateInfos;

    // List<スケジュール表の個人情報>
    private List<ScheduleTablePersonalInfo> personalInfoScheduleTableList;

    // List<一日分の社員の勤怠表示情報　dto>
    private List<OneDayEmployeeAttendanceInfo> listEmpOneDayAttendanceInfo;

    // 個人計集計結果
    private Map<PersonalCounterCategory, Map<String, T>> personalTotalResult;

    // 職場計集計結果
    private Map<WorkplaceCounterCategory, Map<GeneralDate, T>> workplaceTotalResult;
}
