package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.kdw013.a.WorkRecordDetail;

/**
 * 
 * @author tutt
 *
 */
@Setter
@Getter
public class WorkRecordDetailDto {

	// 年月日
	private GeneralDate date;

	// 社員ID
	private String employeeId;

	// 作業詳細リスト
	private List<WorkDetailsParamDto> lstWorkDetailsParamDto;

	// 実績内容
	private ActualContentDto actualContent;

	public static WorkRecordDetailDto toDto(WorkRecordDetail domain) {
		WorkRecordDetailDto workRecordDetailDto = new WorkRecordDetailDto();

		workRecordDetailDto.setDate(domain.getDate());
		workRecordDetailDto.setEmployeeId(domain.getSID());
		workRecordDetailDto.setLstWorkDetailsParamDto(domain.getLstworkDetailsParam().orElse(null).stream()
				.map(m -> WorkDetailsParamDto.toDto(m)).collect(Collectors.toList()));
		workRecordDetailDto.setActualContent(
				domain.getActualContent().isPresent() == true ? ActualContentDto.toDto(domain.getActualContent().get())
						: null);

		return workRecordDetailDto;
	}

}
