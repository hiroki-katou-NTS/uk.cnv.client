package nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class KrcmtChangeableWktpGrpPk {

	@Column(name = "CID")
	public String cid;

	@Column(name = "EMP_CD")
	public String empCd;

	@Column(name = "WORKTYPE_GROUP_NO")
	public BigDecimal workTypeGroupNo;

	public KrcmtChangeableWktpGrpPk() {
		super();
	}

	public KrcmtChangeableWktpGrpPk(String cid, String empCd, BigDecimal workTypeGroupNo) {
		super();
		this.cid = cid;
		this.empCd = empCd;
		this.workTypeGroupNo = workTypeGroupNo;
	}
}
