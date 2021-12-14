package nts.uk.file.com.app.equipment.classificationmaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassificationRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListMode;

/**
 * OEM004-設備分類の登録 Export
 * @author NWS-DungDV
 *
 */
@Stateless
@DomainID("EquipmentClassification")
public class EquipmentClsExportImpl implements MasterListData {
	
	private static final String SHEET_NAME = "設備分類の登録";
	private static final String[] HEADER_NAMES = { "OEM004_6", "OEM004_12" };
	
	@Inject
	private EquipmentClassificationRepository equipmentClsRepository;
	
	@Override
	public String mainSheetName() {
		return SHEET_NAME;
	}
	
	@Override
	public MasterListMode mainSheetMode() {
		return MasterListMode.NONE;
	}
	
	@Override
	public List<MasterHeaderColumn> getHeaderColumns(MasterListExportQuery query) {
		return Stream.of(HEADER_NAMES).map(this::createHeader).collect(Collectors.toList());
	}
	
	@Override
	public List<MasterData> getMasterDatas(MasterListExportQuery query) {
		List<EquipmentClassification> dataList = this.equipmentClsRepository.getAll(AppContexts.user().contractCode());
		return dataList.stream().map(this::createData).collect(Collectors.toList());
	}
	
	private MasterData createData(EquipmentClassification data) {
		Map<String, Object> rowData = new HashMap<>();
		rowData.put(HEADER_NAMES[0], data.getCode().v());
		rowData.put(HEADER_NAMES[1], data.getName().v());
		
		MasterData masterData = new MasterData(rowData, null, "");
		for (String header : HEADER_NAMES) {
			masterData.cellAt(header).getStyle().horizontalAlign(ColumnTextAlign.LEFT);
		}
		return masterData;
	}
	
	private MasterHeaderColumn createHeader(String resourceId) {
		String header = TextResource.localize(resourceId);
		return new MasterHeaderColumn(resourceId, header, ColumnTextAlign.LEFT, "", true);
	}
}
