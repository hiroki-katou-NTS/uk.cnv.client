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
public class KsmmtCalendarClassPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "CID")
	public String companyId;
	
	@Column(name = "CLS_CD")
	public String classId;
	
	@Column(name = "YMD")
	public GeneralDate date;
}
