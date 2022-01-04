/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.app.command.workrecord.workfixed;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmed;
import nts.uk.ctx.at.record.dom.workrecord.workrecord.EmploymentConfirmedRepository;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class WorkFixedSaveCommandHandler.
 */
@Stateless
public class SaveWorkFixedCommandHandler extends CommandHandler<SaveWorkFixedCommand> {

	/** The repository. */
	@Inject
	private EmploymentConfirmedRepository repository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.arc.layer.app.command.CommandHandler#handle(nts.arc.layer.app.command
	 * .CommandHandlerContext)
	 */
	@Override
	protected void handle(CommandHandlerContext<SaveWorkFixedCommand> context) {

		// Get Person Id
//		String personId = AppContexts.user().personId();

		// Get Company Id
//		String companyId = AppContexts.user().companyId();
		
		// Get command
		SaveWorkFixedCommand command = context.getCommand();
		
		EmploymentConfirmed workFixed = new EmploymentConfirmed(
				new CompanyId(AppContexts.user().companyId()),
				new WorkplaceId(command.getWkpId()), 
				ClosureId.valueOf(command.getClosureId()),
				new YearMonth(command.getProcessDate()), 
				AppContexts.user().employeeId(),
				GeneralDateTime.now());
		
		this.repository.insert(workFixed);
	}
}
		
		
//		Optional<EmploymentConfirmed> exist =  repository.get(
//				AppContexts.user().companyId(), 
//				command.getWkpId(), 
//				ClosureId.valueOf(command.getClosureId()), 
//				new YearMonth(command.getProcessDate()));
		

//		// Set PersonId and FixedDate to WorkFixed command
//		WorkFixed workFixed = new WorkFixed();
//		if (Integer.valueOf(ConfirmClsStatus.Pending.value).equals(command.getConfirmClsStatus())) {
//			// Remove FixedDate and PersonId if status = Pending (unchecked)
//			workFixed = command.toDomain(companyId, null, null);
//		} else {
//			workFixed = command.toDomain(companyId, personId, GeneralDate.today());
//		}		
		
//		EmploymentConfirmed workFixed = new EmploymentConfirmed(
//				new CompanyId(AppContexts.user().companyId()),
//				new WorkplaceId(command.getWkpId()), 
//				ClosureId.valueOf(command.getClosureId()),
//				new YearMonth(command.getProcessDate()), 
//				AppContexts.user().employeeId(),
//				GeneralDateTime.now());
//
//		// Save/Update new WorkFixed
//		if (exist.isPresent()) {
//			this.repository.insert(workFixed);
//			return;
//		}
//		this.repository.add(workFixed}
