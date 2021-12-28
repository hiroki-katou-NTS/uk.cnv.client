package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen;

import java.util.Optional;

import lombok.Getter;
import nts.arc.layer.dom.objecttype.DomainObject;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.TimeSpanForDailyCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

@Getter
/** 時間帯別勤怠の時間帯 */
public class TimeSheetOfAttendanceEachOuenSheet implements DomainObject {

	/** 勤務枠No: 勤務NO */
	private WorkNo workNo;
	
	/** 開始: 勤務時刻情報 */
	private Optional<WorkTimeInformation> start;
	
	/** 終了: 勤務時刻情報 */
	private Optional<WorkTimeInformation> end;

	private TimeSheetOfAttendanceEachOuenSheet(WorkNo workNo, Optional<WorkTimeInformation> start,
			Optional<WorkTimeInformation> end) {
		super();
		this.workNo = workNo;
		this.start = start;
		this.end = end;
	}
	
	public static TimeSheetOfAttendanceEachOuenSheet create(WorkNo workNo, 
			Optional<WorkTimeInformation> start, Optional<WorkTimeInformation> end) {
		
		return new TimeSheetOfAttendanceEachOuenSheet(workNo, start, end);
	}

	public void setStart(WorkTimeInformation start) {
		this.start = Optional.ofNullable(start);
	}

	public void setEnd(WorkTimeInformation end) {
		this.end = Optional.ofNullable(end);
	}

	public void setWorkNo(WorkNo workNo) {
		this.workNo = workNo;
	}
	
	/**
	 * 開始終了を取得する
	 * @return 開始～終了
	 */
	public Optional<TimeSpanForDailyCalc> getStartAndEnd() {
		if(!this.getStartTimeWithDayAttr().isPresent())
			return Optional.empty();
		
		if(!this.getEndTimeWithDayAttr().isPresent())
			return Optional.empty();
		
		return Optional.of(new TimeSpanForDailyCalc(this.start.get().getTimeWithDay().get(), this.end.get().getTimeWithDay().get()));
	}
	
	/**
	 * 開始時刻を取得する
	 * @return 開始時刻
	 */
	public Optional<TimeWithDayAttr> getStartTimeWithDayAttr() {
		if(!this.start.isPresent())
			return Optional.empty();
		
		return this.start.get().getTimeWithDay();
	}
	
	/**
	 * 終了時刻を取得する
	 * @return 終了時刻
	 */
	public Optional<TimeWithDayAttr> getEndTimeWithDayAttr() {
		if(!this.end.isPresent())
			return Optional.empty();
		
		return this.end.get().getTimeWithDay();
	}
}
