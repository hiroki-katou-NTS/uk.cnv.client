package nts.uk.ctx.at.request.ac.record.dailyattendancetime;

import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyAttendanceTimePub;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.DailyAttendanceTimePubImport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.importparam.ChildCareTimeZoneImport;
import nts.uk.ctx.at.record.pub.dailyprocess.attendancetime.importparam.OutingTimeZoneImport;
import nts.uk.ctx.at.request.dom.adapter.CalculationParams;
import nts.uk.ctx.at.request.dom.adapter.OneDayAttendanceTimeTempCalcAdapter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktime.common.TimeZone;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.stream.Collectors;

@Stateless
public class OneDayAttendanceTimeTempCalcAdapterImpl implements OneDayAttendanceTimeTempCalcAdapter {
    @Inject
    private DailyAttendanceTimePub dailyAttendanceTimePub;

    @Override
    public IntegrationOfDaily calculate(CalculationParams params) {
        DailyAttendanceTimePubImport dailyAttendanceTimePubImport = new DailyAttendanceTimePubImport();
        dailyAttendanceTimePubImport.setEmployeeid(params.getEmployeeId());
        dailyAttendanceTimePubImport.setYmd(params.getDate());
        dailyAttendanceTimePubImport.setWorkTypeCode(params.getWorkTypeCode());
        dailyAttendanceTimePubImport.setWorkTimeCode(params.getWorkTimeCode());
        dailyAttendanceTimePubImport.setTimeZoneMap(params.getWorkTimeZones().stream().collect(Collectors.toMap(i -> i.getWorkNo().v(), i -> new TimeZone(i.getTimeZone().getStartTime(), i.getTimeZone().getEndTime()))));
        dailyAttendanceTimePubImport.setBreakStartTime(params.getBreakTimeZones().stream().map(i -> new AttendanceTime(i.getTimeZone().getStartTime().v())).collect(Collectors.toList()));
        dailyAttendanceTimePubImport.setBreakEndTime(params.getBreakTimeZones().stream().map(i -> new AttendanceTime(i.getTimeZone().getEndTime().v())).collect(Collectors.toList()));
        dailyAttendanceTimePubImport.setOutingTimeSheets(params.getOutingTimeZones().stream().map(i -> new OutingTimeZoneImport(i.getGoingOutReason(), i.getTimeZone())).collect(Collectors.toList()));
        dailyAttendanceTimePubImport.setShortWorkingTimeSheets(params.getChildCareTimeZones().stream().map(i -> new ChildCareTimeZoneImport(i.getChildCareAtr(), i.getTimeZone())).collect(Collectors.toList()));

        //1日分の勤怠時間を仮計算
        IntegrationOfDaily calcResult = dailyAttendanceTimePub.calcOneDayAttendance(dailyAttendanceTimePubImport);

        return calcResult;
    }
}
