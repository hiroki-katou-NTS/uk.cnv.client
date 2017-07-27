/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.infra.entity.shift.pattern;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.infra.data.entity.JpaEntity;

/**
 * The Class KmpmtMonthPattern.
 */
@Getter
@Setter
@Entity
@Table(name = "KMPMT_MONTH_PATTERN")
public class KmpmtMonthPattern extends JpaEntity implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The kmpmt month pattern PK. */
    @EmbeddedId
    protected KmpmtMonthPatternPK kmpmtMonthPatternPK;
    
    /** The m pattern name. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "M_PATTERN_NAME")
    private String mPatternName;

    /**
     * Instantiates a new kmpmt month pattern.
     */
    public KmpmtMonthPattern() {
    	super();
    }

    public KmpmtMonthPattern(KmpmtMonthPatternPK kmpmtMonthPatternPK) {
        this.kmpmtMonthPatternPK = kmpmtMonthPatternPK;
    }

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kmpmtMonthPatternPK;
	}

    
}
