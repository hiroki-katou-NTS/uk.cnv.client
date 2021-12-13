package nts.uk.ctx.at.function.infra.repository.processexecution;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.time.GeneralDateTime;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogHistory;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogHistRepository;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtExecutionTaskLog;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtProcessExecutionLogHistory;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.jdbc.JDBCUtil;
//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaProcessExecutionLogHistRepository extends JpaRepository implements ProcessExecutionLogHistRepository {

	private static final String SELECT_ALL = "SELECT pelh FROM KfnmtProcessExecutionLogHistory pelh ";

	private static final String SELECT_All_BY_CID_EXECCD_EXECID = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.companyId = :companyId "
			+ "AND pelh.kfnmtProcExecLogHstPK.execItemCd = :execItemCd "
			+ "AND pelh.kfnmtProcExecLogHstPK.execId = :execId ";

	private static final String SELECT_All_BY_CID_EXECCD_DATE = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.companyId = :companyId "
			+ "AND pelh.kfnmtProcExecLogHstPK.execItemCd = :execItemCd "
			+ "AND pelh.lastExecDateTime >= :lastExecDateTime AND pelh.lastExecDateTime< :nextExecDateTime "
			+ "ORDER BY pelh.lastExecDateTime DESC";

	private static final String SELECT_All_BY_CID_EXECCD_DATE_RANGE = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.companyId = :companyId "
			+ "AND pelh.kfnmtProcExecLogHstPK.execItemCd = :execItemCd "
			+ "AND pelh.lastExecDateTime >= :startDate AND pelh.lastExecDateTime < :endDate "
			+ "ORDER BY pelh.lastExecDateTime DESC";

	private static final String SELECT_All_BY_CID_EXECCD = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.companyId = :companyId "
			+ "AND pelh.kfnmtProcExecLogHstPK.execItemCd = :execItemCd ";

	private static final String SELECT_ALL_BY_CID_START_DATE_END_DATE = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.companyId = :companyId "
			+ "AND pelh.lastExecDateTime >= :startDate AND pelh.lastExecDateTime < :endDate "
			+ "ORDER BY pelh.lastExecDateTime DESC";

	private static final String SELECT_ALL_BY_CID_AND_EXECCD = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.companyId = :companyId "
			+ "AND pelh.kfnmtProcExecLogHstPK.execItemCd = :execItemCd "
			+ "ORDER BY pelh.lastExecDateTime DESC";
	private static final String SELECT_BY_EXEC_ID = SELECT_ALL
			+ "WHERE pelh.kfnmtProcExecLogHstPK.execId = :execId";

	private static KfnmtProcessExecutionLogHistory toEntity(ProcessExecutionLogHistory domain) {
		KfnmtProcessExecutionLogHistory entity = new KfnmtProcessExecutionLogHistory();
		domain.setMemento(entity);
		return entity;
	}

	@Override
	public Optional<ProcessExecutionLogHistory> getByExecId(String companyId, String execItemCd, String execId) {
		return this.queryProxy().query(SELECT_All_BY_CID_EXECCD_EXECID, KfnmtProcessExecutionLogHistory.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd)
				.setParameter("execId", execId)
				.getSingle(ProcessExecutionLogHistory::createFromMemento);
	}
	
	@Override
	public void insert(ProcessExecutionLogHistory domain) {
		Optional<KfnmtProcessExecutionLogHistory> data = this.queryProxy().query(SELECT_All_BY_CID_EXECCD_EXECID, KfnmtProcessExecutionLogHistory.class)
			.setParameter("companyId", domain.getCompanyId())
			.setParameter("execItemCd", domain.getExecItemCd())
			.setParameter("execId", domain.getExecId()).getSingle();
		if(data.isPresent()) {
			this.commandProxy().remove(data.get());
			this.getEntityManager().flush();
		}
		KfnmtProcessExecutionLogHistory entity = JpaProcessExecutionLogHistRepository.toEntity(domain);
		this.commandProxy().insert(entity);
		this.getEntityManager().flush();
	}
	
	@Override
	public void update(ProcessExecutionLogHistory domain) {
		KfnmtProcessExecutionLogHistory newEntity = JpaProcessExecutionLogHistRepository.toEntity(domain);
		try {
			String updateTableSQL = " UPDATE KFNDT_AUTOEXEC_LOG_HIST SET"
					+ " OVERALL_STATUS = ?" 
					+ " ,ERROR_DETAIL = CAST(? as numeric) "
					+ " ,LAST_EXEC_DATETIME = ? " 
					+ " ,SCH_CREATE_START = ?" 
					+ " ,SCH_CREATE_END = ?"
					+ " ,DAILY_CREATE_START = ?"
					+ " ,DAILY_CREATE_END = ?"
					+ " ,DAILY_CALC_START = ?"
					+ " ,DAILY_CALC_END = ?"
					+ " ,RFL_APPR_START = ?"
					+ " ,RFL_APPR_END = ?"
					+ " ,LAST_END_EXEC_DATETIME = ?"
					+ " ,ERROR_SYSTEM = CAST(? as numeric)"
					+ " ,ERROR_BUSINESS = CAST(? as numeric)"
					+ " WHERE CID = ? AND EXEC_ITEM_CD = ? AND EXEC_ID = ?";
			try (PreparedStatement ps = this.connection().prepareStatement(JDBCUtil.toUpdateWithCommonField(updateTableSQL))) {
				ps.setInt(1, newEntity.overallStatus);
				ps.setString(2, newEntity.overallError == null ? null : newEntity.overallError.toString());
				if (this.database().is(DatabaseProduct.POSTGRESQL)) {
					ps.setTimestamp(3, newEntity.lastExecDateTime == null ? null : Timestamp.valueOf(newEntity.lastExecDateTime.localDateTime()));
				} else {
					ps.setString(3, newEntity.lastExecDateTime == null ? null : newEntity.lastExecDateTime.toString());
				}
				ps.setDate(4, newEntity.schCreateStart ==null?null: Date.valueOf(newEntity.schCreateStart.localDate()));
				ps.setDate(5, newEntity.schCreateEnd ==null?null: Date.valueOf(newEntity.schCreateEnd.localDate()));
				ps.setDate(6, newEntity.dailyCreateStart ==null?null: Date.valueOf(newEntity.dailyCreateStart.localDate()));
				ps.setDate(7, newEntity.dailyCreateEnd ==null?null: Date.valueOf(newEntity.dailyCreateEnd.localDate()));
				ps.setDate(8, newEntity.dailyCalcStart ==null?null: Date.valueOf(newEntity.dailyCalcStart.localDate()));
				ps.setDate(9, newEntity.dailyCalcEnd ==null?null: Date.valueOf(newEntity.dailyCalcEnd.localDate()));
				ps.setDate(10, newEntity.reflectApprovalResultStart ==null?null: Date.valueOf(newEntity.reflectApprovalResultStart.localDate()));
				ps.setDate(11, newEntity.reflectApprovalResultEnd ==null?null: Date.valueOf(newEntity.reflectApprovalResultEnd.localDate()));
				if (this.database().is(DatabaseProduct.POSTGRESQL)) {
					ps.setTimestamp(12, newEntity.lastEndExecDateTime == null ? null : Timestamp.valueOf(newEntity.lastEndExecDateTime.localDateTime()));
				} else {
					ps.setString(12, newEntity.lastEndExecDateTime == null ? null : newEntity.lastEndExecDateTime.toString());
				}
				ps.setString(13, newEntity.errorSystem == null?null:(newEntity.errorSystem ==1?"1":"0"));
				ps.setString(14, newEntity.errorBusiness == null?null:(newEntity.errorBusiness ==1?"1":"0"));
				ps.setString(15, domain.getCompanyId());
				ps.setString(16, domain.getExecItemCd().v());
				ps.setString(17, domain.getExecId());
				ps.executeUpdate();
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		try {
			for(KfnmtExecutionTaskLog kfnmtExecutionTaskLog : newEntity.taskLogList) {
				String updateTableSQL = " UPDATE KFNDT_AUTOEXEC_TASK_LOG SET"
						+ " STATUS = CAST(? as numeric)"
						+ " ,LAST_END_EXEC_DATETIME = ?"
						+ " ,ERROR_SYSTEM = CAST(? as numeric)"
						+ " ,ERROR_BUSINESS = CAST(? as numeric)"
						+ " , ERROR_SYSTEM_CONT = ?"
						+ " WHERE CID = ? AND EXEC_ITEM_CD = ? AND EXEC_ID = ? AND TASK_ID = ? ";
				try (PreparedStatement ps = this.connection().prepareStatement(JDBCUtil.toUpdateWithCommonField(updateTableSQL))) {
					ps.setString(1, kfnmtExecutionTaskLog.status ==null?null:kfnmtExecutionTaskLog.status.toString());
					if (this.database().is(DatabaseProduct.POSTGRESQL)) {
						ps.setTimestamp(2, kfnmtExecutionTaskLog.lastEndExecDateTime == null ? null : Timestamp.valueOf(kfnmtExecutionTaskLog.lastEndExecDateTime.localDateTime()));
					} else {
						ps.setString(2, kfnmtExecutionTaskLog.lastEndExecDateTime == null ? null : kfnmtExecutionTaskLog.lastEndExecDateTime.toString());
					}
					ps.setString(3, kfnmtExecutionTaskLog.errorSystem == null?null:(kfnmtExecutionTaskLog.errorSystem ==1?"1":"0"));
					ps.setString(4, kfnmtExecutionTaskLog.errorBusiness == null?null:(kfnmtExecutionTaskLog.errorBusiness ==1?"1":"0"));
					ps.setString(5, kfnmtExecutionTaskLog.errorSystemDetail == null?null:(kfnmtExecutionTaskLog.errorSystemDetail));
					ps.setString(6, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.companyId);
					ps.setString(7, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.execItemCd);
					ps.setString(8, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.execId);
					ps.setInt(9, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.taskId);
					ps.executeUpdate();
				}
			}
			
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void remove(String companyId, String execItemCd) {
		 List<KfnmtProcessExecutionLogHistory> list = this.queryProxy().query(SELECT_All_BY_CID_EXECCD, KfnmtProcessExecutionLogHistory.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd).getList();
		 if (!CollectionUtil.isEmpty(list)) {
				this.commandProxy().removeAll(list);
			}
	}
	
	@Override
	public List<ProcessExecutionLogHistory> getByDate(String companyId, String execItemCd,
			GeneralDateTime lastExecDateTime) {
		GeneralDateTime nextExecDateTime = lastExecDateTime.addDays(1);
		
		return this.queryProxy().query(SELECT_All_BY_CID_EXECCD_DATE, KfnmtProcessExecutionLogHistory.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd)
				.setParameter("lastExecDateTime", lastExecDateTime)
				.setParameter("nextExecDateTime", nextExecDateTime)
				.getList(ProcessExecutionLogHistory::createFromMemento);
	}

	@Override
	public List<ProcessExecutionLogHistory> getByDateRange(String companyId, String execItemCd,
			GeneralDateTime startDate, GeneralDateTime endDate) {
		GeneralDateTime endDate1 = endDate.addDays(1);
		
		return this.queryProxy().query(SELECT_All_BY_CID_EXECCD_DATE_RANGE, KfnmtProcessExecutionLogHistory.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate1)
				.getList(ProcessExecutionLogHistory::createFromMemento);
	}

	@Override
	public List<ProcessExecutionLogHistory> getByCompanyIdAndDateAndEmployeeName(String companyId, GeneralDateTime startDateTime, GeneralDateTime endDateTime) {
		endDateTime = endDateTime.addDays(1).addSeconds(-1);
		return this.queryProxy().query(SELECT_ALL_BY_CID_START_DATE_END_DATE, KfnmtProcessExecutionLogHistory.class)
				.setParameter("companyId", companyId)
				.setParameter("startDate", startDateTime)
				.setParameter("endDate", endDateTime)
				.getList(ProcessExecutionLogHistory::createFromMemento);
	}

	@Override
	public List<ProcessExecutionLogHistory> getByCompanyIdAndExecItemCd(String companyId, String execItemCd) {
		return this.queryProxy().query(SELECT_ALL_BY_CID_AND_EXECCD, KfnmtProcessExecutionLogHistory.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd)
				.getList(ProcessExecutionLogHistory::createFromMemento);
	}

	@Override
	public Optional<ProcessExecutionLogHistory> getByExecId(String execId) {
		return this.queryProxy().query(SELECT_BY_EXEC_ID, KfnmtProcessExecutionLogHistory.class)
				.setParameter("execId", execId)
				.getSingle(ProcessExecutionLogHistory::createFromMemento);
	}
}
