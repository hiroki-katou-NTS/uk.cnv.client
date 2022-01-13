package nts.uk.file.at.app.export.attendanceitemprepare;

import lombok.AllArgsConstructor;
/**
 * 
 * @author Hoidd
 *
 */
@AllArgsConstructor
	/** 残業申請（早出） */
public enum ApplicationTypeExport {
	/**	残業申請（早出） */
	OVERTIME_APPLICATION(0, "残業申請（早出"),
	/**	残業申請（通常） */
	OVERTIME_APPLICATION_NORMAL(1, "残業申請（通常）"),
	/**	残業申請（早出・通常） */
	OVERTIME_APPLICATION_EARLY_REGULAR(2, "残業申請（早出・通常）"),
	/**	休暇申請 */
	ABSENCE_APPLICATION(3, "休暇申請"),
	/**	勤務変更申請 */
	WORK_CHANGE_APPLICATION(4, "勤務変更申請"),
	/**	出張申請 */
	BUSINESS_TRIP_APPLICATION(5, "出張申請"),
	/**	直行直帰申請 */
	GO_RETURN_DIRECTLY_APPLICATION(6, "直行直帰申請"),
	/**	休出時間申請 */
	LEAVE_TIME_APPLICATION(7, "休出時間申請"),
	/**	打刻申請 */
	STAMP_APPLICATION(8, "打刻申請"),
	/**	時間休暇申請 */
	ANNUAL_HD_APPLICATION(10, "時間休暇申請"),
	/**	遅刻早退取消申請 */
	EARLY_LEAVE_CANCEL_APPLICATION(11, "遅刻早退取消申請"),
	/**	振休振出申請 */
	COMPLEMENT_LEAVE_APPLICATION(12, "振休振出申請"),
	/**	任意申請 */
	OPTIONAL_APPLICATION(13, "任意項目申請");
	
	public int value;

	public String nameId;

}
