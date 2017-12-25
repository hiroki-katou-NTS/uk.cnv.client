/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.common;

import lombok.Getter;
import nts.arc.error.BusinessException;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class DesignatedTime.
 */
// 指定時間
@Getter
public class DesignatedTime extends DomainObject {

	/** The one day time. */
	// 1日の時間
	private OneDayTime oneDayTime;

	/** The half day time. */
	// 半日の時間
	private OneDayTime halfDayTime;

	/**
	 * Instantiates a new designated time.
	 *
	 * @param oneDayTime the one day time
	 * @param halfDayTime the half day time
	 */
	public DesignatedTime(OneDayTime oneDayTime, OneDayTime halfDayTime) {
		super();
		this.oneDayTime = oneDayTime;
		this.halfDayTime = halfDayTime;
	}
	/**
	 * Instantiates a new designated time.
	 *
	 * @param oneDayTime the one day time
	 * @param halfDayTime the half day time
	 */
	public void saveToMemento(DesignatedTimeSetMemento memento) {
		memento.setOneDayTime(this.oneDayTime);
		memento.setHalfDayTime(this.halfDayTime);
	}
	
	/**
	 * Instantiates a new designated time.
	 *
	 * @param memento the memento
	 */
	public DesignatedTime(DesignatedTimeGetMemento memento){
		this.oneDayTime = memento.getOneDayTime();
		this.halfDayTime = memento.getHalfDayTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.dom.DomainObject#validate()
	 */
	@Override
	public void validate() {
		super.validate();
		if (this.oneDayTime.lessThanOrEqualTo(this.halfDayTime)) {
			throw new BusinessException("Msg_782");
		}
	}

	/**
	 * Restore data.
	 *
	 * @param otherDomain
	 *            the other domain
	 */
	public void restoreData(DesignatedTime oldDomain) {
		this.oneDayTime = oldDomain.getOneDayTime();
		this.halfDayTime = oldDomain.getHalfDayTime();
	}

}
