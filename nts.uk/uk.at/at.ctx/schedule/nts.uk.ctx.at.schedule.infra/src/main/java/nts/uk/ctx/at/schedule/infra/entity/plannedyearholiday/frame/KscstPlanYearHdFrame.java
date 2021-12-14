/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/

package nts.uk.ctx.at.schedule.infra.entity.plannedyearholiday.frame;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KscstPlanYearHdFrame.
 */
@Getter
@Setter
@Entity
@Table(name = "KSCST_PLAN_YEAR_HD_FRAME")

public class KscstPlanYearHdFrame extends ContractUkJpaEntity implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The kscst plan year hd frame PK. */
    @EmbeddedId
    protected KscstPlanYearHdFramePK kscstPlanYearHdFramePK;
    
    /** The exclus ver. */
    @Column(name = "EXCLUS_VER")
    private int exclusVer;
    
    /** The use atr. */
    @Column(name = "USE_ATR")
    private short useAtr;
    
    /** The plan year hd name. */
    @Column(name = "PLAN_YEAR_HD_NAME")
    private String planYearHdName;

    /**
     * Instantiates a new kscst plan year hd frame.
     */
    public KscstPlanYearHdFrame() {
    	super();
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kscstPlanYearHdFramePK != null ? kscstPlanYearHdFramePK.hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof KscstPlanYearHdFrame)) {
            return false;
        }
        KscstPlanYearHdFrame other = (KscstPlanYearHdFrame) object;
        if ((this.kscstPlanYearHdFramePK == null && other.kscstPlanYearHdFramePK != null) || (this.kscstPlanYearHdFramePK != null && !this.kscstPlanYearHdFramePK.equals(other.kscstPlanYearHdFramePK))) {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
     */
    @Override
	protected Object getKey() {
		return this.kscstPlanYearHdFramePK;
	}
}
