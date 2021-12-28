package nts.uk.ctx.at.shared.infra.repository.remainingnumber;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.SneakyThrows;
import nts.arc.layer.infra.data.DbConsts;
import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnLeaGrantRemDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveConditionInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea.KRcmtAnnLeaRemain;
import nts.uk.shr.com.context.AppContexts;
import nts.arc.time.calendar.period.DatePeriod;


@Stateless
public class JpaAnnLeaGrantRemDataRepo extends JpaRepository implements AnnLeaGrantRemDataRepository {

	private static final String QUERY_WITH_EMP_ID = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId ORDER BY a.grantDate DESC";

	private static final String CHECK_UNIQUE_SID_GRANTDATE_FOR_ADD = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId and a.grantDate = :grantDate";
	
	private static final String CHECK_UNIQUE_SID_GRANTDATE = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId and a.grantDate <= :grantDate";
	
	private static final String CHECK_UNIQUE_SID_GRANTDATE_FOR_UPDATE = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId and a.annLeavID !=:annLeavID and a.grantDate = :grantDate";
	
	private static final String QUERY_WITH_EMPID_CHECKSTATE = "SELECT a FROM KRcmtAnnLeaRemain a"
			+ " WHERE a.sid = :employeeId AND a.expStatus = :checkState ORDER BY a.grantDate DESC";

	private static final String DELETE_QUERY = "DELETE FROM KRcmtAnnLeaRemain a"
			+ " WHERE a.sid = :employeeId and a.grantDate = :grantDate";
	
	private static final String DELETE_AFTER_QUERY = "DELETE FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId and a.grantDate > :startDate";
	
	private static final String QUERY_WITH_EMP_ID_NOT_EXP = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId AND a.expStatus = 1 ORDER BY a.grantDate DESC";
	
	private static final String FIND_BY_EMP_AND_DATE = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId AND a.grantDate >= :startDate AND a.grantDate <= :endDate ORDER BY a.grantDate DESC";

	@Override
	public List<AnnualLeaveGrantRemainingData> find(String employeeId) {
		List<KRcmtAnnLeaRemain> entities = this.queryProxy().query(QUERY_WITH_EMP_ID, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId).getList();
		return entities.stream().map(ent -> toDomain(ent)).collect(Collectors.toList());
	}
	

	@Override
	public Optional<AnnualLeaveGrantRemainingData> getLast(String employeeId) {
		List<KRcmtAnnLeaRemain> entities = this.queryProxy().query(QUERY_WITH_EMP_ID, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId).getList();
		
		if (entities.isEmpty()) {
			return Optional.empty();
		}
		
		return Optional.of(toDomain(entities.get(0)));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<AnnualLeaveGrantRemainingData> findNotExp(String employeeId) {
		List<KRcmtAnnLeaRemain> entities = this.queryProxy().query(QUERY_WITH_EMP_ID_NOT_EXP, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId).getList();
		return entities.stream().map(ent -> toDomain(ent)).collect(Collectors.toList());
	}

	private AnnualLeaveGrantRemainingData toDomain(KRcmtAnnLeaRemain ent) {
		return AnnualLeaveGrantRemainingData.createFromJavaType(ent.annLeavID,  ent.sid, ent.grantDate,
				ent.deadline, ent.expStatus, ent.registerType, ent.grantDays, ent.grantMinutes, ent.usedDays,
				ent.usedMinutes, ent.stowageDays, ent.remainingDays, ent.remaningMinutes, ent.usedPercent,
				ent.perscribedDays, ent.deductedDays, ent.workingDays);
	}

	@Override
	public List<AnnualLeaveGrantRemainingData> findByCheckState(String employeeId, int checkState) {
		List<KRcmtAnnLeaRemain> entities = this.queryProxy().query(QUERY_WITH_EMPID_CHECKSTATE, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId).setParameter("checkState", checkState).getList();
		return entities.stream().map(ent -> toDomain(ent)).collect(Collectors.toList());
	}

	@Override
	public void add(String cid, AnnualLeaveGrantRemainingData data) {
		if(data != null) {
		KRcmtAnnLeaRemain entity = new KRcmtAnnLeaRemain();
		entity.annLeavID = data.getLeaveID();
		entity.sid = data.getEmployeeId();
		entity.cid = cid;
		updateValue(entity, data);

		this.commandProxy().insert(entity);
		}
	}

	@Override
	public void update(AnnualLeaveGrantRemainingData data) {
		if (data != null) {
			List<KRcmtAnnLeaRemain> entityOpts = this.queryProxy().query(CHECK_UNIQUE_SID_GRANTDATE_FOR_ADD, KRcmtAnnLeaRemain.class)
			.setParameter("employeeId", data.getEmployeeId())
			.setParameter("grantDate", data.getGrantDate()).getList();
			
			if (!entityOpts.isEmpty()) {
				KRcmtAnnLeaRemain entity = entityOpts.stream().findFirst().get();
				updateValue(entity, data);
				this.commandProxy().update(entity);
			}
		}
	}
	
	
	@Override
	/*
	 * CPS001から呼ばれる専用　付与日変更する時だけこのメソッドを使用すること
	 */
	public void updateWithGrantDate(AnnualLeaveGrantRemainingData data) {
		if (data != null) {
			Optional<KRcmtAnnLeaRemain> entityOpt = this.queryProxy().find(data.getLeaveID(),KRcmtAnnLeaRemain.class);
			if (entityOpt.isPresent()) {
				KRcmtAnnLeaRemain entity = entityOpt.get();
				updateValue(entity, data);
				this.commandProxy().update(entity);
			}
		}
	}

	private void updateValue(KRcmtAnnLeaRemain entity, AnnualLeaveGrantRemainingData data) {
		entity.deadline = data.getDeadline();
		entity.expStatus = data.getExpirationStatus().value;
		entity.registerType = data.getRegisterType().value;
		entity.grantDate = data.getGrantDate();
		AnnualLeaveNumberInfo details = (AnnualLeaveNumberInfo) data.getDetails();

		// grant data
		entity.grantDays = details.getGrantNumber().getDays().v();
		entity.grantMinutes = details.getGrantNumber().getMinutes().isPresent()
				? details.getGrantNumber().getMinutes().get().v() : null;
		// used data
		entity.usedDays = details.getUsedNumber().getDays().v();
		entity.usedMinutes = details.getUsedNumber().getMinutes().isPresent()
				? details.getUsedNumber().getMinutes().get().v() : null;
		entity.stowageDays = details.getUsedNumber().getStowageDays().isPresent()
				? details.getUsedNumber().getStowageDays().get().v() : null;
		// remain data
		entity.remainingDays = details.getRemainingNumber().getDays().v();
		entity.remaningMinutes = details.getRemainingNumber().getMinutes().isPresent()
				? details.getRemainingNumber().getMinutes().get().v() : null;

		entity.usedPercent = details.getUsedPercent().v().doubleValue();

		if (data.getAnnualLeaveConditionInfo().isPresent()) {
			AnnualLeaveConditionInfo conditionInfo = data.getAnnualLeaveConditionInfo().get();
			entity.perscribedDays = conditionInfo.getPrescribedDays().v();
			entity.deductedDays = conditionInfo.getDeductedDays().v();
			entity.workingDays = conditionInfo.getWorkingDays().v();
		} else {
			entity.perscribedDays = null;
			entity.deductedDays = null;
			entity.workingDays = null;
		}
	}

	@Override
	public void delete(String employeeId, GeneralDate grantDate) {
		this.getEntityManager().createQuery(DELETE_QUERY).setParameter("employeeId", employeeId)
				.setParameter("grantDate", grantDate);
	}

	@Override
	public void delete(String annaLeavID) {
		Optional<KRcmtAnnLeaRemain> entity = this.queryProxy().find(annaLeavID, KRcmtAnnLeaRemain.class);
		if (entity.isPresent()) {
			this.commandProxy().remove(KRcmtAnnLeaRemain.class, annaLeavID);
		}
	}

	@Override
	public Optional<AnnualLeaveGrantRemainingData> findByID(String id) {
		Optional<KRcmtAnnLeaRemain> entity = this.queryProxy().find(id, KRcmtAnnLeaRemain.class);
		if (entity.isPresent()) {
			return Optional.ofNullable(toDomain(entity.get()));
		}
		return Optional.empty();
	}

	@Override
	public List<AnnualLeaveGrantRemainingData> find(String employeeId, GeneralDate grantDate) {
		String sql = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId AND a.grantDate = :grantDate";
		return this.queryProxy().query(sql, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId).setParameter("grantDate", grantDate)
				.getList(e -> toDomain(e));
	}


	@Override
	public void deleteAfterDate(String employeeId, GeneralDate date) {
		this.getEntityManager().createQuery(DELETE_AFTER_QUERY).setParameter("employeeId", employeeId)
				.setParameter("startDate", date);
	}


	@Override
	public List<AnnualLeaveGrantRemainingData> checkConditionUniqueForAdd(String employeeId, GeneralDate grantDate) {
		if (grantDate == null) {
			return Collections.emptyList();
		}
		return this.queryProxy().query(CHECK_UNIQUE_SID_GRANTDATE_FOR_ADD, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("grantDate", grantDate).getList(e -> toDomain(e));
	}

	@Override
	public Map<String, List<AnnualLeaveGrantRemainingData>> checkConditionUniqueForAdd(String cid, Map<String, GeneralDate> emp) {
		List<AnnualLeaveGrantRemainingData> remainDataLst = new ArrayList<>();
		List<String> sids = new ArrayList<>(emp.keySet());
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HDPAID_REM WHERE CID = ? AND SID IN (" + NtsStatement.In.createParamsString(subList) + ")";
			
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString( 1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString( i + 1, subList.get(i));
				}
				
				List<AnnualLeaveGrantRemainingData> annualLeavelst = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					KRcmtAnnLeaRemain entity= new KRcmtAnnLeaRemain();
					entity.annLeavID = r.getString("ANNLEAV_ID");
					entity.cid = r.getString("CID");
					entity.sid = r.getString("SID");
					entity.grantDate = r.getGeneralDate("GRANT_DATE");
					entity.deadline = r.getGeneralDate("DEADLINE");
					entity.expStatus = r.getInt("EXP_STATUS");
					entity.registerType = r.getInt("REGISTER_TYPE");
					entity.grantDays = r.getDouble("GRANT_DAYS");
					entity.grantMinutes = r.getInt("GRANT_MINUTES");
					
					entity.usedDays = r.getDouble("USED_DAYS");
					entity.usedMinutes = r.getInt("USED_MINUTES");
					entity.stowageDays = r.getDouble("STOWAGE_DAYS");
					entity.remainingDays = r.getInt("REMAINING_DAYS");
					entity.remaningMinutes = r.getInt("REMAINING_MINUTES");
					entity.usedPercent = r.getDouble("USED_PERCENT");
					
					entity.perscribedDays = r.getDouble("PRESCRIBED_DAYS");
					entity.deductedDays = r.getDouble("DEDUCTED_DAYS");
					entity.workingDays = r.getDouble("WORKING_DAYS");
					return toDomain(entity);
				});
				remainDataLst.addAll(annualLeavelst);
				
			}catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		
		Map<String, List<AnnualLeaveGrantRemainingData>> remainDataMap = remainDataLst.stream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		Map<String, List<AnnualLeaveGrantRemainingData>> result = new HashMap<>();
		
		emp.entrySet().stream().forEach(c ->{
			List<AnnualLeaveGrantRemainingData> remainDataBySids = remainDataMap.get(c.getKey());
			List<AnnualLeaveGrantRemainingData> remainDataByGrantDate = remainDataBySids.stream().filter(item -> item.getGrantDate().equals(c.getValue())).collect(Collectors.toList());
			if(!remainDataByGrantDate.isEmpty()) {
				result.put(c.getKey(), remainDataByGrantDate);
			}
		});
		
		return result;
	}
	

	@Override
	public List<AnnualLeaveGrantRemainingData> checkConditionUniqueForUpdate(String employeeId, String annLeavID,
			GeneralDate grantDate) {
		return this.queryProxy().query(CHECK_UNIQUE_SID_GRANTDATE_FOR_UPDATE, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("annLeavID", annLeavID)
				.setParameter("grantDate", grantDate).getList(e -> toDomain(e));
	}

	@Override
	public List<AnnualLeaveGrantRemainingData> findByPeriod(String employeeId, GeneralDate startDate, GeneralDate endDate) {
		String sql = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId AND a.grantDate >= :startDate AND a.grantDate <= :endDate";
		return this.queryProxy().query(sql, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getList(e -> toDomain(e));
	}

	@Override
	public List<AnnualLeaveGrantRemainingData> findByGrantDateAndDeadline(String employeeId, GeneralDate grantDate, GeneralDate deadline) {
		String sql = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId AND a.grantDate < :grantDate AND a.deadline >= :deadline";
		return this.queryProxy().query(sql, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("grantDate", grantDate)
				.setParameter("deadline", deadline)
				.getList(e -> toDomain(e));
	}
	/**
	 * @author yennth
	 */
	@Override
	public List<AnnualLeaveGrantRemainingData> findInDate(String employeeId, GeneralDate startDate, GeneralDate endDate) {
		List<KRcmtAnnLeaRemain> entities = this.queryProxy().query(FIND_BY_EMP_AND_DATE, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId) 
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getList();
		return entities.stream().map(ent -> toDomain(ent)).collect(Collectors.toList());
	}

	@Override
	@SneakyThrows
	public List<AnnualLeaveGrantRemainingData> findByCidAndSids(String cid, List<String> sids) {
		List<AnnualLeaveGrantRemainingData> result = new ArrayList<>();
		
		CollectionUtil.split(sids, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subList -> {
			String sql = "SELECT * FROM KRCDT_HDPAID_REM WHERE CID = ? AND SID IN (" + NtsStatement.In.createParamsString(subList) + ")"
					+ " AND EXP_STATUS = 1  ORDER BY GRANT_DATE  DESC";
			
			try (PreparedStatement stmt = this.connection().prepareStatement(sql)) {
				stmt.setString( 1, cid);
				for (int i = 0; i < subList.size(); i++) {
					stmt.setString( i + 2, subList.get(i));
				}
				
				List<AnnualLeaveGrantRemainingData> annualLeavelst = new NtsResultSet(stmt.executeQuery()).getList(r -> {
					KRcmtAnnLeaRemain entity= new KRcmtAnnLeaRemain();
					entity.annLeavID = r.getString("ANNLEAV_ID");
					entity.cid = r.getString("CID");
					entity.sid = r.getString("SID");
					entity.grantDate = r.getGeneralDate("GRANT_DATE");
					entity.deadline = r.getGeneralDate("DEADLINE");
					entity.expStatus = r.getInt("EXP_STATUS");
					entity.registerType = r.getInt("REGISTER_TYPE");
					entity.grantDays = r.getDouble("GRANT_DAYS");
					entity.grantMinutes = r.getInt("GRANT_MINUTES");
					
					entity.usedDays = r.getDouble("USED_DAYS");
					entity.usedMinutes = r.getInt("USED_MINUTES");
					entity.stowageDays = r.getDouble("STOWAGE_DAYS");
					entity.remainingDays = r.getDouble("REMAINING_DAYS");
					entity.remaningMinutes = r.getInt("REMAINING_MINUTES");
					entity.usedPercent = r.getDouble("USED_PERCENT");
					
					entity.perscribedDays = r.getDouble("PRESCRIBED_DAYS");
					entity.deductedDays = r.getDouble("DEDUCTED_DAYS");
					entity.workingDays = r.getDouble("WORKING_DAYS");
					return toDomain(entity);
				});
				result.addAll(annualLeavelst);
				
			}catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
		
		return result;
	}

	@Override
	public void addAll(String cid, List<AnnualLeaveGrantRemainingData> domains) {
		String INS_SQL = "INSERT INTO KRCDT_CHILDCARE_HD_REMAIN (INS_DATE, INS_CCD , INS_SCD , INS_PG,"
				+ " UPD_DATE , UPD_CCD , UPD_SCD , UPD_PG,"
				+ " ANNLEAV_ID, CID, SID, GRANT_DATE, DEADLINE, EXP_STATUS, REGISTER_TYPE, GRANT_DAYS, GRANT_MINUTES, USED_DAYS, USED_MINUTES, STOWAGE_DAYS, REMAINING_DAYS, REMAINING_MINUTES, USED_PERCENT, PRESCRIBED_DAYS, DEDUCTED_DAYS, WORKING_DAYS)"
				+ " VALUES (INS_DATE_VAL, INS_CCD_VAL, INS_SCD_VAL, INS_PG_VAL,"
				+ " UPD_DATE_VAL, UPD_CCD_VAL, UPD_SCD_VAL, UPD_PG_VAL,"
				+ " ANNLEAV_ID_VAL, CID_VAL, SID_VAL, GRANT_DATE_VAL, DEADLINE_VAL, EXP_STATUS_VAL, REGISTER_TYPE_VAL, GRANT_DAYS_VAL, GRANT_MINUTES_VAL, USED_DAYS_VAL, USED_MINUTES_VAL, STOWAGE_DAYS_VAL, REMAINING_DAYS_VAL, REMAINING_MINUTES_VAL, USED_PERCENT_VAL, PRESCRIBED_DAYS_VAL, DEDUCTED_DAYS_VAL, WORKING_DAYS_VAL);";
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

			sql = sql.replace("ANNLEAV_ID", "'" +  c.getLeaveID() + "'");
			sql = sql.replace("CID_VAL", "'" + cid+ "'");
			sql = sql.replace("SID_VAL", "'" + c.getEmployeeId()+ "'");
			
			sql = sql.replace("GRANT_DATE_VAL", "'" +  c.getGrantDate() + "'");
			sql = sql.replace("DEADLINE_VAL", "'" + c.getDeadline() + "'");
			sql = sql.replace("EXP_STATUS_VAL", "" + c.getExpirationStatus().value+ "");

			sql = sql.replace("REGISTER_TYPE_VAL", "" +  c.getRegisterType().value + "");
			sql = sql.replace("GRANT_DAYS_VAL", "'" + c.getDeadline() + "'");
			
			AnnualLeaveNumberInfo details = (AnnualLeaveNumberInfo) c.getDetails();
			// grant data
			sql = sql.replace("GRANT_MINUTES_VAL", "" + details.getGrantNumber().getDays().v()+"");
			sql = sql.replace("GRANT_MINUTES_VAL", "" + details.getGrantNumber().getDays().v()+"");
			// used data
			sql = sql.replace("USED_DAYS", "" + details.getUsedNumber().getDays().v()+"");
			sql = sql.replace("USED_MINUTES", details.getUsedNumber().getMinutes().isPresent()
					? ""+ details.getUsedNumber().getMinutes().get().v() + "" : "null");
			sql = sql.replace("STOWAGE_DAYS", details.getUsedNumber().getStowageDays().isPresent()
					? "" + details.getUsedNumber().getStowageDays().get().v() + "" : "null");
			// remain data
			sql = sql.replace("REMAINING_DAYS", "" + details.getRemainingNumber().getDays().v()+"");
			sql = sql.replace("REMAINING_MINUTES", details.getRemainingNumber().getMinutes().isPresent()
					? ""+ details.getRemainingNumber().getMinutes().get().v() + "" : "null");
			sql = sql.replace("USED_PERCENT", "" + details.getUsedPercent().v().doubleValue() + "" );
			
			if (c.getAnnualLeaveConditionInfo().isPresent()) {
				AnnualLeaveConditionInfo conditionInfo = c.getAnnualLeaveConditionInfo().get();
				sql = sql.replace("PRESCRIBED_DAYS", "" + conditionInfo.getPrescribedDays().v()+ "");
				sql = sql.replace("DEDUCTED_DAYS", "" + conditionInfo.getDeductedDays().v() + "");
				sql = sql.replace("WORKING_DAYS", "" + conditionInfo.getWorkingDays().v()+ "");
			} else {
				sql = sql.replace("PRESCRIBED_DAYS", "null");
				sql = sql.replace("DEDUCTED_DAYS", "null");
				sql = sql.replace("WORKING_DAYS", "null");
			}
			
			sb.append(sql);
		});

		int records = this.getEntityManager().createNativeQuery(sb.toString()).executeUpdate();
		System.out.println(records);
		
	}

	@Override
	public List<AnnualLeaveGrantRemainingData> findBySidAndDate(String employeeId, GeneralDate grantDate) {
		return this.queryProxy().query(CHECK_UNIQUE_SID_GRANTDATE, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", employeeId)
				.setParameter("grantDate", grantDate).getList(e -> toDomain(e));
	}
	
	@Override
	public Map<String, List<AnnualLeaveGrantRemainingData>> findInDate(List<String> employeeId, GeneralDate startDate,
			GeneralDate endDate) {
		if (employeeId.isEmpty())
			return Collections.emptyMap();
		String query = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid IN :employeeId AND a.grantDate >= :startDate AND a.grantDate <= :endDate ORDER BY a.grantDate DESC";
		List<AnnualLeaveGrantRemainingData> result = new ArrayList<>();
		CollectionUtil.split(employeeId, DbConsts.MAX_CONDITIONS_OF_IN_STATEMENT, subIdList -> {
			result.addAll(this.queryProxy().query(query, KRcmtAnnLeaRemain.class).setParameter("employeeId", subIdList)
					.setParameter("startDate", startDate)
					.setParameter("endDate", endDate)
					.getList(ent -> toDomain(ent)));
		});
		return result.stream().collect(Collectors.groupingBy(i -> i.getEmployeeId()));
	}


	@Override
	public List<AnnualLeaveGrantRemainingData> findByExpStatus(String sid, LeaveExpirationStatus expStatus,
			DatePeriod datePeriod) {
		String sql = "SELECT a FROM KRcmtAnnLeaRemain a WHERE a.sid = :employeeId"
				+ " AND a.deadline >= :startDate"
				+ " AND a.deadline <= :endDate"
				+ " AND a.expStatus = :expStatus"
				+ " ORDER BY a.grantDate ASC";
		return this.queryProxy().query(sql, KRcmtAnnLeaRemain.class)
				.setParameter("employeeId", sid)
				.setParameter("startDate", datePeriod.start())
				.setParameter("endDate", datePeriod.end())
				.setParameter("expStatus", expStatus.value)
				.getList(e -> toDomain(e));
	}

}
