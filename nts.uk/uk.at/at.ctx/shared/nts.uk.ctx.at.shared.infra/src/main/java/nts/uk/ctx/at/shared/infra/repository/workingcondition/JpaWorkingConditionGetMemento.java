/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.workingcondition;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionGetMemento;
import nts.uk.ctx.at.shared.infra.entity.workingcondition.KshmtWorkingCond;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * The Class JpaWorkingConditionGetMemento.
 */
public class JpaWorkingConditionGetMemento implements WorkingConditionGetMemento {

	/** The Constant FIRST_ITEM_INDEX. */
	private final static int FIRST_ITEM_INDEX = 0;

	/** The kshmt working cond. */
	@Setter
	private List<KshmtWorkingCond> kshmtWorkingConds;

	/**
	 * Instantiates a new jpa working condition get memento.
	 *
	 * @param kshmtWorkingCond
	 *            the kshmt working cond
	 */
	public JpaWorkingConditionGetMemento(List<KshmtWorkingCond> kshmtWorkingConds) {
		this.kshmtWorkingConds = kshmtWorkingConds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionGetMemento#
	 * getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return this.kshmtWorkingConds.get(FIRST_ITEM_INDEX).getCid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionGetMemento#
	 * getEmployeeId()
	 */
	@Override
	public String getEmployeeId() {
		return this.kshmtWorkingConds.get(FIRST_ITEM_INDEX).getKshmtWorkingCondPK().getSid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionGetMemento#
	 * getDateHistoryItem()
	 */
	@Override
	public List<DateHistoryItem> getDateHistoryItem() {
		return this.kshmtWorkingConds.stream()
				.map(item -> new DateHistoryItem(item.getKshmtWorkingCondPK().getHistoryId(),
						new DatePeriod(item.getStrD(), item.getEndD())))
				.collect(Collectors.toList());
	}

}
