package nts.uk.ctx.at.record.infra.entity.workrecord.identificationstatus.manageactualsituation.approval.monthly;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.workrecord.manageactualsituation.approval.monthly.ApprovalStatusMonthlyResult;
import nts.uk.ctx.at.shared.dom.common.Day;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KRCMT_MON_APPROVAL")
public class KrcmtApprovalStatusMonthlyResult extends UkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtApprovalStatusMonthlyResultPK krcmtApprovalStatusMonthlyResultPK;
	
	@Column(name = "CLOSING_DATE")
	public int closingDate;
	
	@Override
	protected Object getKey() {
		return krcmtApprovalStatusMonthlyResultPK;
	}

	public KrcmtApprovalStatusMonthlyResult(KrcmtApprovalStatusMonthlyResultPK krcmtApprovalStatusMonthlyResultPK,
			int closingDate) {
		super();
		this.krcmtApprovalStatusMonthlyResultPK = krcmtApprovalStatusMonthlyResultPK;
		this.closingDate = closingDate;
	}
	
	public static KrcmtApprovalStatusMonthlyResult toEntity(ApprovalStatusMonthlyResult domain ) {
		return new KrcmtApprovalStatusMonthlyResult(
				new KrcmtApprovalStatusMonthlyResultPK(
						domain.getEmployeeId(),
						domain.getProcessDate().v(), 
						domain.getClosureId().value, 
						domain.getRootInstanceID()),
				domain.getClosingDate().v()
				);
	}
	
	public ApprovalStatusMonthlyResult toDomain(KrcmtApprovalStatusMonthlyResult entity) {
		return new ApprovalStatusMonthlyResult(
				entity.krcmtApprovalStatusMonthlyResultPK.employeeId,
				new YearMonth(entity.krcmtApprovalStatusMonthlyResultPK.processDate),
				EnumAdaptor.valueOf(entity.krcmtApprovalStatusMonthlyResultPK.closureId, ClosureId.class) ,
				new Day(entity.closingDate),
				entity.krcmtApprovalStatusMonthlyResultPK.rootInstanceID
				);
	}

}
