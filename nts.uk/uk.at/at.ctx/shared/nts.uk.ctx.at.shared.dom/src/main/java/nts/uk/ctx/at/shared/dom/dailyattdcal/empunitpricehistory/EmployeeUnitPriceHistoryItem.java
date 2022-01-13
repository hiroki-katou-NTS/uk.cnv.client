package nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;

/**
 * AR: 社員単価履歴項目
 * path:UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.人件費計算.社員単価履歴.社員単価履歴
 * @author laitv
 *
 */
@Getter
@AllArgsConstructor
public class EmployeeUnitPriceHistoryItem extends AggregateRoot {
	/* 社員ID */
	private String sid;
	/* 履歴ID */
	private String hisId;
	/* 単価一覧 */
	private List<UnitPricePerNumber> unitPrices;
	
	public static EmployeeUnitPriceHistoryItem createSimpleFromJavaType(String sid, String hisId, List<UnitPricePerNumber> unitPrices) {
		return new EmployeeUnitPriceHistoryItem(sid, hisId, unitPrices);
	}
	
	// 社員時間単価を取得する()
	public Optional<WorkingHoursUnitPrice> getEmployeeHourlyUnitPrice(UnitPrice unitPriceNo){
		Optional<UnitPricePerNumber> unitPrice = this.unitPrices.stream().filter(i -> i.getUnitPriceNo().value == unitPriceNo.value).findFirst();
		return unitPrice.isPresent() ? Optional.of(unitPrice.get().getUnitPrice()) : Optional.empty();
	}
}
