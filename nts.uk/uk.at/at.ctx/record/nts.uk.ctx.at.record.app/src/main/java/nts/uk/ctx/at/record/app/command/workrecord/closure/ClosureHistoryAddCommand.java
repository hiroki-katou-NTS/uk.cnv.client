/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.command.workrecord.closure;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.YearMonth;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.app.find.workrecord.closure.dto.ClosureHistoryAddDto;
import nts.uk.ctx.at.record.dom.workrecord.closure.CloseName;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureDate;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistory;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryId;
import nts.uk.ctx.at.record.dom.workrecord.closure.ClosureId;
import nts.uk.ctx.at.record.dom.workrecord.closure.CompanyId;

/**
 * The Class ClosureHistorySaveCommand.
 */

@Getter
@Setter
public class ClosureHistoryAddCommand {
	
	/** The closure history add. */
	private ClosureHistoryAddDto closureHistoryAdd;

	
	/**
	 * To domain.
	 *
	 * @param companyId the company id
	 * @return the closure history
	 */
	public ClosureHistory toDomain(String companyId){
		return new ClosureHistory(new ClosureHistoryGetMementoImpl(this, companyId));
	}
	
	/**
	 * The Class ClosureGetMementoImpl.
	 */
	class ClosureHistoryGetMementoImpl implements ClosureHistoryGetMemento{
		
		/** The command. */
		private ClosureHistoryAddCommand command;
		
		/** The company id. */
		private String companyId;
		
		/**
		 * Instantiates a new closure get memento impl.
		 *
		 * @param command the command
		 * @param companyId the company id
		 */
		public ClosureHistoryGetMementoImpl(ClosureHistoryAddCommand command,String companyId){
			this.command = command;
			this.companyId = companyId;
		}
		
		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#getCompanyId()
		 */
		@Override
		public CompanyId getCompanyId() {
			return new CompanyId(companyId);
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#getCloseName()
		 */
		@Override
		public CloseName getCloseName() {
			return new CloseName(null);
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#getClosureId()
		 */
		@Override
		public ClosureId getClosureId() {
			return ClosureId.valueOf(command.getClosureHistoryAdd().getClosureId());
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#getClosureHistoryId()
		 */
		@Override
		public ClosureHistoryId getClosureHistoryId() {
			return new ClosureHistoryId(IdentifierUtil.randomUniqueId());
		}

		/* (non-Javadoc)
		 * @see nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#getEndDate()
		 */
		@Override
		public YearMonth getEndDate() {
			return YearMonth.of(command.getClosureHistoryAdd().getEndDate());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#
		 * getClosureDate()
		 */
		@Override
		public ClosureDate getClosureDate() {
			if(command.getClosureHistoryAdd().getClosureDate() == 0){
				return new ClosureDate(0, true);
			}
			return new ClosureDate(command.getClosureHistoryAdd().getClosureDate(), false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * nts.uk.ctx.at.record.dom.workrecord.closure.ClosureHistoryGetMemento#
		 * getStartDate()
		 */
		@Override
		public YearMonth getStartDate() {
			return YearMonth.of(command.getClosureHistoryAdd().getStartDate());
		}

		

	}
}
