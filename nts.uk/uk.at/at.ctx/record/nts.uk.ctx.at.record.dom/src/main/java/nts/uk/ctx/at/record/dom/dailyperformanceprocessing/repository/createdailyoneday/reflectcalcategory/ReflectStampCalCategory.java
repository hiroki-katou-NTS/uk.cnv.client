package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.reflectcalcategory;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;

/**
 * 打刻を計算区分に反映する
 * 
 * @author tutk
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ReflectStampCalCategory {
	public void reflectStamp(AutoCalSetting normalOtTime, List<EditStateOfDailyAttd> editState, int attendanceItem,AutoCalAtrOvertime overTimeAutoCalAtr) {
		// 「パラメータ。自動計算設定。計算区分」を確認する
		if (overTimeAutoCalAtr != AutoCalAtrOvertime.TIMERECORDER) {
			return;
		}
		// パラメータ。自動計算設定。計算区分＝打刻から計算する
		normalOtTime.setCalAtr(AutoCalAtrOvertime.CALCULATEMBOSS);
		// 編集状態を作成するする
		EditStateOfDailyAttd editStateOfDailyAttd = new EditStateOfDailyAttd(attendanceItem, EditStateSetting.IMPRINT);
		// 作成した編集状態を日別実績の編集状態一覧に入れる
		editState.add(editStateOfDailyAttd);
	}

}
