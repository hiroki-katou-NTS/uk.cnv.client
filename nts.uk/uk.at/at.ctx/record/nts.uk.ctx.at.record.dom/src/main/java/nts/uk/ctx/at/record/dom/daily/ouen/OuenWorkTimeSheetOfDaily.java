package nts.uk.ctx.at.record.dom.daily.ouen;


import java.util.List;

import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

@Getter
/** 日別実績の応援作業別勤怠時間帯 */
public class OuenWorkTimeSheetOfDaily extends AggregateRoot {

	/** 社員ID: 社員ID */
	private String empId;
	
	/** 年月日: 年月日 */
	private GeneralDate ymd;
	
	/** 応援時間帯: 日別勤怠の応援作業時間帯 */
	private List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet;

	public OuenWorkTimeSheetOfDaily(String empId, GeneralDate ymd, List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet) {
		super();
		this.empId = empId;
		this.ymd = ymd;
		this.ouenTimeSheet = ouenTimeSheet;
	}
	
	public static OuenWorkTimeSheetOfDaily create(String empId, GeneralDate ymd,
			List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheet) {

		return new OuenWorkTimeSheetOfDaily(empId, ymd, ouenTimeSheet);
	}
}
