package nts.uk.ctx.at.shared.infra.entity.specialholidaynew;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 対象の特別休暇枠
 * 
 * @author tanlv
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHST_SPHD_SPEC_LEAVE")
public class KshstSphdSpecLeave extends UkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/* 主キー */
	@EmbeddedId
	public KshstSphdSpecLeavePK pk;
	
	@Override
	protected Object getKey() {
		return pk;
	}
}
