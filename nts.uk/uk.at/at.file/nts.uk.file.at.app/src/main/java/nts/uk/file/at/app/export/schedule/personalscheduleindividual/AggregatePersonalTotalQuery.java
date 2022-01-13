package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.aggregation.dom.adapter.dailyrecord.DailyRecordAdapter;
import nts.uk.ctx.at.aggregation.dom.adapter.workschedule.WorkScheduleAdapter;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompanyRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountUsageSettingRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountRepository;
import nts.uk.ctx.at.function.dom.adapter.WorkPlaceHistImport;
import nts.uk.ctx.at.function.dom.adapter.WorkplaceWorkRecordAdapter;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.file.at.app.export.schedule.personalschedulebyworkplace.AggregateNumberOfTimesQuery;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 期間中の年月日情報を取得する
 */
@Stateless
public class AggregatePersonalTotalQuery {
    @Inject
    private WorkScheduleAdapter workScheduleAdapter;
    @Inject
    private DailyRecordAdapter dailyRecordAdapter;
    @Inject
    private HandlingOfCriterionAmountRepository handlingOfCriterionAmountRepo;
    @Inject
    private CriterionAmountUsageSettingRepository criterionAmountUsageSettingRepo;
    @Inject
    private CriterionAmountForCompanyRepository criterionAmountForCompanyRepo;
    @Inject
    private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepo;
    @Inject
    private EmploymentHisAdapter employmentHisAdapter;
    @Inject
    private WorkTypeRepository workTypeRepo;

    @Inject
    private AggregateNumberOfTimesQuery aggregateNumberOfTimesQuery;

    @Inject
    private WorkplaceWorkRecordAdapter workplaceWorkRecordAdapter;

    /**
     * 集計する
     *
     * @param employeeId
     * @param period     期間
     * @return List<年月日情報>
     */
    public <T> List<DateInformation> get(String employeeId, DatePeriod period) {
        String companyId = AppContexts.user().companyId();
        //社員（List）と期間から職場履歴を取得する
        List<WorkPlaceHistImport> workPlaceHistImports = workplaceWorkRecordAdapter.getWplByListSidAndPeriod(Arrays.asList(employeeId), period);
        return Collections.emptyList();
    }
}