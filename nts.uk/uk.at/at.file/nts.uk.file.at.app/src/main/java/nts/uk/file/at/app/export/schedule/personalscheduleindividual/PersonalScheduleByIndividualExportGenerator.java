package nts.uk.file.at.app.export.schedule.personalscheduleindividual;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface PersonalScheduleByIndividualExportGenerator {
    void generate(FileGeneratorContext context, PersonalScheduleIndividualDataSource dataSource, PersonalScheduleByIndividualQuery query);
}
