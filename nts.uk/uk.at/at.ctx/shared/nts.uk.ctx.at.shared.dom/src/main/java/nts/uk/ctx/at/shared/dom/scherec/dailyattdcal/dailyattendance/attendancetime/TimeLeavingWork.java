package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime;

import java.util.Optional;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.TimeActualStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkStamp;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.LogOnInfo;
import nts.uk.ctx.at.shared.dom.worktime.common.GoLeavingWorkAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowCalculateSet;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author nampt
 * 出退勤
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class TimeLeavingWork extends DomainObject{
	
	/** 勤務NO */
	private WorkNo workNo;
	/** 出勤 */
	private Optional<TimeActualStamp> attendanceStamp;
	/** 退勤 */
	private Optional<TimeActualStamp> leaveStamp;
	/** 遅刻を取り消した */
	private boolean canceledLate = false;
	/** 早退を取り消した */
	private boolean CanceledEarlyLeave = false;
	// fix bug 114181
	//private TimeSpanForCalc timespan;
	
	// fix bug 114181
	public TimeSpanForCalc getTimespan() {
		TimeWithDayAttr att_attr = getAttendanceTime().orElse(null); //出勤（実じゃない）
		TimeWithDayAttr lea_attr  = getLeaveTime().orElse(null);//退勤（実じゃない）    
		return new TimeSpanForCalc(att_attr, lea_attr);
	}
	
	public TimeLeavingWork(WorkNo workNo, TimeActualStamp attendanceStamp, TimeActualStamp leaveStamp) {
		super();
		this.workNo = workNo;
		this.attendanceStamp = Optional.ofNullable(attendanceStamp);
		this.leaveStamp = Optional.ofNullable(leaveStamp);
		// fix bug 114181
		//this.timespan = this.craeteTimeSpan();
	}
	
	/**
	 * 時間帯から作る
	 * @param workNo 勤務NO
	 * @param timeSpan 時間帯
	 * @return
	 */
	public static TimeLeavingWork createFromTimeSpan(WorkNo workNo, TimeSpanForCalc timeSpan) {
		
		return new TimeLeavingWork(
				workNo, 
				Optional.of(TimeActualStamp.createByAutomaticSet(timeSpan.getStart())), 
				Optional.of(TimeActualStamp.createByAutomaticSet(timeSpan.getEnd())), 
				false, 
				false);
	}
	
	/**
	 * PCログオンログオフを打刻へ入れこむ(大塚モード専用処理)
	 * @param pcLogOnInfo
	 */
	public void setStampFromPCLogOn(Optional<LogOnInfo> pcLogOnInfo) {
		pcLogOnInfo.ifPresent(pc -> {
			
			pc.getLogOn().ifPresent(lo -> {
				this.attendanceStamp.ifPresent(at -> {
					at.setStampFromPcLogOn(lo, GoLeavingWorkAtr.GO_WORK);
				});
			});

			pc.getLogOff().ifPresent(lo -> {
				this.leaveStamp.ifPresent(at -> {
					at.setStampFromPcLogOn(lo, GoLeavingWorkAtr.LEAVING_WORK);
				});
			});
		});
	}

	/**
	 * 出勤時刻と退勤時刻から計算用時間帯クラス作成
	 * @return 計算用時間帯クラス
	 */
	// fix bug 114181
	/*
	 * private TimeSpanForCalc craeteTimeSpan() {
	 * 
	 * TimeWithDayAttr att_attr = getAttendanceTime().orElse(null); //出勤（実じゃない）
	 * TimeWithDayAttr lea_attr = getLeaveTime().orElse(null);//退勤（実じゃない）
	 * 
	 * return new TimeSpanForCalc(att_attr, lea_attr); }
	 */

	/**
	 * 出勤時刻と退勤時刻から時間帯クラス作成
	 * @return 時間帯
	 */
	public TimeZone getTimeZone() {
		TimeSpanForCalc timespan = this.getTimespan();
		return new TimeZone(timespan.getStart(), timespan.getEnd());
		
	}
	
	/**
	 * ジャスト遅刻・早退の設定を見て時刻を調整する
	 * @param isJustTimeLateAttendance ジャスト遅刻とする
	 * @param isJustEarlyLeave ジャスト早退とする
	 * @return 調整後の処理
	 */
	public TimeLeavingWork correctJustTime(boolean isJustTimeLateAttendance,boolean isJustEarlyLeave) {
		
		TimeActualStamp newAttendance = attendanceStamp
				.map(at -> isJustTimeLateAttendance ? at.moveAheadStampTime(1) : at)
				.orElse(null);
		
		TimeActualStamp newLeave = leaveStamp
				.map(le -> isJustEarlyLeave ? le.moveBackStampTime(1) : le)
				.orElse(null);
		
		return new TimeLeavingWork(this.workNo, newAttendance , newLeave);
	}

	public void setTimeLeavingWork(WorkNo workNo, Optional<TimeActualStamp> attendanceStamp, Optional<TimeActualStamp> leaveStamp){
		this.workNo = workNo;
		this.attendanceStamp = attendanceStamp;
		this.leaveStamp = leaveStamp;
	}

	/**
	 * 打刻漏れをしているかチェックする
	 * @return 打刻漏れをしていない 
	 */
	public boolean checkLeakageStamp() {
		/** 出勤時刻が無い */
		if(!this.getAttendanceStamp().isPresent()) {
			return false;
		}
		/** 退勤時刻が無い */
		if(!this.getLeaveStamp().isPresent()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 打刻順序不正であるかチェックする
	 * @return 順序不正である
	 */
	public boolean isReverseOrder() {
		if(this.getTimespan().getStart().greaterThan(this.getTimespan().getEnd())) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param workNo 勤務NO
	 * @param attendanceStamp 出勤
	 * @param leaveStamp 退勤
	 * @param canceledLate 遅刻を取り消した
	 * @param canceledEarlyLeave 早退を取り消した
	 */
	public TimeLeavingWork(WorkNo workNo, Optional<TimeActualStamp> attendanceStamp,
			Optional<TimeActualStamp> leaveStamp, boolean canceledLate, boolean canceledEarlyLeave) {
		super();
		this.workNo = workNo;
		this.attendanceStamp = attendanceStamp;
		this.leaveStamp = leaveStamp;
		this.canceledLate = canceledLate;
		CanceledEarlyLeave = canceledEarlyLeave;
	}
	
	/**
	 * 打刻（出勤）を取得する
	 * @return 出勤
	 */
	public Optional<WorkStamp> getStampOfAttendance() {
		
		return attendanceStamp.flatMap(c -> c.getStamp());
	}
	
	/**
	 * 打刻（退勤）を取得する
	 * @return 退勤
	 */
	public Optional<WorkStamp> getStampOfLeave() {
		
		return leaveStamp.flatMap(c -> c.getStamp());
	}
	
	/**
	 * 出勤時刻（丸め無し）を取得する
	 * @return 出勤時刻（丸め無し）
	 */
	public Optional<TimeWithDayAttr> getAttendanceTime() {
		return getStampOfAttendance().flatMap(c -> c.getTimeDay().getTimeWithDay());
	}
	
	/**
	 * 退勤時刻（丸め無し）を取得する
	 * @return 退勤時刻（丸め無し）
	 */
	public Optional<TimeWithDayAttr> getLeaveTime() {
		Optional<TimeWithDayAttr> result = getStampOfLeave().flatMap(c -> c.getTimeDay().getTimeWithDay());
		return getStampOfLeave().flatMap(c -> c.getTimeDay().getTimeWithDay());
	}
	
	public boolean existsTimeWithDay() {
		return this.getAttendanceTime().isPresent() || this.getLeaveTime().isPresent();
	}
	
	//NOとデフォルトを作成する
	public static TimeLeavingWork createDefaultWithNo(int no, TimeChangeMeans reason) {
		return new TimeLeavingWork(new WorkNo(no),
				TimeActualStamp.createDefaultWithReason(reason), //
				TimeActualStamp.createDefaultWithReason(reason));
	}
	
	/**
	 * 出勤時刻を予定開始時刻にする
	 * @param scheduleStartTime 予定開始時刻
	 * @param flowCalculateSet 流動計算設定
	 */
	public void setScheduleStartTimeForFlow(TimeWithDayAttr scheduleStartTime, FlowCalculateSet flowCalculateSet) {
		//出勤時刻
		Optional<TimeWithDayAttr> myStartTime = this.getAttendanceTime();
		if(!myStartTime.isPresent()) {
			return;
		}
		if(!flowCalculateSet.isCalcFromScheduleStartTime(myStartTime.get(), scheduleStartTime)) {
			return;
		}
		//出勤時刻←予定開始時刻
		this.getStampOfAttendance().get().getTimeDay().setTimeWithDay(Optional.of(scheduleStartTime));
	}
}
