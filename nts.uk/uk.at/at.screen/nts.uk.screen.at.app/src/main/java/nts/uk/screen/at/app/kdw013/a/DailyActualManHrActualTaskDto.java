package nts.uk.screen.at.app.kdw013.a;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.DailyActualManHrActualTask;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyActualManHrActualTaskDto {
	/** 年月日 */
	private GeneralDate date;

	/** 作業ブロック */
	private List<ManHrPerformanceTaskBlockDto> taskBlocks;

	public static DailyActualManHrActualTaskDto fromDomain(DailyActualManHrActualTask dh) {

		return new DailyActualManHrActualTaskDto(dh.getDate(), dh.getTaskBlocks().stream()
				.map(tb -> ManHrPerformanceTaskBlockDto.fromDomain(tb)).collect(Collectors.toList()));
	}
}
