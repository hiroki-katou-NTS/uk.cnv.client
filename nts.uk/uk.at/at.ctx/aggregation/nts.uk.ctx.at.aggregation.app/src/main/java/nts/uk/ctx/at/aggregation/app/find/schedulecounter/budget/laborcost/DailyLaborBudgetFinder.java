package nts.uk.ctx.at.aggregation.app.find.schedulecounter.budget.laborcost;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author HieuLt
 *
 */
/** 日次の人件費予算を取得する **/
@Stateless
public class DailyLaborBudgetFinder {
	
	@Inject
	private LaborCostBudgetRepository repo;

	public List<DailyLaborBudgetDto>  getDailyLaborBudget(int unit , GeneralDate startDate  , GeneralDate  endDate, String targetID){
		String companyId = AppContexts.user().companyId();
		//職場を指定して識別情報を作成する(職場ID)
		TargetOrgIdenInfor targetOrg = 	null;
		if (unit == TargetOrganizationUnit.WORKPLACE.value ){
			targetOrg = TargetOrgIdenInfor.creatIdentifiWorkplace(targetID);
		}
		else{
			targetOrg = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(targetID);
		}
		
		//2.get*(ログイン会社ID,対象組織,期間): List<人件費予算>
		DatePeriod datePeriod = new DatePeriod(startDate, endDate);
		List<DailyLaborBudgetDto> data = repo.get(companyId, targetOrg, datePeriod)
											 .stream()
											 .map(c ->DailyLaborBudgetDto.fromDomain(c) )
											 .collect(Collectors.toList());
		return data;
	}
}
