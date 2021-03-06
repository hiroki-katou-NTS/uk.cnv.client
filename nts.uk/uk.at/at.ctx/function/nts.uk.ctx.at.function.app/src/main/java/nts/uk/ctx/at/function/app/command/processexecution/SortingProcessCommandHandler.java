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
     * UKDesign.?????????????????????.NittsuSystem.UniversalK.??????.contexts.????????????.????????????????????????.??????????????????.??????????????????.??????????????????
     *
     * @param context the context
     */
    @Override //??????????????????
	protected void handle(CommandHandlerContext<ScheduleExecuteCommand> context) {

        log.info("SortingProcessCommandHandler handle");

        ScheduleExecuteCommand command = context.getCommand();
        String companyId = command.getCompanyId();
        String companyCd = command.getCompanyCd();
        String execItemCd = command.getExecItemCd();
        GeneralDateTime nextDate = command.getNextDate();
        // Step 1: RQ580?????????????????????????????????????????????????????????
        boolean isAbolished = this.isCheckCompanyAbolished(companyId);
        //	???????????????????????????????????????????????????
        if (isAbolished) {
            log.info("??????????????????????????????????????????");
            return;
        }
        // Step 2: ???????????????????????????????????????????????????????????????
        Optional<ExecutionTaskSetting> executionTaskSettingOpt = execSettingRepo.getByCidAndExecCd(companyId, command.getExecItemCd());
        if (!executionTaskSettingOpt.isPresent()) {
            log.info("??????????????????????????????????????????????????????");
            return;
        }
        // Step 3: ?????????????????????????????????????????????.????????????????????????????????????????????????
        if (!executionTaskSettingOpt.get().isEnabledSetting()) {
            //???????????????
            log.info("?????????????????????.????????????????????????????????????????????????");
            return;//???????????????
        }
        log.info(":????????????????????????_START_" + command.getExecItemCd() + "_" + GeneralDateTime.now());
        //??????ID?????????????????????
        String execItemId = IdentifierUtil.randomUniqueId();
        // Step 4: ?????????????????????????????????
        String contractCode = AppContexts.user().contractCode();
        UsageStopOutputImport isSuspension = this.stopBycompanyAdapter.checkUsageStop(contractCode, companyCd);
        if (isSuspension.isUsageStop()) {
            // case ??????????????????
            // Step ????????????????????????????????????????????????
            this.DistributionRegistProcess(companyId, execItemCd, execItemId, true);
            log.info("????????????????????????????????????????????????");
            return;
        } else {
            // case ?????????????????????
            // Step ?????????????????????????????????????????????????????????????????????
            Optional<ProcessExecutionLogManage> logManageOpt = this.processExecLogManaRepo.getLogByCIdAndExecCd(companyId, execItemCd);
            if (!logManageOpt.isPresent()) {
                log.info("???????????????????????????????????????????????????????????????");
                return;
            }
            ProcessExecutionLogManage processExecutionLogManage = logManageOpt.get();
            // Step ??????????????????????????????????????????????????????.?????????????????????????????????????????????
            // ???????????????
            if (processExecutionLogManage.getCurrentStatus().isPresent()
            		&& processExecutionLogManage.getCurrentStatus().get() == CurrentExecutionStatus.RUNNING) {

                log.info("??????????????????????????????.?????????????????????????????????");

                // ????????????????????????????????????????????????????????????????????????????????????5?????????????????????????????????????????????
                boolean checkLastTime = checkLastDateTimeLessthanNow5h(processExecutionLogManage.getLastExecDateTime().get());
                if (checkLastTime) {
                    // Step ????????????????????????????????? - Registration process when running
                    log.info("??????????????????????????????.?????????????????? ??????5???????????????????????????????????????");
                    this.DistributionRegistProcess(companyId, execItemCd, execItemId, false);
                } else {
                    // Step ????????????
                    log.info("????????????????????????????????????5???????????????????????????????????????????????????");
                    this.executeHandler(companyId, execItemCd, execItemId, nextDate);
                }
            }
            // ???????????????
            else if (processExecutionLogManage.getCurrentStatus().isPresent()
            		&& processExecutionLogManage.getCurrentStatus().get() == CurrentExecutionStatus.WAITING) {

                log.info("??????????????????????????????.?????????????????????????????????");

                // Step ????????????
                log.info("???????????????????????????????????????????????????????????????");
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
     * UKDesign.?????????????????????.NittsuSystem.UniversalK.??????.contexts.????????????.????????????????????????.??????????????????.??????????????????.????????????????????????????????????????????????.????????????????????????????????????????????????
     *
     * @param companyId  the company id
     * @param execItemCd the exec item cd
     * @param execItemId the exec item id
     * @param nextDate   the next date
     */
    //???????????????????????? -> ????????????????????????????????????????????????
    private void DistributionRegistProcess(String companyId, String execItemCd, String execItemId, boolean isSystemSuspended) {
        // Step ????????????????????????????????????????????????????????????????????????
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

        //???????????????????????????????????????????????????????????????
        Optional<ExecutionTaskSetting> executionTaskSetOpt = this.execSettingRepo.getByCidAndExecCd(companyId, execItemCd);
        if (executionTaskSetOpt.isPresent()) {
            ExecutionTaskSetting executionTaskSetting = executionTaskSetOpt.get();
            // ?????????????????????????????????????????????????????????????????????
            GeneralDateTime nextDate = this.processExecutionService.processNextExecDateTimeCreation(executionTaskSetting);
            executionTaskSetting.setNextExecDateTime(Optional.ofNullable(nextDate));
            this.execSettingRepo.update(executionTaskSetting);
        }
    }

    //No.3604
    private boolean checkLastDateTimeLessthanNow5h(GeneralDateTime dateTime) {
        GeneralDateTime today = GeneralDateTime.now();
        GeneralDateTime newDateTime = dateTime.addHours(5);
		//?????????????????? - ?????????????????? <= 5??????
		return today.beforeOrEquals(newDateTime);
        //?????????????????? - ?????????????????? > 5??????
	}

    /**
     * RQ580?????????????????????????????????????????????????????????.
     *
     * @param companyId the company id
     * @return true, if successful
     */
    private boolean isCheckCompanyAbolished(String companyId) {
        // Step 1 ?????????????????????????????????????????????????????? - Get domain model "company information"
        CompanyInfo companyInfo = this.companyAdapter.getCompanyInfoById(companyId);
        // Step 2 ????????????????????????????????? - Check the abolition category
        // Due to RQ only returns value if isAbolition != 1
        // Otherwise returns a new Company instance
        // If CompanyInfo value equals to a new instance
        // Then this company is abolished
        return companyInfo.equals(CompanyInfo.builder().build())
        		|| companyInfo.getIsAbolition() == Abolition.ABOLISH.value;
    }
}
