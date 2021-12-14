package nts.uk.file.com.app.equipment.achievement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import nts.arc.error.BusinessException;
import nts.uk.file.com.app.equipment.achievement.ac.EquipmentUsageSettingsImport;
import nts.uk.file.com.app.equipment.achievement.ac.ItemDisplayImport;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.masterlist.annotation.DomainID;
import nts.uk.shr.infra.file.report.masterlist.data.ColumnTextAlign;
import nts.uk.shr.infra.file.report.masterlist.data.MasterData;
import nts.uk.shr.infra.file.report.masterlist.data.MasterHeaderColumn;
import nts.uk.shr.infra.file.report.masterlist.data.MasterListData;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListExportQuery;
import nts.uk.shr.infra.file.report.masterlist.webservice.MasterListMode;

@Stateless
@DomainID("EquipmentUsageSettings")
public class EquipmentUsageSettingsExportImpl implements MasterListData {

	@Inject
	private EquipmentUsageSettingsExportRepository repository;

	private static final String SHEET_NAME = TextResource.localize("OEM003_1");
	private static final String[] HEADER_NAMES = { "OEM003_23", "OEM003_8", "OEM003_9", "OEM003_10", "OEM003_11",
			"OEM003_12", "OEM003_13", "OEM003_14", "OEM003_15" };
	private static final Map<ColumnTextAlign, int[]> TEXT_ALIGN_MAP = ImmutableMap.of(
			ColumnTextAlign.LEFT, new int[] { 1, 3, 8 },
			ColumnTextAlign.CENTER, new int[] { 0, 6, 7 },
			ColumnTextAlign.RIGHT, new int[] { 2, 4, 5 }
	);

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
		EquipmentUsageSettingsImport settings = this.repository.findSettings();
		if (settings.getItemSettings().isEmpty()) {
			throw new BusinessException("Msg_35");
		}
		
		return settings.getItemSettings().stream().map(itemSetting -> {
			ItemDisplayImport itemDisplay = settings.getFormatSetting().getItemDisplaySettings().stream()
					.filter(data -> data.getItemNo().equals(itemSetting.getItemNo())).findFirst().orElse(null);
			if (itemDisplay != null) {
				EquipmentUsageSettingsDataSource dataSource = EquipmentUsageSettingsDataSource
						.createDataSource(itemDisplay, itemSetting);
				return this.createData(dataSource);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private MasterData createData(EquipmentUsageSettingsDataSource data) {
		Map<String, Object> rowData = new HashMap<>();
		rowData.put(HEADER_NAMES[0], data.getDisplayNo());
		rowData.put(HEADER_NAMES[1], data.getItemName());
		rowData.put(HEADER_NAMES[2], data.getDisplayWidth());
		rowData.put(HEADER_NAMES[3], data.getItemType());
		rowData.put(HEADER_NAMES[4], data.getMinimum());
		rowData.put(HEADER_NAMES[5], data.getMaximum());
		rowData.put(HEADER_NAMES[6], data.getUnit());
		rowData.put(HEADER_NAMES[7], data.getRequired());
		rowData.put(HEADER_NAMES[8], data.getMemo());

		MasterData masterData = new MasterData(rowData, null, "");
		TEXT_ALIGN_MAP.forEach((textAlign, cols) -> {
			for (int i: cols) {
				masterData.cellAt(HEADER_NAMES[i]).getStyle().horizontalAlign(textAlign);
			}
		});
		return masterData;
	}

	private MasterHeaderColumn createHeader(String resourceId) {
		String header = TextResource.localize(resourceId);
		return new MasterHeaderColumn(resourceId, header, ColumnTextAlign.LEFT, "", true);
	}
}
