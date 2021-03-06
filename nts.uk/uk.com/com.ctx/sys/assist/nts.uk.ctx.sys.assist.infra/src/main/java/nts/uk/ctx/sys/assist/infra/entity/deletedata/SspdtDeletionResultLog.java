package nts.uk.ctx.sys.assist.infra.entity.deletedata;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.sys.assist.dom.deletedata.ErrorContent;
import nts.uk.ctx.sys.assist.dom.deletedata.ResultLogDeletion;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@Table(name = "SSPDT_DELETION_RESULT_LOG")
@NoArgsConstructor
public class SspdtDeletionResultLog extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
    public SspdtResultLogDeletionPK sspdtResultLogDeletionPK;

	/** The company Id. */
	/** 会社ID */
	@Basic(optional = false)
	@Column(name = "CID")
	public String companyID;

	/** The log time. */
	/** ログ登録日時 */
	@Basic(optional = false)
	@Column(name = "LOG_TIME")
	public GeneralDateTime logTime;

	/** The processing content. */
	// 処理内容
	@Basic(optional = false)
	@Column(name = "PROCESSING_CONTENT")
	public String processingContent;

	/** The error content. */
	// エラー内容
	@Basic(optional = true)
	@Column(name = "ERROR_CONTENT")
	public String errorContent;

	/** The error employee id. */
	// エラー社員
	@Basic(optional = true)
	@Column(name = "ERROR_EMPLOYEE_ID")
	public String errorEmployeeId;

	/** The error date. */
	// エラー日付
	@Basic(optional = true)
	@Column(name = "ERROR_DATE")
	public GeneralDate errorDate;
	
	@ManyToOne
	@JoinColumn(name="DEL_ID", referencedColumnName="DEL_ID", insertable = false, updatable = false)		
	public SspdtDeletionResult resultDeletion;

	@Override
	protected Object getKey() {
		return sspdtResultLogDeletionPK;
	}

	public ResultLogDeletion toDomain() {
		return ResultLogDeletion.createFromJavatype(this.sspdtResultLogDeletionPK.seqId, this.sspdtResultLogDeletionPK.delId, this.companyID, this.logTime,
				this.processingContent, this.errorContent, this.errorEmployeeId, this.errorDate);
	}

	public static SspdtDeletionResultLog toEntity(ResultLogDeletion resultLog) {
		SspdtDeletionResultLog e = new SspdtDeletionResultLog(new SspdtResultLogDeletionPK(resultLog.getDelId(), resultLog.getSeqId()), 
				resultLog.getCompanyId(), resultLog.getLogTime(), resultLog.getProcessingContent().v(), resultLog.getErrorContent().map(ErrorContent::v).orElse(null),
				resultLog.getErrorEmployeeId().orElse(null), resultLog.getErrorDate().orElse(null));
		System.out.println();
		return e;
	}

	public SspdtDeletionResultLog
		(
		SspdtResultLogDeletionPK sspdtResultLogDeletionPK, 
		String companyId,
		GeneralDateTime logTime, 
		String processingContent, 
		String errorContent, 
		String errorEmployeeId,
		GeneralDate errorDate
		) {
			super();
			this.sspdtResultLogDeletionPK = sspdtResultLogDeletionPK;
			this.companyID = companyId;
			this.logTime = logTime;
			this.processingContent = processingContent;
			this.errorContent = errorContent;
			this.errorEmployeeId = errorEmployeeId;
			this.errorDate = errorDate;
	}
}
