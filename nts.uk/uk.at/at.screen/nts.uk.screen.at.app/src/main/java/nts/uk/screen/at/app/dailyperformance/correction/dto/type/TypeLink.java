package nts.uk.screen.at.app.dailyperformance.correction.dto.type;

public enum TypeLink {

	DUTY(1, "勤務種類"),

	WORK_HOURS(2, "就業時間帯"),

	SERVICE_PLACE(3, "勤務場所"),

	REASON(4, "乖離理由"),

	WORKPLACE(5, "職場"),

	CLASSIFICATION(6, "分類"),

	POSSITION(7, "職位"),

	EMPLOYMENT(8, "雇用区分"),

	DOWORK(9, "するしない区分"),

	CALC(10, "時間外の自動計算区分"),

	REASON_GO_OUT(11, "外出理由"),

	REMARKS(12,"備考"),

	TIME_LIMIT(13,"時間外の上限設定理由"),
	
	BUSINESS_TYPE(14,"勤務種別"),
	
	WORK(15, "作業"),
	
	ADDITION(16, "加給"),
	
	WORK_SUP_OPTION(19,"作業補足選択肢");

	/** The value. */
	public int value;

	/** The name id. */
	public String nameId;

	/** The Constant values. */
	private final static TypeLink[] values = TypeLink.values();

	/**
	 * Instantiates a new TypeLink
	 *
	 * @param value
	 *            the value
	 * @param nameId
	 *            the name id
	 */
	private TypeLink(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	/**
	 * Value of.
	 *
	 * @param value
	 * @return the TypeLink
	 */
	public static TypeLink valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}
		// Find value.
		for (TypeLink val : TypeLink.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
}
