package nts.uk.ctx.at.record.infra.repository.breakorgoout;

import java.sql.Connection;
import java.sql.Date;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.breakorgoout.BreakTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.BreakTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.infra.entity.breakorgoout.KrcdtDaiBreakTime;
import nts.uk.ctx.at.record.infra.entity.breakorgoout.KrcdtDaiBreakTimePK;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakType;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.infra.data.jdbc.JDBCUtil;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaBreakTimeOfDailyPerformanceRepository extends JpaRepository
		implements BreakTimeOfDailyPerformanceRepository {
	
	private static final String DEL_BY_LIST_KEY;

	private static final String SELECT_BY_EMPLOYEE_AND_DATE;

//	private static final String FIND;

	private static final String REMOVE_BY_BREAKTYPE;

	static {
		StringBuilder builderString = new StringBuilder();
//		builderString.append("DELETE ");
//		builderString.append("FROM KrcdtDaiBreakTime a ");
//		builderString.append("WHERE a.krcdtDaiBreakTimePK.employeeId = :employeeId ");
//		builderString.append("AND a.krcdtDaiBreakTimePK.ymd = :ymd ");
//		REMOVE_BY_EMPLOYEE = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcdtDaiBreakTime a ");
		builderString.append("WHERE a.krcdtDaiBreakTimePK.employeeId IN :employeeIds ");
		builderString.append("AND a.krcdtDaiBreakTimePK.ymd IN :ymds ");
		DEL_BY_LIST_KEY = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDaiBreakTime a ");
		builderString.append("WHERE a.krcdtDaiBreakTimePK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDaiBreakTimePK.ymd = :ymd ");
		SELECT_BY_EMPLOYEE_AND_DATE = builderString.toString();

//		builderString = new StringBuilder();
//		builderString.append("SELECT a ");
//		builderString.append("FROM KrcdtDaiBreakTime a ");
//		builderString.append("WHERE a.krcdtDaiBreakTimePK.employeeId = :employeeId ");
//		builderString.append("AND a.krcdtDaiBreakTimePK.ymd = :ymd ");
//		builderString.append("AND a.krcdtDaiBreakTimePK.breakType = :breakType ");
//		FIND = builderString.toString();

		builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcdtDaiBreakTime a ");
		builderString.append("WHERE a.krcdtDaiBreakTimePK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDaiBreakTimePK.ymd = :ymd ");
//		builderString.append("AND a.krcdtDaiBreakTimePK.breakType = :breakType ");
		REMOVE_BY_BREAKTYPE = builderString.toString();
	}

	@Override
	public void delete(String employeeId, GeneralDate ymd) {
		this.queryProxy().query(SELECT_BY_EMPLOYEE_AND_DATE, KrcdtDaiBreakTime.class)
				.setParameter("employeeId", employeeId).setParameter("ymd", ymd).getList()
				.forEach(c -> this.commandProxy().remove(c));
	}

	@Override
	public void deleteByListEmployeeId(List<String> employeeIds, List<GeneralDate> ymds) {
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, sublistEmployeeIds -> {
			CollectionUtil.split(ymds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, sublistYMDs -> {
				this.getEntityManager().createQuery(DEL_BY_LIST_KEY)
					.setParameter("employeeIds", sublistEmployeeIds)
					.setParameter("ymds", sublistYMDs)
					.executeUpdate();
			});
		});
		this.getEntityManager().flush();
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<BreakTimeOfDailyPerformance> findByKey(String employeeId, GeneralDate ymd) {
		List<KrcdtDaiBreakTime> krcdtDaiBreakTimes = findEntities(employeeId, ymd);
		
		if (krcdtDaiBreakTimes == null || krcdtDaiBreakTimes.isEmpty()) {
			return Optional.empty();
		}
		return group(krcdtDaiBreakTimes);
	}

	@SneakyThrows
	private List<KrcdtDaiBreakTime> findEntities(String employeeId, GeneralDate ymd) {
		List<KrcdtDaiBreakTime> krcdtDaiBreakTimes = null; 

		try (val statement = this.connection().prepareStatement("select * FROM KRCDT_DAY_TS_BREAKTIME where SID = ? and YMD = ?")) {
			statement.setString(1, employeeId);
			statement.setDate(2, Date.valueOf(ymd.toLocalDate()));
			krcdtDaiBreakTimes = new NtsResultSet(statement.executeQuery()).getList(rec -> {
				val entity = new KrcdtDaiBreakTime();
				entity.krcdtDaiBreakTimePK = new KrcdtDaiBreakTimePK();
				entity.krcdtDaiBreakTimePK.employeeId = rec.getString("SID");
				entity.krcdtDaiBreakTimePK.ymd = rec.getGeneralDate("YMD");
//				entity.krcdtDaiBreakTimePK.breakType = rec.getInt("BREAK_TYPE");
				entity.krcdtDaiBreakTimePK.breakFrameNo = rec.getInt("BREAK_FRAME_NO");
				entity.startStampTime = rec.getInt("STR_STAMP_TIME");
				entity.endStampTime = rec.getInt("END_STAMP_TIME");
				return entity;
			});
		}

		if (krcdtDaiBreakTimes == null || krcdtDaiBreakTimes.isEmpty()) {
			return new ArrayList<>();
		}
		
		return krcdtDaiBreakTimes;
	}

	private Optional<BreakTimeOfDailyPerformance> group(List<KrcdtDaiBreakTime> krcdtDaiBreakTimes) {
		return Optional.of(toDtomain(krcdtDaiBreakTimes));
	}

	private BreakTimeOfDailyPerformance toDtomain(List<KrcdtDaiBreakTime> value) {
		return new BreakTimeOfDailyPerformance(value.get(0).krcdtDaiBreakTimePK.employeeId,
				value.get(0).krcdtDaiBreakTimePK.ymd,
//				EnumAdaptor.valueOf(type, BreakType.class),
				value.stream()
						.map(item -> new BreakTimeSheet(new BreakFrameNo(item.krcdtDaiBreakTimePK.breakFrameNo),
								toTimeWithDayAttr(item.startStampTime), toTimeWithDayAttr(item.endStampTime),
								new AttendanceTime(0)))
						.collect(Collectors.toList()));
	}

	private TimeWithDayAttr toTimeWithDayAttr(Integer time) {
		return time == null ? null : new TimeWithDayAttr(time);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void insert(BreakTimeOfDailyPerformance breakTimes) {
//		commandProxy().insertAll(KrcdtDaiBreakTime.toEntity(breakTimes));
//		this.getEntityManager().flush();
		Connection con = this.getEntityManager().unwrap(Connection.class);
		try {
			Statement statementI = con.createStatement();
			for(BreakTimeSheet breakTimeSheet : breakTimes.getTimeZone().getBreakTimeSheets()){
				String insertTableSQL = "INSERT INTO KRCDT_DAY_TS_BREAKTIME (SID, YMD, BREAK_FRAME_NO, STR_STAMP_TIME, END_STAMP_TIME) "
						+ "VALUES( '" + breakTimes.getEmployeeId() + "' , '"
						+ breakTimes.getYmd() + "' , "
//						+ breakTimes.getTimeZone().getBreakType().value + " , "
						+ breakTimeSheet.getBreakFrameNo().v() + " , "
						+ breakTimeSheet.getStartTime().valueAsMinutes() + " , "
						+ breakTimeSheet.getEndTime().valueAsMinutes() + " )";
				statementI.executeUpdate(JDBCUtil.toInsertWithCommonField(insertTableSQL));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void insert(List<BreakTimeOfDailyPerformance> breakTimes) {
		commandProxy().insertAll(breakTimes.stream().map(c -> KrcdtDaiBreakTime.toEntity(c)).flatMap(List::stream)
				.collect(Collectors.toList()));
		this.getEntityManager().flush();
	}

	@Override
	public void update(BreakTimeOfDailyPerformance breakTimes) {
		if(breakTimes == null){ return;}

		update(Arrays.asList(breakTimes));
	}

	@Override
	@SneakyThrows
	public void update(List<BreakTimeOfDailyPerformance> breakTimes) {
		List<KrcdtDaiBreakTime> all = breakTimes.stream().map(c -> KrcdtDaiBreakTime.toEntity(c)).flatMap(List::stream)
				.collect(Collectors.toList());
		if (!all.isEmpty()) {
			List<KrcdtDaiBreakTime> krcdtDaiBreakTimes = this.queryProxy().query(SELECT_BY_EMPLOYEE_AND_DATE, KrcdtDaiBreakTime.class)
																			.setParameter("employeeId", breakTimes.get(0).getEmployeeId())
																			.setParameter("ymd", breakTimes.get(0).getYmd())
																			.getList();
			List<KrcdtDaiBreakTime> toUpdate = all.stream()
					.filter(c -> c.endStampTime != null && c.startStampTime != null).collect(Collectors.toList());
			List<KrcdtDaiBreakTime> toRemove = krcdtDaiBreakTimes.stream()
					.filter(c -> !toUpdate.stream().filter(tu -> tu.krcdtDaiBreakTimePK.breakFrameNo == c.krcdtDaiBreakTimePK.breakFrameNo)
										.findFirst().isPresent())
					.collect(Collectors.toList());
			
			commandProxy().updateAll(toUpdate);
			toRemove.stream().forEach(c -> {
				commandProxy().remove(c);
			});
		} else {
			if(breakTimes.isEmpty() || breakTimes.get(0) == null) return;
			this.delete(breakTimes.get(0).getEmployeeId(), breakTimes.get(0).getYmd());
		}
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<BreakTimeOfDailyPerformance> finds(List<String> employeeId, DatePeriod ymd) {
		List<BreakTimeOfDailyPerformance> result = new ArrayList<>();

		CollectionUtil.split(employeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empIds -> {
			result.addAll(internalQuery(ymd, empIds));
		});
		return result;
	}
	
	@SneakyThrows
	private List<BreakTimeOfDailyPerformance> internalQuery(DatePeriod baseDate, List<String> empIds) {
		String subEmp = NtsStatement.In.createParamsString(empIds);
		StringBuilder query = new StringBuilder("SELECT YMD, SID, BREAK_FRAME_NO, STR_STAMP_TIME, END_STAMP_TIME FROM KRCDT_DAY_TS_BREAKTIME  ");
		query.append(" WHERE YMD <= ? AND YMD >= ? ");
		query.append(" AND SID IN (" + subEmp + ")");
		try (val stmt = this.connection().prepareStatement(query.toString())){
			stmt.setDate(1, Date.valueOf(baseDate.end().localDate()));
			stmt.setDate(2, Date.valueOf(baseDate.start().localDate()));
			for (int i = 0; i < empIds.size(); i++) {
				stmt.setString(i + 3, empIds.get(i));
			}
			val data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				Map<String, Object> r = new HashMap<>();
//				r.put("BREAK_TYPE", rec.getInt("BREAK_TYPE"));
				r.put("YMD", rec.getGeneralDate("YMD"));
				r.put("SID", rec.getString("SID"));
				r.put("BREAK_FRAME_NO", rec.getInt("BREAK_FRAME_NO"));
				r.put("STR_STAMP_TIME", rec.getInt("STR_STAMP_TIME"));
				r.put("END_STAMP_TIME", rec.getInt("END_STAMP_TIME"));
				return r;
			});
			val grouped = group2(data);
			
			return map(grouped);
		}
	}
	
	private List<BreakTimeOfDailyPerformance> map(Map<String, Map<GeneralDate, List<BreakTimeSheet>>> data) {
		
		return data.entrySet().stream().map(r -> {
			
			return r.getValue().entrySet().stream().map(c -> {
				
				return new BreakTimeOfDailyPerformance(r.getKey(), c.getKey(), c.getValue());
			}).collect(Collectors.toList());
		}).flatMap(List::stream).collect(Collectors.toList());
	}
	
	private Map<String, Map<GeneralDate, List<BreakTimeSheet>>> group2(List<Map<String, Object>> entities) {
		
		return entities.stream().collect(Collectors.groupingBy(r -> (String) r.get("SID"), Collectors.collectingAndThen(Collectors.toList(), s -> {
			
			 Map<GeneralDate, List<BreakTimeSheet>> mapped = s.stream().collect(Collectors.groupingBy(c -> (GeneralDate) c.get("YMD"), 
					 Collectors.collectingAndThen(Collectors.toList(), d -> {
//				return d.stream().collect(Collectors.groupingBy(dt -> (int) dt.get("BREAK_TYPE"), Collectors.collectingAndThen(Collectors.toList(), dt -> {
					return d.stream().map(sd -> {
						return new BreakTimeSheet(new BreakFrameNo((int) sd.get("BREAK_FRAME_NO")), 
								new TimeWithDayAttr((int) sd.get("STR_STAMP_TIME")), 
								new TimeWithDayAttr((int) sd.get("END_STAMP_TIME")));
					}).collect(Collectors.toList());
//				})));
			})));
			 
			return mapped;
		})));
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Optional<BreakTimeOfDailyPerformance> find(String employeeId, GeneralDate ymd, int breakType) {

		return findByKey(employeeId, ymd);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void deleteByBreakType(String employeeId, GeneralDate ymd, int breakType) {
		this.getEntityManager().createQuery(REMOVE_BY_BREAKTYPE)
				.setParameter("employeeId", employeeId)
				.setParameter("ymd", ymd)
//				.setParameter("breakType", breakType)
				.executeUpdate();
		this.getEntityManager().flush();
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SneakyThrows
	@Override
	public List<BreakTimeOfDailyPerformance> finds(Map<String, List<GeneralDate>> param) {
    	List<String> subList = param.keySet().stream().collect(Collectors.toList());
    	List<GeneralDate> subListDate = param.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList());
    	List<BreakTimeOfDailyPerformance> result = new ArrayList<>();

		CollectionUtil.split(subList, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empIds -> {
			result.addAll(internalQueryMap(subListDate, empIds));
		});
		return result;
	}

    @SneakyThrows
	private List<BreakTimeOfDailyPerformance> internalQueryMap(List<GeneralDate> subListDate, List<String> subList) {
    	String subEmp = NtsStatement.In.createParamsString(subList);
    	String subInDate = NtsStatement.In.createParamsString(subListDate);
    	
		StringBuilder query = new StringBuilder("SELECT YMD, SID, BREAK_FRAME_NO, STR_STAMP_TIME, END_STAMP_TIME FROM KRCDT_DAY_TS_BREAKTIME  ");
		query.append(" WHERE SID IN (" + subEmp + ")");
		query.append(" AND YMD IN (" + subInDate + ")");
		try (val stmt = this.connection().prepareStatement(query.toString())){
			for (int i = 0; i < subList.size(); i++) {
				stmt.setString(i + 1, subList.get(i));
			}
			
			for (int i = 0; i < subListDate.size(); i++) {
				stmt.setDate(1 + i + subList.size(),  Date.valueOf(subListDate.get(i).localDate()));
			}
			val data = new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				Map<String, Object> r = new HashMap<>();
//				r.put("BREAK_TYPE", rec.getInt("BREAK_TYPE"));
				r.put("YMD", rec.getGeneralDate("YMD"));
				r.put("SID", rec.getString("SID"));
				r.put("BREAK_FRAME_NO", rec.getInt("BREAK_FRAME_NO"));
				r.put("STR_STAMP_TIME", rec.getInt("STR_STAMP_TIME"));
				r.put("END_STAMP_TIME", rec.getInt("END_STAMP_TIME"));
				return r;
			});

			val grouped = group2(data);
			
			return map(grouped);
		}
    }
	@Override
	public void updateForEachOfType(BreakTimeOfDailyPerformance breakTime) {
		Connection con = this.getEntityManager().unwrap(Connection.class);
		try {
			for(BreakTimeSheet breakTimeSheet : breakTime.getTimeZone().getBreakTimeSheets()){
			
				String updateTableSQL = " UPDATE KRCDT_DAY_TS_BREAKTIME SET STR_STAMP_TIME = "
						+ breakTimeSheet.getStartTime().valueAsMinutes() + " , END_STAMP_TIME = "
						+ breakTimeSheet.getEndTime().valueAsMinutes() + " WHERE SID = '" + breakTime.getEmployeeId()
						+ "' AND YMD = '" + breakTime.getYmd() + "'" 
//						+ " AND BREAK_TYPE = " + breakTime.getTimeZone().getBreakType().value 
						+ " AND BREAK_FRAME_NO = " + breakTimeSheet.getBreakFrameNo().v();
				Statement statementU = con.createStatement();
				statementU.executeUpdate(updateTableSQL);
			}
		} catch (Exception e) {
			
		}
	}

	@Override
	public void updateNotDelete(List<BreakTimeOfDailyPerformance> breakTimes) {
		List<KrcdtDaiBreakTime> all = breakTimes.stream().map(c -> KrcdtDaiBreakTime.toEntity(c)).flatMap(List::stream)
				.collect(Collectors.toList());
		if(!all.isEmpty()) {
			commandProxy().updateAll(all);
		}
	}
}
