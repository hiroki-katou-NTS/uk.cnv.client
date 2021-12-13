package nts.uk.ctx.at.shared.ac.workplace.affiliate;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.workplace.affiliate.AffWorkplaceHistoryItemImport;
import nts.uk.ctx.at.shared.dom.adapter.workplace.affiliate.SharedAffWorkplaceHistoryItemAdapter;
import nts.uk.ctx.bs.employee.pub.workplace.affiliate.AffWorkplaceHistoryItemPub;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

@Stateless
public class SharedAffWorkplaceHistoryItemAdapterImpl implements SharedAffWorkplaceHistoryItemAdapter {

    @Inject
    private AffWorkplaceHistoryItemPub affWorkplaceHistoryItemPub;
    
    @Inject
	private WorkplacePub workplacePub;

    @Override
    public List<AffWorkplaceHistoryItemImport> getListAffWkpHistItem(DatePeriod period, List<String> workplaceId) {
        return affWorkplaceHistoryItemPub.getListAffWkpHistItem(period, workplaceId).stream().map(x ->
                new AffWorkplaceHistoryItemImport(
                        x.getHistoryId(),
                        x.getEmployeeId(),
                        x.getWorkplaceId()
                )).collect(Collectors.toList());
    }

	@Override
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
		return this.workplacePub.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);
	}
}
