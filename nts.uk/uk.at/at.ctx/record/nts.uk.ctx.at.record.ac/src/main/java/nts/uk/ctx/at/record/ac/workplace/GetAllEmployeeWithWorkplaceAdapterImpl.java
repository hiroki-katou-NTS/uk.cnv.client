package nts.uk.ctx.at.record.ac.workplace;

import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.workplace.GetAllEmployeeWithWorkplaceAdapter;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceExportPub;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport3;

/**
 *
 * @author sonnlb
 *
 */
@Stateless
public class GetAllEmployeeWithWorkplaceAdapterImpl implements GetAllEmployeeWithWorkplaceAdapter {

	@Inject
	private WorkplaceExportPub workplaceExportPub;

	@Override
	public Map<String, String> get(String companyId, GeneralDate baseDate) {
		return this.workplaceExportPub.getByCID(companyId, baseDate).stream().collect(Collectors
				.toMap(AffWorkplaceHistoryItemExport3::getEmployeeId, AffWorkplaceHistoryItemExport3::getWorkplaceId));
	}

}
