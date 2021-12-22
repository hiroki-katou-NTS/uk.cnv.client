/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktime.flexset;

//import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.usecls.ApplyAtr;
//import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeDomainObject;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.ScreenMode;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * The Class CoreTimeSetting.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
// コアタイム時間帯設定
public class CoreTimeSetting extends WorkTimeDomainObject implements Cloneable{

	/** The core time sheet. */
	// コアタイム時間帯
	private TimeSheet coreTimeSheet;

	/** The timesheet. */
	// 使用区分
	private ApplyAtr timesheet;

	/** The min work time. */
	// 最低勤務時間
	private AttendanceTime minWorkTime;

	/** The Outing Calculation within Core Time. */
	//外出計算
	private OutingCalcWithinCoreTime goOutCalc;

	/** The Constant ZERO_MINUTES. */
	// 00:00
	public static final int ZERO_MINUTES = 0;

	/**
	 * Instantiates a new core time setting.
	 *
	 * @param memento
	 *            the memento
	 */
	public CoreTimeSetting(CoreTimeSettingGetMemento memento) {
		this.coreTimeSheet = memento.getCoreTimeSheet();
		this.timesheet = memento.getTimesheet();
		this.minWorkTime = memento.getMinWorkTime();
		this.goOutCalc = memento.getGoOutCalc();
	}

	/**
	 * Save to memento.
	 *
	 * @param memento
	 *            the memento
	 */
	public void saveToMemento(CoreTimeSettingSetMemento memento) {
		memento.setCoreTimeSheet(this.coreTimeSheet);
		memento.setTimesheet(this.timesheet);
		memento.setMinWorkTime(this.minWorkTime);
		memento.setGoOutCalc(this.goOutCalc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.arc.layer.dom.DomainObject#validate()
	 */
	@Override
	public void validate() {
		// コアタイム時間帯.開始時刻 >= コアタイム時間帯.終了時刻 => Msg_770
		if (this.isUseTimeSheet()
				&& this.coreTimeSheet.getStartTime().greaterThan(this.coreTimeSheet.getEndTime())) {
			this.bundledBusinessExceptions.addMessage("Msg_770", "KMK003_157");
		}

		// 使用区分 = 使用しない AND 最低勤務時間 <= 0
		if (!this.isUseTimeSheet() && (this.minWorkTime == null || this.minWorkTime.valueAsMinutes() <= ZERO_MINUTES)) {
			this.bundledBusinessExceptions.addMessage("Msg_776");
		}

		super.validate();
	}

	/**
	 * Checks if is use time sheet.
	 *
	 * @return true, if is use time sheet
	 */
	public boolean isUseTimeSheet() {
		return this.timesheet == ApplyAtr.USE;
	}

	/**
	 * Correct data.
	 *
	 * @param screenMode
	 *            the screen mode
	 * @param oldDomain
	 *            the old domain
	 */
	public void correctData(ScreenMode screenMode, CoreTimeSetting oldDomain) {
		if (ApplyAtr.NOT_USE.equals(this.timesheet)) {
			this.coreTimeSheet = oldDomain.getCoreTimeSheet();
		} else {
			this.minWorkTime = oldDomain.getMinWorkTime();
		}
	}

	/**
	 * Correct default data.
	 *
	 * @param screenMode
	 *            the screen mode
	 */
	public void correctDefaultData(ScreenMode screenMode) {
		if (ApplyAtr.NOT_USE.equals(this.timesheet)) {
			this.coreTimeSheet = TimeSheet.createDefault();
		} else {
			this.minWorkTime = new AttendanceTime(ZERO_MINUTES);
		}
	}
	
	
	public TimeSheet getDecisionCoreTimeSheet(AttendanceHolidayAttr attr,TimeWithDayAttr AMEndTime,TimeWithDayAttr PMStartTime) {
		switch (attr) {
		case MORNING:
			return new TimeSheet(this.coreTimeSheet.getStartTime(),AMEndTime);
		case AFTERNOON:
			return new TimeSheet(PMStartTime,this.coreTimeSheet.getEndTime());
		case FULL_TIME:
		case HOLIDAY:
			return new TimeSheet(this.coreTimeSheet.getStartTime(),this.coreTimeSheet.getEndTime());
		default:
			throw new RuntimeException("unknown attr:" + attr);
		}
	}
	
	/**
	 * 最低勤務時間を0：00に変更する
	 * @return
	 */
	public CoreTimeSetting changeZeroMinWorkTime() {
		return new CoreTimeSetting(this.coreTimeSheet,this.timesheet,new AttendanceTime(0), this.goOutCalc);
	}
	
	@Override
	public CoreTimeSetting clone() {
		CoreTimeSetting cloned = new CoreTimeSetting();
		try {
			cloned.coreTimeSheet = this.coreTimeSheet.clone();
			cloned.timesheet = ApplyAtr.valueOf(this.timesheet.value);
			cloned.minWorkTime = new AttendanceTime(this.minWorkTime.v());
			cloned.goOutCalc = new OutingCalcWithinCoreTime(this.goOutCalc.getRemoveFromWorkTime(), this.goOutCalc.getEspecialCalc());
		}
		catch (Exception e){
			throw new RuntimeException("CoreTimeSetting clone error.");
		}
		return cloned;
	}
}
