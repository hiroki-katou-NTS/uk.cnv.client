package nts.uk.ctx.at.schedule.dom.budget.schedulevertical.verticalsetting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;
/**
 * 平均単価計算式
 * @author phongtq
 *
 */
@AllArgsConstructor
@Getter
public class FormulaUnitprice {
	/** 会社ID **/
	private String companyId;

	/** コード **/
	private String verticalCalCd;
	
	/** 汎用縦計項目ID **/
	private String verticalCalItemId;
	
	/**出勤区分**/
	private AttendanceAtr attendanceAtr;
	
	/**単価**/
	private UnitPrice unitPrice;
	
	public static FormulaUnitprice createFromJavatype(String companyId, String verticalCalCd, String verticalCalItemId,
			int attendanceAtr, int unitPrice) {
		return new FormulaUnitprice(companyId, verticalCalCd, verticalCalItemId,
				EnumAdaptor.valueOf(attendanceAtr, AttendanceAtr.class),
				EnumAdaptor.valueOf(unitPrice, UnitPrice.class));
	}
}
