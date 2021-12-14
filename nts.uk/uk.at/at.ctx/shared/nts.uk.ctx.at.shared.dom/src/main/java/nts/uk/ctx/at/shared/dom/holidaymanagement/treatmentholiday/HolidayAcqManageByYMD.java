package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;
/**
 * 年月日起算の休日取得管理
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.年月日起算の休日取得管理
 * @author tutk
 *
 */
@Value
public class HolidayAcqManageByYMD implements FourWeekHolidayAcqMana, DomainValue {
	
	/**
	 * 起算日
	 */
	private GeneralDate startingDate;
	
	/**
	 * 4週間の休日日数
	 */
	private FourWeekDays  fourWeekHoliday;
	
	/**
	 * [1] 管理期間の単位を取得する
	 */
//	@Override
//	public HolidayCheckUnit getUnitManagementPeriod() {
//		return HolidayCheckUnit.FOUR_WEEK;
//	}

	/**
	 * [2] 管理期間を取得する
	 */
	@Override
	public HolidayAcqManaPeriod getManagementPeriod(Require require, GeneralDate ymd) {
		DatePeriod period = this.make4Weeks(this.startingDate, ymd);
		return new HolidayAcqManaPeriod(period, this.fourWeekHoliday);
	}

	/**
	 * [3] 起算日の種類を取得する
	 */
	@Override
	public StartDateClassification getStartDateType() {
		return StartDateClassification.SPECIFY_YMD;
	}

	/**
	 * [4] 28日間を取得する
	 */
	@Override
	public DatePeriod get28Days(Require require, GeneralDate ymd) {
		return this.getManagementPeriod(require, ymd).getPeriod();
	}

}
