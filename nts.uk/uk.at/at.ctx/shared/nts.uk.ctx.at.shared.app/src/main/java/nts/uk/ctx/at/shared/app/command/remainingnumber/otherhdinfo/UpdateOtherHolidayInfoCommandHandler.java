package nts.uk.ctx.at.shared.app.command.remainingnumber.otherhdinfo;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday.OtherHolidayInfoService;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemain;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.PeregUpdateCommandHandler;

@Stateless
public class UpdateOtherHolidayInfoCommandHandler extends CommandHandler<UpdateOtherHolidayInfoCommand>
implements PeregUpdateCommandHandler<UpdateOtherHolidayInfoCommand> {

	@Inject
	private OtherHolidayInfoService otherHolidayInfoService;
	
	@Override
	public String targetCategoryCd() {
		return "CS00035";
	}

	@Override
	public Class<?> commandClass() {
		return UpdateOtherHolidayInfoCommand.class;
	}

	@Override
	protected void handle(CommandHandlerContext<UpdateOtherHolidayInfoCommand> context) {
		val command = context.getCommand();
		String cid = AppContexts.user().companyId();
		
		//公休付与残数データ
		PublicHolidayRemain pubHD = new PublicHolidayRemain(cid, command.getEmployeeId(),
				command.getPubHdremainNumber());
		
		//超過有休基本情報
		ExcessLeaveInfo exLeav = ExcessLeaveInfo.createDomain(cid, command.getEmployeeId(), command.getUseAtr(),
				command.getOccurrenceUnit(), command.getPaymentMethod());
		
		otherHolidayInfoService.updateOtherHolidayInfo(cid, pubHD, exLeav, command.getRemainNumber(),
				command.getRemainsLeft());
	}

}
