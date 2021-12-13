/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.worktime.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.worktime.common.BooleanGetAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.HolidayCalculation;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneExtraordTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneGoOutSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneLateEarlySet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneLateNightTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneMedicalSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneOtherSubHolTimeSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneShortTimeWorkSet;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneStampSet;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtComMedical;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtMedicalTimeSetPK;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtComHdcom;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtSubstitutionSetPK;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWtCom;
import nts.uk.ctx.at.shared.infra.entity.worktime.common.KshmtWorktimeCommonSetPK;

/**
 * The Class JpaWorkTimezoneCommonSetSetMemento.
 */
public class JpaWorkTimezoneCommonSetSetMemento implements WorkTimezoneCommonSetSetMemento {

	/** The entity. */
	private KshmtWtCom entity;

	/**
	 * Instantiates a new jpa work timezone common set set memento.
	 *
	 * @param entity the entity
	 */
	public JpaWorkTimezoneCommonSetSetMemento(KshmtWtCom entity) {
		super();
		if (entity.getKshmtWorktimeCommonSetPK() == null) {
			entity.setKshmtWorktimeCommonSetPK(new KshmtWorktimeCommonSetPK());
		}
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setZeroHStraddCalculateSet(boolean)
	 */
	@Override
	public void setZeroHStraddCalculateSet(boolean calcSet) {
		this.entity.setOverDayCalcSet(BooleanGetAtr.getAtrByBoolean(calcSet));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setIntervalSet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * IntervalTimeSetting)
	 */
//	@Override
//	public void setIntervalSet(IntervalTimeSetting itvset) {
//		itvset.saveToMemento(new JpaIntervalTimeSettingSetMemento(this.entity));
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setSubHolTimeSet(java.util.List)
	 */
	@Override
	public void setSubHolTimeSet(List<WorkTimezoneOtherSubHolTimeSet> shtSet) {
		if (CollectionUtil.isEmpty(shtSet)) {
			return;
		}
		if (CollectionUtil.isEmpty(this.entity.getKshmtSubstitutionSets())) {
			this.entity.setKshmtSubstitutionSets(new ArrayList<>());
		}

		Map<KshmtSubstitutionSetPK, KshmtWtComHdcom> existShtSet = this.entity.getKshmtSubstitutionSets().stream()
				.collect(Collectors.toMap(KshmtWtComHdcom::getKshmtSubstitutionSetPK, Function.identity()));

		shtSet.forEach(domain -> {
			KshmtSubstitutionSetPK pk = new KshmtSubstitutionSetPK(this.entity.getKshmtWorktimeCommonSetPK().getCid(),
					this.entity.getKshmtWorktimeCommonSetPK().getWorktimeCd(),
					this.entity.getKshmtWorktimeCommonSetPK().getWorkFormAtr(),
					this.entity.getKshmtWorktimeCommonSetPK().getWorktimeSetMethod(), domain.getOriginAtr().value);
			KshmtWtComHdcom entity = existShtSet.get(pk);
			if (entity == null) {
				entity = new KshmtWtComHdcom();
			}

			domain.saveToMemento(new JpaWorkTimezoneOtherSubHolTimeSetSetMemento(entity));
			entity.setKshmtSubstitutionSetPK(pk);
			existShtSet.put(entity.getKshmtSubstitutionSetPK(), entity);
		});
		this.entity.setKshmtSubstitutionSets(existShtSet.values().stream().collect(Collectors.toList()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setMedicalSet(java.util.List)
	 */
	@Override
	public void setMedicalSet(List<WorkTimezoneMedicalSet> list) {
		if (CollectionUtil.isEmpty(list)) {
			return;
		}
		if (CollectionUtil.isEmpty(this.entity.getKshmtMedicalTimeSets())) {
			this.entity.setKshmtMedicalTimeSets(new ArrayList<>());
		}

		Map<KshmtMedicalTimeSetPK, KshmtWtComMedical> existShtSet = this.entity.getKshmtMedicalTimeSets().stream()
				.collect(Collectors.toMap(KshmtWtComMedical::getKshmtMedicalTimeSetPK, Function.identity()));

		list.forEach(domain -> {
			KshmtMedicalTimeSetPK pk = new KshmtMedicalTimeSetPK(this.entity.getKshmtWorktimeCommonSetPK().getCid(),
					this.entity.getKshmtWorktimeCommonSetPK().getWorktimeCd(),
					this.entity.getKshmtWorktimeCommonSetPK().getWorkFormAtr(),
					this.entity.getKshmtWorktimeCommonSetPK().getWorktimeSetMethod(), domain.getWorkSystemAtr().value);
			KshmtWtComMedical entity = existShtSet.get(pk);

			if (entity == null) {
				entity = new KshmtWtComMedical();
			}

			domain.saveToMemento(new JpaWorkTimezoneMedicalSetSetMemento(entity));
			entity.setKshmtMedicalTimeSetPK(pk);
			existShtSet.put(entity.getKshmtMedicalTimeSetPK(), entity);
		});
		this.entity.setKshmtMedicalTimeSets(existShtSet.values().stream().collect(Collectors.toList()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setGoOutSet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * WorkTimezoneGoOutSet)
	 */
	@Override
	public void setGoOutSet(WorkTimezoneGoOutSet set) {
		set.saveToMemento(new JpaWorkTimezoneGoOutSetSetMemento(this.entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setStampSet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * WorkTimezoneStampSet)
	 */
	@Override
	public void setStampSet(WorkTimezoneStampSet set) {
		set.saveToMemento(new JpaWorkTimezoneStampSetSetMemento(this.entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setLateNightTimeSet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * WorkTimezoneLateNightTimeSet)
	 */
	@Override
	public void setLateNightTimeSet(WorkTimezoneLateNightTimeSet set) {
		this.entity.setLateNightUnit(set.getRoundingSetting().getRoundingTime().value);
		this.entity.setLateNightRounding(set.getRoundingSetting().getRounding().value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setShortTimeWorkSet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * WorkTimezoneShortTimeWorkSet)
	 */
	@Override
	public void setShortTimeWorkSet(WorkTimezoneShortTimeWorkSet set) {
		this.entity.setNurTimezoneWorkUse(BooleanGetAtr.getAtrByBoolean(set.isNursTimezoneWorkUse()));
		this.entity.setEmpTimeDeduct(BooleanGetAtr.getAtrByBoolean(set.isEmploymentTimeDeduct()));
		this.entity.setChildCareWorkUse(BooleanGetAtr.getAtrByBoolean(set.isChildCareWorkUse()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setExtraordTimeSet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * WorkTimezoneExtraordTimeSet)
	 */
	@Override
	public void setExtraordTimeSet(WorkTimezoneExtraordTimeSet set) {
		set.saveToMemento(new JpaWorkTimezoneExtraordTimeSetSetMemento(this.entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setLateEarlySet(nts.uk.ctx.at.shared.dom.worktime.common.
	 * WorkTimezoneLateEarlySet)
	 */
	@Override
	public void setLateEarlySet(WorkTimezoneLateEarlySet set) {
		set.saveToMemento(new JpaWorkTimezoneLateEarlySetSetMemento(this.entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#
	 * setHolidayCalculation(nts.uk.ctx.at.shared.dom.worktime.common.
	 * HolidayCalculation)
	 */
	@Override
	public void setHolidayCalculation(HolidayCalculation holidayCalculation) {
		holidayCalculation.saveToMememto(new JpaHolidayCalculationSetMemento(this.entity));
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.worktime.common.WorkTimezoneCommonSetSetMemento#setRaisingSalarySet(java.util.Optional)
	 */
	@Override
	public void setRaisingSalarySet(Optional<BonusPaySettingCode> set) {
		//this.entity.setRaisingSalarySet(set.isPresent() ? set.get().v() : null);
	}

}
