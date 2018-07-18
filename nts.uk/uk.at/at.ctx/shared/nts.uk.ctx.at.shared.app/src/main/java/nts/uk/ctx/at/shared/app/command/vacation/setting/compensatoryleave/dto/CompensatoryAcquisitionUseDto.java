/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.vacation.setting.compensatoryleave.dto;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.vacation.setting.ApplyPermission;
import nts.uk.ctx.at.shared.dom.vacation.setting.ExpirationTime;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUse;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUseGetMemento;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.DeadlCheckMonth;

/**
 * The Class CompensatoryAcquisitionUseDto.
 */
@Getter
@Setter
public class CompensatoryAcquisitionUseDto {

	/** The expiration time. */
	private Integer expirationTime;

	/** The preemption permit. */
	private Integer preemptionPermit;
	
	/** The deadl check month. */
	private Integer deadlCheckMonth;

	/**
	 * To domain.
	 *
	 * @return the compensatory acquisition use
	 */
	public CompensatoryAcquisitionUse toDomain() {
		return new CompensatoryAcquisitionUse(new CompensatoryAcquisitionUseGetMementoImpl(this));
	}

	/**
	 * The Class CompensatoryAcquisitionUseGetMementoImpl.
	 */
	public class CompensatoryAcquisitionUseGetMementoImpl implements CompensatoryAcquisitionUseGetMemento {

		/** The compensatory acquisition use dto. */
		private CompensatoryAcquisitionUseDto compensatoryAcquisitionUseDto;

		/**
		 * Instantiates a new compensatory acquisition use get memento impl.
		 *
		 * @param compensatoryAcquisitionUseDto the compensatory acquisition use dto
		 */
		public CompensatoryAcquisitionUseGetMementoImpl(CompensatoryAcquisitionUseDto compensatoryAcquisitionUseDto) {
			this.compensatoryAcquisitionUseDto = compensatoryAcquisitionUseDto;
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave2.CompensatoryAcquisitionUseGetMemento#getExpirationTime()
		 */
		@Override
		public ExpirationTime getExpirationTime() {
			return ExpirationTime.valueOf(this.compensatoryAcquisitionUseDto.expirationTime);
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave2.CompensatoryAcquisitionUseGetMemento#getPreemptionPermit()
		 */
		@Override
		public ApplyPermission getPreemptionPermit() {
			return ApplyPermission.valueOf(this.compensatoryAcquisitionUseDto.preemptionPermit);
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryAcquisitionUseGetMemento#getDeadlCheckMonth()
		 */
		@Override
		public DeadlCheckMonth getDeadlCheckMonth() {
			return DeadlCheckMonth.valueOf(this.compensatoryAcquisitionUseDto.deadlCheckMonth);
		}
	}

}
