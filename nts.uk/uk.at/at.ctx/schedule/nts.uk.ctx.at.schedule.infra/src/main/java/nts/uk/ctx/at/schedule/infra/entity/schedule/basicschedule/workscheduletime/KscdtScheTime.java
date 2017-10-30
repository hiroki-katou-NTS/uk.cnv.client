/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.schedule.basicschedule.workscheduletime;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The Class KscdtScheTime.
 */
@Entity
@Table(name = "KSCDT_SCHE_TIME")
public class KscdtScheTime implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The kscmt work sch time PK. */
    @EmbeddedId
    protected KscdtScheTimePK kscdtScheTimePK;
    
    /** The break time. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "BREAK_TIME")
    private int breakTime;
    
    /** The working time. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "WORKING_TIME")
    private int workingTime;
    
    /** The weekday time. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "WEEKDAY_TIME")
    private int weekdayTime;
    
    /** The prescribed time. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "PRESCRIBED_TIME")
    private int prescribedTime;
    
    /** The total labor time. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "TOTAL_LABOR_TIME")
    private int totalLaborTime;
    
    /** The child care time. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "CHILD_CARE_TIME")
    private int childCareTime;

    /**
     * Instantiates a new kscmt work sch time.
     */
    public KscdtScheTime() {
    }

    /**
     * Instantiates a new kscmt work sch time.
     *
     * @param kscmtWorkSchTimePK the kscmt work sch time PK
     */
    public KscdtScheTime(KscdtScheTimePK kscmtWorkSchTimePK) {
        this.kscdtScheTimePK = kscmtWorkSchTimePK;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kscdtScheTimePK != null ? kscdtScheTimePK.hashCode() : 0);
        return hash;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof KscdtScheTime)) {
            return false;
        }
        KscdtScheTime other = (KscdtScheTime) object;
        if ((this.kscdtScheTimePK == null && other.kscdtScheTimePK != null) || (this.kscdtScheTimePK != null && !this.kscdtScheTimePK.equals(other.kscdtScheTimePK))) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "entity.KscmtWorkSchTime[ kscmtWorkSchTimePK=" + kscdtScheTimePK + " ]";
    }
    
    
}
