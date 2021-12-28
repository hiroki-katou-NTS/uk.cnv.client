/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.entity.optitem;

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
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KrcmtAnyv.
 */
@Getter
@Setter
@Entity
@Table(name = "KRCMT_ANYV")
public class KrcmtAnyv extends ContractUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The krcst optional item PK. */
	@EmbeddedId
	protected KrcmtAnyvPK krcmtAnyvPK;

	/** The optional item name. */
	@Column(name = "OPTIONAL_ITEM_NAME")
	private String optionalItemName;

	/** The optional item atr. */
	@Column(name = "OPTIONAL_ITEM_ATR")
	private int optionalItemAtr;

	/** The usage atr. */
	@Column(name = "USAGE_ATR")
	private int usageAtr;

	/** The calculation atr. */
	@Column(name = "CALC_ATR")
	private int calcAtr;

	/** The performance atr. */
	@Column(name = "PERFORMANCE_ATR")
	private int performanceAtr;

	/** The emp condition atr. */
	@Column(name = "EMP_CONDITION_ATR")
	private int empConditionAtr;

	/** The unit of optional item. */
	@Column(name = "UNIT_OF_OPTIONAL_ITEM")
	private String unitOfOptionalItem;
	
	/** The description */
	@Column(name = "ITEM_DESCRIP")
	private String description;
	
	/** The note */
	@Column(name = "ITEM_NOTE")
	private String note;

	// INPUT_WITH_CHECKBOX
	@Column(name = "INPUT_WITH_CHECKBOX")
	private boolean inputCheck;

	/** The krcst calc result range. */
	@OneToOne(optional = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "CID", referencedColumnName = "CID"),
			@PrimaryKeyJoinColumn(name = "OPTIONAL_ITEM_NO", referencedColumnName = "OPTIONAL_ITEM_NO") })
	public KrcmtAnyfResultRange krcstCalcResultRange;
	
	/**
	 * Instantiates a new krcst optional item.
	 */
	public KrcmtAnyv() {
		super();
	}

	/**
	 * Instantiates a new krcst optional item.
	 *
	 * @param krcstOptionalItemPK
	 *            the krcst optional item PK
	 */
	public KrcmtAnyv(KrcmtAnyvPK krcstOptionalItemPK) {
		this.krcmtAnyvPK = krcstOptionalItemPK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (krcmtAnyvPK != null ? krcmtAnyvPK.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof KrcmtAnyv)) {
			return false;
		}
		KrcmtAnyv other = (KrcmtAnyv) object;
		if ((this.krcmtAnyvPK == null && other.krcmtAnyvPK != null)
				|| (this.krcmtAnyvPK != null
						&& !this.krcmtAnyvPK.equals(other.krcmtAnyvPK))) {
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
		return this.krcmtAnyvPK;
	}

}
