package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.adapter.dailyrecord.DailyRecordAdapter;
import nts.uk.ctx.at.aggregation.dom.adapter.workschedule.WorkScheduleAdapter;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceGettingService;
import nts.uk.ctx.at.aggregation.dom.common.ScheRecGettingAtr;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 予定・実績を取得する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).D：出力の設定.メニュー別OCD.予定・実績を取得する.予定・実績を取得する
 */
@Stateless
public class DailyAttendanceGettingFileQuery {
    @Inject
    private WorkScheduleAdapter workScheduleAdapter;

    @Inject
    private DailyRecordAdapter dailyRecordAdapter;

    public Map<ScheRecGettingAtr, List<IntegrationOfDaily>> get(List<String> sortedEmployeeIds, DatePeriod period, boolean isDisplayActual) {
        return DailyAttendanceGettingService.get(
                new DailyAttendanceGettingService.Require() {
                    @Override
                    public List<IntegrationOfDaily> getSchduleList(List<EmployeeId> empIds, DatePeriod period) {
                        return workScheduleAdapter.getList(empIds.stream().map(PrimitiveValueBase::v).collect(Collectors.toList()), period);
                    }

                    @Override
                    public List<IntegrationOfDaily> getRecordList(List<EmployeeId> empIds, DatePeriod period) {
                        return dailyRecordAdapter.getDailyRecordByScheduleManagement(empIds.stream().map(PrimitiveValueBase::v).collect(Collectors.toList()), period);
                    }
                },
                sortedEmployeeIds.stream().map(EmployeeId::new).collect(Collectors.toList()),
                period,
//              ・if 実績も取得するか == trueの時に => 取得対象＝予定+実績
//              ・if 実績も取得するか == false の時に => 取得対象＝予定のみ
                isDisplayActual ? ScheRecGettingAtr.SCHEDULE_WITH_RECORD : ScheRecGettingAtr.ONLY_SCHEDULE
        );
    }
}
