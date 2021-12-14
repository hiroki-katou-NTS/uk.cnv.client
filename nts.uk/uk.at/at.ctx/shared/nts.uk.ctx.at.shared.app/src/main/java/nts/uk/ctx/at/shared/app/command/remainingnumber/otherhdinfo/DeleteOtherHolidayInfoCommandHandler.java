package nts.uk.ctx.at.shared.app.command.remainingnumber.otherhdinfo;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfoRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemainRepository;
import nts.uk.shr.pereg.app.command.PeregDeleteCommandHandler;

@Stateless
public class DeleteOtherHolidayInfoCommandHandler extends CommandHandler<DeleteOtherHolidayInfoCommand>
implements PeregDeleteCommandHandler<DeleteOtherHolidayInfoCommand> {

	@Inject
	private PublicHolidayRemainRepository publicHolidayRemainRepository;
	
	@Inject 
	private ExcessLeaveInfoRepository excessLeaveInfoRepository;
	
	@Override
	public String targetCategoryCd() {
		return "CS00035";
	}

	@Override
	public Class<?> commandClass() {
		return DeleteOtherHolidayInfoCommand.class;
	}

	@Override
	protected void handle(CommandHandlerContext<DeleteOtherHolidayInfoCommand> context) {
		val command = context.getCommand();
		publicHolidayRemainRepository.delete(command.getEmployeeId());
		excessLeaveInfoRepository.delete(command.getEmployeeId());
	}

}
