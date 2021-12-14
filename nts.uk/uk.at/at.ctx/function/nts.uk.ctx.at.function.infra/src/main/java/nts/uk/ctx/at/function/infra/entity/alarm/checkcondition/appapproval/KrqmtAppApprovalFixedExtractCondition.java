package nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.appapproval;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Entity
@Table(name = "KRQMT_APPAPV_FXEXCON")
@NoArgsConstructor
@Getter @Setter
public class KrqmtAppApprovalFixedExtractCondition extends UkJpaEntity {

	@EmbeddedId
	private KrqmtAppApprovalFixedExtractConditionPK pk;
	
	@Column(name = "CONTRACT_CD")
	private String contractCode;
	
	@Column(name = "MESSAGE_DISPLAY")
	private String message;
	
	@Column(name = "USE_ATR")
	private int useAtr;
	
	
	public KrqmtAppApprovalFixedExtractCondition(KrqmtAppApprovalFixedExtractConditionPK pk, String contractCode,
			String message, int useAtr) {
		this.pk = pk;
		this.contractCode = contractCode;
		this.message = message;
		this.useAtr = useAtr;
	}
	
	@Override
	protected Object getKey() {
		return pk;
	}
}
