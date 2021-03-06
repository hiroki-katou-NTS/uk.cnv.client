/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.scherec.totaltimes;

import java.io.Serializable;

//import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KshmtTotalSubjects.
 */
@Getter
@Setter
@Entity
@Table(name = "KSHMT_TOTAL_SUBJECTS")
public class KshmtTotalSubjects extends ContractUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kshst total subjects PK. */
	@EmbeddedId
	protected KshstTotalSubjectsPK kshstTotalSubjectsPK;



	/**
	 * Instantiates a new kshst total subjects.
	 */
	public KshmtTotalSubjects() {
		super();
	}

	/**
	 * Instantiates a new kshst total subjects.
	 *
	 * @param kshstTotalSubjectsPK
	 *            the kshst total subjects PK
	 */
	public KshmtTotalSubjects(KshstTotalSubjectsPK kshstTotalSubjectsPK) {
		this.kshstTotalSubjectsPK = kshstTotalSubjectsPK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (kshstTotalSubjectsPK != null ? kshstTotalSubjectsPK.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof KshmtTotalSubjects)) {
			return false;
		}
		KshmtTotalSubjects other = (KshmtTotalSubjects) object;
		if ((this.kshstTotalSubjectsPK == null && other.kshstTotalSubjectsPK != null)
				|| (this.kshstTotalSubjectsPK != null
						&& !this.kshstTotalSubjectsPK.equals(other.kshstTotalSubjectsPK))) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kshstTotalSubjectsPK;
	}

}
