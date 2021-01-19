package nts.uk.ctx.at.record.infra.entity.divergence.reason;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.record.infra.entity.divergence.time.KrcstDvgcTime;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * The persistent class for the KRCST_DVGC_REASON database table.
 * 
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "KRCST_DVGC_REASON")
public class KrcstDvgcReason extends UkJpaEntity implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private KrcstDvgcReasonPK id;

	/** The reason. */
	@Column(name = "REASON")
	private String reason;

	/** The reason required. */
	@Column(name = "REASON_REQUIRED")
	private BigDecimal reasonRequired;
	
	@ManyToOne
    @PrimaryKeyJoinColumns({
    	@PrimaryKeyJoinColumn(name="CID", referencedColumnName="CID"),
    	@PrimaryKeyJoinColumn(name="[NO]", referencedColumnName="[NO]")
    })
	public KrcstDvgcTime krcstDvgcTime;
	/* (non-Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.id;
	}
}