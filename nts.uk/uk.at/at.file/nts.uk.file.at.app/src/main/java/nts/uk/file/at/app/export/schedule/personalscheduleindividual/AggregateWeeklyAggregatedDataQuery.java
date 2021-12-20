package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.val;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.DailyAttendanceMergingService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkClassificationAsAggregationTarget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkdayHolidayCounterService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkingTimeCounterService;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AttendanceTimesForAggregation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 週の集計データを集計する
 */
@Stateless
public class AggregateWeeklyAggregatedDataQuery {
    @Inject
    private WorkTypeRepository workTypeRepo;

    /**
     * 取得する
     *
     * @param periodItems
     * @param scheduleList
     * @param recordList
     */
    public List<WeeklyAgreegateResult> get(
            List<DatePeriod> periodItems,
            List<IntegrationOfDaily> scheduleList,
            List<IntegrationOfDaily> recordList) {
        String loginEmployeeId = AppContexts.user().employeeId();
        String companyId = AppContexts.user().companyId();
        List<WeeklyAgreegateResult> list = new ArrayList<>();
        int week = 0;
        //1.*
        for (DatePeriod periodItem : periodItems) {
            //1.1List<日別勤怠(Work)>
            List<IntegrationOfDaily> dailyAtList = DailyAttendanceMergingService.mergeToFlatList(
                    Arrays.asList(new EmployeeId(loginEmployeeId)),
                    periodItem,
                    scheduleList,
                    recordList
            );
            //1.2 Map<社員ID, Map<集計対象の勤怠時間, BigDecimal>>
            val workTimeCounterMap = WorkingTimeCounterService.get(dailyAtList);
            BigDecimal workingHours = workTimeCounterMap.isEmpty() ? BigDecimal.ZERO : workTimeCounterMap.get(new EmployeeId(loginEmployeeId)).get(AttendanceTimesForAggregation.WORKING_WITHIN);
            //1.3集計する(Require, List<日別勤怠(Work)>)
            Map<EmployeeId, Map<WorkClassificationAsAggregationTarget, BigDecimal>> holidayService = WorkdayHolidayCounterService.count(new WorkdayHolidayCounterService.Require() {
                @Override
                public Optional<WorkType> getWorkType(WorkTypeCode workTypeCd) {
                    return workTypeRepo.findByPK(companyId, workTypeCd.v());
                }
            }, dailyAtList);
            //1.3.1$労働時間 = result.get(社員ID).get( 就業時間 )
            val holidays = holidayService.isEmpty() ? BigDecimal.ZERO : holidayService.get(new EmployeeId(loginEmployeeId)).get(WorkClassificationAsAggregationTarget.HOLIDAY);
            week++;
            list.add(new WeeklyAgreegateResult(week, workingHours, holidays));
        }
        //return OrderedList<労働時間, 休日日数>
        return list.stream().sorted(Comparator.comparing(WeeklyAgreegateResult::getWeek)).collect(Collectors.toList());
    }
}
