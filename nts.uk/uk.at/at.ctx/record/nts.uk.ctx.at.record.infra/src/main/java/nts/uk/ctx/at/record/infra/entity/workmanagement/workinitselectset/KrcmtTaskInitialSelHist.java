package nts.uk.ctx.at.record.infra.entity.workmanagement.workinitselectset;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSel;
import nts.uk.ctx.at.record.dom.workmanagement.workinitselectset.TaskInitialSelHist;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;
@Entity
@NoArgsConstructor

@Table(name="KRCMT_TASK_INITIAL_SEL_HIST")
public class KrcmtTaskInitialSelHist extends ContractCompanyUkJpaEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrcmtTaskInitialSelHistPk pk;
	
	/** 終了日 **/
	@Basic(optional = false)
	@Column(name = "END_DATE")
	public GeneralDate endDate;
	
	/** 作業コード1 **/
	@Basic(optional = false)
	@Column(name = "TASK_CD_1")
	public String taskCd1;
	
	/** 作業コード2 **/
	@Basic(optional = false)
	@Column(name = "TASK_CD_2")
	public String taskCd2;
	
	/** 作業コード3 **/
	@Basic(optional = false)
	@Column(name = "TASK_CD_3")
	public String taskCd3;
	
	/** 作業コード4 **/
	@Basic(optional = false)
	@Column(name = "TASK_CD_4")
	public String taskCd4;
	
	/** 作業コード5 **/
	@Basic(optional = false)
	@Column(name = "TASK_CD_5")
	public String taskCd5;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcmtTaskInitialSelHist(KrcmtTaskInitialSelHistPk pk, GeneralDate endDate, String taskCd1, String taskCd2,
			String taskCd3, String taskCd4, String taskCd5) {
		super();
		this.pk = pk;
		this.endDate = endDate;
		this.taskCd1 = taskCd1;
		this.taskCd2 = taskCd2;
		this.taskCd3 = taskCd3;
		this.taskCd4 = taskCd4;
		this.taskCd5 = taskCd5;
	}
	
	public static KrcmtTaskInitialSelHist toEntity(TaskInitialSelHist domain, int index){	
		TaskInitialSel taskInitialSel = domain.getLstHistory().get(index);
		
		return  new KrcmtTaskInitialSelHist(
					new KrcmtTaskInitialSelHistPk(domain.getEmpId(), taskInitialSel.getDatePeriod().start()),
					taskInitialSel.getDatePeriod().end(),
					taskInitialSel.getTaskItem().getOtpWorkCode1().isPresent() ? taskInitialSel.getTaskItem().getOtpWorkCode1().get().v() : "",
					taskInitialSel.getTaskItem().getOtpWorkCode2().isPresent() ? taskInitialSel.getTaskItem().getOtpWorkCode2().get().v() : "",
					taskInitialSel.getTaskItem().getOtpWorkCode3().isPresent() ? taskInitialSel.getTaskItem().getOtpWorkCode3().get().v() : "",
					taskInitialSel.getTaskItem().getOtpWorkCode4().isPresent() ? taskInitialSel.getTaskItem().getOtpWorkCode4().get().v() : "",
					taskInitialSel.getTaskItem().getOtpWorkCode5().isPresent() ? taskInitialSel.getTaskItem().getOtpWorkCode5().get().v() : ""
				);
	}
}
