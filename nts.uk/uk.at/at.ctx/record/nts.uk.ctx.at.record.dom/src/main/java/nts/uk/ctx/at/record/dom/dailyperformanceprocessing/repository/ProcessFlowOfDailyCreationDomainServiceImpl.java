package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.appreflect.AppReflectManagerFromRecordImport;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.output.ExecutionAttr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.CreateDailyResultDomainServiceNew;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.DailyCalculationService;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.MonthlyAggregationService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfoRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.TargetPerson;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.TargetPersonRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExeStateOfCalAndSum;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionStatus;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class ProcessFlowOfDailyCreationDomainServiceImpl implements ProcessFlowOfDailyCreationDomainService{

	@Inject
	private EmpCalAndSumExeLogRepository empCalAndSumExeLogRepository;

	@Inject
	private RecordDomRequireService requireService;

	@Inject
	private TargetPersonRepository targetPersonRepository;

	@Inject
	private DailyCalculationService dailyCalculationService;

	@Inject
	private ErrMessageInfoRepository errMessageInfoRepository;
	@Inject
	private ExecutionLogRepository executionLogRepository;
	@Inject
	private AppReflectManagerFromRecordImport appReflectService;

	@Inject
	private CreateDailyResultDomainServiceNew createDailyResultDomainServiceNew;
//	@Inject
//	private PersonInfoAdapter personInfoAdapter;


	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public <C> void processFlowOfDailyCreation(AsyncCommandHandlerContext<C> asyncContext, ExecutionAttr executionAttr, DatePeriod periodTime,
			String empCalAndSumExecLogID) {

		TaskDataSetter dataSetter = asyncContext.getDataSetter();
		dataSetter.setData("dailyCreateCount", 0);
		dataSetter.setData("dailyCreateStatus", ExecutionStatus.PROCESSING.nameId);
		dataSetter.setData("dailyCreateHasError", " ");
		dataSetter.setData("dailyCreateStartTime", 0);
		dataSetter.setData("dailyCreateEndTime", 0);


		dataSetter.setData("dailyCalculateStatus", ExecutionStatus.INCOMPLETE.nameId);
		dataSetter.setData("dailyCalculateStartTime", 0);
		dataSetter.setData("dailyCalculateEndTime", 0);

		dataSetter.setData("monthlyAggregateStatus", ExecutionStatus.INCOMPLETE.nameId);
		dataSetter.setData("monthlyAggregateStartTime", 0);
		dataSetter.setData("monthlyAggregateEndTime", 0);

		dataSetter.setData("reflectApprovalStatus", ExecutionStatus.INCOMPLETE.nameId);
		dataSetter.setData("reflectApprovalStartTime", 0);
		dataSetter.setData("reflectApprovalEndTime", 0);

		LoginUserContext login = AppContexts.user();
		String companyId = login.companyId();

//		String employeeID = AppContexts.user().employeeId();

		//①実行方法を取得する
		//Optional<ExecutionLog> executionLog = empCalAndSumExeLog.get().getExecutionLogs().stream().filter(item -> item.getExecutionContent() == ExecutionContent.DAILY_CREATION).findFirst();
		//***** 日別作成に限定されていたため、暫定的に、より正確な取得処理に一時調整。（2018.1.16 Shuichi Ishida）
		Optional<EmpCalAndSumExeLog> empCalAndSumExeLog = this.empCalAndSumExeLogRepository.getByEmpCalAndSumExecLogID(empCalAndSumExecLogID);

		//②対象者を取得する
		List<TargetPerson> targetPersons = new ArrayList<>();
		//パラメータ「実行区分」＝手動　の場合 (Manual)
		if(executionAttr == ExecutionAttr.MANUAL){
			targetPersons = this.targetPersonRepository.getTargetPersonById(empCalAndSumExecLogID);
		}
		//パラメータ「実行区分」＝自動　の場合 (Auto)
		else if(executionAttr == ExecutionAttr.AUTO){
			// アルゴリズム「社員IDから個人社員基本情報を取得」を実行する
			//this.personInfoAdapter.getListPersonInfo(listSid);
		}

		// get List personId
		List<String> employeeIdList = targetPersons.stream().map(f -> {
			return f.getEmployeeId();
		}).collect(Collectors.toList());

		dataSetter.setData("dailyCreateTotal", targetPersons.size());

		ProcessState finalStatus = ProcessState.SUCCESS;

		// 実行ログを確認する

		//val executionLogs = empCalAndSumExeLog.get().getExecutionLogs();
		val executionLogs = this.executionLogRepository.getExecutionLogs(empCalAndSumExeLog.get().getEmpCalAndSumExecLogID());
		Map<ExecutionContent, ExecutionLog> logsMap = new HashMap<>();
		for (val executionLog : executionLogs){
			logsMap.put(executionLog.getExecutionContent(), executionLog);
		}

		//0: daily, 1: calculate, 2: approval, 3: monthly
		List<Integer> interrupts = new ArrayList<>();
		// 日別実績の作成　実行
		if (logsMap.containsKey(ExecutionContent.DAILY_CREATION)
				&& finalStatus == ProcessState.SUCCESS) {
			dataSetter.updateData("dailyCreateStartTime", GeneralDateTime.now().toString());
			// fix bug 110491
			GeneralDateTime dailyCreateStartTime = GeneralDateTime.now();

			Optional<ExecutionLog> dailyCreationLog =
					Optional.of(logsMap.get(ExecutionContent.DAILY_CREATION));
//			finalStatus = this.createDailyResultDomainService.createDailyResult(asyncContext, employeeIdList,
//					periodTime, executionAttr, companyId, empCalAndSumExecLogID, dailyCreationLog);
			ExecutionTypeDaily executionTypeDaily = ExecutionTypeDaily.CREATE;
			if(dailyCreationLog.isPresent() &&dailyCreationLog.get().getDailyCreationSetInfo().isPresent()) {
				if( dailyCreationLog.get().getDailyCreationSetInfo().get().getExecutionType() == ExecutionType.RERUN ) {
					executionTypeDaily = ExecutionTypeDaily.DELETE_ACHIEVEMENTS;
				}

			}
			Optional<Boolean> checkLock = dailyCreationLog.flatMap(c -> c.getIsCalWhenLock());
			
			finalStatus =createDailyResultDomainServiceNew.createDailyResult(asyncContext, employeeIdList, periodTime, executionAttr,
					companyId, executionTypeDaily,empCalAndSumExeLog, checkLock).value ==0?ProcessState.INTERRUPTION:ProcessState.SUCCESS;

			//*****　更新タイミングが悪い。ここで書かずに、日別作成の中で書くべき。（2018.1.16 Shuichi Ishida）
			//***** タイミング調整に関しては、実行ログの監視処理の完了判定も、念のため、確認が必要。
			if (finalStatus == ProcessState.SUCCESS) {
				dataSetter.updateData("dailyCreateStatus", ExecutionStatus.DONE.nameId);
				this.updateExecutionState(dataSetter, empCalAndSumExecLogID);
			} else {
				dataSetter.updateData("dailyCreateStatus", ExeStateOfCalAndSum.STOPPING.nameId);
				asyncContext.finishedAsCancelled();
			}
			dataSetter.updateData("dailyCreateEndTime", GeneralDateTime.now().toString());
			// fix bug 110491 ↓
			GeneralDateTime dailyCreateEndTime = GeneralDateTime.now();

			this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, dailyCreateEndTime,
					dailyCreateStartTime, dailyCreateEndTime, null, null,
					null, null, null, null, 0);
			interrupts.add(0);
			//***** ↑
		}

		//***** ↓　以下、仮実装。ログ制御全体を見直して、正確な手順に再修正要。（2018.1.16 Shuichi Ishida）
		// 日別実績の計算　実行
		if (logsMap.containsKey(ExecutionContent.DAILY_CALCULATION)
				&& finalStatus == ProcessState.SUCCESS) {
			dataSetter.updateData("dailyCalculateStatus", ExecutionStatus.PROCESSING.nameId);
			dataSetter.updateData("dailyCalculateStartTime", GeneralDateTime.now().toString());
			// fix bug 110491
			GeneralDateTime dailyCalculateStartTime = GeneralDateTime.now();

			Optional<ExecutionLog> dailyCalculationLog =
					Optional.of(logsMap.get(ExecutionContent.DAILY_CALCULATION));
			finalStatus = this.dailyCalculationService.manager(asyncContext, employeeIdList,
					periodTime, executionAttr, empCalAndSumExecLogID, dailyCalculationLog);
			dataSetter.updateData("dailyCalculateEndTime", GeneralDateTime.now().toString());
			// fix bug 110491 ↓
			GeneralDateTime dailyCalculateEndTime = GeneralDateTime.now();

			this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, dailyCalculateEndTime,
					null, null, dailyCalculateStartTime, dailyCalculateEndTime,
					null, null, null, null, 0);
			interrupts.add(1);
		}

		//承認反映
		if(finalStatus == ProcessState.SUCCESS
				&& logsMap.containsKey(ExecutionContent.REFLRCT_APPROVAL_RESULT)) {
			dataSetter.updateData("reflectApprovalStartTime", GeneralDateTime.now().toString());
			// fix bug 110491
			GeneralDateTime reflectApprovalStartTime = GeneralDateTime.now();
			dataSetter.updateData("reflectApprovalStatus", ExecutionStatus.PROCESSING.nameId);
			finalStatus = this.appReflectService.applicationRellect(empCalAndSumExecLogID, periodTime, asyncContext);
			if(finalStatus == ProcessState.INTERRUPTION) {
				this.empCalAndSumExeLogRepository.updateStatus(empCalAndSumExecLogID, ExeStateOfCalAndSum.STOPPING.value);
				asyncContext.finishedAsCancelled();
			}
			if(finalStatus == ProcessState.SUCCESS) {
				dataSetter.updateData("reflectApprovalStatus", ExecutionStatus.DONE.nameId);
			}
			dataSetter.updateData("reflectApprovalEndTime", GeneralDateTime.now().toString());
			// fix bug 110491 ↓
			GeneralDateTime reflectApprovalEndTime = GeneralDateTime.now();

			this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, reflectApprovalEndTime,
					null, null, null, null,
					reflectApprovalStartTime, reflectApprovalEndTime, null, null, 0);
			interrupts.add(2);
		}

		// 月別実績の集計　実行
		if (logsMap.containsKey(ExecutionContent.MONTHLY_AGGREGATION)
				&& finalStatus == ProcessState.SUCCESS) {

			dataSetter.updateData("monthlyAggregateStartTime", GeneralDateTime.now().toString());
			GeneralDateTime monthlyAggregateStatus = GeneralDateTime.now();
			dataSetter.updateData("monthlyAggregateStatus", ExecutionStatus.PROCESSING.nameId);

			Optional<ExecutionLog> monthlyAggregationLog =
					Optional.of(logsMap.get(ExecutionContent.MONTHLY_AGGREGATION));

			// Require の不整合によるエラー
			MonthlyAggregationService.AggregationResult aggreResult = MonthlyAggregationService.manager(
					requireService.createRequire(),
					new CacheCarrier(), asyncContext, companyId, employeeIdList,
					periodTime, executionAttr, empCalAndSumExecLogID, monthlyAggregationLog);

			finalStatus = aggreResult.getStatus();

			/** run to save data to DB */
			AtomTask.bundle(aggreResult.getAtomTasks()).run();

			dataSetter.updateData("monthlyAggregateEndTime", GeneralDateTime.now().toString());
			GeneralDateTime monthlyAggregateEndTime = GeneralDateTime.now();

			this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, monthlyAggregateEndTime,
					null, null, null, null,
					null, null, monthlyAggregateStatus, monthlyAggregateEndTime, 0);
			interrupts.add(3);
		}

		//***** ↑

		// ドメインモデル「就業計算と修正実行ログ」を更新する
		// 就業計算と集計実行ログ．実行状況　←　実行中止
		if (finalStatus == ProcessState.INTERRUPTION) {

			GeneralDateTime time = GeneralDateTime.now();
			if(!interrupts.isEmpty() && interrupts.stream().allMatch(i->i!=0)) {
				this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, time,
						time, time, null, null,
						null, null, null, null, 0);
			}
			if(!interrupts.isEmpty() && interrupts.stream().allMatch(i->i!=1)) {
				this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, time,
						null, null, time, time,
						null, null, null, null, 0);
			}
			if(!interrupts.isEmpty() && interrupts.stream().allMatch(i->i!=2)) {
				this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, time,
						null, null, null, null,
						time, time, null, null, 0);
			}
			if(!interrupts.isEmpty() && interrupts.stream().allMatch(i->i!=3)) {
				this.executionLogRepository.updateExecutionDate(empCalAndSumExecLogID, null, time,
						null, null, null, null,
						null, null, time, time, 0);
			}

			this.empCalAndSumExeLogRepository.updateStatus(empCalAndSumExecLogID, ExeStateOfCalAndSum.STOPPING.value);
			dataSetter.setData("endTime", GeneralDateTime.now().toString());
			asyncContext.finishedAsCancelled();
		} else {
			// 完了処理 (Xử lý hoàn thành)
			List<ErrMessageInfo> errMessageInfos = this.errMessageInfoRepository.getAllErrMessageInfoByEmpID(empCalAndSumExecLogID);
			if (errMessageInfos.isEmpty()) {
				this.empCalAndSumExeLogRepository.updateStatus(empCalAndSumExecLogID, ExeStateOfCalAndSum.DONE.value);

			} else {
				this.empCalAndSumExeLogRepository.updateStatus(empCalAndSumExecLogID, ExeStateOfCalAndSum.DONE_WITH_ERROR.value);
			}
			dataSetter.setData("endTime", GeneralDateTime.now().toString());
		}


	}

	private void updateExecutionState(TaskDataSetter dataSetter, String empCalAndSumExecLogID){

		List<ErrMessageInfo> errMessageInfos = this.errMessageInfoRepository.getAllErrMessageInfoByEmpID(empCalAndSumExecLogID);
		List<String> errorMessage = errMessageInfos.stream().map(error -> {
			return error.getMessageError().v();
		}).collect(Collectors.toList());
		if (errorMessage.isEmpty()) {
			dataSetter.updateData("dailyCreateHasError", ErrorPresent.NO_ERROR.nameId);

		} else {
			dataSetter.updateData("dailyCreateHasError", ErrorPresent.HAS_ERROR.nameId);
		}
	}



}

