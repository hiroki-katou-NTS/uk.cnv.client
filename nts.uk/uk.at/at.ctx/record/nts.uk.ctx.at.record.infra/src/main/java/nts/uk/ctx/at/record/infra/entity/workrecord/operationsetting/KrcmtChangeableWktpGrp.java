package nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * @author huylq
 *
 */
@Entity
@Table(name = "KRCMT_CHANGEABLE_WKTP_GRP")
public class KrcmtChangeableWktpGrp extends ContractUkJpaEntity {

	@EmbeddedId
	public KrcmtChangeableWktpGrpPk pk;

	@Column(name = "WORKTYPE_GROUP_NAME")
	public String workTypeGroupName;

	public KrcmtChangeableWktpGrp() {
		super();
	}

	public KrcmtChangeableWktpGrp(KrcmtChangeableWktpGrpPk pk, String workTypeGroupName) {
		super();
		this.pk = pk;
		this.workTypeGroupName = workTypeGroupName;
	}

	@Override
	protected Object getKey() {
		return pk;
	}
}
