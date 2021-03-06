package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.ctx.at.shared.dom.shortworktime.ChildCareAtr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author nampt
 * 短時間勤務時間帯
 *
 */
@Getter
@NoArgsConstructor
public class ShortWorkingTimeSheet {

	/** 短時間勤務枠NO: 短時間勤務枠NO*/
	private ShortWorkTimFrameNo shortWorkTimeFrameNo;
	
	/** 育児介護区分: 育児介護区分*/
	private ChildCareAtr childCareAttr;

	/** 開始: 時刻(日区分付き) */
	private TimeWithDayAttr startTime;
	
	/** 終了: 時刻(日区分付き) */
	private TimeWithDayAttr endTime;
	
//	/** 控除時間: 勤怠時間 */
//	private AttendanceTime deductionTime;
//	
//	/** 時間: 勤怠時間 */
//	private AttendanceTime shortTime;
	
	
	
	public void setShortWorkTimeFrameNo(ShortWorkTimFrameNo shortWorkTimeFrameNo) {
		this.shortWorkTimeFrameNo = shortWorkTimeFrameNo;
	}

	public void setChildCareAttr(ChildCareAtr childCareAttr) {
		this.childCareAttr = childCareAttr;
	}

	public void setStartTime(TimeWithDayAttr startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(TimeWithDayAttr endTime) {
		this.endTime = endTime;
	}

	public ShortWorkingTimeSheet(ShortWorkTimFrameNo shortWorkTimeFrameNo, ChildCareAtr childCareAttr,
			TimeWithDayAttr startTime, TimeWithDayAttr endTime) {
		super();
		this.shortWorkTimeFrameNo = shortWorkTimeFrameNo;
		this.childCareAttr = childCareAttr;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * 計算用時間帯に変換する
	 * @return
	 */
	public TimeSpanForCalc convertToTimeSpanForCalc() {
		return new TimeSpanForCalc(this.startTime, this.endTime);
	}
	
}