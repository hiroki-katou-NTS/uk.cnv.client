package nts.uk.screen.at.ws.ktgwidget;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.arc.layer.ws.WebService;
import nts.uk.screen.at.app.ktgwidget.find.OptionalWidgetKtgFinder;
import nts.uk.screen.at.app.ktgwidget.find.dto.OptionalWidgetDisplay;
import nts.uk.screen.at.app.ktgwidget.find.dto.WidgetInitialDisplayMonthDto;

@Path("screen/at/OptionalWidget")
@Produces("application/json")
public class OptionalWidgetWebService extends WebService {

	@Inject
	private OptionalWidgetKtgFinder OptionalWidgetFinder; 
	
	@POST
	@Path("getEmploymentCode")
	public String getEmploymentCode(){
		return null;
	}
	
	@POST
	@Path("getOptionalWidgetDisplay")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public OptionalWidgetDisplay getOptionalWidgetDisplay(String topPagePartCode){
		return OptionalWidgetFinder.getOptionalWidgetDisplay(topPagePartCode);
	}
	
	@POST
	@Path("getWidgetInitialDisplayMonth")
	public WidgetInitialDisplayMonthDto getWidgetInitialDisplayMonth() {
		return this.OptionalWidgetFinder.getWidgetInitialDisplayMonth();
	}
}
