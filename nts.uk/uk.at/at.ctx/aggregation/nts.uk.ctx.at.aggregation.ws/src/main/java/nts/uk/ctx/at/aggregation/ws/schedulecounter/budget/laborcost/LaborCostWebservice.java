package nts.uk.ctx.at.aggregation.ws.schedulecounter.budget.laborcost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost.DateAndValueMapData;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost.InsertLaborCostBudgetCommand;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost.InsertLaborCostBudgetCommandHandler;
import nts.uk.ctx.at.aggregation.app.command.schedulecounter.budget.laborcost.InsertLaborCostBudgetData;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.budget.laborcost.DailyLaborBudgetDto;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.budget.laborcost.DailyLaborBudgetFinder;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;

/**
 * 
 * @author HieuLt
 *
 */
@Path("ctx/at/schedule/budget/laborcost")
@Produces("application/json")
public class LaborCostWebservice extends WebService {

	@Inject
	private DailyLaborBudgetFinder finder;

	@Inject
	private InsertLaborCostBudgetCommandHandler insert;

	@Path("getData")
	@POST
	public List<DailyLaborBudgetDto> getDailyLaborBudget(KSU001RCommand command) {
		GeneralDate start = GeneralDate.fromString(command.startDate, "yyyy/MM/dd");
		GeneralDate end = GeneralDate.fromString(command.endDate, "yyyy/MM/dd");
		return finder.getDailyLaborBudget(command.unit, start, end, command.targetID);

	}

	@Path("insert")
	@POST
	public void insertLaborCostBudget(InsertLaborCostBudgetData data) {
		Map<GeneralDate, String> lstmap = new HashMap<>();
		for (DateAndValueMapData bud : data.getDateAndValues()) {
			lstmap.put(GeneralDate.fromString(bud.getDate(), "yyyy/MM/dd"), bud.getValue());
		}
		InsertLaborCostBudgetCommand command = new InsertLaborCostBudgetCommand(data.getUnit(), data.getTargetID(),
				lstmap);
		this.insert.handle(command);
	}

}
