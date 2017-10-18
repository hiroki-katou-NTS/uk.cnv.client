/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.executionlog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtSchErrorLog;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtSchErrorLogPK_;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtSchErrorLog_;

/**
 * The Class JpaScheduleErrorLogRepository.
 */
@Stateless
public class JpaScheduleErrorLogRepository extends JpaRepository
		implements ScheduleErrorLogRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository#
	 * findByExecutionId(java.lang.String)
	 */
	@Override
	public List<ScheduleErrorLog> findByExecutionId(String executionId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KscmtSchErrorLog> cq = criteriaBuilder.createQuery(KscmtSchErrorLog.class);
		Root<KscmtSchErrorLog> root = cq.from(KscmtSchErrorLog.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere.add(criteriaBuilder.equal(
				root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK).get(KscmtSchErrorLogPK_.exeId), executionId));

		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		cq.orderBy(criteriaBuilder
				.desc(root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK).get(KscmtSchErrorLogPK_.ymd)));

		List<KscmtSchErrorLog> lstKscmtScheduleErrLog = em.createQuery(cq).getResultList();
		// check empty
		if (CollectionUtil.isEmpty(lstKscmtScheduleErrLog)) {
			return null;
		}
		return lstKscmtScheduleErrLog.stream().map(item -> {
			return new ScheduleErrorLog(new JpaScheduleErrorLogGetMemento(item));
		}).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository#
	 * findByEmployeeId(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ScheduleErrorLog> findByEmployeeId(String executionId, String employeeId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KscmtSchErrorLog> cq = criteriaBuilder
				.createQuery(KscmtSchErrorLog.class);
		Root<KscmtSchErrorLog> root = cq.from(KscmtSchErrorLog.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		
		lstpredicateWhere
				.add(criteriaBuilder.equal(root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK)
						.get(KscmtSchErrorLogPK_.exeId), executionId));
		
		lstpredicateWhere
				.add(criteriaBuilder.equal(root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK)
						.get(KscmtSchErrorLogPK_.sid), employeeId));

		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		
		cq.orderBy(criteriaBuilder.desc(root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK)
				.get(KscmtSchErrorLogPK_.ymd)));

		List<KscmtSchErrorLog> lstKscmtScheduleErrLog = em.createQuery(cq).getResultList();
		// check empty
		if (CollectionUtil.isEmpty(lstKscmtScheduleErrLog)) {
			return null;
		}
		return lstKscmtScheduleErrLog.stream().map(item -> {
			return new ScheduleErrorLog(new JpaScheduleErrorLogGetMemento(item));
		}).collect(Collectors.toList());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository#
	 * distinctErrorByExecutionId(java.lang.String)
	 */
	@Override
	public Integer distinctErrorByExecutionId(String executionId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
		Root<KscmtSchErrorLog> root = cq.from(KscmtSchErrorLog.class);

		// select root
		cq.select(criteriaBuilder.countDistinct(root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK)
				.get(KscmtSchErrorLogPK_.sid)));

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere
				.add(criteriaBuilder.equal(root.get(KscmtSchErrorLog_.kscmtSchErrorLogPK)
						.get(KscmtSchErrorLogPK_.exeId), executionId));

		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		int cntError = em.createQuery(cq).getSingleResult().intValue();
		return cntError;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository#add(
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog)
	 */
	@Override
	public void add(ScheduleErrorLog domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}
	
	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the kscmt schedule err log
	 */
	private KscmtSchErrorLog toEntity(ScheduleErrorLog domain) {
		KscmtSchErrorLog entity = new KscmtSchErrorLog();
		domain.saveToMemento(new JpaScheduleErrorLogSetMemento(entity));
		return entity;
	}


}
