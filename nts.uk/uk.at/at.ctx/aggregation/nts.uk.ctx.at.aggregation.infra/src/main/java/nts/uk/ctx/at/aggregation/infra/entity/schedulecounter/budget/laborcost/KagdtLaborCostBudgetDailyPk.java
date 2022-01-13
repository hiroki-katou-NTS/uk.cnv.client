package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.budget.laborcost;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * 
 * @author Hieult
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KagdtLaborCostBudgetDailyPk implements Serializable {
	

	private static final long serialVersionUID = 1L;

	/** 対象組織の単位 **/
	@NotNull
	@Column(name = "TARGET_UNIT")
	public int targetUnit;
	
	/** 対象組織の単位に応じたID **/
	@NotNull
	@Column(name = "TARGET_ID")
	public String targetID;
	
	/** **/
	@NotNull
	@Column( name = "YMD")
	public GeneralDate ymd;
	
}
