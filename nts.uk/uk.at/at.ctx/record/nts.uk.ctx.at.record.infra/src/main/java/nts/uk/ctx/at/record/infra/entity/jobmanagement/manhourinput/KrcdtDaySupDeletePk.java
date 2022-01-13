package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinput;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author tutt
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KrcdtDaySupDeletePk implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// 社員ID
	@Column(name = "SID")
	public String sId;
	
	// 年月日
	@Column(name = "YMD")
	public GeneralDate ymd;
	
	// 応援勤務枠NO
	@Column(name = "NO")
	public int supTaskNo;
}
