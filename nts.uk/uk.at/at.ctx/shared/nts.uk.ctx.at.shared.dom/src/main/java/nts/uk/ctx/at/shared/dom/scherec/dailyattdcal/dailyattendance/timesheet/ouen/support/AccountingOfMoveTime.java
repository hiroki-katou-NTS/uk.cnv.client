package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.support;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;

/** 移動時間の計上先 */
@RequiredArgsConstructor
public enum AccountingOfMoveTime {
	/** 移動先 */
	DESTINATION(0),
	/** 移動元 */
	MOUVING_SOURCE(1),
	/** 計算しない */
	NOT_CALC(2);
	
	public final int value;
	
	public static AccountingOfMoveTime of(int value) {
		return EnumAdaptor.valueOf(value, AccountingOfMoveTime.class);
	}
}
