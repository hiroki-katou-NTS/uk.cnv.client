package nts.uk.ctx.at.request.ws.dialog.suspensionholiday;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.dto.AnnualHolidaysDto;
import nts.uk.ctx.at.request.app.find.dialog.suspensionholiday.SuspensionHolidayFinder;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.shr.com.context.AppContexts;

@Path("at/request/dialog/suspensionholidays")
@Produces("application/json")
public class SuspensionHolidayWebServices extends WebService {
	
	@Inject
	private EmployeeRequestAdapter employeeRequestAdapter;
	
	@Inject
	private SuspensionHolidayFinder suspensionHolidayFinder;

	@POST
	@Path("findSuspensionHoliday")
	public AnnualHolidaysDto findAnnualHolidays(List<String> sIDs) {
		return suspensionHolidayFinder.findSuspensionHoliday(sIDs);
	}
	
	@POST
	@Path("getSid")
	public List<String> getEmployee() {		
		List<String> result = employeeRequestAdapter.getAllSidByCid(AppContexts.user().companyId());
		return result;
	}

}