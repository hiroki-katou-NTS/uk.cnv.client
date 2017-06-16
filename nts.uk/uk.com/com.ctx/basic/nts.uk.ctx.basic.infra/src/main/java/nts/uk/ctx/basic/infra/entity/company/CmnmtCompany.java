/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.basic.infra.entity.company;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The Class CmnmtCompany.
 */

@Getter
@Setter
@Entity
@Table(name = "CMNMT_COMPANY")
public class CmnmtCompany extends UkJpaEntity implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The ccd. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "CCD")
    private String ccd;
    
    /** The cid. */
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "CID")
    private String cid;
    
    /** The str M. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "STR_M")
    private Integer strM;

    /**
     * Instantiates a new cmnmt company.
     */
    public CmnmtCompany() {
    }

    /**
     * Instantiates a new cmnmt company.
     *
     * @param cid the cid
     */
    public CmnmtCompany(String cid) {
        this.cid = cid;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (cid != null ? cid.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof CmnmtCompany)) {
			return false;
		}
		CmnmtCompany other = (CmnmtCompany) object;
		if ((this.cid == null && other.cid != null)
				|| (this.cid != null && !this.cid.equals(other.cid))) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "entity.CmnmtCompany[ cid=" + cid + " ]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.getCid();
	}
    
}
