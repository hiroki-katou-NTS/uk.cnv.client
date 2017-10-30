package nts.uk.ctx.at.schedule.app.find.executionlog.dto;

import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.schedule.dom.executionlog.CreateMethodAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ImplementAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ProcessExecutionAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ReCreateAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContentSetMemento;

/**
 * The Class ExecutionContentDto.
 */
@Setter
public class ScheduleCreateContentDto implements ScheduleCreateContentSetMemento {

	/** The execution id. */
	public String executionId;

	/** The copy start date. */
	public GeneralDate copyStartDate;

	/** The create method atr. */
	public Integer createMethodAtr;

	/** The confirm. */
	public Boolean confirm;

	/** The implement atr. */
	public Integer implementAtr;

	/** The process execution atr. */
	public Integer processExecutionAtr;

	/** The re create atr. */
	public Integer reCreateAtr;

	/** The reset master info. */
	public Boolean resetMasterInfo;

	/** The reset absent holiday business. */
	public Boolean resetAbsentHolidayBusines;

	/** The reset working hours. */
	public Boolean resetWorkingHours;

	/** The reset time assignment. */
	public Boolean resetTimeAssignment;

	/** The reset direct line bounce. */
	public Boolean resetDirectLineBounce;

	/** The reset time child care. */
	public Boolean resetTimeChildCare;
	
	/** The start date. */
	public GeneralDate startDate;
	
	/** The end date. */
	public GeneralDate endDate;
	
	/** The execution start. */
	public GeneralDateTime executionStart;
	
	/** The execution end. */
	public GeneralDateTime executionEnd;
	
	/** The count execution. */
	public Integer countExecution;
	
	/** The count error. */
	public Integer countError;

	@Override
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	@Override
	public void setCopyStartDate(GeneralDate copyStartDate) {
		this.copyStartDate = copyStartDate;
	}

	@Override
	public void setCreateMethodAtr(CreateMethodAtr createMethodAtr) {
		if (createMethodAtr == null) {
			this.createMethodAtr = null;
		} else {
			this.createMethodAtr = createMethodAtr.value;
		}
	}

	@Override
	public void setConfirm(Boolean confirm) {
		this.confirm = confirm;
	}

	@Override
	public void setImplementAtr(ImplementAtr implementAtr) {
		this.implementAtr = implementAtr.value;
	}

	@Override
	public void setProcessExecutionAtr(ProcessExecutionAtr processExecutionAtr) {
		if (processExecutionAtr == null) {
			this.processExecutionAtr = null;
		} else {
			this.processExecutionAtr = processExecutionAtr.value;
		}
	}

	@Override
	public void setReCreateAtr(ReCreateAtr reCreateAtr) {
		if (reCreateAtr == null) {
			this.reCreateAtr = null;
		} else {
			this.reCreateAtr = reCreateAtr.value;
		}
	}

	@Override
	public void setResetMasterInfo(Boolean resetMasterInfo) {
		this.resetMasterInfo = resetMasterInfo;
	}

	@Override
	public void setResetAbsentHolidayBusines(Boolean resetAbsentHolidayBusines) {
		this.resetAbsentHolidayBusines = resetAbsentHolidayBusines;
	}

	@Override
	public void setResetWorkingHours(Boolean resetWorkingHours) {
		this.resetWorkingHours = resetWorkingHours;
	}

	@Override
	public void setResetTimeAssignment(Boolean resetTimeAssignment) {
		this.resetTimeAssignment = resetTimeAssignment;
	}

	@Override
	public void setResetDirectLineBounce(Boolean resetDirectLineBounce) {
		this.resetDirectLineBounce = resetDirectLineBounce;
	}

	@Override
	public void setResetTimeChildCare(Boolean resetTimeChildCare) {
		this.resetTimeChildCare = resetTimeChildCare;
	}

}
