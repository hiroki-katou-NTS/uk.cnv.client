package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.app.find.dailyperform.dto.TimeSpanForCalcDto;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrPerformanceTaskBlock;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManHrPerformanceTaskBlockDto {

	/** 時間帯 */
	private TimeSpanForCalcDto caltimeSpan;

	/** 作業詳細 */
	private List<ManHrTaskDetailDto> taskDetails;

	public static ManHrPerformanceTaskBlockDto fromDomain(ManHrPerformanceTaskBlock tb) {

		return new ManHrPerformanceTaskBlockDto(
				new TimeSpanForCalcDto(tb.getCaltimeSpan().getStart().v(), tb.getCaltimeSpan().getEnd().v()),
				tb.getTaskDetails().stream().map(td -> ManHrTaskDetailDto.fromDomain(td)).collect(Collectors.toList()));
	}
}
