/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.common.internal;

import javax.ejb.Stateless;

import nts.arc.error.BundledBusinessException;
import nts.uk.ctx.at.shared.dom.worktime.common.StampReflectTimezone;
import nts.uk.ctx.at.shared.dom.worktime.common.StampReflectTimezonePolicy;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;

/**
 * The Class StampReflectTimezonePolicyImpl.
 */
@Stateless
public class StampReflectTimezonePolicyImpl implements StampReflectTimezonePolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.common.StampReflectTimezonePolicy#
	 * validate(nts.arc.error.BundledBusinessException, boolean,
	 * nts.uk.ctx.at.shared.dom.worktime.common.StampReflectTimezone)
	 */
	@Override
	public void validate(BundledBusinessException bundledBusinessExceptions, boolean isFlow,
			StampReflectTimezone timezone,PredetemineTimeSetting predetemineTimeSetting) {
		if (timezone.isEmpty()) {
			return;
		}

		// Validate Msg_770
		if (timezone.getStartTime().greaterThan(timezone.getEndTime())) {
			if (isFlow) {
				// J2_2, J2_4
				if (timezone.isGoWork1()) {
					bundledBusinessExceptions.addMessage("Msg_770", "KMK003_270");
				}
				// J2_9, J2_11
				if (timezone.isLeaveWork1()) {
					bundledBusinessExceptions.addMessage("Msg_770", "KMK003_273");
				}
			} else {
				// J1_3, J1_5
				if (timezone.isGoWork1()) {
					bundledBusinessExceptions.addMessage("Msg_770", "KMK003_271");
				}
				// J1_7, J1_9
				if (timezone.isGoWork2()) {
					if (predetemineTimeSetting.getPrescribedTimezoneSetting().isUseShiftTwo()) {
						bundledBusinessExceptions.addMessage("Msg_770", "KMK003_272");
					}
				}
				// J1_12, J1_14
				if (timezone.isLeaveWork1()) {
					bundledBusinessExceptions.addMessage("Msg_770", "KMK003_274");
				}
				// J1_16, J1_18
				if (timezone.isLeaveWork2()) {
					if (predetemineTimeSetting.getPrescribedTimezoneSetting().isUseShiftTwo()) {
						bundledBusinessExceptions.addMessage("Msg_770", "KMK003_275");
					}
				}
			}
		}
	}

}
