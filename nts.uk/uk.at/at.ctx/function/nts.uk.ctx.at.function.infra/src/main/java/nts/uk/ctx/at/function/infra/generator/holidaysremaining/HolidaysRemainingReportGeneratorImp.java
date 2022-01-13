package nts.uk.ctx.at.function.infra.generator.holidaysremaining;

import com.aspose.cells.*;
import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.annleauseddays.FindAnnLeaUsedDaysAdapter;
import nts.uk.ctx.at.function.dom.adapter.child.ChildNursingLeaveThisMonthFutureSituation;
import nts.uk.ctx.at.function.dom.adapter.child.NursingCareLeaveThisMonthFutureSituation;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.*;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImportedKdr;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReservedYearHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.RsvLeaUsedCurrentMonImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.StatusHolidayImported;
import nts.uk.ctx.at.function.dom.holidaysremaining.BreakSelection;
import nts.uk.ctx.at.function.dom.holidaysremaining.HolidaysRemainingManagement;
import nts.uk.ctx.at.function.dom.holidaysremaining.VariousVacationControl;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidayRemainingDataSource;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidaysRemainingEmployee;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidaysRemainingReportGenerator;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.SpecialVacationPastSituation;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnLeaMaxDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnLeaRemNumEachMonthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.ChildNursingLeaveStatus;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.childcare.NursingCareLeaveMonthlyRemaining;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmploymentSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHolidayFrameRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class HolidaysRemainingReportGeneratorImp extends AsposeCellsReportGenerator
        implements HolidaysRemainingReportGenerator {
    @Inject
    private SpecialLeaveGrantRepository specialLeaveGrantRepository;

    @Inject
    private AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepository;

    @Inject
    private AnnLeaMaxDataRepository annLeaMaxDataRepository;

    @Inject
    private EmploymentSettingRepository employmentSettingRepository;

    @Inject
    private CompensLeaveEmSetRepository compensLeaveEmSetRepository;
    @Inject
    private CompensLeaveComSetRepository compensLeaveComSetRepository;
    @Inject
    private ComSubstVacationRepository comSubstVacationRepository;
    @Inject
    private EmpSubstVacationRepository substVacationRepository;
    @Inject
    private AnnLeaRemNumEachMonthRepository annLeaRemNumEachMonthRepository;
    @Inject
    private SpecialHolidayFrameRepository specialHolidayFrameRepository;
    @Inject
    private NursingLeaveSettingRepository nursingLeaveSettingRepository;

    @Inject
    private FindAnnLeaUsedDaysAdapter findAnnLeaUsedDaysAdapter;

    private static final String TEMPLATE_FILE = "report/KDR001_v2_template.xlsx";
    private static final String REPORT_FILE_NAME = "休暇残数管理表.xlsx";
    private static final int NUMBER_ROW_OF_HEADER = 5;
    private static final int NUMBER_COLUMN = 23;
    private static final int TOTAL_MONTH_IN_YEAR = 12;
    private static final int MAX_ROW_IN_PAGE = 42;
    private static final int MAX_ROW_IN_PAGE_TEMPLATE = 65;

    private static final String SPACE = "　";

    @Override
    public void generate(FileGeneratorContext generatorContext, HolidayRemainingDataSource dataSource) {
        try {
            val designer = this.createContext(TEMPLATE_FILE);
            Workbook workbook = designer.getWorkbook();

            WorksheetCollection worksheets = workbook.getWorksheets();
            // Get first sheet in template
            Worksheet worksheet = worksheets.get(0);
            printHeader(worksheet, dataSource);
            printTemplate(worksheet, dataSource);
            worksheets.setActiveSheetIndex(0);
            HorizontalPageBreakCollection pageBreaks = worksheet.getHorizontalPageBreaks();
            // pageBreaks.add(53);
            if (dataSource.getPageBreak() == BreakSelection.None.value) {
                printNoneBreakPage(worksheet, dataSource);
            } else if (dataSource.getPageBreak() == BreakSelection.Workplace.value) {
                printWorkplaceBreakPage(worksheet, dataSource, pageBreaks);
            } else if (dataSource.getPageBreak() == BreakSelection.Individual.value) {
                printPersonBreakPage(worksheet, dataSource, pageBreaks);
            }

            removeTemplate(worksheet);
            designer.getDesigner().setWorkbook(workbook);
            designer.processDesigner();
            designer.saveAsExcel(this.createNewFile(generatorContext, this.getReportName(REPORT_FILE_NAME)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printTemplate(Worksheet worksheet, HolidayRemainingDataSource dataSource) {
        Cells cells = worksheet.getCells();

        YearMonth startMonth = dataSource.getStartMonth().yearMonth();
        YearMonth endMonth = dataSource.getEndMonth().yearMonth();

        // B1_1, B1_2 // Update ~ -> KDR001_73
        cells.get(1, 0).setValue(TextResource.localize("KDR001_2") +
                dataSource.getStartMonth().toString("yyyy/MM") + "　"
                + TextResource.localize("KDR001_73") + "　" + dataSource.getEndMonth().toString("yyyy/MM"));
        // B1_3
        //cells.get(2, 0).setValue(TextResource.localize("KDR001_3"));// Ver: 15 : delete
        // C1_1
        cells.get(3, 2).setValue(TextResource.localize("KDR001_4"));
        // C1_2
        cells.get(3, 4).setValue(TextResource.localize("KDR001_5"));
        // C1_3
        cells.get(3, 9).setValue(TextResource.localize("KDR001_6"));
        // C1_4
        cells.get(4, 4).setValue(TextResource.localize("KDR001_7"));
        // C1_5
        cells.get(4, 5).setValue(TextResource.localize("KDR001_8"));
        // C1_6
        cells.get(4, 6).setValue(TextResource.localize("KDR001_9"));
        // C1_7
        cells.get(4, 7).setValue(TextResource.localize("KDR001_10"));
        // C1_8
        cells.get(4, 8).setValue(TextResource.localize("KDR001_11"));

        Optional<HolidaysRemainingEmployee> hdEmpOpt = dataSource.getMapEmployees().values().stream().findFirst();
        Optional<YearMonth> curMonthOpt = hdEmpOpt.isPresent() ? hdEmpOpt.get().getCurrentMonth() : Optional.empty();
        if (!dataSource.isSameCurrentMonth() || !curMonthOpt.isPresent() || curMonthOpt.get().compareTo(endMonth) > 0
                || curMonthOpt.get().compareTo(startMonth) < 0) {
            removeFirstShapes(worksheet);
        } else {
            Shape shape = worksheet.getShapes().get(0);
            shape.setLowerRightColumn(shape.getLowerRightColumn() + totalMonths(startMonth, curMonthOpt.get()));
        }
        // C1_9
        for (int i = 0; i <= totalMonths(startMonth, endMonth); i++) {
            // Update 月 -> TextResource.localize("KDR001_74")
            cells.get(4, 10 + i).setValue(String.valueOf(startMonth.addMonths(i).month()) + TextResource.localize("KDR001_74"));
        }
    }

    private void printNoneBreakPage(Worksheet worksheet, HolidayRemainingDataSource dataSource) throws Exception {
        int firstRow = MAX_ROW_IN_PAGE_TEMPLATE + 40;
        val pageBreaks = worksheet.getHorizontalPageBreaks();
        Cells cells = worksheet.getCells();
        List<String> empIds = dataSource.getEmpIds();
        val dtoCheck = new DtoCheck(firstRow, 0, false, 0);
        cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
        firstRow += NUMBER_ROW_OF_HEADER;
        for (int i = 0; i < empIds.size(); i++) {
            String employeeIds = empIds.get(i);
            HolidaysRemainingEmployee employee = dataSource.getMapEmployees().get(employeeIds);
            Integer counts = dtoCheck.getCount();
            YearMonth currentMonth = employee.getCurrentMonth().get();
            Integer countBfEmp = dtoCheck.getCountEmployeeBefore();
            if (i == 0) {
                counts += 5;
            }
            val countStep = checkStepCount(employee, dataSource);
            int countEmployInfo = 3;
            boolean isDisplayHolidayYear = dataSource.getVariousVacationControl().isAnnualHolidaySetting()
                    && dataSource.getHolidaysRemainingManagement()
                    .getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
            Optional<GeneralDate> grantDate = dataSource.getMapEmployees().get(employee.getEmployeeId())
                    .getHolidayRemainingInfor().getGrantDate();
            if (isDisplayHolidayYear) {
                countEmployInfo += 2;
            }
            if ((MAX_ROW_IN_PAGE - counts) > (countStep > countEmployInfo ? countStep : countEmployInfo)) {
                cells.copyRows(cells, 5, firstRow, 1);
                cells.get(firstRow, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                        + SPACE + employee.getWorkplaceName());
                firstRow += 1;
                counts += 1;
                if (i >= 1) {
                    countBfEmp += 1;
                    dtoCheck.setCountEmployeeBefore(countBfEmp);
                }
            } else {
                dtoCheck.setCountEmployeeBefore(0);
                for (int j = 0; j < NUMBER_COLUMN; j++) {
                    setBottomBorderStyle(cells.get(firstRow - 1, j));
                }
                pageBreaks.add(firstRow);
                cells.copyRows(cells, 0, firstRow, 6);
                cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                        + SPACE + employee.getWorkplaceName());
                for (int index = 0; index < NUMBER_COLUMN; index++) {
                    setBottomBorderStyle(cells.get(firstRow + 5, index));
                }
                firstRow += 6;
                counts = 6;
            }
            dtoCheck.setCount(counts);
            val printHolidayRemainingEachPerson = this.printHolidayRemainingEachPerson(worksheet, firstRow, employee, dataSource, dtoCheck);
            firstRow = printHolidayRemainingEachPerson.getFirstRow();
            val count = printHolidayRemainingEachPerson.getCount();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < count; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(firstRow - (count - 6) + j, 10 + totalMonth));
                }
            }
            printEmployeeInfore(cells, firstRow - (count - 6 - dtoCheck.getCountEmployeeBefore()), dataSource, employee);
            dtoCheck.setCountEmployeeBefore(count - 6);
        }
    }

    private void printWorkplaceBreakPage(Worksheet worksheet, HolidayRemainingDataSource dataSource,
                                         HorizontalPageBreakCollection pageBreaks) throws Exception {
        int firstRow = MAX_ROW_IN_PAGE_TEMPLATE + 40;
        Map<String, List<HolidaysRemainingEmployee>> maps = new HashMap<>();

        for (String employeeIds : dataSource.getEmpIds()) {
            HolidaysRemainingEmployee hRemaiEmployee = dataSource.getMapEmployees().get(employeeIds);
            List<HolidaysRemainingEmployee> list = maps.get(hRemaiEmployee.getWorkplaceId());
            if (list == null) {
                list = new ArrayList<HolidaysRemainingEmployee>();
            }
            list.add(hRemaiEmployee);
            maps.put(hRemaiEmployee.getWorkplaceId(), list);
        }

        for (List<HolidaysRemainingEmployee> listEmployee : maps.values()) {
            val checkDto = new DtoCheck(firstRow, 0, false, 0);
            val printEachWorkplace = printEachWorkplace(worksheet, firstRow, listEmployee, dataSource, pageBreaks, checkDto);
            firstRow = printEachWorkplace.getFirstRow();
        }

    }

    private DtoCheck printEachWorkplace(Worksheet worksheet, int firstRow, List<HolidaysRemainingEmployee> employees,
                                        HolidayRemainingDataSource dataSource, HorizontalPageBreakCollection pageBreaks, DtoCheck checkDto) throws Exception {
        Cells cells = worksheet.getCells();

        cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
        firstRow += NUMBER_ROW_OF_HEADER;
        for (int i = 0; i < employees.size(); i++) {
            String employeeIds = employees.get(i).getEmployeeId();
            HolidaysRemainingEmployee employee = dataSource.getMapEmployees().get(employeeIds);
            Integer counts = checkDto.getCount();
            YearMonth currentMonth = employee.getCurrentMonth().get();
            Integer countBfEmp = checkDto.getCountEmployeeBefore();
            if (i == 0) {
                counts += 5;
            }
            val countStep = checkStepCount(employee, dataSource);
            int countEmployInfo = 3;
            boolean isDisplayHolidayYear = dataSource.getVariousVacationControl().isAnnualHolidaySetting()
                    && dataSource.getHolidaysRemainingManagement()
                    .getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
            Optional<GeneralDate> grantDate = dataSource.getMapEmployees().get(employee.getEmployeeId())
                    .getHolidayRemainingInfor().getGrantDate();
            if (isDisplayHolidayYear) {

                countEmployInfo += 2;
            }
            if ((MAX_ROW_IN_PAGE - counts) > (countStep > countEmployInfo ? countStep : countEmployInfo)) {
                cells.copyRows(cells, 5, firstRow, 1);
                cells.get(firstRow, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                        + SPACE + employee.getWorkplaceName());
                firstRow += 1;
                counts += 1;
                if (i >= 1) {
                    countBfEmp += 1;
                    checkDto.setCountEmployeeBefore(countBfEmp);
                }
            } else {
                checkDto.setCountEmployeeBefore(0);
                for (int j = 0; j < NUMBER_COLUMN; j++) {
                    setBottomBorderStyle(cells.get(firstRow - 1, j));
                }
                pageBreaks.add(firstRow);
                cells.copyRows(cells, 0, firstRow, 6);
                cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                        + SPACE + employee.getWorkplaceName());
                for (int index = 0; index < NUMBER_COLUMN; index++) {
                    setBottomBorderStyle(cells.get(firstRow + 5, index));
                }
                firstRow += 6;
                counts = 6;
            }
            checkDto.setCount(counts);
            val printHolidayRemainingEachPerson = this.printHolidayRemainingEachPerson(worksheet, firstRow, employee, dataSource, checkDto);
            firstRow = printHolidayRemainingEachPerson.getFirstRow();
            Integer count = printHolidayRemainingEachPerson.getCount();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < count; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(firstRow - (count - 6) + j, 10 + totalMonth));
                }
            }
            printEmployeeInfore(cells, firstRow - (count - 6 - checkDto.getCountEmployeeBefore()), dataSource, employee);
            checkDto.setCountEmployeeBefore(count - 6);
        }
        pageBreaks.add(firstRow);
        checkDto.setFirstRow(firstRow);
        return checkDto;
    }

    private void printPersonBreakPage(Worksheet worksheet, HolidayRemainingDataSource dataSource,
                                      HorizontalPageBreakCollection pageBreaks) throws Exception {
        int firstRow = MAX_ROW_IN_PAGE_TEMPLATE + 40;
        Cells cells = worksheet.getCells();
        List<String> empIds = dataSource.getEmpIds();
        val checkDto = new DtoCheck(firstRow, 0, false, 0);
        for (int i = 0; i < empIds.size(); i++) {
            cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
            firstRow += NUMBER_ROW_OF_HEADER;
            String employeeIds = empIds.get(i);
            HolidaysRemainingEmployee employee = dataSource.getMapEmployees().get(employeeIds);
            cells.copyRows(cells, 5, firstRow, 1);
            cells.get(firstRow, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            firstRow += 1;
            checkDto.setCount(6);
            checkDto.setFirstRow(firstRow);
            val printHolidayRemainingEachPerson = this.printHolidayRemainingEachPerson(worksheet, firstRow, employee, dataSource, checkDto);
            firstRow = printHolidayRemainingEachPerson.getFirstRow();
            Integer count = printHolidayRemainingEachPerson.getCount();
            printEmployeeInfore(cells, firstRow - (count - 6), dataSource, employee);
            pageBreaks.add(firstRow);
        }
    }

    private DtoCheck printHolidayRemainingEachPerson(Worksheet worksheet, int firstRow, HolidaysRemainingEmployee employee,
                                                     HolidayRemainingDataSource dataSource, DtoCheck checkDto) throws Exception {
        int first = firstRow;
        Cells cells = worksheet.getCells();
        HorizontalPageBreakCollection pageBreaks = worksheet.getHorizontalPageBreaks();
        // E
        firstRow = printAnnualHoliday(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // F
        firstRow = printLimitForHalfHolidays(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        //G
        firstRow = printLimitUsageHolidays(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // H
        firstRow = printYearlyReserved(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // I
        firstRow = printSubstituteHoliday(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // J
        firstRow = printPauseHoliday(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // K
        firstRow = print60hOversho(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // L
        firstRow = publicHolidays(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // M
        firstRow = printSpecialHoliday(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // N
        firstRow = printChildNursingVacation(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        // O
        firstRow = printNursingCareLeave(cells, firstRow, employee, dataSource, checkDto, pageBreaks).getFirstRow();
        Integer count = checkDto.getCount();
        int totalRowDetails = 0;
        totalRowDetails += countE(dataSource, employee);
        totalRowDetails += countF(dataSource, employee);
        totalRowDetails += countG(dataSource, employee);
        totalRowDetails += countH(dataSource, employee);
        totalRowDetails += countI(dataSource, employee);
        totalRowDetails += countJ(dataSource, employee);
        totalRowDetails += countK(dataSource, employee);
        totalRowDetails += countL(dataSource, employee);
        totalRowDetails += countN(dataSource, employee);
        totalRowDetails += countM(dataSource, employee);
        totalRowDetails += countO(dataSource, employee);
        int countEmployInfo = 3;
        boolean isDisplayHolidayYear = dataSource.getVariousVacationControl().isAnnualHolidaySetting()
                && dataSource.getHolidaysRemainingManagement()
                .getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
        Optional<GeneralDate> grantDate = dataSource.getMapEmployees().get(employee.getEmployeeId())
                .getHolidayRemainingInfor().getGrantDate();
        if (isDisplayHolidayYear) {

            countEmployInfo += 2;
        }
        boolean checkInsertBlank = true;
        if (totalRowDetails < countEmployInfo) {
            // Insert blank rows
            cells.copyRows(cells, 54, firstRow, countEmployInfo - totalRowDetails);
            firstRow += (countEmployInfo - totalRowDetails);
            count += (countEmployInfo - totalRowDetails);
            checkInsertBlank = false;
        }
        int countHeaderAndInfo = 6 + countEmployInfo;
        if (count < countHeaderAndInfo && checkInsertBlank) {
            cells.copyRows(cells, 54, firstRow, countHeaderAndInfo - count);
            firstRow += (countHeaderAndInfo - count);
            count += (countHeaderAndInfo - count);
        }

        for (int i = 0; i < NUMBER_COLUMN; i++) {
            setBottomBorderStyle(cells.get(firstRow - 1, i));
        }
        checkDto.setFirstRow(firstRow);
        checkDto.setCount(count);
        return checkDto;
    }

    // E
    private DtoCheck printAnnualHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                        HolidayRemainingDataSource dataSource, DtoCheck dtoCheck,
                                        HorizontalPageBreakCollection pageBreaks) throws Exception {
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
        YearMonth currentMonth = employee.getCurrentMonth().get();
        NumberFormat df = new DecimalFormat("#0.0");
        // 年休
        // Check holiday management
        if (!dataSource.getVariousVacationControl().isAnnualHolidaySetting()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        boolean yearlyHoliday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
        if (!(yearlyHoliday)) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }

        val isTime = checkShowAreaAnnualBreak2(dataSource.getHolidaysRemainingManagement());
        int totalAddRows = isTime ? 4 : 2;

        int row23 = 0;
        int row24 = 0;
        int row33 = 0;
        int row34 = 0;
        if (isTime) {
            row23 = 0;
            row24 = 1;
            row33 = 2;
            row34 = 3;
        }

        if (!isTime) {
            row23 = 0;
            row33 = 1;
        }
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < totalAddRows) {
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int i = 0; i < NUMBER_COLUMN; i++) {
                setBottomBorderStyle(cells.get(firstRow - 1, i));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }
        cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 1, firstRow, totalAddRows);
        // E1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_13"));

        // E2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_14"));
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            firstRow += totalAddRows;
            count += totalAddRows;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        // Result RequestList281
        List<AnnLeaGrantNumberImported> listAnnLeaGrant = hdRemainingInfor.getListAnnLeaGrantNumber();
        // Update :超える件数分の行を追加して付与日と付与数を表示する : bỏ giới hạn 10 dòng.
        listAnnLeaGrant = listAnnLeaGrant.stream()
                .sorted(Comparator.comparing(AnnLeaGrantNumberImported::getGrantDate))
                .collect(Collectors.toList());
        if (!isTime) {
            // val isThinBoderTop = (listAnnLeaGrant == null ||listAnnLeaGrant.size() == 0 ||listAnnLeaGrant.size() == 1) &&!isTime;
            Style styleCopy = cells.get(firstRow, 10).getStyle();
            cells.get(firstRow, 9).setStyle(styleCopy);

            Style style = cells.get(firstRow, 9).getStyle();
            style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
            style.setHorizontalAlignment(TextAlignmentType.LEFT);
            cells.get(firstRow, 9).setStyle(style);

            Style styleE116 = cells.get(firstRow + 1, 4).getStyle();
            styleE116.setBorder(BorderType.RIGHT_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());
            styleE116.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
            cells.get(firstRow + 1, 4).setStyle(styleE116);

            Style styleE113 = cells.get(firstRow + 1, 5).getStyle();
            styleE113.setBorder(BorderType.RIGHT_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());
            styleE113.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());

            styleE113.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
            cells.get(firstRow + 1, 5).setStyle(styleE113);

            Style styleE114 = cells.get(firstRow + 1, 6).getStyle();
            styleE114.setBorder(BorderType.RIGHT_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());
            styleE114.setBorder(BorderType.LEFT_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());
            styleE114.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());

            styleE114.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
            cells.get(firstRow + 1, 6).setStyle(styleE114);

            Style styleE115 = cells.get(firstRow + 1, 7).getStyle();
            styleE115.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());
            styleE115.setBorder(BorderType.LEFT_BORDER, CellBorderType.NONE, Color.getAntiqueWhite());

            styleE115.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
            cells.get(firstRow + 1, 7).setStyle(styleE115);

        }
        //RQ 363
        // E3_1
        val text = checkShowAreaAnnualBreak1(
                dataSource.getHolidaysRemainingManagement())
                ? TextResource.localize("KDR001_15")
                : "";
        cells.get(firstRow + (isTime ? 2 : 1), 9).setValue(text);
        if (listAnnLeaGrant != null) {
            for (int i = 0; i < listAnnLeaGrant.size(); i++) {
                if (i >= 2) {
                    if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 2) {
                        printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
                        for (int j = 0; j < NUMBER_COLUMN; j++) {
                            setBottomBorderStyle(cells.get(firstRow - 1, j));
                        }
                        int totalRow = count - 6 - countEmployeeBefore;
                        dtoCheck.setCountEmployeeBefore(0);
                        if (!dataSource.isSameCurrentMonth()) {
                            for (int j = 0; j < totalRow; j++) {
                                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                                setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                            }
                        }
                        if (!dataSource.isSameCurrentMonth()) {
                            for (int j = 0; j < totalRow; j++) {
                                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                                setCurrentMonthBackground(cells.get(first + j, 10 + totalMonth));
                            }
                        }
                        pageBreaks.add(firstRow);
                        cells.copyRows(cells, 0, firstRow, 6);
                        cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                                + SPACE + employee.getWorkplaceName());
                        for (int index = 0; index < NUMBER_COLUMN; index++) {
                            setBottomBorderStyle(cells.get(firstRow + 5, index));
                        }
                        firstRow += 6;
                        count = 6;
                    }
                    cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 5, firstRow + (isTime ? i * 2 : i), (isTime ? 2 : 1));
                    totalAddRows += isTime ? 2 : 1;
                }

                // E1_2, 事象(4)của bug #102883
                cells.get(firstRow + (isTime ? 2 * i : i), 3).setValue(TextResource.localize("KDR001_57",
                        listAnnLeaGrant.get(i).getGrantDate().toString("yyyy/MM/dd")));

                // E1_3
                val vlaueE13 = listAnnLeaGrant.get(i).getGrantDays();
                Double days_Granted = checkShowAreaAnnualBreak1(
                        dataSource.getHolidaysRemainingManagement()) ?
                        (Double) vlaueE13 : null;
                cells.get(firstRow + (isTime ? 2 * i : i), 4).setValue(days_Granted == null ? "" : df.format(days_Granted.doubleValue()));
                setTopBorderStyle(cells.get(firstRow + (isTime ? 2 * i : i), 3));
                if (!isTime) {
                    setBottomBorderStyle(cells.get(firstRow + i, 4));
                    setBottomBorderStyle(cells.get(firstRow + i, 3));
                }
                // E1_16
                val valueE116 = listAnnLeaGrant.get(i).getGrantTime();
                Integer time_Granted = checkShowAreaAnnualBreak1(
                        dataSource.getHolidaysRemainingManagement()) ?
                        (Integer) valueE116 : null;
                cells.get(firstRow + (isTime ? 2 * i : i) + 1, 4)
                        .setValue((time_Granted == null || !isTime) ? "" : convertToTime(time_Granted));
            }
        }
        // RQ 363
        //val listAnnLeaveUsageStatusOfThisMonth = hdRemainingInfor.getListAnnLeaveUsageStatusOfThisMonth();
        // rs 363
        List<AggrResultOfAnnualLeaveEachMonthKdr> rs363New = hdRemainingInfor.getRs363New();
        if (dataSource != null && rs363New != null) {
            // E1_4
            val valueE14 = listAnnLeaGrant.stream().mapToDouble(AnnLeaGrantNumberImported::getRemainDay).sum();
            Double leave_DaysRemain = checkShowAreaAnnualBreak1(
                    dataSource.getHolidaysRemainingManagement()) ?
                    valueE14 : null;
            cells.get(firstRow, 5).setValue(leave_DaysRemain == null ? "" : df.format(leave_DaysRemain.doubleValue()));
            if (leave_DaysRemain != null && leave_DaysRemain < 0) {
                setForegroundRed(cells.get(firstRow, 5));
            }
            // E1_5 - value in 363- 年休_使用数_日数
            // 年休_使用数_日数 =  月度使用日数+ 付与後月度使用日数;
            // 月度使用日数 = 年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．年休(マイナスあり)．使用数．付与前;

            Double use_date = rs363New.stream().filter(e -> e.getYearMonth().compareTo(currentMonth) == 0)
                    .map(e -> {
                        val i = e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                .getUsedNumberBeforeGrant().getUsedDays();
                        if (i.isPresent()) {
                            return i.get().v();
                        } else {
                            return (double) 0;
                        }
                    }).mapToDouble(e -> e).sum();
            // 付与後月度使用日数 = 年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．年休(マイナスあり)．使用数．付与後
            Double use_after_grant = rs363New.stream().filter(e -> e.getYearMonth().compareTo(currentMonth) == 0)
                    .map(e -> e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                            .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                            .getUsedNumberAfterGrantOpt()).mapToDouble(e ->
                            {
                                return e.filter(annualLeaveUsedNumber -> annualLeaveUsedNumber.getUsedDays().isPresent())
                                        .map(annualLeaveUsedNumber -> annualLeaveUsedNumber.getUsedDays().get().v()).orElseGet(() -> (double) 0);
                            }
                    ).sum();

            Double valueE15 = use_date + use_after_grant;
            //Double valueE15 = use_after_grant;
            Double used_Days = checkShowAreaAnnualBreak1(
                    dataSource.getHolidaysRemainingManagement()) ?
                    valueE15 : null;
            cells.get(firstRow, 6).setValue(used_Days == null ? "" : df.format(used_Days.doubleValue()));
            // E1_6 - value in 363 - 年休_残数_日数
            // 年休_残数_日数: So luong con la :月度残日数: 年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)
            // ．残数．年休(マイナスあり)．残数．付与前	合計残日数
            Double valueE16 = rs363New.stream().filter(e -> e.getYearMonth().compareTo(currentMonth) == 0)
                    .map(e -> e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                            .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                            .getRemainingNumberBeforeGrant().getTotalRemainingDays()).mapToDouble(e -> e != null ? e.v() : 0).sum(); //

            Double number_date_remain = checkShowAreaAnnualBreak1(
                    dataSource.getHolidaysRemainingManagement()) ?
                    valueE16 : null;
            cells.get(firstRow, 7).setValue(number_date_remain == null ? "" : df.format(number_date_remain.doubleValue()));

            if (number_date_remain != null && number_date_remain < 0) {
                setForegroundRed(cells.get(firstRow, 7));
            }

            // E1_13; SUM VALUE 281
            if (isTime) {
                Double valueE113 = listAnnLeaGrant.stream()
                        .filter(x -> x.getRemainTime() != null)
                        .mapToDouble(AnnLeaGrantNumberImported::getRemainTime).sum();
                Double leave_Hours = isTime ?
                        valueE113 : null;
                val e113 = leave_Hours != null ? convertToTime((int) (leave_Hours.doubleValue())) : "";
                cells.get(firstRow + 1, 5).setValue((e113));
                if (leave_Hours != null && leave_Hours < 0) {
                    setForegroundRed(cells.get(firstRow + 1, 5));
                }
                // E1_14
                // 年休_使用数_時間 = 月度使用時間+付与後月度使用時間
                // 月度使用時間 = 年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．年休(マイナスあり)．使用数．付与前
                val use_time =
                        rs363New.stream().filter(e -> e.getYearMonth().compareTo(currentMonth) == 0)
                                .map((AggrResultOfAnnualLeaveEachMonthKdr e) -> {
                                    val i = e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                            .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                            .getUsedNumberBeforeGrant().getUsedTime();
                                    if (i.isPresent()) {
                                        return i.get().v();
                                    } else {
                                        return 0;
                                    }
                                }).mapToDouble(e -> e).sum();
                // 付与後月度使用時間 =年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．年休(マイナスあり)．使用数．付与後
                val use_after_grant_time = rs363New.stream().filter(e -> e.getYearMonth().compareTo(currentMonth) == 0)
                        .map(e -> {
                                    val i = e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                            .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                            .getUsedNumberAfterGrantOpt();
                                    if (i.isPresent() && i.get().getUsedTime().isPresent()) {
                                        return i.get().getUsedTime().get().v();
                                    } else return 0;
                                }
                        ).mapToDouble(e -> e).sum();

                val valueE114 = use_time + use_after_grant_time;
                //val valueE114 = use_after_grant_time ;
                Double uses_Hours = valueE114;
                val e114 = uses_Hours != null ? convertToTime(uses_Hours.intValue()) : "";
                cells.get(firstRow + 1, 6).setValue(e114);
                // E1_15
                // 年休_残数_時間 :月度残時間
                // 月度残時間 = 年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．年休(マイナスあり)．残数．付与前
                val valueE115 = rs363New.stream().filter(e -> e.getYearMonth().compareTo(currentMonth) == 0)
                        .map(e -> e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                                .getRemainingNumberBeforeGrant().getTotalRemainingTime()).mapToDouble(e -> e.isPresent() && e != null ? e.get().v() : 0).sum();
                Double leave_RemainHours = valueE115;
                val e115 = leave_RemainHours != null ? convertToTime(leave_RemainHours.intValue()) : "";
                cells.get(firstRow + 1, 7).setValue(e115);
                if (leave_RemainHours != null && leave_RemainHours < 0) {
                    setForegroundRed(cells.get(firstRow + 1, 7));
                }
            }
        }
        if (!employee.getCurrentMonth().isPresent()) {
            firstRow += totalAddRows;
            count += totalAddRows;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }

        // Result RequestList255
        List<AnnualLeaveUsageImported> listAnnLeaveUsage =
                hdRemainingInfor.getListAnnualLeaveUsage();

        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        // BEFORE THIS MONTH: 255
        if (listAnnLeaveUsage != null && dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor().getGrantDate().isPresent()) {
            for (AnnualLeaveUsageImported item : listAnnLeaveUsage) {
                if (item.getYearMonth().compareTo(currentMonth) >= 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }

                val value23 = item.getUsedDays() == 0 ? null : item.getUsedDays();
                Double e23 = checkShowAreaAnnualBreak1(
                        dataSource.getHolidaysRemainingManagement()) ?
                        value23 : null;
                // E2_3 当月より前, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                val vl23 = e23 != null && e23 != 0 ? df.format(e23.doubleValue()) : "";
                cells.get(firstRow + row23, 10 + totalMonth)
                        .setValue(vl23);

                if (isTime) {
                    // E2_4:月度使用時間 Update KDR ver 15
                    val value24 = item.getUsedTime();
                    val use_Time = value24;
                    val e24 = use_Time != null && use_Time != 0 ? convertToTime((int) (use_Time)) : "";
                    cells.get(firstRow + +row24, 10 + totalMonth)
                            .setValue(e24);
                }

                // E3_3 当月より前
                val valueE33 = item.getRemainingDays();
                val e33 = checkShowAreaAnnualBreak1(dataSource.getHolidaysRemainingManagement()) ?
                        valueE33 : null;
                cells.get(firstRow + row33, 10 + totalMonth)
                        .setValue(e33 == null ? "" : df.format(e33));
                // E3_4 当月より前

                if (valueE33 != null && valueE33 < 0) {
                    setForegroundRed(cells.get(firstRow + row33, 10 + totalMonth));

                }
                if (isTime) {
                    val valueE34 = item.getRemainingTime();
                    val e34 = valueE34;
                    val vle34 = e34 != null ? convertToTime((int) (e34.doubleValue())) : "";
                    cells.get(firstRow + row34, 10 + totalMonth)
                            .setValue(vle34);
                    if (valueE34 != null && valueE34 < 0) {
                        setForegroundRed(cells.get(firstRow + row34, 10 + totalMonth));
                    }
                }

            }
        }
        // Result RequestList363
        val closeDateOpt = hdRemainingInfor.getClosureInforOpt();
            for (AggrResultOfAnnualLeaveEachMonthKdr item : rs363New) {
                if (currentMonth.compareTo(item.getYearMonth()) > 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                if (currentMonth.compareTo(item.getYearMonth()) == 0) {
                    val listPerMonth = listAnnLeaveUsage.stream().filter(e -> e != null && e.getYearMonth() != null
                            && e.getYearMonth().equals(currentMonth))
                            .collect(Collectors.toList());
                    // 年休使用日数 実績値:
                    // BEFORE : 月度使用日数＋(255)年休利用状況.月度使用日数(※)
                    val use_date363opt = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                            .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                            .getUsedNumberBeforeGrant().getUsedDays();

                    if (checkShowAreaAnnualBreak1(
                            dataSource.getHolidaysRemainingManagement())) {
                        Double value23bf = null;
                        if (use_date363opt.isPresent()) {
                            val use_date363 = use_date363opt.get().v();
                            val use_date255 = listPerMonth.stream().mapToDouble(AnnualLeaveUsageImported::getUsedDays).sum();

                            value23bf = use_date255 + use_date363;

                        }
                        val value23afOpt = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                .getUsedNumberAfterGrantOpt();
                        String value23af = null;
                        if (value23afOpt.isPresent() && value23afOpt.get().getUsedDays().get() != null) {
                            value23af = df.format(value23afOpt.get().getUsedDays().get().v());
                        }
                        if (closeDateOpt.isPresent()) {
                            val cls = closeDateOpt.get();
                            val ymd = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getYmd();
                            if (ymd.day() <= (cls.getClosureDate().getClosureDay().v() + 1)) {
                                if (value23bf != null && value23bf != 0.0) {
                                    val vl23 = df.format(value23bf);
                                    cells.get(firstRow + row23, 10 + totalMonth)
                                            .setValue(vl23);
                                }
                            } else {
                                if (value23af != null && value23bf != null) {
                                    val vl23 = df.format(value23bf) + "/" + value23af;
                                    // E2_3 当月以降, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                                    cells.get(firstRow + row23, 10 + totalMonth)
                                            .setValue(vl23);
                                } else {
                                    val vl23bf = value23bf != null && value23bf != 0 ? df.format(value23bf) : "";
                                    val vl23af = value23af != null && value23bf != 0 ? df.format(value23af) : "";
                                    cells.get(firstRow + row23, 10 + totalMonth)
                                            .setValue(vl23bf + vl23af);
                                }
                            }
                        }

                    }
                    // E3_3 当月以降 : 月度残日数
                    val value33 = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                            .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                            .getRemainingNumberBeforeGrant().getTotalRemainingDays() != null ?
                            item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                    .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                                    .getRemainingNumberBeforeGrant().getTotalRemainingDays()
                                    .v() : null;

                    Double e33 = checkShowAreaAnnualBreak1(
                            dataSource.getHolidaysRemainingManagement()) ?
                            value33 : null;
                    cells.get(firstRow + row33, 10 + totalMonth).setValue(e33 != null
                            && e33 != 0
                            ? df.format(e33) : null);
                    if (e33 != null && e33 < 0) {
                        setForegroundRed(cells.get(firstRow + row33, 10 + totalMonth));
                    }
                    // E2_4
                    if (isTime) {
                        val use_time255 = listPerMonth.stream().mapToDouble(AnnualLeaveUsageImported::getUsedTime).sum();
                        // BEFORE: 付与前 = 月度使用時間＋(255)年休利用状況.月度使用時間(※)
                        val use_time363Opt = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                .getUsedNumberBeforeGrant().getUsedTime();
                        String e24bf = null;
                        String e24af = null;
                        val value243afOpt = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                .getUsedNumberAfterGrantOpt();
                        if (use_time363Opt.isPresent()) {
                            val use_time363 = use_time363Opt.get().v();
                            val value24bf = use_time255 + use_time363;
                            e24bf = value24bf != 0 ? convertToTime((int) value24bf) : null;
                            // AF : 付与後月度使用時間 :付与前
                        }
                        if (value243afOpt.isPresent() && value243afOpt.get().getUsedTime().get() != null
                                && value243afOpt.get().getUsedTime().get().v() != 0) {
                            e24af = convertToTime((int) value243afOpt.get().getUsedTime().get().v());
                        }
                        if (closeDateOpt.isPresent()) {
                            val cls = closeDateOpt.get();
                            val ymd = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getYmd();
                            if (ymd.day() <= (cls.getClosureDate().getClosureDay().v() + 1)) {
                                cells.get(firstRow + row24, 10 + totalMonth).setValue(e24bf);
                            } else {
                                if (e24af != null && e24bf != null) {
                                    cells.get(firstRow + +row24, 10 + totalMonth).setValue(e24bf + "/" + e24af);
                                } else {
                                    e24bf = e24bf != null ? e24bf : "";
                                    e24af = e24af != null ? e24af : "";
                                    cells.get(firstRow + row24, 10 + totalMonth).setValue(e24bf + e24af);
                                }
                            }
                        }
                        //E3-4:
                        // 年休残時間 実績値 : 月度残時間
                        val value34 = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                                .getRemainingNumberBeforeGrant().getTotalRemainingTime().isPresent() ?
                                item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                        .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                                        .getRemainingNumberBeforeGrant().getTotalRemainingTime().get().v() : null;
                        Integer e34 = value34;

                        cells.get(firstRow + row34, 10 + totalMonth).setValue(e34 != null
                                && e34 != 0
                                ? convertToTime((int) e34.doubleValue()) : null);
                        if (e34 != null && e34 < 0) {
                            setForegroundRed(cells.get(firstRow + row34, 10 + totalMonth));
                        }
                    }
                }
                if (item.getYearMonth().compareTo(currentMonth) > 0) {

                    //E2_3 年休使用日数 実績値: 月度使用日数;
                    val value23 = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                            .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                            .getUsedNumberBeforeGrant().getUsedDays().isPresent() ?
                            (item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                    .getRemainingNumber().getAnnualLeaveWithMinus()
                                    .getUsedNumberInfo() != null ?
                                    item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                            .getRemainingNumber().getAnnualLeaveWithMinus()
                                            .getUsedNumberInfo().getUsedNumberBeforeGrant().getUsedDays()
                                            .get().v() : null)

                            : null;

                    Double e23 = checkShowAreaAnnualBreak1(
                            dataSource.getHolidaysRemainingManagement()) ?
                            (Double) value23 : null;
                    cells.get(firstRow + row23, 10 + totalMonth)
                            .setValue(e23 != null
                                    && e23 != 0
                                    ? df.format(e23) : null);
                    if (e23 != null && e23 < 0) {
                        setForegroundRed(cells.get(firstRow + row23, 10 + totalMonth));
                    }

                    if (isTime) {
                        //E2_4: 月度使用時間
                        val value24 = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                .getUsedNumberBeforeGrant().getUsedTime().isPresent() ?
                                item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                        .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                                        .getUsedNumberBeforeGrant().getUsedTime().get().v() : null;

                        Integer e24 = checkShowAreaAnnualBreak1(
                                dataSource.getHolidaysRemainingManagement()) ?
                                (Integer) value24 : null;
                        val vl24 = e24 != null && e24 != 0 ? convertToTime((int) (e24)) : "";

                        cells.get(firstRow + row24, 10 + totalMonth).setValue(vl24);
                        if (value24 != null && value24 < 0) {
                            setForegroundRed(cells.get(firstRow + row24, 10 + totalMonth));
                        }
                        // Update KDR 001 : 値＝(クリア)/背景色＝グレー;
                        setBackgroundGray(cells.get(firstRow + row34, 10 + totalMonth));
                    }
                    // Update KDR 001 : 値＝(クリア)/背景色＝グレー;
                    setBackgroundGray(cells.get(firstRow + row33, 10 + totalMonth));
                }
            }

        firstRow += totalAddRows;
        count += totalAddRows;
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        dtoCheck.setFirstRow(firstRow);
        dtoCheck.setCount(count);
        return dtoCheck;

    }

    // H
    private DtoCheck printYearlyReserved(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                         HolidayRemainingDataSource dataSource, DtoCheck dtoCheck,
                                         HorizontalPageBreakCollection pageBreaks) throws Exception {
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        NumberFormat df = new DecimalFormat("#0.0");
        if (!checkYearlyLeave(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode(),
                dataSource.getVariousVacationControl())) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val checkG = checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement());
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 2) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setTopBorderStyle(cells.get(firstRow, 2 + index));
        }
        cells.copyRows(cells, checkG ? NUMBER_ROW_OF_HEADER + 10 : 63, firstRow, 2);
        // H1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_76"));
        // H2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_14"));
        // H2_2
        cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_15"));
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            firstRow += 2;
            count += 2;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        // Result RequestList268
        val reserveHolidayImported = hdRemainingInfor.getReserveHoliday();
        if (reserveHolidayImported != null) {
            // H1_2
            val h12 = df.format(reserveHolidayImported.getGrantNumber().doubleValue());
            cells.get(firstRow, 4).setValue(h12);
            // H1_3
            cells.get(firstRow, 5).setValue(df.format(reserveHolidayImported.getStartMonthRemain().doubleValue()));
            if (reserveHolidayImported.getStartMonthRemain() < 0) {
                setForegroundRed(cells.get(firstRow, 5));
            }
            // H1_4
            cells.get(firstRow, 6).setValue(df.format(reserveHolidayImported.getUsedNumber().doubleValue()));
            // H1_5
            cells.get(firstRow, 7).setValue(df.format(reserveHolidayImported.getRemainNumber().doubleValue()));
            if (reserveHolidayImported.getRemainNumber() < 0) {
                setForegroundRed(cells.get(firstRow, 7));
            }
            // H1_6
            cells.get(firstRow, 8).setValue(df.format(reserveHolidayImported.getUndigestNumber().doubleValue()));
            if (reserveHolidayImported.getUndigestNumber() != null
                    && reserveHolidayImported.getUndigestNumber() > 0) {
                setForegroundRed(cells.get(firstRow, 8));
            }
        }
        if (!employee.getCurrentMonth().isPresent()) {
            firstRow += 2;
            count += 2;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        YearMonth currentMonth = employee.getCurrentMonth().get();
        // Result RequestList258
        val reservedYearHolidayImportedList = hdRemainingInfor.getListReservedYearHoliday();
        // Result RequestList364
        val rsvLeaUsedCurrentMonImported = hdRemainingInfor.getListRsvLeaUsedCurrentMon();
        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        if (reservedYearHolidayImportedList != null) {
            for (ReservedYearHolidayImported reservedYearHolidayItem : reservedYearHolidayImportedList) {
                // Before this month
                if (currentMonth.compareTo(reservedYearHolidayItem.getYearMonth()) <= 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(),
                        reservedYearHolidayItem.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                // H2_3 当月より前, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                cells.get(firstRow, 10 + totalMonth)
                        .setValue(reservedYearHolidayItem.getUsedDays() != null
                                && reservedYearHolidayItem.getUsedDays() == 0 ? null : df.format(reservedYearHolidayItem.getUsedDays().doubleValue()));
                // H2_4 当月より前
                if (reservedYearHolidayItem != null && reservedYearHolidayItem.getRemainingDays() != null)
                    cells.get(firstRow + 1, 10 + totalMonth).setValue(df.format(reservedYearHolidayItem.getRemainingDays().doubleValue()));
                if (reservedYearHolidayItem.getRemainingDays() < 0) {
                    setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
                }
            }
        }

        if (rsvLeaUsedCurrentMonImported != null) {
            for (RsvLeaUsedCurrentMonImported rsvLeaUsedCurrentMonItem : rsvLeaUsedCurrentMonImported) {
                // After this month
                if (currentMonth.compareTo(rsvLeaUsedCurrentMonItem.getYearMonth()) > 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(),
                        rsvLeaUsedCurrentMonItem.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                // H2_3 当月以降, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                cells.get(firstRow, 10 + totalMonth).setValue(rsvLeaUsedCurrentMonItem.getUsedNumber() != null
                        && rsvLeaUsedCurrentMonItem.getUsedNumber() == 0 ? null : df.format(rsvLeaUsedCurrentMonItem.getUsedNumber().doubleValue()));
                // H2_4 当月以降
                if (currentMonth.compareTo(rsvLeaUsedCurrentMonItem.getYearMonth()) == 0) {
                    cells.get(firstRow + 1, 10 + totalMonth)
                            .setValue(rsvLeaUsedCurrentMonItem.getRemainNumber() != null
                                    && rsvLeaUsedCurrentMonItem.getRemainNumber() == 0 ? null : df.format(rsvLeaUsedCurrentMonItem.getRemainNumber().doubleValue()));
                } else if (currentMonth.compareTo(rsvLeaUsedCurrentMonItem.getYearMonth()) < 0) {
                    setBackgroundGray(cells.get(firstRow + 1, 10 + totalMonth));
                }
            }
        }
        firstRow += 2;
        count += 2;
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    // I
    private DtoCheck printSubstituteHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                            HolidayRemainingDataSource dataSource, DtoCheck dtoCheck, HorizontalPageBreakCollection pageBreaks) throws Exception {
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        // 代休
        NumberFormat df = new DecimalFormat("#0.0");

        if (!checkTakeABreak_01(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode())) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        val checkShow = dataSource.getVariousVacationControl();
        int totalRows = 2;
        val checkG = checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement());
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }
        cells.copyRows(cells, checkG ? NUMBER_ROW_OF_HEADER + 12 : 65, firstRow, 2);
        // I1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_77"));
        // I2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_16"));
        // I3_1
        cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_17"));
        if (hdRemainingInfor == null) {
            firstRow += 2;
            count += 2;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        val isShow41 = checkTakeABreak_02(dataSource.getHolidaysRemainingManagement());
        val isShow51 = checkTakeABreak_03(dataSource.getHolidaysRemainingManagement());
        val show1item = (isShow51 && !isShow41) || (!isShow51 && isShow41);

        if (isShow41 && isShow51) {
            cells.copyRows(cells, checkG ? NUMBER_ROW_OF_HEADER + 14 : 67, firstRow + 2, 2);
            totalRows += 2;
        }
        if (show1item) {
            cells.copyRows(cells, checkG ? NUMBER_ROW_OF_HEADER + 14 : 67, firstRow + 2, 1);
            totalRows += 1;
        }
        if (isShow41) {
            // I4_1
            if (checkTakeABreak_02(dataSource.getHolidaysRemainingManagement()))
                cells.get(firstRow + 2, 9).setValue(TextResource.localize("KDR001_11"));
        }
        if (isShow51) {
            // I5_1
            cells.get(firstRow + (show1item ? 2 : 3), 9).setValue(TextResource.localize("KDR001_18"));
        }

        if (!employee.getCurrentMonth().isPresent()) {
            firstRow += totalRows;
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val companyID = AppContexts.user().companyId();
        YearMonth currentMonth = employee.getCurrentMonth().get();
        // Set value for I
        // Result RequestList259
        val statusHolidayImported = hdRemainingInfor.getListStatusHoliday();
        // RequestList 203
        val substituteHolidayAggrResult = hdRemainingInfor.getSubstituteHolidayAggrResult();
        // ドメインモデル「代休管理設定」を取得する
        CompensatoryLeaveComSetting compensatoryLeaveComSet = compensLeaveComSetRepository.find(companyID);

        boolean isTime = false;
        if (compensatoryLeaveComSet != null)
            isTime = compensatoryLeaveComSet
                    .getCompensatoryDigestiveTimeUnit().getIsManageByTime() == ManageDistinct.YES;

        val substituteHolidayAggrResultsRight = hdRemainingInfor.getSubstituteHolidayAggrResultsRight();

        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());

        if (statusHolidayImported != null) {
            for (StatusHolidayImported statusHolidayItem : statusHolidayImported) {
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), statusHolidayItem.getYm());

                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                // BEFORE
                else if (statusHolidayItem.getYm().compareTo(currentMonth) < 0) {
                    // 代休発生数合計
                    // I2_3
                    String occurrence = "";
                    // 代休使用数合計
                    // I3_3
                    String use = "";
                    //代休未消化数
                    // I4_3
                    String unUsed = "";
                    // 代休残数
                    // I5_3 代休_残数_日数
                    String remain = "";
                    if (statusHolidayItem.getRemainTimes() == null) {
                        occurrence = statusHolidayItem.getOccurrenceDays() == null || statusHolidayItem.getOccurrenceDays() == 0 ? "" :
                                df.format(statusHolidayItem.getOccurrenceDays().doubleValue());
                        use = statusHolidayItem.getUseDays() == null || statusHolidayItem.getUseDays() == 0 ? "" :
                                df.format(statusHolidayItem.getUseDays().doubleValue());
                        unUsed = statusHolidayItem.getUnUsedDays() == null || statusHolidayItem.getUnUsedDays() == 0 ? "" :
                                df.format(statusHolidayItem.getUnUsedDays().doubleValue());
                        remain = statusHolidayItem.getRemainDays() == null ? "" : df.format(statusHolidayItem.getRemainDays().doubleValue());
                    } else {
                        occurrence = (statusHolidayItem.getOccurrenceTimes() == 0 ? "" :
                                convertToTime(statusHolidayItem.getOccurrenceTimes()));
                        use = (statusHolidayItem.getUseTimes() == 0 ? "" : convertToTime(statusHolidayItem.getUseTimes()));

                        unUsed = (statusHolidayItem.getUnUsedTimes() == 0 ? "" :
                                convertToTime(statusHolidayItem.getUnUsedTimes()));
                        remain = (convertToTime(statusHolidayItem.getRemainTimes()));
                    }

                    cells.get(firstRow, 10 + totalMonth).setValue(occurrence);
                    cells.get(firstRow + 1, 10 + totalMonth).setValue(use);
                    if (isShow41) {
                        cells.get(firstRow + 2, 10 + totalMonth)
                                .setValue(unUsed);
                        if (statusHolidayItem.getUnUsedTimes() != null && statusHolidayItem.getUnUsedTimes() > 0) {
                            setForegroundRed(cells.get(firstRow + 2, 10 + totalMonth));
                        }
                        if (statusHolidayItem.getUnUsedTimes() == null && statusHolidayItem.getUnUsedDays() != null && statusHolidayItem.getUnUsedDays() > 0) {
                            setForegroundRed(cells.get(firstRow + 2, 10 + totalMonth));
                        }
                    }
                    if (isShow51) {
                        cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth)
                                .setValue(remain);
                        if (statusHolidayItem.getRemainTimes() != null && statusHolidayItem.getRemainTimes() < 0) {
                            setForegroundRed(cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth));
                        }
                        if (statusHolidayItem.getRemainTimes() == null && statusHolidayItem.getRemainDays() != null && statusHolidayItem.getRemainDays() < 0) {
                            setForegroundRed(cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth));
                        }
                    }
                }
            }

        }

        // Current Month anf future;
        val listMonth = substituteHolidayAggrResultsRight.keySet();
        for (val item : listMonth) {
            val statusHolidayItem = substituteHolidayAggrResultsRight.get(item);

            if (item.compareTo(currentMonth) < 0) {
                continue;
            } else if (item.compareTo(currentMonth) >= 0) {
                //I2_3
                String occurrence = "";
                // I3_3
                String use = "";
                if (!isTime) {
                    occurrence = statusHolidayItem.getOccurrenceDay() == null
                            || statusHolidayItem.getOccurrenceDay().v() == 0 ? "" :
                            df.format(statusHolidayItem.getOccurrenceDay().v());
                    use = statusHolidayItem.getDayUse() == null
                            || statusHolidayItem.getDayUse().v() == 0 ? "" :
                            df.format(statusHolidayItem.getDayUse().v());
                } else {
                    occurrence = (statusHolidayItem.getOccurrenceTime() == null
                            || statusHolidayItem.getOccurrenceTime().v() == 0 ? "" :
                            convertToTime(statusHolidayItem.getOccurrenceTime().v()));
                    use = statusHolidayItem.getTimeUse() == null
                            || statusHolidayItem.getTimeUse().v() == 0 ? "" :
                            convertToTime(statusHolidayItem.getTimeUse().v());
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item);
                cells.get(firstRow, 10 + totalMonth).setValue(occurrence);
                cells.get(firstRow + 1, 10 + totalMonth)
                        .setValue(use);
                if (item.compareTo(currentMonth) == 0) {
                    if (isShow41) {
                        // I4_3 代休_未消化_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                        String unUsed = "";
                        if (!isTime) {
                            unUsed = statusHolidayItem.getUnusedDay() == null
                                    || statusHolidayItem.getUnusedDay().v() == 0 ? "" :
                                    df.format(statusHolidayItem.getUnusedDay().v());
                        } else {
                            unUsed = (statusHolidayItem.getUnusedTime() == null ||
                                    statusHolidayItem.getUnusedTime().v() == 0 ? "" :
                                    convertToTime(statusHolidayItem.getUnusedTime().v()));
                        }
                        cells.get(firstRow + 2, 10 + totalMonth)
                                .setValue(unUsed);
                        if (isTime && statusHolidayItem.getUnusedTime() != null && statusHolidayItem.getUnusedTime().v() > 0) {
                            setForegroundRed(cells.get(firstRow + 2, 10 + totalMonth));
                        }
                        if (!isTime && statusHolidayItem.getUnusedDay() != null && statusHolidayItem.getUnusedDay().v() > 0) {
                            setForegroundRed(cells.get(firstRow + 2, 10 + totalMonth));
                        }
                    }
                    if (isShow51) {
                        // I5_3 代休_残数_日数
                        String remain = "";
                        if (isTime) {
                            remain = (statusHolidayItem.getRemainTime() == null ? "" :
                                    convertToTime(statusHolidayItem.getRemainTime().v()));

                            cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth)
                                    .setValue(remain);
                            if (statusHolidayItem.getRemainTime() != null && statusHolidayItem.getRemainTime().v() < 0) {
                                setForegroundRed(cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth));
                            }
                        }
                        if (!isTime) {
                            remain = statusHolidayItem.getRemainDay() == null ? "" : df.format(statusHolidayItem.getRemainDay().v());
                            cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth)
                                    .setValue(remain);
                            if (statusHolidayItem.getRemainDay() != null && statusHolidayItem.getRemainDay().v() < 0) {
                                setForegroundRed(cells.get(firstRow + (isShow41 ? 3 : 2), 10 + totalMonth));
                            }
                        }
                    }
                } else {
                    setBackgroundGray(cells.get(firstRow + 2, 10 + totalMonth));
                    setBackgroundGray(cells.get(firstRow + (show1item ? 2 : 3), 10 + totalMonth));
                }
            }
        }
        // 月初残数
        val monRemainingDate = substituteHolidayAggrResult.getCarryoverDay().v();
        // 時間
        val monRemainingTime = substituteHolidayAggrResult.getCarryoverTime().v();
        String i12 = "";
        if (checkShow.isSubstituteHolidaySetting() && monRemainingDate != null && !isTime) {
            i12 = monRemainingDate == 0 ? "0.0" : df.format(monRemainingDate);
        } else if (checkShow.isSubstituteHolidaySetting() && monRemainingTime != null && isTime) {
            i12 = monRemainingTime == 0 ? "0:00" : convertToTime(monRemainingTime);
        }
        cells.get(firstRow, 5).setValue(i12);
        if (checkShow.isSubstituteHolidaySetting() && monRemainingDate != null && monRemainingDate < 0) {
            setForegroundRed(cells.get(firstRow, 5));
        }
        if (checkShow.isSubstituteHolidaySetting() && monRemainingTime != null &&
                monRemainingTime < 0) {
            setForegroundRed(cells.get(firstRow, 5));
        }
        //I1_3 使用数
        // 日数
        val dateUse = substituteHolidayAggrResult.getDayUse().v();
        // 時間
        val timeUse = substituteHolidayAggrResult.getTimeUse().v();
        // 代休+時間代休の場合
        String i13 = "";
        if (checkShow.isSubstituteHolidaySetting() && dateUse != null && !isTime)
            i13 = dateUse == 0 ? "0.0" : df.format(dateUse);
        else if (checkShow.isSubstituteHolidaySetting() && timeUse != null && isTime) {
            i13 = timeUse == 0 ? "0:00" : convertToTime(timeUse);
        }
        cells.get(firstRow, 6).setValue(i13);
        val remainingChargeSubstitute = dataSource.getHolidaysRemainingManagement()
                .getListItemsOutput().getSubstituteHoliday().isRemainingChargeSubstitute();
        if (remainingChargeSubstitute) {
            // I1_4 残数
            // 日数
            val remainDay = substituteHolidayAggrResult.getRemainDay().v();
            //時間
            val remainTime = substituteHolidayAggrResult.getRemainTime().v();
            // 代休+時間代休の場合
            String i14 = "";
            if (checkShow.isSubstituteHolidaySetting() && remainDay != null && !isTime) {
                i14 = remainDay == 0 ? "0.0" : df.format(remainDay);
            } else if (checkShow.isSubstituteHolidaySetting()
                    && (remainTime != null) && isTime) {
                i14 = remainTime == 0 ? "0:00" : convertToTime(remainTime);
            }
            cells.get(firstRow, 7).setValue(i14);
            if (checkShow.isSubstituteHolidaySetting() && remainDay != null && remainDay != 0 && remainDay < 0) {
                setForegroundRed(cells.get(firstRow, 7));
            }
            if (checkShow.isSubstituteHolidaySetting() && (remainTime != null)
                    && remainTime < 0) {
                setForegroundRed(cells.get(firstRow, 7));
            }
        } else {
            setBackgroundGray(cells.get(firstRow, 7));
        }

        val representSubstitute = dataSource.getHolidaysRemainingManagement()
                .getListItemsOutput().getSubstituteHoliday().isRepresentSubstitute();
        // I1_5: 未消化数
        //日数
        val unusedDay = substituteHolidayAggrResult.getUnusedDay().v();
        //時間
        val unusedTime = substituteHolidayAggrResult.getUnusedTime().v();
        if (representSubstitute) {
            //代休+時間代休の場合
            String i15 = "";
            if (checkShow.isSubstituteHolidaySetting() && (unusedDay != null) && !isTime) {
                i15 = unusedDay == 0 ? "0.0" : df.format(unusedDay);
            } else if (checkShow.isSubstituteHolidaySetting()
                    && (unusedTime != null) && isTime) {
                i15 = unusedTime == 0 ? "0:00" : convertToTime(unusedTime);
            }
            cells.get(firstRow, 8).setValue(i15);

            if (checkShow.isSubstituteHolidaySetting() && unusedDay != null && unusedDay > 0) {
                setForegroundRed(cells.get(firstRow, 8));
            }
            if (checkShow.isSubstituteHolidaySetting() && (unusedTime != null)
                    && unusedTime > 0) {
                setForegroundRed(cells.get(firstRow, 8));
            }
        } else {
            setBackgroundGray(cells.get(firstRow, 8));
        }

        firstRow += totalRows;
        count += totalRows;
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        dtoCheck.setFirstRow(firstRow);
        dtoCheck.setCount(count);
        return dtoCheck;
    }

    // J
    private DtoCheck printPauseHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                       HolidayRemainingDataSource dataSource, DtoCheck dtoCheck,
                                       HorizontalPageBreakCollection pageBreaks) throws Exception {
        // 振休
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        NumberFormat df = new DecimalFormat("#0.0");
        if (!checkJ1(dataSource, employee)) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val holiday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause();
        boolean isPauseItem = holiday.isPauseItem();
        boolean isUndigestedPause = holiday.isUndigestedPause();
        boolean isNumberRemainingPause = holiday.isNumberRemainingPause();

        if (!isPauseItem) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        int totalRows = 2;
        int rowIndexUndigestedPause = 0;
        int rowIndexNumberRemainingPause = 0;
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }
        val showI = checkTakeABreak_01(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode());
        val isShow41 = checkTakeABreak_02(dataSource.getHolidaysRemainingManagement());
        val isShow51 = checkTakeABreak_03(dataSource.getHolidaysRemainingManagement());
        val show1item = (isShow51 && !isShow41) || (!isShow51 && isShow41);
        val checkG = checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement());

        boolean checkIG = false;
        if (!checkG) {
            checkIG = !checkIG;
        }
        if (showI & show1item) {
            checkIG = !checkIG;
        }
        cells.copyRows(cells, !checkIG ? NUMBER_ROW_OF_HEADER + 16 : 65, firstRow, 2);
        // J1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_78"));
        // J2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_16"));
        // J2_2
        cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_17"));
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            firstRow += 2;
            dtoCheck.setFirstRow(firstRow);
            count += 2;
            dtoCheck.setCount(count);
            return dtoCheck;
        }

        if (isUndigestedPause) {
            rowIndexUndigestedPause = firstRow + totalRows;
            cells.copyRows(cells, !checkIG ? NUMBER_ROW_OF_HEADER + 18 : 67, rowIndexUndigestedPause, 1);
            // J2_3
            cells.get(rowIndexUndigestedPause, 9).setValue(TextResource.localize("KDR001_11"));
            totalRows += 1;
        }
        if (isNumberRemainingPause) {
            rowIndexNumberRemainingPause = firstRow + totalRows;
            cells.copyRows(cells, !checkIG ? NUMBER_ROW_OF_HEADER + 19 : isUndigestedPause ? 68 : 67, rowIndexNumberRemainingPause, 1);
            // J2_4
            cells.get(rowIndexNumberRemainingPause, 9).setValue(TextResource.localize("KDR001_18"));
            totalRows += 1;
        }

        if (!employee.getCurrentMonth().isPresent()) {

            firstRow += totalRows;
            count += totalRows;
            dtoCheck.setCount(count);
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        YearMonth currentMonth = employee.getCurrentMonth().get();
        // Set value for 振休 J
        // Result RequestList 204
        val currentHolidayList = hdRemainingInfor.getListCurrentHolidayRemain();
        // Result RequestList260
        val StatusOfHolidayList = hdRemainingInfor.getListStatusOfHoliday();
        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        if (StatusOfHolidayList != null) {
            for (StatusOfHolidayImported statusOfHDItem : StatusOfHolidayList) {
                // Before this month
                if (currentMonth.compareTo(statusOfHDItem.getYm()) <= 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), statusOfHDItem.getYm());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                // J2_5 振休_発生, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                cells.get(firstRow, 10 + totalMonth).setValue(statusOfHDItem.getOccurredDay() == null ||
                        statusOfHDItem.getOccurredDay() == 0.0 ? null : df.format(statusOfHDItem.getOccurredDay().doubleValue()));
                // J2_6 振休_使用, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                cells.get(firstRow + 1, 10 + totalMonth).setValue(statusOfHDItem.getUsedDays() == null ||
                        statusOfHDItem.getUsedDays() == 0.0 ? null : df.format(statusOfHDItem.getUsedDays().doubleValue()));
                if (isUndigestedPause) {
                    // J2_7 振休_未消化, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                    cells.get(rowIndexUndigestedPause, 10 + totalMonth).setValue(statusOfHDItem.getUnUsedDays() == null
                            || statusOfHDItem.getUnUsedDays() == 0.0 ? null : df.format(statusOfHDItem.getUnUsedDays().doubleValue()));
                    if (statusOfHDItem.getUnUsedDays() != null && statusOfHDItem.getUnUsedDays() > 0) {
                        setForegroundRed(cells.get(rowIndexUndigestedPause, 10 + totalMonth));
                    }
                }
                if (isNumberRemainingPause) {
                    // J2_8 振休_残数
                    cells.get(isUndigestedPause ? rowIndexNumberRemainingPause : rowIndexNumberRemainingPause - 1, 10 + totalMonth)
                            .setValue(df.format(statusOfHDItem.getRemainingDays().doubleValue()));
                    if (statusOfHDItem.getRemainingDays() < 0) {
                        setForegroundRed(cells.get(isUndigestedPause ? rowIndexNumberRemainingPause : rowIndexNumberRemainingPause - 1, 10 + totalMonth));
                    }
                }
            }
        }

        if (currentHolidayList != null) {
            for (CurrentHolidayRemainImported holidayRemainItem : currentHolidayList) {
                if (currentMonth.compareTo(holidayRemainItem.getYm()) <= 0) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), holidayRemainItem.getYm());
                    if (maxRange >= totalMonth && totalMonth >= 0) {
                        // J2_5 振休_発生, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                        cells.get(firstRow, 10 + totalMonth).setValue(holidayRemainItem.getMonthOccurrence() != null
                                && holidayRemainItem.getMonthOccurrence() == 0.0 ? null :
                                df.format(holidayRemainItem.getMonthOccurrence().doubleValue()));
                        // J2_6 振休_使用, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                        cells.get(firstRow + 1, 10 + totalMonth).setValue(holidayRemainItem.getMonthUse() != null
                                && holidayRemainItem.getMonthUse() == 0.0 ? null :
                                df.format(holidayRemainItem.getMonthUse().doubleValue()));
                        if (currentMonth.compareTo(holidayRemainItem.getYm()) == 0) {
                            if (isUndigestedPause) {
                                // J2_7 振休_未消化, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                                cells.get(rowIndexUndigestedPause, 10 + totalMonth).setValue(
                                        holidayRemainItem.getMonthExtinction() != null
                                                && holidayRemainItem.getMonthExtinction() == 0 ? null :
                                                df.format(holidayRemainItem.getMonthExtinction().doubleValue()));
                                if (holidayRemainItem.getMonthExtinction() != null
                                        && holidayRemainItem.getMonthExtinction() > 0) {
                                    setForegroundRed(cells.get(rowIndexUndigestedPause, 10 + totalMonth));
                                }
                            }
                            if (isNumberRemainingPause) {
                                // J2_8 振休_残数
                                cells.get(isUndigestedPause ? rowIndexNumberRemainingPause : rowIndexNumberRemainingPause - 1, 10 + totalMonth)
                                        .setValue(df.format(holidayRemainItem.getMonthEndRemain().doubleValue()));
                                if (holidayRemainItem.getMonthEndRemain() < 0) {
                                    setForegroundRed(cells.get(isUndigestedPause ? rowIndexNumberRemainingPause : rowIndexNumberRemainingPause - 1, 10 + totalMonth));
                                }
                            }
                        }
                    }
                }
            }
        }
        // Set background
        for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
                dataSource.getEndMonth().yearMonth()); i++) {
            if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
                if (isUndigestedPause) {
                    setBackgroundGray(cells.get(firstRow + 2, 10 + i));
                }
                if (isNumberRemainingPause) {
                    setBackgroundGray(cells.get(isUndigestedPause ? (firstRow + 3) : (firstRow + 2), 10 + i));
                }
            }
        }
        val currentHolidayRemainLeft = hdRemainingInfor.getCurrentHolidayRemainLeft();
        // Current month
        if (currentHolidayRemainLeft == null) {
            for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
                setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
                setBottomBorderStyle(cells.get(firstRow + totalRows - 1, 2 + index));
            }
            if (!isNumberRemainingPause) {
                setBackgroundGray(cells.get(firstRow, 7));
            }
            if (!isUndigestedPause) {
                setBackgroundGray(cells.get(firstRow, 8));
            }
            firstRow += totalRows;
            count += totalRows;
            dtoCheck.setCount(count);
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        // J1_2 振休_月初残
        cells.get(firstRow, 5).setValue(df.format(currentHolidayRemainLeft.getMonthStartRemain().doubleValue()));
        if (currentHolidayRemainLeft.getMonthStartRemain() < 0) {
            setForegroundRed(cells.get(firstRow, 5));
        }
        // J1_3 振休_使用数
        cells.get(firstRow, 6).setValue(df.format(currentHolidayRemainLeft.getMonthUse().doubleValue()));
        if (isNumberRemainingPause) {
            // J1_4 振休_残数
            cells.get(firstRow, 7).setValue(df.format(currentHolidayRemainLeft.getMonthEndRemain().doubleValue()));
            if (currentHolidayRemainLeft.getMonthEndRemain() < 0) {
                setForegroundRed(cells.get(firstRow, 7));
            }
        } else {
            setBackgroundGray(cells.get(firstRow, 7));
        }
        if (isUndigestedPause) {
            // J1_5 振休_未消化
            cells.get(firstRow, 8).setValue(df.format(currentHolidayRemainLeft.getMonthExtinction().doubleValue()));
            if (currentHolidayRemainLeft.getMonthExtinction() != null
                    && currentHolidayRemainLeft.getMonthExtinction() > 0) {
                setForegroundRed(cells.get(firstRow, 8));
            }
        } else {
            setBackgroundGray(cells.get(firstRow, 8));
        }

        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
            setBottomBorderStyle(cells.get(firstRow + totalRows - 1, 2 + index));
        }
        firstRow += totalRows;
        count += totalRows;
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    //K
    private DtoCheck print60hOversho(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                     HolidayRemainingDataSource dataSource, DtoCheck dtoCheck, HorizontalPageBreakCollection pageBreaks) throws Exception {

        // 振休
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        val holidayRemainingManagement = dataSource.getHolidaysRemainingManagement().getListItemsOutput();


        if (!holidayRemainingManagement.getOutOfTime().isOvertimeItem()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }

        if (!dataSource.getVariousVacationControl().isCom60HourVacationSetting()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        //※2
        boolean isUndigestedPause = holidayRemainingManagement.getOutOfTime().isOvertimeOverUndigested();
        //※3
        boolean isOvertimeRemaining = holidayRemainingManagement.getOutOfTime().isOvertimeRemaining();
        int totalRows = 2;
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            dtoCheck.setCountEmployeeBefore(0);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }


        cells.copyRows(cells, checkCopyRow(dataSource, employee) ? 69 : NUMBER_ROW_OF_HEADER + 20, firstRow, 2);
        count += 2;
        // K1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_79"));
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            firstRow += 2;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        // K2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_16"));
        // K2_2
        cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_17"));

        if (isUndigestedPause) {
            cells.copyRows(cells, !checkCopyRow(dataSource, employee) ? NUMBER_ROW_OF_HEADER + 22 : 71, firstRow + 2, 1);
            // K2_3
            cells.get(firstRow + 2, 9).setValue(TextResource.localize("KDR001_11"));
            totalRows += 1;
            count += 1;
        }
        val row = isUndigestedPause ? 3 : 2;
        if (isOvertimeRemaining) {

            cells.copyRows(cells, !checkCopyRow(dataSource, employee) ? NUMBER_ROW_OF_HEADER + 23 : (isUndigestedPause ? 72 : 71), firstRow + row, 1);
            // K2_4
            cells.get(firstRow + row, 9).setValue(TextResource.localize("KDR001_18"));
            totalRows += 1;
            count += 1;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            firstRow += totalRows;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        YearMonth currentMonth = employee.getCurrentMonth().get();
        //  Set background
        for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
                dataSource.getEndMonth().yearMonth()); i++) {
            if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
                setBackgroundGray(cells.get(firstRow, 10 + i));
                if (isUndigestedPause) {
                    setBackgroundGray(cells.get(firstRow + 2, 10 + i));
                }
                if (isOvertimeRemaining) {
                    setBackgroundGray(cells.get(firstRow + row, 10 + i));
                }
            }
        }
        if (!isUndigestedPause) {
            setBackgroundGray(cells.get(firstRow, 8));
        }
        if (!isOvertimeRemaining) {
            setBackgroundGray(cells.get(firstRow, 7));
        }
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setTopBorderStyle(cells.get(firstRow, 2 + index));
            setBottomBorderStyle(cells.get(firstRow + totalRows - 1, 2 + index));
        }

        firstRow += totalRows;
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    //F
    private DtoCheck printLimitForHalfHolidays(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                               HolidayRemainingDataSource dataSource, DtoCheck dtoCheck,
                                               HorizontalPageBreakCollection pageBreaks) throws Exception {
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        boolean yearlyHoliday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
        boolean insideHalfDay = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isInsideHalfDay();
        val showF = yearlyHoliday && insideHalfDay;
        if (!checkLimitHoliday(dataSource.getHolidaysRemainingManagement())
                || !employee.getCurrentMonth().isPresent()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        if (!showF) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }

        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        val closeDateOpt = hdRemainingInfor.getClosureInforOpt();
        val rs363New = hdRemainingInfor.getRs363New();
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 2) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }
        cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 7, firstRow, 2);
        //
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setTopBorderStyle(cells.get(firstRow, index + 2));
        }
        val dataOptional = annLeaMaxDataRepository.get(employee.getEmployeeId());
        YearMonth currentMonth = employee.getCurrentMonth().get();
        // F1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_49"));
        if (dataOptional.isPresent()) {
            val data = dataOptional.get();
            // F1_2 - OK
            Integer maxTimes = null;
            val numberOfGrantesOpt = data.getHalfdayAnnualLeaveMax();
            if (numberOfGrantesOpt.isPresent())
                maxTimes = numberOfGrantesOpt.get().getMaxTimes().v();
            String textMaxtime = "";
            if (maxTimes != null) {
                textMaxtime = maxTimes.toString() + TextResource.localize("KDR001_75");
            }
            cells.get(firstRow, 4).setValue(textMaxtime);
            // F1_3
            // 半日年休_月初残回数
            //　半日年休現在状況.月初残回数←半日年休上限．使用回数
            Integer remainingTimes = null;
            if (numberOfGrantesOpt.isPresent()) {
                String valueF13 = "";
                //残回数
                remainingTimes = numberOfGrantesOpt.get().getRemainingTimes().v();
                valueF13 = remainingTimes
                        + TextResource.localize("KDR001_75");
                cells.get(firstRow, 5)
                        .setValue(valueF13);
                if (remainingTimes < 0) {
                    setForegroundRed(cells.get(firstRow, 5));
                }
            }
            //F14: 半日年休_使用回数 = 月度使用回数+付与後月度使用回数
            String valueF14 = "";
            String valueF15 = "";
            if (!rs363New.isEmpty()) {
                // 月度使用回数 :年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．半日年休(マイナスあり)
                val number_of_use = rs363New.stream()
                        .map(e ->
                                {
                                    val i = e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                            .getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
                                    if (i.isPresent()) {
                                        return
                                                i.get().getUsedNum().getTimes().v();
                                    } else {
                                        return 0;
                                    }
                                }
                        ).mapToDouble(e -> e).sum();
                // 付与後月度使用回数:
                val uses_after_granting = rs363New.stream()
                        .map(e ->
                                {
                                    val i = e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                            .getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
                                    if (i.isPresent()) {
                                        val j = i.get().getUsedNum().getTimesAfterGrant();
                                        if (j.isPresent()) {
                                            return j.get().v();
                                        } else return 0;
                                    } else {
                                        return 0;
                                    }
                                }
                        ).mapToDouble(e -> e).sum();
                //valueF14 = number_of_use == 0 ? "" : (number_of_use + uses_after_granting + TextResource.localize("KDR001_75"));
                valueF14 = ((uses_after_granting == 0.0 ? "0" : uses_after_granting) + TextResource.localize("KDR001_75"));
                // F1_5: 半日年休_残回数 : 月度残回数: 年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．半日年休(マイナスあり)
                val number_of_date_remain = rs363New.stream()
                        .map(e ->
                                {
                                    val i = e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                                            .getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
                                    if (i.isPresent()) {
                                        return
                                                i.get().getRemainingNum().getTimesBeforeGrant().v();
                                    } else {
                                        return 0;
                                    }
                                }
                        ).mapToDouble(e -> e).sum();
                valueF15 = ((number_of_date_remain == 0.0 ? "0" : number_of_date_remain) + TextResource.localize("KDR001_75"));
                if (number_of_date_remain < 0) {
                    setForegroundRed(cells.get(firstRow, 7));
                }
                cells.get(firstRow, 6).setValue(valueF14);
                cells.get(firstRow, 7).setValue(valueF15);
            }
        }
        setBackgroundGray(cells.get(firstRow, 8));
        // F2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_14"));
        // F2_1
        cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_15"));
        // Result RequestList255
        val listAnnLeaveUsage = hdRemainingInfor.getListAnnualLeaveUsage();

        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        if (listAnnLeaveUsage != null && dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor().getGrantDate().isPresent()) {
            for (AnnualLeaveUsageImported item : listAnnLeaveUsage) {
                if (item.getYearMonth().compareTo(currentMonth) > 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                if (item.getYearMonth().compareTo(currentMonth) < 0) {
                    val number_use = item.getNumOfuses();
                    if (number_use != null) {
                        cells.get(firstRow, 10 + totalMonth)
                                .setValue(number_use + TextResource.localize("KDR001_75"));
                        if (number_use < 0) {
                            setForegroundRed(cells.get(firstRow, 10 + totalMonth));
                        }
                    }
                    val number_remain = item.getNumOfRemain();//
                    if (number_remain != null) {
                        cells.get(firstRow + 1, 10 + totalMonth)
                                .setValue(number_remain + TextResource.localize("KDR001_75"));
                        if (number_remain < 0) {
                            setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
                        }
                    }
                }
            }
        }
        // Result RequestList363
        for (AggrResultOfAnnualLeaveEachMonthKdr item : rs363New) {
            if (item.getYearMonth().compareTo(currentMonth) < 0) {
                continue;
            }
            int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
            if (maxRange < totalMonth || totalMonth < 0) {
                continue;
            }
            if (item.getYearMonth().compareTo(currentMonth) == 0) {
                if (!dataSource.isSameCurrentMonth()) {
                    setCurrentMonthBackground(cells.get(firstRow, 10 + totalMonth));
                    setCurrentMonthBackground(cells.get(firstRow + 1, 10 + totalMonth));
                }
                Integer number_of_useBf = null;
                //年月毎年休の集計結果．年休の集計結果．年休情報(期間終了日時点)．残数．年休(マイナスあり)．使用数．付与後
                Double date_of_useAf = null;
                val date_of_useAfOpt = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                        .getRemainingNumber().getAnnualLeaveWithMinus().getUsedNumberInfo()
                        .getUsedNumberAfterGrantOpt();

                //F2_3: 半日年休_使用回数 実績値 : 月度使用回数＋(255)半日年休利用状況.月度使用回数(※)
                // 月度使用回数 =
                val number_of_useOpt363 = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                        .getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
                if (number_of_useOpt363.isPresent()) {
                    number_of_useBf = number_of_useOpt363.get().getUsedNum().getTimesBeforeGrant().v();
                    if (listAnnLeaveUsage != null && !listAnnLeaveUsage.isEmpty()) {
                        val list255 = listAnnLeaveUsage.stream()
                                .filter(e -> e.getYearMonth().compareTo(currentMonth) == 0 && e.getNumOfuses() != null)
                                .collect(Collectors.toList()); //;
                        val number_of_useOpt255 = list255.stream()
                                .mapToInt(AnnualLeaveUsageImported::getNumOfuses).sum();
                        number_of_useBf += number_of_useOpt255;
                    }
                }
                if (date_of_useAfOpt.isPresent()) {
                    val optUseDate = date_of_useAfOpt.get().getUsedDays();
                    if (optUseDate.isPresent()) {
                        date_of_useAf = optUseDate.get().v();
                    }
                }
                if (closeDateOpt.isPresent()) {
                    val cls = closeDateOpt.get();
                    val ymd = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getYmd();
                    if (ymd.day() <= (cls.getClosureDate().getClosureDay().v() + 1)) {
                        if (number_of_useBf != null)
                            cells.get(firstRow, 10 + totalMonth)
                                    .setValue(number_of_useBf + TextResource.localize("KDR001_75"));
                    } else {
                        if (number_of_useBf != null && number_of_useBf != 0 && date_of_useAf != null && date_of_useAf != 0.0) {
                            cells.get(firstRow, 10 + totalMonth).setValue(number_of_useBf + "/" + date_of_useAf + TextResource.localize("KDR001_75"));
                        } else {
                            val e24bf = number_of_useBf != null && number_of_useBf != 0 ? number_of_useBf.toString() : "";
                            val e24af = date_of_useAf != null && date_of_useAf != 0 ? date_of_useAf.toString() : "";
                            cells.get(firstRow, 10 + totalMonth).setValue(e24bf + e24af);
                        }
                    }
                }
                //F2_4:半日年休_月度残回数 実績値:月度残回数
                val number_of_date_remainOpt = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                        .getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
                if (number_of_date_remainOpt.isPresent() && number_of_date_remainOpt.get().getRemainingNum() != null &&
                        number_of_date_remainOpt.get().getRemainingNum().getTimesBeforeGrant().v() != 0) {
                    val number_of_date_remain = number_of_date_remainOpt.get().getRemainingNum().getTimesBeforeGrant();
                    cells.get(firstRow + 1, 10 + totalMonth)
                            .setValue(number_of_date_remain + TextResource.localize("KDR001_75"));
                    if (number_of_date_remain.v() < 0) {
                        setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
                    }
                }
            }
            if (item.getYearMonth().compareTo(currentMonth) > 0) {
                Integer number_of_use = null;
                // F2_3 月度使用回数,
                val number_of_useOpt363 = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                        .getRemainingNumber().getHalfDayAnnualLeaveWithMinus();
                if (number_of_useOpt363.isPresent()
                        && (item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getHalfDayAnnualLeaveWithMinus().isPresent())
                        && (item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd().getRemainingNumber().getHalfDayAnnualLeaveWithMinus().get().getUsedNum() != null)
                        && (number_of_useOpt363.get().getUsedNum().getTimes().v() != 0)) {
                    number_of_use = number_of_useOpt363.get().getUsedNum().getTimes().v();
                    cells.get(firstRow, 10 + totalMonth)
                            .setValue(number_of_use + TextResource.localize("KDR001_75"));
                }
                //F2_4 BLANK
                setBackgroundGray(cells.get(firstRow + 1, 10 + totalMonth));
            }
        }
        firstRow += 2;
        count += 2;
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        dtoCheck.setFirstRow(firstRow);
        dtoCheck.setCount(count);
        return dtoCheck;
    }

    //G
    private DtoCheck printLimitUsageHolidays(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                             HolidayRemainingDataSource dataSource, DtoCheck dtoCheck,
                                             HorizontalPageBreakCollection pageBreaks) throws Exception {
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        boolean yearlyHoliday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
        boolean insideHours = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isInsideHours();
        if (!checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement())) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val showG = yearlyHoliday && insideHours;
        if (!showG) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        if (count >= MAX_ROW_IN_PAGE) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            for (int index = 0; index < NUMBER_COLUMN; index++) {
                setBottomBorderStyle(cells.get(firstRow + 5, index));
            }
            firstRow += 6;
            count = 6;
        }
        cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 7, firstRow, 1);
        setBackgroundGray(cells.get(firstRow, 8));
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setTopBorderStyle(cells.get(firstRow, 2 + index));
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            firstRow += 1;
            count += 1;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        val data363 = hdRemainingInfor.getRs363New();

        // G1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_50"));
        //G2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_15"));

        if (data363 == null || data363.isEmpty()) {
            firstRow += 1;
            count += 1;
            for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
                setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
            }
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        val dataOptionalMax = annLeaMaxDataRepository.get(employee.getEmployeeId());
        if (dataOptionalMax.isPresent()) {
            //G1_2
            val timeAnnualLeaveMax = dataOptionalMax.get().getTimeAnnualLeaveMax();
            if (timeAnnualLeaveMax.isPresent()) {
                val valueG12 = timeAnnualLeaveMax.get().getMaxMinutes().v();
                //G1_2
                val g12 = valueG12 != null ? convertToTime((int) (valueG12)) : "";
                cells.get(firstRow, 4).setValue(g12);
                val valueG13 = timeAnnualLeaveMax.get().getRemainingMinutes().valueAsMinutes();
                val g13 = convertToTime((int) (valueG13));
                cells.get(firstRow, 5).setValue(g13);
                if (valueG13 < 0) {
                    setForegroundRed(cells.get(firstRow, 5));
                }
                //G1_4
                val valueG14 = timeAnnualLeaveMax.get().getUsedMinutes().v();
                //G1_4
                cells.get(firstRow, 6).setValue(valueG14 == null ? "" : convertToTime((int) (valueG14)));
            }
        }
        val valueG15 = data363.stream()
                .map(e -> e.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                        .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                        .getRemainingNumberBeforeGrant().getTotalRemainingTime()).mapToDouble(e -> e.isPresent() ? e.get().v() : 0).sum();
        //G1_5 : 月度残時間
        val g15 = convertToTime((int) (valueG15));
        cells.get(firstRow, 7).setValue(g15);
        if (valueG15 < 0) {
            setForegroundRed(cells.get(firstRow, 7));
        }
        // Result RequestList255
        val listAnnLeaveUsage = hdRemainingInfor.getListAnnualLeaveUsage();
        if (!employee.getCurrentMonth().isPresent()) {
            firstRow += 1;
            count += 1;
            dtoCheck.setFirstRow(firstRow);
            dtoCheck.setCount(count);
            return dtoCheck;
        }
        // BEFORE
        YearMonth currentMonth = employee.getCurrentMonth().get();
        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        if (listAnnLeaveUsage != null && dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor().getGrantDate().isPresent()) {
            for (AnnualLeaveUsageImported item : listAnnLeaveUsage) {
                if (item.getYearMonth().compareTo(currentMonth) >= 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }
                val valueG22BeforeCurent = listAnnLeaveUsage.
                        stream().filter(e -> e.getYearMonth().compareTo(item.getYearMonth()) == 0)
                        .mapToDouble(e -> e.getRemainingTime() != null ? e.getRemainingTime() : 0).sum();
                // G2_2 月度残時間,

                val g22 = convertToTime((int) (valueG22BeforeCurent));
                cells.get(firstRow, 10 + totalMonth).setValue(g22);
                if (valueG22BeforeCurent < 0) {
                    setForegroundRed(cells.get(firstRow, 10 + totalMonth));
                }
            }
        }
        // Result RequestList363 // AFTER AND CURRENT
        if (dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor().getGrantDate().isPresent()) {
            for (AggrResultOfAnnualLeaveEachMonthKdr item : data363) {
                if (item.getYearMonth().compareTo(currentMonth) < 0) {
                    continue;
                }
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (maxRange < totalMonth || totalMonth < 0) {
                    continue;
                }

                if (item.getYearMonth().equals(currentMonth)) {
                    // G2_2: 月度残時間:
                    val monthlyRemainingTime = item.getAggrResultOfAnnualLeave().getAsOfPeriodEnd()
                            .getRemainingNumber().getAnnualLeaveWithMinus().getRemainingNumberInfo()
                            .getRemainingNumberBeforeGrant().getTotalRemainingTime();
                    //月度使用回数
                    val g22 = monthlyRemainingTime.map(annualLeaveRemainingTime -> convertToTime((int) (annualLeaveRemainingTime.v()))).orElse("");
                    cells.get(firstRow, 10 + totalMonth).setValue(g22);

                    if (monthlyRemainingTime.isPresent() && monthlyRemainingTime.get().v() < 0) {
                        setForegroundRed(cells.get(firstRow, 10 + totalMonth));
                    }

                } else if (item.getYearMonth().compareTo(currentMonth) > 0) {
                    // Update KDR 001 : 値＝(クリア)/背景色＝グレー;
                    setBackgroundGray(cells.get(firstRow, 10 + totalMonth));
                }

            }
        }
        for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
                dataSource.getEndMonth().yearMonth()); i++) {
            if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
                setBackgroundGray(cells.get(firstRow, 10 + i));
            }
            if (!dataSource.isSameCurrentMonth()
                    && dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
                setCurrentMonthBackground(cells.get(firstRow, 10 + i));
                setCurrentMonthBackground(cells.get(firstRow, 10 + i));
            }
        }
        firstRow += 1;
        count += 1;
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        dtoCheck.setFirstRow(firstRow);
        dtoCheck.setCount(count);
        return dtoCheck;

    }

    // M
    private DtoCheck printSpecialHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                         HolidayRemainingDataSource dataSource, DtoCheck dtoCheck, HorizontalPageBreakCollection pageBreaks) throws Exception {
        // 特別休暇
        Integer count = dtoCheck.getCount();
        NumberFormat df = new DecimalFormat("#0.0");
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val listSphdCode = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getSpecialHoliday();
        Collections.sort(listSphdCode);

        List<SpecialHoliday> specialHolidays = dataSource.getVariousVacationControl().getListSpecialHoliday();
        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());


        for (Integer specialHolidayCode : listSphdCode) {
            Optional<SpecialHoliday> specialHolidayOpt = specialHolidays.stream()
                    .filter(c -> c.getSpecialHolidayCode().v() == specialHolidayCode).findFirst();
            if (!specialHolidayOpt.isPresent()) {
                continue;
            }
            val listFrameNo = specialHolidayOpt.get().getTargetItem().getFrameNo();

            val itemFrame = specialHolidayFrameRepository.findHolidayFrameByListFrame(AppContexts.user().companyId(), listFrameNo)
                    .stream().filter(e -> e.getTimeMngAtr().value == NotUseAtr.USE.value).collect(Collectors.toList());
            if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
                Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
                printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
                for (int j = 0; j < NUMBER_COLUMN; j++) {
                    setBottomBorderStyle(cells.get(firstRow - 1, j));
                }
                int totalRow = count - 6 - countEmployeeBefore;
                dtoCheck.setCountEmployeeBefore(0);
                YearMonth currentMonth = employee.getCurrentMonth().get();
                if (!dataSource.isSameCurrentMonth()) {
                    for (int j = 0; j < totalRow; j++) {
                        int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                        setCurrentMonthBackground(cells.get(firstRow - totalRow + j, 10 + totalMonth));
                    }
                }
                pageBreaks.add(firstRow);
                cells.copyRows(cells, 0, firstRow, 6);
                cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                        + SPACE + employee.getWorkplaceName());
                for (int index = 0; index < NUMBER_COLUMN; index++) {
                    setBottomBorderStyle(cells.get(firstRow + 5, index));
                }
                firstRow += 6;
                count = 6;
            }
            val isTime = !itemFrame.isEmpty();
            if (isTime) {
                cells.copyRows(cells, checkCopyRowL(dataSource, employee) ? 81 : NUMBER_ROW_OF_HEADER + 32, firstRow, 4);
            } else {
                cells.copyRows(cells, checkCopyRowL(dataSource, employee) ? 99 : 97, firstRow, 2);

            }
            // M1_1 特別休暇
            cells.get(firstRow, 2).setValue(specialHolidayOpt.get().getSpecialHolidayName().v());
            // M2_1
            cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_17"));
            // M2_3
            cells.get(firstRow + (isTime ? 2 : 1), 9).setValue(TextResource.localize("KDR001_18"));

            if (employee.getCurrentMonth().isPresent()) {
                YearMonth currentMonth = employee.getCurrentMonth().get();

                // Result RequestList273
                //val specialVacationImported = hdRemainingInfor.getMapSpecialVacation().get(specialHolidayCode);
                val result273New = hdRemainingInfor.getMap273New().get(specialHolidayCode);
                // Result RequestList263
                // RS263 NEW
                val rs263 = hdRemainingInfor.getGetSpeHdOfConfMonVer2()
                        .stream().filter(e -> e.getSpecialHolidayCd() == specialHolidayCode).collect(Collectors.toList());
                // ドメインモデル「特別休暇付与残数データ」を取得
                List<SpecialLeaveGrantRemainingData> listSpecialLeaveGrant = specialLeaveGrantRepository
                        .getAllByExpStatus(employee.getEmployeeId(), specialHolidayCode,
                                LeaveExpirationStatus.AVAILABLE.value);

                // 全てのドメインモデル「特別休暇付与残数データ」の残時間を合計
                Double remainDate = null;
                Double remainTime = null;
                if (!listSpecialLeaveGrant.isEmpty()) {
                    remainDate = listSpecialLeaveGrant.stream()
                            .mapToDouble(item -> item.getDetails().getRemainingNumber().getDays() != null ?
                                    item.getDetails().getRemainingNumber().getDays().v() : 0).sum();
                    remainTime = listSpecialLeaveGrant.stream()
                            .mapToDouble(item -> item.getDetails().getRemainingNumber().getMinutes().isPresent() ?
                                    item.getDetails().getRemainingNumber().getMinutes().get().v() : 0).sum();
                }
                // 特別休暇_付与数日数
                val vlm12 = result273New.getGrantDays();
                val vlm16 = result273New.getGrantTime();
                cells.get(firstRow, 4)
                        .setValue(vlm12 != null ? df.format(vlm12) : "");
                if (isTime) {
                    // M 1_6
                    cells.get(firstRow + 1, 4).setValue(vlm16 != null ? convertToTime((int) vlm16) : "");
                    // M1_7 特別休暇_月初残時間
                    cells.get(firstRow + 1, 5).setValue(remainTime != null ? convertToTime((int) remainTime.doubleValue()) : "");
                    if (remainTime != null && remainTime < 0) {
                        setForegroundRed(cells.get(firstRow + 1, 5));
                    }
                    // M1_8 特別休暇_使用数時間
                    val vlm18 = result273New.getUsedHoursBf();
                    cells.get(firstRow + 1, 6).setValue(vlm18 != null ? convertToTime((int) vlm18.doubleValue()) : "");
                    // M1_9 特別休暇_残数時間
                    val vlm19 = result273New.getRemainHoursBf();
                    cells.get(firstRow + 1, 7).setValue(vlm19 != null ? convertToTime((int) vlm19) : "");
                    if (vlm19 != null && vlm19 < 0) {
                        setForegroundRed(cells.get(firstRow + 1, 7));
                    }
                }
                // M1_4 特別休暇_使用数日数
                val vlm14 = result273New.getUsedDateBf();
                cells.get(firstRow, 6).setValue(vlm14 != null ? df.format(vlm14) : "");
                // M1_3 特別休暇_月初残日数
                cells.get(firstRow, 5).setValue(remainDate != null ? df.format(remainDate) : "");
                if (remainDate != null && remainDate < 0) {
                    setForegroundRed(cells.get(firstRow, 5));
                }
                // M1_5 特別休暇_残数日数
                val vlm15 = result273New.getRemainDateBf();
                cells.get(firstRow, 7).setValue(vlm15 != null ? df.format(vlm15) : "");

                if (result273New.getRemainDate() != null && result273New.getRemainDate() < 0) {
                    setForegroundRed(cells.get(firstRow, 7));
                }
                if (rs263 != null) {
                    for (SpecialVacationPastSituation item : rs263) {
                        // Before this month
                        int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYm());
                        if (currentMonth.compareTo(item.getYm()) > 0) {
                            if (maxRange >= totalMonth && totalMonth >= 0) {
                                val useDays = item.getUseDays();
                                // M2_5 特別休暇１_使用日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                                cells.get(firstRow, 10 + totalMonth).setValue(useDays == 0 ? null : df.format(useDays));
                                if (useDays < 0) {
                                    setForegroundRed(cells.get(firstRow, 10 + totalMonth));
                                }
                                if (isTime) {
                                    //M2_6
                                    val useTime = item.getUseTimes();
                                    cells.get(firstRow + 1, 10 + totalMonth).setValue(useTime == 0 ? null :
                                            convertToTime(useTime));
                                    val remainTimes = item.getRemainTimes();
                                    //M2_8
                                    cells.get(firstRow + 3, 10 + totalMonth).setValue(convertToTime(remainTimes));
                                    if (useTime < 0) {
                                        setForegroundRed(cells.get(firstRow + 3, 10 + totalMonth));
                                    }
                                    if (remainTimes < 0) {
                                        setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
                                    }
                                }
                                val remainDays = item.getRemainDays();
                                // M2_7 特別休暇１_残数日数
                                cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth).setValue(df.format(remainDays));
                                if (remainDays < 0) {
                                    setForegroundRed(cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth));
                                }
                            }
                        }
                    }
                    List<YearMonth> lstYm = new ArrayList<>();
                    for (YearMonth i = currentMonth; i.lessThanOrEqualTo(dataSource.getEndMonth().yearMonth()); i = i.addMonths(1)) {
                        lstYm.add(i);
                    }
                    Collections.sort(lstYm);
                    for (YearMonth ym : lstYm) {
                        int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), ym);
                        SpecialVacationImportedKdr spVaCrurrentMonthImported = hdRemainingInfor.getLstMap273CurrMon().get(ym) != null ?
                                hdRemainingInfor.getLstMap273CurrMon().get(ym).get(specialHolidayCode) : null;
                        if (spVaCrurrentMonthImported != null) {
                            if (totalMonth <= maxRange && totalMonth >= 0) {
                                if (ym.compareTo(currentMonth) == 0) {
                                    val bfDate = spVaCrurrentMonthImported.getUsedDateBf();
                                    val afDate = spVaCrurrentMonthImported.getUsedDateAf();
                                    String vlm25 = "";
                                    if (bfDate != null && bfDate != 0 && afDate != null && afDate != 0) {
                                        vlm25 = String.valueOf(df.format(bfDate)) + "/" + String.valueOf(df.format(afDate));
                                    } else {
                                        vlm25 = ((bfDate == null || bfDate == 0) ? "" : String.valueOf(df.format(bfDate))) + ((afDate == null || afDate == 0) ? "" : String.valueOf(df.format(afDate)));
                                    }
                                    cells.get(firstRow, 10 + totalMonth).setValue(vlm25);
                                    if ((bfDate != null && bfDate < 0) || (afDate != null && afDate < 0)) {
                                        setForegroundRed(cells.get(firstRow, 10 + totalMonth));
                                    }
                                    //M2_6
                                    val bftime = spVaCrurrentMonthImported.getUsedHoursBf();
                                    val afdtime = spVaCrurrentMonthImported.getUsedHoursAf();
                                    String vlm26 = "";
                                    if (bftime != null && bftime != 0 && afdtime != null && afdtime != 0) {
                                        vlm26 = String.valueOf(convertToTime(bftime)) + "/" + String.valueOf(convertToTime(afdtime));
                                    } else {
                                        vlm26 = ((bftime == null || bftime == 0) ? "" : String.valueOf(convertToTime(bftime))) + ((afdtime == null || afdtime == 0) ? "" : String.valueOf(convertToTime(afdtime)));
                                    }
                                    if (isTime) {
                                        cells.get(firstRow + 1, 10 + totalMonth).setValue(vlm26);
                                        if ((bftime != null && bftime < 0) || (afdtime != null && afdtime < 0)) {
                                            setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
                                        }
                                    }
                                    // M2_7 特別休暇１_残数日数
                                    val bfdateRemain = spVaCrurrentMonthImported.getRemainDateBf();
                                    val afdateReman = spVaCrurrentMonthImported.getRemainDateAf();

                                    String m27 = "";
                                    if ((bfdateRemain != null) && (afdateReman != null)) {
                                        m27 = String.valueOf(df.format(bfdateRemain)) + "/" + String.valueOf(df.format(afdateReman));
                                    } else {
                                        if (afdateReman == null) {
                                            m27 = ((bfdateRemain == null) ? "" : String.valueOf(df.format(bfdateRemain)));
                                        }
                                        if (bfdateRemain == null) {
                                            m27 = ((afdateReman == null) ? "" : String.valueOf(df.format(afdateReman)));
                                        }
                                    }
                                    cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth).setValue(m27);
                                    if ((bfdateRemain != null && bfdateRemain < 0) || (afdateReman != null && afdateReman < 0)) {
                                        setForegroundRed(cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth));
                                    }
                                    //M2_8
                                    val bfRemainTime = spVaCrurrentMonthImported.getRemainHoursBf();
                                    val afRemanTime = spVaCrurrentMonthImported.getRemainHoursAf();
                                    String vlm28 = "";
                                    if (bfRemainTime != null && afRemanTime != null) {
                                        vlm28 = String.valueOf(convertToTime(bfRemainTime)) + "/" + String.valueOf(convertToTime(afRemanTime));
                                    } else {
                                        if (afRemanTime == null) {
                                            vlm28 = ((bfRemainTime == null) ? "" : String.valueOf(convertToTime(bfRemainTime)));
                                        }
                                        if (bfRemainTime == null) {
                                            vlm28 = ((afRemanTime == null) ? "" : String.valueOf(convertToTime(afRemanTime)));
                                        }
                                    }
                                    if (isTime) {
                                        cells.get(firstRow + 3, 10 + totalMonth).setValue(vlm28);
                                        if ((bfRemainTime != null && bfRemainTime < 0) || (afRemanTime != null && afRemanTime < 0)) {
                                            setForegroundRed(cells.get(firstRow + 2, 10 + totalMonth));
                                        }
                                    }
                                } else {
                                    if (currentMonth.compareTo(ym) < 0) {
                                        //M25
                                        val bfDate = spVaCrurrentMonthImported.getUsedDateBf();
                                        cells.get(firstRow, 10 + totalMonth).setValue((bfDate == null || bfDate == 0) ? "" : df.format(bfDate));
                                        if (bfDate != null && bfDate < 0) {
                                            setForegroundRed(cells.get(firstRow, 10 + totalMonth));
                                        }
                                        if (isTime) {
                                            val bftime = spVaCrurrentMonthImported.getUsedHoursBf();
                                            cells.get(firstRow + 1, 10 + totalMonth).setValue((bftime == null || bftime == 0) ? "" : convertToTime(bftime));
                                            // M2_6 特別休暇１_残数日数
                                            if (bftime != null && bftime < 0) {
                                                setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
                                            }
                                            setBackgroundGray(cells.get(firstRow + 3, 10 + totalMonth));
                                        }
                                        setBackgroundGray(cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth));

                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (isTime) {
                firstRow += 4;
                count += 4;
            } else {
                firstRow += 2;
                count += 2;
            }
        }
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    // N
    private DtoCheck printChildNursingVacation(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                               HolidayRemainingDataSource dataSource, DtoCheck dtoCheck, HorizontalPageBreakCollection pageBreaks) throws Exception {

        // 子の看護休暇
        // ※1
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        if (!dataSource.getVariousVacationControl().isChildNursingSetting()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        NumberFormat df = new DecimalFormat("#0.0");
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getChildNursingVacation()
                .isChildNursingLeave()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        //※2
        val listNursingLeaveSetting = nursingLeaveSettingRepository.findByCompanyId(AppContexts.user().companyId());
        val childNursing = listNursingLeaveSetting.stream()
                .filter(i -> i.getNursingCategory() == NursingCategory.ChildNursing && i.getTimeCareNursingSetting()
                        .getManageDistinct() == ManageDistinct.YES).findFirst();
        boolean isTime = false;
        if (childNursing.isPresent()) {
            isTime = childNursing.get().isManaged();
        }
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            firstRow += 6;
            count = 6;
        }
        if (isTime) {
            cells.copyRows(cells, !checkCopyRowL(dataSource, employee) ? NUMBER_ROW_OF_HEADER + 40 : 89, firstRow, 4);
        } else {
            cells.copyRows(cells, !checkCopyRowL(dataSource, employee) ? 103 : 101, firstRow, 2);

        }
        // N1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_47"));
        // N2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_9"));
        // N2_2
        cells.get(firstRow + (isTime ? 2 : 1), 9).setValue(TextResource.localize("KDR001_18"));
        YearMonth currentMonth = employee.getCurrentMonth().get();
        // Result RequestList206
        ChildNursingLeaveThisMonthFutureSituation currentSituationImportedLeft = hdRemainingInfor.getChildCareRemNumWithinPeriodLeft();
        // Result RequestList342
        val currentSituationImportedRight =
                hdRemainingInfor.getChildCareRemNumWithinPeriodRight()
                        .stream().collect(Collectors.toMap(ChildNursingLeaveThisMonthFutureSituation::getYm, e -> e));
        if (currentSituationImportedLeft != null) {
            // N1_2 子の看護休暇_使用数日数 == 付与前使用日数+付与後使用日数-- Update ver 29
            val numOfUsebf = currentSituationImportedLeft.getNumberOfDaysUsedBeforeGrant();
            val numOfUseAf = currentSituationImportedLeft.getDaysOfUseAfterGrant();
            Double vl1_2 = (numOfUsebf == null && numOfUseAf == null) ? null :
                    ((numOfUsebf == null ? 0 : numOfUsebf) + (numOfUseAf == null ? 0 : numOfUseAf));
            cells.get(firstRow, 6).setValue(vl1_2 == null ? "" : df.format(vl1_2));

            // N1_3 子の看護休暇_残数 == 付与前残日数(※1)---	(※1)翌年のデータがある(≠empty)場合、付与後残日数
            val remainingDaysBeforeGrant = currentSituationImportedLeft.getRemainingDaysBeforeGrant();
            val remainingDaysAfterGrant = currentSituationImportedLeft.getRemainingDaysAfterGrant();
            if (remainingDaysAfterGrant != null) {
                cells.get(firstRow, 7).setValue(df.format(remainingDaysAfterGrant));
                if (remainingDaysAfterGrant < 0) {
                    setForegroundRed(cells.get(firstRow, 7));
                }
            } else {
                cells.get(firstRow, 7).setValue(remainingDaysBeforeGrant == null ? "" : df.format(remainingDaysBeforeGrant));
                if (remainingDaysBeforeGrant != null && remainingDaysBeforeGrant < 0) {
                    setForegroundRed(cells.get(firstRow, 7));
                }
            }

            // N1_6 子の看護休暇_上限日数
            cells.get(firstRow, 8).setValue(TextResource.localize("KDR001_81"));
            if (isTime) {
                //N1_4子の看護休暇_使用数時間 ->付与前使用時間 : 付与前使用時間+付与後使用時間 : update ver 29
                val usageTimeBeforeGrant = currentSituationImportedLeft.getUsageTimeBeforeGrant();
                val usageTimeAfterGrant = currentSituationImportedLeft.getUsageTimeAfterGrant();
                Integer vl_14 = (usageTimeBeforeGrant == null && usageTimeAfterGrant == null) ? null :
                        ((usageTimeBeforeGrant == null ? 0 : usageTimeBeforeGrant) + (usageTimeAfterGrant == null ? 0 : usageTimeAfterGrant));
                cells.get(firstRow + 1, 6).setValue(vl_14 == null ? "" : convertToTime(vl_14));


                //N1_5子の看護休暇_上限日数	->付与前残時間(※2)(※2)翌年のデータがある(≠empty)場合、付与後残時間
                val remainingTimesBeforeGrant = currentSituationImportedLeft.getRemainingTimesBeforeGrant();
                val remainingTimesAfterGrant = currentSituationImportedLeft.getRemainingTimesAfterGrant();
                if (remainingTimesAfterGrant != null) {
                    cells.get(firstRow + 1, 7).setValue(convertToTime(remainingTimesAfterGrant));
                } else {
                    cells.get(firstRow + 1, 7).setValue(remainingTimesBeforeGrant == null ? "" : convertToTime(remainingTimesBeforeGrant));
                }
            }

            //N1_7子の看護休暇_上限日数	{0}/{1}
            // {0}付与前上限日数
            val maxNumberOfDaysBeforeGrant = currentSituationImportedLeft.getMaxNumberOfDaysBeforeGrant();
            // {1}付与後上限日数
            val maxNumberOfDaysAfterGrant = currentSituationImportedLeft.getMaxNumberOfDaysAfterGrant();
            if (maxNumberOfDaysAfterGrant == null) {
                cells.get(firstRow + 1, 8).setValue(maxNumberOfDaysBeforeGrant == null ? "" : df.format(maxNumberOfDaysBeforeGrant));
            } else {
                val vl17 = new StringBuilder();
                vl17.append(maxNumberOfDaysBeforeGrant == null ? "0.0" : df.format(maxNumberOfDaysBeforeGrant));
                vl17.append("/").append(df.format(maxNumberOfDaysAfterGrant));
                cells.get(firstRow + 1, 8).setValue(vl17);
            }
            if ((maxNumberOfDaysAfterGrant != null && maxNumberOfDaysAfterGrant > 0) || (maxNumberOfDaysBeforeGrant != null && maxNumberOfDaysBeforeGrant > 0)) {
                setForegroundRed(cells.get(firstRow + 1, 8));
            }

        }
        val result342 = hdRemainingInfor.getMonthlyConfirmedCareForEmployees();
        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        if (result342 != null) {
            for (ChildNursingLeaveStatus item : result342) {
                // Before this month
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (currentMonth.compareTo(item.getYearMonth()) > 0) {
                    if (maxRange >= totalMonth && totalMonth >= 0) {
                        // N2_3 子の看護休暇_使用数日数実績値	, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                        // 使用日数
                        Double daysOfUse = item.getDaysOfUse();
                        cells.get(firstRow, 10 + totalMonth)
                                .setValue(daysOfUse == null || daysOfUse == 0 ? null : df.format(daysOfUse));
                        if (isTime) {
                            // N2_6 子の看護休暇_残数時間実績値
                            //残時間
                            Integer timeRemaining = item.getTimeRemaining();
                            cells.get(firstRow + 3, 10 + totalMonth)
                                    .setValue(timeRemaining == null ? "" : convertToTime(timeRemaining));
                            if (timeRemaining != null && timeRemaining < 0) {
                                setForegroundRed(cells.get(firstRow + 3, 10 + totalMonth));
                            }
                            // N2_4 子の看護休暇_使用数時間実績値
                            //使用時間
                            Integer usageTime = item.getUsageTime();
                            cells.get(firstRow + 1, 10 + totalMonth)
                                    .setValue(usageTime == null || usageTime == 0 ? "" : convertToTime(usageTime));
                        }
                        // N2_5子の看護休暇_残数日数実績値
                        //残日数
                        Double remainingDays = item.getRemainingDays();
                        cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth)
                                .setValue(remainingDays == null ? null : df.format(remainingDays));
                        if (remainingDays != null && remainingDays < 0) {
                            setForegroundRed(cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth));
                        }
                    }
                }
            }
            List<YearMonth> lstYm = new ArrayList<>();
            for (YearMonth i = currentMonth; i.lessThanOrEqualTo(dataSource.getEndMonth().yearMonth()); i = i.addMonths(1)) {
                lstYm.add(i);
            }
            Collections.sort(lstYm);
            for (YearMonth ym : lstYm) {
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), ym);
                val thisMonthFutureSituation = currentSituationImportedRight.getOrDefault(ym, null);
                if (thisMonthFutureSituation != null) {
                    if (totalMonth <= maxRange && totalMonth >= 0) {
                        if (ym.compareTo(currentMonth) == 0) {

                            // N2_3: 子の看護休暇_使用数日数実績値
                            // {0}:付与前使用日数
                            Double numberOfDaysUsedBeforeGrant = thisMonthFutureSituation.getNumberOfDaysUsedBeforeGrant();
                            // {1}:付与後使用日数
                            Double daysOfUseAfterGrant = thisMonthFutureSituation.getDaysOfUseAfterGrant();

                            if (daysOfUseAfterGrant == null) {
                                cells.get(firstRow, 10 + totalMonth)
                                        .setValue(numberOfDaysUsedBeforeGrant == null || numberOfDaysUsedBeforeGrant == 0 ? null : df.format(numberOfDaysUsedBeforeGrant));
                            } else {
                                if ((numberOfDaysUsedBeforeGrant == null || numberOfDaysUsedBeforeGrant == 0) && (daysOfUseAfterGrant == 0)) {
                                    cells.get(firstRow + 1, 10 + totalMonth).setValue(null);
                                } else {
                                    val vl23 = new StringBuilder();
                                    vl23.append(numberOfDaysUsedBeforeGrant == null ? "0.0" : numberOfDaysUsedBeforeGrant.toString());
                                    vl23.append("/").append(daysOfUseAfterGrant.toString());
                                    cells.get(firstRow, 10 + totalMonth).setValue(vl23);
                                }
                            }
                            if (isTime) {
                                // N2_4 子の看護休暇_使用数時間実績値
                                //{0}:付与前使用時間
                                Integer usageTimeBeforeGrant = thisMonthFutureSituation.getUsageTimeBeforeGrant();
                                //{1}:付与後使用時間
                                Integer usageTimeAfterGrant = thisMonthFutureSituation.getUsageTimeAfterGrant();
                                if (usageTimeAfterGrant == null) {
                                    cells.get(firstRow + 1, 10 + totalMonth)
                                            .setValue(usageTimeBeforeGrant == null || usageTimeBeforeGrant == 0 ? null : convertToTime(usageTimeBeforeGrant));
                                } else {
                                    if ((usageTimeBeforeGrant == null || usageTimeBeforeGrant == 0) && (usageTimeAfterGrant == 0)) {
                                        cells.get(firstRow + 1, 10 + totalMonth).setValue(null);
                                    } else {
                                        val vl24 = new StringBuilder();
                                        vl24.append(usageTimeBeforeGrant == null ? "0:00" : convertToTime(usageTimeBeforeGrant));
                                        vl24.append("/").append(convertToTime(usageTimeAfterGrant));
                                        cells.get(firstRow + 1, 10 + totalMonth).setValue(vl24);
                                    }
                                }
                                //N2_6 子の看護休暇_残数時間実績値
                                //{0}:付与前残時間
                                Integer remainingTimesBeforeGrant = thisMonthFutureSituation.getRemainingTimesBeforeGrant();
                                //{1}:付与後残時間
                                Integer remainingTimesAfterGrant = thisMonthFutureSituation.getRemainingTimesAfterGrant();

                                if (remainingTimesAfterGrant == null) {
                                    cells.get(firstRow + 3, 10 + totalMonth)
                                            .setValue(remainingTimesBeforeGrant == null ? null : convertToTime(remainingTimesBeforeGrant));
                                } else {
                                    val vl26 = new StringBuilder();
                                    vl26.append(remainingTimesBeforeGrant == null ? "0:00" : convertToTime(remainingTimesBeforeGrant));
                                    vl26.append("/").append(convertToTime(remainingTimesAfterGrant));
                                    cells.get(firstRow + 3, 10 + totalMonth).setValue(vl26);
                                }
                                if (remainingTimesBeforeGrant != null && remainingTimesBeforeGrant < 0) {
                                    setForegroundRed(cells.get(firstRow + 3, 10 + totalMonth));
                                }
                            }
                            //N_25子の看護休暇_残数日数実績値
                            //{0}:付与前残日数
                            Double remainingDaysBeforeGrant = thisMonthFutureSituation.getRemainingDaysBeforeGrant();
                            //{1}:付与後残日数
                            Double remainingDaysAfterGrant = thisMonthFutureSituation.getRemainingDaysAfterGrant();

                            if (remainingDaysAfterGrant == null) {
                                cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth)
                                        .setValue(remainingDaysBeforeGrant == null ? null : df.format(remainingDaysBeforeGrant));
                            } else {
                                val vl25 = new StringBuilder();
                                vl25.append(remainingDaysBeforeGrant == null ? "0.0" : df.format(remainingDaysBeforeGrant));
                                vl25.append("/").append(df.format(remainingDaysAfterGrant));
                                cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth).setValue(vl25);
                            }
                            if (remainingDaysBeforeGrant != null && remainingDaysBeforeGrant < 0) {
                                setForegroundRed(cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth));
                            }

                        } else {
                            if (currentMonth.compareTo(ym) < 0) {
                                //N23_子の看護休暇_使用数日数実績値
                                //付与前使用日数 -> 付与前使用日数+付与後使用日数
                                Double numberOfDaysUsedBeforeGrant = thisMonthFutureSituation.getNumberOfDaysUsedBeforeGrant();
                                Double daysOfUseAfterGrant = thisMonthFutureSituation.getDaysOfUseAfterGrant();
                                Double vl_23 = (numberOfDaysUsedBeforeGrant == null && daysOfUseAfterGrant == null) ? null :
                                        ((numberOfDaysUsedBeforeGrant == null ? 0 : numberOfDaysUsedBeforeGrant) + (daysOfUseAfterGrant == null ? 0 : daysOfUseAfterGrant));
                                cells.get(firstRow, 10 + totalMonth)
                                        .setValue(vl_23 == null || vl_23 == 0 ? null : df.format(vl_23));
                                if (isTime) {
                                    // N2_4 子の看護休暇_使用数時間実績値
                                    //付与前使用時間->付与前使用時間+付与後使用時間
                                    Integer usageTimeBeforeGrant = thisMonthFutureSituation.getUsageTimeBeforeGrant();
                                    Integer usageTimeAfterGrant = thisMonthFutureSituation.getUsageTimeAfterGrant();
                                    Integer vl_24 = (usageTimeBeforeGrant == null && usageTimeAfterGrant == null) ? null :
                                            ((usageTimeBeforeGrant == null ? 0 : usageTimeBeforeGrant) + (usageTimeAfterGrant == null ? 0 : usageTimeAfterGrant));
                                    cells.get(firstRow + 1, 10 + totalMonth)
                                            .setValue(vl_24 == null || vl_24 == 0 ? null : convertToTime(vl_24));
                                    // N2_5 特別休暇１_残数日数
                                    setBackgroundGray(cells.get(firstRow + 2, 10 + totalMonth));
                                    setBackgroundGray(cells.get(firstRow + 3, 10 + totalMonth));
                                } else {
                                    setBackgroundGray(cells.get(firstRow + 1, 10 + totalMonth));
                                }

                            }
                        }
                    }
                }
            }
        }

        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        firstRow += (isTime ? 4 : 2);
        count += (isTime ? 4 : 2);
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    // O
    private DtoCheck printNursingCareLeave(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                           HolidayRemainingDataSource dataSource, DtoCheck dtoCheck, HorizontalPageBreakCollection pageBreaks) throws Exception {
        // 介護休暇
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        if (!dataSource.getVariousVacationControl().isNursingCareSetting()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getNursingcareLeave().isNursingLeave()) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        NumberFormat df = new DecimalFormat("#0.0");
        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            firstRow += 6;
            count = 6;
        }
        // 介護
        val listNursingLeaveSetting = nursingLeaveSettingRepository.findByCompanyId(AppContexts.user().companyId());

        val nursingCare = listNursingLeaveSetting.stream()
                .filter(i -> i.getNursingCategory() == NursingCategory.Nursing
                        && i.getTimeCareNursingSetting()
                        .getManageDistinct() == ManageDistinct.YES).findFirst();
        boolean isTime = false;
        if (nursingCare.isPresent()) {
            isTime = nursingCare.get().isManaged();
        }
        if (isTime) {
            cells.copyRows(cells, !checkCopyRowL(dataSource, employee) ? NUMBER_ROW_OF_HEADER + 40 : 89, firstRow, 4);
        } else {
            cells.copyRows(cells, !checkCopyRowL(dataSource, employee) ? 103 : 101, firstRow, 2);
        }
        // Result RequestList207
        NursingCareLeaveThisMonthFutureSituation currentSituationImportedLeft = hdRemainingInfor
                .getNursingCareLeaveThisMonthFutureSituationLeft();

        // O1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_48"));
        // O2_1
        cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_9"));
        Style style1 = cells.get(firstRow, 9).getStyle();
        style1.setHorizontalAlignment(TextAlignmentType.LEFT);
        cells.get(firstRow, 9).setStyle(style1);
        // O2_2
        cells.get(firstRow + (isTime ? 2 : 1), 9).setValue(TextResource.localize("KDR001_18"));
        Style style2 = cells.get(firstRow + (isTime ? 2 : 1), 9).getStyle();
        style1.setHorizontalAlignment(TextAlignmentType.LEFT);
        cells.get(firstRow + (isTime ? 2 : 1), 9).setStyle(style2);

        if (currentSituationImportedLeft != null) {
            // O1_2 子の看護休暇_使用数日数->付与前使用日数+付与後使用日数
            val numOfUsebf = currentSituationImportedLeft.getNumberOfDaysUsedBeforeGrant();
            val numOfUseAf = currentSituationImportedLeft.getDaysOfUseAfterGrant();
            Double vl1_2 = (numOfUsebf == null && numOfUseAf == null) ? null :
                    ((numOfUsebf == null ? 0 : numOfUsebf) + (numOfUseAf == null ? 0 : numOfUseAf));
            cells.get(firstRow, 6).setValue(vl1_2 == null ? "" : df.format(vl1_2));
            cells.get(firstRow, 6).setValue(vl1_2 == null ? "" : df.format(vl1_2));


            // O1_3 子の看護休暇_残数->付与前残日数(※1)	(※1)翌年のデータがある(≠empty)場合、付与後残日数
            val remainingDaysAfterGrant = currentSituationImportedLeft.getRemainingDaysAfterGrant();
            val remainingDaysBeforeGrant = currentSituationImportedLeft.getRemainingDaysBeforeGrant();
            if (remainingDaysAfterGrant == null) {
                cells.get(firstRow, 7).setValue(remainingDaysBeforeGrant == null ? "" : df.format(remainingDaysBeforeGrant));
                if (remainingDaysBeforeGrant != null && remainingDaysBeforeGrant < 0) {
                    setForegroundRed(cells.get(firstRow, 7));
                }
            } else {
                cells.get(firstRow, 7).setValue(df.format(remainingDaysAfterGrant));
                if (remainingDaysAfterGrant < 0) {
                    setForegroundRed(cells.get(firstRow, 7));
                }
            }
            // O1_6 子の看護休暇_上限日数
            cells.get(firstRow, 8).setValue(TextResource.localize("KDR001_82"));
            if (isTime) {
                //O1_4子の看護休暇_使用数時間 ->付与前使用時間->付与前使用時間+付与後使用時間
                val usageTimeAfterGrant = currentSituationImportedLeft.getUsageTimeAfterGrant();
                val usageTimeBeforeGrant = currentSituationImportedLeft.getUsageTimeBeforeGrant();
                Integer vl_14 = (usageTimeBeforeGrant == null && usageTimeAfterGrant == null) ? null :
                        ((usageTimeBeforeGrant == null ? 0 : usageTimeBeforeGrant) + (usageTimeAfterGrant == null ? 0 : usageTimeAfterGrant));
                cells.get(firstRow + 1, 6).setValue(vl_14 == null ? "" : convertToTime(vl_14));

                //O1_5子の看護休暇_上限日数	->付与前残時間->付与前残時間(※2)	(※2)翌年のデータがある(≠empty)場合、付与後残時間
                val remainingTimesBeforeGrant = currentSituationImportedLeft.getRemainingTimesBeforeGrant();
                val remainingTimesAfterGrant = currentSituationImportedLeft.getRemainingTimesAfterGrant();
                if (remainingTimesAfterGrant != null) {
                    cells.get(firstRow + 1, 7).setValue(convertToTime(remainingTimesAfterGrant));
                } else {
                    cells.get(firstRow + 1, 7).setValue(remainingTimesBeforeGrant == null ? "" : convertToTime(remainingTimesBeforeGrant));
                }

            }
            //O1_7子の看護休暇_上限日数	{0}/{1}
            // {0}付与前上限日数
            val maxNumberOfDaysBeforeGrant = currentSituationImportedLeft.getMaxNumberOfDaysBeforeGrant();
            // {1}付与後上限日数
            val maxNumberOfDaysAfterGrant = currentSituationImportedLeft.getMaxNumberOfDaysAfterGrant();
            if (maxNumberOfDaysAfterGrant != null) {
                val vl17 = new StringBuilder();
                vl17.append(maxNumberOfDaysBeforeGrant == null ? "0.0" : df.format(maxNumberOfDaysBeforeGrant));
                vl17.append("/").append(df.format(maxNumberOfDaysAfterGrant));
                cells.get(firstRow + 1, 8).setValue(vl17);
            } else {
                cells.get(firstRow + 1, 8).setValue(maxNumberOfDaysBeforeGrant == null ? "0.0" : df.format(maxNumberOfDaysBeforeGrant));

            }
            if ((maxNumberOfDaysAfterGrant != null && maxNumberOfDaysAfterGrant > 0) || (maxNumberOfDaysBeforeGrant != null && maxNumberOfDaysBeforeGrant > 0)) {
                setForegroundRed(cells.get(firstRow + 1, 8));
            }
        }
        YearMonth currentMonth = employee.getCurrentMonth().get();
        val currentSituationImportedRight =
                hdRemainingInfor.getNursingCareLeaveThisMonthFutureSituationRight()
                        .stream().collect(Collectors.toMap(NursingCareLeaveThisMonthFutureSituation::getYm, e -> e));
        val result344 = hdRemainingInfor.getObtainMonthlyConfirmedCareForEmployees();
        int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
        if (result344 != null) {
            for (NursingCareLeaveMonthlyRemaining item : result344) {
                // Before this month
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
                if (currentMonth.compareTo(item.getYearMonth()) > 0) {
                    if (maxRange >= totalMonth && totalMonth >= 0) {
                        // O2_3 子の看護休暇_使用数日数実績値	, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
                        // 使用日数
                        Double daysOfUse = item.getDaysOfUse();
                        cells.get(firstRow, 10 + totalMonth)
                                .setValue(daysOfUse == null || daysOfUse == 0 ? null : df.format(daysOfUse));
                        if (isTime) {
                            // O2_4 子の看護休暇_使用数時間実績値
                            //使用時間
                            Integer usageTime = item.getUsageTime();
                            cells.get(firstRow + 1, 10 + totalMonth)
                                    .setValue(usageTime == null || usageTime == 0 ? "" : convertToTime(usageTime));
                            // O2_6 子の看護休暇_残数時間実績値
                            //残時間
                            Integer timeRemaining = item.getTimeRemaining();
                            cells.get(firstRow + 3, 10 + totalMonth)
                                    .setValue(timeRemaining == null ? "" : convertToTime(timeRemaining));
                            if (timeRemaining != null && timeRemaining < 0) {
                                setForegroundRed(cells.get(firstRow + 3, 10 + totalMonth));
                            }
                        }
                        // O2_5子の看護休暇_残数日数実績値
                        //残日数
                        Double remainingDays = item.getRemainingDays();
                        cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth)
                                .setValue(remainingDays == null ? null : df.format(remainingDays));
                        if (remainingDays != null && remainingDays < 0) {
                            setForegroundRed(cells.get(firstRow + 2, 10 + totalMonth));
                        }

                    }
                }
            }
            List<YearMonth> lstYm = new ArrayList<>();
            for (YearMonth i = currentMonth; i.lessThanOrEqualTo(dataSource.getEndMonth().yearMonth()); i = i.addMonths(1)) {
                lstYm.add(i);
            }
            Collections.sort(lstYm);
            for (YearMonth ym : lstYm) {
                int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), ym);
                val thisMonthFutureSituation = currentSituationImportedRight.getOrDefault(ym, null);
                if (thisMonthFutureSituation != null) {
                    if (totalMonth <= maxRange && totalMonth >= 0) {
                        if (ym.compareTo(currentMonth) == 0) {
                            // O2_3: 子の看護休暇_使用数日数実績値
                            // {0}:付与前使用日数
                            Double numberOfDaysUsedBeforeGrant = thisMonthFutureSituation.getNumberOfDaysUsedBeforeGrant();
                            // {1}:付与後使用日数
                            Double daysOfUseAfterGrant = thisMonthFutureSituation.getDaysOfUseAfterGrant();

                            if (daysOfUseAfterGrant != null) {

                                if (daysOfUseAfterGrant == 0 && (numberOfDaysUsedBeforeGrant == null || numberOfDaysUsedBeforeGrant == 0)) {
                                    cells.get(firstRow, 10 + totalMonth).setValue(null);
                                } else {
                                    val vl23 = new StringBuilder();
                                    vl23.append(numberOfDaysUsedBeforeGrant == null ? "0.0" : df.format(numberOfDaysUsedBeforeGrant));
                                    vl23.append("/").append(daysOfUseAfterGrant.toString());
                                    cells.get(firstRow, 10 + totalMonth).setValue(vl23);
                                }
                            } else {
                                cells.get(firstRow, 10 + totalMonth)
                                        .setValue(numberOfDaysUsedBeforeGrant == null || numberOfDaysUsedBeforeGrant == 0 ? null : df.format(numberOfDaysUsedBeforeGrant));
                            }
                            if (isTime) {
                                // O2_4 子の看護休暇_使用数時間実績値
                                //{0}:付与前使用時間
                                Integer usageTimeBeforeGrant = thisMonthFutureSituation.getUsageTimeBeforeGrant();
                                //{1}:付与後使用時間
                                Integer usageTimeAfterGrant = thisMonthFutureSituation.getUsageTimeAfterGrant();
                                if (usageTimeAfterGrant != null) {
                                    if (usageTimeAfterGrant == 0 && (usageTimeBeforeGrant == null || usageTimeBeforeGrant == 0)) {
                                        cells.get(firstRow, 10 + totalMonth).setValue(null);
                                    } else {
                                        val vl24 = new StringBuilder();
                                        vl24.append(usageTimeBeforeGrant == null ? "0:00" : convertToTime(usageTimeBeforeGrant));
                                        vl24.append("/").append(convertToTime(usageTimeAfterGrant));
                                        cells.get(firstRow + 1, 10 + totalMonth).setValue(vl24);
                                    }

                                } else {
                                    cells.get(firstRow + 1, 10 + totalMonth)
                                            .setValue(usageTimeBeforeGrant == null || usageTimeBeforeGrant == 0 ? null : convertToTime(usageTimeBeforeGrant));
                                }
                                //O2_6 子の看護休暇_残数時間実績値
                                //{0}:付与前残時間
                                Integer remainingTimesBeforeGrant = thisMonthFutureSituation.getRemainingTimesBeforeGrant();
                                //{1}:付与後残時間
                                Integer remainingTimesAfterGrant = thisMonthFutureSituation.getRemainingTimesAfterGrant();

                                if (remainingTimesAfterGrant != null) {
                                    val vl26 = new StringBuilder();
                                    vl26.append(remainingTimesBeforeGrant == null ? "0:00" : convertToTime(remainingTimesBeforeGrant));
                                    vl26.append("/").append(convertToTime(remainingTimesAfterGrant));
                                    cells.get(firstRow + 3, 10 + totalMonth).setValue(vl26);
                                } else {
                                    cells.get(firstRow + 3, 10 + totalMonth)
                                            .setValue(remainingTimesBeforeGrant == null ? null : convertToTime(remainingTimesBeforeGrant));
                                }
                                if (remainingTimesBeforeGrant != null && remainingTimesBeforeGrant < 0) {
                                    setForegroundRed(cells.get(firstRow + 3, 10 + totalMonth));
                                }
                            }

                            //O_25子の看護休暇_残数日数実績値
                            //{0}:付与前残日数
                            Double remainingDaysBeforeGrant = thisMonthFutureSituation.getRemainingDaysBeforeGrant();
                            //{1}:付与後残日数
                            Double remainingDaysAfterGrant = thisMonthFutureSituation.getRemainingDaysAfterGrant();

                            if (remainingDaysAfterGrant != null) {
                                val vl25 = new StringBuilder();
                                vl25.append(remainingDaysBeforeGrant == null ? "0.0" : df.format(remainingDaysBeforeGrant));
                                vl25.append("/").append(df.format(remainingDaysAfterGrant));
                                cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth).setValue(vl25);

                            } else {
                                cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth)
                                        .setValue(remainingDaysBeforeGrant == null ? null : df.format(remainingDaysBeforeGrant));
                            }
                            if (remainingDaysBeforeGrant != null && remainingDaysBeforeGrant < 0) {
                                setForegroundRed(cells.get(firstRow + (isTime ? 2 : 1), 10 + totalMonth));
                            }

                        } else {
                            if (currentMonth.compareTo(ym) < 0) {
                                //O23_子の看護休暇_使用数日数実績値
                                //付与前使用日数 = 付与前使用日数+付与後使用日数
                                Double numberOfDaysUsedBeforeGrant = thisMonthFutureSituation.getNumberOfDaysUsedBeforeGrant();
                                Double daysOfUseAfterGrant = thisMonthFutureSituation.getDaysOfUseAfterGrant();
                                Double vl_23 = (numberOfDaysUsedBeforeGrant == null && daysOfUseAfterGrant == null) ? null :
                                        ((numberOfDaysUsedBeforeGrant == null ? 0 : numberOfDaysUsedBeforeGrant) + (daysOfUseAfterGrant == null ? 0 : daysOfUseAfterGrant));
                                cells.get(firstRow, 10 + totalMonth)
                                        .setValue(vl_23 == null || vl_23 == 0 ? null : df.format(vl_23));
                                if (isTime) {
                                    // 02_4 子の看護休暇_使用数時間実績値
                                    //付与前使用時間: 付与前使用時間+付与後使用時間
                                    Integer usageTimeBeforeGrant = thisMonthFutureSituation.getUsageTimeBeforeGrant();
                                    Integer usageTimeAfterGrant = thisMonthFutureSituation.getUsageTimeAfterGrant();
                                    Integer vl_24 = (usageTimeBeforeGrant == null && usageTimeAfterGrant == null) ? null :
                                            ((usageTimeBeforeGrant == null ? 0 : usageTimeBeforeGrant) + (usageTimeAfterGrant == null ? 0 : usageTimeAfterGrant));
                                    cells.get(firstRow + 1, 10 + totalMonth)
                                            .setValue(vl_24 == null || vl_24 == 0 ? null : convertToTime(vl_24));
                                    // 02_5 特別休暇１_残数日数
                                    setBackgroundGray(cells.get(firstRow + 3, 10 + totalMonth));
                                    setBackgroundGray(cells.get(firstRow + 2, 10 + totalMonth));
                                } else {
                                    setBackgroundGray(cells.get(firstRow + 1, 10 + totalMonth));
                                }

                            }
                        }
                    }
                }
            }
        }
        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setBottomBorderStyle(cells.get(firstRow - 1, 2 + index));
        }
        firstRow += (isTime ? 4 : 2);
        count += (isTime ? 4 : 2);
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    // L1 - CREATE IN VER 15
    private DtoCheck publicHolidays(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
                                    HolidayRemainingDataSource dataSource, DtoCheck dtoCheck, HorizontalPageBreakCollection pageBreaks) throws Exception {
        // 代休
        Integer count = dtoCheck.getCount();
        Integer first = firstRow;
        val listItemsOutput = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        val manageHoliday = dataSource.getVariousVacationControl().isPublicHolidaySetting();
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        //※1: 出力項目設定[公休]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休の項目を出力する
        //     公休管理設定：公休設定．管理区分
        val outputItemsHolidays = listItemsOutput.getHolidays().isOutputItemsHolidays();

        if (!outputItemsHolidays) {
            dtoCheck.setFirstRow(firstRow);
            return dtoCheck;
        }
        // ※2 : 出力項目設定[公休繰越数]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休繰越数を出力する
        val outputHolidayForward = listItemsOutput.getHolidays().isOutputHolidayForward();

        // ※3 : 出力項目設定[公休月度残]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休月度残を出力する
        val monthlyPublic = listItemsOutput.getHolidays().isMonthlyPublic();
        int countL = 2;
        if (outputHolidayForward) {
            countL += 1;
        }
        if (monthlyPublic) {
            countL += 1;
        }

        if (count >= MAX_ROW_IN_PAGE || MAX_ROW_IN_PAGE - count < 4) {
            Integer countEmployeeBefore = dtoCheck.getCountEmployeeBefore();
            printEmployeeInfore(cells, firstRow - (count - 6 - countEmployeeBefore), dataSource, employee);
            for (int j = 0; j < NUMBER_COLUMN; j++) {
                setBottomBorderStyle(cells.get(firstRow - 1, j));
            }
            int totalRow = count - 6 - countEmployeeBefore;
            dtoCheck.setCountEmployeeBefore(0);
            YearMonth currentMonth = employee.getCurrentMonth().get();
            if (!dataSource.isSameCurrentMonth()) {
                for (int j = 0; j < totalRow; j++) {
                    int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
                    setCurrentMonthBackground(cells.get(first - totalRow + j, 10 + totalMonth));
                }
            }
            pageBreaks.add(firstRow);
            cells.copyRows(cells, 0, firstRow, 6);
            cells.get(firstRow + 5, 0).setValue(TextResource.localize("KDR001_12") + employee.getWorkplaceCode()
                    + SPACE + employee.getWorkplaceName());
            firstRow += 6;
            count = 6;
        }
        cells.copyRows(cells, !checkCopyRowK(dataSource, employee) ? NUMBER_ROW_OF_HEADER + 24 : 73, firstRow, countL);
        // L1_1
        cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_21"));
        //2
        val isOutputHolidayForward = listItemsOutput.getHolidays().isOutputHolidayForward();
        //3
        val isMonthlyPublic = listItemsOutput.getHolidays().isMonthlyPublic();


        if (isOutputHolidayForward) {
            // L2_1
            cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_23"));
        }
        // L2_2
        cells.get(firstRow + (isOutputHolidayForward ? 1 : 0), 9).setValue(TextResource.localize("KDR001_22"));
        // L2_3
        cells.get(firstRow + (isOutputHolidayForward ? 2 : 1), 9).setValue(TextResource.localize("KDR001_17"));

        if (isMonthlyPublic)
            // L2_4
            cells.get(firstRow + (isOutputHolidayForward ? 3 : 2), 9).setValue(TextResource.localize("KDR001_80"));

        if (employee.getCurrentMonth().isPresent()) {
            YearMonth currentMonth = employee.getCurrentMonth().get();
            //  Set background
            for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
                    dataSource.getEndMonth().yearMonth()); i++) {
                if (manageHoliday) {
                    if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {

                        if (countL == 2) {
                            setBackgroundGray(cells.get(firstRow, 10 + i));
                        }
                        if (countL == 3) {
                            setBackgroundGray(cells.get(firstRow, 10 + i));
                            setBackgroundGray(cells.get(firstRow + (monthlyPublic ? 2 : 1), 10 + i));
                        }
                        if (countL == 4) {
                            setBackgroundGray(cells.get(firstRow, 10 + i));
                            setBackgroundGray(cells.get(firstRow + 1, 10 + i));
                            setBackgroundGray(cells.get(firstRow + 3, 10 + i));
                        }
                    }
                } else {
                    if (countL == 2) {
                        setBackgroundGray(cells.get(firstRow, 10 + i));
                    }
                    if (countL == 3) {
                        setBackgroundGray(cells.get(firstRow, 10 + i));
                        setBackgroundGray(cells.get(firstRow + (monthlyPublic ? 2 : 1), 10 + i));
                    }
                    if (countL == 4) {
                        setBackgroundGray(cells.get(firstRow, 10 + i));
                        setBackgroundGray(cells.get(firstRow + 1, 10 + i));
                        setBackgroundGray(cells.get(firstRow + 3, 10 + i));
                    }
                }
            }
        }
        if (!outputHolidayForward) {

            setBackgroundGray(cells.get(firstRow, 5));
        }
        if (!monthlyPublic) {
            setBackgroundGray(cells.get(firstRow, 7));
        }
        if (!manageHoliday) {
            setBackgroundGray(cells.get(firstRow, 4));
            setBackgroundGray(cells.get(firstRow, 5));
            setBackgroundGray(cells.get(firstRow, 7));
        }

        for (int index = 0; index < NUMBER_COLUMN - 2; index++) {
            setTopBorderStyle(cells.get(firstRow, 2 + index));
            setBottomBorderStyle(cells.get(firstRow + countL - 1, 2 + index));
        }
        firstRow += countL;
        count += countL;
        dtoCheck.setCount(count);
        dtoCheck.setFirstRow(firstRow);
        return dtoCheck;
    }

    private void removeTemplate(Worksheet worksheet) {
        removeFirstShapes(worksheet);
        Cells cells = worksheet.getCells();
        cells.deleteRows(0, MAX_ROW_IN_PAGE_TEMPLATE + 40);
    }

    private void removeFirstShapes(Worksheet worksheet) {
        if (worksheet.getShapes().getCount() > 0) {
            worksheet.getShapes().removeAt(0);
        }
    }

    private void setTopBorderStyle(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
        cell.setStyle(style);
    }

    private void setBottomBorderStyle(Cell cell) {
        Style style = cell.getStyle();
        style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
        cell.setStyle(style);
    }

    private void setBackgroundGray(Cell cell) {
        Style style = cell.getStyle();
        style.setForegroundColor(Color.getGray());
        cell.setStyle(style);
    }

    private void setCurrentMonthBackground(Cell cell) {
        Style style = cell.getStyle();
        style.setForegroundColor(Color.fromArgb(147, 193, 242));
        cell.setStyle(style);
    }

    private void setForegroundRed(Cell cell) {
        Style style = cell.getStyle();
        style.getFont().setColor(Color.getRed());
        cell.setStyle(style);
    }

    private int totalMonths(YearMonth start, YearMonth end) {
        return (end.year() - start.year()) * TOTAL_MONTH_IN_YEAR + (end.month() - start.month());
    }

    /**
     * PRINT PAGE HEADER
     */
    private void printHeader(Worksheet worksheet, HolidayRemainingDataSource dataSource) {
        // Set print page
        PageSetup pageSetup = worksheet.getPageSetup();
        val title = dataSource.getHolidaysRemainingManagement().getName();
        pageSetup.setFirstPageNumber(1);
        //pageSetup.setPrintArea("A1:N");
        //ý 1 của bug #102883  事象(1)
        pageSetup.setHeader(0, "&9&\"ＭＳ ゴシック\"" + dataSource.getCompanyName());
        pageSetup.setHeader(1, "&16&\"ＭＳ ゴシック,Bold\"" + title);
        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN);
        pageSetup.setHeader(2, "&9&\"ＭＳ ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage &P");

    }

    //E Case 1:
    private boolean checkShowAreaAnnualBreak1(HolidaysRemainingManagement holidaysRemainingManagement) {
        String cid = AppContexts.user().companyId();
        val checkLeave = annualPaidLeaveSettingRepository.findByCompanyId(cid);
        if (!holidaysRemainingManagement.getListItemsOutput().getAnnualHoliday().isYearlyHoliday()) {
            return false;
        } else {
            if ((checkLeave == null || checkLeave.getYearManageType() == ManageDistinct.NO)) {
                return false;
            } else {
                return true;
            }
        }
    }

    //E Case 2:
    private boolean checkShowAreaAnnualBreak2(HolidaysRemainingManagement holidaysRemainingManagement) {
        String cid = AppContexts.user().companyId();
        val checkLeave = annualPaidLeaveSettingRepository.findByCompanyId(cid);
        if (!holidaysRemainingManagement.getListItemsOutput().getAnnualHoliday().isYearlyHoliday()) {
            return false;
        } else {
            if ((checkLeave == null || checkLeave.getYearManageType() == ManageDistinct.NO)) {
                return false;
            } else {
                if (checkLeave.getTimeSetting().getTimeManageType() == ManageDistinct.YES) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    // F
    private boolean checkLimitHoliday(HolidaysRemainingManagement holidaysRemainingManagement) {
        String cid = AppContexts.user().companyId();
        val checkLeave = annualPaidLeaveSettingRepository.findByCompanyId(cid);
        if (!holidaysRemainingManagement.getListItemsOutput().getAnnualHoliday().isInsideHalfDay()) {
            return false;
        } else if ((checkLeave == null || checkLeave.getYearManageType() == ManageDistinct.NO)) {
            return false;
        } else if (checkLeave.getManageAnnualSetting().getHalfDayManage().getManageType()
                == ManageDistinct.NO) {
            return false;
        } else {
            return true;
        }
    }

    // G
    private boolean checkLimitHourlyHoliday(HolidaysRemainingManagement holidaysRemainingManagement) {
        String cid = AppContexts.user().companyId();
        val checkLeave = annualPaidLeaveSettingRepository.findByCompanyId(cid);
        if (!holidaysRemainingManagement.getListItemsOutput().getAnnualHoliday().isInsideHours()) {
            return false;
        } else {
            if ((checkLeave == null || checkLeave.getYearManageType() == ManageDistinct.NO)) {
                return false;
            } else {
                if (checkLeave.getTimeSetting().getTimeManageType() == ManageDistinct.NO) {
                    return false;
                } else {
                    if (checkLeave.getTimeSetting().getMaxYearDayLeave().manageType == ManageDistinct.NO) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    // H
    private boolean checkYearlyLeave(HolidaysRemainingManagement holidaysRemainingManagement, String employmentCode, VariousVacationControl vacationControl) {
        String cid = AppContexts.user().companyId();
        val checkLeave = annualPaidLeaveSettingRepository.findByCompanyId(cid);
        val checkByScd = employmentSettingRepository.find(cid, employmentCode);
        if (!holidaysRemainingManagement.getListItemsOutput().getYearlyReserved().isYearlyReserved()) {
            return false;
        } else {
            if ((checkLeave == null || checkLeave.getYearManageType() == ManageDistinct.NO)) {
                return false;
            } else {
                if (checkByScd.isPresent()) {
                    return checkByScd.get().getManagementCategory() == ManageDistinct.YES
                            && vacationControl.isYearlyReservedSetting();
                } else {
                    return vacationControl.isYearlyReservedSetting();
                }
            }
        }
    }

    /* I - CASE 1 */
    private boolean checkTakeABreak_01(HolidaysRemainingManagement holidaysRemainingManagement,
                                       String employmentCode) {
        String cid = AppContexts.user().companyId();
        CompensatoryLeaveComSetting compensatoryLeaveComSetting = compensLeaveComSetRepository.find(cid);
        val checkEmLeave = compensLeaveEmSetRepository.find(cid, employmentCode);
        if (!holidaysRemainingManagement.getListItemsOutput().getSubstituteHoliday().isOutputItemSubstitute()) {
            return false;
        }
        if (checkEmLeave == null) {
            return compensatoryLeaveComSetting.isManaged();
        } else {
            return compensatoryLeaveComSetting.isManaged() && checkEmLeave.getIsManaged() == ManageDistinct.YES;
        }
    }

    private boolean checkJ1(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        val companyId = AppContexts.user().companyId();
        val checkPause = dataSource.getHolidaysRemainingManagement().getListItemsOutput().
                getPause().isPauseItem();
        val substVacation = comSubstVacationRepository.findById(companyId);
        val empSubstVacation = substVacationRepository.findById(companyId, employee.getEmploymentCode());
        if (!checkPause) {
            return false;
        }
        if (!empSubstVacation.isPresent()) {
            return substVacation.map(ComSubstVacation::isManaged).orElse(false);
        } else {
            return substVacation.map(ComSubstVacation::isManaged).orElse(false)
                    && empSubstVacation.map(e -> e.getManageDistinct() == ManageDistinct.YES).orElse(false);
        }
    }

    // I - CASE 2
    private boolean checkTakeABreak_02(HolidaysRemainingManagement holidaysRemainingManagement) {
        return holidaysRemainingManagement.getListItemsOutput().getSubstituteHoliday().isRepresentSubstitute();
    }

    // I - CASE 3
    private boolean checkTakeABreak_03(HolidaysRemainingManagement holidaysRemainingManagement) {
        return holidaysRemainingManagement.getListItemsOutput().getSubstituteHoliday().isRemainingChargeSubstitute();
    }

    // L 1
    private boolean checkPublicHolidays(HolidayRemainingDataSource dataSource) {
        val check = dataSource.getHolidaysRemainingManagement().getListItemsOutput().
                getHolidays();
        val listItemsOutput = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        val outputItemsHolidays = listItemsOutput.getHolidays().isOutputItemsHolidays();
        val outputHolidayForward = listItemsOutput.getHolidays().isOutputHolidayForward();
        val monthlyPublic = listItemsOutput.getHolidays().isOutputHolidayForward();

        return outputItemsHolidays;

    }


    private String convertToTime(int minute) {
        val minuteAbs = Math.abs(minute);
        int hours = minuteAbs / 60;
        int minutes = minuteAbs % 60;
        return (minute < 0 ? "-" : "") + String.format("%d:%02d", hours, minutes);
    }

    private int count(HolidayRemainingDataSource dataSource) {
        int firstRow = 0;
        for (String employeeIds : dataSource.getEmpIds()) {
            HolidaysRemainingEmployee employee = dataSource.getMapEmployees().get(employeeIds);
            firstRow += countE(dataSource, employee);
            firstRow += countF(dataSource, employee);
            firstRow += countG(dataSource, employee);
            firstRow += countM(dataSource, employee);
            firstRow += countN(dataSource, employee);
            firstRow += countO(dataSource, employee);
            firstRow += countH(dataSource, employee);
            firstRow += countI(dataSource, employee);
            firstRow += countJ(dataSource, employee);
            firstRow += countK(dataSource, employee);
            firstRow += countL(dataSource, employee);
        }
        return firstRow;
    }

    private int countE(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        if (!dataSource.getVariousVacationControl().isAnnualHolidaySetting()) {
            return firstRow;
        }
        boolean yearlyHoliday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
        if (!(yearlyHoliday)) {
            return firstRow;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            return firstRow;
        }
        val isTime = checkShowAreaAnnualBreak2(dataSource.getHolidaysRemainingManagement());
        int total = isTime ? 4 : 2;
        List<AnnLeaGrantNumberImported> listAnnLeaGrant = hdRemainingInfor.getListAnnLeaGrantNumber();

        if (listAnnLeaGrant != null) {
            for (int i = 0; i < listAnnLeaGrant.size(); i++) {
                if (i >= 2) {
                    total += isTime ? 2 : 1;
                }
            }
        }
        return firstRow + total;
    }

    private int countF(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        if (!checkLimitHoliday(dataSource.getHolidaysRemainingManagement())
                || !employee.getCurrentMonth().isPresent()) {
            return firstRow;
        }
        firstRow += 2;
        return firstRow;
    }

    private int countG(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        if (!checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement()))
            return firstRow;
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            return firstRow + 1;
        }
        val data363 = hdRemainingInfor.getRs363New();

        if (data363 == null || data363.isEmpty()) {
            return firstRow;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            return firstRow + 1;
        }
        firstRow += 1;
        return firstRow;

    }

    private int countM(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        // 特別休暇
        int firstRow = 0;
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            return firstRow;
        }
        val listSphdCode = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getSpecialHoliday();

        Collections.sort(listSphdCode);
        List<SpecialHoliday> specialHolidays = dataSource.getVariousVacationControl().getListSpecialHoliday();
        for (Integer specialHolidayCode : listSphdCode) {

            Optional<SpecialHoliday> specialHolidayOpt = specialHolidays.stream()
                    .filter(c -> c.getSpecialHolidayCode().v() == specialHolidayCode).findFirst();
            val listFrameNo = specialHolidayOpt.get().getTargetItem().getFrameNo();
            val itemFrame = specialHolidayFrameRepository.findHolidayFrameByListFrame(AppContexts.user().companyId(), listFrameNo)
                    .stream().filter(e -> e.getTimeMngAtr().value == NotUseAtr.USE.value).collect(Collectors.toList());
            val isTime = !itemFrame.isEmpty();
            if (!specialHolidayOpt.isPresent()) {
                continue;
            }
            firstRow += isTime ? 4 : 2;
        }
        return firstRow;
    }

    private int countN(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        // 子の看護休暇
        if (!dataSource.getVariousVacationControl().isChildNursingSetting()) {
            return firstRow;
        }
        val listNursingLeaveSetting = nursingLeaveSettingRepository.findByCompanyId(AppContexts.user().companyId());
        val childNursing = listNursingLeaveSetting.stream()
                .filter(i -> i.getNursingCategory() == NursingCategory.ChildNursing && i.getTimeCareNursingSetting()
                        .getManageDistinct() == ManageDistinct.YES).findFirst();
        boolean isTime = false;
        if (childNursing.isPresent()) {
            isTime = childNursing.get().isManaged();
        }
        if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getChildNursingVacation()
                .isChildNursingLeave()) {
            return firstRow;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            return firstRow;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            return firstRow;
        }
        return firstRow + (isTime ? 4 : 2);
    }

    private int countO(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        // 介護休暇
        int firstRow = 0;
        val listNursingLeaveSetting = nursingLeaveSettingRepository.findByCompanyId(AppContexts.user().companyId());
        val childNursing = listNursingLeaveSetting.stream()
                .filter(i -> i.getNursingCategory() == NursingCategory.ChildNursing && i.getTimeCareNursingSetting()
                        .getManageDistinct() == ManageDistinct.YES).findFirst();
        boolean isTime = false;
        if (childNursing.isPresent()) {
            isTime = childNursing.get().isManaged();
        }
        if (!dataSource.getVariousVacationControl().isNursingCareSetting()) {
            return firstRow;
        }
        if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getNursingcareLeave().isNursingLeave()) {
            return firstRow;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();

        if (hdRemainingInfor == null) {
            return firstRow;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            return firstRow;
        }
        return firstRow + (isTime ? 4 : 2);
    }

    private int countH(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        if (!checkYearlyLeave(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode(),
                dataSource.getVariousVacationControl())) {
            return firstRow;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            return firstRow + 2;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            return firstRow + 2;
        }
        firstRow += 2;
        return firstRow;
    }

    private int countI(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        if (!checkTakeABreak_01(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode())) {
            return firstRow;
        }
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        int totalRows = 2;
        if (hdRemainingInfor == null) {
            return firstRow + 2;
        }
        val isShow41 = checkTakeABreak_02(dataSource.getHolidaysRemainingManagement());
        val isShow51 = checkTakeABreak_03(dataSource.getHolidaysRemainingManagement());
        val show1item = (isShow51 && !isShow41) || (!isShow51 && isShow41);

        if (isShow41 && isShow51) {
            totalRows += 2;
        }
        if (show1item) {
            totalRows += 1;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            return firstRow + totalRows;
        }
        firstRow += totalRows;
        return firstRow;
    }

    private int countJ(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        // 振休
        Integer count = 0;
        Integer first = 0;
        NumberFormat df = new DecimalFormat("#0.0");
        if (!checkJ1(dataSource, employee)) {

            return count;
        }
        val holiday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause();
        boolean isPauseItem = holiday.isPauseItem();
        boolean isUndigestedPause = holiday.isUndigestedPause();
        boolean isNumberRemainingPause = holiday.isNumberRemainingPause();

        if (!isPauseItem) {

            return count;
        }
        int totalRows = 2;

        val showI = checkTakeABreak_01(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode());
        val isShow41 = checkTakeABreak_02(dataSource.getHolidaysRemainingManagement());
        val isShow51 = checkTakeABreak_03(dataSource.getHolidaysRemainingManagement());
        val show1item = (isShow51 && !isShow41) || (!isShow51 && isShow41);
        val checkG = checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement());

        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            count += 2;
            return count;
        }

        if (isUndigestedPause) {

            totalRows += 1;
        }
        if (isNumberRemainingPause) {
            totalRows += 1;
        }

        if (!employee.getCurrentMonth().isPresent()) {
            count += totalRows;
            return count;
        }
        val currentHolidayRemainLeft = hdRemainingInfor.getCurrentHolidayRemainLeft();
        // Current month
        if (currentHolidayRemainLeft == null) {
            count += totalRows;
            return count;
        }
        count += totalRows;
        return count;
    }

    private int countK(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        val holidayRemainingManagement = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        if (!holidayRemainingManagement.getOutOfTime().isOvertimeItem()) {
            return firstRow;
        }
        if (!dataSource.getVariousVacationControl().isCom60HourVacationSetting()) {
            return firstRow;
        }
        boolean isUndigestedPause = holidayRemainingManagement.getOutOfTime().isOvertimeOverUndigested();
        boolean isOvertimeRemaining = holidayRemainingManagement.getOutOfTime().isOvertimeRemaining();
        int totalRows = 2;
        val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
        if (hdRemainingInfor == null) {
            return firstRow + 2;
        }
        if (isUndigestedPause) {
            totalRows += 1;
        }
        if (isOvertimeRemaining) {
            totalRows += 1;
        }
        if (!employee.getCurrentMonth().isPresent()) {
            return firstRow + totalRows;
        }
        return firstRow + totalRows;
    }

    private int countL(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        int firstRow = 0;
        val listItemsOutput = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        // ※2 : 出力項目設定[公休繰越数]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休繰越数を出力する
        //※1: 出力項目設定[公休]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休の項目を出力する
        //     公休管理設定：公休設定．管理区分
        val outputItemsHolidays = listItemsOutput.getHolidays().isOutputItemsHolidays();

        if (!outputItemsHolidays) {
            return firstRow;
        }
        // ※2 : 出力項目設定[公休繰越数]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休繰越数を出力する
        val outputHolidayForward = listItemsOutput.getHolidays().isOutputHolidayForward();
        // ※3 : 出力項目設定[公休月度残]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休月度残を出力する
        val monthlyPublic = listItemsOutput.getHolidays().isMonthlyPublic();
        int countL = 2;
        if (outputHolidayForward) {
            countL += 1;
        }
        if (monthlyPublic) {
            countL += 1;
        }
        return firstRow + countL;
    }

    private void printEmployeeInfore(Cells cells, int firstRow, HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee)
            throws Exception {
        NumberFormat df = new DecimalFormat("#0.0");
        for (int j = 0; j < 2; j++) {
            setTopBorderStyle(cells.get(firstRow, j));
        }
        val d2_7 = employee.getPositionCode() + SPACE
                + employee.getPositionName();
        val d2_6 = employee.getEmploymentCode() + SPACE
                + employee.getEmploymentName();
        Optional<GeneralDate> grantDate = dataSource.getMapEmployees().get(employee.getEmployeeId())
                .getHolidayRemainingInfor().getGrantDate();
        boolean isDisplayHolidayYear = dataSource.getVariousVacationControl().isAnnualHolidaySetting()
                && dataSource.getHolidaysRemainingManagement()
                .getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
        // merger cột  D2_1 + D2_2
        cells.merge(firstRow, 0, 1, 2, true);
        cells.get(firstRow, 0).setValue(employee.getEmployeeCode() + SPACE + employee.getEmployeeName());
        // D2_7 + D25 (POISITION CODE + POISITION NAME)
        cells.merge(firstRow + 1, 0, 1, 2, true);
        cells.get(firstRow + 1, 0).setValue(d2_7);

        // D2_6 +D2_4: EMPLOYMENT CODE +EMPLOYMENT NAME
        cells.merge(firstRow + 2, 0, 1, 2, true);
        cells.get(firstRow + 2, 0).setValue(d2_6);
        //GeneralDate criteriaDate = GeneralDate.today();
        // D2_3 No.369
        if (isDisplayHolidayYear) {
            if (grantDate.isPresent()) {
                val date = grantDate.get();
                // D2_3
                cells.merge(firstRow + 3, 0, 1, 2, true);
                cells.get(firstRow + 3, 0)
                        .setValue(TextResource.localize("KDR001_71",
                                date.toString("yyyy/MM/dd")));
            }
            cells.merge(firstRow + 4, 0, 1, 2, true);
            String yearHoliday = "0.0";
            AnnualLeaveUsedDayNumber yearHolidayDate = findAnnLeaUsedDaysAdapter.findUsedDays(employee.getEmployeeId());
            if (yearHolidayDate != null) {
                yearHoliday = df.format(yearHolidayDate.v());
            }
            cells.get(firstRow + 4, 0).setValue(TextResource.localize("KDR001_72", yearHoliday));
        }
    }

    private int checkStepCount(HolidaysRemainingEmployee employee
            , HolidayRemainingDataSource dataSource) {
        Integer count = 0;
        val countE = countE(dataSource, employee);
        if (countE > 0) {
            return 4;
        }
        val countF = countF(dataSource, employee);
        if (countF > 0) {
            return 2;
        }
        val countG = countG(dataSource, employee);
        if (countG > 0) {
            return 1;
        }
        val countH = countH(dataSource, employee);
        if (countH > 0) {
            return 2;
        }
        val countI = countI(dataSource, employee);
        if (countI > 0) {
            return 4;
        }
        val countJ = countJ(dataSource, employee);
        if (countJ > 0) {
            return 4;
        }
        val countK = countK(dataSource, employee);
        if (countK > 0) {
            return 4;
        }
        val countL = countL(dataSource, employee);
        if (countL > 0) {
            return 4;
        }
        val countM = countM(dataSource, employee);
        if (countM > 0) {
            return 4;
        }
        val countN = countM(dataSource, employee);
        if (countN > 0) {
            return 4;
        }
        val countO = countO(dataSource, employee);
        if (countO > 0) {
            return 4;
        }
        return count;
    }

    boolean checkCopyRow(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        val isShowI = checkTakeABreak_01(dataSource.getHolidaysRemainingManagement(), employee.getEmploymentCode());
        val isShowI41 = checkTakeABreak_02(dataSource.getHolidaysRemainingManagement());
        val isShowI51 = checkTakeABreak_03(dataSource.getHolidaysRemainingManagement());
        val show1itemI = (isShowI51 && !isShowI41) || (!isShowI51 && isShowI41);
        val checkG = checkLimitHourlyHoliday(dataSource.getHolidaysRemainingManagement());
        val holiday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause();
        boolean pauseItem = holiday.isPauseItem();
        boolean isUndigestedPauseJ = holiday.isUndigestedPause();
        boolean isNumberRemainingPauseJ = holiday.isNumberRemainingPause();
        val isShow1itemJ = checkJ1(dataSource, employee) && ((pauseItem & (isUndigestedPauseJ & !isNumberRemainingPauseJ))
                || (pauseItem & (!isUndigestedPauseJ & isNumberRemainingPauseJ)));

        boolean copyNew = false;
        if (!checkG) {
            copyNew = !copyNew;
        }
        if (isShowI && show1itemI) {
            copyNew = !copyNew;
        }
        if (isShow1itemJ) {
            copyNew = !copyNew;
        }
        return copyNew;
    }

    boolean checkCopyRowK(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        val holidayRemainingManagement = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        val isOverTime = holidayRemainingManagement.getOutOfTime().isOvertimeItem();
        boolean isUndigestedPause = holidayRemainingManagement.getOutOfTime().isOvertimeOverUndigested();
        boolean isOvertimeRemaining = holidayRemainingManagement.getOutOfTime().isOvertimeRemaining();
        val show1itemK = isOverTime && (isUndigestedPause && !isOvertimeRemaining) ||
                isOverTime && (!isUndigestedPause && isOvertimeRemaining);
        boolean copyNew = checkCopyRow(dataSource, employee);

        if (show1itemK) {
            copyNew = !copyNew;
        }
        return copyNew;
    }

    boolean checkCopyRowL(HolidayRemainingDataSource dataSource, HolidaysRemainingEmployee employee) {
        val holidayRemainingManagement = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        val isOverTime = holidayRemainingManagement.getOutOfTime().isOvertimeItem();
        boolean isUndigestedPause = holidayRemainingManagement.getOutOfTime().isOvertimeOverUndigested();
        boolean isOvertimeRemaining = holidayRemainingManagement.getOutOfTime().isOvertimeRemaining();
        val show1itemK = isOverTime && (isUndigestedPause && !isOvertimeRemaining) ||
                isOverTime && (!isUndigestedPause && isOvertimeRemaining);
        boolean copyNew = checkCopyRow(dataSource, employee);

        if (show1itemK) {
            copyNew = !copyNew;
        }
        val listItemsOutput = dataSource.getHolidaysRemainingManagement().getListItemsOutput();
        // ※2 : 出力項目設定[公休繰越数]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休繰越数を出力する
        val outputHolidayForward = listItemsOutput.getHolidays().isOutputHolidayForward();

        // ※3 : 出力項目設定[公休月度残]：休暇残数管理表の出力項目設定．出力する項目一覧．公休．公休月度残を出力する
        val monthlyPublic = listItemsOutput.getHolidays().isMonthlyPublic();
        int countL = 2;
        if (outputHolidayForward) {
            countL += 1;
        }
        if (monthlyPublic) {
            countL += 1;
        }
        if (countL == 3) {
            copyNew = !copyNew;
        }
        return copyNew;
    }
}