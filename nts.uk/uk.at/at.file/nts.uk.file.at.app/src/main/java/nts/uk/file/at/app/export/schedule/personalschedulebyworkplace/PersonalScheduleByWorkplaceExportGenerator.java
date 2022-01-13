package nts.uk.file.at.app.export.schedule.personalschedulebyworkplace;

import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.calendar.period.DatePeriod;

public interface PersonalScheduleByWorkplaceExportGenerator {
    void generate(FileGeneratorContext context, PersonalScheduleByWkpDataSource dataSource, String comment, boolean excel, boolean preview);
}
