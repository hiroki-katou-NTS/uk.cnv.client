package nts.uk.ctx.at.record.dom.monthlyprocess.aggr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.OptimisticLockException;

import lombok.Getter;
import lombok.val;
import nts.arc.diagnose.stopwatch.concurrent.ConcurrentStopwatches;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.error.ThrowableAnalyzer;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.AchievementAtr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.GetPeriodCanProcesse;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.IgnoreFlagDuringLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.closegetunlockedperiod.ClosingGetUnlockedPeriod;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.AggrPeriodEachActualClosure;
import nts.uk.ctx.at.record.dom.monthlycommon.aggrperiod.GetClosurePeriod;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLock;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.LockStatus;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExeStateOfCalAndSum;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsRecRemainMngOfInPeriod;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffRemainMngOfInPeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordService;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.AggregateMonthlyRecordValue;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.MonthlyAggregationErrorInfo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrCompanySettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.work.MonAggrEmployeeSettings;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.IntegrationOfMonthly;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * ドメインサービス：月別集計　（社員の月別実績を集計する）
 * @author shuichi_ishida
 */
public class MonthlyAggregationEmployeeService {
	/**
	 * 社員の月別実績を集計する
	 * @param asyncContext 同期コマンドコンテキスト
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param criteriaDate 基準日
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID
	 * @param executionType 実行種別　（通常、再実行）
	 * @return 終了状態
	 */
	@SuppressWarnings("rawtypes")
	public static AggregationResult aggregate(RequireM1 require, CacheCarrier cacheCarrier,
			Optional<AsyncCommandHandlerContext> asyncContext, String companyId, String employeeId,
			GeneralDate criteriaDate, String empCalAndSumExecLogID, ExecutionType executionType,
			Optional<Boolean> canAggrWhenLock) {

		ProcessState status = ProcessState.SUCCESS;

		// 月別集計で必要な会社別設定を取得する
		val companySets = MonAggrCompanySettings.loadSettings(require, companyId);
		if (companySets.getErrorInfos().size() > 0){

			// エラー処理
			List<MonthlyAggregationErrorInfo> errorInfoList = new ArrayList<>();
			for (val errorInfo : companySets.getErrorInfos().entrySet()){
				errorInfoList.add(new MonthlyAggregationErrorInfo(errorInfo.getKey(), errorInfo.getValue()));
			}

			return AggregationResult.build(status).newAtomTask(
					errorProc(require, asyncContext.map(c -> c.getDataSetter()), 
							employeeId, empCalAndSumExecLogID, criteriaDate, errorInfoList));
		}

		val aggrStatus = aggregate(require, cacheCarrier, asyncContext, companyId, employeeId, criteriaDate,
				empCalAndSumExecLogID, executionType, companySets, canAggrWhenLock);

		return aggrStatus;
	}

	/**
	 * 社員の月別実績を集計する
	 * @param asyncContext 同期コマンドコンテキスト
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param criteriaDate 基準日
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID
	 * @param executionType 実行種別　（通常、再実行）
	 * @param companySets 月別集計で必要な会社別設定
	 * @return 終了状態
	 */
	@SuppressWarnings("rawtypes")
	public static AggregationResult aggregate(RequireM1 require, CacheCarrier cacheCarrier,
			Optional<AsyncCommandHandlerContext> asyncContext, String companyId, String employeeId,
			GeneralDate criteriaDate, String empCalAndSumExecLogID, ExecutionType executionType,
			MonAggrCompanySettings companySets, Optional<Boolean> canAggrWhenLock) {

		MonthlyAggrEmpServiceValue status = new MonthlyAggrEmpServiceValue();
		val dataSetter = asyncContext.map(c -> c.getDataSetter());

		// 前回集計結果を初期化する
		Optional<AbsRecRemainMngOfInPeriod> prevAbsRecResultOpt = Optional.empty();
		Optional<BreakDayOffRemainMngOfInPeriod> prevBreakDayOffresultOpt = Optional.empty();

		ConcurrentStopwatches.start("11000:集計期間の判断：");

		// 集計期間の判断　（実締め毎集計期間だけをすべて取り出す）
		List<AggrPeriodEachActualClosure> aggrPeriods = new ArrayList<>();
		val closurePeriods = GetClosurePeriod.get(require, companyId, employeeId, criteriaDate,
				Optional.empty(), Optional.empty(), Optional.empty());
		for (val closurePeriod : closurePeriods) aggrPeriods.addAll(closurePeriod.getAggrPeriods());

		// 残数処理を行う期間を計算　（Redmine#107271、EA#3434）
		DatePeriod remainPeriod = calcPeriodForRemainingProc(companySets, aggrPeriods);

		// 全体の期間を求める
		DatePeriod allPeriod = new DatePeriod(GeneralDate.today(), GeneralDate.today());
		if (aggrPeriods.size() > 0){
			val headPeriod = aggrPeriods.get(0).getPeriod();
			allPeriod = new DatePeriod(headPeriod.start(), headPeriod.end());
			for (val aggrPeriod : aggrPeriods){
				GeneralDate startYmd = allPeriod.start();
				GeneralDate endYmd = allPeriod.end();
				if (startYmd.after(aggrPeriod.getPeriod().start())) startYmd = aggrPeriod.getPeriod().start();
				if (endYmd.before(aggrPeriod.getPeriod().end())) endYmd = aggrPeriod.getPeriod().end();
				allPeriod = new DatePeriod(startYmd, endYmd);
			}

			// 前月の36協定の集計があり得るため、1か月前まで読み込む　（Redmine#107701）
			allPeriod = new DatePeriod(allPeriod.start().addMonths(-1), allPeriod.end());
		}

		// 月別集計で必要な社員別設定を取得
		val employeeSets = MonAggrEmployeeSettings.loadSettings(require, cacheCarrier,
				companyId, employeeId, allPeriod);
		if (employeeSets.getErrorInfos().size() > 0){

			// エラー処理
			List<MonthlyAggregationErrorInfo> errorInfoList = new ArrayList<>();
			for (val errorInfo : employeeSets.getErrorInfos().entrySet()){
				errorInfoList.add(new MonthlyAggregationErrorInfo(errorInfo.getKey(), errorInfo.getValue()));
			}
			return AggregationResult.build(status)
					.newAtomTask(errorProc(require, dataSetter, employeeId, empCalAndSumExecLogID,
											criteriaDate, errorInfoList));
		}

		ConcurrentStopwatches.stop("11000:集計期間の判断：");

		List<AtomTask> atomTasks = new ArrayList<>();
		
		//get ロック中の計算/集計できるか
		Optional<ExecutionLog> executionLog = require.getByExecutionContent(empCalAndSumExecLogID, ExecutionContent.MONTHLY_AGGREGATION.value);
		
		IgnoreFlagDuringLock ignoreFlagDuringLock = executionLog.flatMap(c -> c.getIsCalWhenLock())
				.map(c -> c ? IgnoreFlagDuringLock.CAN_CAL_LOCK : IgnoreFlagDuringLock.CANNOT_CAL_LOCK)
				.orElse(IgnoreFlagDuringLock.CANNOT_CAL_LOCK);

//		List<BsEmploymentHistoryImport> employments = employeeSets.getEmployments();
		
		for (val aggrPeriod : aggrPeriods){
			val yearMonth = aggrPeriod.getYearMonth();
			val closureId = aggrPeriod.getClosureId();
			val closureDate = aggrPeriod.getClosureDate();
			val datePeriod = aggrPeriod.getPeriod();
			//ConcurrentStopwatches.start("12000:集計期間ごと：" + aggrPeriod.getYearMonth().toString());

			// 中断依頼が出されているかチェックする
//			if (asyncContext.hasBeenRequestedToCancel()) {
//				status.setState(ProcessState.INTERRUPTION);
//				return status;
//			}

			// 「就業計算と集計実行ログ」を取得し、実行状況を確認する
			val exeLogOpt = require.calAndSumExeLog(empCalAndSumExecLogID);
//			if (!exeLogOpt.isPresent()){
//				status.setState(ProcessState.INTERRUPTION);
//				return AggregationResult.build(status);
//			}
			if (exeLogOpt.isPresent() && exeLogOpt.get().getExecutionStatus().isPresent()){
				val executionStatus = exeLogOpt.get().getExecutionStatus().get();
				if (executionStatus == ExeStateOfCalAndSum.START_INTERRUPTION){
					status.setState(ProcessState.INTERRUPTION);
					return AggregationResult.build(status);
				}
			}
			
			//実績締めロックされない期間を取得する
			List<DatePeriod> listPeriod = ClosingGetUnlockedPeriod.get(require, datePeriod, closureId.value, ignoreFlagDuringLock, AchievementAtr.MONTHLY);
			if(listPeriod.isEmpty()) {
				continue;
			}
			for(DatePeriod periodNew : listPeriod) {
				// 再実行の場合
				if (executionType == ExecutionType.RERUN){
	
					// 編集状態を削除
					require.transaction(AtomTask.of(() -> require.removeMonthEditState(employeeId, yearMonth, closureId, closureDate)));
				}
	
				// 残数処理を行う必要があるかどうか判断　（Redmine#107271、EA#3434）
				Boolean isRemainProc = false;
				if (remainPeriod.contains(periodNew.start())) isRemainProc = true;
	
				AggregateMonthlyRecordValue value = new AggregateMonthlyRecordValue();
				try {
					// 月別実績を集計する　（アルゴリズム）
					value = AggregateMonthlyRecordService.aggregate(require, cacheCarrier, companyId, employeeId,
							yearMonth, closureId, closureDate, periodNew,
							prevAbsRecResultOpt, prevBreakDayOffresultOpt,
							companySets, employeeSets, Optional.empty(), Optional.empty(), isRemainProc);
				}
				catch (Exception ex) {
					boolean isOptimisticLock = new ThrowableAnalyzer(ex).findByClass(OptimisticLockException.class).isPresent();
					if (!isOptimisticLock) {
						throw ex;
					}
					atomTasks.add(MonthlyAggregationErrorService.errorProcForOptimisticLock(require,
							dataSetter, employeeId, empCalAndSumExecLogID, periodNew.end()));
					aggrPeriod.setHappendOptimistLockError(true);
					status.getOutAggrPeriod().add(aggrPeriod);
					return AggregationResult.build(status, atomTasks);
				}
	
				// 状態を確認する
				if (value.getErrorInfos().size() > 0) {
	
					// エラー処理
					List<MonthlyAggregationErrorInfo> errorInfoList = new ArrayList<>();
					errorInfoList.addAll(value.getErrorInfos().values());
					atomTasks.add(errorProc(require, dataSetter, employeeId, empCalAndSumExecLogID, periodNew.end(), errorInfoList));
	
					// 中断するエラーがある時、中断処理をする
					if (value.isInterruption()){
						//asyncContext.finishedAsCancelled();
						status.setState(ProcessState.INTERRUPTION);
						return AggregationResult.build(status, atomTasks);
					}
	
					break;
				}
	
	//			// 前回集計結果の退避
	//			prevAggrResult = value.getAggrResultOfAnnAndRsvLeave();
	//			prevAbsRecResultOpt = value.getAbsRecRemainMngOfInPeriodOpt();
	//			prevBreakDayOffresultOpt = value.getBreakDayOffRemainMngOfInPeriodOpt();
	//			prevSpecialLeaveResultMap = value.getInPeriodOfSpecialLeaveResultInforMap();
	
				try {
					// 月別実績(WORK)を登録する
					atomTasks.add(mergeMonth(require, Arrays.asList(value.getIntegration()), periodNew.end()));
				}
				catch (Exception ex) {
					boolean isOptimisticLock = new ThrowableAnalyzer(ex).findByClass(OptimisticLockException.class).isPresent();
					if (!isOptimisticLock) {
						throw ex;
					}
					atomTasks.add(MonthlyAggregationErrorService.errorProcForOptimisticLock(
							require, dataSetter, employeeId, empCalAndSumExecLogID, periodNew.end()));
					aggrPeriod.setHappendOptimistLockError(true);
					return AggregationResult.build(status, atomTasks);
				}
				finally {
					status.getOutAggrPeriod().add(aggrPeriod);
				}
			}//end for(DatePeriod periodNew : listPeriod) 

			//ConcurrentStopwatches.stop("12000:集計期間ごと：" + aggrPeriod.getYearMonth().toString());
		}
		return AggregationResult.build(status, atomTasks);
	}

	/**
	 * 実績ロックされているか判定する　（月別用）
	 * @param baseDate 基準日
	 * @param closureId 締めID
	 * @return 実績のロック状態　（ロックorアンロック）
	 */
	public static LockStatus getDetermineActualLocked(RequireM4 require,
			MonAggrCompanySettings comSettings,
			GeneralDate baseDate, Integer closureId){
		// 実績ロック
		val actualLock = require.actualLock(AppContexts.user().companyId(), closureId);

		LockStatus currentLockStatus = LockStatus.UNLOCK;

		// 「実績ロック」を取得する
		if (!actualLock.isPresent()) return currentLockStatus;

		// 月のロック状態を判定する
		currentLockStatus = actualLock.get().getMonthlyLockState();

		// ロック状態をチェックする
		if (currentLockStatus == LockStatus.UNLOCK) return LockStatus.UNLOCK;

		// 基準日が当月に含まれているかチェックする
		if (!comSettings.getCurrentMonthPeriodMap().containsKey(closureId)) return LockStatus.UNLOCK;
		DatePeriod currentPeriod = comSettings.getCurrentMonthPeriodMap().get(closureId);
		if (currentPeriod == null) return LockStatus.UNLOCK;
		if (currentPeriod.contains(baseDate)) {
			// 基準日が締め期間に含まれている
			return LockStatus.LOCK;
		}
		//基準日が締め期間に含まれていない
		return LockStatus.UNLOCK;
	}

	private static AtomTask mergeMonth(RequireM3 require, List<IntegrationOfMonthly> domains, GeneralDate targetDate) {

		return AtomTask.of(() -> require.merge(domains, targetDate) );
	}

	/**
	 * 残数処理を行う期間を計算
	 * @param companySets 月別集計で必要な会社別設定
	 * @param aggrPeriods 集計期間リスト
	 * @return 期間
	 */
	private static DatePeriod calcPeriodForRemainingProc(
			MonAggrCompanySettings companySets,
			List<AggrPeriodEachActualClosure> aggrPeriods){

		DatePeriod result = new DatePeriod(GeneralDate.min(), GeneralDate.min());

		// 集計期間を取得
		aggrPeriods.sort((a, b) -> a.getPeriod().start().compareTo(b.getPeriod().start()));
		for (AggrPeriodEachActualClosure aggrPeriod : aggrPeriods) {

			// 処理年月と締め期間を取得する　（会社別設定内に既に算出してある期間を使う）
			int closureId = aggrPeriod.getClosureId().value;
			if (companySets.getCurrentMonthPeriodMap().containsKey(closureId)) {

				// 処理中の期間が当月かどうか判断
				DatePeriod currentPeriod = companySets.getCurrentMonthPeriodMap().get(closureId);
				if (periodCompareEx(currentPeriod, aggrPeriod.getPeriod()) == true) {

					// 残数処理を行う期間を更新
					if (result.start() == GeneralDate.min()) {
						// 1回目は、開始日・終了日をセット
						result = new DatePeriod(aggrPeriod.getPeriod().start(), aggrPeriod.getPeriod().end());
					}
					else {
						// 2回目以降は、終了日のみセット
						result = new DatePeriod(result.start(), aggrPeriod.getPeriod().end());
					}
				}
			}
		}

		// 残数処理を行う期間を返す
		return result;
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
			Optional<TaskDataSetter> dataSetter,
			String employeeId,
			String empCalAndSumExecLogID,
			GeneralDate outYmd,
			List<MonthlyAggregationErrorInfo> errorInfoList){

		// 「エラーあり」に更新
		dataSetter.ifPresent(ds -> ds.updateData("monthlyAggregateHasError", ErrorPresent.HAS_ERROR.nameId));

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

	/**
	 * 期間重複があるか
	 * @param period1 期間1
	 * @param period2 期間2
	 * @return true：重複あり、false：重複なし
	 */
	private static boolean periodCompareEx(DatePeriod period1, DatePeriod period2){

		if (period1.start().after(period2.end())) return false;
		if (period1.end().before(period2.start())) return false;
		return true;
	}

	@Getter
	public static class AggregationResult {

		MonthlyAggrEmpServiceValue status;

		List<AtomTask> atomTasks;

		private AggregationResult (MonthlyAggrEmpServiceValue status, List<AtomTask> atomTasks) {
			this.atomTasks = atomTasks;
			this.status = status;
		}

		static AggregationResult build(MonthlyAggrEmpServiceValue status) {
			return new AggregationResult(status, new ArrayList<>());
		}

		static AggregationResult build(ProcessState status) {
			MonthlyAggrEmpServiceValue value = new MonthlyAggrEmpServiceValue();
			value.setState(status);
			return new AggregationResult(value, new ArrayList<>());
		}

		static AggregationResult build(MonthlyAggrEmpServiceValue status, List<AtomTask> atomTasks) {
			return new AggregationResult(status, atomTasks);
		}

		public AggregationResult newAtomTask(AtomTask task) {
			this.atomTasks.add(task);
			return this;
		}
	}

	public static interface RequireM4 {
		Optional<ActualLock> actualLock(String comId, int closureId);
		
		Optional<ExecutionLog> getByExecutionContent(String empCalAndSumExecLogID, int executionContent);

	}

	public static interface RequireM2 extends MonthlyAggregationErrorService.RequireM1 {}

	public static interface RequireM1 extends MonAggrEmployeeSettings.RequireM2,
		GetClosurePeriod.RequireM1, AggregateMonthlyRecordService.RequireM2,
		RequireM2, RequireM3, RequireM4, MonAggrCompanySettings.RequireM6,GetPeriodCanProcesse.Require {

		Optional<EmpCalAndSumExeLog> calAndSumExeLog (String empCalAndSumExecLogID);

		void removeMonthEditState(String employeeId, YearMonth yearMonth, ClosureId closureId, ClosureDate closureDate);

		void transaction(AtomTask task);
	}

	public static interface RequireM3 {

		void merge(List<IntegrationOfMonthly> domains, GeneralDate targetDate);
	}

}
