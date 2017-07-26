/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.shift.pattern;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;

/**
 * The Class KwmmtWorkMonthSetPK.
 */

@Getter
@Setter
@Embeddable
public class KwmmtWorkMonthSetPK implements Serializable {

    /** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The cid. */
	@Basic(optional = false)
    @NotNull
    @Column(name = "CID")
    private String cid;
    
    /** The m pattern cd. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "M_PATTERN_CD")
    private String mPatternCd;
    
    /** The ymd K. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "YMD_K")
    @Convert(converter = GeneralDateToDBConverter.class)
    private GeneralDate ymdK;

    /**
     * Instantiates a new kwmmt work month set PK.
     */
    public KwmmtWorkMonthSetPK() {
    }

	/**
	 * Instantiates a new kwmmt work month set PK.
	 *
	 * @param cid the cid
	 * @param mPatternCd the m pattern cd
	 * @param ymdK the ymd K
	 */
	public KwmmtWorkMonthSetPK(String cid, String mPatternCd, GeneralDate ymdK) {
		super();
		this.cid = cid;
		this.mPatternCd = mPatternCd;
		this.ymdK = ymdK;
	}
    
}
