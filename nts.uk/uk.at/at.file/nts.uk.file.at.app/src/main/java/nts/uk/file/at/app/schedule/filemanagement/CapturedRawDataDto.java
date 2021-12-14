package nts.uk.file.at.app.schedule.filemanagement;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.importschedule.CapturedRawData;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CapturedRawDataDto {
    /** 取り込み内容 **/
    private List<CapturedRawDataOfCellDto> contents;

    /** 社員の並び順(OrderdList) **/
    private List<String> employeeCodes;
    
    public static CapturedRawDataDto fromDomain(CapturedRawData domain) {
        return new CapturedRawDataDto(
                domain.getContents().stream().map(x -> CapturedRawDataOfCellDto.fromDomain(x)).collect(Collectors.toList()),
                domain.getEmployeeCodes());
    }
    
    public CapturedRawData toDomain() {
        return new CapturedRawData(
                this.contents.stream().map(x -> x.toDomain()).collect(Collectors.toList()), 
                this.employeeCodes);
    }
}
