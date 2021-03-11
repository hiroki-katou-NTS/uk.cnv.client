package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Embeddable
public class KsrmtUnitPriceItemSyaPk implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 履歴ID **/
	@Column(name = "HIST_ID")
	private String histId;
	
	/** 社員時間単価NO */
	@Column(name = "UNIT_PRICE_NO")
	public String unitPriceNo;
}