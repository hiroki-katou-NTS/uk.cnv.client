package nts.uk.file.at.infra.arbitraryperiodsummarytable;

import com.aspose.cells.*;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.function.app.query.arbitraryperiodsummarytable.*;
import nts.uk.ctx.at.function.dom.arbitraryperiodsummarytable.OutputSettingOfArbitrary;
import nts.uk.ctx.at.function.dom.outputitemsofworkstatustable.enums.CommonAttributesOfForms;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.PrimitiveValueOfAttendanceItem;
import nts.uk.ctx.sys.gateway.dom.adapter.company.CompanyBsImport;
import nts.uk.file.at.app.export.arbitraryperiodsummarytable.ArbitraryPeriodSummaryDto;
import nts.uk.file.at.app.export.arbitraryperiodsummarytable.ArbitraryPeriodSummaryTableGenerator;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AposeArbitraryPeriodSummaryTableGenerator extends AsposeCellsReportGenerator implements ArbitraryPeriodSummaryTableGenerator {
    private static final String TEMPLATE_FILE_ADD = "report/KWR07.xlsx";
    private static final String EXCEL_EXT = ".xlsx";
    private static final String PRINT_AREA = "";
    private static final int MAX_ITEM_ONE_LINE = 20;
    private static final String FORMAT_DATE = "yyyy/MM/dd";


    @Override
    public void generate(FileGeneratorContext generatorContext, ArbitraryPeriodSummaryDto dataSource) {
        try {
            AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE_ADD);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            OutputSettingOfArbitrary ofArbitrary = dataSource.getOfArbitrary();
            String title = ofArbitrary != null ? ofArbitrary.getName().v() : "";

            Worksheet worksheetTemplate = worksheets.get(0);
            Worksheet worksheet = worksheets.get(1);

            worksheet.setName(title);

            settingPage(worksheet, dataSource, title);
            printContents(worksheetTemplate, worksheet, dataSource);
            worksheets.removeAt(0);
            worksheets.setActiveSheetIndex(0);
            reportContext.processDesigner();

            String fileName = title + "_" + GeneralDateTime.now().toString("yyyyMMddHHmmss");
            reportContext.saveAsExcel(this.createNewFile(generatorContext, fileName + EXCEL_EXT));


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void settingPage(Worksheet worksheet,
                             ArbitraryPeriodSummaryDto dataSource, String title) {
        CompanyBsImport companyBsImport = dataSource.getCompanyInfo();
        String companyName = companyBsImport.getCompanyName();
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);

        pageSetup.setHeader(0, "&7&\"ＭＳ フォントサイズ\"" + companyName);
        pageSetup.setHeader(1, "&9&\"ＭＳ フォントサイズ\"" + title);

        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy/MM/dd  H:mm", Locale.JAPAN);
        pageSetup.setHeader(2,
                "&9&\"MS フォントサイズ\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\n" +
                        TextResource.localize("page") + " &P");
        pageSetup.setZoom(100);
    }

    private void printContents(Worksheet worksheetTemplate, Worksheet worksheet, ArbitraryPeriodSummaryDto dataSource) {
        try {
            HorizontalPageBreakCollection pageBreaks = worksheet.getHorizontalPageBreaks();
            DetailOfArbitrarySchedule data = dataSource.getContent();
            if (data != null) {
                val query = dataSource.getQuery();
                List<AttendanceItemDisplayContents> contentsList = data.getContentsList();
                List<AttendanceDetailDisplayContents> detailDisplayContents = data.getDetailDisplayContents();
                List<WorkplaceTotalDisplayContent> totalDisplayContents = data.getTotalDisplayContents();
                List<DisplayContent> totalAll = data.getTotalAll();
                List<CumulativeWorkplaceDisplayContent> cumulativeWorkplaceDisplayContents =
                        data.getCumulativeWorkplaceDisplayContents();
                DatePeriod period = dataSource.getPeriod();
                int count = 0;
                Cells cellsTemplate = worksheetTemplate.getCells();
                Cells cells = worksheet.getCells();
                boolean pageNew = false;
                for (int i = 0; i < detailDisplayContents.size(); i++) {
                    if (i == 0) {
                        // C1_1
                        printInfo(worksheetTemplate, worksheet, contentsList, period);
                        count += 5;
                    }
                    if (pageNew) {
                        cells.copyRows(cells, 1, count, 4);
                        pageNew = false;
                        count += 4;
                    }
                    val content = detailDisplayContents.get(i);
                    int wplHierarchy = Integer.parseInt(content.getHierarchyCode());
                    int pageBreakWplHierarchy = query.getPageBreakWplHierarchy();
                    boolean isPageBreakByWpl = query.isPageBreakByWpl();
                    if ((isPageBreakByWpl && i >= 1) && (wplHierarchy > pageBreakWplHierarchy)) {
                        pageBreaks.add(count);
                        pageNew = true;
                    }
                    List<DisplayedEmployee> listDisplayedEmployees = content.getListDisplayedEmployees();
                    cells.copyRow(cellsTemplate, 5, count);
                    //D1_1
                    cells.get(count, 0).setValue(TextResource.localize("KWR007_302")
                            + " " + content.getWorkplaceCd() + " " + content.getWorkplaceName());
                    count += 1;
                    for (int j = 0; j < listDisplayedEmployees.size(); j++) {
                        val item = listDisplayedEmployees.get(j);
                        if (j % 2 == 0) {
                            cells.copyRows(cellsTemplate, 6, count, 2);

                        } else {
                            cells.copyRows(cellsTemplate, 8, count, 2);
                        }
                        cells.get(count, 0).setValue(
                                item.getEmployeeCode() + " " + item.getEmployeeName());
                        val contents = item.getContentList();
                        for (int k = 0; k < MAX_ITEM_ONE_LINE; k++) {
                            // Line 01:
                            cells.get(count, 1 + k).setValue(contents.get(k).getValue());
                            // Line 02:
                            if (contents.size() >= MAX_ITEM_ONE_LINE)
                                cells.get(count + 1, 1 + k).setValue(contents.get(20 + k).getValue());
                        }
                        count += 2;
                    }
                    if (query.isWorkplaceTotal()) {
                        val sumByWplOpt = totalDisplayContents
                                .stream().filter(e -> e.getWorkplaceId().equals(content.getWorkplaceId())).findFirst();
                        if (sumByWplOpt.isPresent()) {
                            val sumByWpl = sumByWplOpt.get();
                            cells.copyRows(cellsTemplate, 10, count, 2);
                            cells.get(count, 0).setValue(TextResource.localize("KWR007_304"));
                            val listSum = sumByWpl.getListOfWorkplaces();
                            if (listSum != null)
                                for (int k = 0; k < MAX_ITEM_ONE_LINE; k++) {
                                    // Line 01:
                                    cells.get(count, 1 + k).setValue(listSum.get(k).getValue());
                                    // Line 02:
                                    if (listSum.size() >= MAX_ITEM_ONE_LINE)
                                        cells.get(count + 1, 1 + k).setValue(listSum.get(20 + k).getValue());
                                }
                            count += 2;
                        }
                    }
                }
                if (query.isCumulativeWorkplace()) {

                    for (int i = 0; i < cumulativeWorkplaceDisplayContents.size(); i++) {
                        val item = cumulativeWorkplaceDisplayContents.get(i);
                        val listValue = item.getListOfWorkplaces();
                        cells.copyRows(cellsTemplate, 10, count, 2);
                        cells.get(count, 0).setValue(TextResource.localize("KWR007_305",item.getHierarchyCode()));
                        for (int k = 0; k < MAX_ITEM_ONE_LINE; k++) {
                            // Line 01:
                            cells.get(count, 1 + k).setValue(listValue.get(k).getValue());
                            // Line 02:
                            if (listValue.size() >= MAX_ITEM_ONE_LINE)
                                cells.get(count + 1, 1 + k).setValue(listValue.get(20 + k).getValue());
                        }
                        count += 2;
                    }

                }
                if (query.isTotal()) {
                    cells.copyRows(cellsTemplate, 10, count, 2);
                    cells.get(count, 0).setValue(TextResource.localize("KWR007_306"));
                    for (int k = 0; k < MAX_ITEM_ONE_LINE; k++) {
                        // Line 01:
                        cells.get(count, 1 + k).setValue(totalAll.get(k).getValue());
                        // Line 02:
                        if (totalAll.size() >= MAX_ITEM_ONE_LINE)
                            cells.get(count + 1, 1 + k).setValue(totalAll.get(20 + k).getValue());
                    }

                }

                PageSetup pageSetup = worksheet.getPageSetup();
                pageSetup.setPrintArea(PRINT_AREA + count);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }

    private void printInfo(Worksheet worksheetTemplate, Worksheet worksheet,
                           List<AttendanceItemDisplayContents> contentsList, DatePeriod datePeriod) throws Exception {

        Cells cellsTemplate = worksheetTemplate.getCells();
        Cells cells = worksheet.getCells();
        cells.copyRows(cellsTemplate, 0, 0, 5);
        cells.clearContents(0, 0, cells.getMaxRow(), cells.getMaxColumn());

        cells.get(0, 0).setValue(TextResource.localize("KWR007_301")
                + datePeriod.start().toString(FORMAT_DATE) + TextResource.localize("KWR007_307")
                + datePeriod.end().toString(FORMAT_DATE));

        cells.get(1, 0).setValue(TextResource.localize("KWR007_302"));
        for (int i = 0; i < MAX_ITEM_ONE_LINE; i++) {
            // Line 01:
            cells.get(1, 1 + i).setValue(contentsList.get(i).getAttendanceName());
            // Line 02:
            if (contentsList.size() >= MAX_ITEM_ONE_LINE)
                cells.get(3, 1 + i).setValue(contentsList.get(20 + i).getAttendanceName());
        }
    }

    /**
     * Format value
     */
    private String formatValue(Double valueDouble, String valueString, CommonAttributesOfForms attributes, Boolean isZeroDisplay) {
        String rs = "";
        switch (attributes) {

            case WORKING_HOURS:
                rs = valueString;
                break;
            case WORK_TYPE:
                rs = valueString;
                break;
            case OTHER_CHARACTER_NUMBER:
                rs = valueString;
                break;
            case OTHER_CHARACTERS:
                rs = valueString;
                break;
            case OTHER_NUMERICAL_VALUE:
                rs = valueString;
                break;

            case TIME_OF_DAY:
                if (valueDouble != null) {
                    rs = convertToTime((int) valueDouble.intValue());
                }

                break;
            case DAYS:
                if (valueDouble != null) {
                    if (valueDouble != 0 || isZeroDisplay) {
                        DecimalFormat formatter2 = new DecimalFormat("#.#");
                        rs = formatter2.format(valueDouble) + TextResource.localize("KWR_1");
                    }
                }
                break;
            case TIME:
                if (valueDouble != null) {
                    val minute = (int) valueDouble.intValue();
                    if (minute != 0 || isZeroDisplay) {
                        rs = convertToTime(minute);
                    }
                }
                break;
            case AMOUNT_OF_MONEY:
                if (valueDouble != null) {
                    if (valueDouble != 0 || isZeroDisplay) {
                        DecimalFormat formatter3 = new DecimalFormat("#,###");
                        rs = formatter3.format((int) valueDouble.intValue()) + TextResource.localize("KWR_3");
                    }
                }
                break;
            case NUMBER_OF_TIMES:
                if (valueDouble != null) {
                    if (valueDouble != 0 || isZeroDisplay) {
                        DecimalFormat formatter1 = new DecimalFormat("#.#");
                        rs = formatter1.format(valueDouble) + TextResource.localize("KWR_2");
                    }
                }

                break;
        }
        return rs;
    }

    /**
     * Convert minute to HH:mm
     */
    private String convertToTime(int minute) {
        val minuteAbs = Math.abs(minute);
        int hours = minuteAbs / 60;
        int minutes = minuteAbs % 60;
        return (minute < 0 ? "-" : "") + String.format("%d:%02d", hours, minutes);
    }

    private boolean checkCode(boolean isCode, Integer primitive) {
        val listAtt = Arrays.asList(
                PrimitiveValueOfAttendanceItem.WORKPLACE_CD,
                PrimitiveValueOfAttendanceItem.POSITION_CD,
                PrimitiveValueOfAttendanceItem.CLASSIFICATION_CD,
                PrimitiveValueOfAttendanceItem.EMP_CTG_CD,
                PrimitiveValueOfAttendanceItem.WORK_TYPE_DIFFERENT_CD);

        return primitive != null && isCode && listAtt.stream().anyMatch(x -> x.value == primitive);
    }
}
