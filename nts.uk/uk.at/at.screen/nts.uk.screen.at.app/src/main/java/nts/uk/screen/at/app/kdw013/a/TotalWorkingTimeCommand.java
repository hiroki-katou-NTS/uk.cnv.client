package nts.uk.screen.at.app.kdw013.a;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.WorkTimes;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.OutingTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.earlyleavetime.LeaveEarlyTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.interval.IntervalTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.latetime.LateTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.paytime.RaiseSalaryTimeOfDailyPerfor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.temporarytime.TemporaryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.vacationusetime.HolidayOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.worktime.TotalWorkingTime;

@AllArgsConstructor
@Getter
public class TotalWorkingTimeCommand {
	

	/** 総労働時間 */
	private Integer totalTime;
	
	/** 総計算時間 */
	private Integer totalCalcTime;
	
	/** 実働時間 */
	private Integer actualTime;
	
	/** 計算差異時間 */
	private Integer calcDiffTime ;
	
	/** 日別実績の所定内時間 - 所定内時間 (new) */
	private WithinStatutoryTimeOfDailyCommand withinStatutoryTimeOfDaily;
	 
	/** 日別実績の所定外時間 - 所定外時間 (new) */
	private ExcessOfStatutoryTimeOfDailyCommand excessOfStatutoryTimeOfDaily;
	
	/** 日別実績の遅刻時間 */
	private List<LateTimeOfDaily> lateTimeOfDaily = new ArrayList<>();
	
	/** 日別実績の早退時間 */
	private List<LeaveEarlyTimeOfDaily> leaveEarlyTimeOfDaily = new ArrayList<>(); 
	
	/** 日別実績の休憩時間 */
	private BreakTimeOfDaily breakTimeOfDaily;
	
	/** 日別実績の外出時間 */
	private List<OutingTimeOfDaily> outingTimeOfDailyPerformance;
		
	/** 加給時間 */
	private RaiseSalaryTimeOfDailyPerfor raiseSalaryTimeOfDailyPerfor;
	
	/** 勤務回数 */
	private WorkTimes workTimes;
	
	/** 日別実績の臨時時間 */
	private TemporaryTimeOfDaily temporaryTime;
	
	/** 短時間勤務時間 */
	private ShortWorkTimeOfDaily shotrTimeOfDaily;
	
	/** 日別実績の休暇時間 */
	private HolidayOfDaily holidayOfDaily;
	
	/** 休暇加算時間 */
	private AttendanceTime vacationAddTime = new AttendanceTime(0);
	
	/** インターバル時間: 日別勤怠のインターバル時間 */
	private IntervalTimeOfDaily intervalTime;

	public TotalWorkingTime toDomain() {
		// TODO Auto-generated method stub
		return null;
	}

}
