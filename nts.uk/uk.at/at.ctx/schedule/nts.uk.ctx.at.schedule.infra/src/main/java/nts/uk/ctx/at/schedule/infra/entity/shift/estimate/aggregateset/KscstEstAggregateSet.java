/******************************************************************
 * Copyright (c) 2015 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.shift.estimate.aggregateset;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The Class KscstEstAggregateSet.
 */
@Setter
@Getter
@Entity
@Table(name = "KSCST_EST_AGGREGATE_SET")
public class KscstEstAggregateSet extends UkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cid. */
	@Id
    @Column(name = "CID")
    private String cid;
	
    /** The half day atr. */
    @Column(name = "HALF_DAY_ATR")
    private int halfDayAtr;
    
    /** The year hd atr. */
    @Column(name = "YEAR_HD_ATR")
    private int yearHdAtr;
    
    /** The sphd atr. */
    @Column(name = "SPHD_ATR")
    private int sphdAtr;
    
    /** The havy hd atr. */
    @Column(name = "HAVY_HD_ATR")
    private int havyHdAtr;
    
    /** The kscst per cost extra item. */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kscstEstAggregateSet", orphanRemoval = true)
	public List<KscstPerCostExtraItem> kscstPerCostExtraItem;

    /**
     * Instantiates a new kscst est aggregate set.
     */
    public KscstEstAggregateSet() {
    }

    /**
     * Instantiates a new kscst est aggregate set.
     *
     * @param cid the cid
     */
    public KscstEstAggregateSet(String cid) {
        this.cid = cid;
    }

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.cid;
	}

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cid == null) ? 0 : cid.hashCode());
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
		KscstEstAggregateSet other = (KscstEstAggregateSet) obj;
		if (cid == null) {
			if (other.cid != null)
				return false;
		} else if (!cid.equals(other.cid))
			return false;
		return true;
	}

	
	
}
