/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nts.uk.ctx.at.shared.infra.entity.holidaysetting.configuration;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;


/**
 * The Class Kshmt4w4dNumSet.
 */
@Getter
@Setter
@Entity
@Table(name = "KSHMT_4W4D_NUM_SET")
public class Kshmt4w4dNumSet extends ContractUkJpaEntity implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The exclus ver. */
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    
    /** The cid. */
    @Id
    @Column(name = "CID")
    private String cid;
    
    /** The is one week hd. */
    @Column(name = "IS_ONE_WEEK_HD")
    private int isOneWeekHd;
   
    /** The in legel hd fwph. */
    @Column(name = "IN_LEGEL_HD_FWPH")
    private BigDecimal inLegelHdFwph;
   
    /** The out legal hd fwph. */
    @Column(name = "OUT_LEGAL_HD_FWPH")
    private BigDecimal outLegalHdFwph;
   
    /** The in legal hd lwhnofw. */
    @Column(name = "IN_LEGAL_HD_LWHNOFW")
    private BigDecimal inLegalHdLwhnofw;
   
    /** The out legal hd lwhnofw. */
    @Column(name = "OUT_LEGAL_HD_LWHNOFW")
    private BigDecimal outLegalHdLwhnofw;
   
    /** The is four week hd. */
    @Column(name = "IS_FOUR_WEEK_HD")
    private int isFourWeekHd;
    
    /** The in legal hd owph. */
    @Column(name = "IN_LEGAL_HD_OWPH")
    private BigDecimal inLegalHdOwph;
   
    /** The out legal hd owph. */
    @Column(name = "OUT_LEGAL_HD_OWPH")
    private BigDecimal outLegalHdOwph;
    
    /** The in legal hd lwhnoow. */
    @Column(name = "IN_LEGAL_HD_LWHNOOW")
    private BigDecimal inLegalHdLwhnoow;
   
    /** The out legal hd lwhnoow. */
    @Column(name = "OUT_LEGAL_HD_LWHNOOW")
    private BigDecimal outLegalHdLwhnoow;

    /**
     * Instantiates a new kshmt fourweekfour hd numb set.
     */
    public Kshmt4w4dNumSet() {
    	super();
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cid != null ? cid.hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Kshmt4w4dNumSet)) {
            return false;
        }
        Kshmt4w4dNumSet other = (Kshmt4w4dNumSet) object;
        if ((this.cid == null && other.cid != null) || (this.cid != null && !this.cid.equals(other.cid))) {
            return false;
        }
        return true;
    }

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.cid;
	}
    
}
