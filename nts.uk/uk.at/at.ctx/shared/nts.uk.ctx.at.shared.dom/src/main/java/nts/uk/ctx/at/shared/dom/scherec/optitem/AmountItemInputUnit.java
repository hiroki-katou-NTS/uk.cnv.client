package nts.uk.ctx.at.shared.dom.scherec.optitem;

import lombok.AllArgsConstructor;

/**
 * 金額項目の入力単位
 */
@AllArgsConstructor
public enum AmountItemInputUnit {
    // 0:1
    ONE(0,"1"),

    // 1:10
    TEN(1,"10"),

    // 2:100
    ONE_HUNDRED(2,"100"),

    // 3:1000
    ONE_THOUSAND(3,"1000"),

    // 4:10000
    TEN_THOUSAND(4,"10000");

    public int value;
    
    public String nameId;
    
    private final static AmountItemInputUnit[] values = AmountItemInputUnit.values();
    
    public static AmountItemInputUnit valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}

		// Find value.
		for (AmountItemInputUnit val : AmountItemInputUnit.values) {
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
		return Math.pow(10,this.value);
	}
}
