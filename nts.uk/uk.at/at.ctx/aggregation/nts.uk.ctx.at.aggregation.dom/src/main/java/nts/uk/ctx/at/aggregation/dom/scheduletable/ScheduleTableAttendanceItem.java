package nts.uk.ctx.at.aggregation.dom.scheduletable;

/**
 * スケジュール表の勤怠項目
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.就業機能.スケジュール表.スケジュール表の勤怠項目
 * @author dan_pv
 *
 */
public enum ScheduleTableAttendanceItem {

	SHIFT(0, "シフト"), // シフト
	WORK_TYPE(1, "勤務種類"), // 勤務種類
	WORK_TIME(2, "就業時間帯"), // 就業時間帯
	START_TIME(3, "開始時刻"), // 開始時刻
	END_TIME(4, "終了時刻"), // 終了時刻
	START_TIME_2(5, "開始時刻２"), // 開始時刻２
	END_TIME_2(6, "終了時刻２"), // 終了時刻２
	TOTAL_WORKING_HOURS(7, "総労働時間"), // 総労働時間
	WORKING_HOURS(8, "就業時間"), // 就業時間
	ACTUAL_WORKING_HOURS(9, "実働時間"), // 実働時間
	DAY_WORKING_HOURS(10, "日勤勤務時間"), // 日勤勤務時間
	NIGHT_WORKING_HOURS(11, "夜勤勤務時間"), // 夜勤勤務時間
	DAY_TRANSFER_HOURS(12, "日勤申送時間"), // 日勤申送時間
	NIGHT_TRANSFER_HOURS(13, "夜勤申送時間"), // 夜勤申送時間
	LABOR_COST_TIME_1(14, "人件費時間1"), // 人件費時間1
	LABOR_COST_TIME_2(15, "人件費時間2"), // 人件費時間2
	LABOR_COST_TIME_3(16, "人件費時間3"), // 人件費時間3
	LABOR_COST_TIME_4(17, "人件費時間4"), // 人件費時間4
	LABOR_COST_TIME_5(18, "人件費時間5"), // 人件費時間5
	LABOR_COST_TIME_6(19, "人件費時間6"), // 人件費時間6
	LABOR_COST_TIME_7(20, "人件費時間7"), // 人件費時間7
	LABOR_COST_TIME_8(21, "人件費時間8"), // 人件費時間8
	LABOR_COST_TIME_9(22, "人件費時間9"), // 人件費時間9
	LABOR_COST_TIME_10(23, "人件費時間10"); // 人件費時間10

	public int value;
	public String nameId;
	
	private ScheduleTableAttendanceItem(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

}
