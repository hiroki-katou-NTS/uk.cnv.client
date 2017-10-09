/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.repository.executionlog;

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
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreator;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorRepository;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtScheduleCreator;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtScheduleCreatorPK;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtScheduleCreatorPK_;
import nts.uk.ctx.at.schedule.infra.entity.executionlog.KscmtScheduleCreator_;

/**
 * The Class JpaScheduleCreatorRepository.
 */
@Stateless
public class JpaScheduleCreatorRepository extends JpaRepository
		implements ScheduleCreatorRepository {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorRepository#findAll
	 * (java.lang.String)
	 */
	@Override
	public List<ScheduleCreator> findAll(String executionId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<KscmtScheduleCreator> cq = criteriaBuilder
				.createQuery(KscmtScheduleCreator.class);
		Root<KscmtScheduleCreator> root = cq.from(KscmtScheduleCreator.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstpredicateWhere = new ArrayList<>();
		lstpredicateWhere
				.add(criteriaBuilder.equal(root.get(KscmtScheduleCreator_.kscmtScheduleCreatorPK)
						.get(KscmtScheduleCreatorPK_.exeId), executionId));
		
		cq.where(lstpredicateWhere.toArray(new Predicate[] {}));
		
		cq.orderBy(criteriaBuilder.desc(root.get(KscmtScheduleCreator_.kscmtScheduleCreatorPK)
				.get(KscmtScheduleCreatorPK_.sid)));
		
		// create query
		TypedQuery<KscmtScheduleCreator> query = em.createQuery(cq);
		
		// exclude select
		return query.getResultList().stream().map(entity -> this.toDomain(entity))
				.collect(Collectors.toList());
	}

	/**
	 * Find by id.
	 *
	 * @param executionId the execution id
	 * @param employeeId the employee id
	 * @return the optional
	 */
	public Optional<KscmtScheduleCreator> findById(String executionId, String employeeId) {
		return this.queryProxy().find(new KscmtScheduleCreatorPK(executionId, employeeId),
				KscmtScheduleCreator.class);
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorRepository#save(
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreator)
	 */
	@Override
	public void add(ScheduleCreator domain) {
		this.commandProxy().insert(this.toEntity(domain));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorRepository#save(
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreator)
	 */
	@Override
	public void update(ScheduleCreator domain) {
		this.commandProxy().update(this.toEntityUpdate(domain));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorRepository#saveAll
	 * (java.util.List)
	 */
	@Override
	public void saveAll(List<ScheduleCreator> domains) {
		this.commandProxy().insertAll(
				domains.stream().map(domain -> this.toEntity(domain)).collect(Collectors.toList()));
	}
	
	/**
	 * To entity.
	 *
	 * @param domain the domain
	 * @return the kscmt schedule creator
	 */
	private KscmtScheduleCreator toEntity(ScheduleCreator domain){
		KscmtScheduleCreator entity = new KscmtScheduleCreator();
		domain.saveToMemento(new JpaScheduleCreatorSetMemento(entity));
		return entity;
	}
	
	/**
	 * To entity update.
	 *
	 * @param domain the domain
	 * @return the kscmt schedule creator
	 */
	private KscmtScheduleCreator toEntityUpdate(ScheduleCreator domain){
		Optional<KscmtScheduleCreator> opEntity = this.findById(domain.getExecutionId(), domain.getEmployeeId());
		KscmtScheduleCreator entity = new KscmtScheduleCreator();
		
		if(opEntity.isPresent()){
			entity = opEntity.get();
		}
		
		domain.saveToMemento(new JpaScheduleCreatorSetMemento(entity));
		return entity;
	}
	
	/**
	 * To domain.
	 *
	 * @param entity the entity
	 * @return the schedule creator
	 */
	private ScheduleCreator toDomain(KscmtScheduleCreator entity){
		return new ScheduleCreator(new JpaScheduleCreatorGetMemento(entity));
	}

}
