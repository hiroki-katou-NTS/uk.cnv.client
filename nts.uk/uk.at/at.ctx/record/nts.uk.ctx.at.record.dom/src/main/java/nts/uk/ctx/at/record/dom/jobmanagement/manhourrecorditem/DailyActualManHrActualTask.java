package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * Temporary 日別実績の工数実績作業
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@Getter
@Setter
public class DailyActualManHrActualTask {

	/** 年月日 */
	private final GeneralDate date;

	/** 作業ブロック */
	private final List<ManHrPerformanceTaskBlock> taskBlocks;
}
