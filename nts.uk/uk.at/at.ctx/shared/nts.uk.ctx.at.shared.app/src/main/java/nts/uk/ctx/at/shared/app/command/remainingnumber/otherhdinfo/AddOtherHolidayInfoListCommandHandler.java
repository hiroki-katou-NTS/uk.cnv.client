package nts.uk.ctx.at.shared.app.command.remainingnumber.otherhdinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday.OtherHolidayInfoInter;
import nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday.OtherHolidayInfoService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregAddCommandResult;
import nts.uk.shr.pereg.app.command.PeregAddListCommandHandler;
@Stateless
public class AddOtherHolidayInfoListCommandHandler extends CommandHandlerWithResult<List<AddOtherHolidayInfoCommand>, List<MyCustomizeException>>
implements PeregAddListCommandHandler<AddOtherHolidayInfoCommand>{
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
	protected List<MyCustomizeException> handle(CommandHandlerContext<List<AddOtherHolidayInfoCommand>> context) {
		List<AddOtherHolidayInfoCommand> cmd = context.getCommand();
		String cid = AppContexts.user().companyId();
		 Map<String, OtherHolidayInfoInter> otherHolidayInfos = new HashMap<>();
		
		List<PeregAddCommandResult> result = new ArrayList<>();
		
		cmd.stream().forEach(c ->{
			PublicHolidayCarryForwardData pubHD = PublicHolidayCarryForwardData.createFromJavaType(c.getEmployeeId(),
					c.getPubHdremainNumber(),0);
			ExcessLeaveInfo exLeav = new ExcessLeaveInfo(cid, c.getEmployeeId(),
					c.getUseAtr() == null ? null : c.getUseAtr().intValue(),
					c.getOccurrenceUnit() == null ? null : c.getOccurrenceUnit().intValue(),
					c.getPaymentMethod() == null ? null : c.getPaymentMethod().intValue());
			otherHolidayInfos.put(c.getEmployeeId(), new OtherHolidayInfoInter(cid, pubHD, exLeav, c.getRemainNumber(), c.getRemainsLeft()));
			result.add(new PeregAddCommandResult(c.getEmployeeId()));
		});
		if(!otherHolidayInfos.isEmpty()) {
			otherHolidayInfoService.addOtherHolidayInfo(cid, otherHolidayInfos);
		}
		return new ArrayList<>();
	}

}
