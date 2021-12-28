package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 社員単価履歴
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSRMT_UNIT_PRICE_SYA")
public class KsrmtUnitPriceSya extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KsrmtUnitPriceSyaPk pk;
	
	/** 開始日 **/
	@Column(name = "START_DATE")
	public GeneralDate startDate;

	/** 終了日 **/
	@Column(name = "END_DATE")
	public GeneralDate endDate;
	
	@Override
	protected Object getKey() {
		return this.pk;
	}
}
