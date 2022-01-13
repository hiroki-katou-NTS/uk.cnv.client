package nts.uk.ctx.at.record.infra.entity.daily.timegroup;

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
public class KsrdtTaskTsGroupPk implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "SID")
	public String sId;
	
	@Column(name = "YMD")
	public GeneralDate date;
	
	@Column(name = "START_CLOCK") 
	public int startClock;
	
	@Column(name = "SUP_NO")
	public int subNo;
}
