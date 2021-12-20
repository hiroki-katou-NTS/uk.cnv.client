package nts.uk.file.at.infra.schedule.personalschedulebyworkplace;

import com.aspose.cells.*;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.system.ServerSystemProperties;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.EstimatedSalary;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.personcounter.WorkClassificationAsAggregationTarget;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.LaborCostAggregationUnit;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.LaborCostItemType;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.aggregationprocess.workplacecounter.NumberOfPeopleByEachWorkMethod;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.PersonalCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.WorkplaceCounterCategory;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.LaborCostAndTime;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.WorkplaceCounterLaborCostAndTime;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.laborcostandtime.WorkplaceCounterLaborCostAndTimeRepo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.timescounting.TimesNumberCounterSelection;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.timescounting.TimesNumberCounterSelectionRepo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.tally.timescounting.TimesNumberCounterType;
import nts.uk.ctx.at.aggregation.dom.scheduletable.*;
import nts.uk.ctx.at.schedule.dom.budget.external.BudgetAtr;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudget;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudgetCd;
import nts.uk.ctx.at.schedule.dom.budget.external.ExternalBudgetRepository;
import nts.uk.ctx.at.schedule.dom.budget.external.actualresults.ExternalBudgetValues;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AggregationUnitOfLaborCosts;
import nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily.AttendanceTimesForAggregation;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimes;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.TotalTimesRepository;
import nts.uk.ctx.at.shared.dom.scherec.totaltimes.UseAtr;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.*;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceDayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.bs.employee.dom.classification.Classification;
import nts.uk.ctx.bs.employee.dom.classification.ClassificationRepository;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.dom.jobtitle.info.JobTitleInfo;
import nts.uk.ctx.bs.employee.dom.jobtitle.info.JobTitleInfoRepository;
import nts.uk.file.at.app.export.schedule.personalschedulebyworkplace.CodeNameValue;
import nts.uk.file.at.app.export.schedule.personalschedulebyworkplace.PersonalScheduleByWkpDataSource;
import nts.uk.file.at.app.export.schedule.personalschedulebyworkplace.PersonalScheduleByWorkplaceExportGenerator;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AsposePersonalScheduleByWorkplaceExportGenerator extends AsposeCellsReportGenerator implements PersonalScheduleByWorkplaceExportGenerator {
    private final String FONT_NAME = "ＭＳ ゴシック";
    private final int FONT_SIZE = 9;
    private final String PDF_EXT = ".pdf";
    private final String EXCEL_EXT = ".xlsx";
    private final String HTML_EXT = ".html";
    private final String SPACE = "　";
    private final String COLON = "：";

    private final int BG_COLOR_SPECIFIC_DAY = Integer.parseInt("ffc0cb", 16);
    private final int BG_COLOR_SUNDAY = Integer.parseInt("fabf8f", 16);
    private final int BG_COLOR_SATURDAY = Integer.parseInt("8bd8ff", 16);
    private final int BG_COLOR_WEEKDAY = Integer.parseInt("D9D9D9", 16);
    private final int TEXT_COLOR_SUNDAY = Integer.parseInt("ff0000", 16);
    private final int TEXT_COLOR_SATURDAY = Integer.parseInt("0000ff", 16);
    private final int TEXT_COLOR_WEEKDAY = Integer.parseInt("404040", 16);

    private final int COLUMN_WIDTH = 4;
    private final int START_HEADER_ROW = 5;
    private final int START_DATA_ROW = 9;
    private final int START_DATE_COL = 3;
    private final int PERSONAL_INFO_COLUMN = 0;
    private final int ADDITIONAL_PERSONAL_INFO_COLUMN = 2;

    private int startRow = START_DATA_ROW;
    private final List<Integer> empIndexes = new ArrayList<>();
    private final Map<Integer, WorkplaceCounterCategory> wkpTotalIndexes = new HashMap<>();

    private final DecimalFormat df = new DecimalFormat("###,###,###");

    @Inject
    private TotalTimesRepository totalTimesRepo;
    @Inject
    private TimesNumberCounterSelectionRepo timesNumberCounterSelectionRepo;

    @Inject
    private WorkTimeSettingRepository workTimeSettingRepo;
    @Inject
    private ShiftMasterRepository shiftMasterRepo;
    @Inject
    private WorkTypeRepository workTypeRepo;

    @Inject
    private WorkplaceCounterLaborCostAndTimeRepo workplaceCounterLaborCostAndTimeRepo;

    @Inject
    private ExternalBudgetRepository externalBudgetRepo;

    @Inject
    private EmploymentRepository employmentRepo;

    @Inject
    private ClassificationRepository classificationRepo;

    @Inject
    private JobTitleInfoRepository jobTitleInfoRepo;

    @Override
    public void generate(FileGeneratorContext context, PersonalScheduleByWkpDataSource dataSource, String comment, boolean excel, boolean preview) {
        // reset global variables
        startRow = START_DATA_ROW;
        empIndexes.clear();
        wkpTotalIndexes.clear();

        // start exporting
        try {
            AsposeCellsReportContext reportContext = this.createEmptyContext("PersonalScheduleByWorkplace");
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet worksheet = worksheets.get(0);
            worksheet.setName(dataSource.getOutputSetting().getName().v());
            worksheet.setGridlinesVisible(false);
            this.printHeader(worksheet, dataSource, comment);
            this.printContent(worksheet, dataSource);
            this.handlePageBreak(worksheet, dataSource);
            reportContext.processDesigner();
            if (preview) {
                // do not remove these two lines
                worksheet.getCells().get(startRow, 0).setValue(" ");
                worksheet.getCells().get(startRow + 1, 0).setValue(" ");

                worksheet.getCells().setColumnWidth(PERSONAL_INFO_COLUMN, 15);
                worksheet.getCells().setColumnWidth(PERSONAL_INFO_COLUMN + 1, 10);
                if (worksheet.getCells().getColumnWidth(ADDITIONAL_PERSONAL_INFO_COLUMN) != 0) worksheet.getCells().setColumnWidth(ADDITIONAL_PERSONAL_INFO_COLUMN, 12);
                // save as html file
                HtmlSaveOptions options = new HtmlSaveOptions(SaveFormat.AUTO);
                options.setPresentationPreference(true);
                options.setHiddenColDisplayType(HtmlHiddenColDisplayType.REMOVE);
                String fileName = this.getReportName(dataSource.getOutputSetting().getName().v() + HTML_EXT);
                workbook.save(this.createNewFile(context, fileName), options);
                workbook.save(ServerSystemProperties.fileStoragePath() + "\\" + fileName, options);
            } else {
                this.settingPage(worksheet, dataSource);
                if (excel) {
                    worksheet.setViewType(ViewType.PAGE_LAYOUT_VIEW);
                    // save as excel file
                    reportContext.saveAsExcel(this.createNewFile(context, this.getReportName(dataSource.getOutputSetting().getName().v() + EXCEL_EXT)));
                } else {
                    // save as PDF file
                    reportContext.saveAsPdf(this.createNewFile(context, this.getReportName(dataSource.getOutputSetting().getName().v() + PDF_EXT)));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void settingPage(Worksheet worksheet, PersonalScheduleByWkpDataSource dataSource) {
        PageSetup pageSetup = worksheet.getPageSetup();
        pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
        pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
        pageSetup.setFitToPagesTall(0);
        pageSetup.setFitToPagesWide(1);
        pageSetup.setTopMarginInch(0.98);
        pageSetup.setBottomMarginInch(0.39);
        pageSetup.setLeftMarginInch(0.39);
        pageSetup.setRightMarginInch(0.39);
        pageSetup.setHeaderMarginInch(0.39);
        pageSetup.setFooterMarginInch(0.31);
        pageSetup.setCenterHorizontally(true);
        pageSetup.setHeader(0, "&9&\"" + FONT_NAME + "\"" + dataSource.getCompanyName());
        pageSetup.setHeader(1, "&16&\"" + FONT_NAME + ",Bold\"" + dataSource.getOutputSetting().getName().v());
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm", Locale.JAPAN);
        pageSetup.setHeader(2, "&9&\"" + FONT_NAME + "\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage&P ");
        pageSetup.setPrintTitleRows("$1:$9");
    }

    private String getText(String resourceId) {
        return TextResource.localize(resourceId);
    }

    private void printHeader(Worksheet worksheet, PersonalScheduleByWkpDataSource dataSource, String comment) {
        Cells cells = worksheet.getCells();
        // B part
        cells.get("A4").setValue(
                getText("KSU001_4129")
                        + dataSource.getPeriod().start().toString() + "(" + this.getDayOfWeek(dataSource.getPeriod().start().dayOfWeekEnum()) + ")"
                        + getText("KSU001_4130")
                        + dataSource.getPeriod().end().toString() + "(" + this.getDayOfWeek(dataSource.getPeriod().end().dayOfWeekEnum()) + ")"
        );
        Style styleA4 = cells.get("A4").getStyle();
        styleA4.setHorizontalAlignment(TextAlignmentType.LEFT);
        styleA4.getFont().setName(FONT_NAME);
        styleA4.getFont().setSize(FONT_SIZE);
        cells.get("A4").setStyle(styleA4);
        cells.get("A5").setValue((dataSource.getOrgUnit() == 0 ? getText("KSU001_4132") : getText("KSU001_4133")) + dataSource.getOrganizationDisplayInfo().getCode() + SPACE + dataSource.getOrganizationDisplayInfo().getDisplayName());
        cells.get("A5").setStyle(styleA4);

        // C1 part
        cells.get(START_HEADER_ROW, PERSONAL_INFO_COLUMN).setValue(getText("KSU001_4131"));
        for (int i = 0; i < 4; i++) {
            this.setHeaderStyle(cells.get(START_HEADER_ROW + i, PERSONAL_INFO_COLUMN), null, false, i == 0,i == 1, i == 3, false, false);
            this.setHeaderStyle(cells.get(START_HEADER_ROW + i, PERSONAL_INFO_COLUMN + 1), null, false, i == 0, i == 1, i == 3, false, false);
        }
        cells.merge(START_HEADER_ROW, PERSONAL_INFO_COLUMN, 4, 2, true);
        cells.setColumnWidth(PERSONAL_INFO_COLUMN, 10);
        cells.setColumnWidth(PERSONAL_INFO_COLUMN + 1, 7);

        // C2 part
        long additionCount = dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getAdditionalInfo().isPresent()).count();
        if (dataSource.getOutputSetting().getOutputItem().getAdditionalColumnUseAtr() == NotUseAtr.NOT_USE || additionCount == 0) {
            cells.hideColumn(ADDITIONAL_PERSONAL_INFO_COLUMN);
        } else {
            cells.setColumnWidth(ADDITIONAL_PERSONAL_INFO_COLUMN, 9);
            if (additionCount == 1) {
                cells.get(START_HEADER_ROW, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(getText(dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getAdditionalInfo().isPresent()).findFirst().get().getAdditionalInfo().get().nameId));
            }
        }
        for (int i = 0; i < 4; i++) {
            this.setHeaderStyle(cells.get(START_HEADER_ROW + i, ADDITIONAL_PERSONAL_INFO_COLUMN), null, false, i == 0, i == 1, i == 3, false, false);
        }
        cells.merge(START_HEADER_ROW, ADDITIONAL_PERSONAL_INFO_COLUMN, 4, 1, true);

        // C3 part
        int startCol = START_DATE_COL;
        boolean hasPersonalTotal = !dataSource.getOutputSetting().getPersonalCounterCategories().isEmpty();
        for (int i = 0; i < dataSource.getDateInfos().size(); i++) {
            DateInformation dateInfo = (DateInformation) dataSource.getDateInfos().get(i);
            cells.get(START_HEADER_ROW, startCol + i).setValue(dateInfo.getYmd().toString(i == 0 || dateInfo.getYmd().day() == 1 ? "M/d" : "d"));
            cells.get(START_HEADER_ROW + 1, startCol + i).setValue(this.getDayOfWeek(dateInfo.getDayOfWeek()));
            if (dateInfo.getOptCompanyEventName().isPresent()) {
                cells.get(START_HEADER_ROW + 2, startCol + i).setValue(dateInfo.getOptCompanyEventName().get().v());
            }
            if (dateInfo.getOptWorkplaceEventName().isPresent()) {
                cells.get(START_HEADER_ROW + 3, startCol + i).setValue(dateInfo.getOptWorkplaceEventName().get().v());
            }
            for (int j = 0; j < 4; j++) {
                this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol + i), dateInfo, false, j == 0, j == 1, j == 3, i == dataSource.getDateInfos().size() - 1 && hasPersonalTotal, false);
            }
            cells.setColumnWidth(startCol + i, COLUMN_WIDTH);
        }

        // C4 part
        startCol += dataSource.getDateInfos().size();
        for (int i = 0; i < dataSource.getOutputSetting().getPersonalCounterCategories().size(); i++) {
            PersonalCounterCategory personalCounterCategory = dataSource.getOutputSetting().getPersonalCounterCategories().get(i);
            switch (personalCounterCategory) {
                case WORKING_HOURS:
                    cells.get(START_HEADER_ROW, startCol).setValue(getText(personalCounterCategory.nameId));
                    cells.get(START_HEADER_ROW + 1, startCol).setValue(getText("KSU001_20"));
                    cells.get(START_HEADER_ROW + 1, startCol + 1).setValue(getText("KSU001_50"));
                    cells.get(START_HEADER_ROW + 1, startCol + 2).setValue(getText("KSU001_51"));
                    for (int j = 0; j < 4; j++) {
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol), null, j != 0, j == 0, false, j == 3, false, false);
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol + 1), null, j != 0, j == 0, false, j == 3, false, false);
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol + 2), null, j != 0, j == 0, false, j == 3, false, false);
                    }
                    cells.merge(START_HEADER_ROW, startCol, 1, 3);
                    cells.merge(START_HEADER_ROW + 1, startCol, 3, 1);
                    cells.merge(START_HEADER_ROW + 1, startCol + 1, 3, 1);
                    cells.merge(START_HEADER_ROW + 1, startCol + 2, 3, 1);
                    cells.setColumnWidth(startCol, COLUMN_WIDTH);
                    cells.setColumnWidth(startCol + 1, COLUMN_WIDTH);
                    cells.setColumnWidth(startCol + 2, COLUMN_WIDTH);
                    startCol += 3;
                    break;
                case MONTHLY_EXPECTED_SALARY:
                case CUMULATIVE_ESTIMATED_SALARY:
                    cells.get(START_HEADER_ROW, startCol).setValue(getText(personalCounterCategory.nameId));
                    cells.get(START_HEADER_ROW + 1, startCol).setValue(getText("KSU001_18"));
                    cells.get(START_HEADER_ROW + 1, startCol + 1).setValue(getText("KSU001_19"));
                    for (int j = 0; j < 4; j++) {
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol), null, j != 0, j == 0, false, j == 3, false, false);
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol + 1), null, j != 0, j == 0, false, j == 3, false, false);
                    }
                    cells.merge(START_HEADER_ROW, startCol, 1, 2);
                    cells.merge(START_HEADER_ROW + 1, startCol, 3, 1);
                    cells.merge(START_HEADER_ROW + 1, startCol + 1, 3, 1);
                    cells.setColumnWidth(startCol, COLUMN_WIDTH);
                    cells.setColumnWidth(startCol + 1, COLUMN_WIDTH);
                    startCol += 2;
                    break;
                case TIMES_COUNTING_1:
                case TIMES_COUNTING_2:
                case TIMES_COUNTING_3:
                    TimesNumberCounterType counterType;
                    if (personalCounterCategory == PersonalCounterCategory.TIMES_COUNTING_1)
                        counterType = TimesNumberCounterType.PERSON_1;
                    else if (personalCounterCategory == PersonalCounterCategory.TIMES_COUNTING_2)
                        counterType = TimesNumberCounterType.PERSON_2;
                    else counterType = TimesNumberCounterType.PERSON_3;
                    Optional<TimesNumberCounterSelection> timesNumberCounterSelection = timesNumberCounterSelectionRepo.get(AppContexts.user().companyId(), counterType);
                    if (timesNumberCounterSelection.isPresent()) {
                        cells.get(START_HEADER_ROW, startCol).setValue(getText(personalCounterCategory.nameId));
                        List<TotalTimes> totalTimes = totalTimesRepo.getTotalTimesDetailByListNo(AppContexts.user().companyId(), timesNumberCounterSelection.get().getSelectedNoList()).stream()
                                .filter(t -> t.getUseAtr() == UseAtr.Use)
                                .collect(Collectors.toList());
                        for (int j = 0; j < totalTimes.size(); j++) {
                            TotalTimes totalTime = totalTimes.get(j);
                            cells.get(START_HEADER_ROW + 1, startCol + j).setValue(totalTime.getTotalTimesABName().v());
                        }
                        for (int j = 0; j < Math.max(totalTimes.size(), 1); j++) {
                            for (int k = 0; k < 4; k++) {
                                this.setHeaderStyle(cells.get(START_HEADER_ROW + k, startCol + j), null, k != 0, k == 0, false, k == 3, false, false);
                            }
                        }
                        for (int j = 0; j < Math.max(totalTimes.size(), 1); j++) {
                            cells.merge(START_HEADER_ROW + 1, startCol + j, 3, 1);
                            cells.setColumnWidth(startCol + j, COLUMN_WIDTH);
                        }
                        cells.merge(START_HEADER_ROW, startCol, 1, Math.max(totalTimes.size(), 1));
                        startCol += Math.max(totalTimes.size(), 1);
                    }
                    break;
                case ATTENDANCE_HOLIDAY_DAYS:
                    cells.get(START_HEADER_ROW, startCol).setValue(getText(personalCounterCategory.nameId));
                    cells.get(START_HEADER_ROW + 1, startCol).setValue(getText("KSU001_62"));
                    cells.get(START_HEADER_ROW + 1, startCol + 1).setValue(getText("KSU001_63"));
                    for (int j = 0; j < 4; j++) {
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol), null, j != 0, j == 0, false, j == 3, false, false);
                        this.setHeaderStyle(cells.get(START_HEADER_ROW + j, startCol + 1), null, j != 0, j == 0, false, j == 3, false, false);
                    }
                    cells.merge(START_HEADER_ROW, startCol, 1, 2);
                    cells.merge(START_HEADER_ROW + 1, startCol, 3, 1);
                    cells.merge(START_HEADER_ROW + 1, startCol + 1, 3, 1);
                    cells.setColumnWidth(startCol, COLUMN_WIDTH);
                    cells.setColumnWidth(startCol + 1, COLUMN_WIDTH);
                    startCol += 2;
                    break;
                default:
                    break;
            }
        }

        String[] commentLines = comment.split("\n");
        cells.get(0, START_DATE_COL).setValue(commentLines.length > 2 ? StringUtils.join(commentLines, SPACE) : comment);
        Style styleC1 = cells.get(0, START_DATE_COL).getStyle();
        styleC1.setVerticalAlignment(TextAlignmentType.TOP);
        styleC1.setHorizontalAlignment(TextAlignmentType.RIGHT);
        styleC1.setTextWrapped(true);
        styleC1.getFont().setName(FONT_NAME);
        styleC1.getFont().setSize(FONT_SIZE);
        cells.get(0, START_DATE_COL).setStyle(styleC1);
        cells.merge(0, START_DATE_COL, 3, startCol - START_DATE_COL);
    }

    private void setHeaderStyle(Cell cell, DateInformation dateInfo, boolean wrapText, boolean firstRow, boolean secondRow, boolean lastRow, boolean doubleBorder, boolean wkp) {
        Style style = commonStyle();
        if (wrapText) {
            style.setTextWrapped(true);
        }
        if (firstRow) {
            style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(wkp ? CellBorderType.THIN : CellBorderType.MEDIUM);
            style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.NONE);
        }
        if (secondRow)
            style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.NONE);
        if (lastRow)
            style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(wkp ? CellBorderType.THIN : CellBorderType.MEDIUM);
        if (doubleBorder)
            style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOUBLE);
        if (dateInfo != null) {
            if (dateInfo.isSpecificDay()) {
                style.setForegroundColor(Color.fromArgb(BG_COLOR_SPECIFIC_DAY));
            } else if (dateInfo.isHoliday() || dateInfo.getDayOfWeek() == DayOfWeek.SUNDAY) {
                style.setForegroundColor(Color.fromArgb(BG_COLOR_SUNDAY));
                style.getFont().setColor(Color.fromArgb(TEXT_COLOR_SUNDAY));
            } else if (dateInfo.getDayOfWeek() == DayOfWeek.SATURDAY) {
                style.setForegroundColor(Color.fromArgb(BG_COLOR_SATURDAY));
                style.getFont().setColor(Color.fromArgb(TEXT_COLOR_SATURDAY));
            } else {
                style.setForegroundColor(Color.fromArgb(BG_COLOR_WEEKDAY));
                style.getFont().setColor(Color.fromArgb(TEXT_COLOR_WEEKDAY));
            }
        } else {
            style.setForegroundColor(Color.fromArgb(BG_COLOR_WEEKDAY));
            style.getFont().setColor(Color.fromArgb(TEXT_COLOR_WEEKDAY));
        }
        cell.setStyle(style);
    }

    private String getDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "月";
            case TUESDAY: return "火";
            case WEDNESDAY: return "水";
            case THURSDAY: return "木";
            case FRIDAY: return "金";
            case SATURDAY: return "土";
            case SUNDAY: return "日";
            default: return "";
        }
    }

    private void printContent(Worksheet worksheet, PersonalScheduleByWkpDataSource dataSource) {
        String companyId = AppContexts.user().companyId();
        Cells cells = worksheet.getCells();
        // E part
        List<ScheduleTablePersonalInfo> personalInfoScheduleTableList = dataSource.getPersonalInfoScheduleTableList();
        personalInfoScheduleTableList.sort(Comparator.comparing(i -> i.getPersonalInfoMap().get(ScheduleTablePersonalInfoItem.EMPLOYEE_NAME).getCode()));

        int rows = dataSource.getOutputSetting().getOutputItem().getDetails().size();
        boolean hasPersonalTotal = !dataSource.getOutputSetting().getPersonalCounterCategories().isEmpty();
        boolean hasWorkplaceTotal = !dataSource.getOutputSetting().getWorkplaceCounterCategories().isEmpty();
        long additionCount = dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getAdditionalInfo().isPresent()).count();
        List<OneRowOutputItem> personalItems = dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getPersonalInfo().isPresent()).collect(Collectors.toList());
        List<OneRowOutputItem> additionalItems = dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getAdditionalInfo().isPresent()).collect(Collectors.toList());
        List<OneRowOutputItem> attendanceItems = dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getAttendanceItem().isPresent()).collect(Collectors.toList());

        List<ShiftMaster> shiftMasters = new ArrayList<>();
        List<WorkType> workTypes = workTypeRepo.findByCompanyId(companyId);
        List<WorkTimeSetting> workTimeSettings = new ArrayList<>();
        if (attendanceItems.stream().anyMatch(i -> i.getAttendanceItem().get() == ScheduleTableAttendanceItem.SHIFT)) {
            shiftMasters.addAll(shiftMasterRepo.getAllByCid(companyId).stream().map(i -> new ShiftMaster(
                    i.getCompanyId(),
                    new ShiftMasterCode(i.getShiftMasterCode()),
                    new ShiftMasterDisInfor(
                            new ShiftMasterName(i.getShiftMasterName()),
                            new ColorCodeChar6(i.getColor()),
                            new ColorCodeChar6(i.getColorSmartphone()),
                            StringUtil.isNullOrEmpty(i.getRemark(),true) ? Optional.empty() : Optional.of(new Remarks(i.getRemark()))
                    ),
                    i.getWorkTypeCd(),
                    i.getWorkTimeCd(),
                    StringUtil.isNullOrEmpty(i.getImportCode(),true) ? Optional.empty() : Optional.of(new ShiftMasterImportCode(i.getImportCode()))
            )).collect(Collectors.toList()));
        }
        if (attendanceItems.stream().anyMatch(i -> i.getAttendanceItem().get() == ScheduleTableAttendanceItem.WORK_TIME)) {
            workTimeSettings.addAll(workTimeSettingRepo.findByCompanyId(companyId));
        }
        OneDayEmployeeAttendanceInfo.Require require = new OneDayEmployeeAttendanceInfo.Require() {
            @Override
            @TransactionAttribute(TransactionAttributeType.SUPPORTS)
            public Optional<ShiftMaster> getShiftMaster(String workTypeCode, Optional<String> workTimeCode) {
                return shiftMasters.stream()
                        .filter(i -> i.getWorkTypeCode().v().equals(workTypeCode)
                                && ((i.getWorkTimeCodeNotNull().isPresent() && workTimeCode.isPresent() && i.getWorkTimeCodeNotNull().get().v().equals(workTimeCode.get()))
                                || (!i.getWorkTimeCodeNotNull().isPresent() && !workTimeCode.isPresent())))
                        .findFirst();
            }
            @Override
            @TransactionAttribute(TransactionAttributeType.SUPPORTS)
            public Optional<WorkType> getWorkType(String workTypeCode) {
                return workTypes.stream().filter(i -> i.getWorkTypeCode().v().equals(workTypeCode)).findFirst();
            }
            @Override
            @TransactionAttribute(TransactionAttributeType.SUPPORTS)
            public Optional<WorkTimeSetting> getWorkTimeSetting(String workTimeCode) {
                return workTimeSettings.stream().filter(i -> i.getWorktimeCode().v().equals(workTimeCode)).findFirst();
            }
        };

        // loop employee
        for (int i1 = 0; i1 < personalInfoScheduleTableList.size(); i1++) {
            ScheduleTablePersonalInfo emp = personalInfoScheduleTableList.get(i1);
            empIndexes.add(startRow);
            List<OneDayEmployeeAttendanceInfo> attendanceInfos = (List<OneDayEmployeeAttendanceInfo>) dataSource.getListEmpOneDayAttendanceInfo()
                    .stream().filter(i -> ((OneDayEmployeeAttendanceInfo) i).getEmployeeId().equals(emp.getEmployeeId()))
                    .collect(Collectors.toList());
            int startCol = START_DATE_COL;
            for (int i = 0; i < rows; i++) {
                // E1 part
                if (i < personalItems.size()) {
                    OneRowOutputItem personalItem = personalItems.get(i);
                    this.setPesonalInfoValue(cells, personalItem.getPersonalInfo(), emp, startRow + i, PERSONAL_INFO_COLUMN, false);
                }
                this.setPersonalStyle(cells, startRow + i, PERSONAL_INFO_COLUMN, i == 0, i == rows - 1, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal);
                this.setPersonalStyle(cells, startRow + i, PERSONAL_INFO_COLUMN + 1, i == 0, i == rows - 1, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal);
                cells.merge(startRow + i, PERSONAL_INFO_COLUMN, 1, 2);

                // E2 part
                if (i < additionalItems.size()) {
                    OneRowOutputItem additionalItem = additionalItems.get(i);
                    this.setPesonalInfoValue(cells, additionalItem.getAdditionalInfo(), emp, startRow + i, ADDITIONAL_PERSONAL_INFO_COLUMN, additionCount == 1);
                }
                this.setPersonalStyle(cells, startRow + i, ADDITIONAL_PERSONAL_INFO_COLUMN, i == 0, i == rows - 1, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal);

                // E3 part
                for (int j = 0; j < dataSource.getDateInfos().size(); j++) {
                    DateInformation dateInfo = (DateInformation) dataSource.getDateInfos().get(j);
                    Optional<OneDayEmployeeAttendanceInfo> attendanceInfo = attendanceInfos.stream().filter(info -> info.getDate().equals(dateInfo.getYmd())).findFirst();
                    this.setAttendanceValue(
                            cells,
                            attendanceInfo,
                            require,
                            attendanceItems.size() > i ? attendanceItems.get(i).getAttendanceItem().get() : null,
                            startRow + i,
                            startCol + j,
                            i == 0,
                            i == rows - 1,
                            i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal,
                            j == dataSource.getDateInfos().size() - 1 && hasPersonalTotal,
                            dataSource.getOutputSetting().getOutputItem().getShiftBackgroundColorUseAtr() == NotUseAtr.USE
                    );
                }
            }
            startCol += dataSource.getDateInfos().size();
            // E4 part
            for (int ii = 0; ii < dataSource.getOutputSetting().getPersonalCounterCategories().size(); ii++) {
                PersonalCounterCategory personalCounterCategory = dataSource.getOutputSetting().getPersonalCounterCategories().get(ii);
                switch (personalCounterCategory) {
                    case WORKING_HOURS:
                        Map<String, Map<AttendanceTimesForAggregation, BigDecimal>> workingHoursMap = (Map<String, Map<AttendanceTimesForAggregation, BigDecimal>>) dataSource.getPersonalTotalResult().getOrDefault(personalCounterCategory, new HashMap<>());
                        Map<AttendanceTimesForAggregation, BigDecimal> workingHours = workingHoursMap.get(emp.getEmployeeId());
                        AttendanceTimesForAggregation[] values = AttendanceTimesForAggregation.values();
                        for (int jj = 0; jj < values.length; jj++) {
                            AttendanceTimesForAggregation attendanceTimesForAggregation = values[jj];
                            if (attendanceTimesForAggregation != AttendanceTimesForAggregation.NIGHTSHIFT) {
                                String value = workingHours != null && workingHours.get(attendanceTimesForAggregation) != null ? new TimeWithDayAttr(workingHours.get(attendanceTimesForAggregation).intValue()).getRawTimeWithFormat() : "";
                                this.setPersonalTotalValue(cells.get(startRow, startCol + jj), value, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                                for (int kk = 1; kk < rows; kk++) {
                                    this.setPersonalTotalValue(cells.get(startRow + kk, startCol + jj), null, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                                }
                                cells.merge(startRow, startCol + jj, rows, 1);
                            }
                        }
                        startCol += 3;
                        break;
                    case MONTHLY_EXPECTED_SALARY:
                    case CUMULATIVE_ESTIMATED_SALARY:
                        Map<String, EstimatedSalary> estimatedSalaryMap = (Map<String, EstimatedSalary>) dataSource.getPersonalTotalResult().getOrDefault(personalCounterCategory, new HashMap<>());
                        EstimatedSalary estimatedSalary = estimatedSalaryMap.get(emp.getEmployeeId());
                        this.setPersonalTotalValue(
                                cells.get(startRow, startCol),
                                estimatedSalary == null ? "" : df.format(estimatedSalary.getCriterion().v()),
                                i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal,
                                estimatedSalary == null ? Optional.empty() : estimatedSalary.getBackground().map(i -> i.v().length() > 6 ? i.v().substring(1) : i.v())
                        );
                        this.setPersonalTotalValue(
                                cells.get(startRow, startCol + 1),
                                estimatedSalary == null ? "" : df.format(estimatedSalary.getSalary()),
                                i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal,
                                estimatedSalary == null ? Optional.empty() : estimatedSalary.getBackground().map(i -> i.v().length() > 6 ? i.v().substring(1) : i.v())
                        );
                        for (int kk = 1; kk < rows; kk++) {
                            this.setPersonalTotalValue(cells.get(startRow + kk, startCol), null, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                            this.setPersonalTotalValue(cells.get(startRow + kk, startCol + 1), null, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                        }
                        cells.merge(startRow, startCol, rows, 1);
                        cells.merge(startRow, startCol + 1, rows, 1);
                        startCol += 2;
                        break;
                    case TIMES_COUNTING_1:
                    case TIMES_COUNTING_2:
                    case TIMES_COUNTING_3:
                        Map<String, Map<Integer, BigDecimal>> timesCountMap = (Map<String, Map<Integer, BigDecimal>>) dataSource.getPersonalTotalResult().getOrDefault(personalCounterCategory, new HashMap<>());
                        Map<Integer, BigDecimal> timesCount = timesCountMap.get(emp.getEmployeeId());
                        TimesNumberCounterType counterType;
                        if (personalCounterCategory == PersonalCounterCategory.TIMES_COUNTING_1)
                            counterType = TimesNumberCounterType.PERSON_1;
                        else if (personalCounterCategory == PersonalCounterCategory.TIMES_COUNTING_2)
                            counterType = TimesNumberCounterType.PERSON_2;
                        else counterType = TimesNumberCounterType.PERSON_3;
                        Optional<TimesNumberCounterSelection> timesNumberCounterSelection = timesNumberCounterSelectionRepo.get(AppContexts.user().companyId(), counterType);
                        if (timesNumberCounterSelection.isPresent()) {
                            List<TotalTimes> totalTimes = totalTimesRepo.getTotalTimesDetailByListNo(AppContexts.user().companyId(), timesNumberCounterSelection.get().getSelectedNoList()).stream()
                                    .filter(t -> t.getUseAtr() == UseAtr.Use)
                                    .collect(Collectors.toList());
                            for (int jj = 0; jj < totalTimes.size(); jj++) {
                                TotalTimes totalTime = totalTimes.get(jj);
                                this.setPersonalTotalValue(
                                        cells.get(startRow, startCol + jj),
                                        timesCount == null || timesCount.get(totalTime.getTotalCountNo()) == null ? "" : timesCount.get(totalTime.getTotalCountNo()).toString(),
                                        i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal,
                                        Optional.empty()
                                );
                                for (int kk = 1; kk < rows; kk++) {
                                    this.setPersonalTotalValue(cells.get(startRow + kk, startCol + jj), null, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                                }
                                cells.merge(startRow, startCol + jj, rows, 1);
                            }
                            startCol += totalTimes.size();
                        }
                        break;
                    case ATTENDANCE_HOLIDAY_DAYS:
                        Map<String, Map<WorkClassificationAsAggregationTarget, BigDecimal>> holidayWorkMap = (Map<String, Map<WorkClassificationAsAggregationTarget, BigDecimal>>) dataSource.getPersonalTotalResult().getOrDefault(personalCounterCategory, new HashMap<>());
                        Map<WorkClassificationAsAggregationTarget, BigDecimal> holidayWork = holidayWorkMap.get(emp.getEmployeeId());
                        this.setPersonalTotalValue(
                                cells.get(startRow, startCol),
                                holidayWork == null || holidayWork.get(WorkClassificationAsAggregationTarget.WORKING) == null ? "" : holidayWork.get(WorkClassificationAsAggregationTarget.WORKING).toString(),
                                i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal,
                                Optional.empty()
                        );
                        this.setPersonalTotalValue(
                                cells.get(startRow, startCol + 1),
                                holidayWork == null || holidayWork.get(WorkClassificationAsAggregationTarget.HOLIDAY) == null ? "" : holidayWork.get(WorkClassificationAsAggregationTarget.HOLIDAY).toString(),
                                i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal,
                                Optional.empty()
                        );
                        for (int kk = 1; kk < rows; kk++) {
                            this.setPersonalTotalValue(cells.get(startRow + kk, startCol), null, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                            this.setPersonalTotalValue(cells.get(startRow + kk, startCol + 1), null, i1 == personalInfoScheduleTableList.size() - 1 && hasWorkplaceTotal, Optional.empty());
                        }
                        cells.merge(startRow, startCol, rows, 1);
                        cells.merge(startRow, startCol + 1, rows, 1);
                        startCol += 2;
                        break;
                    default:
                        break;
                }
            }
            startRow += rows;
        }

        // F1, F2, F3 parts
        this.printContentF(worksheet, dataSource);
    }

    private Style commonStyle() {
        Style commonStyle = new Style();
        commonStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.DOTTED);
        commonStyle.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.DOTTED);
        commonStyle.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(CellBorderType.DOTTED);
        commonStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOTTED);
        commonStyle.getFont().setName(FONT_NAME);
        commonStyle.getFont().setSize(FONT_SIZE);
        commonStyle.setShrinkToFit(true);
        commonStyle.setPattern(BackgroundType.SOLID);
        commonStyle.setVerticalAlignment(TextAlignmentType.CENTER);
        commonStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
        return commonStyle;
    }

    private void setPesonalInfoValue(Cells cells, Optional<ScheduleTablePersonalInfoItem> item, ScheduleTablePersonalInfo emp, int row, int column, boolean isValueOnly) {
        if (item.isPresent()) {
            String value = isValueOnly ? "" : getText(item.get().nameId) + COLON;
            switch (item.get()) {
                case EMPLOYEE_NAME:
                    value = emp.getPersonalInfoMap().get(item.get()).getCode() + SPACE + emp.getPersonalInfoMap().get(item.get()).getName();
                    break;
                case EMPLOYMENT:
                case JOBTITLE:
                case CLASSIFICATION:
                case TEAM:
                case RANK:
                case NURSE_CLASSIFICATION:
                    value += emp.getPersonalInfoMap().getOrDefault(item.get(), new ScheduleTablePersonalInfoItemData("", "")).getName();
                    break;
//                case QUALIFICATION:
                default:
                    break;
            }
            cells.get(row, column).setValue(value);
        }
    }

    private void setPersonalStyle(Cells cells, int row, int column, boolean firstRow, boolean lastRow, boolean doubleBorder) {
        Style style = commonStyle();
        if (firstRow)
            style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        if (lastRow)
            style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(doubleBorder ? CellBorderType.DOUBLE : CellBorderType.THIN);
        style.setHorizontalAlignment(TextAlignmentType.LEFT);
//        if (firstRow) style.setForegroundColor(Color.fromArgb(221, 235, 247));
        cells.get(row, column).setStyle(style);
    }

    private void setAttendanceValue(Cells cells, Optional<OneDayEmployeeAttendanceInfo> attendanceInfo, OneDayEmployeeAttendanceInfo.Require require, ScheduleTableAttendanceItem attendanceItem, int row, int column, boolean isFirstRow, boolean isLastRow, boolean doubleBorderBottom, boolean doubleBorderRight, boolean displayShiftBackground) {
        String value = "";
        Style style = commonStyle();
        if (attendanceItem != null && attendanceInfo.isPresent()) {
            Optional<WorkType> workType = attendanceInfo.get().getWorkType(require);
            boolean leaveBlank = false;
            if (workType.isPresent()) {
                if (workType.get().chechAttendanceDay() == AttendanceDayAttr.FULL_TIME) {
                    style.getFont().setColor(Color.getBlue());
                } else if (workType.get().chechAttendanceDay() == AttendanceDayAttr.HOLIDAY) {
                    style.getFont().setColor(Color.getRed());
                    leaveBlank = true;
                } else if (workType.get().chechAttendanceDay() == AttendanceDayAttr.HALF_TIME_AM || workType.get().chechAttendanceDay() == AttendanceDayAttr.HALF_TIME_PM) {
                    style.getFont().setColor(Color.fromArgb(255, 127, 39));
                }
            }
            switch (attendanceItem) {
                case SHIFT:
                    if (leaveBlank) break;
                    Optional<ShiftMaster> shiftMaster = attendanceInfo.get().getShiftMaster(require);
                    if (shiftMaster.isPresent()) {
                        value = shiftMaster.get().getDisplayInfor().getName().v();
                        if (displayShiftBackground) style.setForegroundColor(Color.fromArgb(Integer.parseInt(shiftMaster.get().getDisplayInfor().getColor().v(), 16)));
                    } else {
                        value = getText("KSU001_4136");
                    }
                    break;
                case WORK_TYPE:
                    if (workType.isPresent()) {
                        value = workType.get().getAbbreviationName().v();
                    } else if (attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem) != null)
                        value = attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem) + getText("KSU001_4135");
                    break;
                case WORK_TIME:
                    if (leaveBlank) break;
                    Optional<WorkTimeSetting> workTime = attendanceInfo.get().getWorkTime(require);
                    if (workTime.isPresent())
                        value = workTime.get().getWorkTimeDisplayName().getWorkTimeAbName().v();
                    else if (attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem) != null)
                        value = attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem) + getText("KSU001_4135");
                    break;
                case START_TIME:
                case END_TIME:
                case START_TIME_2:
                case END_TIME_2:
                    if (leaveBlank) break;
                    TimeWithDayAttr time = (TimeWithDayAttr) attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem);
                    if (time != null) value = time.getRawTimeWithFormat();
                    break;
                case TOTAL_WORKING_HOURS:
                case WORKING_HOURS:
                case ACTUAL_WORKING_HOURS:
                case LABOR_COST_TIME_1:
                case LABOR_COST_TIME_2:
                case LABOR_COST_TIME_3:
                case LABOR_COST_TIME_4:
                case LABOR_COST_TIME_5:
                case LABOR_COST_TIME_6:
                case LABOR_COST_TIME_7:
                case LABOR_COST_TIME_8:
                case LABOR_COST_TIME_9:
                case LABOR_COST_TIME_10:
                    if (leaveBlank) break;
                    AttendanceTime attdTime = (AttendanceTime) attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem);
                    if (attdTime != null) value = new TimeWithDayAttr(attdTime.v()).getRawTimeWithFormat();
                    break;
                default:
                    if (leaveBlank) break;
                    if (attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem) != null)
                        value = attendanceInfo.get().getAttendanceItemInfoMap().get(attendanceItem).toString();
                    break;
            }
        }
        cells.get(row, column).setValue(value);
        if (isFirstRow)
            style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        if (isLastRow)
            style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(doubleBorderBottom ? CellBorderType.DOUBLE : CellBorderType.THIN);
        if (doubleBorderRight)
            style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOUBLE);
//        if (isFirstRow) {
//            style.setForegroundColor(Color.fromArgb(221, 235, 247));
//        } else
        cells.get(row, column).setStyle(style);
    }

    private void setPersonalTotalValue(Cell cell, String value, boolean doubleBorder, Optional<String> colorCode) {
        cell.setValue(value);
        Style style = commonStyle();
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(doubleBorder ? CellBorderType.DOUBLE : CellBorderType.THIN);
        style.setHorizontalAlignment(TextAlignmentType.RIGHT);
//        if (isFirstRow)
//            style.setForegroundColor(Color.fromArgb(221, 235, 247));
//        else
        if (colorCode.isPresent()) {
            style.setForegroundColor(Color.fromArgb(Integer.parseInt(colorCode.get(), 16)));
        }
        cell.setStyle(style);
    }

    private void printContentF(Worksheet worksheet, PersonalScheduleByWkpDataSource dataSource) {
        String companyId = AppContexts.user().companyId();
        Cells cells = worksheet.getCells();
        boolean hasPersonalTotal = !dataSource.getOutputSetting().getPersonalCounterCategories().isEmpty();
        for (WorkplaceCounterCategory category : dataSource.getOutputSetting().getWorkplaceCounterCategories()) {
            // header
            this.printHeaderF(cells, startRow, category, dataSource.getDateInfos(), hasPersonalTotal, false);
            empIndexes.add(startRow);
            startRow += 2;

            // content
            switch (category) {
                case LABOR_COSTS_AND_TIME:
                    Map<GeneralDate, Map<LaborCostAggregationUnit, BigDecimal>> laborCostTimeMap = (Map<GeneralDate, Map<LaborCostAggregationUnit, BigDecimal>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                    Optional<WorkplaceCounterLaborCostAndTime> workplaceCounterLaborCostAndTime = workplaceCounterLaborCostAndTimeRepo.get(companyId);
                    if (workplaceCounterLaborCostAndTime.isPresent()) {
                        List<AggregationUnitOfLaborCosts> units = Arrays.asList(AggregationUnitOfLaborCosts.WITHIN, AggregationUnitOfLaborCosts.EXTRA, AggregationUnitOfLaborCosts.TOTAL);
                        List<String> unitStrings = Arrays.asList(getText("KSU001_50"), getText("KSU001_51"), getText("KSU001_58"));
                        for (int i = 0; i < units.size(); i++) {
                            AggregationUnitOfLaborCosts unit = units.get(i);
                            LaborCostAndTime laborCostAndTime = workplaceCounterLaborCostAndTime.get().getLaborCostAndTimeList().get(unit);
                            if (laborCostAndTime.getUseClassification() == NotUseAtr.USE) {
                                if (i != 0) {
                                    empIndexes.add(startRow);
                                    wkpTotalIndexes.put(startRow, category);
                                }
                                int count = 0, copyStartRow = startRow;
                                if (laborCostAndTime.isTargetAggregation(LaborCostItemType.TIME)) {
                                    this.setWorkplaceLaborCostTimeTotalValue(
                                            cells,
                                            dataSource.getDateInfos(),
                                            laborCostTimeMap,
                                            unit,
                                            LaborCostItemType.TIME,
                                            true,
                                            !laborCostAndTime.isTargetAggregation(LaborCostItemType.AMOUNT) && !laborCostAndTime.isTargetAggregation(LaborCostItemType.BUDGET),
                                            hasPersonalTotal
                                    );
                                    startRow++;
                                    count++;
                                }
                                if (laborCostAndTime.isTargetAggregation(LaborCostItemType.AMOUNT)) {
                                    this.setWorkplaceLaborCostTimeTotalValue(
                                            cells,
                                            dataSource.getDateInfos(),
                                            laborCostTimeMap,
                                            unit,
                                            LaborCostItemType.AMOUNT,
                                            !laborCostAndTime.isTargetAggregation(LaborCostItemType.TIME),
                                            !laborCostAndTime.isTargetAggregation(LaborCostItemType.BUDGET),
                                            hasPersonalTotal
                                    );
                                    startRow++;
                                    count++;
                                }
                                if (laborCostAndTime.isTargetAggregation(LaborCostItemType.BUDGET)) {
                                    this.setWorkplaceLaborCostTimeTotalValue(
                                            cells,
                                            dataSource.getDateInfos(),
                                            laborCostTimeMap,
                                            unit,
                                            LaborCostItemType.BUDGET,
                                            !laborCostAndTime.isTargetAggregation(LaborCostItemType.TIME) && !laborCostAndTime.isTargetAggregation(LaborCostItemType.AMOUNT),
                                            true,
                                            hasPersonalTotal
                                    );
                                    startRow++;
                                    count++;
                                }
                                cells.get(copyStartRow, PERSONAL_INFO_COLUMN).setValue(unitStrings.get(i));
                                for (int k = 0; k < count; k++) {
                                    Style style = commonStyle();
                                    if (count > 1) style.setVerticalAlignment(TextAlignmentType.TOP);
                                    style.setHorizontalAlignment(TextAlignmentType.LEFT);
                                    style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
                                    style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
                                    cells.get(copyStartRow + k, PERSONAL_INFO_COLUMN).setStyle(style);
                                }
                                cells.merge(copyStartRow, PERSONAL_INFO_COLUMN, count, 1);
                            }
                        }
                    }
                    break;
                case TIMES_COUNTING:
                    Optional<TimesNumberCounterSelection> timesNumberCounterSelection = timesNumberCounterSelectionRepo.get(companyId, TimesNumberCounterType.WORKPLACE);
                    if (timesNumberCounterSelection.isPresent()) {
                        Map<GeneralDate, Map<Integer, BigDecimal>> timeCountMap = (Map<GeneralDate, Map<Integer, BigDecimal>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                        List<Integer> selectedNoList = timesNumberCounterSelection.get().getSelectedNoList();
                        List<TotalTimes> totalTimes = totalTimesRepo.getTotalTimesDetailByListNo(companyId, selectedNoList).stream()
                                .filter(t -> t.getUseAtr() == UseAtr.Use)
                                .collect(Collectors.toList());
                        for (int i = 0; i < totalTimes.size(); i++) {
                            if (i != 0) {
                                empIndexes.add(startRow);
                                wkpTotalIndexes.put(startRow, category);
                            }
                            TotalTimes totalTime = totalTimes.get(i);
                            cells.get(startRow, PERSONAL_INFO_COLUMN).setValue(totalTime.getTotalTimesName().v());
                            Style itemStyle = commonStyle();
                            itemStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
                            cells.get(startRow, PERSONAL_INFO_COLUMN).setStyle(itemStyle);
                            cells.get(startRow, PERSONAL_INFO_COLUMN + 1).setStyle(itemStyle);
                            cells.merge(startRow, PERSONAL_INFO_COLUMN, 1, 2);
                            BigDecimal total = BigDecimal.ZERO;
                            for (int j = 0; j < dataSource.getDateInfos().size(); j++) {
                                DateInformation dateInfo = (DateInformation) dataSource.getDateInfos().get(j);
                                Map<Integer, BigDecimal> mapValue = timeCountMap.getOrDefault(dateInfo.getYmd(), new HashMap<>());
                                BigDecimal value = mapValue.getOrDefault(totalTime.getTotalCountNo(), BigDecimal.ZERO);
                                cells.get(startRow, START_DATE_COL + j).setValue(value.toString());
                                total = total.add(value);
                                itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                                if (j == dataSource.getDateInfos().size() - 1 && hasPersonalTotal) itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOUBLE);
                                cells.get(startRow, START_DATE_COL + j).setStyle(itemStyle);
                            }
                            cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(total.toString());
                            itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                            itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOTTED);
                            cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setStyle(itemStyle);
                            startRow++;
                        }
                    }
                    break;
                case EXTERNAL_BUDGET:
                    Map<GeneralDate, Map<ExternalBudgetCd, ExternalBudgetValues>> externalBudgetMap = (Map<GeneralDate, Map<ExternalBudgetCd, ExternalBudgetValues>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                    List<ExternalBudget> externalBudgets = externalBudgetRepo.findAll(companyId);
                    for (int i = 0; i < externalBudgets.size(); i++) {
                        if (i != 0) {
                            empIndexes.add(startRow);
                            wkpTotalIndexes.put(startRow, category);
                        }
                        ExternalBudget externalBudget = externalBudgets.get(i);
                        cells.get(startRow, PERSONAL_INFO_COLUMN).setValue(externalBudget.getExternalBudgetName().v());
                        Style itemStyle = commonStyle();
                        itemStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
                        cells.get(startRow, PERSONAL_INFO_COLUMN).setStyle(itemStyle);
                        cells.get(startRow, PERSONAL_INFO_COLUMN + 1).setStyle(itemStyle);
                        cells.merge(startRow, PERSONAL_INFO_COLUMN, 1, 2);

                        BigDecimal total = BigDecimal.ZERO;
                        for (int j = 0; j < dataSource.getDateInfos().size(); j++) {
                            DateInformation dateInfo = (DateInformation) dataSource.getDateInfos().get(j);
                            Map<ExternalBudgetCd, ExternalBudgetValues> valueMap = externalBudgetMap.getOrDefault(dateInfo.getYmd(), new HashMap<>());
                            ExternalBudgetValues value = valueMap.get(externalBudget.getExternalBudgetCd());
                            if (value != null) {
                                BigDecimal bdValue = new BigDecimal(value.toString());
                                total = total.add(bdValue);
                                cells.get(startRow, START_DATE_COL + j).setValue(externalBudget.getBudgetAtr() == BudgetAtr.TIME ? new TimeWithDayAttr(bdValue.intValue()).getRawTimeWithFormat() : df.format(bdValue));
                            } else {
                                cells.get(startRow, START_DATE_COL + j).setValue(externalBudget.getBudgetAtr() == BudgetAtr.TIME ? "0:00" : "0");
                            }
                            itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                            if (j == dataSource.getDateInfos().size() - 1 && hasPersonalTotal) itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOUBLE);
                            cells.get(startRow, START_DATE_COL + j).setStyle(itemStyle);
                        }
                        cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(externalBudget.getBudgetAtr() == BudgetAtr.TIME ? new TimeWithDayAttr(total.intValue()).getRawTimeWithFormat() : df.format(total.intValue()));
                        itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                        itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOTTED);
                        cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setStyle(itemStyle);
                        startRow++;
                    }
                    break;
                case WORKTIME_PEOPLE:
                    Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<CodeNameValue>>> shiftTimeMap = (Map<GeneralDate, List<NumberOfPeopleByEachWorkMethod<CodeNameValue>>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                    List<CodeNameValue> masters;
                    List<String> workCodes = shiftTimeMap.entrySet()
                            .stream()
                            .map(x -> x.getValue())
                            .flatMap(x -> x.stream())
                            .map(x -> x.getWorkMethod().getCode())
                            .distinct()
                            .collect(Collectors.toList());
                    if (dataSource.getOutputSetting().getOutputItem().getDisplayAttendanceItems().contains(ScheduleTableAttendanceItem.SHIFT)) {
                        masters = shiftMasterRepo.getByListShiftMaterCd2(companyId, workCodes).stream()
                                .map(s -> new CodeNameValue(s.getShiftMasterCode().v(), s.getDisplayInfor().getName().v()))
                                .collect(Collectors.toList());
                    } else {
                        masters = workCodes.isEmpty()
                                ? new ArrayList<>() // Arrays.asList(new CodeNameValue("", ""))
                                : workTimeSettingRepo.findByCodes(companyId, workCodes).stream()
                                        .map(w -> new CodeNameValue(w.getWorktimeCode().v(), w.getWorkTimeDisplayName().getWorkTimeName().v()))
                                        .collect(Collectors.toList());
                    }
                    for (int i = 0; i < masters.size(); i++) {
                        if (i != 0) {
                            empIndexes.add(startRow);
                            wkpTotalIndexes.put(startRow, category);
                        }
                        CodeNameValue master = masters.get(i);
                        cells.get(startRow, PERSONAL_INFO_COLUMN).setValue(master.getName());
                        cells.get(startRow, PERSONAL_INFO_COLUMN + 1).setValue(getText("KSU001_70"));
                        cells.get(startRow + 1, PERSONAL_INFO_COLUMN + 1).setValue(getText("KSU001_71"));
                        cells.get(startRow + 2, PERSONAL_INFO_COLUMN + 1).setValue(getText("KSU001_72"));
                        Style itemStyle = commonStyle();
                        itemStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
                        for (int j = 0; j < 3; j++) {
                            if (j == 0) {
                                itemStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
                                itemStyle.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.DOTTED);
                            } else if (j == 1) {
                                itemStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.DOTTED);
                                itemStyle.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.DOTTED);
                            } else {
                                itemStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.DOTTED);
                                itemStyle.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
                            }
                            itemStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
                            itemStyle.setVerticalAlignment(TextAlignmentType.TOP);
                            cells.get(startRow + j, PERSONAL_INFO_COLUMN).setStyle(itemStyle);
                            itemStyle.setVerticalAlignment(TextAlignmentType.CENTER);
                            cells.get(startRow + j, PERSONAL_INFO_COLUMN + 1).setStyle(itemStyle);
                            itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                            cells.get(startRow + j, ADDITIONAL_PERSONAL_INFO_COLUMN).setStyle(itemStyle);
                        }
                        cells.merge(startRow, PERSONAL_INFO_COLUMN, 3, 1);

                        BigDecimal total = BigDecimal.ZERO;
                        BigDecimal total2 = BigDecimal.ZERO;
                        BigDecimal total3 = BigDecimal.ZERO;
                        for (int j = 0; j < dataSource.getDateInfos().size(); j++) {
                            DateInformation dateInfo = (DateInformation) dataSource.getDateInfos().get(j);
                            List<NumberOfPeopleByEachWorkMethod<CodeNameValue>> values = shiftTimeMap.get(dateInfo.getYmd());
                            Optional<NumberOfPeopleByEachWorkMethod<CodeNameValue>> value = values.stream().filter(s -> s.getWorkMethod().getCode().equals(master.getCode())).findFirst();
                            if (value.isPresent()) {
                                cells.get(startRow, START_DATE_COL + j).setValue(value.get().getPlanNumber().toString());
                                cells.get(startRow + 1, START_DATE_COL + j).setValue(value.get().getScheduleNumber().toString());
                                cells.get(startRow + 2, START_DATE_COL + j).setValue(value.get().getActualNumber().toString());
                                total = total.add(value.get().getPlanNumber());
                                total2 = total2.add(value.get().getScheduleNumber());
                                total3 = total3.add(value.get().getActualNumber());
                            } else {
                                cells.get(startRow, START_DATE_COL + j).setValue("0");
                                cells.get(startRow + 1, START_DATE_COL + j).setValue("0");
                                cells.get(startRow + 2, START_DATE_COL + j).setValue("0");
                            }
                            Style valueStyle = commonStyle();
                            valueStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                            if (j == dataSource.getDateInfos().size() - 1 && hasPersonalTotal) valueStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOUBLE);
                            cells.get(startRow + 1, START_DATE_COL + j).setStyle(valueStyle);
                            valueStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
                            cells.get(startRow, START_DATE_COL + j).setStyle(valueStyle);
                            valueStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.DOTTED);
                            valueStyle.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
                            cells.get(startRow + 2, START_DATE_COL + j).setStyle(valueStyle);
                        }
                        cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(total.toString());
                        cells.get(startRow + 1, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(total2.toString());
                        cells.get(startRow + 2, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(total3.toString());
                        startRow += 3;
                    }
                    break;
                case EMPLOYMENT_PEOPLE:
                    Map<GeneralDate, Map<Employment, BigDecimal>> employmentMap = (Map<GeneralDate, Map<Employment, BigDecimal>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                    Map<GeneralDate, Map<CodeNameValue, BigDecimal>> empMap = employmentMap.entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e1 -> e1.getValue().entrySet().stream().collect(Collectors.toMap(
                                    e2 -> new CodeNameValue(e2.getKey().getEmploymentCode().v(), e2.getKey().getEmploymentName().v()),
                                    e2 -> e2.getValue()
                            ))
                    ));
                    List<CodeNameValue> employments = employmentRepo.findAll(companyId).stream().map(e -> new CodeNameValue(e.getEmploymentCode().v(), e.getEmploymentName().v())).collect(Collectors.toList());
                    this.printEmpClsJobContent(cells, category, empMap, employments, dataSource.getDateInfos(), hasPersonalTotal);
                    break;
                case POSITION_PEOPLE:
                    Map<GeneralDate, Map<JobTitleInfo, BigDecimal>> jobTitleMap = (Map<GeneralDate, Map<JobTitleInfo, BigDecimal>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                    Map<GeneralDate, Map<CodeNameValue, BigDecimal>> jobMap = jobTitleMap.entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e1 -> e1.getValue().entrySet().stream().collect(Collectors.toMap(
                                    e2 -> new CodeNameValue(e2.getKey().getJobTitleCode().v(), e2.getKey().getJobTitleName().v()),
                                    e2 -> e2.getValue()
                            ))
                    ));
                    List<CodeNameValue> jobTitles = jobTitleInfoRepo.findAll(companyId, dataSource.getPeriod().end()).stream().map(j -> new CodeNameValue(j.getJobTitleCode().v(), j.getJobTitleName().v())).collect(Collectors.toList());
                    this.printEmpClsJobContent(cells, category, jobMap, jobTitles, dataSource.getDateInfos(), hasPersonalTotal);
                    break;
                case CLASSIFICATION_PEOPLE:
                    Map<GeneralDate, Map<Classification, BigDecimal>> classificationMap = (Map<GeneralDate, Map<Classification, BigDecimal>>) dataSource.getWorkplaceTotalResult().getOrDefault(category, new HashMap<>());
                    Map<GeneralDate, Map<CodeNameValue, BigDecimal>> classMap = classificationMap.entrySet().stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e1 -> e1.getValue().entrySet().stream().collect(Collectors.toMap(
                                    e2 -> new CodeNameValue(e2.getKey().getClassificationCode().v(), e2.getKey().getClassificationName().v()),
                                    e2 -> e2.getValue()
                            ))
                    ));
                    List<CodeNameValue> classifications = classificationRepo.getAllManagementCategory(companyId).stream().map(c -> new CodeNameValue(c.getClassificationCode().v(), c.getClassificationName().v())).collect(Collectors.toList());
                    this.printEmpClsJobContent(cells, category, classMap, classifications, dataSource.getDateInfos(), hasPersonalTotal);
                    break;
                default:
                    break;
            }
        }
        empIndexes.add(startRow);
    }

    private void printHeaderF(Cells cells, int row, WorkplaceCounterCategory category, List<DateInformation> dateInformations, boolean hasPersonalTotal, boolean insertRow) {
        if (insertRow) {
            cells.insertRow(row);
            cells.insertRow(row);
        }
        // F1 part
        cells.get(row, PERSONAL_INFO_COLUMN).setValue(getText(category.nameId));
        this.setHeaderStyle(cells.get(row, PERSONAL_INFO_COLUMN), null, false, true, false, false, false, true);
        this.setHeaderStyle(cells.get(row + 1, PERSONAL_INFO_COLUMN), null, false, false, true, true, false, true);
        this.setHeaderStyle(cells.get(row, PERSONAL_INFO_COLUMN + 1), null, false, true, false, false, false, true);
        this.setHeaderStyle(cells.get(row + 1, PERSONAL_INFO_COLUMN + 1), null, false, false, true, true, false, true);
        cells.merge(row, PERSONAL_INFO_COLUMN, 2, 2, true);

        // F2 part
        cells.get(row, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(getText("KSU001_4134"));
        this.setHeaderStyle(cells.get(row, ADDITIONAL_PERSONAL_INFO_COLUMN), null, false, true, false, false, false, true);
        this.setHeaderStyle(cells.get(row + 1, ADDITIONAL_PERSONAL_INFO_COLUMN), null, false, false, true, true, false, true);
        cells.merge(row, ADDITIONAL_PERSONAL_INFO_COLUMN, 2, 1, true);

        // F3 part
        int startCol = START_DATE_COL;
        for (int i = 0; i < dateInformations.size(); i++) {
            DateInformation dateInfo = dateInformations.get(i);
            cells.get(row, startCol + i).setValue(dateInfo.getYmd().toString(i == 0 || dateInfo.getYmd().day() == 1 ? "M/d" : "d"));
            cells.get(row + 1, startCol + i).setValue(this.getDayOfWeek(dateInfo.getDayOfWeek()));
            this.setHeaderStyle(cells.get(row, startCol + i), dateInfo, false, true, false, false, i == dateInformations.size() - 1 && hasPersonalTotal, true);
            this.setHeaderStyle(cells.get(row + 1, startCol + i), dateInfo, false, false, true, true, i == dateInformations.size() - 1 && hasPersonalTotal, true);
        }
    }

    private void setWorkplaceLaborCostTimeTotalValue(Cells cells, List<DateInformation> dateInformations, Map<GeneralDate, Map<LaborCostAggregationUnit, BigDecimal>> laborCostTimeMap, AggregationUnitOfLaborCosts unit, LaborCostItemType itemType, boolean startUnit, boolean endUnit, boolean hasPesonalTotal) {
        String itemString;
        if (itemType == LaborCostItemType.TIME) itemString = getText("KSU001_59");
        else if (itemType == LaborCostItemType.AMOUNT) itemString = getText("KSU001_60");
        else itemString = getText("KSU001_61");
        cells.get(startRow, PERSONAL_INFO_COLUMN + 1).setValue(itemString);
        Style itemStyle = commonStyle();
        itemStyle.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(startUnit ? CellBorderType.THIN : CellBorderType.DOTTED);
        itemStyle.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(endUnit ? CellBorderType.THIN : CellBorderType.DOTTED);
        itemStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
        cells.get(startRow, PERSONAL_INFO_COLUMN + 1).setStyle(itemStyle);

        BigDecimal total = BigDecimal.ZERO;
        for (int j = 0; j < dateInformations.size(); j++) {
            DateInformation dateInfo = dateInformations.get(j);
            Map<LaborCostAggregationUnit, BigDecimal> mapUnitValue = laborCostTimeMap.getOrDefault(dateInfo.getYmd(), new HashMap<>());
            BigDecimal value = mapUnitValue.getOrDefault(new LaborCostAggregationUnit(unit, itemType), BigDecimal.ZERO);
            total = total.add(value);
            cells.get(startRow, START_DATE_COL + j).setValue(itemType == LaborCostItemType.TIME ? new TimeWithDayAttr(value.intValue()).getRawTimeWithFormat() : df.format(value.intValue()));
            itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(j == dateInformations.size() - 1 && hasPesonalTotal ? CellBorderType.DOUBLE : CellBorderType.DOTTED);
            itemStyle.setVerticalAlignment(TextAlignmentType.CENTER);
            itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
            cells.get(startRow, START_DATE_COL + j).setStyle(itemStyle);
        }

        cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(itemType == LaborCostItemType.TIME ? new TimeWithDayAttr(total.intValue()).getRawTimeWithFormat() : df.format(total.intValue()));
        itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
        itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOTTED);
        cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setStyle(itemStyle);
    }

    private int printEmpClsJobContent(Cells cells, WorkplaceCounterCategory category, Map<GeneralDate, Map<CodeNameValue, BigDecimal>> dataMap, List<CodeNameValue> targets, List<DateInformation> dateInfos, boolean hasPersonalTotal) {
        for (int i = 0; i < targets.size(); i++) {
            if (i != 0) {
                empIndexes.add(startRow);
                wkpTotalIndexes.put(startRow, category);
            }
            CodeNameValue master = targets.get(i);
            cells.get(startRow, PERSONAL_INFO_COLUMN).setValue(master.getName());
            Style itemStyle = commonStyle();
            itemStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
            cells.get(startRow, PERSONAL_INFO_COLUMN).setStyle(itemStyle);
            cells.get(startRow, PERSONAL_INFO_COLUMN + 1).setStyle(itemStyle);
            cells.merge(startRow, PERSONAL_INFO_COLUMN, 1, 2);

            BigDecimal total = BigDecimal.ZERO;
            for (int j = 0; j < dateInfos.size(); j++) {
                DateInformation dateInfo = dateInfos.get(j);
                Map<CodeNameValue, BigDecimal> valueMap = dataMap.getOrDefault(dateInfo.getYmd(), new HashMap<>());
                BigDecimal bdValue = valueMap.getOrDefault(master, BigDecimal.ZERO);
                cells.get(startRow, START_DATE_COL + j).setValue(bdValue.toString());
                total = total.add(bdValue);
                itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
                if (j == dateInfos.size() - 1 && hasPersonalTotal) itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOUBLE);
                cells.get(startRow, START_DATE_COL + j).setStyle(itemStyle);
            }
            cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setValue(total.toString());
            itemStyle.setHorizontalAlignment(TextAlignmentType.RIGHT);
            itemStyle.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.DOTTED);
            cells.get(startRow, ADDITIONAL_PERSONAL_INFO_COLUMN).setStyle(itemStyle);
            startRow++;
        }
        return startRow;
    }

    private void handlePageBreak(Worksheet worksheet, PersonalScheduleByWkpDataSource dataSource) {
        Cells cells = worksheet.getCells();
        boolean hasPersonalTotal = !dataSource.getOutputSetting().getPersonalCounterCategories().isEmpty();
        int start = START_DATA_ROW, totalColWidth = 17, maxRowPerPage;
        if (dataSource.getOutputSetting().getOutputItem().getAdditionalColumnUseAtr() == NotUseAtr.USE
                && dataSource.getOutputSetting().getOutputItem().getDetails().stream().filter(i -> i.getAdditionalInfo().isPresent()).count() > 0) {
            totalColWidth += 9;
        }
        totalColWidth += dataSource.getPeriod().datesBetween().size() * COLUMN_WIDTH;
        for (PersonalCounterCategory category : dataSource.getOutputSetting().getPersonalCounterCategories()) {
            switch (category) {
                case WORKING_HOURS:
                    totalColWidth += 3 * COLUMN_WIDTH;
                    break;
                case MONTHLY_EXPECTED_SALARY:
                case CUMULATIVE_ESTIMATED_SALARY:
                    totalColWidth += 2 * COLUMN_WIDTH;
                    break;
                case TIMES_COUNTING_1:
                case TIMES_COUNTING_2:
                case TIMES_COUNTING_3:
                    TimesNumberCounterType counterType;
                    if (category == PersonalCounterCategory.TIMES_COUNTING_1)
                        counterType = TimesNumberCounterType.PERSON_1;
                    else if (category == PersonalCounterCategory.TIMES_COUNTING_2)
                        counterType = TimesNumberCounterType.PERSON_2;
                    else counterType = TimesNumberCounterType.PERSON_3;
                    Optional<TimesNumberCounterSelection> timesNumberCounterSelection = timesNumberCounterSelectionRepo.get(AppContexts.user().companyId(), counterType);
                    if (timesNumberCounterSelection.isPresent()) {
                        List<TotalTimes> totalTimes = totalTimesRepo.getTotalTimesDetailByListNo(AppContexts.user().companyId(), timesNumberCounterSelection.get().getSelectedNoList()).stream()
                                .filter(t -> t.getUseAtr() == UseAtr.Use)
                                .collect(Collectors.toList());
                        totalColWidth += totalTimes.size() * COLUMN_WIDTH;
                    } else {
                        totalColWidth += COLUMN_WIDTH;
                    }
                    break;
                case ATTENDANCE_HOLIDAY_DAYS:
                    totalColWidth += 2 * COLUMN_WIDTH;
                    break;
                default:
                    break;
            }
        }
        maxRowPerPage = (int) Math.floor(totalColWidth / 3.9);
        int addedHeaderRows = 0;
        for (int i = 0; i < empIndexes.size(); i++) {
            if (empIndexes.get(i) + addedHeaderRows - start > maxRowPerPage) {
                if (wkpTotalIndexes.containsKey(empIndexes.get(i - 1))) {
                    this.printHeaderF(cells, empIndexes.get(i - 1)  + addedHeaderRows, wkpTotalIndexes.get(empIndexes.get(i - 1)), dataSource.getDateInfos(), hasPersonalTotal, true);
					startRow += 2;
                }
                start = empIndexes.get(i - 1) + addedHeaderRows;
                // adding page break must be after inserting rows
                worksheet.getHorizontalPageBreaks().add(empIndexes.get(i - 1).intValue() + addedHeaderRows);
                // increasing addedHeaderRows must be last step
                if (wkpTotalIndexes.containsKey(empIndexes.get(i - 1))) {
                    addedHeaderRows += 2;
                }
            }
        }
    }
}
