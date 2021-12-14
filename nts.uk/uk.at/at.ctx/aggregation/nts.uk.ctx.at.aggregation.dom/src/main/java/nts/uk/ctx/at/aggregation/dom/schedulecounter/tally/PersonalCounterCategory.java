package nts.uk.ctx.at.aggregation.dom.schedulecounter.tally;

import nts.arc.enums.EnumAdaptor;

/**
 * 個人計カテゴリ
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.予実集計.スケジュール集計.職場計・個人計.個人計カテゴリ
 * @author dan_pv
 *
 */
public enum PersonalCounterCategory {

	/** 月間想定給与額   */
	MONTHLY_EXPECTED_SALARY(0, "月間想定給与額 "),

	/** 累計想定給与額  */
	CUMULATIVE_ESTIMATED_SALARY(1, "年間想定給与額"),

	/** 基準労働時間比較 */
	STANDARD_WORKING_HOURS_COMPARISON(2, "基準労働時間比較"),

	/** 労働時間 */
	WORKING_HOURS(3, "労働時間"),

	/** 夜勤時間 */
	NIGHT_SHIFT_HOURS(4, "夜勤時間"),

	/** 週間休日日数 */
	WEEKS_HOLIDAY_DAYS(5, "週間休日日数"),

	/** 出勤・休日日数 */
	ATTENDANCE_HOLIDAY_DAYS(6, "出勤・休日日数"),

	/** 回数集計１ */
	TIMES_COUNTING_1(7, "回数集計１"),

	/** 回数集計２ */
	TIMES_COUNTING_2(8, "回数集計２"),

	/** 回数集計３ */
	TIMES_COUNTING_3(9, "回数集計３");

	public int value;
	public String nameId;

	private PersonalCounterCategory(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	public static PersonalCounterCategory of(int value) {
		return EnumAdaptor.valueOf(value, PersonalCounterCategory.class);
	}

}
