package nts.uk.ctx.at.record.infra.repository.monthly.mergetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.infra.entity.monthly.mergetable.KrcdtMonMergePk;
import nts.uk.ctx.at.record.infra.entity.monthly.mergetable.KrcdtMonRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.MonthlyDayoffRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.MonthMergeKey;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMerge;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.remainmerge.RemainMergeRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.ClosureStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.absenceleave.AbsenceLeaveRemainData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.care.CareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildcareRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.publicholiday.PublicHolidayRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.RsvLeaRemNumEachMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.specialholiday.SpecialHolidayRemainData;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * リポジトリ実装：残数系
 * @author shuichi_ishida
 */
@Stateless
public class JpaRemainMerge extends JpaRepository implements RemainMergeRepository {

	private static final String DELETE_BY_PK = String.join(" ", "DELETE FROM KrcdtMonRemain a ",
			"WHERE  a.krcdtMonRemainPk.employeeId = :employeeId ",
			"AND    a.krcdtMonRemainPk.yearMonth = :yearMonth ",
			"AND    a.krcdtMonRemainPk.closureId = :closureId",
			"AND    a.krcdtMonRemainPk.closureDay = :closureDay",
			"AND    a.krcdtMonRemainPk.isLastDay = :isLastDay");

	private static final String FIND_BY_SIDS_AND_MONTHS = "SELECT a FROM KrcdtMonRemain a "
			+ "WHERE a.krcdtMonRemainPk.employeeId IN :employeeIds "
			+ "AND a.krcdtMonRemainPk.yearMonth IN :yearMonths "
			+ "ORDER BY a.krcdtMonRemainPk.employeeId, a.startDate ";
	//hoatt
	private static final String FIND_BY_YEAR_MONTH = "SELECT a FROM KrcdtMonRemain a "
			+ "WHERE a.krcdtMonRemainPk.employeeId = :employeeId "
			+ "AND a.krcdtMonRemainPk.yearMonth = :yearMonth "
			+ "ORDER BY a.startDate ";
	private static final String FIND_BY_LIST_YRMON = "SELECT a FROM KrcdtMonRemain a "
			+ "WHERE a.krcdtMonRemainPk.employeeId = :employeeId "
			+ "AND a.krcdtMonRemainPk.yearMonth IN :lstyrMon "
			+ "ORDER BY a.startDate ";

	private static final String FIND_BY_YM_AND_CLOSURE_ID = "SELECT a FROM KrcdtMonRemain a "
			+ "WHERE a.krcdtMonRemainPk.employeeId = :employeeId "
			+ "AND a.krcdtMonRemainPk.yearMonth = :yearMonth "
			+ "AND a.krcdtMonRemainPk.closureId = :closureId "
			+ "ORDER BY a.startDate ";

	private static final String FIND_BY_SIDS = "SELECT a FROM KrcdtMonRemain a "
			+ "WHERE a.krcdtMonRemainPk.employeeId IN :employeeIds "
			+ "AND a.krcdtMonRemainPk.yearMonth = :yearMonth "
			+ "AND a.krcdtMonRemainPk.closureId = :closureId "
			+ "AND a.krcdtMonRemainPk.closureDay = :closureDay "
			+ "AND a.krcdtMonRemainPk.isLastDay = :isLastDay "
			+ "ORDER BY a.krcdtMonRemainPk.employeeId, a.startDate ";

	private static final String SQL_BY_YM_STATUS = "SELECT c FROM KrcdtMonRemain c"
			+ " WHERE c.krcdtMonRemainPk.employeeId = :sid"
			+ " AND c.krcdtMonRemainPk.yearMonth = :ym"
			+ " AND c.closureStatus = :status"
			+ " ORDER BY c.endDate ASC";

	private static final String FIND_BY_CLOSURE_PERIOD = "SELECT a FROM KrcdtMonRemain a "
			+ "WHERE a.krcdtMonRemainPk.employeeId = :employeeId "
			+ "AND a.startDate >= :startDate "
			+ "AND a.endDate <= :endDate "
			+ "AND a.closureStatus = 1 "
			+ "ORDER BY a.startDate ";

	/** 検索 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<RemainMerge> find(MonthMergeKey key) {

		return this.queryProxy()
				.find(new KrcdtMonMergePk(
						key.getEmployeeId(),
						key.getYearMonth().v(),
						key.getClosureId().value,
						key.getClosureDate().getClosureDay().v(),
						(key.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
						KrcdtMonRemain.class)
				.map(c -> c.toDomain());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<RemainMerge> find(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		return find(new MonthMergeKey(employeeId, yearMonth, closureId, closureDate));
	}

	/** 検索　（社員IDリストと年月リスト） */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<RemainMerge> findBySidsAndYearMonths(List<String> employeeIds, List<YearMonth> yearMonths) {

		val yearMonthValues = yearMonths.stream().map(c -> c.v()).collect(Collectors.toList());

		String sql = "select * from KRCDT_MON_REMAIN"
				+ " where SID in @emps"
				+ " and YM in @yms";

		List<RemainMerge> results = NtsStatement.In.split(employeeIds, emps -> {
			return new NtsStatement(sql, this.jdbcProxy())
					.paramString("emps", emps)
					.paramInt("yms", yearMonthValues)
					.getList(rec -> KrcdtMonRemain.MAPPER.toEntity(rec).toDomain());
		});

		return results;
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<RemainMerge> findByYearMonthOrderByStartYmd(String employeeId, YearMonth yearMonth){
		return this.queryProxy().query(FIND_BY_YEAR_MONTH, KrcdtMonRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.getList(c -> c.toDomain());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<RemainMerge> findByYMAndClosureIdOrderByStartYmd(String employeeId, YearMonth yearMonth,
			ClosureId closureId) {
		return this.queryProxy().query(FIND_BY_YM_AND_CLOSURE_ID, KrcdtMonRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.setParameter("closureId", closureId.value)
				.getList(c -> c.toDomain());
	}

	public List<RemainMerge> getByYmStatus(String sid, YearMonth ym, ClosureStatus status) {
		return this.queryProxy().query(SQL_BY_YM_STATUS, KrcdtMonRemain.class)
								.setParameter("sid", sid)
								.setParameter("ym", ym.v())
								.setParameter("status", status.value)
								.getList(e -> e.toDomain());
	}
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RemainMerge> findByClosurePeriod(String employeeId, DatePeriod closurePeriod) {
		return this.queryProxy().query(FIND_BY_CLOSURE_PERIOD, KrcdtMonRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("startDate", closurePeriod.start())
				.setParameter("endDate", closurePeriod.end())
				.getList(c -> c.toDomain());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<RemainMerge> findByEmployees(List<String> employeeIds, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		List<RemainMerge> results = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			results.addAll(this.queryProxy().query(FIND_BY_SIDS, KrcdtMonRemain.class)
					.setParameter("employeeIds", splitData)
					.setParameter("yearMonth", yearMonth.v())
					.setParameter("closureId", closureId.value)
					.setParameter("closureDay", closureDate.getClosureDay().v())
					.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
					.getList(c -> c.toDomain()));
		});

		return results;
	}


	/**
	 * @author hoatt
	 * Doi ung response KDR001 - RQ260
	 * @param employeeId
	 * @param yearMonth
	 * @return
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AbsenceLeaveRemainData> findByYearMonthRQ260(String employeeId, YearMonth yearMonth) {
		return this.queryProxy().query(FIND_BY_YEAR_MONTH, KrcdtMonRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.getList(c -> c.toDomainAbsenceLeaveRemainData());
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001 - RQ263
	 * @param employeeId
	 * @param yearMonth
	 * @return
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<SpecialHolidayRemainData> findByYearMonthRQ263(String employeeId, YearMonth yearMonth) {
		val entitys = this.queryProxy().query(FIND_BY_YEAR_MONTH, KrcdtMonRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.getList();
		List<SpecialHolidayRemainData> results = new ArrayList<>();
		for (val entity : entitys) results.addAll(entity.toDomainSpecialHolidayRemainData());
		return results;
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001 - RQ259
	 * @param employeeId
	 * @param yearMonth
	 * @return
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<MonthlyDayoffRemainData> findByYearMonthRQ259(String employeeId, YearMonth yearMonth) {
		return this.queryProxy().query(FIND_BY_YEAR_MONTH, KrcdtMonRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.getList(c -> c.toDomainMonthlyDayoffRemainData());
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001 - RQ258
	 * @param lstSID
	 * @param lstYrMon
	 * @return
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<RsvLeaRemNumEachMonth> findByYearMonthRQ258(List<String> lstSID, List<YearMonth> lstYrMon) {
		if(lstSID.isEmpty() || lstYrMon.isEmpty()){
			return new ArrayList<>();
		}
		val yearMonthVal = lstYrMon.stream().map(c -> c.v()).collect(Collectors.toList());
		List<KrcdtMonRemain> results = new ArrayList<>();
		CollectionUtil.split(lstSID, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			CollectionUtil.split(yearMonthVal, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstYearMonth -> {
				results.addAll(this.queryProxy().query(FIND_BY_SIDS_AND_MONTHS, KrcdtMonRemain.class)
						.setParameter("employeeIds", splitData)
						.setParameter("yearMonths", lstYearMonth)
						.getList());
			});
		});
		results.sort((o1, o2) -> {
			int tmp = o1.getKrcdtMonRemainPk().getEmployeeId().compareTo(o2.getKrcdtMonRemainPk().getEmployeeId());
			if (tmp != 0) return tmp;
			return o1.getStartDate().compareTo(o2.getStartDate());
		});
		return results.stream().map(c -> c.toDomainRsvLeaRemNumEachMonth()).collect(Collectors.toList());
	}
	/**
	 * @author hoatt
	 * Doi ung response KDR001 - RQ255
	 * @param lstSID
	 * @param lstYrMon
	 * @return
	 */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<AnnLeaRemNumEachMonth> findByYearMonthRQ255(List<String> lstSID, List<YearMonth> lstYrMon) {
		if(lstSID.isEmpty() || lstYrMon.isEmpty()){
			return new ArrayList<>();
		}
		val yearMonthValues = lstYrMon.stream().map(c -> c.v()).collect(Collectors.toList());
		List<KrcdtMonRemain> results = new ArrayList<>();
		CollectionUtil.split(lstSID, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			CollectionUtil.split(yearMonthValues, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstYearMonth -> {
				results.addAll(this.queryProxy().query(FIND_BY_SIDS_AND_MONTHS, KrcdtMonRemain.class)
						.setParameter("employeeIds", splitData)
						.setParameter("yearMonths", lstYearMonth)
						.getList());
			});
		});
		results.sort((o1, o2) -> {
			int tmp = o1.getKrcdtMonRemainPk().getEmployeeId().compareTo(o2.getKrcdtMonRemainPk().getEmployeeId());
			if (tmp != 0) return tmp;
			return o1.getStartDate().compareTo(o2.getStartDate());
		});
		return results.stream().map(c -> c.toDomainAnnLeaRemNumEachMonth()).collect(Collectors.toList());
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Map<YearMonth, List<RemainMerge>> findBySidsAndYrMons(String employeeId, List<YearMonth> lstYrMon) {
		if(lstYrMon.isEmpty()){
			return new HashMap<>();
		}
		val lstYrMonVal = lstYrMon.stream().map(c -> c.v()).collect(Collectors.toList());
		Map<YearMonth, List<RemainMerge>> mapResult = new HashMap<>();
		List<RemainMerge> lstTmp = this.queryProxy().query(FIND_BY_LIST_YRMON, KrcdtMonRemain.class)
										.setParameter("employeeId", employeeId)
										.setParameter("lstyrMon", lstYrMonVal)
										.getList(c -> c.toDomain());
		for (YearMonth yearMon : lstYrMon) {
			List<RemainMerge> lstFl = lstTmp.stream()
					.filter(c -> c.getMonthMergeKey().getYearMonth().equals(yearMon))
					.collect(Collectors.toList());
			mapResult.put(yearMon, lstFl);
		}
		return mapResult;
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(MonthMergeKey key, RemainMerge domains) {

		// キー
		val entityKey = new KrcdtMonMergePk(
				key.getEmployeeId(),
				key.getYearMonth().v(),
				key.getClosureId().value,
				key.getClosureDate().getClosureDay().v(),
				(key.getClosureDate().getLastDayOfMonth() ? 1 : 0));

		internalPersistAndUpdate(entityKey, entity -> entity.toEntityRemainMerge(domains));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(RemainMerge domains) {

		persistAndUpdate(domains.getMonthMergeKey(), domains);
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(CareRemNumEachMonth domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getEmployeeId(),
														domain.getYearMonth().v(),
														domain.getClosureId().value,
														domain.getClosureDate().getClosureDay().v(),
														domain.getClosureDate().getLastDayOfMonth()? 1 : 0),
								entity -> entity.toEntityCareRemainData(domain));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(SpecialHolidayRemainData domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getSid(),
														domain.getYm().v(),
														domain.getClosureId(),
														domain.getClosureDate().getClosureDay().v(),
														(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
								entity -> entity.toEntitySpeRemain(domain));
	}
	/** 登録および更新 */
	@Override
	public void persistAndUpdate(AnnLeaRemNumEachMonth domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getEmployeeId(),
														domain.getYearMonth().v(),
														domain.getClosureId().value,
														domain.getClosureDate().getClosureDay().v(),
														(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
								entity -> entity.toEntityMonAnnleaRemain(domain));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(AbsenceLeaveRemainData domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getSId(),
														domain.getYm().v(),
														domain.getClosureId(),
														domain.getClosureDay(),
														(domain.isLastDayIs() ? 1 : 0)),
								entity -> entity.toEntityAbsenceLeaveRemainData(domain));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(MonthlyDayoffRemainData domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getSId(),
														domain.getYm().v(),
														domain.getClosureId(),
														domain.getClosureDay(),
														(domain.isLastDayis() ? 1 : 0)),
								entity -> entity.toEntityDayOffRemainDayAndTimes(domain));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(List<SpecialHolidayRemainData> domains) {

		if (domains.isEmpty()) return;

		SpecialHolidayRemainData domain = domains.get(0);

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getSid(),
														domain.getYm().v(),
														domain.getClosureId(),
														domain.getClosureDate().getClosureDay().v(),
														(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
								entity -> entity.toEntitySpeRemains(domains));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(ChildcareRemNumEachMonth domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getEmployeeId(),
														domain.getYearMonth().v(),
														domain.getClosureId().value,
														domain.getClosureDate().getClosureDay().v(),
														(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
								entity -> entity.toEntityChildRemainData(domain));
	}

	/** 登録および更新 */
	@Override
	public void persistAndUpdate(RsvLeaRemNumEachMonth domain) {

		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getEmployeeId(),
														domain.getYearMonth().v(),
														domain.getClosureId().value,
														domain.getClosureDate().getClosureDay().v(),
														(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
								entity -> entity.toEntityRsvLeaRemNumEachMonth(domain));
	}
	
	/** 登録および更新 */
	@Override
	public void persistAndUpdate(PublicHolidayRemNumEachMonth domain){
		internalPersistAndUpdate(new KrcdtMonMergePk(	domain.getEmployeeId(),
														domain.getYearMonth().v(),
														domain.getClosureId().value,
														domain.getClosureDate().getClosureDay().v(),
														(domain.getClosureDate().getLastDayOfMonth() ? 1 : 0)),
								entity -> entity.toEntityPublicHoliday(domain));
	}


	/** 削除 */
	@Override
	public void remove(MonthMergeKey key) {

		this.getEntityManager().createQuery(DELETE_BY_PK)
				.setParameter("employeeId", key.getEmployeeId())
				.setParameter("yearMonth", key.getYearMonth().v())
				.setParameter("closureId", key.getClosureId().value)
				.setParameter("closureDay", key.getClosureDate().getClosureDay().v())
				.setParameter("isLastDay", (key.getClosureDate().getLastDayOfMonth() ? 1 : 0))
				.executeUpdate();
	}

	@Override
	public void removeDayOff(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteDayOffRemainDayAndTimes());
	}

	@Override
	public void removeRsvLea(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteRsvLeaRemNumEachMonth());
	}

	@Override
	public void removeRsvLea(String employeeId, YearMonth yearMonth){
		internalRemove(employeeId, yearMonth, entity -> entity.deleteRsvLeaRemNumEachMonth());
	}

	@Override
	public void removeMonCareHd(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteCareRemainData());
	}

	@Override
	public void removeMonCareHd(String employeeId, YearMonth yearMonth){
		internalRemove(employeeId, yearMonth, entity -> entity.deleteCareRemainData());
	}

	@Override
	public void removeAnnLea(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteMonAnnleaRemain());
	}

	@Override
	public void removeAnnLea(String employeeId, YearMonth yearMonth){
		internalRemove(employeeId, yearMonth, entity -> entity.deleteMonAnnleaRemain());
	}

	@Override
	public void removeMonChildHd(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteChildRemainData());
	}

	@Override
	public void removeAbsenceLeave(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteAbsenceLeaveRemainData());
	}

	@Override
	public void removeMonChildHd(String employeeId, YearMonth yearMonth){
		internalRemove(employeeId, yearMonth, entity -> entity.deleteChildRemainData());
	}

	@Override
	public void removeSpecHoliday(String employeeId, YearMonth yearMonth) {
		internalRemove(employeeId, yearMonth, entity -> entity.deleteAllSpeRemains());
	}

	@Override
	public void removeSpecHoliday(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteAllSpeRemains());
	}

	@Override
	public void removeSpecHoliday(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate, int no) {
		internalRemove(employeeId, yearMonth, closureId, closureDate, entity -> entity.deleteSpeRemain(no));
	}
	
	@Override
	public void removeMonpublicHoliday(String employeeId, YearMonth yearMonth){
		internalRemove(employeeId, yearMonth, entity -> entity.deletePublicHoliday());
	}

	private void internalRemove(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate, Consumer<KrcdtMonRemain> remove) {

		// キー
		val key = new KrcdtMonMergePk(	employeeId,
										yearMonth.v(),
										closureId.value,
										closureDate.getClosureDay().v(),
										(closureDate.getLastDayOfMonth() ? 1 : 0));

		// 削除
		KrcdtMonRemain entity = this.getEntityManager().find(KrcdtMonRemain.class, key);
		if (entity != null) {
			remove.accept(entity);
		}
	}

	private void internalRemove(String employeeId, YearMonth yearMonth, Consumer<KrcdtMonRemain> remove) {

		val entitys = this.queryProxy().query(FIND_BY_YEAR_MONTH, KrcdtMonRemain.class)
										.setParameter("employeeId", employeeId)
										.setParameter("yearMonth", yearMonth.v())
										.getList();

		for (val entity : entitys) {
			remove.accept(entity);
		}
	}

	private void internalPersistAndUpdate(KrcdtMonMergePk entityKey, Consumer<KrcdtMonRemain> update) {

		// 登録・更新
		KrcdtMonRemain entity = this.getEntityManager().find(KrcdtMonRemain.class, entityKey);
		if (entity == null){
			entity = new KrcdtMonRemain();
			entity.setKrcdtMonRemainPk(entityKey);
			update.accept(entity);
			this.getEntityManager().persist(entity);
		}
		else {
			update.accept(entity);
		}
	}
}
