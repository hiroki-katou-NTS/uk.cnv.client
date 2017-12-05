/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.command.worktime.common.dto;

import java.math.BigDecimal;

import nts.uk.ctx.at.shared.dom.worktime.common.EmTimezoneNo;
import nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento;
import nts.uk.ctx.at.shared.dom.worktime.common.SettlementOrder;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZoneRounding;

/**
 * The Class OverTimeOfTimeZoneSetDto.
 */
public class OverTimeOfTimeZoneSetDto implements OverTimeOfTimeZoneSetGetMemento {

	/** The work timezone no. */
	private Integer workTimezoneNo;

	/** The restraint time use. */
	private boolean restraintTimeUse;

	/** The early OT use. */
	private boolean earlyOTUse;

	/** The timezone. */
	private TimeZoneRoundingDto timezone;

	/** The OT frame no. */
	private BigDecimal OTFrameNo;

	/** The legal O tframe no. */
	private BigDecimal legalOTframeNo;

	/** The settlement order. */
	private Integer settlementOrder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getWorkTimezoneNo()
	 */
	@Override
	public EmTimezoneNo getWorkTimezoneNo() {
		return new EmTimezoneNo(this.workTimezoneNo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getRestraintTimeUse()
	 */
	@Override
	public boolean getRestraintTimeUse() {
		return this.restraintTimeUse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getEarlyOTUse()
	 */
	@Override
	public boolean getEarlyOTUse() {
		return this.earlyOTUse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getTimezone()
	 */
	@Override
	public TimeZoneRounding getTimezone() {
		return new TimeZoneRounding(this.timezone);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getOTFrameNo()
	 */
	@Override
	public nts.uk.ctx.at.shared.dom.worktime.common.OTFrameNo getOTFrameNo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getLegalOTframeNo()
	 */
	@Override
	public nts.uk.ctx.at.shared.dom.worktime.common.OTFrameNo getLegalOTframeNo() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetGetMemento#
	 * getSettlementOrder()
	 */
	@Override
	public SettlementOrder getSettlementOrder() {
		return new SettlementOrder(this.settlementOrder);
	}
}
