package nts.uk.ctx.at.record.infra.repository.editstate;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
import nts.arc.layer.infra.data.query.TypedQueryWrapper;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.infra.entity.editstate.KrcdtDailyRecEditSetPK;
import nts.uk.ctx.at.record.infra.entity.editstate.KrcdtDayEditState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaEditStateOfDailyPerformanceRepository extends JpaRepository
		implements EditStateOfDailyPerformanceRepository {
	private static final String DEL_BY_LIST_KEY;
	
	static {
		StringBuilder builderString = new StringBuilder();
		builderString.append("DELETE ");
		builderString.append("FROM KrcdtDayEditState a ");
		builderString.append("WHERE a.krcdtDailyRecEditSetPK.employeeId IN :employeeIds ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.processingYmd IN :processingYmds ");
		DEL_BY_LIST_KEY = builderString.toString();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void delete(String employeeId, GeneralDate ymd) {
		
		Connection con = this.getEntityManager().unwrap(Connection.class);
		String sqlQuery = "Delete From KRCDT_DAY_EDIT_STATE Where SID = " + "'" + employeeId + "'" + " and YMD = " + "'" + ymd + "'" ;
		try {
			con.createStatement().executeUpdate(sqlQuery);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
//		this.getEntityManager().createQuery(REMOVE_BY_EMPLOYEE).setParameter("employeeId", employeeId)
//				.setParameter("ymd", ymd).executeUpdate();
	}

	@Override
	public void deleteByListEmployeeId(List<String> employeeIds, List<GeneralDate> processingYmds) {
		CollectionUtil.split(employeeIds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, lstEmployeeIds -> {
			CollectionUtil.split(processingYmds, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, ymds -> {
				this.getEntityManager().createQuery(DEL_BY_LIST_KEY)
					.setParameter("employeeIds", lstEmployeeIds)
					.setParameter("processingYmds", ymds)
					.executeUpdate();
			});
		});
		this.getEntityManager().flush();
	}

	@Override
	public void add(List<EditStateOfDailyPerformance> editStates) {
		this.commandProxy().insertAll(
						editStates.stream()
								.map(c -> new KrcdtDayEditState(new KrcdtDailyRecEditSetPK(c.getEmployeeId(),
										c.getYmd(), c.getEditState().getAttendanceItemId()), c.getEditState().getEditStateSetting().value))
								.collect(Collectors.toList()));
		
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<EditStateOfDailyPerformance> findByKey(String employeeId, GeneralDate ymd) {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDayEditState a ");
		builderString.append("WHERE a.krcdtDailyRecEditSetPK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.processingYmd = :ymd ");
		return this.queryProxy().query(builderString.toString(), KrcdtDayEditState.class)
				.setParameter("employeeId", employeeId).setParameter("ymd", ymd)
				.getList(c -> toDomain(c));
	}

	private EditStateOfDailyPerformance toDomain(KrcdtDayEditState c) {
		return new EditStateOfDailyPerformance(c.krcdtDailyRecEditSetPK.employeeId,
				c.krcdtDailyRecEditSetPK.attendanceItemId, c.krcdtDailyRecEditSetPK.processingYmd,
				EnumAdaptor.valueOf(c.editState, EditStateSetting.class));
	}

	@Override
	public void updateByKey(List<EditStateOfDailyPerformance> editStates) {
		
		this.commandProxy().updateAll(
				editStates.stream()
						.map(c -> new KrcdtDayEditState(new KrcdtDailyRecEditSetPK(c.getEmployeeId(),
								c.getYmd(), c.getEditState().getAttendanceItemId()), c.getEditState().getEditStateSetting().value))
						.collect(Collectors.toList()));
	
	}
	
	@Override
	public void deleteExclude(List<EditStateOfDailyPerformance> editStates) {
		Map<String, List<GeneralDate>> keys = editStates.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId(), 
				Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().map(c -> c.getYmd()).collect(Collectors.toList()))));
		
		List<KrcdtDayEditState> olds = internalFinds(keys, c -> c);
		
		olds.removeIf(c -> editStates.stream().anyMatch(e -> e.getEditState().getAttendanceItemId() == c.krcdtDailyRecEditSetPK.attendanceItemId
																&& e.getEmployeeId().equals(c.krcdtDailyRecEditSetPK.employeeId) 
																&& e.getYmd().equals(c.krcdtDailyRecEditSetPK.processingYmd)));
		
		this.commandProxy().removeAll(olds);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void deleteExclude(String employeeId, GeneralDate ymd, List<EditStateOfDailyPerformance> editStates) {
		Map<String, List<GeneralDate>> keys = new HashMap<String, List<GeneralDate>>() {
			{
				put(employeeId, Arrays.asList(ymd));
			}
		};

		List<KrcdtDayEditState> olds = internalFinds(keys, c -> c);
		
		olds.removeIf(c -> editStates.stream().anyMatch(e -> e.getEditState().getAttendanceItemId() == c.krcdtDailyRecEditSetPK.attendanceItemId
																&& e.getEmployeeId().equals(c.krcdtDailyRecEditSetPK.employeeId) 
																&& e.getYmd().equals(c.krcdtDailyRecEditSetPK.processingYmd)));
		
		this.commandProxy().removeAll(olds);
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<EditStateOfDailyPerformance> finds(List<String> employeeId, DatePeriod ymd) {
		List<EditStateOfDailyPerformance> result = new ArrayList<>();
		
		CollectionUtil.split(employeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empIds -> {
			result.addAll(internalQuery(ymd, empIds));
		});
		return result;
	}
	
	@SneakyThrows
	private List<EditStateOfDailyPerformance> internalQuery(DatePeriod baseDate, List<String> empIds) {
		String subEmp = NtsStatement.In.createParamsString(empIds);
		StringBuilder query = new StringBuilder("SELECT SID, YMD, ATTENDANCE_ITEM_ID, EDIT_STATE FROM KRCDT_DAY_EDIT_STATE");
		query.append(" WHERE YMD <= ? AND YMD >= ? ");
		query.append(" AND SID IN (" + subEmp + ")");
		try (val stmt = this.connection().prepareStatement(query.toString())){
			stmt.setDate(1, Date.valueOf(baseDate.end().localDate()));
			stmt.setDate(2, Date.valueOf(baseDate.start().localDate()));
			for (int i = 0; i < empIds.size(); i++) {
				stmt.setString(i + 3, empIds.get(i));
			}
			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				return new EditStateOfDailyPerformance(rec.getString("SID"), rec.getInt("ATTENDANCE_ITEM_ID"),
						rec.getGeneralDate("YMD"), EnumAdaptor.valueOf(rec.getInt("EDIT_STATE"), EditStateSetting.class));
			});
		}
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @SneakyThrows
	@Override
	public List<EditStateOfDailyPerformance> finds(Map<String, List<GeneralDate>> param) {
		
		List<String> subList = param.keySet().stream().collect(Collectors.toList());
		List<GeneralDate> subListDate = param.values().stream().flatMap(x -> x.stream()).collect(Collectors.toList());
		List<EditStateOfDailyPerformance> result = new ArrayList<>();

		CollectionUtil.split(subList, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, empIds -> {
			result.addAll(internalQueryMap(subListDate, empIds));
		});
		return result;
	}
	
    @SneakyThrows
	private List<EditStateOfDailyPerformance> internalQueryMap(List<GeneralDate> subListDate, List<String> subList) {
    	String subEmp = NtsStatement.In.createParamsString(subList);
    	String subInDate = NtsStatement.In.createParamsString(subListDate);
		StringBuilder query = new StringBuilder("SELECT SID, YMD, ATTENDANCE_ITEM_ID, EDIT_STATE FROM KRCDT_DAY_EDIT_STATE");
		query.append(" WHERE SID IN (" + subEmp + ")");
		query.append(" AND YMD IN (" + subInDate + ")");
		try (val stmt = this.connection().prepareStatement(query.toString())){
			for (int i = 0; i < subList.size(); i++) {
				stmt.setString(i + 1, subList.get(i));
			}
			
			for (int i = 0; i < subListDate.size(); i++) {
				stmt.setDate(1 + i + subList.size(),  Date.valueOf(subListDate.get(i).localDate()));
			}
			return new NtsResultSet(stmt.executeQuery()).getList(rec -> {
				return new EditStateOfDailyPerformance(rec.getString("SID"), rec.getInt("ATTENDANCE_ITEM_ID"),
						rec.getGeneralDate("YMD"), EnumAdaptor.valueOf(rec.getInt("EDIT_STATE"), EditStateSetting.class));
			});
		}
    }
	private <T> List<T> internalFinds(Map<String, List<GeneralDate>> param, Function<KrcdtDayEditState, T> actions) {
		List<T> result = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT a FROM KrcdtDayEditState a ");
		query.append("WHERE a.krcdtDailyRecEditSetPK.employeeId IN :employeeId ");
		query.append("AND a.krcdtDailyRecEditSetPK.processingYmd IN :date");
		TypedQueryWrapper<KrcdtDayEditState> tQuery=  this.queryProxy().query(query.toString(), KrcdtDayEditState.class);
		CollectionUtil.split(param, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, p -> {
			result.addAll(tQuery.setParameter("employeeId", p.keySet())
					.setParameter("date", p.values().stream().flatMap(List::stream).collect(Collectors.toSet()))
					.getList().stream()
					.filter(c -> p.get(c.krcdtDailyRecEditSetPK.employeeId).contains(c.krcdtDailyRecEditSetPK.processingYmd))
					.collect(Collectors.groupingBy(
							c -> c.krcdtDailyRecEditSetPK.employeeId + c.krcdtDailyRecEditSetPK.processingYmd.toString()))
					.entrySet().stream().map(c -> c.getValue().stream().map(actions).collect(Collectors.toList()))
					.flatMap(List::stream).collect(Collectors.toList()));
		});
		return result;
	}

	@Override
	public void addAndUpdate(List<EditStateOfDailyPerformance> editStates) {
		editStates.forEach(x -> {
			if(this.findByKeyId(x.getEmployeeId(), x.getYmd(), x.getEditState().getAttendanceItemId()).isPresent()){
				this.commandProxy().update(new KrcdtDayEditState(new KrcdtDailyRecEditSetPK(x.getEmployeeId(),
						x.getYmd(), x.getEditState().getAttendanceItemId()), x.getEditState().getEditStateSetting().value));
			}else{
				KrcdtDayEditState entity = new KrcdtDayEditState(new KrcdtDailyRecEditSetPK(x.getEmployeeId(),
						x.getYmd(), x.getEditState().getAttendanceItemId()), x.getEditState().getEditStateSetting().value);
				this.commandProxy().insert(entity);
			}
		});
		this.getEntityManager().flush();
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public Optional<EditStateOfDailyPerformance> findByKeyId(String employeeId, GeneralDate ymd, Integer id) {
		return this.queryProxy().find(new KrcdtDailyRecEditSetPK(employeeId, ymd, id), KrcdtDayEditState.class)
				.map(x -> new EditStateOfDailyPerformance(employeeId, id, ymd, EnumAdaptor.valueOf(x.editState, EditStateSetting.class)));
	}

	@Override
	public void updateByKeyFlush(List<EditStateOfDailyPerformance> editStates) {
		this.addAndUpdate(editStates);
		this.getEntityManager().flush();
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<EditStateOfDailyPerformance> findByItems(String employeeId, GeneralDate ymd, List<Integer> ids) {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDayEditState a ");
		builderString.append("WHERE a.krcdtDailyRecEditSetPK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.processingYmd = :ymd ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.attendanceItemId IN :items ");
		
		List<EditStateOfDailyPerformance> resultList = new ArrayList<>();
		CollectionUtil.split(ids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			resultList.addAll(this.queryProxy().query(builderString.toString(), KrcdtDayEditState.class)
					.setParameter("employeeId", employeeId)
					.setParameter("ymd", ymd)
					.setParameter("items", subList)
					.getList(c -> toDomain(c)));
		});
		
		return resultList;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void deleteByListItemId(String employeeId, GeneralDate ymd, List<Integer> itemIdList) {
//		this.getEntityManager().createQuery(DEL_BY_LIST_ITEM_ID).setParameter("employeeId", employeeId)
//		.setParameter("ymd", ymd).setParameter("itemIdList", itemIdList).executeUpdate();
//		this.getEntityManager().flush();
		
		CollectionUtil.split(itemIdList, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String listItemIdString = "(";
			for(int i = 0; i < subList.size(); i++){
				listItemIdString += "'"+ subList.get(i) +"',";
			}
			// remove last , in string and add )
			listItemIdString = listItemIdString.substring(0, listItemIdString.length() - 1) + ")";
			
			Connection con = this.getEntityManager().unwrap(Connection.class);
			String sqlQuery = "Delete From KRCDT_DAY_EDIT_STATE Where SID = " + "'" + employeeId + "'" + " and YMD = " + "'" + ymd + "'"
					+ " and ATTENDANCE_ITEM_ID IN " + listItemIdString;
			try {
				con.createStatement().executeUpdate(sqlQuery);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<EditStateOfDailyPerformance> findByEditState(String sid, GeneralDate ymd, List<Integer> ids,
			EditStateSetting editState) {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDayEditState a ");
		builderString.append("WHERE a.krcdtDailyRecEditSetPK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.processingYmd = :ymd ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.attendanceItemId IN :items ");
		builderString.append("AND a.editState = :editState ");
		builderString.append("ORDER BY a.krcdtDailyRecEditSetPK.attendanceItemId");
		return this.queryProxy().query(builderString.toString(), KrcdtDayEditState.class)
				.setParameter("employeeId", sid)
				.setParameter("ymd", ymd)
				.setParameter("items", ids)
				.setParameter("editState", editState.value)
				.getList(c -> toDomain(c));
	}
    
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<EditStateOfDailyPerformance> findByEditState(String sid, GeneralDate ymd, EditStateSetting editState) {
		StringBuilder builderString = new StringBuilder();
		builderString.append("SELECT a ");
		builderString.append("FROM KrcdtDayEditState a ");
		builderString.append("WHERE a.krcdtDailyRecEditSetPK.employeeId = :employeeId ");
		builderString.append("AND a.krcdtDailyRecEditSetPK.processingYmd = :ymd ");
		builderString.append("AND a.editState = :editState ");
		builderString.append("ORDER BY a.krcdtDailyRecEditSetPK.attendanceItemId");
		return this.queryProxy().query(builderString.toString(), KrcdtDayEditState.class)
				.setParameter("employeeId", sid)
				.setParameter("ymd", ymd)
				.setParameter("editState", editState.value)
				.getList(c -> toDomain(c));
	}

}
