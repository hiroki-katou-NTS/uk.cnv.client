package nts.uk.ctx.at.shared.dom.workrule.outsideworktime;

import lombok.Data;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums.TimeItemTypeAtr;

/**
 * 加給の自動計算設定
 * @author keisuke_hoshina
 *
 */
@Data
public class AutoCalRaisingSalarySetting {
	private boolean specificRaisingSalaryCalcAtr;
	private boolean raisingSalaryCalcAtr;
	
	public AutoCalRaisingSalarySetting(boolean specificRaisingSalaryCalcAtr, boolean raisingSalaryCalcAtr) {
		super();
		this.specificRaisingSalaryCalcAtr = specificRaisingSalaryCalcAtr;
		this.raisingSalaryCalcAtr = raisingSalaryCalcAtr;
	}
	
	public static AutoCalRaisingSalarySetting defaultValue(){
		return new AutoCalRaisingSalarySetting(false, false);
	}
	
	/**
	 * 計算するか
	 * @param timeItemTypeAtr 加給区分
	 * @return true:計算する、false:計算しない
	 */
	public boolean isCalc(TimeItemTypeAtr timeItemTypeAtr) {
		if(timeItemTypeAtr.isNomalType()) {
			return raisingSalaryCalcAtr;
		}
		return specificRaisingSalaryCalcAtr;
	}
}
