package nts.uk.screen.at.app.ksu001.displayinworkinformation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;

/**
 * @author laitv
 *
 */
@Setter
@Getter
@NoArgsConstructor
public class EditStateOfDailyAttdDto {

	/** 勤怠項目ID: 勤怠項目ID */
	private int attendanceItemId;

	/** 編集状態: 日別実績の編集状態 */
	// HAND_CORRECTION_MYSELF(0), 手修正（本人）
	// HAND_CORRECTION_OTHER(1), 手修正（他人）
	// REFLECT_APPLICATION(2), 申請反映
	// IMPRINT(3); 打刻反映
	private int editStateSetting;

	public EditStateOfDailyAttdDto(int attendanceItemId, int editStateSetting) {
		super();
		this.attendanceItemId = attendanceItemId;
		this.editStateSetting = editStateSetting;
	}

	public static EditStateOfDailyAttdDto fromDomain(EditStateOfDailyAttd domain) {

		return new EditStateOfDailyAttdDto(domain.getAttendanceItemId(), domain.getEditStateSetting().value);
	}
}
