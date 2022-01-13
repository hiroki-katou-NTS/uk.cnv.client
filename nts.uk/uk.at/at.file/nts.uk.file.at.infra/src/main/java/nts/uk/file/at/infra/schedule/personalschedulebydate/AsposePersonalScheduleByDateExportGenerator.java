package nts.uk.file.at.infra.schedule.personalschedulebydate;

import com.aspose.cells.*;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.calendar.DayOfWeek;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.shift.specificdayset.primitives.SpecificName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;
import nts.uk.ctx.at.shared.dom.worktime.service.WorkTimeForm;
import nts.uk.ctx.sys.portal.dom.enums.MenuAtr;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.PersonalScheduleByDateDataSource;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.PersonalScheduleByDateExportGenerator;
import nts.uk.file.at.app.export.schedule.personalschedulebydate.dto.EmployeeWorkScheduleResultDto;
import nts.uk.screen.at.app.ksu003.start.dto.ChangeableWorkTimeDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class AsposePersonalScheduleByDateExportGenerator extends AsposeCellsReportGenerator implements PersonalScheduleByDateExportGenerator {
    private static final String TEMPLATE_FILE = "report/KSU003.xlsx";
    private static final String EXCEL_EXT = ".xlsx";
    private static final int MAX_ROW_IN_PAGE = 68;
    private static final int MAX_ROW_HEADER_IN_PAGE = 8;
    private static final int MAX_EMPLOYEE_PER_PAGE = 30;
    private final String SPACE = "　";
    private final String EMPTY = "";
    private static final String PRINT_AREA = "A1:BE";
    private static int MINUTES_IN_AN_HOUR = 60;
    private static int ROUNDING_INCREMENTS = 5;
    private static final int MAX_ROW_B5 = 4;

    @Inject
    private StandardMenuRepository standardMenuRepo;

    @Override
    public void generate(FileGeneratorContext context, PersonalScheduleByDateDataSource dataSource) {
        try {
            long startTime = System.nanoTime();
            List<StandardMenu> menus = standardMenuRepo.findAll(AppContexts.user().companyId());
            String menuName = menus.stream().filter(i -> i.getSystem().value == 1 && i.getMenuAtr() == MenuAtr.Menu && i.getProgramId().equals("KSU003"))
                    .findFirst().map(i -> i.getDisplayName().v()).orElse(TextResource.localize("KSU003_138"));

            AsposeCellsReportContext reportContext = createContext(TEMPLATE_FILE);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();

            Worksheet wsSource = worksheets.get(2);
            Worksheet wsDestination = worksheets.get(1);
            wsDestination.setName(menuName);

            pageSetting(wsDestination, dataSource);
            printHeader(wsDestination, dataSource);
            printContent(wsDestination, wsSource, dataSource);

            worksheets.removeAt(2);
            worksheets.setActiveSheetIndex(1);
            reportContext.processDesigner();

            // Save as excel file
            reportContext.saveAsExcel(createNewFile(context, getReportName(menuName + EXCEL_EXT)));
            System.out.println("Thoi gian export excel: " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ページヘッダ① : Area A
     */
    private void pageSetting(Worksheet worksheet, PersonalScheduleByDateDataSource dataSource) {
        PageSetup pageSetup = worksheet.getPageSetup();
        // A1_1
        pageSetup.setHeader(0, "&9&\"ＭＳ ゴシック\"" + dataSource.getCompanyInfo().getCompanyName());
        // A1_2
        pageSetup.setHeader(1, "&16&\"ＭＳ ゴシック\"" + getText("KSU003_138"));
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN);
        // A1_3, A1_4
        pageSetup.setHeader(2, "&9&\"ＭＳ ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage &P");
    }

    /**
     * ページヘッダ② : Area B & Header C
     */
    private void printHeader(Worksheet worksheet, PersonalScheduleByDateDataSource dataSource) throws Exception {
        Cells cells = worksheet.getCells();
        val dateInfo = dataSource.getDateInformation();
        val orgInfo = dataSource.getDisplayInfoOrganization();
        val isDoubleWorkDisplay = dataSource.getQuery().isDoubleWorkDisplay();

        // B1_1, B1_2
        cells.get(3, 0).setValue(getText("KSU003_139") + dateInfo.getYmd() + '(' + getDayOfWeek(dateInfo.getDayOfWeek()) + ')');
        // B2_1, B2_2, B2_3
        val workplaceLabel = getText(dataSource.getQuery().getOrgUnit() == TargetOrganizationUnit.WORKPLACE.value ? getText("KSU003_140") : getText("KSU003_141"));
        cells.get(4, 0).setValue(workplaceLabel + orgInfo.getCode() + SPACE + orgInfo.getDisplayName());
        // B3_1, B3_2
        val companyEvent = dateInfo.getOptCompanyEventName().isPresent() ? dateInfo.getOptCompanyEventName().get().v() : EMPTY;
        cells.get(3, 5).setValue(getText("KSU003_142") + companyEvent);
        // B4_1, B4_2
        if (dataSource.getQuery().getOrgUnit() == TargetOrganizationUnit.WORKPLACE.value) {
            cells.get(4, 5).setValue(getText("KSU003_143") + (dateInfo.getOptWorkplaceEventName().isPresent() ? dateInfo.getOptWorkplaceEventName().get().v() : EMPTY));
            setBottomBorder(cells, 4, 5, 11);
        }

        // B5
        printB5(cells, dateInfo.getListSpecDayNameCompany(), dateInfo.getListSpecDayNameWorkplace());

        // B6: Color description
        int widthPixel = cells.getColumnWidthPixel(8);
        ShapeCollection shapes = worksheet.getShapes();
        drawSample(shapes, 0, 30, widthPixel, Color.fromArgb(204, 204, 255), null);
        drawSample(shapes, 0, 34, widthPixel, Color.fromArgb(255, 192, 0), null);
        drawSample(shapes, 0, 38, widthPixel, Color.fromArgb(255, 255, 0), null);
        drawSample(shapes, 0, 42, widthPixel, Color.fromArgb(111, 165, 39), null);
        drawSample(shapes, 0, 46, widthPixel, Color.fromArgb(195, 214, 155), null);
        drawSample(shapes, 0, 51, widthPixel, Color.fromArgb(254, 223, 230), null);
        cells.get(0, 31).setValue(getText("KSU003_144"));     // B6_1
        cells.get(0, 35).setValue(getText("KSU003_146"));     // B6_3
        cells.get(0, 39).setValue(getText("KSU003_148"));     // B6_5
        cells.get(0, 43).setValue(getText("KSU003_150"));     // B6_7
        cells.get(0, 47).setValue(getText("KSU003_152"));     // B6_9
        cells.get(0, 52).setValue(getText("KSU003_154"));     // B6_11

        drawSample(shapes, 2, 30, widthPixel, Color.fromArgb(255, 153, 153), null);
        drawSample(shapes, 2, 34, widthPixel, Color.fromArgb(0, 255, 204), null);
        drawSample(shapes, 2, 42, widthPixel, Color.fromArgb(196, 189, 151), null);
        drawSample(shapes, 2, 46, widthPixel, Color.fromArgb(235, 241, 222), null);
        drawSample(shapes, 2, 51, widthPixel, Color.fromArgb(255, 204, 255), null);
        cells.get(2, 31).setValue(getText("KSU003_145"));     // B6_2
        cells.get(2, 35).setValue(getText("KSU003_147"));     // B6_4
        cells.get(2, 43).setValue(getText("KSU003_149"));     // B6_6
        cells.get(2, 47).setValue(getText("KSU003_153"));     // B6_10
        cells.get(2, 52).setValue(getText("KSU003_155"));     // B6_12

        drawSample(shapes, 4, 30, widthPixel, Color.fromArgb(24, 23, 23), null);
        drawSample(shapes, 4, 34, widthPixel, Color.fromArgb(255, 153, 255), null);
        drawSample(shapes, 4, 38, widthPixel, Color.fromArgb(0, 102, 255), null);
        cells.get(4, 31).setValue(getText("KSU003_156"));     // B6_13
        cells.get(4, 35).setValue(getText("KSU003_157"));     // B6_14
        cells.get(4, 39).setValue(getText("KSU003_158"));     // B6_15

        // Header C:
        cells.get(6, 0).setValue(getText("KSU003_159"));      // C1_1
        if (!isDoubleWorkDisplay) {      //２回勤務表示 = 非表示
            cells.get(6, 1).setValue(charBreak(getText("KSU003_165"), 2));  // C2_2_1
            cells.get(6, 2).setValue(charBreak(getText("KSU003_166"), 2));  // C2_2_2
            cells.get(6, 3).setValue(charBreak(getText("KSU003_167"), 2));  // C2_2_3
            cells.get(6, 4).setValue(charBreak(getText("KSU003_168"), 2));  // C2_2_4
            cells.get(6, 5).setValue(charBreak(getText("KSU003_169"), 2));  // C2_2_5
            cells.get(6, 6).setValue(charBreak(getText("KSU003_170"), 2));  // C2_2_6
        } else {                         //２回勤務表示 = 表示
            cells.get(6, 1).setValue(charBreak(getText("KSU003_171"), 2));  // C2_3_1
            cells.get(6, 2).setValue(charBreak(getText("KSU003_172"), 2));  // C2_3_2
            cells.get(6, 3).setValue(charBreak(getText("KSU003_173"), 2));  // C2_3_3
            cells.get(6, 4).setValue(charBreak(getText("KSU003_174"), 2));  // C2_3_4
            cells.get(7, 3).setValue(getText("KSU003_175"));  // C2_3_5
            cells.get(7, 4).setValue(getText("KSU003_176"));  // C2_3_6
            cells.get(6, 5).setValue(charBreak(getText("KSU003_177"), 2));  // C2_3_7
            cells.get(6, 6).setValue(charBreak(getText("KSU003_178"), 2));  // C2_3_8
        }

        // Set style by each column
        setStyleEmpWorkInfo(cells, 6, isDoubleWorkDisplay);

        // C3_1 or C3_3: Graph ruler header
        int graphStartTimeInitValue = dataSource.getQuery().getGraphStartTime();
        int graphStartTime = dataSource.getQuery().getGraphStartTime();
        if (isEven(graphStartTime)) {          // GraphStartTime is even
            for (int column = 7; column <= 55; column += 4) {
                if (graphStartTimeInitValue == 0 && graphStartTime == 24) {
                    graphStartTime = 0;
                }
                cells.get(6, column).setValue(graphStartTime);
                graphStartTime += 2;
            }
        } else {                                // GraphStartTime is odd
            cells.get(6, 7).setValue(graphStartTime);
            graphStartTime += 1;                // +1 => value is even
            for (int column = 9; column <= 53; column += 4) {
                cells.get(6, column).setValue(graphStartTime);
                graphStartTime += 2;
            }
        }
    }

    private void printContent(Worksheet wsDestination, Worksheet wsSource, PersonalScheduleByDateDataSource dataSource) throws Exception {
        Cells cells = wsDestination.getCells();
        Cells cellsTemplate = wsSource.getCells();
        ShapeCollection shapes = wsDestination.getShapes();
        HorizontalPageBreakCollection hPageBreaks = wsDestination.getHorizontalPageBreaks();
        cells.deleteRows(9, 60);

        // Set CopyOptions.ReferToDestinationSheet to true
        CopyOptions options = new CopyOptions();
        options.setReferToDestinationSheet(true);
        // Set PasteOptions
        PasteOptions pasteOptions = new PasteOptions();
        pasteOptions.setPasteType(PasteType.ALL);
        pasteOptions.setOnlyVisibleCells(true);

        int rowCount = 9;
        int pageIndex = 0;
        int elementsPerPage = 0;
        val employeeInfoList = dataSource.getEmployeeInfoList();
        val employeeWorkScheduleList = dataSource.getEmployeeWorkScheduleList();
        val isDoubleWorkDisplay = dataSource.getQuery().isDoubleWorkDisplay();
        val displayActual = dataSource.getQuery().isDisplayActual();
        int graphStartTime = dataSource.getQuery().getGraphStartTime();
        int pixelOfColumn = cells.getColumnWidthPixel(8);

        for (int i = 1; i <= employeeInfoList.size(); i++) {
            val empInfo = employeeInfoList.get(i - 1);
            if (i == employeeInfoList.size())
                if (employeeInfoList.size() == 1)
                    cells.copyRows(cellsTemplate, isDoubleWorkDisplay ? 18 : 21, rowCount, 2);
                else
                    cells.copyRows(cellsTemplate, isDoubleWorkDisplay ? 55 : 49, rowCount, 2);
            else
                cells.copyRows(cellsTemplate, isDoubleWorkDisplay ? (i == 1 || elementsPerPage == 0 ? 13 : elementsPerPage == 29 ? 55 : 15) : (elementsPerPage == 29 ? 49 : 9), rowCount, 2);
//                cells.copyRows(cellsTemplate, isDoubleWorkDisplay ? (i == 1 ? 13 : 11) : (elementsPerPage == 29 ? 49 : 9), rowCount, 2);

            cells.clearContents(CellArea.createCellArea(rowCount, 0, cells.getMaxRow(), cells.getMaxColumn()));

            // C1_2 + C1_3
            cells.get(rowCount, 0).setValue(empInfo.getEmployeeCode() + SPACE + empInfo.getBusinessName());

            EmployeeWorkScheduleResultDto item = employeeWorkScheduleList.stream().filter(x -> x.getEmployeeId().equals(empInfo.getEmployeeId())).findFirst().orElse(null);
            if (item != null) {
                if (!isDoubleWorkDisplay) {
                    cells.get(rowCount, 1).setValue(getWorkName(item.getWorkTypeCode(), item.getWorkTypeName()));  // C2_2_7: WorkType
                    cells.get(rowCount, 2).setValue(getWorkName(item.getWorkTimeCode(), item.getWorkTimeName()));  // C2_2_8: WorkTime
                    cells.get(rowCount, 3).setValue(minuteToTime(item.getStartTime1()));          // C2_2_9
                    cells.get(rowCount, 4).setValue(minuteToTime(item.getEndTime1()));            // C2_2_10
                    cells.get(rowCount, 5).setValue(minuteToTime(item.getTotalWorkingHours()));   // C2_2_11
                    cells.get(rowCount, 6).setValue(minuteToTime(item.getTotalBreakTime()));      // C2_2_12
                } else {
                    cells.get(rowCount, 1).setValue(getWorkName(item.getWorkTypeCode(), item.getWorkTypeName()));  // C2_3_9:  WorkType
                    cells.get(rowCount, 2).setValue(getWorkName(item.getWorkTimeCode(), item.getWorkTimeName()));  // C2_3_10: WorkTime
                    cells.get(rowCount, 3).setValue(minuteToTime(item.getStartTime1()));              // C2_3_11
                    cells.get(rowCount, 4).setValue(minuteToTime(item.getEndTime1()));                // C2_3_12
                    cells.get(rowCount + 1, 3).setValue(minuteToTime(item.getStartTime2()));     // C2_3_13
                    cells.get(rowCount + 1, 4).setValue(minuteToTime(item.getEndTime2()));       // C2_3_14
                    cells.get(rowCount, 5).setValue(minuteToTime(item.getTotalWorkingHours()));      // C2_3_15
                    cells.get(rowCount, 6).setValue(minuteToTime(item.getTotalBreakTime()));         // C2_3_16
                }

                // C3_2_1
                if (item.getWorkType() != null && item.getWorkType().equals(WorkTimeForm.FIXED.value)) {
                    val shape1a = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getStartTime1(), item.getEndTime1());
                    if (shape1a.getColumn() != null) {
                        drawRectangle(shapes, rowCount, shape1a.getColumn(), shape1a.getWidth(), shape1a.getLeft(), getBarColor(BarType.FIXED_WORKING_HOURS), false, null);
                    }

                    if (isDoubleWorkDisplay && item.getStartTime2() != null && item.getEndTime2() != null) {
                        val shape1b = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getStartTime2(), item.getEndTime2());
                        if (shape1b.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape1b.getColumn(), shape1b.getWidth(), shape1b.getLeft(), getBarColor(BarType.FIXED_WORKING_HOURS), false, null);
                        }
                    }
                }

                // C3_2_4
                if (item.getWorkType() != null && item.getWorkType().equals(WorkTimeForm.FLOW.value)) {
                    val shape4a = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getStartTime1(), item.getEndTime1());
                    if (shape4a.getColumn() != null) {
                        drawRectangle(shapes, rowCount, shape4a.getColumn(), shape4a.getWidth(), shape4a.getLeft(), getBarColor(BarType.FLOWING_WORKING_HOURS), false, null);
                    }

                    if (isDoubleWorkDisplay && item.getStartTime2() != null && item.getEndTime2() != null) {
                        val shape4b = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getStartTime2(), item.getEndTime2());
                        if (shape4a.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape4b.getColumn(), shape4b.getWidth(), shape4b.getLeft(), getBarColor(BarType.FLOWING_WORKING_HOURS), false, null);
                        }
                    }
                }

                // C3_2_5
                if (item.getWorkType() != null && item.getWorkType().equals(WorkTimeForm.FLEX.value)) {
                    val shape5 = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getStartTime1(), item.getEndTime1());
                    if (shape5.getColumn() != null) {
                        drawRectangle(shapes, rowCount, shape5.getColumn(), shape5.getWidth(), shape5.getLeft(), getBarColor(BarType.FLEX_WORKING_HOURS), false, null);
                    }

                    // C3_2_6
                    if (item.getCoreStartTime() != null && item.getCoreEndTime() != null) {
                        val shape6 = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getCoreStartTime(), item.getCoreEndTime());
                        if (shape6.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape6.getColumn(), shape6.getWidth(), shape6.getLeft(), getBarColor(BarType.CORE_TIME), false, null);
                        }
                    }
                }

                // C3_2_3
                if (!item.getOverTimeList().isEmpty()) {
                    for (ChangeableWorkTimeDto overTime : item.getOverTimeList()) {
                        TimeCheckedDto timeChecked = validateTime(graphStartTime, overTime.getStartTime(), overTime.getEndTime(), new TimeRangeLimitDto(item.getStartTime1(), item.getEndTime1()),
                                (isDoubleWorkDisplay && item.getStartTime2() != null && item.getEndTime2() != null) ? new TimeRangeLimitDto(item.getStartTime2(), item.getEndTime2()) : null);
                        if (timeChecked.getStartTime() == null || timeChecked.getEndTime() == null) continue;

                        val shape3 = calculateConvertToShape(pixelOfColumn, graphStartTime, timeChecked.getStartTime(), timeChecked.getEndTime());
                        if (shape3.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape3.getColumn(), shape3.getWidth(), shape3.getLeft(), getBarColor(BarType.OVERTIME_HOURS), false, null);
                        }
                    }
                }

                // C3_2_2
                if (!item.getBreakTimeList().isEmpty()) {
                    val breakTimeList = item.getBreakTimeList();
                    for (BreakTimeSheet breakTime : breakTimeList) {
                        TimeCheckedDto timeChecked = validateTime(graphStartTime, breakTime.getStartTime().v(), breakTime.getEndTime().v(), new TimeRangeLimitDto(item.getStartTime1(), item.getEndTime1()),
                                (isDoubleWorkDisplay && item.getStartTime2() != null && item.getEndTime2() != null) ? new TimeRangeLimitDto(item.getStartTime2(), item.getEndTime2()) : null);
                        if (timeChecked.getStartTime() == null || timeChecked.getEndTime() == null) continue;

                        val shape2 = calculateConvertToShape(pixelOfColumn, graphStartTime, timeChecked.getStartTime(), timeChecked.getEndTime());
                        if (shape2.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape2.getColumn(), shape2.getWidth(), shape2.getLeft(), getBarColor(BarType.BREAK_TIME), false, null);
                        }
                    }
                }

                // C3_2_8
                if (!item.getChildCareShortTimeList().isEmpty()) {
                    for (val time : item.getChildCareShortTimeList()) {
                        val shape8 = calculateConvertToShape(pixelOfColumn, graphStartTime, time.getStartTime(), time.getEndTime());
                        if (shape8.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape8.getColumn(), shape8.getWidth(), shape8.getLeft(), getBarColor(BarType.CHILDCARE_SHORT_TIME), false, null);
                        }
                    }
                }

                // C3_2_7
                if (!item.getListTimeVacationAndType().isEmpty()) {
                    for (val timeVacation : item.getListTimeVacationAndType()) {
                        for (val time : timeVacation.getTimeVacation().getTimeZone()) {
                            val shape7 = calculateConvertToShape(pixelOfColumn, graphStartTime, time.getStart(), time.getEnd());
                            if (shape7.getColumn() != null) {
                                drawRectangle(shapes, rowCount, shape7.getColumn(), shape7.getWidth(), shape7.getLeft(), getBarColor(BarType.TIME_VACATION), false, null);
                            }
                        }
                    }
                }

                if (displayActual) {
                    // C3_2_14
                    if (item.getActualStartTime1() != null && item.getActualEndTime1() != null) {
                        val shape14a = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getActualStartTime1(), item.getActualEndTime1());
                        if (shape14a.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape14a.getColumn(), shape14a.getWidth(), shape14a.getLeft(), getBarColor(BarType.WORKING_HOURS_ACTUAL), true, null);
                        }
                    }

                    if (isDoubleWorkDisplay && item.getActualStartTime2() != null && item.getActualEndTime2() != null) {
                        val shape14b = calculateConvertToShape(pixelOfColumn, graphStartTime, item.getActualStartTime2(), item.getActualEndTime2());
                        if (shape14b.getColumn() != null) {
                            drawRectangle(shapes, rowCount, shape14b.getColumn(), shape14b.getWidth(), shape14b.getLeft(), getBarColor(BarType.WORKING_HOURS_ACTUAL), true, null);
                        }
                    }

                    // C3_2_16
                    if (!item.getOverTimeList().isEmpty() && item.getActualStartTime1() != null && item.getActualEndTime1() != null) {
                        for (val time : item.getOverTimeList()) {
                            TimeCheckedDto timeChecked = validateTime(graphStartTime, time.getStartTime(), time.getEndTime(), new TimeRangeLimitDto(item.getActualStartTime1(), item.getActualEndTime1()),
                                    (isDoubleWorkDisplay && item.getActualStartTime2() != null && item.getActualEndTime2() != null) ? new TimeRangeLimitDto(item.getActualStartTime2(), item.getActualEndTime2()) : null);
                            if (timeChecked.getStartTime() == null || timeChecked.getEndTime() == null) continue;

                            val shape16 = calculateConvertToShape(pixelOfColumn, graphStartTime, timeChecked.getStartTime(), timeChecked.getEndTime());
                            if (shape16.getColumn() != null) {
                                drawRectangle(shapes, rowCount, shape16.getColumn(), shape16.getWidth(), shape16.getLeft(), getBarColor(BarType.OVERTIME_HOURS_ACTUAL), true, null);
                            }
                        }
                    }

                    // C3_2_15
                    if (!item.getActualBreakTimeList().isEmpty() && item.getActualStartTime1() != null && item.getActualEndTime1() != null) {
                        for (val time : item.getActualBreakTimeList()) {     // max 10
                            TimeCheckedDto timeChecked = validateTime(graphStartTime, time.getStartTime().v(), time.getEndTime().v(), new TimeRangeLimitDto(item.getActualStartTime1(), item.getActualEndTime1()),
                                    (isDoubleWorkDisplay && item.getActualStartTime2() != null && item.getActualEndTime2() != null) ? new TimeRangeLimitDto(item.getActualStartTime2(), item.getActualEndTime2()) : null);
                            if (timeChecked.getStartTime() == null || timeChecked.getEndTime() == null) continue;

                            val shape15 = calculateConvertToShape(pixelOfColumn, graphStartTime, timeChecked.getStartTime(), timeChecked.getEndTime());
                            if (shape15.getColumn() != null) {
                                drawRectangle(shapes, rowCount, shape15.getColumn(), shape15.getWidth(), shape15.getLeft(), getBarColor(BarType.BREAK_TIME_ACTUAL), true, null);
                            }
                        }
                    }
                }
            }

            rowCount += 2;
            elementsPerPage += 1;

            // Paging
            if (elementsPerPage == MAX_EMPLOYEE_PER_PAGE) {
                cells.copyRows(cellsTemplate, 57, rowCount, 1);  // close ruler
                rowCount += 1;     // close ruler
                hPageBreaks.add(rowCount);
                pageIndex += 1;
                cells.copyRows(cells, 0, rowCount, 9);
                rowCount += 9;
                elementsPerPage = 0;
            }

            // Ruler close
            if (i == employeeInfoList.size()) {
                cells.copyRows(cellsTemplate, 57, rowCount, 1);
                rowCount += 1;
            }
        }
        PageSetup pageSetup = wsDestination.getPageSetup();
        pageSetup.setPrintArea(PRINT_AREA + rowCount);
    }

    /**
     * Calculate data to draw shape
     *
     * @param graphStartTime graphStartTime
     * @param start          start
     * @param end            end
     * @return DrawRectangleProperties
     */
    private DrawRectangleProperties calculateConvertToShape(int pixelOfColumn, int graphStartTime, Integer start, Integer end) {
        if (start == null && end == null) return new DrawRectangleProperties(null, null, null);

        // Check time limit
        val timeChecked = checkRangeLimit(graphStartTime, start, end);
        if (timeChecked == null) return new DrawRectangleProperties(null, null, null);

        // Convert to hour and minute
        val timeConverted = convertToHourMinute(timeChecked.getStartTime(), timeChecked.getEndTime());

        // Get map with key is Hour and value is column
        val hourColumnMap = getMapHourColumn(graphStartTime);

        val startTime = timeConverted.getStart();
        val endTime = timeConverted.getEnd();
        int columnStart = hourColumnMap.containsKey(startTime.getHour()) ? hourColumnMap.get(startTime.getHour()) + 1 : Ksu003Utils.findFirstEntry(hourColumnMap).getValue();

        int minuteStart = roundUp(startTime.getMinute(), ROUNDING_INCREMENTS);
        if (minuteStart >= 30 && minuteStart <= 60) {
            columnStart += 1;
            minuteStart -= 30;
        }
        int left = 0;
        if (minuteStart != 0) {
            left = Math.round(calcRatioCell(pixelOfColumn) * minuteStart);
        }

        int columnEnd = hourColumnMap.get(endTime.getHour()) == null ? 0 : hourColumnMap.get(endTime.getHour()) + 1;
        int minuteEnd = roundDown(endTime.getMinute(), ROUNDING_INCREMENTS);
        if (minuteEnd >= 30 && minuteEnd <= 60) {
            columnEnd += 1;
            minuteEnd -= 30;
        }

        int shapeWidth = minuteEnd == 0
                ? (columnEnd * pixelOfColumn) - (columnStart * pixelOfColumn) - left
                : ((columnEnd * pixelOfColumn) + Math.round(calcRatioCell(pixelOfColumn) * minuteEnd)) - (columnStart * pixelOfColumn) - left;
//        System.out.println("Input start: " + start + " => " + startTime.getHour() + ":" + startTime.getMinute() + ";   " + "Input end: " + end + " => " + endTime.getHour() + ":" + endTime.getMinute());
//        System.out.println("minuteStart: " + minuteStart + " => minuteEnd: " + minuteEnd + ";   " + "columnStart: " + columnStart + " => columnEnd: " + columnEnd);
//        System.out.println("left: " + left + ";   " + "shapeWidth: " + shapeWidth);

        return new DrawRectangleProperties(columnStart, left, shapeWidth);
    }

    private TimeCheckedDto validateTime(int graphStartTime, Integer start, Integer end, TimeRangeLimitDto timeRange1, TimeRangeLimitDto timeRange2) {
        // Check data time in range of ruler time?
        val timeChecked = checkRangeLimit(graphStartTime, start, end);

        // Check overlap
        val overlapChecked = timeChecked != null ? checkOverlapRange(timeChecked.getStartTime(), timeChecked.getEndTime(), timeRange1, timeRange2) : null;
        return new TimeCheckedDto(overlapChecked != null ? overlapChecked.getStartTime() : null, overlapChecked != null ? overlapChecked.getEndTime() : null);
    }

    /**
     * Check limit time with ruler of time in excel
     *
     * @param graphStartTime graphStartTime
     * @param start          startTime
     * @param end            endTime
     * @return TimeCheckedDto
     */
    private TimeCheckedDto checkRangeLimit(int graphStartTime, Integer start, Integer end) {
        val timeLimit = getTimeLimit(graphStartTime, OutputType.TOTAL_MINUTE);
        if (!isInRange(start, timeLimit.getMinLimit(), timeLimit.getMaxLimit()) && !isInRange(end, timeLimit.getMinLimit(), timeLimit.getMaxLimit())) {
            return null;
        }
        if (start > timeLimit.getMaxLimit()) return null;
        if (!isInRange(start, timeLimit.getMinLimit(), timeLimit.getMaxLimit()) || !isInRange(end, timeLimit.getMinLimit(), timeLimit.getMaxLimit())) {
            if (start < timeLimit.getMinLimit()) {
                start = timeLimit.getMinLimit();
            }

            if (end > timeLimit.getMaxLimit()) {
                end = timeLimit.getMaxLimit();
            }
        }

        return new TimeCheckedDto(start, end);
    }

    /**
     * Get min time limit and max time limit
     *
     * @param graphStartTime graphStartTime
     * @return TimeRangeLimitDto
     */
    private TimeRangeLimitDto getTimeLimit(int graphStartTime, OutputType type) {
        Integer minLimit = null, maxLimit = null;
        val mapHourAndColumn = getMapHourColumn(graphStartTime);
        val firstEntry = Ksu003Utils.findFirstEntry(mapHourAndColumn);

        switch (type) {
            case TOTAL_MINUTE:
                minLimit = firstEntry.getKey() * MINUTES_IN_AN_HOUR;
                maxLimit = Ksu003Utils.findLastEntryKey(mapHourAndColumn) * MINUTES_IN_AN_HOUR;
                break;
            case HOUR:
                minLimit = firstEntry.getKey();
                maxLimit = Ksu003Utils.findLastEntryKey(mapHourAndColumn);
                break;
            case COLUMN:
                minLimit = firstEntry.getValue();
                maxLimit = Ksu003Utils.findLastEntryValue(mapHourAndColumn);
                break;
        }

        return new TimeRangeLimitDto(minLimit, maxLimit);
    }

    /**
     * Check overlap range
     *
     * @param start start
     * @param end   end
     * @return TimeRangeLimitDto
     */
    private TimeCheckedDto checkOverlapRange(Integer start, Integer end, TimeRangeLimitDto rangeTime1, TimeRangeLimitDto rangeTime2) {
        Integer startTime = start, endTime = end;

        if (rangeTime2 == null) {
            if (!isOverlap(start, end, rangeTime1.getMinLimit(), rangeTime1.getMaxLimit())) return null;
        } else {
            if (!isOverlap(start, end, rangeTime1.getMinLimit(), rangeTime1.getMaxLimit()) && !isOverlap(start, end, rangeTime2.getMinLimit(), rangeTime2.getMaxLimit()))
                return null;
        }

        if (isOverlap(start, end, rangeTime1.getMinLimit(), rangeTime1.getMaxLimit())) {
            if (start < rangeTime1.getMinLimit())
                startTime = rangeTime1.getMinLimit();
            if (end > rangeTime1.getMaxLimit())
                endTime = rangeTime1.getMaxLimit();
        } else if (rangeTime2 != null && isOverlap(start, end, rangeTime2.getMinLimit(), rangeTime2.getMaxLimit())) {
            if (start < rangeTime2.getMinLimit())
                startTime = rangeTime2.getMinLimit();
            if (end > rangeTime2.getMaxLimit())
                endTime = rangeTime2.getMaxLimit();
        }

        return new TimeCheckedDto(startTime, endTime);
    }

    private <T extends TimeCheckedDto> List<T> checkContinuityOfTime(List<T> sources) {
        List<T> destinations = new ArrayList<>();
        if (sources.size() > 0 && sources.size() <= 1) return sources;
        for (int i = 1; i < sources.size(); i++) {
            T firstItem = sources.get(i - 1);
            if (firstItem.getEndTime().equals(sources.get(i).getStartTime())) {
                firstItem.setEndTime(sources.get(i).getEndTime());
                destinations.add(firstItem);
            }
        }
        return destinations;
    }

    /**
     * Check overlap range for actual time
     *
     * @param start start
     * @param end   end
     * @return TimeRangeLimitDto
     */
    private TimeRangeLimitDto checkOverlapRangeActualTime(TimeRangeLimitDto rangeTime, Integer start, Integer end) {
        Integer startTime = start, endTime = end;
        if (start < rangeTime.getMinLimit())
            startTime = rangeTime.getMinLimit();
        if (end > rangeTime.getMaxLimit())
            endTime = rangeTime.getMaxLimit();

        return new TimeRangeLimitDto(startTime, endTime);
    }

    /**
     * Convert to hour + minute
     *
     * @param start start
     * @param end   end
     * @return HourMinuteInfo
     */
    private HourMinuteInfo convertToHourMinute(int start, int end) {
        val startTime = minuteToTime(start);
        String[] hourMinutesStart = startTime.trim().split(":");

        val endTime = minuteToTime(end);
        String[] hourMinutesEnd = endTime.trim().split(":");

        return new HourMinuteInfo(
                new HourMinuteValue(Integer.parseInt(hourMinutesStart[0]), Integer.parseInt(hourMinutesStart[1])),
                new HourMinuteValue(Integer.parseInt(hourMinutesEnd[0]), Integer.parseInt(hourMinutesEnd[1]))
        );
    }

    /**
     * Map Hour and column
     *
     * @param graphStartTime graphStartTime
     * @return Map
     */
    private Map<Integer, Integer> getMapHourColumn(int graphStartTime) {
        Map<Integer, Integer> mapHourAndColumn = new LinkedHashMap<>(); // Map<Hour,Column>
        if (isEven(graphStartTime)) {
            for (int column = 7; column <= 55; column += 2) {
                mapHourAndColumn.put(graphStartTime, column);
                graphStartTime += 1;
            }
        } else {
            mapHourAndColumn.put(graphStartTime, 7);
            graphStartTime += 1;                // +1 => value is even
            for (int column = 9; column <= 55; column += 2) {
                mapHourAndColumn.put(graphStartTime, column);
                graphStartTime += 1;
            }
        }

        return mapHourAndColumn;
    }

    private String getWorkName(String code, String name) {
        return StringUtils.isNotEmpty(name) ? name : StringUtils.isNotEmpty(code) ? code + getText("KSU003_189") : Strings.EMPTY;
    }

    private boolean isNextPage(int rowCount, int pageIndex) {
        return (rowCount - (MAX_ROW_IN_PAGE * pageIndex)) - MAX_ROW_HEADER_IN_PAGE > 60;
    }

    private boolean isEndOfPage(int rowCount, int pageIndex) {
        return (rowCount - (MAX_ROW_IN_PAGE * pageIndex)) - MAX_ROW_HEADER_IN_PAGE > MAX_ROW_IN_PAGE - 2;
    }

    /**
     * Check number is even or odd
     *
     * @param number number
     * @return true/false
     */
    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    private String minuteToTime(Integer totalMinute) {
        if (totalMinute == null) return "0:00";
        int hour = totalMinute / MINUTES_IN_AN_HOUR;
        int minute = totalMinute % MINUTES_IN_AN_HOUR;

        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }

    private int calcWidthCells(int column, int pixelOfColumn) {
        return column * pixelOfColumn;
    }

    private float calcRatioCell(int pixelOfColumn) {
        return (float) pixelOfColumn / 30;
    }

    private int roundUp(int num, int multipleOf) {
        int temp = num % multipleOf;
        if (temp < 0)
            temp = multipleOf + temp;
        if (temp == 0)
            return num;
        return num + multipleOf - temp;
    }

    private int roundDown(int num, int multipleOf) {
        double result = num / multipleOf;
        result = Math.floor(result);
        result *= multipleOf;
        return (int) result;
    }

    private String getText(String resourceId) {
        return TextResource.localize(resourceId);
    }

    private void drawRectangle(ShapeCollection shapes, int row, int column, int width, int left, Color color, boolean displayActual, Integer zOrderIndex) throws Exception {
        Shape shape = shapes.addShape(MsoDrawingType.RECTANGLE, row, displayActual ? 12 : 4, column, left, displayActual ? 8 : 25, width);
        shape.setPrintable(true);
        shape.getFill().setFillType(FillType.SOLID);
        shape.getFill().getSolidFill().setColor(color);
        shape.getLine().setWeight(0);
        shape.getLine().setFillType(FillType.SOLID);
        shape.getLine().getSolidFill().setColor(Color.getBlack());
        if (zOrderIndex != null) {
            shape.setZOrderPosition(zOrderIndex);
        }
    }

    private void drawSample(ShapeCollection shapes, int row, int column, int width, Color color, Integer zOrderIndex) throws Exception {
        Shape shape = shapes.addShape(MsoDrawingType.RECTANGLE, row, 7, column, 0, 25, width);
        shape.setPrintable(true);
        shape.getFill().setFillType(FillType.SOLID);
        shape.getFill().getSolidFill().setColor(color);
        shape.getLine().setWeight(0);
        shape.getLine().setFillType(FillType.SOLID);
        shape.getLine().getSolidFill().setColor(Color.getBlack());
        if (zOrderIndex != null) {
            shape.setZOrderPosition(zOrderIndex);
        }
    }

    private Color getBarColor(BarType barType) {
        switch (barType) {
            case FIXED_WORKING_HOURS:    // C3_2_1
                return Color.fromArgb(204, 204, 255);
            case BREAK_TIME:             // C3_2_2
                return Color.fromArgb(255, 153, 153);
            case OVERTIME_HOURS:          // C3_2_3
                return Color.fromArgb(255, 255, 0);
            case FLOWING_WORKING_HOURS:   // C3_2_4
                return Color.fromArgb(255, 192, 0);
            case FLEX_WORKING_HOURS:      // C3_2_5
                return Color.fromArgb(204, 204, 255);
            case CORE_TIME:               // C3_2_6
                return Color.fromArgb(0, 255, 204);
            case TIME_VACATION:           // C3_2_7
                return Color.fromArgb(196, 189, 151);
            case CHILDCARE_SHORT_TIME:    // C3_2_8
                return Color.fromArgb(111, 165, 39);
            case WORKING_HOURS_ACTUAL:    // C3_2_14
                return Color.fromArgb(24, 23, 23);
            case BREAK_TIME_ACTUAL:       // C3_2_15
                return Color.fromArgb(255, 153, 255);
            case OVERTIME_HOURS_ACTUAL:   // C3_2_16
                return Color.fromArgb(0, 102, 255);
            default:
                return Color.getEmpty();
        }
    }

    @SuppressWarnings("Duplicates")
    private String getDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "月";
            case TUESDAY:
                return "火";
            case WEDNESDAY:
                return "水";
            case THURSDAY:
                return "木";
            case FRIDAY:
                return "金";
            case SATURDAY:
                return "土";
            case SUNDAY:
                return "日";
            default:
                return "";
        }
    }

    private boolean isInRange(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }

    private boolean isOverlap(int startTime, int endTime, int minValue, int maxValue) {
//        Range<Integer> range = Range.between(minValue, maxValue);
//        return range.contains(startTime) || range.contains(endTime);
//        return (IntStream.rangeClosed(minValue, maxValue).anyMatch(n -> n == startTime)) || (IntStream.rangeClosed(minValue, maxValue).anyMatch(n -> n == endTime));

        return startTime <= maxValue && minValue <= endTime;
    }

    private String charBreak(String source, int charNumBreak) {
        StringBuilder destination = new StringBuilder();
        for (int i = 1; i <= source.length(); i++) {
            destination.append(source.charAt(i - 1));
            if (i == charNumBreak) destination.append("\n");
        }
        return destination.toString();
    }

    private void printB5(Cells cells, List<SpecificName> specDayCompanies, List<SpecificName> specDayWorkplaces) {
        val mergedSpecDayList = Stream.of(specDayCompanies, specDayWorkplaces).flatMap(Collection::stream).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(mergedSpecDayList)) return;
        int maxSize = mergedSpecDayList.size();

        int rowStart = getRowStartB5(maxSize);
        int row = rowStart;
        int column = 14;
        for (int i = 0; i < 10; i++) {
            if (i > maxSize - 1) break;
            SpecificName specDayName = mergedSpecDayList.get(i);
            // B5_1, B5_2
            if (row > MAX_ROW_B5) {
                row = rowStart;
                column = 21;
            }
            cells.get(row, column).setValue(getText(i <= specDayCompanies.size() - 1 ? "KSU003_186" : "KSU003_187") + specDayName.v());
            setBottomBorder(cells, row, column, column == 14 ? 19 : 26);
            row += 1;
        }
    }

    private int getRowStartB5(int maxSize) {
        switch (maxSize) {
            case 1:
            case 2:
                return 4;
            case 3:
            case 4:
                return 3;
            case 5:
            case 6:
                return 2;
            case 7:
            case 8:
                return 1;
            case 9:
            case 10:
                return 0;
            default:
                return 0;
        }
    }

    private void setStyleEmpWorkInfo(Cells cells, int row, boolean isDoubleWorkDisplay) {
        for (int column = 0; column < 7; column++) {
            if (isDoubleWorkDisplay && (column == 3 || column == 4)) continue;
            cells.merge(row, column, 2, 1, true, true);
            setStyleEmployeeWorkInfo(cells.get(row, column), isDoubleWorkDisplay);
        }
    }

    private void setStyleEmployeeWorkInfo(Cell cell, boolean isDoubleWorkDisplay) {
        Style style = cell.getStyle();
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        if (!isDoubleWorkDisplay) {
            style.setTextWrapped(true);
        }
        style.getFont().setSize(9);
        cell.setStyle(style);
    }

    private void setBottomBorder(Cells cells, int row, int columnStart, int columnEnd) {
        for (int col = columnStart; col <= columnEnd; col++) {
            Cell cell = cells.get(row, col);
            Style style = cell.getStyle();
            style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
            cell.setStyle(style);
        }
    }

    private void removeTopBorder(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.NONE, Color.getEmpty());
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.NONE);
        cell.setStyle(style);
    }
}

class Ksu003Utils {
    public static <K, V> Map.Entry<K, V> findFirstEntry(Map<K, V> map) {
        return map.entrySet().iterator().next();
    }

    public static <K, V> Map.Entry<K, V> findLastEntry(Map<K, V> linkedMap) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(linkedMap.entrySet());
        return entryList.get(entryList.size() - 1);
    }

    public static Integer findLastEntryKey(Map<Integer, Integer> linkedMap) {
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(linkedMap.entrySet());
        return entryList.get(entryList.size() - 1).getKey();
    }

    public static Integer findLastEntryValue(Map<Integer, Integer> linkedMap) {
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(linkedMap.entrySet());
        return entryList.get(entryList.size() - 1).getValue();
    }
}
