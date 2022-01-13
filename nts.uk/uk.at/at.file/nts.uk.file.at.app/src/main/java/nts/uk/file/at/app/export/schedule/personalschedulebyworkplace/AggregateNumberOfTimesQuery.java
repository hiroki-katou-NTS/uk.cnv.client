package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import nts.uk.ctx.at.aggregation.app.find.schedulecounter.timesnumbercounter.SelectNoListDto;
import nts.uk.ctx.at.aggregation.app.find.schedulecounter.timesnumbercounter.TimesNumberCounterSelectionFinder;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.TotalTimesCounterService;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.timescounting.TimesNumberCounterType;
import nts.uk.ctx.at.shared.dom.common.EmployeeId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimes;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimesRepository;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.UseAtr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 回数集計を集計する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AggregateNumberOfTimesQuery {
    @Inject
    private TimesNumberCounterSelectionFinder finder;

    @Inject
    private TotalTimesRepository totalTimesRepo;

    @Inject
    private WorkTypeRepository workTypeRepo;

    @Inject
    private AttendanceItemConvertFactory factory;

    /**
     * 集計する
     * @param personalTotalCategory 個人計カテゴリ
     * @param integrationOfDailyList List<日別勤怠>
     * @return Map<社員ID, Map<回数集計, BigDecimal>>
     */
    public Map<String, Map<Integer, BigDecimal>> get(PersonalCounterCategory personalTotalCategory, List<IntegrationOfDaily> integrationOfDailyList) {
        int countType = 1;
        if (personalTotalCategory == PersonalCounterCategory.TIMES_COUNTING_1) countType = TimesNumberCounterType.PERSON_1.value;
        else if (personalTotalCategory == PersonalCounterCategory.TIMES_COUNTING_2) countType = TimesNumberCounterType.PERSON_2.value;
        else if (personalTotalCategory == PersonalCounterCategory.TIMES_COUNTING_3) countType = TimesNumberCounterType.PERSON_3.value;
        SelectNoListDto selectNoListDto = finder.findById(countType);
        List<TotalTimes> totalTimes = totalTimesRepo.getTotalTimesDetailByListNo(AppContexts.user().companyId(), selectNoListDto.getSelectedNoList())
                .stream().filter(t -> t.getUseAtr() == UseAtr.Use).collect(Collectors.toList());
        Map<EmployeeId, Map<Integer, BigDecimal>> result = TotalTimesCounterService.countingNumberOfTotalTimeByEmployee(
                new TotalTimesCounterService.Require() {
                    @Override
                    public Optional<WorkType> workType(String companyId, String workTypeCd) {
                        return workTypeRepo.findByPK(companyId, workTypeCd);
                    }
                    @Override
                    public DailyRecordToAttendanceItemConverter createDailyConverter() {
                        return factory.createDailyConverter();
                    }
                    @Override
                    public List<TotalTimes> getTotalTimesList(List<Integer> totalTimeNos) {
                        return totalTimesRepo.getTotalTimesDetailByListNo(AppContexts.user().companyId(), totalTimeNos);
                    }
                },
                totalTimes.stream().map(TotalTimes::getTotalCountNo).collect(Collectors.toList()),
                integrationOfDailyList
        );
        return result.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey().v(), Map.Entry::getValue));
    }
}
