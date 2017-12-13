/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.bs.employee.pubimp.workplace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository_v1;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository_v1;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory_ver1;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfoRepository;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceHierarchy;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceInfo;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceInfoRepository;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub;
import nts.uk.ctx.bs.employee.pub.workplace.WkpCdNameExport;

/**
 * The Class WorkplacePubImp.
 */
@Stateless
public class WorkplacePubImp implements SyWorkplacePub {

	/** The first item index. */
	private final int FIRST_ITEM_INDEX = 0;

	/** The workplace config info repository. */
	@Inject
	private WorkplaceConfigInfoRepository workplaceConfigInfoRepo;

	/** The workplace info repository. */
	@Inject
	private WorkplaceInfoRepository workplaceInfoRepo;

	/** The workplace history repository. */
	@Inject
	private AffWorkplaceHistoryRepository workplaceHistoryRepo;
	
	/**AffWorkplaceHistoryRepository_v1*/
	@Inject
	private AffWorkplaceHistoryRepository_v1 affWorkplaceHistoryRepository_v1;
	
	/**AffWorkplaceHistoryItemRepository_v1*/
	@Inject
	private AffWorkplaceHistoryItemRepository_v1 affWorkplaceHistoryItemRepository_v1;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.WorkplacePub#findWpkIds(java.lang.
	 * String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<String> findWpkIdsByWkpCode(String companyId, String wpkCode,
			GeneralDate baseDate) {
		return workplaceInfoRepo.findByWkpCd(companyId, wpkCode, baseDate).stream()
				.map(item -> item.getWorkplaceId()).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.WorkplacePub#findByWkpId(java.lang.
	 * String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<WkpCdNameExport> findByWkpId(String workplaceId, GeneralDate baseDate) {
		Optional<WorkplaceInfo> optWorkplaceInfo = workplaceInfoRepo.findByWkpId(workplaceId,
				baseDate);

		// Check exist
		if (!optWorkplaceInfo.isPresent()) {
			return Optional.empty();
		}

		// Return
		WorkplaceInfo wkpInfo = optWorkplaceInfo.get();
		return Optional.of(WkpCdNameExport.builder().wkpCode(wkpInfo.getWorkplaceCode().v())
				.wkpName(wkpInfo.getWorkplaceName().v()).build());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.WorkplacePub#getWorkplaceId(java.
	 * lang.String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public String getWorkplaceId(String companyId, String employeeId, GeneralDate baseDate) {
		Optional<AffWorkplaceHistory_ver1> affWrkPlc = affWorkplaceHistoryRepository_v1.getByEmpIdAndStandDate(employeeId, baseDate);
		if(!affWrkPlc.isPresent()) 
			return null;
		String historyId = affWrkPlc.get().getHistoryItems().get(0).identifier();
		Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWorkplaceHistoryItemRepository_v1.getByHistId(historyId);
		if(affWrkPlcItem.isPresent())
			return affWrkPlcItem.get().getWorkplaceId();
		
		return null;
//		// Query
//		List<AffWorkplaceHistory> affWorkplaceHistories = workplaceHistoryRepo
//				.searchWorkplaceHistoryByEmployee(employeeId, baseDate);
//
//		List<String> wkpIds = affWorkplaceHistories.stream().map(item -> item.getWorkplaceId().v())
//				.collect(Collectors.toList());
//
//		// Check exist
//		if (CollectionUtil.isEmpty(wkpIds)) {
//			return null;
//		}
//
//		// Return workplace id
//		return wkpIds.get(FIRST_ITEM_INDEX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.WorkplacePub#findWpkIdsBySid(java.
	 * lang.String, java.lang.String, nts.arc.time.GeneralDate)
	 */
	@Override
	public List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate baseDate) {
		Optional<AffWorkplaceHistoryItem> affWrkPlcItem  = getAffWrkPlcHistItem(employeeId, baseDate);
		List<String> lstWpkIds = new ArrayList<>();		
		return lstWpkIds;
//		// Query
//		List<AffWorkplaceHistory> affWorkplaceHistories = workplaceHistoryRepo
//				.searchWorkplaceHistoryByEmployee(employeeId, baseDate);
//
//		// Return
//		return affWorkplaceHistories.stream().map(item -> {
//			return item.getWorkplaceId().v();
//		}).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#findBySid(java.lang.
	 * String, nts.arc.time.GeneralDate)
	 */
	@Override
	public Optional<SWkpHistExport> findBySid(String employeeId, GeneralDate baseDate) {
		//get AffWorkplaceHistory_ver1
		Optional<AffWorkplaceHistory_ver1> affWrkPlc = affWorkplaceHistoryRepository_v1.getByEmpIdAndStandDate(employeeId, baseDate);
		if(!affWrkPlc.isPresent()) 
			return Optional.empty();
		
		//get AffWorkplaceHistoryItem
		String historyId = affWrkPlc.get().getHistoryItems().get(0).identifier();
		Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWorkplaceHistoryItemRepository_v1.getByHistId(historyId);
		if(!affWrkPlcItem.isPresent())
			return Optional.empty();
		
		// Get workplace info.
		Optional<WorkplaceInfo> optWorkplaceInfo = workplaceInfoRepo.findByWkpId(affWrkPlcItem.get().getWorkplaceId(), baseDate);

		// Check exist
		if (!optWorkplaceInfo.isPresent()) {
			return Optional.empty();
		}

		// Return workplace id
		WorkplaceInfo wkpInfo = optWorkplaceInfo.get();
		
		return Optional.of(SWkpHistExport.builder().dateRange(affWrkPlc.get().getHistoryItems().get(0).span())
				.employeeId(affWrkPlc.get().getEmployeeId())
				.workplaceId(wkpInfo.getWorkplaceId()).workplaceCode(wkpInfo.getWorkplaceCode().v())
				.workplaceName(wkpInfo.getWorkplaceName().v())
				.wkpDisplayName(wkpInfo.getWkpDisplayName().v()).build());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#
	 * findParentWpkIdsByWkpId(java.lang.String, java.lang.String,
	 * nts.arc.time.GeneralDate)
	 */
	@Override
	public List<String> findParentWpkIdsByWkpId(String companyId, String workplaceId,
			GeneralDate date) {
		// Get config info
		Optional<WorkplaceConfigInfo> optWorkplaceConfigInfo = workplaceConfigInfoRepo
				.findAllParentByWkpId(companyId, date, workplaceId);

		// Check exist
		if (!optWorkplaceConfigInfo.isPresent()) {
			return Collections.emptyList();
		}

		// Return
		return optWorkplaceConfigInfo.get().getLstWkpHierarchy().stream()
				.map(WorkplaceHierarchy::getWorkplaceId).collect(Collectors.toList());
	}
	
	private Optional<AffWorkplaceHistoryItem> getAffWrkPlcHistItem(String employeeId, GeneralDate baseDate){
		//get AffWorkplaceHistory_ver1
		Optional<AffWorkplaceHistory_ver1> affWrkPlc = affWorkplaceHistoryRepository_v1.getByEmpIdAndStandDate(employeeId, baseDate);
		if(!affWrkPlc.isPresent()) 
			return Optional.empty();
		
		//get AffWorkplaceHistoryItem
		String historyId = affWrkPlc.get().getHistoryItems().get(0).identifier();
		Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWorkplaceHistoryItemRepository_v1.getByHistId(historyId);
		if(!affWrkPlcItem.isPresent())
			return Optional.empty();
		return affWrkPlcItem;
	}
}
