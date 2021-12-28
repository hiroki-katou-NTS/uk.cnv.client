package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.ouen;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;

@Getter
/** 応援作業集計明細 */
public class OuenWorkAggregateDetail implements DomainObject {

	/**　総集計時間: 勤怠月間時間　*/
	private AttendanceTimeMonth totalTime;

	/**　合計金額: 勤怠月間金額　*/
	private AttendanceAmountMonth totalAmount;

	private OuenWorkAggregateDetail(AttendanceTimeMonth totalTime, AttendanceAmountMonth totalAmount) {
		super();
		this.totalTime = totalTime;
		this.totalAmount = totalAmount;
	}
	
	public static OuenWorkAggregateDetail empty() {
		return OuenWorkAggregateDetail.create(new AttendanceTimeMonth(0), new AttendanceAmountMonth(0L));
	}
	
	public static OuenWorkAggregateDetail create(
			AttendanceTimeMonth totalTime, AttendanceAmountMonth totalAmount) {

		return new OuenWorkAggregateDetail(totalTime, totalAmount);
	}
	
	public void plus(OuenWorkTimeOfDailyAttendance ouenTime) {
		
		totalTime = totalTime.addMinutes(ouenTime.getWorkTime().getTotalTime().valueAsMinutes());
		
		totalAmount = totalAmount.addAmount(ouenTime.getAmount().v());
	}
}
