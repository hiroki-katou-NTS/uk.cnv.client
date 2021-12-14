package nts.uk.ctx.at.schedule.infra.entity.shift.specificdayset.workplace;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_SPEC_DATE_WKP")
public class KscmtSpecDateWkp extends ContractUkJpaEntity implements Serializable {


	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public KsmmtWpSpecDateSetPK ksmmtWpSpecDateSetPK;

	@Override
	protected Object getKey() {
		return ksmmtWpSpecDateSetPK;
	}
}

