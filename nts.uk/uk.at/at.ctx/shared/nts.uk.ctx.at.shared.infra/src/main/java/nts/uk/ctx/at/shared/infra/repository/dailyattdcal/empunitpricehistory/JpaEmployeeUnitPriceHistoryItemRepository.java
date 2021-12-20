/**
 * 
 */
package nts.uk.ctx.at.shared.infra.repository.dailyattdcal.empunitpricehistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.UnitPricePerNumber;
import nts.uk.ctx.at.shared.infra.entity.dailyattdcal.empunitpricehistory.KrcmtUnitPriceItem;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author laitv
 * 社員単価履歴項目Repository
 */
@Stateless
public class JpaEmployeeUnitPriceHistoryItemRepository extends JpaRepository implements EmployeeUnitPriceHistoryItemRepository{

	private static final String SELECT_BY_EMPID_BASEDATE = "SELECT upi FROM KrcmtUnitPriceItem upi"
			+ " INNER JOIN  KrcmtUnitPrice up ON up.pk.histId = upi.pk.histId"
			+ " WHERE up.pk.sid = :sid AND up.startDate <= :baseDate AND :baseDate <= up.endDate";
	
	private static final String SELECT_BY_HISTID = "SELECT upi FROM KrcmtUnitPriceItem upi WHERE upi.pk.histId = :histId";
	
	private static final String SELECT_BY_HISTIDLLIST = "SELECT upi FROM KrcmtUnitPriceItem upi WHERE upi.pk.histId IN :histIdList";
	
	@Override
	public Optional<EmployeeUnitPriceHistoryItem> getByEmployeeIdAndBaseDate(String sid, GeneralDate baseDate) {
		List<KrcmtUnitPriceItem> listHistItem = this.queryProxy()
				.query(SELECT_BY_EMPID_BASEDATE, KrcmtUnitPriceItem.class)
				.setParameter("sid", sid).setParameter("baseDate", baseDate)
				.getList();

		// Check exist items
		if (listHistItem.isEmpty()) {
			return Optional.empty();
		}

		// Return
		return Optional.of(toDomain(listHistItem));
	}
	
	private EmployeeUnitPriceHistoryItem toDomain(List<KrcmtUnitPriceItem> entitis) {
		List<UnitPricePerNumber> unitPrices = entitis.stream().map(i -> KrcmtUnitPriceItem.toUnitPricePerNumber(i.pk.unitPriceNo, i.hourlyUnitPrice)).collect(Collectors.toList());
		return EmployeeUnitPriceHistoryItem.createSimpleFromJavaType(entitis.get(0).sid, entitis.get(0).pk.histId, unitPrices);
	}

	@Override
	public Optional<EmployeeUnitPriceHistoryItem> getByHistoryId(String histId) {
		List<KrcmtUnitPriceItem> listHistItem = this.queryProxy()
				.query(SELECT_BY_HISTID, KrcmtUnitPriceItem.class)
				.setParameter("histId", histId)
				.getList();

		// Check exist items
		if (listHistItem.isEmpty()) {
			return Optional.empty();
		}

		// Return
		return Optional.of(toDomain(listHistItem));
	}
	
	@Override
	public List<EmployeeUnitPriceHistoryItem> getByHistIdList(List<String> histIdList) {
		if (histIdList.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<KrcmtUnitPriceItem> listHistItem = this.queryProxy()
				.query(SELECT_BY_HISTIDLLIST, KrcmtUnitPriceItem.class)
				.setParameter("histIdList", histIdList)
				.getList();

		// Check exist items
		if (listHistItem.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<EmployeeUnitPriceHistoryItem> listHistItemDomain = new ArrayList<EmployeeUnitPriceHistoryItem>();
		
		Map<String, List<KrcmtUnitPriceItem>> listHistItemMapBySid = listHistItem.stream().collect(Collectors.groupingBy(entity -> entity.sid));
		
		for (Map.Entry<String, List<KrcmtUnitPriceItem>> entry : listHistItemMapBySid.entrySet()) {
		    if (!entry.getValue().isEmpty()) {
		    	listHistItemDomain.add(toDomain(entry.getValue()));
		    }
		}

		// Return
		return listHistItemDomain;
	}

	@Override
	public void add(EmployeeUnitPriceHistoryItem employeeUnitPriceHistoryItem) {
		this.commandProxy().insertAll(toEntity(employeeUnitPriceHistoryItem));
	}

	@Override
	public void update(EmployeeUnitPriceHistoryItem employeeUnitPriceHistoryItem) {
		List<KrcmtUnitPriceItem> oldEntities = this.queryProxy()
				.query(SELECT_BY_HISTID, KrcmtUnitPriceItem.class)
				.setParameter("histId", employeeUnitPriceHistoryItem.getHisId())
				.getList();

		List<KrcmtUnitPriceItem> newEntities = toEntity(employeeUnitPriceHistoryItem);
		
		oldEntities.forEach(x -> {
			Optional<KrcmtUnitPriceItem> newDataOldId = newEntities.stream().filter(el -> el.pk.unitPriceNo == x.pk.unitPriceNo).findFirst();
			if (newDataOldId.isPresent()) {
				KrcmtUnitPriceItem y = newDataOldId.get();
				x.hourlyUnitPrice = y.hourlyUnitPrice;
			}
		});
	}

	@Override
	public void delete(String companyId, String empId, String historyId) {
		List<KrcmtUnitPriceItem> listHistItem = this.queryProxy()
				.query(SELECT_BY_HISTID, KrcmtUnitPriceItem.class)
				.setParameter("histId", historyId)
				.getList();
		if (!listHistItem.isEmpty()) {
			this.commandProxy().removeAll(listHistItem);
		}
	}

	private List<KrcmtUnitPriceItem> toEntity(EmployeeUnitPriceHistoryItem domain) {
		List<KrcmtUnitPriceItem> entityList = domain.getUnitPrices().stream().map(unitPrice -> {
			return new KrcmtUnitPriceItem(domain.getHisId(), unitPrice.getUnitPriceNo().value, AppContexts.user().companyId(), domain.getSid(), unitPrice.getUnitPrice().v());
		}).collect(Collectors.toList());
		return entityList;
	}
}
