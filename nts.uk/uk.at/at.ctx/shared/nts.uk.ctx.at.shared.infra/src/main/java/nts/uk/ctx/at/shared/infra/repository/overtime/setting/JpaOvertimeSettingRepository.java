/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.overtime.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.shared.dom.outsideot.OutsideOTSetting;
import nts.uk.ctx.at.shared.dom.outsideot.OutsideOTSettingRepository;
import nts.uk.ctx.at.shared.dom.outsideot.breakdown.OutsideOTBRDItem;
import nts.uk.ctx.at.shared.dom.outsideot.breakdown.OutsideOTBRDItemRepository;
import nts.uk.ctx.at.shared.dom.outsideot.overtime.Overtime;
import nts.uk.ctx.at.shared.dom.outsideot.overtime.OvertimeRepository;
import nts.uk.ctx.at.shared.infra.entity.outsideot.KshstOverTimeSet;
import nts.uk.ctx.at.shared.infra.entity.outsideot.breakdown.KshstOutsideOtBrd;
import nts.uk.ctx.at.shared.infra.entity.outsideot.overtime.KshstOverTime;
import nts.uk.ctx.at.shared.infra.repository.outsideot.breakdown.JpaOvertimeBRDItemSetMemento;
import nts.uk.ctx.at.shared.infra.repository.outsideot.overtime.JpaOvertimeSetMemento;

/**
 * The Class JpaOvertimeSettingRepository.
 */
@Stateless
public class JpaOvertimeSettingRepository extends JpaRepository
		implements OutsideOTSettingRepository {

	/** The repository. */
	@Inject
	private OvertimeRepository overtimeRepository;
	
	/** The overtime BRD item repository. */
	@Inject
	private OutsideOTBRDItemRepository overtimeBRDItemRepository;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.overtime.OvertimeSettingRepository#findById(java
	 * .lang.String)
	 */
	@Override
	public Optional<OutsideOTSetting> findById(String companyId) {

		// call repository find entity setting
		Optional<KshstOverTimeSet> entity = this.queryProxy().find(companyId,
				KshstOverTimeSet.class);

		// call repository find all domain overtime
		List<Overtime> domainOvertime = this.overtimeRepository.findAllUse(companyId);

		// call repository find all domain overtime break down item
		List<OutsideOTBRDItem> domainOvertimeBrdItem = this.overtimeBRDItemRepository
				.findAllUse(companyId);

		// domain to entity
		List<KshstOverTime> entityOvertime = domainOvertime.stream().map(domain -> {
			KshstOverTime entityItem = new KshstOverTime();
			domain.saveToMemento(new JpaOvertimeSetMemento(entityItem, companyId));
			return entityItem;
		}).collect(Collectors.toList());

		// domain to entity
		List<KshstOutsideOtBrd> entityOvertimeBRDItem = domainOvertimeBrdItem.stream()
				.map(domain -> {
					KshstOutsideOtBrd entityItem = new KshstOutsideOtBrd();
					domain.saveToMemento(new JpaOvertimeBRDItemSetMemento(entityItem, companyId));
					return entityItem;
				}).collect(Collectors.toList());

		// check exist data
		if (entity.isPresent()) {
			return Optional
					.ofNullable(this.toDomain(entity.get(), entityOvertimeBRDItem, entityOvertime));
		}
		// default data
		return Optional.ofNullable(
				this.toDomain(new KshstOverTimeSet(), entityOvertimeBRDItem, entityOvertime));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.overtime.setting.OvertimeSettingRepository#save(
	 * nts.uk.ctx.at.shared.dom.overtime.setting.OvertimeSetting)
	 */
	@Override
	public void save(OutsideOTSetting domain) {
		KshstOverTimeSet entity = new KshstOverTimeSet();
		// call repository find entity setting
		Optional<KshstOverTimeSet> opEntity = this.queryProxy().find(domain.getCompanyId().v(),
				KshstOverTimeSet.class);
		
		// check exist data
		if(opEntity.isPresent()){
			entity = opEntity.get();
			entity = this.toEntity(domain);
			this.commandProxy().update(entity);
		}
		// insert data
		else {
			entity = this.toEntity(domain);
			this.commandProxy().insert(entity);
		}
		// save all overtime
		overtimeRepository.saveAll(domain.getOvertimes(), domain.getCompanyId().v());
		
		// save all over time breakdown item
		overtimeBRDItemRepository.saveAll(domain.getBreakdownItems(), domain.getCompanyId().v());
	}
	/**
	 * To domain.
	 *
	 * @param entity the entity
	 * @param entityOvertimeBRDItems the entity overtime BRD items
	 * @param entityOvertime the entity overtime
	 * @return the overtime setting
	 */
	private OutsideOTSetting toDomain(KshstOverTimeSet entity,
			List<KshstOutsideOtBrd> entityOvertimeBRDItems, List<KshstOverTime> entityOvertime) {
		return new OutsideOTSetting(
				new JpaOvertimeSettingGetMemento(entity, entityOvertimeBRDItems, entityOvertime));
	}

	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the kshst over time set
	 */
	private KshstOverTimeSet toEntity(OutsideOTSetting domain) {
		KshstOverTimeSet entity = new KshstOverTimeSet();
		domain.saveToMemento(
				new JpaOvertimeSettingSetMemento(new ArrayList<>(), new ArrayList<>(), entity));
		return entity;
	}
	

}
