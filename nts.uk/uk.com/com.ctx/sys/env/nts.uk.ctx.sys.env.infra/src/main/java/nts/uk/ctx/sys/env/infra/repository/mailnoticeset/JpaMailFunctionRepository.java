/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.env.infra.repository.mailnoticeset;

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
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunction;
import nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionRepository;
import nts.uk.ctx.sys.env.infra.entity.mailnoticeset.SevmtMailFunction;
import nts.uk.ctx.sys.env.infra.entity.mailnoticeset.SevmtMailFunction_;

/**
 * The Class JpaMailFunctionRepository.
 */
@Stateless
public class JpaMailFunctionRepository extends JpaRepository implements MailFunctionRepository {

	private static final Integer TRUE_VAL = 1;
	private static final Integer FALSE_VAL = 0;
	
	private static final String FIND_ALL_MAIL_FUNCTION = "select m from SevmtMailFunction m ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.sys.env.dom.mailnoticeset.MailFunctionRepository#findAll(java.
	 * lang.Boolean)
	 */
	@Override
	public List<MailFunction> findAll(Boolean proprietySendMailSettingAtr) {
		List<MailFunction> lstMailFunction = new ArrayList<>();
		// Get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<SevmtMailFunction> cq = criteriaBuilder.createQuery(SevmtMailFunction.class);
		Root<SevmtMailFunction> root = cq.from(SevmtMailFunction.class);

		// Build query
		cq.select(root);

		// Add where conditions
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere.add(criteriaBuilder.equal(root.get(SevmtMailFunction_.sendMailSetAtr),
				proprietySendMailSettingAtr ? TRUE_VAL : FALSE_VAL));
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		cq.orderBy(criteriaBuilder.asc(root.get(SevmtMailFunction_.sortOrder)));

		List<SevmtMailFunction> listSevstMailFunction = em.createQuery(cq).getResultList();

		// Check exist
		if (!CollectionUtil.isEmpty(listSevstMailFunction)) {
			lstMailFunction = listSevstMailFunction.stream().map(this::toDomain).collect(Collectors.toList());
		}
		// Return
		return lstMailFunction;
	}

	@Override
	public List<MailFunction> findAll() {
		return this.queryProxy().query(FIND_ALL_MAIL_FUNCTION, SevmtMailFunction.class)
				.getList().stream()
				.filter(m -> m.getSendMailSetAtr() == 1)
				.map(item -> new MailFunction(item))
				.collect(Collectors.toList());
	}

	private MailFunction toDomain(SevmtMailFunction entity) {
		return new MailFunction(new JpaMailFunctionGetMemento(entity));
	}

}
