package nts.uk.ctx.bs.employee.pubimp.workplace.affiliate;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.pub.workplace.affiliate.AffWorkplaceHistoryItemExport;
import nts.uk.ctx.bs.employee.pub.workplace.affiliate.AffWorkplaceHistoryItemPub;

@Stateless
public class AffWorkplaceHistoryItemPubImpl implements AffWorkplaceHistoryItemPub {

    @Inject
    private AffWorkplaceHistoryItemRepository repo;

    @Override
    public List<AffWorkplaceHistoryItemExport> getListAffWkpHistItem(DatePeriod period, List<String> workplaceId) {
        List<AffWorkplaceHistoryItem> historyItems = repo.getAffWkpHistItemByListWkpIdAndDatePeriod(period, workplaceId);
        return historyItems.stream().map(x -> {
            return new AffWorkplaceHistoryItemExport(
                x.getHistoryId(),
                x.getEmployeeId(),
                x.getWorkplaceId());
        }).collect(Collectors.toList());
    }
}
