package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.calendar.period.DatePeriod;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PersonalScheduleByWorkplaceExportService extends ExportService<PersonalScheduleByWkpQuery> {
    @Inject
    private PersonalScheduleByWorkplaceExportQuery exportQuery;

    @Inject
    private PersonalScheduleByWorkplaceExportGenerator exportGenerator;

    @Override
    protected void handle(ExportServiceContext<PersonalScheduleByWkpQuery> exportServiceContext) {
        PersonalScheduleByWkpQuery query = exportServiceContext.getQuery();
        DatePeriod period = new DatePeriod(query.getPeriodStart(), query.getPeriodEnd());
        PersonalScheduleByWkpDataSource dataSource = exportQuery.get(
                query.getOrgUnit(),
                query.getOrgId(),
                period,
                query.getOutputSettingCode(),
                query.getEmployeeIds(),
                query.getClosureDate()
        );
        exportGenerator.generate(exportServiceContext.getGeneratorContext(), dataSource, query.getComment(), query.isExcel(), query.isPreview());
    }
}
