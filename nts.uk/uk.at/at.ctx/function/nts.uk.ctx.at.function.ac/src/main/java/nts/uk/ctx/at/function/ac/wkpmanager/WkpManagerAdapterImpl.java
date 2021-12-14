package nts.uk.ctx.at.function.ac.wkpmanager;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.dom.adapter.wkpmanager.WkpManagerAdapter;
import nts.uk.ctx.at.function.dom.adapter.wkpmanager.WkpManagerImport;
import nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager.WorkplaceManagerAdapter;
import nts.uk.ctx.at.record.dom.adapter.auth.wkpmanager.WorkplaceManagerImport;
import nts.uk.ctx.sys.auth.pub.wkpmanager.WorkplaceManagerPub;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class WkpManagerAdapterImpl implements WkpManagerAdapter {

    @Inject
    private WorkplaceManagerPub workplaceManagerPub;

    @Override
    public List<WkpManagerImport> findByPeriodAndBaseDate(String wkpId, GeneralDate baseDate) {
        return workplaceManagerPub.findByPeriodAndBaseDate(wkpId, baseDate).stream().map(x ->
            new WkpManagerImport(
                x.getWorkplaceManagerId(),
                x.getEmployeeId(),
                x.getWorkplaceId(),
                x.getHistoryPeriod()))
            .collect(Collectors.toList());
    }
}
