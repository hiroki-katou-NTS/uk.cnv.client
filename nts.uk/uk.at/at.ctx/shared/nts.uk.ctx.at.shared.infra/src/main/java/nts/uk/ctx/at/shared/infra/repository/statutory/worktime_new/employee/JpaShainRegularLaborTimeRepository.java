/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.statutory.worktime_new.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.common.TimeOfDay;
import nts.uk.ctx.at.shared.dom.common.WeeklyTime;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.DailyUnit;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.WeeklyUnit;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeSha;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.week.regular.RegularLaborTimeShaRepo;
import nts.uk.ctx.at.shared.infra.entity.statutory.worktime_new.employee.KshmtLegaltimeDRegSya;
import nts.uk.ctx.at.shared.infra.entity.statutory.worktime_new.employee.KshstShaRegLaborTimePK;
import nts.uk.ctx.at.shared.infra.entity.statutory.worktime_new.employee.KshstShaRegLaborTimePK_;
import nts.uk.ctx.at.shared.infra.entity.statutory.worktime_new.employee.KshstShaRegLaborTime_;

/**
 * The Class JpaRegularLaborTimeShaRepository.
 */
@Stateless
public class JpaShainRegularLaborTimeRepository extends JpaRepository implements RegularLaborTimeShaRepo {

	/*
	 * @see nts.uk.ctx.at.shared.dom.statutory.worktime.employeeNew.
	 * ShainRegularWorkTimeRepository#add(nts.uk.ctx.at.shared.dom.statutory.
	 * worktime.employeeNew.ShainRegularWorkTime)
	 */
	@Override
	public void add(RegularLaborTimeSha setting) {
		commandProxy().insert(this.toEntity(setting));
	}

	/*
	 * @see nts.uk.ctx.at.shared.dom.statutory.worktime.employeeNew.
	 * ShainRegularWorkTimeRepository#update(nts.uk.ctx.at.shared.dom.statutory.
	 * worktime.employeeNew.ShainRegularWorkTime)
	 */
	@Override
	public void update(RegularLaborTimeSha setting) {
		commandProxy().update(this.toEntity(setting));
	}

	/*
	 * @see nts.uk.ctx.at.shared.dom.statutory.worktime.employeeNew.
	 * ShainRegularWorkTimeRepository#delete(nts.uk.ctx.at.shared.dom.statutory.
	 * worktime.employeeNew.ShainRegularWorkTime)
	 */
	@Override
	public void delete(String cid, String empId) {
		commandProxy().remove(KshmtLegaltimeDRegSya.class, new KshstShaRegLaborTimePK(cid, empId));
	}

	/*
	 * @see nts.uk.ctx.at.shared.dom.statutory.worktime.employeeNew.
	 * ShainRegularWorkTimeRepository#find(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<RegularLaborTimeSha> find(String cid, String empId) {
		Optional<KshmtLegaltimeDRegSya> optEntity = this.queryProxy().find(new KshstShaRegLaborTimePK(cid, empId), KshmtLegaltimeDRegSya.class);

		// Check exist
		if (!optEntity.isPresent()) {
			return Optional.empty();
		}

		return Optional.ofNullable(this.toDomain(optEntity.get()));
	}

	/**
	 * To entity.
	 *
	 * @param emplRegWorkHour
	 *            the empl reg work hour
	 * @return the kshst sha reg labor time
	 */
	private KshmtLegaltimeDRegSya toEntity(RegularLaborTimeSha domain) {
		KshmtLegaltimeDRegSya entity = new KshmtLegaltimeDRegSya();

		entity.setDailyTime(domain.getDailyTime().getDailyTime().v());
		entity.setWeeklyTime(domain.getWeeklyTime().getTime().v());
		entity.setKshstShaRegLaborTimePK(new KshstShaRegLaborTimePK(domain.getComId(), domain.getEmpId()));
		
		return entity;
	}

	/**
	 * To domain.
	 *
	 * @param entity
	 *            the entity
	 * @return the shain regular work time
	 */
	private RegularLaborTimeSha toDomain(KshmtLegaltimeDRegSya entity) {
		return RegularLaborTimeSha.of(entity.getKshstShaRegLaborTimePK().getCid(),
				entity.getKshstShaRegLaborTimePK().getSid(),
				new WeeklyUnit(new WeeklyTime(entity.getWeeklyTime())), 
				new DailyUnit(new TimeOfDay(entity.getDailyTime())));
	}
	
	/**
	 * To domain.
	 *
	 * @param entities the entities
	 * @return the list
	 */
	private List<RegularLaborTimeSha> toDomain(List<KshmtLegaltimeDRegSya> entities) {
		if (entities == null || entities.isEmpty()) {
			return Collections.emptyList();
		}
		
		return entities.stream().map(entity -> toDomain(entity)).collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.statutory.worktime.employeeNew.ShainRegularWorkTimeRepository#findAll(java.lang.String)
	 */
	@Override
	public List<RegularLaborTimeSha> findAll(String cid) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<KshmtLegaltimeDRegSya> cq = cb.createQuery(KshmtLegaltimeDRegSya.class);
		Root<KshmtLegaltimeDRegSya> root = cq.from(KshmtLegaltimeDRegSya.class);

		// Constructing condition.
		List<Predicate> predicateList = new ArrayList<Predicate>();
		predicateList
				.add(cb.equal(root.get(KshstShaRegLaborTime_.kshstShaRegLaborTimePK).get(KshstShaRegLaborTimePK_.cid), cid));
		cq.where(predicateList.toArray(new Predicate[] {}));

		List<KshmtLegaltimeDRegSya> resultList = em.createQuery(cq).getResultList();

		return this.toDomain(resultList);
	}

	@Override
	public List<RegularLaborTimeSha> findList(String cid, List<String> empId) {

		if (empId.isEmpty())
			return Collections.emptyList();
		String query = "SELECT s FROM KshmtLegaltimeDRegSya s"
				+ " where s.kshstShaRegLaborTimePK.cid = :cid"
				+ " and s.kshstShaRegLaborTimePK.sid in :empId";
		List<RegularLaborTimeSha> result = new ArrayList<>();
		CollectionUtil.split(empId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			result.addAll(this.queryProxy().query(query, KshmtLegaltimeDRegSya.class)
					.setParameter("cid", cid)
					.setParameter("empId", subIdList)
					.getList(f -> toDomain(f)));
		});
		return result;
	}
}
