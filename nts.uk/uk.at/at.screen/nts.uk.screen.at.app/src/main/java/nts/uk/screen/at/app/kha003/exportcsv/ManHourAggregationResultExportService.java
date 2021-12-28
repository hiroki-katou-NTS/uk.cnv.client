package nts.uk.screen.at.app.kha003.exportcsv;

import lombok.val;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.workrecord.workmanagement.manhoursummarytable.TotalUnit;
import nts.uk.screen.at.app.kha003.*;
import nts.uk.screen.at.app.kha003.a.ManHoursListScreenQuery;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.csv.CSVFileData;
import nts.uk.shr.infra.file.csv.CSVReportGenerator;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ManHourAggregationResultExportService extends ExportService<ManHourDataSummaryQuery> {
    @Inject
    private CSVReportGenerator generator;

    private static final String DATE_FORMAT = "yyyy/MM/dd";

    private static final String VERTICAL_TOTAL = "KHA003_99";

    private static final String HORIZONTAL_TOTAL = "KHA003_98";

    private static final String TOTAL = "KHA003_100";

    private static final String FILE_EXTENSION = ".csv";

    @Inject
    private ManHoursListScreenQuery manHoursListScreenQuery;

    @Override
    protected void handle(ExportServiceContext<ManHourDataSummaryQuery> exportServiceContext) {
        val query = exportServiceContext.getQuery();
        if (query == null) return;

        val formatSetting = query.getSummaryTableFormat();
        val outputContent = query.getOutputContent();
        val totalUnit = formatSetting.getTotalUnit();
        val dispFormat = formatSetting.getDisplayFormat();
        int maxRangeDate = totalUnit == TotalUnit.DATE.value ? query.getPeriod().getDateList().size() : query.getPeriod().getYearMonthList().size();
        String dateRange = totalUnit == TotalUnit.DATE.value
                ? query.getPeriod().getDatePeriod().start().toString(DATE_FORMAT) + "　～　" + query.getPeriod().getDatePeriod().end().toString(DATE_FORMAT)
                : query.getPeriod().getYearMonthPeriod().start().toString() + "　～　" + query.getPeriod().getYearMonthPeriod().end().toString();

        // Flag display total  (USE = 1)
        val isDisplayTotal = formatSetting.getDispHierarchy() == 1;

        // create Header list
        List<String> headerList = this.createTextHeader(query, isDisplayTotal, dateRange);

        // Add data source
        List<Map<String, Object>> dataSource = new ArrayList<>();
        // Handle data on needed data to export
        this.dataOutputProcessing(outputContent, isDisplayTotal, totalUnit, dataSource, headerList, maxRangeDate, dispFormat);

        // Execute export
        String fileName = formatSetting.getName() + "_" + GeneralDateTime.now().toString("yyyyMMddHHmmss");
        CSVFileData fileData = new CSVFileData(
                fileName + FILE_EXTENSION, headerList, dataSource);
        generator.generate(exportServiceContext.getGeneratorContext(), fileData);
    }

    private void dataOutputProcessing(ManHourSummaryTableOutputContentDto outputContent, boolean isDispTotal, int unit, List<Map<String, Object>> dataSource,
                                      List<String> headerList, int maxRangeDate, int dispFormat) {
        for (SummaryItemDetailDto level1 : outputContent.getItemDetails()) {
            if (level1.getChildHierarchyList().isEmpty()) {
                Map<String, Object> row1 = new HashMap<>();
                row1.put(headerList.get(0), level1.getDisplayInfo().getCode());
                row1.put(headerList.get(1), level1.getDisplayInfo().getName());

                val workingTimeMap1 = this.getWorkingTimeByDate(unit, level1.getVerticalTotalList());
                for (int i = 2; i < maxRangeDate + 2; i++) {
                    row1.put(headerList.get(i), dispFormat == 2 ? removeComma(workingTimeMap1.getOrDefault(headerList.get(i), "")) : workingTimeMap1.getOrDefault(headerList.get(i), ""));
                }
                if (isDispTotal) {  // Tong chieu ngang
                    row1.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level1.getTotalPeriod()) : level1.getTotalPeriod());
                }
                dataSource.add(row1);
            } else {
                for (SummaryItemDetailDto level2 : level1.getChildHierarchyList()) {
                    if (level2.getChildHierarchyList().isEmpty()) {
                        Map<String, Object> row2 = new HashMap<>();
                        row2.put(headerList.get(0), level1.getDisplayInfo().getCode());
                        row2.put(headerList.get(1), level1.getDisplayInfo().getName());
                        row2.put(headerList.get(2), level2.getDisplayInfo().getCode());
                        row2.put(headerList.get(3), level2.getDisplayInfo().getName());

                        for (int i = 4; i < maxRangeDate + 4; i++) {
                            val workingTimeMap2 = this.getWorkingTimeByDate(unit, level2.getVerticalTotalList());
                            row2.put(headerList.get(i), dispFormat == 2 ? removeComma(workingTimeMap2.getOrDefault(headerList.get(i), "")) : workingTimeMap2.getOrDefault(headerList.get(i), ""));
                        }
                        if (isDispTotal) {  // Tong chieu ngang
                            row2.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level2.getTotalPeriod()) : level2.getTotalPeriod());
                        }
                        dataSource.add(row2);
                    } else {
                        for (SummaryItemDetailDto level3 : level2.getChildHierarchyList()) {
                            if (level3.getChildHierarchyList().isEmpty()) {
                                Map<String, Object> row3 = new HashMap<>();
                                row3.put(headerList.get(0), level1.getDisplayInfo().getCode());
                                row3.put(headerList.get(1), level1.getDisplayInfo().getName());
                                row3.put(headerList.get(2), level2.getDisplayInfo().getCode());
                                row3.put(headerList.get(3), level2.getDisplayInfo().getName());
                                row3.put(headerList.get(4), level3.getDisplayInfo().getCode());
                                row3.put(headerList.get(5), level3.getDisplayInfo().getName());

                                for (int i = 6; i < maxRangeDate + 6; i++) {
                                    val workingTimeMap3 = this.getWorkingTimeByDate(unit, level3.getVerticalTotalList());
                                    row3.put(headerList.get(i), dispFormat == 2 ? removeComma(workingTimeMap3.getOrDefault(headerList.get(i), "")) : workingTimeMap3.getOrDefault(headerList.get(i), ""));
                                }
                                if (isDispTotal) { // Tong chieu ngang
                                    row3.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level3.getTotalPeriod()) : level3.getTotalPeriod());
                                }
                                dataSource.add(row3);
                            } else {
                                for (SummaryItemDetailDto level4 : level3.getChildHierarchyList()) {
                                    Map<String, Object> row4 = new HashMap<>();
                                    row4.put(headerList.get(0), level1.getDisplayInfo().getCode());
                                    row4.put(headerList.get(1), level1.getDisplayInfo().getName());
                                    row4.put(headerList.get(2), level2.getDisplayInfo().getCode());
                                    row4.put(headerList.get(3), level2.getDisplayInfo().getName());
                                    row4.put(headerList.get(4), level3.getDisplayInfo().getCode());
                                    row4.put(headerList.get(5), level3.getDisplayInfo().getName());
                                    row4.put(headerList.get(6), level4.getDisplayInfo().getCode());
                                    row4.put(headerList.get(7), level4.getDisplayInfo().getName());

                                    for (int i = 8; i < maxRangeDate + 8; i++) {
                                        val workingTimeMap4 = this.getWorkingTimeByDate(unit, level4.getVerticalTotalList());
                                        row4.put(headerList.get(i), dispFormat == 2 ? removeComma(workingTimeMap4.getOrDefault(headerList.get(i), "")) : workingTimeMap4.getOrDefault(headerList.get(i), ""));
                                    }
                                    if (isDispTotal) { // Tong chieu ngang
                                        row4.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level4.getTotalPeriod()) : level4.getTotalPeriod());
                                    }
                                    dataSource.add(row4);
                                }
                                if (isDispTotal) {  // Tong level 4 theo chieu doc
                                    Map<String, Object> rowTotalLv4 = new HashMap<>();
                                    rowTotalLv4.put(headerList.get(5), level3.getDisplayInfo().getName() + TextResource.localize(TOTAL));
                                    for (int i = 8; i < headerList.size(); i++) {
                                        val mapTotal4 = this.getWorkingTimeByDate(unit, level3.getVerticalTotalList());
                                        rowTotalLv4.put(headerList.get(i), dispFormat == 2 ? removeComma(mapTotal4.getOrDefault(headerList.get(i), "")) : mapTotal4.getOrDefault(headerList.get(i), ""));
                                    }
                                    rowTotalLv4.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level3.getTotalPeriod()) : level3.getTotalPeriod());
                                    dataSource.add(rowTotalLv4);
                                }
                            }
                        }
                        if (isDispTotal) {  // Tong level 3 theo chieu doc
                            Map<String, Object> rowTotalLv3 = new HashMap<>();
                            rowTotalLv3.put(headerList.get(3), level2.getDisplayInfo().getName() + TextResource.localize(TOTAL));
                            for (int i = 6; i < headerList.size(); i++) {
                                val mapTotal2 = this.getWorkingTimeByDate(unit, level2.getVerticalTotalList());
                                rowTotalLv3.put(headerList.get(i), dispFormat == 2 ? removeComma(mapTotal2.getOrDefault(headerList.get(i), "")) : mapTotal2.getOrDefault(headerList.get(i), ""));
                            }
                            rowTotalLv3.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level2.getTotalPeriod()) : level2.getTotalPeriod());
                            dataSource.add(rowTotalLv3);
                        }
                    }
                }
                if (isDispTotal) { // Tong level 2 theo chieu doc
                    Map<String, Object> rowTotalLv2 = new HashMap<>();
                    rowTotalLv2.put(headerList.get(1), level1.getDisplayInfo().getName() + TextResource.localize(TOTAL));
                    for (int i = 4; i < headerList.size(); i++) {
                        val mapTotal2 = this.getWorkingTimeByDate(unit, level1.getVerticalTotalList());
                        rowTotalLv2.put(headerList.get(i), dispFormat == 2 ? removeComma(mapTotal2.getOrDefault(headerList.get(i), "")) : mapTotal2.getOrDefault(headerList.get(i), ""));
                    }
                    rowTotalLv2.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(level1.getTotalPeriod()) : level1.getTotalPeriod());
                    dataSource.add(rowTotalLv2);
                }
            }
        }
        if (isDispTotal) {  // Tong tat ca cac level theo chieu doc
            Map<String, Object> rowTotal = new HashMap<>();
            rowTotal.put(headerList.get(1), TextResource.localize(VERTICAL_TOTAL));
            for (int i = 2; i < headerList.size(); i++) {
                val mapTotal = this.getWorkingTimeByDate(unit, outputContent.getVerticalTotalValues());
                rowTotal.put(headerList.get(i), dispFormat == 2 ? removeComma(mapTotal.getOrDefault(headerList.get(i), "")) : mapTotal.getOrDefault(headerList.get(i), ""));
            }
            rowTotal.put(headerList.get(headerList.size() - 1), dispFormat == 2 ? removeComma(outputContent.getTotalPeriod()) : outputContent.getTotalPeriod());
            dataSource.add(rowTotal);
        }
    }

    private Map<String, String> getWorkingTimeByDate(int unit, List<VerticalValueDailyDto> lstValueDaily) {
        Map<String, String> map = new HashMap<>();
        if (unit == TotalUnit.DATE.value)
            lstValueDaily.forEach(d -> map.put(d.getDate(), d.getWorkingHours()));
        else
            lstValueDaily.forEach(d -> map.put(d.getYearMonth().substring(0, 4) + "/" + d.getYearMonth().substring(4), d.getWorkingHours()));

        return map;
    }

    /**
     * create text Header
     *
     * @param query
     * @param isDispTotal
     * @return
     */
    private List<String> createTextHeader(ManHourDataSummaryQuery query, boolean isDispTotal, String dateRange) {
        val formatSetting = query.getSummaryTableFormat();
        List<TaskFrameSettingDto> taskFrameSettings = manHoursListScreenQuery.getManHoursList().getTaskFrameSettings();
        List<String> lstHeader = new ArrayList<>();
        // Sort before adding
        val sortedList = formatSetting.getSummaryItems().stream().sorted(Comparator.comparing(SummaryItemDto::getHierarchicalOrder)).collect(Collectors.toList());
        // Add code & name to header
        for (int i = 0; i < sortedList.size(); i++) {
            SummaryItemDto item = sortedList.get(i);
            if (i == 0) {
                val name = query.getSummaryTableFormat().getName();
                lstHeader.add(name + "\n" + dateRange + "\n" + TextResource.localize(getCodeHeader(i + 1)));
            } else {
                lstHeader.add(TextResource.localize(getCodeHeader(i + 1)));
            }
            lstHeader.add(this.mapTaskNameByType(item, taskFrameSettings));
        }

        // Add date/yearMonth list to header
        if (formatSetting.getTotalUnit() == TotalUnit.DATE.value)
            query.getPeriod().getDateList().forEach(date -> lstHeader.add(date.toString()));
        else
            query.getPeriod().getYearMonthList().forEach(ym -> lstHeader.add(yearMonthToString(ym)));

        // Add horizontal total to header
        if (isDispTotal) {
            lstHeader.add(TextResource.localize(HORIZONTAL_TOTAL));
        }
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

    private String getCodeHeader(int num) {
        switch (num) {
            case 1:
                return "KHA003_117";
            case 2:
                return "KHA003_118";
            case 3:
                return "KHA003_119";
            case 4:
                return "KHA003_120";
        }
        return "";
    }

    /**
     * convert YearMonth to String with format: yyyy/MM
     *
     * @param yearMonth
     * @return
     */
    private String yearMonthToString(YearMonth yearMonth) {
        return String.format("%04d/%02d", yearMonth.year(), yearMonth.month());
    }

    /**
     * Convert to minute (HH:mm)
     *
     * @param value
     * @return
     */
    private String toMinute(int value) {
        val minuteAbs = Math.abs(value);
        int hours = minuteAbs / 60;
        int minutes = minuteAbs % 60;
        return (value < 0 ? "-" : "") + String.format("%d:%02d", hours, minutes);
    }

}
