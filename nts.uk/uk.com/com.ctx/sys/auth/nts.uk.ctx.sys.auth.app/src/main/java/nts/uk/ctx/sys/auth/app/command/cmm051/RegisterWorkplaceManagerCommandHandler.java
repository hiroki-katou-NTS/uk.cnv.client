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
 * 職場管理者を登録する
 */
@Stateless
public class RegisterWorkplaceManagerCommandHandler extends CommandHandler<RegisterWorkplaceManagerCommand> {
    @Inject
    private WorkplaceManagerRepository workplaceManagerRepository;

    @Inject
    private EmpCompanyHistoryAdapter empCompanyHistoryAdapter;

    @Override
    protected void handle(CommandHandlerContext<RegisterWorkplaceManagerCommand> commandHandlerContext) {
        val command = commandHandlerContext.getCommand();
        val id = command.getWkpManagerId();
        val oldDomainOpt = workplaceManagerRepository.getWorkplaceManagerByID(id);
        if (oldDomainOpt.isPresent()) {
            val oldDomain = oldDomainOpt.get();
            RequireImpl require = new RequireImpl(empCompanyHistoryAdapter, workplaceManagerRepository);
            DatePeriod historyPeriod = new DatePeriod(command.getStartDate(), command.getEndDate());
            oldDomain.setHistoryPeriod(historyPeriod);
            AtomTask task = RegisterWorkplaceManagerService.changePeriod(require,oldDomain);
            transaction.execute(task);
        }
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
