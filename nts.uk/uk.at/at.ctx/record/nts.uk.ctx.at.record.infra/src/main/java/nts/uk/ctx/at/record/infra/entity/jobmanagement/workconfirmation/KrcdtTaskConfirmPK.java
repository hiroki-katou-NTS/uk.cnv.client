package nts.uk.ctx.at.record.infra.entity.jobmanagement.workconfirmation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KrcdtTaskConfirmPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "TARGET_SID")
	public String targetSid;
	
	@Column(name = "TARGET_YMD")
	public GeneralDate targetYMD;
}
