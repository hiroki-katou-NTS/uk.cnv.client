package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory;

/**
 * 
 * @author Doan Duy Hung
 * enum : 社員時間単価NO
 */

public enum UnitPrice {
	
	// 単価１ 
	Price_1(0),
		
	// 単価2
	Price_2(1), 
	
	// 単価3
	Price_3(2),
	
	// 単価4 
	Price_4(3),
	
	// 単価5
	Price_5(4), 
	
	// 単価6
	Price_6(5),
	
	// 単価7 
	Price_7(6),
	
	// 単価8
	Price_8(7), 
	
	// 単価9
	Price_9(8),
	
	// 単価10
	Price_10(9);
	
	public final int value;
	
	private final static UnitPrice[] values = UnitPrice.values();
	
	UnitPrice(int value){
		this.value = value;
	}
	
	public static UnitPrice valueOf(Integer value) {
		// Invalid object.
		if (value == null) {
			return null;
		}
		// Find value.
		for (UnitPrice val : UnitPrice.values) {
			if (val.value == value) {
				return val;
			}
		}
		// Not found.
		return null;
	}
}
