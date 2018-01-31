package nts.uk.ctx.bs.employee.app.command.employee.contact;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.bs.employee.dom.employee.contact.EmployeeInfoContact;
import nts.uk.ctx.bs.employee.dom.employee.contact.EmployeeInfoContactRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;

@Stateless
public class AddEmployeeInfoContactCommandHandler extends CommandHandlerWithResult<AddEmployeeInfoContactCommand, PeregAddCommandResult>
	implements PeregAddCommandHandler<AddEmployeeInfoContactCommand>{
	
	@Inject
	private EmployeeInfoContactRepository employeeInfoContactRepository;
	
	@Override
	public String targetCategoryCd() {
		return "CS00023";
	}

	@Override
	public Class<?> commandClass() {
		return AddEmployeeInfoContactCommand.class;
	}

	@Override
	protected PeregAddCommandResult handle(CommandHandlerContext<AddEmployeeInfoContactCommand> context) {
		val command = context.getCommand();
		String cid = AppContexts.user().companyId();
		
		EmployeeInfoContact domain = new EmployeeInfoContact(cid, command.getSid(), command.getMailAddress(),
				command.getSeatDialIn(), command.getSeatExtensionNo(), command.getPhoneMailAddress(),
				command.getCellPhoneNo());
		employeeInfoContactRepository.add(domain);
		
		return new PeregAddCommandResult(command.getSid());
	}

}
