package nts.uk.ctx.at.shared.app.command.remainingnumber.otherhdinfo;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday.OtherHolidayInfoService;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemain;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.PeregAddCommandHandler;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;

@Stateless
public class AddOtherHolidayInfoCommandHandler
		extends CommandHandlerWithResult<AddOtherHolidayInfoCommand, PeregAddCommandResult>
		implements PeregAddCommandHandler<AddOtherHolidayInfoCommand> {

	@Inject
	private OtherHolidayInfoService otherHolidayInfoService;

	@Override
	public String targetCategoryCd() {
		return "CS00035";
	}

	@Override
	public Class<?> commandClass() {
		return AddOtherHolidayInfoCommand.class;
	}

	@Override
	protected PeregAddCommandResult handle(CommandHandlerContext<AddOtherHolidayInfoCommand> context) {
		val command = context.getCommand();
		String cid = AppContexts.user().companyId();

		PublicHolidayRemain pubHD = new PublicHolidayRemain(cid, command.getEmployeeId(),
				command.getPubHdremainNumber());
		ExcessLeaveInfo exLeav = new ExcessLeaveInfo(cid, command.getEmployeeId(),
				command.getUseAtr() == null ? null : command.getUseAtr().intValue(),
				command.getOccurrenceUnit() == null ? null : command.getOccurrenceUnit().intValue(),
				command.getPaymentMethod() == null ? null : command.getPaymentMethod().intValue());
		otherHolidayInfoService.addOtherHolidayInfo(cid, pubHD, exLeav, command.getRemainNumber(),
				command.getRemainsLeft());

		return new PeregAddCommandResult(command.getEmployeeId());
	}

}
