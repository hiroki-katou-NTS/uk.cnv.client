package nts.uk.ctx.at.request.ws.dialog.annualholiday;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.AnnualHolidaysFinder;
import nts.uk.ctx.at.request.app.find.dialog.annualholiday.dto.AnnualHolidaysDto;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.shr.com.context.AppContexts;

@Path("at/request/dialog/annualholidays")
@Produces("application/json")
public class AnnualHolidaysWebServices extends WebService {
	
	@Inject
	private EmployeeRequestAdapter employeeRequestAdapter;
	
	@Inject
	private AnnualHolidaysFinder holidayFinder;

	@POST
	@Path("findAnnualHolidays")
	public AnnualHolidaysDto findAnnualHolidays(List<String> sIDs) {
		return holidayFinder.findAnnualHolidays(sIDs);
	}
	
	@POST
	@Path("getSid")
	public List<String> getEmployee() {		
		List<String> result = employeeRequestAdapter.getAllSidByCid(AppContexts.user().companyId());
		return result;
	}

}
