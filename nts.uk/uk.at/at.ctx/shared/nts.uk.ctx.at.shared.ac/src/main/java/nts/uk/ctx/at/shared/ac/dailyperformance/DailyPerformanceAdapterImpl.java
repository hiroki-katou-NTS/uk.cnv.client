package nts.uk.ctx.at.shared.ac.dailyperformance;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.adapter.dailyperformance.ApproveRootStatusForEmpImport;
import nts.uk.ctx.at.shared.dom.adapter.dailyperformance.DailyPerformanceAdapter;
import nts.uk.ctx.workflow.pub.service.ApprovalRootStatePub;
@Stateless
public class DailyPerformanceAdapterImpl implements DailyPerformanceAdapter {

	
	@Inject
	private ApprovalRootStatePub approvalRootStatePub;
	


	@Override
	public boolean checkDataApproveed(GeneralDate startDate, GeneralDate endDate, String approverID, Integer rootType,String companyID) {
		return approvalRootStatePub.checkDataApproveed(startDate, endDate, approverID, rootType, companyID);
	}
	
	public List<ApproveRootStatusForEmpImport> getApprovalByListEmplAndListApprovalRecordDate(List<GeneralDate> approvalRecordDates, List<String> employeeID,Integer rootType){
		return approvalRootStatePub.getApprovalByListEmplAndListApprovalRecordDate(approvalRecordDates, employeeID, rootType).stream().map(item ->{
			return new ApproveRootStatusForEmpImport(item.getEmployeeID(), item.getAppDate(), item.getApprovalStatus().value);
		}).collect(Collectors.toList());
	}
}
