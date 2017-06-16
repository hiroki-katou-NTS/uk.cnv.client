/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.entity.workrecord.closure;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The Class KclmtClosureHistPK.
 */
@Getter
@Setter
@Embeddable
public class KclmtClosureHistPK implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cid. */
	@Basic(optional = false)
	@NotNull
	@Column(name = "CID")
	private String cid;
	
	/** The closure id. */
	@Basic(optional = false)
	@NotNull
	@Column(name = "CLOSURE_ID")
	private Integer closureId;
	
	
	/** The history id. */
	@Basic(optional = false)
	@NotNull
	@Column(name = "HIST_ID")
	private String histId;

	/**
	 * Instantiates a new kclmt closure hist PK.
	 */
	public KclmtClosureHistPK() {
	}

	/**
	 * Instantiates a new kclmt closure hist PK.
	 *
	 * @param ccid the ccid
	 * @param closureId the closure id
	 */
	public KclmtClosureHistPK(String cid, Integer closureId, String histId) {
		this.cid = cid;
		this.closureId = closureId;
		this.histId = histId;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (cid != null ? cid.hashCode() : 0);
		hash += (closureId != null ? closureId.hashCode() : 0);
		return hash;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof KclmtClosureHistPK)) {
			return false;
		}
		KclmtClosureHistPK other = (KclmtClosureHistPK) object;
		if ((this.cid == null && other.cid != null)
			|| (this.cid != null && !this.cid.equals(other.cid))) {
			return false;
		}
		if ((this.closureId == null && other.closureId != null)
			|| (this.closureId != null && !this.closureId.equals(other.closureId))) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "entity.KclmtClosureHistPK[ cid=" + cid + ", closureId=" + closureId + " ]";
	}
	

}
