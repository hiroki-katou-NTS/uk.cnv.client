package nts.uk.ctx.at.aggregation.infra.repository.schedulecounter.budget.laborcost;

import java.util.List;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetRepository;
import nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.budget.laborcost.KagdtLaborCostBudgetDaily;
import nts.uk.ctx.at.aggregation.infra.entity.schedulecounter.budget.laborcost.KagdtLaborCostBudgetDailyPk;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

@Stateless
/**
 * 
 * @author hieult
 *
 */
public class JpaLaborCostBudgetRepository extends JpaRepository implements LaborCostBudgetRepository   {
	
	private static final String DELETE = "DELETE FROM KagdtLaborCostBudgetDaily c "
									   + "WHERE c.companyId = :companyId "
									   + "AND c.pk.targetUnit = :targetUnit "	
									   + "AND c.pk.targetID = :targetID "
									   + "AND c.pk.ymd = :ymd ";
	
	
	private static final String SELECT_BY_WORKPLACE = " SELECT c FROM KagdtLaborCostBudgetDaily c"
														+ " WHERE c.pk.targetUnit = :targetUnit "
														+ "AND c.pk.targetID = :id "	
														+ "AND c.companyId = :companyId "
														+ "AND c.pk.ymd >= :startDate "
														+ "AND c.pk.ymd <= :endDate";
	
	
	
	@Override
	public void insert(String companyId, LaborCostBudget domain) {
			this.commandProxy().insert(KagdtLaborCostBudgetDaily.toEntity(domain));
		
	}

	@Override
	public void delete(String companyId, TargetOrgIdenInfor targetOrg, GeneralDate ymd) {

		
	/*	if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE.value){
			this.commandProxy().remove(KagdtLaborCostBudgetDaily.class,
					new KagdtLaborCostBudgetDailyPk(0, targetOrg.getTargetId(), ymd)  );
		}else{
			this.commandProxy().remove(KagdtLaborCostBudgetDaily.class,
					new KagdtLaborCostBudgetDailyPk(1, targetOrg.getTargetId(), ymd)  );
			
		}*/
		this.getEntityManager().createQuery(DELETE, KagdtLaborCostBudgetDaily.class)
		.setParameter("companyId", companyId)
		.setParameter("targetUnit", targetOrg.getUnit().value)
		.setParameter("targetID", targetOrg.getTargetId())
		.setParameter("ymd", ymd)
		.executeUpdate();
		
	}

	@Override
	public List<LaborCostBudget> get(String companyId, TargetOrgIdenInfor targetOrg, DatePeriod period) {
		if(targetOrg.getUnit().value == TargetOrganizationUnit.WORKPLACE.value){
			int targetUnit = 0;
	
		List<LaborCostBudget> data = this.queryProxy().query(SELECT_BY_WORKPLACE ,KagdtLaborCostBudgetDaily.class )
				.setParameter("targetUnit", targetUnit)
				.setParameter("id", targetOrg.getTargetId())
				.setParameter("companyId", companyId)
				.setParameter("startDate",period.start())
				.setParameter("endDate",period.end())
				.getList(c -> c.toDomain());
		return data;
		}
		else{
			int targetUnit = 1;
			List<LaborCostBudget> data = this.queryProxy().query(SELECT_BY_WORKPLACE ,KagdtLaborCostBudgetDaily.class )
					.setParameter("targetUnit", targetUnit)
					.setParameter("id", targetOrg.getTargetId())
					.setParameter("companyId", companyId)
					.setParameter("startDate",period.start())
					.setParameter("endDate",period.end())
					.getList(c -> c.toDomain());
			return data;
		}
	}

}
