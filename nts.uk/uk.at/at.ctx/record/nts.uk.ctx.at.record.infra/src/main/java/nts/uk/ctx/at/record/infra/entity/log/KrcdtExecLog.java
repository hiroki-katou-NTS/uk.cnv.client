package nts.uk.ctx.at.record.infra.entity.log;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
//import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
//import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ExecutionLog;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author nampt
 * 実行ログ
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_EXEC_LOG")
public class KrcdtExecLog extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtExecutionLogPK krcdtExecutionLogPK;
	/**
	 * エラーの有無
	 */
	@Column(name = "EXISTENCE_ERROR")
	public int existenceError;

	@Column(name = "EXECUTION_START_DATE")
	public GeneralDateTime executionStartDate;
	
	@Column(name = "EXECUTION_END_DATE")
	public GeneralDateTime executionEndDate;

	@Column(name = "PROCESSING_SITUATION")
	public int processStatus;

	@Column(name = "PERIOD_COVERED_START_DATE")
	public GeneralDate periodCoverdStartDate;

	@Column(name = "PERIOD_COVERED_END_DATE")
	public GeneralDate periodCoverdEndDate;
	
	@Column(name = "CAL_EXECUTION_SET_INFO_ID")
	public String calExecutionSetInfoID;
	
    @Column(name = "CAN_CALCULATE_WHEN_LOCK")
    public Integer isCalWhenLock;
//	@ManyToOne
//	@JoinColumn(name="EMP_EXECUTION_LOG_ID", referencedColumnName="EMP_EXECUTION_LOG_ID", insertable = false, updatable = false)
//	public KrcdtExec empexecutionlog;
	
	@OneToOne(mappedBy="executionlog", cascade = CascadeType.ALL)
	@JoinTable(name = "KRCMT_EXEC_CASE_DETAIL")
	public KrcdtCalExeSetInfor calExeSetInfor;
	
	public KrcdtExecLog(KrcdtExecutionLogPK krcdtExecutionLogPK, int existenceError,
			GeneralDateTime executionStartDate, GeneralDateTime executionEndDate, int processStatus,
            GeneralDate periodCoverdStartDate, GeneralDate periodCoverdEndDate, String calExecutionSetInfoID,Integer isCalWhenLock
			) {
		super();
		this.krcdtExecutionLogPK = krcdtExecutionLogPK;
		this.existenceError = existenceError;
		this.executionStartDate = executionStartDate;
		this.executionEndDate = executionEndDate;
		this.processStatus = processStatus;
		this.periodCoverdStartDate = periodCoverdStartDate;
		this.periodCoverdEndDate = periodCoverdEndDate;
		this.calExecutionSetInfoID = calExecutionSetInfoID;
        this.isCalWhenLock = isCalWhenLock;
	}
	
	public KrcdtExecLog(KrcdtExecutionLogPK krcdtExecutionLogPK, int existenceError,
			GeneralDateTime executionStartDate, GeneralDateTime executionEndDate, int processStatus,
			GeneralDate periodCoverdStartDate, GeneralDate periodCoverdEndDate) {
		super();
		this.krcdtExecutionLogPK = krcdtExecutionLogPK;
		this.existenceError = existenceError;
		this.executionStartDate = executionStartDate;
		this.executionEndDate = executionEndDate;
		this.processStatus = processStatus;
		this.periodCoverdStartDate = periodCoverdStartDate;
		this.periodCoverdEndDate = periodCoverdEndDate;
	}

	public ExecutionLog toDomain() {
		val domain = ExecutionLog.createFromJavaType(
				this.krcdtExecutionLogPK.empCalAndSumExecLogID,
				this.krcdtExecutionLogPK.executionContent, 
				this.existenceError, 
				this.executionStartDate,
				this.executionEndDate, 
				this.processStatus, 
				this.periodCoverdStartDate, 
				this.periodCoverdEndDate,
                Optional.ofNullable(this.isCalWhenLock == null ? null : this.isCalWhenLock== 1));
		if (this.krcdtExecutionLogPK.executionContent == ExecutionContent.DAILY_CREATION.value) {
			domain.setDailyCreationSetInfo(calExeSetInfor.toDomain());
		} else if(this.krcdtExecutionLogPK.executionContent == ExecutionContent.DAILY_CALCULATION.value ) {
			domain.setDailyCalSetInfo(calExeSetInfor.toDomain());
		} else if(this.krcdtExecutionLogPK.executionContent == ExecutionContent.REFLRCT_APPROVAL_RESULT.value) {
			domain.setReflectApprovalSetInfo(calExeSetInfor.toDomain());
		} else {
			domain.setMonlyAggregationSetInfo(calExeSetInfor.toDomain());
		}
		return domain;
	}
	
	@Override
	protected Object getKey() {
		return this.krcdtExecutionLogPK;
	}
	
	public static KrcdtExecLog toEntity(ExecutionLog domain) {
		val entity = new KrcdtExecLog(
				 new KrcdtExecutionLogPK(
					 domain.getEmpCalAndSumExecLogID(),
					 domain.getExecutionContent().value
					),
				 domain.getExistenceError().value,
				 domain.getExecutionTime().getStartTime(),
				 domain.getExecutionTime().getEndTime(),
				 domain.getProcessStatus().value,
				(domain.getObjectPeriod()!=null&&domain.getObjectPeriod().isPresent())?domain.getObjectPeriod().get().getStartDate():null,
				(domain.getObjectPeriod()!=null&&domain.getObjectPeriod().isPresent())?domain.getObjectPeriod().get().getEndDate():null,
				 domain.getCalExecutionSetInfoID(),
                 domain.getIsCalWhenLock().map(c -> c ? 1 : 0).orElse(0));
		if (domain.getExecutionContent() == ExecutionContent.DAILY_CREATION) {
			entity.calExeSetInfor = KrcdtCalExeSetInfor.toEntity(domain.getDailyCreationSetInfo().get());
		}else if(domain.getExecutionContent() == ExecutionContent.DAILY_CALCULATION) {
			entity.calExeSetInfor = KrcdtCalExeSetInfor.toEntity(domain.getDailyCalSetInfo().get());
		}else if(domain.getExecutionContent() == ExecutionContent.REFLRCT_APPROVAL_RESULT) {
			entity.calExeSetInfor = KrcdtCalExeSetInfor.toEntity(domain.getReflectApprovalSetInfo().get());
		}else {
			entity.calExeSetInfor = KrcdtCalExeSetInfor.toEntity(domain.getMonlyAggregationSetInfo().get());
		}
		return entity;
	}
}
