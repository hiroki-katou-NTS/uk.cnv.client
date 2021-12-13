package nts.uk.ctx.at.aggregation.dom.common;

import java.util.Arrays;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

public class TimeLeavingOfDailyAttdHelperInAggregation {
	
	/**
	 * @param start1 開始時刻１
	 * @param end1 終了時刻１
	 * @param start2 開始時刻２
	 * @param end2 終了時刻２
	 * @return
	 */
	public static TimeLeavingOfDailyAttd create(
			TimeWithDayAttr start1,
			TimeWithDayAttr end1,
			Optional<TimeWithDayAttr> start2,
			Optional<TimeWithDayAttr> end2) {
		
		// 勤務時刻情報 １
		WorkTimeInformation start1_wti = new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), start1);
		WorkTimeInformation end1_wti = new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), end1);
		// 勤怠打刻１
		WorkStamp start1_ws = new WorkStamp(start1_wti, Optional.empty());
		WorkStamp end1_ws = new WorkStamp(end1_wti, Optional.empty());
		// 勤怠打刻（実打刻付き）１
		TimeActualStamp timeActualStampStart1 = new TimeActualStamp(Optional.empty(), Optional.of(start1_ws), 1, Optional.empty(), Optional.empty());
		TimeActualStamp timeActualStampEnd1 = new TimeActualStamp(Optional.empty(), Optional.of(end1_ws), 1, Optional.empty(), Optional.empty());
		// 出退勤１
		TimeLeavingWork timeLeavingWork1 = new TimeLeavingWork(
				new WorkNo(1), 
				Optional.of(timeActualStampStart1), 
				Optional.of(timeActualStampEnd1), 
				false, 
				false);
		
		
		if ( !start2.isPresent() || !end2.isPresent() ) {
			return new TimeLeavingOfDailyAttd( Arrays.asList(timeLeavingWork1), new WorkTimes(1));
		}
		
		// 勤務時刻情報 ２
		WorkTimeInformation start2_wti = new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), start2.get());
		WorkTimeInformation end2_wti = new WorkTimeInformation(ReasonTimeChange.createByAutomaticSet(), end2.get());
		// 勤怠打刻２
		WorkStamp start2_ws = new WorkStamp(start2_wti, Optional.empty());
		WorkStamp end2_ws = new WorkStamp(end2_wti, Optional.empty());
		// 勤怠打刻（実打刻付き）２
		TimeActualStamp timeActualStampStart2 = new TimeActualStamp(Optional.empty(), Optional.of(start2_ws), 1, Optional.empty(), Optional.empty());
		TimeActualStamp timeActualStampEnd2 = new TimeActualStamp(Optional.empty(), Optional.of(end2_ws), 1, Optional.empty(), Optional.empty());
		TimeLeavingWork timeLeavingWork2 = new TimeLeavingWork(
				new WorkNo(2), 
				Optional.of(timeActualStampStart2), 
				Optional.of(timeActualStampEnd2), 
				false, 
				false);
		
		return new TimeLeavingOfDailyAttd( Arrays.asList(timeLeavingWork1, timeLeavingWork2), new WorkTimes(2) );
		
	}

}
