package nts.uk.ctx.at.record.infra.entity.workrecord.operationsetting;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class KrcmtChangeableWktpGrpDetailPk {

	@Column(name = "CID")
	public String cid;

	@Column(name = "EMP_CD")
	public String empCd;

	@Column(name = "WORKTYPE_GROUP_NO")
	public BigDecimal workTypeGroupNo;

	@Column(name = "WORKTYPE_CD")
	public String workTypeCd;

	public KrcmtChangeableWktpGrpDetailPk() {
		super();
	}

	public KrcmtChangeableWktpGrpDetailPk(String cid, String empCd, BigDecimal workTypeGroupNo, String workTypeCd) {
		super();
		this.cid = cid;
		this.empCd = empCd;
		this.workTypeGroupNo = workTypeGroupNo;
		this.workTypeCd = workTypeCd;
	}
}
