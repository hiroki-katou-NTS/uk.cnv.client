package nts.uk.ctx.at.record.dom.calculationsetting.enums;

import lombok.AllArgsConstructor;

/**
 * 
 * @author nampt
 * 休出切替区分
 *
 */
@AllArgsConstructor
public enum BreakSwitchClass {

	/* 勤務種類は「休日出勤」にする	 */
	WORKTYPE_IS_HOLIDAY_WORK(0),
	/* 勤務種類は「出勤」にする */
	WORKTYPE_IS_ATTENDANCE(1);
	
	public final int value;
}
