/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.ws.shift.estimate.employment;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nts.arc.layer.ws.WebService;
import nts.uk.ctx.at.schedule.app.command.shift.estimate.company.CompanyEstablishmentSaveCommand;
import nts.uk.ctx.at.schedule.app.command.shift.estimate.company.CompanyEstablishmentSaveCommandHandler;
import nts.uk.ctx.at.schedule.app.find.shift.estimate.employment.EmploymentEstablishmentFinder;
import nts.uk.ctx.at.schedule.app.find.shift.estimate.employment.dto.EmploymentEstablishmentDto;

/**
 * The Class EmploymentEstablishmentWs.
 */
@Path("ctx/at/schedule/shift/estimate/employment")
@Produces(MediaType.APPLICATION_JSON)
public class EmploymentEstablishmentWs extends WebService {

	/** The finder. */
	@Inject
	private EmploymentEstablishmentFinder finder;

	/** The save. */
	@Inject
	private CompanyEstablishmentSaveCommandHandler save;

	/**
	 * Find by target year.
	 *
	 * @param targetYear the target year
	 * @return the company establishment dto
	 */
	@POST
	@Path("find/{employmentCode}/{targetYear}")
	public EmploymentEstablishmentDto findByTargetYear(
			@PathParam("employmentCode") String employmentCode,
			@PathParam("targetYear") Integer targetYear) {
		return this.finder.findEstimateTime(employmentCode, targetYear);
	}

	/**
	 * Save company estimate.
	 *
	 * @param command the command
	 */
	@POST
	@Path("save")
	public void saveCompanyEstimate(CompanyEstablishmentSaveCommand command) {
		this.save.handle(command);
	}
}
