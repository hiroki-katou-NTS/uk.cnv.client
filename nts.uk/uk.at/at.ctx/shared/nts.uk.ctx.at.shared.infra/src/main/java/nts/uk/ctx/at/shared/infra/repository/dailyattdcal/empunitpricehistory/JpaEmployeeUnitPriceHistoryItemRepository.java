/**
 * 
 */
package nts.uk.ctx.at.shared.infra.repository.dailyattdcal.empunitpricehistory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.arc.layer.infra.data.JpaRepository;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItemRepository;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.UnitPricePerNumber;
import nts.uk.ctx.at.shared.infra.entity.dailyattdcal.empunitpricehistory.KrcmtUnitPriceItem;

/**
 * @author laitv
 * 社員単価履歴項目Repository
 */
@Stateless
public class JpaEmployeeUnitPriceHistoryItemRepository extends JpaRepository implements EmployeeUnitPriceHistoryItemRepository{

	private static final String SELECT_BY_EMPID_BASEDATE = "SELECT upi FROM KrcmtUnitPriceItem upi"
			+ " INNER JOIN  KrcmtUnitPrice up ON up.pk.histId = upi.pk.histId"
			+ " WHERE up.pk.sid = :sid AND up.startDate <= :baseDate AND :baseDate <= up.endDate";
	
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


}
