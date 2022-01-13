package nts.uk.screen.at.app.kdw013.e;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.command.kdw013.e.UpdateAttendanceTimeZoneBySupportWorkCommandHandler;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.E：時刻なし作業内容.メニュー別OCD.応援時間帯を更新する
 * 
 * @author tutt
 *
 */
@Stateless
public class UpdateAttendanceTimeZoneBySupportWork {

	@Inject
	private UpdateAttendanceTimeZoneBySupportWorkCommandHandler handler;

	public void update(UpdateAttendanceTimeZoneBySupportWorkCommand command) {
		
		handler.update(command.empId, command.date,
				OuenWorkTimeSheetOfDailyAttendance.create(SupportFrameNo.of(command.ouenTime.workNo),
						command.ouenTimeSheet.workContent.toDomain(), command.ouenTimeSheet.timeSheet.toDomain(),
						Optional.empty()),

				OuenWorkTimeOfDailyAttendance.create(SupportFrameNo.of(command.ouenTime.workNo),
						command.ouenTime.workTime.toDomain(), null, null));
	}
}
