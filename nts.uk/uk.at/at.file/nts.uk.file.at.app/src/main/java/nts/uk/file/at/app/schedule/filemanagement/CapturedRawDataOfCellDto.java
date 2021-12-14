package nts.uk.file.at.app.schedule.filemanagement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.importschedule.CapturedRawDataOfCell;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CapturedRawDataOfCellDto {
    /** 社員コード **/
    private String employeeCode;
    /** 年月日 **/
    private String ymd;
    /** 取り込みコード **/
    private String importCode;
    
    public static CapturedRawDataOfCellDto fromDomain(CapturedRawDataOfCell domain) {
        return new CapturedRawDataOfCellDto(
                domain.getEmployeeCode(), 
                domain.getYmd().toString(), 
                domain.getImportCode().v());
    }
    
    public CapturedRawDataOfCell toDomain() {
        return new CapturedRawDataOfCell(
                this.employeeCode, 
                GeneralDate.fromString(this.ymd, "yyyy/MM/dd"), 
                new ShiftMasterImportCode(this.importCode));
    }
}
