package nts.uk.screen.at.ws.kml.kml002.k;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.kml002.K.CriterionAmountForEmploymentDto;
import nts.uk.screen.at.app.kml002.K.EmploymentInfoFinder;
import nts.uk.screen.at.app.kml002.K.EstimatedEmploymentInfoFinder;
import nts.uk.screen.at.app.kml002.K.InitEmploymentInfoDto;
import nts.uk.shr.com.context.AppContexts;
/**
 * KML002 K
 * @author hoangnd
 *
 */
@Path("screen/at/kml002/k")
@Produces("application/json")
public class EmploymentInfoWebService extends WebService {
	
	@Inject
	private EmploymentInfoFinder employmentInfoFinder;
	
	@Inject
	private EstimatedEmploymentInfoFinder estimatedEmploymentInfoFinder;
	
	@Path("init")
	@POST
	public InitEmploymentInfoDto init() {
		
		return employmentInfoFinder.getInitInfo();
	}
	
	@Path("getEstimatedInfo/{employmentCd}")
	@POST
	public CriterionAmountForEmploymentDto getEstimatedInfo(@PathParam("employmentCd") String employmentCd) {
		
		return estimatedEmploymentInfoFinder.getEstimatedInfo(AppContexts.user().companyId(), employmentCd);
	}
}
