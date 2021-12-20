/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.repository.optitem;

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyfResultRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.AmountRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcRangeCheck;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRangeGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.optitem.DailyAmountRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.DailyTimeRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.DailyTimesRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.MonthlyAmountRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.MonthlyTimeRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.MonthlyTimesRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.NumberRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.TimeRange;

/**
 * The Class JpaCalcResultRangeGetMemento.
 */
public class JpaCalcResultRangeGetMemento implements CalcResultRangeGetMemento {

	/** The type value. */
	private KrcmtAnyfResultRange typeValue;

	/**
	 * Instantiates a new jpa calc result range get memento.
	 *
	 * @param typeValue
	 *            the type value
	 */
	public JpaCalcResultRangeGetMemento(KrcmtAnyfResultRange typeValue) {
		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeGetMemento#getUpperLimit(
	 * )
	 */
	@Override
	public CalcRangeCheck getUpperLimit() {
		return CalcRangeCheck.valueOf(BooleanUtils.toInteger(this.typeValue.isUpperLimitAtr()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeGetMemento#getLowerLimit(
	 * )
	 */
	@Override
	public CalcRangeCheck getLowerLimit() {
		return CalcRangeCheck.valueOf(BooleanUtils.toInteger(this.typeValue.isLowerLimitAtr()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeGetMemento#getNumberRange
	 * ()
	 */
	@Override
	public Optional<NumberRange> getNumberRange() {
//		return Optional.of(new NumberRange(this.typeValue.getUpperNumberRange(),
//				this.typeValue.getLowerNumberRange()));
	    return Optional.of(new NumberRange(
	            Optional.of(new DailyTimesRange(this.typeValue.getUpperDayNumberRange(), this.typeValue.getLowerDayNumberRange())), 
	            Optional.of(new MonthlyTimesRange(
	                    this.typeValue.getUpperMonNumberRange() == null ? null : this.typeValue.getUpperMonNumberRange().doubleValue(), 
	                    this.typeValue.getLowerMonNumberRange() == null ? null : this.typeValue.getLowerMonNumberRange().doubleValue()))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeGetMemento#getTimeRange()
	 */
	@Override
	public Optional<TimeRange> getTimeRange() {
//		return Optional.of(new TimeRange(this.typeValue.getUpperTimeRange(),
//				this.typeValue.getLowerTimeRange()));
	    return Optional.of(new TimeRange(
	            Optional.of(new DailyTimeRange(this.typeValue.getUpperDayTimeRange(), this.typeValue.getLowerDayTimeRange())),
	            Optional.of(new MonthlyTimeRange(this.typeValue.getUpperMonTimeRange(), this.typeValue.getLowerMonTimeRange()))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeGetMemento#getAmountRange
	 * ()
	 */
	@Override
	public Optional<AmountRange> getAmountRange() {
//		return Optional.of(new AmountRange(this.typeValue.getUpperAmountRange(),
//				this.typeValue.getLowerAmountRange()));
	    return Optional.of(new AmountRange(
	            Optional.of(new DailyAmountRange(this.typeValue.getUpperdayAmountRange(), this.typeValue.getLowerDayAmountRange())),
	            Optional.of(new MonthlyAmountRange(this.typeValue.getUpperMonAmountRange(), this.typeValue.getLowerMonAmountRange()))));
	}

}
