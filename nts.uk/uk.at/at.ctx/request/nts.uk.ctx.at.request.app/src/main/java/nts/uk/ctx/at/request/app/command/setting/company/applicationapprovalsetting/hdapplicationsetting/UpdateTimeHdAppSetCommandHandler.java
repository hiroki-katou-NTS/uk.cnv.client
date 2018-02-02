package nts.uk.ctx.at.request.app.command.setting.company.applicationapprovalsetting.hdapplicationsetting;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdapplicationsetting.TimeHdAppSet;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.hdapplicationsetting.TimeHdAppSetRepository;
@Stateless
public class UpdateTimeHdAppSetCommandHandler extends CommandHandler<TimeHdAppSetCommand>{
	@Inject
	private TimeHdAppSetRepository timeRep;

	@Override
	protected void handle(CommandHandlerContext<TimeHdAppSetCommand> context) {
		TimeHdAppSetCommand data = context.getCommand();
		Optional<TimeHdAppSet> time = timeRep.getByCid();
		TimeHdAppSet timeHd = TimeHdAppSet.createFromJavaType(data.getCompanyId(), 
				data.getCheckDay(), data.getUse60h(), data.getUseAttend2(), data.getNameBefore2(), 
				data.getUseBefore(), data.getNameBefore(), data.getActualDisp(), data.getCheckOver(), 
				data.getUseTimeHd(), data.getUseTimeYear(), data.getUsePrivate(), data.getPrivateName(),
				data.getUnionLeave(), data.getUnionName(), data.getUseAfter2(), data.getNameAfter2(), 
				data.getUseAfter(), data.getNameAfter());
		if(time.isPresent()){
			timeRep.update(timeHd);
		}
		timeRep.insert(timeHd);
	}
}
