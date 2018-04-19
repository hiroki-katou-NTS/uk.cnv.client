package nts.uk.ctx.at.function.app.command.holidaysremaining.report;

import java.util.List;

import lombok.Value;
import nts.arc.time.GeneralDate;

@Value
public class HolidaysRemainingReportQuery {
	private HolidaysRemainingOutputConditionQuery hdRemOutCondQuery;
	private List<EmployeeQuery> lstEmpIds;
}
