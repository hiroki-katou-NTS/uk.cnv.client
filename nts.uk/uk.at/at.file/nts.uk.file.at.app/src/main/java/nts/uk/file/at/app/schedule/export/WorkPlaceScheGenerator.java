package nts.uk.file.at.app.schedule.export;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface WorkPlaceScheGenerator {
    void generate(FileGeneratorContext generatorContext, WorkPlaceScheDataSource dataSource);
}
