package nts.uk.ctx.at.shared.dom.scherec.optitem;

import lombok.AllArgsConstructor;

/**
 * 時間項目の入力単位
 */
@AllArgsConstructor
public enum TimeItemInputUnit {
    //  0:1分
    ONE_MINUTE(0,"1分"),

    //  1:5分
    FIVE_MINUTES(1,"5分"),

    //  2:10分
    TEN_MINUTES(2,"10分"),

    //  3:15分
    FIFTEEN_MINUTES(3,"15分"),

    //  4:30分
    THIRTY_MINUTES(4,"30分"),

    //  4:60分
    SIXTY_MINUTES(5,"60分");

    public int value;
    
    public String nameId;
    
    private final static TimeItemInputUnit[] values = TimeItemInputUnit.values();

	public static TimeItemInputUnit valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (TimeItemInputUnit val : TimeItemInputUnit.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}

	/**
	 * Enum値を返す
	 */
	public double valueEnum() {
		switch (this) {
		case ONE_MINUTE:
			return 1;
		case FIVE_MINUTES:
			return 5;
		case TEN_MINUTES:
			return 10;
		case FIFTEEN_MINUTES:
			return 15;
		case THIRTY_MINUTES:
			return 30;
		default:
			return 60;
		}
	}
}
