/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.ws.shift.autocalsetting;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.uk.ctx.at.schedule.app.command.shift.autocalsettingwkpjob.SaveWkpJobAutoCalSetCommandHandler;
import nts.uk.ctx.at.schedule.app.command.shift.autocalsettingwkpjob.WkpJobAutoCalSetCommand;
import nts.uk.ctx.at.schedule.app.find.shift.autocalsetting.wkpjob.WkpJobAutoCalSetFinder;
import nts.uk.ctx.at.schedule.app.find.shift.autocalsetting.wkpjob.WkpJobAutoCalSettingDto;

/**
 * The Class WkpJobAutoCalWS.
 */
@Path("ctx/at/schedule/shift/autocalwkpjob")
@Produces("application/json")
public class WkpJobAutoCalWS {

	/** The wkp job auto cal set finder. */
	@Inject
	private WkpJobAutoCalSetFinder wkpJobAutoCalSetFinder;

	/** The save wkp job auto cal set command handler. */
	@Inject
	private SaveWkpJobAutoCalSetCommandHandler saveWkpJobAutoCalSetCommandHandler;

	/**
	 * Gets the wkp job auto cal setting dto.
	 *
	 * @param wkpId the wkp id
	 * @param jobId the job id
	 * @return the wkp job auto cal setting dto
	 */
	@POST
	@Path("getautocalwkpjob/{wkpId}/{jobId}")
	public WkpJobAutoCalSettingDto getWkpJobAutoCalSettingDto(@PathParam("wkpId") String wkpId,
			@PathParam("jobId") String jobId) {
		return this.wkpJobAutoCalSetFinder.getWkpJobAutoCalSetting(wkpId, jobId);
	}

	/**
	 * Save.
	 *
	 * @param command the command
	 */
	@POST
	@Path("save")
	public void save(WkpJobAutoCalSetCommand command) {
		this.saveWkpJobAutoCalSetCommandHandler.handle(command);
	}

	/**
	 * Deledte.
	 *
	 * @param wkpId the wkp id
	 * @param jobId the job id
	 */
	@POST
	@Path("delete/{wkpId}/{jobId}")
	public void deledte(@PathParam("wkpId") String wkpId, @PathParam("jobId") String jobId) {
		this.wkpJobAutoCalSetFinder.deleteByCode(wkpId, jobId);
	}
}
