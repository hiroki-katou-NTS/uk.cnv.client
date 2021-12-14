package nts.uk.file.at.ws.schedule.export.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.file.at.app.schedule.filemanagement.CaptureDataOutput;

/**
 * @author anhnm
 *
 */
@Getter
@AllArgsConstructor
public class CaptureDataOutputDto {
    
    // 社員リスト　：OrderedList<社員ID, 社員コード, ビジネスネーム>
    private List<PersonEmpBasicInfoImportDto> listPersonEmp;
    
    // 年月日リスト：OrderedList<年月日, 曜日>
    private List<String> importableDates;
    
    // 祝日リスト　：List<祝日>
    private List<PublicHolidayDto> holidays;
    
    // 取り込み結果
    private ImportResultDto importResult;
    
    // エラーリスト：List<取り込みエラーDto>
    private List<MappingErrorOutput> mappingErrorList;
    
    public static CaptureDataOutputDto fromDomain(CaptureDataOutput domain) {
        return new CaptureDataOutputDto(
                domain.getListPersonEmp().stream().map(x -> PersonEmpBasicInfoImportDto.fromDomain(x)).collect(Collectors.toList()), 
                domain.getImportableDates().stream().map(x -> x.toString("yyyy/MM/dd")).collect(Collectors.toList()), 
                domain.getHolidays().stream().map(x -> PublicHolidayDto.fromDomain(x)).collect(Collectors.toList()), 
                ImportResultDto.fromDomain(domain.getImportResult()), 
                domain.getMappingErrorList().stream().map(x -> MappingErrorOutput.fromDomain(x)).collect(Collectors.toList()));
    }
}
