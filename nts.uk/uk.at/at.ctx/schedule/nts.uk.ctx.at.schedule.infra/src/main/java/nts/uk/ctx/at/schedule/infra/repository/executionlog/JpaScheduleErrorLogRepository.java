/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.executionlog;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscdtScheErrLog;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscdtScheErrLogPK;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscdtScheErrLogPK_;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscdtScheErrLog_;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaScheduleErrorLogRepository.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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

		CriteriaQuery<KscdtScheErrLog> cq = criteriaBuilder.createQuery(KscdtScheErrLog.class);
		Root<KscdtScheErrLog> root = cq.from(KscdtScheErrLog.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere.add(criteriaBuilder.equal(
				root.get(KscdtScheErrLog_.kscdtScheErrLogPK).get(KscdtScheErrLogPK_.exeId), executionId));

		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		cq.orderBy(criteriaBuilder
				.desc(root.get(KscdtScheErrLog_.kscdtScheErrLogPK).get(KscdtScheErrLogPK_.ymd)));

		List<KscdtScheErrLog> lstKscmtScheduleErrLog = em.createQuery(cq).getResultList();
		// check empty
		if (CollectionUtil.isEmpty(lstKscmtScheduleErrLog)) {
			return Collections.emptyList();
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

		CriteriaQuery<KscdtScheErrLog> cq = criteriaBuilder
				.createQuery(KscdtScheErrLog.class);
		Root<KscdtScheErrLog> root = cq.from(KscdtScheErrLog.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		
		lstpredicateWhere
				.add(criteriaBuilder.equal(root.get(KscdtScheErrLog_.kscdtScheErrLogPK)
						.get(KscdtScheErrLogPK_.exeId), executionId));
		
		lstpredicateWhere
				.add(criteriaBuilder.equal(root.get(KscdtScheErrLog_.kscdtScheErrLogPK)
						.get(KscdtScheErrLogPK_.sid), employeeId));

		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		
		cq.orderBy(criteriaBuilder.desc(root.get(KscdtScheErrLog_.kscdtScheErrLogPK)
				.get(KscdtScheErrLogPK_.ymd)));

		List<KscdtScheErrLog> lstKscmtScheduleErrLog = em.createQuery(cq).getResultList();
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
		Root<KscdtScheErrLog> root = cq.from(KscdtScheErrLog.class);

		// select root
		cq.select(criteriaBuilder
				.countDistinct(root.get(KscdtScheErrLog_.kscdtScheErrLogPK).get(KscdtScheErrLogPK_.sid)));

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere.add(criteriaBuilder
				.equal(root.get(KscdtScheErrLog_.kscdtScheErrLogPK).get(KscdtScheErrLogPK_.exeId), executionId));

		cq.where(lstpredicateWhere.toArray(new Predicate[]{}));
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
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void addByTransaction(ScheduleErrorLog domain) {
			this.commandProxy().insert(this.toEntity(domain));
	}

	@Override
	public Optional<ScheduleErrorLog> findByKey(String executionId, String employeeId, GeneralDate baseDate) {
		return this.queryProxy().find(new KscdtScheErrLogPK(executionId, employeeId, baseDate), KscdtScheErrLog.class)
				.map(entity -> new ScheduleErrorLog(new JpaScheduleErrorLogGetMemento(entity)));
	}
	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the kscmt schedule err log
	 */
	private KscdtScheErrLog toEntity(ScheduleErrorLog domain) {
		KscdtScheErrLog entity = new KscdtScheErrLog();
		val cid = AppContexts.user().companyId();
		val cd = AppContexts.user().contractCode();
		domain.saveToMemento(new JpaScheduleErrorLogSetMemento(entity));
		entity.setCid(cid);
		entity.setContractCd(cd);
		return entity;
	}

	@Override
	public Integer distinctErrorByExecutionIdNew(String executionId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
		Root<KscdtScheErrLog> root = cq.from(KscdtScheErrLog.class);
		// select root
		cq.select(criteriaBuilder.count(root));

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere.add(criteriaBuilder
				.equal(root.get(KscdtScheErrLog_.kscdtScheErrLogPK).get(KscdtScheErrLogPK_.exeId), executionId));

		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		int cntError = em.createQuery(cq).getSingleResult().intValue();
		return cntError;
	}
	
	@Override
	public Boolean checkExistErrorByKey(String executionId, String employeeId, GeneralDate baseDate) {
		
		String sqlQuery = "select count(*) from KSCDT_BATCH_ERR_LOG where YMD = " + "'" + baseDate + "' and EXE_ID = " + "'" + executionId + "'"
				+ "and SID = " + "'" + employeeId + "'";
		
		Connection con = this.getEntityManager().unwrap(Connection.class);
		
		try {
			ResultSet rs = con.createStatement().executeQuery(sqlQuery);
			int number = 0;
			while(rs.next()){
				number = rs.getInt(1);
			}
			if(number > 0) return true;
			return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Boolean checkExistErrorByKey(String executionId, String employeeId) {
		String sqlQuery = "select count(*) from KSCDT_BATCH_ERR_LOG where EXE_ID = " + "'" + executionId + "'"
				+ "and SID = " + "'" + employeeId + "'";
		
		Connection con = this.getEntityManager().unwrap(Connection.class);
		
		try {
			ResultSet rs = con.createStatement().executeQuery(sqlQuery);
			int number = 0;
			while(rs.next()){
				number = rs.getInt(1);
			}
			if(number > 0) return true;
			return false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(ScheduleErrorLog domain) {
		this.commandProxy().update(this.toEntity(domain));
	}

}
