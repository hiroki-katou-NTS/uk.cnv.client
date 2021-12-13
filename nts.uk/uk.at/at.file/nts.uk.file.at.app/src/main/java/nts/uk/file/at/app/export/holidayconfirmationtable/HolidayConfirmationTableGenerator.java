package nts.uk.file.at.app.export.holidayconfirmationtable;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface HolidayConfirmationTableGenerator {
    void generate(FileGeneratorContext generatorContext, Kdr004DataSource dataSource);
}
