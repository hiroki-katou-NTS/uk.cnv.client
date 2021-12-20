package nts.uk.ctx.at.shared.app.find.dailyattdcal.empunitpricehistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistory;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.pereg.app.ComboBoxObject;
import nts.uk.shr.pereg.app.find.PeregFinder;
import nts.uk.shr.pereg.app.find.PeregQuery;
import nts.uk.shr.pereg.app.find.PeregQueryByListEmp;
import nts.uk.shr.pereg.app.find.dto.DataClassification;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainBySidDto;
import nts.uk.shr.pereg.app.find.dto.GridPeregDomainDto;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Stateless
public class EmployeeUnitPriceFinder implements PeregFinder<EmployeeUnitPriceDto> {
	
	@Inject
	private EmployeeUnitPriceHistoryRepository eupHistRepo;
	
	@Inject
	private EmployeeUnitPriceHistoryItemRepository eupHistItemRepo;

	@Override
	public String targetCategoryCode() {
		return "CS00097";
	}

	@Override
	public Class<EmployeeUnitPriceDto> dtoClass() {
		return EmployeeUnitPriceDto.class;
	}

	@Override
	public DataClassification dataType() {
		return DataClassification.EMPLOYEE;
	}

	@Override
	public PeregDomainDto getSingleData(PeregQuery query) {
		Optional<EmployeeUnitPriceHistory> eupHistOp = getEmployeeUnitPrice(query);
		if(!eupHistOp.isPresent()) return null;
		Optional<EmployeeUnitPriceHistoryItem> eupHisItemOp = eupHistItemRepo.getByHistoryId(eupHistOp.get().getHistoryItems().get(0).identifier());
		if(!eupHisItemOp.isPresent()) return null;
		return EmployeeUnitPriceDto.createEmployeeUnitPriceDto(eupHistOp.get().getSid(), eupHistOp.get().getHistoryItems().get(0), eupHisItemOp.get());
	}
	
	public Optional<EmployeeUnitPriceHistory> getEmployeeUnitPrice(PeregQuery query) {
		if(query.getInfoId() == null){
			return eupHistRepo.getHistByEmployeeIdAndBaseDate(query.getEmployeeId(), query.getStandardDate());
		}else{
			return eupHistRepo.getHistByHistId(query.getInfoId());
		}
	}

	@Override
	public List<PeregDomainDto> getListData(PeregQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ComboBoxObject> getListFirstItems(PeregQuery query) {
		Optional<EmployeeUnitPriceHistory> eupHistOp = eupHistRepo.getBySidDesc(AppContexts.user().companyId(), query.getEmployeeId());
		if(!eupHistOp.isPresent()) return new ArrayList<>();
		List<DateHistoryItem> hists = eupHistOp.get().getHistoryItems();
		if(hists.size() == 0) return new ArrayList<>();
		return hists.stream()
				.sorted((a, b) -> b.start().compareTo(a.start()))
				.map(x -> ComboBoxObject.toComboBoxObject(x.identifier(), x.start().toString(), 
						x.end().equals(GeneralDate.max()) 
						//&& query.getCtgType() == 3 
						? "" : x.end().toString()))
				.collect(Collectors.toList());
	}

	@Override
	public List<GridPeregDomainDto> getAllData(PeregQueryByListEmp query) {
		String cid = AppContexts.user().companyId();

		List<GridPeregDomainDto> result = new ArrayList<>();

		List<String> sids = query.getEmpInfos().stream().map(c -> c.getEmployeeId()).collect(Collectors.toList());
		
		query.getEmpInfos().forEach(c -> {
			result.add(new GridPeregDomainDto(c.getEmployeeId(), c.getPersonId(), null));
		});
		
		List<EmployeeUnitPriceHistory> eupHistList = eupHistRepo.getBySidsAndCidAndBaseDate(sids, cid, query.getStandardDate());
		
		Map<String, DateHistoryItem> dateHistList = new HashMap<>();

		eupHistList.stream().forEach(c -> {
			dateHistList.put(c.getSid(), c.getHistoryItems().get(0));
		});
		
		List<String> historyIds = dateHistList.values().stream().map(c -> c.identifier()).collect(Collectors.toList());
		
		List<EmployeeUnitPriceHistoryItem> eupHistItemList = eupHistItemRepo.getByHistIdList(historyIds);
		
		result.stream().forEach(c -> {
			Optional<EmployeeUnitPriceHistoryItem> eupHistItemOp = eupHistItemList.stream()
					.filter(emp -> emp.getSid().equals(c.getEmployeeId())).findFirst();
			if (eupHistItemOp.isPresent()) {
				DateHistoryItem dateHistItem = dateHistList.get(c.getEmployeeId());
				c.setPeregDomainDto(dateHistItem != null
						? EmployeeUnitPriceDto.createEmployeeUnitPriceDto(c.getEmployeeId(), dateHistItem, eupHistItemOp.get())
						: null);
			}
		});
		
		return result;
	}

	@Override
	public List<GridPeregDomainBySidDto> getListData(PeregQueryByListEmp query) {
		// TODO Auto-generated method stub
		return null;
	}

}
