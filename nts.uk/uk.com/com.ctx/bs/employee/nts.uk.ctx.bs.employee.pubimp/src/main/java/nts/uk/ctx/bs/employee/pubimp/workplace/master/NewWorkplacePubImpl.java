package nts.uk.ctx.bs.employee.pubimp.workplace.master;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.uk.ctx.bs.employee.pub.workplace.*;
import nts.uk.ctx.bs.employee.pub.workplace.config.WorkPlaceConfigExport;
import nts.uk.ctx.bs.employee.pub.workplace.config.WorkPlaceConfigPub;
import nts.uk.ctx.bs.employee.pub.workplace.config.WorkplaceConfigHistoryExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplaceInformationExport;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistItem;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistRepository;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfo;
import nts.uk.ctx.bs.employee.dom.employee.mgndata.EmployeeDataMngInfoRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistory;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItem;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryItemRepository;
import nts.uk.ctx.bs.employee.dom.workplace.affiliate.AffWorkplaceHistoryRepository;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceInfo;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceConfiguration;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceConfigurationRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformationRepository;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceExportService;
import nts.uk.ctx.bs.employee.dom.workplace.master.service.WorkplaceInforParam;
import nts.uk.ctx.bs.employee.pub.employee.SyEmployeePub;
import nts.uk.ctx.bs.employee.pub.workplace.AffAtWorkplaceExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport;
import nts.uk.ctx.bs.employee.pub.workplace.AffWorkplaceHistoryItemExport2;
import nts.uk.ctx.bs.employee.pub.workplace.ResultRequest597Export;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.SWkpHistWrkLocationExport;
import nts.uk.ctx.bs.employee.pub.workplace.WkpByEmpExport;
import nts.uk.ctx.bs.employee.pub.workplace.WkpCdNameExport;
import nts.uk.ctx.bs.employee.pub.workplace.WkpConfigAtTimeExport;
import nts.uk.ctx.bs.employee.pub.workplace.WkpInfoExport;
import nts.uk.ctx.bs.employee.pub.workplace.WorkPlaceHistExport;
import nts.uk.ctx.bs.employee.pub.workplace.WorkPlaceIdAndPeriod;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplaceInforExport;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

@Stateless
public class NewWorkplacePubImpl implements WorkplacePub {

	@Inject
	private WorkplaceExportService wkpExpService;

    @Inject
    private AffWorkplaceHistoryRepository affWkpHistRepo;

    @Inject
    private AffWorkplaceHistoryItemRepository affWkpHistItemRepo;
    
    @Inject
    private WorkplaceConfigurationRepository workplaceConfigurationRepository;
    
    @Inject
    private WorkplaceInformationRepository workplaceInformationRepository;
    
    @Inject
	private SyEmployeePub subEmp;
	
	@Inject
	private AffCompanyHistRepository affCompanyHistRepo;
	
	@Inject
	private EmployeeDataMngInfoRepository empDataMngRepo;

	@Inject
	private WorkPlaceConfigPub workPlaceConfigPub;

	@Override
	public List<WorkplaceInforExport> getWorkplaceInforByWkpIds(String companyId, List<String> listWorkplaceId,
																GeneralDate baseDate) {
		return wkpExpService.getWorkplaceInforFromWkpIds(companyId, listWorkplaceId, baseDate).stream()
				.map(i -> new WorkplaceInforExport(i.getWorkplaceId(), i.getHierarchyCode(), i.getWorkplaceCode(),
						i.getWorkplaceName(), i.getDisplayName(), i.getGenericName(), i.getExternalCode()))
				.collect(Collectors.toList());
	}

	@Override
	public List<WorkplaceInforExport> getAllActiveWorkplaceInfor(String companyId, GeneralDate baseDate) {
		return wkpExpService.getAllActiveWorkplace(companyId, baseDate).stream()
				.map(i -> new WorkplaceInforExport(i.getWorkplaceId(), i.getHierarchyCode().v(),
						i.getWorkplaceCode().v(), i.getWorkplaceName().v(), i.getWorkplaceDisplayName().v(),
						i.getWorkplaceGeneric().v(),
						i.getWorkplaceExternalCode().isPresent() ? i.getWorkplaceExternalCode().get().v() : null))
				.collect(Collectors.toList());
	}

	@Override
	public List<WorkplaceInforExport> getPastWorkplaceInfor(String companyId, String historyId,
															List<String> listWorkplaceId) {
		return wkpExpService.getPastWorkplaceInfor(companyId, historyId, listWorkplaceId).stream()
				.map(i -> new WorkplaceInforExport(i.getWorkplaceId(), i.getHierarchyCode(), i.getWorkplaceCode(),
						i.getWorkplaceName(), i.getDisplayName(), i.getGenericName(), i.getExternalCode()))
				.collect(Collectors.toList());
	}

    @Override
    public List<String> getAllChildrenOfWorkplaceId(String companyId, GeneralDate baseDate, String parentWorkplaceId) {
        return wkpExpService.getAllChildWorkplaceId(companyId, baseDate, parentWorkplaceId);
    }

    @Override
    public List<String> getWorkplaceIdAndChildren(String companyId, GeneralDate baseDate, String workplaceId) {
        return wkpExpService.getWorkplaceIdAndChildren(companyId, baseDate, workplaceId);
    }

    @Override
    public Optional<SWkpHistExport> findBySid(String employeeId, GeneralDate baseDate) {
        // get AffWorkplaceHistory
        Optional<AffWorkplaceHistory> affWrkPlc = affWkpHistRepo.getByEmpIdAndStandDate(employeeId,
                baseDate);
        if (!affWrkPlc.isPresent())
            return Optional.empty();

        // get AffWorkplaceHistoryItem
        String historyId = affWrkPlc.get().getHistoryItems().get(0).identifier();
        Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWkpHistItemRepo.getByHistId(historyId);
        if (!affWrkPlcItem.isPresent())
            return Optional.empty();

        // Get workplace info.
        String companyId = AppContexts.user().companyId();
        String workplaceId = affWrkPlcItem.get().getWorkplaceId();
        List<WorkplaceInforParam> lstWkpInfo = wkpExpService.getWorkplaceInforFromWkpIds(companyId, Arrays.asList(workplaceId), baseDate);

        // Check exist
        if (lstWkpInfo.isEmpty()) {
            return Optional.empty();
        }

        // Return workplace id
        WorkplaceInforParam param = lstWkpInfo.get(0);

        return Optional.of(SWkpHistExport.builder().dateRange(affWrkPlc.get().getHistoryItems().get(0).span())
                .employeeId(affWrkPlc.get().getEmployeeId()).workplaceId(param.getWorkplaceId())
                .workplaceCode(param.getWorkplaceCode()).workplaceName(param.getWorkplaceName())
                .wkpDisplayName(param.getDisplayName())
                .build());
    }
    
    @Override
	public Map<GeneralDate, Map<String, Optional<SWkpHistExport>>> findBySid(String companyID, List<String> employeeIds, DatePeriod datePeriod) {
    	
    	Map<GeneralDate, Map<String, Optional<SWkpHistExport>>> result = new HashMap<GeneralDate, Map<String,Optional<SWkpHistExport>>>();
    	
    	Map<GeneralDate, List<AffWorkplaceHistory>> affWrkPlcMap = affWkpHistRepo.findByEmployees(companyID, employeeIds, datePeriod.datesBetween());
    	
    	List<String> hisIdAll = new ArrayList<String>();
    	affWrkPlcMap.entrySet().forEach(affWrkPlc -> {
    		hisIdAll.addAll(affWrkPlc.getValue().stream().map(c-> c.getHistoryItems().get(0).identifier()).collect(Collectors.toList()));
    	});
    	List<AffWorkplaceHistoryItem> affWrkPlcAllItems = affWkpHistItemRepo.findByHistIds(hisIdAll);
    	List<String> workplaceIdsAll = affWrkPlcAllItems.stream().map(c->c.getWorkplaceId()).collect(Collectors.toList());
    	Map<GeneralDate, List<WorkplaceInforParam>> lstWkpInfoAll = wkpExpService.getWorkplaceInforFromWkpIds(companyID, workplaceIdsAll, datePeriod);
    	
    	for (GeneralDate baseDate : datePeriod.datesBetween()) {
    		Map<String, Optional<SWkpHistExport>> resultId = new HashMap<String, Optional<SWkpHistExport>>();
    		
	    	// get AffWorkplaceHistory
	        List<AffWorkplaceHistory> affWrkPlcList = affWrkPlcMap.get(baseDate);
	        
	        // get AffWorkplaceHistoryItem
	        List<String> hisIds = affWrkPlcList.stream().map(c-> c.getHistoryItems().get(0).identifier()).collect(Collectors.toList());
	        List<AffWorkplaceHistoryItem> affWrkPlcItems = affWrkPlcAllItems.stream().filter(c-> hisIds.contains(c.getHistoryId())).collect(Collectors.toList());

	        // Get workplace info.
	        List<String> workplaceIds = affWrkPlcItems.stream().map(c->c.getWorkplaceId()).collect(Collectors.toList());
	        List<WorkplaceInforParam> lstWkpInfo = lstWkpInfoAll.get(baseDate).stream().filter(c->workplaceIds.contains(c.getWorkplaceId())).collect(Collectors.toList());
	        
	        for (String employeeId : employeeIds) {
	        	Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWrkPlcItems.stream().filter(c->c.getEmployeeId().equals(employeeId)).findAny();
	        	Optional<AffWorkplaceHistory> affWrkPlc = affWrkPlcList.stream().filter(c->c.getEmployeeId().equals(employeeId)).findAny();
	        	if(affWrkPlcItem.isPresent() && affWrkPlc.isPresent()) {
	        		Optional<WorkplaceInforParam> wkpInfo = lstWkpInfo.stream().filter(c->c.getWorkplaceId().equals(affWrkPlcItem.get().getWorkplaceId())).findAny();
	        		if(wkpInfo.isPresent()) {
	        			Optional<SWkpHistExport> sWkpHistExport = Optional.of(SWkpHistExport.builder()
	        					.dateRange(affWrkPlc.get().getHistoryItems().get(0).span())
	        	                .employeeId(employeeId)
	        	                .workplaceId(wkpInfo.get().getWorkplaceId())
	        	                .workplaceCode(wkpInfo.get().getWorkplaceCode())
	        	                .workplaceName(wkpInfo.get().getWorkplaceName())
	        	                .wkpDisplayName(wkpInfo.get().getDisplayName())
	        	                .build());
	        			resultId.put(employeeId, sWkpHistExport);
	        		}else {
	        			resultId.put(employeeId, Optional.empty());
	        		}
	        	}else {
	        		resultId.put(employeeId, Optional.empty());
	        	}
			}
	        result.put(baseDate, resultId);
    	}
    	return result;
	}

	@Override
	public AffWorkplaceHistoryItemExport getAffWkpHistItemByEmpDate(String employeeID, GeneralDate date) {
		List<AffWorkplaceHistoryItem> itemLst = affWkpHistItemRepo.getAffWrkplaHistItemByEmpIdAndDate(date, employeeID);
		if(CollectionUtil.isEmpty(itemLst)) {
			return null;
		} else {
			return new AffWorkplaceHistoryItemExport(
					itemLst.get(0).getHistoryId(), 
					itemLst.get(0).getWorkplaceId());
		}
	}

	@Override
	public List<String> getUpperWorkplace(String companyID, String workplaceID, GeneralDate date) {
		// ドメインモデル「職場構成」を取得する(lấy domain 「WorkplaceConfig」)
		Optional<WorkplaceConfiguration> opWorkplaceConfig = workplaceConfigurationRepository.findByDate(companyID, date);
		if(!opWorkplaceConfig.isPresent()) {
			return new ArrayList<String>();
		}
		// ドメインモデル「職場情報」を取得する
		List<WorkplaceInformation> workplaceInforLst = workplaceInformationRepository.getAllActiveWorkplaceByCompany(
				companyID, 
				opWorkplaceConfig.get().items().get(0).identifier());
		// 取得した「職場情報」から基準となる職場の階層コードを求める
		if (!workplaceInforLst.stream().filter(x -> x.getWorkplaceId().equals(workplaceID)).findAny().isPresent()) {
			return new ArrayList<String>();
		}
		WorkplaceInformation workplaceInfor = workplaceInforLst.stream().filter(x -> x.getWorkplaceId().equals(workplaceID)).findAny().get();
		// 求めた基準となる職場の階層コードから上位階層の職場を求める
		List<String> hierachyCDLst = new ArrayList<>();
		String sumCD = workplaceInfor.getHierarchyCode().toString();
		Integer index = 3;
		while(sumCD.length() - 3 >= index) {
			hierachyCDLst.add(sumCD.substring(0, index));
			index+=3;
		}
		Collections.reverse(hierachyCDLst);
		// 求めた上位階層の職場のIDをOutputする
		List<String> upperWkpIDLst = new ArrayList<>();
		for(String hierachyCD : hierachyCDLst) {
			upperWkpIDLst.add(workplaceInforLst.stream().filter(x -> x.getHierarchyCode().v().equals(hierachyCD)).findAny().get().getWorkplaceId());
		}
		return upperWkpIDLst;
	}

	@Override
	public List<String> getWorkplaceIdAndUpper(String companyId, GeneralDate baseDate, String workplaceId) {
		List<String> lstResult = new ArrayList<>();
		lstResult.add(workplaceId);
		lstResult.addAll(this.getUpperWorkplace(companyId, workplaceId, baseDate));
		return lstResult;
	}
	
	@Override
	public List<String> findWpkIdsBySid(String companyId, String employeeId, GeneralDate baseDate) {
		// Query
		List<AffWorkplaceHistoryItem> items = affWkpHistItemRepo
				.getAffWrkplaHistItemByEmpIdAndDate(baseDate, employeeId);

		List<String> lstWpkIds = new ArrayList<>();

		// Get all parent wkp.
		items.stream().forEach(item -> {
			lstWpkIds.addAll(this.getUpperWorkplace(companyId, item.getWorkplaceId(), baseDate));
			lstWpkIds.add(item.getWorkplaceId());
		});

		// reverse list (child -> parent)
		Collections.reverse(lstWpkIds);

		// Return
		return lstWpkIds.stream().distinct().collect(Collectors.toList());
	}
	
	@Override
	public Map<GeneralDate, Map<String, List<String>>> findWpkIdsBySids(String companyId, List<String> employeeId, DatePeriod date) {
		// Query
		Map<GeneralDate, List<AffWorkplaceHistoryItem>> his = new HashMap<>();
		date.datesBetween().stream().forEach(c -> {
			his.put(c, affWkpHistItemRepo.getAffWrkplaHistItemByListEmpIdAndDateV2(c, employeeId));
		});

		List<String> lstWpkIds = his.entrySet().stream().map(c -> c.getValue().stream().map(h -> h.getWorkplaceId()).collect(Collectors.toList()))
										.flatMap(List::stream).distinct().collect(Collectors.toList());
		Map<DateHistoryItem, List<WorkplaceConfigInfo>> wpc = workplaceInformationRepository.findAllParentByWkpId(companyId, date, lstWpkIds);

		return his.entrySet().stream().collect(Collectors.toMap(c -> c.getKey(), c -> {
			List<WorkplaceConfigInfo> wpConfig = wpc.entrySet().stream().filter(wpch -> wpch.getKey().contains(c.getKey())).findFirst().get().getValue();
			return c.getValue().stream().collect(Collectors.groupingBy(h -> h.getEmployeeId(), Collectors.collectingAndThen(Collectors.toList(), list -> {
				Optional<WorkplaceConfigInfo> currentWpci = wpConfig.stream().filter(w -> w.getLstWkpHierarchy().get(0).getWorkplaceId().equals(list.get(0).getEmployeeId())).findFirst();
				if(currentWpci.isPresent()){
					return currentWpci.get().getLstWkpHierarchy().stream().map(wkph -> wkph.getWorkplaceId()).collect(Collectors.toList());
				}
				return new ArrayList<>();
			})));
		}));
	}
	
	@Override
	public List<AffAtWorkplaceExport> findBySIdAndBaseDateV2(List<String> sids, GeneralDate baseDate) {

		return affWkpHistItemRepo.getAffWrkplaHistItemByListEmpIdAndDateV2(baseDate, sids).stream().map(x -> {
			return new AffAtWorkplaceExport(x.getEmployeeId(), x.getWorkplaceId(), x.getHistoryId());
		}).collect(Collectors.toList());
	}
	
	@Override
	public List<DatePeriod> getLstPeriod(String companyId, DatePeriod period){
		WorkplaceConfiguration wkps = this.workplaceConfigurationRepository.findByCompanyIdAndPeriod(companyId, period).get(0);

		List<DatePeriod> dateList = new ArrayList<>();

		wkps.items().stream().forEach(item -> {
			dateList.add(item.span());
		});

		return dateList;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public List<String> getLstWorkplaceIdBySidAndPeriod(String employeeId, DatePeriod period) {
		List<String> workPlaceIds = affWkpHistItemRepo.getHistIdLstBySidAndPeriod(employeeId, period);
		return workPlaceIds;
	}

	@Override
	public List<ResultRequest597Export> getLstEmpByWorkplaceIdsAndPeriod(List<String> workplaceIds, DatePeriod period) {
		List<String> employeeIds = affWkpHistItemRepo.getHistIdLstByWorkplaceIdsAndPeriod(workplaceIds, period);
		if (CollectionUtil.isEmpty(employeeIds)) {
			return new ArrayList<>();
		}
		List<ResultRequest597Export> results = subEmp.getEmpInfoLstBySids(employeeIds, period, true, true).stream()
				.map(c -> new ResultRequest597Export(c.getSid(), c.getEmployeeCode(), c.getEmployeeName()))
				.collect(Collectors.toList());
		return results;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#
	 * findListSIdByCidAndWkpIdAndPeriod(java.lang.String, nts.arc.time.GeneralDate,
	 * nts.arc.time.GeneralDate)
	 */
	@Override
	public List<AffWorkplaceExport> findListSIdByCidAndWkpIdAndPeriod(String workplaceId, GeneralDate startDate,
			GeneralDate endDate) {

		List<String> listSid = affWkpHistRepo.getByWplIdAndPeriod(workplaceId, startDate, endDate);

		if (listSid.isEmpty())
			return new ArrayList<>();

		List<AffCompanyHist> listAffCompanyHist = affCompanyHistRepo.getAffCompanyHistoryOfEmployees(listSid);

		Map<String, AffCompanyHist> mapPidAndAffCompanyHist = listAffCompanyHist.stream()
				.collect(Collectors.toMap(x -> x.getPId(), x -> x));

		List<AffWorkplaceExport> result = new ArrayList<>();

		//EA修正履歴2638 liên quan đến bug #100243, lọc ra những employee không bị xóa
		List<EmployeeDataMngInfo> listEmpDomain = empDataMngRepo.findBySidNotDel(listSid);

		Map<String, String> mapSidPid = listEmpDomain.stream()
				.collect(Collectors.toMap(x -> x.getEmployeeId(), x -> x.getPersonId()));

		listSid.forEach(sid -> {
			AffCompanyHist affCompanyHist = mapPidAndAffCompanyHist.get(mapSidPid.get(sid));
			// check null
			if (affCompanyHist != null) {
				AffCompanyHistByEmployee affCompanyHistByEmp = affCompanyHist.getAffCompanyHistByEmployee(sid);
				// check null
				if (affCompanyHistByEmp != null) {
					List<AffCompanyHistItem> listAffComHisItem = affCompanyHistByEmp.getLstAffCompanyHistoryItem() == null? new ArrayList<>(): affCompanyHistByEmp.getLstAffCompanyHistoryItem();
					if (!CollectionUtil.isEmpty(listAffComHisItem)) {
						listAffComHisItem.forEach(m -> {
							/*
							 * EA修正履歴2059 update RequestList120 【Codition】 param．period．startDate ＜＝
							 * retirementDate AND entrialDate ＜＝ param．period．endDate
							 */
							if (startDate.beforeOrEquals(m.end()) && endDate.afterOrEquals(m.start())) {
								AffWorkplaceExport aff = new AffWorkplaceExport(sid, m.start(), m.end());
								result.add(aff);
							}
						});
					}
				}
			}
		});

		return result;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#
	 * findListWorkplaceIdByBaseDate(nts.arc.time.GeneralDate)
	 */
	
	@Override
	public List<String> getListWorkplaceIdByBaseDate(GeneralDate baseDate) {

		// get all WorkplaceConfigInfo with StartDate
		String companyId = AppContexts.user().companyId();

		Optional<WorkplaceConfiguration> optionalWkpConfig = workplaceConfigurationRepository.findByDate(companyId, baseDate);
		if (!optionalWkpConfig.isPresent()) {
			return null;
		}
		WorkplaceConfiguration wkpConfig = optionalWkpConfig.get();
		String historyId = wkpConfig.items().get(0).identifier();
		
		List<WorkplaceInformation> opWkpConfigInfos = workplaceInformationRepository.getAllWorkplaceByCompany(companyId, historyId);
		if (opWkpConfigInfos.isEmpty()) {
			return Collections.emptyList();
		}
		
		return opWkpConfigInfos.stream().map(c -> c.getWorkplaceId()).collect(Collectors.toList());
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#GetWplByListSidAndPeriod(
	 * java.util.List, nts.arc.time.calendar.period.DatePeriod)
	 */
	@Override
	public List<WorkPlaceHistExport> getWplByListSidAndPeriod(List<String> employeeIds, DatePeriod datePeriod) {

		if (employeeIds.isEmpty() || datePeriod.start() == null || datePeriod.end() == null)
			return Collections.emptyList();

		List<AffWorkplaceHistory> lstAffWkpHist = affWkpHistRepo.getByListSid(employeeIds);
		if (lstAffWkpHist.isEmpty())
			return Collections.emptyList();

		List<WorkPlaceHistExport> result = new ArrayList<>();

		lstAffWkpHist.forEach(affWkp -> {
			WorkPlaceHistExport workPlaceHistExport = new WorkPlaceHistExport();

			workPlaceHistExport.setEmployeeId(affWkp.getEmployeeId());

			if (!affWkp.getHistoryItems().isEmpty()) {
				workPlaceHistExport.setLstWkpIdAndPeriod(getLstWkpIdAndPeriod(affWkp, datePeriod));
			}

			result.add(workPlaceHistExport);
		});

		return result;
	}
	
	
	/**
	 * Gets the lst wkp id and period.
	 *
	 * @param affWkp
	 *            the aff wkp
	 * @param datePeriod
	 *            the date period
	 * @return the lst wkp id and period
	 */
	private List<WorkPlaceIdAndPeriod> getLstWkpIdAndPeriod(AffWorkplaceHistory affWkp, DatePeriod datePeriod) {

		List<WorkPlaceIdAndPeriod> result = new ArrayList<>();

		affWkp.getHistoryItems().forEach(itemHist -> {

			WorkPlaceIdAndPeriod workPlaceIdAndPeriod = new WorkPlaceIdAndPeriod();

			boolean check = itemHist.start().beforeOrEquals(datePeriod.end()) && itemHist.end().afterOrEquals(datePeriod.start());

			if (check) {
				DatePeriod date = new DatePeriod(itemHist.start(), itemHist.end());

				AffWorkplaceHistoryItem affWkpHisItem = affWkpHistItemRepo
						.getByHistId(itemHist.identifier()).get();

				workPlaceIdAndPeriod.setWorkplaceId(affWkpHisItem.getWorkplaceId());

				workPlaceIdAndPeriod.setDatePeriod(date);

				result.add(workPlaceIdAndPeriod);

			}
		});
		return result;
	}
	
	@Override
	public List< AffWorkplaceHistoryExport> getWorkplaceBySidsAndBaseDate(List<String> employeeIds, GeneralDate baseDate) {
		List<String> histIds =  new ArrayList<>();
		List<AffWorkplaceHistoryExport>  result = new ArrayList<>();
		Map<String, List<AffWorkplaceHistory>> workplaceHistMap = affWkpHistRepo.getWorkplaceHistoryBySidsAndDateV2( baseDate, employeeIds).stream().collect(Collectors.groupingBy(c -> c.getEmployeeId()));
		workplaceHistMap.values().stream().forEach(c -> {
			histIds.addAll(c.get(0).getHistoryIds());
		});
		List<AffWorkplaceHistoryItem> histItemMaps = affWkpHistItemRepo.findByHistIds(histIds);
		workplaceHistMap.entrySet().stream().forEach(c -> {
			AffWorkplaceHistory value = c.getValue().get(0);
			Map<String, AffWorkplaceHistoryItemExport> workplaceHistItems = new HashMap<>();
			value.getHistoryItems().stream().forEach(hist -> {
				Optional<AffWorkplaceHistoryItem> workplacehistItemOpt = histItemMaps.stream()
						.filter(item -> item.getHistoryId().equals(hist.identifier())).findFirst();
				if (workplacehistItemOpt.isPresent()) {
					AffWorkplaceHistoryItem histItem = workplacehistItemOpt.get();
					AffWorkplaceHistoryItemExport histItemExport = new AffWorkplaceHistoryItemExport(
							histItem.getHistoryId(), histItem.getWorkplaceId());
					workplaceHistItems.put(hist.identifier(), histItemExport);
				}
			});
			AffWorkplaceHistoryExport export = new AffWorkplaceHistoryExport(value.getEmployeeId(), value.getHistoryItems(), workplaceHistItems);
			result.add(export);
		});
		return result;
	}
	
	@Override
	public Optional<SWkpHistExport> findByWkpIdNew(String companyId, String wkpId, GeneralDate baseDate) {
		return workplaceInformationRepository.getWkpNewByIdDate(companyId, wkpId, baseDate)
				.map(c -> SWkpHistExport.builder()
				.workplaceId(c.getWorkplaceId())
				.workplaceCode(c.getWorkplaceCode().v())
				.workplaceName(c.getWorkplaceName().v())
				.wkpDisplayName(c.getWorkplaceDisplayName().v()).build());
	}
	
	@Override
	public List<AffWorkplaceExport> getByLstWkpIdAndPeriod(List<String> lstWkpId, GeneralDate startDate,
			GeneralDate endDate) {
		if (lstWkpId.isEmpty() ||startDate == null  || endDate == null)
			return new ArrayList<>();

		List<EmployeeDataMngInfo> listEmpDomain = empDataMngRepo.getAllEmpNotDeleteByCid(AppContexts.user().companyId());

		Map<String, String> mapSidPid = listEmpDomain.stream()
				.collect(Collectors.toMap(x -> x.getEmployeeId(), x -> x.getPersonId()));

		List<String> listSid = affWkpHistRepo.getByLstWplIdAndPeriod(lstWkpId, startDate, endDate);

		if (listSid.isEmpty())
			return new ArrayList<>();

		List<AffCompanyHist> listAffCompanyHist = new ArrayList<>();
		List<AffCompanyHist> lstAffCompanyHist = affCompanyHistRepo.getAffCompanyHistoryOfEmployees(listSid);
		listAffCompanyHist.addAll(lstAffCompanyHist);

		Map<String, AffCompanyHist> mapPidAndAffCompanyHist = listAffCompanyHist.stream()
				.collect(Collectors.toMap(x -> x.getPId(), x -> x));

		List<AffWorkplaceExport> result = new ArrayList<>();

		listSid.forEach(sid -> {

			AffCompanyHist affCompanyHist = mapPidAndAffCompanyHist.get(mapSidPid.get(sid));
			if(affCompanyHist != null){
				AffCompanyHistByEmployee affCompanyHistByEmp = affCompanyHist.getAffCompanyHistByEmployee(sid);
				Optional.ofNullable(affCompanyHistByEmp).ifPresent(f -> {
					if (f.items() != null) {
						List<AffCompanyHistItem> listAffComHisItem = affCompanyHistByEmp.getLstAffCompanyHistoryItem();

						if (!CollectionUtil.isEmpty(listAffComHisItem)) {
							listAffComHisItem.forEach(m -> {
								if (m.start().beforeOrEquals(startDate) && m.end().afterOrEquals(endDate)) {
									AffWorkplaceExport aff = new AffWorkplaceExport(sid, m.start(), m.end());
									result.add(aff);
								}
							});
						}
					}
				});
			}else{
				System.out.println("data sai: " + sid);
			}
		});

		return result;
	}
	
	@Override
	public List<AffAtWorkplaceExport> findBySIdAndBaseDate(List<String> sids, GeneralDate baseDate) {

		if (sids.isEmpty() || baseDate == null)
			return Collections.emptyList();

		List<AffWorkplaceHistory> lstAffWkpHist = affWkpHistRepo.getByListSid(sids);
		if (lstAffWkpHist.isEmpty())
			return Collections.emptyList();

		List<String> historyIds = new ArrayList<>();

		lstAffWkpHist.stream().forEach(x -> {

			List<DateHistoryItem> historyItemList = x.items();
			List<String> hists = new ArrayList<>();
			if (!historyItemList.isEmpty()) {
				hists = historyItemList.stream().filter(m -> {
					return m.end().afterOrEquals(baseDate) && m.start().beforeOrEquals(baseDate);
				}).map(y -> y.identifier()).collect(Collectors.toList());

				historyIds.addAll(hists);
			}

		});

		if (historyIds.isEmpty())
			return Collections.emptyList();

		List<AffWorkplaceHistoryItem> affWrkPlcItems = affWkpHistItemRepo.findByHistIds(historyIds);

		return affWrkPlcItems.stream().map(x -> {
			AffAtWorkplaceExport affWkp = new AffAtWorkplaceExport();
			affWkp.setEmployeeId(x.getEmployeeId());
			affWkp.setHistoryID(x.getHistoryId());
			affWkp.setWorkplaceId(x.getWorkplaceId());
			return affWkp;
		}).collect(Collectors.toList());
	}
	/* (non-Javadoc)
	 * @see nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#getWorkplaceMapCodeBaseDateName(java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public Map<String, Pair<String,String>> getWorkplaceMapCodeBaseDateName(String companyId,
			List<String> wpkIds, List<GeneralDate> baseDates) {
		// Query infos
		List<WorkplaceInformation> workplaceInfos = this.workplaceInformationRepository
				.findByWkpIds(companyId, wpkIds);

		Map<String, Pair<String,String>> mapResult = new HashMap<>();
		workplaceInfos.stream().forEach(item -> {
			mapResult.put(item.getWorkplaceId(), Pair.of(item.getWorkplaceCode().v(), item.getWorkplaceName().v()));
		});

		return mapResult;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<SWkpHistExport> findBySidNew(String employeeId, GeneralDate baseDate) {
		return this.findBySidNew(AppContexts.user().companyId(), employeeId, baseDate);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Optional<SWkpHistExport> findBySidNew(String companyId, String employeeId, GeneralDate baseDate) {
		if(Strings.isBlank(companyId)) {
			companyId = AppContexts.user().companyId();
		}
		// get AffWorkplaceHistory
		Optional<AffWorkplaceHistory> affWrkPlc = affWkpHistRepo.getByEmpIdAndStandDate(employeeId,
				baseDate);
		if (!affWrkPlc.isPresent())
			return Optional.empty();

		// get AffWorkplaceHistoryItem
		String historyId = affWrkPlc.get().getHistoryItems().get(0).identifier();
		Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWkpHistItemRepo.getByHistId(historyId);
		if (!affWrkPlcItem.isPresent())
			return Optional.empty();

		// Get workplace info.
		Optional<WorkplaceInformation> opWkpNew = workplaceInformationRepository.getWkpNewByIdDate(companyId, affWrkPlcItem.get().getWorkplaceId(),
				baseDate);

		// Check exist
		if (!opWkpNew.isPresent()) {
			return Optional.empty();
		}

		// Return workplace id
		WorkplaceInformation wkpInfo = opWkpNew.get();

		return Optional.of(SWkpHistExport.builder().dateRange(affWrkPlc.get().getHistoryItems().get(0).span())
				.employeeId(affWrkPlc.get().getEmployeeId()).workplaceId(wkpInfo.getWorkplaceId())
				.workplaceCode(wkpInfo.getWorkplaceCode().v()).workplaceName(wkpInfo.getWorkplaceName().v())
				.wkpDisplayName(wkpInfo.getWorkplaceDisplayName().v()).build());
	}
	
	@Override
	public WkpByEmpExport getLstHistByEmpAndPeriod(String employeeID, GeneralDate startDate, GeneralDate endDate) {
		String companyID = AppContexts.user().companyId();
		List<AffWorkplaceHistory> affWorkplaceHistoryLst = affWkpHistRepo.findByEmployeesWithPeriod(
				Arrays.asList(employeeID), new DatePeriod(startDate, endDate));
		if(CollectionUtil.isEmpty(affWorkplaceHistoryLst)){
			return new WkpByEmpExport(employeeID, Collections.emptyList());
		}
		List<DateHistoryItem> dateHistoryItemLst = new ArrayList<>();
		affWorkplaceHistoryLst.forEach(x -> {
			dateHistoryItemLst.addAll(x.getHistoryItems());
		});
		List<String> hisIDs = dateHistoryItemLst.stream().map(x -> x.identifier()).collect(Collectors.toList());
		Map<String,String> wkpIDLst = new HashMap<String,String>();
		affWkpHistItemRepo.findByHistIds(hisIDs).forEach(x -> {
			wkpIDLst.put(x.getHistoryId(), x.getWorkplaceId());
		});

		List<WorkplaceInformation> wkpInfoLst = workplaceInformationRepository.findByWkpIds(companyID, wkpIDLst.entrySet().stream().map(x -> x.getValue()).collect(Collectors.toList()));
		List<WkpInfoExport> wkpInfoExportLst = new ArrayList<>();
		dateHistoryItemLst.forEach(x -> {
			Optional<Entry<String,String>> wkpIDItem = wkpIDLst.entrySet().stream().filter(t -> t.getKey().equals(x.identifier())).findAny();
			String wkpID = wkpIDItem.get().getValue();
			WkpInfoExport wkpInfoExport = wkpInfoLst.stream().filter(y -> y.getWorkplaceId().equals(wkpID)).findAny()
					.map(k -> new WkpInfoExport(
							x.span(),
							k.getWorkplaceId(),
							k.getWorkplaceCode().toString(),
							k.getWorkplaceName().toString()))
					.orElse(new WkpInfoExport(
							x.span(),
							wkpID,
							"マスタ未登録",
							"マスタ未登録"));
			wkpInfoExportLst.add(wkpInfoExport);
		});
		return new WkpByEmpExport(employeeID, wkpInfoExportLst);
	}
	
	@Override
	public List<SWkpHistExport> findBySId(List<String> sids, GeneralDate baseDate) {
		String companyID = AppContexts.user().companyId();
		// Get AffWorkplaceHistory
		List<AffWorkplaceHistory> affWrkPlc = affWkpHistRepo.getWorkplaceHistoryByEmpIdsAndDate(baseDate, sids);
		// Check exist
		if (affWrkPlc.isEmpty())
			return Collections.emptyList();

		// Get AffWorkplaceHistoryItem
		List<String> historyIds = affWrkPlc.stream().map(c -> c.getHistoryItems().get(0).identifier()).collect(Collectors.toList());
		List<AffWorkplaceHistoryItem> affWrkPlcItem = affWkpHistItemRepo.findByHistIds(historyIds);
		// Check exist
		if (affWrkPlcItem.isEmpty())
			return Collections.emptyList();

		// Get workplace info.
		List<String> workplaceIds = affWrkPlcItem.stream().map(AffWorkplaceHistoryItem::getWorkplaceId).distinct().collect(Collectors.toList());
		List<WorkplaceInformation> list = workplaceInformationRepository.findByBaseDateWkpIds2(companyID, workplaceIds, baseDate);
		// Check exist
		if (list.isEmpty()) {
			return Collections.emptyList();
		}

		// Convert and get data
		List<SWkpHistExport> listData = new ArrayList<>();
		Map<String, AffWorkplaceHistoryItem> affWorkplaceHistItemMap = affWrkPlcItem.stream().collect(Collectors.toMap(AffWorkplaceHistoryItem::getEmployeeId, Function.identity()));
		Map<String, WorkplaceInformation> workplaceInfoMap = list.stream().collect(Collectors.toMap(WorkplaceInformation::getWorkplaceId, Function.identity()));

		affWrkPlc.forEach(w -> {
			String workplaceId = affWorkplaceHistItemMap.get(w.getEmployeeId()).getWorkplaceId();
			listData.add(convertFromWorkplaceInfo(workplaceInfoMap.get(workplaceId), w));
		});

		return listData;
	}
	
	@Override
	public List<SWkpHistExport> findBySId(List<String> sids) {
		String companyID = AppContexts.user().companyId();
		// get AffWorkplaceHistory
		List<AffWorkplaceHistory> affWrkPlc = affWkpHistRepo.getByListSid(sids);
		if (affWrkPlc.isEmpty())
			return Collections.emptyList();

		// get AffWorkplaceHistoryItem
		List<String> historyIds = affWrkPlc.stream().map(c -> c.getHistoryItems().get(0).identifier())
				.collect(Collectors.toList());
		List<AffWorkplaceHistoryItem> affWrkPlcItem = affWkpHistItemRepo.findByHistIds(historyIds);
		if (affWrkPlcItem.isEmpty())
			return Collections.emptyList();

		// Get workplace info.
		List<WorkplaceInformation> optWorkplaceInfo = workplaceInformationRepository.findByWkpIds(companyID,
				affWrkPlcItem.stream().map(c -> c.getWorkplaceId()).collect(Collectors.toList()));

		// Check exist
		if (optWorkplaceInfo.isEmpty()) {
			return Collections.emptyList();
		}

		// // Return workplace id
		// WorkplaceInfo wkpInfo = optWorkplaceInfo.get();

		List<SWkpHistExport> listData = new ArrayList<>();
		for (WorkplaceInformation workplaceInfo : optWorkplaceInfo) {
			for (AffWorkplaceHistoryItem affWorkplaceHistoryItem : affWrkPlcItem) {
				if (affWorkplaceHistoryItem.getHistoryId().equals(workplaceInfo.getWorkplaceHistoryId())) {
					for (AffWorkplaceHistory affWorkplaceHistory : affWrkPlc) {
						if (affWorkplaceHistoryItem.getEmployeeId().equals(affWorkplaceHistory.getEmployeeId())) {
							listData.add(convertFromWorkplaceInfo(workplaceInfo, affWorkplaceHistory));
							break;
						}
					}
					break;
				}

			}

		}

		return listData;
	}
	
	private SWkpHistExport convertFromWorkplaceInfo(WorkplaceInformation wkpInfo, AffWorkplaceHistory affWrkPlc) {
		return SWkpHistExport.builder()
				.dateRange(affWrkPlc.getHistoryItems().get(0).span())
		.employeeId(affWrkPlc.getEmployeeId())
		.workplaceId(wkpInfo.getWorkplaceId())
		.workplaceCode(wkpInfo.getWorkplaceCode().v())
		.workplaceName(wkpInfo.getWorkplaceName().v())
		.wkpDisplayName(wkpInfo.getWorkplaceDisplayName().v()).build();
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * nts.uk.ctx.bs.employee.pub.workplace.SyWorkplacePub#findByWkpIdsAtTime(java.
	 * lang.String, nts.arc.time.GeneralDate, java.util.List)
	 */
	@Override
	public List<WkpConfigAtTimeExport> findByWkpIdsAtTime(String companyId, GeneralDate baseDate, List<String> wkpIds) {
		List<WorkplaceInformation> configInfos = workplaceInformationRepository.findByBaseDateWkpIds2(companyId, wkpIds, baseDate);
		
		return configInfos.stream().map(configInfo -> WkpConfigAtTimeExport.builder().workplaceId(configInfo.getWorkplaceId())
				.hierarchyCd(configInfo.getHierarchyCode().v()).build()).collect(Collectors.toList());
	}
	@Override
	public List<WorkplaceInforExport> findByWkpIds(List<String> wkpIds) {
		return workplaceInformationRepository.findByWkpIds(wkpIds).stream().map(i -> new WorkplaceInforExport(i.getWorkplaceId(), "", i.getWorkplaceCode().v(),
				i.getWorkplaceName().v(), i.getWkpDisplayName().v(), i.getWkpGenericName().v(), i.getOutsideWkpCode().v())).collect(Collectors.toList());
	}
	
	@Override
	public Optional<WkpCdNameExport> findByWkpId(String wkpId) {
		List<WorkplaceInfo> workplaceInfoLst = workplaceInformationRepository.findByWkpId(wkpId);
		if(CollectionUtil.isEmpty(workplaceInfoLst)) {
			return Optional.empty();
		}
		WorkplaceInfo optWorkplaceInfo = workplaceInfoLst.get(0);
		return Optional.of(WkpCdNameExport.builder().wkpCode(optWorkplaceInfo.getWorkplaceCode().v())
				.wkpName(optWorkplaceInfo.getWorkplaceName().v()).build());
	}

	@Override
	public Optional<String> getWkpNewByCdDate(String companyId, String wkpCd, GeneralDate baseDate) {
		return workplaceInformationRepository.getWkpNewByCdDate(companyId, wkpCd, baseDate)
				.map(c -> c.getWorkplaceId());
	}

	@Override
	public List<AffWorkplaceHistoryItemExport2> getWorkHisItemfromWkpIdAndBaseDate(String workPlaceId, GeneralDate baseDate) {
		
		List<AffWorkplaceHistoryItem> affWrkPlcItems = affWkpHistItemRepo.getAffWrkplaHistItemByListWkpIdAndDate(baseDate, Arrays.asList(workPlaceId));
		
		if (affWrkPlcItems.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<AffWorkplaceHistoryItemExport2> result = affWrkPlcItems.stream().map(item -> {
			return new AffWorkplaceHistoryItemExport2(item.getHistoryId(), item.getEmployeeId(), item.getWorkplaceId()); 
		}).collect(Collectors.toList());
		
		return result;
	}
	
	@Override
	public List<AffWorkplaceHistoryItemExport3> getWorkHisItemfromWkpIdsAndBaseDate(List<String> workPlaceIds, GeneralDate baseDate) {
		List<AffWorkplaceHistoryItem> affWrkPlcItems = affWkpHistItemRepo.getAffWrkplaHistItemByListWkpIdAndDate(baseDate, workPlaceIds);

		if (affWrkPlcItems.isEmpty()) {
			return new ArrayList<>();
		}

		List<AffWorkplaceHistoryItemExport3> result = affWrkPlcItems.stream().map(item -> {
			return new AffWorkplaceHistoryItemExport3(item.getHistoryId(), item.getEmployeeId(), item.getWorkplaceId());
		}).collect(Collectors.toList());

		return result;
	}


	@Override
	public Optional<SWkpHistWrkLocationExport> findBySidWrkLocationCD(String employeeId, GeneralDate baseDate) {
		// get AffWorkplaceHistory
		Optional<AffWorkplaceHistory> affWrkPlc = affWkpHistRepo.getByEmpIdAndStandDate(employeeId,
				baseDate);
		if (!affWrkPlc.isPresent())
			return Optional.empty();

		// get AffWorkplaceHistoryItem
		String historyId = affWrkPlc.get().getHistoryItems().get(0).identifier();
		Optional<AffWorkplaceHistoryItem> affWrkPlcItem = affWkpHistItemRepo.getByHistId(historyId);
		if (!affWrkPlcItem.isPresent())
			return Optional.empty();

		// Get workplace info.
		String companyId = AppContexts.user().companyId();
		String workplaceId = affWrkPlcItem.get().getWorkplaceId();
		List<WorkplaceInforParam> lstWkpInfo = wkpExpService.getWorkplaceInforFromWkpIds(companyId, Arrays.asList(workplaceId), baseDate);

		// Check exist
		if (lstWkpInfo.isEmpty()) {
			return Optional.empty();
		}

		// Return workplace id
		WorkplaceInforParam param = lstWkpInfo.get(0);

		return Optional.of(SWkpHistWrkLocationExport.builder().dateRange(affWrkPlc.get().getHistoryItems().get(0).span())
				.employeeId(affWrkPlc.get().getEmployeeId()).workplaceId(param.getWorkplaceId())
				.workplaceCode(param.getWorkplaceCode()).workplaceName(param.getWorkplaceName())
				.wkpDisplayName(param.getDisplayName())
				.workLocationCd( null )
				.build());
	}

	@Override
	public List<WorkplaceInformationExport> getByCidAndPeriod(String companyId, DatePeriod datePeriod) {

		//[No.647]期間に対応する職場構成を取得する
		List<WorkPlaceConfigExport> workPlaceConfigLst = workPlaceConfigPub.findByCompanyIdAndPeriod(companyId, datePeriod);
		List<String> wkpHistIds = new ArrayList<>();
		List<WorkplaceConfigHistoryExport> wkpConfigs = new ArrayList<>();
		workPlaceConfigLst.forEach(x -> {
			x.getWkpConfigHistory().forEach(i -> wkpHistIds.add(i.getHistoryId()));
			wkpConfigs.addAll(x.getWkpConfigHistory());
		});
		List<WorkplaceInformation> workplaceInforLst = workplaceInformationRepository.findByHistoryIds(AppContexts.user().companyId(), wkpHistIds);

		return workplaceInforLst.stream()
			.map(i -> new WorkplaceInformationExport(
					i.getCompanyId(),
					i.isDeleteFlag(),
					i.getWorkplaceHistoryId(),
					i.getWorkplaceId(),
					i.getWorkplaceCode().v(),
					i.getWorkplaceName().v(),
					i.getWorkplaceGeneric().v(),
					i.getWorkplaceDisplayName().v(),
					i.getHierarchyCode().v(),
					i.getWorkplaceExternalCode().isPresent() ? Optional.of(i.getWorkplaceExternalCode().get().v()) : Optional.empty(),
					wkpConfigs.stream().filter(c -> c.getHistoryId().equals(i.getWorkplaceHistoryId())).findFirst().map(c -> c.getPeriod()).orElse(null)
				)
			).collect(Collectors.toList());
	}

}
