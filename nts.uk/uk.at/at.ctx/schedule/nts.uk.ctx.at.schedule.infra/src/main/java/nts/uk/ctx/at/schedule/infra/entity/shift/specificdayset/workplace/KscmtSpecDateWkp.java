package nts.uk.ctx.at.schedule.infra.entity.shift.specificdayset.workplace;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSCMT_SPECIFIC_DATE_WKP")
public class KscmtSpecDateWkp extends ContractCompanyUkJpaEntity implements Serializable {


	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public KsmmtWpSpecDateSetPK ksmmtWpSpecDateSetPK;

	@Override
	protected Object getKey() {
		return ksmmtWpSpecDateSetPK;
	}
}

