package nts.uk.ctx.at.shared.dom.workrule.shiftmaster;

import nts.arc.primitive.StringPrimitiveValue;
import nts.arc.primitive.constraint.StringMaxLength;

/**
 * @author anhnm
 * 取り込みシート名
 *
 */
@StringMaxLength(62)
public class CaptureSheetName extends StringPrimitiveValue<CaptureSheetName> {

    public CaptureSheetName(String rawValue) {
        super(rawValue);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

}
