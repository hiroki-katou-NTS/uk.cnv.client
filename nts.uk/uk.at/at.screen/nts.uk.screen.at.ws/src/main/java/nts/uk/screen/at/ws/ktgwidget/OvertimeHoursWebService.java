package nts.uk.screen.at.ws.ktgwidget;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureResultModel;
import nts.uk.screen.at.app.ktgwidget.KTG027QueryProcessor;
import nts.uk.screen.at.app.ktgwidget.find.dto.AcquisitionOfOvertimeHoursOfEmployeesDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimeHours;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimeHoursDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimedDisplayForSuperiorsDto;
@Path("screen/at/overtimehours")
@Produces("application/json")
public class OvertimeHoursWebService extends WebService{
	@Inject
	private KTG027QueryProcessor finder;
	@POST
	@Path("getlistclosure")
	public List<ClosureResultModel> getListClosure(){
		return finder.getListClosure();
	}
	
	@POST
	@Path("getovertimehours/{targetMonth}")
	public OvertimeHoursDto getOvertimeHours(@PathParam("targetMonth") int targetMonth){
		return finder.initialActivationArocess(targetMonth);
	}
	
	@POST
	@Path("buttonPressingProcess/{targetMonth}/{selectedClosureID}")
	public OvertimeHours getOvertimeHours(@PathParam("targetMonth") int targetMonth, @PathParam("selectedClosureID") int selectedClosureID){
		return finder.buttonPressingProcess(targetMonth, selectedClosureID);
	}
	
	@POST
	@Path("getOvertimedDisplayForSuperiorsDto")
	public OvertimedDisplayForSuperiorsDto getOvertimeDisplayForSuperiorsDto(ToppageParams toppageParams) {
		return finder.getOvertimeDisplayForSuperiorsDto(toppageParams.currentOrNextMonth,
				toppageParams.closingId,
				toppageParams.startDate,
				toppageParams.endDate,
				toppageParams.processingYm);
	}
	
	@POST
	@Path("onChangeDate/{closureId}/{targetDate}")
	public AcquisitionOfOvertimeHoursOfEmployeesDto onChangeDate(@PathParam("closureId") int closureId, @PathParam("targetDate") Integer targetDate) {
		return finder.getAcquisitionOfOvertimeHoursOfEmployeesDto(closureId, YearMonth.of(targetDate), Optional.ofNullable(null));
		
	}
}
