package nts.uk.ctx.at.record.infra.repository.weekly;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekAggrHdwkTime;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekAggrOverTime;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekAnyItemValue;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekAttendanceTime;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekAttendanceTimePK;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekTimeOutside;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekTimeTotalcount;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.workdays.KrcdtWekDaysAbsence;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.workdays.KrcdtWekAggrSpecDays;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.workdays.KrcdtWekAggrSpvcDays;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekTimeBonusPay;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekTimeDvgc;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekTimeGoout;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekAggrPremTime;
import nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime.KrcdtWekMedicalTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.SpecificDateItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeeklyKey;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeeklyRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.holidaywork.HolidayWorkFrameNo;
import nts.uk.ctx.at.shared.dom.workrule.outsideworktime.overtime.overtimeframe.OverTimeFrameNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkTimeNightShift;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * リポジトリ実装：週別実績の勤怠時間
 * @author shuichi_ishida
 */
@Stateless
public class JpaAttendanceTimeOfWeekly extends JpaRepository implements AttendanceTimeOfWeeklyRepository {

	private static final String WHERE_YM = "WHERE a.PK.employeeId = :employeeId "
			+ "AND a.PK.yearMonth = :yearMonth ";

	private static final String WHERE_SIDS_AND_YM = "WHERE a.PK.employeeId IN :employeeIds "
			+ "AND a.PK.yearMonth = :yearMonth ";

	private static final String WHERE_CLOSURE = WHERE_YM
			+ "AND a.PK.closureId = :closureId "
			+ "AND a.PK.closureDay = :closureDay "
			+ "AND a.PK.isLastDay = :isLastDay ";

	private static final String WHERE_SIDS_AND_CLOSURE = WHERE_SIDS_AND_YM
			+ "AND a.PK.closureId = :closureId "
			+ "AND a.PK.closureDay = :closureDay "
			+ "AND a.PK.isLastDay = :isLastDay ";
	
	private static final String WHERE_PK = WHERE_CLOSURE
			+ "AND a.PK.weekNo = :weekNo ";
	
	private static final String FIND_BY_CLOSURE = "SELECT a FROM KrcdtWekAttendanceTime a "
			+ WHERE_CLOSURE
			+ "ORDER BY a.startYmd ";

	private static final String FIND_BY_YEARMONTH = "SELECT a FROM KrcdtWekAttendanceTime a "
			+ WHERE_YM
			+ "ORDER BY a.startYmd ";

	private static final String FIND_BY_EMPLOYEES = "SELECT a FROM KrcdtWekAttendanceTime a "
			+ WHERE_SIDS_AND_CLOSURE
			+ "ORDER BY a.startYmd ";

	private static final String FIND_BY_SIDS_AND_YEARMONTHS = "SELECT a FROM KrcdtWekAttendanceTime a "
			+ WHERE_SIDS_AND_YM
			+ "ORDER BY a.PK.employeeId, a.PK.yearMonth, a.startYmd ";
	
	private static final String FIND_BY_SIDS_AND_PERIOD = "SELECT a FROM KrcdtWekAttendanceTime a "
			+ "WHERE a.PK.employeeId IN :employeeIds "
			+ "AND a.startYmd >= :startDate "
			+ "AND a.endYmd <= :endDate ";
	
	private static final String FIND_BY_PERIOD = "SELECT a FROM KrcdtWekAttendanceTime a "
			+ "WHERE a.PK.employeeId = :employeeId "
			+ "AND a.startYmd <= :endDate "
			+ "AND a.endYmd >= :startDate ";
	
	private static final List<String> DELETE_TABLES = Arrays.asList(
			"DELETE FROM KrcdtWekAttendanceTime a ",
			"DELETE FROM KrcdtWekAggrOverTime a ",
			"DELETE FROM KrcdtWekAggrHdwkTime a ",
			"DELETE FROM KrcdtWekDaysAbsence a ",
			"DELETE FROM KrcdtWekAggrSpecDays a ",
			"DELETE FROM KrcdtWekAggrSpvcDays a ",
			"DELETE FROM KrcdtWekTimeBonusPay a ",
			"DELETE FROM KrcdtWekTimeDvgc a ",
			"DELETE FROM KrcdtWekTimeGoout a ",
			"DELETE FROM KrcdtWekAggrPremTime a ",
			"DELETE FROM KrcdtWekMedicalTime a ",
			"DELETE FROM KrcdtWekTimeOutside a ",
			"DELETE FROM KrcdtWekTimeTotalcount a ",
			"DELETE FROM KrcdtWekAnyItemValue a ");

	/** 検索 */
	@Override
	public Optional<AttendanceTimeOfWeekly> find(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate, int weekNo) {
		
		return this.queryProxy()
				.find(new KrcdtWekAttendanceTimePK(
						employeeId,
						yearMonth.v(),
						closureId.value,
						closureDate.getClosureDay().v(),
						(closureDate.getLastDayOfMonth() ? 1 : 0),
						weekNo),
					KrcdtWekAttendanceTime.class)
				.map(c -> c.toDomain());
	}

	/** 検索　（締め） */
	@Override
	public List<AttendanceTimeOfWeekly> findByClosure(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		
		return this.queryProxy().query(FIND_BY_CLOSURE, KrcdtWekAttendanceTime.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.setParameter("closureId", closureId.value)
				.setParameter("closureDay", closureDate.getClosureDay().v())
				.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
				.getList(c -> c.toDomain());
	}

	/** 検索　（年月） */
	@Override
	public List<AttendanceTimeOfWeekly> findByYearMonth(String employeeId, YearMonth yearMonth) {
		
		return this.queryProxy().query(FIND_BY_YEARMONTH, KrcdtWekAttendanceTime.class)
				.setParameter("employeeId", employeeId)
				.setParameter("yearMonth", yearMonth.v())
				.getList(c -> c.toDomain());
	}
	
	/** 検索　（社員IDリストと締め） */
	@Override
	public List<AttendanceTimeOfWeekly> findBySids(List<String> employeeIds, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		
		List<AttendanceTimeOfWeekly> results = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			results.addAll(this.queryProxy().query(FIND_BY_EMPLOYEES, KrcdtWekAttendanceTime.class)
					.setParameter("employeeIds", splitData)
					.setParameter("yearMonth", yearMonth.v())
					.setParameter("closureId", closureId.value)
					.setParameter("closureDay", closureDate.getClosureDay().v())
					.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
					.getList(c -> c.toDomain()));
		});
		return results;
	}

	/** 検索　（社員IDリストと年月リスト） */
	@Override
	public List<AttendanceTimeOfWeekly> findBySidsAndYearMonths(List<String> employeeIds, List<YearMonth> yearMonths) {
		
		val yearMonthValues = yearMonths.stream().map(c -> c.v()).collect(Collectors.toList());
		
		List<AttendanceTimeOfWeekly> results = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			CollectionUtil.split(yearMonthValues, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstYearMonth -> {
				results.addAll(this.queryProxy().query(FIND_BY_SIDS_AND_YEARMONTHS, KrcdtWekAttendanceTime.class)
						.setParameter("employeeIds", splitData)
						.setParameter("yearMonths", lstYearMonth)
						.getList(c -> c.toDomain()));
			});
		});
		return results;
	}
	
	/** 検索　（社員IDリストと基準日ト） */
	@Override
	public List<AttendanceTimeOfWeekly> findBySidsAndDatePeriod(List<String> employeeIds, DatePeriod datePeriod) {
		
		GeneralDate startDate = datePeriod.start();
		GeneralDate endDate = datePeriod.end();
		
		List<AttendanceTimeOfWeekly> results = new ArrayList<>();
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, splitData -> {
			results.addAll(this.queryProxy().query(FIND_BY_SIDS_AND_PERIOD, KrcdtWekAttendanceTime.class)
					.setParameter("employeeIds", splitData)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate)
					.getList(c -> c.toDomain()));
		});
		return results;
	}
	
	/** 検索　（基準日） */
	@Override
	public List<AttendanceTimeOfWeekly> findByDate(String employeeId, GeneralDate criteriaDate) {
		
		return this.queryProxy().query(FIND_BY_PERIOD, KrcdtWekAttendanceTime.class)
				.setParameter("employeeId", employeeId)
				.setParameter("startDate", criteriaDate)
				.setParameter("endDate", criteriaDate)
				.getList(c -> c.toDomain());
	}
	
	/** 登録および更新 */
	@SneakyThrows
	@Override
	public void persistAndUpdate(AttendanceTimeOfWeekly domain){
		
		// 登録・更新を判断
		boolean isNeedInsert;
		KrcdtWekAttendanceTime entity;
		try (PreparedStatement stmt = this.connection().prepareStatement(
				"select INS_DATE, INS_CCD, INS_SCD, INS_PG from KRCDT_WEK_ATTENDANCE_TIME"
				// SQLServerの場合は共有ロックがかかってしまい、並列で実行するとここでデッドロックが起きるのでNOLOCKで取得
				// ダーティリードの可能性があるが許容する（週次テーブルへの更新は月次集計だけなので、恐らく問題になることはない）
				+ (this.database().is(DatabaseProduct.MSSQLSERVER) ? " with(nolock)" : "")
				+ " where SID = ?"
				+ " and YM = ?"
				+ " and CLOSURE_ID = ?"
				+ " and CLOSURE_DAY = ?"
				+ " and IS_LAST_DAY = ?"
				+ " and WEEK_NO = ?")) {
			
			stmt.setString(1, domain.getEmployeeId());
			stmt.setInt(2, domain.getYearMonth().v());
			stmt.setInt(3, domain.getClosureId().value);
			stmt.setInt(4, domain.getClosureDate().getClosureDay().v());
			stmt.setInt(5, (domain.getClosureDate().getLastDayOfMonth() ? 1 : 0));
			stmt.setInt(6, domain.getWeekNo());
			
			// INS_***の列だけは取得しておく
			Optional<KrcdtWekAttendanceTime> opt = new NtsResultSet(stmt.executeQuery())
					.getSingle(r -> {
						KrcdtWekAttendanceTime e = new KrcdtWekAttendanceTime();
						e.setInsDate(r.getGeneralDateTime("INS_DATE"));
						e.setInsCcd(r.getString("INS_CCD"));
						e.setInsScd(r.getString("INS_SCD"));
						e.setInsPg(r.getString("INS_PG"));
						return e;
					});
			
			isNeedInsert = !opt.isPresent();
			entity = opt.orElseGet(() -> new KrcdtWekAttendanceTime());
			entity.fromDomainForPersist(domain);
		}
		
		fillEntity(domain, entity);
		
		// 登録が必要な時、登録を実行
		if (isNeedInsert) this.commandProxy().insert(entity);
		else this.commandProxy().update(entity);
	}
	
	/** 登録 */
	@Override
	public void persist(AttendanceTimeOfWeekly domain){
		
		KrcdtWekAttendanceTime entity = new KrcdtWekAttendanceTime();
		entity.fromDomainForPersist(domain);
		fillEntity(domain, entity);
		this.commandProxy().insert(entity);
	}

	private static void fillEntity(AttendanceTimeOfWeekly domain, KrcdtWekAttendanceTime entity) {
		
		// キー
		val domainKey = new AttendanceTimeOfWeeklyKey(
				domain.getEmployeeId(),
				domain.getYearMonth(),
				domain.getClosureId(),
				domain.getClosureDate(),
				domain.getWeekNo());

		// 週別の計算
		val weeklyCalculation = domain.getWeeklyCalculation();
		
		// 集計時間：残業時間：集計残業時間
		val totalWorkingTime = weeklyCalculation.getTotalWorkingTime();
		val aggrOverTimeMap = totalWorkingTime.getOverTime().getAggregateOverTimeMap();
		if (entity.krcdtWekAggrOverTimes == null) entity.krcdtWekAggrOverTimes = new ArrayList<>();
		val entityAggrOverTimeList = entity.krcdtWekAggrOverTimes;
		entityAggrOverTimeList.removeIf(
				a -> {return !aggrOverTimeMap.containsKey(new OverTimeFrameNo(a.PK.overTimeFrameNo));} );
		for (val aggrOverTime : aggrOverTimeMap.values()){
			KrcdtWekAggrOverTime entityAggrOverTime = new KrcdtWekAggrOverTime();
			val entityAggrOverTimeOpt = entityAggrOverTimeList.stream()
					.filter(c -> c.PK.overTimeFrameNo == aggrOverTime.getOverTimeFrameNo().v()).findFirst();
			if (entityAggrOverTimeOpt.isPresent()){
				entityAggrOverTime = entityAggrOverTimeOpt.get();
				entityAggrOverTime.fromDomainForUpdate(aggrOverTime);
			}
			else {
				entityAggrOverTime.fromDomainForPersist(domainKey, aggrOverTime);
				entityAggrOverTimeList.add(entityAggrOverTime);
			}
		}
		
		// 集計時間：休出・代休：集計休出時間
		val aggrHolidayWorkTimeMap = totalWorkingTime.getHolidayWorkTime().getAggregateHolidayWorkTimeMap();
		if (entity.krcdtWekAggrHdwkTimes == null) entity.krcdtWekAggrHdwkTimes = new ArrayList<>();
		val entityAggrHdwkTimeList = entity.krcdtWekAggrHdwkTimes;
		entityAggrHdwkTimeList.removeIf(
				a -> {return !aggrHolidayWorkTimeMap.containsKey(new HolidayWorkFrameNo(a.PK.holidayWorkFrameNo));} );
		for (val aggrHolidayWorkTime : aggrHolidayWorkTimeMap.values()){
			KrcdtWekAggrHdwkTime entityAggrHdwkTime = new KrcdtWekAggrHdwkTime();
			val entityAggrHdwkTimeOpt = entityAggrHdwkTimeList.stream()
					.filter(c -> c.PK.holidayWorkFrameNo == aggrHolidayWorkTime.getHolidayWorkFrameNo().v()).findFirst();
			if (entityAggrHdwkTimeOpt.isPresent()){
				entityAggrHdwkTime = entityAggrHdwkTimeOpt.get();
				entityAggrHdwkTime.fromDomainForUpdate(aggrHolidayWorkTime);
			}
			else {
				entityAggrHdwkTime.fromDomainForPersist(domainKey, aggrHolidayWorkTime);
				entityAggrHdwkTimeList.add(entityAggrHdwkTime);
			}
		}
		
		// 時間外超過：時間外超過
		val excessOutsideTimeMap = domain.getExcessOutside().getExcessOutsideItems();
		if (entity.krcdtWekExcoutTime == null) entity.krcdtWekExcoutTime = new ArrayList<>();
		val entityExcoutTimeList = entity.krcdtWekExcoutTime;
		entityExcoutTimeList.removeIf(
				a -> {return !excessOutsideTimeMap.containsKey(a.PK.breakdownNo);} );
		for (val breakdown : excessOutsideTimeMap.values()){
			KrcdtWekTimeOutside entityExcoutTime = new KrcdtWekTimeOutside();
			val entityExcoutTimeOpt = entityExcoutTimeList.stream()
					.filter(c -> c.PK.breakdownNo == breakdown.getBreakdownNo()).findFirst();
			if (entityExcoutTimeOpt.isPresent()){
				entityExcoutTime = entityExcoutTimeOpt.get();
				entityExcoutTime.fromDomainForUpdate(breakdown);
			}
			else {
				entityExcoutTime.fromDomainForPersist(domainKey, breakdown);
				entityExcoutTimeList.add(entityExcoutTime);
			}
		}
		
		// 縦計：勤務日数：集計欠勤日数
		val vtWorkDays = domain.getVerticalTotal().getWorkDays();
		val absenceDaysMap = vtWorkDays.getAbsenceDays().getAbsenceDaysList();
		if (entity.krcdtWekAggrAbsnDays == null) entity.krcdtWekAggrAbsnDays = new ArrayList<>();
		val entityAggrAbsnDaysList = entity.krcdtWekAggrAbsnDays;
		entityAggrAbsnDaysList.removeIf(a -> {return !absenceDaysMap.containsKey(a.PK.absenceFrameNo);} );
		for (val absenceDays : absenceDaysMap.values()){
			KrcdtWekDaysAbsence entityAggrAbsnDays = new KrcdtWekDaysAbsence();
			val entityAggrAbsnDaysOpt = entityAggrAbsnDaysList.stream()
					.filter(c -> c.PK.absenceFrameNo == absenceDays.getAbsenceFrameNo()).findFirst();
			if (entityAggrAbsnDaysOpt.isPresent()){
				entityAggrAbsnDays = entityAggrAbsnDaysOpt.get();
				entityAggrAbsnDays.fromDomainForUpdate(absenceDays);
			}
			else {
				entityAggrAbsnDays.fromDomainForPersist(domainKey, absenceDays);
				entityAggrAbsnDaysList.add(entityAggrAbsnDays);
			}
		}
		
		// 縦計：勤務日数：集計特定日数
		val specificDaysMap = vtWorkDays.getSpecificDays().getSpecificDays();
		if (entity.krcdtWekAggrSpecDays == null) entity.krcdtWekAggrSpecDays = new ArrayList<>();
		val entityAggrSpecDaysList = entity.krcdtWekAggrSpecDays;
		entityAggrSpecDaysList.removeIf(
				a -> {return !specificDaysMap.containsKey(new SpecificDateItemNo(a.PK.specificDayItemNo));} );
		for (val specificDays : specificDaysMap.values()){
			KrcdtWekAggrSpecDays entityAggrSpecDays = new KrcdtWekAggrSpecDays();
			val entityAggrSpecDaysOpt = entityAggrSpecDaysList.stream()
					.filter(c -> c.PK.specificDayItemNo == specificDays.getSpecificDayItemNo().v()).findFirst();
			if (entityAggrSpecDaysOpt.isPresent()){
				entityAggrSpecDays = entityAggrSpecDaysOpt.get();
				entityAggrSpecDays.fromDomainForUpdate(specificDays);
			}
			else {
				entityAggrSpecDays.fromDomainForPersist(domainKey, specificDays);
				entityAggrSpecDaysList.add(entityAggrSpecDays);
			}
		}
		
		// 縦計：勤務日数：集計特別休暇日数
		val spcVactDaysMap = vtWorkDays.getSpecialVacationDays().getSpcVacationDaysList();
		if (entity.krcdtWekAggrSpvcDays == null) entity.krcdtWekAggrSpvcDays = new ArrayList<>();
		val entityAggrSpvcDaysList = entity.krcdtWekAggrSpvcDays;
		entityAggrSpvcDaysList.removeIf(a -> {return !spcVactDaysMap.containsKey(a.PK.specialVacationFrameNo);} );
		for (val spcVactDays : spcVactDaysMap.values()){
			KrcdtWekAggrSpvcDays entityAggrSpvcDays = new KrcdtWekAggrSpvcDays();
			val entityAggrSpvcDaysOpt = entityAggrSpvcDaysList.stream()
					.filter(c -> c.PK.specialVacationFrameNo == spcVactDays.getSpcVacationFrameNo()).findFirst();
			if (entityAggrSpvcDaysOpt.isPresent()){
				entityAggrSpvcDays = entityAggrSpvcDaysOpt.get();
				entityAggrSpvcDays.fromDomainForUpdate(spcVactDays);
			}
			else {
				entityAggrSpvcDays.fromDomainForPersist(domainKey, spcVactDays);
				entityAggrSpvcDaysList.add(entityAggrSpvcDays);
			}
		}
		
		// 縦計：勤務時間：集計加給時間
		val vtWorkTime = domain.getVerticalTotal().getWorkTime();
		val bonusPayTimeMap = vtWorkTime.getBonusPayTime().getBonusPayTime();
		if (entity.krcdtWekAggrBnspyTime == null) entity.krcdtWekAggrBnspyTime = new ArrayList<>();
		val entityAggrBnspyTimeList = entity.krcdtWekAggrBnspyTime;
		entityAggrBnspyTimeList.removeIf(a -> {return !bonusPayTimeMap.containsKey(a.PK.bonusPayFrameNo);} );
		for (val bonusPayTime : bonusPayTimeMap.values()){
			KrcdtWekTimeBonusPay entityAggrBnspyTime = new KrcdtWekTimeBonusPay();
			val entityAggrBnspyTimeOpt = entityAggrBnspyTimeList.stream()
					.filter(c -> c.PK.bonusPayFrameNo == bonusPayTime.getBonusPayFrameNo()).findFirst();
			if (entityAggrBnspyTimeOpt.isPresent()){
				entityAggrBnspyTime = entityAggrBnspyTimeOpt.get();
				entityAggrBnspyTime.fromDomainForUpdate(bonusPayTime);
			}
			else {
				entityAggrBnspyTime.fromDomainForPersist(domainKey, bonusPayTime);
				entityAggrBnspyTimeList.add(entityAggrBnspyTime);
			}
		}
		
		// 縦計：勤務時間：集計乖離時間
		val divergenceTimeMap = vtWorkTime.getDivergenceTime().getDivergenceTimeList();
		if (entity.krcdtWekAggrDivgTime == null) entity.krcdtWekAggrDivgTime = new ArrayList<>();
		val entityAggrDivgTimeList = entity.krcdtWekAggrDivgTime;
		entityAggrDivgTimeList.removeIf(a -> {return !divergenceTimeMap.containsKey(a.PK.divergenceTimeNo);} );
		for (val divergenceTime : divergenceTimeMap.values()){
			KrcdtWekTimeDvgc entityAggrDivgTime = new KrcdtWekTimeDvgc();
			val entityAggrDivgTimeOpt = entityAggrDivgTimeList.stream()
					.filter(c -> c.PK.divergenceTimeNo == divergenceTime.getDivergenceTimeNo()).findFirst();
			if (entityAggrDivgTimeOpt.isPresent()){
				entityAggrDivgTime = entityAggrDivgTimeOpt.get();
				entityAggrDivgTime.fromDomainForUpdate(divergenceTime);
			}
			else {
				entityAggrDivgTime.fromDomainForPersist(domainKey, divergenceTime);
				entityAggrDivgTimeList.add(entityAggrDivgTime);
			}
		}
		
		// 縦計：勤務時間：集計外出
		val goOutMap = vtWorkTime.getGoOut().getGoOuts();
		if (entity.krcdtWekAggrGoout == null) entity.krcdtWekAggrGoout = new ArrayList<>();
		val entityAggrGooutList = entity.krcdtWekAggrGoout;
		entityAggrGooutList.removeIf(
				a -> {return !goOutMap.containsKey(EnumAdaptor.valueOf(a.PK.goOutReason, GoingOutReason.class));} );
		for (val goOut : goOutMap.values()){
			KrcdtWekTimeGoout entityAggrGoout = new KrcdtWekTimeGoout();
			val entityAggrGooutOpt = entityAggrGooutList.stream()
					.filter(c -> c.PK.goOutReason == goOut.getGoOutReason().value).findFirst();
			if (entityAggrGooutOpt.isPresent()){
				entityAggrGoout = entityAggrGooutOpt.get();
				entityAggrGoout.fromDomainForUpdate(goOut);
			}
			else {
				entityAggrGoout.fromDomainForPersist(domainKey, goOut);
				entityAggrGooutList.add(entityAggrGoout);
			}
		}
		
		// 縦計：勤務時間：集計割増時間
		val premiumTimeMap = vtWorkTime.getPremiumTime().getPremiumTime();
		if (entity.krcdtWekAggrPremTime == null) entity.krcdtWekAggrPremTime = new ArrayList<>();
		val entityAggrPremTimeList = entity.krcdtWekAggrPremTime;
		entityAggrPremTimeList.removeIf(a -> {return !premiumTimeMap.containsKey(a.PK.premiumTimeItemNo);} );
		for (val premiumTime : premiumTimeMap.values()){
			KrcdtWekAggrPremTime entityAggrPremTime = new KrcdtWekAggrPremTime();
			val entityAggrPremTimeOpt = entityAggrPremTimeList.stream()
					.filter(c -> c.PK.premiumTimeItemNo == premiumTime.getPremiumTimeItemNo().value).findFirst();
			if (entityAggrPremTimeOpt.isPresent()){
				entityAggrPremTime = entityAggrPremTimeOpt.get();
				entityAggrPremTime.fromDomainForUpdate(premiumTime);
			}
			else {
				entityAggrPremTime.fromDomainForPersist(domainKey, premiumTime);
				entityAggrPremTimeList.add(entityAggrPremTime);
			}
		}
		
		// 縦計：勤務時間：月別実績の医療時間
		val medicalTimeMap = vtWorkTime.getMedicalTime();
		if (entity.krcdtWekMedicalTime == null) entity.krcdtWekMedicalTime = new ArrayList<>();
		val entityMedicalTimeList = entity.krcdtWekMedicalTime;
		entityMedicalTimeList.removeIf(
				a -> {return !medicalTimeMap.containsKey(EnumAdaptor.valueOf(a.PK.dayNightAtr, WorkTimeNightShift.class));} );
		for (val medicalTime : medicalTimeMap.values()){
			KrcdtWekMedicalTime entityMedicalTime = new KrcdtWekMedicalTime();
			val entityMedicalTimeOpt = entityMedicalTimeList.stream()
					.filter(c -> c.PK.dayNightAtr == medicalTime.getDayNightAtr().value).findFirst();
			if (entityMedicalTimeOpt.isPresent()){
				entityMedicalTime = entityMedicalTimeOpt.get();
				entityMedicalTime.fromDomainForUpdate(medicalTime);
			}
			else {
				entityMedicalTime.fromDomainForPersist(domainKey, medicalTime);
				entityMedicalTimeList.add(entityMedicalTime);
			}
		}
		
		// 回数集計：回数集計
		val totalCountMap = domain.getTotalCount().getTotalCountList();
		if (entity.krcdtWekTotalTimes == null) entity.krcdtWekTotalTimes = new ArrayList<>();
		val entityTotalTimesList = entity.krcdtWekTotalTimes;
		entityTotalTimesList.removeIf(a -> {return !totalCountMap.containsKey(a.PK.totalTimesNo);} );
		for (val totalCount : totalCountMap.values()){
			KrcdtWekTimeTotalcount entityTotalTimes = new KrcdtWekTimeTotalcount();
			val entityTotalTimesOpt = entityTotalTimesList.stream()
					.filter(c -> c.PK.totalTimesNo == totalCount.getTotalCountNo()).findFirst();
			if (entityTotalTimesOpt.isPresent()){
				entityTotalTimes = entityTotalTimesOpt.get();
				entityTotalTimes.fromDomainForUpdate(totalCount);
			}
			else {
				entityTotalTimes.fromDomainForPersist(domainKey, totalCount);
				entityTotalTimesList.add(entityTotalTimes);
			}
		}
		
		// 任意項目：任意項目値
		val anyItemValueMap = domain.getAnyItem().getAnyItemValues();
		if (entity.krcdtWekAnyItemValue == null) entity.krcdtWekAnyItemValue = new ArrayList<>();
		val entityAnyItemValueList = entity.krcdtWekAnyItemValue;
		entityAnyItemValueList.removeIf(a -> {return !anyItemValueMap.containsKey(a.PK.anyItemId);} );
		for (val anyItemValue : anyItemValueMap.values()){
			KrcdtWekAnyItemValue entityAnyItemValue = new KrcdtWekAnyItemValue();
			val entityAnyItemValueOpt = entityAnyItemValueList.stream()
					.filter(c -> c.PK.anyItemId == anyItemValue.getAnyItemNo()).findFirst();
			if (entityAnyItemValueOpt.isPresent()){
				entityAnyItemValue = entityAnyItemValueOpt.get();
				entityAnyItemValue.fromDomainForUpdate(anyItemValue);
			}
			else {
				entityAnyItemValue.fromDomainForPersist(domainKey, anyItemValue);
				entityAnyItemValueList.add(entityAnyItemValue);
			}
		}
	}
	
	/** 削除 */
	@Override
	public void remove(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate,
			int weekNo) {
		
		for (val deleteTable : DELETE_TABLES){
			this.getEntityManager().createQuery(deleteTable + WHERE_PK)
					.setParameter("employeeId", employeeId)
					.setParameter("yearMonth", yearMonth.v())
					.setParameter("closureId", closureId.value)
					.setParameter("closureDay", closureDate.getClosureDay().v())
					.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
					.setParameter("weekNo", weekNo)
					.executeUpdate();
		}
	}

	/** 削除　（締め） */
	@Override
	public void removeByClosure(String employeeId, YearMonth yearMonth, ClosureId closureId,
			ClosureDate closureDate) {
		
		for (val deleteTable : DELETE_TABLES){
			this.getEntityManager().createQuery(deleteTable + WHERE_CLOSURE)
					.setParameter("employeeId", employeeId)
					.setParameter("yearMonth", yearMonth.v())
					.setParameter("closureId", closureId.value)
					.setParameter("closureDay", closureDate.getClosureDay().v())
					.setParameter("isLastDay", (closureDate.getLastDayOfMonth() ? 1 : 0))
					.executeUpdate();
		}
	}
	
	/** 削除　（年月） */
	@Override
	public void removeByYearMonth(String employeeId, YearMonth yearMonth) {
		
		for (val deleteTable : DELETE_TABLES){
			this.getEntityManager().createQuery(deleteTable + WHERE_YM)
					.setParameter("employeeId", employeeId)
					.setParameter("yearMonth", yearMonth.v())
					.executeUpdate();
		}
	}
}
