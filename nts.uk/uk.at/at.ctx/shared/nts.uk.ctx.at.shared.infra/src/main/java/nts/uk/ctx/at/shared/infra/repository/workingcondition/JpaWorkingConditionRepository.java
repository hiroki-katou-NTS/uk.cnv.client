/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.repository.workingcondition;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemWithPeriod;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.infra.entity.workingcondition.KshmtWorkcondHist;
import nts.uk.ctx.at.shared.infra.entity.workingcondition.KshmtWorkcondHistItem;
import nts.uk.ctx.at.shared.infra.entity.workingcondition.KshmtWorkingCondPK_;
import nts.uk.ctx.at.shared.infra.entity.workingcondition.KshmtWorkingCond_;

import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * The Class JpaWorkingConditionRepository.
 */
@Stateless
public class JpaWorkingConditionRepository extends JpaRepository implements WorkingConditionRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#
	 * getAllWokingCondition(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<WorkingCondition> getBySid(String companyId, String sId) {

		List<KshmtWorkcondHist> result = this.findBy(companyId, sId, null);

		// Check exist
		if (CollectionUtil.isEmpty(result)) {
			return Optional.empty();
		}

		// exclude select
		return Optional.of(new WorkingCondition(new JpaWorkingConditionGetMemento(result)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#
	 * getBySid(java.lang.String)
	 */
	@Override
	public Optional<WorkingCondition> getBySid(String sId) {

		List<KshmtWorkcondHist> result = this.findBy(null, sId, null);

		// Check exist
		if (CollectionUtil.isEmpty(result)) {
			return Optional.empty();
		}

		// exclude select
		return Optional.of(new WorkingCondition(new JpaWorkingConditionGetMemento(result)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#
	 * getByHistoryId(java.lang.String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<WorkingCondition> getByHistoryId(String historyId) {
		List<KshmtWorkcondHist> result = this.findBy(null, null, historyId);

		// Check exist
		if (CollectionUtil.isEmpty(result)) {
			return Optional.empty();
		}

		// exclude select
		return Optional.of(new WorkingCondition(new JpaWorkingConditionGetMemento(result)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#
	 * getBySidAndStandardDate(java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<WorkingCondition> getBySidAndStandardDate(String employeeId, GeneralDate baseDate) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();

		// eq company id
		lstpredicateWhere.add(criteriaBuilder
				.equal(root.get(KshmtWorkingCond_.kshmtWorkingCondPK).get(KshmtWorkingCondPK_.sid), employeeId));
		lstpredicateWhere.add(criteriaBuilder.lessThanOrEqualTo(root.get(KshmtWorkingCond_.strD), baseDate));
		lstpredicateWhere.add(criteriaBuilder.greaterThanOrEqualTo(root.get(KshmtWorkingCond_.endD), baseDate));

		// set where to SQL
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

		// creat query
		TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);

		List<KshmtWorkcondHist> result = query.getResultList();

		// Check exist
		if (CollectionUtil.isEmpty(result)) {
			return Optional.empty();
		}

		return Optional.of(new WorkingCondition(new JpaWorkingConditionGetMemento(result)));
	}

	/**
	 * Adds the.
	 *
	 * @param entities
	 *            the entities
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#add(
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition)
	 */
	private void add(List<KshmtWorkcondHist> entities) {
		this.commandProxy().insertAll(entities);
		this.getEntityManager().flush();
	}

	/**
	 * Delete all.
	 *
	 * @param entities
	 *            the entities
	 */
	private void deleteAll(List<KshmtWorkcondHist> entities) {
		this.commandProxy().removeAll(entities);
		this.getEntityManager().flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#save
	 * (nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition)
	 */
	@Override
	@Transactional
	public void save(WorkingCondition workingCondition) {
		List<KshmtWorkcondHist> entities = this.findBy(workingCondition.getCompanyId(), workingCondition.getEmployeeId(),
		null);
		
		List<KshmtWorkcondHist> newWorkingCondition = new ArrayList<>(entities);

		workingCondition.saveToMemento(new JpaWorkingConditionSetMemento(newWorkingCondition));
		
		this.add(newWorkingCondition.stream()
				.filter(item -> {
					int index = entities.indexOf(item);
					if (index == -1) {
						return true;
					}				
					
					KshmtWorkcondHist oldItem = entities.get(index);
					if (oldItem.getStrD().equals(item.getStrD()) && oldItem.getEndD().equals(item.getEndD())) {
						return false;
					}
					return true;
				})
				.collect(Collectors.toList()));
		
		this.deleteAll(entities.stream().filter(item ->  !newWorkingCondition.contains(item)).collect(Collectors.toList()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#
	 * remove(java.lang.String, java.lang.String)
	 */
	@Override
	public void delete(String employeeId) {
		List<KshmtWorkcondHist> entities = this.findBy(null, employeeId, null);
		this.commandProxy().removeAll(entities);
	}

	/**
	 * Find by.
	 *
	 * @param companyId
	 *            the company id
	 * @param sId
	 *            the s id
	 * @param historyId
	 *            the history id
	 * @return the list
	 */
	private List<KshmtWorkcondHist> findBy(String companyId, String sId, String historyId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();

		// eq company id
		if (companyId != null) {
			lstpredicateWhere.add(criteriaBuilder.equal(root.get(KshmtWorkingCond_.cid), companyId));
		}

		if (sId != null) {
			lstpredicateWhere.add(criteriaBuilder
					.equal(root.get(KshmtWorkingCond_.kshmtWorkingCondPK).get(KshmtWorkingCondPK_.sid), sId));
		}

		if (historyId != null) {
			lstpredicateWhere.add(criteriaBuilder.equal(
					root.get(KshmtWorkingCond_.kshmtWorkingCondPK).get(KshmtWorkingCondPK_.historyId), historyId));
		}

		// set where to SQL
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

		// Order by start date
		cq.orderBy(criteriaBuilder.desc(root.get(KshmtWorkingCond_.strD)));

		// creat query
		TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);

		return query.getResultList();
	}
	

	/**
	 * Find by.
	 *
	 * @param companyId
	 *            the company id
	 * @param sId
	 *            the s id
	 * @param historyId
	 *            the history id
	 * @return the list
	 */
	private Map<String, List<KshmtWorkcondHist>> findBy(String cid, List<String> sids) {
		// Check exist
		if (CollectionUtil.isEmpty(sids)) {
			return new HashMap<>();
		}
				
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);
		
		List<KshmtWorkcondHist> result =  new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// add where
			List<Predicate> lstpredicateWhere = new ArrayList<>();
			
			// eq company id
			lstpredicateWhere.add(criteriaBuilder.equal(root.get(KshmtWorkingCond_.cid), cid));
			lstpredicateWhere.add(root.get(KshmtWorkingCond_.kshmtWorkingCondPK)
					.get(KshmtWorkingCondPK_.sid).in(subList));

			// set where to SQL
			cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

			// creat query
			TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);
			
			result.addAll(query.getResultList());
			
		});

		return result.stream()
				.collect(Collectors.groupingBy(entity -> entity.getKshmtWorkingCondPK().getSid()));

	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository
	 * #getBySidAndStandardDate(java.lang.String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<WorkingCondition> getBySidAndStandardDate(String companyId, String employeeId,
			GeneralDate baseDate) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();

		// eq company id
		lstpredicateWhere.add(criteriaBuilder
				.equal(root.get(KshmtWorkingCond_.cid), companyId));
		lstpredicateWhere.add(criteriaBuilder
				.equal(root.get(KshmtWorkingCond_.kshmtWorkingCondPK).get(KshmtWorkingCondPK_.sid), employeeId));
		lstpredicateWhere.add(criteriaBuilder.lessThanOrEqualTo(root.get(KshmtWorkingCond_.strD), baseDate));
		lstpredicateWhere.add(criteriaBuilder.greaterThanOrEqualTo(root.get(KshmtWorkingCond_.endD), baseDate));

		// set where to SQL
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

		// creat query
		TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);

		List<KshmtWorkcondHist> result = query.getResultList();

		// Check exist
		if (CollectionUtil.isEmpty(result)) {
			return Optional.empty();
		}

		return Optional.of(new WorkingCondition(new JpaWorkingConditionGetMemento(result)));
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#getBySids(java.util.List)
	 */
	@Override
	public List<WorkingCondition> getBySidsAndDatePeriod(List<String> employeeIds, DatePeriod datePeriod) {
		
		// Check exist
		if (CollectionUtil.isEmpty(employeeIds)) {
			return Collections.emptyList();
		}
				
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);
		
		List<KshmtWorkcondHist> result =  new ArrayList<>();
		
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// add where
			List<Predicate> lstpredicateWhere = new ArrayList<>();
			
			// eq company id
			lstpredicateWhere.add(root.get(KshmtWorkingCond_.kshmtWorkingCondPK)
					.get(KshmtWorkingCondPK_.sid).in(subList));
			lstpredicateWhere.add(criteriaBuilder.not(criteriaBuilder.or(
					criteriaBuilder.lessThan(root.get(KshmtWorkingCond_.endD), datePeriod.start()),
					criteriaBuilder.greaterThan(root.get(KshmtWorkingCond_.strD), datePeriod.end()))));

			// set where to SQL
			cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

			// creat query
			TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);
			
			result.addAll(query.getResultList());
		});

		List<WorkingCondition> data = result.parallelStream()
				.collect(Collectors.groupingBy(entity -> entity.getKshmtWorkingCondPK().getSid()))
				.values().parallelStream()
				.map(item -> new WorkingCondition(new JpaWorkingConditionGetMemento(item)))
				.collect(Collectors.toList());
		 return data;
	}
	
	@Override
	public List<WorkingCondition> getBySidsAndDatePeriodNew(List<String> employeeIds, DatePeriod datePeriod) {
		// Check exist
		if (CollectionUtil.isEmpty(employeeIds)) {
			return Collections.emptyList();
		}

		List<WorkingCondition> result = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "select * from KSHMT_WORKCOND_HIST h" + " where h.SID in ("
					+ NtsStatement.In.createParamsString(subList) + ")" + " and h.START_DATE <= ?"
					+ " and h.END_DATE >= ?" + " and h.CID >= ?";

			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {

				int i = 0;
				for (; i < subList.size(); i++) {
					stmt.setString(1 + i, subList.get(i));
				}

				stmt.setDate(1 + i, Date.valueOf(datePeriod.end().localDate()));
				stmt.setDate(2 + i, Date.valueOf(datePeriod.start().localDate()));
				stmt.setString(3 + i, companyId);

				result.addAll(new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					List<DateHistoryItem> lstHist = new ArrayList<>();
					lstHist.add(new DateHistoryItem(rec.getString("HIST_ID"),
							new DatePeriod(rec.getGeneralDate("START_DATE"), rec.getGeneralDate("END_DATE"))));
					WorkingCondition ent = new WorkingCondition(rec.getString("CID"), rec.getString("SID"), lstHist);
					return ent;
				}));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

		});

		Map<String, List<WorkingCondition>> mapGroupSId = result.stream()
				.collect(Collectors.groupingBy(x -> x.getEmployeeId()));
		return mapGroupSId.entrySet().stream().map(x -> {
			WorkingCondition wC = new WorkingCondition(companyId, x.getKey(),
					x.getValue().stream().flatMap(y -> y.getDateHistoryItem().stream()).collect(Collectors.toList()));
			return wC;
		}).collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository#getBySidsAndDatePeriod(java.util.List)
	 */
	@Override
	public List<WorkingCondition> getBySids(List<String> employeeIds,GeneralDate baseDate) {
		// Check exist
				if (CollectionUtil.isEmpty(employeeIds)) {
					return Collections.emptyList();
				}
						
				// get entity manager
				EntityManager em = this.getEntityManager();
				CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

				CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

				// root data
				Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

				// select root
				cq.select(root);
				
				List<KshmtWorkcondHist> result =  new ArrayList<>();
				
				CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
					// add where
					List<Predicate> lstpredicateWhere = new ArrayList<>();
					
					// eq company id
					lstpredicateWhere.add(root.get(KshmtWorkingCond_.kshmtWorkingCondPK)
							.get(KshmtWorkingCondPK_.sid).in(subList));
					lstpredicateWhere.add(criteriaBuilder.not(criteriaBuilder.or(
							criteriaBuilder.lessThan(root.get(KshmtWorkingCond_.endD), baseDate),
							criteriaBuilder.greaterThan(root.get(KshmtWorkingCond_.strD), baseDate))));


					// set where to SQL
					cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

					// creat query
					TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);
					
					result.addAll(query.getResultList());
				});

				return result.stream()
						.collect(Collectors.groupingBy(entity -> entity.getKshmtWorkingCondPK().getSid()))
						.values().stream()
						.map(item -> new WorkingCondition(new JpaWorkingConditionGetMemento(item)))
						.collect(Collectors.toList());
	}

	@Override
	public List<WorkingCondition> getBySidsAndCid(List<String> employeeIds, GeneralDate baseDate, String cid) {
		// Check exist
		if (CollectionUtil.isEmpty(employeeIds)) {
			return Collections.emptyList();
		}
				
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);
		
		List<KshmtWorkcondHist> result =  new ArrayList<>();
		
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// add where
			List<Predicate> lstpredicateWhere = new ArrayList<>();
			
			// eq company id
			lstpredicateWhere.add(criteriaBuilder.equal(root.get(KshmtWorkingCond_.cid), cid));
			lstpredicateWhere.add(root.get(KshmtWorkingCond_.kshmtWorkingCondPK)
					.get(KshmtWorkingCondPK_.sid).in(subList));
			lstpredicateWhere.add(criteriaBuilder.not(criteriaBuilder.or(
					criteriaBuilder.lessThan(root.get(KshmtWorkingCond_.endD), baseDate),
					criteriaBuilder.greaterThan(root.get(KshmtWorkingCond_.strD), baseDate))));


			// set where to SQL
			cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

			// creat query
			TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);
			
			result.addAll(query.getResultList());
		});

		return result.stream()
				.collect(Collectors.groupingBy(entity -> entity.getKshmtWorkingCondPK().getSid()))
				.values().stream()
				.map(item -> new WorkingCondition(new JpaWorkingConditionGetMemento(item)))
				.collect(Collectors.toList());
	}

	@Override
	public List<WorkingCondition> getBySidsAndCid(String cid, List<String> sIds) {
		// Check exist
		if (CollectionUtil.isEmpty(sIds)) {
			return Collections.emptyList();
		}
				
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KshmtWorkcondHist> cq = criteriaBuilder.createQuery(KshmtWorkcondHist.class);

		// root data
		Root<KshmtWorkcondHist> root = cq.from(KshmtWorkcondHist.class);

		// select root
		cq.select(root);
		
		List<KshmtWorkcondHist> result =  new ArrayList<>();
		
		CollectionUtil.split(sIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			// add where
			List<Predicate> lstpredicateWhere = new ArrayList<>();
			
			// eq company id
			lstpredicateWhere.add(criteriaBuilder.equal(root.get(KshmtWorkingCond_.cid), cid));
			lstpredicateWhere.add(root.get(KshmtWorkingCond_.kshmtWorkingCondPK)
					.get(KshmtWorkingCondPK_.sid).in(subList));

			// set where to SQL
			cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
			// Order by start date DESC
			cq.orderBy(criteriaBuilder.desc(root.get(KshmtWorkingCond_.strD)));

			// creat query
			TypedQuery<KshmtWorkcondHist> query = em.createQuery(cq);
			
			result.addAll(query.getResultList());
		});

		return result.stream()
				.collect(Collectors.groupingBy(entity -> entity.getKshmtWorkingCondPK().getSid()))
				.values().stream()
				.map(item -> new WorkingCondition(new JpaWorkingConditionGetMemento(item)))
				.collect(Collectors.toList());
	}

	@Override
	public void saveAll(List<WorkingCondition> workingConditions) {
		String cid = workingConditions.get(0).getCompanyId();
		List<KshmtWorkcondHist> insertLst = new ArrayList<>();
		List<KshmtWorkcondHist> deleteLst = new ArrayList<>();
		List<String> sids = workingConditions.stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		Map<String, List<KshmtWorkcondHist>> entityMaps = this.findBy(cid, sids);
		
		workingConditions.stream().forEach(c ->{
			List<KshmtWorkcondHist> entities = entityMaps.get(c.getEmployeeId());
			List<KshmtWorkcondHist> newWorkingCondition = new ArrayList<>(entities);
			c.saveToMemento(new JpaWorkingConditionSetMemento(newWorkingCondition));
			insertLst.addAll(newWorkingCondition.stream()
					.filter(item -> {
						int index = entities.indexOf(item);
						if (index == -1) {
							return true;
						}				
						
						KshmtWorkcondHist oldItem = entities.get(index);
						if (oldItem.getStrD().equals(item.getStrD()) && oldItem.getEndD().equals(item.getEndD())) {
							return false;
						}
						return true;
					}).collect(Collectors.toList()));

			deleteLst.addAll(entities.stream().filter(item ->  !newWorkingCondition.contains(item)).collect(Collectors.toList()));
		});
		
		if(!insertLst.isEmpty()) {
			this.commandProxy().insertAll(insertLst);
		}
		
		if(!deleteLst.isEmpty()) {
			this.commandProxy().removeAll(deleteLst);
		}
	}

	@Override
	public void insert(WorkingCondition workingCondition, WorkingConditionItem workingConditionItem) {
		this.commandProxy().insertAll(KshmtWorkcondHist.toEntity(workingCondition));
		this.commandProxy().insert(KshmtWorkcondHistItem.toEntity(workingConditionItem));
		
	}
			private static final String GET_DATA_UPDATE = " SELECT c FROM KshmtWorkcondHist c  " 
					 + " WHERE c.cid = :cid "   
			         + " AND c.kshmtWorkingCondPK.sid = :sid "
		             + " AND c.kshmtWorkingCondPK.historyId IN :lstHistID ";
	@Override
	public void update(WorkingCondition workingCondition, WorkingConditionItem workingConditionItem) {
		// TODO Auto-generated method stub
		List<String> lstHistIDNew = workingCondition.getDateHistoryItem().stream().map(x -> x.identifier()).collect(Collectors.toList());
		List<DateHistoryItem> lstDateHistoryItemAdd = new ArrayList<>();
		List<DateHistoryItem> lstDateHistoryItemUpdate = new ArrayList<>();
		List<DateHistoryItem> lstDateHistoryItemRemove= new ArrayList<>();
		
		List<KshmtWorkcondHist> lstOldEntity =  this.queryProxy().query(GET_DATA_UPDATE, KshmtWorkcondHist.class)
										.setParameter("cid", workingCondition.getCompanyId())	
										.setParameter("sid", workingCondition.getEmployeeId())
										.setParameter("lstHistID",lstHistIDNew)
										.getList();
		List<String> lstHistIDOld = lstOldEntity.stream().map( c -> c.getKshmtWorkingCondPK().getHistoryId()).collect(Collectors.toList());
		List<String> lstHistIdUpdate = new ArrayList<>();
		List<String> lstHistIdAdd = new ArrayList<>();
		List<String> lstHistIdDelete = new ArrayList<>();
		for(String hist : lstHistIDNew){
			if(lstHistIDOld.contains(hist)){
				lstHistIdUpdate.add(hist);
			}
			lstHistIdAdd.add(hist);
		}
		for(String i : lstHistIdDelete){
			DateHistoryItem item = workingCondition.getDateHistoryItem().stream()
					.filter(c -> c.identifier().equals(i)).findFirst().get();
			lstDateHistoryItemRemove.add(item);		
		}
		for (String i : lstHistIdAdd) {
			DateHistoryItem item = workingCondition.getDateHistoryItem().stream()
					.filter(c -> c.identifier().equals(i)).findFirst().get();
			lstDateHistoryItemAdd.add(item);
		}
		for(String i : lstHistIdUpdate ){
			DateHistoryItem item = workingCondition.getDateHistoryItem().stream()
					.filter(c -> c.identifier().equals(i)).findFirst().get();
			lstDateHistoryItemUpdate.add(item);
		}
		//Add
		WorkingCondition addWorkingCondition  = new WorkingCondition(workingCondition.getCompanyId(), workingCondition.getEmployeeId(), lstDateHistoryItemAdd);
		this.commandProxy().insert(KshmtWorkcondHist.toEntity(addWorkingCondition));
		//Remove
		WorkingCondition removeWorkingCondition = new WorkingCondition(workingCondition.getCompanyId(), workingCondition.getEmployeeId(), lstDateHistoryItemRemove);
		this.commandProxy().remove(KshmtWorkcondHist.toEntity(removeWorkingCondition));
		//Update
		WorkingCondition updateWorkingCondition = new WorkingCondition(workingCondition.getCompanyId(), workingCondition.getEmployeeId(), lstDateHistoryItemUpdate);
		this.commandProxy().update(updateWorkingCondition);
		// Update WorkingConditionItem
		KshmtWorkcondHistItem entity = this.queryProxy().find(workingConditionItem.getHistoryId(), KshmtWorkcondHistItem.class).get();
		entity.setSid(workingConditionItem.getEmployeeId());
		entity.setHourlyPayAtr(workingConditionItem.getHourlyPaymentAtr().value);
		entity.setScheManagementAtr(workingConditionItem.getScheduleManagementAtr().value);
		entity.setAutoStampSetAtr(workingConditionItem.getAutoStampSetAtr().value);
		entity.setAutoIntervalSetAtr(workingConditionItem.getAutoIntervalSetAtr().value);
		entity.setVacationAddTimeAtr(workingConditionItem.getVacationAddedTimeAtr().value);
		entity.setContractTime(workingConditionItem.getContractTime().v());
		entity.setLaborSys(workingConditionItem.getLaborSystem().value);
		entity.setHdAddTimeOneDay(workingConditionItem.getHolidayAddTimeSet().isPresent() ? workingConditionItem.getHolidayAddTimeSet().get().getOneDay().v() : null );
		entity.setHdAddTimeAfternoon(workingConditionItem.getHolidayAddTimeSet().isPresent() ? workingConditionItem.getHolidayAddTimeSet().get().getAfternoon().v() : null);
		entity.setHdAddTimeMorning(workingConditionItem.getHolidayAddTimeSet().isPresent() ? workingConditionItem.getHolidayAddTimeSet().get().getMorning().v() : null);
		entity.setTimeApply(workingConditionItem.getTimeApply().isPresent() ? workingConditionItem.getTimeApply().get().v() : null );
		entity.setMonthlyPattern(workingConditionItem.getMonthlyPattern().isPresent() ? workingConditionItem.getMonthlyPattern().get().v() : null);
		this.commandProxy().update(entity);
	
	}
	
	
	private final static String SELECT_WORK_COND_BY_SID_HIST = new StringBuilder("SELECT item FROM KshmtWorkcondHist item ")
			.append(" LEFT JOIN  item.KshmtWorkcondHistItem hst ")
			.append(" WHERE item.cid = :cid ")
			.append(" AND  item.kshmtWorkingCondPK.sid = :empID ")
			.append(" AND  item.kshmtWorkingCondPK.historyId = :historyId ")
			.toString();
	private final static String SELECT_ITEM_BY_SID_HIST = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN item.kshmtWorkingCond hst ")
			.append(" WHERE hst.cid = :cid ")
			.append(" AND item.sid = :sid")
			.append(" AND item.historyId = :historyId")
			.toString();
	@Override
	public void delete(String companyID, String empID, String histID) {
		//労働条件
		Optional<KshmtWorkcondHist> kshmtWorkingCond = this.queryProxy().query(SELECT_WORK_COND_BY_SID_HIST, KshmtWorkcondHist.class)
				.setParameter("cid", companyID)
				.setParameter("empID", empID)
				.setParameter("historyId", histID)
				.getSingle();
		if(kshmtWorkingCond.isPresent()) {
			this.commandProxy().remove(kshmtWorkingCond.get());
		}
		
		//労働条件項目
		Optional<KshmtWorkcondHistItem> kshmtWorkingCondItem = this.queryProxy().query(SELECT_ITEM_BY_SID_HIST, KshmtWorkcondHistItem.class)
				.setParameter("cid", companyID)
				.setParameter("sid", empID)
				.setParameter("historyId", histID)
				.getSingle();
		if(kshmtWorkingCondItem.isPresent()) {
			this.commandProxy().remove(kshmtWorkingCondItem.get());
		}
	}

	
	private final static String SELECT_ITEM_BY_SID = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN item.kshmtWorkingCond hst ")
			.append(" WHERE hst.cid = :cid ")
			.append(" AND item.sid = :sid").toString();
	@Override
	public void delete(String companyID, String empID) {
		List<KshmtWorkcondHist> lstKshmtWorkingCond = this.queryProxy().query(SELECT_WORK_COND_BY_SID, KshmtWorkcondHist.class)
				.setParameter("cid", companyID)
				.setParameter("empID", empID)
				.getList();
		this.commandProxy().removeAll(lstKshmtWorkingCond);
		
		Optional<KshmtWorkcondHistItem> kshmtWorkingCondItem = this.queryProxy().query(SELECT_ITEM_BY_SID, KshmtWorkcondHistItem.class)
			.setParameter("cid", companyID)
			.setParameter("sid", empID)
			.getSingle();
		if(kshmtWorkingCondItem.isPresent()) {
			this.commandProxy().remove(kshmtWorkingCondItem.get());
		}
	}
	
	
	private final static String SELECT_WORK_COND_BY_SID = new StringBuilder("SELECT item FROM KshmtWorkcondHist item ")
			.append(" LEFT JOIN  item.kshmtWorkingCondItem hst ")
			.append(" WHERE item.cid = :cid ")
			.append(" AND  item.kshmtWorkingCondPK.sid = :empID ")
			.toString();
	@Override
	public Optional<WorkingCondition> getWorkingCondition(String companyID, String empID) {
		List<KshmtWorkcondHist> lstEntity = this.queryProxy().query(SELECT_WORK_COND_BY_SID, KshmtWorkcondHist.class)
				.setParameter("cid", companyID)
				.setParameter("empID", empID)
				.getList();
		Optional<WorkingCondition> workSchedules = KshmtWorkcondHist.toDomainHis(lstEntity);
		return workSchedules;
	}

	
	
	@Override
	public List<WorkingCondition> getWorkingConditionByListEmpID(String companyID, List<String> lstEmpID) {
		List<WorkingCondition> data = new ArrayList<>();
		for(String empID :lstEmpID) {
			Optional<WorkingCondition> workingCondition  = getWorkingCondition(companyID, empID);
			if(workingCondition.isPresent()) {
				data.add(workingCondition.get());
			}
		}
		return data;
	}
	
	
	private final static String SELECT_BY_HISTID = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN  item.kshmtWorkingCond hst ")
			.append(" WHERE item.historyId = :histID ").toString();
	@Override
	public Optional<WorkingConditionItem> getWorkingConditionItem(String histID) {
		Optional<WorkingConditionItem> workSchedules = this.queryProxy().query(SELECT_BY_HISTID, KshmtWorkcondHistItem.class)
				.setParameter("histID", histID)
				.getSingle(c -> c.toDomain());
		return workSchedules;
	}
	
	
	private final static String SELECT_BY_LIST_HISTID = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN  item.kshmtWorkingCond hst ")
			.append(" WHERE item.historyId IN :listHistID ").toString();
	@Override
	public List<WorkingConditionItem> getWorkingConditionItemByListHistID(List<String> listHistID) {
		if(listHistID.isEmpty()) {
			return new ArrayList<>();
		}
		List<WorkingConditionItem> workSchedules = this.queryProxy().query(SELECT_BY_LIST_HISTID, KshmtWorkcondHistItem.class)
				.setParameter("listHistID", listHistID)
				.getList(c -> c.toDomain());
				return workSchedules;
	}
	
	
	private final static String SELECT_BY_ID_DATE_NOT_SID = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN  item.kshmtWorkingCond hst ")
			.append(" WHERE hst.cid = :cid ")
			.append(" AND hst.strD <= :ymds ")
			.append(" AND hst.endD >= :ymds ").toString();
	@Override
	public List<WorkingConditionItem> getWorkingConditionItemByCompanyIDAndDate(String companyID, GeneralDate ymd) {
		List<WorkingConditionItem> workSchedules = this.queryProxy().query(SELECT_BY_ID_DATE_NOT_SID, KshmtWorkcondHistItem.class)
		.setParameter("cid", companyID)
		.setParameter("ymds", ymd)
		.getList(c -> c.toDomain());
		return workSchedules;
	}
	
	
	private final static String SELECT_BY_ID_DATE = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN item.kshmtWorkingCond hst ")
			.append(" WHERE hst.cid = :cid ")
			.append(" AND hst.strD <= :ymds ")
			.append(" AND hst.endD >= :ymds ")
			.append(" AND item.sid = :sid").toString();
	// [6-1] 社員を指定して年月日時点の履歴項目を取得する ( 会社ID, 年月日, 社員ID )
	@Override
	public Optional<WorkingConditionItem> getWorkingConditionItemByEmpIDAndDate(String companyID, GeneralDate ymd,
			String employeeID) {
		Optional<WorkingConditionItem> workSchedule = this.queryProxy().query(SELECT_BY_ID_DATE, KshmtWorkcondHistItem.class)
				.setParameter("cid", companyID)
				.setParameter("sid", employeeID)
				.setParameter("ymds", ymd)
				.getSingle(c -> c.toDomain());
		return workSchedule;
	}
	
	
	private final static String SELECT_BY_LIST_ID_AND_DATE = new StringBuilder("SELECT item FROM KshmtWorkcondHistItem item ")
			.append(" LEFT JOIN item.kshmtWorkingCond hst ")
			.append(" WHERE hst.cid = :cid ")
			.append(" AND hst.strD <= :ymds ")
			.append(" AND hst.endD >= :ymds ")
			.append(" AND item.sid IN :empID").toString();
	
	@Override
	public List<WorkingConditionItem> getWorkingConditionItemByLstEmpIDAndDate(String companyID, GeneralDate ymd,
			List<String> empIDs) {
		List<WorkingConditionItem> workSchedules = new ArrayList<WorkingConditionItem>();
		CollectionUtil.split(empIDs, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empID -> {
			workSchedules.addAll(this.queryProxy()
				.query(SELECT_BY_LIST_ID_AND_DATE, KshmtWorkcondHistItem.class)
				.setParameter("cid", companyID)
				.setParameter("ymds", ymd)
				.setParameter("empID", empID)
				.getList(c -> c.toDomain()));
		});
		return workSchedules;
	}

	
	@Override
	public List<WorkingConditionItemWithPeriod> getWorkingConditionItemWithPeriod(String companyID,
			List<String> lstEmpID, DatePeriod datePeriod) {
		// $履歴リスト = [3-2] *社員IDを指定して履歴を取得する ( 会社ID, 社員IDリスト )
		List<WorkingCondition> lstWorkingCondition = getWorkingConditionByListEmpID(companyID, lstEmpID);
		// $汎用履歴項目リスト = $履歴リスト:
		List<DateHistoryItem> lstDateHistoryItem = new ArrayList<>();
		// flatMap $.履歴項目
		lstDateHistoryItem = lstWorkingCondition.stream().flatMap(c -> c.getDateHistoryItem().stream())
				.collect(Collectors.toList());
		// Filter
		lstDateHistoryItem = lstDateHistoryItem.stream()
				.filter(x -> (datePeriod.contains(x.start()) || datePeriod.contains(x.end())
						|| x.contains(datePeriod.start()) || x.contains(datePeriod.end())))
				.collect(Collectors.toList());
		// 	$履歴IDリスト = $汎用履歴項目リスト: map $.履歴ID																		
		List<String> lstHisId = lstDateHistoryItem.stream().map(mapper -> mapper.identifier())
				.collect(Collectors.toList());
		//	$履歴項目リスト = [4-2] *履歴IDを指定して履歴項目を取得する ( $履歴IDリスト )	
		List<WorkingConditionItem> lstWorkConditionItem = getWorkingConditionItemByListHistID(lstHisId);
		//return $汎用履歴項目 in $汎用履歴項目リスト:													
		List<WorkingConditionItemWithPeriod> result = new ArrayList<>();
		 for (DateHistoryItem item : lstDateHistoryItem){
			// $履歴項目 
			 Optional<WorkingConditionItem> optWorkingConditionItem = lstWorkConditionItem.stream()
					 											.filter(predicate -> predicate.getHistoryId().equals(item.identifier())).findFirst();
			// 	filter $.isPresent()							
			 WorkingConditionItemWithPeriod workingConditionItemWithPeriod = new WorkingConditionItemWithPeriod(new DatePeriod(item.start(), item.end()),
					 optWorkingConditionItem.isPresent() ? optWorkingConditionItem.get() : null);
			         result.add(workingConditionItemWithPeriod);	
		 }
		 result = result.stream().filter(x -> x.getWorkingConditionItem() != null).collect(Collectors.toList());
		return result;
	}

}

