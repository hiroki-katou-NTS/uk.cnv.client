package nts.uk.file.at.ws.schedule.export.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.importschedule.ImportResultDetail;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ImportResultDetailDto {
    /** 社員ID **/
    private String employeeId;
    /** 年月日 **/
    private String ymd;
    /** 取り込みコード **/
    private String importCode;
    /** 状態 **/
    private int status;
    
    public static ImportResultDetailDto fromDomain(ImportResultDetail domain) {
        return new ImportResultDetailDto(
                domain.getEmployeeId().v(), 
                domain.getYmd().toString("yyyy/MM/dd"), 
                domain.getImportCode().v(), 
                domain.getStatus().getValue());
    }
}
