package nts.uk.ctx.at.function.infra.repository.processexecution;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLog;
//import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogRepository;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtExecutionTaskLog;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtProcessExecutionLog;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtProcessExecutionLogPK;
//import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtProcessExecutionLogManage;
//import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtProcessExecutionLogPK;
import nts.uk.shr.infra.data.jdbc.JDBCUtil;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class JpaProcessExecutionLogRepository extends JpaRepository
		implements ProcessExecutionLogRepository {
	/**
	 * Query strings
	 */
	private static final String SELECT_ALL = "SELECT pel FROM KfnmtProcessExecutionLog pel ";
	private static final String SELECT_All_BY_CID = SELECT_ALL
			+ "WHERE pel.kfnmtProcExecLogPK.companyId = :companyId ORDER BY pel.kfnmtProcExecLogPK.execItemCd ASC";
	private static final String SELECT_All_BY_CID_AND_EXEC = SELECT_ALL
			+ " WHERE pel.kfnmtProcExecLogPK.companyId = :companyId"
			+ " AND pel.kfnmtProcExecLogPK.execItemCd IN :execItemCds"
			+ " ORDER BY pel.kfnmtProcExecLogPK.execItemCd ASC";
	
	private static final String SELECT_BY_PK = SELECT_ALL
			+ "WHERE pel.kfnmtProcExecLogPK.companyId = :companyId "
			+ "AND pel.kfnmtProcExecLogPK.execItemCd = :execItemCd "
			+ "AND pel.execId = :execId ";
	
	private static final String SELECT_TASK_LOG = "SELECT k FROM KfnmtExecutionTaskLog k"+ 
	" WHERE k.kfnmtExecTaskLogPK.companyId = :companyId " + " AND k.kfnmtExecTaskLogPK.execItemCd= :execItemCd ";
	
	
	private static final String SELECT_BY_CID_AND_EXEC_CD = SELECT_ALL
			+ "WHERE pel.kfnmtProcExecLogPK.companyId = :companyId "
			+ "AND pel.kfnmtProcExecLogPK.execItemCd = :execItemCd ";
	
	private static final String SELECT_BY_KEY_NATIVE = "SELECT * FROM KFNDT_AUTOEXEC_LOG as pel WITH (READUNCOMMITTED)"
			+ "WHERE pel.CID = ? "
			+ "AND pel.EXEC_ITEM_CD = ? ";

	@Override
	public List<ProcessExecutionLog> getProcessExecutionLogByCompanyId(String companyId) {
		return this.queryProxy().query(SELECT_All_BY_CID, KfnmtProcessExecutionLog.class)
				.setParameter("companyId", companyId).getList(c -> c.toDomain());
	}

	@Override
	public List<ProcessExecutionLog> getProcessExecutionLogByCompanyIdAndExecItemCd(String companyId,
			List<String> execItemCds) {
		return this.queryProxy().query(SELECT_All_BY_CID_AND_EXEC, KfnmtProcessExecutionLog.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCds", execItemCds)
				.getList(c -> c.toDomain());
	}
	
	@Override
	public Optional<ProcessExecutionLog> getLogByCIdAndExecCd(String companyId, String execItemCd, String execId) {
		return this.queryProxy().query(SELECT_BY_PK, KfnmtProcessExecutionLog.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd)
				.setParameter("execId", execId).getSingle(c -> c.toDomain());
	}
	
	@Override
	public void insert(ProcessExecutionLog domain) {
		this.commandProxy().insert(KfnmtProcessExecutionLog.toEntity(domain));
	}
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void update(ProcessExecutionLog domain) {
		KfnmtProcessExecutionLog updateData = KfnmtProcessExecutionLog.toEntity(domain);
//		Optional<KfnmtProcessExecutionLog> oldDataOtp = this.queryProxy().find(updateData.kfnmtProcExecLogPK, KfnmtProcessExecutionLog.class);
//		if(!oldDataOtp.isPresent())
//			return;
//		KfnmtProcessExecutionLog oldData = oldDataOtp.get();
//		oldData.schCreateStart = updateData.schCreateStart;
//		oldData.schCreateEnd = updateData.schCreateEnd;
//		oldData.dailyCreateStart = updateData.dailyCreateStart;
//		oldData.dailyCreateEnd = updateData.dailyCreateEnd;
//		oldData.dailyCalcStart = updateData.dailyCalcStart;
//		oldData.dailyCalcEnd = updateData.dailyCalcEnd;
//		oldData.reflectApprovalResultStart = updateData.reflectApprovalResultStart;
//		oldData.reflectApprovalResultEnd = updateData.reflectApprovalResultEnd;
//		oldData.taskLogList = updateData.taskLogList;
//		this.commandProxy().update(oldData);
		try {
			String updateTableSQL = " UPDATE KFNDT_AUTOEXEC_LOG SET"
					+ " SCH_CREATE_START = ?" 
					+ " ,SCH_CREATE_END = ? "
					+ " ,DAILY_CREATE_START = ? " 
					+ " ,DAILY_CREATE_END = ?" 
					+ " ,DAILY_CALC_START = ?"
					+ " ,DAILY_CALC_END = ?"
					+ " ,RFL_APPR_START = ?"
					+ " ,RFL_APPR_END = ?"
					+ " WHERE CID = ? AND EXEC_ITEM_CD = ? AND EXEC_ID = ? ";
			try (PreparedStatement ps = this.connection().prepareStatement(JDBCUtil.toUpdateWithCommonField(updateTableSQL))) {
				ps.setDate(1, updateData.schCreateStart ==null?null: Date.valueOf(updateData.schCreateStart.localDate()));
				ps.setDate(2, updateData.schCreateEnd ==null?null: Date.valueOf(updateData.schCreateEnd.localDate()));
				ps.setDate(3, updateData.dailyCreateStart ==null?null: Date.valueOf(updateData.dailyCreateStart.localDate()));
				ps.setDate(4, updateData.dailyCreateEnd ==null?null: Date.valueOf(updateData.dailyCreateEnd.localDate()));
				ps.setDate(5, updateData.dailyCalcStart ==null?null: Date.valueOf(updateData.dailyCalcStart.localDate()));
				ps.setDate(6, updateData.dailyCalcEnd ==null?null: Date.valueOf(updateData.dailyCalcEnd.localDate()));
				ps.setDate(7, updateData.reflectApprovalResultStart ==null?null: Date.valueOf(updateData.reflectApprovalResultStart.localDate()));
				ps.setDate(8, updateData.reflectApprovalResultEnd ==null?null: Date.valueOf(updateData.reflectApprovalResultEnd.localDate()));
				ps.setString(9, domain.getCompanyId());
				ps.setString(10, domain.getExecItemCd().v());
				ps.setString(11, domain.getExecId());
				ps.executeUpdate();
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			for(KfnmtExecutionTaskLog kfnmtExecutionTaskLog : updateData.taskLogList) {
				String updateTableSQL = " UPDATE KFNDT_AUTOEXEC_TASK_LOG SET"
						+ " STATUS = CAST(? as numeric)"
						+ " ,LAST_EXEC_DATETIME = ?"
						+ " ,LAST_END_EXEC_DATETIME = ?"
						+ " ,ERROR_SYSTEM = CAST(? as numeric)"
						+ " ,ERROR_BUSINESS = CAST(? as numeric)"
						+ " , ERROR_SYSTEM_CONT = ?"
						+ " WHERE CID = ? AND EXEC_ITEM_CD = ? AND EXEC_ID = ? AND TASK_ID = ? ";
				try (PreparedStatement ps = this.connection().prepareStatement(JDBCUtil.toUpdateWithCommonField(updateTableSQL))) {
					ps.setString(1, kfnmtExecutionTaskLog.status ==null?null:kfnmtExecutionTaskLog.status.toString());
					if (this.database().is(DatabaseProduct.POSTGRESQL)) {
						ps.setTimestamp(2, kfnmtExecutionTaskLog.lastExecDateTime == null ? null : Timestamp.valueOf(kfnmtExecutionTaskLog.lastExecDateTime.localDateTime()));
					} else {
						ps.setString(2, kfnmtExecutionTaskLog.lastExecDateTime == null ? null : kfnmtExecutionTaskLog.lastExecDateTime.toString());
					}
					if (this.database().is(DatabaseProduct.POSTGRESQL)) {
						ps.setTimestamp(3, kfnmtExecutionTaskLog.lastEndExecDateTime == null ? null : Timestamp.valueOf(kfnmtExecutionTaskLog.lastEndExecDateTime.localDateTime()));
					} else {
						ps.setString(3, kfnmtExecutionTaskLog.lastEndExecDateTime == null ? null : kfnmtExecutionTaskLog.lastEndExecDateTime.toString());
					}
					ps.setString(4, kfnmtExecutionTaskLog.errorSystem == null?null:(kfnmtExecutionTaskLog.errorSystem ==1?"1":"0"));
					ps.setString(5, kfnmtExecutionTaskLog.errorBusiness == null?null:(kfnmtExecutionTaskLog.errorBusiness ==1?"1":"0"));
					ps.setString(6, kfnmtExecutionTaskLog.errorSystemDetail == null?null:(kfnmtExecutionTaskLog.errorSystemDetail));
					ps.setString(7, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.companyId);
					ps.setString(8, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.execItemCd);
					ps.setString(9, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.execId);
					ps.setInt(10, kfnmtExecutionTaskLog.kfnmtExecTaskLogPK.taskId);
					ps.executeUpdate();
				}
			}
			
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public void remove(String companyId, String execItemCd, String execId) {	
		this.commandProxy().remove(KfnmtProcessExecutionLog.class, new KfnmtProcessExecutionLogPK(companyId, execItemCd));
	}

	@Override
	public void removeList(String companyId, String execItemCd) {
		List<KfnmtProcessExecutionLog> logList = this.queryProxy().query(SELECT_BY_CID_AND_EXEC_CD, KfnmtProcessExecutionLog.class)
														.setParameter("companyId", companyId)
														.setParameter("execItemCd", execItemCd).getList();
		if (!CollectionUtil.isEmpty(logList)) {
			this.commandProxy().removeAll(logList);
		}
	}

	@Override
	public Optional<ProcessExecutionLog> getLog(String companyId, String execItemCd) {
		 List<KfnmtExecutionTaskLog> list = this.queryProxy().query(SELECT_TASK_LOG, KfnmtExecutionTaskLog.class)
				.setParameter("companyId", companyId).setParameter("execItemCd", execItemCd).getList();
		if(!list.isEmpty()){
			 return getProcessExecutionLog(companyId, execItemCd)
					 .map(entity -> entity.toDomainMaxDate(list));
		} else {
			return getProcessExecutionLog(companyId, execItemCd)
					.map(KfnmtProcessExecutionLog::toDomainMaxDate);
		}
	}
	
	@Override
	public Optional<ProcessExecutionLog> getLogReadUncommit(String companyId, String execItemCd){
		Query query = this.getEntityManager().createNativeQuery(SELECT_BY_KEY_NATIVE, KfnmtProcessExecutionLog.class)
				.setParameter(1, companyId).setParameter(2, execItemCd);
		@SuppressWarnings("unchecked")
		List<KfnmtProcessExecutionLog> resultList = query.getResultList();
		List<ProcessExecutionLog> lstProcessExecutionLog = new ArrayList<ProcessExecutionLog>();
		resultList.forEach(x->{
			lstProcessExecutionLog.add(x.toDomainMaxDate());
		});
		 if(lstProcessExecutionLog!=null && !lstProcessExecutionLog.isEmpty()){
				return Optional.ofNullable(lstProcessExecutionLog.get(0));
			 }
		 return Optional.empty();
	}
	
	private Optional<KfnmtProcessExecutionLog> getProcessExecutionLog(String companyId,String execItemCd){
		return this.queryProxy()
				.find(new KfnmtProcessExecutionLogPK(companyId, execItemCd), KfnmtProcessExecutionLog.class);
	}
	
}

