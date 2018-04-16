package nts.uk.ctx.exio.dom.exi.condset;

import java.math.BigDecimal;

import nts.arc.primitive.DecimalPrimitiveValue;
import nts.arc.primitive.constraint.DecimalMaxValue;
import nts.arc.primitive.constraint.DecimalMinValue;

@DecimalMaxValue("9999999999.99")
@DecimalMinValue("-9999999999.99")
public class AcceptanceConditionValue extends DecimalPrimitiveValue<AcceptanceConditionValue>{

	public AcceptanceConditionValue(BigDecimal rawValue) {
		super(rawValue);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}
