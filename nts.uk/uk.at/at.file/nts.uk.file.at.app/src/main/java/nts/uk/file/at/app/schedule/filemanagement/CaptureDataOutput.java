package nts.uk.file.at.app.schedule.filemanagement;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.importschedule.ImportResult;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.holiday.PublicHoliday;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class CaptureDataOutput {

    // 社員リスト　：OrderedList<社員ID, 社員コード, ビジネスネーム>
    private List<PersonEmpBasicInfoImport> listPersonEmp;
    
    // 年月日リスト：OrderedList<年月日, 曜日>
    private List<GeneralDate> importableDates;
    
    // 祝日リスト　：List<祝日>
    private List<PublicHoliday> holidays;
    
    // 取り込み結果
    private ImportResult importResult;
    
    // エラーリスト：List<取り込みエラーDto>
    private List<MappingErrorDto> mappingErrorList; 
}
