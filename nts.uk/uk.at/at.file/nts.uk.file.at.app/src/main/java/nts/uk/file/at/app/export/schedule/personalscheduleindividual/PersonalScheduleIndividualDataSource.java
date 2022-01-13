package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceImport;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.LegalWorkTimeOfEmployee;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.WorkScheduleWorkInforDto;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Data
public class PersonalScheduleIndividualDataSource {
    //会社情報
    private String companyName;

    //職場情報
    private WorkplaceImport workplaceInfo;

    //List＜年月日情報＞
    private List<DateInformation> dateInformationList;

    //Optional<社員の法定労働時間>
    private Optional<LegalWorkTimeOfEmployee> legalWorkTimeOfEmployee;
    //List<勤務予定（勤務情報）dto>
    private List<WorkScheduleWorkInforDto> workInforDtoList;

    //List<週目, 労働時間, 休日日数>
    private List<WeeklyAgreegateResult> agreegateResults;
}