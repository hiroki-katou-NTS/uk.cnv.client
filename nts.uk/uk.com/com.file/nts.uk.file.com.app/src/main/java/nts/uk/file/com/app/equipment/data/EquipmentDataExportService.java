package nts.uk.file.com.app.equipment.data;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EquipmentDataExportService extends ExportService<EquipmentDataQuery> {

	@Inject
	private EquipmentDataExportFileQuery fileQuery;
	
	@Override
	protected void handle(ExportServiceContext<EquipmentDataQuery> context) {
		this.fileQuery.handle(context, context.getQuery());
	}

}
