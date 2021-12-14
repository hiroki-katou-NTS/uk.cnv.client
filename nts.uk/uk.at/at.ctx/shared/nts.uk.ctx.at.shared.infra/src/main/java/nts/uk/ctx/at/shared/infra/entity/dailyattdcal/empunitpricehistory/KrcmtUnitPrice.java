package nts.uk.ctx.at.shared.infra.entity.dailyattdcal.empunitpricehistory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;
/**
 * @author laitv
 * 社員単価履歴
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSRMT_UNIT_PRICE_SYA")
public class KrcmtUnitPrice  extends ContractUkJpaEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/** 主キー */
	@EmbeddedId
	public KrcmtUnitPricePK pk;
	
	/** 会社ID */
	@Column(name = "会社ID")
	public String cid;
	
	/** 開始日 */
	@Column(name = "START_DATE")
	public GeneralDate startDate;
	
	/** 終了日 */
	@Column(name = "END_DATE")
	public GeneralDate endDate;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public KrcmtUnitPrice(String sid, String hisId, String cid, GeneralDate startDate, GeneralDate endDate) {
		super();
		this.pk = new KrcmtUnitPricePK(sid, hisId);
		this.cid = cid;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
