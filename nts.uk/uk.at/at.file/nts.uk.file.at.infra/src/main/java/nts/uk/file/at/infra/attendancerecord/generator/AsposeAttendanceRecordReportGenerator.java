package nts.uk.file.at.infra.attendancerecord.generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.HorizontalPageBreakCollection;
import com.aspose.cells.PageOrientationType;
import com.aspose.cells.PageSetup;
import com.aspose.cells.PaperSizeType;
import com.aspose.cells.PasteOptions;
import com.aspose.cells.PasteType;
import com.aspose.cells.Range;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.VerticalPageBreakCollection;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import lombok.val;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ExportFontSize;
import nts.uk.file.at.app.export.attendancerecord.AttendanceRecordReportDatasource;
import nts.uk.file.at.app.export.attendancerecord.AttendanceRecordReportGenerator;
import nts.uk.file.at.app.export.attendancerecord.TempAbsenceData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportColumnData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportDailyData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportEmployeeData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportWeeklyData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportWeeklySumaryData;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

/**
 * The Class AsposeAttendanceRecordReportGenerator.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AsposeAttendanceRecordReportGenerator extends AsposeCellsReportGenerator
		implements AttendanceRecordReportGenerator {

	/** The Constant TEMPLATE_FILE. */
	private static final String TEMPLATE_FILE = "report/KWR002.xlsx";
	
	/** The Constant TEMPLATE_FILE_MEDIUM. */
	private static final String TEMPLATE_FILE_MEDIUM = "report/KWR002_FM.xlsx";
	
	/** The Constant TEMPLATE_FILE_MEDIUM. */
	private static final String TEMPLATE_FILE_SMALL = "report/KWR002_FS.xlsx";

	/** The Constant PDF_EXT. */
	private static final String PDF_EXT = ".pdf";

	/** The Constant EXCEL_EXT. */
	private static final String EXCEL_EXT = ".xlsx";

	private static final String REPORT_START_PAGE_ROW = "REPORT_START_PAGE_ROW";

	/** The Constant REPORT_LEFT_ROW. */
	private static final String REPORT_LEFT_ROW = "REPORT_LEFT_ROW";

	/** The Constant REPORT_RIGHT_ROW. */
	private static final String REPORT_RIGHT_ROW = "REPORT_RIGHT_ROW";

	/** The Constant REPORT_ROW_BG. */
	private static final String REPORT_ROW_BG = "REPORT_ROW_BG";

	/** The Constant REPORT_ROW_START_RIGHT. */
	private static final String REPORT_ROW_START_RIGHT = "REPORT_ROW_START_RIGHT";

	/** The Constant REPORT_PAGE_ADDR. */
	private static final String REPORT_PAGE_ADDR = "A1:AO";
	
	/** The Constant REPORT_PAGE_ADDR_FM. */
	private static final String REPORT_PAGE_ADDR_FM = "A1:AW";
	
	/** The Constant REPORT_PAGE_ADDR_FS. */
	private static final String REPORT_PAGE_ADDR_FS = "A1:BE";

	/** The Constant MONTHLY_DATA_ADDR. */
	private static final String MONTHLY_DATA_ADDR = "C%d:Z%d";
	
	/** The Constant MONTHLY_DATA_ADDR_FM. */
	private static final String MONTHLY_DATA_ADDR_FM = "C%d:AD%d";
	
	/** The Constant MONTHLY_DATA_ADDR_FS. */
	private static final String MONTHLY_DATA_ADDR_FS = "C%d:AH%d";

	/** The Constant DAILY_W_RANGE_TMPL_ADDR. */
	private static final String DAILY_W_RANGE_TMPL_ADDR = "AQ1:BJ2";
	
	/** The Constant DAILY_W_RANGE_TMPL_ADDR_FM. */
	private static final String DAILY_W_RANGE_TMPL_ADDR_FM = "AY1:BV2";
	
	/** The Constant DAILY_W_RANGE_TMPL_ADDR_FS. */
	private static final String DAILY_W_RANGE_TMPL_ADDR_FS = "BG1:CH2";

	/** The Constant DAILY_B_RANGE_TMPL_ADDR. */
	private static final String DAILY_B_RANGE_TMPL_ADDR = "AQ4:BJ5";
	
	/** The Constant DAILY_B_RANGE_TMPL_ADDR. */
	private static final String DAILY_B_RANGE_TMPL_ADDR_FM = "AY4:BV5";
	
	/** The Constant DAILY_B_RANGE_TMPL_ADDR. */
	private static final String DAILY_B_RANGE_TMPL_ADDR_FS = "BG4:CH5";

	/** The Constant WEEKLY_RANGE_TMPL_ADDR. */
	private static final String WEEKLY_RANGE_TMPL_ADDR = "AQ11:BJ12";
	
	/** The Constant WEEKLY_RANGE_TMPL_ADDR. */
	private static final String WEEKLY_RANGE_TMPL_ADDR_FM = "AY11:BV12";
	
	/** The Constant WEEKLY_RANGE_TMPL_ADDR. */
	private static final String WEEKLY_RANGE_TMPL_ADDR_FS = "BG11:CH12";

	/** The Constant SEAL_RANGE_TMPL_ADDR. */
	private static final String SEAL_RANGE_TMPL_ADDR = "AQ14:AR17";
	
	/** The Constant SEAL_RANGE_TMPL_ADDR_FM. */
	private static final String SEAL_RANGE_TMPL_ADDR_FM = "AY14:AZ17";
	
	/** The Constant SEAL_RANGE_TMPL_ADDR_FS. */
	private static final String SEAL_RANGE_TMPL_ADDR_FS = "BG14:BH17";

	/** The Constant SEAL_COL_ADDR. */
	private static final List<String> SEAL_COL_ADDR = Arrays
			.asList(new String[] { "AN1", "AL1", "AJ1", "AH1", "AF1", "AD1" });
	
	/** The Constant SEAL_COL_ADDR_FM. */
	private static final List<String> SEAL_COL_ADDR_FM = Arrays
			.asList(new String[] { "AV1", "AT1", "AR1", "AP1", "AN1", "AL1" });
	
	/** The Constant SEAL_COL_ADDR_FS. */
	private static final List<String> SEAL_COL_ADDR_FS = Arrays
			.asList(new String[] { "BD1", "BB1", "AZ1", "AX1", "AV1", "AT1" });
	
	/** The Constant END_REPORT_COL2. */
	private static final String END_REPORT_PAGE_BREAK = "AP";
	
	/** The Constant END_REPORT_PAGE_BREAK_FM. */
	private static final String END_REPORT_PAGE_BREAK_FM = "AX";
	
	/** The Constant END_REPORT_PAGE_BREAK_FS. */
	private static final String END_REPORT_PAGE_BREAK_FS = "BF";
	
	// B8_1~B8_33
	private static final String REPORT_HEADER_INFO_ADDR = "A%d:AB%d";
	private static final String REPORT_HEADER_INFO_ADDR_FM = "A%d:AJ%d";
	private static final String REPORT_HEADER_INFO_ADDR_FS = "A%d:AL%d";

	/** The Constant REPORT_LEFT_COL_ADDR. font size large */
	private static final String REPORT_LEFT_COL_ADDR = "A%d:T%d";
	
	/** The Constant REPORT_LEFT_COL_ADDR_FM. font size medium */
	private static final String REPORT_LEFT_COL_ADDR_FM = "A%d:X%d";
	
	/** The Constant REPORT_LEFT_COL_ADDR_FS. font size small */
	private static final String REPORT_LEFT_COL_ADDR_FS = "A%d:AB%d";

	/** The Constant REPORT_RIGHT_COL_ADDR. */
	private static final String REPORT_RIGHT_COL_ADDR = "V%d:AO%d";
	
	/** The Constant REPORT_RIGHT_COL_ADDR_FM. */
	private static final String REPORT_RIGHT_COL_ADDR_FM = "Z%d:AW%d";
	
	/** The Constant REPORT_RIGHT_COL_ADDR_FS. */
	private static final String REPORT_RIGHT_COL_ADDR_FS = "AD%d:BE%d";

	/** The Constant START_EMPLOYEE_DATA_ROW. */
	private static final int START_EMPLOYEE_DATA_ROW = 1;
	
	/** The Constant START_EMPLOYEE_CENTER_DATA_ROW. */
	private static final int START_EMPLOYEE_CENTER_DATA_ROW = 2;
	
	/** The Constant START_EMPLOYEE_BOTTOM_DATA_ROW. */
	private static final int START_EMPLOYEE_BOTTOM_DATA_ROW = 3;

	/** The Constant START_REPORT_DATA_ROW. in ra data .... */ 
	private static final int START_REPORT_DATA_ROW = 11;

	/** The Constant MAX_ROW_PER_EMPL. */
	private static final int MAX_ROW_PER_EMPL = 50;

	/** The Constant EMPL_INVIDUAL_INDEX. */
	private static final int EMPL_INVIDUAL_INDEX = 0;

	/** The Constant EMPL_WORKPLACE_INDEX. */
	private static final int EMPL_WORKPLACE_INDEX = 10;

	/** The Constant EMPL_EMPLOYMENT_INDEX. */
	private static final int EMPL_EMPLOYMENT_INDEX = 0;

	/** The Constant EMPL_TITLE_INDEX. */
	private static final int EMPL_TITLE_INDEX = 10;

	/** The Constant EMPL_WORKTYPE_INDEX. */
	private static final int EMPL_WORKTYPE_INDEX = 19;

	/** The Constant EMPL_YEARMONTH_INDEX. */
	private static final int EMPL_YEARMONTH_INDEX = 0;

	/** The Constant MONTHLY_DATA_START_ROW. */
	private static final int MONTHLY_DATA_START_ROW = 6;
	
	/** The Constant REPORT_ROW_BG_WHITE. */
	private static final int REPORT_ROW_BG_WHITE = 1;

	/** The Constant REPORT_ROW_BG_BLUE. */
	private static final int REPORT_ROW_BG_BLUE = 2;

	/** The Constant REPORT_ROW_START_RIGHT_COUNT. */
	private static final int REPORT_ROW_START_RIGHT_COUNT = 1;
	
	private static final int BLANK_ROW_INDEX = 7;
	private static final int FIRST_DATA_ROW_INDEX = 10;

	/** The Constant EXPORT_EXCEL. */
	private static final int EXPORT_EXCEL = 2;

	/** The Constant EXPORT_PDF. */
	private static final int EXPORT_PDF = 1;
	
	/** The Constant FONT_SIZE. */
	private static final int FONT_SIZE = 16;
	
	/** The Constant FONT_SIZE_FM. */
	private static final int FONT_SIZE_FM = 14;
	
	/** The Constant FONT_SIZE_FS. */
	private static final int FONT_SIZE_FS = 12;
	
	/** The report approval */
	private static final String REPORT_APPROVAL = "AB7:AC7";
	
	/** The report REPORT_APPROVAL_FM */
	private static final String REPORT_APPROVAL_FM = "AF7:AG7";
	
	/** The report REPORT_APPROVAL_FS */
	private static final String REPORT_APPROVAL_FS = "AJ7:AK7";
	
	/** The report approval copy*/
	private static final String RANGE_APPROVAL_COPY = "AB%d:AC%d";
	
	/** The report RANGE_APPROVAL_COPY_FM*/
	private static final String RANGE_APPROVAL_COPY_FM = "AF%d:AG%d";
	
	/** The report RANGE_APPROVAL_COPY_FSy*/
	private static final String RANGE_APPROVAL_COPY_FS = "AJ%d:AK%d";
	
	/** The report approval start row */
	private static final int APPROVAL_START_ROW = 7;
	
	/** The report approval */
	private static final String APPROVAL = "確認済";
		
	/** The report monthly title start row */
	private static final int MONTHLY_TITLE_START_ROW = 4;
	
	/** The report monthly title fix */
	private static final String MONTHLY_TITLE_FIX = "C4:Z5";
	
	/** The report MONTHLY_TITLE_FIX_FM */
	private static final String MONTHLY_TITLE_FIX_FM = "C4:AD5";
	
	/** The report MONTHLY_TITLE_FIX_FS */
	private static final String MONTHLY_TITLE_FIX_FS = "C4:AH5";
	
	/** The report monthly content fix */
	private static final String MONTHLY_CONTENT_FIX = "C6:Z7";
	
	/** The report MONTHLY_CONTENT_FIX_FM */
	private static final String MONTHLY_CONTENT_FIX_FM = "C6:AD7";
	
	/** The report MONTHLY_CONTENT_FIX_FS */
	private static final String MONTHLY_CONTENT_FIX_FS = "C6:AH7";

	/** The report monthly cumulative total fix */
	private static final String MONTHLY_CUMULATIVE_TOTAL_FIX = "A4:B7";
	
	/** The report report cumulative fix */
	private static final String REPORT_CUMULATIVE_FIX = "A%d:B%d";
	
	/** The report daily title fix left */
	private static final String DAILY_TITLE_FIX_LEFT = "A9:T10";
	
	/** The report DAILY_TITLE_FIX_LEFT_FM */
	private static final String DAILY_TITLE_FIX_LEFT_FM = "A9:X10";
	
	/** The report DAILY_TITLE_FIX_LEFT_FS */
	private static final String DAILY_TITLE_FIX_LEFT_FS = "A9:AB10";
	
	/** The report daily title fix right */
	private static final String DAILY_TITLE_FIX_RIGHT = "V9:AO10";
	
	/** The report DAILY_TITLE_FIX_RIGHT_FM */
	private static final String DAILY_TITLE_FIX_RIGHT_FM = "Z9:AW10";
	
	/** The report DAILY_TITLE_FIX_RIGHT_FS */
	private static final String DAILY_TITLE_FIX_RIGHT_FS = "AD9:BE10";
	
	/** The report daily title start row */
	private static final int DAILY_TITLE_START_ROW = 9;
	
	/** The report seal range copy */
	private static final String SEAL_RANGE_COPY_FIX = "AD1:AO4";
	
	/** The report SEAL_RANGE_COPY_FIX_FM */
	private static final String SEAL_RANGE_COPY_FIX_FM = "AL1:AW4";
	
	/** The report SEAL_RANGE_COPY_FIX_FS */
	private static final String SEAL_RANGE_COPY_FIX_FS = "AT1:BE4";
	
	/** The report seal range copy */
	private static final String SEAL_RANGE_COPY = "AD%d:AO%d";

	/** The report SEAL_RANGE_COPY_FM */
	private static final String SEAL_RANGE_COPY_FM = "AL%d:AW%d";
	
	/** The report SEAL_RANGE_COPY_FS */
	private static final String SEAL_RANGE_COPY_FS = "AT%d:BE%d";
	
	// Merge cells B8_1~B8_33
	// Define columns count of each merge columns
	// which row to start from will be passed as variable later
	// will always merge 1 row at a time
	private static final ColumnsToMerge[] REPORT_HEADER_INFO_MERGE_COLS = new ColumnsToMerge[] {
			new ColumnsToMerge(10, 18), 
			new ColumnsToMerge(10, 9, 9), 
			new ColumnsToMerge(28) };

	private static final ColumnsToMerge[] REPORT_HEADER_INFO_MERGE_COLS_FM = new ColumnsToMerge[] {
			new ColumnsToMerge(10, 26), 
			new ColumnsToMerge(10, 9, 17), 
			new ColumnsToMerge(36) };

	private static final ColumnsToMerge[] REPORT_HEADER_INFO_MERGE_COLS_FS = new ColumnsToMerge[] {
			new ColumnsToMerge(10, 28), 
			new ColumnsToMerge(10, 9, 19), 
			new ColumnsToMerge(38) };
	
	private static final int SIDE_HEADER_FONT_SIZE = 9;
	
	private static final int SIDE_HEADER_FONT_SIZE_FM = 8;
	
	private static final int SIDE_HEADER_FONT_SIZE_FS = 7;
	
	private static final int MAXIMUM_ABSENCE_PERIOD = 3;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nts.uk.file.at.app.export.attendancerecord.AttendanceRecordReportGenerator#
	 * generate(nts.arc.layer.infra.file.export.FileGeneratorContext,
	 * nts.uk.file.at.app.export.attendancerecord.AttendanceRecordReportDatasource)
	 */
	@Override
	public void generate(FileGeneratorContext generatorContext, AttendanceRecordReportDatasource dataSource) {

		AttendanceRecordReportData data = dataSource.getData();
		String templateFile = TEMPLATE_FILE;
		int fontSize = FONT_SIZE;
		int sideHeaderFontSize = SIDE_HEADER_FONT_SIZE;
		String reportPageAddr = REPORT_PAGE_ADDR;
		String dailyWeekRangeTmpAddr =  DAILY_W_RANGE_TMPL_ADDR;
		String dailyBRangeTmpAddr = DAILY_B_RANGE_TMPL_ADDR;
		String weeklyRangeTmpAddr = WEEKLY_RANGE_TMPL_ADDR;

		if (data.getFontSize() == ExportFontSize.CHAR_SIZE_MEDIUM.value) {
			templateFile = TEMPLATE_FILE_MEDIUM;
			
			reportPageAddr = REPORT_PAGE_ADDR_FM ;
			
			dailyWeekRangeTmpAddr = DAILY_W_RANGE_TMPL_ADDR_FM;
			
			dailyBRangeTmpAddr = DAILY_B_RANGE_TMPL_ADDR_FM;
			
			weeklyRangeTmpAddr = WEEKLY_RANGE_TMPL_ADDR_FM;
			
			fontSize = FONT_SIZE_FM;
			
			sideHeaderFontSize = SIDE_HEADER_FONT_SIZE_FM;
			
		} else if( data.getFontSize() == ExportFontSize.CHARS_SIZE_SMALL.value) {
			templateFile = TEMPLATE_FILE_SMALL;
			
			reportPageAddr = REPORT_PAGE_ADDR_FS;
			
			dailyWeekRangeTmpAddr = DAILY_W_RANGE_TMPL_ADDR_FS;
			
			dailyBRangeTmpAddr = DAILY_B_RANGE_TMPL_ADDR_FS;
			
			weeklyRangeTmpAddr = WEEKLY_RANGE_TMPL_ADDR_FS;
			
			fontSize = FONT_SIZE_FS;
			
			sideHeaderFontSize = SIDE_HEADER_FONT_SIZE_FS;
		}

		try (val reportContext = this.createContext(templateFile, data.getExportDateTime())) {

			// Get workbook
			Workbook workbook = reportContext.getWorkbook();
			WorksheetCollection worksheetCollection = workbook.getWorksheets();

			// set data source
			reportContext.setDataSource("companyName", dataSource.getData().getCompanyName());
			reportContext.setDataSource("reportName", dataSource.getData().getReportName());
			reportContext.setDataSource("exportDateTime", dataSource.getData().getExportDateTime());
			reportContext.setDataSource("reportMonthHead", dataSource.getData().getMonthlyHeader());
			reportContext.setDataSource("reportDayHead", dataSource.getData().getDailyHeader());
			
			// process data binginds in template
			reportContext.processDesigner();

			// Prepare template ranges
			Worksheet templateSheet = worksheetCollection.get(0);
			Range reportPageTmpl = templateSheet.getCells().createRange(reportPageAddr + MAX_ROW_PER_EMPL);
			Range dailyWTmpl = templateSheet.getCells().createRange(dailyWeekRangeTmpAddr);
			Range dailyBTmpl = templateSheet.getCells().createRange(dailyBRangeTmpAddr);
			Range weeklyRangeTmpl = templateSheet.getCells().createRange(weeklyRangeTmpAddr);

			// Generate seal column
			this.generateSealColumn(templateSheet, data.getSealColName(), dataSource.getData().getFontSize());

			// Init report page
			int page = 1;

			// Start loop to create report for earch month
			Map<String, List<AttendanceRecordReportEmployeeData>> reportDatas = data.getReportData();
			// set sheet name report
			String sheetName = data.getReportName();
			// create new sheet from template sheet

			worksheetCollection.get(worksheetCollection.addCopy(0)).setName(sheetName);
			int startNewPage = 0;
			
			for (String employeeCd : reportDatas.keySet()) {
				
				// get list employee data
				List<AttendanceRecordReportEmployeeData> reportEmployeeDatas = reportDatas.get(employeeCd);
				reportPageTmpl = templateSheet.getCells().createRange(reportPageAddr + (MAX_ROW_PER_EMPL * reportDatas.keySet().size() * reportEmployeeDatas.size()));

				// Generate employee report page
				for (AttendanceRecordReportEmployeeData employeeData : reportEmployeeDatas) {
					if (dataSource.getMode() == EXPORT_PDF) {
						sheetName = employeeCd + "-" + employeeData.getReportYearMonth() + page;
						worksheetCollection.get(worksheetCollection.addCopy(0)).setName(sheetName);
						startNewPage = 0;
					}
					Worksheet worksheet = worksheetCollection.get(sheetName);
					startNewPage = this.generateEmployeeReportPage(startNewPage, worksheet, employeeData, page,
							reportPageTmpl, dailyWTmpl, dailyBTmpl, weeklyRangeTmpl, data.getFontSize(), dataSource.getMode());
					page++;

					// create print area
					PageSetup pageSetup = worksheet.getPageSetup();
					
					pageSetup.setPaperSize(PaperSizeType.PAPER_A_4);
					pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
					
					// Set header value
					pageSetup.setHeader(0, "&\"ＭＳ ゴシック\"&" + sideHeaderFontSize + "\0" + dataSource.getData().getCompanyName());
					pageSetup.setHeader(1, "&\"ＭＳ ゴシック,Bold\"&"+ fontSize + "\0" + dataSource.getData().getReportName());
					// Get current date and format it
					DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm", Locale.JAPAN);
					String currentFormattedDate = LocalDateTime.now().format(fullDateTimeFormatter);
					pageSetup.setHeader(2, "&\"ＭＳ ゴシック\"&" + sideHeaderFontSize + "\0" + currentFormattedDate+"\npage&P");
					// Delete template column
					if (dataSource.getData().getFontSize() == ExportFontSize.CHAR_SIZE_LARGE.value) {
						worksheet.getCells().deleteColumns(42, 20, true);
					} else if (dataSource.getData().getFontSize() == ExportFontSize.CHAR_SIZE_MEDIUM.value) {
						worksheet.getCells().deleteColumns(50, 24, true);
					} else {
						worksheet.getCells().deleteColumns(58, 28, true);
					}
					
//					pageSetup.setPrintTitleRows(PRINT_TITLE_ROW);
					if (dataSource.getMode() == EXPORT_EXCEL) {
						pageSetup.setZoom(100);
						startNewPage += MAX_ROW_PER_EMPL;
					} else if (dataSource.getMode() == EXPORT_PDF) {
						pageSetup.setFitToPagesTall(1);
						pageSetup.setFitToPagesWide(1);
					}
					pageSetup.setPrintArea(reportPageAddr + startNewPage);
				}
				
			}
			
			// delete template sheet
			worksheetCollection.removeAt(0);
			worksheetCollection.setActiveSheetIndex(0);
			// Create file name
			String fileName = data.getReportName() + "_"
					+ data.getExportDateTime().replaceAll(" ", "").replaceAll(":", "").replaceAll("/", "");

			// Fix #119010
			reportContext.getWorkbook().getBuiltInDocumentProperties().setAuthor("Kinjirou");
			reportContext.getWorkbook().getBuiltInDocumentProperties().setLastSavedBy("");
			
			if (dataSource.getMode() == EXPORT_EXCEL) {
				// save as excel file
				reportContext.saveAsExcel(this.createNewFile(generatorContext, fileName + EXCEL_EXT));
			} else if (dataSource.getMode() == EXPORT_PDF) {
				worksheetCollection.removeAt(0);
				// save as PDF file
				reportContext.saveAsPdf(this.createNewFile(generatorContext, fileName + PDF_EXT));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generate seal column.
	 *
	 * @param templateSheet
	 *            the template sheet
	 * @param sealColNames
	 *            the seal col names
	 * @throws Exception
	 *             the exception
	 */
	private void generateSealColumn(Worksheet templateSheet, List<String> sealColNames, int fontSize) throws Exception {
		String sealRangeTmpAddr = "";
		List<String> sealColAddrress = new ArrayList<>();
		if(fontSize == ExportFontSize.CHAR_SIZE_LARGE.value) {
			sealRangeTmpAddr = SEAL_RANGE_TMPL_ADDR;
			sealColAddrress = SEAL_COL_ADDR;
		} else if (fontSize == ExportFontSize.CHAR_SIZE_MEDIUM.value) {
			sealRangeTmpAddr = SEAL_RANGE_TMPL_ADDR_FM;
			sealColAddrress = SEAL_COL_ADDR_FM;
		} else {
			sealRangeTmpAddr = SEAL_RANGE_TMPL_ADDR_FS;
			sealColAddrress = SEAL_COL_ADDR_FS;
		}
		Range sealColTmpl = templateSheet.getCells().createRange(sealRangeTmpAddr);

		for (int i = 0, j = sealColNames.size(); i < j; i++) {
			String sealColAddr = sealColAddrress.get(j - i - 1);
			Range sealCol = templateSheet.getCells().createRange(sealColAddr);
			sealCol.copy(sealColTmpl);
			sealCol.get(0, 0).setValue(sealColNames.get(i));
		}
	}

	/**
	 * Generate employee report page.
	 *
	 * @param worksheet
	 *            the worksheet
	 * @param employeeData
	 *            the employee data
	 * @param page
	 *            the page
	 * @param pageTmpl
	 *            the page tmpl
	 * @param dailyWTmpl
	 *            the daily W tmpl
	 * @param dailyBTmpl
	 *            the daily B tmpl
	 * @param weeklyRangeTmpl
	 *            the weekly range tmpl
	 * @throws Exception
	 *             the exception
	 */
	private int generateEmployeeReportPage(int startNewPage, Worksheet worksheet,
			AttendanceRecordReportEmployeeData employeeData, int page, Range pageTmpl, Range dailyWTmpl,
			Range dailyBTmpl, Range weeklyRangeTmpl, int fontSize, int mode) throws Exception {

		String monthlyDataAddr = "";
		String monthlyTitleFix = "";
		String reportLeftColAddr = "";
		String reportRightColAddr = "";
		String rangeApprovalCopy = "";
		String endReportPageBreak = "";
		String reportApproval = "";
		String monthlyContentFix = "";
		String dailyTitelFixLeft = ""; 
		String dailyTitleFixRight = "";
		String sealRangeCopyFix = "";
		String sealRangeCopy = "";
		String headerInfoAddr = "";
		ColumnsToMerge[] headerMergeCols = null;
		if (fontSize == ExportFontSize.CHAR_SIZE_LARGE.value) {
			monthlyDataAddr = MONTHLY_DATA_ADDR;
			
			monthlyTitleFix = MONTHLY_TITLE_FIX;
			
			reportLeftColAddr = REPORT_LEFT_COL_ADDR_FM;
			
			reportRightColAddr = REPORT_RIGHT_COL_ADDR;
			
			rangeApprovalCopy = RANGE_APPROVAL_COPY;
			
			endReportPageBreak = END_REPORT_PAGE_BREAK;
			
			reportApproval = REPORT_APPROVAL;
			
			monthlyContentFix = MONTHLY_CONTENT_FIX;
			
			dailyTitelFixLeft = DAILY_TITLE_FIX_LEFT;
			
			dailyTitleFixRight = DAILY_TITLE_FIX_RIGHT;
			
			sealRangeCopyFix = SEAL_RANGE_COPY_FIX;
			
			sealRangeCopy = SEAL_RANGE_COPY;
			
			headerInfoAddr = REPORT_HEADER_INFO_ADDR;
			
			headerMergeCols = REPORT_HEADER_INFO_MERGE_COLS;
		} else if (fontSize == ExportFontSize.CHAR_SIZE_MEDIUM.value) {
			monthlyDataAddr = MONTHLY_DATA_ADDR_FM;
			
			monthlyTitleFix = MONTHLY_TITLE_FIX_FM;
			
			reportLeftColAddr = REPORT_LEFT_COL_ADDR_FM;
			
			reportRightColAddr = REPORT_RIGHT_COL_ADDR_FM;
			
			rangeApprovalCopy = RANGE_APPROVAL_COPY_FM;
			
			endReportPageBreak = END_REPORT_PAGE_BREAK_FM;
			
			reportApproval = REPORT_APPROVAL_FM;
			
			monthlyContentFix = MONTHLY_CONTENT_FIX_FM;
			
			dailyTitelFixLeft = DAILY_TITLE_FIX_LEFT_FM;
			
			dailyTitleFixRight = DAILY_TITLE_FIX_RIGHT_FM;
			
			sealRangeCopyFix = SEAL_RANGE_COPY_FIX_FM;
			
			sealRangeCopy = SEAL_RANGE_COPY_FM;
			
			headerInfoAddr = REPORT_HEADER_INFO_ADDR_FM;
			
			headerMergeCols = REPORT_HEADER_INFO_MERGE_COLS_FM;
		} else {
			monthlyDataAddr = MONTHLY_DATA_ADDR_FS;
			
			monthlyTitleFix = MONTHLY_TITLE_FIX_FS;
			
			reportLeftColAddr = REPORT_LEFT_COL_ADDR_FS;
			
			reportRightColAddr = REPORT_RIGHT_COL_ADDR_FS;
			
			rangeApprovalCopy = RANGE_APPROVAL_COPY_FS;
			
			endReportPageBreak = END_REPORT_PAGE_BREAK_FS;
			
			reportApproval = REPORT_APPROVAL_FS;
			
			monthlyContentFix = MONTHLY_CONTENT_FIX_FS;
			
			dailyTitelFixLeft = DAILY_TITLE_FIX_LEFT_FS;
			
			dailyTitleFixRight = DAILY_TITLE_FIX_RIGHT_FS;
			
			sealRangeCopyFix = SEAL_RANGE_COPY_FIX_FS;
			
			sealRangeCopy = SEAL_RANGE_COPY_FS;
			
			headerInfoAddr = REPORT_HEADER_INFO_ADDR_FS;
			
			headerMergeCols = REPORT_HEADER_INFO_MERGE_COLS_FS;
		}
		// Add monthly data
		Range monththDataRange = worksheet.getCells().createRange(String.format(monthlyDataAddr,
				(startNewPage + MONTHLY_DATA_START_ROW), (startNewPage + MONTHLY_DATA_START_ROW + 1)));
		
		// if case next page: copy monthly header
		if (startNewPage > 0) {
			PasteOptions opts = new PasteOptions();
			opts.setPasteType(PasteType.ALL);
			// copy monthly header 
			Range fixMonthyHeader = worksheet.getCells().createRange(monthlyTitleFix);
			Range monthTitleRange = worksheet.getCells().createRange(String.format(monthlyDataAddr,
					(startNewPage + MONTHLY_TITLE_START_ROW), (startNewPage + MONTHLY_TITLE_START_ROW + 1)));
			monthTitleRange.copyData(fixMonthyHeader);
			monthTitleRange.copy(fixMonthyHeader, opts);
			
			// copy layout content monthly 
			Range fixMonthyLayoutContent = worksheet.getCells().createRange(monthlyContentFix); 
			monththDataRange.copy(fixMonthyLayoutContent, opts);
			
			// copy lai KWR002_221 月間累計
			Range fixCumulativeTotal = worksheet.getCells().createRange(MONTHLY_CUMULATIVE_TOTAL_FIX);
			Range monthCumulativeRange = worksheet.getCells().createRange(String.format(REPORT_CUMULATIVE_FIX,
					(startNewPage + MONTHLY_TITLE_START_ROW), (startNewPage + MONTHLY_TITLE_START_ROW + 3)));
			monthCumulativeRange.copy(fixCumulativeTotal, opts);
			monthCumulativeRange.copyData(fixCumulativeTotal);
			
			// copy daily header left
			Range fixLeftDailyHeader = worksheet.getCells().createRange(dailyTitelFixLeft);
			Range leftDailyTitleRange = worksheet.getCells().createRange(String.format(reportLeftColAddr,
					(startNewPage + DAILY_TITLE_START_ROW), (startNewPage + DAILY_TITLE_START_ROW + 1)));
			leftDailyTitleRange.copy(fixLeftDailyHeader, opts);
			leftDailyTitleRange.copyData(fixLeftDailyHeader);
			
			// copy daily header right 
			Range fixRightDailyHeader = worksheet.getCells().createRange(dailyTitleFixRight);
			Range rightDailyTitleRange = worksheet.getCells().createRange(String.format(reportRightColAddr,
					(startNewPage + DAILY_TITLE_START_ROW), (startNewPage + DAILY_TITLE_START_ROW + 1)));
			rightDailyTitleRange.copy(fixRightDailyHeader, opts);
			rightDailyTitleRange.copyData(fixRightDailyHeader);
			
			// copy seal range 
			Range fixSealRange = worksheet.getCells().createRange(sealRangeCopyFix);
			Range sealRangeCopylayout = worksheet.getCells().createRange(String.format(sealRangeCopy,
					(startNewPage + START_EMPLOYEE_DATA_ROW), (startNewPage + START_EMPLOYEE_DATA_ROW + 3)));
			sealRangeCopylayout.copy(fixSealRange, opts);
			sealRangeCopylayout.copyData(fixSealRange);
			
			// copy approval - copy dau xac nhan - monthly display mark
			Range fixApprovalRange  = worksheet.getCells().createRange(reportApproval);
			Range approvalCopy = worksheet.getCells().createRange(String.format(rangeApprovalCopy,
					(startNewPage + APPROVAL_START_ROW), (startNewPage + APPROVAL_START_ROW)));
			approvalCopy.copy(fixApprovalRange, opts);
			approvalCopy.copyData(fixApprovalRange);
			
			// copy blank row
			double blankRowHeight = worksheet.getCells().getRowHeight(BLANK_ROW_INDEX);
			worksheet.getCells().setRowHeight(startNewPage + BLANK_ROW_INDEX, blankRowHeight);
		} 
		// fill data monthly column
		List<AttendanceRecordReportColumnData> monthLyData = employeeData.getEmployeeMonthlyData();
		for (int i = 0, j = monthLyData.size(); i < j; i++) {
			monththDataRange.get(0, i * 2).setValue(monthLyData.get(i).getUper());
			monththDataRange.get(1, i * 2).setValue(monthLyData.get(i).getLower());
		}

		// Add employee info
		Range employeeInfoL = worksheet.getCells().createRange(String.format(headerInfoAddr,
				(startNewPage + START_EMPLOYEE_DATA_ROW), (startNewPage + START_EMPLOYEE_DATA_ROW)));
		Range employeeInfoR = worksheet.getCells().createRange(String.format(headerInfoAddr,
				(startNewPage + START_EMPLOYEE_CENTER_DATA_ROW), (startNewPage + START_EMPLOYEE_CENTER_DATA_ROW)));
		Range employeeYearInfo = worksheet.getCells().createRange(String.format(headerInfoAddr,
				(startNewPage + START_EMPLOYEE_BOTTOM_DATA_ROW), (startNewPage + START_EMPLOYEE_BOTTOM_DATA_ROW)));

		employeeInfoL.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		employeeInfoR.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		employeeYearInfo.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		// set bold for header
		this.setFontBold(employeeInfoL.get(0, EMPL_INVIDUAL_INDEX));
		this.setFontBold(employeeInfoL.get(0, EMPL_WORKPLACE_INDEX));
		DateFormat df = new SimpleDateFormat("yyyy/MM"); 
		Date startDate = df.parse(employeeData.getYearMonth());

		// Build B8_12~B8_33
		final String PERIOD_TEMPLATE = "%s　%s";
		String yearMonth = TextResource.localize("KWR002_217") + df.format(startDate);
		String deadlineDay = TextResource.localize("KWR002_235")
				+ (employeeData.isLastDayOfMonth() ? TextResource.localize("KWR002_236")
						: (employeeData.getClosureDay() + TextResource.localize("KWR002_237")).toString());

		// Only print the first 3 periods
		List<TempAbsenceData> tempAbsenceDatas = employeeData.getTempAbsenceDatas()
				.subList(0, Math.min(employeeData.getTempAbsenceDatas().size(), MAXIMUM_ABSENCE_PERIOD));
		String periodInfoText = tempAbsenceDatas.stream()
				.map(data -> data.getTempAbsenceFrameName() + TextResource.localize("KWR002_238")
						+ data.getPeriodStart() + TextResource.localize("KWR002_239") + data.getPeriodEnd())
				.collect(Collectors.joining("　"));
		String periodText = String.format(PERIOD_TEMPLATE, yearMonth, deadlineDay);
		if (!StringUtil.isNullOrEmpty(periodInfoText, true)) {
			periodText = periodText.concat("　　" + periodInfoText);
		}
		
		employeeInfoL.get(0, EMPL_INVIDUAL_INDEX)
				.setValue(TextResource.localize("KWR002_212") + employeeData.getInvidual());
		employeeInfoL.get(0, EMPL_WORKPLACE_INDEX)
				.setValue(TextResource.localize("KWR002_213", "#Com_Workplace") + employeeData.getWorkplace());
		employeeInfoR.get(0, EMPL_EMPLOYMENT_INDEX)
				.setValue(TextResource.localize("KWR002_214", "#Com_Employment") + employeeData.getEmployment());
		employeeInfoR.get(0, EMPL_TITLE_INDEX)
				.setValue(TextResource.localize("KWR002_215", "#Com_Jobtitle") + employeeData.getTitle());
		employeeInfoR.get(0, EMPL_WORKTYPE_INDEX)
				.setValue(TextResource.localize("KWR002_216") + employeeData.getWorkType());
		employeeYearInfo.get(0, EMPL_YEARMONTH_INDEX)
				.setValue(periodText);
		
		// Merge range
		for (int i = 0; i < headerMergeCols.length; i++) {
			// Because first page has already been merged, skip page 1
			if (startNewPage == 0)
				break;
			int row = startNewPage + i;
			headerMergeCols[i].getIndexes().forEach(indexes -> this.mergeCells(worksheet.getCells(), row,
					indexes.getStartColumn(), 1, indexes.getNumberToMerge()));
		}
		
		// Create weekly data
		List<AttendanceRecordReportWeeklyData> weeklyDatas = employeeData.getWeeklyDatas();
		Map<String, Integer> dataRow = new HashMap<>();
		dataRow.put(REPORT_START_PAGE_ROW, startNewPage);
		dataRow.put(REPORT_LEFT_ROW, startNewPage + START_REPORT_DATA_ROW);
		dataRow.put(REPORT_RIGHT_ROW, startNewPage + START_REPORT_DATA_ROW);
		dataRow.put(REPORT_ROW_BG, REPORT_ROW_BG_WHITE);
		dataRow.put(REPORT_ROW_START_RIGHT, REPORT_ROW_START_RIGHT_COUNT);
		// fill data column 
		for (AttendanceRecordReportWeeklyData weeklyData : weeklyDatas) {
			this.generateWeeklyData(worksheet, weeklyData, dataRow, dailyWTmpl, dailyBTmpl, weeklyRangeTmpl, fontSize);
		}

		// generate display a confirmation mark in month
		if (employeeData.isApprovalStatus()) {
			Range approvalRange =  worksheet.getCells().createRange(reportApproval);
			approvalRange.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getRed());
			approvalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getRed());
			approvalRange.setOutlineBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getRed());
			approvalRange.setOutlineBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getRed());
			approvalRange.get(0, 0).setValue(APPROVAL);
		}
		// update start page row value
		if (mode == EXPORT_PDF) {
			startNewPage = dataRow.get(REPORT_START_PAGE_ROW) - 1;
		}
		

		VerticalPageBreakCollection vPageBreaks = worksheet.getVerticalPageBreaks();
		vPageBreaks.add(endReportPageBreak + (MAX_ROW_PER_EMPL * page + 1));
		HorizontalPageBreakCollection hPageBreaks = worksheet.getHorizontalPageBreaks();
		hPageBreaks.add(endReportPageBreak + (MAX_ROW_PER_EMPL * page + 1));

 		return startNewPage;
	}

	/**
	 * Generate weekly data.
	 *
	 * @param worksheet
	 *            the worksheet
	 * @param weeklyData
	 *            the weekly data
	 * @param dataRow
	 *            the data row
	 * @param dailyDataW
	 *            the daily data W
	 * @param dailyDataB
	 *            the daily data B
	 * @param weekSumaryTmpl
	 *            the week sumary
	 * @throws Exception
	 *             the exception
	 */
	private void generateWeeklyData(Worksheet worksheet, AttendanceRecordReportWeeklyData weeklyData,
			Map<String, Integer> dataRow, Range dailyDataW, Range dailyDataB, Range weekSumaryTmpl, int fontSize) throws Exception {
		String reportLeftColAddr = "";
		String reportRightColAddr = "";
		if (fontSize == ExportFontSize.CHAR_SIZE_LARGE.value) {
			reportLeftColAddr = REPORT_LEFT_COL_ADDR;
			reportRightColAddr = REPORT_RIGHT_COL_ADDR;
		} else if (fontSize == ExportFontSize.CHAR_SIZE_MEDIUM.value) {
			reportLeftColAddr = REPORT_LEFT_COL_ADDR_FM;
			reportRightColAddr = REPORT_RIGHT_COL_ADDR_FM;
		} else {
			reportLeftColAddr = REPORT_LEFT_COL_ADDR_FS;
			reportRightColAddr = REPORT_RIGHT_COL_ADDR_FS;
		}
		List<AttendanceRecordReportDailyData> dailyDatas = weeklyData.getDailyDatas();
		boolean isWhiteBackground = dataRow.get(REPORT_ROW_BG) == REPORT_ROW_BG_WHITE;
		double dataRowHeight = worksheet.getCells().getRowHeight(FIRST_DATA_ROW_INDEX);
		for (int i = 1, j = dailyDatas.size(); i <= j; i++) {
			Range dailyRange;
			AttendanceRecordReportDailyData data = dailyDatas.get(i - 1);
			if (!data.isSecondCol()) {
				int row = dataRow.get(REPORT_LEFT_ROW);

				// Get range
				dailyRange = worksheet.getCells().createRange(String.format(reportLeftColAddr, row, (row + 1)));
				dailyRange.copy(isWhiteBackground ? dailyDataW : dailyDataB);

				dataRow.put(REPORT_LEFT_ROW, row + 2);
			} else {
				if (dataRow.get(REPORT_ROW_START_RIGHT) == REPORT_ROW_START_RIGHT_COUNT) {
					dataRow.put(REPORT_ROW_START_RIGHT, REPORT_ROW_START_RIGHT_COUNT + 1);
					isWhiteBackground = true;
				}
				int row = dataRow.get(REPORT_RIGHT_ROW);

				// Get range
				dailyRange = worksheet.getCells().createRange(String.format(reportRightColAddr, row, row + 1));
				dailyRange.copy(isWhiteBackground ? dailyDataW : dailyDataB);

				dataRow.put(REPORT_RIGHT_ROW, row + 2);
			}
			dailyRange.setRowHeight(dataRowHeight);

			// fill data data
			dailyRange.get(0, 0).setValue(data.getDate());
			dailyRange.get(0, 1).setValue(data.getDayOfWeek());
			List<AttendanceRecordReportColumnData> reportColumnDatas = data.getColumnDatas();
			// fill data to left daily column
			for (int k = 0, l = reportColumnDatas.size(); k < l; k++) {
				// set left or right
				if (reportColumnDatas.get(k).isAlignUper()) {
					this.setHorizontalLeft(dailyRange.get(0, 2 * (k + 1)));
				}
				if (reportColumnDatas.get(k).isAlignLower()) {
					this.setHorizontalLeft(dailyRange.get(1, 2 * (k + 1)));
				}
				dailyRange.get(0, 2 * (k + 1)).setValue(reportColumnDatas.get(k).getUper());
				dailyRange.get(1, 2 * (k + 1)).setValue(reportColumnDatas.get(k).getLower());
			}

			isWhiteBackground = !isWhiteBackground;
		}

		// fill weekly summary data
		AttendanceRecordReportWeeklySumaryData sumaryData = weeklyData.getWeeklySumaryData();
		Range weeklySumaryRange;
		if (!sumaryData.isSecondCol()) {
			int row = dataRow.get(REPORT_LEFT_ROW);
			// Get range
			weeklySumaryRange = worksheet.getCells().createRange(String.format(reportLeftColAddr, row, row + 1));
			dataRow.put(REPORT_LEFT_ROW, row + 2);
		} else {
			int row = dataRow.get(REPORT_RIGHT_ROW);
			// Get range
			weeklySumaryRange = worksheet.getCells().createRange(String.format(reportRightColAddr, row, row + 1));
			dataRow.put(REPORT_RIGHT_ROW, row + 2);
		}
		weeklySumaryRange.copy(weekSumaryTmpl);
		weeklySumaryRange.get(1, 0).setValue(sumaryData.getDateRange());

		List<AttendanceRecordReportColumnData> summaryColDatas = sumaryData.getColumnDatas();
		// fill data to right daily column
		for (int i = 0, j = summaryColDatas.size(); i < j; i++) {
			// set align left right
			if (summaryColDatas.get(i).isAlignUper()) {
				this.setHorizontalLeft(weeklySumaryRange.get(0, 14 + (i * 2)));
			}
			if (summaryColDatas.get(i).isAlignLower()) {
				this.setHorizontalLeft(weeklySumaryRange.get(1, 14 + (i * 2)));
			}
			weeklySumaryRange.get(0, 14 + (i * 2)).setValue(summaryColDatas.get(i).getUper());
			weeklySumaryRange.get(1, 14 + (i * 2)).setValue(summaryColDatas.get(i).getLower());
		}

		// update last next row color
		dataRow.put(REPORT_ROW_BG, isWhiteBackground ? REPORT_ROW_BG_WHITE : REPORT_ROW_BG_BLUE);
		dataRow.put(REPORT_START_PAGE_ROW,
				dataRow.get(REPORT_LEFT_ROW) < dataRow.get(REPORT_RIGHT_ROW) ? dataRow.get(REPORT_RIGHT_ROW)
						: dataRow.get(REPORT_LEFT_ROW));
	}
	
	/**
	 * set style font bold to cell
	 * @param cell
	 */
	private void setFontBold(Cell cell) {
		Style style = cell.getStyle();
		style.getFont().setBold(true);
		cell.setStyle(style);
	}
	
	/**
	 * set style horizontal left
	 * @param cell
	 */
	private void setHorizontalLeft(Cell cell) {
		Style style = cell.getStyle();
		style.setHorizontalAlignment(TextAlignmentType.LEFT);
		cell.setStyle(style);
	}
	
	private void mergeCells(Cells cells, int startRow, int startCol, int rowCount, int colCount) {
		cells.merge(startRow, startCol, rowCount, colCount);
		Cell cell = cells.get(startRow, startCol);
		Style style = cell.getStyle();
		style.setShrinkToFit(true);
		style.setHorizontalAlignment(TextAlignmentType.LEFT);
		style.setVerticalAlignment(TextAlignmentType.CENTER);
		cell.setStyle(style);
	}
}
