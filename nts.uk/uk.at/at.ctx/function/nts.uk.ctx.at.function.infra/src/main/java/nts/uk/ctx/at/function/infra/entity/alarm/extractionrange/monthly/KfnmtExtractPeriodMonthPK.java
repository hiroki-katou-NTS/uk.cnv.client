package nts.uk.ctx.at.function.infra.entity.alarm.extractionrange.monthly;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KfnmtExtractPeriodMonthPK implements Serializable{
	private static final long serialVersionUID = 1L;

	@Column(name = "CID")	
	public String companyID;
	
	@Column(name = "ALARM_PATTERN_CD")	
	public String alarmPatternCD;
	
	@Column(name = "ALARM_CATEGORY")	
	public int alarmCategory;
	
	@Column(name = "UNIT")
	public int unit;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + alarmCategory;
		result = prime * result + ((alarmPatternCD == null) ? 0 : alarmPatternCD.hashCode());
		result = prime * result + ((companyID == null) ? 0 : companyID.hashCode());
		result = prime * result + unit;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KfnmtExtractPeriodMonthPK other = (KfnmtExtractPeriodMonthPK) obj;
		if (alarmCategory != other.alarmCategory)
			return false;
		if (alarmPatternCD == null) {
			if (other.alarmPatternCD != null)
				return false;
		} else if (!alarmPatternCD.equals(other.alarmPatternCD))
			return false;
		if (companyID == null) {
			if (other.companyID != null)
				return false;
		} else if (!companyID.equals(other.companyID))
			return false;
		if (unit != other.unit)
			return false;
		return true;
	}
	

}
