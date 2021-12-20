/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.repository.optitem;

import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import nts.uk.ctx.at.record.infra.entity.optitem.KrcmtAnyfResultRange;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyAmountMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimeMonth;
import nts.uk.ctx.at.shared.dom.common.anyitem.AnyTimesMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemAmount;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.optionalitemvalue.AnyItemTimes;
import nts.uk.ctx.at.shared.dom.scherec.optitem.AmountRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcRangeCheck;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRangeSetMemento;
import nts.uk.ctx.at.shared.dom.scherec.optitem.NumberRange;
import nts.uk.ctx.at.shared.dom.scherec.optitem.TimeRange;

/**
 * The Class JpaCalcResultRangeSetMemento.
 */
public class JpaCalcResultRangeSetMemento implements CalcResultRangeSetMemento {

	/** The type value. */
	private KrcmtAnyfResultRange typeValue;

	/**
	 * Instantiates a new jpa calc result range set memento.
	 *
	 * @param typeValue the type value
	 */
	public JpaCalcResultRangeSetMemento(KrcmtAnyfResultRange typeValue) {
		this.typeValue = typeValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeSetMemento#setUpperLimit(
	 * nts.uk.ctx.at.record.dom.optitem.CalcRangeCheck)
	 */
	@Override
	public void setUpperLimit(CalcRangeCheck upper) {
		this.typeValue.setUpperLimitAtr(BooleanUtils.toBoolean(upper.value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeSetMemento#setLowerLimit(
	 * nts.uk.ctx.at.record.dom.optitem.CalcRangeCheck)
	 */
	@Override
	public void setLowerLimit(CalcRangeCheck lower) {
		this.typeValue.setLowerLimitAtr(BooleanUtils.toBoolean(lower.value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeSetMemento#setNumberRange
	 * (nts.uk.ctx.at.record.dom.optitem.NumberRange)
	 */
	@Override
	public void setNumberRange(Optional<NumberRange> range) {
//		this.typeValue.setLowerNumberRange(range.isPresent() && range.get().getLowerLimit().isPresent()
//				? range.get().getLowerLimit().get().v() : null);
//		this.typeValue.setUpperNumberRange(range.isPresent() && range.get().getUpperLimit().isPresent()
//				? range.get().getUpperLimit().get().v() : null);
	    if (range.isPresent()) {
	        Optional<AnyItemTimes> lowerDailyNumberOpt = range.get().getDailyTimesRange().get().getLowerLimit();
	        Optional<AnyItemTimes> upperDailyNumberOpt = range.get().getDailyTimesRange().get().getUpperLimit();
	        Optional<AnyTimesMonth> lowerMonNumberOpt = range.get().getMonthlyTimesRange().get().getLowerLimit();
	        Optional<AnyTimesMonth> upperMonNumberOpt = range.get().getMonthlyTimesRange().get().getUpperLimit();
	        
	        typeValue.setLowerDayNumberRange(lowerDailyNumberOpt.isPresent() ? lowerDailyNumberOpt.get().v() : null);
	        typeValue.setUpperDayNumberRange(upperDailyNumberOpt.isPresent() ? upperDailyNumberOpt.get().v() : null);
	        typeValue.setLowerMonNumberRange(lowerMonNumberOpt.isPresent() ? lowerMonNumberOpt.get().v() : null);
	        typeValue.setUpperMonNumberRange(upperMonNumberOpt.isPresent() ? upperMonNumberOpt.get().v() : null);
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeSetMemento#setTimeRange(
	 * nts.uk.ctx.at.record.dom.optitem.TimeRange)
	 */
	@Override
	public void setTimeRange(Optional<TimeRange> range) {
//		this.typeValue.setLowerTimeRange(range.isPresent() && range.get().getLowerLimit().isPresent()
//				? range.get().getLowerLimit().get().v() : null);
//		this.typeValue.setUpperTimeRange(range.isPresent() && range.get().getUpperLimit().isPresent()
//				? range.get().getUpperLimit().get().v() : null);
	    if (range.isPresent()) {
	        Optional<AnyItemTime> lowerDailyTimeOpt = range.get().getDailyTimeRange().get().getLowerLimit();
	        Optional<AnyItemTime> upperDailyTimeOpt = range.get().getDailyTimeRange().get().getUpperLimit();
	        Optional<AnyTimeMonth> lowerMonTimeOpt = range.get().getMonthlyTimeRange().get().getLowerLimit();
	        Optional<AnyTimeMonth> upperMonTimeOpt = range.get().getMonthlyTimeRange().get().getUpperLimit();
	        
	        typeValue.setLowerDayTimeRange(lowerDailyTimeOpt.isPresent() ? lowerDailyTimeOpt.get().v() : null);
	        typeValue.setUpperDayTimeRange(upperDailyTimeOpt.isPresent() ? upperDailyTimeOpt.get().v() : null);
	        typeValue.setLowerMonTimeRange(lowerMonTimeOpt.isPresent() ? lowerMonTimeOpt.get().v() : null);
	        typeValue.setUpperMonTimeRange(upperMonTimeOpt.isPresent() ? upperMonTimeOpt.get().v() : null);
	    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.optitem.CalcResultRangeSetMemento#setAmountRange
	 * (nts.uk.ctx.at.record.dom.optitem.AmountRange)
	 */
	@Override
	public void setAmountRange(Optional<AmountRange> range) {
//		this.typeValue.setLowerAmountRange(range.isPresent() && range.get().getLowerLimit().isPresent()
//				? range.get().getLowerLimit().get().v() : null);
//		this.typeValue.setUpperAmountRange(range.isPresent() && range.get().getUpperLimit().isPresent()
//				? range.get().getUpperLimit().get().v() : null);
	    if (range.isPresent()) {
            Optional<AnyItemAmount> lowerDailyAmountOpt = range.get().getDailyAmountRange().get().getLowerLimit();
            Optional<AnyItemAmount> upperDailyAmountOpt = range.get().getDailyAmountRange().get().getUpperLimit();
            Optional<AnyAmountMonth> lowerMonAmountOpt = range.get().getMonthlyAmountRange().get().getLowerLimit();
            Optional<AnyAmountMonth> upperMonAmountOpt = range.get().getMonthlyAmountRange().get().getUpperLimit();
            
            typeValue.setLowerDayTimeRange(lowerDailyAmountOpt.isPresent() ? lowerDailyAmountOpt.get().v() : null);
            typeValue.setUpperDayTimeRange(upperDailyAmountOpt.isPresent() ? upperDailyAmountOpt.get().v() : null);
            typeValue.setLowerMonTimeRange(lowerMonAmountOpt.isPresent() ? lowerMonAmountOpt.get().v() : null);
            typeValue.setUpperMonTimeRange(upperMonAmountOpt.isPresent() ? upperMonAmountOpt.get().v() : null);
        }
	}

}
