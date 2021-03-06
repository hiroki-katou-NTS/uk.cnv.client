package nts.uk.ctx.at.function.dom.processexecution.updateprocessreexeccondition.refinementprocess;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.function.dom.adapter.AffWorkplaceHistoryImport;
import nts.uk.ctx.at.function.dom.adapter.WorkplaceWorkRecordAdapter;
import nts.uk.ctx.at.function.dom.processexecution.UpdateProcessAutoExecution;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.BusinessTypeOfEmployeeHistory;
import nts.uk.ctx.at.shared.dom.employeeworkway.businesstype.employee.repository.BusinessTypeEmpOfHistoryRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * 絞り込み処理
 * @author tutk
 *
 */
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class RefinementProcess {
	
	@Inject
	private WorkplaceWorkRecordAdapter workplaceWorkRecordAdapter;
	
	@Inject
	private BusinessTypeEmpOfHistoryRepository businessTypeEmpOfHistAdapter;
	
	public void refinementProcess(String companyId, List<String> empIds,Set<String> setEmpIds,List<String> newEmpIdList, GeneralDate startDate, UpdateProcessAutoExecution procExec) {
		if (procExec.getReExecCondition().getRecreateTransfer().equals(NotUseAtr.USE)) {
			// 異動者の絞り込み todo request list 189
			List<AffWorkplaceHistoryImport> list = workplaceWorkRecordAdapter.getWorkplaceBySidsAndBaseDate(empIds,
					startDate);
			list.forEach(emp -> {
				emp.getHistoryItems().forEach(x -> {
					if (x.start().afterOrEquals(startDate)) {
						setEmpIds.add(emp.getSid());
						return;
					}
				});
			});
		}
		if (procExec.getReExecCondition().getRecreatePersonChangeWkt().equals(NotUseAtr.USE)) {
			// 勤務種別の絞り込み
			this.refineWorkType(companyId, empIds, startDate, newEmpIdList);
		}
	}
	
	// 勤務種別の絞り込み
	private void refineWorkType(String companyId, List<String> empIdList, GeneralDate startDate, List<String> newEmpIdList) {
		for (String empId : empIdList) {
			// ドメインモデル「社員の勤務種別の履歴」を取得する
			Optional<BusinessTypeOfEmployeeHistory> businessTypeOpt = this.businessTypeEmpOfHistAdapter
					.findByEmployee(AppContexts.user().companyId(), empId);
			if (businessTypeOpt.isPresent()) {
				BusinessTypeOfEmployeeHistory businessTypeOfEmpHistImport = businessTypeOpt.get();
				List<DateHistoryItem> lstDate = businessTypeOfEmpHistImport.getHistory();
				int size = lstDate.size();
				for (int i = 0; i < size; i++) {
					DateHistoryItem dateHistoryItem = lstDate.get(i);
					if (dateHistoryItem.start().compareTo(startDate) >= 0) {
						newEmpIdList.add(empId);
						break;
					}
				}
			}
		}
	}
}
