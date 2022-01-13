package nts.uk.ctx.at.request.dom.application.overtime;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.申請承認.申請.残業休出申請.残業申請
 * 残業回数
 */
@IntegerRange(min = 1, max = 10)
public class OvertimeNumber extends IntegerPrimitiveValue<OvertimeNumber> {
    private static final long serialVersionUID = 1L;

    public OvertimeNumber(int rawValue) {
        super(rawValue);
    }
}
