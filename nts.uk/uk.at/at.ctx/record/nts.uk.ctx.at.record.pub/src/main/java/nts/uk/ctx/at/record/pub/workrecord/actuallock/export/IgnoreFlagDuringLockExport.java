package nts.uk.ctx.at.record.pub.workrecord.actuallock.export;

import lombok.AllArgsConstructor;
/**
 * ロック中無視フラグ
 * @author tutk
 *
 */
@AllArgsConstructor
public enum IgnoreFlagDuringLockExport {
	/* ロック中の計算/集計する (true)*/
	CAN_CAL_LOCK(0, "ロック中の計算/集計する"),
	/* ロック中の計算/集計しない( false)*/
	CANNOT_CAL_LOCK(1, "ロック中の計算/集計しない");
	
	public final int value;
	
	/** The name id. */
	public final String nameId;
}
