package nts.uk.ctx.at.schedule.app.command.executionlog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.executionlog.CompletionStatus;
import nts.uk.ctx.at.schedule.dom.executionlog.CreationMethod;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionDateTime;
import nts.uk.ctx.at.schedule.dom.executionlog.ExecutionStatus;
import nts.uk.ctx.at.schedule.dom.executionlog.ImplementAtr;
import nts.uk.ctx.at.schedule.dom.executionlog.RecreateCondition;
import nts.uk.ctx.at.schedule.dom.executionlog.ReferenceMaster;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContent;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContentGetMemento;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreator;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorGetMemento;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLogGetMemento;
import nts.uk.ctx.at.schedule.dom.executionlog.SpecifyCreation;
import nts.uk.ctx.at.schedule.dom.shift.pattern.monthly.MonthlyPatternCode;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.employmentrules.organizationmanagement.ConditionEmployee;

/**
 * command clone ScheduleExecutionLogAddCommand update 28/8/2020
 */
@Getter
@Setter
public class ScheduleExecutionAddCommand {
    /**
     * The employee ids. 1
     */

    private String employeeIdLogin;
    /**
     * The period start date.
     */
    private GeneralDate periodStartDate;

    /**
     * The period end date.
     */
    private GeneralDate periodEndDate;

    /**
     * The process execution atr.
     */
    private int creationType;
    /**
     * The reTargetAtr.
     */
    private Boolean reTargetAtr;
    /**
     * The reset master info.
     */
    private int referenceMaster;

    /**
     * The reTargetTransfer.
     */
    private boolean reTargetTransfer;

    /**
     * The reTargetLeave.
     */
    private boolean reTargetLeave;

    /**
     * The reset start end time.
     */
    private boolean reTargetShortWork;

    /**
     * The reTargetLaborChange.
     */
    private boolean reTargetLaborChange;

    /**
     * The reOverwriteConfirmed.
     */
    private boolean reOverwriteConfirmed;

    /**
     * The reOverwriteRevised.
     */
    private boolean reOverwriteRevised;

    /**
     * The monthlyPatternId.
     */
    private String monthlyPatternId;

    /**
     * The confirm.
     */
    private boolean beConfirmed;
    ;

    /**
     * The create method atr.
     */
    private int creationMethod;

    /**
     * The copy start date.
     */
    private GeneralDate copyStartYmd;

    /**
     * The employee ids.
     */
    private List<String> employeeIds;

    /**
     * To domain.
     *
     * @param companyId   the company id
     * @param employeeId  the employee id
     * @param executionId the execution id
     * @return the schedule execution log
     */
    public ScheduleExecutionLog toDomain(String companyId, String employeeId, String executionId ) {
        return new ScheduleExecutionLog(new ScheduleExecutionLogSaveGetMementoImpl(companyId, executionId, employeeId));
    }
    // command to domain
    public ScheduleCreateContent toDomainContentNew(String executionId) {
        val monthly = new MonthlyPatternCode(monthlyPatternId);
        val creMethod = CreationMethod.valueOf(creationMethod);
        Optional copyStartD = Optional.empty();
        if(copyStartYmd!=null){
            copyStartD =  Optional.of(copyStartYmd);
        }
        val refMasValueOf = ReferenceMaster.valueOf(referenceMaster);
        Optional refMasOpt = Optional.empty();
        Optional monthlyOpt = Optional.empty();
        if(creationMethod == CreationMethod.SPECIFY_CREATION.value){
            if(refMasValueOf!=null){
                refMasOpt =   Optional.of(refMasValueOf);
            }
            if(referenceMaster== ReferenceMaster.MONTH_PATTERN.value){
                monthlyOpt = Optional.of(monthly);
            }
        }
        val specify = new SpecifyCreation(creMethod,copyStartD, refMasOpt,monthlyOpt);
        val con = new ConditionEmployee(reTargetTransfer,reTargetLeave,reTargetShortWork,reTargetLaborChange);
        val recreateCondition = new RecreateCondition(reTargetAtr,reOverwriteConfirmed,reOverwriteRevised,Optional.of(con));
        return new ScheduleCreateContent(executionId,beConfirmed,ImplementAtr.valueOf(creationType),specify,Optional.of(recreateCondition));
    }

    /**
     * To domain creator.
     *
     * @param executionId the execution id
     * @return the list
     */
    public List<ScheduleCreator> toDomainCreator(String executionId) {
        return this.employeeIds.stream().map(employeeId -> {
            ScheduleCreator domain = new ScheduleCreator(new ScheduleCreatorGetMemento() {

                /**
                 * Gets the execution status.
                 *
                 * @return the execution status
                 */
                @Override
                public ExecutionStatus getExecutionStatus() {
                    return ExecutionStatus.NOT_CREATED;
                }

                /**
                 * Gets the execution id.
                 *
                 * @return the execution id
                 */
                @Override
                public String getExecutionId() {
                    return executionId;
                }

                /**
                 * Gets the employee id.
                 *
                 * @return the employee id
                 */
                @Override
                public String getEmployeeId() {
                    return employeeId;
                }
            });
            return domain;
        }).collect(Collectors.toList());
    }

    /**
     * The Class ScheduleExecutionLogSaveGetMementoImpl.
     */
    class ScheduleExecutionLogSaveGetMementoImpl implements ScheduleExecutionLogGetMemento {

        /**
         * The company id.
         */
        private String companyId;

        /**
         * The execution id.
         */
        private String executionId;

        /**
         * The employee id.
         */
        private String employeeId;

        /**
         * Instantiates a new schedule execution log save get memento impl.
         *
         * @param companyId   the company id
         * @param executionId the execution id
         * @param employeeId  the employee id
         */
        public ScheduleExecutionLogSaveGetMementoImpl(String companyId, String executionId, String employeeId) {
            this.companyId = companyId;
            this.executionId = executionId;
            this.employeeId = employeeId;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.
         * ScheduleExecutionLogGetMemento#getCompanyId()
         */
        @Override
        public CompanyId getCompanyId() {
            return new CompanyId(companyId);
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.
         * ScheduleExecutionLogGetMemento#getCompletionStatus()
         */
        @Override
        public CompletionStatus getCompletionStatus() {
            return CompletionStatus.INCOMPLETE;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.
         * ScheduleExecutionLogGetMemento#getExecutionId()
         */
        @Override
        public String getExecutionId() {
            return executionId;
        }

        @Override
        public ExecutionDateTime getExecutionDateTime() {
            return new ExecutionDateTime(GeneralDateTime.now(), GeneralDateTime.now());
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.
         * ScheduleExecutionLogGetMemento#getExecutionEmployeeId()
         */
        @Override
        public String getExecutionEmployeeId() {
            return employeeId;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.
         * ScheduleExecutionLogGetMemento#getPeriod()
         */
        @Override
        public DatePeriod getPeriod() {
            return new DatePeriod(periodStartDate, periodEndDate);
        }

        @Override
        public ExecutionAtr getExeAtr() {
            return ExecutionAtr.MANUAL;
        }

    }

    /**
     * The Class ScheduleCreateContentGetMementoImpl.
     */
    class ScheduleCreateContentGetMementoImpl implements ScheduleCreateContentGetMemento {
        //TODO Sua domain: スケジュール作成内容 se tiep tuc khi co tai lieu moi cua man ksc001
        /**
         * The execution id.
         */
        private String executionId;

        /**
         * Instantiates a new schedule create content get memento impl.
         *
         * @param executionId the execution id
         */
        public ScheduleCreateContentGetMementoImpl(String executionId) {
            this.executionId = executionId;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.
         * ScheduleCreateContentGetMemento#getExecutionId()
         */
        @Override
        public String getExecutionId() {
            return this.executionId;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.ExecutionContentGetMemento#
         * getCopyStartDate()
         */
        @Override
        public SpecifyCreation getSpecifyCreation() {
            return new SpecifyCreation(
                    CreationMethod.valueOf(creationMethod),
                    Optional.of(copyStartYmd),
                    Optional.empty(),
                    Optional.empty()

            );
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.ExecutionContentGetMemento#
         * getCreateMethodAtr()
         */
        @Override
        public Optional<RecreateCondition> getRecreateCondition() {
            return Optional.of(new RecreateCondition(
                    false,
                    false,
                    false,
                    Optional.empty()
            ));
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.ExecutionContentGetMemento#
         * getConfirm()
         */
        @Override
        public Boolean getConfirm() {
            return beConfirmed;
        }

        /*
         * (non-Javadoc)
         *
         * @see nts.uk.ctx.at.schedule.dom.executionlog.ExecutionContentGetMemento#
         * getImplementAtr()
         */
        @Override
        public ImplementAtr getCreationType() {
            return ImplementAtr.valueOf(1);
        }
    }
}