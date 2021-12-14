package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import lombok.Value;
import nts.arc.layer.dom.objecttype.DomainValue;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.common.days.FourWeekDays;
import nts.uk.ctx.at.shared.dom.common.days.WeeklyDays;
import nts.uk.shr.com.time.calendar.MonthDay;

/**
 * 月日起算の休日取得管理
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.月日起算の休日取得管理
 * @author tutk
 *
 */
@Value
public class HolidayAcqManageByMD implements FourWeekHolidayAcqMana, DomainValue {
	
	/**
	 * 起算月日
	 */
	private MonthDay startingMonthDay;
	
	/**
	 * 4週間の休日日数
	 */
	private FourWeekDays  fourWeekHoliday;
	
	/**
	 * 最終週の休日日数
	 */
	private WeeklyDays numberHolidayLastweek;
	
	
	/**
	 * [1] 管理期間の単位を取得する
	 */

	/**
	 * [2] 管理期間を取得する
	 */
	@Override
	public HolidayAcqManaPeriod getManagementPeriod(Require require, GeneralDate ymd) {
		//$起算年月日 = 年月日#年月日を指定(基準日.年,@起算月日.月,@起算月日.日)																
		GeneralDate startingDate = GeneralDate.ymd(ymd.year(), this.startingMonthDay.getMonth(), this.startingMonthDay.getDay());
		//$対象月日 = 月日#(基準日.月,基準日.日)
		MonthDay targetMonthDay = new MonthDay(ymd.month(), ymd.day());
		if (targetMonthDay.getMonth() < (this.startingMonthDay.getMonth())
				|| (targetMonthDay.getMonth() == this.startingMonthDay.getMonth()
						&& targetMonthDay.getDay() < this.startingMonthDay.getDay())) {
			startingDate = GeneralDate.ymd(ymd.addYears(-1).year(), this.startingMonthDay.getMonth(), this.startingMonthDay.getDay());
		}
		//$期間 = [prv-1] 4週間を作る($起算年月日,基準日)
		DatePeriod period = this.make4Weeks(startingDate, ymd);
		//$週間数 = (切り捨て(($期間.終了日 - $起算年月日) / (7 * 4)) ) + 1
		int numberOfWeeks = (new DatePeriod(startingDate,period.end()).datesBetween().size()-1)/(7*4) + 1; 
		
		if(numberOfWeeks<13) {
			//return 休日取得の管理期間#($期間,@4週間の休日日数)
			return new HolidayAcqManaPeriod(period,this.fourWeekHoliday);
		}
		//$期間.終了日 = $起算年月日.年を足す(1).日を足す(-1)  
		period = new DatePeriod(period.start(), startingDate.addYears(1).addDays(-1));

		//return 休日取得の管理期間#($期間,@4週間の休日日数 + 最終週の休日日数)
		return new HolidayAcqManaPeriod(period, new FourWeekDays(this.fourWeekHoliday.v() + this.numberHolidayLastweek.v()));
	}

	/**
	 * [3] 起算日の種類を取得する
	 */
	@Override
	public StartDateClassification getStartDateType() {
		return StartDateClassification.SPECIFY_MD;
	}

	/**
	 * [4] 28日間を取得する
	 */
	@Override
	public DatePeriod get28Days(Require require, GeneralDate ymd) {
		return this.getManagementPeriod(require, ymd).getPeriod();
	}
}
