package command.person.contact;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.bs.person.dom.person.contact.PersonContact;
import nts.uk.ctx.bs.person.dom.person.contact.PersonContactRepository;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;

@Stateless
public class AddPerContactCommandHandler extends CommandHandlerWithResult<AddPerContactCommand,PeregAddCommandResult>
 	implements PeregAddCommandHandler<AddPerContactCommand>{

	@Inject
	private PersonContactRepository personContactRepository;
	
	@Override
	public String targetCategoryCd() {
		return "CS00022";
	}

	@Override
	public Class<?> commandClass() {
		return AddPerContactCommand.class;
	}

	@Override
	protected PeregAddCommandResult handle(CommandHandlerContext<AddPerContactCommand> context) {
		val command = context.getCommand();
		
		PersonContact perContact = new PersonContact(command.getPersonId(), command.getCellPhoneNumber(),
				command.getMailAdress(), command.getMobileMailAdress(), command.getMemo1(), command.getContactName1(),
				command.getPhoneNumber1(), command.getMemo2(), command.getContactName2(), command.getPhoneNumber2());
		
		
		personContactRepository.add(perContact);
		
		return new PeregAddCommandResult(command.getPersonId());
	}

}
