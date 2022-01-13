package nts.uk.ctx.at.record.app.command.workrecord.operationsetting;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DayFuncControlRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.DaiPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;

@Stateless
@Transactional
public class UpdateDayFuncControlCommandHandler extends CommandHandler<DayFuncControlCommand>{

	@Inject
	private DayFuncControlRepository repository;
	
	@Override
	protected void handle(CommandHandlerContext<DayFuncControlCommand> context) {
		DayFuncControlCommand updateCommand = context.getCommand();
		DaiPerformanceFun daiPerformanceFun = DaiPerformanceFun.createFromJavaType(updateCommand.getCid(), 
				updateCommand.getComment(), 
	    		updateCommand.isDisp36Atr() ? 1 : 0, 
	    		updateCommand.isFlexDispAtr() ? 1 : 0, 
				updateCommand.isCheckErrRefDisp()? 1 : 0);
		IdentityProcess identityProcess = IdentityProcess.createFromJavaType(updateCommand.getCid(), 
        		updateCommand.isDaySelfChk() ? 1 :0, 
        		updateCommand.isMonSelfChK() ? 1 : 0, 
        		updateCommand.getDaySelfChkError());
		ApprovalProcess approvalProcess = ApprovalProcess.createFromJavaType(updateCommand.getCid(),
				updateCommand.isDayBossChk() ? 1 : 0, 
				updateCommand.isMonBossChk() ? 1 : 0, 
        		updateCommand.getDayBossChkError());
		repository.update(daiPerformanceFun, identityProcess, approvalProcess);
	}

}
