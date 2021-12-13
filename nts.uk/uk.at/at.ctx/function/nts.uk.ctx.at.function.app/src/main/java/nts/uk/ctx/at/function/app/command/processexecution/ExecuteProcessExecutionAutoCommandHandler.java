package nts.uk.ctx.at.function.app.command.processexecution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.layer.app.command.AsyncCommandHandler;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.task.parallel.ManagedParallelWithContext.ControlOption;
import nts.arc.task.tran.DeadLock;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.error.ThrowableAnalyzer;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.app.command.processexecution.approuteupdatedaily.AppRouteUpdateDailyService;
import nts.uk.ctx.at.function.app.command.processexecution.approuteupdatedaily.OutputAppRouteDaily;
import nts.uk.ctx.at.function.app.command.processexecution.approuteupdatemonthly.AppRouteUpdateMonthlyService;
import nts.uk.ctx.at.function.app.command.processexecution.approuteupdatemonthly.OutputAppRouteMonthly;
import nts.uk.ctx.at.function.app.command.processexecution.createlogfileexecution.CreateLogFileExecution;
import nts.uk.ctx.at.function.app.command.processexecution.createschedule.executionprocess.CalPeriodTransferAndWorktype;
import nts.uk.ctx.at.function.dom.adapter.WorkplaceWorkRecordAdapter;
import nts.uk.ctx.at.function.dom.adapter.alarm.BasicScheduleAdapter;
import nts.uk.ctx.at.function.dom.adapter.appreflectmanager.AppReflectManagerAdapter;
import nts.uk.ctx.at.function.dom.adapter.appreflectmanager.ProcessStateReflectImport;
import nts.uk.ctx.at.function.dom.adapter.dailymonthlyprocessing.DailyMonthlyprocessAdapterFn;
import nts.uk.ctx.at.function.dom.adapter.dailymonthlyprocessing.ExeStateOfCalAndSumImportFn;
import nts.uk.ctx.at.function.dom.adapter.employeemanage.EmployeeManageAdapter;
import nts.uk.ctx.at.function.dom.adapter.resultsperiod.optionalaggregationperiod.AnyAggrPeriodAdapter;
import nts.uk.ctx.at.function.dom.adapter.resultsperiod.optionalaggregationperiod.AnyAggrPeriodImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.RecordWorkInfoFunAdapter;
import nts.uk.ctx.at.function.dom.adapter.worklocation.WorkInfoOfDailyPerFnImport;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.createextractionprocess.CreateExtraProcessService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.execalarmlistprocessing.ExecAlarmListProcessingService;
import nts.uk.ctx.at.function.dom.alarm.alarmlist.execalarmlistprocessing.OutputExecAlarmListPro;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.AlarmListExtraProcessStatusRepository;
import nts.uk.ctx.at.function.dom.alarm.extraprocessstatus.ExtractionState;
import nts.uk.ctx.at.function.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodExcutionAdapter;
import nts.uk.ctx.at.function.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodExcutionImport;
import nts.uk.ctx.at.function.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodTargetAdapter;
import nts.uk.ctx.at.function.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodTargetImport;
import nts.uk.ctx.at.function.dom.indexreconstruction.FragmentationRate;
import nts.uk.ctx.at.function.dom.indexreconstruction.IndexName;
import nts.uk.ctx.at.function.dom.indexreconstruction.IndexReorgTable;
import nts.uk.ctx.at.function.dom.indexreconstruction.ProcExecIndex;
import nts.uk.ctx.at.function.dom.indexreconstruction.ProcExecIndexResult;
import nts.uk.ctx.at.function.dom.indexreconstruction.TableName;
import nts.uk.ctx.at.function.dom.indexreconstruction.repository.CalculateFragRate;
import nts.uk.ctx.at.function.dom.indexreconstruction.repository.IndexReorgTableRepository;
import nts.uk.ctx.at.function.dom.indexreconstruction.repository.ProcExecIndexRepository;
import nts.uk.ctx.at.function.dom.processexecution.ExecutionCode;
import nts.uk.ctx.at.function.dom.processexecution.ExecutionScopeClassification;
import nts.uk.ctx.at.function.dom.processexecution.ExternalOutputConditionCode;
import nts.uk.ctx.at.function.dom.processexecution.LastExecDateTime;
import nts.uk.ctx.at.function.dom.processexecution.ProcessExecType;
import nts.uk.ctx.at.function.dom.processexecution.ProcessExecutionService;
import nts.uk.ctx.at.function.dom.processexecution.ServerExternalOutputAdapter;
import nts.uk.ctx.at.function.dom.processexecution.ServerExternalOutputImport;
import nts.uk.ctx.at.function.dom.processexecution.TempAbsenceHistoryService;
import nts.uk.ctx.at.function.dom.processexecution.UpdateProcessAutoExecution;
import nts.uk.ctx.at.function.dom.processexecution.createfromupdateautorunerror.CreateFromUpdateAutoRunError;
import nts.uk.ctx.at.function.dom.processexecution.createfromupdateautorunerror.DefaultRequireImpl;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.CurrentExecutionStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.EachProcessPeriod;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.EndStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ExecutionTaskLog;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLog;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogHistory;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionTask;
import nts.uk.ctx.at.function.dom.processexecution.listempautoexec.ListEmpAutoExec;
import nts.uk.ctx.at.function.dom.processexecution.personalschedule.CreateScheduleYear;
import nts.uk.ctx.at.function.dom.processexecution.personalschedule.CreationPeriod;
import nts.uk.ctx.at.function.dom.processexecution.personalschedule.PersonalScheduleCreationPeriod;
import nts.uk.ctx.at.function.dom.processexecution.personalschedule.TargetDate;
import nts.uk.ctx.at.function.dom.processexecution.personalschedule.TargetMonth;
import nts.uk.ctx.at.function.dom.processexecution.repository.ExecutionTaskLogRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ExecutionTaskSettingRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.LastExecDateTimeRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogHistRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogManageRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionRepository;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.ExecutionTaskSetting;
import nts.uk.ctx.at.function.dom.processexecution.updatelogafterprocess.UpdateLogAfterProcess;
import nts.uk.ctx.at.function.dom.processexecution.updateprocessautoexeclog.overallerrorprocess.ErrorConditionOutput;
import nts.uk.ctx.at.function.dom.processexecution.updateprocessautoexeclog.overallerrorprocess.OverallErrorProcess;
import nts.uk.ctx.at.function.dom.processexecution.updateprocessexecsetting.changepersionlist.ChangePersionList;
import nts.uk.ctx.at.function.dom.processexecution.updateprocessexecsetting.changepersionlist.ListLeaderOrNotEmp;
import nts.uk.ctx.at.function.dom.processexecution.updateprocessexecsetting.changepersionlistforsche.ChangePersionListForSche;
import nts.uk.ctx.at.function.dom.processexecution.updateprocessexecsetting.changepersionlistforsche.ChangePersionListForSche.EmployeeDataDto;
import nts.uk.ctx.at.function.dom.statement.EmployeeGeneralInfoAdapter;
import nts.uk.ctx.at.function.dom.statement.dtoimport.EmployeeGeneralInfoImport;
import nts.uk.ctx.at.record.dom.adapter.company.AffComHistItemImport;
import nts.uk.ctx.at.record.dom.adapter.company.SyCompanyRecordAdapter;
import nts.uk.ctx.at.record.dom.affiliationinformation.wkplaceinfochangeperiod.WkplaceInfoChangePeriod;
import nts.uk.ctx.at.record.dom.affiliationinformation.wktypeinfochangeperiod.WkTypeInfoChangePeriod;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.output.ExecutionAttr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.ExecutionTypeDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.CreateDailyResultDomainServiceNew;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.OutputCreateDailyResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.DailyCalculationEmployeeService;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.periodexcution.PresenceOfError;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.periodtarget.State;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.MonthlyAggregationEmployeeService;
import nts.uk.ctx.at.record.dom.monthlyprocess.aggr.MonthlyAggregationEmployeeService.AggregationResult;
import nts.uk.ctx.at.record.dom.monthlyprocess.byperiod.ByPeriodAggregationService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.CalExeSettingInfor;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionTime;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ObjectPeriod;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.SetInforReflAprResult;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.SettingInforForDailyCreation;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.CalAndAggClassification;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.DailyRecreateClassification;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ErrorPresent;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutedMenu;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionStatus;
import nts.uk.ctx.at.schedule.app.command.executionlog.ScheduleCreatorExecutionCommand;
import nts.uk.ctx.at.schedule.app.command.executionlog.ScheduleCreatorExecutionService;
import nts.uk.ctx.at.schedule.dom.executionlog.CreationMethod;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ImplementAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.RecreateCondition;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContent;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLog;
import nts.uk.ctx.at.schedule.dom.executionlog.SpecifyCreation;
import nts.uk.ctx.at.shared.dom.adapter.generalinfo.dtoimport.ExWorkplaceHistItemImport;
import nts.uk.ctx.at.shared.dom.adapter.temporaryabsence.TempAbsenceImport;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.BusinessTypeOfEmployeeHis;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.BusinessTypeOfEmployeeService;
import nts.uk.ctx.at.shared.dom.employmentrules.organizationmanagement.ConditionEmployee;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.getprocessingdate.GetProcessingDate;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.CurrentMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
@Slf4j
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ExecuteProcessExecutionAutoCommandHandler extends AsyncCommandHandler<ExecuteProcessExecutionCommand> {

	public static int MAX_DELAY_PARALLEL = 0;
	@Inject
	private ProcessExecutionRepository procExecRepo;
	@Inject
	private ExecutionTaskSettingRepository execSettingRepo;
	@Inject
	private ProcessExecutionLogRepository procExecLogRepo;
	@Inject
	private ProcessExecutionLogHistRepository procExecLogHistRepo;
	@Inject
	private ExecutionTaskLogRepository execTaskLogRepo;
	@Inject
	private LastExecDateTimeRepository lastExecDateTimeRepo;
	@Inject
	private EmpCalAndSumExeLogRepository empCalSumRepo;
	@Inject
	private ClosureRepository closureRepo;
	@Inject
	private ClosureEmploymentRepository closureEmpRepo;
	@Inject
	private ScheduleCreatorExecutionService executeService;
	@Inject
	private WorkplaceWorkRecordAdapter workplaceAdapter;
	@Inject
	private ExecutionLogRepository executionLogRepository;
	@Inject
	private ProcessExecutionLogManageRepository processExecLogManaRepo;
	@Inject
	private DailyCalculationEmployeeService dailyCalculationEmployeeService;
	@Inject
	private SyCompanyRecordAdapter syCompanyRecordAdapter;
	@Inject
	private AppReflectManagerAdapter appReflectManagerAdapter;
	@Inject
	private ManagedParallelWithContext managedParallelWithContext;
	@Inject
	private AppRouteUpdateDailyService appRouteUpdateDailyService;
	@Inject
	private AppRouteUpdateMonthlyService appRouteUpdateMonthlyService;
	@Inject
	private DailyMonthlyprocessAdapterFn dailyMonthlyprocessAdapterFn;
	@Inject
	private ChangePersionList changePersionList;
	@Inject
	private EmployeeGeneralInfoAdapter employeeGeneralInfoAdapter;
	@Inject
	private WkplaceInfoChangePeriod wkplaceInfoChangePeriod;
	@Inject
	private WkTypeInfoChangePeriod wkTypeInfoChangePeriod;
	@Inject
	private BusinessTypeOfEmployeeService businessTypeOfEmpHisService;
	@Inject
	private ListEmpAutoExec listEmpAutoExec;
	@Inject
	private CreateLogFileExecution createLogFileExecution;
	@Inject
	private UpdateLogAfterProcess updateLogAfterProcess;
	@Inject
	private OverallErrorProcess overallErrorProcess;
	@Inject
	private ProcessExecutionService processExecutionService;
	@Inject
	private GetProcessingDate getProcessingDate;
	@Inject
	private CreateDailyResultDomainServiceNew createDailyResultDomainServiceNew;
	@Inject
	private RecordDomRequireService requireService;

	@Inject
	private IndexReorgTableRepository indexReorgTableRepository;

	@Inject
	private AnyAggrPeriodAdapter anyAggrPeriodAdapter;

	@Inject
	private AggrPeriodTargetAdapter aggrPeriodTargetAdapter;

	@Inject
	private AggrPeriodExcutionAdapter aggrPeriodExcutionAdapter;

	@Inject
	private ProcExecIndexRepository proExecIndexRepository;
	@Inject
	private CreateExtraProcessService createExtraProcessService;
	@Inject
	private ExecAlarmListProcessingService execAlarmListProcessingService;
	@Inject
	private RecordWorkInfoFunAdapter recordWorkInfoFunAdapter;

	@Inject
	private ServerExternalOutputAdapter serverExternalOutputAdapter;

	@Inject
	private AutoExecutionPreparationAdapter autoExecutionPreparationAdapter;
	@Inject
	private ChangePersionListForSche changePersionListForSche;
	@Inject
    private BasicScheduleAdapter basicScheduleAdapter;
	@Inject
	private CalPeriodTransferAndWorktype calPeriodTransferAndWorktype;

	@Inject
	private ProcessExecutionLogManageRepository processExecutionLogManageRepository;
	
	@Inject
	private EmployeeManageAdapter employeeManageAdapter;
	
	@Inject
	private TopPageAlarmAdapter topPageAlarmAdapter;
	@Inject
	private AlarmListExtraProcessStatusRepository alarmExtraProcessStatusRepo;
	
	@Inject
	private ByPeriodAggregationService byPeriodAggregationService;
	
	@Inject
	private TempAbsenceHistoryService tempAbsenceHistoryService;
	
	@Override
	public boolean keepsTrack() {
		return false;
	}

	/**
	 * 更新処理を開始する
	 *
	 * @param execType   実行タイプ
	 * @param execId     実行ID
	 * @param execItemCd 更新処理自動実行項目コード
	 * @param companyId  会社ID
	 */
	// 実行処理
	@Override
	public void handle(CommandHandlerContext<ExecuteProcessExecutionCommand> context) {
		ExecuteProcessExecutionCommand command = context.getCommand();
		String execItemCd = command.getExecItemCd();
		String companyId = command.getCompanyId();

		log.info("Run batch service by auto run! (" + execItemCd + "@" + companyId + ")");

		// String execId = command.getExecId();
		// vi ExecuteProcessExecCommandHandler dang loi nen dung tam random execId
		String execId = IdentifierUtil.randomUniqueId();
		int execType = command.getExecType();
		// ドメインモデル「更新処理自動実行」を取得する
		UpdateProcessAutoExecution procExec = null;
		Optional<UpdateProcessAutoExecution> procExecOpt = this.procExecRepo
				.getProcessExecutionByCidAndExecCd(companyId, execItemCd);
		if (procExecOpt.isPresent()) {
			procExec = procExecOpt.get();
		}
		// ドメインモデル「実行タスク設定」を取得する
		ExecutionTaskSetting execSetting = null;
		if (execType == 0) {
			Optional<ExecutionTaskSetting> execSettingOpt = this.execSettingRepo.getByCidAndExecCd(companyId,
					execItemCd);
			if (execSettingOpt.isPresent()) {
				execSetting = execSettingOpt.get();
			}
		}

		// ドメインモデル「更新処理自動実行管理」を取得する NO.4
		ProcessExecutionLogManage processExecutionLogManage = null;
		Optional<ProcessExecutionLogManage> logByCIdAndExecCdOpt = this.processExecLogManaRepo
				.getLogByCIdAndExecCd(companyId, execItemCd);
		if (logByCIdAndExecCdOpt.isPresent()) {
			processExecutionLogManage = logByCIdAndExecCdOpt.get();
		}

		// ドメインモデル「更新処理前回実行日時」を取得する
		LastExecDateTime lastExecDateTime = null;
		Optional<LastExecDateTime> lastDateTimeOpt = Optional.empty();
		if (procExec != null) {
			lastDateTimeOpt = lastExecDateTimeRepo.get(procExec.getCompanyId(), procExec.getExecItemCode().v());
		}
		if (lastDateTimeOpt.isPresent()) {
			lastExecDateTime = lastDateTimeOpt.get();
		}
		if (execType == 0) {
			// ドメインモデルの取得結果をチェックする
			if (procExec == null || execSetting == null || processExecutionLogManage == null
					|| lastExecDateTime == null) {
				return;
			}
		} else {
			if (procExec == null || processExecutionLogManage == null || lastExecDateTime == null) {
				return;
			}
		}
		EmpCalAndSumExeLog empCalAndSumExeLog = null;
		// ドメインモデル「就業計算と集計実行ログ」を追加する
		empCalAndSumExeLog = new EmpCalAndSumExeLog(execId, command.getCompanyId(),
				new YearMonth(GeneralDate.today().year() * 100 + 1), ExecutedMenu.SELECT_AND_RUN, GeneralDateTime.now(),
				null, "", 1, IdentifierUtil.randomUniqueId(),
				CalAndAggClassification.AUTOMATIC_EXECUTION);
		this.empCalSumRepo.add(empCalAndSumExeLog);
		// }

		// アルゴリズム「実行前登録処理」を実行する
		// 実行前登録処理
		GeneralDateTime dateTimeOutput = this.preExecutionRegistrationProcessing(companyId, execItemCd, execId,
				processExecutionLogManage, execType);
		Optional<ProcessExecutionLog> procExecLogData = procExecLogRepo.getLogByCIdAndExecCd(companyId, execItemCd,
				execId);
		/*
		 * /* ドメインモデル「更新処理自動実行ログ」を更新する 現在の実行状態 ＝ 実行 全体の終了状態 ＝ NULL
		 *
		 * procExecLog.setCurrentStatus(CurrentExecutionStatus.RUNNING);
		 * procExecLog.setOverallStatus(null); this.procExecLogRepo.update(procExecLog);
		 */

		/*
		 * 各処理を実行する 【パラメータ】 実行ID ＝ 取得した実行ID
		 * 取得したドメインモデル「更新処理自動実行」、「実行タスク設定」、「更新処理自動実行ログ」の情報
		 */
		this.doProcesses(context, empCalAndSumExeLog, execId, procExec, procExecLogData.get(), companyId);

		processExecutionLogManage = this.processExecLogManaRepo.getLogByCIdAndExecCd(companyId, execItemCd).get();
		// アルゴリズム「自動実行登録処理」を実行する
		this.updateDomains(execItemCd, execType, companyId, execId, execSetting, procExecLogData.get(),
				lastExecDateTime, processExecutionLogManage, dateTimeOutput);
		// 実行時情報「アプリケーションコンテキスト．オプションライセンス．カスタマイズ．大塚」をチェックする
		if (AppContexts.optionLicense().customize().ootsuka()) {
			// アルゴリズム「実行状態ログファイル作成処理」を実行する
			createLogFileExecution.createLogFile(companyId, execItemCd);
		}
		
        //更新処理自動実行エラーからトップページアラームを作成する
        DefaultRequireImpl rq = new DefaultRequireImpl(processExecutionLogManageRepository, employeeManageAdapter, topPageAlarmAdapter);
        CreateFromUpdateAutoRunError.create(rq, companyId);
	}

	/**
	 * 自動実行登録処理
	 */
	private void updateDomains(String execItemCd, int execType, String companyId, String execId,
			ExecutionTaskSetting execSetting, ProcessExecutionLog procExecLog, LastExecDateTime lastExecDateTime,
			ProcessExecutionLogManage processExecutionLogManage, GeneralDateTime dateTimeOutput) {

		// ドメインモデル「更新処理自動実行ログ」を取得する - procExecLog
		// アルゴリズム[全体エラー状況確認処理]を実行する
		ErrorConditionOutput errorCondition = overallErrorProcess.overallErrorProcess(procExecLog);

		if (processExecutionLogManage.getOverallStatus().isPresent()
				&& processExecutionLogManage.getOverallStatus().get() == EndStatus.CLOSING) {
			// ドメインモデル「更新処理自動実行管理」を取得する
			Optional<ProcessExecutionLogManage> optExecLogManage = processExecLogManaRepo
					.getLogByCIdAndExecCdAndDateTiem(companyId, execItemCd, dateTimeOutput);
			if (optExecLogManage.isPresent()) {
				// ドメインモデル「更新処理自動実行管理」を更新する
				processExecutionLogManage.setLastEndExecDateTime(GeneralDateTime.now());
				processExecutionLogManage.setOverallStatus(EndStatus.FORCE_END);
				processExecutionLogManage.setErrorSystem(errorCondition.getSystemErrorCondition());
				processExecutionLogManage.setErrorBusiness(errorCondition.getBusinessErrorStatus());

				this.processExecLogManaRepo.updateByDatetime(processExecutionLogManage, dateTimeOutput);

				List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
						execId);
				if (CollectionUtil.isEmpty(taskLogList)) {
					this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
				} else {
					this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
				}
				// ドメインモデル「更新処理自動実行ログ履歴」を更新する
				List<ProcessExecutionTaskLogCommand> taskLogListCommand = procExecLog.getTaskLogList().stream()
						.map(item -> ProcessExecutionTaskLogCommand.builder().taskId(item.getProcExecTask().value)
								.status(item.getStatus().map(e -> e.value).orElse(null))
								.lastExecDateTime(item.getLastExecDateTime().orElse(null))
								.lastEndExecDateTime(item.getLastEndExecDateTime().orElse(null))
								.errorSystem(item.getErrorSystem().orElse(null))
								.errorBusiness(item.getErrorBusiness().orElse(null))
								.systemErrorDetails(item.getSystemErrorDetails().orElse(null)).build())
						.collect(Collectors.toList());
				Optional<DatePeriod> scheduleCreationPeriod = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getScheduleCreationPeriod).orElse(null);
				Optional<DatePeriod> dailyCreationPeriod = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getDailyCreationPeriod).orElse(null);
				Optional<DatePeriod> dailyCalcPeriod = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getDailyCalcPeriod).orElse(null);
				Optional<DatePeriod> reflectApprovalResult = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getReflectApprovalResult).orElse(null);
				ProcessExecutionLogHistoryCommand processExecutionLogHistoryCommand = ProcessExecutionLogHistoryCommand
						.builder().execItemCd(execItemCd).companyId(companyId).execId(procExecLog.getExecId())
						.overallError(processExecutionLogManage.getOverallError().map(item -> item.value).orElse(null))
						.overallStatus(
								processExecutionLogManage.getOverallStatus().map(item -> item.value).orElse(null))
						.lastExecDateTime(processExecutionLogManage.getLastExecDateTime().orElse(null))
						.lastEndExecDateTime(processExecutionLogManage.getLastEndExecDateTime().orElse(null))
						.errorSystem(processExecutionLogManage.getErrorSystem().orElse(null))
						.errorBusiness(processExecutionLogManage.getErrorBusiness().orElse(null))
						.taskLogList(taskLogListCommand)
						.schCreateStart(scheduleCreationPeriod.map(DatePeriod::start).orElse(null))
						.schCreateEnd(scheduleCreationPeriod.map(DatePeriod::end).orElse(null))
						.dailyCreateStart(dailyCreationPeriod.map(DatePeriod::start).orElse(null))
						.dailyCreateEnd(dailyCreationPeriod.map(DatePeriod::end).orElse(null))
						.dailyCalcStart(dailyCalcPeriod.map(DatePeriod::start).orElse(null))
						.dailyCalcEnd(dailyCalcPeriod.map(DatePeriod::end).orElse(null))
						.reflectApprovalResultStart(reflectApprovalResult.map(DatePeriod::start).orElse(null))
						.reflectApprovalResultEnd(reflectApprovalResult.map(DatePeriod::end).orElse(null)).build();
				this.procExecLogHistRepo
						.update(ProcessExecutionLogHistory.createFromMemento(processExecutionLogHistoryCommand));
			} else {
				// ドメインモデル「更新処理自動実行ログ履歴」を更新する
				List<ProcessExecutionTaskLogCommand> taskLogListCommand = procExecLog.getTaskLogList().stream()
						.map(item -> ProcessExecutionTaskLogCommand.builder().taskId(item.getProcExecTask().value)
								.status(item.getStatus().map(e -> e.value).orElse(null))
								.lastExecDateTime(item.getLastExecDateTime().orElse(null))
								.lastEndExecDateTime(item.getLastEndExecDateTime().orElse(null))
								.errorSystem(item.getErrorSystem().orElse(null))
								.errorBusiness(item.getErrorBusiness().orElse(null))
								.systemErrorDetails(item.getSystemErrorDetails().orElse(null)).build())
						.collect(Collectors.toList());
				Optional<DatePeriod> scheduleCreationPeriod = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getScheduleCreationPeriod).orElse(null);
				Optional<DatePeriod> dailyCreationPeriod = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getDailyCreationPeriod).orElse(null);
				Optional<DatePeriod> dailyCalcPeriod = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getDailyCalcPeriod).orElse(null);
				Optional<DatePeriod> reflectApprovalResult = procExecLog.getEachProcPeriod()
						.map(EachProcessPeriod::getReflectApprovalResult).orElse(null);
				ProcessExecutionLogHistoryCommand processExecutionLogHistoryCommand = ProcessExecutionLogHistoryCommand
						.builder().execItemCd(execItemCd).companyId(companyId).execId(procExecLog.getExecId())
						.overallError(processExecutionLogManage.getOverallError().map(data -> data.value).orElse(null))
						.overallStatus(EndStatus.FORCE_END.value)
						.lastExecDateTime(processExecutionLogManage.getLastExecDateTime().orElse(null))
						.lastEndExecDateTime(GeneralDateTime.now())
						.errorSystem(errorCondition.getSystemErrorCondition())
						.errorBusiness(errorCondition.getBusinessErrorStatus()).taskLogList(taskLogListCommand)
						.schCreateStart(scheduleCreationPeriod.map(DatePeriod::start).orElse(null))
						.schCreateEnd(scheduleCreationPeriod.map(DatePeriod::end).orElse(null))
						.dailyCreateStart(dailyCreationPeriod.map(DatePeriod::start).orElse(null))
						.dailyCreateEnd(dailyCreationPeriod.map(DatePeriod::end).orElse(null))
						.dailyCalcStart(dailyCalcPeriod.map(DatePeriod::start).orElse(null))
						.dailyCalcEnd(dailyCalcPeriod.map(DatePeriod::end).orElse(null))
						.reflectApprovalResultStart(reflectApprovalResult.map(DatePeriod::start).orElse(null))
						.reflectApprovalResultEnd(reflectApprovalResult.map(DatePeriod::end).orElse(null)).build();
				this.procExecLogHistRepo
						.update(ProcessExecutionLogHistory.createFromMemento(processExecutionLogHistoryCommand));
			}

		} else {
			// ドメインモデル「更新処理自動実行管理」を更新する
			processExecutionLogManage.setLastEndExecDateTime(GeneralDateTime.now());
			processExecutionLogManage.setOverallStatus(EndStatus.SUCCESS);
			processExecutionLogManage.setErrorSystem(errorCondition.getSystemErrorCondition());
			processExecutionLogManage.setErrorBusiness(errorCondition.getBusinessErrorStatus());
			processExecutionLogManage.setCurrentStatus(CurrentExecutionStatus.WAITING);
			this.processExecLogManaRepo.updateByDatetime(processExecutionLogManage, dateTimeOutput);

			List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
					execId);
			if (CollectionUtil.isEmpty(taskLogList)) {
				this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
			} else {
				this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
			}

			// ドメインモデル「更新処理自動実行ログ履歴」を新規登録する
			Optional<DatePeriod> scheduleCreationPeriod = procExecLog.getEachProcPeriod()
					.map(EachProcessPeriod::getScheduleCreationPeriod).orElse(null);
			Optional<DatePeriod> dailyCreationPeriod = procExecLog.getEachProcPeriod()
					.map(EachProcessPeriod::getDailyCreationPeriod).orElse(null);
			Optional<DatePeriod> dailyCalcPeriod = procExecLog.getEachProcPeriod()
					.map(EachProcessPeriod::getDailyCalcPeriod).orElse(null);
			Optional<DatePeriod> reflectApprovalResult = procExecLog.getEachProcPeriod()
					.map(EachProcessPeriod::getReflectApprovalResult).orElse(null);
			ProcessExecutionLogHistoryCommand processExecutionLogHistoryCommand = ProcessExecutionLogHistoryCommand
					.builder().execItemCd(execItemCd).companyId(companyId).execId(procExecLog.getExecId())
					.overallError(processExecutionLogManage.getOverallError().map(item -> item.value).orElse(null))
					.overallStatus(processExecutionLogManage.getOverallStatus().map(item -> item.value).orElse(null))
					.lastExecDateTime(processExecutionLogManage.getLastExecDateTime().orElse(null))
					.lastEndExecDateTime(processExecutionLogManage.getLastEndExecDateTime().orElse(null))
					.errorSystem(processExecutionLogManage.getErrorSystem().orElse(null))
					.errorBusiness(processExecutionLogManage.getErrorBusiness().orElse(null))
					.taskLogList(Collections.emptyList())
					.schCreateStart(scheduleCreationPeriod.map(DatePeriod::start).orElse(null))
					.schCreateEnd(scheduleCreationPeriod.map(DatePeriod::end).orElse(null))
					.dailyCreateStart(dailyCreationPeriod.map(DatePeriod::start).orElse(null))
					.dailyCreateEnd(dailyCreationPeriod.map(DatePeriod::end).orElse(null))
					.dailyCalcStart(dailyCalcPeriod.map(DatePeriod::start).orElse(null))
					.dailyCalcEnd(dailyCalcPeriod.map(DatePeriod::end).orElse(null))
					.reflectApprovalResultStart(reflectApprovalResult.map(DatePeriod::start).orElse(null))
					.reflectApprovalResultEnd(reflectApprovalResult.map(DatePeriod::end).orElse(null)).build();
			this.procExecLogHistRepo
					.insert(ProcessExecutionLogHistory.createFromMemento(processExecutionLogHistoryCommand));
		}
		// パラメータ.実行タイプのチェック
		if (execType == 1) {
			return;
		}

		/*
		 * ドメインモデル「実行タスク設定」を更新する
		 *
		 * 次回実行日時 ＝ 次回実行日時を作成する。 ※補足資料⑤参照
		 */
		if (execSetting != null) {
			GeneralDateTime nextFireTime = this.processExecutionService.processNextExecDateTimeCreation(execSetting);
			execSetting.setNextExecDateTime(Optional.ofNullable(nextFireTime));
			this.execSettingRepo.update(execSetting);
		}

		/*
		 * ドメインモデル「更新処理前回実行日時」を更新する 前回実行日時 ＝ システム日時
		 */
		lastExecDateTime.setLastExecDateTime(GeneralDateTime.now());
		this.lastExecDateTimeRepo.update(lastExecDateTime);
	}

	/**
	 * 各処理を実行する
	 *
	 * @param execId      実行ID
	 * @param procExec    更新処理自動実行
	 * @param execSetting 実行タスク設定
	 * @param procExecLog 更新処理自動実行ログ
	 */
	// true interrupt false non interrupt
	// 各処理の分岐
	private boolean doProcesses(CommandHandlerContext<ExecuteProcessExecutionCommand> context,
			EmpCalAndSumExeLog empCalAndSumExeLog, String execId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog, String companyId) {
		// Fix bug wrong startDateTime at screen G
		procExecLog.setTaskLogList(procExecLog.getTaskLogList().stream()
				.filter(item -> item.getExecId().equals(execId))
				.collect(Collectors.toList()));
		// Initialize status [未実施] for each task
		initAllTaskStatus(procExecLog, EndStatus.NOT_IMPLEMENT);
		/*
		 * スケジュールの作成 【パラメータ】 実行ID 取得したドメインモデル「更新処理自動実行」、「実行タスク設定」、「更新処理自動実行ログ」の情報
		 */
		OutputCreateScheduleAndDaily dataSchedule = this.createSchedule(context, execId, procExec, procExecLog);
		if (!dataSchedule.isCheckStop()) {
			return true;
		}
		OutputCreateScheduleAndDaily dataDaily = this.createDailyData(context, empCalAndSumExeLog, execId, procExec,
				procExecLog);
		if (!dataDaily.isCheckStop()) {
			return true;
		}
		List<ApprovalPeriodByEmp> listSche = dataSchedule.getListApprovalPeriodByEmp();
		List<ApprovalPeriodByEmp> listDaily = dataDaily.getListApprovalPeriodByEmp();

		List<ApprovalPeriodByEmp> listApprovalPeriodByEmpAll = new ArrayList<>();
		listApprovalPeriodByEmpAll.addAll(listSche);
		listApprovalPeriodByEmpAll.addAll(listDaily);
		Map<String, ApprovalPeriodByEmp> mapApprovalPeriod = listApprovalPeriodByEmpAll.stream()
				.collect(Collectors.groupingBy(x -> x.getEmployeeID(),
						Collectors.collectingAndThen(Collectors.toList(), list -> mergeList(list))));
		List<ApprovalPeriodByEmp> lstApprovalPeriod = mapApprovalPeriod.values().stream().collect(Collectors.toList());

		if (this.reflectApprovalResult(execId, procExec, procExecLog, companyId, lstApprovalPeriod)) {
			return true;
		}
		if (this.monthlyAggregation(execId, procExec, procExecLog, companyId, context)) {
			return true;
		}
		// Step 任意期間の集計
		if (this.aggregationOfArbitraryPeriod(execId, companyId, procExec, procExecLog, context)) {
			return true;
		}
		// 外部出力
		if (this.externalOutput(execId, companyId, procExec, procExecLog)) {
			return true;
		}

		// アラーム抽出
		if (this.alarmExtraction(execId, procExec, procExecLog, companyId, context)) {
			return true;
		}

		boolean checkErrAppDaily = false;
		OutputAppRouteDaily outputAppRouteDaily = new OutputAppRouteDaily();
		String errorMessageDaily = "";
		// 承認ルート更新（日次）
		try {
			outputAppRouteDaily = this.appRouteUpdateDailyService.checkAppRouteUpdateDaily(execId, procExec,
					procExecLog);
			if (outputAppRouteDaily.isCheckError1552Daily()) {
				errorMessageDaily = "Msg_1552";
			}
		} catch (Exception e) {
			checkErrAppDaily = true;
			errorMessageDaily = "Msg_1339";
		}

		if (outputAppRouteDaily.isCheckStop()) {
			updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.APP_ROUTE_U_DAI, companyId,
					procExecLog.getExecItemCd().v(), procExec, procExecLog,
					outputAppRouteDaily.isCheckError1552Daily() || checkErrAppDaily, outputAppRouteDaily.isCheckStop(),
					errorMessageDaily);
			return true;
		}
		if (procExec.getExecSetting().getAppRouteUpdateDaily().getAppRouteUpdateAtr() == NotUseAtr.USE) {
			updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.APP_ROUTE_U_DAI, companyId,
					procExecLog.getExecItemCd().v(), procExec, procExecLog,
					outputAppRouteDaily.isCheckError1552Daily() || checkErrAppDaily, false, errorMessageDaily);
		}
		boolean checkErrAppMonth = false;
		String errorMessageMonthly = "";
		OutputAppRouteMonthly outputAppRouteMonthly = new OutputAppRouteMonthly();
		try {
			// 承認ルート更新（月次）
			outputAppRouteMonthly = this.appRouteUpdateMonthlyService.checkAppRouteUpdateMonthly(execId, procExec,
					procExecLog);
			if (outputAppRouteMonthly.isCheckError1552Monthly()) {
				errorMessageMonthly = "Msg_1552";
			}
		} catch (Exception e) {
			checkErrAppMonth = true;
			errorMessageMonthly = "Msg_1339";
		}

		if (procExec.getExecSetting().getAppRouteUpdateMonthly().getAppRouteUpdateAtr() == NotUseAtr.USE) {
			updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.APP_ROUTE_U_MON, companyId,
					procExecLog.getExecItemCd().v(), procExec, procExecLog,
					outputAppRouteMonthly.isCheckError1552Monthly() || checkErrAppMonth,
					outputAppRouteMonthly.isCheckStop(), errorMessageMonthly);
		}
		// データの保存
		if (this.dataStorage(execId, companyId, procExec, procExecLog)) {
			return true;
		}
		
		// データの削除
		if (this.dataDeletion(execId, companyId, procExec, procExecLog)) {
			return true;
		}

		// インデックス再構成
		this.indexReconstruction(execId, companyId, procExec, procExecLog);
		return false;
	}

	/**
	 * スケジュールの作成
	 *
	 * @param execId      実行ID
	 * @param procExec    更新処理自動実行
	 * @param execSetting 実行タスク設定
	 * @param procExecLog 更新処理自動実行ログ
	 */
	private OutputCreateScheduleAndDaily createSchedule(CommandHandlerContext<ExecuteProcessExecutionCommand> context,
			String execId, UpdateProcessAutoExecution procExec, ProcessExecutionLog procExecLog) {
		List<ApprovalPeriodByEmp> listApprovalPeriodByEmp = new ArrayList<>();
		String errorMessage = "";
		boolean isException = false;
		// ドメインモデル「更新処理自動実行ログ」を更新する
		this.updateEachTaskStatus(procExecLog, ProcessExecutionTask.SCH_CREATION, null);
		String companyId = context.getCommand().getCompanyId();
		String execItemCd = context.getCommand().getExecItemCd();
		List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
				execId);
		if (CollectionUtil.isEmpty(taskLogList)) {
			this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		} else {
			this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		}
		this.procExecLogRepo.update(procExecLog);
		boolean checkStopExec = false;
		try {
			// 個人スケジュール作成区分の判定
			if (procExec.getExecSetting().getPerScheduleCreation().getPerScheduleCls() == NotUseAtr.NOT_USE) {
				this.updateStatusAndStartDateNull(procExecLog, ProcessExecutionTask.SCH_CREATION,
						EndStatus.NOT_IMPLEMENT);
				this.procExecLogRepo.update(procExecLog);
				return new OutputCreateScheduleAndDaily(true, listApprovalPeriodByEmp);
			}
			log.info("更新処理自動実行_個人スケジュール作成_START_" + context.getCommand().getExecItemCd() + "_" + GeneralDateTime.now());

			// 新入社員作成区分（Boolean）←属性「新入社員を作成」
			boolean checkCreateEmployee = procExec.getExecSetting().getPerScheduleCreation()
					.getCreateNewEmpSched() == NotUseAtr.USE;
			// 期間の計算
			DatePeriod calculateSchedulePeriod = this.calculateSchedulePeriod(procExec, procExecLog,
					checkCreateEmployee, companyId);

			List<String> workplaceIdList = procExec.getExecScope().getWorkplaceIdList();
			// 更新処理自動実行の実行対象社員リストを取得する
			List<String> listEmp = listEmpAutoExec.getListEmpAutoExec(companyId, calculateSchedulePeriod,
					procExec.getExecScope().getExecScopeCls(), Optional.of(workplaceIdList), Optional.empty());

			/*
			 * 作成対象の判定
			 */
			// 全員の場合
			if (procExec.getExecutionType().equals(ProcessExecType.NORMAL_EXECUTION)) {
				// 対象社員を取得 -

				ScheduleCreatorExecutionCommand scheduleCommand = getScheduleCreatorExecutionAllEmp(execId, procExec,
						calculateSchedulePeriod, listEmp, companyId, execItemCd);

				try {
					this.executeService.handle(scheduleCommand, Optional.of(context.asAsync()));
					if (scheduleCommand.getIsExForKBT()) {
						// 再実行の場合にExceptionが発生したかどうかを確認する。
						if (procExec.getExecutionType() == ProcessExecType.RE_CREATE) {
							checkStopExec = true;
						}
						isException = true;
						errorMessage = "Msg_1339";
					}
					log.info("更新処理自動実行_個人スケジュール作成_END_" + context.getCommand().getExecItemCd() + "_"
							+ GeneralDateTime.now());
					if (checkStop(execId)) {
						checkStopExec = true;
					}
				} catch (Exception e) {
					// 再実行の場合にExceptionが発生したかどうかを確認する。
					if (procExec.getExecutionType() == ProcessExecType.RE_CREATE) {
						checkStopExec = true;
					}
					isException = true;
					errorMessage = "Msg_1339";
				}
			}
			// 異動者・新入社員のみ作成の場合
			else {
				// 対象社員を絞り込む
				EmployeeDataDto filterData = this.changePersionListForSche.filterEmployeeList(procExec, listEmp);
				listEmp = filterData.getEmployeeIds();
				// 社員ID（異動者、勤務種別変更者）（List）のみ
				if (!CollectionUtil.isEmpty(listEmp) && !checkStopExec) {
					// 異動者、勤務種別変更者、休職者・休業者の期間の計算 (RQ 439)
					Optional<GeneralDate> endDate = basicScheduleAdapter.acquireMaxDateBasicSchedule(listEmp);

					if (endDate.isPresent()) {
						ScheduleCreatorExecutionCommand scheduleCreatorExecutionOneEmp = this
								.getScheduleCreatorExecutionOneEmp(execId, procExec,
										calculateSchedulePeriod, listEmp, companyId, execItemCd);
//
						boolean isTransfer = procExec.getReExecCondition().getRecreateTransfer().equals(NotUseAtr.USE);
						boolean isWorkType = procExec.getReExecCondition().getRecreatePersonChangeWkt()
								.equals(NotUseAtr.USE);
						boolean isLeave = procExec.getReExecCondition().getRecreateLeave().isUse();

						// 異動者・勤務種別変更者の作成対象期間の計算（個人別）
						listApprovalPeriodByEmp = calPeriodTransferAndWorktype.calPeriodTransferAndWorktype(companyId,
								listEmp, new DatePeriod(filterData.getStartDate(), endDate.get()),
								isTransfer, isWorkType, isLeave);
						List<DatePeriod> targetDates = listApprovalPeriodByEmp.stream()
								.map(ApprovalPeriodByEmp::getListPeriod)
								.flatMap(List::stream)
								.collect(Collectors.toList());
						if (!targetDates.isEmpty()) {
							GeneralDate targetStartDate = targetDates.stream()
									.map(DatePeriod::start)
									.min(GeneralDate::compareTo)
									.orElse(null);
							GeneralDate targetEndDate = targetDates.stream()
									.map(DatePeriod::end)
									.max(GeneralDate::compareTo)
									.orElse(null);
							scheduleCreatorExecutionOneEmp.getScheduleExecutionLog()
									.setPeriod(new DatePeriod(targetStartDate, targetEndDate));
							try {
								this.executeService.handle(scheduleCreatorExecutionOneEmp, Optional.of(context.asAsync()));
								if (scheduleCreatorExecutionOneEmp.getIsExForKBT()) {
									// 再実行の場合にExceptionが発生したかどうかを確認する。
									if (procExec.getExecutionType() == ProcessExecType.RE_CREATE) {
										checkStopExec = true;
									}
									isException = true;
									errorMessage = "Msg_1339";
								}
								log.info("更新処理自動実行_個人スケジュール作成_END_" + context.getCommand().getExecItemCd() + "_"
										+ GeneralDateTime.now());
								if (checkStop(execId)) {
									checkStopExec = true;
								}
							} catch (Exception e) {
								// 再実行の場合にExceptionが発生したかどうかを確認する。
								if (procExec.getExecutionType() == ProcessExecType.RE_CREATE) {
									checkStopExec = true;
								}
								errorMessage = "Msg_1339";
								isException = true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			errorMessage = "Msg_1552";
			isException = true;

		}
		boolean isInterruption = false;

		updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.SCH_CREATION, companyId, execItemCd, procExec,
				procExecLog, isException, checkStopExec, errorMessage);
		if (isInterruption) {
			return new OutputCreateScheduleAndDaily(false, listApprovalPeriodByEmp);
		}
		if (checkStopExec) {
			return new OutputCreateScheduleAndDaily(false, listApprovalPeriodByEmp);
		}
		return new OutputCreateScheduleAndDaily(true, listApprovalPeriodByEmp);
	}

	private boolean checkStop(String execId) {
		Optional<ExeStateOfCalAndSumImportFn> exeStateOfCalAndSumImportFn = dailyMonthlyprocessAdapterFn
				.executionStatus(execId);
		if (exeStateOfCalAndSumImportFn.isPresent())
			return exeStateOfCalAndSumImportFn.get() == ExeStateOfCalAndSumImportFn.START_INTERRUPTION;
		return false;
	}

	private ScheduleCreatorExecutionCommand getScheduleCreatorExecutionAllEmp(String execId,
			UpdateProcessAutoExecution procExec, DatePeriod calculateSchedulePeriod,
			List<String> empIds, String companyId, String execItemCd) {
		ScheduleCreatorExecutionCommand scheduleCommand = new ScheduleCreatorExecutionCommand();
		scheduleCommand.setExecutionId(execId);
		scheduleCommand.setAutomatic(true);
		scheduleCommand.setEmployeeIds(empIds);
		// 2-対象開始日 ＝ 「期間の計算」で作成した開始日とする
		// companyId
		scheduleCommand.setCompanyId(companyId);
		// 3-対象開始日 ＝ 「期間の計算」で作成した開始日とする
		// 4-対象終了日 ＝ 「期間の計算」で作成した終了日とする
		// calculateSchedulePeriod
		ScheduleExecutionLog scheduleExecutionLog = ScheduleExecutionLog.creator(
				companyId, execId, "", new DatePeriod(calculateSchedulePeriod.start(), calculateSchedulePeriod.end()),
				ExecutionAtr.AUTOMATIC);
		ScheduleCreateContent s = createContent(execId, companyId, execItemCd);
		// ・実施区分 → 通常作成
		s.setImplementAtr(ImplementAtr.CREATE_NEW_ONLY);
		scheduleCommand.setScheduleExecutionLog(scheduleExecutionLog);
		scheduleCommand.setContent(s);
		return scheduleCommand;
	}

	private ScheduleCreatorExecutionCommand getScheduleCreatorExecutionOneEmp(String execId,
			UpdateProcessAutoExecution procExec, DatePeriod calculateSchedulePeriod,
			List<String> empIds, String cid, String execItemCd) {
		ScheduleCreatorExecutionCommand scheduleCommand = new ScheduleCreatorExecutionCommand();
		scheduleCommand.setAutomatic(true);
		scheduleCommand.setEmployeeIds(empIds);
		// 1-実行ID ＝ 取得した実行ID
		// execId
		scheduleCommand.setExecutionId(execId);
		// 2-対象開始日 ＝ 「期間の計算」で作成した開始日とする
		// companyId
		scheduleCommand.setCompanyId(cid);
		// 3-対象開始日 ＝ 「期間の計算」で作成した開始日とする
		// 4-対象終了日 ＝ 「期間の計算」で作成した終了日とする
		// calculateSchedulePeriod
		ScheduleExecutionLog scheduleExecutionLog = ScheduleExecutionLog.creator(
				cid, execId, "", new DatePeriod(calculateSchedulePeriod.start(), calculateSchedulePeriod.end()),
				ExecutionAtr.AUTOMATIC);
		ScheduleCreateContent s = createContent(execId, cid, execItemCd);
		// ・実施区分 → 再作成 とする
		s.setImplementAtr(ImplementAtr.CREATE_WORK_SCHEDULE);
		scheduleCommand.setContent(s);
		scheduleCommand.setScheduleExecutionLog(scheduleExecutionLog);
		return scheduleCommand;
	}

	/**
	 * 日別作成・計算
	 *
	 * @param execId      実行ID
	 * @param procExec    更新処理自動実行
	 * @param execSetting 実行タスク設定
	 * @param procExecLog 更新処理自動実行ログ
	 */
	private OutputCreateScheduleAndDaily createDailyData(CommandHandlerContext<ExecuteProcessExecutionCommand> context,
			EmpCalAndSumExeLog empCalAndSumExeLog, String execId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog) {
		List<ApprovalPeriodByEmp> listApprovalPeriodByEmp = new ArrayList<>();
		ExecuteProcessExecutionCommand command = context.getCommand();
		String companyId = command.getCompanyId();
//		boolean checkError1552 = false;
		String errorMessage = "";
		// 個人スケジュール作成区分の判定
		if (procExec.getExecSetting().getPerScheduleCreation().getPerScheduleCls().equals(NotUseAtr.USE)) {
			this.updateEndtimeSchedule(procExecLog);
		}
		// ドメインモデル「更新処理自動実行ログ」を更新する
		this.updateEachTaskStatus(procExecLog, ProcessExecutionTask.DAILY_CREATION, null);
		this.updateEachTaskStatus(procExecLog, ProcessExecutionTask.DAILY_CALCULATION, null);
		this.procExecLogRepo.update(procExecLog);
		// 個人スケジュール作成区分の判定 --set time end( bao gồm cả tg nhả bộ nhớ mà schedule đã dùng)
		if (procExec.getExecSetting().getPerScheduleCreation().getPerScheduleCls().equals(NotUseAtr.USE)) {
			this.updateEndtimeSchedule(procExecLog);
		}
		// 日別実績の作成・計算区分の判定
		if (!procExec.getExecSetting().getDailyPerf().getDailyPerfCls().equals(NotUseAtr.USE)) {
			this.updateStatusAndStartDateNull(procExecLog, ProcessExecutionTask.DAILY_CREATION,
					EndStatus.NOT_IMPLEMENT);
			this.updateStatusAndStartDateNull(procExecLog, ProcessExecutionTask.DAILY_CALCULATION,
					EndStatus.NOT_IMPLEMENT);
			this.procExecLogRepo.update(procExecLog);
			return new OutputCreateScheduleAndDaily(true, listApprovalPeriodByEmp);
		}
		log.info("更新処理自動実行_日別実績の作成・計算_START_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
		String execItemCd = context.getCommand().getExecItemCd();
		List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
				execId);
		if (CollectionUtil.isEmpty(taskLogList)) {
			this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		} else {
			this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		}

		// ドメインモデル「就業締め日」を取得する
		List<Closure> closureList = this.closureRepo.findAllActive(companyId, UseClassification.UseClass_Use);
		DatePeriod period = this.findClosureMinMaxPeriod(companyId, closureList);

		// ドメインモデル「実行ログ」を新規登録する
		ExecutionLog dailyCreateLog = new ExecutionLog(execId, ExecutionContent.DAILY_CREATION, ErrorPresent.NO_ERROR,
				new ExecutionTime(GeneralDateTime.now(), GeneralDateTime.now()), ExecutionStatus.INCOMPLETE,
				new ObjectPeriod(period.end(), period.end()), Optional.empty());
		dailyCreateLog.setDailyCreationSetInfo(
				new SettingInforForDailyCreation(ExecutionContent.DAILY_CREATION, ExecutionType.NORMAL_EXECUTION,
						IdentifierUtil.randomUniqueId(), DailyRecreateClassification.REBUILD, Optional.empty()));
		ExecutionLog dailyCalLog = new ExecutionLog(execId, ExecutionContent.DAILY_CALCULATION, ErrorPresent.NO_ERROR,
				new ExecutionTime(GeneralDateTime.now(), GeneralDateTime.now()), ExecutionStatus.INCOMPLETE,
				new ObjectPeriod(period.start(), period.end()), Optional.empty());
		dailyCalLog.setDailyCalSetInfo(new CalExeSettingInfor(ExecutionContent.DAILY_CALCULATION,
				ExecutionType.NORMAL_EXECUTION, IdentifierUtil.randomUniqueId()));
		this.executionLogRepository.addExecutionLog(dailyCreateLog);
		this.executionLogRepository.addExecutionLog(dailyCalLog);

		// ドメインモデル「更新処理自動実行ログ」を更新する
		if (procExecLog.getEachProcPeriod() != null && procExecLog.getEachProcPeriod().isPresent()) {
			EachProcessPeriod eachProcessPeriod = procExecLog.getEachProcPeriod().get();
			DatePeriod scheduleCreationPeriod = (eachProcessPeriod.getScheduleCreationPeriod() != null
					&& eachProcessPeriod.getScheduleCreationPeriod().isPresent())
							? eachProcessPeriod.getScheduleCreationPeriod().get()
							: null;
			DatePeriod reflectApprovalResult = (eachProcessPeriod.getReflectApprovalResult() != null
					&& eachProcessPeriod.getReflectApprovalResult().isPresent())
							? eachProcessPeriod.getReflectApprovalResult().get()
							: null;
			procExecLog.setEachProcPeriod(
					new EachProcessPeriod(scheduleCreationPeriod, period, period, reflectApprovalResult));
		} else {
			procExecLog.setEachProcPeriod(new EachProcessPeriod(null, period, period, null));
		}

		boolean isHasCreateDailyException = false;
		boolean isHasDailyCalculateException = false;
		try {
			for (Closure closure : closureList) {

				// 雇用コードを取得する
				List<ClosureEmployment> employmentList = this.closureEmpRepo.findByClosureId(companyId,
						closure.getClosureId().value);
				List<String> lstEmploymentCode = new ArrayList<String>();
				employmentList.forEach(x -> {
					lstEmploymentCode.add(x.getEmploymentCD());
				});
				List<String> workplaceIdList = procExec.getExecScope().getWorkplaceIdList();

				if (procExec.getExecutionType() == ProcessExecType.NORMAL_EXECUTION) {
					// 実行呼び出し処理
					// 期間の計算
					DailyCreatAndCalOutput calculateDailyPeriod = this.calculateDailyPeriod(procExec, closure);
					if (calculateDailyPeriod == null)
						continue;
					// 更新処理自動実行の実行対象社員リストを取得する
					List<String> listEmp = listEmpAutoExec.getListEmpAutoExec(companyId,
							calculateDailyPeriod.getDailyCreationPeriod(), procExec.getExecScope().getExecScopeCls(),
							Optional.of(workplaceIdList), Optional.of(lstEmploymentCode));
					String typeExecution = "日別作成";
					// 日別実績の作成
					try {
						log.info("更新処理自動実行_日別実績の作成_START_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
						boolean dailyPerformanceCreation = this.dailyPerformanceCreation(companyId, context, procExec,
								empCalAndSumExeLog, listEmp, calculateDailyPeriod.getDailyCreationPeriod(),
								workplaceIdList, typeExecution, dailyCreateLog);

						if (dailyPerformanceCreation) {
							updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CREATION, companyId,
									execItemCd, procExec, procExecLog, isHasCreateDailyException, true, errorMessage);
							updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CALCULATION,
									companyId, execItemCd, procExec, procExecLog, isHasDailyCalculateException, true,
									errorMessage);
							return new OutputCreateScheduleAndDaily(false, listApprovalPeriodByEmp);
						}
					} catch (CreateDailyException ex) {
						isHasCreateDailyException = true;
						errorMessage = "Msg_1339";
					}
					log.info("更新処理自動実行_日別実績の作成_END_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now()
							+ "closure : " + closure.getClosureId().value);
					log.info("更新処理自動実行_日別実績の計算_START_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now()
							+ "closure : " + closure.getClosureId().value);

					typeExecution = "日別計算";
					// 日別実績の計算
					try {
						boolean dailyPerformanceCreation2 = this.dailyPerformanceCreation(companyId, context, procExec,
								empCalAndSumExeLog, listEmp, calculateDailyPeriod.getDailyCalcPeriod(), workplaceIdList,
								typeExecution, dailyCalLog);
						log.info("更新処理自動実行_日別実績の計算_END_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
						if (dailyPerformanceCreation2) {
							updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CREATION, companyId,
									execItemCd, procExec, procExecLog, isHasCreateDailyException, true, errorMessage);
							updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CALCULATION,
									companyId, execItemCd, procExec, procExecLog, isHasDailyCalculateException, true,
									errorMessage);
							return new OutputCreateScheduleAndDaily(false, listApprovalPeriodByEmp);
						}
					} catch (DailyCalculateException ex) {
						isHasDailyCalculateException = true;
						errorMessage = "Msg_1339";
					}

				} else {
					GeneralDate calculateDate = this.calculatePeriod(closure.getClosureId().value, period, companyId);
					// 更新処理自動実行の実行対象社員リストを取得する
					List<String> listEmp = listEmpAutoExec.getListEmpAutoExec(companyId,
							new DatePeriod(calculateDate, GeneralDate.ymd(9999, 12, 31)),
							procExec.getExecScope().getExecScopeCls(), Optional.of(workplaceIdList),
							Optional.of(lstEmploymentCode));

					// 異動者・勤務種別変更者リスト作成処理
					ListLeaderOrNotEmp listLeaderOrNotEmp = changePersionList
							.createProcessForChangePerOrWorktype(companyId, listEmp, calculateDate, procExec);

					boolean isHasInterrupt = false;
					for (String empLeader : listLeaderOrNotEmp.getLeaderEmpIdList()) {
						// ドメインモデル「日別実績の勤務情報」を取得する
						List<WorkInfoOfDailyPerFnImport> listWorkInfo = recordWorkInfoFunAdapter
								.findByPeriodOrderByYmd(empLeader);

						if (listWorkInfo.isEmpty())
							continue;
						// 再作成処理
						// 日別実績処理の再実行
						// 「作成した開始日」～「取得した日別実績の勤務情報.年月日」を対象期間とする
						GeneralDate maxDate = listWorkInfo.stream().map(u -> u.getYmd()).max(GeneralDate::compareTo)
								.get();
						if (calculateDate.beforeOrEquals(maxDate)) {
							DatePeriod datePeriod = new DatePeriod(calculateDate, maxDate);
							List<DatePeriod> listDatePeriodWorkplace = new ArrayList<>();
							List<DatePeriod> listDatePeriodWorktype = new ArrayList<>();
							List<DatePeriod> listDatePeriodLeave = new ArrayList<>();
							List<DatePeriod> listDatePeriodAll = new ArrayList<>();
							// INPUT．「異動時に再作成」をチェックする
							if (procExec.getReExecCondition().getRecreateTransfer()
									.equals(NotUseAtr.USE)) {
								// 社員ID（List）と期間から個人情報を取得する - RQ401
								EmployeeGeneralInfoImport employeeGeneralInfoImport = employeeGeneralInfoAdapter
										.getEmployeeGeneralInfo(Arrays.asList(empLeader), datePeriod, false, false,
												false, true, false); // 職場を取得するか = True
								if (!employeeGeneralInfoImport.getExWorkPlaceHistoryImports().isEmpty()) {
									nts.uk.ctx.at.function.dom.statement.dtoimport.ExWorkPlaceHistoryImport exWorkPlaceHistoryImportFn = employeeGeneralInfoImport
											.getExWorkPlaceHistoryImports().get(0);
									List<ExWorkplaceHistItemImport> workplaceItems = exWorkPlaceHistoryImportFn
											.getWorkplaceItems().stream()
											.map(c -> new ExWorkplaceHistItemImport(c.getHistoryId(), c.getPeriod(),
													c.getWorkplaceId()))
											.collect(Collectors.toList());
									// 職場情報変更期間を求める
									listDatePeriodWorkplace = wkplaceInfoChangePeriod
											.getWkplaceInfoChangePeriod(empLeader, datePeriod, workplaceItems, true);
								}
							}
							boolean check = false;
							if (listDatePeriodWorkplace.size() == 1
									&& listDatePeriodWorkplace.get(0).equals(datePeriod)) {
								listDatePeriodAll.addAll(listDatePeriodWorkplace);
								check = true;
							}
							// INPUT．「勤務種別変更時に再作成」をチェックする
							if (procExec.getReExecCondition().getRecreatePersonChangeWkt()
									.equals(NotUseAtr.USE) && !check) {
								// <<Public>> 社員ID(List)、期間で期間分の勤務種別情報を取得する
								List<BusinessTypeOfEmployeeHis> listBusinessTypeOfEmpDto = businessTypeOfEmpHisService.find(Arrays.asList(empLeader), datePeriod);
								// 勤務種別情報変更期間を求める
								listDatePeriodWorktype = wkTypeInfoChangePeriod.getWkTypeInfoChangePeriod(empLeader, datePeriod, 
																					listBusinessTypeOfEmpDto, true);
							}
							// INPUT．「休職・休業者再作成」をチェックする
                            if (procExec.getReExecCondition().getRecreateLeave().isUse()) {
                            	// 社員（List）と期間から休職休業を取得する
                            	TempAbsenceImport tempAbsence = this.tempAbsenceHistoryService
                            			.getTempAbsence(companyId, datePeriod, Arrays.asList(empLeader));
                            	// 休職休業履歴変更期間を求める
                            	listDatePeriodLeave = this.tempAbsenceHistoryService
                            			.findChangingLeaveHistoryPeriod(empLeader, datePeriod, tempAbsence,
                            					procExec.getReExecCondition().getRecreateLeave().isUse(),
                            					ProcessExecutionTask.DAILY_CALCULATION);
                            }
							listDatePeriodAll
									.addAll(createListAllPeriod(listDatePeriodWorkplace, listDatePeriodWorktype, listDatePeriodLeave));

							// 取り除いた期間をOUTPUT「承認結果の反映対象期間（List）」に追加する
							listApprovalPeriodByEmp.add(new ApprovalPeriodByEmp(empLeader, listDatePeriodAll));
							try {
								for (DatePeriod p : listDatePeriodAll) {
									log.info("更新処理自動実行_日別実績の作成_START_" + procExec.getExecItemCode() + "_"
											+ GeneralDateTime.now());
									isHasInterrupt = this.redoDailyPerformanceProcessing(context, companyId, empLeader,
											p, empCalAndSumExeLog.getEmpCalAndSumExecLogID(), dailyCreateLog, procExec);
									if (isHasInterrupt) {
										updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CREATION,
												companyId, execItemCd, procExec, procExecLog, isHasCreateDailyException,
												true, errorMessage);
										updateLogAfterProcess.updateLogAfterProcess(
												ProcessExecutionTask.DAILY_CALCULATION, companyId, execItemCd, procExec,
												procExecLog, isHasDailyCalculateException, true, errorMessage);
										return new OutputCreateScheduleAndDaily(false, listApprovalPeriodByEmp);
									}
								}
							} catch (RuntimeException ex) {
								if (!DeadLock.isSQLDeadLock(ex)) {
                                	if (ex instanceof CreateDailyException) {
                                        //create error
                                        isHasCreateDailyException = true;
                                    } else if (ex instanceof DailyCalculateException) {
                                        //calculation error
                                        isHasDailyCalculateException = true;
                                    } else {
                            			ex.printStackTrace();
                                        isHasCreateDailyException = true;
                                        isHasDailyCalculateException = true;
                                    }
                                    errorMessage = "Msg_1339";
                                } else {
                        			ex.printStackTrace();
                                }
							}
						}
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			isHasCreateDailyException = true;
			isHasDailyCalculateException = true;
			errorMessage = "Msg_1552";
		}
		log.info("更新処理自動実行_日別実績の作成・計算_END_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
		// exceptionがあるか確認する（日別作成）
		// ドメインモデル「エラーメッセージ情報」を取得する
		updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CREATION, companyId, execItemCd,
				procExec, procExecLog, isHasCreateDailyException, false, errorMessage);
		updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DAILY_CALCULATION, companyId, execItemCd,
				procExec, procExecLog, isHasDailyCalculateException, false, errorMessage);
		return new OutputCreateScheduleAndDaily(true, listApprovalPeriodByEmp);
	}

	private List<DatePeriod> createListAllPeriod(List<DatePeriod> list1, List<DatePeriod> list2, List<DatePeriod> list3) {

		List<DatePeriod> listAll = new ArrayList<>();
		listAll.addAll(list1);
		listAll.addAll(list2);
		listAll.addAll(list3);

		return mergePeriod(listAll);
	}

	private List<DatePeriod> mergePeriod(List<DatePeriod> listAll) {
		List<DatePeriod> listResult = new ArrayList<>();
		listAll.sort((x, y) -> x.start().compareTo(y.start()));
		for (int i = 0; i < listAll.size(); i++) {
			DatePeriod merged = new DatePeriod(listAll.get(i).start(), listAll.get(i).end());
			for (int j = i + 1; j < listAll.size(); j++) {
				DatePeriod next = listAll.get(j);
				if (merged.contains(next.start()) && merged.contains(next.end())) {
					i++;
				} else if (merged.contains(next.start()) || merged.end().addDays(1).equals(next.start())) {
					merged = merged.cutOffWithNewEnd(next.end());
					i++;
				} else {
					break;
				}
			}
			listResult.add(merged);
		}
		return listResult;
	}

	private DatePeriod findClosureMinMaxPeriod(String companyId, List<Closure> closureList) {
		GeneralDate startYearMonth = null;
		GeneralDate endYearMonth = null;
		for (Closure closure : closureList) {
			DatePeriod datePeriod = ClosureService.getClosurePeriod(closure.getClosureId().value,
					closure.getClosureMonth().getProcessingYm(), Optional.of(closure));

			if (startYearMonth == null || datePeriod.start().before(startYearMonth)) {
				startYearMonth = datePeriod.start();
			}

			if (endYearMonth == null || datePeriod.end().after(endYearMonth)) {
				endYearMonth = datePeriod.end();
			}
		}

		return new DatePeriod(startYearMonth, endYearMonth);
	}

	private void updateEndtimeSchedule(ProcessExecutionLog procExecLog) {
		procExecLog.getTaskLogList().forEach(task -> {
			if (task.getProcExecTask().value == ProcessExecutionTask.SCH_CREATION.value) {
				task.setLastEndExecDateTime(GeneralDateTime.now());
			}
		});
	}

	private void updateEachTaskStatus(ProcessExecutionLog procExecLog, ProcessExecutionTask execTask,
			EndStatus status) {
		procExecLog.getTaskLogList().forEach(task -> {
			if (execTask.value == task.getProcExecTask().value) {
				task.setStatus(status);
				task.setLastExecDateTime(GeneralDateTime.now());
				task.setErrorBusiness(null);
				task.setErrorSystem(null);
				task.setLastEndExecDateTime(null);
			}
		});
	}

	private void updateStatusAndStartDateNull(ProcessExecutionLog procExecLog, ProcessExecutionTask execTask,
			EndStatus status) {
		boolean hasTask = false;
        for (ExecutionTaskLog task: procExecLog.getTaskLogList()) {
            if (execTask.value == task.getProcExecTask().value) {
            	hasTask = true;
                task.setStatus(status);
                task.setLastExecDateTime(null);
                task.setErrorBusiness(null);
                task.setErrorSystem(null);
                task.setLastEndExecDateTime(null);
            }
        }
        if (!hasTask) {
        	procExecLog.getTaskLogList().add(ExecutionTaskLog.builder()
        			.procExecTask(execTask)
        			.status(Optional.ofNullable(status))
        			.build());
        }
	}

	private void initAllTaskStatus(ProcessExecutionLog procExecLog, EndStatus status) {
		if (CollectionUtil.isEmpty(procExecLog.getTaskLogList())) {
			procExecLog.initTaskLogList();
		}
		procExecLog.getTaskLogList().forEach(task -> {
			task.setStatus(status);
		});
	}

	/**
	 * 個人スケジュール作成期間を計算する
	 *
	 * @param procExec
	 * @return 期間
	 */
	private DatePeriod calculateSchedulePeriod(UpdateProcessAutoExecution procExec, ProcessExecutionLog procExecLog,
			boolean checkCreateEmployee, String cid) {

		GeneralDate today = GeneralDate.today();
		int targetMonth = procExec.getExecSetting().getPerScheduleCreation().getPerSchedulePeriod()
				.getTargetMonth().value;
		int targetDate = procExec.getExecSetting().getPerScheduleCreation().getPerSchedulePeriod().getTargetDate()
				.map(TargetDate::v).map(Integer::intValue).orElse(0);
		int startMonth = today.month();
		GeneralDate startDate;
		switch (targetMonth) {
		case 0:
			startMonth = today.month();
			startDate = GeneralDate.ymd(today.year(), startMonth, targetDate);
			break;
		case 1:
			startMonth = today.month();
			startDate = GeneralDate.ymd(today.year(), startMonth, targetDate).addMonths(1);
			break;
		case 2:
			startMonth = today.month();
			startDate = GeneralDate.ymd(today.year(), startMonth, targetDate).addMonths(2);
			break;
		// 開始月を指定する の場合
		case 3:
			PersonalScheduleCreationPeriod creationPeriod = procExec.getExecSetting().getPerScheduleCreation()
					.getPerSchedulePeriod();
			// 個人スケジュール作成期間の年を計算する
			int year = GeneralDate.today().year();
			if (creationPeriod.getDesignatedYear().get() == CreateScheduleYear.FOLLOWING_YEAR) {
				year = year + 1;
			}
			// 個人スケジュール作成期間の月日を計算する
			startDate = GeneralDate.ymd(year, creationPeriod.getStartMonthDay().get().getMonth(),
					creationPeriod.getStartMonthDay().get().getDay());
			break;
		default:
			startDate = GeneralDate.ymd(today.year(), startMonth, targetDate);
			break;
		}
		int createPeriod = procExec.getExecSetting().getPerScheduleCreation().getPerSchedulePeriod().getCreationPeriod()
				.map(CreationPeriod::v).map(Integer::intValue).orElse(0);
		GeneralDate endDate;
		if (targetMonth == TargetMonth.DESIGNATE_START_MONTH.value) {
			PersonalScheduleCreationPeriod creationPeriod = procExec.getExecSetting().getPerScheduleCreation()
					.getPerSchedulePeriod();
			// 個人スケジュール作成期間の年を計算する
			int year = GeneralDate.today().year();
			if (creationPeriod.getDesignatedYear().get() == CreateScheduleYear.FOLLOWING_YEAR) {
				year = year + 1;
			}
			// 個人スケジュール作成期間の月日を計算する
			endDate = GeneralDate.ymd(year, creationPeriod.getEndMonthDay().get().getMonth(),
					creationPeriod.getEndMonthDay().get().getDay());
			if (endDate.before(startDate)) {
				endDate = endDate.addYears(1);
			}
		} else {
			if (targetDate == 1) {
				GeneralDate date = GeneralDate.ymd(startDate.year(), startDate.month(), 1).addMonths(createPeriod - 1);
				int dateMax = date.lastDateInMonth();
				endDate = GeneralDate.ymd(date.year(), date.month(), dateMax);
			} else {
				GeneralDate dateTest = GeneralDate.ymd(startDate.year(), startDate.month(), 1).addMonths(createPeriod);
				int maxdate = dateTest.lastDateInMonth();
				if (maxdate < (targetDate - 1)) {
					targetDate = maxdate;
				}
				endDate = GeneralDate.ymd(startDate.year(), startDate.month(), targetDate).addMonths(createPeriod)
						.addDays(-1);
			}
		}
		// パラメータ「新入社員作成区分」を判断 : true
		if (checkCreateEmployee) {
			// 全締めから一番早い期間.開始日を取得する
			// 開始日を取得した開始日に置き換える
			startDate = getMinPeriodFromStartDate(cid).start();
		}

		if (procExecLog.getEachProcPeriod() == null || procExecLog.getEachProcPeriod().isPresent()) {
			procExecLog.setEachProcPeriod(new EachProcessPeriod(new DatePeriod(startDate, endDate), null, null, null));
		} else {
			procExecLog.getEachProcPeriod().get().setScheduleCreationPeriod(new DatePeriod(startDate, endDate));
		}
		// ドメインモデル「更新処理自動実行ログ」を更新する
		// this.procExecLogRepo.update(procExecLog);
		return new DatePeriod(startDate, endDate);
	}

	// 期間を求める
	private DailyCreatAndCalOutput calculateDailyPeriod(UpdateProcessAutoExecution procExec, Closure closure) {
		int closureId = closure.getClosureId().value;
		CurrentMonth currentMonth = closure.getClosureMonth();
		DatePeriod closurePeriod = ClosureService.getClosurePeriod(closureId, currentMonth.getProcessingYm(),
				Optional.of(closure));

		// ドメインモデル「更新処理自動実行.実行設定.日別実績の作成・計算.作成・計算項目」を元に日別作成の期間を作成する
		GeneralDate crtStartDate = null;
		GeneralDate crtEndDate = null;
		// ドメインモデル「更新処理自動実行.実行設定.日別実績の作成・計算.作成・計算項目」を元に日別計算の期間を作成する
		GeneralDate calStartDate = null;
		GeneralDate calEndDate = null;

		GeneralDate lastExecDate = GeneralDate.today();
		GeneralDate today = GeneralDate.today();
		// ドメインモデル「更新処理前回実行日時」を取得する
		Optional<LastExecDateTime> lastDateTimeOpt = lastExecDateTimeRepo.get(procExec.getCompanyId(),
				procExec.getExecItemCode().v());
		if (lastDateTimeOpt.isPresent()) {
			GeneralDateTime lastExecDateTime = lastDateTimeOpt.get().getLastExecDateTime();
			if (lastExecDateTime != null) {
				lastExecDate = GeneralDate.ymd(lastExecDateTime.year(), lastExecDateTime.month(),
						lastExecDateTime.day());
			}
		}

		switch (procExec.getExecSetting().getDailyPerf().getDailyPerfItem()) {
		case FIRST_OPT:
			crtStartDate = lastExecDate;
			crtEndDate = today;
			calStartDate = lastExecDate;
			calEndDate = today;
			if (lastDateTimeOpt.isPresent()) {
				if (lastDateTimeOpt.get().getLastExecDateTime() == null) {
					crtStartDate = GeneralDate.ymd(currentMonth.getProcessingYm().year(),
							currentMonth.getProcessingYm().month(), 1);
					calStartDate = GeneralDate.ymd(currentMonth.getProcessingYm().year(),
							currentMonth.getProcessingYm().month(), 1);
				}
			}
			break;
		case SECOND_OPT:
			crtStartDate = lastExecDate;
			crtEndDate = today;
			calStartDate = closurePeriod.start();
			calEndDate = today;
			break;
		case THIRD_OPT:
			crtStartDate = closurePeriod.start();
			crtEndDate = today;
			calStartDate = closurePeriod.start();
			calEndDate = today;
			break;
		case FOURTH_OPT:
			crtStartDate = closurePeriod.start();
			crtEndDate = closurePeriod.end();
			calStartDate = closurePeriod.start();
			calEndDate = closurePeriod.end();
			break;
		case FIFTH_OPT:
			crtStartDate = closurePeriod.start();
			GeneralDate closurePeriodFifth = closurePeriod.end().addMonths(1);
			int lastDateFifth = closurePeriodFifth.yearMonth().lastDateInMonth();
			crtEndDate = GeneralDate.ymd(closurePeriodFifth.year(), closurePeriodFifth.month(), lastDateFifth);
			calStartDate = closurePeriod.start();
			calEndDate = GeneralDate.ymd(closurePeriodFifth.year(), closurePeriodFifth.month(), lastDateFifth);
			break;
		case SIXTH_OPT:
			GeneralDate closurePeriodSixth = closurePeriod.end().addMonths(1);
			int lastDateSixth = closurePeriodSixth.yearMonth().lastDateInMonth();
			crtStartDate = closurePeriod.start().addMonths(1);
			crtEndDate = GeneralDate.ymd(closurePeriodSixth.year(), closurePeriodSixth.month(), lastDateSixth);
			calStartDate = closurePeriod.start().addMonths(1);
			calEndDate = GeneralDate.ymd(closurePeriodSixth.year(), closurePeriodSixth.month(), lastDateSixth);
			break;
		case SEVENTH_OPT:
			GeneralDate todayNow = GeneralDate.today();
			GeneralDate startDate = GeneralDate.today();
			GeneralDate endDate = GeneralDate.today();
			// monthtly 12
			if (todayNow.month() == 12 && todayNow.day() > 28) {
				if (todayNow.day() == 30 || todayNow.day() == 31)
					return null;
				if (todayNow.day() == 29) {
					if (todayNow.addMonths(2).lastDateInMonth() == 29) {
						startDate = todayNow.addMonths(2);
						endDate = todayNow.addMonths(2);
					} else {
						return null;
					}
				}
				// monthly 2
			} else if (todayNow.month() == 2 && todayNow.day() == todayNow.lastDateInMonth()) {
				startDate = todayNow.addMonths(2);
				endDate = todayNow.addMonths(2);
				endDate = endDate.addDays(todayNow.lastDateInMonth() - todayNow.day());
				// end monthly = end monthly + 2 monthly
			} else if (todayNow.day() == todayNow.lastDateInMonth()
					&& todayNow.addMonths(2).day() == todayNow.addMonths(2).lastDateInMonth()) {
				startDate = todayNow.addMonths(2);
				endDate = todayNow.addMonths(2);
			} else {
				startDate = todayNow.addMonths(2);
				endDate = todayNow.addMonths(2).addDays(1);

			}
			crtStartDate = startDate;
			crtEndDate = endDate;
			calStartDate = startDate;
			calEndDate = endDate;
			break;
		}
		DailyCreatAndCalOutput dailyCreatAndCalOutput = new DailyCreatAndCalOutput();
		dailyCreatAndCalOutput.setDailyCreationPeriod(new DatePeriod(crtStartDate, crtEndDate));
		dailyCreatAndCalOutput.setDailyCalcPeriod(new DatePeriod(calStartDate, calEndDate));
		return dailyCreatAndCalOutput;
	}

	// 承認結果反映
	private boolean reflectApprovalResult(String execId, UpdateProcessAutoExecution processExecution,
			ProcessExecutionLog ProcessExecutionLog, String companyId, List<ApprovalPeriodByEmp> lstApprovalPeriod) {
		// ドメインモデル「更新処理自動実行ログ」を更新する
		List<ExecutionTaskLog> taskLogLists = ProcessExecutionLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		int size = taskLogLists.size();
		boolean existExecutionTaskLog = false;
//		boolean checkError1552 = false;
		String errorMessage = "";
		boolean checkStopExec = false;
		String execItemCd = ProcessExecutionLog.getExecItemCd().v();
		for (int i = 0; i < size; i++) {
			ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
			// 承認結果反映
			if (executionTaskLog.getProcExecTask().equals(ProcessExecutionTask.RFL_APR_RESULT)) {
				executionTaskLog.setStatus(null);
				executionTaskLog.setLastExecDateTime(GeneralDateTime.now());
				executionTaskLog.setErrorBusiness(null);
				executionTaskLog.setErrorSystem(null);
				executionTaskLog.setLastEndExecDateTime(null);
				existExecutionTaskLog = true;
				break;
			}
		}
		if (!existExecutionTaskLog) {
			ExecutionTaskLog exeTaskLog = ExecutionTaskLog.builder()
					.procExecTask(
							EnumAdaptor.valueOf(ProcessExecutionTask.RFL_APR_RESULT.value, ProcessExecutionTask.class))
					.lastExecDateTime(Optional.ofNullable(GeneralDateTime.now())).build();
			taskLogLists.add(exeTaskLog);
		}
		this.procExecLogRepo.update(ProcessExecutionLog);

		boolean reflectResultCls = processExecution.getExecSetting().getReflectAppResult().getReflectResultCls()
				.equals(NotUseAtr.USE);
		// 承認結果反映の判定
		if (!reflectResultCls) {
			// ドメインモデル「更新処理自動実行ログ」を更新する
			for (int i = 0; i < size; i++) {
				ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
				// 承認結果反映
				if (executionTaskLog.getProcExecTask().equals(ProcessExecutionTask.RFL_APR_RESULT)) {
					// 未実施
					executionTaskLog.setStatus(EndStatus.NOT_IMPLEMENT);
					executionTaskLog.setLastExecDateTime(null);
				}
			}
			this.procExecLogRepo.update(ProcessExecutionLog);
			return false; // chua confirm
		}
		log.info("更新処理自動実行_承認結果の反映_START_" + processExecution.getExecItemCode() + "_" + GeneralDateTime.now());
		boolean endStatusIsInterrupt = false;
		boolean isHasException = false;
		// ドメインモデル「就業締め日」を取得する
		List<Closure> lstClosure = this.closureRepo.findAllUse(companyId);

		// ドメインモデル「実行ログ」を新規登録する
		DatePeriod period = this.findClosurePeriodMinDate(companyId, lstClosure);
		GeneralDateTime now = GeneralDateTime.now();
		ExecutionLog executionLog = new ExecutionLog(execId, ExecutionContent.REFLRCT_APPROVAL_RESULT,
				ErrorPresent.NO_ERROR, new ExecutionTime(now, now), ExecutionStatus.INCOMPLETE,
				new ObjectPeriod(period.start(), period.end()), Optional.empty());
		executionLog.setReflectApprovalSetInfo(new SetInforReflAprResult(ExecutionContent.REFLRCT_APPROVAL_RESULT,
				processExecution.getExecutionType() == ProcessExecType.NORMAL_EXECUTION ? ExecutionType.NORMAL_EXECUTION
						: ExecutionType.RERUN,
				IdentifierUtil.randomUniqueId(), false));
		this.executionLogRepository.addExecutionLog(executionLog);
		// ドメインモデル「更新処理自動実行ログ」を更新する
		if (ProcessExecutionLog.getEachProcPeriod() != null && ProcessExecutionLog.getEachProcPeriod().isPresent()) {
			EachProcessPeriod eachProcessPeriod = ProcessExecutionLog.getEachProcPeriod().get();
			DatePeriod scheduleCreationPeriod = (eachProcessPeriod.getScheduleCreationPeriod() != null
					&& eachProcessPeriod.getScheduleCreationPeriod().isPresent())
							? eachProcessPeriod.getScheduleCreationPeriod().get()
							: null;
			DatePeriod dailyCreationPeriod = (eachProcessPeriod.getDailyCreationPeriod() != null
					&& eachProcessPeriod.getDailyCreationPeriod().isPresent())
							? eachProcessPeriod.getDailyCreationPeriod().get()
							: null;
			DatePeriod dailyCalcPeriod = (eachProcessPeriod.getDailyCalcPeriod() != null
					&& eachProcessPeriod.getDailyCalcPeriod().isPresent())
							? eachProcessPeriod.getDailyCalcPeriod().get()
							: null;
			ProcessExecutionLog.setEachProcPeriod(
					new EachProcessPeriod(scheduleCreationPeriod, dailyCreationPeriod, dailyCalcPeriod, period));

		} else {
			ProcessExecutionLog.setEachProcPeriod(new EachProcessPeriod(null, null, null, period));
		}
		if (processExecution.getExecutionType() == ProcessExecType.NORMAL_EXECUTION) {
			try {
				int sizeClosure = lstClosure.size();
				for (int i = 0; i < sizeClosure; i++) {
					Closure closure = lstClosure.get(i);
					// 雇用コードを取得する ~ 締めに紐付く雇用コード一覧を取得
					List<ClosureEmployment> employmentList = this.closureEmpRepo.findByClosureId(companyId,
							closure.getClosureId().value);
					// 雇用コードを取得する
					List<String> lstEmploymentCode = new ArrayList<String>();
					employmentList.forEach(x -> {
						lstEmploymentCode.add(x.getEmploymentCD());
					});

					// 指定した年月の期間を算出する
					DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(closure.getClosureId().value,
							closure.getClosureMonth().getProcessingYm(), Optional.of(closure));
					// 取得した「締め期間」から「期間」を計算する
					DatePeriod newDatePeriod = new DatePeriod(datePeriodClosure.start(), GeneralDate.ymd(9999, 12, 31));

					List<String> workplaceIdList = processExecution.getExecScope().getWorkplaceIdList();
					// 更新処理自動実行の実行対象社員リストを取得する
					List<String> listEmp = listEmpAutoExec.getListEmpAutoExec(companyId, newDatePeriod,
							processExecution.getExecScope().getExecScopeCls(), Optional.of(workplaceIdList),
							Optional.of(lstEmploymentCode));
					List<String> leaderEmpIdList = new ArrayList<>();
					List<String> lstRegulationInfoEmployeeNew = new ArrayList<>();
					if (leaderEmpIdList.isEmpty()) {
						lstRegulationInfoEmployeeNew = listEmp;
					} else {
						for (String emp : listEmp) {
							for (String empId : leaderEmpIdList) {
								if (emp.equals(empId)) {
									lstRegulationInfoEmployeeNew.add(empId);
									break;
								}
							}
						}
					}
					int sizeEmployee = lstRegulationInfoEmployeeNew.size();
					for (int j = 0; j < sizeEmployee; j++) {
						try {
							String regulationInfoEmployeeAdapterDto = lstRegulationInfoEmployeeNew.get(j);
							ProcessStateReflectImport processStateReflectImport = appReflectManagerAdapter
									.reflectAppOfEmployeeTotal(execId, regulationInfoEmployeeAdapterDto, newDatePeriod);
							if (processStateReflectImport == ProcessStateReflectImport.INTERRUPTION) {
								endStatusIsInterrupt = true;
							}
							if (endStatusIsInterrupt) {
								checkStopExec = true;
								break;
							}
						} catch (Exception e) {
							isHasException = true;
							errorMessage = "Msg_1339";
						}
					}
					if (endStatusIsInterrupt) {
						break;
					}
				}
			} catch (Exception e) {
				isHasException = true;
				errorMessage = "Msg_1552";
			}
		} else {
			// INPUT．「承認結果の反映対象期間（List）．社員」をループする
			// 社員の件数分ループ
			for (ApprovalPeriodByEmp approvalPeriodByEmp : lstApprovalPeriod) {
				if (checkStopExec) {
					break;
				}
				try {
					for (DatePeriod p : approvalPeriodByEmp.getListPeriod()) {
						// 社員の申請を反映

						ProcessStateReflectImport processStateReflectImport = appReflectManagerAdapter
								.reflectAppOfEmployeeTotal(execId, approvalPeriodByEmp.getEmployeeID(), p);
						if (processStateReflectImport == ProcessStateReflectImport.INTERRUPTION) {
							endStatusIsInterrupt = true;
						}
						if (endStatusIsInterrupt) {
							checkStopExec = true;
							break;
						}

					}
				} catch (Exception e) {
					isHasException = true;
					errorMessage = "Msg_1552";
				}
			}

		}

		log.info("更新処理自動実行_承認結果の反映_END_" + processExecution.getExecItemCode() + "_" + GeneralDateTime.now());

		updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.RFL_APR_RESULT, companyId, execItemCd,
				processExecution, ProcessExecutionLog, isHasException, checkStopExec, errorMessage);

		// 終了状態 ＝ 中断
		return endStatusIsInterrupt;
	}

	// 月別集計
	private boolean monthlyAggregation(String execId, UpdateProcessAutoExecution processExecution,
			ProcessExecutionLog ProcessExecutionLog, String companyId,
			CommandHandlerContext<ExecuteProcessExecutionCommand> context) {
		// ドメインモデル「更新処理自動実行ログ」を更新する
		List<ExecutionTaskLog> taskLogLists = ProcessExecutionLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		int size = taskLogLists.size();
		boolean existExecutionTaskLog = false;
//		boolean checkError1552 = false;
		String errorMessage = "";
		boolean checkStopExec = false;
		for (int i = 0; i < size; i++) {
			ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
			// 月別集計
			if (executionTaskLog.getProcExecTask().equals(ProcessExecutionTask.MONTHLY_AGGR)) {
				executionTaskLog.setStatus(null);
				executionTaskLog.setLastExecDateTime(GeneralDateTime.now());
				existExecutionTaskLog = true;
				executionTaskLog.setErrorBusiness(null);
				executionTaskLog.setErrorSystem(null);
				executionTaskLog.setLastEndExecDateTime(null);
				break;

			}
		}
		if (!existExecutionTaskLog) {
			ExecutionTaskLog execTaskLog = ExecutionTaskLog.builder()
					.procExecTask(
							EnumAdaptor.valueOf(ProcessExecutionTask.MONTHLY_AGGR.value, ProcessExecutionTask.class))
					.lastExecDateTime(Optional.ofNullable(GeneralDateTime.now())).build();
			taskLogLists.add(execTaskLog);
		}
		String execItemCd = context.getCommand().getExecItemCd();
		List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
				execId);
		if (CollectionUtil.isEmpty(taskLogList)) {
			this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, ProcessExecutionLog.getTaskLogList());
		} else {
			this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, ProcessExecutionLog.getTaskLogList());
		}

		this.procExecLogRepo.update(ProcessExecutionLog);
		// 月別集計の判定
		boolean reflectResultCls = processExecution.getExecSetting().getMonthlyAggregate().getMonthlyAggCls()
				.equals(NotUseAtr.USE);
		if (!reflectResultCls) {
			// ドメインモデル「更新処理自動実行ログ」を更新する
			for (int i = 0; i < size; i++) {
				ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
				// 月別集計
				if (executionTaskLog.getProcExecTask().equals(ProcessExecutionTask.MONTHLY_AGGR)) {
					// 未実施
					executionTaskLog.setStatus(EndStatus.NOT_IMPLEMENT);
					executionTaskLog.setLastExecDateTime(null);
				}
			}
			this.procExecLogRepo.update(ProcessExecutionLog);
			return false; // chua confirm
		}
		log.info("更新処理自動実行_月別実績の集計_START_" + processExecution.getExecItemCode() + "_" + GeneralDateTime.now());
		// ドメインモデル「就業締め日」を取得する
		List<Closure> lstClosure = this.closureRepo.findAllUse(companyId);

		// ドメインモデル「実行ログ」を新規登録する
		DatePeriod period = this.findClosureMinMaxPeriod(companyId, lstClosure);
		GeneralDateTime now = GeneralDateTime.now();
		ExecutionLog executionLog = new ExecutionLog(execId, ExecutionContent.MONTHLY_AGGREGATION,
				ErrorPresent.NO_ERROR, new ExecutionTime(now, now), ExecutionStatus.INCOMPLETE,
				new ObjectPeriod(period.start(), period.end()), Optional.empty());
		executionLog.setMonlyAggregationSetInfo(new CalExeSettingInfor(ExecutionContent.MONTHLY_AGGREGATION,
				ExecutionType.NORMAL_EXECUTION, IdentifierUtil.randomUniqueId()));
		this.executionLogRepository.addExecutionLog(executionLog);

		boolean isHasException = false;
		List<Boolean> listCheck = new ArrayList<>();
		try {
			int sizeClosure = lstClosure.size();
			for (int i = 0; i < sizeClosure; i++) {
				Closure closure = lstClosure.get(i);
				// 雇用コードを取得する ~ 締めに紐付く雇用コード一覧を取得
				List<ClosureEmployment> employmentList = this.closureEmpRepo.findByClosureId(companyId,
						closure.getClosureId().value);
				List<String> lstEmploymentCode = new ArrayList<String>();
				employmentList.forEach(x -> {
					lstEmploymentCode.add(x.getEmploymentCD());
				});

				// 指定した年月の期間を算出する
				DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(closure.getClosureId().value,
						closure.getClosureMonth().getProcessingYm(), Optional.of(closure));
				// 取得した「締め期間」から「期間」を計算する
				DatePeriod newDatePeriod = new DatePeriod(datePeriodClosure.start(), GeneralDate.ymd(9999, 12, 31));

				// <<Public>> 就業条件で社員を検索して並び替える
				List<String> workplaceIdList = processExecution.getExecScope().getWorkplaceIdList();

				// 更新処理自動実行の実行対象社員リストを取得する
				List<String> listEmp = listEmpAutoExec.getListEmpAutoExec(companyId, newDatePeriod,
						processExecution.getExecScope().getExecScopeCls(), Optional.of(workplaceIdList),
						Optional.of(lstEmploymentCode));
				List<String> leaderEmpIdList = new ArrayList<>();
				if (processExecution.getExecutionType() == ProcessExecType.RE_CREATE) {
					// 異動者・勤務種別変更者リスト作成処理
					ListLeaderOrNotEmp listLeaderOrNotEmp = changePersionList.createProcessForChangePerOrWorktype(
							companyId, new ArrayList<>(listEmp), newDatePeriod.start(), processExecution);
					leaderEmpIdList = listLeaderOrNotEmp.getLeaderEmpIdList();
				}
				List<String> lstRegulationInfoEmployeeNew = new ArrayList<>();
				if (leaderEmpIdList.isEmpty()) {
					lstRegulationInfoEmployeeNew = listEmp;
				} else {
					for (String emp : listEmp) {
						for (String empId : leaderEmpIdList) {
							if (emp.equals(empId)) {
								lstRegulationInfoEmployeeNew.add(empId);
								break;
							}
						}
					}
				}
				try {
					val require = requireService.createRequire();
					val cacheCarrier = new CacheCarrier();

					this.managedParallelWithContext.forEach(
							ControlOption.custom().millisRandomDelay(MAX_DELAY_PARALLEL), lstRegulationInfoEmployeeNew,
							item -> {
								Optional<GeneralDate> date = getProcessingDate.getProcessingDate(item,
										GeneralDate.legacyDate(now.date()));
								if (!date.isPresent()) {
									return;
								}
								AsyncCommandHandlerContext<ExecuteProcessExecutionCommand> asyContext = (AsyncCommandHandlerContext<ExecuteProcessExecutionCommand>) context;
								AggregationResult result = MonthlyAggregationEmployeeService.aggregate(require,
										cacheCarrier, Optional.of(asyContext), companyId, item, date.get(), execId,
										ExecutionType.NORMAL_EXECUTION, Optional.empty());
								// 中断
								transaction.allInOneTransaction(result.getAtomTasks());

								if (result.getStatus().getState().value == 0) {
									// endStatusIsInterrupt = true;
									listCheck.add(true);
									// break;
									return;
								}
							});
				} catch (Exception e) {
					isHasException = true;
					errorMessage = "Msg_1339";
				}
				if (!listCheck.isEmpty()) {
					if (listCheck.get(0)) {
						checkStopExec = true;
						updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.MONTHLY_AGGR, companyId,
								execItemCd, processExecution, ProcessExecutionLog, isHasException, checkStopExec,
								errorMessage);
						return true;
					}
				}
			}
		} catch (Exception e) {
			isHasException = true;
			errorMessage = "Msg_1552";
		}
		log.info("更新処理自動実行_月別実績の集計_END_" + processExecution.getExecItemCode() + "_" + GeneralDateTime.now());
		if (!listCheck.isEmpty()) {
			if (listCheck.get(0)) {
				return true; // 終了状態 ＝ 中断
			}
		}

		updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.MONTHLY_AGGR, companyId, execItemCd,
				processExecution, ProcessExecutionLog, isHasException, checkStopExec, errorMessage);

		return false; // 終了状態 !＝ 中断
	}

	// アラーム抽出
	private boolean alarmExtraction(String execId, UpdateProcessAutoExecution processExecution,
			ProcessExecutionLog ProcessExecutionLog, String companyId,
			CommandHandlerContext<ExecuteProcessExecutionCommand> context) {
		context.asAsync().getDataSetter().updateData("taskId", context.asAsync().getTaskId());
		// ドメインモデル「更新処理自動実行ログ」を更新する
		List<ExecutionTaskLog> taskLogLists = ProcessExecutionLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		int size = taskLogLists.size();
		boolean existExecutionTaskLog = false;
		String execItemCd = context.getCommand().getExecItemCd();
		boolean checkException = false;
		boolean checkStopExec = false;
		String errorMessage = "";
		OutputExecAlarmListPro outputExecAlarmListPro = new OutputExecAlarmListPro();
		try {
			for (int i = 0; i < size; i++) {
				ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
				if (executionTaskLog.getProcExecTask() == ProcessExecutionTask.AL_EXTRACTION) {
					executionTaskLog.setStatus(null);
					executionTaskLog.setLastExecDateTime(GeneralDateTime.now());
					existExecutionTaskLog = true;
					break;
				}
			}
			if (!existExecutionTaskLog) {
				ExecutionTaskLog execTaskLog = ExecutionTaskLog.builder()
						.procExecTask(EnumAdaptor.valueOf(ProcessExecutionTask.AL_EXTRACTION.value,
								ProcessExecutionTask.class))
						.lastExecDateTime(Optional.ofNullable(GeneralDateTime.now())).build();
				taskLogLists.add(execTaskLog);
			}
			List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
					execId);
			if (CollectionUtil.isEmpty(taskLogList)) {
				this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, ProcessExecutionLog.getTaskLogList());
			} else {
				this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, ProcessExecutionLog.getTaskLogList());
			}
			this.procExecLogRepo.update(ProcessExecutionLog);
			// アラーム抽出区分の判定
			boolean alarmAtr = processExecution.getExecSetting().getAlarmExtraction().getAlarmExtractionCls()
					.equals(NotUseAtr.USE);
			if (!alarmAtr) {
				// ドメインモデル「更新処理自動実行ログ」を更新する
				for (int i = 0; i < size; i++) {
					ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
					if (executionTaskLog.getProcExecTask() == ProcessExecutionTask.AL_EXTRACTION) {
						executionTaskLog.setStatus(EndStatus.NOT_IMPLEMENT);
						executionTaskLog.setLastExecDateTime(null);
					}
				}
				this.procExecLogRepo.update(ProcessExecutionLog);
				return false;
			}
			log.info("更新処理自動実行_アラーム抽出_START_" + processExecution.getExecItemCode() + "_" + GeneralDateTime.now());
			// アルゴリズム「抽出処理状況を作成する」を実行する
			String extraProcessStatusID = createExtraProcessService.createExtraProcess(companyId);
			// 実行 :
			// List<職場コード>
			List<String> workplaceIdList = new ArrayList<>();
			if (processExecution.getExecScope().getExecScopeCls() == ExecutionScopeClassification.COMPANY) {
				workplaceIdList = workplaceAdapter.findListWorkplaceIdByBaseDate(GeneralDate.today());
			} else {
				workplaceIdList = processExecution.getExecScope().getWorkplaceIdList();
			}
			// List<パターンコード>
			List<String> listPatternCode = new ArrayList<>();
			listPatternCode.add(processExecution.getExecSetting().getAlarmExtraction().getAlarmCode().get().v());
			boolean sendMailPerson = false;
			if (processExecution.getExecSetting().getAlarmExtraction().getMailPrincipal().isPresent()) {
				if (processExecution.getExecSetting().getAlarmExtraction().getMailPrincipal().get().booleanValue())
					sendMailPerson = true;
			}
			boolean sendMailAdmin = false;
			if (processExecution.getExecSetting().getAlarmExtraction().getMailAdministrator().isPresent()) {
				if (processExecution.getExecSetting().getAlarmExtraction().getMailAdministrator().get().booleanValue())
					sendMailAdmin = true;
			}
			boolean isDisplayAdmin = false;
			if (processExecution.getExecSetting().getAlarmExtraction().getDisplayOnTopPageAdministrator().isPresent()) {
				if (processExecution.getExecSetting().getAlarmExtraction().getDisplayOnTopPageAdministrator().get().booleanValue())
					isDisplayAdmin = true;
			}
			boolean isDisplayPerson = false;
			if (processExecution.getExecSetting().getAlarmExtraction().getDisplayOnTopPagePrincipal().isPresent()) {
				if (processExecution.getExecSetting().getAlarmExtraction().getDisplayOnTopPagePrincipal().get().booleanValue())
					isDisplayPerson = true;
			}
			try {
				// アラームリスト自動実行処理を実行する
				outputExecAlarmListPro = this.execAlarmListProcessingService.execAlarmListProcessing(
						extraProcessStatusID, companyId, workplaceIdList, listPatternCode, GeneralDateTime.now(),
						sendMailPerson, sendMailAdmin,
						!processExecution.getExecSetting().getAlarmExtraction().getAlarmCode().isPresent() ? ""
								: processExecution.getExecSetting().getAlarmExtraction().getAlarmCode().get().v(),
						execId, execItemCd, isDisplayAdmin, isDisplayPerson);
				log.info("更新処理自動実行_アラーム抽出_END_" + processExecution.getExecItemCode() + "_" + GeneralDateTime.now());
				if (outputExecAlarmListPro.isCheckStop()) {
					checkStopExec = true;
					updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.AL_EXTRACTION, companyId,
							execItemCd, processExecution, ProcessExecutionLog, checkException, checkStopExec,
							errorMessage);
					return true;
				}
			} catch (Exception e) {
				setExtractStatusAbnormalTermination(extraProcessStatusID);
				// 各処理の後のログ更新処理
				checkException = true;
				errorMessage = "Msg_1339";
			}
		} catch (Exception e) {
			checkException = true;
			errorMessage = "Msg_1552";
		}
		if (!checkException) {
			errorMessage = outputExecAlarmListPro.getErrorMessage() == null ? ""
					: outputExecAlarmListPro.getErrorMessage();
		}
		updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.AL_EXTRACTION, companyId, execItemCd,
				processExecution, ProcessExecutionLog, checkException, checkStopExec, errorMessage);
		return false;
	}

	private void setExtractStatusAbnormalTermination(String extraProcessStatusID) {
		val alarmExtraProcessStatusOpt = alarmExtraProcessStatusRepo.getAlListExtaProcessByID(extraProcessStatusID);
		if (alarmExtraProcessStatusOpt.isPresent()){
			val extractProcessStatus = alarmExtraProcessStatusOpt.get();
			extractProcessStatus.setStatus(ExtractionState.ABNORMAL_TERMI);
			alarmExtraProcessStatusRepo.updateAlListExtaProcess(extractProcessStatus);
		}
	}

	private DatePeriod findClosurePeriodMinDate(String companyId, List<Closure> closureList) {
		YearMonth startYearMonth = null;
		YearMonth endYearMonth = null;
		for (Closure closure : closureList) {
			Optional<ClosureHistory> firstHist = this.closureRepo.findByHistoryBegin(companyId,
					closure.getClosureId().value);
			if (firstHist.isPresent()) {
				if (startYearMonth == null || firstHist.get().getStartYearMonth().lessThan(startYearMonth)) {
					startYearMonth = firstHist.get().getStartYearMonth();
				}
			}
			Optional<ClosureHistory> lastHist = this.closureRepo.findByHistoryLast(companyId,
					closure.getClosureId().value);
			if (lastHist.isPresent()) {
				if (endYearMonth == null || lastHist.get().getEndYearMonth().greaterThan(endYearMonth)) {
					endYearMonth = lastHist.get().getEndYearMonth();
				}
			}
		}
		GeneralDate startClosingDate = GeneralDate.ymd(startYearMonth.year(), startYearMonth.month(), 1);
		GeneralDate endClosingDate = GeneralDate.ymd(9999, 12, 31);
		return new DatePeriod(startClosingDate, endClosingDate);
	}

	// 実行前登録処理
	private GeneralDateTime preExecutionRegistrationProcessing(String companyId, String execItemCd, String execId,
			ProcessExecutionLogManage processExecutionLogManage, int execType) {
		Optional<ProcessExecutionLog> procExecLogOpt = this.procExecLogRepo.getLog(companyId, execItemCd);
		ProcessExecutionLog procExecLog = null;
		GeneralDateTime dateTime = GeneralDateTime.fromString(GeneralDateTime.now().toString(), "yyyy/MM/dd HH:mm:ss");
		if (procExecLogOpt.isPresent()) {
			procExecLog = procExecLogOpt.get();
			// アルゴリズム「更新処理自動実行ログ新規登録処理」を実行する
			// 現在の実行状態 ＝ 実行
			processExecutionLogManage.setCurrentStatus(CurrentExecutionStatus.RUNNING);
			// 全体の終了状態 ＝ NULL
			processExecutionLogManage.setOverallStatus(null);
			processExecutionLogManage.setLastEndExecDateTime(null);
			processExecutionLogManage.setErrorBusiness(null);
			processExecutionLogManage.setErrorSystem(null);
			processExecutionLogManage.setOverallError(null);
			if (execType == 1) {
				processExecutionLogManage.setLastExecDateTime(dateTime);
			} else {
				processExecutionLogManage.setLastExecDateTime(dateTime);
				processExecutionLogManage.setLastExecDateTimeEx(dateTime);
			}
			this.processExecLogManaRepo.update(processExecutionLogManage);
			// ドメインモデル「更新処理自動実行ログ」を削除する
			this.procExecLogRepo.remove(companyId, execItemCd, procExecLogOpt.get().getExecId());
			procExecLog.initTaskLogList();

			procExecLog.setExecId(execId);

			this.procExecLogRepo.insert(procExecLog);

		} else {
			// アルゴリズム「更新処理自動実行ログ新規登録処理」を実行する
			// 現在の実行状態 ＝ 実行
			processExecutionLogManage.setCurrentStatus(CurrentExecutionStatus.RUNNING);
			// 全体の終了状態 ＝ NULL
			processExecutionLogManage.setOverallStatus(null);
			processExecutionLogManage.setLastEndExecDateTime(null);
			processExecutionLogManage.setErrorBusiness(null);
			processExecutionLogManage.setErrorSystem(null);
			processExecutionLogManage.setOverallError(null);
			if (execType == 1) {
				processExecutionLogManage.setLastExecDateTime(dateTime);
			} else {
				processExecutionLogManage.setLastExecDateTime(dateTime);
				processExecutionLogManage.setLastExecDateTimeEx(dateTime);
			}
			this.processExecLogManaRepo.update(processExecutionLogManage);
			List<ExecutionTaskLog> taskLogList = ProcessExecutionLog.processInitTaskLog(execId);
			procExecLog = new ProcessExecutionLog(new ExecutionCode(execItemCd), companyId, null, taskLogList, execId);
			this.procExecLogRepo.insert(procExecLog);
		}
		return dateTime;

	}

	// true is interrupt
	// 日別実績の作成 ~ 日別実績の計算
	private boolean dailyPerformanceCreation(String companyId,
			CommandHandlerContext<ExecuteProcessExecutionCommand> context, UpdateProcessAutoExecution processExecution,
			EmpCalAndSumExeLog empCalAndSumExeLog, List<String> lstEmpId, DatePeriod period, List<String> workPlaceIds,
			String typeExecution, ExecutionLog dailyCreateLog) throws CreateDailyException, DailyCalculateException {
		boolean isInterrupt = false;

		List<Boolean> listIsInterrupt = Collections.synchronizedList(new ArrayList<>());
		try {
			this.managedParallelWithContext.forEach(ControlOption.custom().millisRandomDelay(MAX_DELAY_PARALLEL),
					lstEmpId, empId -> {
						if (!listIsInterrupt.isEmpty()) {
		                    return;
		                }
						// アルゴリズム「開始日を入社日にする」を実行する
						DatePeriod employeeDatePeriod = this.makeStartDateForHiringDate(processExecution, empId,
								period);
						if (employeeDatePeriod == null && processExecution.getExecSetting().getDailyPerf()
								.getCreateNewEmpDailyPerf().equals(NotUseAtr.USE)) {

						} else {
							if (employeeDatePeriod != null) {
								boolean executionDaily = this.executionDaily(companyId, context, processExecution,
										empId, empCalAndSumExeLog, employeeDatePeriod, typeExecution, dailyCreateLog);
								if (executionDaily) {
									listIsInterrupt.add(true);
									return;
								}
							}
						}
					});
		} catch (Exception e) {
			val analyzer = new ThrowableAnalyzer(e);
			if (analyzer.findByClass(CreateDailyException.class).isPresent()) {
				throw new CreateDailyException(e);
			} else if (analyzer.findByClass(DailyCalculateException.class).isPresent()) {
				throw new DailyCalculateException(e);
			}
            throw e; 
		}

		if (!listIsInterrupt.isEmpty()) {
			isInterrupt = true;
		}
		
		return isInterrupt;
	}

	// 開始日を入社日にする
	private DatePeriod makeStartDateForHiringDate(UpdateProcessAutoExecution processExecution, String employeeId,
			DatePeriod period) {
		List<String> lstEmployeeId = new ArrayList<String>();
		lstEmployeeId.add(employeeId);
		// ドメインモデル「更新処理自動実行.実行設定.日別実績の作成・計算.途中入社は入社日からにする」の判定
		if (processExecution.getExecSetting().getDailyPerf().getCreateNewEmpDailyPerf().equals(NotUseAtr.USE)) {
			// request list 211
			List<nts.uk.ctx.at.record.dom.adapter.company.AffCompanyHistImport> affCompanyHistByEmployee = this.syCompanyRecordAdapter
					.getAffCompanyHistByEmployee(lstEmployeeId, period);
			if (affCompanyHistByEmployee != null && !affCompanyHistByEmployee.isEmpty()) {
				List<AffComHistItemImport> lstAffComHistItem = affCompanyHistByEmployee.get(0).getLstAffComHistItem();
				if (lstAffComHistItem.isEmpty())
					return null;
				List<AffComHistItemImport> lstAffComHistItemSort = lstAffComHistItem.stream()
						.sorted((x, y) -> x.getDatePeriod().start().compareTo(y.getDatePeriod().start()))
						.collect(Collectors.toList());
				// int size = lstAffComHistItem.size();
				GeneralDate startDate = GeneralDate.ymd(9999, 12, 31);
				if (lstAffComHistItemSort.get(0).getDatePeriod().start().before(period.start())) {
					return period;
				}
				if (lstAffComHistItemSort.get(0).getDatePeriod().start().after(period.end())) {
					return null;
				}
				if (lstAffComHistItemSort.get(0).getDatePeriod().start().afterOrEquals(period.start())
						&& lstAffComHistItemSort.get(0).getDatePeriod().start().beforeOrEquals(period.end())) {
					startDate = lstAffComHistItemSort.get(0).getDatePeriod().start();
				}

				return new DatePeriod(startDate, period.end());
			}
			return null;
		}
		return period;
	}

	// true is interrupt
	private boolean executionDaily(String companyId, CommandHandlerContext<ExecuteProcessExecutionCommand> context,
			UpdateProcessAutoExecution processExecution, String employeeId, EmpCalAndSumExeLog empCalAndSumExeLog,
			DatePeriod period, String typeExecution, ExecutionLog dailyCreateLog) {
		AsyncCommandHandlerContext<ExecuteProcessExecutionCommand> asyContext = (AsyncCommandHandlerContext<ExecuteProcessExecutionCommand>) context;
		ProcessState processState;
		// 受け取った期間が「作成した期間（日別作成）」の場合
		if ("日別作成".equals(typeExecution)) {
			try {
				// ⑤社員の日別実績を作成する
				ExecutionTypeDaily executionTypeDaily = ExecutionTypeDaily.CREATE;
				if (dailyCreateLog.getDailyCreationSetInfo().isPresent()
						&& dailyCreateLog.getDailyCreationSetInfo().get().getExecutionType() == ExecutionType.RERUN) {
					executionTypeDaily = ExecutionTypeDaily.DELETE_ACHIEVEMENTS;
				}
				OutputCreateDailyResult status = createDailyResultDomainServiceNew.createDataNewWithNoImport(asyContext,
						employeeId, period, ExecutionAttr.AUTO, companyId, executionTypeDaily,
						Optional.of(empCalAndSumExeLog), Optional.empty());
				processState = (status.getProcessState().value == 0 ? ProcessState.INTERRUPTION : ProcessState.SUCCESS);
			} catch (Exception e) {
				if (DeadLock.isSQLDeadLock(e)) {
					throw e;
				}
				throw new CreateDailyException(e);
			}
		} else {
			try {
				processState = this.dailyCalculationEmployeeService.calculateForOnePerson(companyId, employeeId, period,
						Optional.empty(), empCalAndSumExeLog.getEmpCalAndSumExecLogID(),
						dailyCreateLog.getIsCalWhenLock().orElse(false));
			} catch (Exception e) {
				if (DeadLock.isSQLDeadLock(e)) {
					throw e;
				}
				throw new DailyCalculateException(e);
			}

		}
		// fixed
		return processState.value == 0;
	}

	// 期間を計算
	private GeneralDate calculatePeriod(int closureId, DatePeriod period, String companyId) {
		Optional<Closure> closureOpt = this.closureRepo.findById(companyId, closureId);
		if (closureOpt.isPresent()) {
			Closure closure = closureOpt.get();
			YearMonth processingYm = closure.getClosureMonth().getProcessingYm();
			DatePeriod closurePeriod = ClosureService.getClosurePeriod(closureId, processingYm, closureOpt);
			return closurePeriod.start();
		}
		return period.start();
	}

	/**
	 * 日別実績処理の再実行
	 *
	 * @param context
	 * @param companyId
	 * @param empId
	 * @param period
	 * @param empCalAndSumExeLogId
	 * @param dailyCreateLog
	 * @param procExec
	 * @return
	 * @throws CreateDailyException
	 * @throws DailyCalculateException
	 */
	private boolean redoDailyPerformanceProcessing(CommandHandlerContext<ExecuteProcessExecutionCommand> context,
			String companyId, String empId, DatePeriod period, String empCalAndSumExeLogId, ExecutionLog dailyCreateLog,
			UpdateProcessAutoExecution procExec) throws CreateDailyException, DailyCalculateException {
		AsyncCommandHandlerContext<ExecuteProcessExecutionCommand> asyncContext = (AsyncCommandHandlerContext<ExecuteProcessExecutionCommand>) context;
		ProcessState processState1;
		try {
			// ⑤社員の日別実績を作成する
			Optional<EmpCalAndSumExeLog> oEmpCalAndSumExeLog = this.empCalSumRepo
					.getByEmpCalAndSumExecLogID(empCalAndSumExeLogId);
			OutputCreateDailyResult status = this.createDailyResultDomainServiceNew.createDataNewWithNoImport(
					asyncContext, empId, period, ExecutionAttr.AUTO, companyId, ExecutionTypeDaily.CREATE,
					oEmpCalAndSumExeLog, Optional.empty());
			processState1 = (status.getProcessState().value == 0 ? ProcessState.INTERRUPTION : ProcessState.SUCCESS);
		} catch (Exception e) {
			if (DeadLock.isSQLDeadLock(e)) {
				throw e;
			}
			throw new CreateDailyException(e);
		}
		log.info("更新処理自動実行_日別実績の作成_END_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
		log.info("更新処理自動実行_日別実績の計算_START_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
		ProcessState ProcessState2;

		try {
			// 社員の日別実績を計算
			ProcessState2 = this.dailyCalculationEmployeeService.calculateForOnePerson(companyId, empId, period, Optional.empty(),
					empCalAndSumExeLogId, dailyCreateLog.getIsCalWhenLock().orElse(false));
			log.info("更新処理自動実行_日別実績の計算_END_" + procExec.getExecItemCode() + "_" + GeneralDateTime.now());
		} catch (Exception e) {
			// 暫定データの登録
			if (DeadLock.isSQLDeadLock(e)) {
				throw e;
			}
			throw new DailyCalculateException(e);
		}

		// 中断
		return processState1.value == 0 || ProcessState2.value == 0;
	}

	// 全締めから一番早い期間.開始日を取得する
	private DatePeriod getMinPeriodFromStartDate(String companyId) {
		// ドメインモデル「就業締め日」を取得する
		List<Closure> closureList = this.closureRepo.findAllActive(companyId, UseClassification.UseClass_Use);
		// 全締めから一番早い期間.開始日を取得する
		return this.findClosureMinMaxPeriod(companyId, closureList);
	}

	private ApprovalPeriodByEmp mergeList(List<ApprovalPeriodByEmp> lstApprovalPeriod) {
		List<DatePeriod> lstDatePeriod = lstApprovalPeriod.stream().flatMap(x -> x.getListPeriod().stream())
				.collect(Collectors.toList());
		return new ApprovalPeriodByEmp(lstApprovalPeriod.get(0).getEmployeeID(), mergePeriod(lstDatePeriod));
	}

	private ScheduleCreateContent createContent(String execId, String companyId, String execItemCd) {
		ScheduleCreateContent s = new ScheduleCreateContent();
		// 1-実行ID ＝ 取得した実行ID
		// execId
		s.setExecutionId(execId);
		// 確定済みにする←false
		s.setConfirm(false);
		// 作成種類←"新規作成"

		s.setImplementAtr(ImplementAtr.CREATE_NEW_ONLY);
		// 作成方法の指定 {
		// ・作成方法←"個人情報"
		// ・コピー開始日←optional.empty
		// ・マスタ参照先←optional.empty
		// ・月間パターンコード←optional.empty
		// }
		s.setSpecifyCreation(new SpecifyCreation(CreationMethod.PERSONAL_INFO, Optional.empty(), Optional.empty(),
				Optional.empty()));
		// ドメインモデル「更新処理自動実行」を取得する
		UpdateProcessAutoExecution procExec;
		Optional<UpdateProcessAutoExecution> procExecOpt = this.procExecRepo
				.getProcessExecutionByCidAndExecCd(companyId, execItemCd);
		if (procExecOpt.isPresent()) {
			procExec = procExecOpt.get();
            // 対象者を限定する←{
            // 　ドメインモデル「更新処理自動実行」.実行種別="通常実行" →false
            // 　　　　ドメインモデル「更新処理自動実行」.実行種別="再実行" →true
        	// 　　}
            boolean reTargetAtr = procExec.getExecutionType().equals(ProcessExecType.RE_CREATE);
            // 確定済みも対象とする←false
            boolean reOverwriteConfirmed = false;
            // 手修正・申請反映も対象とする←false
            boolean reOverwriteRevised = false;
            // 異動者←ドメインモデル「更新処理自動実行」.再実行条件.異動者を再作成する
            boolean transfer = procExec.getReExecCondition().getRecreateTransfer().equals(NotUseAtr.USE);
            // 休職休業者←ドメインモデル「更新処理自動実行」.実行種別.休職者・休業者を再作成
            boolean leaveOfAbsence = procExec.getReExecCondition().getRecreateLeave().equals(NotUseAtr.USE);
            // 短時間勤務者←false
            boolean shortWorkingHours = false;
            // 労働条件変更者←false
            boolean changedWorkingConditions = false;
            ConditionEmployee conditionEmployee = new ConditionEmployee(transfer, leaveOfAbsence, 
            		shortWorkingHours, changedWorkingConditions);
            RecreateCondition recreateCondition = new RecreateCondition(reTargetAtr, reOverwriteConfirmed, 
            		reOverwriteRevised, Optional.of(conditionEmployee));
            s.setRecreateCondition(Optional.of(recreateCondition));
		}
		return s;
	}

	/**
	 * Index reconstruction. インデックス再構成
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.アルゴリズム.更新処理自動実行.実行処理.各処理の分岐.インデックス再構成.インデックス再構成
	 *
	 * @param execId      the exec id
	 * @param companyId   the company id
	 * @param procExec    the proc exec 更新処理自動実行
	 * @param procExecLog the proc exec log 更新処理自動実行ログ
	 */
	private void indexReconstruction(String execId, String companyId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog) {
		String errorMessage = "";
		boolean isHasException = false;
		// Step 1: ドメインモデル「更新処理自動実行ログ」を更新する - Update the domain model 更新処理自動実行ログ
		List<ExecutionTaskLog> taskLogLists = procExecLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		for (int i = 0; i < taskLogLists.size(); i++) {
			// Check 各処理の終了状態.更新処理 ＝ インデックス再構成
			if (taskLogLists.get(i).getProcExecTask() == ProcessExecutionTask.INDEX_RECUNSTRUCTION) {
				// Set 各処理の終了状態 ＝ [インデックス再構成、NULL]
				taskLogLists.get(i).setStatus(null);
				// Set 開始日時 ＝ [インデックス再構成、システム日時]
				taskLogLists.get(i).setLastExecDateTime(GeneralDateTime.now());
			}
		}
		procExecLog.setTaskLogList(taskLogLists);
		this.procExecLogRepo.update(procExecLog);
		// Step 2: INPUT「更新処理自動実行．実行設定．インデックス再構成．使用区分」を判定する - INPUT "Automatic execution
		// of update process. Execution setting. Index reconstruction. Usage
		// classification" is judged.
		if (procExec.getExecSetting().getIndexReconstruction().getIndexReorgAttr() == NotUseAtr.NOT_USE) {
			// Step 3: if False: ドメインモデル「更新処理自動実行ログ」を更新する -> return
			for (int i = 0; i < taskLogLists.size(); i++) {
				ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
				// Check 各処理の終了状態.更新処理 ＝ 任意期間の集計
				if (executionTaskLog.getProcExecTask() == ProcessExecutionTask.INDEX_RECUNSTRUCTION) {
					// Set 各処理の終了状態 ＝ [任意期間の集計、未実施]
					executionTaskLog.setStatus(EndStatus.NOT_IMPLEMENT);
					// Set 開始日時 ＝ [任意期間の集計、NULL]
					executionTaskLog.setLastExecDateTime(null);
				}
			}
			procExecLog.setTaskLogList(taskLogLists);
			this.procExecLogRepo.update(procExecLog);
			return;
		}
		// カテゴリNO in INPUT「更新処理自動実行．実行設定．インデックス再作成．カテゴリリスト」
		List<BigDecimal> categoryList = procExec.getExecSetting().getIndexReconstruction().getCategoryList().stream()
				.map(i -> new BigDecimal(i.v())).collect(Collectors.toList());
		try {
			// Step 3: if True: ドメインモデル「インデックス再構成テーブル」を取得する - Get the domain model "index
			// reconstruction table"
			List<IndexReorgTable> listIndexReorgTable = this.indexReorgTableRepository
					.findAllByCategoryIds(categoryList);
			// Step 4: 「インデックス再構成結果履歴」を作成する
			ProcExecIndex proExecIndex = new ProcExecIndex(execId, Collections.emptyList());
			// 「インデックス再構成テーブル」を取得できるか確認する - Check if you can get the "index reconstruction
			// table"
			if (!listIndexReorgTable.isEmpty()) {
				// 取得した「インデックス再構成テーブル」をループする - Loop the acquired "index reconstruction table"
				List<ProcExecIndexResult> indexReconstructionResult = new ArrayList<ProcExecIndexResult>();
				listIndexReorgTable.forEach(indexReorgTable -> {
					// Step 5: インデックス再構成前の断片化率を計算する - Calculate the fragmentation rate before index
					// reconstruction
					// Step 6: 「インデックス再構成結果」を作成する
					List<ProcExecIndexResult> resultCaculateFragRates = this.indexReorgTableRepository
							.calculateFragRate(indexReorgTable.getTablePhysName().v()).stream()
							.filter(item -> item.getIndexId() > 0) // Check condition 実行したSQLの結果の「index_id」> 0
							.map(item -> ProcExecIndexResult.builder().indexName(new IndexName(item.getIndexName()))
									.fragmentationRate(new FragmentationRate(item.getFragmentationRate()))
									.tablePhysicalName(new TableName(item.getTablePhysicalName())).build())
							.collect(Collectors.toList());
					// Step 7 テーブルのインデックス再構成する
					this.indexReorgTableRepository.reconfiguresIndex(indexReorgTable.getTablePhysName().v());
					// INPUT「更新処理自動実行．実行設定．インデックス再構成．統計情報を更新する」を判定する
					// if USE
					if (procExec.getExecSetting().getIndexReconstruction().getUpdateStatistics() == NotUseAtr.USE) {
						// Step 8 統計情報を更新する - Update stats
						this.indexReorgTableRepository.updateStatis(indexReorgTable.getTablePhysName().v());
					}
					// Step 9 インデックス再構成後の断片化率を計算する
					List<CalculateFragRate> resultCaculateFragRatesAfter = this.indexReorgTableRepository
							.calculateFragRate(indexReorgTable.getTablePhysName().v());
					resultCaculateFragRates.forEach(indexResult -> 
						resultCaculateFragRatesAfter.stream()
								.filter(item -> item.getTablePhysicalName().equals(indexResult.getTablePhysicalName().v())
										&& (indexResult.getIndexName().v().equals(item.getIndexName())))
								.findFirst()
								.ifPresent(fragRate -> indexResult.setFragmentationRateAfterProcessing(
										new FragmentationRate(fragRate.getFragmentationRate())))
					);
					indexReconstructionResult.addAll(resultCaculateFragRates);
				});
				// Step 10: 「インデックス再構成結果」を更新して「インデックス再構成結果履歴」に追加する
				proExecIndex.setIndexReconstructionResult(indexReconstructionResult);
			}
			// Step 11: 作成した「インデックス再構成結果履歴」を登録する
			this.proExecIndexRepository.update(proExecIndex);
		} catch (Exception e) {
			isHasException = true;
			errorMessage = "Msg_1339";
		}
		// Step 12: 各処理の後のログ更新処理
		this.updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.INDEX_RECUNSTRUCTION, companyId, execId,
				procExec, procExecLog, isHasException, false, errorMessage);
	}

	/**
	 * 任意期間の集計
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.アルゴリズム.更新処理自動実行.実行処理.各処理の分岐.任意期間の集計.任意期間の集計
	 *
	 * @param execId      実行ID
	 * @param companyId   会社ID
	 * @param procExec    更新処理自動実行
	 * @param procExecLog 更新処理自動実行ログ
	 * @param context
	 * @return
	 */
	private boolean aggregationOfArbitraryPeriod(String execId, String companyId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog, CommandHandlerContext<ExecuteProcessExecutionCommand> context) {
		// Step ドメインモデル「更新処理自動実行ログ」を更新する
		List<ExecutionTaskLog> taskLogLists = procExecLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		int size = taskLogLists.size();
		boolean existExecutionTaskLog = false;
		boolean checkStopExec = false;
		String errorMessage = "";
		for (int i = 0; i < size; i++) {
			// Check 各処理の終了状態.更新処理 ＝ 任意期間の集計
			if (taskLogLists.get(i).getProcExecTask() == ProcessExecutionTask.AGGREGATION_OF_ARBITRARY_PERIOD) {
				// Set 各処理の終了状態 ＝ [任意期間の集計、NULL]
				taskLogLists.get(i).setStatus(null);
				// Set 開始日時 ＝ [任意期間の集計、システム日時]
				taskLogLists.get(i).setLastExecDateTime(GeneralDateTime.now());
				existExecutionTaskLog = true;
				break;
			}
		}
		if (!existExecutionTaskLog) {
			ExecutionTaskLog execTaskLog = ExecutionTaskLog.builder().procExecTask(EnumAdaptor
					.valueOf(ProcessExecutionTask.AGGREGATION_OF_ARBITRARY_PERIOD.value, ProcessExecutionTask.class))
					.lastExecDateTime(Optional.of(GeneralDateTime.now())).build();
			taskLogLists.add(execTaskLog);
		}
		String execItemCd = context.getCommand().getExecItemCd();
		List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
				execId);
		if (CollectionUtil.isEmpty(taskLogList)) {
			this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		} else {
			this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		}
		this.procExecLogRepo.update(procExecLog);
		// Step INPUT「更新処理自動実行．実行設定．任意期間の集計．使用区分」を判定する
		// FALSE（しない）の場合
		if (procExec.getExecSetting().getAggrAnyPeriod().getAggAnyPeriodAttr() == NotUseAtr.NOT_USE) {
			// Step ドメインモデル「更新処理自動実行ログ」を更新する (update domain 「更新処理自動実行ログ」)
			for (int i = 0; i < taskLogLists.size(); i++) {
				ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
				// Check 各処理の終了状態.更新処理 ＝ 任意期間の集計
				if (executionTaskLog.getProcExecTask() == ProcessExecutionTask.AGGREGATION_OF_ARBITRARY_PERIOD) {
					// Set 各処理の終了状態 ＝ [任意期間の集計、未実施]
					executionTaskLog.setStatus(EndStatus.NOT_IMPLEMENT);
					// Set 開始日時 ＝ [任意期間の集計、NULL]
					executionTaskLog.setLastExecDateTime(null);
					existExecutionTaskLog = true;
				}
			}
			procExecLog.setTaskLogList(taskLogLists);
			this.procExecLogRepo.update(procExecLog);
			return false;
		}
		// TRUE（する）の場合
		boolean isHasException = false;
		try {
			String aggrFrameCode = procExec.getExecSetting().getAggrAnyPeriod().getAggrFrameCode().map(item -> item.v())
					.orElse(null);
			// Step ドメインモデル「任意集計期間」を取得する
			Optional<AnyAggrPeriodImport> anyAggrPeriod = this.anyAggrPeriodAdapter.findOne(companyId, aggrFrameCode);
			// 「任意集計期間」取得できたかチェック - check if could get AnyAggrPeriod
			if (!anyAggrPeriod.isPresent()) {
				// 取得できない - if can't get
				for (int i = 0; i < taskLogLists.size(); i++) {
					ExecutionTaskLog executionTaskLog = taskLogLists.get(i);
					// Check 各処理の終了状態.更新処理 ＝ 任意期間の集計
					if (executionTaskLog.getProcExecTask() == ProcessExecutionTask.AGGREGATION_OF_ARBITRARY_PERIOD) {
						// Set 各処理の終了状態 ＝ [任意期間の集計、未実施]
						executionTaskLog.setStatus(EndStatus.NOT_IMPLEMENT);
						// Set 開始日時 ＝ [任意期間の集計、NULL]
						executionTaskLog.setLastExecDateTime(null);
					}
				}
				procExecLog.setTaskLogList(taskLogLists);
				this.procExecLogRepo.update(procExecLog);
				return false;
			} else {
				// 更新処理自動実行の実行対象社員リストを取得する
				List<String> listEmp = listEmpAutoExec.getListEmpAutoExec(companyId, anyAggrPeriod.get().getPeriod(),
						procExec.getExecScope().getExecScopeCls(),
						Optional.of(procExec.getExecScope().getWorkplaceIdList()), Optional.empty());
				// Step ドメインモデル「任意期間集計実行ログ」を新規登録する - Registering a new domain model 任意期間集計実行ログ
				// (AggrPeriodExcution)
				int executionAtr = nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.periodexcution.
						ExecutionAtr.AUTOMATIC_EXECUTION.value;
				AggrPeriodExcutionImport aggrPeriodExcution = AggrPeriodExcutionImport.builder().companyId(companyId)
						.aggrId(execId).aggrFrameCode(aggrFrameCode).executionEmpId("System")
						.startDateTime(GeneralDateTime.now()).executionAtr(executionAtr)
						.executionStatus(Optional.empty()).presenceOfError(PresenceOfError.NO_ERROR.value)
						.endDateTime(GeneralDateTime.now()).build();
				this.aggrPeriodExcutionAdapter.addExcution(aggrPeriodExcution);

				// 取得した「社員ID＜List＞」の分だけ「任意期間集計対象者」を登録する
				List<AggrPeriodTargetImport> targetLists = new ArrayList<>();
				listEmp.forEach(empId -> {
					targetLists.add(AggrPeriodTargetImport.builder().aggrId(execId).employeeId(empId)
							.state(State.UNDONE.value).build());
				});
				if (targetLists.isEmpty()) {
					isHasException = true;
					errorMessage = "Msg_1339";
					checkStopExec = true;
				}
				this.aggrPeriodTargetAdapter.addTarget(targetLists);
				try {
					// Step 任意期間集計Mgrクラス
					this.byPeriodAggregationService.manager(companyId, execId, context.asAsync());
				} catch (Exception e) {
					isHasException = true;
					errorMessage = "Msg_1339";
					checkStopExec = true;
				}
			}
		} catch (Exception e) {
			isHasException = true;
			errorMessage = "Msg_1552";
			checkStopExec = true;
		}
		// 各処理の後のログ更新処理
		this.updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.AGGREGATION_OF_ARBITRARY_PERIOD,
				companyId, execId, procExec, procExecLog, isHasException, checkStopExec, errorMessage);
		return false;
	}

	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.外部入出力.外部出力.アルゴリズム.サーバ外部出力自動実行.サーバ外部出力実行時引数処理.サーバ外部出力実行時引数処理
	 * 
	 * @param execId      実行ID
	 * @param companyId   会社ID
	 * @param procExec    更新処理自動実行
	 * @param procExecLog 更新処理自動実行ログ
	 * @return
	 */
	private boolean externalOutput(String execId, String companyId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog) {
		List<ExecutionTaskLog> taskLogLists = procExecLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		boolean hasError = false;
		boolean checkStopExec = false;
		String errorMessage = "";
		// Step ドメインモデル「更新処理自動実行ログ」を更新する
		this.updateLog(execId, ProcessExecutionTask.EXTERNAL_OUTPUT, procExec, procExecLog);

		// 外部出力区分の判定する
		// しないの場合
		if (procExec.getExecSetting().getExternalOutput().getExtOutputCls().equals(NotUseAtr.NOT_USE)) {
			// ドメインモデル「更新処理自動実行ログ」を更新する
			this.updateTaskLogs(taskLogLists, ProcessExecutionTask.EXTERNAL_OUTPUT, EndStatus.NOT_IMPLEMENT, null);
			procExecLog.setTaskLogList(taskLogLists);
			this.procExecLogRepo.update(procExecLog);
			return false;
		}
		// するの場合
		// 実行条件ごとにループする
		for (ExternalOutputConditionCode conditionCd : procExec.getExecSetting().getExternalOutput()
				.getExtOutCondCodeList()) {
			// サーバ外部出力実行時引数処理
			Optional<ServerExternalOutputImport> optOutput = this.serverExternalOutputAdapter
					.findExternalOutput(companyId, conditionCd.v());
			if (optOutput.isPresent()) {
				ServerExternalOutputImport output = optOutput.get();
				// 実行結果を確認する
				if (output.isExecutionResult()) {
					// アルゴリズム「サーバ外部出力自動実行」を実行する
					Optional<String> optErrMessage = this.serverExternalOutputAdapter.processAutoExecution(
							procExec.getExecScope(), companyId, execId, output.getPeriod(), output.getBaseDate(),
							conditionCd.v());
					if (optErrMessage.isPresent()) {
						hasError = true;
						errorMessage = optErrMessage.get();
					}
				} else {
					hasError = true;
					errorMessage = output != null ? output.getErrorMessage() : null;
				}
			} else {
				hasError = true;
			}
		}
		// ドメインモデル「更新処理自動実行ログ」を取得しチェックする
		Optional<ProcessExecutionLog> optLog = this.procExecLogRepo
				.getLogByCIdAndExecCd(companyId, procExec.getExecItemCode().v(), execId);
		if (optLog.isPresent()) {
			ProcessExecutionLog log = optLog.get();
			EndStatus status = log.getTaskLogList().stream()
					.filter(task -> task.getProcExecTask().equals(ProcessExecutionTask.EXTERNAL_OUTPUT)
							&& task.getExecId().equals(execId)).findFirst()
					.get().getStatus().orElse(null);
			checkStopExec = EndStatus.FORCE_END.equals(status);
		}
		// 各処理の後のログ更新処理
		this.updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.EXTERNAL_OUTPUT, companyId, execId,
				procExec, procExecLog, hasError, checkStopExec, errorMessage);
		return checkStopExec;
	}

	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.アルゴリズム.更新処理自動実行.実行処理.各処理の分岐.データの保存.データの保存
	 * 
	 * @param execId      実行ID
	 * @param companyId   会社ID
	 * @param procExec    更新処理自動実行
	 * @param procExecLog 更新処理自動実行ログ
	 * @return
	 */
	private boolean dataStorage(String execId, String companyId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog) {
		List<ExecutionTaskLog> taskLogLists = procExecLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		boolean hasError = false;
		boolean checkStopExec = false;
		String errorMessage = "";
		// Step ドメインモデル「更新処理自動実行ログ」を更新する
		this.updateLog(execId, ProcessExecutionTask.SAVE_DATA, procExec, procExecLog);

		// データの保存区分の判定する
		// しないの場合
		if (procExec.getExecSetting().getSaveData().getSaveDataCls().equals(NotUseAtr.NOT_USE)) {
			this.updateTaskLogs(taskLogLists, ProcessExecutionTask.SAVE_DATA, EndStatus.NOT_IMPLEMENT, null);
			procExecLog.setTaskLogList(taskLogLists);
			this.procExecLogRepo.update(procExecLog);
			return false;
		}

		// アルゴリズム「自動削除準備」を実行する
		try {
			Optional<String> msg = this.autoExecutionPreparationAdapter.autoStoragePrepare(procExec);
			errorMessage = msg.orElse(null);
			hasError = msg.isPresent();
		} catch (Exception e) {
			hasError = true;
		}
		// ドメインモデル「更新処理自動実行ログ」を取得しチェックする（中断されている場合は更新されているため、最新の情報を取得する）
		Optional<ProcessExecutionLog> optLog = this.procExecLogRepo.getLogByCIdAndExecCd(companyId,
				procExec.getExecItemCode().v(), execId);
		if (optLog.isPresent()) {
			ProcessExecutionLog log = optLog.get();
			EndStatus status = log.getTaskLogList().stream()
					.filter(task -> task.getProcExecTask().equals(ProcessExecutionTask.SAVE_DATA)
							&& task.getExecId().equals(execId)).findFirst().get()
					.getStatus().orElse(null);
			checkStopExec = EndStatus.FORCE_END.equals(status);
		}
		// 各処理の後のログ更新処理
		this.updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.SAVE_DATA, companyId, execId, procExec,
				procExecLog, hasError, checkStopExec, errorMessage);
		return checkStopExec;
	}

	/**
	 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.アルゴリズム.更新処理自動実行.実行処理.各処理の分岐.データの削除.データの削除
	 * 
	 * @param execId      実行ID
	 * @param companyId   会社ID
	 * @param procExec    更新処理自動実行
	 * @param procExecLog 更新処理自動実行ログ
	 * @return
	 */
	private boolean dataDeletion(String execId, String companyId, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog) {
		List<ExecutionTaskLog> taskLogLists = procExecLog.getTaskLogList().stream()
        		.filter(item -> item.getExecId().equals(execId))
        		.collect(Collectors.toList());
		boolean hasError = false;
		boolean checkStopExec = false;
		String errorMessage = "";
		// Step ドメインモデル「更新処理自動実行ログ」を更新する
		this.updateLog(execId, ProcessExecutionTask.DELETE_DATA, procExec, procExecLog);

		// データの削除区分の判定する
		// しないの場合
		if (procExec.getExecSetting().getDeleteData().getDataDelCls().equals(NotUseAtr.NOT_USE)) {
			this.updateTaskLogs(taskLogLists, ProcessExecutionTask.DELETE_DATA, EndStatus.NOT_IMPLEMENT, null);
			procExecLog.setTaskLogList(taskLogLists);
			this.procExecLogRepo.update(procExecLog);
			return false;
		}

		// アルゴリズム「自動削除準備」を実行する
		try {
			Optional<String> msg = this.autoExecutionPreparationAdapter.autoDeletionPrepare(procExec);
			errorMessage = msg.orElse(null);
			hasError = msg.isPresent();
		} catch (Exception e) {
			hasError = true;
		}
		// ドメインモデル「更新処理自動実行ログ」を取得しチェックする（中断されている場合は更新されているため、最新の情報を取得する）
		Optional<ProcessExecutionLog> optLog = this.procExecLogRepo
				.getLogByCIdAndExecCd(companyId, procExec.getExecItemCode().v(), execId);
		if (optLog.isPresent()) {
			ProcessExecutionLog log = optLog.get();
			EndStatus status = log.getTaskLogList().stream()
					.filter(task -> task.getProcExecTask().equals(ProcessExecutionTask.DELETE_DATA)
							&& task.getExecId().equals(execId)).findFirst().get()
					.getStatus().orElse(null);
			checkStopExec = EndStatus.FORCE_END.equals(status);
		}
		// 各処理の後のログ更新処理
		this.updateLogAfterProcess.updateLogAfterProcess(ProcessExecutionTask.DELETE_DATA, companyId, execId, procExec,
				procExecLog, hasError, checkStopExec, errorMessage);
		return checkStopExec;
	}

	private boolean updateTaskLogs(List<ExecutionTaskLog> taskLogLists, ProcessExecutionTask procTask, EndStatus status,
			GeneralDateTime execDateTime) {
		boolean existExecutionTaskLog = false;
		for (int i = 0; i < taskLogLists.size(); i++) {
			if (taskLogLists.get(i).getProcExecTask() == procTask) {
				taskLogLists.get(i).setStatus(status);
				taskLogLists.get(i).setLastExecDateTime(execDateTime);
				existExecutionTaskLog = true;
				break;
			}
		}
		return existExecutionTaskLog;
	}

	private void updateLog(String execId, ProcessExecutionTask procTask, UpdateProcessAutoExecution procExec,
			ProcessExecutionLog procExecLog) {
		List<ExecutionTaskLog> taskLogLists = procExecLog.getTaskLogList();
		boolean existExecutionTaskLog = this.updateTaskLogs(taskLogLists, procTask, null, GeneralDateTime.now());
		String companyId = procExec.getCompanyId();
		if (!existExecutionTaskLog) {
			ExecutionTaskLog execTaskLog = ExecutionTaskLog.builder()
					.procExecTask(EnumAdaptor.valueOf(procTask.value, ProcessExecutionTask.class))
					.lastExecDateTime(Optional.of(GeneralDateTime.now())).build();
			taskLogLists.add(execTaskLog);
		}
		String execItemCd = procExec.getExecItemCode().v();
		List<ExecutionTaskLog> taskLogList = this.execTaskLogRepo.getAllByCidExecCdExecId(companyId, execItemCd,
				execId);
		if (CollectionUtil.isEmpty(taskLogList)) {
			this.execTaskLogRepo.insertAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		} else {
			this.execTaskLogRepo.updateAll(companyId, execItemCd, execId, procExecLog.getTaskLogList());
		}
		this.procExecLogRepo.update(procExecLog);
	}
}
