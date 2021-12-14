package nts.uk.file.at.ws.schedule.export.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.file.at.app.schedule.filemanagement.MappingErrorDto;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class MappingErrorOutput {
    
    // 社員コード
    private String employeeCode;
    
    // 社員名
    private String employeeName;
    
    // 年月日
    private String date;

    // エラーメッセージ
    private String errorMessage;
    
    public static MappingErrorOutput fromDomain(MappingErrorDto domain) {
        return new MappingErrorOutput(
                domain.getEmployeeCode().orElse(null), 
                domain.getEmployeeName().orElse(null), 
                domain.getDate().map(x -> x.toString("yyyy/MM/dd")).orElse(null), 
                domain.getErrorMessage());
    }
}
