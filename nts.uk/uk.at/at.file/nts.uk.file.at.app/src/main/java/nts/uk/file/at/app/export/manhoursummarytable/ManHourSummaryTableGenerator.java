package nts.uk.file.at.app.export.manhoursummarytable;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface ManHourSummaryTableGenerator {
    void generate(FileGeneratorContext generatorContext, ManHourSummaryExportData dataSource);
}
