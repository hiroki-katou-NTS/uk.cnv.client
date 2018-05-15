package nts.uk.ctx.at.record.app.command.stamp.card.stampcard.add;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;

@Stateless
public class AddStampCardCommandHandler extends CommandHandlerWithResult<AddStampCardCommand, PeregAddCommandResult>
		implements PeregAddCommandHandler<AddStampCardCommand> {

		
	@Inject
	private StampCardRepository stampCardRepo;

	@Override
	public String targetCategoryCd() {
		return "CS00069";
	}

	@Override
	public Class<?> commandClass() {
		return AddStampCardCommand.class;
	}

	@Override
	protected PeregAddCommandResult handle(CommandHandlerContext<AddStampCardCommand> context) {
		AddStampCardCommand command = context.getCommand();
		
		// create new domain and add
		String stampCardId = IdentifierUtil.randomUniqueId();
		StampCard stampCard = StampCard.createFromJavaType(stampCardId, command.getEmployeeId(),
				command.getStampNumber(), GeneralDate.today(), AppContexts.user().contractCode());
		stampCardRepo.add(stampCard);
		
		return new PeregAddCommandResult(stampCardId);
	}

}
