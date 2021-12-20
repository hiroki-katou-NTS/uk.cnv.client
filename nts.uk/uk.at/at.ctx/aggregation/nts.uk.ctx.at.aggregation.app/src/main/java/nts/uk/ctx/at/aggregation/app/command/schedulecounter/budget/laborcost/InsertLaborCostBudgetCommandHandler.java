package nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetRegisterService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.budget.laborcost.LaborCostBudgetRepository;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author HieuLT
 *
 */
@Stateless
public class InsertLaborCostBudgetCommandHandler extends CommandHandler<InsertLaborCostBudgetCommand> {
	
	@Inject
	private LaborCostBudgetRepository repo;

	@Override
	protected void handle(CommandHandlerContext<InsertLaborCostBudgetCommand> context) {

		InsertLaborCostBudgetCommand cmd = context.getCommand();
		// 1: call<>
		String companyId = AppContexts.user().companyId();
		// 職場を指定して識別情報を作成する(職場ID)
		TargetOrgIdenInfor targetOrg = null;
		if (cmd.getUnit() == TargetOrganizationUnit.WORKPLACE.value) {
			targetOrg = TargetOrgIdenInfor.creatIdentifiWorkplace(cmd.getTargetID());
		} else {
			targetOrg = TargetOrgIdenInfor.creatIdentifiWorkplaceGroup(cmd.getTargetID());
		}
		// LaborCostBudgetRegisterService
		// 2
		RequireImpl require = new RequireImpl(repo);
		Map<GeneralDate, String> map = cmd.getLstmap();
		//
		for (Map.Entry<GeneralDate, String> entry : map.entrySet()) {
			AtomTask atomTask = LaborCostBudgetRegisterService.register(require, targetOrg, entry.getKey(),
					Optional.ofNullable(entry.getValue() != "" ? new LaborCostBudgetAmount(Integer.parseInt(entry.getValue())) : null));
			transaction.execute(() -> {
				atomTask.run();
			});
		}

	}

	@AllArgsConstructor
	private class RequireImpl implements LaborCostBudgetRegisterService.Require {
		@Inject
		private LaborCostBudgetRepository laborCostBudgetRepository;

		@Override
		public void insert(LaborCostBudget domain) {
			String companyID = AppContexts.user().companyId();
			laborCostBudgetRepository.insert(companyID, domain);

		}

		@Override
		public void delete(TargetOrgIdenInfor targetOrg, GeneralDate ymd) {
 			String companyID = AppContexts.user().companyId(); 

			laborCostBudgetRepository.delete(companyID, targetOrg, ymd);

		}

	}

}
