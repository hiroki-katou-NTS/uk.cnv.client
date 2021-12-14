package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.layer.dom.objecttype.DomainObject;

/**
 * 日別勤怠の編集状態
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).日の勤怠計算.日別勤怠.編集状態.日別勤怠の編集状態
 * @author tutk
 *
 */
@Getter
@NoArgsConstructor
public class EditStateOfDailyAttd implements DomainObject {
	
	/** 勤怠項目ID: 勤怠項目ID */
	private int attendanceItemId;
	
	/** 編集状態: 日別実績の編集状態 */
	@Setter
	private EditStateSetting editStateSetting;

	public EditStateOfDailyAttd(int attendanceItemId, EditStateSetting editStateSetting) {
		super();
		this.attendanceItemId = attendanceItemId;
		this.editStateSetting = editStateSetting;
	}
	
	/**
	 * 	[C-1] 手修正で作る
	 * @param require
	 * @param attendanceItemId 	勤怠項目ID
	 * @param targetEmployeeId 	対象社員ID
	 * @return
	 */
	public static EditStateOfDailyAttd createByHandCorrection(
			Require require, 
			int attendanceItemId, 
			String targetEmployeeId) {
		
		String loginEmployeeId = require.getLoginEmployeeId();
		EditStateSetting editStateSetting = loginEmployeeId.equals(targetEmployeeId) ? 
				EditStateSetting.HAND_CORRECTION_MYSELF : EditStateSetting.HAND_CORRECTION_OTHER;
		
		return new EditStateOfDailyAttd(attendanceItemId, editStateSetting);
	}
	
	public boolean isHandCorrect() {
		return this.editStateSetting == EditStateSetting.HAND_CORRECTION_MYSELF
				|| this.editStateSetting == EditStateSetting.HAND_CORRECTION_OTHER;
	}
	
	public static interface Require {
		
		/** [R-1] ログイン社員IDを取得する */
		String getLoginEmployeeId();
		
	}
}
