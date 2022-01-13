package nts.uk.ctx.at.function.app.command.processexecution;

import java.util.Collections;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDateTime;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.function.dom.adapter.stopbycompany.StopByCompanyAdapter;
import nts.uk.ctx.at.function.dom.adapter.stopbycompany.UsageStopOutputImport;
import nts.uk.ctx.at.function.dom.processexecution.ProcessExecutionService;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.CurrentExecutionStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.EndStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.OverallErrorDetail;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogHistory;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;
import nts.uk.ctx.at.function.dom.processexecution.repository.ExecutionTaskSettingRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogHistRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogManageRepository;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.ExecutionTaskSetting;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyAdapter;
import nts.uk.ctx.at.shared.dom.adapter.holidaymanagement.CompanyInfo;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.Abolition;

//import nts.uk.shr.com.task.schedule.UkJobScheduler;
@Stateless
@Slf4j
public class SortingProcessCommandHandler extends CommandHandler<ScheduleExecuteCommand> {
    @Inject
    private ExecuteProcessExecutionAutoCommandHandler execHandler;
    @Inject
    private ProcessExecutionLogManageRepository processExecLogManaRepo;
    @Inject
    private ProcessExecutionLogHistRepository processExecLogHistRepo;
    @Inject
    private ExecutionTaskSettingRepository execSettingRepo;

    @Inject
    private CompanyAdapter companyAdapter;

    @Inject
    private StopByCompanyAdapter stopBycompanyAdapter;
    
    @Inject
    private ProcessExecutionService processExecutionService;

//	@Inject
//	private UkJobScheduler ukJobScheduler;

    /**
     * Handle.
     * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.アルゴリズム.振り分け処理.振り分け処理
     *
     * @param context the context
     */
    @Override //振り分け処理
	protected void handle(CommandHandlerContext<ScheduleExecuteCommand> context) {

        log.info("SortingProcessCommandHandler handle");

        ScheduleExecuteCommand command = context.getCommand();
        String companyId = command.getCompanyId();
        String companyCd = command.getCompanyCd();
        String execItemCd = command.getExecItemCd();
        GeneralDateTime nextDate = command.getNextDate();
        // Step 1: RQ580「会社の廃止をチェックする」を実行する
        boolean isAbolished = this.isCheckCompanyAbolished(companyId);
        //	取得した「廃止区分」をチェックする
        if (isAbolished) {
            log.info("会社が廃止されているため中断");
            return;
        }
        // Step 2: ドメインモデル「実行タスク設定」を取得する
        Optional<ExecutionTaskSetting> executionTaskSettingOpt = execSettingRepo.getByCidAndExecCd(companyId, command.getExecItemCd());
        if (!executionTaskSettingOpt.isPresent()) {
            log.info("実行タスク設定が取得できないため中断");
            return;
        }
        // Step 3: ドメインモデル「実行タスク設定.更新処理有効設定」をチェックする
        if (!executionTaskSettingOpt.get().isEnabledSetting()) {
            //無効の場合
            log.info("実行タスク設定.更新処理有効設定が無効のため中断");
            return;//フロー終了
        }
        log.info(":更新処理自動実行_START_" + command.getExecItemCd() + "_" + GeneralDateTime.now());
        //実行IDを新規採番する
        String execItemId = IdentifierUtil.randomUniqueId();
        // Step 4: 利用停止をチェックする
        String contractCode = AppContexts.user().contractCode();
        UsageStopOutputImport isSuspension = this.stopBycompanyAdapter.checkUsageStop(contractCode, companyCd);
        if (isSuspension.isUsageStop()) {
            // case 利用停止する
            // Step 前回の更新処理が実行中の登録処理
            this.DistributionRegistProcess(companyId, execItemCd, execItemId, true);
            log.info("前回の更新処理が実行中のため中断");
            return;
        } else {
            // case 利用停止しない
            // Step ドメインモデル「更新処理自動実行管理」取得する
            Optional<ProcessExecutionLogManage> logManageOpt = this.processExecLogManaRepo.getLogByCIdAndExecCd(companyId, execItemCd);
            if (!logManageOpt.isPresent()) {
                log.info("更新処理自動実行管理が取得できないため中断");
                return;
            }
            ProcessExecutionLogManage processExecutionLogManage = logManageOpt.get();
            // Step ドメインモデル「更新処理自動実行管理.現在の実行状態」をチェックする
            // 「実行中」
            if (processExecutionLogManage.getCurrentStatus().isPresent()
            		&& processExecutionLogManage.getCurrentStatus().get() == CurrentExecutionStatus.RUNNING) {

                log.info("更新処理自動実行管理.現在の実行状態：実行中");

                // ドメインモデル「更新処理自動実行管理．前回実行日時」から5時間を経っているかチェックする
                boolean checkLastTime = checkLastDateTimeLessthanNow5h(processExecutionLogManage.getLastExecDateTime().get());
                if (checkLastTime) {
                    // Step 実行中の場合の登録処理 - Registration process when running
                    log.info("更新処理自動実行管理.前回実行日時 から5時間経過していないため中断");
                    this.DistributionRegistProcess(companyId, execItemCd, execItemId, false);
                } else {
                    // Step 実行処理
                    log.info("実行中ステータスであるが5時間経過したため強制的に実行処理へ");
                    this.executeHandler(companyId, execItemCd, execItemId, nextDate);
                }
            }
            // 「待機中」
            else if (processExecutionLogManage.getCurrentStatus().isPresent()
            		&& processExecutionLogManage.getCurrentStatus().get() == CurrentExecutionStatus.WAITING) {

                log.info("更新処理自動実行管理.現在の実行状態：待機中");

                // Step 実行処理
                log.info("待機中ステータスであるため正常に実行処理へ");
                this.executeHandler(companyId, execItemCd, execItemId, nextDate);
            }
        }

        log.info("SortingProcessCommandHandler handle finished");
    }

    private void executeHandler(String companyId, String execItemCd, String execItemId, GeneralDateTime nextDate) {
        ExecuteProcessExecutionCommand executeProcessExecutionCommand = new ExecuteProcessExecutionCommand();
        executeProcessExecutionCommand.setCompanyId(companyId);
        executeProcessExecutionCommand.setExecItemCd(execItemCd);
        executeProcessExecutionCommand.setExecId(execItemId);
        executeProcessExecutionCommand.setExecType(0);
        executeProcessExecutionCommand.setNextFireTime(Optional.ofNullable(nextDate));
        //AsyncCommandHandlerContext<ExecuteProcessExecutionCommand> ctxNew = new AsyncCommandHandlerContext<ExecuteProcessExecutionCommand>(executeProcessExecutionCommand);
        this.execHandler.handle(executeProcessExecutionCommand);
    }

    /**
     * Distribution regist process.
     * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.更新処理自動実行.アルゴリズム.振り分け処理.前回の更新処理が実行中の登録処理.前回の更新処理が実行中の登録処理
     *
     * @param companyId  the company id
     * @param execItemCd the exec item cd
     * @param execItemId the exec item id
     * @param nextDate   the next date
     */
    //振り分け登録処理 -> 前回の更新処理が実行中の登録処理
    private void DistributionRegistProcess(String companyId, String execItemCd, String execItemId, boolean isSystemSuspended) {
        // Step ドメインモデル「更新処理自動実行管理」を更新する
        ProcessExecutionLogManage processExecutionLogManage = this.processExecLogManaRepo.getLogByCIdAndExecCd(companyId, execItemCd).get();
        processExecutionLogManage.setLastExecDateTimeEx(GeneralDateTime.now());
        processExecLogManaRepo.update(processExecutionLogManage);

        ProcessExecutionLogHistoryCommand processExecutionLogHistoryCommand = ProcessExecutionLogHistoryCommand.builder()
                .execItemCd(execItemCd)
                .companyId(companyId)
                .execId(execItemId)
                .overallError(isSystemSuspended ? OverallErrorDetail.NOT_EXECUTE.value : OverallErrorDetail.NOT_FINISHED.value)
                .overallStatus(EndStatus.FORCE_END.value)
                .lastExecDateTime(GeneralDateTime.now())
                .lastEndExecDateTime(GeneralDateTime.now())
                .taskLogList(Collections.emptyList())
                .build();
        ProcessExecutionLogHistory processExecutionLogHistory = ProcessExecutionLogHistory.createFromMemento(processExecutionLogHistoryCommand);
        processExecLogHistRepo.insert(processExecutionLogHistory);

        //ドメインモデル「実行タスク設定」を更新する
        Optional<ExecutionTaskSetting> executionTaskSetOpt = this.execSettingRepo.getByCidAndExecCd(companyId, execItemCd);
        if (executionTaskSetOpt.isPresent()) {
            ExecutionTaskSetting executionTaskSetting = executionTaskSetOpt.get();
            // アルゴリズム「次回実行日時作成処理」を実行する
            GeneralDateTime nextDate = this.processExecutionService.processNextExecDateTimeCreation(executionTaskSetting);
            executionTaskSetting.setNextExecDateTime(Optional.ofNullable(nextDate));
            this.execSettingRepo.update(executionTaskSetting);
        }
    }

    //No.3604
    private boolean checkLastDateTimeLessthanNow5h(GeneralDateTime dateTime) {
        GeneralDateTime today = GeneralDateTime.now();
        GeneralDateTime newDateTime = dateTime.addHours(5);
		//システム日時 - 前回実行日時 <= 5時間
		return today.beforeOrEquals(newDateTime);
        //システム日時 - 前回実行日時 > 5時間
	}

    /**
     * RQ580「会社の廃止をチェックする」を実行する.
     *
     * @param companyId the company id
     * @return true, if successful
     */
    private boolean isCheckCompanyAbolished(String companyId) {
        // Step 1 ドメインモデル「会社情報」を取得する - Get domain model "company information"
        CompanyInfo companyInfo = this.companyAdapter.getCompanyInfoById(companyId);
        // Step 2 廃止区分をチェックする - Check the abolition category
        // Due to RQ only returns value if isAbolition != 1
        // Otherwise returns a new Company instance
        // If CompanyInfo value equals to a new instance
        // Then this company is abolished
        return companyInfo.equals(CompanyInfo.builder().build())
        		|| companyInfo.getIsAbolition() == Abolition.ABOLISH.value;
    }
}
