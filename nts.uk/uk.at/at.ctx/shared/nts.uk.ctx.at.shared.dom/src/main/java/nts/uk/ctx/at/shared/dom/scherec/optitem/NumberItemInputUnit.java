package nts.uk.ctx.at.shared.dom.scherec.optitem;

import lombok.AllArgsConstructor;

/**
 * 回数項目の入力単位
 */
@AllArgsConstructor
public enum NumberItemInputUnit {
	// 0:0.01回
	ONE_HUNDREDTH(0,"0.01回"),

	// 1:0.1回
	ONE_TENTH(1,"0.1回"),

	// 2:0.5回
	ONE_HALF(2,"0.5回"),

	// 3:1回
	ONE(3,"1回");

	public int value;
	
	public String nameId;

	private final static NumberItemInputUnit[] values = NumberItemInputUnit.values();

	public static NumberItemInputUnit valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (NumberItemInputUnit val : NumberItemInputUnit.values) {
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
		case ONE_HUNDREDTH:
			return 0.01;
		case ONE_TENTH:
			return 0.1;
		case ONE_HALF:
			return 0.5;
		default:
			return 1.0;
		}
	}
}
