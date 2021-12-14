/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.executionlog;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.app.command.executionlog.internal.ScheduleErrorLogGeterCommand;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContent;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLog;

/**
 * The Class ScheduleCreatorExecutionCommand.
 */
@Data
public class ScheduleCreatorExecutionCommand {
	
	/** The employee id. */
	private String employeeId;
	
	/** The execution id. */
	private String executionId;
	
	/** The company id. */
	private String companyId;
	
	/** The content. */
	private ScheduleCreateContent content;
	
	/** The is confirm. */
//	private Boolean confirm; 
	
	/** The is delete befor insert. */
//	private Boolean isDeleteBeforInsert;

	private ScheduleExecutionLog scheduleExecutionLog;

	private PersonalSchedule personalSchedule;

	private boolean isAutomatic;

	private List<String> employeeIds;
	
//	@Getter
//	@Setter
//	private CountDownLatch countDownLatch;
	
	@Getter
	@Setter
	private Boolean isExForKBT = false;
	
	@Setter
	@Getter
	private Object companySetting;

	/**
	 * To base command.
	 *
	 * @return the schedule error log geter command
	 */
	public ScheduleErrorLogGeterCommand toBaseCommand(GeneralDate date) {
		ScheduleErrorLogGeterCommand command = new ScheduleErrorLogGeterCommand();
		command.setCompanyId(this.companyId);
		command.setExecutionId(this.executionId);
		command.setToDate(date);
		return command;
	}
}
