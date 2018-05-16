package nts.uk.ctx.at.record.app.command.remainingnumber.paymana;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.record.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;

@Stateless
public class UpdateSubstitutionOfHDManaDataCommandHandler extends CommandHandler<UpdateSubstitutionOfHDManaDataCommand>  {

	@Inject
	private SubstitutionOfHDManaDataRepository subHDMDTRepo;
	
	@Override
	protected void handle(CommandHandlerContext<UpdateSubstitutionOfHDManaDataCommand> context) {
		UpdateSubstitutionOfHDManaDataCommand command = context.getCommand();
		
		Optional<SubstitutionOfHDManagementData>  subManagementData =  subHDMDTRepo.findByID(command.getSID());
		SubstitutionOfHDManagementData data = subManagementData.get();
		
		subHDMDTRepo.update(data);
		
		
	}

}
