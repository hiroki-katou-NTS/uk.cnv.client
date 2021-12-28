package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;

/** 社員単価履歴項目 */
@Getter
@AllArgsConstructor
public class EmployeeUnitPriceHistoryItem extends AggregateRoot {
	
	/** 社員ID */
	private final String employeeId;
	
	/** 履歴ID */
	private final String historyId;
	
	/** 単価一覧 */
	private List<UnitPricePerNumber> unitPrices;
	
	/**
	 * 社員時間単価を取得する
	 * @param unitPriceNo 社員時間単価NO
	 * @return 社員時間単価
	 */
	public Optional<WorkingHoursUnitPrice> getWorkingHoursUnitPrice(UnitPrice no){
		return this.unitPrices.stream()
				.filter(u -> u.getNo().equals(no))
				.map(u -> u.getUnitPrice())
				.findFirst();
	}
}
