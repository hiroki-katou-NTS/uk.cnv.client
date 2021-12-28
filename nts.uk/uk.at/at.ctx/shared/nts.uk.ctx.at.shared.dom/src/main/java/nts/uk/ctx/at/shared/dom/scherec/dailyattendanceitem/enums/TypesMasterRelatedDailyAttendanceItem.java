package nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums;

/**
 * 日次の勤怠項目に関連するマスタの種類
 */
public enum TypesMasterRelatedDailyAttendanceItem {
	WORK_TYPE(1, "勤務種類"),
	WORKING_HOURS(2, "就業時間帯"),
	WORK_LOCATION(3, "勤務場所"),
	REASON_DIVERGENCE(4, "乖離理由"),
	WORK_PLACE(5, "職場"),
	CLASSIFICATION(6, "分類"),
	POSITION(7, "職位"),
	EMPLOYMENT(8, "雇用"),
	NOT_USE_ATR(9, "するしない区分"),
	REASON_GOING_OUT(11, "外出理由"),
	AUTO_CALC_CTG_OUTSIDE(10, "時間外の自動計算区分"),
	REMARKS(12, "備考"),
	TIME_LIMIT_UPPER_SET(13, "時間外の上限設定"),
	BUSINESS_TYPE(14,"勤務種別"),
	SUPPORT_WORK(15,"作業"),
	BONUS_PAY(16,"加給"),
	WORK_SUPPLEMENT_OPT(19,"作業補足選択肢");

	public final int value;
	public final String name;

	private TypesMasterRelatedDailyAttendanceItem(int value, String name) {
		this.value = value;
		this.name = name;
	}
}
