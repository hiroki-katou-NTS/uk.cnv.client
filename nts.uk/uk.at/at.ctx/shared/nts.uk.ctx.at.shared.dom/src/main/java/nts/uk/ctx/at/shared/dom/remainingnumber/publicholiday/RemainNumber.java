package nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday;

import java.math.BigDecimal;

import nts.arc.primitive.DecimalPrimitiveValue;
import nts.arc.primitive.constraint.DecimalMaxValue;
import nts.arc.primitive.constraint.DecimalMinValue;

@DecimalMinValue("-999.5")
@DecimalMaxValue("999.5")
public class RemainNumber extends DecimalPrimitiveValue<RemainNumber>{

	private static final long serialVersionUID = 1L;

	public RemainNumber(BigDecimal rawValue) {
		super(rawValue);
	}

}
