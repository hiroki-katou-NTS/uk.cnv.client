package nts.uk.file.at.ws.schedule.export.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;

@Getter
@Setter
@AllArgsConstructor
public class PersonEmpBasicInfoImportDto {

    // 個人ID
    private String personId;

    // 社員ID
    private String employeeId;

    // ビジネスネーム
    private String businessName;

    // 性別
    private int gender;

    // 生年月日
    private String birthday;

    // 社員コード
    private String employeeCode;

    // 入社年月日
    private String jobEntryDate;

    // 退職年月日
    private String retirementDate;
    
    public static PersonEmpBasicInfoImportDto fromDomain(PersonEmpBasicInfoImport domain) {
        return new PersonEmpBasicInfoImportDto(
                domain.getPersonId(), 
                domain.getEmployeeId(), 
                domain.getBusinessName(), 
                domain.getGender(), 
                domain.getBirthday().toString("yyyy/MM/dd"), 
                domain.getEmployeeCode(), 
                domain.getJobEntryDate().toString("yyyy/MM/dd"), 
                domain.getRetirementDate().toString("yyyy/MM/dd"));
    }
}
