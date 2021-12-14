package nts.uk.ctx.at.shared.dom.specialholiday;

import java.util.ArrayList;
import java.util.List;

import nts.arc.primitive.IntegerPrimitiveValue;
import nts.arc.primitive.PrimitiveValue;
import nts.arc.primitive.constraint.IntegerRange;

/** 特別休暇コード */
@IntegerRange(min = 1, max = 20)
public class SpecialHolidayCode extends IntegerPrimitiveValue<PrimitiveValue<Integer>> {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	public SpecialHolidayCode(int rawValue) {
		super(rawValue);
	}

	public static List<SpecialHolidayCode> getCodeList() {
		List<SpecialHolidayCode> codes = new ArrayList<>();
		int counter = 1;
		while (true) {
			try {
				SpecialHolidayCode code = new SpecialHolidayCode(counter);
				code.validate();
				codes.add(code);
			} catch (Exception e) {
				break;
			}
			counter += 1;
			if (counter > Integer.MAX_VALUE)
				break;
		}
		return codes;
	}
}
