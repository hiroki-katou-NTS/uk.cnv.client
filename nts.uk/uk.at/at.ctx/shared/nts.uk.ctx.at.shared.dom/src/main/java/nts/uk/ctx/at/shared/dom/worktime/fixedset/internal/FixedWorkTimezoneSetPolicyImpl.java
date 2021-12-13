/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.fixedset.internal;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BundledBusinessException;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeZoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.EmTimeZoneSetPolicy;
import nts.uk.ctx.at.shared.dom.worktime.common.FixedWorkTimezoneSet;
import nts.uk.ctx.at.shared.dom.worktime.common.OverTimeOfTimeZoneSetPolicy;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.policy.FixedWorkTimezoneSetPolicy;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimedisplay.DisplayMode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.HalfDayWorkSet;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * The Class FixedWorkTimezoneSetPolicyImpl.
 */
@Stateless
public class FixedWorkTimezoneSetPolicyImpl implements FixedWorkTimezoneSetPolicy {

	/** The em tz policy. */
	@Inject
	private EmTimeZoneSetPolicy emTzPolicy;

	/** The ot policy. */
	@Inject
	private OverTimeOfTimeZoneSetPolicy otPolicy;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.common.FixedWorkTimezoneSetPolicy#
	 * validate(nts.arc.error.BundledBusinessException,
	 * nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting,
	 * nts.uk.ctx.at.shared.dom.worktime.common.FixedWorkTimezoneSet,
	 * nts.uk.ctx.at.shared.dom.worktime.worktimedisplay.DisplayMode,
	 * nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr, boolean)
	 */
	@Override
	public void validateFixedAndDiff(BundledBusinessException be, PredetemineTimeSetting predTime,
			FixedWorkTimezoneSet worktimeSet, DisplayMode displayMode, AmPmAtr dayAtr) {
		// Validate list working timezone
		worktimeSet.getLstWorkingTimezone().forEach(workingTimezone -> {
			this.emTzPolicy.validateFixedAndDiff(be, predTime, workingTimezone, displayMode, dayAtr);
		});

		// Validate list OT timezone
		worktimeSet.getLstOTTimezone().forEach(workingTimezone -> {
			this.otPolicy.validate(be, predTime, workingTimezone, displayMode, dayAtr);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.fixedset.policy.
	 * FixedWorkTimezoneSetPolicy#validateFlex(nts.arc.error.
	 * BundledBusinessException,
	 * nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting,
	 * nts.uk.ctx.at.shared.dom.worktime.common.FixedWorkTimezoneSet,
	 * nts.uk.ctx.at.shared.dom.worktime.worktimedisplay.DisplayMode,
	 * nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr, boolean)
	 */
	@Override
	public void validateFlex(BundledBusinessException be, PredetemineTimeSetting predTime,
			FixedWorkTimezoneSet worktimeSet, DisplayMode displayMode, AmPmAtr dayAtr) {
		// Validate list working timezone
		worktimeSet.getLstWorkingTimezone().forEach(workingTimezone -> {
			this.emTzPolicy.validateFlex(be, predTime, workingTimezone, displayMode, dayAtr);
		});

		// Validate list OT timezone
		worktimeSet.getLstOTTimezone().forEach(workingTimezone -> {
			this.otPolicy.validate(be, predTime, workingTimezone, displayMode, dayAtr);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.fixedset.policy.
	 * FixedWorkTimezoneSetPolicy#filterTimezone(nts.uk.ctx.at.shared.dom.
	 * worktime.predset.PredetemineTimeSetting,
	 * nts.uk.ctx.at.shared.dom.worktime.common.FixedWorkTimezoneSet,
	 * nts.uk.ctx.at.shared.dom.worktime.worktimedisplay.DisplayMode,
	 * nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr, boolean)
	 */
	@Override
	public void filterTimezone(PredetemineTimeSetting predTime, FixedWorkTimezoneSet origin, DisplayMode displayMode,
			AmPmAtr dayAtr) {
		TimeWithDayAttr morningEndTime = predTime.getPrescribedTimezoneSetting().getMorningEndTime();
		TimeWithDayAttr afternoonStartTime = predTime.getPrescribedTimezoneSetting().getAfternoonStartTime();

		// Filter AM timezone
		if ((AmPmAtr.AM.equals(dayAtr) && DisplayMode.DETAIL.equals(displayMode))
				|| (AmPmAtr.AM.equals(dayAtr) && DisplayMode.SIMPLE.equals(displayMode))) {

			// Filter work timezone
			List<EmTimeZoneSet> employmentTimezones = origin.getLstWorkingTimezone().stream()
					.filter(timezone -> timezone.getTimezone().getStart().lessThanOrEqualTo(morningEndTime))
					.map(item -> {
//						if (item.getTimezone().getStart().equals(morningEndTime)) {
//							return null;
//						}
						if (item.getTimezone().getStart().lessThan(morningEndTime)
								&& item.getTimezone().getEnd().greaterThan(morningEndTime)) {
							item.getTimezone().setEnd(morningEndTime);
							return item;
						}
						return item;
					}).filter(Objects::nonNull).collect(Collectors.toList());
			origin.setLstWorkingTimezone(this.updateTimeFrameNo(employmentTimezones));

			// Filter OT timezone
//			List<OverTimeOfTimeZoneSet> oTTimezones = origin.getLstOTTimezone().stream()
//					.filter(timezone -> timezone.getTimezone().getStart().lessThanOrEqualTo(morningEndTime))
//					.map(item -> {
//						if (item.getTimezone().getStart().equals(morningEndTime)) {
//							return null;
//						}
//						if (item.getTimezone().getStart().lessThan(morningEndTime)
//								&& item.getTimezone().getEnd().greaterThan(morningEndTime)) {
//							item.getTimezone().setEnd(morningEndTime);
//							return item;
//						}
//						return item;
//					}).filter(Objects::nonNull).collect(Collectors.toList());
//			origin.setLstOTTimezone(this.updateTimeZoneNo(oTTimezones));
			return;
		}

		// Filter PM timezone
		if ((AmPmAtr.PM.equals(dayAtr) && DisplayMode.DETAIL.equals(displayMode))
				|| (AmPmAtr.PM.equals(dayAtr) && DisplayMode.SIMPLE.equals(displayMode))) {

			// Filter work timezone
			List<EmTimeZoneSet> employmentTimezones = origin.getLstWorkingTimezone().stream()
					.filter(timezone -> timezone.getTimezone().getEnd().greaterThanOrEqualTo(afternoonStartTime))
					.map(item -> {
//						if (item.getTimezone().getEnd().equals(afternoonStartTime)) {
//							return null;
//						}
						if (item.getTimezone().getStart().lessThan(afternoonStartTime)
								&& item.getTimezone().getEnd().greaterThan(afternoonStartTime)) {
							item.getTimezone().setStart(afternoonStartTime);
							return item;
						}
						return item;
					}).filter(Objects::nonNull).collect(Collectors.toList());
			origin.setLstWorkingTimezone(this.updateTimeFrameNo(employmentTimezones));

			// Filter OT timezone
//			List<OverTimeOfTimeZoneSet> oTTimezones = origin.getLstOTTimezone().stream()
//					.filter(timezone -> timezone.getTimezone().getEnd().greaterThanOrEqualTo(afternoonStartTime))
//					.map(item -> {
//						if (item.getTimezone().getEnd().equals(afternoonStartTime)) {
//							return null;
//						}
//						if (item.getTimezone().getStart().lessThan(afternoonStartTime)
//								&& item.getTimezone().getEnd().greaterThan(afternoonStartTime)) {
//							item.getTimezone().setStart(afternoonStartTime);
//							return item;
//						}
//						return item;
//					}).filter(Objects::nonNull).collect(Collectors.toList());
//			origin.setLstOTTimezone(this.updateTimeZoneNo(oTTimezones));
			return;
		}
	}

	/**
	 * Update time frame no.
	 *
	 * @param listItem
	 *            the list item
	 * @return the list
	 */
	private List<EmTimeZoneSet> updateTimeFrameNo(List<EmTimeZoneSet> listItem) {
		int timeFrameNo = 0;
		for (EmTimeZoneSet item : listItem) {
			timeFrameNo++;
			item.setEmploymentTimeFrameNo(new EmTimeFrameNo(timeFrameNo));
		}
		return listItem;
	}

	/**
	 * Update time zone no.
	 *
	 * @param listItem
	 *            the list item
	 * @return the list
	 */
//	private List<OverTimeOfTimeZoneSet> updateTimeZoneNo(List<OverTimeOfTimeZoneSet> listItem) {
//		int timeZoneNo = 0;
//		for (OverTimeOfTimeZoneSet item : listItem) {
//			timeZoneNo++;
//			item.setWorkTimezoneNo(new EmTimezoneNo(timeZoneNo));
//		}
//		return listItem;
//	}
}
