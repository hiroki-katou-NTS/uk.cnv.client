/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktime.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.common.timerounding.TimeRoundingSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.worktime.common.BooleanGetAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.HolidayCalculation;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneExtraordTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneGoOutSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneLateEarlySet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneLateNightTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneMedicalSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneOtherSubHolTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneShortTimeWorkSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSet;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtComTemporary;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtCom;

/**
 * The Class JpaWorkTimezoneCommonSetGetMemento.
 */
public class JpaWorkTimezoneCommonSetGetMemento implements WorkTimezoneCommonSetGetMemento {

	/** The entity. */
	private KshmtWtCom entity;

	/**
	 * Instantiates a new jpa work timezone common set get memento.
	 *
	 * @param entity
	 *            the entity
	 */
	public JpaWorkTimezoneCommonSetGetMemento(KshmtWtCom entity) {
		super();
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getZeroHStraddCalculateSet()
	 */
	@Override
	public boolean getZeroHStraddCalculateSet() {
		return BooleanGetAtr.getAtrByInteger(this.entity.getOverDayCalcSet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getIntervalSet()
	 */
//	@Override
//	public IntervalTimeSetting getIntervalSet() {
//		return new IntervalTimeSetting(new JpaIntervalTimeSettingGetMemento(entity));
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getSubHolTimeSet()
	 */
	@Override
	public List<WorkTimezoneOtherSubHolTimeSet> getSubHolTimeSet() {
		if (CollectionUtil.isEmpty(this.entity.getKshmtSubstitutionSets())) {
			return new ArrayList<>();
		}
		return this.entity.getKshmtSubstitutionSets().stream()
				.map(item -> new WorkTimezoneOtherSubHolTimeSet(new JpaWorkTimezoneOtherSubHolTimeSetGetMemento(item)))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getMedicalSet()
	 */
	@Override
	public List<WorkTimezoneMedicalSet> getMedicalSet() {
		if (CollectionUtil.isEmpty(this.entity.getKshmtMedicalTimeSets())) {
			return new ArrayList<>();
		}
		return this.entity.getKshmtMedicalTimeSets().stream()
				.map(item -> new WorkTimezoneMedicalSet(new JpaWorkTimezoneMedicalSetGetMemento(item)))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getGoOutSet()
	 */
	@Override
	public WorkTimezoneGoOutSet getGoOutSet() {
		return new WorkTimezoneGoOutSet(new JpaWorkTimezoneGoOutSetGetMemento(this.entity.getKshmtWtComGoout(),
				this.entity.getKshmtSpecialRoundOuts()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getStampSet()
	 */
	@Override
	public WorkTimezoneStampSet getStampSet() {
		return new WorkTimezoneStampSet(new JpaWorkTimezoneStampSetGetMemento(this.entity.getKshmtWtComStmp()));
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getLateNightTimeSet()
	 */
	@Override
	public WorkTimezoneLateNightTimeSet getLateNightTimeSet() {
		return new WorkTimezoneLateNightTimeSet(
				new TimeRoundingSetting(this.entity.getLateNightUnit(), this.entity.getLateNightRounding()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getShortTimeWorkSet()
	 */
	@Override
	public WorkTimezoneShortTimeWorkSet getShortTimeWorkSet() {
		return new WorkTimezoneShortTimeWorkSet(BooleanGetAtr.getAtrByInteger(this.entity.getNurTimezoneWorkUse()),
				BooleanGetAtr.getAtrByInteger(this.entity.getEmpTimeDeduct()),
				BooleanGetAtr.getAtrByInteger(this.entity.getChildCareWorkUse()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getExtraordTimeSet()
	 */
	@Override
	public WorkTimezoneExtraordTimeSet getExtraordTimeSet() {
		KshmtWtComTemporary entityTemp = this.entity.getKshmtTempWorktimeSet();
		return new WorkTimezoneExtraordTimeSet(new JpaWorkTimezoneExtraordTimeSetGetMemento(entityTemp));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getLateEarlySet()
	 */
	@Override
	public WorkTimezoneLateEarlySet getLateEarlySet() {
		return new WorkTimezoneLateEarlySet(new JpaWorkTimezoneLateEarlySetGetMemento(this.entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getHolidayCalculation()
	 */
	@Override
	public HolidayCalculation getHolidayCalculation() {
		return new HolidayCalculation(new JpaHolidayCalculationGetMemento(this.entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetGetMemento#
	 * getRaisingSalarySet()
	 */
	@Override
	public Optional<BonusPaySettingCode> getRaisingSalarySet() {
		return Optional.ofNullable(new BonusPaySettingCode(null));
	}

}
