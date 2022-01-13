/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.command.workrecord.workfixed;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class WorkFixedSaveCommand.
 */
@Getter
@Setter
public class SaveWorkFixedCommand {
	
	/** The closure id. */
	private Integer closureId;
	
	/** The wkp id. */
	private String wkpId;
	
	/** The confirm cls status. */
	private Integer confirmClsStatus;
	
	/** The process date. */
	private Integer processDate;
	
	/** The is edited. */
	private Boolean isEdited;
    
//	/**
//	 * To domain.
//	 *
//	 * @param personId the person id
//	 * @param fixedDate the fixed date
//	 * @return the work fixed
//	 */
//	public WorkFixed toDomain(String companyId, String personId, GeneralDate fixedDate) {
//        return new WorkFixed(new WorkFixedGetMementoImpl(companyId, personId, fixedDate, this));
//    }
//	
//	/**
//	 * The Class WorkFixedGetMementoImpl.
//	 */
//	public class WorkFixedGetMementoImpl implements WorkFixedGetMemento {
//        
//		/** The person id. */
//        private String personId;
//        
//        /** The fixed date. */
//        private GeneralDate fixedDate;
//
//        /** The closure id. */
//    	private Integer closureId;
//    	
//    	/** The wkp id. */
//    	private String wkpId;
//    	
//    	/** The confirm cls status. */
//    	private Integer confirmClsStatus;
//    	
//    	/** The process date. */
//    	private Integer processDate;
//    	
//    	/** The cid. */
//        private String cid;
//		
//    	/**
//	     * Instantiates a new work fixed get memento impl.
//	     *
//	     * @param personId the person id
//	     * @param fixedDate the fixed date
//	     * @param saveCommand the save command
//	     */
//	    public WorkFixedGetMementoImpl(String companyId, String personId, GeneralDate fixedDate, SaveWorkFixedCommand saveCommand) {
//	    	this.cid = companyId;
//            this.personId = personId;
//            this.fixedDate = fixedDate;
//            this.closureId = saveCommand.closureId;
//            this.wkpId = saveCommand.wkpId;
//            this.confirmClsStatus = saveCommand.confirmClsStatus;
//            this.processDate = saveCommand.processDate;
//        }
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getClosureId()
//		 */
//		@Override
//		public Integer getClosureId() {
//			return this.closureId;
//		}
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getConfirmPId()
//		 */
//		@Override
//		public String getConfirmPId() {
//			return this.personId;
//		}
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getWorkPlaceId()
//		 */
//		@Override
//		public String getWorkPlaceId() {
//			return this.wkpId;
//		}
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getConfirmClsStatus()
//		 */
//		@Override
//		public ConfirmClsStatus getConfirmClsStatus() {
//			return ConfirmClsStatus.valueOf(confirmClsStatus);
//		}
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getFixedDate()
//		 */
//		@Override
//		public GeneralDate getFixedDate() {
//			return this.fixedDate;
//		}
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getProcessDate()
//		 */
//		@Override
//		public YearMonth getProcessYm() {
//			return new YearMonth(this.processDate);
//		}
//
//		/* (non-Javadoc)
//		 * @see nts.uk.ctx.at.record.dom.workrecord.workfixed.WorkFixedGetMemento#getCid()
//		 */
//		@Override
//		public String getCid() {
//			return this.cid;
//		}
//	}
}
