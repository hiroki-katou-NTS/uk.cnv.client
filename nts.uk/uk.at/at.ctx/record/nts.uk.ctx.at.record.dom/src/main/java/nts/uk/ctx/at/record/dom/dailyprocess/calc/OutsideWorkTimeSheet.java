package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.Optional;

import lombok.Getter;
import lombok.Value;
import nts.uk.ctx.at.record.dom.dailyprocess.calc.record.mekestimesheet.OverTimeWorkSheet;

/**
 * 就業時間外時間帯
 * @author keisuke_hoshina
 *
 */
public class OutsideWorkTimeSheet {
	private Optional<OverTimeWorkSheet> overTimeWorkSheet;
	
	private Optional<HolidayWorkTimeSheet> holidayWorkTimeSheet;

	public OutsideWorkTimeSheet(Optional<OverTimeWorkSheet> overTimeWorkSheet,Optional<HolidayWorkTimeSheet> holidayWorkTimeSheet) {
			setOverTimeWorkSheet(overTimeWorkSheet);
			setHolidayWorkTimeSheet(holidayWorkTimeSheet);
	}

	/**
	 * 残業時間帯のセッター
	 * @param holidayWorkTimeSheet2
	 */
	private void setHolidayWorkTimeSheet(Optional<HolidayWorkTimeSheet> holidayWorkTimeSheet) {
		if(holidayWorkTimeSheet.isPresent()) {
			this.holidayWorkTimeSheet = holidayWorkTimeSheet;
		}
		else {
			this.holidayWorkTimeSheet = null;
		}
	}

	/**
	 * 休出時間帯のセッター
	 * @param overTimeWorkSheet2
	 */
	private void setOverTimeWorkSheet(Optional<OverTimeWorkSheet> overTimeWorkSheet) {
		if(overTimeWorkSheet.isPresent()) {
			this.overTimeWorkSheet = overTimeWorkSheet;
		}
		else {
			this.overTimeWorkSheet = null;
		}
	}
	

}
