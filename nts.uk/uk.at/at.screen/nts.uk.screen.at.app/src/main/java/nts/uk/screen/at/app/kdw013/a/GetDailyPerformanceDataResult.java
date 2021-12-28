package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.DailyActualManHrActualTask;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHrRecordConvertResult;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetDailyPerformanceDataResult {
	// List<日別勤怠(Work)>
	private List<IntegrationOfDaily> lstIntegrationOfDaily;
	// List<日別実績の実績内容>
	private List<ManHrRecordConvertResult> convertRes = new ArrayList<>();
	// List<日別実績の工数実績作業>
	private List<DailyActualManHrActualTask> dailyManHrTasks = new ArrayList<>();
}
