package nts.uk.ctx.at.function.app.command.holidaysremaining.report;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidaysRemainingReportGenerator;

@Stateless
public class HolidaysRemainingReportHandler extends ExportService<HolidaysRemainingReportQuery> {

	@Inject
	private HolidaysRemainingReportGenerator reportGenerator;
	
	@Override
	protected void handle(ExportServiceContext<HolidaysRemainingReportQuery> context) {
		this.reportGenerator.generate(context.getGeneratorContext());
	}
}