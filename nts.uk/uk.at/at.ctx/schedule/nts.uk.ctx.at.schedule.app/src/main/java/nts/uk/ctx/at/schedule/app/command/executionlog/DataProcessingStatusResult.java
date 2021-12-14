package nts.uk.ctx.at.schedule.app.command.executionlog;
/**
 * データ（処理状態付き）
 * @author phongtq
 *
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.schedule.dom.adapter.employmentstatus.EmploymentInfoImported;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ProcessingStatus;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
@AllArgsConstructor
@Getter
public class DataProcessingStatusResult {
	
	private String CID;
	
	/** エラー */
	private ScheduleErrorLog errorLog; 
	
	/** 処理状態 */
	private ProcessingStatus processingStatus;
	
	/** 勤務予定 */
	private WorkSchedule workSchedule;
	
	/** 社員の当日労働条件 */
	private WorkCondItemDto workingCondition;
	
	/** 社員の当日在職状態 */
	private ScheManaStatuTempo statusImported;
}

