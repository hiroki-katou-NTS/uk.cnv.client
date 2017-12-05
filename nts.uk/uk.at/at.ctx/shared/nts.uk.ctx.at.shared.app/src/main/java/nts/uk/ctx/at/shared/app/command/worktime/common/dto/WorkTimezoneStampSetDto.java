/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.worktime.common.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import nts.uk.ctx.at.shared.dom.worktime.common.PrioritySetting;
import nts.uk.ctx.at.shared.dom.worktime.common.RoundingSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSetGetMemento;

/**
 * The Class WorkTimezoneStampSetDto.
 */
@Value
public class WorkTimezoneStampSetDto implements WorkTimezoneStampSetGetMemento {

	/** The rounding set. */
	private List<RoundingSetDto> roundingSet;

	/** The priority set. */
	private List<PrioritySettingDto> prioritySet;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSetGetMemento#
	 * getRoundingSet()
	 */
	@Override
	public List<RoundingSet> getRoundingSet() {
		return this.roundingSet.stream().map(item -> new RoundingSet(item)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSetGetMemento#
	 * getPrioritySet()
	 */
	@Override
	public List<PrioritySetting> getPrioritySet() {
		return this.prioritySet.stream().map(item -> new PrioritySetting(item)).collect(Collectors.toList());
	}

}
