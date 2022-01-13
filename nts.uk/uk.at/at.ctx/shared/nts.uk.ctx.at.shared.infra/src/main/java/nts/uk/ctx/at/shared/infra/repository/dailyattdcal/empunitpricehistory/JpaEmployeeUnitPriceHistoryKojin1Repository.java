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
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistory;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.UnitPricePerNumber;
import nts.uk.ctx.at.shared.infra.entity.dailyattdcal.empunitpricehistory.KrcmtUnitPrice;
import nts.uk.ctx.at.shared.infra.entity.dailyattdcal.empunitpricehistory.KrcmtUnitPriceItem;
import nts.uk.ctx.at.shared.infra.entity.dailyattdcal.empunitpricehistory.KrcmtUnitPricePK;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * @author laitv
 * 社員単価履歴Repository
 */
@Stateless
public class JpaEmployeeUnitPriceHistoryKojin1Repository extends JpaRepository implements EmployeeUnitPriceHistoryRepository{

	private static final String SELECT_ITEM_BY_EMPID_BASEDATE = "SELECT upi FROM KrcmtUnitPriceItem upi"
			+ " INNER JOIN KrcmtUnitPrice up ON up.pk.histId = upi.pk.histId"
			+ " WHERE up.pk.sid = :sid AND up.startDate <= :baseDate AND :baseDate <= up.endDate";
	
	private static final String SELECT_HIST_BY_EMPID_BASEDATE = "SELECT up FROM KrcmtUnitPrice up "
			+ " WHERE up.pk.sid = :sid AND up.startDate <= :baseDate AND :baseDate <= up.endDate";
	
	private static final String SELECT_BY_HISTID = "SELECT up FROM KrcmtUnitPrice up WHERE up.pk.histId = :histId";
	
	private static final String SELECT_BY_HISTIDLIST = "SELECT up FROM KrcmtUnitPrice up up.pk.sid = :sid AND up.pk.histId IN :histIdList";
	
	private static final String SELECT_BY_SIDS_AND_CID = "SELECT up FROM KrcmtUnitPrice up "
			+ " WHERE up.cid = :cid AND up.pk.sid IN :sids ORDER BY up.pk.sid, up.startDate DESC";
	
	private static final String SELECT_BY_SIDS_AND_CID_AND_BASEDATE = "SELECT up FROM KrcmtUnitPrice up "
			+ " WHERE up.cid = :cid AND up.pk.sid IN :sids AND up.startDate <= :baseDate AND :baseDate <= up.endDate";
	
	private static final String SELECT_BY_SID_DESC = "SELECT up FROM KrcmtUnitPrice up WHERE up.cid = :cid AND up.pk.sid = :sid ORDER BY up.startDate DESC";
	
	private static final String SELECT_BY_SID = "SELECT up FROM KrcmtUnitPrice up WHERE up.cid = :cid AND up.pk.sid = :sid ORDER BY up.startDate";
	
	private static final String SELECT_BY_EMPID_BASEDATE = "SELECT upi FROM KrcmtUnitPriceItem upi"
			+ " INNER JOIN  KrcmtUnitPrice up ON up.pk.histId = upi.pk.histId"
			+ " WHERE up.pk.sid = :sid AND up.startDate <= :baseDate AND :baseDate <= up.endDate";
	
	@Override
	public Optional<EmployeeUnitPriceHistoryItem> getByEmployeeIdAndBaseDate(String sid, GeneralDate baseDate) {
		List<KrcmtUnitPriceItem> listHistItem = this.queryProxy()
				.query(SELECT_ITEM_BY_EMPID_BASEDATE, KrcmtUnitPriceItem.class)
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
	public Optional<EmployeeUnitPriceHistory> getHistByEmployeeIdAndBaseDate(String sid, GeneralDate baseDate) {
		if (baseDate == null) return Optional.empty();
		List<KrcmtUnitPrice> listHist = this.queryProxy()
				.query(SELECT_HIST_BY_EMPID_BASEDATE, KrcmtUnitPrice.class)
				.setParameter("sid", sid).setParameter("baseDate", baseDate)
				.getList();

		if (listHist.isEmpty()) {
			return Optional.empty();
		}

		// Return
		return Optional.of(toHistDomain(listHist));
	}
	
	private EmployeeUnitPriceHistory toHistDomain(List<KrcmtUnitPrice> entities) {
		List<DateHistoryItem> listDateHistItem = new ArrayList<DateHistoryItem>();
		entities.forEach(entity -> {
			listDateHistItem.add(new DateHistoryItem(entity.pk.histId, new DatePeriod(entity.startDate, entity.endDate)));
		});
		return new EmployeeUnitPriceHistory(entities.get(0).pk.sid, listDateHistItem);
	}

	@Override
	public Optional<EmployeeUnitPriceHistory> getHistByHistId(String histId) {
		List<KrcmtUnitPrice> listHist = this.queryProxy()
				.query(SELECT_BY_HISTID, KrcmtUnitPrice.class)
				.setParameter("histId", histId)
				.getList();

		if (listHist.isEmpty()) {
			return Optional.empty();
		}

		// Return
		return Optional.of(toHistDomain(listHist));
	}

	@Override
	public List<EmployeeUnitPriceHistory> getBySidsAndCid(List<String> employeeIds, String cid) {
		if (employeeIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<KrcmtUnitPrice> listHist = this.queryProxy()
				.query(SELECT_BY_SIDS_AND_CID, KrcmtUnitPrice.class)
				.setParameter("cid", cid).setParameter("sids", employeeIds)
				.getList();

		if (listHist.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<EmployeeUnitPriceHistory> listHistDomain = new ArrayList<EmployeeUnitPriceHistory>();
		
		Map<String, List<KrcmtUnitPrice>> listHistMapBySid = listHist.stream().collect(Collectors.groupingBy(entity -> entity.pk.sid));
		
		for (Map.Entry<String, List<KrcmtUnitPrice>> entry : listHistMapBySid.entrySet()) {
		    if (!entry.getValue().isEmpty()) {
		    	listHistDomain.add(toHistDomain(entry.getValue()));
		    }
		}
		
		return listHistDomain;
	}

	@Override
	public Optional<EmployeeUnitPriceHistory> getBySidDesc(String cid, String sid) {
		List<KrcmtUnitPrice> listHist = this.queryProxy()
				.query(SELECT_BY_SID_DESC, KrcmtUnitPrice.class)
				.setParameter("cid", cid).setParameter("sid", sid)
				.getList();

		if (listHist.isEmpty()) {
			return Optional.empty();
		}

		// Return
		return Optional.of(toHistDomain(listHist));
	}
	
	@Override
	public Optional<EmployeeUnitPriceHistory> getBySid(String cid, String sid) {
		List<KrcmtUnitPrice> listHist = this.queryProxy()
				.query(SELECT_BY_SID, KrcmtUnitPrice.class)
				.setParameter("cid", cid).setParameter("sid", sid)
				.getList();

		if (listHist.isEmpty()) {
			return Optional.empty();
		}

		// Return
		return Optional.of(toHistDomain(listHist));
	}

	@Override
	public void add(EmployeeUnitPriceHistory employeeUnitPriceHistory) {
		this.commandProxy().insertAll(toEntity(employeeUnitPriceHistory));
	}

	@Override
	public void update(EmployeeUnitPriceHistory employeeUnitPriceHistory) {
		List <String> lstHistId = employeeUnitPriceHistory.getHistoryItems()
				.stream().map( c -> c.identifier()).collect(Collectors.toList());
		
		if (lstHistId.isEmpty()) {
			return;
		}

		List<KrcmtUnitPrice> oldData = this.queryProxy().query(SELECT_BY_HISTIDLIST, KrcmtUnitPrice.class)
																  .setParameter("sid", employeeUnitPriceHistory.getSid())
																  .setParameter("histIdList", lstHistId)
																  .getList();
		
		
		List<KrcmtUnitPrice> newData = toEntity(employeeUnitPriceHistory);
		
		oldData.forEach(x->{
			Optional<KrcmtUnitPrice> newDataOldId = newData.stream().filter(el -> el.pk.histId.equals(x.pk.histId)).findFirst();
			if (newDataOldId.isPresent()) {
				KrcmtUnitPrice y = newDataOldId.get();
				x.startDate = y.startDate;
			    x.endDate = y.endDate;
			}
		});
		
		this.commandProxy().updateAll(oldData);
	}
	
	@Override
	public void update(DateHistoryItem itemToBeUpdated) {
		Optional<KrcmtUnitPrice> histItemOp = this.queryProxy()
				.query(SELECT_BY_HISTID, KrcmtUnitPrice.class)
				.setParameter("histId", itemToBeUpdated.identifier())
				.getSingle();
		if (!histItemOp.isPresent()) {
			throw new RuntimeException("Invalid KrcmtUnitPrice");
		}
		KrcmtUnitPrice histItem = histItemOp.get();
		histItem.startDate = itemToBeUpdated.start();
		histItem.endDate = itemToBeUpdated.end();
		this.commandProxy().update(histItem);
	}

	@Override
	public void delete(String companyId, String empId, String historyId) {
		this.commandProxy().remove(KrcmtUnitPrice.class, new KrcmtUnitPricePK(empId, historyId));
	}

	private List<KrcmtUnitPrice> toEntity(EmployeeUnitPriceHistory domain) {
		List<KrcmtUnitPrice> entityList = domain.getHistoryItems().stream().map(histItem -> {
			return new KrcmtUnitPrice(domain.getSid(), histItem.identifier(), AppContexts.user().companyId(), histItem.start(), histItem.end());
		}).collect(Collectors.toList());
		return entityList;
	}
	
	private KrcmtUnitPrice toEntity(String sid, DateHistoryItem histItem) {
		return new KrcmtUnitPrice(sid, histItem.identifier(), AppContexts.user().companyId(), histItem.start(), histItem.end());
	}
	
	@Override
	public void add(String sid, DateHistoryItem domain) {
		this.commandProxy().insert(toEntity(sid, domain));
	}

	@Override
	public List<EmployeeUnitPriceHistory> getBySidsAndCidAndBaseDate(List<String> employeeIds, String cid,
			GeneralDate baseDate) {
		if (employeeIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<KrcmtUnitPrice> listHist = this.queryProxy()
				.query(SELECT_BY_SIDS_AND_CID_AND_BASEDATE, KrcmtUnitPrice.class)
				.setParameter("cid", cid).setParameter("sids", employeeIds).setParameter("baseDate", baseDate)
				.getList();

		if (listHist.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<EmployeeUnitPriceHistory> listHistDomain = new ArrayList<EmployeeUnitPriceHistory>();
		
		Map<String, List<KrcmtUnitPrice>> listHistMapBySid = listHist.stream().collect(Collectors.groupingBy(entity -> entity.pk.sid));
		
		for (Map.Entry<String, List<KrcmtUnitPrice>> entry : listHistMapBySid.entrySet()) {
		    if (!entry.getValue().isEmpty()) {
		    	listHistDomain.add(toHistDomain(entry.getValue()));
		    }
		}
		
		return listHistDomain;
	}
}
