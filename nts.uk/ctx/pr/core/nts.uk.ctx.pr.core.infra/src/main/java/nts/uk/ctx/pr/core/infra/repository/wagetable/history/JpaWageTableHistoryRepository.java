/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.pr.core.infra.repository.wagetable.history;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.ListUtil;
import nts.uk.ctx.pr.core.dom.wagetable.history.WageTableHistory;
import nts.uk.ctx.pr.core.dom.wagetable.history.WageTableHistoryRepository;
import nts.uk.ctx.pr.core.infra.entity.wagetable.history.QwtmtWagetableHist;
import nts.uk.ctx.pr.core.infra.entity.wagetable.history.QwtmtWagetableHistPK_;
import nts.uk.ctx.pr.core.infra.entity.wagetable.history.QwtmtWagetableHist_;

/**
 * The Class JpaWageTableHistoryRepository.
 */
@Stateless
public class JpaWageTableHistoryRepository extends JpaRepository
		implements WageTableHistoryRepository {

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.wagetable.history.WageTableHistoryRepository#
	 * findAll(nts.uk.ctx.core.dom.company.CompanyCode)
	 */
	@Override
	public List<WageTableHistory> findAll(String companyCode) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Query for.
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QwtmtWagetableHist> cq = cb.createQuery(QwtmtWagetableHist.class);
		Root<QwtmtWagetableHist> root = cq.from(QwtmtWagetableHist.class);

		// Constructing list of parameters
		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Construct condition.
		predicateList.add(cb.equal(
				root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK).get(QwtmtWagetableHistPK_.ccd),
				companyCode));

		cq.where(predicateList.toArray(new Predicate[] {}));

		return em.createQuery(cq).getResultList().stream()
				.map(item -> new WageTableHistory(new JpaWageTableHistoryGetMemento(item)))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryRepository#
	 * deleteHistory(java.lang.String)
	 */
	@Override
	public void deleteHistory(String uuid) {
		// Get entity manager.
		EntityManager em = this.getEntityManager();

		CriteriaBuilder cb = em.getCriteriaBuilder();

		// create delete
		CriteriaDelete<QwtmtWagetableHist> delete = cb
				.createCriteriaDelete(QwtmtWagetableHist.class);

		// set the root class
		Root<QwtmtWagetableHist> root = delete.from(QwtmtWagetableHist.class);

		// set where clause
		delete.where(cb.equal(root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK)
				.get(QwtmtWagetableHistPK_.histId), uuid));

		// perform update
		em.createQuery(delete).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryRepository#
	 * findLastestHistoryByMasterCode(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<WageTableHistory> findLastestHistoryByMasterCode(String companyCode,
			String masterCode) {
		return this.findByIndex(0, companyCode, masterCode);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryRepository#
	 * findHistoryByUuid(java.lang.String)
	 */
	@Override
	public Optional<WageTableHistory> findHistoryByUuid(String uuid) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Query for.
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QwtmtWagetableHist> cq = cb.createQuery(QwtmtWagetableHist.class);
		Root<QwtmtWagetableHist> root = cq.from(QwtmtWagetableHist.class);

		// Constructing list of parameters
		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Construct condition.
		predicateList.add(cb.equal(root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK)
				.get(QwtmtWagetableHistPK_.histId), uuid));

		cq.where(predicateList.toArray(new Predicate[] {}));

		List<QwtmtWagetableHist> result = em.createQuery(cq).getResultList();

		// Check empty.
		if (ListUtil.isEmpty(result)) {
			return Optional.empty();
		}

		// Return
		return Optional.of(new WageTableHistory(new JpaWageTableHistoryGetMemento(result.get(0))));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryRepository#
	 * addHistory(nts.uk.ctx.pr.core.dom.base.simplehistory.History)
	 */
	@Override
	public void addHistory(WageTableHistory history) {
		QwtmtWagetableHist entity = new QwtmtWagetableHist();
		history.saveToMemento(new JpaWageTableHistorySetMemento(entity));
		this.commandProxy().update(entity);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.base.simplehistory.SimpleHistoryRepository#
	 * updateHistory(nts.uk.ctx.pr.core.dom.base.simplehistory.History)
	 */
	@Override
	public void updateHistory(WageTableHistory unitPriceHistory) {
		QwtmtWagetableHist entity = new QwtmtWagetableHist();
		unitPriceHistory.saveToMemento(new JpaWageTableHistorySetMemento(entity));
		this.commandProxy().update(entity);
	}

	/**
	 * Find by index.
	 *
	 * @param index
	 *            the index
	 * @param companyCode
	 *            the company code
	 * @param wageTableCode
	 *            the wage table code
	 * @return the optional
	 */
	private Optional<WageTableHistory> findByIndex(int index, String companyCode,
			String wageTableCode) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Query for indicated stress check.
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QwtmtWagetableHist> cq = cb.createQuery(QwtmtWagetableHist.class);
		Root<QwtmtWagetableHist> root = cq.from(QwtmtWagetableHist.class);
		// Constructing list of parameters
		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Construct condition.
		predicateList.add(cb.equal(
				root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK).get(QwtmtWagetableHistPK_.ccd),
				companyCode));
		predicateList.add(cb.equal(root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK)
				.get(QwtmtWagetableHistPK_.wageTableCd), wageTableCode));

		cq.orderBy(cb.desc(root.get(QwtmtWagetableHist_.strYm)));
		cq.where(predicateList.toArray(new Predicate[] {}));

		List<QwtmtWagetableHist> result = em.createQuery(cq).getResultList();

		// Check empty.
		if (ListUtil.isEmpty(result)) {
			return Optional.empty();
		}

		// Return
		return Optional
				.of(new WageTableHistory(new JpaWageTableHistoryGetMemento(result.get(index))));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.pr.core.dom.wagetable.history.WageTableHistoryRepository#
	 * isValidDateRange(nts.uk.ctx.core.dom.company.CompanyCode,
	 * nts.uk.ctx.pr.core.dom.rule.employment.unitprice.UnitPriceCode,
	 * nts.arc.time.YearMonth)
	 */
	@Override
	public boolean isValidDateRange(String companyCode, String wageTableCode, Integer startMonth) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create query condition.
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<QwtmtWagetableHist> root = cq.from(QwtmtWagetableHist.class);
		// Constructing list of parameters
		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Construct condition.
		predicateList.add(cb.equal(
				root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK).get(QwtmtWagetableHistPK_.ccd),
				companyCode));
		predicateList.add(cb.equal(root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK)
				.get(QwtmtWagetableHistPK_.wageTableCd), wageTableCode));
		predicateList.add(cb.ge(root.get(QwtmtWagetableHist_.strYm), startMonth));

		cq.select(cb.count(root));
		cq.where(predicateList.toArray(new Predicate[] {}));

		return !(em.createQuery(cq).getSingleResult().longValue() > 0L);
	}

	@Override
	public List<WageTableHistory> findAllHistoryByMasterCode(String companyCode,
			String masterCode) {
		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Query for indicated stress check.
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QwtmtWagetableHist> cq = cb.createQuery(QwtmtWagetableHist.class);
		Root<QwtmtWagetableHist> root = cq.from(QwtmtWagetableHist.class);
		// Constructing list of parameters
		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Construct condition.
		predicateList.add(cb.equal(
				root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK).get(QwtmtWagetableHistPK_.ccd),
				companyCode));
		predicateList.add(cb.equal(root.get(QwtmtWagetableHist_.qwtmtWagetableHistPK)
				.get(QwtmtWagetableHistPK_.wageTableCd), masterCode));

		cq.orderBy(cb.desc(root.get(QwtmtWagetableHist_.strYm)));
		cq.where(predicateList.toArray(new Predicate[] {}));

		return em.createQuery(cq).getResultList().stream()
				.map(item -> new WageTableHistory(new JpaWageTableHistoryGetMemento(item)))
				.collect(Collectors.toList());
	}

}
