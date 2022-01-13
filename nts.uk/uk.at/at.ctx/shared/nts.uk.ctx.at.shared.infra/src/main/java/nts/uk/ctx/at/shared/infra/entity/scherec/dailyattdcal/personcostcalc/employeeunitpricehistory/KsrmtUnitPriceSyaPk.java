package nts.uk.ctx.at.shared.infra.entity.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KsrmtUnitPriceSyaPk implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 社員ID **/
	@Column(name = "SID")
	public String sId;
	
	/** 履歴ID **/
	@Column(name = "HIST_ID")
	public String histId;
}
