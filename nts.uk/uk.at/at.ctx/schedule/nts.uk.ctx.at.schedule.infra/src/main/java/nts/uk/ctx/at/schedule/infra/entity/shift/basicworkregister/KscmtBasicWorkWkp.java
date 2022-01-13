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
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * The Class KscmtBasicWorkWkp.
 */
@Entity
@Setter
@Getter
@Table(name = "KSCMT_STANDARD_WORK_WKP")
public class KscmtBasicWorkWkp extends ContractCompanyUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kwbmt workplace work set PK. */
	@EmbeddedId
	protected KscmtWorkplaceWorkSetPK kscmtWorkplaceWorkSetPK;
	
	/** The worktype code. */
	@Column(name = "WKTP_CD")
	private String worktypeCode;

	/** The working code. */
	@Column(name = "WKTM_CD")
	private String workingCode;


	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kscmtWorkplaceWorkSetPK;
	}


	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((kscmtWorkplaceWorkSetPK == null) ? 0 : kscmtWorkplaceWorkSetPK.hashCode());
		result = prime * result + ((workingCode == null) ? 0 : workingCode.hashCode());
		result = prime * result + ((worktypeCode == null) ? 0 : worktypeCode.hashCode());
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
		KscmtBasicWorkWkp other = (KscmtBasicWorkWkp) obj;
		if (kscmtWorkplaceWorkSetPK == null) {
			if (other.kscmtWorkplaceWorkSetPK != null)
				return false;
		} else if (!kscmtWorkplaceWorkSetPK.equals(other.kscmtWorkplaceWorkSetPK))
			return false;
		if (workingCode == null) {
			if (other.workingCode != null)
				return false;
		} else if (!workingCode.equals(other.workingCode))
			return false;
		if (worktypeCode == null) {
			if (other.worktypeCode != null)
				return false;
		} else if (!worktypeCode.equals(other.worktypeCode))
			return false;
		return true;
	}

	
	

}
