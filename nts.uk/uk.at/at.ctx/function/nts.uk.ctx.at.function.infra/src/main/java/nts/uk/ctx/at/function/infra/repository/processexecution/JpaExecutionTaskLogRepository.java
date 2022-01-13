package nts.uk.ctx.at.function.infra.repository.processexecution;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.EndStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ExecutionTaskLog;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionTask;
import nts.uk.ctx.at.function.dom.processexecution.repository.ExecutionTaskLogRepository;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtExecutionTaskLog;
import nts.uk.ctx.at.function.infra.entity.processexecution.KfnmtExecutionTaskLogPK;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.jdbc.JDBCUtil;

@Stateless
public class JpaExecutionTaskLogRepository extends JpaRepository implements ExecutionTaskLogRepository {
	/**
	 * Query strings
	 */
	private static final String SELECT_ALL = "SELECT etl FROM KfnmtExecutionTaskLog etl ";
	private static final String SELECT_LIST = SELECT_ALL + "WHERE etl.kfnmtExecTaskLogPK.companyId = :companyId "
			+ "AND etl.kfnmtExecTaskLogPK.execItemCd = :execItemCd " + "AND etl.kfnmtExecTaskLogPK.execId = :execId ";

	@Override
	public List<ExecutionTaskLog> getAllByCidExecCdExecId(String companyId, String execItemCd, String execId) {
		return this.queryProxy().query(SELECT_LIST, KfnmtExecutionTaskLog.class)
				.setParameter("companyId", companyId)
				.setParameter("execItemCd", execItemCd)
				.setParameter("execId", execId)
				.getList(entity -> ExecutionTaskLog.builder()
						.execId(execId)
						.errorBusiness(Optional.ofNullable(entity.errorBusiness).map(data -> data == 1))
						.errorSystem(Optional.ofNullable(entity.errorSystem).map(data -> data == 1))
						.lastEndExecDateTime(Optional.ofNullable(entity.lastEndExecDateTime))
						.lastExecDateTime(Optional.ofNullable(entity.lastExecDateTime))
						.procExecTask(EnumAdaptor.valueOf(entity.kfnmtExecTaskLogPK.taskId, ProcessExecutionTask.class))
						.status(Optional.ofNullable(entity.status).map(data -> EnumAdaptor.valueOf(data, EndStatus.class)))
						.systemErrorDetails(Optional.ofNullable(entity.errorSystemDetail)).build());
	}

	/**
	 * insert task log list
	 */
	@Override
	public void insertAll(String companyId, String execItemCd, String execId, List<ExecutionTaskLog> taskLogList) {
		this.commandProxy()
				.insertAll(taskLogList.stream()
						.map(item -> KfnmtExecutionTaskLog.builder()
								.kfnmtExecTaskLogPK(new KfnmtExecutionTaskLogPK(companyId, execItemCd, execId, item.getProcExecTask().value))
								.status((item.getStatus() != null && item.getStatus().isPresent())
										? item.getStatus().get().value
										: null)
								.build())
						.collect(Collectors.toList()));
	}

	/**
	 * update task log list
	 */
	@Override
	public void updateAll(String companyId, String execItemCd, String execId, List<ExecutionTaskLog> taskLogList) {
		try {
			for (ExecutionTaskLog executionTaskLog : taskLogList) {
				String updateTableSQL = " UPDATE KFNDT_AUTOEXEC_TASK_LOG SET" + " STATUS = CAST(? as numeric)"
						+ " WHERE CID = ? AND EXEC_ITEM_CD = ? AND EXEC_ID = ? AND TASK_ID = ? ";
				try (PreparedStatement ps = this.connection()
						.prepareStatement(JDBCUtil.toUpdateWithCommonField(updateTableSQL))) {
					ps.setString(1,
							(executionTaskLog.getStatus() != null && executionTaskLog.getStatus().isPresent())
									? String.valueOf(executionTaskLog.getStatus().get().value)
									: null);
					ps.setString(2, companyId);
					ps.setString(3, execItemCd);
					ps.setString(4, execId);
					ps.setInt(5, executionTaskLog.getProcExecTask().value);
					ps.executeUpdate();
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeAll(String companyId, String execItemCd, String execId, List<ExecutionTaskLog> taskLogList) {
		List<Integer> taskLogIds = this.getAllByCidExecCdExecId(companyId, execItemCd, execId).stream()
				.map(data -> data.getProcExecTask().value).collect(Collectors.toList());
		taskLogList = taskLogList.stream().filter(data -> taskLogIds.contains(data.getProcExecTask().value))
				.collect(Collectors.toList());
		taskLogList.forEach(data -> this.commandProxy().remove(KfnmtExecutionTaskLog.class,
				new KfnmtExecutionTaskLogPK(companyId, execItemCd, execId, data.getProcExecTask().value)));
	}
}
