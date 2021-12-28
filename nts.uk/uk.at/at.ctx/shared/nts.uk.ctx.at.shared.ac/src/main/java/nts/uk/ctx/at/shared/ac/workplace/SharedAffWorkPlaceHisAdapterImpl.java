package nts.uk.ctx.at.shared.ac.workplace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workplace.SharedAffWorkPlaceHisImport;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

@Stateless
public class SharedAffWorkPlaceHisAdapterImpl implements SharedAffWorkPlaceHisAdapter{
	
//	@Inject
//	private SyWorkplacePub wkpPub;
	
	@Inject
	private WorkplacePub workplacePub;

	@Override
	public Optional<SharedAffWorkPlaceHisImport> getAffWorkPlaceHis(String employeeId, GeneralDate processingDate) {

		Optional<SWkpHistExport> opSWkpHistExport = this.workplacePub.findBySid(employeeId, processingDate);
		
		if (!opSWkpHistExport.isPresent()) {
			return Optional.empty();
		}
		
		SharedAffWorkPlaceHisImport sharedAffWorkPlaceHisImport = new SharedAffWorkPlaceHisImport(opSWkpHistExport.get().getDateRange(),
				opSWkpHistExport.get().getEmployeeId(), opSWkpHistExport.get().getWorkplaceId(),
				opSWkpHistExport.get().getWorkplaceCode(), opSWkpHistExport.get().getWorkplaceName(),
				opSWkpHistExport.get().getWkpDisplayName());
		
		return Optional.of(sharedAffWorkPlaceHisImport);
	}
	
	@Override
	public Map<GeneralDate, Map<String, Optional<SharedAffWorkPlaceHisImport>>> getAffWorkPlaceHisClones(String companyId, List<String> employeeId, DatePeriod processingDate) {
		Map<GeneralDate, Map<String, Optional<SWkpHistExport>>> sWkpHistExportMap = this.workplacePub.findBySid(companyId, employeeId, processingDate);
		
		Map<GeneralDate, Map<String, Optional<SharedAffWorkPlaceHisImport>>> resultMap = new HashMap<GeneralDate, Map<String,Optional<SharedAffWorkPlaceHisImport>>>();
		
		for (val sWkpHistExpor : sWkpHistExportMap.entrySet()) {
			Map<String, Optional<SharedAffWorkPlaceHisImport>> sharedAffWorkPlaceHisImportMap = new HashMap<String, Optional<SharedAffWorkPlaceHisImport>>();
			for (val opSWkpHistExport : sWkpHistExpor.getValue().entrySet()) {
				if (!opSWkpHistExport.getValue().isPresent()) {
					sharedAffWorkPlaceHisImportMap.put(opSWkpHistExport.getKey(), Optional.empty());
				} else {
				
				SharedAffWorkPlaceHisImport sharedAffWorkPlaceHisImport = new SharedAffWorkPlaceHisImport(opSWkpHistExport.getValue().get().getDateRange(),
						opSWkpHistExport.getValue().get().getEmployeeId(), opSWkpHistExport.getValue().get().getWorkplaceId(),
						opSWkpHistExport.getValue().get().getWorkplaceCode(), opSWkpHistExport.getValue().get().getWorkplaceName(),
						opSWkpHistExport.getValue().get().getWkpDisplayName());
				
				sharedAffWorkPlaceHisImportMap.put(opSWkpHistExport.getKey(), Optional.of(sharedAffWorkPlaceHisImport));
				}
			}
			resultMap.put(sWkpHistExpor.getKey(), sharedAffWorkPlaceHisImportMap);
		}
		
		return resultMap;
	}

	@Override
	public List<String> findParentWpkIdsByWkpId(String companyId, String workplaceId, GeneralDate date) {
		List<String> workPlaceIDList = this.workplacePub.getWorkplaceIdAndChildren(companyId, date, workplaceId);
		return workPlaceIDList;
	}
	
	@Override
	public List<String> findAffiliatedWorkPlaceIdsToRootRequire(CacheCarrier cacheCarrier, String companyId,String employeeId, GeneralDate baseDate) {
		//Require対応まち
		return this.workplacePub.findWpkIdsBySid(companyId ,employeeId, baseDate);
	}
	
	@Override
	public List<String> findAffiliatedWorkPlaceIdsToRoot(String companyId,String employeeId, GeneralDate baseDate) {
		return this.workplacePub.findWpkIdsBySid(companyId ,employeeId, baseDate);
	}

	@Override
	public List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate baseDate) {
		return workplacePub.findWpkIdsBySid(companyId, employeeId, baseDate);
	}
	
	@Override
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
		return this.workplacePub.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);

	}

}
