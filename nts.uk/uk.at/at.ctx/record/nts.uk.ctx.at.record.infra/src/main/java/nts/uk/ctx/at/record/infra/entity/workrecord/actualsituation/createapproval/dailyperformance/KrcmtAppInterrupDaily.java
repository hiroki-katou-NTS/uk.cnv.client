package nts.uk.ctx.at.record.infra.entity.workrecord.actualsituation.createapproval.dailyperformance;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.createapproval.dailyperformance.AppInterrupDaily;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;
@Entity
@Table(name="KRCMT_APP_INTERRUP_DAILY")
@NoArgsConstructor
public class KrcmtAppInterrupDaily extends ContractCompanyUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "EXECUTION_ID")
	public String executionId;

	@Column(name = "SUSPENDED_STATE")
	public int suspendedState;
	@Override
	protected Object getKey() {
		return executionId;
	}
	
	public KrcmtAppInterrupDaily(String executionId, int suspendedState) {
		super();
		this.executionId = executionId;
		this.suspendedState = suspendedState;
	}
	
	public static KrcmtAppInterrupDaily toEntity(AppInterrupDaily domain) {
		return new KrcmtAppInterrupDaily(
				domain.getExecutionId(),
				domain.isSuspendedState()?1:0);
	}

	public AppInterrupDaily toDomain() {
		return new AppInterrupDaily(
				this.executionId,
				this.suspendedState == 1?true:false
				);
	}
	
}
