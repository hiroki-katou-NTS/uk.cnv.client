package nts.uk.ctx.at.shared.dom.holidaymanagement.treatmentholiday;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nts.arc.layer.dom.objecttype.DomainAggregate;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 休日の扱い : Root
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.休日管理.休日の扱い.休日の扱い
 * 
 * @author tutk
 *
 */
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class TreatmentHoliday implements DomainAggregate {
	/**
	 * 会社ID
	 */
	private final String companyId;

	/**
	 * 法定外休日を休日取得数に加える
	 */
	private NotUseAtr addNonstatutoryHolidays;

	/**
	 * 休日取得管理
	 */
	private HolidayAcquisitionManagement holidayManagement;

	/**
	 * [1] 休日取得数と管理期間を取得する
	 * 
	 * @param require
	 * @param referenceDate 基準日
	 * @return
	 */
	public HolidayNumberManagement getNumberHoliday(Require require, GeneralDate referenceDate) {
		// $休日取得の管理期間 = @休日取得管理.管理期間を取得する(require,基準日)
		HolidayAcqManaPeriod holidayAcqManaPeriod = this.holidayManagement.getManagementPeriod(require, referenceDate);

		// return 休日取得数管理#作成する($休日取得の管理期間,@法定外休日を休日取得数に加える)
		return HolidayNumberManagement.create(holidayAcqManaPeriod, this.addNonstatutoryHolidays);
	}
	
	/**
	 * 28日間を取得する
	 * @param require
	 * @param ymd 年月日
	 * @return
	 */
	public DatePeriod get28Days(Require require, GeneralDate ymd) {
		return this.holidayManagement.get28Days(require, ymd);
	}
	
	

	public static interface Require extends HolidayAcquisitionManagement.Require {

	}
}
