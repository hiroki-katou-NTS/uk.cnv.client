package nts.uk.ctx.at.record.infra.entity.workmanagement.workinitselectset;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * @author HieuLt
 */
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class KrcmtTaskInitialSelHistPk implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/** 社員ID */
	@Basic(optional = false)
	@NotNull
	@Column(name = "SID")
	public String sID;
	
	/** 開始日 */
	@Basic(optional = false)
	@NotNull
	@Column(name = "START_DATE")
	public GeneralDate startDate;

}
