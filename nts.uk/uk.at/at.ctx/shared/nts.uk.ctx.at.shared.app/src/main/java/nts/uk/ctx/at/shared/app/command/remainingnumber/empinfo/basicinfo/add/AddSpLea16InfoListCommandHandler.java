package nts.uk.ctx.at.shared.app.command.remainingnumber.empinfo.basicinfo.add;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.shared.app.command.remainingnumber.empinfo.basicinfo.SpLeaInfoCommandHandler;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.SpecialLeaveCode;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.basicinfo.SpecialLeaveBasicInfo;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.pereg.app.command.MyCustomizeException;
import nts.uk.shr.pereg.app.command.PeregAddListCommandHandler;
@Stateless
public class AddSpLea16InfoListCommandHandler extends CommandHandlerWithResult<List<AddSpecialleave16informationCommand>, List<MyCustomizeException>>
implements PeregAddListCommandHandler<AddSpecialleave16informationCommand>{
	@Inject
	private SpLeaInfoCommandHandler addSpLeaInfoCommandHandler;
	
	@Override
	public String targetCategoryCd() {
		return "CS00054";
	}

	@Override
	public Class<?> commandClass() {
		return AddSpecialleave16informationCommand.class;
	}

	@Override
	protected List<MyCustomizeException> handle(
			CommandHandlerContext<List<AddSpecialleave16informationCommand>> context) {
		String cid = AppContexts.user().companyId();
		List<AddSpecialleave16informationCommand> cmd = context.getCommand();
		List<SpecialLeaveBasicInfo> domains = cmd.stream().map(c ->{return new SpecialLeaveBasicInfo(cid, c.getSID(), SpecialLeaveCode.CS00054.value,
				c.getUseAtr(), c.getAppSet(), c.getGrantDate(),
				c.getGrantDays() != null ? c.getGrantDays().intValue() : null, c.getGrantTable());}).collect(Collectors.toList());
		return addSpLeaInfoCommandHandler.addAllHandler(domains);
	}

}
