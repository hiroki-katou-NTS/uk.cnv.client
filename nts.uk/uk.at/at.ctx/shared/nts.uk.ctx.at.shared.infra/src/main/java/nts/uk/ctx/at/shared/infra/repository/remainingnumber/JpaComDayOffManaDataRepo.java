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
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.subhdmana.KrcdtHdComMng;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.subhdmana.KrcdtHdWorkMng;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class JpaComDayOffManaDataRepo extends JpaRepository implements ComDayOffManaDataRepository {

	private static final String GET_BYSID = "SELECT a FROM KrcdtHdComMng a WHERE a.sID = :employeeId AND a.cID = :cid";
	
	private String GET_BY_SID_DATE = GET_BYSID + " AND a.dayOff < :dayOff";

	private static final String GET_BY_REDAY = String.join(" ", GET_BYSID, " AND a.remainDays <> 0");

	private static final String GET_BYSID_WITHREDAY = String.join(" ", GET_BYSID, " AND a.remainDays > 0");

	private static final String QUERY_BY_SID_HOLIDAY = "SELECT c FROM KrcdtHdComMng c"
			+ " WHERE c.sID = :employeeId" + " AND c.unknownDate = :unknownDate" + " AND c.dayOff >= :startDate";
	private static final String GET_BYCOMDAYOFFID = String.join(" ", GET_BYSID,
			" AND a.comDayOffID = :leaveID");

	private static final String GET_BYSID_BY_HOLIDAYDATECONDITION = "SELECT c FROM KrcdtHdComMng c WHERE c.sID = :employeeId AND c.cID = :cid AND c.dayOff = :dateSubHoliday";

	private static final String GET_BY_LISTID = " SELECT c FROM KrcdtHdComMng c WHERE c.comDayOffID IN :comDayOffIDs";

	private static final String GET_BY_DAYOFFDATE_PERIOD = "SELECT c FROM KrcdtHdComMng c"
			+ " WHERE c.dayOff >= :startDate" + " AND c.dayOff <= :endDate" + " AND c.sID = :sid";
	private String GET_BY_YMD_UNOFFSET = "SELECT a FROM KrcdtHdComMng a"
			+ " WHERE  a.cID = :cid"
			+ " AND a.sID = :employeeId"
			+ " AND (a.dayOff < :dayOff OR a.dayOff is null)"
			+ " AND (a.remainDays > 0 OR a.remainTimes > 0)";
	private static final String GET_ALL_DATA = "SELECT a FROM KrcdtHdComMng a";
	
	private static final String GET_BY_LST_DAYOFF_DATE = "SELECT a FROM KrcdtHdComMng a"
			+ " WHERE a.cID = :cId"
			+ " AND a.dayOff IN :lstDate";
	
	private static final String SELECT_BY_SID_AND_DAY_OFF = "SELECT t FROM KrcdtHdComMng t "
			+ "WHERE t.sID = :sid "
			+ "AND t.dayOff = :date";
	
	@Override
	public List<CompensatoryDayOffManaData> getBySidDate(String cid, String sid, GeneralDate ymd) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BY_SID_DATE, KrcdtHdComMng.class)
				.setParameter("employeeId", sid)
				.setParameter("cid", cid)
				.setParameter("dayOff", ymd)
				.getList();

		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}
	
	@Override
	public List<CompensatoryDayOffManaData> getBySidWithReDay(String cid, String sid) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BYSID_WITHREDAY, KrcdtHdComMng.class)
				.setParameter("employeeId", sid).setParameter("cid", cid).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<CompensatoryDayOffManaData> getBySid(String cid, String sid) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BYSID, KrcdtHdComMng.class)
				.setParameter("employeeId", sid).setParameter("cid", cid).getList();

		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public void update(CompensatoryDayOffManaData domain) {
		this.commandProxy().update(toEnitty(domain));
	}

	@Override
	public void deleteByComDayOffId(String comDayOffID) {
		KrcdtHdComMng entity = this.getEntityManager().find(KrcdtHdComMng.class, comDayOffID);
		if (Objects.isNull(entity)) {
			throw new BusinessException("Msg_198");
		}
		this.commandProxy().remove(entity);
	}
	
	@Override
	public void deleteAllByEmployeeId(String employeeId) {
		String jpql = "DELETE FROM KrcdtHdComMng a WHERE a.sID = :sid";
		this.getEntityManager().createQuery(jpql).setParameter("sid", employeeId).executeUpdate();
	}

	/**
	 * Convert to domain
	 * 
	 * @param entity
	 * @return
	 */
	private CompensatoryDayOffManaData toDomain(KrcdtHdComMng entity) {
		return new CompensatoryDayOffManaData(entity.comDayOffID, entity.cID, entity.sID, entity.unknownDate,
				entity.dayOff, entity.requiredDays, entity.requiredTimes, entity.remainDays, entity.remainTimes);
	}

	private KrcdtHdComMng toEnitty(CompensatoryDayOffManaData domain) {
		KrcdtHdComMng entity = new KrcdtHdComMng();
		entity.comDayOffID = domain.getComDayOffID();
		entity.cID = domain.getCID();
		entity.sID = domain.getSID();
		entity.unknownDate = domain.getDayOffDate().isUnknownDate();
		entity.dayOff = domain.getDayOffDate().getDayoffDate().isPresent()
				? domain.getDayOffDate().getDayoffDate().get() : null;
		entity.requiredDays = domain.getRequireDays().v();
		entity.requiredTimes = domain.getRequiredTimes().v();
		entity.remainDays = domain.getRemainDays().v();
		entity.remainTimes = domain.getRemainTimes().v();
		return entity;
	}

	@Override
	public void create(CompensatoryDayOffManaData domain) {
		this.commandProxy().insert(toEnitty(domain));
	}

	@Override
	public List<CompensatoryDayOffManaData> getBySidComDayOffIdWithReDay(String cid, String sid, String leaveId) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BYCOMDAYOFFID, KrcdtHdComMng.class)
				.setParameter("employeeId", sid).setParameter("cid", cid).setParameter("leaveID", leaveId).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	public List<CompensatoryDayOffManaData> getByDateCondition(String cid, String sid, GeneralDate startDate,
			GeneralDate endDate) {
		String query = "";
		List<KrcdtHdComMng> list = new ArrayList<>();
		if (!Objects.isNull(startDate) && !Objects.isNull(endDate)) {
			query = "SELECT a FROM KrcdtHdComMng a WHERE a.cID = :cid AND"
					+ " a.sID =:employeeId AND a.dayOff >= :startDate AND a.dayOff <= :endDate";
			list = this.queryProxy().query(query, KrcdtHdComMng.class).setParameter("employeeId", sid)
					.setParameter("cid", cid).setParameter("startDate", startDate).setParameter("endDate", endDate)
					.getList();
		} else if (!Objects.isNull(startDate)) {
			query = "SELECT a FROM KrcdtHdComMng a WHERE a.cID = :cid AND"
					+ " a.sID =:employeeId AND a.dayOff >= :startDate";
			list = this.queryProxy().query(query, KrcdtHdComMng.class).setParameter("employeeId", sid)
					.setParameter("cid", cid).setParameter("startDate", startDate).getList();
		} else if (!Objects.isNull(endDate)) {
			query = "SELECT a FROM KrcdtHdComMng a WHERE a.cID = :cid AND"
					+ " a.sID =:employeeId AND a.dayOff <= :endDate";
			list = this.queryProxy().query(query, KrcdtHdComMng.class).setParameter("employeeId", sid)
					.setParameter("cid", cid).setParameter("endDate", endDate).getList();
		} else {
			query = "SELECT a FROM KrcdtHdComMng a WHERE a.cID = :cid AND" + " a.sID =:employeeId";
			list = this.queryProxy().query(query, KrcdtHdComMng.class).setParameter("employeeId", sid)
					.setParameter("cid", cid).getList();
		}
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.at.record.dom.remainingnumber.subhdmana.
	 * ComDayOffManaDataRepository#getBySidWithHolidayDateCondition(java.lang.
	 * String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<CompensatoryDayOffManaData> getBySidWithHolidayDateCondition(String cid, String sid,
			GeneralDate dateSubHoliday) {
		List<KrcdtHdComMng> list = this.queryProxy()
				.query(GET_BYSID_BY_HOLIDAYDATECONDITION, KrcdtHdComMng.class).setParameter("employeeId", sid)
				.setParameter("cid", cid).setParameter("dateSubHoliday", dateSubHoliday).getList();

		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public void updateReDayByComDayId(List<String> comDayIds) {
		List<KrcdtHdComMng> KrcdtHdComMng = new ArrayList<>();
		CollectionUtil.split(comDayIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			KrcdtHdComMng.addAll(this.queryProxy()
				.query(GET_BY_LISTID, KrcdtHdComMng.class)
				.setParameter("comDayOffIDs", subList)
				.getList());
		});
		for (KrcdtHdComMng busItem : KrcdtHdComMng) {
			busItem.remainDays = new Double(0);
		}
		this.commandProxy().updateAll(KrcdtHdComMng);
	}

	@Override
	public void updateReDayReqByComDayId(String comDayId, Boolean check) {
		KrcdtHdComMng comDayOff = this.getEntityManager().find(KrcdtHdComMng.class, comDayId);
		if (check) {
			comDayOff.remainDays = comDayOff.requiredDays;
		} else {
			comDayOff.remainDays = 0.5;
		}
		this.commandProxy().update(comDayOff);
	}

	@Override
	public List<CompensatoryDayOffManaData> getByReDay(String cid, String sid) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BY_REDAY, KrcdtHdComMng.class)
				.setParameter("employeeId", sid).setParameter("cid", cid).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public Optional<CompensatoryDayOffManaData> getBycomdayOffId(String comDayOffId) {

		String QUERY_BY_ID = "SELECT s FROM KrcdtHdComMng s WHERE s.comDayOffID = :comDayOffID";
		Optional<KrcdtHdComMng> entity = this.queryProxy().query(QUERY_BY_ID, KrcdtHdComMng.class).setParameter("comDayOffID", comDayOffId).getSingle();
		if (entity.isPresent()) {
			return Optional.ofNullable(toDomain(entity.get()));
		}
		return Optional.empty();
	}

	@Override
	public void updateRemainDay(String comDayOffID, Double remainDay) {
		KrcdtHdComMng comDayOff = this.getEntityManager().find(KrcdtHdComMng.class, comDayOffID);
		comDayOff.remainDays = remainDay;
		this.commandProxy().update(comDayOff);
	}

	@Override
	public List<CompensatoryDayOffManaData> getByDayOffDatePeriod(String sid, DatePeriod dateData) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BY_DAYOFFDATE_PERIOD, KrcdtHdComMng.class)
				.setParameter("sid", sid).setParameter("startDate", dateData.start())
				.setParameter("endDate", dateData.end()).getList();
		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	@Override
	public List<CompensatoryDayOffManaData> getByHoliday(String sid, Boolean unknownDate, DatePeriod dayOff) {
		List<KrcdtHdComMng> listLeaveData = this.queryProxy()
				.query(QUERY_BY_SID_HOLIDAY, KrcdtHdComMng.class)
				.setParameter("employeeId", sid)
				.setParameter("unknownDate", unknownDate)
				.setParameter("startDate", dayOff.start()).getList();
		return listLeaveData.stream().map(x -> toDomain(x)).collect(Collectors.toList());
	}

	@Override
	public void deleteById(List<String> comDayOffID) {
		this.commandProxy().removeAll(KrcdtHdComMng.class, comDayOffID);
	}

	@Override
	public List<CompensatoryDayOffManaData> getBySidYmd(String cid, String sid, GeneralDate ymd) {
		List<KrcdtHdComMng> list = this.queryProxy().query(GET_BY_YMD_UNOFFSET, KrcdtHdComMng.class)
				.setParameter("employeeId", sid)
				.setParameter("cid", cid)
				.setParameter("dayOff", ymd)
				.getList();

		return list.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository#getAllBySidWithReDay(java.lang.String, java.util.List)
	 */
	@Override
	public Map<String, Double> getAllBySidWithReDay(String cid, List<String> sid) {
		Map <String ,Double> result = new HashMap<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HD_COM_MNG WHERE  CID = ? AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<KrcdtHdComMng> data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtHdComMng entity = new KrcdtHdComMng();
					entity.cID = rec.getString("CID");
					entity.sID = rec.getString("SID");
					entity.remainDays = rec.getDouble("REMAIN_DAYS");
					return entity;
			
				});
				Map<String, List<KrcdtHdComMng>> dataMap = data.stream().collect(Collectors.groupingBy(c -> c.sID));
				dataMap.entrySet().stream().forEach(c ->{
					result.put(c.getKey(), c.getValue().stream().mapToDouble(i -> i.remainDays).sum());
				});
			
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		return result;
	}

	@Override	
	public List<CompensatoryDayOffManaData> getBySidsAndCid(String cid, List<String> sid) {
		List<CompensatoryDayOffManaData> result = new ArrayList<>();
		CollectionUtil.split(sid, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HD_COM_MNG WHERE  CID = ? AND REMAIN_DAYS > 0   AND SID IN ("
					+ NtsStatement.In.createParamsString(subList) + ")";
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString(1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString(2 + i, subList.get(i));
				}
				List<CompensatoryDayOffManaData> data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
					KrcdtHdComMng entity = new KrcdtHdComMng();
					entity.comDayOffID =  rec.getString("COM_DAYOFF_ID");
					entity.cID = rec.getString("CID");
					entity.sID = rec.getString("SID");
					entity.unknownDate = rec.getBoolean("UNKNOWN_DATE");
					entity.dayOff = rec.getGeneralDate("DAYOFF_DATE");
					entity.requiredDays = rec.getDouble("REQUIRED_DAYS");
					entity.requiredTimes = rec.getInt("REQUIRED_TIMES");
					entity.remainDays = rec.getDouble("REMAIN_DAYS");
					entity.remainTimes = rec.getInt("REMAIN_TIMES");
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
	public void addAll(List<CompensatoryDayOffManaData> domains) {
		String INS_SQL = "INSERT INTO KRCDT_HD_COM_MNG (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG," 
				+ " COM_DAYOFF_ID, CID, SID, UNKNOWN_DATE, DAYOFF_DATE, REQUIRED_DAYS, REQUIRED_TIMES, REMAIN_DAYS, REMAIN_TIMES)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " COM_DAYOFF_ID_VAL, CID_VAL, SID_VAL, UNKNOWN_DATE_VAL, DAYOFF_DATE_VAL, REQUIRED_DAYS_VAL, REQUIRED_TIMES_VAL, REMAIN_DAYS_VAL, REMAIN_TIMES_VAL); ";
		String insCcd = AppContexts.user().companyCode();
		String insScd = AppContexts.user().employeeCode();
		String insPg = AppContexts.programId();
		
		String updCcd = insCcd;
		String updScd = insScd;
		String updPg = insPg;
		StringBuilder sb = new StringBuilder();
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

			sql = sql.replace("COM_DAYOFF_ID_VAL", "'"+ c.getComDayOffID() + "'");
			sql = sql.replace("CID_VAL", "'" + c.getCID() + "'");
			sql = sql.replace("SID_VAL", "'" + c.getSID()+ "'");
			
			sql = sql.replace("UNKNOWN_DATE_VAL", c.getDayOffDate().isUnknownDate() == true? "1" : "0");
			sql = sql.replace("DAYOFF_DATE_VAL",  c.getDayOffDate().getDayoffDate().isPresent()
					? "'"+ c.getDayOffDate().getDayoffDate().get()+ "'" : "null");
			
			sql = sql.replace("REQUIRED_DAYS_VAL", c.getRequireDays() == null? "null": ""+ c.getRequireDays().v()+ "");
			sql = sql.replace("REQUIRED_TIMES_VAL", "" + c.getRequiredTimes().v()+ "");
			
			sql = sql.replace("REMAIN_DAYS_VAL", c.getRemainDays() == null? "null": ""+ c.getRemainDays().v()+ "");
			sql = sql.replace("REMAIN_TIMES_VAL", "" + c.getRemainTimes().v()  +"");
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
		
	
	}
	
	@Override
	public List<CompensatoryDayOffManaData> getAllData() {
		List<KrcdtHdComMng> allData = this.queryProxy().query(GET_ALL_DATA, KrcdtHdComMng.class)
				.getList();

		return allData.stream().map(i -> toDomain(i)).collect(Collectors.toList());
	}
	
	/**
	 * Get data by list dayoff date
	 * @param cid
	 * @param lstDate
	 * @return
	 */
	@Override
	public List<CompensatoryDayOffManaData> getByLstDate(String cid, List<GeneralDate> lstDate) {
		return this.queryProxy().query(GET_BY_LST_DAYOFF_DATE, KrcdtHdComMng.class)
				.setParameter("cId", cid)
				.setParameter("lstDate", lstDate)
				.getList()
				.stream()
				.map(x -> toDomain(x))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CompensatoryDayOffManaData> getListComdayOffId(List<String> comDayOffId) {
		String QUERY_BY_ID = "SELECT s FROM KrcdtHdComMng s WHERE s.comDayOffID IN :comDayOffID";
		return this.queryProxy().query(QUERY_BY_ID, KrcdtHdComMng.class).setParameter("comDayOffID", comDayOffId)
				.getList(x -> toDomain(x));
	}
	
	@Override
	public Optional<CompensatoryDayOffManaData> findBySidAndDate(String sid, GeneralDate date) {
		return this.queryProxy().query(SELECT_BY_SID_AND_DAY_OFF, KrcdtHdComMng.class)
				.setParameter("sid", sid).setParameter("date", date)
				.getSingle(this::toDomain);
	}

	@Override
	public void deleteAfter(String sid, boolean unknownDateFlag, GeneralDate target) {
		this.getEntityManager().createQuery("DELETE FROM KrcdtHdComMng d WHERE d.sID = :sid "
				+ " AND d.unknownDate = :unknownDate AND d.dayOff >= :targetDate", KrcdtHdWorkMng.class)
		.setParameter("sid", sid)
		.setParameter("unknownDate", unknownDateFlag)
		.setParameter("targetDate", target)
		.executeUpdate();
	}

}
