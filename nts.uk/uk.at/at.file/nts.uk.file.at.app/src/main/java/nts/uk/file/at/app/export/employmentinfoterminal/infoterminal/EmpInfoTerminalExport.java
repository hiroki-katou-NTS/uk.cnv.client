package nts.uk.file.at.app.export.employmentinfoterminal.infoterminal;

import java.util.List;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface EmpInfoTerminalExport {

	void export(FileGeneratorContext generatorContext, List<EmpInfoTerminalExportDataSource> dataSource);
}
