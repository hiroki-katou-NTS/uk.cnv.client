package nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums;

import lombok.AllArgsConstructor;

// 日次勤怠項目の属性

@AllArgsConstructor
public enum DailyAttendanceAtr {

	/* コード */
	Code(0),
	/* マスタを参照する */
	ReferToMaster(1),
	/* 回数*/
	NumberOfTime(2),
	/* 金額*/
	AmountOfMoney(3),
	/* 区分 */
	Classification(4),
	/* 時間 */
	Time(5),
	/* 時刻*/
	TimeOfDay(6),
	/* 文字 */
	Charater(7),

	/*申請*/
	Application(8),
	
	/*数値*/
	NumbericValue(9);
	
	public final int value;
	
}
