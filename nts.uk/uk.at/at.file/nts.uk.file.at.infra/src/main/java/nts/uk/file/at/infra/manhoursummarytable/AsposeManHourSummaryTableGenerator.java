package nts.uk.file.at.infra.manhoursummarytable;

import com.aspose.cells.*;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.DisplayFormat;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.TotalUnit;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.repo.taskframe.TaskFrameUsageSettingRepository;
import nts.uk.file.at.app.export.manhoursummarytable.ManHourSummaryExportData;
import nts.uk.file.at.app.export.manhoursummarytable.ManHourSummaryTableGenerator;
import nts.uk.screen.at.app.kha003.*;
import nts.uk.screen.at.app.kha003.a.ManHoursListScreenQuery;
import nts.uk.screen.at.app.kha003.b.ManHourPeriod;
import nts.uk.screen.at.app.kha003.exportcsv.ManHourSummaryTableOutputContentDto;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AsposeManHourSummaryTableGenerator extends AsposeCellsReportGenerator implements ManHourSummaryTableGenerator {
    private static final String TEMPLATE_FILE = "report/KHA003.xlsx";
    private static final String EXCEL_EXTENSION = ".xlsx";
    private static final String DATE_FORMAT = "yyyy/MM/dd";
    private static final String VERTICAL_TOTAL = "KHA003_99";
    private static final String HORIZONTAL_TOTAL = "KHA003_98";
    private static final String TOTAL = "KHA003_100";
    private static final int MAX_COLUMN_TEMPLATE = 36;

    @Inject
    private ManHoursListScreenQuery manHoursListScreenQuery;

    @Override
    public void generate(FileGeneratorContext generatorContext, ManHourSummaryExportData dataSource) {
        try {
            AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            String title = dataSource.getSummaryTableFormat().getName();

            val isDisplayTotal = dataSource.getSummaryTableFormat().getDispHierarchy() == 1;
            Worksheet worksheetTemplate = isDisplayTotal ? worksheets.get(0) : worksheets.get(1);
            Worksheet worksheet = worksheets.get(2);
            worksheet.setName(title);

            pageSetting(worksheet, title);
            printContents(worksheetTemplate, worksheet, dataSource, title);
            worksheets.removeAt(1);
            worksheets.removeAt(0);
            worksheets.setActiveSheetIndex(0);
            reportContext.processDesigner();
            val fileName = title + "_" + GeneralDateTime.now().toString("yyyyMMddHHmmss");
            reportContext.saveAsExcel(this.createNewFile(generatorContext, fileName + EXCEL_EXTENSION));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void pageSetting(Worksheet worksheet, String title) {
        String companyName = "";
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);

        pageSetup.setHeader(0, "&9&\"ＭＳ フォントサイズ\"" + companyName);
        pageSetup.setHeader(1, "&16&\"ＭＳ フォントサイズ,Bold\"" + title);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy/MM/dd  H:mm", Locale.JAPAN);
        pageSetup.setHeader(2,
                "&9&\"MS フォントサイズ\"" + LocalDateTime.now().format(dateTimeFormatter) + "\n" +
                        TextResource.localize("page") + " &P");
        pageSetup.setFitToPagesTall(0);
        pageSetup.setFitToPagesWide(0);
        pageSetup.setCenterHorizontally(true);
        pageSetup.setBottomMarginInch(1.5);
        pageSetup.setTopMarginInch(1.5);
        pageSetup.setLeftMarginInch(1.0);
        pageSetup.setRightMarginInch(1.0);
        pageSetup.setHeaderMarginInch(0.8);
        pageSetup.setZoom(100);
    }

    private void printContents(Worksheet worksheetTemplate, Worksheet worksheet, ManHourSummaryExportData data, String title) throws Exception {
        val detailFormatSetting = data.getSummaryTableFormat();
        val dispFormat = DisplayFormat.of(detailFormatSetting.getDisplayFormat());
        val totalUnit = detailFormatSetting.getTotalUnit();
        val isDisplayTotal = detailFormatSetting.getDispHierarchy() == 1;
        val outputContent = data.getOutputContent();

        String dateRange = totalUnit == TotalUnit.DATE.value
                ? data.getPeriod().getDatePeriod().start().toString(DATE_FORMAT) + "　～　" + data.getPeriod().getDatePeriod().end().toString(DATE_FORMAT)
                : data.getPeriod().getYearMonthPeriod().start().toString() + "　～　" + data.getPeriod().getYearMonthPeriod().end().toString();
        int maxDateRange = totalUnit == TotalUnit.DATE.value ? data.getPeriod().getDateList().size() : data.getPeriod().getYearMonthList().size();
        int totalLevel = data.getTotalLevel();
        if (totalLevel == 0) return;
        val headerList = getHeaderList(data.getPeriod(), detailFormatSetting, isDisplayTotal);
        int countColumn = headerList.size();

        Cells cellsTemplate = worksheetTemplate.getCells();
        Cells cells = worksheet.getCells();
        cells.copyRows(cellsTemplate, 0, 0, 3);  // Copy 3 row

        // Delete column name thừa
        cells.deleteColumns(0, totalLevel == 1 ? 3 : totalLevel == 2 ? 2 : totalLevel == 3 ? 1 : 0, true);

        // Check total column
        int maxColumnTemplate = isDisplayTotal ? MAX_COLUMN_TEMPLATE : (MAX_COLUMN_TEMPLATE - 1);
        int columnHandle = checkTotalColumn(maxColumnTemplate, countColumn);
        if (columnHandle < 0) {
            for (int i = 1; i <= Math.abs(columnHandle); i++) {
                cells.copyColumns(cellsTemplate, 5, headerList.size(), i);
            }
        }

        cells.clearContents(0, 0, cells.getMaxRow(), cells.getMaxColumn());
        //A1_1
        cells.get(0, 0).setValue(title);
        //A1_2
        cells.get(1, 0).setValue(dateRange);
        //D2_1 -> D5_1 , D6_1... D7_1 : Print Header
        for (int i = 0; i <= headerList.size() - 1; i++) {
            String item = headerList.get(i);
            cells.get(2, i).setValue(item);
        }

        // Print data
        switch (totalLevel) {
            case 1:
                printData1Level(cellsTemplate, cells, outputContent, isDisplayTotal, maxDateRange, headerList, dispFormat, totalUnit);
                worksheet.freezePanes(2, 1, 2, cells.getMaxColumn());
                break;
            case 2:
                printData2Level(cellsTemplate, cells, outputContent, isDisplayTotal, maxDateRange, headerList, dispFormat, totalUnit);
                worksheet.freezePanes(2, 2, 2, cells.getMaxColumn());
                break;
            case 3:
                printData3Level(cellsTemplate, cells, outputContent, isDisplayTotal, maxDateRange, headerList, dispFormat, totalUnit);
                worksheet.freezePanes(2, 3, 2, cells.getMaxColumn());
                break;
            case 4:
                printData4Level(cellsTemplate, cells, outputContent, isDisplayTotal, maxDateRange, headerList, dispFormat, totalUnit);
                worksheet.freezePanes(2, 4, 2, cells.getMaxColumn());
                break;
        }

        // Xoa column date thừa
        if (columnHandle > 0) {
            cells.deleteColumns(headerList.size(), columnHandle, true);
        }
//        worksheet.freezePanes(2, 4, 2, cells.getMaxColumn());
    }

    private void printData1Level(Cells cellsTemplate, Cells cells, ManHourSummaryTableOutputContentDto outputContent, boolean isDispTotal, int maxDateRange, List<String> headerList, DisplayFormat dispFormat, int unit) throws Exception {
        List<SummaryItemDetailDto> itemDetails = outputContent.getItemDetails();
        int countRow = 3;
        for (int i = 1; i <= itemDetails.size(); i++) {
            SummaryItemDetailDto level1 = itemDetails.get(i - 1);
            cells.copyRows(cellsTemplate, isDispTotal ? 11 : 8, countRow, 1);
            cells.get(countRow, 0).setValue(level1.getDisplayInfo().getName());
            // Border
            Cell cell = cells.get(countRow, 0);
            Style style = cell.getStyle();
            style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.HAIR, Color.getBlack());
            cell.setStyle(style);
            val workingTimeMap1 = this.getWorkingTimeByDate(unit, level1.getVerticalTotalList());
            for (int c = 1; c < maxDateRange + 1; c++) {
                cells.get(countRow, c).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(workingTimeMap1.getOrDefault(headerList.get(c), "")) : workingTimeMap1.getOrDefault(headerList.get(c), ""));
                setHorizontalAlignment(cells.get(countRow, c));
            }
            if (isDispTotal) {  // Tong chieu ngang level
                cells.get(countRow, headerList.size() - 1).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(level1.getTotalPeriod()) : level1.getTotalPeriod());
                setHorizontalAlignment(cells.get(countRow, headerList.size() - 1));
            }
            countRow++;
        }
        if (isDispTotal) { // Tong chieu doc cua level 1
            cells.copyRows(cellsTemplate, 37, countRow, 1);
            printAllTotalByVertical(cells, outputContent, maxDateRange, headerList, dispFormat, unit, countRow, 0);
            setBorderStyleForTotal(cells.get(countRow, 1));
        } else {
            for (int j = 0; j < headerList.size(); j++) {
                setBorderBottomStyle(cells.get(countRow - 1, j));
            }
        }
    }

    private void printData2Level(Cells cellsTemplate, Cells cells, ManHourSummaryTableOutputContentDto outputContent, boolean isDispTotal, int maxDateRange, List<String> headerList, DisplayFormat dispFormat, int unit) throws Exception {
        List<SummaryItemDetailDto> itemDetails = outputContent.getItemDetails();
        int countRow = 3;
        for (SummaryItemDetailDto level1 : itemDetails) {
            boolean isPrintNameLv1 = false;
            int mergeIndexLv1 = countRow;
            List<SummaryItemDetailDto> childHierarchyList = level1.getChildHierarchyList();
            for (int i = 1; i <= childHierarchyList.size(); i++) {
                cells.copyRows(cellsTemplate, isDispTotal ? 11 : 8, countRow, 1);
                SummaryItemDetailDto level2 = childHierarchyList.get(i - 1);
                cells.get(countRow, 0).setValue(!isPrintNameLv1 ? level1.getDisplayInfo().getName() : "");
                isPrintNameLv1 = true;
                cells.get(countRow, 1).setValue(level2.getDisplayInfo().getName());
                val workingTimeMap2 = this.getWorkingTimeByDate(unit, level2.getVerticalTotalList());
                for (int c = 2; c < maxDateRange + 2; c++) {
                    cells.get(countRow, c).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(workingTimeMap2.getOrDefault(headerList.get(c), "")) : workingTimeMap2.getOrDefault(headerList.get(c), ""));
                    setHorizontalAlignment(cells.get(countRow, c));
                }
                if (isDispTotal) {  // Tong chieu ngang level 3
                    cells.get(countRow, headerList.size() - 1).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(level2.getTotalPeriod()) : level2.getTotalPeriod());
                    setHorizontalAlignment(cells.get(countRow, headerList.size() - 1));
                }
                countRow++;
            }
            if (isDispTotal) { // Tong chieu doc level 2
                cells.copyRows(cellsTemplate, 11, countRow, 1);
                printTotalByVerticalOfEachLevel(cells, level1, maxDateRange, headerList, dispFormat, unit, countRow, 1, 0);
                countRow++;
            }
            cells.merge(mergeIndexLv1, 0, isDispTotal ? countRow - mergeIndexLv1 - 1 : countRow - mergeIndexLv1, 1, true, true);
            setVerticalAlignment(cells.get(mergeIndexLv1, 1));
            for (int j = 0; j < headerList.size(); j++) {
                setBorderBottomStyle(cells.get(countRow - 1, j));
            }
        }
        if (isDispTotal) { // Tong chieu doc cua level 1
            cells.copyRows(cellsTemplate, 37, countRow, 1);
            printAllTotalByVertical(cells, outputContent, maxDateRange, headerList, dispFormat, unit, countRow, 1);
            cells.merge(countRow, 0, 1, 2, true,true);
            setBorderStyleForTotal(cells.get(countRow, 2));
        }
    }

    private void printData3Level(Cells cellsTemplate, Cells cells, ManHourSummaryTableOutputContentDto outputContent, boolean isDispTotal, int maxDateRange, List<String> headerList, DisplayFormat dispFormat, int unit) throws Exception {
        List<SummaryItemDetailDto> itemDetails = outputContent.getItemDetails();
        int countRow = 3;
        for (SummaryItemDetailDto level1 : itemDetails) {
            boolean isPrintNameLv1 = false;
            int mergeIndexLv1 = countRow;
            for (SummaryItemDetailDto level2 : level1.getChildHierarchyList()) {
                boolean isPrintNameLv2 = false;
                int mergeIndexLv2 = countRow;
                List<SummaryItemDetailDto> childHierarchyList = level2.getChildHierarchyList();
                for (int i = 1; i <= childHierarchyList.size(); i++) {
                    if (isDispTotal) {
                        cells.copyRows(cellsTemplate, 6, countRow, 1);
                    } else {
                        if (i == childHierarchyList.size()) {
                            cells.copyRows(cellsTemplate, 8, countRow, 1);
                        } else {
                            cells.copyRows(cellsTemplate, 5, countRow, 1);
                        }
                    }
                    SummaryItemDetailDto level3 = childHierarchyList.get(i - 1);
                    cells.get(countRow, 0).setValue(!isPrintNameLv1 ? level1.getDisplayInfo().getName() : "");
                    isPrintNameLv1 = true;
                    cells.get(countRow, 1).setValue(!isPrintNameLv2 ? level2.getDisplayInfo().getName() : "");
                    isPrintNameLv2 = true;
                    cells.get(countRow, 2).setValue(level3.getDisplayInfo().getName());
                    val workingTimeMap3 = this.getWorkingTimeByDate(unit, level3.getVerticalTotalList());
                    for (int c = 3; c < maxDateRange + 3; c++) {
                        cells.get(countRow, c).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(workingTimeMap3.getOrDefault(headerList.get(c), "")) : workingTimeMap3.getOrDefault(headerList.get(c), ""));
                        setHorizontalAlignment(cells.get(countRow, c));
                    }
                    if (isDispTotal) {  // Tong chieu ngang level 3
                        cells.get(countRow, headerList.size() - 1).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(level3.getTotalPeriod()) : level3.getTotalPeriod());
                        setHorizontalAlignment(cells.get(countRow, headerList.size() - 1));
                    }
                    countRow++;
                }
                if (isDispTotal) { // Tong chieu doc level 3
                    cells.copyRows(cellsTemplate, 11, countRow, 1);
                    printTotalByVerticalOfEachLevel(cells, level2, maxDateRange, headerList, dispFormat, unit, countRow, 2, 1);
                    countRow++;
                }
                cells.merge(mergeIndexLv2, 1, isDispTotal ? countRow - mergeIndexLv2 - 1 : countRow - mergeIndexLv2, 1, true, true);
                setVerticalAlignment(cells.get(mergeIndexLv2, 2));
            }
            if (isDispTotal) { // Tong chieu doc level 2
                cells.copyRows(cellsTemplate, 21, countRow, 1);
                printTotalByVerticalOfEachLevel(cells, level1, maxDateRange, headerList, dispFormat, unit, countRow, 2, 0);
                countRow++;
            }
            cells.merge(mergeIndexLv1, 0, isDispTotal ? countRow - mergeIndexLv1 - 1 : countRow - mergeIndexLv1, 1, true, true);
            setVerticalAlignment(cells.get(mergeIndexLv1, 1));
            for (int j = 0; j < headerList.size(); j++) {
                setBorderBottomStyle(cells.get(countRow - 1, j));
            }
        }
        if (isDispTotal) { // Tong chieu doc cua level 1
            cells.copyRows(cellsTemplate, 37, countRow, 1);
            printAllTotalByVertical(cells, outputContent, maxDateRange, headerList, dispFormat, unit, countRow, 2);
            cells.merge(countRow, 0, 1, 3, true,true);
            setBorderStyleForTotal(cells.get(countRow, 3));
        }
    }

    private void printData4Level(Cells cellsTemplate, Cells cells, ManHourSummaryTableOutputContentDto outputContent, boolean isDispTotal, int maxDateRange, List<String> headerList, DisplayFormat dispFormat, int unit) throws Exception {
        List<SummaryItemDetailDto> itemDetails = outputContent.getItemDetails();
        int countRow = 3;
        for (SummaryItemDetailDto level1 : itemDetails) {
            boolean isPrintNameLv1 = false;
            int mergeIndexLv1 = countRow;
            for (SummaryItemDetailDto level2 : level1.getChildHierarchyList()) {
                boolean isPrintNameLv2 = false;
                int mergeIndexLv2 = countRow;
                for (SummaryItemDetailDto level3 : level2.getChildHierarchyList()) {
                    boolean isPrintNameLv3 = false;
                    int mergeIndexLv3 = countRow;
                    List<SummaryItemDetailDto> childHierarchyList = level3.getChildHierarchyList();
                    for (int i = 1; i <= childHierarchyList.size(); i++) {
                        if (isDispTotal) {
                            cells.copyRows(cellsTemplate, 4, countRow, 1);
                        } else {
                            if (i == childHierarchyList.size()) {
                                cells.copyRows(cellsTemplate, 8, countRow, 1);
                            } else {
                                cells.copyRows(cellsTemplate, 5, countRow, 1);
                            }
                        }

                        SummaryItemDetailDto level4 = childHierarchyList.get(i - 1);
                        cells.get(countRow, 0).setValue(!isPrintNameLv1 ? level1.getDisplayInfo().getName() : "");
                        isPrintNameLv1 = true;
                        cells.get(countRow, 1).setValue(!isPrintNameLv2 ? level2.getDisplayInfo().getName() : "");
                        isPrintNameLv2 = true;
                        cells.get(countRow, 2).setValue(!isPrintNameLv3 ? level3.getDisplayInfo().getName() : "");
                        isPrintNameLv3 = true;
                        cells.get(countRow, 3).setValue(level4.getDisplayInfo().getName());
                        val workingTimeMap4 = this.getWorkingTimeByDate(unit, level4.getVerticalTotalList());
                        for (int c = 4; c < maxDateRange + 4; c++) {
                            cells.get(countRow, c).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(workingTimeMap4.getOrDefault(headerList.get(c), "")) : workingTimeMap4.getOrDefault(headerList.get(c), ""));
                            setHorizontalAlignment(cells.get(countRow, c));
                        }
                        if (isDispTotal) {  // Tong chieu ngang level 4
                            cells.get(countRow, headerList.size() - 1).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(level4.getTotalPeriod()) : level4.getTotalPeriod());
                            setHorizontalAlignment(cells.get(countRow, headerList.size() - 1));
                        }
                        countRow++;
                    }
                    if (isDispTotal) { // Tong chieu doc level 4
                        cells.copyRows(cellsTemplate, 6, countRow, 1);
                        printTotalByVerticalOfEachLevel(cells, level3, maxDateRange, headerList, dispFormat, unit, countRow, 3, 2);
                        countRow++;
                    }
                    cells.merge(mergeIndexLv3, 2, isDispTotal ? countRow - mergeIndexLv3 - 1 : countRow - mergeIndexLv3, 1, true, true);
                    setVerticalAlignment(cells.get(mergeIndexLv3, 2));
                }
                if (isDispTotal) { // Tong chieu doc level 3
                    cells.copyRows(cellsTemplate, 11, countRow, 1);
                    printTotalByVerticalOfEachLevel(cells, level2, maxDateRange, headerList, dispFormat, unit, countRow, 3, 1);
                    countRow++;
                }
                cells.merge(mergeIndexLv2, 1, isDispTotal ? countRow - mergeIndexLv2 - 1 : countRow - mergeIndexLv2, 1, true, true);
                setVerticalAlignment(cells.get(mergeIndexLv2, 1));
            }
            if (isDispTotal) { // Tong chieu doc level 2
                cells.copyRows(cellsTemplate, 21, countRow, 1);
                printTotalByVerticalOfEachLevel(cells, level1, maxDateRange, headerList, dispFormat, unit, countRow, 3, 0);
                countRow++;
            }
            cells.merge(mergeIndexLv1, 0, isDispTotal ? countRow - mergeIndexLv1 - 1 : countRow - mergeIndexLv1, 1, true, true);
            setVerticalAlignment(cells.get(mergeIndexLv1, 0));
            for (int j = 0; j < headerList.size(); j++) {
                setBorderBottomStyle(cells.get(countRow - 1, j));
            }
        }
        if (isDispTotal) { // Tong chieu doc cua level 1
            cells.copyRows(cellsTemplate, 37, countRow, 1);
            printAllTotalByVertical(cells, outputContent, maxDateRange, headerList, dispFormat, unit, countRow, 3);
            cells.merge(countRow, 0, 1, 4, true,true);
        }
    }

    // Total of each column of each level by vertical
    private void printTotalByVerticalOfEachLevel(Cells cells, SummaryItemDetailDto summaryItemDetail, int maxDateRange, List<String> headerList, DisplayFormat dispFormat, int unit,
                                                 int row, int index, int columnNameIndex) {
        cells.get(row, columnNameIndex).setValue(summaryItemDetail.getDisplayInfo().getName() + TextResource.localize(TOTAL));
        for (int t = 1; t <= maxDateRange; t++) {
            val mapTotal = this.getWorkingTimeByDate(unit, summaryItemDetail.getVerticalTotalList());
            setHorizontalAlignment(cells.get(row, t + index));
            cells.get(row, t + index).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(mapTotal.getOrDefault(headerList.get(t + index), "")) : mapTotal.getOrDefault(headerList.get(t + index), ""));
        }
        cells.get(row, headerList.size() - 1).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(summaryItemDetail.getTotalPeriod()) : summaryItemDetail.getTotalPeriod());
    }

    // All total by vertical
    private void printAllTotalByVertical(Cells cells, ManHourSummaryTableOutputContentDto outputContent, int maxDateRange, List<String> headerList, DisplayFormat dispFormat,
                                         int unit, int row, int index) {
        cells.get(row, 0).setValue(TextResource.localize(VERTICAL_TOTAL));
        for (int t = 1; t <= maxDateRange; t++) {
            val mapTotal = this.getWorkingTimeByDate(unit, outputContent.getVerticalTotalValues());
            setHorizontalAlignment(cells.get(row, t + index));
            cells.get(row, t + index).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(mapTotal.getOrDefault(headerList.get(t + index), "")) : mapTotal.getOrDefault(headerList.get(t + index), ""));
        }
        cells.get(row, headerList.size() - 1).setValue(dispFormat == DisplayFormat.MINUTE ? removeComma(outputContent.getTotalPeriod()) : outputContent.getTotalPeriod());
    }

    private int checkTotalColumn(int maxColumnTemplate, int countColumn) {
        int countColumnNeedHandle;
        if (countColumn < maxColumnTemplate) { // Thừa: > 0
            countColumnNeedHandle = maxColumnTemplate - countColumn;
        } else if (countColumn > maxColumnTemplate) { // Thiếu: < 0
            countColumnNeedHandle = maxColumnTemplate - countColumn;
        } else {
            countColumnNeedHandle = 0;
        }

        return countColumnNeedHandle;
    }

    private Map<String, String> getWorkingTimeByDate(int unit, List<VerticalValueDailyDto> lstValueDaily) {
        Map<String, String> map = new HashMap<>();
        if (unit == TotalUnit.DATE.value)
            lstValueDaily.forEach(d -> map.put(d.getDate(), d.getWorkingHours()));
        else
            lstValueDaily.forEach(d -> map.put(d.getYearMonth().substring(0, 4) + "/" + d.getYearMonth().substring(4), d.getWorkingHours()));

        return map;
    }

    private List<String> getHeaderList(ManHourPeriod period, ManHourSummaryTableFormatDto detailSetting, boolean isDispTotal) {
        List<String> lstHeader = new ArrayList<>();
        List<TaskFrameSettingDto> taskFrameSettings = manHoursListScreenQuery.getManHoursList().getTaskFrameSettings();
        // Sort before adding
        val sortedList = detailSetting.getSummaryItems().stream().sorted(Comparator.comparing(SummaryItemDto::getHierarchicalOrder)).collect(Collectors.toList());
        // Add code & name to header
        for (SummaryItemDto item : sortedList) {
            lstHeader.add(this.mapTaskNameByType(item, taskFrameSettings));
        }

        // Add date/yearMonth list to header
        if (detailSetting.getTotalUnit() == TotalUnit.DATE.value)
            period.getDateList().forEach(date -> lstHeader.add(date.toString()));
        else
            period.getYearMonthList().forEach(ym -> lstHeader.add(toYearMonthString(ym)));

        // Add horizontal total to header
        if (isDispTotal) lstHeader.add(TextResource.localize(HORIZONTAL_TOTAL));

        return lstHeader;
    }

    @SuppressWarnings("Duplicates")
    private String mapTaskNameByType(SummaryItemDto item, List<TaskFrameSettingDto> taskFrameSettings) {
        if (taskFrameSettings.isEmpty()) return item.getItemTypeName();
        switch (item.getItemType()) {
            case 3:
                return taskFrameSettings.stream().filter(x -> x.getTaskFrameNo() == 1).map(TaskFrameSettingDto::getTaskFrameName).findFirst().orElse(item.getItemTypeName());
            case 4:
                return taskFrameSettings.stream().filter(x -> x.getTaskFrameNo() == 2).map(TaskFrameSettingDto::getTaskFrameName).findFirst().orElse(item.getItemTypeName());
            case 5:
                return taskFrameSettings.stream().filter(x -> x.getTaskFrameNo() == 3).map(TaskFrameSettingDto::getTaskFrameName).findFirst().orElse(item.getItemTypeName());
            case 6:
                return taskFrameSettings.stream().filter(x -> x.getTaskFrameNo() == 4).map(TaskFrameSettingDto::getTaskFrameName).findFirst().orElse(item.getItemTypeName());
            case 7:
                return taskFrameSettings.stream().filter(x -> x.getTaskFrameNo() == 5).map(TaskFrameSettingDto::getTaskFrameName).findFirst().orElse(item.getItemTypeName());
            default:
                return item.getItemTypeName();
        }
    }

    private String removeComma(String total){
        return total.contains(",") ? total.replace(",","") : total;
    }

    private void setVerticalAlignment(Cell cell) {
        Style style = cell.getStyle();
        style.setVerticalAlignment(TextAlignmentType.TOP);
        cell.setStyle(style);
    }

    private void setBorderStyleForTotal(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
        style.setVerticalAlignment(TextAlignmentType.LEFT);
        cell.setStyle(style);
    }

    private void setBorderBottomStyle(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        cell.setStyle(style);
    }

    private void setHorizontalAlignment(Cell cell) {
        Style style = cell.getStyle();
        style.setHorizontalAlignment(TextAlignmentType.RIGHT);
        cell.setStyle(style);
    }

    // Convert YearMonth to String with format: yyyy/MM
    private String toYearMonthString(YearMonth yearMonth) {
        return String.format("%04d/%02d", yearMonth.year(), yearMonth.month());
    }
}
