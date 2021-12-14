package nts.uk.file.com.infra.equipment.data;

import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.Encoding;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.TxtSaveOptions;
import com.aspose.cells.TxtValueQuoteType;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.gul.text.StringUtil;
import nts.uk.ctx.office.dom.equipment.achievement.EquipmentUsageRecordItemSetting;
import nts.uk.ctx.office.dom.equipment.achievement.ItemClassification;
import nts.uk.ctx.office.dom.equipment.achievement.ItemDisplay;
import nts.uk.ctx.office.dom.equipment.achievement.UsageRecordUnit;
import nts.uk.ctx.office.dom.equipment.classificationmaster.EquipmentClassification;
import nts.uk.ctx.office.dom.equipment.data.ActualItemUsageValue;
import nts.uk.ctx.office.dom.equipment.data.EquipmentData;
import nts.uk.ctx.office.dom.equipment.data.ResultData;
import nts.uk.ctx.office.dom.equipment.information.EquipmentInformation;
import nts.uk.file.com.app.equipment.data.EquipmentDataExportDataSource;
import nts.uk.file.com.app.equipment.data.EquipmentDataExportGenerator;
import nts.uk.file.com.app.equipment.data.EquipmentDataReportType;
import nts.uk.query.model.employee.EmployeeInformation;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AsposeEquipmentDataReportGenerator extends AsposeCellsReportGenerator
		implements EquipmentDataExportGenerator {

	private static final String TEMPLATE_FILE = "report/OEW001.xlsx";
	// B1_1 年月見出し
	private static final String EXPORT_YM_HEADER = "A1";
	// B2_1 設備分類コードヘッダー
	private static final String EQUIPMENT_CLS_CODE_HEADER = "A2";
	// B2_2 設備分類名称ヘッダー
	private static final String EQUIPMENT_CLS_NAME_HEADER = "B2";
	// B2_3 設備コードヘッダー
	private static final String EQUIPMENT_CODE_HEADER = "C2";
	// B2_4 設備名称ヘッダー
	private static final String EQUIPMENT_NAME_HEADER = "D2";
	// B2_5 利用日付ヘッダー
	private static final String USE_DATE_HEADER = "E2";
	// B2_6 氏名ヘッダー
	private static final String BUSINESS_NAME_HEADER = "F2";
	// D3_1 設備分類コード見出し
	private static final String CSV_EQUIPMENT_CLS_CODE_HEADER = "A3";
	// D3_2 設備分類名称見出し
	private static final String CSV_EQUIPMENT_CLS_NAME_HEADER = "B3";
	// D3_3 設備コード見出し
	private static final String CSV_EQUIPMENT_CODE_HEADER = "C3";
	// D3_4 設備名称見出し
	private static final String CSV_EQUIPMENT_NAME_HEADER = "D3";
	// D3_5 利用日付見出し
	private static final String CSV_USE_DATE_HEADER = "E3";
	// D3_6 社員コード見出し
	private static final String CSV_SCD_HEADER = "F3";
	// D3_7 社員名称見出し
	private static final String CSV_BUSINESS_NAME_HEADER = "G3";
	// The column where dynamic header or dynamic data begins (G~)
	private static final int DYNAMIC_COL_START = 6;
	private static final int CSV_DYNAMIC_COL_START = 7;
	// Header row index
	private static final int HEADER_ROW = 1;
	private static final int CSV_HEADER_ROW = 2;
	// First data row index
	private static final int DATA_ROW = 2;
	private static final int CSV_DATA_ROW = 3;
	// First data col index
	private static final int DATA_COL = 0;
	// C1_1 設備分類コード
	private static final int EQUIPMENT_CLS_CODE_INDEX = 0;
	// C1_2 設備分類名称
	private static final int EQUIPMENT_CLS_NAME_INDEX = 1;
	// C1_3 設備コード
	private static final int EQUIPMENT_CODE_INDEX = 2;
	// C1_4 設備名称
	private static final int EQUIPMENT_NAME_INDEX = 3;
	// C1_5 利用日付
	private static final int USE_DATE_INDEX = 4;
	// C1_6 氏名
	private static final int BUSINESS_NAME_INDEX = 5;
	// D5_7 社員名称
	private static final int CSV_BUSINESS_NAME_INDEX = 6;
	// Start of lines in CSV
	private static final int CSV_LINE_START_COL_INDEX = 0;
	// 1行目 ヘッダ
	private static final int CSV_TITLE_INDEX = 0;
	// 2行目 条件
	private static final int CSV_CONDITIONS_INDEX = 1;
	// File extension
	private static final String EXCEL_EXTENSION = ".xlsx";
	private static final String CSV_EXTENSION = ".csv";
	// 固定値Ｃ
	private static final int FIXED_VALUE_C = 8;
	// Max col width
	private static final int MAX_COLUMN_WIDTH = 300;
	// Excel Number format: Text
	// https://docs.aspose.com/cells/net/data-formatting/
	private static final int TEXT_FORMAT = 49;
	// マスタ未登録
	private static final String MASTER_NOT_REGISTERED = "マスタ未登録";

	@Override
	public void generate(FileGeneratorContext generatorContext, EquipmentDataExportDataSource dataSource) {
		try (AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE)) {
			WorksheetCollection collection = reportContext.getWorkbook().getWorksheets();
			Worksheet sheet = collection.get(0);
			String extension = null;
			dataSource.getFormSetting().ifPresent(data -> sheet.setName(data.getTitle().v()));
			if (dataSource.getReportType().equals(EquipmentDataReportType.EXCEL)) {
				this.printHeaderExcel(sheet, reportContext, dataSource);
				extension = EXCEL_EXTENSION;
			} else {
				this.printHeaderCsv(sheet, reportContext, dataSource);
				Cells cells = sheet.getCells();
				cells.deleteBlankRows();
				cells.deleteBlankColumns();
				extension = CSV_EXTENSION;
			}
			this.printData(sheet, reportContext, dataSource);
			sheet.setActiveCell(EXPORT_YM_HEADER);
			
			String fileName = dataSource.getFormSetting().map(data -> data.getTitle().v()).orElse("") + extension;
			OutputStream outputStream = this.createNewFile(generatorContext, this.getReportName(fileName));
			reportContext.processDesigner();
			if (dataSource.getReportType().equals(EquipmentDataReportType.EXCEL)) {
				reportContext.saveAsExcel(outputStream);
			} else {
				TxtSaveOptions saveOptions = new TxtSaveOptions(SaveFormat.CSV);
				saveOptions.setQuoteType(TxtValueQuoteType.MINIMUM);
				saveOptions.setEncoding(Encoding.getUTF8());
				reportContext.saveWithOtherOption(outputStream, saveOptions);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printHeaderExcel(Worksheet sheet, AsposeCellsReportContext reportContext,
			EquipmentDataExportDataSource dataSource) {
		String companyName = dataSource.getCompanyInfo().map(data -> data.getCompanyName().v()).orElse("");
		String titleName = dataSource.getFormSetting().map(data -> data.getTitle().v()).orElse("");
		String yearMonth = dataSource.getYearMonth().firstGeneralDate().toString("yyyy/MM");
		reportContext.setDataSource("titleName", titleName);
		reportContext.setDataSource("companyName", companyName);
		reportContext.setDataSource("yearMonth", yearMonth);
		sheet.getPageSetup().setHeader(0, "&\"ＭＳ ゴシック\"" + companyName);
		sheet.getPageSetup().setHeader(1, "&\"ＭＳ ゴシック\"" + titleName);
		this.printCell(sheet.getCells().get(EXPORT_YM_HEADER), TextResource.localize("OEW001_101"),
				TextAlignmentType.LEFT, false);
	}

	private void printHeaderCsv(Worksheet sheet, AsposeCellsReportContext reportContext,
			EquipmentDataExportDataSource dataSource) {
		Cells cells = sheet.getCells();
		String equipmentName = "すべての設備分類";
		if (dataSource.getOptEquipmentClsCode().isPresent()) {
			if (dataSource.getOptEquipmentInfoCode().isPresent()) {
				if (!dataSource.getEquipmentInfos().isEmpty()) {
					equipmentName = dataSource.getEquipmentInfos().get(0).getEquipmentName().v();
				}
			} else if (!dataSource.getEquipmentClassifications().isEmpty()) {
				equipmentName = dataSource.getEquipmentClassifications().get(0).getName().v();
			}
		}

		String yearMonth = dataSource.getYearMonth().firstGeneralDate().toString("yyyy/MM");
		this.printCell(cells.get(CSV_TITLE_INDEX, CSV_LINE_START_COL_INDEX),
				dataSource.getFormSetting().map(data -> data.getTitle().v()).orElse(null), TextAlignmentType.LEFT,
				null);
		this.printCell(cells.get(CSV_CONDITIONS_INDEX, CSV_LINE_START_COL_INDEX),
				TextResource.localize("OEW001_100") + "：" + equipmentName + "、" + yearMonth, TextAlignmentType.LEFT, null);
	}

	private void printData(Worksheet sheet, AsposeCellsReportContext reportContext,
			EquipmentDataExportDataSource dataSource) {
		Cells cells = sheet.getCells();
		boolean isPrintExcel = dataSource.getReportType().equals(EquipmentDataReportType.EXCEL);

		// Print headers
		// Print fixed headers
		if (isPrintExcel) {
			this.printFixedColumnsHeaderExcel(cells);
		} else {
			this.printFixedColumnsHeaderCsv(cells);
		}

		// Print dynamic headers
		List<ItemDisplay> itemDisplays = dataSource.getFormatSetting().getItemDisplaySettings().stream()
				.sorted(Comparator.comparing(ItemDisplay::getDisplayOrder)).collect(Collectors.toList());
		this.printDynamicColumnsHeader(cells, itemDisplays, dataSource.getItemSettings(), isPrintExcel);

		// Print data
		List<EquipmentData> equipmentDatas = dataSource.getEquipmentDatas();
		// Sort data (①設備分類コー ②設備コード ③日付 ④社員コード ⑤利用回目)
		equipmentDatas.sort(Comparator.comparing(EquipmentData::getEquipmentClassificationCode)
				.thenComparing(EquipmentData::getEquipmentCode).thenComparing(EquipmentData::getUseDate)
				.thenComparing(EquipmentData::getSid, (s1, s2) -> this.compareSids(s1, s2, dataSource))
				.thenComparing(EquipmentData::getInputDate));

		AtomicInteger currentRow = new AtomicInteger(isPrintExcel ? DATA_ROW : CSV_DATA_ROW);
		AtomicBoolean isBlueBackground = new AtomicBoolean(false);
		equipmentDatas.forEach(equipmentData -> {
			AtomicInteger currentColumn = new AtomicInteger(DATA_COL);

			Optional<EquipmentClassification> optEquipmentCls = dataSource.getEquipmentClassifications().stream()
					.filter(data -> data.getCode().equals(equipmentData.getEquipmentClassificationCode())).findFirst();
			Optional<EquipmentInformation> optEquipmentInfo = dataSource.getEquipmentInfos().stream()
					.filter(data -> data.getEquipmentCode().equals(equipmentData.getEquipmentCode())).findFirst();
			Optional<EmployeeInformation> optEmployee = dataSource.getEmployees().stream()
					.filter(data -> data.getEmployeeId().equals(equipmentData.getSid())).findFirst();

			// Print fixed data
			int colDataStart = isPrintExcel ? DYNAMIC_COL_START : CSV_DYNAMIC_COL_START;
			while (currentColumn.get() < colDataStart) {
				Cell cell = cells.get(currentRow.get(), currentColumn.get());
				switch (currentColumn.get()) {
				case EQUIPMENT_CLS_CODE_INDEX:
					this.printDataCell(cell, equipmentData.getEquipmentClassificationCode().v(), TextAlignmentType.CENTER,
							isBlueBackground.get());
					break;
				case EQUIPMENT_CLS_NAME_INDEX:
					this.printDataCell(cell, optEquipmentCls.map(data -> data.getName().v()).orElse(MASTER_NOT_REGISTERED),
							TextAlignmentType.LEFT, isBlueBackground.get());
					break;
				case EQUIPMENT_CODE_INDEX:
					this.printDataCell(cell, equipmentData.getEquipmentCode().v(), TextAlignmentType.CENTER,
							isBlueBackground.get());
					break;
				case EQUIPMENT_NAME_INDEX:
					// #120573
					this.printDataCell(cell, optEquipmentInfo.map(data -> data.getEquipmentName().v()).orElse(MASTER_NOT_REGISTERED),
							TextAlignmentType.LEFT, isBlueBackground.get());
					break;
				case USE_DATE_INDEX:
					this.printDataCell(cell, equipmentData.getUseDate().toString("yyyy/MM/dd"), TextAlignmentType.CENTER,
							isBlueBackground.get());
					break;
				case BUSINESS_NAME_INDEX:
					if (isPrintExcel) {
						this.printDataCell(cell, optEmployee.map(EmployeeInformation::getBusinessName).orElse(null),
								TextAlignmentType.LEFT, isBlueBackground.get());
					} else {
						// case CSV_SCD_INDEX
						// #120570
						this.printDataCell(cell, optEmployee.map(EmployeeInformation::getEmployeeCode).map(String::trim).orElse(null),
								TextAlignmentType.LEFT, isBlueBackground.get());
					}
					break;
				case CSV_BUSINESS_NAME_INDEX:
					this.printDataCell(cell, optEmployee.map(EmployeeInformation::getBusinessName).orElse(null),
							TextAlignmentType.LEFT, isBlueBackground.get());
					break;
				}
				currentColumn.getAndIncrement();
			}
			// Print dynamic data
			// Get display number
			itemDisplays.forEach(itemDisplay -> {
				Cell cell = cells.get(currentRow.get(), currentColumn.getAndIncrement());
				Optional<EquipmentUsageRecordItemSetting> optItemSetting = dataSource.getItemSettings().stream()
						.filter(data -> data.getItemNo().equals(itemDisplay.getItemNo())).findFirst();
				if (!optItemSetting.isPresent()) {
					this.printCell(cell, null, TextAlignmentType.LEFT, isBlueBackground.get());
				} else {
					Optional<ResultData> optItemData = equipmentData.getResultDatas().stream().filter(
							data -> data.getItemNo().equals(itemDisplay.getItemNo()) && data.getItemClassification()
									.equals(optItemSetting.get().getInputcontrol().getItemCls()))
							.findFirst();
					optItemData.ifPresent(itemData -> {
						String value = itemData.getActualValue().map(ActualItemUsageValue::v).orElse(null);
						int alignType = TextAlignmentType.LEFT;

						if (value != null) {
							// Only format number data if excel
							if (itemData.getItemClassification().equals(ItemClassification.NUMBER) && isPrintExcel) {
								value = String.format("%,d", Integer.valueOf(value));
								alignType = TextAlignmentType.RIGHT;
							} else if (itemData.getItemClassification().equals(ItemClassification.TIME) && isPrintExcel) {
								value = this.formatTime(Integer.valueOf(value));
								alignType = TextAlignmentType.RIGHT;
							}
						}
						this.printDataCell(cell, value, alignType, isBlueBackground.get());
					});
				}
			});
			// Row + 1 and change background color
			currentRow.incrementAndGet();
			isBlueBackground.set(!isBlueBackground.get());
		});
	}

	/**
	 * Print cells A2~F2 (excel)
	 * 
	 * @param cells
	 */
	private void printFixedColumnsHeaderExcel(Cells cells) {
		int alignType = TextAlignmentType.CENTER;
		this.printCell(cells.get(EQUIPMENT_CLS_CODE_HEADER), TextResource.localize("OEW001_102"), alignType, null);
		this.printCell(cells.get(EQUIPMENT_CLS_NAME_HEADER), TextResource.localize("OEW001_103"), alignType, null);
		this.printCell(cells.get(EQUIPMENT_CODE_HEADER), TextResource.localize("OEW001_104"), alignType, null);
		this.printCell(cells.get(EQUIPMENT_NAME_HEADER), TextResource.localize("OEW001_105"), alignType, null);
		this.printCell(cells.get(USE_DATE_HEADER), TextResource.localize("OEW001_106"), alignType, null);
		this.printCell(cells.get(BUSINESS_NAME_HEADER), TextResource.localize("OEW001_107"), alignType, null);
	}
	
	private void printFixedColumnsHeaderCsv(Cells cells) {
		int alignType = TextAlignmentType.CENTER;
		this.printCell(cells.get(CSV_EQUIPMENT_CLS_CODE_HEADER), TextResource.localize("OEW001_102"), alignType, null);
		this.printCell(cells.get(CSV_EQUIPMENT_CLS_NAME_HEADER), TextResource.localize("OEW001_103"), alignType, null);
		this.printCell(cells.get(CSV_EQUIPMENT_CODE_HEADER), TextResource.localize("OEW001_104"), alignType, null);
		this.printCell(cells.get(CSV_EQUIPMENT_NAME_HEADER), TextResource.localize("OEW001_105"), alignType, null);
		this.printCell(cells.get(CSV_USE_DATE_HEADER), TextResource.localize("OEW001_106"), alignType, null);
		this.printCell(cells.get(CSV_SCD_HEADER), TextResource.localize("OEW001_108"), alignType, null);
		this.printCell(cells.get(CSV_BUSINESS_NAME_HEADER), TextResource.localize("OEW001_107"), alignType, null);
	}

	/**
	 * Print cells G2~ (excel) / H2~ (csv)
	 * 
	 * @param cells
	 * @param col
	 * @param itemName
	 * @param width
	 */
	private void printDynamicColumnsHeader(Cells cells, List<ItemDisplay> itemDisplays,
			List<EquipmentUsageRecordItemSetting> itemSettings, boolean isPrintExcel) {
		AtomicInteger currentCol = new AtomicInteger(isPrintExcel ? DYNAMIC_COL_START : CSV_DYNAMIC_COL_START);
		int headerRow = isPrintExcel ? HEADER_ROW : CSV_HEADER_ROW;
		itemDisplays.forEach(itemDisplay -> {
			EquipmentUsageRecordItemSetting itemSetting = itemSettings.stream()
					.filter(data -> data.getItemNo().equals(itemDisplay.getItemNo())).findFirst().orElse(null);
			if (itemSetting != null) {
				int col = currentCol.getAndIncrement();
				int alignType = TextAlignmentType.CENTER;
				Cell cell = cells.get(headerRow, col);

				// Set column value
				String value = itemSetting.getItems().getItemName().v();
				Optional<UsageRecordUnit> optUnit = itemSetting.getItems().getUnit();
				if (optUnit.isPresent() && !StringUtil.isNullOrEmpty(optUnit.get().v(), true)) {
					value = value.concat(TextResource.localize("OEW001_109") + optUnit.get()
							+ TextResource.localize("OEW001_110"));
				}
				this.printCell(cell, value, alignType, null);

				// Set column width
				int width = Math.min(MAX_COLUMN_WIDTH, itemDisplay.getDisplayWidth().v() * FIXED_VALUE_C);
				cells.setColumnWidthPixel(col, width);

				// Set style header
				Style style = cell.getStyle();
				style.setBorder(BorderType.TOP_BORDER, CellBorderType.MEDIUM, Color.getBlack());
				style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
				style.setBorder(BorderType.LEFT_BORDER, CellBorderType.DOTTED, Color.getBlack());
				style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.DOTTED, Color.getBlack());
				cell.setStyle(style);
			}
		});
	}
	
	private void printDataCell(Cell cell, String value, int alignType, Boolean isBlueBackground) {
		this.printCell(cell, value, alignType, isBlueBackground);
		// Set style data
		Style style = cell.getStyle();
		style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.DOTTED, Color.getBlack());
		cell.setStyle(style);
	}

	/**
	 * Print a cell
	 * 
	 * @param cell
	 * @param value
	 * @param alignType        {@link TextAlignmentType}
	 * @param isBlueBackground
	 *                         <li>null: for header</li>
	 *                         <li>true: for even row</li>
	 *                         <li>false: for odd row</li>
	 */
	private void printCell(Cell cell, String value, int alignType, Boolean isBlueBackground) {
		cell.setValue(value);
		Style style = cell.getStyle();
		style.setHorizontalAlignment(alignType);
		style.setVerticalAlignment(TextAlignmentType.CENTER);
		style.setPattern(BackgroundType.SOLID);
		style.setNumber(TEXT_FORMAT);
		if (isBlueBackground != null) {
			if (isBlueBackground) {
				// Even row background color
				style.setForegroundColor(Color.fromArgb(221, 235, 247));
			} else {
				// Odd row background color
				style.setForegroundColor(Color.getWhite());
			}
		} else {
			// Header background color
			style.setForegroundColor(Color.fromArgb(155, 194, 230));
		}
		cell.setStyle(style);
	}

	/**
	 * Compare 2 employees by SCD from SID
	 * 
	 * @param s1         sid1
	 * @param s2         sid2
	 * @param dataSource
	 * @return
	 */
	private int compareSids(String s1, String s2, EquipmentDataExportDataSource dataSource) {
		Optional<EmployeeInformation> e1 = dataSource.getEmployees().stream()
				.filter(data -> data.getEmployeeId().equals(s1)).findFirst();
		Optional<EmployeeInformation> e2 = dataSource.getEmployees().stream()
				.filter(data -> data.getEmployeeId().equals(s2)).findFirst();
		if (!e1.isPresent()) {
			return -1;
		}
		if (!e2.isPresent()) {
			return 1;
		}
		return e1.get().getEmployeeCode().compareTo(e2.get().getEmployeeCode());
	}
	
	/**
	 * Format Time_Short_Hm
	 * 
	 * @param value minutes
	 * @return
	 */
	private String formatTime(int value) {
		final String template = "%d:%s";
		final int hours = value / 60;
		final int minutes = value % 60;
		return String.format(template, hours, 
				StringUtil.padLeft(String.valueOf(minutes), 2, '0'));
	}
}
