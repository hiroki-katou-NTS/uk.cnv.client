/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.basic.infra.entity.company.organization.employment.history;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.infra.data.entity.type.GeneralDateToDBConverter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The Class KmnmtEmploymentHist.
 */
@Getter
@Setter
@Entity
@Table(name = "KMNMT_EMPLOYMENT_HIST")
public class KmnmtEmploymentHist extends UkJpaEntity implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The kmnmt employment hist PK. */
    @EmbeddedId
    protected KmnmtEmploymentHistPK kmnmtEmploymentHistPK;
    
    /** The str D. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "STR_D")
    @Convert(converter = GeneralDateToDBConverter.class)
    private GeneralDate strD;
    
    /** The end D. */
    @Basic(optional = false)
    @NotNull
    @Column(name = "END_D")
    @Convert(converter = GeneralDateToDBConverter.class)
    private GeneralDate endD;

    /**
     * Instantiates a new kmnmt employment hist.
     */
    public KmnmtEmploymentHist() {
    }

    /**
     * Instantiates a new kmnmt employment hist.
     *
     * @param kmnmtEmploymentHistPK the kmnmt employment hist PK
     */
    public KmnmtEmploymentHist(KmnmtEmploymentHistPK kmnmtEmploymentHistPK) {
        this.kmnmtEmploymentHistPK = kmnmtEmploymentHistPK;
    }

	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.getKmnmtEmploymentHistPK();
	}

    
}
