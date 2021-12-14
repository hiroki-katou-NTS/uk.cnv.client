package nts.uk.ctx.at.schedule.app.command.executionlog;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.ShortWorkTimeDto;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLog;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;

/**
 * 勤務情報・勤務時間を用意する Output
 * @author phongtq
 *
 */

@Getter
@Setter
@NoArgsConstructor
public class PrepareWorkOutput {
	
	/** 勤務情報 */
	private WorkInformation information;
	
	/** 勤務予定時間帯 */
	private List<TimezoneUse> scheduleTimeZone;
	
	/** 社員の短時間勤務 */
	private List<ShortWorkTimeDto> lstWorkTimeDto;
	
	/** スケジュール作成ログ＜Optional＞ */
	private Optional<ScheduleErrorLog> executionLog;
	
	/**  */
	private Optional<WorkType> workType;

	public PrepareWorkOutput(WorkInformation information, List<TimezoneUse> scheduleTimeZone,
			List<ShortWorkTimeDto> lstWorkTimeDto, Optional<ScheduleErrorLog> executionLog) {
		super();
		this.information = information;
		this.scheduleTimeZone = scheduleTimeZone;
		this.lstWorkTimeDto = lstWorkTimeDto;
		this.executionLog = executionLog;
		this.workType = Optional.empty();
	}

	public PrepareWorkOutput(WorkInformation information, List<TimezoneUse> scheduleTimeZone,
			List<ShortWorkTimeDto> workTimeDto, Optional<ScheduleErrorLog> executionLog, Optional<WorkType> workType) {
		super();
		this.information = information;
		this.scheduleTimeZone = scheduleTimeZone;
		this.lstWorkTimeDto = workTimeDto;
		this.executionLog = executionLog;
		this.workType = workType;
	}
	
	
}
