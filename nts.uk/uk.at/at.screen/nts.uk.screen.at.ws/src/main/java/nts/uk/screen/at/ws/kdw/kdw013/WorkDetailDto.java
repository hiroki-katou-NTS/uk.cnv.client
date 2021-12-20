package nts.uk.screen.at.ws.kdw.kdw013;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.command.workrecord.workrecord.WorkDetail;

/**
 * 
 * @author tutt
 *
 */
@Getter
@Setter
public class WorkDetailDto {
	/** 年月日 */
	private GeneralDate date;

	/** List<作業詳細> */
	private List<WorkDetailsParamDto> lstWorkDetailsParam;

	public static WorkDetail toDomain(WorkDetailDto dto) {
		return new WorkDetail(dto.getDate(), dto.getLstWorkDetailsParam().stream()
				.map(m -> WorkDetailsParamDto.toDomain(m)).collect(Collectors.toList()));
	}
}
