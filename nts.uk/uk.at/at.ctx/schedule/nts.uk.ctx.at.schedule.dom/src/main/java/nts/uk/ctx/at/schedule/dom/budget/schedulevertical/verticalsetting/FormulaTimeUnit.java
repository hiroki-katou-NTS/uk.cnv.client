package nts.uk.ctx.at.schedule.dom.budget.schedulevertical.verticalsetting;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.common.timerounding.Rounding;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;

/**
 * TanLV
 * 計算関数
 * 
 */
@AllArgsConstructor
@Getter
public class FormulaTimeUnit {
	/** 会社ID */
	private String companyId;

	/** コード */
	private String verticalCalCd;
	
	/** 汎用縦計項目ID */
	private String verticalCalItemId;

	/** 単位 */
	private RoundingTime roundingTime;

	/** 端数処理 */
	private Rounding roundingAtr;

	/** 単価 */
	private UnitPrice unitPrice;

	/** 単価 */
	private ActualDisplayAtr actualDisplayAtr;

	private List<TimeUnitFunc> lstTimeUnitFuncs;
	
	/**
	 * Create From Javatype
	 * @param companyId
	 * @param verticalCalCd
	 * @param verticalCalItemId
	 * @param roundingTime
	 * @param roundingAtr
	 * @param unitPrice
	 * @param actualDisplayAtr
	 * @param lstTimeUnitFuncs
	 * @return
	 */
	public static FormulaTimeUnit createFromJavatype(String companyId, String verticalCalCd, String verticalCalItemId,
			int roundingTime, int roundingAtr, int unitPrice, int actualDisplayAtr,List<TimeUnitFunc> lstTimeUnitFuncs) {
		return new FormulaTimeUnit(companyId, verticalCalCd, verticalCalItemId,
				EnumAdaptor.valueOf(roundingTime, RoundingTime.class), EnumAdaptor.valueOf(roundingAtr, Rounding.class),
				EnumAdaptor.valueOf(unitPrice, UnitPrice.class),
				EnumAdaptor.valueOf(actualDisplayAtr, ActualDisplayAtr.class),lstTimeUnitFuncs);
	}
}
