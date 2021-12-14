package nts.uk.ctx.at.record.dom.workrecord.errorsetting.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.AttendanceLeavingGateOfDaily;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.entranceandexit.AttendanceLeavingGate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.worktime.common.AmPmAtr;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;

/**
 * 打刻漏れ(入退門) - 入退門打刻漏れ
 * 
 * @author nampt
 *
 */
@Stateless
public class ExitStampCheck {

	@Inject
	private BasicScheduleService basicScheduleService;

	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

	public EmployeeDailyPerError exitStampCheck(String companyId, String employeeId, GeneralDate processingDate,
			AttendanceLeavingGateOfDaily attendanceLeavingGateOfDaily,
			WorkInfoOfDailyPerformance workInfoOfDailyPerformance) {

		EmployeeDailyPerError employeeDailyPerError = null;

		// 1日半日出勤・1日休日系の判定
		WorkStyle workStyle = basicScheduleService
				.checkWorkDay(workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTypeCode().v());

		if (workStyle != WorkStyle.ONE_DAY_REST) {
			if (attendanceLeavingGateOfDaily != null
					&& !attendanceLeavingGateOfDaily.getTimeZone().getAttendanceLeavingGates().isEmpty()) {
				List<Integer> attendanceItemIDList = new ArrayList<>();

				// 所定時間設定を取得
				Optional<PredetemineTimeSetting> predetemineTimeSet = Optional.empty();
				if (workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull()
						.isPresent()) {
					predetemineTimeSet = predetemineTimeSettingRepository.findByWorkTimeCode(companyId,
							workInfoOfDailyPerformance.getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull()
									.get().v());
				}

				// 所定労働時間帯の件数を取得
				int predTimeSpanCount = predetemineTimeSet.isPresent()
						? predetemineTimeSet.get()
								.getTimezoneByAmPmAtrForCalc(workStyle.toAmPmAtr().orElse(AmPmAtr.ONE_DAY)).size()
						: 0;

				List<AttendanceLeavingGate> attendanceLeavingGates = attendanceLeavingGateOfDaily.getTimeZone()
						.getAttendanceLeavingGates();
				for (int number = 1; number <= predTimeSpanCount; number++) {// start for 1
					for (AttendanceLeavingGate attendanceLeavingGate : attendanceLeavingGates) {// start for 2
						if (attendanceLeavingGate.getWorkNo().v().intValue() == number) {
							// 入門のみ存在している(only has attendance time)
							if (!attendanceLeavingGate.leakageCheck()
									&& !attendanceLeavingGate.getLeavingTime().isPresent()) {
								if (attendanceLeavingGate.getWorkNo().v() == 1) {
									attendanceItemIDList.add(77);
								} else if (attendanceLeavingGate.getWorkNo().v() == 2) {
									attendanceItemIDList.add(81);
								}
							}
							// 退門のみ存在している(only has leaving time)
							if (!attendanceLeavingGate.leakageCheck()
									&& !attendanceLeavingGate.getAttendanceTime().isPresent()) {
								if (attendanceLeavingGate.getWorkNo().v() == 1) {
									attendanceItemIDList.add(75);
								} else if (attendanceLeavingGate.getWorkNo().v() == 2) {
									attendanceItemIDList.add(79);
								}
							}
						}
					} // end for 2
				} // end for 1

				if (!attendanceItemIDList.isEmpty()) {
					employeeDailyPerError = new EmployeeDailyPerError(companyId, employeeId, processingDate,
							new ErrorAlarmWorkRecordCode("S003"), attendanceItemIDList);
				}
			}
		}

		return employeeDailyPerError;
	}

}
