/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.shift.basicworkregister;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KscmtBasicWorkCom.
 */
@Entity
@Setter
@Getter
@Table(name = "KSCMT_STANDARD_WORK_CMP")
public class KscmtBasicWorkCom extends ContractUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kcbmt company work set PK. */
	@EmbeddedId
	protected KscmtCompanyWorkSetPK kscmtCompanyWorkSetPK;

	/** The worktype code. */
	@Column(name = "WKTP_CD")
	private String worktypeCode;
	
	/** The working code. */
	@Column(name = "WKTM_CD")
	private String workingCode;



	

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kscmtCompanyWorkSetPK;
	}



	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((kscmtCompanyWorkSetPK == null) ? 0 : kscmtCompanyWorkSetPK.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		KscmtBasicWorkCom other = (KscmtBasicWorkCom) obj;
		if (kscmtCompanyWorkSetPK == null) {
			if (other.kscmtCompanyWorkSetPK != null)
				return false;
		} else if (!kscmtCompanyWorkSetPK.equals(other.kscmtCompanyWorkSetPK))
			return false;
		return true;
	}

}
