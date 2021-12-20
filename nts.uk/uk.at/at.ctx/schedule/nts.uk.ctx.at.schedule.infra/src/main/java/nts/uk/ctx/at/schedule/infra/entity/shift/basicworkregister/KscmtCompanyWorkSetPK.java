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
 * The Class KscmtCompanyWorkSetPK.
 */
@Setter
@Getter
@Embeddable
public class KscmtCompanyWorkSetPK implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cid. */
	@Column(name = "CID")
	private String cid;
	
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
		result = prime * result + ((cid == null) ? 0 : cid.hashCode());
		result = prime * result + ((workdayDivision == null) ? 0 : workdayDivision.hashCode());
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
		KscmtCompanyWorkSetPK other = (KscmtCompanyWorkSetPK) obj;
		if (cid == null) {
			if (other.cid != null)
				return false;
		} else if (!cid.equals(other.cid))
			return false;
		if (workdayDivision == null) {
			if (other.workdayDivision != null)
				return false;
		} else if (!workdayDivision.equals(other.workdayDivision))
			return false;
		return true;
	}

	public KscmtCompanyWorkSetPK(String cid, Integer workdayDivision) {
		super();
		this.cid = cid;
		this.workdayDivision = workdayDivision;
	}
	
	/**
	 * Instantiates a new kcbmt company work set PK.
	 */
	public KscmtCompanyWorkSetPK() {
		super();
	}
	
	
}
