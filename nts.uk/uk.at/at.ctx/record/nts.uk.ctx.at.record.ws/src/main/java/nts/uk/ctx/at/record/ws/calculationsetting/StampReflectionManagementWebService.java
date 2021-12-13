package nts.uk.ctx.at.record.ws.calculationsetting;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.record.app.command.calculationsetting.StampReflectionManagementCommand;
import nts.uk.ctx.at.record.app.command.calculationsetting.StampReflectionManagementCommandHandler;
import nts.uk.ctx.at.record.app.find.calculationsetting.StampReflectionManagementDto;
import nts.uk.ctx.at.record.app.find.calculationsetting.StampReflectionManagementFinder;
import nts.uk.ctx.at.record.app.find.calculationsetting.UsageDataDto;

/**
 * The Class StampReflectionManagementWebService.
 */
@Path("at/record/calculation")
@Produces("application/json")
public class StampReflectionManagementWebService {
	
	/** The finder. */
	@Inject
	private StampReflectionManagementFinder finder;
	
	/** The handler. */
	@Inject
	private StampReflectionManagementCommandHandler handler;
	
	/**
	 * Gets the stamp reflection.
	 *
	 * @return the stamp reflection
	 */
	@POST
	@Path("findByCode")
	public StampReflectionManagementDto getStampReflection(){
		return this.finder.findByCode();
	}
	
	@POST
	@Path("findUsageData")
	public UsageDataDto findUsageData() {
		return this.finder.findUsageData();
	}
	
	/**
	 * Adds the.
	 *
	 * @param command the command
	 */
	@Path("add")
	@POST
	public void add(StampReflectionManagementCommand command) {
		this.handler.handle(command);
	}
	
	/**
	 * Update.
	 *
	 * @param command the command
	 */
	@Path("update")
	@POST
	public void update(StampReflectionManagementCommand command) {
		this.handler.handle(command);
	}
}
