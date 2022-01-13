package nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * @author huylq
 *
 */
@Entity
@Table(name = "KRCMT_CHANGEABLE_WKTP_GRP_DETAIL")
public class KrcmtChangeableWktpGrpDetail extends ContractUkJpaEntity {

	@EmbeddedId
	public KrcmtChangeableWktpGrpDetailPk pk;

	public KrcmtChangeableWktpGrpDetail() {
		super();
	}

	public KrcmtChangeableWktpGrpDetail(KrcmtChangeableWktpGrpDetailPk pk) {
		super();
		this.pk = pk;
	}

	@Override
	protected Object getKey() {
		return pk;
	}
}
