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
public class KagmtCriterionMoneyCmpPk {
	/** 会社ID */
	@Column(name = "CID")
	public String companyId;

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
