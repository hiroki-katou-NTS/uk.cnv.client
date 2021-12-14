package nts.uk.file.com.app.equipment.data;

import nts.arc.layer.app.file.export.ExportServiceContext;

public interface EquipmentDataExportFileQuery {
	void handle(ExportServiceContext<EquipmentDataQuery> context, EquipmentDataQuery query);
	EquipmentDataExportDataSource getExportData(EquipmentDataQuery query);
}
