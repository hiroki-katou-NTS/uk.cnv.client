package nts.uk.ctx.at.schedule.infra.entity.budget.schedulevertical.verticalsetting;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KscstFormulaNumericalPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/*会社ID*/
	@Column(name = "CID")
	public String companyId;
	
	/* コード */
	@Column(name = "VERTICAL_CAL_CD")
	public String verticalCalCd;
	
	/* 汎用縦計項目ID */
	@Column(name = "VERTICAL_CAL_ITEM_ID")
	public String verticalCalItemId;
	
	/* 外部予算実績項目コード */
	@Column(name = "EXTERNAL_BUDGET_CD")
	public String externalBudgetCd;
}
