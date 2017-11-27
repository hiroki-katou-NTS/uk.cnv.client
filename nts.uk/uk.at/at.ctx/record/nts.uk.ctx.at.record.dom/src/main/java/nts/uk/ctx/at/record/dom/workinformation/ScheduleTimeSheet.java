package nts.uk.ctx.at.record.dom.workinformation;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.record.dom.worktime.primitivevalue.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author nampt
 * 予定時間帯
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ScheduleTimeSheet extends DomainObject{
	
	private WorkNo workNo;
	
	private TimeWithDayAttr attendance;
	
	private TimeWithDayAttr leaveWork;

	public ScheduleTimeSheet(BigDecimal workNo, int attendance, int leaveWork) {
		super();
		this.workNo = new WorkNo(workNo);
		this.attendance = new TimeWithDayAttr(attendance);
		this.leaveWork = new TimeWithDayAttr(leaveWork);
	}
	
}
