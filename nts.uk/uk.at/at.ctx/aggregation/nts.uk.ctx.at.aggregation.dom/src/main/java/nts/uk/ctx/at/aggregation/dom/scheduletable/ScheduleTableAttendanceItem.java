package nts.uk.ctx.at.aggregation.dom.scheduletable;

import java.util.HashMap;
import java.util.Map;

/**
 * スケジュール表の勤怠項目
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール表.スケジュール表の勤怠項目
 * @author dan_pv
 *
 */
public enum ScheduleTableAttendanceItem {
	/** シフト */
	SHIFT(0, "Enum_ScheTable_Shift"),
	
	/** 勤務種類 */
	WORK_TYPE(1, "Enum_ScheTable_WorkType"),
	
	/** 就業時間帯 */
	WORK_TIME(2, "Enum_ScheTable_WorkTime"),
	
	/** 開始時刻 */
	START_TIME(3, "Enum_ScheTable_StartTime"),
	
	/** 終了時刻*/
	END_TIME(4, "Enum_ScheTable_EndTime"),
	
	/** 開始時刻２ */
	START_TIME_2(5, "Enum_ScheTable_StartTime2"),
	
	/** 終了時刻２ */
	END_TIME_2(6, "Enum_ScheTable_EndTime2"),
	
	/** 総労働時間 */
	TOTAL_WORKING_HOURS(7, "Enum_ScheTable_TotalWorkingHours"),
	
	/** 就業時間 */
	WORKING_HOURS(8, "Enum_ScheTable_WorkingHours"),
	
	/** 実働時間 */
	ACTUAL_WORKING_HOURS(9, "Enum_ScheTable_ActualWorkingHours"),
	
	/** 日勤勤務時間 */
	DAY_WORKING_HOURS(10, "Enum_ScheTable_DayWorkingHours"),
	
	/** 夜勤勤務時間 */
	NIGHT_WORKING_HOURS(11, "Enum_ScheTable_NightWorkingHours"),
	
	/** 日勤申送時間 */
	DAY_TRANSFER_HOURS(12, "Enum_ScheTable_DayTranserHours"),
	
	/** 夜勤申送時間 */
	NIGHT_TRANSFER_HOURS(13, "Enum_ScheTable_NightTransferHours"),
	
	/** 人件費時間1 */
	LABOR_COST_TIME_1(14, "Enum_ScheTable_LaborCostTime1"),
	
	/** 人件費時間2 */
	LABOR_COST_TIME_2(15, "Enum_ScheTable_LaborCostTime2"),
	
	/** 人件費時間3 */
	LABOR_COST_TIME_3(16, "Enum_ScheTable_LaborCostTime3"),
	
	/** 人件費時間4 */
	LABOR_COST_TIME_4(17, "Enum_ScheTable_LaborCostTime4"),
	
	/** 人件費時間5 */
	LABOR_COST_TIME_5(18, "Enum_ScheTable_LaborCostTime5"),
	
	/** 人件費時間6 */
	LABOR_COST_TIME_6(19, "Enum_ScheTable_LaborCostTime6"),
	
	/** 人件費時間7 */
	LABOR_COST_TIME_7(20, "Enum_ScheTable_LaborCostTime7"),
	
	/** 人件費時間8 */
	LABOR_COST_TIME_8(21, "Enum_ScheTable_LaborCostTime8"),
	
	/** 人件費時間9*/
	LABOR_COST_TIME_9(22, "Enum_ScheTable_LaborCostTime9"),
	
	/** 人件費時間10 */
	LABOR_COST_TIME_10(23, "Enum_ScheTable_LaborCostTime10");

	public int value;
	public String nameId;
	
	private ScheduleTableAttendanceItem(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}
	
	/**
	 * 人件費時間種類を取得する
	 * @return Map<スケジュール表の勤怠項目, Integer>
	 */
	@SuppressWarnings("serial")
	public static Map<ScheduleTableAttendanceItem, Integer> getLaborCostTimeTypes() {
		
		return new HashMap<ScheduleTableAttendanceItem, Integer>() {{
			put(LABOR_COST_TIME_1, 1);
			put(LABOR_COST_TIME_2, 2);
			put(LABOR_COST_TIME_3, 3);
			put(LABOR_COST_TIME_4, 4);
			put(LABOR_COST_TIME_5, 5);
			put(LABOR_COST_TIME_6, 6);
			put(LABOR_COST_TIME_7, 7);
			put(LABOR_COST_TIME_8, 8);
			put(LABOR_COST_TIME_9, 9);
			put(LABOR_COST_TIME_10, 10);
		}};
	}

}