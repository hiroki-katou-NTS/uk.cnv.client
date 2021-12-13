package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.criterion;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author TU-TK
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KagmtCriterionMoneyEmpPk {
	/** 会社ID */
	@Column(name = "CID")
	public String companyId;
	
	/**
	 * 	目安金額枠NO
	 */
	@Column(name = "EMPLOYEMENT_CD")
	public String employmentCode;

	/**
	 * 	0：年間目安金額  1：月間目安金額
	 */
	@Column(name = "YEAR_MONTH_ATR")
	public int yearMonthAtr;
	
	/**
	 * 	目安金額枠NO
	 */
	@Column(name = "FRAME_NO")
	public int frameNo;
}
