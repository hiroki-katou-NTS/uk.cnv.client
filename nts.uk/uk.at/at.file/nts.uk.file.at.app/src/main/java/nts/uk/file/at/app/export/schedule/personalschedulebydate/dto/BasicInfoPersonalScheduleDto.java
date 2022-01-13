package nts.uk.file.at.app.export.schedule.personalschedulebydate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationImport;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;
import nts.uk.shr.com.company.CompanyInfor;

import java.util.List;

@AllArgsConstructor
@Data
public class BasicInfoPersonalScheduleDto {
    /* 会社情報 */
    private CompanyInfor companyInfo;

    /* 組織の表示情報 */
    private DisplayInfoOrganization displayInfoOrganization;

    /* 年月日情報 */
    private DateInformation dateInformation;

    /* 社員情報リスト */
    private List<EmployeeInformationImport> employeeInfoList;
}
