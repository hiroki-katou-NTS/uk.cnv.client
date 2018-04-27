package nts.uk.ctx.sys.auth.dom.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmpWorkplaceHistoryAdapter;
import nts.uk.ctx.sys.auth.dom.adapter.employee.employeeinfo.EmpWorkplaceHistoryImport;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.sys.auth.dom.role.EmployeeReferenceRange;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AcquireListWorkplaceByEmpIDServiceImpl implements AcquireListWorkplaceByEmpIDService {
	@Inject
	private EmpWorkplaceHistoryAdapter empWorkplaceHistoryAdapter;

	@Inject
	private WorkplaceManagerRepository workplaceManagerRepository;

	@Inject
	private WorkplaceAdapter workplaceAdapter;

	@Override
	public List<String> getListWorkPlaceID(String employeeID, int empRange, GeneralDate referenceDate) {
		List<String> listWorkPlaceID3 = new ArrayList<>();
		if (empRange == EmployeeReferenceRange.ALL_EMPLOYEE.value) {
			// imported（権限管理）「職場」を取得する Lấy Request 157
			List<String> listWorkPlaceID = empWorkplaceHistoryAdapter.getListWorkPlaceIDByDate(referenceDate);
			return listWorkPlaceID;
		} else {
			// ドメインモデル「職場管理者」をすべて取得する
			// (Lấy all domain 「職場管理者」)
			List<WorkplaceManager> listWorkplaceManager = workplaceManagerRepository.findListWkpManagerByEmpIdAndBaseDate(employeeID, referenceDate);
			List<String> listWorkPlaceID2 = listWorkplaceManager.stream().map(c -> c.getWorkplaceId()).collect(Collectors.toList());
			// imported（権限管理）「所属職場履歴」を取得する Lay RequestList 30
			Optional<EmpWorkplaceHistoryImport> workPlaceImport = empWorkplaceHistoryAdapter.findBySid(employeeID, referenceDate);
			String workPlaceID1 = workPlaceImport.get().getWorkplaceID();
			if (empRange == EmployeeReferenceRange.DEPARTMENT_AND_CHILD.value) {
				// 配下の職場をすべて取得する Lay Request list 154
				listWorkPlaceID3 = workplaceAdapter.findListWorkplaceIdByCidAndWkpIdAndBaseDate(AppContexts.user().companyId(), workPlaceID1, referenceDate);
			}
			List<String> listResultWorkPlaceID = new ArrayList<>();
			listResultWorkPlaceID.add(workPlaceID1);
			listResultWorkPlaceID.addAll(listWorkPlaceID2);
			listResultWorkPlaceID.addAll(listWorkPlaceID3);
			// filter duplicate WorkPlaceID
			return listResultWorkPlaceID.stream().distinct().collect(Collectors.toList());
		}
	}

}
