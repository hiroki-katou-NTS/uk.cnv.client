package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * PrimitiveValue:備考
 */
@StringMaxLength(1000)
public class Remarks extends StringPrimitiveValue<Remarks> {
    private static final long serialVersionUID = 1L;
    public Remarks(String rawValue) {
        super(rawValue);
    }
}
