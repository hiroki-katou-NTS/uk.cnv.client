/**
 * 
 */
package nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;

/**
 * DO : 割増率毎の単価
 * path:UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.人件費計算.社員単価履歴.社員単価履歴
 * @author laitv
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class UnitPricePerNumber extends DomainObject{
	
	// 単価NO
	private UnitPrice unitPriceNo;
	// 単価
	private WorkingHoursUnitPrice unitPrice;
	
	public static UnitPricePerNumber createSimpleFromJavaType(int unitPriceNo, int unitPrice) {
		return new UnitPricePerNumber(EnumAdaptor.valueOf(unitPriceNo, UnitPrice.class), new WorkingHoursUnitPrice(unitPrice));
	}
}
