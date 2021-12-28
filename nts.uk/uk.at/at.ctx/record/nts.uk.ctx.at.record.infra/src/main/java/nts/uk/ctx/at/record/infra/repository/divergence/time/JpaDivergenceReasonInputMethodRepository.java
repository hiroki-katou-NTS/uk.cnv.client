package nts.uk.ctx.at.record.infra.repository.divergence.time;

import java.util.ArrayList;
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
import nts.uk.ctx.at.record.dom.divergence.time.DivergenceReasonInputMethod;
import nts.uk.ctx.at.record.dom.divergence.time.DivergenceReasonInputMethodGetMemento;
import nts.uk.ctx.at.record.dom.divergence.time.DivergenceReasonInputMethodRepository;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcmtDvgcTime;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTimePK;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTimePK_;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTime_;

/**
 * The Class JpaDivergenceReasonInputMethodRepository.
 */
@Stateless
public class JpaDivergenceReasonInputMethodRepository extends JpaRepository
		implements DivergenceReasonInputMethodRepository {
	
	private final static String FIND_DVGC_TIME;
	private final static String FIND_DVGC_TIME_V2;
	
	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT d FROM KrcmtDvgcTime d ");
		builderString.append("WHERE d.id.cid = :cid ");
		builderString.append("AND d.id.no IN :no ");
		builderString.append("AND d.dvgcReasonSelected = :dvgcReasonSelected");
		FIND_DVGC_TIME = builderString.toString();
	}

	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT d FROM KrcmtDvgcTime d ");
		builderString.append("WHERE d.id.cid = :cid ");
		builderString.append("AND d.id.no IN :no ");
		FIND_DVGC_TIME_V2 = builderString.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.
	 * DivergenceReasonInputMethodRepository#getAllDivTime(java.lang.String)
	 */
	@Override
	public List<DivergenceReasonInputMethod> getAllDivTime(String companyId) {

		return this.findByCompanyId(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.
	 * DivergenceReasonInputMethodRepository#getDivTimeInfo(java.lang.String,
	 * int)
	 */
	@Override
	public Optional<DivergenceReasonInputMethod> getDivTimeInfo(String companyId, int divTimeNo) {

		KrcstDvgcTimePK PK = new KrcstDvgcTimePK(companyId, divTimeNo);
		return this.queryProxy().find(PK, KrcmtDvgcTime.class).map(e->this.toDomain(e));
	}

	/**
	 * To domain.
	 *
	 * @param entities
	 *            the entities
	 * @return the company divergence reference time history
	 */
	private DivergenceReasonInputMethod toDomain(KrcmtDvgcTime entities) {
		DivergenceReasonInputMethodGetMemento memento = new JpaDivergenceReasonInputMethodGetMemento(entities);
		return new DivergenceReasonInputMethod(memento);
	}

	/**
	 * Find by company id.
	 *
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	private List<DivergenceReasonInputMethod> findByCompanyId(String companyId) {
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<KrcmtDvgcTime> cq = criteriaBuilder.createQuery(KrcmtDvgcTime.class);
		Root<KrcmtDvgcTime> root = cq.from(KrcmtDvgcTime.class);

		// Build query
		cq.select(root);

		// create where conditions
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.cid), companyId));

		// add where to query
		cq.where(predicates.toArray(new Predicate[] {}));

		// query data
		List<KrcmtDvgcTime> KrcmtDvgcTime = em.createQuery(cq).getResultList();

		// return
		return KrcmtDvgcTime.isEmpty() ? new ArrayList<DivergenceReasonInputMethod>()
				: KrcmtDvgcTime.stream().map(item -> this.toDomain(item)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.
	 * DivergenceReasonInputMethodRepository#update(nts.uk.ctx.at.record.dom.
	 * divergence.time.DivergenceReasonInputMethod)
	 */
	@Override
	public void update(DivergenceReasonInputMethod domain) {
		this.commandProxy().update(this.toEntity(domain));

	}

	/**
	 * To entity.
	 *
	 * @param domain
	 *            the domain
	 * @return the krcst dvgc time
	 */
	private KrcmtDvgcTime toEntity(DivergenceReasonInputMethod domain) {

		KrcstDvgcTimePK PK = new KrcstDvgcTimePK(domain.getCompanyId(), domain.getDivergenceTimeNo());
		KrcmtDvgcTime entity = this.queryProxy().find(PK, KrcmtDvgcTime.class).orElse(new KrcmtDvgcTime());

		domain.saveToMemento(new JpaDivergenceReasonInputMethodSetMemento(entity));

		return entity;
	}

	@Override
	public List<DivergenceReasonInputMethod> getByCidAndLstTimeInfo(String companyId, List<Integer> divTimeNos, int useClassification) {
		return this.queryProxy().query(FIND_DVGC_TIME, KrcmtDvgcTime.class)
				.setParameter("cid", companyId)
				.setParameter("no", divTimeNos)
				.setParameter("dvgcReasonSelected", useClassification == 1)
				.getList(t -> toDomain(t));
	}

	@Override
	public List<DivergenceReasonInputMethod> getByCidAndLstTimeInfo(String companyId, List<Integer> divTimeNos) {
		divTimeNos = divTimeNos.stream().filter(no -> no != null).distinct().collect(Collectors.toList());
		return this.queryProxy().query(FIND_DVGC_TIME_V2, KrcmtDvgcTime.class)
				.setParameter("cid", companyId)
				.setParameter("no", divTimeNos)
				.getList(t -> toDomain(t));
	}

}
