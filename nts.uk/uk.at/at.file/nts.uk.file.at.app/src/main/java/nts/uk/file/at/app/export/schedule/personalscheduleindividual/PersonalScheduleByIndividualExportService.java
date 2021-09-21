package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.calendar.period.DatePeriod;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PersonalScheduleByIndividualExportService extends ExportService<PersonalScheduleByIndividualQuery> {
    @Inject
    private PersonalScheduleByIndividualExportQuery exportQuery;

    @Inject
    private PersonalScheduleByIndividualExportGenerator exportGenerator;

    @Override
    protected void handle(ExportServiceContext<PersonalScheduleByIndividualQuery> exportServiceContext) {
        PersonalScheduleByIndividualQuery query = exportServiceContext.getQuery();
        DatePeriod period = new DatePeriod(query.getPeriod().getStartDate(), query.getPeriod().getEndDate());
        PersonalScheduleIndividualDataSource dataSource = exportQuery.get(
                query.getEmployeeId(),
                query.getEmployeeCode(),
                query.getDate(),
                period,
                query.getStartDate(),
                query.isTotalDisplay()

        );
        exportGenerator.generate(exportServiceContext.getGeneratorContext(), dataSource,query);
    }
}
