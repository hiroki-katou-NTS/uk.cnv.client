package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.DatePeriodListDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.PeriodListPeriodDto;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * 取得期間と週合計期間リストを取得する
 */
@Stateless
public class GetTheAcquisitionPeriodAndWeekPeriodListQuery {

    @Inject
    private WeekRuleManagementRepo weekRuleManagementRepo;

    /**
     * 取得する
     *
     * @param period
     * @param startDate
     * @return DatePeriodListDto
     */
    public DatePeriodListDto get(DatePeriod period, int startDate) {
        String companyId = AppContexts.user().companyId();
        val weekRuleManagement = weekRuleManagementRepo.find(companyId);
        List<String> periodList = new ArrayList<>();
        List<DatePeriod> periodItems = new ArrayList<>();
        DatePeriod datePeriod = period;
        if (weekRuleManagement.isPresent() && weekRuleManagement.get().getDayOfWeek().value == startDate) {
//            int count = 0;
//            String starOfWeek = null;
//            String endOfWeek = null;
//
//            GeneralDate starOfWeekDate = null;
//            GeneralDate endOfWeekDate = null;
//
//            for (val date : period.datesBetween()) {
//                count++;
//                if (count == 1) {
//                    starOfWeek = date.toString("MM/dd");
//                    starOfWeekDate = date;
//                }
//                if (count == 7 || date.equals(period.end())) {
//                    endOfWeek = date.toString("MM/dd");
//                    endOfWeekDate = date;
//                    periodList.add(starOfWeek + "~" + endOfWeek);
//                    periodItems.add(new DatePeriod(starOfWeekDate, endOfWeekDate));
//                    count = 0;
//                }
//            }

            GeneralDate start = period.start();
            while (start.dayOfWeekEnum().value != startDate) {
                start = start.addDays(-1);
            }
            GeneralDate end = period.end();
            end = end.addDays(1);
            while (end.dayOfWeekEnum().value != startDate) {
                end = end.addDays(1);
            }
            end = end.addDays(-1);

            datePeriod = new DatePeriod(start, end);
            GeneralDate startDateNew = start;
            while (startDateNew.before(end)) {
                periodList.add(startDateNew.toString("MM/dd") + "~" + startDateNew.addDays(6).toString("MM/dd"));
                periodItems.add(new DatePeriod(startDateNew, startDateNew.addDays(6)));
                startDateNew = startDateNew.addDays(7);
            }
        }
        return new DatePeriodListDto(
                datePeriod,  //period
                periodList,
                periodItems
        );
    }
}
