/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.dailypattern;

import java.util.List;
import java.util.stream.Collectors;

import nts.uk.ctx.at.shared.dom.dailypattern.DailyPatternGetMemento;
import nts.uk.ctx.at.shared.dom.dailypattern.DailyPatternVal;
import nts.uk.ctx.at.shared.infra.entity.dailypattern.KdpstDailyPatternSet;

// TODO: Auto-generated Javadoc
/**
 * The Class JpaDailyPatternGetMemento.
 */
public class JpaDailyPatternGetMemento implements DailyPatternGetMemento {

	/** The pattern calendar. */
	private KdpstDailyPatternSet patternCalendar;

	/**
	 * @param patternCalendar
	 */
	public JpaDailyPatternGetMemento(KdpstDailyPatternSet patternCalendar) {
		this.patternCalendar = patternCalendar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.patterncalendar.PatternCalendarGetMemento#
	 * getCompanyId()
	 */
	@Override
	public String getCompanyId() {
		return patternCalendar.getKdpstDailyPatternSetPK().getCid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.patterncalendar.PatternCalendarGetMemento#
	 * getPatternCode()
	 */
	@Override
	public String getPatternCode() {
		return patternCalendar.getKdpstDailyPatternSetPK().getPatternCd();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.patterncalendar.PatternCalendarGetMemento#
	 * getPatternName()
	 */
	@Override
	public String getPatternName() {
		return patternCalendar.getPatternName();
	}

	@Override
	public List<DailyPatternVal> getListDailyPatternVal() {
		return this.patternCalendar.getListKdpstDailyPatternVal().stream()
				.map(entity -> new DailyPatternVal(new JpaDailyPatternValGetMemento(entity))).collect(Collectors.toList());
	}

}
