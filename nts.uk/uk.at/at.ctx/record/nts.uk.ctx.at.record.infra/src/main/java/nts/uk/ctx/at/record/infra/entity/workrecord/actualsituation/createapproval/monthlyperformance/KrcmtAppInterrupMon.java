package nts.uk.ctx.at.record.infra.entity.workrecord.actualsituation.createapproval.monthlyperformance;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.createapproval.monthlyperformance.AppInterrupMon;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;
@Entity
@Table(name="KRCMT_APP_INTERRUP_MON")
@NoArgsConstructor
public class KrcmtAppInterrupMon extends ContractCompanyUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "EXECUTION_ID")
	public String executionId;

	@Column(name = "SUSPENDED_STATE")
	public boolean suspendedState;
	
	@Override
	protected Object getKey() {
		return executionId;
	}
	
	public KrcmtAppInterrupMon(String executionId, int suspendedState) {
		super();
		this.executionId = executionId;
		this.suspendedState = BooleanUtils.toBoolean(suspendedState);
	}
	
	public static KrcmtAppInterrupMon toEntity(AppInterrupMon domain) {
		return new KrcmtAppInterrupMon(
				domain.getExecutionId(),
				domain.isSuspendedState()?1:0);
	}

	public AppInterrupMon toDomain() {
		return new AppInterrupMon(
				this.executionId,
				this.suspendedState
				);
	}
	
}
