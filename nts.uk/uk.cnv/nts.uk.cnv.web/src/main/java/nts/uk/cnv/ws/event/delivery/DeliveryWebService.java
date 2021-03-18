package nts.uk.cnv.ws.event.delivery;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.uk.cnv.app.cnv.finder.DeliveryEventFinder;
import nts.uk.cnv.app.td.command.event.delivery.DeliveryCommand;
import nts.uk.cnv.app.td.command.event.delivery.DeliveryCommandHandler;
import nts.uk.cnv.dom.td.alteration.summary.AlterationSummary;

@Path("td/event/delivery")
@Produces(MediaType.APPLICATION_JSON)
public class DeliveryWebService {

	@Inject
	private DeliveryCommandHandler deliveryCommandHandler;
	
	@Inject
	private DeliveryEventFinder deliveryEventFinder;

	@POST
	@Path("add")
	public void add(DeliveryCommand command) {
		deliveryCommandHandler.handle(command);
	}
	
	@GET
	@Path("get/{deliveryId}")
	public List<AlterationSummary> get(@PathParam("deliveryId") String deliveryId){
		return deliveryEventFinder.getBy(deliveryId);
	}
}
