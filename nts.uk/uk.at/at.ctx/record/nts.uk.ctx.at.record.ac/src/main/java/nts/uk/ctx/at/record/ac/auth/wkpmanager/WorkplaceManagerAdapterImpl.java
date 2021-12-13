package nts.uk.ctx.at.record.ac.auth.wkpmanager;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager.WorkplaceManagerAdapter;
import nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager.WorkplaceManagerImport;
import nts.uk.ctx.sys.auth.pub.wkpmanager.WorkplaceManagerPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class WorkplaceManagerAdapterImpl implements WorkplaceManagerAdapter {

    @Inject
    private WorkplaceManagerPub workplaceManagerPub;

    @Override
    public List<WorkplaceManagerImport> findByPeriodAndWkpIds(List<String> wkpIds, DatePeriod datePeriod) {
        return workplaceManagerPub.findByPeriodAndWkpIds(wkpIds, datePeriod).stream().map(x ->
            new WorkplaceManagerImport(
                x.getWorkplaceManagerId(),
                x.getEmployeeId(),
                x.getWorkplaceId(),
                x.getHistoryPeriod()))
            .collect(Collectors.toList());
    }

    @Override
    public List<WorkplaceManagerImport> getWkpManagerListByWkpId(String workplaceId) {
        return workplaceManagerPub.getWkpManagerListByWkpId(workplaceId).stream().map(x ->
                new WorkplaceManagerImport(
                        x.getWorkplaceManagerId(),
                        x.getEmployeeId(),
                        x.getWorkplaceId(),
                        x.getHistoryPeriod()))
                .collect(Collectors.toList());
    }
}
