/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.shift.autocalsettingjob;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.shift.autocalsetting.AutoCalFlexOvertimeSetting;
import nts.uk.ctx.at.schedule.dom.shift.autocalsetting.AutoCalOvertimeSetting;
import nts.uk.ctx.at.schedule.dom.shift.autocalsetting.AutoCalRestTimeSetting;
import nts.uk.ctx.at.schedule.dom.shift.autocalsetting.JobAutoCalSetting;
import nts.uk.ctx.at.schedule.dom.shift.autocalsetting.JobAutoCalSettingGetMemento;
import nts.uk.ctx.at.schedule.dom.shift.autocalsetting.PositionId;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Class JobAutoCalSetCommand.
 */

/**
 * Sets the rest time.
 *
 * @param restTime the new rest time
 */
@Setter

/**
 * Gets the rest time.
 *
 * @return the rest time
 */

/**
 * Gets the rest time.
 *
 * @return the rest time
 */
@Getter
public class JobAutoCalSetCommand {
	
	/** The job ID. */
	private String jobId;
	
	/** The normal OT time. */
	// 残業時間
	private AutoCalOvertimeSettingDto normalOTTime;

	/** The flex OT time. */
	// フレックス超過時間
	private AutoCalFlexOvertimeSettingDto flexOTTime;

	/** The rest time. */
	// 休出時間
	private AutoCalRestTimeSettingDto restTime;

	/**
	 * To domain.
	 *
	 * @param companyId the company id
	 * @return the job auto cal set command
	 */
	public JobAutoCalSetting toDomain(String companyId) {
		return new JobAutoCalSetting(new DtoGetMemento(companyId, this));
	}

	/**
	 * The Class DtoGetMemento.
	 */
	private class DtoGetMemento implements JobAutoCalSettingGetMemento {

		/** The company id. */
		private String companyId;
		
		

		/** The command. */
		private JobAutoCalSetCommand command;

		/**
		 * Instantiates a new dto get memento.
		 *
		 * @param companyId the company id
		 * @param command the command
		 */
		public DtoGetMemento(String companyId, JobAutoCalSetCommand command) {
			this.companyId = companyId;
			this.command = command;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.at.schedule.dom.shift.totaltimes.TotalTimesGetMemento#
		 * getCompanyId()
		 */
		@Override
		public CompanyId getCompanyId() {
			return new CompanyId(this.companyId);
		}
		
		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.JobAutoCalSettingGetMemento#getPositionId()
		 */
		@Override
		public PositionId getPositionId() {
			return new PositionId(this.command.getJobId());
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * ComAutoCalSettingGetMemento#getNormalOTTime()
		 */
		@Override
		public AutoCalOvertimeSetting getNormalOTTime() {
			return this.command.getNormalOTTime().toDomain(companyId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * ComAutoCalSettingGetMemento#getFlexOTTime()
		 */
		@Override
		public AutoCalFlexOvertimeSetting getFlexOTTime() {
			return this.command.getFlexOTTime().toDomain(companyId);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see nts.uk.ctx.at.schedule.dom.shift.autocalsetting.
		 * ComAutoCalSettingGetMemento#getRestTime()
		 */
		@Override
		public AutoCalRestTimeSetting getRestTime() {
			return this.command.getRestTime().toDomain(companyId);
		}

	}

}
