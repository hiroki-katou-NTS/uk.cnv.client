package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

/**
 * 時刻変更区分
 * @author phongtq
 *
 */
public enum ChangeClockArt {
	/**
	 * "時刻変更区分
	 * 0:出勤 1:退勤 2:入門 3:退門 4:外出 5:戻り 6:応援開始 7:臨時出勤 8:応援終了 9:臨時退勤 10:PCログオン
	 * 11:PCログオフ 12:応援出勤 13:臨時+応援出勤
	 */
	
	/** 出勤 */
	GOING_TO_WORK(0,"出勤"),

	/** 退勤 */
	WORKING_OUT(1,"退勤"),

	/** 入門 */
	OVER_TIME(2,"入門"),

	/** 退門 */
	BRARK(3,"退門"),

	/** 外出 */
	GO_OUT(4,"外出"),
	
	/** 戻り */
	RETURN(5,"戻り"),
	
	/** 応援開始 */
	START_OF_SUPPORT(6,"応援開始 "),
	
	/** 臨時出勤 */
	TEMPORARY_WORK(7,"臨時出勤"),

	/** 応援終了 */
	END_OF_SUPPORT(8,"応援終了"),
	
	/** 臨時退勤 */
	TEMPORARY_LEAVING(9,"臨時退勤"),

	/** PCログオン */
	PC_LOG_ON(10,"PCログオン"),

	/** PCログオフ */
	PC_LOG_OFF(11,"PCログオフ");

	/** The value. */
	public int value;
	
	/** The value. */
	public String nameId;

	/** The Constant values. */
	private final static ChangeClockArt[] values = ChangeClockArt.values();

	/**
	 * Instantiates a new closure id.
	 *
	 * @param value
	 *            the value
	 * @param description
	 *            the description
	 */
	private ChangeClockArt(int value, String nameId) {
		this.value = value;
		this.nameId = nameId;
	}

	/**
	 * Value of.
	 *
	 * @param value
	 *            the value
	 * @return the use division
	 */
	public static ChangeClockArt valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (ChangeClockArt val : ChangeClockArt.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
	/**
	 * 	[1] 打刻後のエラー確認する必要があるか
	 * @return
	 */
	public boolean checkWorkingOut() {
		return this == ChangeClockArt.WORKING_OUT;
	}
}
