/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.common;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.ScreenMode;

/**
 * The Class SubHolTransferSet.
 */
//代休振替設定
@Getter
public class SubHolTransferSet extends DomainObject{
	
	/** The certain time. */
	//一定時間
	private OneDayTime certainTime;
	
	/** The use division. */
	//使用区分
	private boolean useDivision;
	
	/** The designated time. */
	//指定時間
	private DesignatedTime designatedTime;
	
	/** The Sub hol transfer set atr. */
	//振替区分
	private SubHolTransferSetAtr subHolTransferSetAtr;

	/**
	 * Instantiates a new sub hol transfer set.
	 *
	 * @param memento the memento
	 */
	public SubHolTransferSet(SubHolTransferSetGetMemento memento) {
		this.certainTime = memento.getCertainTime();
		this.useDivision = memento.getUseDivision();
		this.designatedTime = memento.getDesignatedTime();
		this.subHolTransferSetAtr = memento.getSubHolTransferSetAtr();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(SubHolTransferSetSetMemento memento) {
		memento.setCertainTime(this.certainTime);
		memento.setUseDivision(this.useDivision);
		memento.setDesignatedTime(this.designatedTime);
		memento.setSubHolTransferSetAtr(this.subHolTransferSetAtr);
	}
	
	/**
	 * Restore data.
	 *
	 * @param other the other
	 */
	public void restoreData(ScreenMode screenMode, SubHolTransferSet oldDomain) {
		// Simple mode
		if (screenMode == ScreenMode.SIMPLE) {
			// Only designatedTime not get restore
			this.certainTime = oldDomain.getCertainTime();
			this.useDivision = oldDomain.isUseDivision();
			this.subHolTransferSetAtr = oldDomain.getSubHolTransferSetAtr();
			return;
		}
		
		// Detail mode				
		// Setting not use - restore old data
		if (!this.useDivision) {
			this.certainTime = oldDomain.getCertainTime();
			this.subHolTransferSetAtr = oldDomain.getSubHolTransferSetAtr();			
			this.designatedTime.restoreData(oldDomain.getDesignatedTime());
			return;
		} 
		
		// Setting being used
		switch (this.subHolTransferSetAtr) {		
			case SPECIFIED_TIME_SUB_HOL:
				this.certainTime = oldDomain.getCertainTime();
				break;
	
			case CERTAIN_TIME_EXC_SUB_HOL:
				this.designatedTime.restoreData(oldDomain.getDesignatedTime());
				break;
	
			default:
				throw new RuntimeException("SubHolTransferType not found.");
		}
	}
	
	@Override
	public void validate() {
		// check use 
		if (this.useDivision) {
			
			// one day >= half day
			if (this.getDesignatedTime().getOneDayTime().lessThan(this.getDesignatedTime().getHalfDayTime())) {
				throw new BusinessException("Msg_782");
			}
		}

	}
}
