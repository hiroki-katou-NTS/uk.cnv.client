package nts.uk.ctx.at.record.ac.workplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.EmployeeInfoImported;
import nts.uk.ctx.at.record.dom.adapter.workplace.SWkpHistRcImported;
import nts.uk.ctx.at.record.dom.adapter.workplace.SyWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.WorkplaceInformationImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.WorkplaceInforImport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

/**
 *
 * @author sonnh1
 *
 */
@Stateless
public class SyWorkplaceAdapterImp implements SyWorkplaceAdapter {

//	@Inject
//	private SyWorkplacePub syWorkplacePub;

	@Inject
	private WorkplacePub workplacePub;

	@Override
	public Map<String, Pair<String, String>> getWorkplaceMapCodeBaseDateName(String companyId,
			List<String> wpkCodes, List<GeneralDate> baseDates) {
		return this.workplacePub.getWorkplaceMapCodeBaseDateName(companyId, wpkCodes, baseDates);
	}

	@Override
	public Optional<SWkpHistRcImported> findBySid(String employeeId, GeneralDate baseDate) {
		return this.workplacePub.findBySid(employeeId, baseDate)
				.map(x -> new SWkpHistRcImported(x.getDateRange(), x.getEmployeeId(), x.getWorkplaceId(),
						x.getWorkplaceCode(), x.getWorkplaceName(), x.getWkpDisplayName()));
	}

	//社員（List）と基準日から所属職場履歴項目を取得する
	@Override
	public List<SWkpHistRcImported> findBySid(List<String> employeeIds, GeneralDate baseDate) {
		return workplacePub.findBySId(employeeIds, baseDate).stream()
				.map(x -> new SWkpHistRcImported(x.getDateRange(), x.getEmployeeId(), x.getWorkplaceId(),
						x.getWorkplaceCode(), x.getWorkplaceName(), x.getWkpDisplayName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<WorkplaceInforImport> getWorkplaceInforByWkpIds(String companyId, List<String> listWorkplaceId,
			GeneralDate baseDate) {
		return workplacePub.getWorkplaceInforByWkpIds(companyId, listWorkplaceId, baseDate).stream()
				.map(x -> new WorkplaceInforImport(x.getWorkplaceId(), x.getHierarchyCode(), x.getWorkplaceCode(), 
						x.getWorkplaceName(), x.getWorkplaceDisplayName(), x.getWorkplaceGenericName(), x.getWorkplaceExternalCode()))
				.collect(Collectors.toList());
	}
	@Override
	public List<EmployeeInfoImported> getLstEmpByWorkplaceIdsAndPeriod(List<String> workplaceIds, DatePeriod period) {
		return workplacePub.getLstEmpByWorkplaceIdsAndPeriod(workplaceIds, period).stream()
				.map(x -> new EmployeeInfoImported(x.getSid(), x.getEmployeeCode(), x.getEmployeeName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<WorkplaceInformationImport> getByCidAndPeriod(String companyId, DatePeriod datePeriod) {
		return workplacePub.getByCidAndPeriod(companyId, datePeriod).stream().map(x ->
				new WorkplaceInformationImport(
						x.getCompanyId(),
						x.isDeleteFlag(),
						x.getWorkplaceHistoryId(),
						x.getWorkplaceId(),
						x.getWorkplaceCode(),
						x.getWorkplaceName(),
						x.getWorkplaceGeneric(),
						x.getWorkplaceDisplayName(),
						x.getHierarchyCode(),
						x.getWorkplaceExternalCode(),
						x.getPeriod()
				)).collect(Collectors.toList());
	}

	@Override
	public String getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date) {
		// TODO Auto-generated method stub
		return this.workplacePub.getAffWkpHistItemByEmpDate(employeeID, date).getWorkplaceId();
	}

}
