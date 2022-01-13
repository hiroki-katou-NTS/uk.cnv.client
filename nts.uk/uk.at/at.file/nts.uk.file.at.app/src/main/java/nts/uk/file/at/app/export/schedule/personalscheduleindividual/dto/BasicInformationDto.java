package nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.adapter.workplace.WorkplaceImport;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.LegalWorkTimeOfEmployee;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasicInformationDto<T> {
    //会社情報
    private String companyName;

    //職場情報
    private WorkplaceImport workplaceInfo;

    //List＜年月日情報＞
    private List<DateInformation> dateInformationList;

    //Optional<社員の法定労働時間>
    private Optional<LegalWorkTimeOfEmployee> legalWorkTimeOfEmployee;
}