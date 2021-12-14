package nts.uk.file.at.ws.schedule.export.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.importschedule.ImportResult;

/**
 * @author anhnm
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class ImportResultDto {
    /** 1件分の取り込み結果 **/
    private List<ImportResultDetailDto> results;
    /** 取り込み不可日 **/
    private List<String> unimportableDates;
    /** 存在しない社員 **/
    private List<String> unexistsEmployees;

    /** 社員の並び順(OrderdList) **/
    private List<String> orderOfEmployees;
    
    public static ImportResultDto fromDomain(ImportResult domain) {
        return new ImportResultDto(
                domain.getResults().stream().map(x -> ImportResultDetailDto.fromDomain(x)).collect(Collectors.toList()), 
                domain.getUnimportableDates().stream().map(x -> x.toString("yyyy/MM/dd")).collect(Collectors.toList()), 
                domain.getUnexistsEmployees(), 
                domain.getOrderOfEmployees().stream().map(x -> x.v()).collect(Collectors.toList()));
    }
}
