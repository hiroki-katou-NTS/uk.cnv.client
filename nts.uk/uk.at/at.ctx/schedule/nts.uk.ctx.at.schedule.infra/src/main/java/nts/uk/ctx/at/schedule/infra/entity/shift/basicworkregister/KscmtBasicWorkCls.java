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

@Entity
@Setter
@Getter
@Table(name = "KSCMT_STANDARD_WORK_CLS")
public class KscmtBasicWorkCls extends ContractUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kcbmt classify work set PK. */
	@EmbeddedId
	protected KscmtClassifyWorkSetPK kscmtClassifyWorkSetPK;

	/** The wd work type code. */
	@Column(name = "WKTP_CD")
	private String worktypeCode;

	/** The wd working code. */
	@Column(name = "WKTM_CD")
	private String workingCode;

	

	

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kscmtClassifyWorkSetPK;
	}

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((kscmtClassifyWorkSetPK == null) ? 0 : kscmtClassifyWorkSetPK.hashCode());
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
		KscmtBasicWorkCls other = (KscmtBasicWorkCls) obj;
		if (kscmtClassifyWorkSetPK == null) {
			if (other.kscmtClassifyWorkSetPK != null)
				return false;
		} else if (!kscmtClassifyWorkSetPK.equals(other.kscmtClassifyWorkSetPK))
			return false;
		return true;
	}

}
