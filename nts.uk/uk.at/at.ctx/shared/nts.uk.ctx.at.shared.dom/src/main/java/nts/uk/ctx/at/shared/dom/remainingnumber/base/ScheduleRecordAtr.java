package nts.uk.ctx.at.shared.dom.remainingnumber.base;

import lombok.AllArgsConstructor;

/**
 * 予定実績区分
 * @author shuichu_ishida
 */
@AllArgsConstructor
public enum ScheduleRecordAtr {
	/** 未反映状態 */
	NOT_APPLICABLE(0),
	/** 実績 */
	RECORD(1),
	/** スケジュール */
	SCHEDULE(2);

	public final int value;
}
