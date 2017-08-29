package nts.uk.ctx.bs.person.dom.person.info.numericitem;

import java.math.BigDecimal;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.bs.person.dom.person.info.singleitem.DataTypeState;
import nts.uk.ctx.bs.person.dom.person.info.singleitem.DataTypeValue;

@Getter
public class NumericItem extends DataTypeState {

	private NumericItemMinus numericItemMinus;
	private NumericItemAmount numericItemAmount;
	private IntegerPart integerPart;
	private DecimalPart decimalPart;
	private NumericItemMin NumericItemMin;
	private NumericItemMax NumericItemMax;

	private NumericItem(int numericItemMinus, int numericItemAmount, int integerPart, int decimalPart,
			BigDecimal numericItemMin, BigDecimal numericItemMax) {
		super();
		this.dataTypeValue = DataTypeValue.NUMERIC;
		this.numericItemMinus = EnumAdaptor.valueOf(numericItemMinus, NumericItemMinus.class);
		this.numericItemAmount = EnumAdaptor.valueOf(numericItemAmount, NumericItemAmount.class);
		this.integerPart = new IntegerPart(integerPart);
		this.decimalPart = new DecimalPart(decimalPart);
		this.NumericItemMin = numericItemMin != null ? new NumericItemMin(numericItemMin) : null;
		this.NumericItemMax = NumericItemMax != null ? new NumericItemMax(numericItemMax) : null;
	}

	public static NumericItem createFromJavaType(int numericItemMinus, int numericItemAmount, int integerPart,
			int decimalPart, BigDecimal numericItemMin, BigDecimal numericItemMax) {
		return new NumericItem(numericItemMinus, numericItemAmount, integerPart, decimalPart, numericItemMin,
				numericItemMax);
	}

}
