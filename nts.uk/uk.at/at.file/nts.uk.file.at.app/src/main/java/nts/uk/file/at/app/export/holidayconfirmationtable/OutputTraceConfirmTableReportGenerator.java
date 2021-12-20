package nts.uk.file.at.app.export.holidayconfirmationtable;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface OutputTraceConfirmTableReportGenerator {
    void generate(FileGeneratorContext generatorContext, OutputTraceConfirmTableDataSource dataSource);

}
