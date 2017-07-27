/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.dailypattern;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.common.CompanyId;

/**
 * The Class DailyPatternVal.
 */

/**
 * Gets the days.
 *
 * @return the days
 */
@Getter
public class DailyPatternVal extends DomainObject{
	
	/** The cid. */
	private CompanyId cid;
	
    /** The pattern cd. */
    private PatternCode patternCd;
    
    /** The disp order. */
    private DispOrder dispOrder;
    
    /** The work type set cd. */
    private WorkTypeCode workTypeSetCd;
    
    /** The working hours cd. */
    private WorkingCode workingHoursCd;
    
    /** The days. */
    private Days days;

	/**
	 * Instantiates a new daily pattern val.
	 */
	public DailyPatternVal() {
	}

	/**
	 * Instantiates a new daily pattern val.
	 *
	 * @param memento the memento
	 */
	public DailyPatternVal(DailyPatternValGetMemento memento) {
		this.cid = memento.getCompanyId();
		this.patternCd = memento.getPatternCode();
		this.dispOrder = memento.getDispOrder();
		this.workTypeSetCd = memento.getWorkTypeSetCd();
		this.workingHoursCd = memento.getWorkingHoursCd();
		this.days = memento.getDays();
	}
	
	/**
	 * Save to memento.
	 *
	 * @param memento the memento
	 */
	public void saveToMemento(DailyPatternValSetMemento memento) {
		memento.setCompanyId(this.cid);
		memento.setPatternCode(this.patternCd);
		memento.setDispOrder(this.dispOrder);
		memento.setWorkTypeCodes(this.workTypeSetCd);
		memento.setWorkHouseCodes(this.workingHoursCd);
		memento.setDays(this.days);
	}
    
}
