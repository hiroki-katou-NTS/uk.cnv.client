/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.infra.entity.optitem;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.record.infra.repository.optitem.JpaCalcResultRangeGetMemento;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CalcResultRange;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KrcstCalcResultRange.
 */
@Getter
@Setter
@Entity
@Table(name = "KRCMT_ANYF_RESULT_RANGE")
public class KrcmtAnyfResultRange extends ContractUkJpaEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The krcst calc result range PK. */
	@EmbeddedId
	protected KrcmtCalcResultRangePK krcstCalcResultRangePK;

	/** The upper limit atr. */
	@Column(name = "UPPER_LIMIT_ATR")
	private boolean upperLimitAtr;

	/** The lower limit atr. */
	@Column(name = "LOWER_LIMIT_ATR")
	private boolean lowerLimitAtr;

//	/** The upper time range. */
//	@Column(name = "UPPER_TIME_RANGE")
//	private Integer upperTimeRange;
//
//	/** The lower time range. */
//	@Column(name = "LOWER_TIME_RANGE")
//	private Integer lowerTimeRange;
//
//	/** The upper number range. */
//	@Column(name = "UPPER_NUMBER_RANGE")
//	private Double upperNumberRange;
//
//	/** The lower number range. */
//	@Column(name = "LOWER_NUMBER_RANGE")
//	private Double lowerNumberRange;
//
//	/** The upper amount range. */
//	@Column(name = "UPPER_AMOUNT_RANGE")
//	private Integer upperAmountRange;
//
//	/** The lower amount range. */
//	@Column(name = "LOWER_AMOUNT_RANGE")
//	private Integer lowerAmountRange;
	
	@Column(name = "UPPER_DAY_TIME_RANGE")
	private Integer upperDayTimeRange;
	
	@Column(name = "LOWER_DAY_TIME_RANGE")
	private Integer lowerDayTimeRange;
	
	@Column(name = "UPPER_DAY_NUMBER_RANGE")
	private BigDecimal upperDayNumberRange;
	
	@Column(name = "LOWER_DAY_NUMBER_RANGE")
	private BigDecimal lowerDayNumberRange;
	
	@Column(name = "UPPER_DAY_AMOUNT_RANGE")
	private Integer upperdayAmountRange;
	
	@Column(name = "LOWER_DAY_AMOUNT_RANGE")
	private Integer lowerDayAmountRange;
	
	@Column(name = "UPPER_MON_TIME_RANGE")
	private Integer upperMonTimeRange;
	
	@Column(name = "LOWER_MON_TIME_RANGE")
	private Integer lowerMonTimeRange;
	
	@Column(name = "UPPER_MON_NUMBER_RANGE")
	private BigDecimal upperMonNumberRange;
	
	@Column(name = "LOWER_MON_NUMBER_RANGE")
	private BigDecimal lowerMonNumberRange;
	
	@Column(name = "UPPER_MON_AMOUNT_RANGE")
	private Integer upperMonAmountRange;
	
	@Column(name = "LOWER_MON_AMOUNT_RANGE")
	private Integer lowerMonAmountRange;

	/**
	 * Instantiates a new krcst calc result range.
	 */
	public KrcmtAnyfResultRange() {
	}

	/**
	 * Instantiates a new krcst calc result range.
	 *
	 * @param krcstCalcResultRangePK
	 *            the krcst calc result range PK
	 */
	public KrcmtAnyfResultRange(KrcmtCalcResultRangePK krcstCalcResultRangePK) {
		this.krcstCalcResultRangePK = krcstCalcResultRangePK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (krcstCalcResultRangePK != null ? krcstCalcResultRangePK.hashCode() : 0);
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof KrcmtAnyfResultRange)) {
			return false;
		}
		KrcmtAnyfResultRange other = (KrcmtAnyfResultRange) object;
		if ((this.krcstCalcResultRangePK == null && other.krcstCalcResultRangePK != null)
				|| (this.krcstCalcResultRangePK != null
						&& !this.krcstCalcResultRangePK.equals(other.krcstCalcResultRangePK))) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	@Override
	protected Object getKey() {
		return this.krcstCalcResultRangePK;
	}
	
	public CalcResultRange toDomain() {
	    return new CalcResultRange(new JpaCalcResultRangeGetMemento(this));
	}
}
