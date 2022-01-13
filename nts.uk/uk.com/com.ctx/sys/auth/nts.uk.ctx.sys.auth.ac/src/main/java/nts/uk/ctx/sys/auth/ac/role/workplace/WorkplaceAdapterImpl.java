/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.sys.auth.ac.role.workplace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.pub.employee.workplace.export.WorkplaceExportPub;
import nts.uk.ctx.bs.employee.pub.workplace.AffAtWorkplaceExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport3;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.AffWorkplaceHistImport;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.AffWorkplaceImport;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.AffiliationWorkplace;
import nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManagerRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class WorkplaceAdapterImpl.
 */
@Stateless
public class WorkplaceAdapterImpl implements WorkplaceAdapter {

	/** The sy workplace pub. */
//	@Inject
//	private SyWorkplacePub syWorkplacePub;
	
	@Inject
	private WorkplaceManagerRepository workplaceManagerRepository;
	
	@Inject
	private WorkplacePub workplacePub;
	
	@Inject
	private WorkplaceExportPub workplaceExportPub;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter#findListWkpIdByBaseDate(nts.arc.time.GeneralDate)
	 */
	@Override
	public List<String> findListWkpIdByBaseDate(GeneralDate baseDate) {
		return workplacePub.getListWorkplaceIdByBaseDate(baseDate);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter#findWkpByBaseDateAndEmployeeId(nts.arc.time.GeneralDate, java.lang.String)
	 */
	@Override
	public Optional<AffWorkplaceHistImport> findWkpByBaseDateAndEmployeeId(GeneralDate baseDate, String employeeId) {

		AffWorkplaceHistImport affWorkplaceHistImport = new AffWorkplaceHistImport();

		Optional<SWkpHistExport> opSWkpHistExport = workplacePub.findBySid(employeeId, baseDate);
		if (opSWkpHistExport.isPresent()) {			
			affWorkplaceHistImport.setWorkplaceId(opSWkpHistExport.get().getWorkplaceId());			
		}
		return Optional.ofNullable(affWorkplaceHistImport);
	}

	/* (non-Javadoc)
	 * @see nts.uk.ctx.sys.auth.dom.adapter.workplace.WorkplaceAdapter#findListWorkplaceIdByCidAndWkpIdAndBaseDate(java.lang.String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<String> findListWorkplaceIdByCidAndWkpIdAndBaseDate(String companyId, String workplaceId,
			GeneralDate baseDate) {	
		return workplacePub.getWorkplaceIdAndChildren(companyId, baseDate, workplaceId);
	}

	@Override
	public List<AffWorkplaceImport> findListSIdByCidAndWkpIdAndPeriod(String workplaceId, GeneralDate startDate,
			GeneralDate endDate) {
		return workplacePub.findListSIdByCidAndWkpIdAndPeriod(workplaceId, startDate, endDate).stream().map(
				item -> new AffWorkplaceImport(item.getEmployeeId(), item.getJobEntryDate(), item.getRetirementDate()))
				.collect(Collectors.toList());
	}

	private AffiliationWorkplace toImport (AffAtWorkplaceExport ex){
		return new AffiliationWorkplace(ex.getHistoryID(), ex.getEmployeeId(), ex.getWorkplaceId());
	}
	
	@Override
	public List<AffiliationWorkplace> findByListEmpIDAndDate(List<String> listEmployeeID, GeneralDate baseDate) {
		// TODO Auto-generated method stub
		return workplacePub.findBySIdAndBaseDate(listEmployeeID, baseDate).stream().map(c -> toImport(c)).collect(Collectors.toList());
	}

	@Override
	public List<AffWorkplaceImport> findListSIdWkpIdAndPeriod(List<String> lstWkpId, GeneralDate startDate,
			GeneralDate endDate) {
		return workplacePub.getByLstWkpIdAndPeriod(lstWkpId, startDate, endDate).stream().map(
				item -> new AffWorkplaceImport(item.getEmployeeId(), item.getJobEntryDate(), item.getRetirementDate()))
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getWorkplaceId(GeneralDate baseDate, String employeeId) {
		String companyID = AppContexts.user().companyId();
		List<String> subListWorkPlace = new ArrayList<>();
		// (Lấy all domain 「職場管理者」)
		List<WorkplaceManager> listWorkplaceManager = workplaceManagerRepository.findListWkpManagerByEmpIdAndBaseDate(employeeId, GeneralDate.today());
		for (WorkplaceManager workplaceManager : listWorkplaceManager) {
			if(!subListWorkPlace.contains(workplaceManager.getWorkplaceId())){
				subListWorkPlace.add(workplaceManager.getWorkplaceId());
				List<String> list = this.getAllChildrenOfWkpIdNEW(companyID, baseDate, workplaceManager.getWorkplaceId());
				subListWorkPlace.addAll(list);
//				subListWorkPlace.addAll(syWorkplacePub.findListWorkplaceIdByCidAndWkpIdAndBaseDate(companyID, workplaceManager.getWorkplaceId(), GeneralDate.today()));
			}
		}
		return subListWorkPlace.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public Optional<AffWorkplaceHistImport> findWkpByBaseDateAndSIdNEW(GeneralDate baseDate, String employeeId) {

		AffWorkplaceHistImport affWorkplaceHistImport = new AffWorkplaceHistImport();


		Optional<SWkpHistExport> opSWkpHistExport = workplacePub.findBySid(employeeId, baseDate);

		if (opSWkpHistExport.isPresent()) {			
			affWorkplaceHistImport.setWorkplaceId(opSWkpHistExport.get().getWorkplaceId());			
		}
		return Optional.ofNullable(affWorkplaceHistImport);
	}

	@Override
	public List<String> getAllChildrenOfWkpIdNEW(String companyId, GeneralDate baseDate, String parentWorkplaceId) {
		return workplacePub.getAllChildrenOfWorkplaceId(companyId, baseDate, parentWorkplaceId);
	}

	@Override
	public List<String> getWorkplaceIdAndChildren(String companyId, GeneralDate baseDate, String workplaceId) {
		return workplacePub.getWorkplaceIdAndChildren(companyId, baseDate, workplaceId);
	}

	@Override
	public Map<String, String> getAWorkplace(String employeeID, GeneralDate date) {
		//$職場ID =  [No.650]社員が所属している職場を取得する(社員ID,基準日)
		AffWorkplaceHistoryItemExport i =  workplacePub.getAffWkpHistItemByEmpDate(employeeID, date);
		
		Map<String, String> result = new HashMap<String, String>();
		if(i != null) {
			result.put(employeeID, i.getWorkplaceId());
		}
		// return map <社員ID,$職場ID>
		return result;
	}

	@Override
	public Map<String, String> getByListIds(List<String> workPlaceIds, GeneralDate baseDate) {
		Map<String, String> result = new HashMap<String, String>();
		//	$所属職場 = 職場に所属する社員Publish.取得する(基準日,職場リスト)
		List<AffWorkplaceHistoryItemExport3> list =  workplaceExportPub.getByListId(workPlaceIds, baseDate);
		for (AffWorkplaceHistoryItemExport3 i : list) {
			result.put(i.getEmployeeId(), i.getWorkplaceId());
		}
//		return $所属職場：
//				map <$.社員ID,$.職場ID>
		return result;
	}

}
