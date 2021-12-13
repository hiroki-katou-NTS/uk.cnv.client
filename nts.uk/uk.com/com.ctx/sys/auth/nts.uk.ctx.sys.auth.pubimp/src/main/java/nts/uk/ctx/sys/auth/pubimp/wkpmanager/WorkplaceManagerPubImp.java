package nts.uk.ctx.sys.auth.pubimp.wkpmanager;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.ctx.sys.auth.pub.wkpmanager.WorkplaceManagerExport;
import nts.uk.ctx.sys.auth.pub.wkpmanager.WorkplaceManagerPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class WorkplaceManagerPubImp.
 */
@Stateless
public class WorkplaceManagerPubImp implements WorkplaceManagerPub {

    @Inject
    private WorkplaceManagerRepository repository;

    @Override
    public List<WorkplaceManagerExport> findByPeriodAndWkpIds(List<String> wkpIds, DatePeriod datePeriod) {
        List<WorkplaceManager> workplaceManagers = repository.findByPeriodAndWkpIds(wkpIds, datePeriod);

        return workplaceManagers.stream().map(x -> new WorkplaceManagerExport(
            x.getWorkplaceManagerId(),
            x.getEmployeeId(),
            x.getWorkplaceId(),
            x.getHistoryPeriod()
        )).collect(Collectors.toList());
    }

    @Override
    public List<WorkplaceManagerExport> findByPeriodAndBaseDate(String wkpId, GeneralDate baseDate) {
        List<WorkplaceManager> workplaceManagers = repository.findByPeriodAndBaseDate(wkpId, baseDate);

        return workplaceManagers.stream().map(x -> new WorkplaceManagerExport(
            x.getWorkplaceManagerId(),
            x.getEmployeeId(),
            x.getWorkplaceId(),
            x.getHistoryPeriod()
        )).collect(Collectors.toList());
    }

    @Override
    public List<WorkplaceManagerExport> getWkpManagerListByWkpId(String workplaceId) {
        List<WorkplaceManager> workplaceManagers = repository.getWkpManagerListByWkpId(workplaceId);

        return workplaceManagers.stream().map(x -> new WorkplaceManagerExport(
                x.getWorkplaceManagerId(),
                x.getEmployeeId(),
                x.getWorkplaceId(),
                x.getHistoryPeriod()
        )).collect(Collectors.toList());
    }

    @Override
    public List<WorkplaceManagerExport> findListWkpManagerByWkpIdsAndBaseDate(List<String> wkpIDLst, GeneralDate baseDate) {
        List<WorkplaceManager> workplaceManagers = repository.findListWkpManagerByWkpIdsAndBaseDate(wkpIDLst, baseDate);
        return workplaceManagers.stream().map(x -> new WorkplaceManagerExport(
                x.getWorkplaceManagerId(),
                x.getEmployeeId(),
                x.getWorkplaceId(),
                x.getHistoryPeriod()
        )).collect(Collectors.toList());
    }
}
