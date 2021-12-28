package nts.uk.ctx.at.record.app.command.kdw013.e;

import java.util.Arrays;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDailyRepo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;

/**
 * @author thanhpv
 * @part UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.E：時刻なし作業内容.メニュー別OCD.応援時間帯を更新する
 */
@Stateless
public class UpdateAttendanceTimeZoneBySupportWorkCommandHandler {

	@Inject
	private OuenWorkTimeSheetOfDailyRepo ouenWorkTimeSheetOfDailyRepo;
	
	@Inject
	private OuenWorkTimeOfDailyRepo ouenWorkTimeOfDailyRepo;

	/**
	 * @name 応援時間帯を更新する
	 * @param empId 社員ID
	 * @param date 年月日
	 * @param ouenTimeSheet 応援時間帯
	 * @param ouenWorkTimeOfDaily 応援時間
	 */
	public void update(String empId, GeneralDate date, OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet, OuenWorkTimeOfDailyAttendance ouenTime) {
		//Get(社員ID,年月日)
		OuenWorkTimeSheetOfDaily ouenWorkTimeSheetOfDaily = ouenWorkTimeSheetOfDailyRepo.find(empId, date);
		if(ouenWorkTimeSheetOfDaily == null) {
			return;
		}
		//応援時間帯．応援勤務枠No == INPUT「日別勤怠の応援作業時間帯．応援勤務枠No」:set(応援時間帯)
		Optional<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheetOpt = ouenWorkTimeSheetOfDaily.getOuenTimeSheet().stream().filter(c->c.getWorkNo().v()== ouenTimeSheet.getWorkNo().v()).findFirst();
		if(ouenTimeSheetOpt.isPresent()) {
			ouenWorkTimeSheetOfDaily.updateOuenTimeSheet(ouenTimeSheet);
			//persist
			ouenWorkTimeSheetOfDailyRepo.update(Arrays.asList(ouenWorkTimeSheetOfDaily));
		}
		//Get(社員ID,年月日)
		Optional<OuenWorkTimeOfDaily> ouenWorkTimeOfDaily = ouenWorkTimeOfDailyRepo.find(empId, date);
		if(ouenWorkTimeOfDaily.isPresent()) {
			//応援時間帯．応援勤務枠No == INPUT「日別勤怠の応援作業時間帯．応援勤務枠No」:set(応援時間帯)
			Optional<OuenWorkTimeOfDailyAttendance> ouenTimeOpt = ouenWorkTimeOfDaily.get().getOuenTimes().stream().filter(c->c.getWorkNo().v()== ouenTime.getWorkNo().v()).findFirst();
			if(ouenTimeOpt.isPresent()) {
				ouenWorkTimeOfDaily.get().updateOuenTime(ouenTime);
				//persist
				ouenWorkTimeOfDailyRepo.update(Arrays.asList(ouenWorkTimeOfDaily.get()));
			}
		}
	}
}
