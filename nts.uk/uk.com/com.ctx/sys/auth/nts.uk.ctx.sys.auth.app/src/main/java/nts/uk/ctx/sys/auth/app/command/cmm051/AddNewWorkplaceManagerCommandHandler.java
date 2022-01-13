package nts.uk.ctx.sys.auth.app.command.cmm051;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.wkpmanager.RegisterWorkplaceManagerService;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.ctx.sys.shared.dom.employee.EmpCompanyHistoryAdapter;
import nts.uk.ctx.sys.shared.dom.employee.EmpEnrollPeriodImport;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

/**
 * 職場管理者を追加する
 */
@Stateless
public class AddNewWorkplaceManagerCommandHandler extends CommandHandler<AddNewWorkplaceManagerCommand> {
    @Inject
    private WorkplaceManagerRepository workplaceManagerRepository;
    @Inject
    private EmpCompanyHistoryAdapter empCompanyHistoryAdapter;

    @Override
    protected void handle(CommandHandlerContext<AddNewWorkplaceManagerCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        RequireImpl require = new RequireImpl(empCompanyHistoryAdapter, workplaceManagerRepository);
        String workplaceId = command.getWorkPlaceId();
        String sid = command.getSid();
        DatePeriod historyPeriod = new DatePeriod(command.getStartDate(), command.getEndDate());
        AtomTask task = RegisterWorkplaceManagerService.add(require, workplaceId, sid, historyPeriod);
        transaction.execute(task);
    }

    @AllArgsConstructor
    public class RequireImpl implements RegisterWorkplaceManagerService.Require {
        private EmpCompanyHistoryAdapter empCompanyHistoryAdapter;
        private WorkplaceManagerRepository workplaceManagerRepository;


        @Override
        public List<WorkplaceManager> getWorkplaceMangager(String workplaceId, String sid) {
            return workplaceManagerRepository.getWkpManagerByWorkplaceIdAndSid(workplaceId, sid);
        }

        @Override
        public void insert(WorkplaceManager workplaceManager) {
            workplaceManagerRepository.add(workplaceManager);

        }

        @Override
        public void update(WorkplaceManager workplaceManager) {
            workplaceManagerRepository.update(workplaceManager);

        }

        @Override
        public List<EmpEnrollPeriodImport> getEmployeeCompanyHistory(String sid, DatePeriod datePeriod) {
            return empCompanyHistoryAdapter.getEnrollmentPeriod(Collections.singletonList(sid), datePeriod);
        }
    }
}
