package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class CalculateAttendanceTimeBySupportWorkServiceHelper {


	public static IntegrationOfDaily getIntegrationOfDaily() {
		
		TimeActualStamp actualStamp = new TimeActualStamp(new WorkStamp(new TimeWithDayAttr(100),
				new WorkLocationCD("001"),
				EnumAdaptor.valueOf(0, TimeChangeMeans.class)),
			new WorkStamp(new TimeWithDayAttr(200),
					new WorkLocationCD("002"),
					EnumAdaptor.valueOf(0, TimeChangeMeans.class)),
			1);
		
		List<TimeLeavingWork> timeleavings = new ArrayList<>();
		
//		TimeLeavingWork leavingWork = new TimeLeavingWork(new WorkNo(1), actualStamp, actualStamp);
		TimeLeavingWork leavingWork = new TimeLeavingWork(new WorkNo(1), Optional.of(actualStamp), Optional.of(actualStamp), true, true);
		
		timeleavings.add(leavingWork);
		timeleavings.add(leavingWork);
		
		IntegrationOfDaily integrationOfDaily = new IntegrationOfDaily(
				"employeeId",
				GeneralDate.today(),
				null,
				null,
				null,
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				null,
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				Optional.empty());
				
				return integrationOfDaily;
	}
	
public static IntegrationOfDaily getIntegrationOfDaily1() {
		
//		TimeActualStamp actualStamp = new TimeActualStamp(new WorkStamp(new TimeWithDayAttr(100),
//				new WorkLocationCD("001"),
//				EnumAdaptor.valueOf(0, TimeChangeMeans.class)),
//			new WorkStamp(new TimeWithDayAttr(200),
//					new WorkLocationCD("002"),
//					EnumAdaptor.valueOf(0, TimeChangeMeans.class)),
//			1);
		
		List<TimeLeavingWork> timeleavings = new ArrayList<>();
		
//		TimeLeavingWork leavingWork = new TimeLeavingWork(new WorkNo(1), actualStamp, actualStamp);
		TimeLeavingWork leavingWork = new TimeLeavingWork(new WorkNo(1), Optional.empty(), Optional.empty(), true, true);
		
		timeleavings.add(leavingWork);
		timeleavings.add(leavingWork);
	
		
		IntegrationOfDaily integrationOfDaily = new IntegrationOfDaily("employeeId",
				GeneralDate.today(),
				null,
				null,
				null,
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				null,
				Optional.empty(),
				Optional.of(new TimeLeavingOfDailyAttd(timeleavings, new WorkTimes(1))),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				Optional.empty(),
				new ArrayList<>(),
				Optional.empty(),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				Optional.empty());
				
				return integrationOfDaily;
	}

}
