package nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.budget.laborcost;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetAmount;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "	KAGDT_LABOR_COST_BUDGET_DAILY")

public class KagdtLaborCostBudgetDaily extends ContractCompanyUkJpaEntity  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KagdtLaborCostBudgetDailyPk pk;


	/** 人件費予算 **/
	@Column(name = "BUDGET")
	public int amount;

	@Override
	protected Object getKey() {
		return pk;
	}

	public static KagdtLaborCostBudgetDaily toEntity(LaborCostBudget laborCostBudget) {
		return new KagdtLaborCostBudgetDaily(
				new KagdtLaborCostBudgetDailyPk(laborCostBudget.getTargetOrg().getUnit().value,
						laborCostBudget.getTargetOrg().getTargetId(), laborCostBudget.getYmd()),
				laborCostBudget.getAmount().v()

		);
	}

	public LaborCostBudget toDomain() {
		String workplaceID = null;
		String workplaceGroupID = null;
		if (this.pk.targetUnit == 0) {
			workplaceID = this.pk.targetID;
		} else {
			workplaceGroupID = this.pk.targetID;
		}
		return new LaborCostBudget(

				new TargetOrgIdenInfor(
						EnumAdaptor.valueOf(this.pk.targetUnit, TargetOrganizationUnit.class), Optional.ofNullable(workplaceID) ,
						Optional.ofNullable(workplaceGroupID)),
				this.pk.ymd, new LaborCostBudgetAmount(amount));

	}
}
