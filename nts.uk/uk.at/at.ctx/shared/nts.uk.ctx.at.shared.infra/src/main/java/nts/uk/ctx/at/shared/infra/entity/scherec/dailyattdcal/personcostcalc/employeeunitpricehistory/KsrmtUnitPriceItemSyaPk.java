package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KsrmtUnitPriceItemSyaPk implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 履歴ID **/
	@Column(name = "HIST_ID")
	public String histId;
	
	/** 社員時間単価NO */
	@Column(name = "UNIT_PRICE_NO")
	public int unitPriceNo;
}