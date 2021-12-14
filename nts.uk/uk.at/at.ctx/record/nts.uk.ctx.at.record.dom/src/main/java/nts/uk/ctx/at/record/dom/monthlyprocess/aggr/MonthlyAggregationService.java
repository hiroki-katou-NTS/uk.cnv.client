package nts.uk.ctx.at.record.dom.monthlyprocess.aggr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.Getter;
import lombok.val;
import nts.arc.diagnose.stopwatch.concurrent.ConcurrentStopwatches;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.output.ExecutionAttr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.EmployeeExecutionStatus;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExeStateOfCalAndSum;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionStatus;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.MonthlyAggregationErrorInfo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;

/**
 * ドメインサービス：月次集計
 * @author shuichu_ishida
 */
public class MonthlyAggregationService {

	/**
	 * Managerクラス
	 * @param asyncContext 同期コマンドコンテキスト
	 * @param companyId 会社ID
	 * @param employeeIds 社員IDリスト
	 * @param datePeriod 期間
	 * @param executionAttr 実行区分　（手動、自動）
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID
	 * @param executionLog 実行ログ
	 */
	@SuppressWarnings("rawtypes")
	public static AggregationResult manager(RequireM1 require, CacheCarrier cacheCarrier, AsyncCommandHandlerContext asyncContext, 
			String companyId, List<String> employeeIds,
			DatePeriod datePeriod, ExecutionAttr executionAttr, String empCalAndSumExecLogID,
			Optional<ExecutionLog> executionLog) {
		
		ProcessState success = ProcessState.SUCCESS;
		List<AtomTask> atomTasks = Collections.synchronizedList(new ArrayList<>());
		
		// 実行状態　初期設定
		val dataSetter = asyncContext.getDataSetter();
		dataSetter.setData("monthlyAggregateCount", 0);
		dataSetter.setData("monthlyAggregateHasError", ErrorPresent.NO_ERROR.nameId);

		// 月次集計を実行するかチェックする
		// ※　実行しない時、終了状態＝正常終了
		if (!executionLog.isPresent()) return AggregationResult.build(success);
		if (executionLog.get().getExecutionContent() != ExecutionContent.MONTHLY_AGGREGATION) return AggregationResult.build(success);
		if (!executionLog.get().getMonlyAggregationSetInfo().isPresent()) return AggregationResult.build(success);
		val executionContent = executionLog.get().getExecutionContent();
		
		// 再実行の判断
		ExecutionType reAggrAtr = executionLog.get().getMonlyAggregationSetInfo().get().getExecutionType();
		
		// ログ情報（実行ログ）を更新する
		atomTasks.add(AtomTask.of(() -> require.updateLogInfo(empCalAndSumExecLogID, executionContent.value,
				ExecutionStatus.PROCESSING.value)));
		
		// システム日付を取得する
		val criteriaDate = GeneralDate.today();
		
		// 対象社員を判断
		List<String> procEmployeeIds = new ArrayList<>();
		if (executionAttr == ExecutionAttr.AUTO){
			
			// 自動実行の時
			//*****（未）　社員IDリストを得るアルゴリズムの引数設計に不整合があるので、確認待ち。以下、仮対応。
			procEmployeeIds = employeeIds;
		}
		else {
			
			// 手動実行の時
			procEmployeeIds = employeeIds;
		}
		
		ConcurrentStopwatches.start("08000:会社別データ読み込み");
		
		// 月別集計で必要な会社別設定を取得する
		MonAggrCompanySettings companySets = MonAggrCompanySettings.loadSettings(require, companyId);
		if (companySets.getErrorInfos().size() > 0){
			
			// エラー処理
			List<MonthlyAggregationErrorInfo> errorInfoList = new ArrayList<>();
			for (val errorInfo : companySets.getErrorInfos().entrySet()){
				errorInfoList.add(new MonthlyAggregationErrorInfo(errorInfo.getKey(), errorInfo.getValue()));
			}
			atomTasks.add(errorProc(require, dataSetter, procEmployeeIds.get(0),
									empCalAndSumExecLogID, criteriaDate, errorInfoList));
			
			// 処理を完了する
			dataSetter.updateData("monthlyAggregateStatus", ExecutionStatus.DONE.nameId);

			atomTasks.add(AtomTask.of(() -> require.updateLogInfo(empCalAndSumExecLogID, executionContent.value,
					ExecutionStatus.DONE.value)));

			return AggregationResult.build(success, atomTasks);
		}
		
		ConcurrentStopwatches.stop("08000:会社別データ読み込み");
		
		// 社員の数だけループ　（並列処理対象）
		StateHolder stateHolder = new StateHolder(employeeIds.size());
		require.parallelContext().forEach(employeeIds, employeeId -> {
			
			Optional<GeneralDate> date = require.getProcessingDate(employeeId, criteriaDate);
            if(!date.isPresent()) {
                return;
            }
            
			if (stateHolder.isInterrupt()) return;
		
			ConcurrentStopwatches.start("10000:社員ごと：" + employeeId);
			
			// 社員1人分の処理　（社員の月別実績を集計する）
			MonthlyAggregationEmployeeService.AggregationResult aggrStatus = MonthlyAggregationEmployeeService.aggregate(
					require, cacheCarrier, asyncContext,
					companyId, employeeId, date.get(), empCalAndSumExecLogID, reAggrAtr, companySets);
			ProcessState coStatus = aggrStatus.getStatus().getState();
//			atomTasks.addAll(aggrStatus.getAtomTasks());
			stateHolder.add(coStatus);

			ConcurrentStopwatches.stop("10000:社員ごと：" + employeeId);

			// 終了状態を確認する
			if (coStatus == ProcessState.SUCCESS){
				
				// 成功時、ログ情報（実行内容の完了状態(（社員別））を更新する
				aggrStatus.getAtomTasks().add(AtomTask.of(() -> require.updateLogWithContent(employeeId, empCalAndSumExecLogID,
						ExecutionContent.MONTHLY_AGGREGATION.value, EmployeeExecutionStatus.COMPLETE.value)));
				
				/** 永続化 */
				require.transaction(AtomTask.bundle(aggrStatus.getAtomTasks()));
				dataSetter.updateData("monthlyAggregateCount", stateHolder.count());
			}
			if (coStatus == ProcessState.INTERRUPTION){
				
				// 中断時
				dataSetter.updateData("monthlyAggregateStatus", ExecutionStatus.INCOMPLETE.nameId);
				return;
			}
		});
		
		//ConcurrentStopwatches.printAll();
		ConcurrentStopwatches.STOPWATCHES.clear();
		
		if (stateHolder.isInterrupt()){
			atomTasks.add(AtomTask.of(() -> require.updateLogInfo(empCalAndSumExecLogID, 
					executionContent.value, ExecutionStatus.INCOMPLETE.value)));
			dataSetter.updateData("monthlyAggregateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
			return AggregationResult.build(ProcessState.INTERRUPTION, atomTasks);
		}
		
		// 処理を完了する
		atomTasks.add(AtomTask.of(() -> require.updateLogInfo(empCalAndSumExecLogID, 
				executionContent.value, ExecutionStatus.DONE.value)));

		dataSetter.updateData("monthlyAggregateCount", stateHolder.count());
		dataSetter.updateData("monthlyAggregateStatus", ExecutionStatus.DONE.nameId);
		
		return AggregationResult.build(success, atomTasks);
	}
	
	/**
	 * 状態保存
	 * @author shuichu_ishida
	 */
	private static class StateHolder {
		private BlockingQueue<ProcessState> status;
		
		public StateHolder(int max) {
			this.status = new ArrayBlockingQueue<>(max);
		}
		
		public void add(ProcessState status) { this.status.add(status); }
		
		public int count() { return this.status.size(); }
		
		public boolean isInterrupt() {
			return this.status.stream().filter(c -> c == ProcessState.INTERRUPTION).findFirst().isPresent();
		}
	}
	
	/**
	 * エラー処理
	 * @param dataSetter データセッター
	 * @param employeeId 社員ID
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID
	 * @param outYmd 出力年月日
	 * @param errorInfoList エラー情報リスト
	 */
	private static AtomTask errorProc(RequireM2 require,
			TaskDataSetter dataSetter,
			String employeeId,
			String empCalAndSumExecLogID,
			GeneralDate outYmd,
			List<MonthlyAggregationErrorInfo> errorInfoList){
		
		// 「エラーあり」に更新
		dataSetter.updateData("monthlyAggregateHasError", ErrorPresent.HAS_ERROR.nameId);
		
		// エラー出力
		errorInfoList.sort((a, b) -> a.getResourceId().compareTo(b.getResourceId()));
		return AtomTask.of(() -> {
			for (val errorInfo : errorInfoList){
				require.add(new ErrMessageInfo(
						employeeId,
						empCalAndSumExecLogID,
						new ErrMessageResource(errorInfo.getResourceId()),
						ExecutionContent.MONTHLY_AGGREGATION,
						outYmd,
						errorInfo.getMessage()));
			}
		});
	}

	@Getter
	public static class AggregationResult {
		
		private ProcessState status;
		
		private List<AtomTask> atomTasks;
		
		private AggregationResult (ProcessState status, List<AtomTask> atomTasks) {
			this.atomTasks = atomTasks;
			this.status = status;
		}
		
		static AggregationResult build(ProcessState status) {
			return new AggregationResult(status, new ArrayList<>());
		}
		
		static AggregationResult build(ProcessState status, List<AtomTask> atomTasks) {
			return new AggregationResult(status, atomTasks);
		}
		
		public AggregationResult newAtomTask(AtomTask task) {
			this.atomTasks.add(task);
			return this;
		}
	}

	public static interface RequireM1 extends MonAggrCompanySettings.RequireM6, RequireM2,
		MonthlyAggregationEmployeeService.RequireM1 {
		
		Optional<GeneralDate> getProcessingDate(String employeeId, GeneralDate date);
		
		void updateLogInfo(String empCalAndSumExecLogID,int executionContent, int processStatus);
		
		ManagedParallelWithContext parallelContext();
		
		void updateLogWithContent(String employeeID, String empCalAndSumExecLogId, int executionContent, int state);
		
		void transaction(AtomTask task);
	}
	
	public static interface RequireM2 extends MonthlyAggregationErrorService.RequireM1 {}
}
