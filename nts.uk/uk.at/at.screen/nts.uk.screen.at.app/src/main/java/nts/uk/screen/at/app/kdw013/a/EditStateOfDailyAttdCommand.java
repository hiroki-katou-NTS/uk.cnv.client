package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

@AllArgsConstructor
@Getter
public class EditStateOfDailyAttdCommand {
	/** 勤怠項目ID: 勤怠項目ID */
	private int attendanceItemId;

	/** 編集状態: 日別実績の編集状態 */
	private int editStateSetting;

	public EditStateOfDailyAttd toDomain() {
		return new EditStateOfDailyAttd(attendanceItemId,
				EnumAdaptor.valueOf(editStateSetting, EditStateSetting.class));
	}
}
