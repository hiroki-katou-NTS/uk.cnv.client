package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.val;
import nts.arc.primitive.PrimitiveValueBase;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.adapter.dailyrecord.DailyRecordAdapter;
import nts.uk.ctx.at.aggregation.dom.adapter.workschedule.WorkScheduleAdapter;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceGettingService;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.AppointmentDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 予定を取得する
 */
@Stateless
public class GetAnAppointmentQuery {

    @Inject
    private GetTheAcquisitionPeriodAndWeekPeriodListQuery periodListQuery;

    @Inject
    private WorkScheduleAdapter workScheduleAdapter;

    @Inject
    private DailyRecordAdapter dailyRecordAdapter;

    @Inject
    private CreateWorkScheduleDtoQuery createWorkScheduleDtoQuery;

    @Inject
    private AggregateWeeklyAggregatedDataQuery aggregatedDataQuery;

    /**
     * 取得する
     *
     * @param period
     * @param startDate
     * @param isTotalDisplay
     * @return AppointmentDto
     */
    public AppointmentDto get(DatePeriod period, int startDate, boolean isTotalDisplay) {
        String employeeId = AppContexts.user().employeeId();
        //1.
        //取得期間と週合計期間リストを取得する
        val aqDatePeriodList = periodListQuery.get(period, startDate);
        //2.
        //日別勤怠を取得する
        List<IntegrationOfDaily> dailyAttendance = DailyAttendanceGettingService.getSchedule(
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
                Arrays.asList(new EmployeeId(employeeId)),
                aqDatePeriodList.getPeriod());
        //3.List<勤務予定（勤務情報）dto）
        val workInforDtoList = createWorkScheduleDtoQuery.get(dailyAttendance);
        //4.
        List<WeeklyAgreegateResult> agreegateResults = Collections.emptyList();
        if (isTotalDisplay) {
            agreegateResults = aggregatedDataQuery.get(aqDatePeriodList.getPeriodItem(), dailyAttendance, Collections.emptyList());
        }
        return new AppointmentDto(
                workInforDtoList,
                agreegateResults,
                aqDatePeriodList.getPeriodItem(),
                aqDatePeriodList.getPeriod()
        );
    }
}
