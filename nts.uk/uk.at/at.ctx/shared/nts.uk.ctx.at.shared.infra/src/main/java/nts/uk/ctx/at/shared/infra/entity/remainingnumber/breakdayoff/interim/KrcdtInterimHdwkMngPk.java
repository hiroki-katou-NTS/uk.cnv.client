package nts.uk.ctx.at.shared.infra.entity.remainingnumber.breakdayoff.interim;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author sonnlb
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KrcdtInterimHdwkMngPk implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 会社ID */
	@Column(name = "CID")
	public String companyID;

	/** 社員ID */
	@Column(name = "SID")
	public String sid;

	/** 対象日 */
	@Column(name = "YMD")
	public GeneralDate ymd;

}
