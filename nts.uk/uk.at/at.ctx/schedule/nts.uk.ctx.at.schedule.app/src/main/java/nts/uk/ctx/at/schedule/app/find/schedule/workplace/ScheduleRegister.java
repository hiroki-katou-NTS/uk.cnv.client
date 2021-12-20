package nts.uk.ctx.at.schedule.app.find.schedule.workplace;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.task.AsyncTaskInfo;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.schedule.app.command.budget.external.actualresult.dto.ExecutionInfor;
import nts.uk.ctx.at.schedule.app.command.schedule.workplace.ScheduleRegisterCommand;

/**
 * @author anhnm
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ScheduleRegister {
    
    @Inject
    private ScheduleRegisterCommandHandler scheduleRegisterCommandHandler;

    public ExecutionInfor handle(ScheduleRegisterCommand command) {
        String executeId = IdentifierUtil.randomUniqueId();
        
        AsyncTaskInfo taskInfor = scheduleRegisterCommandHandler.handle(command);
        return ExecutionInfor.builder().taskInfor(taskInfor).executeId(executeId).build();
    }
}
