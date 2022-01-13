/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.shift.basicworkregister;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


/**
 * The Class KscmtWorkplaceWorkSetPK.
 */
@Setter
@Getter
@Embeddable
public class KscmtWorkplaceWorkSetPK implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The workplace id. */
	@Column(name = "WKP_ID")
	private String workplaceId;
	
	/** The workday division. */
	@Column(name = "WORKING_DAY_ATR")
	private Integer workdayDivision;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((workdayDivision == null) ? 0 : workdayDivision.hashCode());
		result = prime * result + ((workplaceId == null) ? 0 : workplaceId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KscmtWorkplaceWorkSetPK other = (KscmtWorkplaceWorkSetPK) obj;
		if (workdayDivision == null) {
			if (other.workdayDivision != null)
				return false;
		} else if (!workdayDivision.equals(other.workdayDivision))
			return false;
		if (workplaceId == null) {
			if (other.workplaceId != null)
				return false;
		} else if (!workplaceId.equals(other.workplaceId))
			return false;
		return true;
	}

}
