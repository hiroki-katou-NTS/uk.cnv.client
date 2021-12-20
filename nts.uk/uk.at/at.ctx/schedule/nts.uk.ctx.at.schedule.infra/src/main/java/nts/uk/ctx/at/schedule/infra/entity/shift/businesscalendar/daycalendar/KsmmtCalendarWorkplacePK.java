package nts.uk.ctx.at.schedule.infra.entity.shift.businesscalendar.daycalendar;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KsmmtCalendarWorkplacePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "WKP_ID")
	public String workPlaceId;

	
	@Column(name = "YMD")
	public GeneralDate date;

}
