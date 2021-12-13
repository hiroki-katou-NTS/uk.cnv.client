/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.repository.employment;

import java.util.ArrayList;
import java.util.List;
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

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.infra.entity.employment.BsymtEmployment;
import nts.uk.ctx.bs.employee.infra.entity.employment.BsymtEmploymentPK;
import nts.uk.ctx.bs.employee.infra.entity.employment.BsymtEmploymentPK_;
import nts.uk.ctx.bs.employee.infra.entity.employment.BsymtEmployment_;

/**
 * The Class JpaEmploymentRepository.
 */
@Stateless
public class JpaEmploymentRepository extends JpaRepository implements EmploymentRepository {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.basic.dom.company.organization.employment.EmploymentRepository
	 * #findAll(java.lang.String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<Employment> findAll(String companyId) {
		// Get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BsymtEmployment> cq = cb.createQuery(BsymtEmployment.class);

		// Root
		Root<BsymtEmployment> root = cq.from(BsymtEmployment.class);
		cq.select(root);

		// Predicate where clause
		List<Predicate> predicateList = new ArrayList<>();
		predicateList.add(
				cb.equal(root.get(BsymtEmployment_.bsymtEmploymentPK).get(BsymtEmploymentPK_.cid),
						companyId));

		// Set Where clause to SQL Query
		cq.where(predicateList.toArray(new Predicate[] {}));
		
		// order by no id asc
		cq.orderBy(cb.asc(root.get(BsymtEmployment_.bsymtEmploymentPK).get(BsymtEmploymentPK_.code)));

		// Create Query
		TypedQuery<BsymtEmployment> query = em.createQuery(cq);

		return query.getResultList().stream().map(item -> this.toDomain(item))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.basic.dom.company.organization.employment.EmploymentRepository
	 * #findEmployment(java.lang.String, java.lang.String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<Employment> findEmployment(String companyId, String employmentCode) {
		return this.queryProxy()
				.find(new BsymtEmploymentPK(companyId, employmentCode), BsymtEmployment.class)
				.map(e -> this.toDomain(e));
	}

	/**
	 * To domain.
	 *
	 * @param entity
	 *            the entity
	 * @return the employment
	 */
	private Employment toDomain(BsymtEmployment entity) {
		return new Employment(new JpaEmploymentGetMemento(entity));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.basic.dom.company.organization.employment.EmploymentRepository
	 * #findByEmpCodes(java.util.List)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<Employment> findByEmpCodes(String companyId, List<String> empCodes) {
    	if(empCodes.isEmpty())
    		return new ArrayList<>();
		// Get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BsymtEmployment> cq = cb.createQuery(BsymtEmployment.class);

		// Root
		Root<BsymtEmployment> root = cq.from(BsymtEmployment.class);
		cq.select(root);

		// Predicate where clause
		List<Predicate> predicateList = new ArrayList<>();
		predicateList.add(
				cb.equal(root.get(BsymtEmployment_.bsymtEmploymentPK).get(BsymtEmploymentPK_.cid),
						companyId));
		predicateList.add(root.get(BsymtEmployment_.bsymtEmploymentPK).get(BsymtEmploymentPK_.code)
				.in(empCodes));

		// Set Where clause to SQL Query
		cq.where(predicateList.toArray(new Predicate[] {}));

		// Create Query
		TypedQuery<BsymtEmployment> query = em.createQuery(cq);

		return query.getResultList().stream().map(item -> this.toDomain(item))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository#insert(nts.uk.
	 * ctx.bs.employee.dom.employment.Employment)
	 */
	@Override
	public void insert(Employment employment) {
		BsymtEmployment entity = new BsymtEmployment();
		employment.saveToMemento(new JpaEmploymentSetMemento(entity));
		this.commandProxy().insert(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository#update(nts.uk.
	 * ctx.bs.employee.dom.employment.Employment)
	 */
	@Override
	public void update(Employment employment) {
		Optional<BsymtEmployment> optional = this.queryProxy()
				.find(new BsymtEmploymentPK(employment.getCompanyId().v(),
						employment.getEmploymentCode().v()), BsymtEmployment.class);
		BsymtEmployment entity = null;
		if (optional.isPresent()) {
			entity = optional.get();
		} else {
			entity = new BsymtEmployment();
		}
		employment.saveToMemento(new JpaEmploymentSetMemento(entity));
		this.commandProxy().update(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository#remove(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public void remove(String companyId, String employmentCode) {
		this.commandProxy().remove(BsymtEmployment.class,
				new BsymtEmploymentPK(companyId, employmentCode));
	}
}
