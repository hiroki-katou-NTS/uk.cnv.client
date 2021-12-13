/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.vacation.setting.annualpaidleave;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KtvmtTimeVacationSet.
 */
@Setter
@Getter
@Entity
@Table(name = "KSHMT_HDPAID_TIME_SET")
public class KshmtHdpaidTimeSet extends ContractUkJpaEntity implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The cid. */
    @Id
    @Basic(optional = false)
    @Column(name = "CID")
    private String cid;

    /** The time manage atr. */
    @Column(name = "TIME_MANAGE_ATR")
    private Integer timeManageAtr;

    /** The time unit. */
    @Column(name = "TIME_UNIT")
    private Integer timeUnit;

    /** The time max day manage atr. */
    @Column(name = "TIME_MAX_DAY_MANAGE_ATR")
    private Integer timeMaxDayManageAtr;

    /** The time max day reference. */
    @Column(name = "TIME_MAX_DAY_REFERENCE")
    private Integer timeMaxDayReference;

    /** The time max day unif comp. */
    @Column(name = "TIME_MAX_DAY_UNIF_COMP")
    private Integer timeMaxDayUnifComp;

    /** The Round Processing Classification. */
    @Column(name = "ROUND_PROC_CLA")
    private boolean roundProcessCla;

    /** 1日の時間年休時間参照先**/
    @Column(name = "TIME_OF_DAY_REFERENCE")
    private int timeOfDayRef;
    
    /** 全社一律の時間 **/
    @Column (name = "UNIFORM_TIME")
    private Integer uniformTime;
    
    /** 契約時間丸め **/
    @Column(name = "CONTRACT_TIME_ROUND")
    private Integer contractTimeRound;
   
    /**
     * Instantiates a new ktvmt time vacation set.
     */
    public KshmtHdpaidTimeSet() {
    }

    /**
     * Instantiates a new ktvmt time vacation set.
     *
     * @param cid the cid
     */
    public KshmtHdpaidTimeSet(String cid) {
        this.cid = cid;
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
        if (!(object instanceof KshmtHdpaidTimeSet)) {
            return false;
        }
        KshmtHdpaidTimeSet other = (KshmtHdpaidTimeSet) object;
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
