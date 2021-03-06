package nts.uk.ctx.at.record.ws.workrecord.stamp.management;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.stamp.management.DisplayScreenStampingResultDto;
import nts.uk.ctx.at.record.app.find.stamp.management.DisplayScreenStampingResultFinder;

/**
 * 
 * @author tutk
 *
 */
@Path("at/record/workrecord/stamp/management/")
@Produces("application/json")
public class StampWebService extends WebService {
	
	@Inject
	private DisplayScreenStampingResultFinder displayScreenStampingResultFinder;
	
	@POST 
	@Path("getAllStampingResult/{employeeId}")
	public List<DisplayScreenStampingResultDto> getDisplay(@PathParam("employeeId") String employeeId){
		DatePeriod datePerriod = new DatePeriod(GeneralDate.today().addDays(-3), GeneralDate.today());
		List<DisplayScreenStampingResultDto> data = displayScreenStampingResultFinder.getDisplay(datePerriod, employeeId);
		return data;
	}
}
