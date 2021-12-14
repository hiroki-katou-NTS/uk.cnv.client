/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KshmtHdpubUnitSet.
 */
@Getter
@Setter
@Entity
@Table(name = "KSHMT_HDPUB_UNIT_SET")
public class KshmtHdpubUnitSet extends ContractUkJpaEntity implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The exclus ver. */
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    
    /** The cid. */
    @Id
    @Column(name = "CID")
    private String cid;
    
    /** The is manage S pub hd. */
    @Column(name = "IS_MANAGE_S_PUB_HD")
    private int isManageSPubHd;
   
    /** The is manage wkp pub hd. */
    @Column(name = "IS_MANAGE_WKP_PUB_HD")
    private int isManageWkpPubHd;
   
    /** The is manage emp pub hd. */
    @Column(name = "IS_MANAGE_EMP_PUB_HD")
    private int isManageEmpPubHd;

    /**
     * Instantiates a new kshmt pub hd mng unit set.
     */
    public KshmtHdpubUnitSet() {
    	super();
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof KshmtHdpubUnitSet)) {
            return false;
        }
        KshmtHdpubUnitSet other = (KshmtHdpubUnitSet) object;
        if ((this.cid == null && other.cid != null) || (this.cid != null && !this.cid.equals(other.cid))) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "entities.KshmtHdpubUnitSet[ cid=" + cid + " ]";
    }

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.cid;
	}
    
}
