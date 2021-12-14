/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.infra.repository.temporaryabsence.frame;

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

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceFrame;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.TempAbsenceRepositoryFrame;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.frame.BsystTempAbsenceFrame;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.frame.BsystTempAbsenceFramePK;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.frame.BsystTempAbsenceFramePK_;
import nts.uk.ctx.bs.employee.infra.entity.temporaryabsence.frame.BsystTempAbsenceFrame_;

/**
 * The Class JpaTempAbsenceFrameRespository.
 */
@Stateless
public class JpaTempAbsenceFrameRespository extends JpaRepository
		implements TempAbsenceRepositoryFrame {

	private static final String GET_BY_CID_FOR_REQ546 = " SELECT e FROM BsystTempAbsenceFrame e "
			+ " WHERE e.bsystTempAbsenceFramePK.cid = :cid "
			+ " AND e.useAtr = 1 "
			+ " AND e.bsystTempAbsenceFramePK.tempAbsenceFrNo >= 2"
			+ " AND e.bsystTempAbsenceFramePK.tempAbsenceFrNo <= 9";
	
	private static final String FIND_BY_CID_AND_FRAME_NOS = "SELECT t FROM BsystTempAbsenceFrame t "
			+ "WHERE t.bsystTempAbsenceFramePK.cid = :cid "
			+ "AND t.bsystTempAbsenceFramePK.tempAbsenceFrNo IN :frameNos";

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.
	 * TempAbsenceRepositoryFrame#udpate(nts.uk.ctx.bs.employee.dom.
	 * temporaryabsence.frame.TempAbsenceFrame)
	 */
	@Override
	public void udpate(TempAbsenceFrame tempAbsenceFrame) {
		this.commandProxy().update(this.toEntity(tempAbsenceFrame));
	}

	/**
	 * To entity.
	 *
	 * @param tempAbsenceFrame
	 *            the temp absence frame
	 * @return the bsyst temp absence frame
	 */
	private BsystTempAbsenceFrame toEntity(TempAbsenceFrame tempAbsenceFrame) {
		Optional<BsystTempAbsenceFrame> optional = this.queryProxy()
				.find(new BsystTempAbsenceFramePK(tempAbsenceFrame.getCompanyId(),
						tempAbsenceFrame.getTempAbsenceFrNo().v().intValue()),
						BsystTempAbsenceFrame.class);
		BsystTempAbsenceFrame entity = new BsystTempAbsenceFrame();
		if (optional.isPresent()) {
			entity = optional.get();
		}
		JpaTempAbsenceFrameSetMemento memento = new JpaTempAbsenceFrameSetMemento(entity);
		tempAbsenceFrame.saveToMemento(memento);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.
	 * TempAbsenceRepositoryFrame#findByTAFPk(java.lang.String, short)
	 */
	@Override
	public TempAbsenceFrame findByTempAbsenceFramePk(String cId, int tempAbsenceFrameNo) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<BsystTempAbsenceFrame> cq = criteriaBuilder
				.createQuery(BsystTempAbsenceFrame.class);
		Root<BsystTempAbsenceFrame> root = cq.from(BsystTempAbsenceFrame.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstPredicateWhere = new ArrayList<>();
		lstPredicateWhere
			.add(criteriaBuilder.equal(root.get(BsystTempAbsenceFrame_.bsystTempAbsenceFramePK)
				.get(BsystTempAbsenceFramePK_.cid), cId));
		
		lstPredicateWhere
			.add(criteriaBuilder.equal(root.get(BsystTempAbsenceFrame_.bsystTempAbsenceFramePK)
				.get(BsystTempAbsenceFramePK_.tempAbsenceFrNo), tempAbsenceFrameNo));

		cq.where(lstPredicateWhere.toArray(new Predicate[] {}));

		return new TempAbsenceFrame(
				new JpaTempAbsenceFrameGetMemento(em.createQuery(cq).getSingleResult()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.
	 * TempAbsenceRepositoryFrame#findByCid(java.lang.String)
	 */
	@Override
	public List<TempAbsenceFrame> findByCid(String cId) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<BsystTempAbsenceFrame> cq = criteriaBuilder
				.createQuery(BsystTempAbsenceFrame.class);
		Root<BsystTempAbsenceFrame> root = cq.from(BsystTempAbsenceFrame.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstPredicateWhere = new ArrayList<>();
		lstPredicateWhere
				.add(criteriaBuilder.equal(root.get(BsystTempAbsenceFrame_.bsystTempAbsenceFramePK)
						.get(BsystTempAbsenceFramePK_.cid), cId));

		cq.where(lstPredicateWhere.toArray(new Predicate[] {}));

		return em.createQuery(cq).getResultList().stream()
				.map(item -> new TempAbsenceFrame(new JpaTempAbsenceFrameGetMemento(item)))
				.collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.dom.temporaryabsence.frame.
	 * TempAbsenceRepositoryFrame#findWithUseState(java.lang.String,
	 * java.lang.Integer)
	 */
	@Override
	public List<TempAbsenceFrame> findWithUseState(String cId, Integer useAtr) {
		// get entity manager
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<BsystTempAbsenceFrame> cq = criteriaBuilder
				.createQuery(BsystTempAbsenceFrame.class);
		Root<BsystTempAbsenceFrame> root = cq.from(BsystTempAbsenceFrame.class);

		// select root
		cq.select(root);

		// add where
		List<Predicate> lstPredicateWhere = new ArrayList<>();

		lstPredicateWhere
				.add(criteriaBuilder.equal(root.get(BsystTempAbsenceFrame_.bsystTempAbsenceFramePK)
						.get(BsystTempAbsenceFramePK_.cid), cId));
		lstPredicateWhere
				.add(criteriaBuilder.equal(root.get(BsystTempAbsenceFrame_.useAtr), useAtr));

		cq.where(lstPredicateWhere.toArray(new Predicate[] {}));
		
		cq.orderBy(criteriaBuilder.asc(root.get(BsystTempAbsenceFrame_.bsystTempAbsenceFramePK)
				.get(BsystTempAbsenceFramePK_.tempAbsenceFrNo)));

		return em.createQuery(cq).getResultList().stream()
				.map(item -> new TempAbsenceFrame(new JpaTempAbsenceFrameGetMemento(item)))
				.collect(Collectors.toList());
	}

	@Override
	public List<TempAbsenceFrame> findByCidForReq546(String cid) {
		List<BsystTempAbsenceFrame> listEntity = this.queryProxy().query(GET_BY_CID_FOR_REQ546, BsystTempAbsenceFrame.class)
				.setParameter("cid", cid).getList();
		if (listEntity.isEmpty()) {
			return new ArrayList<>();
		}
		return toListTempAbsenceFrame(listEntity);
	}
	
	@Override
	public List<TempAbsenceFrame> findByCidAndFrameNos(String cid, List<Integer> tempAbsenceFrameNos) {
		if (tempAbsenceFrameNos.isEmpty()) {
			return Collections.emptyList();
		}
		return this.queryProxy().query(FIND_BY_CID_AND_FRAME_NOS, BsystTempAbsenceFrame.class)
				.setParameter("cid", cid).setParameter("frameNos", tempAbsenceFrameNos)
				.getList(this::toDomain);
	}

	private List<TempAbsenceFrame> toListTempAbsenceFrame(List<BsystTempAbsenceFrame> listEntity) {
		List<TempAbsenceFrame> lstTempAbsenceFrame = new ArrayList<>();
		if (!listEntity.isEmpty()) {
			listEntity.stream().forEach(c -> {
				TempAbsenceFrame tempAbsenceFrame = toDomain(c);
				lstTempAbsenceFrame.add(tempAbsenceFrame);
			});
		}
		return lstTempAbsenceFrame;
	}
	
	private TempAbsenceFrame toDomain(BsystTempAbsenceFrame entity) {
		return new TempAbsenceFrame(entity.getBsystTempAbsenceFramePK().getCid(),
				entity.getBsystTempAbsenceFramePK().getTempAbsenceFrNo(), Integer.valueOf(entity.getUseAtr()),
				entity.getTempAbsenceFrName());
	}

}
