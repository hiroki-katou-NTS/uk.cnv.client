package nts.uk.ctx.at.schedule.app.command.schedule.basicschedule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.schedule.app.command.schedule.basicschedule.log.BasicScheCorrectCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.basicschedule.log.BasicScheCorrectCommandHandler;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.BasicSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.service.RegisterBasicScheduleService;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.service.RegistrationListDateSchedule;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnh1
 *
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class RegisterBasicScheduleCommandHandler
		extends CommandHandlerWithResult<DataRegisterBasicSchedule, List<String>> {
	@Inject
	private RegisterBasicScheduleService basicScheduleService;
	@Inject
	private BasicScheCorrectCommandHandler basicScheCorrectCommandHandler;
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;

	@Override
	protected List<String> handle(CommandHandlerContext<DataRegisterBasicSchedule> context) {
		RegistrationListDateSchedule registrationListDateSchedule = new RegistrationListDateSchedule(new ArrayList<>());
		String companyId = AppContexts.user().companyId();
		DataRegisterBasicSchedule command = context.getCommand();
		int modeDisplay = command.getModeDisplay();
		boolean isInsertMode = true;
		List<RegisterBasicScheduleCommand> listRegisterBasicScheduleCommand = command.getListRegisterBasicSchedule();
		
		// list listBasicScheduleAfter is data from screen
		List<BasicSchedule> listBasicSchedule = listRegisterBasicScheduleCommand.stream().map(x -> x.toDomain()).collect(Collectors.toList());

		// list listBasicScheduleBefore is data from DB
		List<BasicSchedule> listBasicScheduleBefore = new ArrayList<BasicSchedule>();
		List<BasicSchedule> listBasicScheduleAfter = new ArrayList<BasicSchedule>();
		
		List<String> errorList = basicScheduleService.register(
				companyId,
				Integer.valueOf(modeDisplay),
				listBasicSchedule,
				listBasicScheduleBefore,
				listBasicScheduleAfter,
				registrationListDateSchedule);

		// <<Public>> ????????????????????????????????????(????ng k?? record ch???nh s??? data)
		this.basicScheCorrectCommandHandler.handle(new BasicScheCorrectCommand(listBasicScheduleBefore, listBasicScheduleAfter, isInsertMode));
		
		// ??????????????????????????????
		registrationListDateSchedule.getRegistrationListDateSchedule().stream().forEach(x -> {
			// ???????????????????????????????????????????????????????????????(Th???c hi???n thu???t to??n [????ng k?? data t???m]) 
			this.interimRemainDataMngRegisterDateChange.registerDateChange(companyId, x.getEmployeeId(), x.getListDate());
		});
		
		return errorList;
	}
}
