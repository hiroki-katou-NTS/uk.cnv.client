package nts.uk.ctx.at.request.dom.adapter.workrecod.actuallock.dto;

import lombok.AllArgsConstructor;
/**
 * ロック中無視フラグ
 * @author tutk
 *
 */
@AllArgsConstructor
public enum IgnoreFlagDuringLockImport {
	/* ロック中の計算/集計する (true)*/
	CAN_CAL_LOCK(0, "ロック中の計算/集計する"),
	/* ロック中の計算/集計しない( false)*/
	CANNOT_CAL_LOCK(1, "ロック中の計算/集計しない");
	
	public final int value;
	
	/** The name id. */
	public final String nameId;
}
