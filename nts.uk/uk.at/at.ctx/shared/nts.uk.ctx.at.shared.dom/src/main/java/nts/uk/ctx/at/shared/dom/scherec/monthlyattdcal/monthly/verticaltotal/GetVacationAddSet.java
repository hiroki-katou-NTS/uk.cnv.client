package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.val;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;

/**
 * 休暇加算設定を取得する
 * @author shuichu_ishida
 */
public class GetVacationAddSet {

	/**
	 * 休暇加算設定を取得する
	 * @param companyId 会社ID
	 * @return 休暇加算設定
	 */
	public static VacationAddSet get(RequireM1 require, String companyId) {
		
		boolean annualLeave = false;
		boolean retentionYearly = false;
		List<String> specialHolidays = new ArrayList<>();
		
		val annualLeaveSet = require.annualPaidLeaveSetting(companyId);
		if (annualLeaveSet != null) {
			annualLeave = annualLeaveSet.getManageAnnualSetting().isWorkDayCalculate();
		}
		val retentionYearlySet = require.retentionYearlySetting(companyId);
		if (retentionYearlySet.isPresent()){
			retentionYearly = retentionYearlySet.get().getLeaveAsWorkDays();
		}
		//*****（未）　特別休暇の設定クラスが未実装または誤り。判定方法の設計確認要。
		
		return VacationAddSet.of(annualLeave, retentionYearly, specialHolidays);
	}
	
	public static interface RequireM1 {
		
		AnnualPaidLeaveSetting annualPaidLeaveSetting(String companyId);
		
		Optional<RetentionYearlySetting> retentionYearlySetting(String companyId);
	}
}
