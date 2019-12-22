package nts.uk.file.hr.infra.databeforereflecting.retirementinformation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.ejb.Stateless;

import com.aspose.cells.BackgroundType;
import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Color;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Style;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.hr.develop.app.databeforereflecting.retirementinformation.find.PlannedRetirementDto;
import nts.uk.ctx.hr.develop.app.databeforereflecting.retirementinformation.find.SearchRetiredEmployeesQuery;
import nts.uk.ctx.hr.develop.app.databeforereflecting.retirementinformation.find.SearchRetiredResultDto;
import nts.uk.ctx.hr.develop.dom.databeforereflecting.retirementinformation.ResignmentDivision;
import nts.uk.ctx.hr.develop.dom.databeforereflecting.retirementinformation.Status;
import nts.uk.ctx.hr.develop.dom.interview.dto.InterviewRecordInfo;
import nts.uk.file.hr.app.databeforereflecting.retirementinformation.RetirementInformationGenerator;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

@Stateless
public class AsposeRetirementInformationReportGenerator extends AsposeCellsReportGenerator
		implements RetirementInformationGenerator {

	private static final String TEMPLATE_FILE = "report/定年退職者一覧_Template.xlsx";

	private static final String REPORT_FILE_NAME = "定年退職者一覧_";

	private static final String REPORT_FILE_EXTENSION = ".xlsx";

	private static final String FORMAT_DATE = "yyyy/MM/dd";

	private static final String TITLE = "定年退職者一覧";

	private static final int FIRST_ROW_FILL = 7;

	private static final int RETENTION = 0;
	private static final int RETIREMENT = 1;
	private static final int STATUS = 2;
	private static final int DESIRED_CODE = 3;
	private static final int DESIRED_NAME = 4;
	private static final int EMPLOYEE_CD = 5;
	private static final int EMPLOYEE_NAME = 6;
	private static final int EMPLOYEE_NAME_KANA = 7;
	private static final int BIRTH_DAY = 8;
	private static final int AGE = 9;
	private static final int DEPARTMENT_CODE = 10;
	private static final int DEPARTMENT_NAME = 11;
	private static final int EMPLOYMENT_CODE = 12;
	private static final int EMPLOYMENT_NAME = 13;
	private static final int JOIN_DATE = 14;
	private static final int RETI_DATE = 15;
	private static final int RELEASE_DATE = 16;
	private static final int INPUT_DATE = 17;
	private static final int PER_EVAL_1 = 18;
	private static final int PER_EVAL_2 = 19;
	private static final int PER_EVAL_3 = 20;
	private static final int HEATH_STATUS_1 = 21;
	private static final int HEATH_STATUS_2 = 22;
	private static final int HEATH_STATUS_3 = 23;
	private static final int STRESS_CHECK_1 = 24;
	private static final int STRESS_CHECK_2 = 25;
	private static final int STRESS_CHECK_3 = 26;
	private static final int INTERVIEW_DATE = 27;
	private static final int INTERVIEWER = 28;

	@Override
	public void generate(FileGeneratorContext generatorContext, SearchRetiredResultDto dataSource,
			SearchRetiredEmployeesQuery query, String companyName) {
		try (val reportContext = this.createContext(TEMPLATE_FILE)) {
			val designer = this.createContext(TEMPLATE_FILE);
			Workbook workbook = designer.getWorkbook();
			WorksheetCollection worksheets = workbook.getWorksheets();
			// set up page prepare print
			this.writeFileExcel(worksheets, dataSource, query, companyName);

			designer.getDesigner().setWorkbook(workbook);
			designer.processDesigner();

			designer.saveAsExcel(this.createNewFile(generatorContext, this.getReportName(
					REPORT_FILE_NAME + REPORT_FILE_EXTENSION)));

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void writeFileExcel(WorksheetCollection wsc, SearchRetiredResultDto dto, SearchRetiredEmployeesQuery query,
			String companyName) {
		try {

			List<PlannedRetirementDto> exportData = dto.getRetiredEmployees();
			List<InterviewRecordInfo> interviewRecords = dto.getInterView().listInterviewRecordInfo;
			int rowIndex = FIRST_ROW_FILL;
			Worksheet ws = wsc.get(0);
			int lineCopy = 3;
			this.settingHeader(ws, query, companyName);
			for (int i = 0; i < exportData.size(); i++) {

				PlannedRetirementDto entity = exportData.get(i);
				Optional<InterviewRecordInfo> interViewOpt = interviewRecords.stream()
						.filter(x -> x.getEmployeeID().equals(entity.getSId())).findFirst();
				if (i % 2 == 0) {
					ws.getCells().copyRows(ws.getCells(), rowIndex, rowIndex + lineCopy - 1, lineCopy);
				}
				if (i == exportData.size() - 1) {
					ws.getCells().deleteRows(rowIndex, exportData.size() % 2 == 0 ? 3 : 4);
				}
				ws.getCells().get(rowIndex, RETENTION).putValue(entity.getNotificationCategory() == 0 ? "空白" : "保留");
				ws.getCells().get(rowIndex, RETIREMENT)
						.putValue(EnumAdaptor.valueOf(entity.getExtendEmploymentFlg(), ResignmentDivision.class).name);
				ws.getCells().get(rowIndex, STATUS)
						.putValue(EnumAdaptor.valueOf(entity.getStatus(), Status.class).name);
				ws.getCells().get(rowIndex, DESIRED_CODE).putValue(entity.getDesiredWorkingCourseCd());
				ws.getCells().get(rowIndex, DESIRED_NAME).putValue(entity.getDesiredWorkingCourseName());
				ws.getCells().get(rowIndex, EMPLOYEE_CD).putValue(entity.getScd());
				ws.getCells().get(rowIndex, EMPLOYEE_NAME).putValue(entity.getBusinessName());
				ws.getCells().get(rowIndex, EMPLOYEE_NAME_KANA).putValue(entity.getBusinessnameKana());
				ws.getCells().get(rowIndex, BIRTH_DAY).putValue(convertToString(entity.getBirthday()));
				ws.getCells().get(rowIndex, AGE).putValue(entity.getAge());
				ws.getCells().get(rowIndex, DEPARTMENT_CODE).putValue(entity.getDepartmentCode());
				ws.getCells().get(rowIndex, DEPARTMENT_NAME).putValue(entity.getDepartmentName());
				ws.getCells().get(rowIndex, EMPLOYMENT_CODE).putValue(entity.getEmploymentCode());
				ws.getCells().get(rowIndex, EMPLOYMENT_NAME).putValue(entity.getEmploymentName());
				ws.getCells().get(rowIndex, JOIN_DATE).putValue(convertToString(entity.getDateJoinComp()));
				ws.getCells().get(rowIndex, RETI_DATE).putValue(convertToString(entity.getRetirementDate()));
				ws.getCells().get(rowIndex, RELEASE_DATE).putValue(entity.getReleaseDate());
				ws.getCells().get(rowIndex, INPUT_DATE).putValue(convertToString(entity.getInputDate()));
				ws.getCells().get(rowIndex, PER_EVAL_1).putValue(entity.getHrEvaluation1());
				ws.getCells().get(rowIndex, PER_EVAL_2).putValue(entity.getHrEvaluation2());
				ws.getCells().get(rowIndex, PER_EVAL_3).putValue(entity.getHrEvaluation3());
				ws.getCells().get(rowIndex, HEATH_STATUS_1).putValue(entity.getHealthStatus1());
				ws.getCells().get(rowIndex, HEATH_STATUS_2).putValue(entity.getHealthStatus2());
				ws.getCells().get(rowIndex, HEATH_STATUS_3).putValue(entity.getHealthStatus3());
				ws.getCells().get(rowIndex, STRESS_CHECK_1).putValue(entity.getStressStatus1());
				ws.getCells().get(rowIndex, STRESS_CHECK_2).putValue(entity.getStressStatus2());
				ws.getCells().get(rowIndex, STRESS_CHECK_3).putValue(entity.getStressStatus3());
				ws.getCells().get(rowIndex, INTERVIEW_DATE).putValue(
						interViewOpt.isPresent() ? convertToString(interViewOpt.get().getInterviewDate()) : "");
				ws.getCells().get(rowIndex, INTERVIEWER)
						.putValue(interViewOpt.isPresent() ? interViewOpt.get().getBusinessName() : "");
				setBorder(wsc.get(0),rowIndex);
				rowIndex++;
			}
			if (exportData.size() == 0) {
				ws.getCells().deleteRows(rowIndex, 2);
			}
			
				
				
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setBorder(Worksheet worksheet, int rowIndex) {
		int totalColumn = 29;
		int columnStart = 0;
		for (int column = columnStart; column < totalColumn; column++) {
			Cell cell = worksheet.getCells().get(rowIndex, column);
			Style style = cell.getStyle();
			style.setPattern(BackgroundType.SOLID);
			style.setBorder(BorderType.LEFT_BORDER, CellBorderType.THIN, Color.getBlack());
			style.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
			style.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			style.setBorder(BorderType.RIGHT_BORDER, CellBorderType.THIN, Color.getBlack());
			cell.setStyle(style);
		}
	}

	private String convertToString(GeneralDate date) {
		if (date == null) {
			return null;
		}
		return date.toString(FORMAT_DATE);
	}

	private void settingHeader(Worksheet ws, SearchRetiredEmployeesQuery query, String companyName) {

		// Set print page
		PageSetup pageSetup = ws.getPageSetup();
		pageSetup.setPrintArea("A1:AC");
		pageSetup.setHeader(0, "&\"ＭＳ ゴシック\"&10 " + companyName);
		pageSetup.setHeader(1, "&\"ＭＳ ゴシック\"&16 " + TITLE);
		// Set header date
		DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d  HH:mm:ss", Locale.JAPAN);
		pageSetup.setHeader(2, "&\"ＭＳ ゴシック\"&10 " + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage&P ");

		ws.getCells().get(1, 3).putValue(query.getStartDate() + " ～ " + query.getEndDate());
		ws.getCells().get(1, 5).putValue(query.isIncludingReflected() ? "※反映済みを含む" : "");
		ws.getCells().get(2, 3).putValue(query.getRetirementAge());
		ws.getCells().get(3, 3).putValue(query.isAllSelectDepartment() ? "と表示" : "全て");
		ws.getCells().get(4, 3).putValue(query.isAllSelectEmployment() ? "と表示" : "全て");
	}

}
