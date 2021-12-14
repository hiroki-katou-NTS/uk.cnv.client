package nts.uk.ctx.at.function.infra.generator.holidaysremaining;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Shape;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnLeaveUsageStatusOfThisMonthImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.AnnualLeaveUsageImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.CurrentHolidayRemainImported;
import nts.uk.ctx.at.function.dom.adapter.holidaysremaining.StatusOfHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.periodofspecialleave.SpecialVacationImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.ReservedYearHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.reserveleave.RsvLeaUsedCurrentMonImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.CurrentHolidayImported;
import nts.uk.ctx.at.function.dom.adapter.vacation.StatusHolidayImported;
import nts.uk.ctx.at.function.dom.holidaysremaining.BreakSelection;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidayRemainingDataSource;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidaysRemainingEmployee;
import nts.uk.ctx.at.function.dom.holidaysremaining.report.HolidaysRemainingReportGenerator;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRepository;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

@Stateless
public class HolidaysRemainingReportGeneratorImp extends AsposeCellsReportGenerator
		implements HolidaysRemainingReportGenerator {

	@Inject
	private SpecialLeaveGrantRepository specialLeaveGrantRepository;

	private static final String TEMPLATE_FILE = "report/休暇残数管理表_テンプレート.xlsx";
	private static final String REPORT_FILE_NAME = "休暇残数管理表.xlsx";
	private static final int NUMBER_ROW_OF_PAGE = 37;
	private static final int NUMBER_ROW_OF_HEADER = 5;
	private static final int NUMBER_COLUMN = 23;
	private static final int MIN_ROW_DETAILS = 4;
	private static final int TOTAL_MONTH_IN_YEAR = 12;
	private static final int MAX_ROW_ANNUAL_HOLIDAY = 10;
	private static final int MIN_ROW_ANNUAL_HOLIDAY = 2;

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

			if (dataSource.getPageBreak() == BreakSelection.None.value) {
				printNoneBreakPage(worksheet, dataSource);
			} else if (dataSource.getPageBreak() == BreakSelection.Workplace.value) {
				printWorkplaceBreakPage(worksheet, dataSource);
			} else if (dataSource.getPageBreak() == BreakSelection.Individual.value) {
				printPersonBreakPage(worksheet, dataSource);
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

		// B1_1, B1_2
		cells.get(1, 0).setValue(TextResource.localize("KDR001_2") + dataSource.getStartMonth().toString("yyyy/MM")
				+ "　～　" + dataSource.getEndMonth().toString("yyyy/MM"));
		// B1_3
		cells.get(2, 0).setValue(TextResource.localize("KDR001_3"));
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
			cells.get(4, 10 + i).setValue(String.valueOf(startMonth.addMonths(i).month()) + "月");
		}
	}

	private void printNoneBreakPage(Worksheet worksheet, HolidayRemainingDataSource dataSource) throws Exception {
		int firstRow = NUMBER_ROW_OF_PAGE;

		Cells cells = worksheet.getCells();

		// print Header
		cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
		firstRow += NUMBER_ROW_OF_HEADER;

		for (String employeeIds : dataSource.getEmpIds()) {
			HolidaysRemainingEmployee employee = dataSource.getMapEmployees().get(employeeIds);
			int rowCount = countRowEachPerson(dataSource) + 1;
			if (firstRow % NUMBER_ROW_OF_PAGE != NUMBER_ROW_OF_HEADER
					&& firstRow / NUMBER_ROW_OF_PAGE != (firstRow + rowCount) / NUMBER_ROW_OF_PAGE) {
				firstRow = (firstRow / NUMBER_ROW_OF_PAGE + 1) * NUMBER_ROW_OF_PAGE;
				// print Header
				cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
				firstRow += NUMBER_ROW_OF_HEADER;
			}
			// D1_1, D1_2
			cells.get(firstRow, 0).setValue(TextResource.localize("KDR001_12") + ": " + employee.getWorkplaceCode()
					+ "　" + employee.getWorkplaceName());
			firstRow += 1;
			firstRow = this.printHolidayRemainingEachPerson(worksheet, firstRow, employee, dataSource);
		}
	}

	private void printWorkplaceBreakPage(Worksheet worksheet, HolidayRemainingDataSource dataSource) throws Exception {
		int firstRow = NUMBER_ROW_OF_PAGE;
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
			firstRow = printEachWorkplace(worksheet, firstRow, listEmployee, dataSource);
		}
	}

	private int printEachWorkplace(Worksheet worksheet, int firstRow, List<HolidaysRemainingEmployee> employees,
			HolidayRemainingDataSource dataSource) throws Exception {
		if (employees.size() == 0) {
			return firstRow;
		}
		Cells cells = worksheet.getCells();

		// print Header
		cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
		// D1_1, D1_2
		cells.get(firstRow + NUMBER_ROW_OF_HEADER, 0).setValue(TextResource.localize("KDR001_12") + ": "
				+ employees.get(0).getWorkplaceCode() + "　" + employees.get(0).getWorkplaceName());
		firstRow += NUMBER_ROW_OF_HEADER + 1;

		for (HolidaysRemainingEmployee employee : employees) {
			int rowCount = countRowEachPerson(dataSource);
			if (firstRow % NUMBER_ROW_OF_PAGE != NUMBER_ROW_OF_HEADER + 1
					&& firstRow / NUMBER_ROW_OF_PAGE != (firstRow + rowCount) / NUMBER_ROW_OF_PAGE) {
				firstRow = (firstRow / NUMBER_ROW_OF_PAGE + 1) * NUMBER_ROW_OF_PAGE;
				// print Header
				cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
				// D1_1, D1_2
				cells.get(firstRow + NUMBER_ROW_OF_HEADER, 0).setValue(TextResource.localize("KDR001_12") + ": "
						+ employees.get(0).getWorkplaceCode() + "　" + employees.get(0).getWorkplaceName());
				firstRow += NUMBER_ROW_OF_HEADER + 1;
			}
			firstRow = printHolidayRemainingEachPerson(worksheet, firstRow, employee, dataSource);
		}
		if (firstRow % NUMBER_ROW_OF_PAGE != 0) {
			firstRow += (NUMBER_ROW_OF_PAGE - firstRow % NUMBER_ROW_OF_PAGE);
		}

		return firstRow;
	}

	private void printPersonBreakPage(Worksheet worksheet, HolidayRemainingDataSource dataSource) throws Exception {
		int firstRow = NUMBER_ROW_OF_PAGE;

		for (String employeeIds : dataSource.getEmpIds()) {
			HolidaysRemainingEmployee employee = dataSource.getMapEmployees().get(employeeIds);
			firstRow = this.printEachPerson(worksheet, firstRow, employee, dataSource);
		}
	}

	private int printEachPerson(Worksheet worksheet, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {

		Cells cells = worksheet.getCells();
		// D index
		// print Header
		cells.copyRows(cells, 0, firstRow, NUMBER_ROW_OF_HEADER + 1);
		// D1_1, D1_2
		cells.get(firstRow + NUMBER_ROW_OF_HEADER, 0).setValue(TextResource.localize("KDR001_12") + ": "
				+ employee.getWorkplaceCode() + "　" + employee.getWorkplaceName());
		firstRow += NUMBER_ROW_OF_HEADER + 1;

		firstRow = printHolidayRemainingEachPerson(worksheet, firstRow, employee, dataSource);

		if (firstRow % NUMBER_ROW_OF_PAGE != 0) {
			firstRow += (NUMBER_ROW_OF_PAGE - firstRow % NUMBER_ROW_OF_PAGE);
		}

		return firstRow;
	}

	private int printHolidayRemainingEachPerson(Worksheet worksheet, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		int rowIndexD = firstRow;
		Cells cells = worksheet.getCells();

		firstRow = printAnnualHoliday(cells, firstRow, employee, dataSource);

		firstRow = printYearlyReserved(cells, firstRow, employee, dataSource);

		firstRow = printSubstituteHoliday(cells, firstRow, employee, dataSource);

		firstRow = printPauseHoliday(cells, firstRow, employee, dataSource);

		firstRow = printSpecialHoliday(cells, firstRow, employee, dataSource);

		firstRow = printChildNursingVacation(cells, firstRow, employee, dataSource);

		firstRow = printNursingCareLeave(cells, firstRow, employee, dataSource);

		int totalRowDetails = countRowEachPerson(dataSource);
		if (totalRowDetails < MIN_ROW_DETAILS) {
			// Insert blank rows
			cells.copyRows(cells, 33, firstRow, MIN_ROW_DETAILS - totalRowDetails);
			firstRow += (MIN_ROW_DETAILS - totalRowDetails);
		}

		// merger cột  D2_1 + D2_2
		cells.merge(rowIndexD, 0, 1, 2, true);
		// D2_1
		cells.get(rowIndexD, 0).setValue(employee.getEmployeeCode()+ " " + employee.getEmployeeName());

		// D2_3 No.369
		cells.merge(rowIndexD + 1, 0, 1, 2, true);
		Optional<GeneralDate> grantDate = dataSource.getMapEmployees().get(employee.getEmployeeId())
				.getHolidayRemainingInfor().getGrantDate();
		//  truyền params 事象(3), ý 3 của bug #102883
		boolean isDisplayHolidayYear = dataSource.getVariousVacationControl().isAnnualHolidaySetting() && dataSource.getHolidaysRemainingManagement()
				.getListItemsOutput().getAnnualHoliday().isYearlyHoliday();
		if (isDisplayHolidayYear) {
			if(grantDate.isPresent()) {
				 cells.get(rowIndexD + 1, 0).setValue(TextResource.localize("KDR001_56", grantDate.get().toString("yyyy/MM/dd")));
			}else {
				cells.get(rowIndexD + 1, 0).setValue(TextResource.localize("KDR001_56", TextResource.localize("KDR001_58")));
			}
		}
		// merger cột D2_4, ý 2 của bug #102883
		cells.merge(isDisplayHolidayYear== true? rowIndexD + 2: rowIndexD + 1, 0, 1, 2, true);
		// D2_4
		cells.get(isDisplayHolidayYear== true? rowIndexD + 2: rowIndexD + 1, 0).setValue(employee.getEmploymentName());

		// merger cột D2_5, ý 2 của bug #102883
		cells.merge(isDisplayHolidayYear== true? rowIndexD + 3: rowIndexD + 2, 0, 1, 2, true);
		cells.get(isDisplayHolidayYear== true? rowIndexD + 3: rowIndexD + 2, 0).setValue(employee.getJobTitle());

		// Set Style
		for (int index = 0; index < NUMBER_COLUMN; index++) {
			setTopBorderStyle(cells.get(rowIndexD, index));
			setBottomBorderStyle(cells.get(firstRow - 1, index));
		}

		return firstRow;
	}

	private int printAnnualHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		// 年休
		// Check holiday management
		if (!dataSource.getVariousVacationControl().isAnnualHolidaySetting()) {
			return firstRow;
		}

		if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isYearlyHoliday()) {
			return firstRow;
		}

		val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
		if (hdRemainingInfor == null) {
			return firstRow;
		}
		int totalAddRows = MIN_ROW_ANNUAL_HOLIDAY;
		cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 1, firstRow, MIN_ROW_ANNUAL_HOLIDAY);
		// E1_1
		cells.get(firstRow, 2).setValue(TextResource.localize("Com_PaidHoliday"));
		// E2_1
		cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_14"));
		// E3_1
		cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_15"));

		// Result RequestList281
		val listAnnLeaGrant = hdRemainingInfor.getListAnnLeaGrantNumber();

		Optional<GeneralDate> grantDate = dataSource.getMapEmployees().get(employee.getEmployeeId())
				.getHolidayRemainingInfor().getGrantDate();

		if (listAnnLeaGrant != null && grantDate.isPresent()) {
			for (int i = 0; i < listAnnLeaGrant.size() && i < MAX_ROW_ANNUAL_HOLIDAY; i++) {
				if (i >= MIN_ROW_ANNUAL_HOLIDAY) {
					totalAddRows += 1;
					cells.copyRows(cells, NUMBER_ROW_OF_HEADER + i + 2, firstRow + i, 1);
				}
				// E1_2, 事象(4)của bug #102883
				cells.get(firstRow + i, 3).setValue(TextResource.localize("KDR001_57",
						listAnnLeaGrant.get(i).getGrantDate().toString("yyyy/MM/dd")));
				// E1_3
				cells.get(firstRow + i, 4).setValue(listAnnLeaGrant.get(i).getGrantDays());
			}
		}

		// Result RequestList265
		AnnLeaveOfThisMonthImported annLeave = hdRemainingInfor.getAnnLeaveOfThisMonth();
		if (annLeave != null && grantDate.isPresent()) {
			// E1_4
			cells.get(firstRow, 5).setValue(annLeave.getFirstMonthRemNumDays());
			if (annLeave.getFirstMonthRemNumDays() < 0) {
				setForegroundRed(cells.get(firstRow, 5));
			}
			// E1_5
			cells.get(firstRow, 6).setValue(annLeave.getUsedDays());
			// E1_6
			cells.get(firstRow, 7).setValue(annLeave.getRemainDays());
			if (annLeave.getRemainDays() < 0) {
				setForegroundRed(cells.get(firstRow, 7));
			}
		}
		if (!employee.getCurrentMonth().isPresent()) {
			return firstRow + totalAddRows;
		}
		YearMonth currentMonth = employee.getCurrentMonth().get();
		// Result RequestList255
		val listAnnLeaveUsage = hdRemainingInfor.getListAnnualLeaveUsage();

		int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
		if (listAnnLeaveUsage != null && dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor().getGrantDate().isPresent()) {
			for (AnnualLeaveUsageImported item : listAnnLeaveUsage) {
				if (currentMonth.compareTo(item.getYearMonth()) <= 0) {
					continue;
				}
				int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
				if (maxRange < totalMonth || totalMonth < 0) {
					continue;
				}
				// E2_3 当月より前, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
				cells.get(firstRow, 10 + totalMonth).setValue(item.getUsedDays()!=null && item.getUsedDays()!=0? item.getUsedDays(): null);
				// E3_3 当月より前
				cells.get(firstRow + 1, 10 + totalMonth).setValue(item.getRemainingDays());
				if (item.getRemainingDays() < 0) {
					setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
				}
			}
		}

		// Result RequestList363
		val listAnnLeaveUsageOfThisMonth = hdRemainingInfor.getListAnnLeaveUsageStatusOfThisMonth();

		if (listAnnLeaveUsageOfThisMonth != null && grantDate.isPresent()) {
			for (AnnLeaveUsageStatusOfThisMonthImported item : listAnnLeaveUsageOfThisMonth) {
				if (currentMonth.compareTo(item.getYearMonth()) > 0) {
					continue;
				}
				int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYearMonth());
				if (maxRange < totalMonth || totalMonth < 0) {
					continue;
				}
				// E2_3 当月以降, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
				cells.get(firstRow, 10 + totalMonth).setValue(item.getMonthlyUsageDays()!=null && item.getMonthlyUsageDays()!=0? item.getMonthlyUsageDays(): null);
				// E3_3 当月以降
				if(item.getYearMonth().equals(currentMonth)){
					cells.get(firstRow + 1, 10 + totalMonth).setValue(item.getMonthlyRemainingDays());
				}

				if (item.getMonthlyRemainingDays() < 0) {
					setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
				}
			}
		}

		// Set background
		for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
				dataSource.getEndMonth().yearMonth()); i++) {
			if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
				setBackgroundGray(cells.get(firstRow + 1, 10 + i));
//				for (int j = 0; j < totalAddRows; j++) {
//					setBackgroundGray(cells.get(firstRow + j, 10 + i));
//				}
			}
			if (!dataSource.isSameCurrentMonth()
					&& dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
				for (int j = 0; j < totalAddRows; j++) {
					setCurrentMonthBackground(cells.get(firstRow + j, 10 + i));
				}
			}
		}

		return firstRow + totalAddRows;
	}

	private int printYearlyReserved(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		// 積立年休
		if (!dataSource.getVariousVacationControl().isYearlyReservedSetting()) {
			return firstRow;
		}
		if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getYearlyReserved().isYearlyReserved()) {
			return firstRow;
		}

		val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
		if (hdRemainingInfor == null) {
			return firstRow;
		}

		cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 11, firstRow, 2);
		// H1_1
		cells.get(firstRow, 2).setValue(TextResource.localize("Com_FundedPaidHoliday"));
		// H2_1
		cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_14"));
		// H2_2
		cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_15"));

		// Set data for 積立年休 (H)
		// Result RequestList268
		val reserveHolidayImported = hdRemainingInfor.getReserveHoliday();
		if (reserveHolidayImported != null){
				// H1_2
			cells.get(firstRow, 4).setValue(reserveHolidayImported.getGrantNumber());
			// H1_3
			cells.get(firstRow, 5).setValue(reserveHolidayImported.getStartMonthRemain());
			if (reserveHolidayImported.getStartMonthRemain() < 0) {
				setForegroundRed(cells.get(firstRow, 5));
			}
			// H1_4
			cells.get(firstRow, 6).setValue(reserveHolidayImported.getUsedNumber());
			// H1_5
			cells.get(firstRow, 7).setValue(reserveHolidayImported.getRemainNumber());
			if (reserveHolidayImported.getRemainNumber() < 0) {
				setForegroundRed(cells.get(firstRow, 7));
			}
			// H1_6
			cells.get(firstRow, 8).setValue(reserveHolidayImported.getUndigestNumber());

			if (reserveHolidayImported.getUndigestNumber() != null
					&& reserveHolidayImported.getUndigestNumber() > 0) {
				setForegroundRed(cells.get(firstRow, 8));
			}

		}

		if (!employee.getCurrentMonth().isPresent()) {
			return firstRow + 2;
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
								&& reservedYearHolidayItem.getUsedDays() == 0 ? null: reservedYearHolidayItem.getUsedDays());
				// H2_4 当月より前
				cells.get(firstRow + 1, 10 + totalMonth).setValue(reservedYearHolidayItem.getRemainingDays());
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
						&& rsvLeaUsedCurrentMonItem.getUsedNumber()==0? null: rsvLeaUsedCurrentMonItem.getUsedNumber());
				if (currentMonth.compareTo(rsvLeaUsedCurrentMonItem.getYearMonth()) != 0) {
					continue;
				}
				// H2_4 当月以降
				cells.get(firstRow + 1, 10 + totalMonth).setValue(rsvLeaUsedCurrentMonItem.getRemainNumber());
				if (rsvLeaUsedCurrentMonItem.getRemainNumber() < 0) {
					setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
				}
			}
		}

		// Set background
		for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
				dataSource.getEndMonth().yearMonth()); i++) {
			if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
				setBackgroundGray(cells.get(firstRow + 1, 10 + i));
			}
			if (!dataSource.isSameCurrentMonth()
					&& dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
				setCurrentMonthBackground(cells.get(firstRow, 10 + i));
				setCurrentMonthBackground(cells.get(firstRow + 1, 10 + i));
			}
		}

		return firstRow + 2;
	}

	private int printSubstituteHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {

		// 代休
		if (!dataSource.getVariousVacationControl().isSubstituteHolidaySetting()) {
			return firstRow;
		}
		val holiday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getSubstituteHoliday();
		boolean isOutputItemSubstitute = holiday.isOutputItemSubstitute();
		boolean isRepresentSubstitute = holiday.isRepresentSubstitute();
		boolean isRemainingChargeSubstitute = holiday.isRemainingChargeSubstitute();

		if (!isOutputItemSubstitute) {
			return firstRow;
		}

		val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
		if (hdRemainingInfor == null) {
			return firstRow;
		}

		int totalRows = 2;
		int rowIndexRepresentSubstitute = 0;
		int rowIndexIsRemainingChargeSubstitute = 0;
		cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 13, firstRow, 2);
		// I1_1
		cells.get(firstRow, 2).setValue(TextResource.localize("Com_CompensationHoliday"));
		// I2_1
		cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_16"));
		// I3_1
		cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_17"));

		if (isRepresentSubstitute) {
			rowIndexRepresentSubstitute = firstRow + totalRows;
			cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 15, rowIndexRepresentSubstitute, 1);
			// I4_1
			cells.get(rowIndexRepresentSubstitute, 9).setValue(TextResource.localize("KDR001_11"));
			totalRows += 1;

		}

		if (isRemainingChargeSubstitute) {
			rowIndexIsRemainingChargeSubstitute = firstRow + totalRows;
			cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 16, rowIndexIsRemainingChargeSubstitute, 1);
			// I5_1
			cells.get(rowIndexIsRemainingChargeSubstitute, 9).setValue(TextResource.localize("KDR001_18"));
			totalRows += 1;
		}

		if (!employee.getCurrentMonth().isPresent()) {
			return firstRow + totalRows;
		}
		YearMonth currentMonth = employee.getCurrentMonth().get();
		// Set value for I
		// Result RequestList269
		val currentHolidayImported = hdRemainingInfor.getListCurrentHoliday();
		// Result RequestList259
		val statusHolidayImported = hdRemainingInfor.getListStatusHoliday();

		val currentHolidayLeft = hdRemainingInfor.getCurrentHolidayLeft();


		int maxRange = totalMonths(dataSource.getStartMonth().yearMonth(), dataSource.getEndMonth().yearMonth());
		if (statusHolidayImported != null) {
			for (StatusHolidayImported statusHolidayItem : statusHolidayImported) {
				// Before this month
				if (currentMonth.compareTo(statusHolidayItem.getYm()) <= 0) {
					continue;
				}
				// Before this month
				int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), statusHolidayItem.getYm());
				if (maxRange >= totalMonth && totalMonth >= 0) {
					// I2_3 代休_発生_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
					cells.get(firstRow, 10 + totalMonth)
							.setValue(statusHolidayItem.getOccurrenceDays() != null
									&& statusHolidayItem.getOccurrenceDays() == 0 ? null: statusHolidayItem.getOccurrenceDays());
					// I3_3 代休_使用_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
					cells.get(firstRow + 1, 10 + totalMonth).setValue(statusHolidayItem.getUseDays()!=null && statusHolidayItem.getUseDays()==0? null: statusHolidayItem.getUseDays());
					if (isRepresentSubstitute) {
						// I4_3 代休_未消化_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
						cells.get(rowIndexRepresentSubstitute, 10 + totalMonth)
								.setValue(statusHolidayItem.getUnUsedDays()!=null && statusHolidayItem.getUnUsedDays()==0? null: statusHolidayItem.getUnUsedDays());
						if(statusHolidayItem.getUnUsedDays() != null && statusHolidayItem.getUnUsedDays() > 0) {
							setForegroundRed(cells.get(rowIndexRepresentSubstitute, 10 + totalMonth));
						}

					}

					if (isRemainingChargeSubstitute) {
						// I5_3 代休_残数_日数
						cells.get(rowIndexIsRemainingChargeSubstitute, 10 + totalMonth)
								.setValue(statusHolidayItem.getRemainDays());
						if (statusHolidayItem.getRemainDays() < 0) {
							setForegroundRed(cells.get(rowIndexIsRemainingChargeSubstitute, 10 + totalMonth));
						}
					}
				}

			}
		}

		if (currentHolidayImported != null) {
			for (CurrentHolidayImported currentHolidayItem : currentHolidayImported) {
				// After this month
				if (currentMonth.compareTo(currentHolidayItem.getYm()) <= 0) {
					int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentHolidayItem.getYm());
					if (maxRange >= totalMonth && totalMonth >= 0) {
						// I2_3 代休_発生_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
						cells.get(firstRow, 10 + totalMonth)
								.setValue(currentHolidayItem.getMonthOccurrence() != null
										&& currentHolidayItem.getMonthOccurrence() == 0 ? null: currentHolidayItem.getMonthOccurrence());
						// I3_3 代休_使用_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
						cells.get(firstRow + 1, 10 + totalMonth).setValue(currentHolidayItem.getMonthUse()!=null
								&& currentHolidayItem.getMonthUse()==0? null: currentHolidayItem.getMonthUse());
						if (currentMonth.compareTo(currentHolidayItem.getYm()) == 0) {
							if (isRepresentSubstitute) {
								// I4_3 代休_未消化_日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
								cells.get(rowIndexRepresentSubstitute, 10 + totalMonth)
										.setValue(currentHolidayItem.getMonthExtinction() != null
												&& currentHolidayItem.getMonthExtinction() == 0 ? null: currentHolidayItem.getMonthExtinction());
								if (currentHolidayItem.getMonthExtinction() != null
										&& currentHolidayItem.getMonthExtinction() > 0) {
									setForegroundRed(cells.get(rowIndexRepresentSubstitute, 10 + totalMonth));
								}
							}
							if (isRemainingChargeSubstitute) {
								// I5_3 代休_残数_日数
								cells.get(rowIndexIsRemainingChargeSubstitute, 10 + totalMonth)
										.setValue(currentHolidayItem.getMonthEndRemain());
								if (currentHolidayItem.getMonthEndRemain() < 0) {
									setForegroundRed(cells.get(rowIndexIsRemainingChargeSubstitute, 10 + totalMonth));
								}
							}
						}
					}
				}

				// Current month
			//	if (currentMonth.compareTo(currentHolidayItem.getYm()) == 0) {
					// I1_2 代休_月初残_日数

			//	}
			}
		}
		cells.get(firstRow, 5).setValue(currentHolidayLeft.getMonthStartRemain());
		if (currentHolidayLeft.getMonthStartRemain() < 0) {
			setForegroundRed(cells.get(firstRow, 5));
		}
		// I1_3 代休_使用数_日数
		cells.get(firstRow, 6).setValue(currentHolidayLeft.getMonthUse());
		if (isOutputItemSubstitute) {
			// I1_4 代休_残数_日数
			cells.get(firstRow, 7).setValue(currentHolidayLeft.getMonthEndRemain());
			if (currentHolidayLeft.getMonthEndRemain() < 0) {
				setForegroundRed(cells.get(firstRow, 7));
			}
		}
		if (isRepresentSubstitute) {
			// I1_5 代休_未消化_日数
			cells.get(firstRow, 8).setValue(currentHolidayLeft.getMonthExtinction());
			if (currentHolidayLeft.getMonthExtinction() != null
					&& currentHolidayLeft.getMonthExtinction() > 0) {
				setForegroundRed(cells.get(firstRow, 8));
			}
		}

		// Set background
		for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
				dataSource.getEndMonth().yearMonth()); i++) {
			if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
				if (isRepresentSubstitute) {
					setBackgroundGray(cells.get(rowIndexRepresentSubstitute, 10 + i));
				}
				if (isRemainingChargeSubstitute) {
					setBackgroundGray(cells.get(rowIndexIsRemainingChargeSubstitute, 10 + i));
				}
			}
			if (!dataSource.isSameCurrentMonth()
					&& dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
				setCurrentMonthBackground(cells.get(firstRow, 10 + i));
				setCurrentMonthBackground(cells.get(firstRow + 1, 10 + i));
				if (isRepresentSubstitute) {
					setCurrentMonthBackground(cells.get(rowIndexRepresentSubstitute, 10 + i));
				}
				if (isRemainingChargeSubstitute) {
					setCurrentMonthBackground(cells.get(rowIndexIsRemainingChargeSubstitute, 10 + i));
				}
			}
		}

		return firstRow + totalRows;
	}

	private int printPauseHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		// 振休
		if (!dataSource.getVariousVacationControl().isPauseItemHolidaySetting()) {
			return firstRow;
		}
		val holiday = dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause();
		boolean isPauseItem = holiday.isPauseItem();
		boolean isUndigestedPause = holiday.isUndigestedPause();
		boolean isNumberRemainingPause = holiday.isNumberRemainingPause();

		if (!isPauseItem) {
			return firstRow;
		}

		val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
		if (hdRemainingInfor == null) {
			return firstRow;
		}

		int totalRows = 2;
		int rowIndexUndigestedPause = 0;
		int rowIndexNumberRemainingPause = 0;
		cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 17, firstRow, 2);
		// J1_1
		cells.get(firstRow, 2).setValue(TextResource.localize("Com_SubstituteHoliday"));
		// J2_1
		cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_16"));
		// J2_2
		cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_17"));

		if (isUndigestedPause) {
			rowIndexUndigestedPause = firstRow + totalRows;
			cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 19, rowIndexUndigestedPause, 1);
			// J2_3
			cells.get(rowIndexUndigestedPause, 9).setValue(TextResource.localize("KDR001_11"));
			totalRows += 1;
		}
		if (isNumberRemainingPause) {
			rowIndexNumberRemainingPause = firstRow + totalRows;
			cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 20, rowIndexNumberRemainingPause, 1);
			// J2_4
			cells.get(rowIndexNumberRemainingPause, 9).setValue(TextResource.localize("KDR001_18"));
			totalRows += 1;
		}

		if (!employee.getCurrentMonth().isPresent()) {
			return firstRow + totalRows;
		}
		YearMonth currentMonth = employee.getCurrentMonth().get();
		// Set value for 振休 J
		// Result RequestList270
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
				cells.get(firstRow, 10 + totalMonth).setValue(statusOfHDItem.getOccurredDay()!= null && statusOfHDItem.getOccurredDay()==0?null:statusOfHDItem.getOccurredDay() );
				// J2_6 振休_使用, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
				cells.get(firstRow + 1, 10 + totalMonth).setValue(statusOfHDItem.getUsedDays()!=null && statusOfHDItem.getUsedDays()==0?null:statusOfHDItem.getUsedDays());
				if (isUndigestedPause) {
					// J2_7 振休_未消化, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
					cells.get(rowIndexUndigestedPause, 10 + totalMonth).setValue(statusOfHDItem.getUnUsedDays()!=null &&statusOfHDItem.getUnUsedDays()==0?null: statusOfHDItem.getUnUsedDays());
					if(statusOfHDItem.getUnUsedDays() !=null && statusOfHDItem.getUnUsedDays() > 0) {
						setForegroundRed(cells.get(rowIndexUndigestedPause, 10 + totalMonth));
					}
				}
				if (isNumberRemainingPause) {
					// J2_8 振休_残数
					cells.get(rowIndexNumberRemainingPause, 10 + totalMonth)
							.setValue(statusOfHDItem.getRemainingDays());
					if (statusOfHDItem.getRemainingDays() < 0) {
						setForegroundRed(cells.get(rowIndexNumberRemainingPause, 10 + totalMonth));
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
						cells.get(firstRow, 10 + totalMonth).setValue(holidayRemainItem.getMonthOccurrence()!=null
								&& holidayRemainItem.getMonthOccurrence()==0?null:holidayRemainItem.getMonthOccurrence());
						// J2_6 振休_使用, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
						cells.get(firstRow + 1, 10 + totalMonth).setValue(holidayRemainItem.getMonthUse()!=null
								&& holidayRemainItem.getMonthUse()==0? null:holidayRemainItem.getMonthUse());
						if (currentMonth.compareTo(holidayRemainItem.getYm()) == 0) {
							if (isUndigestedPause) {
								// J2_7 振休_未消化, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
								cells.get(rowIndexUndigestedPause, 10 + totalMonth).setValue(holidayRemainItem.getMonthExtinction()!=null
										&& holidayRemainItem.getMonthExtinction()==0? null:holidayRemainItem.getMonthExtinction());
								if(holidayRemainItem.getMonthExtinction() != null
										&& holidayRemainItem.getMonthExtinction() > 0) {
									setForegroundRed(cells.get(rowIndexUndigestedPause, 10 + totalMonth));
								}
							}
							if (isNumberRemainingPause) {
								// J2_8 振休_残数
								cells.get(rowIndexNumberRemainingPause, 10 + totalMonth)
										.setValue(holidayRemainItem.getMonthEndRemain());
								if (holidayRemainItem.getMonthEndRemain() < 0) {
									setForegroundRed(cells.get(rowIndexNumberRemainingPause, 10 + totalMonth));
								}
							}
						}
					}
				}

				//}
			}
		}

		val currentHolidayRemainLeft = hdRemainingInfor.getCurrentHolidayRemainLeft();
		// Current month
		//if (currentMonth.compareTo(holidayRemainItem.getYm()) == 0) {
			// J1_2 振休_月初残
			cells.get(firstRow, 5).setValue(currentHolidayRemainLeft.getMonthStartRemain());
			if (currentHolidayRemainLeft.getMonthStartRemain() < 0) {
				setForegroundRed(cells.get(firstRow, 5));
			}
			// J1_3 振休_使用数
			cells.get(firstRow, 6).setValue(currentHolidayRemainLeft.getMonthUse());
			if (isPauseItem) {
				// J1_4 振休_残数
				cells.get(firstRow, 7).setValue(currentHolidayRemainLeft.getMonthEndRemain());
				if (currentHolidayRemainLeft.getMonthEndRemain() < 0) {
					setForegroundRed(cells.get(firstRow, 7));
				}
			}
			if (isUndigestedPause) {
				// J1_5 振休_未消化
				cells.get(firstRow, 8).setValue(currentHolidayRemainLeft.getMonthExtinction());
				if (currentHolidayRemainLeft.getMonthExtinction() != null
						&& currentHolidayRemainLeft.getMonthExtinction() > 0) {
					setForegroundRed(cells.get(firstRow, 8));
				}
			}

		// Set background
		for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
				dataSource.getEndMonth().yearMonth()); i++) {
			if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) > 0) {
				if (isUndigestedPause) {
					setBackgroundGray(cells.get(rowIndexUndigestedPause, 10 + i));
				}
				if (isNumberRemainingPause) {
					setBackgroundGray(cells.get(rowIndexNumberRemainingPause, 10 + i));
				}
			}
			if (!dataSource.isSameCurrentMonth()
					&& dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
				setCurrentMonthBackground(cells.get(firstRow, 10 + i));
				setCurrentMonthBackground(cells.get(firstRow + 1, 10 + i));
				if (isUndigestedPause) {
					setCurrentMonthBackground(cells.get(rowIndexUndigestedPause, 10 + i));
				}
				if (isNumberRemainingPause) {
					setCurrentMonthBackground(cells.get(rowIndexNumberRemainingPause, 10 + i));
				}
			}
		}

		return firstRow + totalRows;
	}

	private int printSpecialHoliday(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		// 特別休暇

		val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
		if (hdRemainingInfor == null) {
			return firstRow;
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

			cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 21, firstRow, 2);
			// M1_1 特別休暇
			cells.get(firstRow, 2).setValue(specialHolidayOpt.get().getSpecialHolidayName().v());
			// M2_1
			cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_17"));
			// M2_3
			cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_18"));

			if (employee.getCurrentMonth().isPresent()) {
				YearMonth currentMonth = employee.getCurrentMonth().get();

				// Result RequestList273
				val specialVacationImported = hdRemainingInfor.getMapSpecialVacation().get(specialHolidayCode);
				// Result RequestList263
				val specialHolidayList = hdRemainingInfor.getMapListSpecialHoliday().get(specialHolidayCode);

				// ドメインモデル「特別休暇付与残数データ」を取得
				List<SpecialLeaveGrantRemainingData> listSpecialLeaveGrant = specialLeaveGrantRepository
						.getAllByExpStatus(employee.getEmployeeId(), specialHolidayCode,
								LeaveExpirationStatus.AVAILABLE.value);

				// 全てのドメインモデル「特別休暇付与残数データ」の残時間を合計
				Double dayNumbers = listSpecialLeaveGrant.stream()
//						.mapToDouble(item -> item.getDetails().getRemainingNumber().getDayNumberOfRemain().v()).sum();
						.mapToDouble(item -> item.getDetails().getRemainingNumber().getMinutes().isPresent()?
								item.getDetails().getRemainingNumber().getMinutes().get().v() : 0).sum();

				if (specialVacationImported != null) {
					// M1_2 特別休暇１_付与数日数
					cells.get(firstRow, 4).setValue(specialVacationImported.getGrantDays());
					// M1_3 特別休暇１_月初残日数
					cells.get(firstRow, 5).setValue(dayNumbers);
					if (dayNumbers < 0) {
						setForegroundRed(cells.get(firstRow, 5));
					}
					// M1_4 特別休暇１_使用数日数
					cells.get(firstRow, 6).setValue(specialVacationImported.getUsedDate());
					// M1_5 特別休暇１_残数日数
					cells.get(firstRow, 7).setValue(specialVacationImported.getRemainDate());
					if (specialVacationImported.getRemainDate() < 0) {
						setForegroundRed(cells.get(firstRow, 7));
					}
				}

				if (specialHolidayList != null) {
					for (SpecialHolidayImported item : specialHolidayList) {

						// Before this month
						if (currentMonth.compareTo(item.getYm()) > 0) {
							int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), item.getYm());
							if (maxRange >= totalMonth && totalMonth >= 0) {
								// M2_5 特別休暇１_使用日数, set lại giá trị cho cột này nếu bằng 0 thì không hiển thị ra
								cells.get(firstRow, 10 + totalMonth).setValue(item.getUseDays()!=null && item.getUseDays()==0? null:item.getUseDays());
								// M2_7 特別休暇１_残数日数
								cells.get(firstRow + 1, 10 + totalMonth).setValue(item.getRemainDays());
								if (item.getRemainDays() < 0) {
									setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
								}
							}
						}
					}
				}

                // Result RequestList273
				int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
				List<YearMonth> lstYm = new ArrayList<>();
//				int exportMonth = totalMonths(currentMonth, dataSource.getEndMonth().yearMonth());
				for(YearMonth i =currentMonth; i.lessThanOrEqualTo(dataSource.getEndMonth().yearMonth()); i = i.addMonths(1)){
					lstYm.add(i);
				}
				Collections.sort(lstYm);

				// update ver14
				// M2_7 特別休暇１_残数日数
				if(!lstYm.isEmpty()){
					SpecialVacationImported spVaCrurrentMonthImported0 = hdRemainingInfor.getMapSPVaCrurrentMonth().get(lstYm.get(0)) != null ?
							hdRemainingInfor.getMapSPVaCrurrentMonth().get(lstYm.get(0)).get(specialHolidayCode) : null;
					if(spVaCrurrentMonthImported0 != null){
						if(totalMonth <= maxRange && totalMonth >= 0) {
							cells.get(firstRow + 1, 10 + totalMonth).setValue(spVaCrurrentMonthImported0.getRemainDate());
						}
					}
				}

				for(YearMonth ym : lstYm){
					SpecialVacationImported spVaCrurrentMonthImported = hdRemainingInfor.getMapSPVaCrurrentMonth().get(ym) != null ?
							hdRemainingInfor.getMapSPVaCrurrentMonth().get(ym).get(specialHolidayCode) : null;
					if (spVaCrurrentMonthImported != null) {
						if (totalMonth <= maxRange && totalMonth >= 0) {
							// M2_5 特別休暇１_使用日数
							cells.get(firstRow, 10 + totalMonth).setValue(spVaCrurrentMonthImported.getUsedDate()!=null
									&& spVaCrurrentMonthImported.getUsedDate()==0?null:spVaCrurrentMonthImported.getUsedDate());
							// M2_7 特別休暇１_残数日数
//							cells.get(firstRow + 1, 10 + totalMonth).setValue(spVaCrurrentMonthImported.getRemainDate());
							if (spVaCrurrentMonthImported.getRemainDate() < 0) {
								setForegroundRed(cells.get(firstRow + 1, 10 + totalMonth));
							}
						}
					}
					totalMonth++;
				}

				// Set background
				for (int index = 0; index <= totalMonths(dataSource.getStartMonth().yearMonth(),
						dataSource.getEndMonth().yearMonth()); index++) {
					if (dataSource.getStartMonth().addMonths(index).yearMonth().compareTo(currentMonth) > 0) {
						setBackgroundGray(cells.get(firstRow + 1, 10 + index));
//						setBackgroundGray(cells.get(firstRow, 10 + index));
//						setBackgroundGray(cells.get(firstRow + 1, 10 + index));
					}

					if (!dataSource.isSameCurrentMonth()
							&& dataSource.getStartMonth().addMonths(index).yearMonth().compareTo(currentMonth) == 0) {
						setCurrentMonthBackground(cells.get(firstRow, 10 + index));
						setCurrentMonthBackground(cells.get(firstRow + 1, 10 + index));
					}
				}
			}

			firstRow += 2;
		}

		return firstRow;
	}

	private int printChildNursingVacation(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		// 子の看護休暇
		if (!dataSource.getVariousVacationControl().isChildNursingSetting()) {
			return firstRow;
		}
		if (!dataSource.getHolidaysRemainingManagement().getListItemsOutput().getChildNursingVacation()
				.isChildNursingLeave()) {
			return firstRow;
		}
		val hdRemainingInfor = dataSource.getMapEmployees().get(employee.getEmployeeId()).getHolidayRemainingInfor();
		if (hdRemainingInfor == null) {
			return firstRow;
		}
		cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 23, firstRow, 2);
		// N1_1
		cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_47"));
		// N2_1
		cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_9"));
		// N2_2
		cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_18"));

		if (!employee.getCurrentMonth().isPresent()) {
			return firstRow + 2;
		}
		YearMonth currentMonth = employee.getCurrentMonth().get();

		// Result RequestList206
		val currentSituationImported = hdRemainingInfor.getChildNursingLeave();
		if (currentSituationImported != null) {
			// N1_2 子の看護休暇_使用数
			cells.get(firstRow, 6).setValue(currentSituationImported.getNumberOfUse());
			// N1_3 子の看護休暇_残数
			cells.get(firstRow, 7).setValue(currentSituationImported.getRemainingDays());
			if (currentSituationImported.getRemainingDays().startsWith("-")) {
				setForegroundRed(cells.get(firstRow, 7));
			}

			// Position of current month
			int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
			if (currentMonth.compareTo(dataSource.getEndMonth().yearMonth()) <= 0 && totalMonth >= 0) {
				// N2_3 子の看護休暇_使用日数 当月
				cells.get(firstRow, 10 + totalMonth).setValue(currentSituationImported.getNumberOfUse());

				// N2_4 子の看護休暇_残日数
				cells.get(firstRow + 1, 10 + totalMonth).setValue(currentSituationImported.getRemainingDays());
				if (currentSituationImported.getRemainingDays().startsWith("-")) {
					setForegroundRed(cells.get(firstRow + 1, 10));
				}
			}
		}
		// Set background
		for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
				dataSource.getEndMonth().yearMonth()); i++) {
			if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) != 0) {
				setBackgroundGray(cells.get(firstRow, 10 + i));
				setBackgroundGray(cells.get(firstRow + 1, 10 + i));
			}
			if (!dataSource.isSameCurrentMonth()
					&& dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
				setCurrentMonthBackground(cells.get(firstRow, 10 + i));
				setCurrentMonthBackground(cells.get(firstRow + 1, 10 + i));
			}
		}

		return firstRow + 2;
	}

	private int printNursingCareLeave(Cells cells, int firstRow, HolidaysRemainingEmployee employee,
			HolidayRemainingDataSource dataSource) throws Exception {
		// 介護休暇
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

		cells.copyRows(cells, NUMBER_ROW_OF_HEADER + 25, firstRow, 2);
		// O1_1
		cells.get(firstRow, 2).setValue(TextResource.localize("KDR001_48"));
		// O2_1
		cells.get(firstRow, 9).setValue(TextResource.localize("KDR001_9"));
		// O2_2
		cells.get(firstRow + 1, 9).setValue(TextResource.localize("KDR001_18"));

		if (!employee.getCurrentMonth().isPresent()) {
			return firstRow + 2;
		}
		YearMonth currentMonth = employee.getCurrentMonth().get();

		// Result RequestList207
		val currentSituationImported = hdRemainingInfor.getNursingLeave();

		if (currentSituationImported != null) {
			// O1_2 介護休暇_使用数
			cells.get(firstRow, 6).setValue(currentSituationImported.getNumberOfUse());
			// O1_3 介護休暇_残数
			cells.get(firstRow, 7).setValue(currentSituationImported.getRemainingDays());
			if (currentSituationImported.getRemainingDays().startsWith("-")) {
				setForegroundRed(cells.get(firstRow, 7));
			}

			// Position of current month
			int totalMonth = totalMonths(dataSource.getStartMonth().yearMonth(), currentMonth);
			if (currentMonth.compareTo(dataSource.getEndMonth().yearMonth()) <= 0 && totalMonth >= 0) {
				// O2_3 介護休暇_使用日数 当月
				cells.get(firstRow, 10 + totalMonth).setValue(currentSituationImported.getNumberOfUse());

				// O2_4 介護休暇_残日数
				cells.get(firstRow + 1, 10 + totalMonth).setValue(currentSituationImported.getRemainingDays());
				if (currentSituationImported.getRemainingDays().startsWith("-")) {
					setForegroundRed(cells.get(firstRow + 1, 10));
				}
			}
		}

		// Set background
		for (int i = 0; i <= totalMonths(dataSource.getStartMonth().yearMonth(),
				dataSource.getEndMonth().yearMonth()); i++) {
			if (dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) != 0) {
				setBackgroundGray(cells.get(firstRow, 10 + i));
				setBackgroundGray(cells.get(firstRow + 1, 10 + i));
			}
			if (!dataSource.isSameCurrentMonth()
					&& dataSource.getStartMonth().addMonths(i).yearMonth().compareTo(currentMonth) == 0) {
				setCurrentMonthBackground(cells.get(firstRow, 10 + i));
				setCurrentMonthBackground(cells.get(firstRow + 1, 10 + i));
			}
		}

		return firstRow + 2;
	}

	private int countRowEachPerson(HolidayRemainingDataSource dataSource) {
		int totalRowDetails = 0;
		// 年休
		if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getAnnualHoliday().isYearlyHoliday()) {
			totalRowDetails += 2;
		}
		// 積立年休
		if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getYearlyReserved().isYearlyReserved()) {
			totalRowDetails += 2;
		}
		// 代休
		if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getSubstituteHoliday()
				.isOutputItemSubstitute()) {
			totalRowDetails += 2;

			if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getSubstituteHoliday()
					.isRemainingChargeSubstitute()) {
				totalRowDetails += 1;
			}
			if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getSubstituteHoliday()
					.isRepresentSubstitute()) {
				totalRowDetails += 1;
			}
		}
		// 振休
		if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause().isPauseItem()) {
			totalRowDetails += 2;
			if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause().isNumberRemainingPause()) {
				totalRowDetails += 1;
			}
			if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getPause().isUndigestedPause()) {
				totalRowDetails += 1;
			}
		}
		// 特別休暇
		List<Integer> specialHoliday = dataSource.getHolidaysRemainingManagement().getListItemsOutput()
				.getSpecialHoliday();
		for (int i = 0; i < specialHoliday.size(); i++) {
			totalRowDetails += 2;
		}
		// 子の看護休暇
		if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getChildNursingVacation()
				.isChildNursingLeave()) {
			totalRowDetails += 2;
		}
		// 介護休暇
		if (dataSource.getHolidaysRemainingManagement().getListItemsOutput().getNursingcareLeave().isNursingLeave()) {
			totalRowDetails += 2;
		}

		return totalRowDetails;
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

	private int totalMonths(YearMonth start, YearMonth end) {
		return (end.year() - start.year()) * TOTAL_MONTH_IN_YEAR + (end.month() - start.month());
	}

	/**
	 * PRINT PAGE HEADER
	 *
	 * @param worksheet
	 * @param lstDeparmentInf
	 */
	private void printHeader(Worksheet worksheet, HolidayRemainingDataSource dataSource) {
		// Set print page
		PageSetup pageSetup = worksheet.getPageSetup();
		pageSetup.setFirstPageNumber(1);
		pageSetup.setPrintArea("A1:N");
		//ý 1 của bug #102883  事象(1)
		pageSetup.setHeader(0, "&9&\"MS ゴシック\""+ dataSource.getCompanyName());
		pageSetup.setHeader(1, "&16&\"MS ゴシック\""+ dataSource.getTitle());
		DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm", Locale.JAPAN);
		pageSetup.setHeader(2, "&9&\"MS ゴシック\"" + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage &P");
	}
}
