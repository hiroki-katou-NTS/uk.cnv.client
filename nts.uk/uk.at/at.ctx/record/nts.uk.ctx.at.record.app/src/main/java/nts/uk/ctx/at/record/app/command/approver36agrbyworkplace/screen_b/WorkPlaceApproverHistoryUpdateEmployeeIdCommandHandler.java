package nts.uk.ctx.at.record.app.command.approver36agrbyworkplace.screen_b;


import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.Approver36AgrByWorkplace;
import nts.uk.ctx.at.record.dom.monthly.agreement.approver.Approver36AgrByWorkplaceRepo;
import javax.ejb.Stateless;
import javax.inject.Inject;


/**
 * Screen B: 36申請の承認者/確認者を更新登録する Renew and register
 */
@Stateless
public class WorkPlaceApproverHistoryUpdateEmployeeIdCommandHandler extends CommandHandler<WorkPlaceApproverHistoryUpdateEmployeeIdCommand>{
    @Inject
    private Approver36AgrByWorkplaceRepo repo;

    @Override
    protected void handle(CommandHandlerContext<WorkPlaceApproverHistoryUpdateEmployeeIdCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        val domain = new Approver36AgrByWorkplace(command.getWorkPlaceId(),command.getPeriod(),command.getApprovedList()
                ,command.getConfirmedList());
        val domainPrevOpt = repo.getByWorkplaceIdAndEndDate(command.getWorkPlaceId(),command.getStartDateBeforeChange().addDays(-1));
        if(domainPrevOpt.isPresent()){
            val domainPrev = domainPrevOpt.get();
            DatePeriod period = new DatePeriod(domainPrev.getPeriod().start(),command.getPeriod().start().addDays(-1));
            val domainPrevUpdate = new Approver36AgrByWorkplace(domainPrev.getWorkplaceId(),period,domainPrev.getApproverIds(),domainPrev.getConfirmerIds());
            repo.update(domainPrevUpdate,period.start());
        }
        repo.update(domain,command.getStartDateBeforeChange());

    }
}
