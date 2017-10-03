/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.executionlog;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.schedule.dom.executionlog.CompletionStatus;
import nts.uk.ctx.at.schedule.dom.executionlog.CreateMethodAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionContent;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionContentGetMemento;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionDateTime;
import nts.uk.ctx.at.schedule.dom.executionlog.ImplementAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ProcessExecutionAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ReCreateAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLogGetMemento;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.workrule.closure.Period;

@Getter
@Setter
public class ScheduleExecutionLogSaveCommand {

	/** The target start date. */
	private GeneralDate targetStartDate;
	
	/** The period start date. */
	private GeneralDate periodStartDate;
	
	/** The implement atr. */
	private int implementAtr;
	
	/** The re create atr. */
	private int reCreateAtr;
	
	/** The process execution atr. */
	private int processExecutionAtr;
	
	/** The reset working hours. */
	private boolean resetWorkingHours;
	
	/** The reset direct line bounce. */
	private boolean resetDirectLineBounce;
	
	/** The reset master info. */
	private boolean resetMasterInfo;
	
	/** The reset time child care. */
	private boolean resetTimeChildCare;
	
	/** The reset absent holiday busines. */
	private boolean resetAbsentHolidayBusines;
	
	/** The reset time assignment. */
	private boolean resetTimeAssignment;
	
	/** The confirm. */
	private boolean confirm;
	
	/** The create method atr. */
	private int createMethodAtr;
	
	/** The period end date. */
	private GeneralDate copyStartDate;
	
	
	/**
	 * The Class ScheduleExecutionLogSaveGetMementoImpl.
	 */
	class ScheduleExecutionLogSaveGetMementoImpl implements ScheduleExecutionLogGetMemento{
		
		/** The company id. */
		private String companyId;
		
		/** The execution id. */
		private String executionId;
		
		/** The employee id. */
		private String employeeId;

		
		/**
		 * Instantiates a new schedule execution log save get memento impl.
		 *
		 * @param companyId the company id
		 * @param executionId the execution id
		 * @param employeeId the employee id
		 */
		public ScheduleExecutionLogSaveGetMementoImpl(String companyId, String executionId,String employeeId) {
			this.companyId = companyId;
			this.executionId = executionId;
			this.employeeId = employeeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getCompanyId()
		 */
		@Override
		public CompanyId getCompanyId() {
			return new CompanyId(companyId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getCompletionStatus()
		 */
		@Override
		public CompletionStatus getCompletionStatus() {
			return CompletionStatus.INCOMPLETE;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getExecutionId()
		 */
		@Override
		public String getExecutionId() {
			return executionId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getExecutionContent()
		 */
		@Override
		public ExecutionContent getExecutionContent() {
			return new ExecutionContent().toDomain(new ExecutionContentGetMemento() {
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getResetWorkingHours()
				 */
				@Override
				public Boolean getResetWorkingHours() {
					return resetWorkingHours;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getResetTimeChildCare()
				 */
				@Override
				public Boolean getResetTimeChildCare() {
					return resetTimeChildCare;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getResetTimeAssignment()
				 */
				@Override
				public Boolean getResetTimeAssignment() {
					return resetTimeAssignment;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getResetMasterInfo()
				 */
				@Override
				public Boolean getResetMasterInfo() {
					return resetMasterInfo;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getResetDirectLineBounce()
				 */
				@Override
				public Boolean getResetDirectLineBounce() {
					return resetDirectLineBounce;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getResetAbsentHolidayBusines()
				 */
				@Override
				public Boolean getResetAbsentHolidayBusines() {
					return resetAbsentHolidayBusines;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getReCreateAtr()
				 */
				@Override
				public ReCreateAtr getReCreateAtr() {
					return ReCreateAtr.valueOf(reCreateAtr);
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getProcessExecutionAtr()
				 */
				@Override
				public ProcessExecutionAtr getProcessExecutionAtr() {
					return ProcessExecutionAtr.valueOf(processExecutionAtr);
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getImplementAtr()
				 */
				@Override
				public ImplementAtr getImplementAtr() {
					return ImplementAtr.valueOf(implementAtr);
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getCreateMethodAtr()
				 */
				@Override
				public CreateMethodAtr getCreateMethodAtr() {
					return CreateMethodAtr.valueOf(createMethodAtr);
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getCopyStartDate()
				 */
				@Override
				public GeneralDate getCopyStartDate() {
					return copyStartDate;
				}
				
				/*
				 * (non-Javadoc)
				 * 
				 * @see nts.uk.ctx.at.schedule.dom.executionlog.
				 * ExecutionContentGetMemento#getConfirm()
				 */
				@Override
				public Boolean getConfirm() {
					return confirm;
				}
			});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getExecutionDateTime()
		 */
		@Override
		public ExecutionDateTime getExecutionDateTime() {
			return new ExecutionDateTime(GeneralDateTime.now(), GeneralDateTime.now());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getExecutionEmployeeId()
		 */
		@Override
		public String getExecutionEmployeeId() {
			return employeeId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.executionlog.
		 * ScheduleExecutionLogGetMemento#getPeriod()
		 */
		@Override
		public Period getPeriod() {
			return new Period(periodStartDate, periodStartDate);
		}
		
	}
	
}
