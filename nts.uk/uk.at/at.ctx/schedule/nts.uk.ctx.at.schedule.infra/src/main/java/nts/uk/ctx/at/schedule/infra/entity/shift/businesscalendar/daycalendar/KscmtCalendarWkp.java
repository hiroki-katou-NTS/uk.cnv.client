package nts.uk.ctx.at.schedule.infra.entity.shift.businesscalendar.daycalendar;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_BUSINESS_CALENDAR_WKP")
@Setter
public class KscmtCalendarWkp extends ContractCompanyUkJpaEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KsmmtCalendarWorkplacePK ksmmtCalendarWorkplacePK; 
	
	@Column(name = "WORKING_DAY_ATR")
	public int workingDayAtr;

	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return this.ksmmtCalendarWorkplacePK;
	}	
	
	

}
