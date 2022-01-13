package nts.uk.file.at.infra.schedule.personalschedulebyindividual;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellArea;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.HorizontalPageBreakCollection;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.DayOfWeek;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.ctx.at.shared.dom.common.MonthlyEstimateTime;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.statutory.worktime.LegalWorkTimeOfEmployee;
import nts.uk.ctx.at.shared.dom.workrule.weekmanage.WeekRuleManagementRepo;
import nts.uk.ctx.sys.portal.dom.enums.MenuAtr;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.PersonalScheduleByIndividualExportGenerator;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.PersonalScheduleByIndividualQuery;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.PersonalScheduleIndividualDataSource;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.WeeklyAgreegateResult;
import nts.uk.file.at.app.export.schedule.personalscheduleindividual.dto.WorkScheduleWorkInforDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
public class AsposePersonalScheduleByIndividualExportGenerator extends AsposeCellsReportGenerator implements PersonalScheduleByIndividualExportGenerator {
    private final String FONT_NAME = "ＭＳ ゴシック";
    private final String EXCEL_EXT = ".xlsx";
    private static final String TEMPLATE_FILE = "report/KSU002B.xlsx";
    private static final int NUMBER_ROW_OF_PAGE = 37;
    private final String SPACE = "　";

    private final int BG_COLOR_SPECIFIC_DAY = Integer.parseInt("ffc0cb", 16);
    private final int TEXT_COLOR_SUNDAY = Integer.parseInt("ff0000", 16);
    private static final String PRINT_AREA = "A1:AN";
    private static final int MAX_ROW_IN_PAGE = 30;

    @Inject
    private StandardMenuRepository standardMenuRepo;

    @Inject
    private WeekRuleManagementRepo weekRuleManagementRepo;

    @Override
    public void generate(FileGeneratorContext context, PersonalScheduleIndividualDataSource dataSource, PersonalScheduleByIndividualQuery query) {

        try {
            long startTime = System.nanoTime();
            List<StandardMenu> menus = standardMenuRepo.findAll(AppContexts.user().companyId());
            String menuName = menus.stream().filter(i -> i.getSystem().value == 1 && i.getMenuAtr() == MenuAtr.Menu && i.getProgramId().equals("KSU002"))
                    .findFirst().map(i -> i.getDisplayName().v()).orElse(TextResource.localize("KSU002_56"));

            AsposeCellsReportContext reportContext = createContext(TEMPLATE_FILE);
            Workbook workbook = reportContext.getWorkbook();
            WorksheetCollection worksheets = workbook.getWorksheets();
            Worksheet wsSource = worksheets.get(0);
            if (query.isTotalDisplay()) {
                wsSource = worksheets.get(1);
                worksheets.removeAt(0);
            } else {
                worksheets.removeAt(1);
            }
            wsSource.setName(menuName);

            //  Worksheet wsDestination = worksheets.get(1);
            pageSetting(wsSource, dataSource);
            printHeader(wsSource, dataSource, query);
            printContent(wsSource, dataSource, query);

            reportContext.processDesigner();


            // Save as excel file
            reportContext.saveAsExcel(createNewFile(context, getReportName(menuName + EXCEL_EXT)));
            long estimatedTime = (System.nanoTime() - startTime) / 1000000000;
            System.out.println("Thoi gian export excel la: " + estimatedTime + " seconds");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * ページヘッダ① : Area A
     */
    private void pageSetting(Worksheet worksheet, PersonalScheduleIndividualDataSource dataSource) {
        PageSetup pageSetup = worksheet.getPageSetup();
        // A1_1
        pageSetup.setHeader(0, "&9&\"ＭＳ ゴシック\"" + dataSource.getCompanyName());
        // A1_2
        pageSetup.setHeader(1, "&16&\"ＭＳ ゴシック,Bold\"" + getText("KSU002_56"));
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN);
        // A1_3, A1_4
        pageSetup.setHeader(2, "&9&\"ＭＳ ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage &P");
        pageSetup.setPrintTitleRows("$1:$3");
    }

    private void printHeader(Worksheet worksheet, PersonalScheduleIndividualDataSource dataSource, PersonalScheduleByIndividualQuery query) {
        Cells cells = worksheet.getCells();
        int firstRow = 0;
        int secondRow = 1;
        int tableHeaderRow = 2;
        // B1_1
        cells.get(firstRow, 0).setValue(getText("KSU002_57"));
        // B1_2
        cells.get(firstRow, 3).setValue(dataSource.getWorkplaceInfo().getWorkplaceCode());
        // B1_3
        cells.get(firstRow, 7).setValue(dataSource.getWorkplaceInfo().getWorkplaceName());
        // B2_1
        cells.get(secondRow, 0).setValue(getText("KSU002_59"));
        // B2_2
        cells.get(secondRow, 3).setValue(query.getEmployeeCode());
        // B2_3
        cells.get(secondRow, 7).setValue(query.getEmployeeName());
        GeneralDate date = GeneralDate.fromString(query.getDate(), "yyyy/MM/dd");
        // B3_1
        cells.get(firstRow, 18).setValue(date.year() + "年 " + date.month() + "月");
        if (!query.isTotalDisplay()) {
            cells.get(tableHeaderRow, 35).setValue(getText("KSU002_68"));
        } else {
            cells.get(tableHeaderRow, 35).setValue(getText("KSU002_69"));
        }
        val headerList = generateTableHeader(query.getStartDate());
        Color colorSaturday = Color.fromArgb(0, 0, 255);
        Color colorSunday = Color.fromArgb(255, 0, 0);
        Map<Integer, String> dayOfWeekMap = new HashMap<Integer, String>() {{
            put(7, getText("KSU002_60"));
            put(1, getText("KSU002_61"));
            put(2, getText("KSU002_62"));
            put(3, getText("KSU002_63"));
            put(4, getText("KSU002_64"));
            put(5, getText("KSU002_65"));
            put(6, getText("KSU002_66"));
        }};
        int index = 0;
        for (Integer dayOfWeek : headerList) {
            DayOfWeek dateInfo = DayOfWeek.valueOf(dayOfWeek);
            Cell cell = cells.get(tableHeaderRow, index);
            cell.setValue(dayOfWeekMap.get(dayOfWeek));
            Style style = cell.getStyle();
            if (dateInfo == DayOfWeek.SUNDAY) {
                style.getFont().setColor(colorSunday);
            }
            if (dateInfo == DayOfWeek.SATURDAY) {
                style.getFont().setColor(colorSaturday);
            }
            cell.setStyle(style);
            index += 5;
        }
    }

    private List<Integer> generateTableHeader(int startDate) {
        DayOfWeek dateInfo = DayOfWeek.valueOf(startDate);
        List<Integer> list1 = Arrays.asList(7, 1, 2, 3, 4, 5, 6);
        List<Integer> list2 = Arrays.asList(6, 7, 1, 2, 3, 4, 5);
        List<Integer> list3 = Arrays.asList(5, 6, 7, 1, 2, 3, 4);
        List<Integer> list4 = Arrays.asList(4, 5, 6, 7, 1, 2, 3);
        List<Integer> list5 = Arrays.asList(3, 4, 5, 6, 7, 1, 2);
        List<Integer> list6 = Arrays.asList(2, 3, 4, 5, 6, 7, 1);
        List<Integer> list7 = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<Integer> list = new ArrayList<>();
        switch (dateInfo) {
            case MONDAY:
                list = list7;
                break;
            case TUESDAY:
                list = list6;
                break;
            case WEDNESDAY:
                list = list5;
                break;
            case THURSDAY:
                list = list4;
                break;
            case FRIDAY:
                list = list3;
                break;

            case SATURDAY:
                list = list2;
                break;
            case SUNDAY:
                list = list1;
                break;
        }
        return list;
    }

    private void printCalender(Cells cells, int rowCount, int col,
                               String l1P1, String l1P2, String l2P1, String l2P2,
                               String l3P1, String l3P2, Map<Integer, String> holidayMap,
                               int colNO, Integer holidayClass, DateInformation dateInformation, DatePeriod datePeriod) {
        int secondLieOfCalender = rowCount + 1;
        int thirdLieOfCalender = rowCount + 2;
        String divider = getText("KSU002_67");
        if (l1P1 == null) l1P1 = "";
        if (l1P2 == null) l1P2 = "";
        if (l2P1 == null) l2P1 = "";
        if (l2P2 == null) l2P2 = "";
        if (l3P1 == null) l3P1 = "";
        List<Cell> cellsClass = new ArrayList<>();
        List<Cell> cellsWhile = new ArrayList<>();
        if (holidayClass == null) {
            holidayClass = 3;
        }

            if (holidayMap.containsKey(colNO)) {
                val holidayName = holidayMap.getOrDefault(colNO, "");
                cells.get(rowCount, col).setValue(l1P1 + "   " + holidayName);
            } else {
                cells.get(rowCount, col).setValue(l1P1 + "   " + l1P2);
            }
            cells.get(secondLieOfCalender, col).setValue(l2P1);
            cells.get(secondLieOfCalender, col + 3).setValue(l2P2);
            if (l3P1 != null && l3P2 != null) {
                cells.get(thirdLieOfCalender, col).setValue(l3P1 + " " + divider + " " + l3P2);
            }
            if (holidayClass != null) {
                cellsClass.addAll(
                        Arrays.asList(cells.get(secondLieOfCalender, col), cells.get(secondLieOfCalender, col + 3), cells.get(thirdLieOfCalender, col))
                );
                setHolidayClassColor(cellsClass, holidayClass);
            }

        if (Objects.nonNull(dateInformation) && datePeriod.contains(dateInformation.getYmd())) {
            cellsWhile.addAll(
                    Arrays.asList(cells.get(secondLieOfCalender, col),
                            cells.get(secondLieOfCalender, col + 2),
                            cells.get(secondLieOfCalender, col + 3),
                            cells.get(thirdLieOfCalender, col),
                            cells.get(thirdLieOfCalender + 1, col))
            );
        }
        Cell cell = cells.get(rowCount, col);
        if (dateInformation != null) {
            if (dateInformation.isSpecificDay()) {
                setBgColor(Color.fromArgb(250, 230, 180), cell);
            } else if (dateInformation.isHoliday() || dateInformation.getDayOfWeek().value == DayOfWeek.SUNDAY.value) {
                setBgColor(Color.fromArgb(250, 200, 250), cell);
            } else if (dateInformation.getDayOfWeek().value == DayOfWeek.SATURDAY.value) {
                setBgColor(Color.fromArgb(204, 236, 255), cell);
            } else {
                setBgColor(Color.fromArgb(242, 242, 242), cell);
            }
        }
        for (Cell cell1 : cellsWhile) {
            setBgColor(Color.getWhite(), cell1);
        }


    }

    void setBgColor(Color color, Cell cell) {
        Style style = cell.getStyle();
        style.setPattern(BackgroundType.SOLID);
        style.setForegroundColor(color);
        cell.setStyle(style);
    }

    private void setHolidayClassColor(List<Cell> cellsClass, int HolidayClass) {
        WorkStyle workStyle = EnumAdaptor.valueOf(HolidayClass, WorkStyle.class);
        Color color = new Color();
        switch (workStyle) {
            case MORNING_WORK:
                color = Color.fromArgb(255, 127, 39);
                break;
            case ONE_DAY_REST:
                color = Color.fromArgb(255, 0, 0);
                break;
            case ONE_DAY_WORK:
                color = Color.fromArgb(0, 0, 255);
                break;
            case AFTERNOON_WORK:
                color = Color.fromArgb(255, 127, 39);
                break;
        }
        for (Cell cell : cellsClass) {
            Style style = cell.getStyle();
            style.getFont().setColor(color);
            cell.setStyle(style);
        }
    }

    private void setTextColorRed(Cell cell) {
        Style style = cell.getStyle();
        style.getFont().setColor(Color.getRed());
        cell.setStyle(style);
    }

    private void printContent(Worksheet wsSource, PersonalScheduleIndividualDataSource dataSource, PersonalScheduleByIndividualQuery query) throws Exception {
        Cells cells = wsSource.getCells();
        HorizontalPageBreakCollection hPageBreaks = wsSource.getHorizontalPageBreaks();
        List<PersonalScheduleByIndividualFormat> dataBuildList = this.buildData(dataSource, query.getStartDate());
        DatePeriod period = new DatePeriod(GeneralDate.fromString(query.getPeriod().getStartDate(), "yyyy/MM/dd"),
                GeneralDate.fromString(query.getPeriod().getEndDate(), "yyyy/MM/dd"));
        int rowCount = 3;
        int pageIndex = 0;
        for (PersonalScheduleByIndividualFormat item : dataBuildList) {
            int weekNO = item.getWeekNo();
            Map<Integer, String> holiday = item.getHoliday().get(weekNO);
            printCalender(cells,
                    rowCount,
                    0,
                    item.getColn1C21(),
                    item.getColn1C22(),
                    item.getColn1C231(),
                    item.getColn1C232(),
                    item.getColn1C233(),
                    item.getColn1C234(),
                    holiday,
                    1,
                    item.getColn1HoliayClass(),
                    item.getColn1Info(),
                    period
            );
            printCalender(cells,
                    rowCount,
                    5,
                    item.getColn2C21(),
                    item.getColn2C22(),
                    item.getColn2C231(),
                    item.getColn2C232(),
                    item.getColn2C233(),
                    item.getColn2C234(),
                    holiday,
                    2,
                    item.getColn2HoliayClass(),
                    item.getColn2Info(),
                    period
            );

            printCalender(cells,
                    rowCount,
                    10,
                    item.getColn3C21(),
                    item.getColn3C22(),
                    item.getColn3C231(),
                    item.getColn3C232(),
                    item.getColn3C233(),
                    item.getColn3C234(),
                    holiday,
                    3,
                    item.getColn3HoliayClass(),
                    item.getColn3Info(),
                    period
            );
            printCalender(cells,
                    rowCount,
                    15,
                    item.getColn4C21(),
                    item.getColn4C22(),
                    item.getColn4C231(),
                    item.getColn4C232(),
                    item.getColn4C233(),
                    item.getColn4C234(),
                    holiday,
                    4,
                    item.getColn4HoliayClass(),
                    item.getColn4Info(),
                    period
            );

            printCalender(cells,
                    rowCount,
                    20,
                    item.getColn5C21(),
                    item.getColn5C22(),
                    item.getColn5C231(),
                    item.getColn5C232(),
                    item.getColn5C233(),
                    item.getColn5C234(),
                    holiday,
                    5,
                    item.getColn5HoliayClass(),
                    item.getColn5Info(),
                    period
            );
            printCalender(cells,
                    rowCount,
                    25,
                    item.getColn6C21(),
                    item.getColn6C22(),
                    item.getColn6C231(),
                    item.getColn6C232(),
                    item.getColn6C233(),
                    item.getColn6C234(),
                    holiday,
                    6,
                    item.getColn6HoliayClass(),
                    item.getColn6Info(),
                    period
            );

            //calender item seven for each row
            printCalender(cells,
                    rowCount,
                    30,
                    item.getColn7C21(),
                    item.getColn7C22(),
                    item.getColn7C231(),
                    item.getColn7C232(),
                    item.getColn7C233(),
                    item.getColn7C234(),
                    holiday,
                    7,
                    item.getColn7HoliayClass(),
                    item.getColn7Info(),
                    period
            );
            if (query.isTotalDisplay()) {
                //calender item seven for each row
                int firstCol = 35;
                int secondCol = 38;
                cells.get(rowCount, firstCol).setValue(item.getD21());
                Cell cellD22 = cells.get(rowCount + 1, firstCol);
                Cell cellD23 = cells.get(rowCount + 1, secondCol);
                Cell cellD24 = cells.get(rowCount + 2, firstCol);
                Cell cellD25 = cells.get(rowCount + 2, secondCol);
                Cell cellD26 = cells.get(rowCount + 3, firstCol);
                Cell cellD27 = cells.get(rowCount + 3, secondCol);
                cellD22.setValue(item.getD22());
                cellD23.setValue(item.getD23());
                cellD24.setValue(item.getD24());
                cellD25.setValue(item.getD25());
                cellD26.setValue(item.getD26());
                cellD27.setValue(item.getD27());
                setBgWhile(cellD22);
                setBgWhile(cellD23);
                setBgWhile(cellD24);
                setBgWhile(cellD25);
                setBgWhile(cellD26);
                setBgWhile(cellD27);
                setBgWhile(cells.get(rowCount + 4, firstCol));

            }
            rowCount += 5;

            if (isNextPage(rowCount, pageIndex) && dataBuildList.size() > 6) {
                hPageBreaks.add(rowCount);
                pageIndex += 1;
                cells.copyRows(cells, 3, rowCount, 30);
                cells.clearContents(CellArea.createCellArea(rowCount, 0, cells.getMaxRow(), cells.getMaxColumn()));
            }
        }
        int dataRemaining = Math.abs(dataBuildList.size() - (pageIndex <= 0 ? 6 : pageIndex * 6));
        if (dataRemaining < 6) {
            int col = 0;
            while (dataRemaining > 0) {
                for (Integer header : generateTableHeader(query.getStartDate())) {
                    Cell cell = cells.get(rowCount, col);
                    if (header == DayOfWeek.SUNDAY.value) {
                        setBgColor(Color.fromArgb(250, 200, 250), cell);
                    } else if (header == DayOfWeek.SATURDAY.value) {
                        setBgColor(Color.fromArgb(204, 236, 255), cell);
                    } else {
                        setBgColor(Color.fromArgb(242, 242, 242), cell);
                    }
                    col += 5;
                }
                rowCount += 5;
                col = 0;
                dataRemaining--;
            }
        }
        PageSetup pageSetup = wsSource.getPageSetup();
        val totalRow = ((pageIndex + 1) * 30) + 3;
        pageSetup.setPrintArea(PRINT_AREA + totalRow);
    }

    public static boolean isInteger(double number) {
        return Math.ceil(number) == Math.floor(number);
    }

    void setHeader(DateInformation dateInformation, Cell cell) {
        if (dateInformation.isSpecificDay()) {
            setBgColor(Color.fromArgb(250, 230, 180), cell);
        } else if (dateInformation.isHoliday() || dateInformation.getDayOfWeek().value == DayOfWeek.SUNDAY.value) {
            setBgColor(Color.fromArgb(250, 200, 250), cell);
        } else if (dateInformation.getDayOfWeek().value == DayOfWeek.SATURDAY.value) {
            setBgColor(Color.fromArgb(204, 236, 255), cell);
        } else {
            setBgColor(Color.fromArgb(242, 242, 242), cell);
        }
    }

    /*
     * #ddddd2
     * */
    private void setBgWhile(Cell cell) {
        Style style = cell.getStyle();
        style.setForegroundColor(Color.getWhite());
        cell.setStyle(style);
    }

    private void removeTopBorder(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.NONE, Color.getEmpty());
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.NONE);
        cell.setStyle(style);
    }

    private boolean isNextPage(int rowCount, int pageIndex) {
        return (rowCount - (MAX_ROW_IN_PAGE * pageIndex)) > MAX_ROW_IN_PAGE;
    }

    int getInit(List<DateInformation> dateInfolist) {
        int count = 0;
        if (dateInfolist.size() > 0) {
            DayOfWeek dateInfo = dateInfolist.get(0).getDayOfWeek();
            switch (dateInfo) {
                case SUNDAY:
                    count = 0;
                    break;
                case MONDAY:
                    count = 1;
                    break;
                case TUESDAY:
                    count = 2;
                    break;
                case WEDNESDAY:
                    count = 3;
                    break;
                case THURSDAY:
                    count = 4;
                    break;
                case FRIDAY:
                    count = 5;
                    break;

                case SATURDAY:
                    count = 6;
                    break;
            }
        }
        return count;
    }

    private List<PersonalScheduleByIndividualFormat> buildData(PersonalScheduleIndividualDataSource dataSource, int startDayOfWeek) {
        List<DateInformation> dateInfolist = dataSource.getDateInformationList();
        List<WorkScheduleWorkInforDto> workInforDtoList = dataSource.getWorkInforDtoList();
        List<WeeklyAgreegateResult> weeklyAgreegateResults = dataSource.getAgreegateResults();
        Optional<LegalWorkTimeOfEmployee> legalWorktime = dataSource.getLegalWorkTimeOfEmployee();

        List<Integer> headerList = generateTableHeader(startDayOfWeek);
        val startDateValue = dataSource.getDateInformationList().get(0).getDayOfWeek().value;
        int count = ArrayUtils.indexOf(headerList.toArray(), startDateValue);

        val weekRuleManagement = weekRuleManagementRepo.find(AppContexts.user().companyId());
        boolean isFirst = weekRuleManagement.isPresent() && weekRuleManagement.get().getDayOfWeek().value == startDayOfWeek;
        List<PersonalScheduleByIndividualFormat> dataList = new ArrayList<>();
        PersonalScheduleByIndividualFormat format = new PersonalScheduleByIndividualFormat();
        Map<Integer, String> holiday = new HashMap<>();
        String divider = getText("KSU002_67");
        String d11 = getText("KSU002_68");
        String d12 = getText("KSU002_69");

        String d22 = getText("KSU002_76");
        String d26 = getText("KSU002_78");
        String d24 = getText("KSU002_77");
        int size = dateInfolist.size();
        int weekCount = 0;
        int iteration = 0;
        int d21ResourceStart = 70;
        int d21Count = 0;
        for (DateInformation dateInfo : dateInfolist) {
            String d21 = getText("KSU002_" + d21ResourceStart);
            if (count == 0) {
                format.setColn1C21(getDate(dateInfo.getYmd(), isFirst));
                format.setColn1C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn1C231(workTypeCodeAndName(workDetail));
                format.setColn1C232(workHourCode(workDetail));
                format.setColn1Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn1C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : null);
                    format.setColn1C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : null);
                    format.setColn1HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                isFirst = false;
                if (dateInfo.isHoliday()) {
                    holiday.put(1, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            if (count == 1) {
                format.setColn2C21(getDate(dateInfo.getYmd(), false));
                format.setColn2C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn2C231(workTypeCodeAndName(workDetail));
                format.setColn2C232(workHourCode(workDetail));
                format.setColn2Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn2C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : "");
                    format.setColn2C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : "");
                    format.setColn2HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                if (dateInfo.isHoliday()) {
                    holiday.put(2, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            if (count == 2) {
                format.setColn3C21(getDate(dateInfo.getYmd(), false));
                format.setColn3C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn3C231(workTypeCodeAndName(workDetail));
                format.setColn3C232(workHourCode(workDetail));
                format.setColn3Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn3C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : null);
                    format.setColn3C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : null);
                    format.setColn3HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                if (dateInfo.isHoliday()) {
                    holiday.put(3, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            if (count == 3) {
                format.setColn4C21(getDate(dateInfo.getYmd(), false));
                format.setColn4C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn4C231(workTypeCodeAndName(workDetail));
                format.setColn4C232(workHourCode(workDetail));
                format.setColn4Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn4C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : null);
                    format.setColn4C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : null);
                    format.setColn4HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                if (dateInfo.isHoliday()) {
                    holiday.put(4, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            if (count == 4) {
                format.setColn5C21(getDate(dateInfo.getYmd(), false));
                format.setColn5C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn5C231(workTypeCodeAndName(workDetail));
                format.setColn5C232(workHourCode(workDetail));
                format.setColn5Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn5C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : null);
                    format.setColn5C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : null);
                    format.setColn5HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                if (dateInfo.isHoliday()) {
                    holiday.put(5, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            if (count == 5) {
                format.setColn6C21(getDate(dateInfo.getYmd(), false));
                format.setColn6C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn6C231(workTypeCodeAndName(workDetail));
                format.setColn6C232(workHourCode(workDetail));
                format.setColn6Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn6C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : null);
                    format.setColn6C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : null);
                    format.setColn6HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                if (dateInfo.isHoliday()) {
                    holiday.put(6, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            if (count == 6) {
                format.setColn7C21(getDate(dateInfo.getYmd(), false));
                format.setColn7C22(getEventName(dateInfo));
                val workDetail = getWorkDetails(dateInfo, workInforDtoList);
                format.setColn7C231(workTypeCodeAndName(workDetail));
                format.setColn7C232(workHourCode(workDetail));
                format.setColn7Info(dateInfo);
                if (workDetail.isPresent()) {
                    format.setColn7C233(workDetail.get().getStartTime().isPresent() ? minuteToTime(workDetail.get().getStartTime().get()) : null);
                    format.setColn7C234(workDetail.get().getEndTime().isPresent() ? minuteToTime(workDetail.get().getEndTime().get()) : null);
                    format.setColn7HoliayClass(workDetail.get().getWorkHolidayCls().isPresent() ? workDetail.get().getWorkHolidayCls().get().value : null);
                }
                if (dateInfo.isHoliday()) {
                    holiday.put(7, dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "");
                }
            }
            count++;
            iteration++;

            if (count > 6) {
                count = 0;
                weekCount++;
                Optional<WeeklyAgreegateResult> weekTotal = weekTotal(weeklyAgreegateResults, weekCount);
                if (weekTotal.isPresent()) {
                    format.setD27(String.valueOf(weekTotal.get().getHolidays().doubleValue()));
                    format.setD23(minuteToTime(weekTotal.get().getWorkingHours().intValue()));
                } else {
                    format.setD27("0");
                    format.setD23("0:00");
                }
                if (!legalWorktime.isPresent()) {
                    format.setD25("0:00");
                } else {
                    if (legalWorktime.get().getWeeklyEstimateTime().isPresent()) {
                        val hourDifference = calculateHoursDifference(weekCount, weeklyAgreegateResults, legalWorktime.get().getWeeklyEstimateTime());
                        format.setD25(convertNumberToTime(hourDifference));
                    }
                }
                format.setD11(d11);
                format.setD12(d12);
                format.setD21(d21);
                format.setD22(d22);
                format.setD26(d26);
                format.setD24(d24);
                format.setFromTo(divider);
                Map<Integer, Map<Integer, String>> holidayd = new HashMap<>();
                holidayd.put(Integer.valueOf(weekCount), holiday);
                format.setHoliday(holidayd);
                format.setWeekNo(weekCount);
                dataList.add(format);
                format = new PersonalScheduleByIndividualFormat();
                holiday = new HashMap<>();
                d21ResourceStart++;
                d21Count++;
                if (d21Count > 6) {
                    d21ResourceStart = 70;
                }
            } else {
                if (iteration == size) {
                    Optional<WeeklyAgreegateResult> weekTotal = weekTotal(weeklyAgreegateResults, weekCount);
                    if (weekTotal.isPresent()) {
                        format.setD27(String.valueOf(weekTotal.get().getHolidays().doubleValue()));
                        format.setD23(minuteToTime(weekTotal.get().getWorkingHours().intValue()));
                    } else {
                        format.setD27("0");
                        format.setD23("0:00");
                    }
                    if (!legalWorktime.isPresent()) {
                        format.setD25("0:00");
                    } else {
                        if (legalWorktime.get().getWeeklyEstimateTime().isPresent()) {
                            val hourDifference = calculateHoursDifference(weekCount, weeklyAgreegateResults, legalWorktime.get().getWeeklyEstimateTime());
                            format.setD25(convertNumberToTime(hourDifference));
                        }
                    }
                    format.setD11(d11);
                    format.setD12(d12);
                    format.setD21(d21);
                    format.setD22(d22);
                    format.setD26(d26);
                    format.setD24(d24);
                    format.setFromTo(divider);
                    Map<Integer, Map<Integer, String>> holidayd = new HashMap<>();
                    holidayd.put(Integer.valueOf(weekCount), holiday);
                    format.setHoliday(holidayd);
                    format.setWeekNo(weekCount);
                    dataList.add(format);
                    d21ResourceStart++;
                    d21Count++;
                    if (d21Count > 6) {
                        d21ResourceStart = 70;
                    }
                    break;
                }
            }
        }
        return dataList;
    }

    private Optional<WeeklyAgreegateResult> weekTotal(List<WeeklyAgreegateResult> weeklyAgreegateResults, int weekCount) {
        return weeklyAgreegateResults.stream().filter(x -> x.getWeek() == weekCount).findFirst();
    }


    private String getDate(GeneralDate date, boolean isMdFormat) {
        if (date.day() == 1 || isMdFormat) {
            return (date.month()) + "/" + date.day();
        }
        return String.valueOf(date.day());
    }

    private String getEventName(DateInformation dateInfo) {
        return dateInfo.getOptWorkplaceEventName().isPresent() ? dateInfo.getOptWorkplaceEventName().get().v() : dateInfo.getOptCompanyEventName().isPresent()
                ? dateInfo.getOptCompanyEventName().get().v() : dateInfo.getHolidayName().isPresent() ? dateInfo.getHolidayName().get().v() : "";
    }

    private Optional<WorkScheduleWorkInforDto> getWorkDetails(DateInformation dateInformation,
                                                              List<WorkScheduleWorkInforDto> workInforDtos) {
        return workInforDtos.stream().filter(x -> x.getDate().equals(dateInformation.getYmd())).findFirst();
    }

    private String workTypeCodeAndName(Optional<WorkScheduleWorkInforDto> workInforDto) {
        if (!workInforDto.isPresent()) {
            return "";
        }
        val inforDto = workInforDto.get();
        if (inforDto.getWorkTypeCode().isPresent() && StringUtils.isNotEmpty(inforDto.getWorkTypeCode().get())) {
            if (inforDto.getWorkTypeName().isPresent()) {
                if (StringUtils.isNotEmpty(inforDto.getWorkTypeName().get())) {
                    return inforDto.getWorkTypeName().get();
                } else {
                    return inforDto.getWorkTypeCode().get() + getText("KSU002_31");
                }
            }
        }
        return "";
    }

    private String workHourCode(Optional<WorkScheduleWorkInforDto> workInforDto) {
        if (!workInforDto.isPresent()) {
            return "";
        }
        val inforDto = workInforDto.get();
        if (inforDto.getWorkingHoursCode().isPresent() && StringUtils.isNotEmpty(inforDto.getWorkingHoursCode().get())) {
            if (inforDto.getWorkingHoursName().isPresent()) {
                if (StringUtils.isNotEmpty(inforDto.getWorkingHoursName().get())) {
                    return inforDto.getWorkingHoursName().get();
                } else {
                    return inforDto.getWorkingHoursCode().get() + getText("KSU002_31");
                }
            }
        }
        return "";
    }

    private String minuteToTime(Integer totalMinute) {
        if (totalMinute == null) return "0:00";
        int hour = totalMinute / 60;
        int minute = totalMinute % 60;

        return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * Calculate data of D2_5
     *
     * @param weekNumber
     * @param weeklyAgreegateResults
     * @param weeklyEstimateTime
     * @return
     */
    private int calculateHoursDifference(int weekNumber, List<WeeklyAgreegateResult> weeklyAgreegateResults, Optional<MonthlyEstimateTime> weeklyEstimateTime) {
        AtomicInteger hourDifference = new AtomicInteger(0);
        val totalWorkingHourWeek = weeklyAgreegateResults.stream().filter(week -> week.getWeek() == weekNumber).findFirst();
        weeklyEstimateTime.ifPresent(legal -> {
            totalWorkingHourWeek.ifPresent(totalHour -> {
                hourDifference.set(totalHour.getWorkingHours().intValue() - legal.valueAsMinutes());
            });
        });
        return hourDifference.intValue();
    }

    private String convertNumberToTime(Integer number) {
        return (number < 0 ? String.valueOf((int) Math.ceil(number / 60)) : String.valueOf((int) Math.floor(number / 60)))
                + ':' + (Math.abs(number % 60) == 0 ? "00" : (String.valueOf(Math.abs(number % 60))));
    }

    private void setHeaderStyle(Cell cell, DateInformation dateInfo, boolean isDate) {
        Style style = cell.getStyle();
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        if (isDate) {
            style.setTextWrapped(true);
        } else {
            style.setShrinkToFit(true);
        }
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
        style.setPattern(BackgroundType.SOLID);
        if (dateInfo.isSpecificDay()) {
            style.setForegroundColor(Color.fromArgb(BG_COLOR_SPECIFIC_DAY));
            style.getFont().setColor(Color.fromArgb(TEXT_COLOR_SUNDAY));
        }
        style.getFont().setName(FONT_NAME);
        style.getFont().setSize(9);
        cell.setStyle(style);
    }

    private void setPersonalStyle(Cells cells, int row, int column, boolean isFirstRow) {
        Style style = cells.get(row, column).getStyle();
        style.getBorders().getByBorderType(BorderType.TOP_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.BOTTOM_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.LEFT_BORDER).setLineStyle(CellBorderType.THIN);
        style.getBorders().getByBorderType(BorderType.RIGHT_BORDER).setLineStyle(CellBorderType.THIN);
        style.getFont().setName(FONT_NAME);
        style.getFont().setSize(9);
        style.setShrinkToFit(true);
        style.setPattern(BackgroundType.SOLID);
        style.setVerticalAlignment(TextAlignmentType.CENTER);
        if (isFirstRow) style.setForegroundColor(Color.fromArgb(221, 235, 247));
        cells.get(row, column).setStyle(style);
    }

    private String getText(String resourceId) {
        return TextResource.localize(resourceId);
    }

    private void removeTemplate(Worksheet worksheet) {
        removeFirstShapes(worksheet);
        Cells cells = worksheet.getCells();
        cells.deleteRows(0, NUMBER_ROW_OF_PAGE);
    }

    private void removeFirstShapes(Worksheet worksheet) {
        if (worksheet.getShapes().getCount() > 0) {
            worksheet.getShapes().removeAt(0);
        }
    }

    private void setTopBorderStyle(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.MEDIUM, Color.getBlack());
        cell.setStyle(style);
    }

    private void setBottomBorderStyle(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
        cell.setStyle(style);
    }

    private void setBackgroundGray(Cell cell) {
        Style style = cell.getStyle();
        style.setForegroundColor(Color.getGray());
        cell.setStyle(style);
    }

    private void setCurrentMonthBackground(Cell cell) {
        Style style = cell.getStyle();
        style.setForegroundColor(Color.fromArgb(197, 217, 241));
        cell.setStyle(style);
    }

    private void setForegroundRed(Cell cell) {
        Style style = cell.getStyle();
        style.getFont().setColor(Color.getRed());
        cell.setStyle(style);
    }
}
