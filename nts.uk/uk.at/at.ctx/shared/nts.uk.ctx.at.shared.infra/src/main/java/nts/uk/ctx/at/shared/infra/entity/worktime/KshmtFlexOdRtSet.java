/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.worktime;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The Class KshmtFlexOdRtSet.
 */
@Getter
@Setter
@Entity
@Table(name = "KSHMT_FLEX_OD_RT_SET")
public class KshmtFlexOdRtSet extends UkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kshmt flex od rt set PK. */
	@EmbeddedId
	protected KshmtFlexOdRtSetPK kshmtFlexOdRtSetPK;

	/** The exclus ver. */
	@Column(name = "EXCLUS_VER")
	private int exclusVer;

	/** The fix rest time. */
	@Column(name = "FIX_REST_TIME")
	private int fixRestTime;

	/** The use rest after set. */
	@Column(name = "USE_REST_AFTER_SET")
	private int useRestAfterSet;

	/** The after rest time. */
	@Column(name = "AFTER_REST_TIME")
	private int afterRestTime;

	/** The after passage time. */
	@Column(name = "AFTER_PASSAGE_TIME")
	private int afterPassageTime;

	/**
	 * Instantiates a new kshmt flex od rt set.
	 */
	public KshmtFlexOdRtSet() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (kshmtFlexOdRtSetPK != null ? kshmtFlexOdRtSetPK.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof KshmtFlexOdRtSet)) {
			return false;
		}
		KshmtFlexOdRtSet other = (KshmtFlexOdRtSet) object;
		if ((this.kshmtFlexOdRtSetPK == null && other.kshmtFlexOdRtSetPK != null)
				|| (this.kshmtFlexOdRtSetPK != null
						&& !this.kshmtFlexOdRtSetPK.equals(other.kshmtFlexOdRtSetPK))) {
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
		return this.kshmtFlexOdRtSetPK;
	}
	
	/** The kshmt flex od fix rest. */
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "CID", referencedColumnName = "CID"),
			@PrimaryKeyJoinColumn(name = "WORKTIME_CD", referencedColumnName = "WORKTIME_CD") })
	private KshmtFlexOdFixRest kshmtFlexOdFixRest;

}
