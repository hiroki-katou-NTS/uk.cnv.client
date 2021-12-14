package nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber;

import nts.arc.primitive.HalfIntegerPrimitiveValue;
import nts.arc.primitive.constraint.HalfIntegerRange;

/**
 * 月別休暇残日数  
 * @author masaaki_jinno
 *
 */
@HalfIntegerRange(min = -999.5 , max = 999.5)
public class LeaveRemainingDayNumber extends HalfIntegerPrimitiveValue<LeaveRemainingDayNumber>{

	//protected static final long serialVersionUID = 8578961613409044770L;

	private static final long serialVersionUID = -5514142904958956571L;

	public LeaveRemainingDayNumber(Double rawValue) {
		super(rawValue);
	}

	@Override
	protected Double reviseRawValue(Double rawValue) {
		if (rawValue == null) return super.reviseRawValue(rawValue);
		if (rawValue > 999.5) rawValue = 999.5;
		if (rawValue < -999.5) rawValue = -999.5;
		return super.reviseRawValue(rawValue);
	}

	/**
	 * 休暇残数（日数）を加算
	 * @param aLeaveRemainingDayNumber
	 */
	public LeaveRemainingDayNumber add(LeaveRemainingDayNumber aLeaveRemainingDayNumber){
		
		// 日付加算
		return new LeaveRemainingDayNumber( v() + aLeaveRemainingDayNumber.v() );
	}
	
}