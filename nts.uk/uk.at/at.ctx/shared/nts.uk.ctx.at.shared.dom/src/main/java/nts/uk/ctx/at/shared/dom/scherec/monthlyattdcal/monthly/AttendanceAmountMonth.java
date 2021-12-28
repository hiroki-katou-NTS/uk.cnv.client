package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly;

import nts.arc.primitive.LongPrimitiveValue;
import nts.arc.primitive.constraint.LongRange;

/**
 * 勤怠月間金額
 * @author shuichu_ishida
 */
@LongRange(min = -99999999999L, max = 99999999999L)
public class AttendanceAmountMonth extends LongPrimitiveValue<AttendanceAmountMonth> {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * コンストラクタ
	 * @param amount 金額
	 */
	public AttendanceAmountMonth(long amount){
		
		super(amount);
	}
	
	/**
	 * 金額を加算する
	 * @param amount 金額
	 * @return 加算後の勤怠月間金額
	 */
	public AttendanceAmountMonth addAmount(long amount){
	
		return new AttendanceAmountMonth(this.v() + amount);
	}
}
