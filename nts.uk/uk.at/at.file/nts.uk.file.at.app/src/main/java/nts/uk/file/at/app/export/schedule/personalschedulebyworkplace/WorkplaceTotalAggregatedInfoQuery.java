package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.common.ScheRecGettingAtr;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.aggregation.dom.scheduletable.ScheduleTableAttendanceItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.shr.com.enumcommon.NotUseAtr;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 職場計の集計情報を取得する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WorkplaceTotalAggregatedInfoQuery {
    @Inject
    private AggregateWorkplaceTotalQuery totalQuery;

    /**
     * 集計する
     * @param scheduleTableAttendanceItems
     * @param workplaceCounterCategories
     * @param integrationOfDailyMap
     * @param period
     * @param targetOrg
     * @param scheRecGettingAtr
     * @return <T>
     */
    public <T> Map<WorkplaceCounterCategory, Map<GeneralDate, T>> get(
            List<ScheduleTableAttendanceItem> scheduleTableAttendanceItems,
            List<WorkplaceCounterCategory> workplaceCounterCategories,
            Map<ScheRecGettingAtr, List<IntegrationOfDaily>> integrationOfDailyMap,
            DatePeriod period,
            TargetOrgIdenInfor targetOrg,
            ScheRecGettingAtr scheRecGettingAtr
    ) {
        Map<WorkplaceCounterCategory, Map<GeneralDate, T>> result = totalQuery.get(
                targetOrg,
                workplaceCounterCategories,
                integrationOfDailyMap,
                scheRecGettingAtr,
                period,
                scheduleTableAttendanceItems.contains(ScheduleTableAttendanceItem.SHIFT)
        );
        return result;
    }
}
