package nts.uk.file.at.app.schedule.export;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;

@Stateless
public class WorkPlaceScheExportService extends ExportService<WorkPlaceScheDataSource> {

    @Inject
    private WorkPlaceScheGenerator generator;
    
    @Override
    protected void handle(ExportServiceContext<WorkPlaceScheDataSource> context) {
        WorkPlaceScheDataSource dataSource = context.getQuery();
        generator.generate(context.getGeneratorContext(), dataSource);
    }

}
