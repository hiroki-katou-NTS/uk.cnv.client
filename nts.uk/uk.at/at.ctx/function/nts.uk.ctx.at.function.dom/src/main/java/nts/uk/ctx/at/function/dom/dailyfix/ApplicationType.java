package nts.uk.ctx.at.function.dom.dailyfix;

import lombok.AllArgsConstructor;
/**
 *    実績で使用する申請種類
 * @author yennth
 *
 */
@AllArgsConstructor
public enum ApplicationType {
	/**	残業申請（早出） */
	OVERTIME_APPLICATION(0),
	/**	残業申請（通常） */
	OVERTIME_APPLICATION_NORMAL(1),
	/**	残業申請（早出・通常） */
	OVERTIME_APPLICATION_EARLY_REGULAR(2),
	/**	休暇申請 */
	ABSENCE_APPLICATION(3),
	/**	勤務変更申請 */
	WORK_CHANGE_APPLICATION(4),
	/**	出張申請 */
	BUSINESS_TRIP_APPLICATION(5),
	/**	直行直帰申請 */
	GO_RETURN_DIRECTLY_APPLICATION(6),
	/**	休出時間申請 */
	LEAVE_TIME_APPLICATION(7),
	/**	打刻申請 */
	STAMP_APPLICATION(8),
	/**	時間休暇申請 */
	ANNUAL_HD_APPLICATION(10),
	/**	遅刻早退取消申請 */
	EARLY_LEAVE_CANCEL_APPLICATION(11),
	/**	振休振出申請 */
	COMPLEMENT_LEAVE_APPLICATION(12),
	/**	任意項目申請 */
	OPTIONAL_APPLICATION(13),
	/** 申請一覧 */
	APPLICATION_LIST(14);
	public final int value;
}
