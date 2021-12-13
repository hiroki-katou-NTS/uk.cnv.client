package nts.uk.file.at.app.export.schedule.personalschedulebydate;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface PersonalScheduleByDateExportGenerator {
    void generate(FileGeneratorContext context, PersonalScheduleByDateDataSource dataSource);
}
