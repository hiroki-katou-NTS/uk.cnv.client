package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.dto.EmployeeWorkScheduleResultDto;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 日付別実績情報を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).D：出力の設定.メニュー別OCD.日付別実績情報を取得する.日付別実績情報を取得する
 */
@Stateless
public class GetPerformanceInfoByDateExportQuery {

    public List<EmployeeWorkScheduleResultDto> get(List<IntegrationOfDaily> lstIntegrationOfDaily) {
        List<EmployeeWorkScheduleResultDto> empWorkSchedules = new ArrayList<>();

        lstIntegrationOfDaily.forEach(dailyInfo -> {
            Integer actualStartTime1 = null, actualStartTime2 = null, achievementEndTime1 = null, achievementEndTime2 = null;
            List<TimeLeavingWork> dailyAttendance = dailyInfo.getAttendanceLeave().isPresent()
                    ? dailyInfo.getAttendanceLeave().get().getTimeLeavingWorks()
                    : Collections.emptyList();

            val timeLeavingWork1 = dailyAttendance.stream().filter(x -> x.getWorkNo().v() == 1).findFirst();
            if (timeLeavingWork1.isPresent()) {
                // 実績開始時刻 1 = 日別勤怠(Work)．出退勤．出退勤．出勤 ※勤務NO = 1のもの。
                actualStartTime1 = (timeLeavingWork1.get().getAttendanceStamp().isPresent() && timeLeavingWork1.get().getAttendanceStamp().get().getStamp().isPresent()
                        && timeLeavingWork1.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent())
                        ? timeLeavingWork1.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()
                        : null;

                // 実績終了時刻 1 = 日別勤怠(Work)．出退勤．出退勤．退勤 ※勤務NO = 1のもの。
                achievementEndTime1 = (timeLeavingWork1.get().getLeaveStamp().isPresent() && timeLeavingWork1.get().getLeaveStamp().get().getStamp().isPresent()
                        && timeLeavingWork1.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent())
                        ? timeLeavingWork1.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()
                        : null;
            }

            val timeLeavingWork2 = dailyAttendance.stream().filter(x -> x.getWorkNo().v() == 2).findFirst();
            if (timeLeavingWork2.isPresent()) {
                // 実績開始時刻 2 = 日別勤怠(Work)．出退勤．出退勤．出勤 ※勤務NO = 2のもの。
                actualStartTime2 = (timeLeavingWork2.get().getAttendanceStamp().isPresent() && timeLeavingWork2.get().getAttendanceStamp().get().getStamp().isPresent()
                        && timeLeavingWork2.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent())
                        ? timeLeavingWork2.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()
                        : null;

                // 実績終了時刻 2 = 日別勤怠(Work)．出退勤．出退勤．退勤 ※勤務NO = 2のもの。
                achievementEndTime2 = (timeLeavingWork2.get().getLeaveStamp().isPresent() && timeLeavingWork2.get().getLeaveStamp().get().getStamp().isPresent()
                        && timeLeavingWork2.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent())
                        ? timeLeavingWork2.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v()
                        : null;
            }

            empWorkSchedules.add(new EmployeeWorkScheduleResultDto(
                    dailyInfo.getYmd(),
                    dailyInfo.getEmployeeId(),
                    Collections.emptyList(),
                    dailyInfo.getBreakTime().getBreakTimeSheets(),  // actualBreakTimeList
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    actualStartTime1,
                    achievementEndTime1,
                    actualStartTime2,
                    achievementEndTime2,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
        });

        return empWorkSchedules;
    }
}
