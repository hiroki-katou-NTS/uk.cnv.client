package nts.uk.ctx.at.shared.app.command.specialholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.event.SpecialHolidayDomainEvent;
import nts.uk.shr.com.context.AppContexts;

/**
 *
 * @author tanlv
 *
 */
@Transactional
@Stateless
public class CreateSpecialHolidayCommandHandler extends CommandHandlerWithResult<SpecialHolidayCommand, List<String>> {
	@Inject
	private SpecialHolidayRepository sphdRepo;

	@Override
	protected List<String> handle(CommandHandlerContext<SpecialHolidayCommand> context) {
		SpecialHolidayCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();
		List<String> errList = new ArrayList<String>();

		// check exists code
		Optional<SpecialHoliday> specialHoliday = sphdRepo.findBySingleCD(companyId, command.getSpecialHolidayCode());
		if (specialHoliday.isPresent()) {
			addMessage(errList, "Msg_3");
		}

		SpecialHoliday domain = command.toDomain(companyId);
		if ( domain.getGrantRegular().getGrantPeriodic().isPresent() ) {
			errList.addAll(domain.getGrantRegular().getGrantPeriodic().get().getGrantDeadline().validateInput());
		}

		errList.addAll(domain.getSpecialLeaveRestriction().validateInput());

		if (errList.isEmpty()) {
			// call event
			SpecialHolidayDomainEvent sHC = SpecialHolidayDomainEvent.createFromDomain(true,domain);
			sHC.toBePublished();
			// add to db
			sphdRepo.add(domain);
		}

		return errList;
	}

	/**
	 * Add exception message
	 *
	 * @param exceptions
	 * @param messageId
	 */
	private void addMessage(List<String> errorsList, String messageId) {
		if (!errorsList.contains(messageId)) {
			errorsList.add(messageId);
		}
	}
}
