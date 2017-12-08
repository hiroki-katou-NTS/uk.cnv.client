/**
 * 5:25:32 PM Dec 5, 2017
 */
package nts.uk.ctx.at.record.infra.entity.workrecord.erroralarm.condition.attendanceitem;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * @author hungnm
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCST_ER_AL_ATD_TARGET")
public class KrcstErAlAtdTarget extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
    public KrcstErAlAtdTargetPK krcstErAlAtdTargetPK;
	
	@Basic(optional = false)
    @NotNull
    @Column(name = "TARGET_ATR")
    public BigDecimal targetAtr;

	@Override
	protected Object getKey() {
		return this.krcstErAlAtdTargetPK;
	}

}
