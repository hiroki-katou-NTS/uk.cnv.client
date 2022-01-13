  
package nts.uk.ctx.at.record.infra.repository.divergence.time;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import nts.arc.layer.infra.data.JpaRepository;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcmtDvgcAttendance;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcAttendancePK;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcAttendancePK_;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcAttendance_;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcmtDvgcTime;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTimePK;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTimePK_;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTime_;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.deviationtime.deviationtimeframe.DivergenceTimeUseSet;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class JpaDivergenceTimeRepository.
 */
@Stateless
public class JpaDivergenceTimeRepository extends JpaRepository implements DivergenceTimeRepository {
	
	private static final String FIND_BY_COMPANYID_AND_USE_ATR = "SELECT a FROM KrcmtDvgcTime a "
			+ "WHERE a.id.cid = :companyId "
			+ "		AND a.dvgcTimeUseSet = :dvgcTimeUseSet ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * getAllDivTime(java.lang.String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DivergenceTimeRoot> getAllDivTime(String companyId) {
		return this.findByCompanyId(companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#update(
	 * nts.uk.ctx.at.record.dom.divergence.time.DivergenceTime)
	 */
	@Override
	public void update(DivergenceTimeRoot divTimeDomain) {

		if (divTimeDomain.isDivergenceTimeUse()) {
			// Update Divergence Time Information
			this.commandProxy().update(this.toEntity(divTimeDomain));

			// Delete Divergence Attendance List
			this.deleteAllAttendance(divTimeDomain.getDivergenceTimeNo(), divTimeDomain.getCompanyId());

			// Get Divergence Attendance Entity List
			List<KrcmtDvgcAttendance> attendanceList = divTimeDomain.getTargetItems().stream()
					.map(e -> toEntityAttendance(divTimeDomain.getDivergenceTimeNo(), divTimeDomain.getCompanyId(),
							e.intValue()))
					.collect(Collectors.toList());

			// Add new Divergence Attendance List
			this.addAttendance(attendanceList);
		} else {
			// get primaty Key
			KrcstDvgcTimePK PK = new KrcstDvgcTimePK(AppContexts.user().companyId(),
					divTimeDomain.getDivergenceTimeNo());

			// get optional entity
			Optional<KrcmtDvgcTime> optionalEntity = this.queryProxy().find(PK, KrcmtDvgcTime.class);

			// if entity is present
			if (optionalEntity.isPresent()) {
				KrcmtDvgcTime entity = optionalEntity.get();
				// update UsesageSet
				entity.setDvgcTimeUseSet(new BigDecimal(divTimeDomain.getDivTimeUseSet().value));
				this.commandProxy().update(entity);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * getDivTimeListByNo(java.lang.String, java.util.List)
	 */
	// Pls don't use this method, will be removed
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DivergenceTimeRoot> getDivTimeListByNo(String companyId, List<Integer> divTimeNo) {

		// Define DivergenceTime list
		List<DivergenceTimeRoot> divTimeList = new ArrayList<DivergenceTimeRoot>();

		// Foreach integer list
		divTimeNo.forEach(item -> {
			// Get OptionalDivTime
			Optional<DivergenceTimeRoot> optionalDivTime = this.getDivTimeInfo(companyId, item);
			// if present DivergenceTime
			if (optionalDivTime.isPresent()) {
				// get domain
				DivergenceTimeRoot divTime = optionalDivTime.get();
				// add domain to DivergenceTime list
				divTimeList.add(divTime);
			}
		});
		// return
		return divTimeList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * getDivTimeInfo(java.lang.String, int)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<DivergenceTimeRoot> getDivTimeInfo(String companyId, int divTimeNo) {

		KrcstDvgcTimePK PK = new KrcstDvgcTimePK(companyId, divTimeNo);
		return this.queryProxy().find(PK, KrcmtDvgcTime.class).map(e -> this.toDomain(e));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * findAttendanceId(java.lang.String, int)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<Integer> findAttendanceId(String companyId, int divTimeNo) {
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<KrcmtDvgcAttendance> cq = criteriaBuilder.createQuery(KrcmtDvgcAttendance.class);
		Root<KrcmtDvgcAttendance> root = cq.from(KrcmtDvgcAttendance.class);

		// Build query
		cq.select(root);

		// create where conditions
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(
				criteriaBuilder.equal(root.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.cid), companyId));
		predicates.add(
				criteriaBuilder.equal(root.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.no), divTimeNo));

		// add where to query
		cq.where(predicates.toArray(new Predicate[] {}));

		// query data
		List<KrcmtDvgcAttendance> KrcmtDvgcAttendance = em.createQuery(cq).getResultList();

		// return
		return KrcmtDvgcAttendance.isEmpty() ? new ArrayList<Integer>()
				: KrcmtDvgcAttendance.stream().map(item -> item.getId().getAttendanceId()).collect(Collectors.toList());

	}

	/**
	 * Find attendance.
	 *
	 * @param companyId
	 *            the company id
	 * @param divTimeNo
	 *            the div time no
	 * @return the list
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * findAttendanceId(java.lang.String, int)
	 */
	public List<KrcmtDvgcAttendance> findAttendance(String companyId, int divTimeNo) {
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<KrcmtDvgcAttendance> cq = criteriaBuilder.createQuery(KrcmtDvgcAttendance.class);
		Root<KrcmtDvgcAttendance> root = cq.from(KrcmtDvgcAttendance.class);

		// Build query
		cq.select(root);

		// create where conditions
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(
				criteriaBuilder.equal(root.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.cid), companyId));
		predicates.add(
				criteriaBuilder.equal(root.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.no), divTimeNo));

		// add where to query
		cq.where(predicates.toArray(new Predicate[] {}));

		// query data
		List<KrcmtDvgcAttendance> KrcmtDvgcAttendance = em.createQuery(cq).getResultList();

		// return
		return KrcmtDvgcAttendance.isEmpty() ? null : KrcmtDvgcAttendance;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * getDivTimeListByUseSet(java.lang.String)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DivergenceTimeRoot> getDivTimeListByUseSet(String companyId) {

		// get all div time by logined company
		List<DivergenceTimeRoot> divTimeList = this.findByCompanyId(companyId);

		// filter the div time list by use classification is USE and sort by div
		// time NO
		return divTimeList.stream().filter(item -> item.getDivTimeUseSet().equals(DivergenceTimeUseSet.USE))
				.sorted(Comparator.comparing(DivergenceTimeRoot::getDivergenceTimeNo)).collect(Collectors.toList());
	}

	/**
	 * To domain.
	 *
	 * @param entityDvgcTime
	 *            the entity dvgc time
	 * @return the divergence time
	 */
	private DivergenceTimeRoot toDomain(KrcmtDvgcTime entityDvgcTime) {

		List<KrcmtDvgcAttendance> entityAttendance = this.findAttendance(entityDvgcTime.getId().getCid(),
				entityDvgcTime.getId().getNo());

		DivergenceTimeGetMemento memento = new JpaDivergenceTimeGetMemento(entityDvgcTime, entityAttendance);

		return new DivergenceTimeRoot(memento);
	}

	/**
	 * To entity attendance.
	 *
	 * @param divTimeNo
	 *            the div time no
	 * @param companyId
	 *            the company id
	 * @param attendanceId
	 *            the attendance id
	 * @return the krcst dvgc attendance
	 */
	private KrcmtDvgcAttendance toEntityAttendance(Integer divTimeNo, String companyId, Integer attendanceId) {

		KrcstDvgcAttendancePK PK = new KrcstDvgcAttendancePK(divTimeNo, companyId, attendanceId);

		KrcmtDvgcAttendance entity = new KrcmtDvgcAttendance();
		entity.setId(PK);

		return entity;
	}

	/**
	 * Delete all attendance.
	 *
	 * @param divTimeNo
	 *            the div time no
	 * @param companyId
	 *            the company id
	 */
	private void deleteAllAttendance(int divTimeNo, String companyId) {

		// Get List entity
		List<KrcmtDvgcAttendance> attendanceList = findAttendanceEntity(companyId, divTimeNo);

		// Remove List
		// this.commandProxy().removeAll(KrcmtDvgcAttendance.class,
		// attendanceList);
		this.commandProxy().removeAll(attendanceList);
		this.getEntityManager().flush();

	}

	/**
	 * Adds the attendance.
	 *
	 * @param attendanceList
	 *            the attendance list
	 */
	private void addAttendance(List<KrcmtDvgcAttendance> attendanceList) {

		this.commandProxy().insertAll(attendanceList);

	}

	/**
	 * Find by company id.
	 *
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	private List<DivergenceTimeRoot> findByCompanyId(String companyId) {
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<?> cq = criteriaBuilder.createQuery();

		Root<KrcmtDvgcTime> root = cq.from(KrcmtDvgcTime.class);
		Join<KrcmtDvgcTime, KrcmtDvgcAttendance> joinRoot = root
				.join(KrcstDvgcTime_.krcstDvgcAttendances, JoinType.LEFT);

		// create where conditions
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(criteriaBuilder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.cid),
				companyId));

		// add where to query
		cq.multiselect(root, joinRoot);
		cq.where(predicates.toArray(new Predicate[] {}));

		// query data
		List<Object[]> results = (List<Object[]>) em.createQuery(cq).getResultList();

		// Check empty
		if (CollectionUtil.isEmpty(results)) {
			return Collections.emptyList();
		}

		Map<KrcmtDvgcTime, List<KrcmtDvgcAttendance>> mapItem = results.stream()
				.collect(Collectors.groupingBy(item -> (KrcmtDvgcTime) item[0],
						Collectors.mapping(x -> (KrcmtDvgcAttendance) x[1], Collectors.toList())));

		return mapItem.entrySet().stream()
				.map(item -> new DivergenceTimeRoot(
						new JpaDivergenceTimeGetMemento(item.getKey(), item.getValue())))
				.collect(Collectors.toList());
	}

	/**
	 * To entity.
	 *
	 * @param domain
	 *            the domain
	 * @return the krcst dvgc time
	 */
	private KrcmtDvgcTime toEntity(DivergenceTimeRoot domain) {

//		List<KrcmtDvgcAttendance> entityAttendanceList = this.findAttendance(domain.getCompanyId(),
//				domain.getDivergenceTimeNo());
		KrcstDvgcTimePK PK = new KrcstDvgcTimePK(domain.getCompanyId(), domain.getDivergenceTimeNo());
		KrcmtDvgcTime entityDvgcTime = this.queryProxy().find(PK, KrcmtDvgcTime.class).orElse(new KrcmtDvgcTime());

		domain.saveToMemento(new JpaDivergenceTimeSetMemento(entityDvgcTime));

		return entityDvgcTime;

	}

	/**
	 * Find attendance entity.
	 *
	 * @param companyId
	 *            the company id
	 * @param divTimeNo
	 *            the div time no
	 * @return the list
	 */
	private List<KrcmtDvgcAttendance> findAttendanceEntity(String companyId, int divTimeNo) {
		EntityManager em = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<KrcmtDvgcAttendance> cq = criteriaBuilder.createQuery(KrcmtDvgcAttendance.class);
		Root<KrcmtDvgcAttendance> root = cq.from(KrcmtDvgcAttendance.class);

		// Build query
		cq.select(root);

		// create where conditions
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(
				criteriaBuilder.equal(root.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.cid), companyId));
		predicates.add(
				criteriaBuilder.equal(root.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.no), divTimeNo));

		// add where to query
		cq.where(predicates.toArray(new Predicate[] {}));

		// query data
		List<KrcmtDvgcAttendance> KrcmtDvgcAttendance = em.createQuery(cq).getResultList();

		// return
		return KrcmtDvgcAttendance.isEmpty() ? new ArrayList<KrcmtDvgcAttendance>() : KrcmtDvgcAttendance;

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.divergence.time.DivergenceTimeRepository#
	 * getDivTimeListByNo(java.lang.String, java.util.List)
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DivergenceTimeRoot> getUsedDivTimeListByNoV2(String companyId, List<Integer> divTimeNo) {
    	
		divTimeNo = divTimeNo.stream().filter(no -> no != null).distinct().collect(Collectors.toList());

		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<Tuple> cq = builder.createTupleQuery();

		// From table
		Root<KrcmtDvgcTime> root = cq.from(KrcmtDvgcTime.class);
		Root<KrcmtDvgcAttendance> root2 = cq.from(KrcmtDvgcAttendance.class);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.cid), companyId));
		//predicateList.add(builder.isTrue(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no).in(divTimeNo)));
		predicateList.add(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no).in(divTimeNo));
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no), 
										root2.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.no)));
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.cid), 
				root2.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.cid)));
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.dvgcTimeUseSet), DivergenceTimeUseSet.USE.value));
		cq.where(predicateList.toArray(new Predicate[] {}));
		
		// order by
		cq.orderBy(builder.asc(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no)));
		
		// Get NO and optional attr only
		/** TODO: JOIN */
		cq = cq.multiselect(root, root2);

		// Get results
		List<Tuple> results = em.createQuery(cq).getResultList();
		return results.stream().collect(Collectors.groupingBy(c -> (KrcmtDvgcTime) c.get(0), Collectors.collectingAndThen(Collectors.toList(),
				list -> list.stream().map(dt -> (KrcmtDvgcAttendance) dt.get(1)).collect(Collectors.toList())))).entrySet().stream().map(c -> {
					DivergenceTimeGetMemento memento = new JpaDivergenceTimeGetMemento(c.getKey(), c.getValue());

					return new DivergenceTimeRoot(memento);
				}).collect(Collectors.toList());
	}
	
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<DivergenceTimeRoot> getUsedDivTimeListByNoV3(String companyId, List<Integer> divTimeNo) {

		// Get entity manager
		EntityManager em = this.getEntityManager();

		// Create builder
		CriteriaBuilder builder = em.getCriteriaBuilder();

		// Create query
		CriteriaQuery<Tuple> cq = builder.createTupleQuery();

		// From table
		Root<KrcmtDvgcTime> root = cq.from(KrcmtDvgcTime.class);
		Root<KrcmtDvgcAttendance> root2 = cq.from(KrcmtDvgcAttendance.class);

		List<Predicate> predicateList = new ArrayList<Predicate>();

		// Add where condition
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.cid), companyId));
		//predicateList.add(builder.isTrue(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no).in(divTimeNo)));
		predicateList.add(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no).in(divTimeNo));
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no), 
										root2.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.no)));
		predicateList.add(builder.equal(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.cid), 
				root2.get(KrcstDvgcAttendance_.id).get(KrcstDvgcAttendancePK_.cid)));
		cq.where(predicateList.toArray(new Predicate[] {}));
		
		// order by
		cq.orderBy(builder.asc(root.get(KrcstDvgcTime_.id).get(KrcstDvgcTimePK_.no)));
		
		// Get NO and optional attr only
		/** TODO: JOIN */
		cq = cq.multiselect(root, root2);

		// Get results
		List<Tuple> results = em.createQuery(cq).getResultList();
		return results.stream().collect(Collectors.groupingBy(c -> (KrcmtDvgcTime) c.get(0), Collectors.collectingAndThen(Collectors.toList(),
				list -> list.stream().map(dt -> (KrcmtDvgcAttendance) dt.get(1)).collect(Collectors.toList())))).entrySet().stream().map(c -> {
					DivergenceTimeGetMemento memento = new JpaDivergenceTimeGetMemento(c.getKey(), c.getValue());

					return new DivergenceTimeRoot(memento);
				}).collect(Collectors.toList());
	}

	@Override
	public List<DivergenceTimeRoot> findByCompanyAndUseDistination(String companyId, int useDistination) {
		return this.queryProxy().query(FIND_BY_COMPANYID_AND_USE_ATR, KrcmtDvgcTime.class)
								.setParameter("companyId", companyId)
								.setParameter("dvgcTimeUseSet", BigDecimal.valueOf(useDistination))
								.getList().stream()
								.map(t -> this.toDomain(t)).collect(Collectors.toList());
	}
}