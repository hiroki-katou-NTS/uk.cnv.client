/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.record.ac.workplace.affiliate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.adapter.workplace.WorkPlaceConfig;
import nts.uk.ctx.at.record.dom.adapter.workplace.WorkplaceConfigHistory;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffAtWorkplaceImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkPlaceSidImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceDto;
//import nts.uk.ctx.bs.employee.pub.workplace.AffAtWorkplaceExport;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.config.WorkPlaceConfigPub;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;

/**
 * The Class AffWorkplaceAdapterImpl.
 */
@Stateless
public class AffWorkplaceAdapterImpl implements AffWorkplaceAdapter {

	/** The wkp pub. */
	
	@Inject
	private WorkPlaceConfigPub workPlaceConfigPub;
	
	@Inject
	private WorkplacePub workplacePub;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter#findBySid(java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<AffWorkplaceDto> findBySid(String employeeId, GeneralDate baseDate) {

		Optional<SWkpHistExport> opSWkpHistExport = this.workplacePub.findBySid(employeeId, baseDate);
		
		AffWorkplaceDto affWorkplaceDto = new AffWorkplaceDto();
		
		if(opSWkpHistExport.isPresent()){
		affWorkplaceDto = AffWorkplaceDto.builder().workplaceId(opSWkpHistExport.get().getWorkplaceId())
				.workplaceCode(opSWkpHistExport.get().getWorkplaceCode())
				.workplaceName(opSWkpHistExport.get().getWorkplaceName()).build();
		}
		
		return Optional.ofNullable(affWorkplaceDto);
	}

	@Override
	public Optional<AffWorkPlaceSidImport> findBySidAndDate(String employeeId, GeneralDate baseDate) {
		Optional<SWkpHistExport> opSWkpHistExport = this.workplacePub.findBySid(employeeId, baseDate);
		AffWorkPlaceSidImport affWorkPlaceSidImport = new AffWorkPlaceSidImport();
		if(opSWkpHistExport.isPresent() && opSWkpHistExport != null){
			affWorkPlaceSidImport = new AffWorkPlaceSidImport(opSWkpHistExport.get().getDateRange(),
					opSWkpHistExport.get().getEmployeeId(),
					opSWkpHistExport.get().getWorkplaceId(),
					opSWkpHistExport.get().getWorkplaceCode(),
					opSWkpHistExport.get().getWorkplaceName(),
					opSWkpHistExport.get().getWkpDisplayName());
		} else {
			return Optional.empty();
		}
		
		return Optional.of(affWorkPlaceSidImport);
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
	public Map<GeneralDate, Map<String, List<String>>> findAffiliatedWorkPlaceIdsToRoot(String companyId, List<String> employeeId, DatePeriod baseDate){
		return this.workplacePub.findWpkIdsBySids(companyId, employeeId, baseDate);
	}

	@Override
	public List<AffAtWorkplaceImport> findBySIdAndBaseDate(List<String> employeeIds, GeneralDate baseDate) {
		return this.workplacePub.findBySIdAndBaseDateV2(employeeIds, baseDate).stream().map(item -> {
			return new AffAtWorkplaceImport(item.getEmployeeId(), item.getWorkplaceId(), item.getHistoryID());
		}).collect(Collectors.toList());
	}

	@Override
	public List<DatePeriod> getLstPeriod(String companyId, DatePeriod period) {
		List<DatePeriod> datePeriods = this.workplacePub.getLstPeriod(companyId, period);
		return datePeriods;
	}

	@Override
	public List<WorkPlaceConfig> findByCompanyIdAndPeriod(String companyId, DatePeriod datePeriod) {
		return this.workPlaceConfigPub.findByCompanyIdAndPeriod(companyId, datePeriod).stream().map(item -> {
			return new WorkPlaceConfig(item.getCompanyId(), item.getWkpConfigHistory().stream().map(i -> {
				return new WorkplaceConfigHistory(i.getHistoryId(), i.getPeriod());
			}).collect(Collectors.toList()));
		}).collect(Collectors.toList());
	}

	@Override
	public List<String> getUpperWorkplace(String companyID, String workplaceID, GeneralDate date) {
		
		return this.workplacePub.getUpperWorkplace(companyID, workplaceID, date);
	}

	@Override
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
		return this.workplacePub.getWorkplaceIdAndUpper(companyId, baseDate, workplaceId);
	}

}