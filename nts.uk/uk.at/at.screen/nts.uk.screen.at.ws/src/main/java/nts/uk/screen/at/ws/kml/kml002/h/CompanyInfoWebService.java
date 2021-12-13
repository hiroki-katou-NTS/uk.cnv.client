package nts.uk.screen.at.ws.kml.kml002.h;


import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.kml002.H.CompanyInfoFinder;
import nts.uk.screen.at.app.kml002.H.EsimatedInfoDto;
import nts.uk.screen.at.app.kml002.H.EstimatedCompanyInfoFinder;
import nts.uk.screen.at.app.kml002.H.InitCompanyInfoDto;

/**
 * KML002 H
 * @author hoangnd
 *
 */

@Path("screen/at/kml002/h")
@Produces("application/json")
public class CompanyInfoWebService extends WebService {
	
	@Inject
	private CompanyInfoFinder companyInfoFinder;
	
	@Inject
	private EstimatedCompanyInfoFinder estimatedInfoFinder;
	
	@Path("init")
	@POST
	public InitCompanyInfoDto init() {
		
		return companyInfoFinder.getInitInfo();
	}
	
	@Path("selectedTab")
	@POST
	public EsimatedInfoDto selectedTab() {
		
		return estimatedInfoFinder.getEstimatedInfo();
	}

	
}
