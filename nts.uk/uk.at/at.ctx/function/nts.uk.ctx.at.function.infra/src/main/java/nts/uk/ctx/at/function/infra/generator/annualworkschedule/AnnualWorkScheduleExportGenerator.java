package nts.uk.ctx.at.function.infra.generator.annualworkschedule;

import java.util.List;

import javax.ejb.Stateless;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Color;
import com.aspose.cells.HorizontalPageBreakCollection;
import com.aspose.cells.Range;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.uk.ctx.at.function.dom.annualworkschedule.export.AnnualWorkScheduleData;
import nts.uk.ctx.at.function.dom.annualworkschedule.export.AnnualWorkScheduleGenerator;
import nts.uk.ctx.at.function.dom.annualworkschedule.export.EmployeeData;
import nts.uk.ctx.at.function.dom.annualworkschedule.export.ExportData;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

@Stateless
public class AnnualWorkScheduleExportGenerator extends AsposeCellsReportGenerator implements AnnualWorkScheduleGenerator {

	private static final String TEMPLATE_FILE = "report/年間勤務表（1ヶ月）.xlsx";

	private static final String REPORT_FILE_NAME = "年間勤務表.xlsx";

	private static final int MAX_EXPORT_ITEM = 10;
	private static final int ROW_PER_PAGE = 27;

	@Override
	public void generate(FileGeneratorContext fileContext, ExportData dataSource) {
		try (AsposeCellsReportContext reportContext = this.createContext(TEMPLATE_FILE)) {
			Workbook wb = reportContext.getWorkbook();
			WorksheetCollection wsc = wb.getWorksheets();
			Worksheet ws = wsc.get(0);
			reportContext.setHeader(0, dataSource.getHeader().getTitle());
			Range empRange = wsc.getRangeByName("employeeRange");
			int rowPerEmp = dataSource.getExportItems().size();
			int empPerPage = ROW_PER_PAGE/rowPerEmp;
			//delete superfluous rows
			ws.getCells().deleteRows(empRange.getFirstRow() + empRange.getRowCount() //last row
									- (MAX_EXPORT_ITEM - rowPerEmp),
									MAX_EXPORT_ITEM - rowPerEmp);
			//refresh range after delete superfluous rows
			empRange = wsc.getRangeByName("employeeRange");
			Range workplaceRange = wsc.getRangeByName("workplaceRange");
			//set border bottom after delete superfluous rows
			empRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			//print header
			this.printHeader(wsc);

			HorizontalPageBreakCollection pageBreaks = ws.getHorizontalPageBreaks();
			List<EmployeeData> emps = dataSource.getEmployees();
			//set first employee
			EmployeeData firstEmp = emps.remove(0);
			print(new RangeCustom(empRange, 0), firstEmp, true);

			String workplace = firstEmp.getEmployeeInfo().get(0);
			RangeCustom newRange = new RangeCustom(empRange, 0);
			int offset = 0, countEmpPerPage = 1;
			for (EmployeeData emp : emps) {
				if (!workplace.equals(emp.getEmployeeInfo().get(0)) ||
					countEmpPerPage == empPerPage) {
					workplace = emp.getEmployeeInfo().get(0);
					newRange = copyRangeDown(workplaceRange, offset);
					pageBreaks.add(newRange.range.getFirstRow());
					countEmpPerPage = 1; //reset count
				} else {
					newRange = copyRangeDown(empRange, offset);
					countEmpPerPage ++;
				}
				print(newRange, emp, countEmpPerPage == 1);
				offset = newRange.offset;
			}

			reportContext.processDesigner();
			reportContext.saveAsExcel(this.createNewFile(fileContext, this.getReportName(REPORT_FILE_NAME)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * TODO
	 * @param wsc Work sheet collection
	 */
	private void printHeader(WorksheetCollection wsc) {
		// print C1_2
		wsc.getRangeByName("month1stLabel").setValue("4");
		wsc.getRangeByName("month2ndLabel").setValue("5");
		wsc.getRangeByName("month3rdLabel").setValue("6");
		wsc.getRangeByName("month4thLabel").setValue("7");
		wsc.getRangeByName("month5thLabel").setValue("8");
		wsc.getRangeByName("month6thLabel").setValue("9");
		wsc.getRangeByName("month7thLabel").setValue("10");
		wsc.getRangeByName("month8thLabel").setValue("11");
		wsc.getRangeByName("month9thLabel").setValue("12");
		wsc.getRangeByName("month10thLabel").setValue("1");
		wsc.getRangeByName("month11thLabel").setValue("2");
		wsc.getRangeByName("month12thLabel").setValue("3");
		
	}

	/**
	 * TODO print data
	 * @param range
	 * @param emp
	 * @param isNewPage
	 */
	private void print(RangeCustom range, EmployeeData emp, boolean isNewPage) {
		if(isNewPage) {
			String workplace = emp.getEmployeeInfo().get(0);
			range.cell("workplace").putValue(workplace);
		}
		int rowOffset = 0;
		for (AnnualWorkScheduleData data: emp.getAnnualWorkSchedule()) {
			range.cell("item", rowOffset, 0).putValue(data.getName());
			range.cell("month1st", rowOffset, 0).putValue("10:00");
			range.cell("month1st", rowOffset, 0).putValue("10:00");
			range.cell("month2nd", rowOffset, 0).putValue("20:00");
			range.cell("month3rd", rowOffset, 0).putValue("30:00");
			rowOffset++;
		}
	}

	private RangeCustom copyRangeDown(Range range, int extra) throws Exception {
		Range newRange = range.getWorksheet().getCells().createRange(range.getFirstRow() + range.getRowCount() + extra,
				range.getFirstColumn(), range.getRowCount(), range.getColumnCount());
		newRange.copyStyle(range);
		int offset = newRange.getFirstRow() - range.getFirstRow();
		return new RangeCustom(newRange, offset);
	}

	private RangeCustom copyRangeDown(Range range) throws Exception {
		return copyRangeDown(range, 0);
	}
}

class RangeCustom {
	int index;
	final int offset;
	Range range;
	Worksheet worksheet;
	WorksheetCollection worksheets;
	public RangeCustom(Range range, int offset) {
		this.offset = offset;
		this.range = range;
		this.worksheet = range.getWorksheet();
		this.worksheets = this.worksheet.getWorkbook().getWorksheets();
	}
	public RangeCustom(Range range, int offset, int index) {
		this.index = index;
		this.offset = offset;
		this.range = range;
		this.worksheet = range.getWorksheet();
		this.worksheets = this.worksheet.getWorkbook().getWorksheets();
	}

	public Cell cell(String name) {
		Cell orginCell = worksheets.getRangeByName(name).getCellOrNull(0, 0);
		return worksheet.getCells().get(orginCell.getRow() + offset, orginCell.getColumn());
	}

	public Cell cell(String name, int rowOffset, int columnOffset) {
		Cell orginCell = worksheets.getRangeByName(name).getCellOrNull(0, 0);
		return worksheet.getCells().get(orginCell.getRow() + offset + rowOffset, orginCell.getColumn() + columnOffset);
	}

	public Cell firstCell() {
		return range.getCellOrNull(0, 0);
	}
}