package nts.uk.ctx.at.request.infra.entity.application.common.approvalframe;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
/**
 * 
 * @author hieult
 *
 */
@Entity
@Table(name = "KRQDT_APPROVAL_FRAME")
@AllArgsConstructor
@NoArgsConstructor
public class KrqdtApprovalFrame extends UkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrqdtApprovalFramePK krqdtApprovalFramePK;
	
	@Column(name = "AUTHORIZER_SID")
	public String authorizerSID;
	
	@Column(name = "SUBSTITUTE_SID")
	public String substituteSID;
	
	@Column(name = "APPROVAL_ATR")
	public String approvalATR;
	
	@Column(name = "APPROVAL_DATE")
	public String approvalDate;
	
	@Column(name = "REASON")
	public String reason;
	
	@Column(name = "CONFIRM_ATR")
	public String confirmATR;
	
	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return krqdtApprovalFramePK;
	}

}
