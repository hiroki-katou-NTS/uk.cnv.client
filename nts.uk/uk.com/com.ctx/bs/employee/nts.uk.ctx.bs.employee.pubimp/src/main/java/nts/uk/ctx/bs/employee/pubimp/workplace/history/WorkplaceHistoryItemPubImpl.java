package nts.uk.ctx.bs.employee.pubimp.workplace.history;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.pub.workplace.history.WorkplaceHistoryItemExport;
import nts.uk.ctx.bs.employee.pub.workplace.history.WorkplaceHistoryItemPub;

@Stateless
public class WorkplaceHistoryItemPubImpl implements WorkplaceHistoryItemPub {

	@Inject
	private AffWorkplaceHistoryItemRepository workplaceHistoryItemRepo;
	
	@Override
	public List<WorkplaceHistoryItemExport> findByEmpIdsAndDate(List<String> empIds, GeneralDate baseDate) {
		return workplaceHistoryItemRepo.getAffWrkplaHistItemByListEmpIdAndDateV2(baseDate, empIds).stream()
				.map(w -> new WorkplaceHistoryItemExport(w.getHistoryId(), w.getEmployeeId(), w.getWorkplaceId()))
				.collect(Collectors.toList());
	}

}
