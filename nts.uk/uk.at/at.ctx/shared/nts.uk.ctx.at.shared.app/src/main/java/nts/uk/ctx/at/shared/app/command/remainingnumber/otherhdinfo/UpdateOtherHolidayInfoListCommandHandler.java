package nts.uk.ctx.at.shared.app.command.remainingnumber.otherhdinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday.OtherHolidayInfoInter;
import nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday.OtherHolidayInfoService;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemain;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;
import nts.uk.shr.pereg.app.command.PeregUpdateListCommandHandler;
@Stateless
public class UpdateOtherHolidayInfoListCommandHandler extends CommandHandler<List<UpdateOtherHolidayInfoCommand>>
implements PeregUpdateListCommandHandler<UpdateOtherHolidayInfoCommand>{
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
	protected void handle(CommandHandlerContext<List<UpdateOtherHolidayInfoCommand>> context) {
		List<UpdateOtherHolidayInfoCommand> cmd = context.getCommand();
		String cid = AppContexts.user().companyId();
		 Map<String, OtherHolidayInfoInter> otherHolidayInfos = new HashMap<>();
		List<PeregAddCommandResult> result = new ArrayList<>();
		
		cmd.parallelStream().forEach(c ->{
			//公休付与残数データ
			PublicHolidayRemain pubHD = new PublicHolidayRemain(cid, c.getEmployeeId(),
					c.getPubHdremainNumber());
			//超過有休基本情報
			ExcessLeaveInfo exLeav = ExcessLeaveInfo.createDomain(cid, c.getEmployeeId(), c.getUseAtr(),
					c.getOccurrenceUnit(), c.getPaymentMethod());
			otherHolidayInfos.put(c.getEmployeeId(), new OtherHolidayInfoInter(cid, pubHD, exLeav, c.getRemainNumber(), c.getRemainsLeft()));
			result.add(new PeregAddCommandResult(c.getEmployeeId()));
		});
		
		if(!otherHolidayInfos.isEmpty()) {
			otherHolidayInfoService.updateOtherHolidayInfo(cid, otherHolidayInfos);
		}
	}

}
