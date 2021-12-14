package nts.uk.ctx.at.schedule.infra.entity.shift.specificdayset.workplace;

import java.io.Serializable;
//import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KsmmtWpSpecDateSetPK implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "WKPID")
	public String workplaceId;

	@Column(name = "SPECIFIC_DATE")
	public GeneralDate specificDate;

	@Column(name = "SPECIFIC_DATE_ITEM_NO")
	public Integer specificDateItemNo;

}
