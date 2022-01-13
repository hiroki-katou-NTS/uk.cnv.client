package nts.uk.ctx.sys.shared.dom.employee;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SecondSituation {
	// なし
	NONE(0),
	// 受入中
	ACCEPTING(1),
	// 出向中
	SECONDED(2);

	public final int value;
	private final static SecondSituation[] values = SecondSituation.values();
	
	public static SecondSituation valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (SecondSituation val : SecondSituation.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
}
