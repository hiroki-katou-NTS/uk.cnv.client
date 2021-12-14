package nts.uk.file.com.app.equipment.data;

import nts.arc.layer.infra.file.export.FileGeneratorContext;

public interface EquipmentDataExportGenerator {

	void generate (FileGeneratorContext generatorContext, EquipmentDataExportDataSource dataSource);
}
