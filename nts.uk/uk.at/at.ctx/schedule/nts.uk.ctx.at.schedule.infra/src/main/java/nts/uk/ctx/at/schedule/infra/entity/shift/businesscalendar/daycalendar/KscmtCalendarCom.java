package nts.uk.ctx.at.schedule.infra.entity.shift.businesscalendar.daycalendar;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_BUSINESS_CALENDAR_CMP")
@Setter
public class KscmtCalendarCom extends ContractUkJpaEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KsmmtCalendarCompanyPK ksmmtCalendarCompanyPK;
	
	@Column(name = "WORKING_DAY_ATR")
	public int workingDayAtr;

	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return this.ksmmtCalendarCompanyPK;
	}
	
	
	

}
