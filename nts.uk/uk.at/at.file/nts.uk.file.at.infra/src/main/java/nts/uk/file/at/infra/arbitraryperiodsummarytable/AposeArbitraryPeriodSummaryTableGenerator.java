package nts.uk.file.at.infra.arbitraryperiodsummarytable;

import com.aspose.cells.*;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDateTime;
import nts.arc.time.YearMonth;
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
    private static final String TEMPLATE_FILE_ADD = "report/KWR007.xlsx";
    private static final String EXCEL_EXT = ".xlsx";
    private static final String PRINT_AREA = "";


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

    private void printContents(Worksheet worksheetTemplate, Worksheet worksheet, ArbitraryPeriodSummaryDto dataSource) throws Exception {
        HorizontalPageBreakCollection pageBreaks = worksheet.getHorizontalPageBreaks();
        DetailOfArbitrarySchedule data = dataSource.getContent();
        if (data != null) {
            List<AttendanceItemDisplayContents> contentsList = data.getContentsList();
            List<AttendanceDetailDisplayContents> detailDisplayContents = data.getDetailDisplayContents();

            List<WorkplaceTotalDisplayContent> totalDisplayContents = data.getTotalDisplayContents();

            List<DisplayContent> totalAll = data.getTotalAll();

            List<CumulativeWorkplaceDisplayContent> cumulativeWorkplaceDisplayContents =
                    data.getCumulativeWorkplaceDisplayContents();
            int count = 0;
            int itemOnePage = 0;
            Cells cellsTemplate = worksheet.getCells();
            Cells cells = worksheet.getCells();
            printInfor(worksheetTemplate, worksheet, 2, contentsList);
            for (int i = 0; i < detailDisplayContents.size(); i++) {
                val content = detailDisplayContents.get(i);
                if (i >= 1) {
                    itemOnePage = 0;
                    pageBreaks.add(count);
                    cells.copyRows(cellsTemplate, 0, count,4);
                    cells.copyRow(cells, 1, count + 1);
                    cells.copyRow(cells, 2, count + 2);
                    cells.clearContents(count, 0, cells.getMaxRow(), 16);
                }
                cells.get(count, 0).setValue(TextResource.localize("")
                        + "　" + content.getWorkplaceCd() + "　" + content.getWorkplaceName());
                //cells.get(count + 1, 0).setValue(TextResource.localize("") + "　" + content.getEmployeeCode() + "　" + content.getEmployeeName());
            }
            PageSetup pageSetup = worksheet.getPageSetup();
            pageSetup.setPrintArea(PRINT_AREA + count);
        }

    }

    private void printInfor(Worksheet worksheetTemplate, Worksheet worksheet, int rowCount, List<AttendanceItemDisplayContents> contentsList) {
        Cells cells = worksheet.getCells();
        cells.get(rowCount, 2).setValue("");
        cells.get(rowCount, 14).setValue(TextResource.localize(""));
    }

    /**
     * Display year month yyyy/MM
     */
    private String toYearMonthString(YearMonth yearMonth) {
        return String.format("%04d/%02d", yearMonth.year(), yearMonth.month());
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
