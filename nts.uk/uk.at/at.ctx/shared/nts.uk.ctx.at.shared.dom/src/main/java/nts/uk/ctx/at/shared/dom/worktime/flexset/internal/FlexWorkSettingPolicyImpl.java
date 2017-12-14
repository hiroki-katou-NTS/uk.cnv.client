/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.flexset.internal;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.at.shared.dom.common.usecls.ApplyAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetPolicy;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexHalfDayWorkTimePolicy;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexOffdayWorkTimePolicy;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.service.PredeteminePolicyService;

/**
 * The Class FlexWorkSettingPolicyImpl.
 */
@Stateless
public class FlexWorkSettingPolicyImpl {
	
	/** The service. */
//	@Inject
//	private PredeteminePolicyService service;

	/** The flex half day policy. */
	@Inject
	private FlexHalfDayWorkTimePolicy flexHalfDayPolicy;

	/** The flex offday policy. */
	@Inject
	private FlexOffdayWorkTimePolicy flexOffdayPolicy;

	/** The wtz common set policy. */
	@Inject
	private WorkTimezoneCommonSetPolicy wtzCommonSetPolicy;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingPolicy#
	 * canRegisterFlexWorkSetting(nts.uk.ctx.at.shared.dom.worktime.flexset.
	 * FlexWorkSetting,
	 * nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSet)
	 */
	public void canRegisterFlexWorkSetting(FlexWorkSetting flexWorkSetting, PredetemineTimeSetting predetemineTimeSet) {

		// 使用区分 = 使用しない AND 最低勤務時間 > 所定時間.1日 => Msg_775
		if (flexWorkSetting.getCoreTimeSetting().getTimesheet().equals(ApplyAtr.NOT_USE)
				&& flexWorkSetting.getCoreTimeSetting().getMinWorkTime().valueAsMinutes() > predetemineTimeSet
						.getPredTime().getPredTime().getOneDay().valueAsMinutes()) {
			throw new BusinessException("Msg_775");
		}
		
		// 使用区分 = 使用する AND NOT( 午前終了時刻 >=  コア開始 AND 午後開始時刻 <= コア終了) => Msg_777
		if (flexWorkSetting.getCoreTimeSetting().getTimesheet().equals(ApplyAtr.USE)
				&& !(predetemineTimeSet.getPrescribedTimezoneSetting().getMorningEndTime()
						.valueAsMinutes() >= flexWorkSetting.getCoreTimeSetting().getCoreTimeSheet().getStartTime()
								.valueAsMinutes()
				&& predetemineTimeSet.getPrescribedTimezoneSetting().getAfternoonStartTime()
						.valueAsMinutes() <= flexWorkSetting.getCoreTimeSetting().getCoreTimeSheet().getEndTime()
								.valueAsMinutes())) {
			throw new BusinessException("Msg_777");
		}
		
		// 使用区分 = 使用する 
		if (flexWorkSetting.getCoreTimeSetting().getTimesheet().equals(ApplyAtr.USE)){
			
			// get time zone 
			TimezoneUse timezone =  predetemineTimeSet.getPrescribedTimezoneSetting().getTimezoneShiftOne();
			
			// 開始 = 勤務NO = 1の場合の 所定時間帯設定.時間帯.開始
			int startTime = timezone.getStart().valueAsMinutes();
			
			// 終了 = 勤務NO = 1の場合の 所定時間帯設定.時間帯.終了
			int endTime = timezone.getEnd().valueAsMinutes();
			
			// NOT (コアタイム時間帯.開始時刻 >= 開始  &&  コアタイム時間帯.終了時刻 <= 終了) => Msg_773
			if (!(flexWorkSetting.getCoreTimeSetting().getCoreTimeSheet().getStartTime().valueAsMinutes() >= startTime
					&& flexWorkSetting.getCoreTimeSetting().getCoreTimeSheet().getEndTime()
							.valueAsMinutes() <= endTime)) {
				throw new BusinessException("Msg_773");
			}
		}
		
		// 使用区分 = 使用する
		if (flexWorkSetting.getCoreTimeSetting().getTimesheet().equals(ApplyAtr.USE)) {
//			this.service.validateOneDay(predetemineTimeSet,
//					flexWorkSetting.getCoreTimeSetting().getCoreTimeSheet().getStartTime(),
//					flexWorkSetting.getCoreTimeSetting().getCoreTimeSheet().getEndTime());
		}
		// Msg_781 DesignatedTime
//		this.service.compareWithOneDayRange(predetemineTimeSet,
//				flexWorkSetting.getCommonSetting().getSubHolTimeSet().getSubHolTimeSet().getDesignatedTime());
		
		// Msg_516
		flexWorkSetting.getLstStampReflectTimezone().forEach(setting -> {
//			this.service.validateOneDay(predetemineTimeSet, setting.getStartTime(), setting.getEndTime());
		});

		// valiadte FlexHalfDayWorkTime
		flexWorkSetting.getLstHalfDayWorkTimezone()
				.forEach(halfDay -> this.flexHalfDayPolicy.validate(halfDay, predetemineTimeSet));

		// validate FlexOffdayWorkTime
		this.flexOffdayPolicy.validate(predetemineTimeSet, flexWorkSetting.getOffdayWorkTime());

		// validate WorkTimezoneCommonSet
		this.wtzCommonSetPolicy.validate(predetemineTimeSet, flexWorkSetting.getCommonSetting());
	}
	

}
