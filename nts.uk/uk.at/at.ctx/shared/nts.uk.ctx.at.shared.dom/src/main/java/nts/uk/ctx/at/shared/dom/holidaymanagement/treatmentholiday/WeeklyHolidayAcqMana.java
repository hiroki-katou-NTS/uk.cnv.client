package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.seek.DateSeek;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;
import nts.uk.ctx.at.shared.dom.common.days.WeeklyDays;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagement;

/**
 * 1週間単位の休日取得管理
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.1週間単位の休日取得管理
 * 
 * @author tutk
 *
 */
@Value
public class WeeklyHolidayAcqMana implements HolidayAcquisitionManagement, DomainValue {

	/**
	 * 1週間の休日日数
	 */
	private WeeklyDays weeklyDays;

	/**
	 * [1] 管理期間の単位を取得する
	 * 
	 * @return
	 */
	@Override
	public HolidayCheckUnit getUnitManagementPeriod() {
		return HolidayCheckUnit.ONE_WEEK;
	}

	/**
	 * [2] 管理期間を取得する
	 * 
	 * @param require
	 * @param baseDate 基準日
	 * @return
	 */
	@Override
	public HolidayAcqManaPeriod getManagementPeriod(HolidayAcquisitionManagement.Require require,
			GeneralDate baseDate) {
		// $仮設定 = require.開始曜日を取得する
		WeekRuleManagement weekRuleManagement = require.find(); 
		// $開始日 = 基準日.日を足す(1).直前の日(年月日の検索#($週の管理.週開始)
		baseDate = baseDate.addDays(1).previous(DateSeek.dayOfWeek(weekRuleManagement.getDayOfWeek()));
		// $期間 = 期間#期間($開始日,$開始日#日を足す(6))
		DatePeriod period = new DatePeriod(baseDate, baseDate.addDays(6));
		// return 休日取得の管理期間#($期間,@1週間の休日日数)
		return new HolidayAcqManaPeriod(period, new FourWeekDays(this.weeklyDays.v()));
	}

	public static interface Require {
		/**
		 * [R-1] 開始曜日を取得する
		 * 
		 * @param companyId
		 * @return
		 */
		public WeekRuleManagement find();
	}
}
