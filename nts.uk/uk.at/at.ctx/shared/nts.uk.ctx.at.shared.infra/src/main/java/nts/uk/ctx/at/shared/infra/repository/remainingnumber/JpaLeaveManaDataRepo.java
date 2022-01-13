package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.error.BusinessException;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.DigestionAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.DaysOffMana;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.subhdmana.KrcdtHdWorkMng;
import nts.uk.shr.com.context.AppContexts;
import nts.arc.time.calendar.period.DatePeriod;

@Stateless
public class JpaLeaveManaDataRepo extends JpaRepository implements LeaveManaDataRepository {

	private static final String QUERY_BYSID = "SELECT l FROM KrcdtHdWorkMng l WHERE l.cID = :cid AND l.sID =:employeeId";

	private static final String QUERY_BYSIDWITHSUBHDATR = String.join(" ", QUERY_BYSID, "AND l.subHDAtr =:subHDAtr");

	private static final String QUERY_LEAVEDAYOFF = String.join(" ", QUERY_BYSID, "AND l.leaveID = :comDayOffID");

	private static final String QUERY_BYSIDANDHOLIDAYDATECONDITION = "SELECT l FROM KrcdtHdWorkMng l WHERE l.cID = :cid AND l.sID =:employeeId AND l.dayOff = :dateHoliday";

	private static final String QUERY_BY_SID_HOLIDAY = "SELECT c FROM KrcdtHdWorkMng c"
			+ " WHERE c.sID = :employeeId"
			+ " AND c.unknownDate = :unknownDate"
			+ " AND c.dayOff >= :startDate";
	
	private static final String QUERY_BYSID_AND_NOT_UNUSED = "SELECT l FROM KrcdtHdWorkMng l "
			+ "INNER JOIN KrcdtHdComMng b ON l.leaveID = b.comDayOffID "
			+ "WHERE l.cID = :cid AND l.sID =:employeeId AND b.remainDays > 0";

	private static final String QUERY_BYID = "SELECT l FROM KrcdtHdWorkMng l WHERE l.leaveID IN :leaveIDs";

	private static final String QUERY_BY_DAYOFF_PERIOD = "SELECT c FROM KrcdtHdWorkMng c" + " WHERE c.sID = :sid"
			+ " AND c.dayOff >= :startDate" + " AND c.dayOff <= :endDate";
	private static final String QUERY_BY_EX = QUERY_BY_DAYOFF_PERIOD
			+ " AND (c.unUsedDays > :unUsedDays AND c.expiredDate >= :sDate AND c.expiredDate <= :eDate)"
			+ " OR (c.subHDAtr = :subHDAtr AND c.disapearDate >= :sDate AND c.disapearDate <= :eDate)";
	private String QUERY_BY_SID_DATE = QUERY_BYSID + " AND l.dayOff < :dayOff";

//	private String QUERY_DEADLINE_COMPENSATORY_NORMAL = "SELECT COUNT(*) FROM KrcdtHdWorkMng m WHERE m.sID = :sID AND m.dayOff<= :dayOff AND m.subHDAtr = :subHDAtr";
	private String QUERY_DEADLINE_COMPENSATORY = "SELECT COUNT(m.SID) FROM KRCDT_HD_WORK_MNG m WHERE m.SID = ?sID "
			+ "AND m.SUB_HD_ATR = ?subHDAtr"
			+ " AND (((MONTH(m.DAYOFF_DATE) + ?deadlMonth) >= ?currentMonth AND  MONTH(m.DAYOFF_DATE) <= ?currentMonth AND YEAR(m.DAYOFF_DATE) = ?currentYear)"
						+ " OR (12 - (MONTH(m.DAYOFF_DATE)) + ?currentMonth <= ?deadlMonth AND YEAR(m.DAYOFF_DATE) = (?currentYear - 1))) ";
	
	private String QUERY_BYSIDYMD = "SELECT l FROM KrcdtHdWorkMng l"
			+ " WHERE l.cID = :cid AND l.sID =:employeeId"
			+ " AND (l.dayOff < :dayOff OR l.dayOff is null)"
			+ " AND (l.unUsedDays > 0 OR l.unUsedTimes >0)"
			+ " AND l.subHDAtr = :subHDAtr ";
	
	// 全ての状況
	private static final String QUERY_ALL_DATA = "SELECT l FROM KrcdtHdWorkMng l";
	
	private static final String QUERY_BY_SID_STATE = "SELECT l from KrcdtHdWorkMng l"
			+ " WHERE l.cID = :cId"
			+ " AND l.sID = :sId"
			+ " AND l.subHDAtr = :subHDAtr"
			+ " ORDER BY l.dayOff";
	
	private static final String QUERY_BY_SID_DAYOFF = "SELECT l from KrcdtHdWorkMng l"
			+ " WHERE l.sID = :sId"
			+ " AND l.dayOff IN :dayOffs";
	
 	@Override
	public Integer getDeadlineCompensatoryLeaveCom(String sID, GeneralDate currentDay, int deadlMonth) {
		return (Integer) this.getEntityManager()
				.createNativeQuery(QUERY_DEADLINE_COMPENSATORY)
				.setParameter("sID", sID)
				.setParameter("currentMonth", currentDay.month())
				.setParameter("currentYear", currentDay.year())
				.setParameter("deadlMonth", deadlMonth)
				.setParameter("subHDAtr", 0)
				.getSingleResult();
	}

	@Override
	public List<LeaveManagementData> getBySidDate(String cid, String sid, GeneralDate ymd) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy().query(QUERY_BY_SID_DATE, KrcdtHdWorkMng.class)
				.setParameter("cid", cid)
				.setParameter("employeeId", sid)
				.setParameter("dayOff", ymd)
				.getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<LeaveManagementData> getBySidWithsubHDAtr(String cid, String sid, int state) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy()
				.query(QUERY_BYSIDWITHSUBHDATR, KrcdtHdWorkMng.class).setParameter("cid", cid)
				.setParameter("employeeId", sid).setParameter("subHDAtr", state).getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<LeaveManagementData> getBySid(String cid, String sid) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy().query(QUERY_BYSID, KrcdtHdWorkMng.class)
				.setParameter("cid", cid).setParameter("employeeId", sid).getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	/**
	 * Convert to domain
	 * 
	 * @param entity
	 * @return
	 */
	private LeaveManagementData toDomain(KrcdtHdWorkMng entity) {
		return new LeaveManagementData(entity.leaveID, entity.cID, entity.sID, entity.unknownDate, entity.dayOff,
				entity.expiredDate, entity.occurredDays, entity.occurredTimes, entity.unUsedDays, entity.unUsedTimes,
				entity.subHDAtr, entity.fullDayTime, entity.halfDayTime);
	}

	private KrcdtHdWorkMng toEntity(LeaveManagementData domain) {
		KrcdtHdWorkMng entity = new KrcdtHdWorkMng();
		entity.leaveID = domain.getID();
		entity.cID = domain.getCID();
		entity.sID = domain.getSID();
		entity.unknownDate = domain.getComDayOffDate().isUnknownDate();
		entity.dayOff = domain.getComDayOffDate().getDayoffDate().isPresent()
				? domain.getComDayOffDate().getDayoffDate().get() : null;
		entity.expiredDate = domain.getExpiredDate();
		entity.occurredDays = domain.getOccurredDays().v();
		entity.occurredTimes = domain.getOccurredTimes().v();
		entity.unUsedDays = domain.getUnUsedDays().v();
		entity.unUsedTimes = domain.getUnUsedTimes().v();
		entity.subHDAtr = domain.getSubHDAtr().value;
		entity.fullDayTime = domain.getFullDayTime().v();
		entity.halfDayTime = domain.getHalfDayTime().v();
		return entity;
	}

	@Override
	public void create(LeaveManagementData domain) {
		this.commandProxy().insert(toEntity(domain));
	}

	@Override
	public List<LeaveManagementData> getByDateCondition(String cid, String sid, GeneralDate startDate,
			GeneralDate endDate) {
		List<KrcdtHdWorkMng> listLeaveData = new ArrayList<>();
		String query = "";
		if (!Objects.isNull(startDate) && !Objects.isNull(endDate)) {
			query = "SELECT a FROM KrcdtHdWorkMng a WHERE a.cID = :cid AND a.sID =:employeeId AND a.dayOff >= :startDate AND a.dayOff <= :endDate";
			listLeaveData = this.queryProxy().query(query, KrcdtHdWorkMng.class).setParameter("cid", cid)
					.setParameter("employeeId", sid).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).getList();
		} else if (!Objects.isNull(startDate)) {
			query = "SELECT a FROM KrcdtHdWorkMng a WHERE a.cID = :cid AND a.sID =:employeeId AND a.dayOff >= :startDate";
			listLeaveData = this.queryProxy().query(query, KrcdtHdWorkMng.class).setParameter("cid", cid)
					.setParameter("employeeId", sid).setParameter("startDate", startDate).getList();
		} else if (!Objects.isNull(endDate)) {
			query = "SELECT a FROM KrcdtHdWorkMng a WHERE a.cID = :cid AND a.sID =:employeeId AND a.dayOff <= :endDate";
			listLeaveData = this.queryProxy().query(query, KrcdtHdWorkMng.class).setParameter("cid", cid)
					.setParameter("employeeId", sid).setParameter("endDate", endDate).getList();
		} else {
			query = "SELECT a FROM KrcdtHdWorkMng a WHERE a.cID = :cid AND a.sID =:employeeId ";
			listLeaveData = this.queryProxy().query(query, KrcdtHdWorkMng.class).setParameter("cid", cid)
					.setParameter("employeeId", sid).getList();
		}
		return listLeaveData.stream().map(x -> toDomain(x)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.remainingnumber.subhdmana.
	 * LeaveManaDataRepository#getBySidWithHolidayDate(java.lang.String,
	 * java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<LeaveManagementData> getBySidWithHolidayDate(String cid, String sid, GeneralDate dateHoliday) {
		List<KrcdtHdWorkMng> listLeaveData = this.queryProxy()
				.query(QUERY_BYSIDANDHOLIDAYDATECONDITION, KrcdtHdWorkMng.class).setParameter("cid", cid)
				.setParameter("employeeId", sid).setParameter("dateHoliday", dateHoliday).getList();
		return listLeaveData.stream().map(x -> toDomain(x)).collect(Collectors.toList());
	}
	
	@Override
	public List<LeaveManagementData> getByHoliday(String sid, Boolean unknownDate, DatePeriod dayOff) {
		List<KrcdtHdWorkMng> listLeaveData = this.queryProxy()
				.query(QUERY_BY_SID_HOLIDAY, KrcdtHdWorkMng.class)
				.setParameter("employeeId", sid)
				.setParameter("unknownDate", unknownDate)
				.setParameter("startDate", dayOff.start()).getList();
		return listLeaveData.stream().map(x -> toDomain(x)).collect(Collectors.toList());
	}

	@Override
	public List<LeaveManagementData> getBySidNotUnUsed(String cid, String sid) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy()
				.query(QUERY_BYSID_AND_NOT_UNUSED, KrcdtHdWorkMng.class).setParameter("cid", cid)
				.setParameter("employeeId", sid).setParameter("subHDAtr", 0).getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<LeaveManagementData> getByComDayOffId(String cid, String sid, String comDayOffID) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy().query(QUERY_LEAVEDAYOFF, KrcdtHdWorkMng.class)
				.setParameter("cid", cid).setParameter("employeeId", sid).setParameter("comDayOffID", comDayOffID)
				.getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public void updateByLeaveIds(List<String> leaveIds) {
		List<KrcdtHdWorkMng> listListMana = new ArrayList<>();
		CollectionUtil.split(leaveIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			listListMana.addAll(this.queryProxy().query(QUERY_BYID, KrcdtHdWorkMng.class)
									.setParameter("leaveIDs", subList)
									.getList());
		});
		for (KrcdtHdWorkMng busItem : listListMana) {
			busItem.subHDAtr = DigestionAtr.USED.value;
			busItem.unUsedDays = 0.0;
		}
		this.commandProxy().updateAll(listListMana);
	}

	@Override
	public void updateSubByLeaveId(String leaveId, Boolean check) {
		KrcdtHdWorkMng entity = this.getEntityManager().find(KrcdtHdWorkMng.class, leaveId);
		if (check) {
			entity.subHDAtr = DigestionAtr.UNUSED.value;
			entity.unUsedDays = entity.occurredDays;
		} else {
			entity.subHDAtr = DigestionAtr.UNUSED.value;
			entity.unUsedDays = 0.5;
		}
		this.commandProxy().update(entity);
	}

	@Override
	public void updateUnUseDayLeaveId(String leaveId, Double unUsedDay, List<DaysOffMana> daysOffMana) {
		KrcdtHdWorkMng leaveMana = this.getEntityManager().find(KrcdtHdWorkMng.class, leaveId);
		if ((unUsedDay < leaveMana.occurredDays && unUsedDay != 0.0) || daysOffMana.isEmpty()) {
			leaveMana.unUsedDays = unUsedDay;
			leaveMana.subHDAtr = 0;
		} else {
			leaveMana.unUsedDays = unUsedDay;
			leaveMana.subHDAtr = 1;
		}
		this.commandProxy().update(leaveMana);
	}

	public Optional<LeaveManagementData> getByLeaveId(String leaveManaId) {
		String QUERY_BY_ID = "SELECT s FROM KrcdtHdWorkMng s WHERE s.leaveID = :leaveID";
		Optional<KrcdtHdWorkMng> entity = this.queryProxy().query(QUERY_BY_ID, KrcdtHdWorkMng.class).setParameter("leaveID", leaveManaId).getSingle();
		if (entity.isPresent()) {
			return Optional.ofNullable(toDomain(entity.get()));
		}
		return Optional.empty();
	}

	@Override
	public List<LeaveManagementData> getListByLeaveId(List<String> leaveIDs) {
		String QUERY_BY_ID = "SELECT s FROM KrcdtHdWorkMng s WHERE s.leaveID IN :leaveIDs";
		return this.queryProxy().query(QUERY_BY_ID, KrcdtHdWorkMng.class).setParameter("leaveIDs", leaveIDs)
				.getList(x -> toDomain(x));
	}

	@Override
	public void udpateByHolidaySetting(String leaveId, Boolean isCheckedExpired, GeneralDate expiredDate,
			double occurredDays, double unUsedDays) {
		KrcdtHdWorkMng entity = this.getEntityManager().find(KrcdtHdWorkMng.class, leaveId);
		if (Objects.isNull(entity)) {
			throw new BusinessException("Msg_198");
		}
		if (!entity.unknownDate) {
			entity.subHDAtr = isCheckedExpired ? 2 : entity.subHDAtr;
		}
		entity.expiredDate = expiredDate;
		entity.occurredDays = occurredDays;
		entity.unUsedDays = unUsedDays;
		this.commandProxy().update(entity);
	}

	@Override
	public void deleteByLeaveId(String leaveId) {
		KrcdtHdWorkMng entity = this.getEntityManager().find(KrcdtHdWorkMng.class, leaveId);
		if (Objects.isNull(entity)) {
			throw new BusinessException("Msg_198");
		}
		this.commandProxy().remove(entity);
	}
	
	@Override
	public void deleteAllByEmployeeId(String employeeId) {
		String jpql = "DELETE FROM KrcdtHdWorkMng a WHERE a.sID = :sid";
		this.getEntityManager().createQuery(jpql).setParameter("sid", employeeId).executeUpdate();
	}

	@Override
	public List<LeaveManagementData> getByDayOffDatePeriod(String sid, DatePeriod dateData) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy()
				.query(QUERY_BY_DAYOFF_PERIOD, KrcdtHdWorkMng.class).setParameter("sid", sid)
				.setParameter("startDate", dateData.start()).setParameter("endDate", dateData.end()).getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<LeaveManagementData> getByExtinctionPeriod(String sid, DatePeriod tmpDateData, DatePeriod dateData,
			double unUseDays, DigestionAtr subHDAtr) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy().query(QUERY_BY_EX, KrcdtHdWorkMng.class)
				.setParameter("sid", sid).setParameter("startDate", tmpDateData.start())
				.setParameter("endDate", tmpDateData.end()).setParameter("unUsedDays", unUseDays)
				.setParameter("sDate", dateData.start()).setParameter("eDate", dateData.end())
				.setParameter("subHDAtr", subHDAtr.value).getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public void update(LeaveManagementData domain) {
		this.commandProxy().update(toEntity(domain));
	}
	
	@Override
	public void deleteById(List<String> leaveId) {
		this.commandProxy().removeAll(KrcdtHdWorkMng.class, leaveId);
	}

	@Override
	public List<LeaveManagementData> getBySidYmd(String cid, String sid, GeneralDate ymd, DigestionAtr state) {
		List<KrcdtHdWorkMng> listListMana = this.queryProxy().query(QUERY_BYSIDYMD, KrcdtHdWorkMng.class)
				.setParameter("cid", cid)
				.setParameter("employeeId", sid)
				.setParameter("dayOff", ymd)
				.setParameter("subHDAtr", state.value)
				.getList();
		return listListMana.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public Map <String ,Double> getAllBySidWithsubHDAtr(String cid, List<String> sids, int state) {
		Map <String ,Double> result = new HashMap<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HD_WORK_MNG WHERE  CID = ? AND SUB_HD_ATR = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				stmt.setInt(2, state);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(3 + i, subList.get(i));
				}
				List<KrcdtHdWorkMng> data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtHdWorkMng entity = new KrcdtHdWorkMng();
					entity.leaveID = rec.getString("LEAVE_MANA_ID");
					entity.cID = rec.getString("CID");
					entity.sID = rec.getString("SID");
					entity.unUsedDays = rec.getDouble("UNUSED_DAYS");
					return entity;
			
				});
				Map<String, List<KrcdtHdWorkMng>> dataMap = data.parallelStream().collect(Collectors.groupingBy(c -> c.sID));
				dataMap.entrySet().parallelStream().forEach(c ->{
					result.put(c.getKey(), c.getValue().parallelStream().mapToDouble(i -> i.unUsedDays).sum());
				} );
			
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
		
	}

	@Override
	public List<LeaveManagementData> getBySidsAndCid(String cid, List<String> sids) {
		List<LeaveManagementData> result = new ArrayList<>();
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HD_WORK_MNG WHERE  CID = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<LeaveManagementData> data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtHdWorkMng entity = new KrcdtHdWorkMng();
					entity.leaveID = rec.getString("LEAVE_MANA_ID");
					entity.cID = rec.getString("CID");
					entity.sID = rec.getString("SID");
					entity.unknownDate = rec.getBoolean("UNKNOWN_DATE");
					
					entity.dayOff = rec.getGeneralDate("DAYOFF_DATE");
					entity.expiredDate = rec.getGeneralDate("EXPIRED_DATE");
					entity.occurredDays = rec.getDouble("OCCURRED_DAYS");
					entity.occurredTimes = rec.getInt("OCCURRED_TIMES");
					
					entity.unUsedDays = rec.getDouble("UNUSED_DAYS");
					entity.unUsedTimes = rec.getInt("UNUSED_TIMES");
					entity.subHDAtr = rec.getInt("SUB_HD_ATR");
					entity.fullDayTime = rec.getInt("FULL_DAY_TIME");
					entity.halfDayTime = rec.getInt("HALF_DAY_TIME");
					entity.disapearDate = rec.getGeneralDate("DISAPEAR_DATE");
					
					return toDomain(entity);
				});
				result.addAll(data);
			
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override
	public void addAll(List<LeaveManagementData> domains) {
		String INS_SQL = "INSERT INTO KRCDT_HD_WORK_MNG (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " LEAVE_MANA_ID, CONTRACT_CD, CID, SID, UNKNOWN_DATE, DAYOFF_DATE, EXPIRED_DATE, OCCURRED_DAYS, OCCURRED_TIMES,"
				+ " UNUSED_DAYS, UNUSED_TIMES, SUB_HD_ATR, FULL_DAY_TIME, HALF_DAY_TIME, DISAPEAR_DATE )"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " LEAVE_MANA_ID_VAL, CONTRACT_CD_VAL, CID_VAL, SID_VAL, UNKNOWN_DATE_VAL, DAYOFF_DATE_VAL, EXPIRED_DATE_VAL, OCCURRED_DAYS_VAL, OCCURRED_TIMES_VAL,"
				+ " UNUSED_DAYS_VAL, UNUSED_TIMES_VAL, SUB_HD_ATR_VAL, FULL_DAY_TIME_VAL, HALF_DAY_TIME_VAL, DATE_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		String contractCd = AppContexts.user().contractCode();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
		boolean isPostgreSQL = this.database().is(DatabaseProduct.POSTGRESQL);
		domains.stream().forEach(c -> {
			String sql = INS_SQL;
			sql = sql.replace("INS_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("INS_CCD_VAL", "'" + insCcd + "'");
			sql = sql.replace("INS_SCD_VAL", "'" + insScd + "'");
			sql = sql.replace("INS_PG_VAL", "'" + insPg + "'");

			sql = sql.replace("UPD_DATE_VAL", "'" + GeneralDateTime.now() + "'");
			sql = sql.replace("UPD_CCD_VAL", "'" + updCcd + "'");
			sql = sql.replace("UPD_SCD_VAL", "'" + updScd + "'");
			sql = sql.replace("UPD_PG_VAL", "'" + updPg + "'");

			sql = sql.replace("LEAVE_MANA_ID_VAL", "'" + c.getID() + "'");
			sql = sql.replace("CONTRACT_CD_VAL", "'" + contractCd + "'");
			sql = sql.replace("CID_VAL", "'" + c.getCID()+ "'");
			sql = sql.replace("SID_VAL", "'" + c.getSID()+ "'");
			
			if (isPostgreSQL) {
				sql = sql.replace("UNKNOWN_DATE_VAL", "" + c.getComDayOffDate().isUnknownDate());
			} else {
	            sql = sql.replace("UNKNOWN_DATE_VAL", c.getComDayOffDate().isUnknownDate() == true? "1":"0");
			}
			sql = sql.replace("DAYOFF_DATE_VAL", c.getComDayOffDate().getDayoffDate().isPresent()
					? "'" + c.getComDayOffDate().getDayoffDate().get() +"'" : "null");
			sql = sql.replace("EXPIRED_DATE_VAL", c.getExpiredDate() == null? "null" : "'" + c.getExpiredDate() + "'");
			
			sql = sql.replace("OCCURRED_DAYS_VAL", c.getOccurredDays() == null?  "null": "" + c.getOccurredDays().v() + "");
			sql = sql.replace("OCCURRED_TIMES_VAL", ""+ c.getOccurredTimes().v() + "");
			
			
			sql = sql.replace("UNUSED_DAYS_VAL", "" + c.getUnUsedDays().v() +"");
			sql = sql.replace("UNUSED_TIMES_VAL", "" + c.getUnUsedTimes().v() +"");
			sql = sql.replace("SUB_HD_ATR_VAL", "" + c.getSubHDAtr().value +"");
			sql = sql.replace("FULL_DAY_TIME_VAL", "" + c.getFullDayTime().v() +"");
			sql = sql.replace("HALF_DAY_TIME_VAL", "" + c.getHalfDayTime().v() +"");
			sql = sql.replace("DATE_VAL",  c.getDisapearDate() == null? "null": (c.getDisapearDate().isPresent()? "'" + c.getDisapearDate().get() +"'": "null"));
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}
	
	@Override
	public List<LeaveManagementData> getAllData() {
		List<KrcdtHdWorkMng> allData = this.queryProxy()
				.query(QUERY_ALL_DATA, KrcdtHdWorkMng.class)
				.getList();
		
		return allData.stream()
				.map(i -> toDomain(i))
				.collect(Collectors.toList());
	}
	
	/**
	 * ドメイン「休出管理データ」を取得する
	 * @param cid the company Id
	 * @param sid 社員ID
	 * @param state 消化区分
	 * @return List leave management data
	 */
	@Override
	public List<LeaveManagementData> getBySidAndStateAtr(String cid, String sid, DigestionAtr state) {
		return this.queryProxy().query(QUERY_BY_SID_STATE, KrcdtHdWorkMng.class)
				.setParameter("cId", cid)
				.setParameter("sId", sid)
				.setParameter("subHDAtr", state.value)
				.getList()
				.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}
	
	/**
	 * ドメインモデル「休出管理データ」を取得
	 * @param sid 社員ID
	 * @param dayOffs 振出データID
	 * @return List<LeaveManagementData> List<休出管理データ＞
	 */
	@Override
	public List<LeaveManagementData> getBySidAndDatOff(String sid, List<GeneralDate> dayOffs) {
		return this.queryProxy().query(QUERY_BY_SID_DAYOFF, KrcdtHdWorkMng.class)
				.setParameter("sId", sid)
				.setParameter("dayOffs", dayOffs)
				.getList()
				.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<LeaveManagementData> getListByIdAndUnUse(String cid, String sid, GeneralDate expiredDate, double unUse) {
		String QUERY = "SELECT l FROM KrcdtHdWorkMng l"
				+ " WHERE l.cID = :cid"
				+ " AND l.sID = :sid"
				+ " AND l.expiredDate >= :expiredDate"
				+ " AND l.unUsedDays >= :unUse"
				+ " ORDER BY l.dayOff ASC";
		return this.queryProxy().query(QUERY, KrcdtHdWorkMng.class)
				.setParameter("cid", cid)
				.setParameter("sid", sid)
				.setParameter("expiredDate", expiredDate)
				.setParameter("unUse", unUse)
				.getList(entity -> toDomain(entity));
	}	
}
