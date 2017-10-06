/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.repository.classification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.classification.Classification;
import nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository;
import nts.uk.ctx.bs.employee.infra.entity.classification.BsymtClassification;
import nts.uk.ctx.bs.employee.infra.entity.classification.BsymtClassificationPK;
import nts.uk.ctx.bs.employee.infra.entity.classification.BsymtClassificationPK_;
import nts.uk.ctx.bs.employee.infra.entity.classification.BsymtClassification_;
import nts.uk.ctx.bs.employee.infra.entity.classification.CclmtClassification;
import nts.uk.ctx.bs.employee.infra.entity.classification.CclmtClassificationPK;

/**
 * The Class JpaManagementCategoryRepository.
 */
@Stateless
public class JpaClassificationRepository extends JpaRepository
	implements ClassificationRepository {

	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository#findClassification(java.lang.String, java.lang.String)
	 */
	@Override
	public Optional<Classification> findClassification(String companyId, String classificationCode) {
		return this.queryProxy()
				.find(new BsymtClassificationPK(companyId, classificationCode), BsymtClassification.class)
				.map(e -> this.toDomain(e));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.basic.dom.company.organization.category.
	 * ManagementCategoryRepository#add(nts.uk.ctx.basic.dom.company.
	 * organization.category.ManagementCategory)
	 */
	@Override
	public void add(Classification managementCategory) {
		this.commandProxy().insert(this.toEntity(managementCategory));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.basic.dom.company.organization.category.
	 * ManagementCategoryRepository#update(nts.uk.ctx.basic.dom.company.
	 * organization.category.ManagementCategory)
	 */
	@Override
	public void update(Classification managementCategory) {
		this.commandProxy().update(this.toEntity(managementCategory));
	}
	
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository#remove(java.lang.String, java.lang.String)
	 */
	@Override
	public void remove(String companyId, String classificationCode) {
		this.commandProxy().remove(
				CclmtClassification.class, 
				new CclmtClassificationPK(companyId, classificationCode));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.basic.dom.company.organization.category.
	 * ManagementCategoryRepository#getAllManagementCategory(java.lang.String)
	 */
	@Override
	public List<Classification> getAllManagementCategory(String companyId) {
		
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<BsymtClassification> cq = criteriaBuilder
			.createQuery(BsymtClassification.class);

		// root data
		Root<BsymtClassification> root = cq.from(BsymtClassification.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();

		// eq company id
		lstpredicateWhere
			.add(criteriaBuilder.equal(root.get(BsymtClassification_.bsymtClassificationPK)
				.get(BsymtClassificationPK_.cid), companyId));
		// set where to SQL
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));

		// creat query
		TypedQuery<BsymtClassification> query = em.createQuery(cq);

		// exclude select
		return query.getResultList().stream().map(category -> toDomain(category))
			.collect(Collectors.toList());
	}
	
	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the cclmt management category
	 */
	private BsymtClassification toEntity(Classification domain){
		BsymtClassification entity = new BsymtClassification();
		domain.saveToMemento(new JpaClassificationSetMemento(entity));
		return entity;
	}
	
	/**
	 * To domain.
	 *
	 * @param entity the entity
	 * @return the management category
	 */
	private Classification toDomain(BsymtClassification entity){
		return new Classification(new JpaClassificationGetMemento(entity));
	}
}
