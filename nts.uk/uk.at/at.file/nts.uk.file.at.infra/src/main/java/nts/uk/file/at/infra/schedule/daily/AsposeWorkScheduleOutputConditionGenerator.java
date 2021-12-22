/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.file.at.infra.schedule.daily;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Cells;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.PageSetup;
import com.aspose.cells.Range;
import com.aspose.cells.Style;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorkbookDesigner;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.error.RawErrorMessage;
import nts.arc.layer.infra.file.export.FileGeneratorContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringLength;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceItemValueImport;
import nts.uk.ctx.at.function.dom.adapter.dailyattendanceitem.AttendanceResultImport;
import nts.uk.ctx.at.function.dom.dailyworkschedule.AttendanceItemsDisplay;
import nts.uk.ctx.at.function.dom.dailyworkschedule.FontSizeEnum;
import nts.uk.ctx.at.function.dom.dailyworkschedule.NameWorkTypeOrHourZone;
import nts.uk.ctx.at.function.dom.dailyworkschedule.OutputItemDailyWorkSchedule;
import nts.uk.ctx.at.function.dom.dailyworkschedule.OutputItemDailyWorkScheduleRepository;
import nts.uk.ctx.at.function.dom.dailyworkschedule.PrintRemarksContent;
import nts.uk.ctx.at.function.dom.dailyworkschedule.RemarksContentChoice;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceDto;
import nts.uk.ctx.at.record.app.find.dailyperform.editstate.EditStateOfDailyPerformanceFinder;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.employment.EmploymentHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.employment.ScEmploymentAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.SCEmployeeAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.EmployeeDto;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.AutoCalAtrOvertime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.autocalsetting.TimeLimitUpperLimitSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.ErrorAlarmWorkRecordCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.SystemFixedErrorAlarm;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.enums.DailyAttendanceAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.service.CompanyDailyItemService;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.ManageAtr;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.HierarchyCode;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceHierarchy;
import nts.uk.ctx.bs.employee.dom.workplace.info.WorkplaceInfo;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformationRepository;
import nts.uk.ctx.bs.employee.pub.company.StatusOfEmployee;
import nts.uk.ctx.bs.employee.pub.company.SyCompanyPub;
import nts.uk.ctx.bs.employee.pub.employmentstatus.EmploymentInfo;
import nts.uk.ctx.bs.employee.pub.employmentstatus.EmploymentState;
import nts.uk.ctx.bs.employee.pub.employmentstatus.EmploymentStatus;
import nts.uk.ctx.bs.employee.pub.employmentstatus.EmploymentStatusPub;
import nts.uk.file.at.app.export.dailyschedule.ActualValue;
import nts.uk.file.at.app.export.dailyschedule.AttendanceResultImportAdapter;
import nts.uk.file.at.app.export.dailyschedule.FileOutputType;
import nts.uk.file.at.app.export.dailyschedule.FormOutputType;
import nts.uk.file.at.app.export.dailyschedule.ItemSelectionType;
import nts.uk.file.at.app.export.dailyschedule.PageBreakIndicator;
import nts.uk.file.at.app.export.dailyschedule.SwitchItemDisplay;
import nts.uk.file.at.app.export.dailyschedule.TotalDayCountWs;
import nts.uk.file.at.app.export.dailyschedule.TotalWorkplaceHierachy;
import nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputCondition;
import nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputGenerator;
import nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputQuery;
import nts.uk.file.at.app.export.dailyschedule.WorkScheduleSettingTotalOutput;
import nts.uk.file.at.app.export.dailyschedule.ZeroDisplayType;
import nts.uk.file.at.app.export.dailyschedule.data.DailyPerformanceHeaderData;
import nts.uk.file.at.app.export.dailyschedule.data.DailyPerformanceReportData;
import nts.uk.file.at.app.export.dailyschedule.data.DailyPersonalPerformanceData;
import nts.uk.file.at.app.export.dailyschedule.data.DailyReportData;
import nts.uk.file.at.app.export.dailyschedule.data.DailyWorkplaceData;
import nts.uk.file.at.app.export.dailyschedule.data.DetailedDailyPerformanceReportData;
import nts.uk.file.at.app.export.dailyschedule.data.EmployeeReportData;
import nts.uk.file.at.app.export.dailyschedule.data.EmployeesStatusByDate;
import nts.uk.file.at.app.export.dailyschedule.data.OutputItemSetting;
import nts.uk.file.at.app.export.dailyschedule.data.WorkplaceDailyReportData;
import nts.uk.file.at.app.export.dailyschedule.data.WorkplaceReportData;
import nts.uk.file.at.app.export.dailyschedule.totalsum.TotalCountDay;
import nts.uk.file.at.app.export.dailyschedule.totalsum.TotalSumRowOfDailySchedule;
import nts.uk.file.at.app.export.dailyschedule.totalsum.TotalValue;
import nts.uk.file.at.app.export.dailyschedule.totalsum.WorkplaceTotal;
import nts.uk.file.at.app.export.employee.jobtitle.EmployeeJobHistExport;
import nts.uk.file.at.app.export.employee.jobtitle.JobTitleImportAdapter;
import nts.uk.file.at.infra.schedule.EmployeePrintOrder;
import nts.uk.file.at.infra.schedule.RowPageTracker;
import nts.uk.file.at.infra.schedule.WorkSheetInfo;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceScreenRepo;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.CalculationStateDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.workinfomation.WorkInfoOfDailyPerformanceDetailDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

/**
 * The Class AsposeWorkScheduleOutputConditionGenerator.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AsposeWorkScheduleOutputConditionGenerator extends AsposeCellsReportGenerator
		implements WorkScheduleOutputGenerator {
	
	/** The workplace info repository. */
	@Inject
	private WorkplaceInformationRepository workplaceInfoRepository;
	
	/** The error alarm repository. */
	@Inject
	private EmployeeDailyPerErrorRepository errorAlarmRepository;
	
	/** The attendace adapter. */
	@Inject
	private AttendanceResultImportAdapter attendaceAdapter;
	
	/** The total day count ws. */
	@Inject
	private TotalDayCountWs totalDayCountWs;
	
	/** The error alarm work record repo. */
	@Inject
	private ErrorAlarmWorkRecordRepository errorAlarmWorkRecordRepo;
	
	/** The edit state finder. */
	@Inject
	private EditStateOfDailyPerformanceFinder editStateFinder;
	
	/** The workplace adapter. */
	@Inject
	private WorkplaceAdapter workplaceAdapter;
	
	/** The employment adapter. */
	@Inject
	private ScEmploymentAdapter employmentAdapter;
	
	/** The output item repo. */
	@Inject
	private OutputItemDailyWorkScheduleRepository outputItemRepo;
	
	/** The employee adapter. */
	@Inject
	private SCEmployeeAdapter employeeAdapter;
	
	/** The employment repository. */
	@Inject
	private EmploymentRepository employmentRepository;
	
	/** The job title adapter. */
	@Inject
	private JobTitleImportAdapter jobTitleAdapter;
	
	/** The company repository. */
	@Inject
	private CompanyRepository companyRepository;
	
	/** The worktype repo. */
	@Inject
	private WorkTypeRepository worktypeRepo;
	
	/** The work time repo. */
	@Inject
	private WorkTimeSettingRepository workTimeRepo;
	
	/** The daily performance repo. */
	@Inject
	private DailyPerformanceScreenRepo dailyPerformanceRepo;
	
	/** The data processor. */
	@Inject
	private DataDialogWithTypeProcessor dataProcessor;

	/** The company daily item service. */
	@Inject
	private CompanyDailyItemService companyDailyItemService;
	
	@Inject
	private ManagedParallelWithContext parallel;
	
	/** The optional item repo. */
	@Inject
	private OptionalItemRepository optionalItemRepo;
	
	@Inject
	private SyCompanyPub symCompany;
	
	@Inject
	private EmploymentStatusPub empStatusPub;
	
	/** The Constant filename. */
	private static final String TEMPLATE_DATE = "report/KWR001_Date.xlsx";
	
	/** The Constant filename. */
	private static final String TEMPLATE_EMPLOYEE = "report/KWR001_Employee.xlsx";
	
	/** The Constant filename. */
	private static final String TEMPLATE_DATE_SMALL_SIZE = "report/KWR001_Date_Small_Size.xlsx";
	
	/** The Constant filename. */
	private static final String TEMPLATE_EMPLOYEE_SMALL_SIZE = "report/KWR001_Employee_Small_Size.xlsx";
	
	/** The Constant DATA_PREFIX_NO_WORKPLACE. */
	private static final String DATA_PREFIX_NO_WORKPLACE = "NOWPK_";
	
	/** The Constant CHUNK_SIZE. for font size large */
	private static final int CHUNK_SIZE_BIG = 16;

	/** The Constant CHUNK_SIZE_SMALL. for font size small */
	private static final int CHUNK_SIZE_SMALL = 20;
	
	/** The Constant LIMIT_DATA_PACK. */
	private static final int LIMIT_DATA_PACK = 5;
	
	/** The Constant LIMIT_REMARK_INPUT. */
	private static final int LIMIT_REMARK_INPUT = 19;
	
	/** The Constant DATA_COLUMN_INDEX. */
	private static final int[] DATA_COLUMN_INDEX = {3};
	
	/** The Constant MASTER_UNREGISTERED. */
	private static final String MASTER_UNREGISTERED = "マスタ未登録";
	
	/** The Constant ATTENDANCE_ID_WORK_TYPE. */
	// All attendance ID which has value type = CODE
	private static final int[] ATTENDANCE_ID_WORK_TYPE = {1, 28};
	
	/** The Constant ATTENDANCE_ID_WORK_TIME. */
	private static final int[] ATTENDANCE_ID_WORK_TIME = {2, 29};
	
	/** The Constant ATTENDANCE_ID_WORK_LOCATION. */
	private static final int[] ATTENDANCE_ID_WORK_LOCATION = {30, 33, 36, 38, 40, 43, 46, 48,
			50, 52, 54, 56, 58, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 87, 90, 94,
			97, 101, 104, 108, 111, 115, 118, 122, 125, 129, 132, 136, 139, 143, 146, 150, 153,
			921, 931, 941, 951, 961, 971, 981, 991, 1001, 1011, 1021, 1031, 1041, 1051, 1061, 1071, 1081, 1091, 1101, 1111};
	
	/** The Constant ATTENDANCE_ID_REASON. */
	private static final int[] ATTENDANCE_ID_REASON = {438, 443, 448, 453, 458, 801, 806, 811, 816, 821};
	private static final int[] ATTENDANCE_ID_REASON_DIVERGENCE = {439, 444, 449, 454, 459, 802, 807, 812, 817, 822};

	private static final int[] ATTENDANCE_ID_WORKCODE  = {924, 925, 926,
			927, 928, 934, 935, 936, 937, 938, 944, 945, 946, 947, 948,
			954, 955, 956, 957, 958, 964, 965, 966, 967, 968, 974, 975,
			976, 977, 978, 984, 985, 986, 987, 988, 994, 995, 996, 997,
			998, 1004, 1005, 1006, 1007, 1008, 1014, 1015, 1016, 1017,
			1018, 1024, 1025, 1026, 1027, 1028, 1034, 1035, 1036, 1037,
			1038, 1044, 1045, 1046, 1047, 1048, 1054, 1055, 1056, 1057,
			1058, 1064, 1065, 1066, 1067, 1068, 1074, 1075, 1076, 1077,
			1078, 1084, 1085, 1086, 1087, 1088, 1094, 1095, 1096, 1097,
			1098, 1104, 1105, 1106, 1107, 1108, 1114, 1115, 1116, 1117, 1118 };

	private static final int ATTENDANCE_ID_ADDITION = 1294;
	
	/** The Constant ATTENDANCE_ID_WORKPLACE. */
	private static final int ATTENDANCE_ID_WORKPLACE = 623;
	
	/** The Constant ATTENDANCE_ID_CLASSIFICATION. */
	private static final int ATTENDANCE_ID_CLASSIFICATION = 624;
	
	/** The Constant ATTENDANCE_ID_POSITION. */
	private static final int ATTENDANCE_ID_POSITION = 625;
	
	/** The Constant ATTENDANCE_ID_EMPLOYMENT. */
	private static final int ATTENDANCE_ID_EMPLOYMENT = 626;
	
	/** The Constant ATTENDANCE_ID_USE_MAP. */
	// All attendance ID which has value type = ATTR
	private static final int[] ATTENDANCE_ID_USE_MAP = {};
	
	/** The Constant ATTENDANCE_ID_CALCULATION_MAP. */
	private static final int[] ATTENDANCE_ID_CALCULATION_MAP = {627, 628, 629, 630, 631, 632, 633, 634, 635, 636, 637, 638, 639, 640};
	
	/** The Constant ATTENDANCE_ID_OVERTIME_MAP. */
	private static final int[] ATTENDANCE_ID_OVERTIME_MAP = {824, 825, 826, 827, 828, 829, 830, 831, 832};
	
	/** The Constant ATTENDANCE_ID_OUTSIDE_MAP. */
	private static final int[] ATTENDANCE_ID_OUTSIDE_MAP = {86, 93, 100, 107, 114, 121, 128, 135, 142, 149};
	
	/** The Constant ATTENDANCE_ID_OPTIONAL_START. */
	// Attendance id with optional item from KMK002, for workaround
	private static final int ATTENDANCE_ID_OPTIONAL_START = 641;
	
	/** The Constant ATTENDANCE_ID_OPTIONAL_END. */
	private static final int ATTENDANCE_ID_OPTIONAL_END = 740;
	
	/** The Constant FONT_FAMILY. */
	private static final String FONT_FAMILY = "ＭＳ ゴシック";
	
	/** The Constant FONT_SIZE. */
	private static final int FONT_SIZE_BIG = 9;
	private static final int FONT_SIZE_SMALL = 7;

	/** The Constant MAX_PAGE_PER_SHEET. */
	private static final int MAX_PAGE_PER_SHEET = 1000;
	
	/** The Constant ATTENDANCE_ID_EMPLOYMENT. */
	private static final int ATTENDANCE_ID_BUSSINESS_TYPE = 858;

	/* (non-Javadoc)
	 * @see nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputGenerator#generate(nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputCondition, nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo, nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputQuery)
	 */
	@Override
	public void generate(FileGeneratorContext generatorContext, TaskDataSetter setter, WorkScheduleOutputQuery query) {
		AsposeCellsReportContext reportContext = null;
		WorkScheduleOutputCondition condition = query.getCondition();

		String layoutId = condition.getSelectionType() == ItemSelectionType.FREE_SETTING
				? condition.getFreeSettingLayoutId()
				: condition.getStandardSelectionLayoutId();

		// 出力レイアウトIDから勤怠項目IDを取得する(Get attendance item ID from Output layout ID)
		Optional<OutputItemDailyWorkSchedule> optOutputItemDailyWork = outputItemRepo.findByLayoutId(layoutId);

		if (!optOutputItemDailyWork.isPresent()) {
			throw new BusinessException(new RawErrorMessage("Msg_1141"));
		}
		
		OutputItemDailyWorkSchedule outputItemDailyWork = optOutputItemDailyWork.get();
		
		if (condition.getOutputType() == FormOutputType.BY_EMPLOYEE) {
			reportContext = outputItemDailyWork.getFontSize() == FontSizeEnum.BIG
									? this.createContext(TEMPLATE_EMPLOYEE)
									: this.createContext(TEMPLATE_EMPLOYEE_SMALL_SIZE);
		} else {
			reportContext = outputItemDailyWork.getFontSize() == FontSizeEnum.BIG
									? this.createContext(TEMPLATE_DATE)
									: this.createContext(TEMPLATE_DATE_SMALL_SIZE);
		}

		Workbook workbook;
		try {
			workbook = reportContext.getWorkbook();
			
			WorkbookDesigner designer = new WorkbookDesigner();
			designer.setWorkbook(workbook);
			
			TotalSumRowOfDailySchedule sum = new TotalSumRowOfDailySchedule();
			sum.setPersonalTotal(new ArrayList<>());
			
			// Calculate row size and get sheet
			List<AttendanceItemsDisplay> lstAttendanceItemDisplay = outputItemDailyWork.getLstDisplayedAttendance();
			List<Integer> lstAttendanceId = lstAttendanceItemDisplay.stream().sorted((o1, o2) -> (o1.getOrderNo() - o2.getOrderNo()))
					.map(x -> x.getAttendanceDisplay()).collect(Collectors.toList());
			String companyID = AppContexts.user().companyId();
			String roleId = AppContexts.user().roles().forAttendance();
			
			// アルゴリズム「会社の日次項目を取得する」を実行 (Get the algorithm 「会社の日次項目を取得する」)
			List<AttItemName> lstDailyAttendanceItem = companyDailyItemService.getDailyItems(companyID, Optional.of(roleId),
					lstAttendanceId, Arrays.asList(DailyAttendanceAtr.values()));
			int nListOutputCode = lstDailyAttendanceItem.size();
			int nSize;
			int chunkSize = outputItemDailyWork.getFontSize() == FontSizeEnum.BIG ? CHUNK_SIZE_BIG : CHUNK_SIZE_SMALL;
			if (nListOutputCode % chunkSize == 0) {
				nSize = nListOutputCode / chunkSize;
			}
			else {
				nSize = nListOutputCode / chunkSize + 1;
			}
			
			/**
			 * Collect data
			 */
			DailyPerformanceReportData reportData = new DailyPerformanceReportData();
			collectData(reportData, query, condition, optOutputItemDailyWork.get(), nSize, setter);
			
			Worksheet sheet;
			Integer currentRow, dateRow;
			
			if (condition.getOutputType() == FormOutputType.BY_EMPLOYEE) {
				switch (nSize) {
				case 1:
					sheet = workbook.getWorksheets().get(0);
					break;
				case 2:
					sheet = workbook.getWorksheets().get(1);
					break;
				case 3:
				default:
					sheet = workbook.getWorksheets().get(2);
				}
				
			} else {
				switch (nSize) {
				case 1:
					sheet = workbook.getWorksheets().get(3);
					break;
				case 2:
					sheet = workbook.getWorksheets().get(4);
					break;
				case 3:
				default:
					sheet = workbook.getWorksheets().get(5);
					
				}
			}
			currentRow = 2;
			dateRow = 0;
			
			WorksheetCollection sheetCollection = workbook.getWorksheets();
			// set work sheet name 
			String sheetName = optOutputItemDailyWork.isPresent() ? optOutputItemDailyWork.get().getItemName().v() : WorkScheOutputConstants.SHEET_NAME;
			
			// Write header data
			writeHeaderData(query, outputItemDailyWork, sheet, reportData, dateRow);
			
			// Copy footer
			//copyFooter(sheet, sheetCollection.get(6));
			// if font size is big remark position is 35 else 39
			int remarkPosition = outputItemDailyWork.getFontSize() == FontSizeEnum.BIG ? 35 : 43;
			// Set all fixed cell
			setFixedData(condition, sheet, reportData, currentRow, remarkPosition);
			
			// Write display map
			writeDisplayMap(sheet.getCells(),reportData, currentRow, nSize, chunkSize, outputItemDailyWork.getFontSize());
			
			currentRow+=nSize*2;
			
			// Create row page tracker
			DatePeriod period = new DatePeriod(query.getStartDate(), query.getEndDate());
			RowPageTracker rowPageTracker = new RowPageTracker();
			if (nSize == 1 && RowPageTracker.MAX_ROW_PER_PAGE_EMPLOYEE_1 / period.datesBetween().size() >= 1) {
				rowPageTracker.initMaxRowAllowed(nSize, condition.getOutputType());
			} else {
				rowPageTracker.initMaxRowAllowed(nSize, condition.getOutputType(), period.datesBetween().size() + 4);
			}

			int sheetCount = sheetCollection.getCount();

			// Get index of temp sheet
			int indexTempSheet = sheet.getIndex();
			WorkSheetInfo sheetInfo = new WorkSheetInfo(sheet, currentRow, indexTempSheet, sheetName);
			// Copy sheet
			sheet = this.copySheet(sheetCollection, sheetInfo);
			sheetInfo.setSheet(sheet);
			
			// Write detailed work schedule
			if (condition.getOutputType() == FormOutputType.BY_EMPLOYEE)
				
				currentRow = writeDetailedWorkSchedule(currentRow
						, sheetCollection
						, sheetInfo
						, reportData.getWorkplaceReportData()
						, nSize
						, condition
						, rowPageTracker
						, chunkSize
						, outputItemDailyWork.getFontSize());
			else {
				DailyReportData dailyReportData = reportData.getDailyReportData();
				currentRow = writeDetailedDailySchedule(currentRow
						, sheetCollection
						, sheetInfo
						, dailyReportData
						, nSize
						, condition
						, rowPageTracker
						, chunkSize
						, outputItemDailyWork.getFontSize());
			}
			
			// Delete footer if user doesn't set remark content
			if (!outputItemDailyWork.getLstRemarkContent().stream().anyMatch(remark -> remark.isUsedClassification())) {
				hideFooter(sheet);
			}
			
			alignTopCotent(sheet);
			
			// Delete the rest of workbook
			for (int i = 0; i < sheetCount; i++) {
				sheetCollection.removeAt(0);
			}
			
			// Process designer
			reportContext.processDesigner();
			
			// Set cursor position
			sheet.setActiveCell("A1");
			
			// Get current date and format it
			DateTimeFormatter jpFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.JAPAN);
			String currentFormattedDate = LocalDateTime.now().format(jpFormatter);
			
			// Create print area
			//createPrintArea(currentRow, sheet);
			
			// Save workbook
			if (query.getFileType() == FileOutputType.FILE_TYPE_EXCEL) {
				
				// set file name
				reportContext.saveAsExcel(this.createNewFile(generatorContext, outputItemDailyWork.getItemName().v() + "_" + currentFormattedDate + ".xlsx"));
			} else {
				reportContext.saveAsPdf(this.createNewFile(generatorContext, outputItemDailyWork.getItemName().v() + "_" + currentFormattedDate + ".pdf"));
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Sets the font style.
	 *
	 * @param style the new font style
	 */
	private void setFontStyle (Style style, FontSizeEnum fontSize) {
		
		Font font = style.getFont();
		font.setSize(fontSize == FontSizeEnum.BIG ? FONT_SIZE_BIG : FONT_SIZE_SMALL);
		font.setName(FONT_FAMILY);
	}
	
	/**
	 * Collect data.
	 *
	 * @param reportData the report data
	 * @param query the query
	 * @param condition the condition
	 * @param outputItem the output item
	 * @param dataRowCount the data row count
	 * @param setter the setter
	 */
	private void collectData(DailyPerformanceReportData reportData
			, WorkScheduleOutputQuery query
			, WorkScheduleOutputCondition condition
			, OutputItemDailyWorkSchedule outputItem
			, int dataRowCount
			, TaskDataSetter setter) {
		String companyId = AppContexts.user().companyId();
		String roleId = AppContexts.user().roles().forAttendance();
		
		reportData.setHeaderData(new DailyPerformanceHeaderData());
		collectHeaderData(reportData.getHeaderData(), condition);
		List<Integer> lstHaveName = collectDisplayMap(reportData.getHeaderData(), outputItem, companyId, roleId);
		GeneralDate endDate = query.getEndDate();
		GeneralDate baseDate = query.getBaseDate();
		
		WorkScheduleQueryData queryData = new WorkScheduleQueryData();
		queryData.setQuery(query);
		
		DatePeriod period = new DatePeriod(query.getStartDate(), endDate);
		
		// Init remark query data container
		RemarkQueryDataContainer remarkQueryDataContainer = new RemarkQueryDataContainer();
		remarkQueryDataContainer.setDailyPerformanceRepo(dailyPerformanceRepo);
		remarkQueryDataContainer.setEditStateFinder(editStateFinder);
		remarkQueryDataContainer.setLstRemarkContent(outputItem.getLstRemarkContent());
		remarkQueryDataContainer.initData(query.getEmployeeId(), period);
		queryData.setRemarkDataContainter(remarkQueryDataContainer);		
		
		// delete comment of 106578 
		List<GeneralDate> lstDate = new DateRange(query.getStartDate(), endDate).toListDate();
		queryData.setDatePeriod(lstDate);
		
		Map<String, WorkplaceInfo> lstWorkplace = new TreeMap<>(); // Automatically sort by code, will need to check hierarchy later
		List<String> lstWorkplaceId = new ArrayList<>();
		List<String> lstEmployeeNoWorkplace = new ArrayList<>();
		
		// Push all employeeId into print order
		List<EmployeePrintOrder> lstEmployeePrintOrder = new ArrayList<>();
		int order = 0;

/*		// Get all workplace of selected employees within given period
		for (String employeeId: query.getEmployeeId()) {
			WkpHistImport workplaceHist = workplaceAdapter.findWkpBySid(employeeId, baseDate);
			if (workplaceHist == null) {
				lstEmployeeNoWorkplace.add(employeeId);
				continue;
			}
			lstWorkplaceId.add(workplaceHist.getWorkplaceId());
			queryData.getLstWorkplaceImport().add(workplaceHist);

			// Also add into print order list
			lstEmployeePrintOrder.add(new EmployeePrintOrder(order++, employeeId));
		}*/

		/*
		 * Tuning performance
		 */
        // Get all workplace of selected employees within given period
		Map<String, WkpHistImport> workplaceHists = workplaceAdapter.findWkpBySidAndBaseDate(query.getEmployeeId(), baseDate).stream().collect(Collectors.toMap(WkpHistImport::getEmployeeId, Function.identity()));

		//Get employees having no workplace on base date
        for (String employeeId : query.getEmployeeId()) {
            if (!workplaceHists.containsKey(employeeId)) {
                lstEmployeeNoWorkplace.add(employeeId);
            }
        }

        lstWorkplaceId = workplaceHists.values().stream().map(WkpHistImport::getWorkplaceId).distinct().collect(Collectors.toList());
        queryData.setLstWorkplaceImport(workplaceHists);

        // Also add into print order list
        for (String key : workplaceHists.keySet()) {
            lstEmployeePrintOrder.add(new EmployeePrintOrder(order++, key));
        }

        // 職場情報がない社員の職場エラーリストを作成する
		if (!lstEmployeeNoWorkplace.isEmpty()) {
			List<EmployeeDto> lstEmployeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeNoWorkplace);
			int numOfChunks = (int)Math.ceil((double)lstEmployeeDto.size() / LIMIT_DATA_PACK);
			int start, length;
			List<EmployeeDto> lstSplitEmployeeDto;
			for(int i = 0; i < numOfChunks; i++) {
				start = i * LIMIT_DATA_PACK;
	            length = Math.min(lstEmployeeDto.size() - start, LIMIT_DATA_PACK);

	            lstSplitEmployeeDto = lstEmployeeDto.subList(start, start + length);
	            
	            // Convert to json array
	            JsonArrayBuilder arr = Json.createArrayBuilder();
	    		
	    		for (EmployeeDto employee : lstSplitEmployeeDto) {
	    			arr.add(employee.buildJsonObject());
	    		}
	            
	            setter.setData(DATA_PREFIX_NO_WORKPLACE + i, arr.build().toString());
			}
			
			// Remove all of these employees from original list
			lstEmployeeNoWorkplace.stream().forEach(employee -> {
				query.getEmployeeId().remove(employee);
			});
			
			// Stop sequence if no employee left
			if (query.getEmployeeId().isEmpty())
				throw new BusinessException(new RawErrorMessage("Msg_1396"));
		}
		
		List<WorkplaceConfigInfo> lstWorkplaceConfigInfo = this.convertData(workplaceInfoRepository.findByBaseDateWkpIds2(companyId, lstWorkplaceId, baseDate));
		queryData.setLstWorkplaceConfigInfo(lstWorkplaceConfigInfo);
		
		// Collect child workplace, automatically sort into tree map
		for (String entry: lstWorkplaceId) {
			lstWorkplace.putAll(collectWorkplaceHierarchy(entry, baseDate, lstWorkplaceConfigInfo));
		}
		
//		List<Integer> itemsId = AttendanceResultImportAdapter.convertList(outputItem.getLstDisplayedAttendance(), x -> x.getAttendanceDisplay());
		List<Integer> itemsId = lstHaveName;
		// Add additional item from remark input
		List<RemarksContentChoice> lstRemarkEnable = outputItem.getLstRemarkContent().stream().filter(x -> x.isUsedClassification()).map(x -> {
			return x.getPrintItem();
		}).collect(Collectors.toList());
		if (lstRemarkEnable.contains(RemarksContentChoice.REMARKS_INPUT))
			itemsId.add(outputItem.getRemarkInputNo().value + 833); // Remark no (0~4) -> attendanceId (833~837)
		
		List<AttendanceResultImport> lstAttendanceResultImport;
		
		//社員の指定期間中の所属期間を取得する RequestList 588
		List<StatusOfEmployee> statusEmps = this.symCompany.GetListAffComHistByListSidAndPeriod(query.getEmployeeId(), new DatePeriod(query.getStartDate(), query.getEndDate()));
		
		{
			List<AttendanceResultImport> resultsSync = Collections.synchronizedList(new ArrayList<>());
			this.parallel.forEach(query.getEmployeeId(), employeeId -> {
				Optional<StatusOfEmployee> statusOfEmployee = statusEmps.stream().filter(c-> c.getEmployeeId().equals(employeeId)).findFirst();
				if(statusOfEmployee.isPresent()){
					statusOfEmployee.get().getListPeriod().forEach(c-> {
						// Request list #332
						List<AttendanceResultImport> result = attendaceAdapter.getValueOf(Arrays.asList(employeeId), c, itemsId);
						resultsSync.addAll(result);
					});
				}
			});
			lstAttendanceResultImport = new ArrayList<>(resultsSync);
		}

		queryData.setLstAttendanceResultImport(lstAttendanceResultImport);
		
		// Extract list employeeId from attendance result list -> List employee won't have those w/o data
		List<String> lstEmployeeWithData = lstAttendanceResultImport.stream().map(AttendanceResultImport::getEmployeeId).distinct().collect(Collectors.toList());
		
		// From list employeeId above -> Find back their workplace hierachy code
		Set<String> lstWorkplaceIdWithData;
		lstWorkplaceIdWithData = lstEmployeeWithData.stream().map(employeeId -> {
			WkpHistImport workplaceImport = queryData.getLstWorkplaceImport().get(employeeId);
			List<WorkplaceConfigInfo> workplaceConfigInfos = lstWorkplaceConfigInfo.stream().filter(x -> {
				List<String> workplaceIds = new ArrayList<>();
				if (!x.getLstWkpHierarchy().isEmpty()) {
					workplaceIds.addAll(x.getLstWkpHierarchy().stream().map(t -> t.getWorkplaceId()).collect(Collectors.toList()));
				}
				return workplaceIds.contains(workplaceImport.getWorkplaceId());
			}).collect(Collectors.toList());
			
			List<WorkplaceHierarchy> workplaceHierarchies = workplaceConfigInfos.stream().flatMap(t -> t.getLstWkpHierarchy().stream()).collect(Collectors.toList());
			return workplaceHierarchies.stream()
					.filter(t -> t.getWorkplaceId().equalsIgnoreCase(workplaceImport.getWorkplaceId()))
					.findFirst()
					.map(t -> t.getHierarchyCode().v())
					.orElse("");
		}).collect(Collectors.toSet());
//#102989 - ea_3011 && ea_3012
//		// This employee list with data, find out all other employees who don't have data.
//		List<String> lstEmployeeIdNoData = query.getEmployeeId().stream().filter(x -> !lstEmployeeWithData.contains(x)).collect(Collectors.toList());
//		if (!lstEmployeeIdNoData.isEmpty()) {
//			List<EmployeeDto> lstEmployeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeIdNoData);
//			int numOfChunks = (int)Math.ceil((double)lstEmployeeDto.size() / LIMIT_DATA_PACK);
//			int start, length;
//			List<EmployeeDto> lstSplitEmployeeDto;
//			for(int i = 0; i < numOfChunks; i++) {
//				start = i * LIMIT_DATA_PACK;
//	            length = Math.min(lstEmployeeDto.size() - start, LIMIT_DATA_PACK);
//
//	            lstSplitEmployeeDto = lstEmployeeDto.subList(start, start + length);
//	            
//	            // Convert to json array
//	            JsonArrayBuilder arr = Json.createArrayBuilder();
//	    		
//	    		for (EmployeeDto employee : lstSplitEmployeeDto) {
//	    			arr.add(employee.buildJsonObject());
//	    		}
//	            
//	            setter.setData(DATA_PREFIX + i, arr.build().toString());
//			}
//		}
		
		
		// Check lowest level of employee and highest level of output setting, and attendance result count is 0
		// 階層累計行のみ出力する設定の場合、データ取得件数は0件として扱い、エラーメッセージを表示(#Msg_37#)
		int lowestEmployeeLevel = checkLowestWorkplaceLevel(lstWorkplaceIdWithData); // Get lowest possible workplace level -> lowestEmployeeLevel
		WorkScheduleSettingTotalOutput totalOutputCondition = condition.getSettingDetailTotalOutput();
		TotalWorkplaceHierachy outputSetting = condition.getSettingDetailTotalOutput().getWorkplaceHierarchyTotal();
		int highestOutputLevel = outputSetting.getHighestLevelEnabled();
		if ((lowestEmployeeLevel < highestOutputLevel && !totalOutputCondition.isDetails() && !totalOutputCondition.isGrossTotal()
				&& !totalOutputCondition.isPersonalTotal() && !totalOutputCondition.isPersonalTotal() && !totalOutputCondition.isTotalNumberDay()
				&& !totalOutputCondition.isWorkplaceTotal()) || lstAttendanceResultImport.size() == 0) {
			throw new BusinessException(new RawErrorMessage("Msg_37"));
		}
		
		/*
		 *  Collect all data which uses value type = code
		 */
		// 勤務種類を取得する
		if (itemsId.stream().filter(x -> IntStream.of(ATTENDANCE_ID_WORK_TYPE).anyMatch(y -> x == y)).count() > 0) {
			List<WorkType> lstWorkType = worktypeRepo.findByCompanyId(companyId);
			queryData.setLstWorkType(lstWorkType);
		}
		// 就業時間帯を取得する
		if (itemsId.stream().filter(x -> IntStream.of(ATTENDANCE_ID_WORK_TIME).anyMatch(y -> x == y)).count() > 0) {
			List<WorkTimeSetting> lstWorkTime = workTimeRepo.findByCompanyId(companyId);
			queryData.setLstWorkTime(lstWorkTime);
		}
		// 職場を取得する
		if (itemsId.stream().filter(x -> ATTENDANCE_ID_WORKPLACE == x).count() > 0) {
			List<CodeName> lstWorkplaceInfo = dataProcessor.getWorkPlace(companyId, endDate).getCodeNames();
			queryData.setLstWorkplaceInfo(lstWorkplaceInfo);
		}
		// 勤務場所を取得する
		if (itemsId.stream().filter(x -> IntStream.of(ATTENDANCE_ID_WORK_LOCATION).anyMatch(y -> x == y)).count() > 0) {
			List<CodeName> lstWorkLocation = dataProcessor.getServicePlace(companyId).getCodeNames();
			queryData.setLstWorkLocation(lstWorkLocation);
		}
		// 乖離理由を取得する
		if (itemsId.stream().filter(x -> IntStream.of(ATTENDANCE_ID_REASON).anyMatch(y -> x == y)).count() > 0) {
			List<CodeName> lstReason = dataProcessor.getReason(companyId).getCodeNames();
			queryData.setLstReason(lstReason);
		}
		// 分類を取得する
		if (itemsId.stream().filter(x -> IntStream.of(ATTENDANCE_ID_CLASSIFICATION).anyMatch(y -> x == y)).count() > 0) {
			List<CodeName> lstClassification = dataProcessor.getClassification(companyId).getCodeNames();
			queryData.setLstClassification(lstClassification);
		}
		// 職位を取得する
		if (itemsId.stream().filter(x -> ATTENDANCE_ID_POSITION == x).count() > 0) {
			List<CodeName> lstPosition = dataProcessor.getPossition(companyId, endDate).getCodeNames();
			queryData.setLstPosition(lstPosition);
		}
		// 雇用を取得する
		if (itemsId.stream().filter(x -> ATTENDANCE_ID_EMPLOYMENT == x).count() > 0) {
			List<CodeName> lstEmployment = dataProcessor.getEmployment(companyId).getCodeNames();
			queryData.setLstEmployment(lstEmployment);
		}
		//勤務種別を取得する
		if (itemsId.stream().filter(x -> ATTENDANCE_ID_BUSSINESS_TYPE == x).count() > 0) {
			List<CodeName> lstBusiness = dataProcessor.getBussinessType(companyId).getCodeNames();
			queryData.setLstBusiness(lstBusiness);
		}
		// 加給
		if (itemsId.stream().filter(x -> ATTENDANCE_ID_ADDITION == x).count() > 0) {
			List<CodeName> lstAddtition = dataProcessor.getAddition(companyId).getCodeNames();
			queryData.setLstAddition(lstAddtition);
		}
		// 作業
		if (itemsId.stream().filter(x -> IntStream.of(ATTENDANCE_ID_ADDITION).anyMatch(y -> x == y)).count() > 0) {
			List<CodeName> lstWork = dataProcessor.getWork(companyId).getCodeNames();
			queryData.setLstWork(lstWork);
		}
		// Collect optional item from KMK002 if there is at least 1 attendance id fall into id 641 -> 740
		List<Integer> lstOptionalAttendanceId = itemsId.stream()
				.filter(x -> x >= ATTENDANCE_ID_OPTIONAL_START && x <= ATTENDANCE_ID_OPTIONAL_END)
				.map(x -> x - 640)
				.collect(Collectors.toList());
		if (!lstOptionalAttendanceId.isEmpty()) {
			List<OptionalItem> lstOptionalItem = optionalItemRepo.findByListNos(companyId, lstOptionalAttendanceId);
			queryData.setLstOptionalItem(lstOptionalItem);
		}
		
		// Sort attendance display
		List<AttendanceItemsDisplay> lstAttendanceItemsDisplay = outputItem.getLstDisplayedAttendance().stream()
				.filter(item -> itemsId.contains(item.getAttendanceDisplay()))
				.sorted((o1,o2) -> o1.getOrderNo() - o2.getOrderNo()).collect(Collectors.toList());
		queryData.setLstDisplayItem(lstAttendanceItemsDisplay);
		//帳票種別をチェックする
		if (condition.getOutputType() == FormOutputType.BY_EMPLOYEE) {
			WorkplaceReportData data = new WorkplaceReportData();
			data.workplaceCode = "";
			reportData.setWorkplaceReportData(data);
			
			analyzeInfoExportByEmployee(lstWorkplace, data);
			
			List<EmployeeDto> lstEmloyeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeWithData).stream().sorted((o1, o2)->o1.getEmployeeCode().
                    compareTo(o2.getEmployeeCode())).
                    collect(Collectors.toList());
			
//			// Put these dto into print order list
//			lstEmloyeeDto.stream().forEach(dto -> {
//				lstEmployeePrintOrder.stream().filter(employee -> StringUtils.equals(employee.getEmployeeId(), dto.getEmployeeId())).findFirst().get().setEmployeeDto(Optional.of(dto));
//			});
//			
//			// Filter out all employee w/o data, workplace (no employee dto)
//			List<EmployeePrintOrder> lstEmployeePrintOrderWithData = lstEmployeePrintOrder.stream().filter(employee -> employee.getEmployeeDto().isPresent()).collect(Collectors.toList());
//			
//			// Convert print order list back into list employee dto so we can keep the correct printing order as inputed
//			lstEmloyeeDto = lstEmployeePrintOrderWithData.stream().map(employee -> employee.getEmployeeDto().get()).collect(Collectors.toList());
			
			for (EmployeeDto dto : lstEmloyeeDto) {

				statusEmps.stream().filter(x -> !StringUtil.isNullOrEmpty(x.getEmployeeId(), false)
						&& x.getEmployeeId().equals(dto.getEmployeeId())).findFirst()
						.ifPresent(status -> {
							// アルゴリズム「個人別の日別勤務表を作成する」を実行する
							status.getListPeriod().forEach(periodDate -> {
								DateRange range = new DateRange(periodDate.start(), periodDate.end());
								queryData.setDatePeriod(range.toListDate());
								EmployeeReportData employeeReportData = collectEmployeePerformanceDataByEmployee(
										reportData, queryData, dto, dataRowCount);

								// Calculate total count day
								if (condition.getSettingDetailTotalOutput().isTotalNumberDay()) {
									employeeReportData.totalCountDay = totalDayCountWs.calculateAllDayCount(
											dto.getEmployeeId(),
											new DateRange(query.getStartDate(), query.getEndDate()),
											employeeReportData.totalCountDay);
								}
							});
							
						});

			}
			//取得件数を確認する
			calculateTotalExportByEmployee(data, lstAttendanceItemsDisplay, condition.getZeroDisplayType());
		}
		else {
			List<EmployeesStatusByDate> empStatusList = new ArrayList<>();
			//Imported(就業)「社員の在職状態」を取得する
			List<EmploymentStatus> employmentStatus = this.empStatusPub.findListOfEmployee(lstEmployeeWithData,
					period);
			employmentStatus.forEach(empStatus -> {
				// RQ433で取得した社員在職データを日付毎のデータに並び替える
				List<EmploymentInfo> empInfos = empStatus.getEmploymentInfo().stream()
						.filter(x -> x.getEmploymentState().equals(EmploymentState.INCUMBENT))
						.collect(Collectors.toList());
				empStatus.setEmploymentInfo(empInfos);
			});
			for (int i = 0; i < period.datesBetween().size(); i++) {
				GeneralDate targetDate = period.start().addDays(i);
				List<String> employees = new ArrayList<>();
				employmentStatus.forEach(emp -> {
					Optional<EmploymentInfo> empInfoOpt = emp.getEmploymentInfo().stream()
							.filter(x -> x.getStandardDate().equals(targetDate)).findFirst();
					if (empInfoOpt.isPresent()) {
						employees.add(emp.getEmployeeId());
					}
				});
				if(!employees.isEmpty()) {
					empStatusList.add(new EmployeesStatusByDate(targetDate, employees));
				}
			}
			
			DailyReportData dailyReportData = new DailyReportData();
			reportData.setDailyReportData(dailyReportData);
			List<WorkplaceDailyReportData> lstReportData = empStatusList.stream().map(x -> {
				WorkplaceDailyReportData dailyWorkplaceReportData = new WorkplaceDailyReportData();
				dailyWorkplaceReportData.lstWorkplaceData = new DailyWorkplaceData();
				dailyWorkplaceReportData.lstWorkplaceData.workplaceCode = "";
				dailyWorkplaceReportData.date = x.getTargetDate();
				return dailyWorkplaceReportData;
			}).collect(Collectors.toList());
			dailyReportData.setLstDailyReportData(lstReportData);
			
			final Map<String, WorkplaceInfo> lstWorkplaceTemp = lstWorkplace;
			List<String> lstAddedCode = new ArrayList<>();
			lstReportData.forEach(x -> {
				analyzeInfoExportByDate(lstWorkplaceTemp, x.getLstWorkplaceData(), lstAddedCode);
			});
			empStatusList.forEach(empStatus->{
				DateRange range = new DateRange(empStatus.getTargetDate(), empStatus.getTargetDate());
				queryData.getQuery().setEmployeeId(empStatus.getEmployees());
				queryData.setDatePeriod(range.toListDate());
				//アルゴリズム「日付別の日別勤務表を作成する」を実行する
				this.collectEmployeePerformanceDataByDate(reportData, queryData, dataRowCount);
			});
			
			// Calculate workplace total
			lstReportData.forEach(dailyData -> {
				calculateTotalExportByDate(dailyData.getLstWorkplaceData());
			});
			
			// Calculate gross total
			calculateGrossTotalByDate(dailyReportData);
		}
	}
	
	/**
	 * Check lowest workplace level.
	 *
	 * @param lstWorkplaceId the lst workplace id
	 * @return the int
	 */
	private int checkLowestWorkplaceLevel(Set<String> lstWorkplaceId) {
		int lowestLevel = 0;
		
		for (String workplaceId: lstWorkplaceId) {
			int level = workplaceId.length() / 3;
			if (lowestLevel < level) {
				lowestLevel = level;
			}
		}
		
		return lowestLevel;
	}
	
	/**
	 * Collect employee performance data by date.
	 * 日付別の日別勤務表を作成する
	 * @param reportData the report data
	 * @param queryData the query data
	 * @param dataRowCount the data row count
	 */
	private void collectEmployeePerformanceDataByDate(DailyPerformanceReportData reportData, WorkScheduleQueryData queryData, int dataRowCount) {
		String companyId = AppContexts.user().companyId();
		WorkScheduleOutputQuery query = queryData.getQuery();
		GeneralDate endDate = query.getEndDate();
		WorkScheduleOutputCondition condition = query.getCondition();

		String layoutId = condition.getSelectionType() == ItemSelectionType.FREE_SETTING
				? condition.getFreeSettingLayoutId()
				: condition.getStandardSelectionLayoutId();

		// Always has item because this has passed error check
		OutputItemDailyWorkSchedule outSche = outputItemRepo.findByLayoutId(layoutId).get();
		
		// Get all data from query data container
		List<GeneralDate> datePeriod = queryData.getDatePeriod();
		Map<String, Map<GeneralDate, AttendanceResultImport>> lstAttendanceResultImport = queryData.getLstAttendanceResultImport()
				.stream().collect(Collectors.groupingBy(AttendanceResultImport::getEmployeeId, Collectors.toMap(AttendanceResultImport::getWorkingDate, Function.identity())));
		List<AttendanceItemsDisplay> lstDisplayItem = queryData.getLstDisplayItem();
		Map<String, WkpHistImport> lstWorkplaceHistImport = queryData.getLstWorkplaceImport();
		List<OptionalItem> lstOptionalItem = queryData.getLstOptionalItem();
		
		// Get all error alarm code
		List<ErrorAlarmWorkRecordCode> lstErAlCode = condition.getErrorAlarmCode().get();
		
		List<String> lstEmployeeId = query.getEmployeeId();
		Map<String, EmployeeDto> lstEmployeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeId).stream().collect(Collectors.toMap(EmployeeDto::getEmployeeId, Function.identity()));
		
		List<EmployeeDailyPerError> errorListAllEmployee = errorAlarmRepository.finds(lstEmployeeId, new DatePeriod(query.getStartDate(), endDate));
		// Get list remark check box from screen C (UI)
		List<PrintRemarksContent> lstRemarkContent = outSche.getLstRemarkContent();

        List<String> tempErrorCode = errorListAllEmployee.stream().map(x -> x.getErrorAlarmWorkRecordCode().v()).distinct().collect(Collectors.toList());
		List<ErrorAlarmWorkRecord> lstAllErrorRecord = errorAlarmWorkRecordRepo.getListErAlByListCode(companyId, tempErrorCode);
		
		Map<GeneralDate, WorkplaceDailyReportData> workplaceDailyReportDataMap = reportData.getDailyReportData().getLstDailyReportData().stream().collect(Collectors.toMap(WorkplaceDailyReportData::getDate, Function.identity()));
		
		for (String employeeId: lstEmployeeId) {
			EmployeeDto optEmployeeDto = lstEmployeeDto.get(employeeId);
			
			datePeriod.stream().forEach(date -> {
				if (!workplaceDailyReportDataMap.containsKey(date)) {
					return;
				}
				WorkplaceDailyReportData optDailyWorkplaceData = workplaceDailyReportDataMap.get(date);
				DailyWorkplaceData dailyWorkplaceData = findWorkplace(employeeId, optDailyWorkplaceData.getLstWorkplaceData(), query.getBaseDate(), lstWorkplaceHistImport, queryData.getLstWorkplaceConfigInfo());
				if (dailyWorkplaceData != null) {
					DailyPersonalPerformanceData personalPerformanceDate = new DailyPersonalPerformanceData();
					if (optEmployeeDto != null)
					{
						personalPerformanceDate.setEmployeeName(optEmployeeDto.getEmployeeName());
						personalPerformanceDate.setEmployeeCode(optEmployeeDto.getEmployeeCode());
					}
					dailyWorkplaceData.getLstDailyPersonalData().add(personalPerformanceDate);

					AttendanceResultImport attResultImport = lstAttendanceResultImport.get(employeeId).get(date);
					if (attResultImport != null) {
						GeneralDate workingDate = attResultImport.getWorkingDate();
						
						// ドメインモデル「日別勤務表の出力項目」を取得する
						List<EmployeeDailyPerError> errorList = errorListAllEmployee.stream()
								.filter(error -> StringUtils.equalsAnyIgnoreCase(error.getEmployeeID(), employeeId) && error.getDate().compareTo(workingDate) == 0).collect(Collectors.toList());
						// Manually set error list into remark query data container
						queryData.getRemarkDataContainter().setErrorList(errorList);
						
						List<String> lstRemarkContentStr = new ArrayList<>();
						if (query.getCondition().getErrorAlarmCode().isPresent()) {
							personalPerformanceDate.detailedErrorData = "";
							
							lstRemarkContent.stream().filter(remark -> remark.isUsedClassification()).forEach(remark -> {
								
								// Append 備考入力
								if (remark.getPrintItem() == RemarksContentChoice.REMARKS_INPUT) {
									Optional<AttendanceItemValueImport> optRemarkInput = attResultImport.getAttendanceItems().stream()
											.filter(att -> att.getItemId() == outSche.getRemarkInputNo().value + 833)
											.findFirst();
									if (optRemarkInput.isPresent()) {
										String value = optRemarkInput.get().getValue();
										if(value != null) {
											value = StringLength.cutOffAsLengthHalf(value, LIMIT_REMARK_INPUT);
										}
										lstRemarkContentStr.add(value == null? "" : value);
									}
								// Append マスタ未登録
								} else if (remark.getPrintItem() == RemarksContentChoice.MASTER_UNREGISTERED) {
									List<AttendanceItemValueImport> lstItemMasterUnregistered = attResultImport.getAttendanceItems().stream().
											filter(att -> EnumAdaptor.valueOf(att.getValueType(), ValueType.class) == ValueType.CODE)
											.collect(Collectors.toList());
									
									List<String> names = lstItemMasterUnregistered.stream()
											.map(item -> this.getNameFromCode(item.getItemId(),
													item.getValue(), queryData, outSche, condition.getSwitchItemDisplay()))
											.collect(Collectors.toList());

									boolean masterUnregistedFlag = names.stream()
											.anyMatch(item -> item.contains(MASTER_UNREGISTERED));
										
									if (masterUnregistedFlag) {
										lstRemarkContentStr.add(TextResource.localize(RemarksContentChoice.MASTER_UNREGISTERED.shortName));
									}
								// append 乖離理由
								} else if (remark.getPrintItem() == RemarksContentChoice.DIVERGENCE_REASON) {
									// 乖離理由コードある場合
									List<AttendanceItemValueImport> attendanceItemReason = attResultImport.getAttendanceItems().stream()
											.filter(attendance -> IntStream.of(ATTENDANCE_ID_REASON).anyMatch(id -> id == attendance.getItemId()))
											.collect(Collectors.toList());
									List<String> names = attendanceItemReason.stream()
											.map(item -> this.getNameFromCode(item.getItemId(),
													item.getValue(), queryData, outSche, condition.getSwitchItemDisplay()))
											.filter(Objects::nonNull)
											.collect(Collectors.toList());
									lstRemarkContentStr.addAll(names.stream()
											.map(t -> this.cutStringByByte(t, 0, 18))
											.collect(Collectors.toList()));
									// 乖離理由手入力の場合
									List<String> lstStrReason = attResultImport.getAttendanceItems().stream()
													.filter(attendance -> IntStream.of(ATTENDANCE_ID_REASON_DIVERGENCE)
															.anyMatch(id -> id == attendance.getItemId())
															&& !StringUtil.isNullOrEmpty(attendance.getValue(), false))
											.map(t -> this.cutStringByByte(t.getValue(), 0, 18))
											.collect(Collectors.toList());
									lstRemarkContentStr.addAll(lstStrReason);
								}  else {
									// Get possible content based on remark choice
									Optional<PrintRemarksContent> optContent = getRemarkContent(employeeId // 社員ID
											, workingDate // 期間(年月日)
											, remark.getPrintItem() // 日別勤務表の出力項目.備考内容
											, lstErAlCode // 日別勤務表の出力条件.エラーアラームコード
											, queryData.getRemarkDataContainter());
									if (optContent.isPresent()) {
										// Add to remark
										lstRemarkContentStr.add(TextResource.localize(optContent.get().getPrintItem().shortName));
									}
								}
							});
							
							// Analyze remark content size and append remark content list
							StringBuilder stringBuilder = new StringBuilder();
							int maxByte = 35;
							for (int i = 0; i < lstRemarkContentStr.size(); i++) {
								String remarkContentStr = lstRemarkContentStr.get(i);
								int numberLine = stringBuilder.toString().split("/n").length;
								int currentStrLenght = lstRemarkContentStr.get(i).getBytes().length;
								if (numberLine <= dataRowCount && maxByte >= currentStrLenght) {
									maxByte = maxByte - currentStrLenght;
									if (!StringUtil.isNullOrEmpty(lstRemarkContentStr.get(i), false)) {
										stringBuilder.append(" ").append(remarkContentStr);
										maxByte = maxByte - 1;
									}
									if (numberLine == dataRowCount
										&& (i + 1) < lstRemarkContentStr.size()
										&& maxByte <= (lstRemarkContentStr.get(i + 1).getBytes().length + 8)) {
										stringBuilder.append(" 他").append(lstRemarkContentStr.size() - i).append("件");
										break;
									}
								} else {
									stringBuilder.append("/n").append(remarkContentStr);
									maxByte = 35 - remarkContentStr.getBytes().length;
								}
							}
                            personalPerformanceDate.detailedErrorData  += stringBuilder.toString();
						}
						
						// ER/AL
						boolean erMark = false, alMark = false;
						
						List<String> lstErrorCode = errorList.stream().map(error -> error.getErrorAlarmWorkRecordCode().v()).collect(Collectors.toList());
						// ドメインモデル「勤務実績のエラーアラーム」を取得する
						
						List<ErrorAlarmWorkRecord> lstErrorRecord = lstAllErrorRecord.stream().filter(err -> {
							return  lstErrorCode.contains(err.getCode().v());
						}).collect(Collectors.toList());
						
						for (ErrorAlarmWorkRecord error : lstErrorRecord) {
							// コードから区分を取得する
							switch (error.getTypeAtr()) {
							case ALARM: // 区分　＝　エラー　のとき　AL
								alMark = true; 
								break;
							case ERROR: // 区分　=　アラーム　のとき　ER
								erMark = true;
								break;
							case OTHER:
								break;
							}
							if (erMark && alMark) break;
						}
						if (erMark && alMark) {
							personalPerformanceDate.errorAlarmCode = WorkScheOutputConstants.ER_AL;
						} else if (erMark) {
							personalPerformanceDate.errorAlarmCode = WorkScheOutputConstants.ER;
						} else if (alMark) {
							personalPerformanceDate.errorAlarmCode = WorkScheOutputConstants.AL;
						}
						personalPerformanceDate.actualValue = new ArrayList<>();
						lstDisplayItem.stream().forEach(item -> {
							Optional<AttendanceItemValueImport> optItemValue = attResultImport.getAttendanceItems().stream().filter(att -> att.getItemId() == item.getAttendanceDisplay()).findFirst();
							if (optItemValue.isPresent()) {
								AttendanceItemValueImport itemValue = optItemValue.get();
								ValueType valueTypeEnum = EnumAdaptor.valueOf(itemValue.getValueType(), ValueType.class);
								int attendanceId = itemValue.getItemId();
								if ((valueTypeEnum == ValueType.CODE || valueTypeEnum == ValueType.ATTR) && itemValue.getValue() != null) {
									String value = this.getNameFromCode(attendanceId, itemValue.getValue(), queryData, outSche, condition.getSwitchItemDisplay());
									itemValue.setValue(value);
								}
								// Workaround for optional attendance item from KMK002
								if (attendanceId >= ATTENDANCE_ID_OPTIONAL_START && attendanceId <= ATTENDANCE_ID_OPTIONAL_END) {
									Optional<OptionalItem> optOptionalItem = lstOptionalItem.stream().filter(opt -> opt.getOptionalItemNo().v() == attendanceId - 640).findFirst();
									optOptionalItem.ifPresent(optionalItem -> {
										switch(optionalItem.getOptionalItemAtr()) {
										case TIME:
											//int val = Integer.parseInt(itemValue.getValue());
											itemValue.setValueType(ValueType.TIME.value);
											break;
										case NUMBER:
											itemValue.setValueType(ValueType.COUNT_WITH_DECIMAL.value);
											break;
										case AMOUNT:
											itemValue.setValueType(ValueType.AMOUNT.value);
											break;
										}
									});
									
//									try {
//										int val = Integer.parseInt(itemValue.getValue());
//										itemValue.setValueType(ValueType.TIME.value);
//									} catch (Exception e) {
//										itemValue.setValueType(ValueType.AMOUNT.value);
//									}
								}
								personalPerformanceDate.actualValue.add(new ActualValue(itemValue.getItemId(), itemValue.getValue(), itemValue.getValueType()));
							}
							else {
								personalPerformanceDate.actualValue.add(new ActualValue(item.getAttendanceDisplay(), "", ActualValue.STRING));
							}
						});
					}
				}
			});
			
			
		}
	}

	/**
	 * Collect employee performance data by employee.
	 * 個人別の日別勤務表を作成する
	 * @param reportData the report data
	 * @param queryData the query data
	 * @param employeeDto the employee dto
	 * @param dataRowCount the data row count
	 * @return the employee report data
	 */
	private EmployeeReportData collectEmployeePerformanceDataByEmployee(DailyPerformanceReportData reportData
			, WorkScheduleQueryData queryData
			, EmployeeDto employeeDto
			, int dataRowCount) {
		String companyId = AppContexts.user().companyId();
		WorkScheduleOutputQuery query = queryData.getQuery();
		GeneralDate endDate = query.getEndDate();
		WorkScheduleOutputCondition condition = query.getCondition();

		String layoutId = condition.getSelectionType() == ItemSelectionType.FREE_SETTING
				? condition.getFreeSettingLayoutId()
				: condition.getStandardSelectionLayoutId();

		// Always has item because this has passed error check
		OutputItemDailyWorkSchedule outSche = outputItemRepo.findByLayoutId(layoutId).get();
		
		// Get all data from query data container
		List<AttendanceResultImport> lstAttendanceResultImport = queryData.getLstAttendanceResultImport();
		List<AttendanceItemsDisplay> lstDisplayItem = queryData.getLstDisplayItem();
		Map<String, WkpHistImport> lstWorkplaceImport = queryData.getLstWorkplaceImport();
		List<WorkplaceConfigInfo> lstWorkplaceConfigInfo = queryData.getLstWorkplaceConfigInfo();
		List<OptionalItem> lstOptionalItem = queryData.getLstOptionalItem();
		String employeeId = employeeDto.getEmployeeId();
		
		List<ErrorAlarmWorkRecordCode> lstErAlCode = condition.getErrorAlarmCode().get();

		EmployeeReportData employeeData = new EmployeeReportData();
		
		// Employee code and name
		employeeData.employeeName = employeeDto.getEmployeeName();
		employeeData.employeeCode = employeeDto.getEmployeeCode();
		employeeData.employeeId = employeeId;
		
		// Employment
		Optional<EmploymentHistoryImported> optEmploymentImport = employmentAdapter.getEmpHistBySid(companyId, employeeId, endDate);
		if (optEmploymentImport.isPresent()) {
			String employmentCode = optEmploymentImport.get().getEmploymentCode();
			Optional<Employment> optEmployment = employmentRepository.findEmployment(companyId, employmentCode);
			if (optEmployment.isPresent()) {
				Employment employment = optEmployment.get();
				employeeData.employmentName = employment.getEmploymentName().v();
				employeeData.employmentCode = employment.getEmploymentCode().v();
			}
		}
		
		// Job title
		Optional<EmployeeJobHistExport> optJobTitle = jobTitleAdapter.findBySid(employeeId, endDate);
		if (optJobTitle.isPresent()) {
			employeeData.position = !StringUtil.isNullOrEmpty(optJobTitle.get().getJobTitleName(), false) ? optJobTitle.get().getJobTitleName() : "";
			employeeData.jobTitleCode = !StringUtil.isNullOrEmpty(optJobTitle.get().getJobCode(), false) ? optJobTitle.get().getJobCode() : "";
		} else {
			employeeData.position = "";
			employeeData.jobTitleCode = "";
		}
		
		employeeData.lstDetailedPerformance = new ArrayList<>();
		
		WorkplaceReportData workplaceData = findWorkplace(employeeId, reportData.getWorkplaceReportData(), query.getBaseDate(), lstWorkplaceImport, lstWorkplaceConfigInfo);
		if (workplaceData != null) {
			workplaceData.lstEmployeeReportData.add(employeeData);
		}
		
		lstAttendanceResultImport.stream()
			.filter(x -> x.getEmployeeId().equals(employeeId))
			.sorted((o1,o2) -> o1.getWorkingDate().compareTo(o2.getWorkingDate()))
			.forEach(x -> {

			GeneralDate workingDate = x.getWorkingDate();
			
			DetailedDailyPerformanceReportData detailedDate = new DetailedDailyPerformanceReportData();
			detailedDate.setDate(workingDate);
			detailedDate.setDayOfWeek(String.valueOf(workingDate.dayOfWeek()));
			employeeData.lstDetailedPerformance.add(detailedDate);
			
			// ドメインモデル「社員の日別実績エラー一覧」を取得する
			List<EmployeeDailyPerError> errorList = errorAlarmRepository.find(employeeId, workingDate);

			// Manually set error list into remark data container
			queryData.getRemarkDataContainter().setErrorList(errorList);
			
			List<String> lstRemarkContentStr = new ArrayList<>();
			detailedDate.errorDetail = "";
			if (query.getCondition().getErrorAlarmCode().isPresent()) {
				// Get list remark check box from screen C (UI)
				List<PrintRemarksContent> lstRemarkContent = outSche.getLstRemarkContent();
				lstRemarkContent.stream().filter(remark -> remark.isUsedClassification()).forEach(remark -> {
					// Append 備考入力
					if (remark.getPrintItem() == RemarksContentChoice.REMARKS_INPUT) {
						Optional<AttendanceItemValueImport> optRemarkInput = x.getAttendanceItems().stream()
								.filter(att -> att.getItemId() == outSche.getRemarkInputNo().value + 833)
								.findFirst();
						if (optRemarkInput.isPresent()) {
							String value = optRemarkInput.get().getValue();
							if (value != null) {
								value = StringLength.cutOffAsLengthHalf(value, LIMIT_REMARK_INPUT);
							}
							lstRemarkContentStr.add((value == null ? "" : value));
						}
					// Append マスタ未登録
					} else if (remark.getPrintItem() == RemarksContentChoice.MASTER_UNREGISTERED) {
						List<AttendanceItemValueImport> lstItemMasterUnregistered = x.getAttendanceItems().stream()
								.filter(att -> EnumAdaptor.valueOf(att.getValueType(), ValueType.class) == ValueType.CODE)
								.collect(Collectors.toList());
						
						List<String> names = lstItemMasterUnregistered.stream()
								.map(item -> this.getNameFromCode(item.getItemId(),
										item.getValue(), queryData, outSche, condition.getSwitchItemDisplay()))
								.collect(Collectors.toList());

						boolean masterUnregistedFlag = names.stream()
								.anyMatch(item -> item.contains(MASTER_UNREGISTERED));
							
						if (masterUnregistedFlag) {
							lstRemarkContentStr.add(TextResource.localize(RemarksContentChoice.MASTER_UNREGISTERED.shortName));
						}
					// Append 乖離理由
					} else if (remark.getPrintItem() == RemarksContentChoice.DIVERGENCE_REASON) {
						// 乖離理由コードある場合
						List<AttendanceItemValueImport> attendanceItemReason = x.getAttendanceItems().stream()
								.filter(attendance -> IntStream.of(ATTENDANCE_ID_REASON).anyMatch(id -> id == attendance.getItemId()))
								.collect(Collectors.toList());
						List<String> names = attendanceItemReason.stream()
								.map(item -> this.getNameFromCode(item.getItemId(),
										item.getValue(), queryData, outSche, condition.getSwitchItemDisplay()))
								.filter(Objects::nonNull)
								.collect(Collectors.toList());
						lstRemarkContentStr.addAll(names.stream()
								.map(t -> this.cutStringByByte(t, 0, 18))
								.collect(Collectors.toList()));
						// 乖離理由手入力の場合
						List<String> lstStrReason = x.getAttendanceItems().stream()
										.filter(attendance -> IntStream.of(ATTENDANCE_ID_REASON_DIVERGENCE)
												.anyMatch(id -> id == attendance.getItemId())
												&& !StringUtil.isNullOrEmpty(attendance.getValue(), false))
								.map(t -> this.cutStringByByte(t.getValue(), 0, 18))
								.collect(Collectors.toList());
						lstRemarkContentStr.addAll(lstStrReason);
					} else {
						// Get other possible content based on remark choice
						Optional<PrintRemarksContent> optContent = getRemarkContent(employeeId
								, workingDate
								, remark.getPrintItem() // 日別勤務表の出力項目.備考内容
								, lstErAlCode 
								, queryData.getRemarkDataContainter());
						if (optContent.isPresent()) {
							// Add to remark
							lstRemarkContentStr.add(TextResource.localize(optContent.get().getPrintItem().shortName));
						}
					}
				});
				
				// Analyze remark content size and append remark content list
				StringBuilder errorDetails = new StringBuilder();
				int maxByte = 35;
				for (int i = 0; i < lstRemarkContentStr.size(); i++) {
					String remarkContentStr = lstRemarkContentStr.get(i);
					int numberLine = errorDetails.toString().split("/n").length;
					int currentStrLenght = lstRemarkContentStr.get(i).getBytes().length;
					if (numberLine <= dataRowCount && maxByte >= currentStrLenght) {
						maxByte = maxByte - currentStrLenght;
						if (!StringUtil.isNullOrEmpty(lstRemarkContentStr.get(i), false)) {
							errorDetails.append(" ").append(remarkContentStr);
							maxByte = maxByte - 1;
						}
						if (numberLine == dataRowCount
							&& (i + 1) < lstRemarkContentStr.size()
							&& maxByte <= (lstRemarkContentStr.get(i + 1).getBytes().length + 8)) {
							errorDetails.append(" 他").append(lstRemarkContentStr.size() - i).append("件");
							break;
						}
					} else {
						errorDetails.append("/n").append(remarkContentStr);
						maxByte = 35 - remarkContentStr.getBytes().length;
					}
				}
				detailedDate.errorDetail += errorDetails.toString();
			}
			
			// ER/AL
			//if (query.getCondition().getConditionSetting() == OutputConditionSetting.USE_CONDITION) {
				boolean erMark = false, alMark = false;
				List<String> lstErrorCode = errorList.stream().map(error -> error.getErrorAlarmWorkRecordCode().v()).collect(Collectors.toList());
				// ドメインモデル「勤務実績のエラーアラーム」を取得する
				List<ErrorAlarmWorkRecord> lstErrorRecord = errorAlarmWorkRecordRepo.getListErAlByListCode(companyId, lstErrorCode);
				
				for (ErrorAlarmWorkRecord error : lstErrorRecord) {
					// コードから区分を取得する
					switch (error.getTypeAtr()) {
					case ALARM: // 区分　＝　エラー　のとき　AL
						alMark = true; 
						break;
					case ERROR: // 区分　=　アラーム　のとき　ER
						erMark = true;
						break;
					case OTHER:
						break;
					}
					if (erMark && alMark) break;
				}
				if (erMark && alMark) {
					detailedDate.errorAlarmMark = WorkScheOutputConstants.ER_AL;
				}
				else if (erMark) {
					detailedDate.errorAlarmMark = WorkScheOutputConstants.ER;
				}
				else if (alMark) {
					detailedDate.errorAlarmMark = WorkScheOutputConstants.AL;
				}
			//}
			
			detailedDate.actualValue = new ArrayList<>();
			lstDisplayItem.stream().forEach(item -> {
				Optional<AttendanceItemValueImport> optItemValue = x.getAttendanceItems().stream().filter(att -> att.getItemId() == item.getAttendanceDisplay()).findFirst();
				if (optItemValue.isPresent()) {
					AttendanceItemValueImport itemValue = optItemValue.get();
					ValueType valueTypeEnum = EnumAdaptor.valueOf(itemValue.getValueType(), ValueType.class);
					int attendanceId = itemValue.getItemId();
					if ((valueTypeEnum == ValueType.CODE || valueTypeEnum == ValueType.ATTR) && itemValue.getValue() != null) {
						String value = this.getNameFromCode(attendanceId, itemValue.getValue(), queryData, outSche, condition.getSwitchItemDisplay());
						itemValue.setValue(value);
					}
					// Workaround for optional attendance item from KMK002
					if (attendanceId >= ATTENDANCE_ID_OPTIONAL_START && attendanceId <= ATTENDANCE_ID_OPTIONAL_END) {
						Optional<OptionalItem> optOptionalItem = lstOptionalItem.stream().filter(opt -> opt.getOptionalItemNo().v() == attendanceId - 640).findFirst();
						optOptionalItem.ifPresent(optionalItem -> {
							switch(optionalItem.getOptionalItemAtr()) {
							case TIME:
								itemValue.setValueType(ValueType.TIME.value);
								break;
							case NUMBER:
								itemValue.setValueType(ValueType.COUNT_WITH_DECIMAL.value);
								break;
							case AMOUNT:
								itemValue.setValueType(ValueType.AMOUNT.value);
								break;
							}
						}); 
					}
					
					detailedDate.actualValue.add(new ActualValue(attendanceId, itemValue.getValue(), itemValue.getValueType()));
				}
				else {
					detailedDate.actualValue.add(new ActualValue(item.getAttendanceDisplay(), "", ActualValue.STRING));
				}
			});
		});
		return employeeData;
	}
	
	/**
	 * Calculate total export by employee.
	 * 取得件数を確認する
	 * @param workplaceData the workplace data
	 * @param lstAttendanceId the lst attendance id
	 */
	private void calculateTotalExportByEmployee(WorkplaceReportData workplaceData, List<AttendanceItemsDisplay> lstAttendanceId, ZeroDisplayType displayType) {
		// Recursive
		workplaceData.getLstChildWorkplaceReportData().values().forEach(x -> {
			calculateTotalExportByEmployee(x, lstAttendanceId, displayType);
		});
		// Workplace
		workplaceData.workplaceTotal = new WorkplaceTotal();
		workplaceData.workplaceTotal.setWorkplaceId(workplaceData.getWorkplaceCode());
		List<TotalValue> lstTotalValue = new ArrayList<>();
		workplaceData.workplaceTotal.setTotalWorkplaceValue(lstTotalValue);
		
		// Calculate total of child workplaces
		final List<TotalValue> lstWorkplaceGrossTotal = lstAttendanceId.stream().map(item -> {
			TotalValue totalVal = new TotalValue();
			totalVal.setAttendanceId(item.getAttendanceDisplay());
			totalVal.setValue("");
			totalVal.setValueType(TotalValue.STRING);
			return totalVal;
		}).collect(Collectors.toList());
		workplaceData.setGrossTotal(lstWorkplaceGrossTotal);
		workplaceData.getLstChildWorkplaceReportData().values().forEach(child -> {
			child.getGrossTotal().stream().forEach(val -> {
				if (val.value() == null) return;
				int attendanceId = val.getAttendanceId();
				TotalValue totalVal = lstWorkplaceGrossTotal.stream().filter(attendance -> attendance.getAttendanceId() == attendanceId).findFirst().get();
				ValueType totalValueTypeEnum = EnumAdaptor.valueOf(totalVal.getValueType(), ValueType.class);
				ValueType valueTypeEnum = EnumAdaptor.valueOf(val.getValueType(), ValueType.class);
				if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
					totalVal.addValue(val.value(), totalValueTypeEnum);
					totalVal.setValueType(val.getValueType());
					totalVal.setUnit(val.getUnit());
				}
			});
		});
		
		// Calculate total of employees of current level (after calculating total of child workplace)
		List<EmployeeReportData> lstReportData = workplaceData.getLstEmployeeReportData();
		if (lstReportData != null && lstReportData.size() > 0) {
			workplaceData.getLstEmployeeReportData().stream().forEach(employeeData -> {
				// Put attendanceId into map
				lstAttendanceId.stream().forEach(attendanceId -> {
					int attendanceDisplay = attendanceId.getAttendanceDisplay();
					if (!employeeData.mapPersonalTotal.containsKey(attendanceDisplay)) {
						employeeData.mapPersonalTotal.put(attendanceDisplay, new TotalValue(attendanceDisplay, "", TotalValue.STRING, ""));
						TotalValue totalVal = new TotalValue();
						totalVal.setAttendanceId(attendanceDisplay);
						totalVal.setValue("");
						totalVal.setValueType(TotalValue.STRING);
						Optional<TotalValue> optWorkplaceTotalValue = lstTotalValue.stream().filter(x -> x.getAttendanceId() == attendanceDisplay).findFirst();
						if (!optWorkplaceTotalValue.isPresent()) {
							lstTotalValue.add(totalVal);
						}
					}
				});
				
				// Add into total by attendanceId
				employeeData.getLstDetailedPerformance().stream().forEach(detail -> {
					detail.actualValue.stream().forEach((aVal) -> {
						int attdId = aVal.getAttendanceId();
						int valueType = aVal.getValueType();
						
						// Get all total
						ValueType valueTypeEnum = EnumAdaptor.valueOf(valueType, ValueType.class);
	                    TotalValue personalTotal = employeeData.mapPersonalTotal.get(attdId);
	                    TotalValue totalVal = lstTotalValue.stream().filter(attendance -> attendance.getAttendanceId() == attdId).findFirst().get();
	                    TotalValue totalGrossVal = lstWorkplaceGrossTotal.stream().filter(attendance -> attendance.getAttendanceId() == attdId).findFirst().get();
	                    
	                    // Change value type
	                    personalTotal.setValueType(valueType);
	                    personalTotal.setUnit(aVal.getUnit());
	                    totalVal.setValueType(valueType);
	                    totalVal.setUnit(aVal.getUnit());
	                    totalGrossVal.setValueType(valueType);
	                    totalGrossVal.setUnit(aVal.getUnit());
	                    
	                    if (aVal.value() == null) return;

	                    if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
	                    	personalTotal.addValue(aVal.value(), valueTypeEnum);
	                    	totalVal.addValue(aVal.value(), valueTypeEnum);
	                    	totalGrossVal.addValue(aVal.value(), valueTypeEnum);
	                    } else {
	                        personalTotal.setValue("");
	                        employeeData.mapPersonalTotal.put(attdId, personalTotal);
	                        totalVal.setValue("");
	                        totalGrossVal.setValue("");
	                    }
					});
				});
			});
		}
		
		// Calculate pure gross total of root workplace
		if (workplaceData.level == 0) {
			List<TotalValue> lstTotalVal = new ArrayList<>();
			workplaceData.setGrossTotal(lstTotalVal);
			
			// Sum of all workplace total and workplace hierarchy total
			workplaceData.lstChildWorkplaceReportData.forEach((k,child) -> {
				child.getGrossTotal().forEach(item -> {
					Optional<TotalValue> optTotalVal = lstTotalVal.stream().filter(x -> x.getAttendanceId() == item.getAttendanceId()).findFirst();
					if (optTotalVal.isPresent()) {
						TotalValue totalVal = optTotalVal.get();
						int valueType = item.getValueType();
						totalVal.setValueType(valueType);
						ValueType valueTypeEnum = EnumAdaptor.valueOf(valueType, ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							totalVal.addValue(item.value(), valueTypeEnum);
						} else {
							totalVal.setValue("");
						}
						totalVal.setUnit(item.getUnit());
					} else {
						TotalValue totalVal = new TotalValue();
						totalVal.setAttendanceId(item.getAttendanceId());
						totalVal.setValue(item.getValue());
						totalVal.setValueType(item.getValueType());
						totalVal.setUnit(item.getUnit());
						lstTotalVal.add(totalVal);
					}
				});
			});
		}
	}
	
	/**
	 * Calculate total export by date.
	 *
	 * @param workplaceData the workplace data
	 */
	private void calculateTotalExportByDate(DailyWorkplaceData workplaceData) {
		// Recursive
		workplaceData.getLstChildWorkplaceData().values().forEach(x -> {
			calculateTotalExportByDate(x);
		});
		// Workplace
		workplaceData.workplaceTotal = new WorkplaceTotal();
		workplaceData.workplaceTotal.setWorkplaceId(workplaceData.getWorkplaceCode());
		List<TotalValue> lstTotalValue = new ArrayList<>();
		workplaceData.workplaceTotal.setTotalWorkplaceValue(lstTotalValue);
		workplaceData.workplaceHierarchyTotal = new WorkplaceTotal();
		workplaceData.workplaceHierarchyTotal.setWorkplaceId(workplaceData.getWorkplaceCode());
		List<TotalValue> lstTotalHierarchyValue = new ArrayList<>();
		workplaceData.workplaceHierarchyTotal.setTotalWorkplaceValue(lstTotalHierarchyValue);
		
		List<DailyPersonalPerformanceData> performanceData = workplaceData.getLstDailyPersonalData();
		
		// Employee
		if (performanceData != null && performanceData.size() > 0) {
			for (DailyPersonalPerformanceData data: performanceData) {
				List<ActualValue> lstActualValue = data.getActualValue();
				if (lstActualValue == null) continue;
				lstActualValue.forEach(actualValue -> {
					int valueType = actualValue.getValueType();
					Optional<TotalValue> optTotalVal = lstTotalValue.stream().filter(x -> x.getAttendanceId() == actualValue.getAttendanceId()).findFirst();
					TotalValue totalValue;
					if (optTotalVal.isPresent()) {
						if (actualValue.value() == null) return;
						totalValue = optTotalVal.get();
						int totalValueType = totalValue.getValueType();
						ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							totalValue.addValue(actualValue.value(), valueTypeEnum);
						}
						totalValue.setUnit(actualValue.getUnit());
					}
					else {
						totalValue = new TotalValue();
						totalValue.setAttendanceId(actualValue.getAttendanceId());
						ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							if (actualValue.getValue() != null) {
								totalValue.setValue(actualValue.getValue());
							} else {
								totalValue.setValue("0");
							}
						}
						totalValue.setUnit(actualValue.getUnit());
						totalValue.setValueType(valueType);
						lstTotalValue.add(totalValue);
					}
					
					Optional<TotalValue> optTotalWorkplaceVal = lstTotalHierarchyValue.stream().filter(x -> x.getAttendanceId() == actualValue.getAttendanceId()).findFirst();
					TotalValue totalWorkplaceValue;
					if (optTotalWorkplaceVal.isPresent()) {
						totalWorkplaceValue = optTotalWorkplaceVal.get();
						int totalValueType = totalValue.getValueType();
						ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							totalWorkplaceValue.addValue(actualValue.value(), valueTypeEnum);
						}
					}
					else {
						totalWorkplaceValue = new TotalValue();
						totalWorkplaceValue.setAttendanceId(actualValue.getAttendanceId());
						ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							if (actualValue.getValue() != null) {
								totalWorkplaceValue.setValue(actualValue.getValue());
							} else {
								totalWorkplaceValue.setValue("0");
							}
						}
						totalWorkplaceValue.setValueType(valueType);
						lstTotalHierarchyValue.add(totalWorkplaceValue);
					}
					totalWorkplaceValue.setUnit(actualValue.getUnit());
				});
			}
		}
		
		// Workplace
		Map<String, DailyWorkplaceData> lstReportData = workplaceData.getLstChildWorkplaceData();
		lstReportData.values().forEach((value) -> {
			List<TotalValue> lstActualValue = value.getWorkplaceTotal().getTotalWorkplaceValue();
			lstActualValue.forEach(actualValue -> {
				int valueType = actualValue.getValueType();
				Optional<TotalValue> optTotalVal = lstTotalValue.stream().filter(x -> x.getAttendanceId() == actualValue.getAttendanceId()).findFirst();
				TotalValue totalValue;
				if (optTotalVal.isPresent()) {
					if (actualValue.value() == null) return;
					totalValue = optTotalVal.get();
					int totalValueType = totalValue.getValueType();
					ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
					if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
						totalValue.addValue(actualValue.value(), valueTypeEnum);
					}
				}
				else {
					totalValue = new TotalValue();
					totalValue.setAttendanceId(actualValue.getAttendanceId());
					ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
					if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
						if (actualValue.getValue() != null) {
							totalValue.setValue(actualValue.getValue());
						} else {
							totalValue.setValue("0");
						}
					}
					totalValue.setValueType(valueType);
					lstTotalValue.add(totalValue);
				}
				totalValue.setUnit(actualValue.getUnit());
			});
		});
	}
	
	/**
	 * Calculate gross total by date.
	 *
	 * @param dailyReportData the daily report data
	 */
	private void calculateGrossTotalByDate(DailyReportData dailyReportData) {
		List<WorkplaceDailyReportData> lstWorkplaceDailyReportData = dailyReportData.getLstDailyReportData();
		List<TotalValue> lstGrossTotal = new ArrayList<>();
		dailyReportData.setListTotalValue(lstGrossTotal);
		
		lstWorkplaceDailyReportData.stream().forEach(workplaceDaily -> {
			WorkplaceTotal workplaceTotal = workplaceDaily.getLstWorkplaceData().getWorkplaceTotal();
			List<TotalValue> lstTotalVal = workplaceTotal.getTotalWorkplaceValue();
			lstTotalVal.stream().forEach(totalVal -> {
				// Literally 0-1 result in list
				Optional<TotalValue> optGrossTotal = lstGrossTotal.stream().filter(x -> x.getAttendanceId() == totalVal.getAttendanceId()).findFirst();
				TotalValue totalValue;
				if (optGrossTotal.isPresent()) {
					if (totalVal.value() == null) return;
					totalValue = optGrossTotal.get();
					int totalValueType = totalValue.getValueType();
					ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
					if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
						totalValue.addValue(totalVal.value(), valueTypeEnum);
					}
				}
				else {
					totalValue = new TotalValue();
					totalValue.setAttendanceId(totalVal.getAttendanceId());
					totalValue.setValue(totalVal.getValue());
					totalValue.setValueType(totalVal.getValueType());
					lstGrossTotal.add(totalValue);
				}
				totalValue.setUnit(totalVal.getUnit());
			});
		});
	}
	
	/**
	 * Find workplace.
	 *
	 * @param employeeId the employee id
	 * @param rootWorkplace the root workplace
	 * @param baseDate the base date
	 * @param lstWorkplaceHistImport the lst workplace hist import
	 * @param lstWorkplaceConfigInfo the lst workplace config info
	 * @return the workplace report data
	 */
	private WorkplaceReportData findWorkplace(String employeeId, WorkplaceReportData rootWorkplace, GeneralDate baseDate,
			Map<String, WkpHistImport> lstWorkplaceHistImport, List<WorkplaceConfigInfo> lstWorkplaceConfigInfo) {

		Map<String, WorkplaceReportData> mapWorkplaceInfo = rootWorkplace.getLstChildWorkplaceReportData();
		WkpHistImport workplaceImport = lstWorkplaceHistImport.get(employeeId);
		List<WorkplaceConfigInfo> workplaceConfigInfos = lstWorkplaceConfigInfo.stream().filter(x -> {
			List<String> workplaceIds = new ArrayList<>();
			if (!x.getLstWkpHierarchy().isEmpty()) {
				workplaceIds.addAll(x.getLstWkpHierarchy().stream().map(t -> t.getWorkplaceId()).collect(Collectors.toList()));
			}
			return workplaceIds.contains(workplaceImport.getWorkplaceId());
		}).collect(Collectors.toList());
		
		List<WorkplaceHierarchy> workplaceHierarchies = workplaceConfigInfos.stream().flatMap(t -> t.getLstWkpHierarchy().stream()).collect(Collectors.toList());
		HierarchyCode hierarchyCode = workplaceHierarchies.stream()
				.filter(t -> t.getWorkplaceId().equalsIgnoreCase(workplaceImport.getWorkplaceId()))
				.findFirst()
				.map(WorkplaceHierarchy::getHierarchyCode)
				.orElse(null);
		if (hierarchyCode != null && mapWorkplaceInfo.containsKey(hierarchyCode.v())) {
			return mapWorkplaceInfo.get(hierarchyCode.v());
		} else {
			for (Map.Entry<String, WorkplaceReportData> keySet : mapWorkplaceInfo.entrySet()) {
				WorkplaceReportData data = findWorkplace(employeeId, keySet.getValue(), baseDate, lstWorkplaceHistImport, lstWorkplaceConfigInfo);
				if (data != null)
					return data;
			}
		}
		return null;
	}
	
	/**
	 * Find workplace.
	 *
	 * @param employeeId the employee id
	 * @param rootWorkplace the root workplace
	 * @param baseDate the base date
	 * @param lstWkpHistImport the lst wkp hist import
	 * @param lstWorkplaceConfigInfo the lst workplace config info
	 * @return the daily workplace data
	 */
	private DailyWorkplaceData findWorkplace(String employeeId, DailyWorkplaceData rootWorkplace, GeneralDate baseDate,
			Map<String, WkpHistImport> lstWkpHistImport, List<WorkplaceConfigInfo> lstWorkplaceConfigInfo) {
		Map<String, DailyWorkplaceData> mapWorkplaceInfo = rootWorkplace.getLstChildWorkplaceData();
		WkpHistImport workplaceImport = lstWkpHistImport.get(employeeId);
		List<WorkplaceConfigInfo> workplaceConfigInfos = lstWorkplaceConfigInfo.stream().filter(x -> {
			List<String> workplaceIds = new ArrayList<>();
			if (!x.getLstWkpHierarchy().isEmpty()) {
				workplaceIds.addAll(x.getLstWkpHierarchy().stream().map(t -> t.getWorkplaceId()).collect(Collectors.toList()));
			}
			return workplaceIds.contains(workplaceImport.getWorkplaceId());
		}).collect(Collectors.toList());
		
		List<WorkplaceHierarchy> workplaceHierarchies = workplaceConfigInfos.stream().flatMap(t -> t.getLstWkpHierarchy().stream()).collect(Collectors.toList());
		HierarchyCode hierarchyCode = workplaceHierarchies.stream()
				.filter(t -> t.getWorkplaceId().equalsIgnoreCase(workplaceImport.getWorkplaceId()))
				.findFirst()
				.map(WorkplaceHierarchy::getHierarchyCode)
				.orElse(null);
		if (hierarchyCode != null && mapWorkplaceInfo.containsKey(hierarchyCode.v())) {
			return mapWorkplaceInfo.get(hierarchyCode.v());
		} else {
			for (Map.Entry<String, DailyWorkplaceData> keySet : mapWorkplaceInfo.entrySet()) {
				DailyWorkplaceData data = findWorkplace(employeeId, keySet.getValue(), baseDate, lstWkpHistImport, lstWorkplaceConfigInfo);
				if (data != null)
					return data;
			}
		}
		return null;

	}
	
	/**
	 * Collect workplace hierarchy.
	 *
	 * @param workplaceId the workplace id
	 * @param baseDate the base date
	 * @param lstWorkplaceConfigInfo the lst workplace config info
	 * @return the map
	 */
	private Map<String, WorkplaceInfo> collectWorkplaceHierarchy(String workplaceId, GeneralDate baseDate, List<WorkplaceConfigInfo> lstWorkplaceConfigInfo) {
		Map<String, WorkplaceInfo> lstWorkplace = new TreeMap<>();
		Optional<WorkplaceConfigInfo> optHierachyInfo = lstWorkplaceConfigInfo.stream().filter(x -> StringUtils.equalsIgnoreCase(x.getLstWkpHierarchy().get(0).getWorkplaceId(), workplaceId)).findFirst();
		optHierachyInfo.ifPresent(info -> {
			List<WorkplaceHierarchy> lstHierarchy = info.getLstWkpHierarchy();
			for (WorkplaceHierarchy hierarchy: lstHierarchy) {
				if (CollectionUtil.isEmpty(workplaceInfoRepository.findByWkpId(hierarchy.getWorkplaceId()))) {
					continue;
				}
				WorkplaceInfo workplace = workplaceInfoRepository.findByWkpId(hierarchy.getWorkplaceId()).get(0);
				lstWorkplace.put(hierarchy.getHierarchyCode().v(), workplace);
				
				// Recursive
				if (!StringUtils.equals(workplace.getWorkplaceId(), workplaceId)) {
					collectWorkplaceHierarchy(workplace.getWorkplaceId(), baseDate, lstWorkplaceConfigInfo);
				}
			}
		});
		return lstWorkplace;
	}
	
	/**
	 * Analyze info export by employee.
	 *
	 * @param lstWorkplace the lst workplace
	 * @param parent the parent
	 */
	private void analyzeInfoExportByEmployee(Map<String, WorkplaceInfo> lstWorkplace, WorkplaceReportData parent) {
		boolean isFoundParent;
		// Add workplace into respective parent
		for (Map.Entry<String, WorkplaceInfo> entry : lstWorkplace.entrySet()) {
			String k = entry.getKey();
			WorkplaceInfo wpi = entry.getValue();
			
			isFoundParent = false;
			
			// Init an workplace report data
			WorkplaceReportData wrp = new WorkplaceReportData();
			wrp.workplaceCode = wpi.getWorkplaceCode().v();
			wrp.workplaceName = wpi.getWorkplaceName().v();
			wrp.lstEmployeeReportData = new ArrayList<>();
			wrp.level = k.length() / 3;
			
			// Break down code
			String breakDownCode;
			for (int level = k.length(); level >= 3; level -= 3) {
				breakDownCode = k.substring(0, level);
				
				// Find workplace based on breakdown code
				WorkplaceReportData targetWorkplace = findWorkplaceByHierarchyCodeExportEmployee(parent, breakDownCode);
				
				// Find parent workplace, add into its child
				if (targetWorkplace != null) {
					wrp.parent = targetWorkplace;
					targetWorkplace.lstChildWorkplaceReportData.put(k, wrp);
					//targetWorkplace.lstChildWorkplaceReportData = sortByWorkplaceCodeExportByEmployee(targetWorkplace.lstChildWorkplaceReportData);
					isFoundParent = true;
					break;
				}
			}

			// Not found, then add to root
			if (!isFoundParent) {
				wrp.parent = parent;
				parent.lstChildWorkplaceReportData.put(k, wrp);
				//parent.lstChildWorkplaceReportData = sortByWorkplaceCodeExportByEmployee(parent.lstChildWorkplaceReportData);
			}
		}
	}
	
	/**
	 * Find workplace by hierarchy code export employee.
	 *
	 * @param rootWorkplace the root workplace
	 * @param hierarchyCode the hierarchy code
	 * @return the workplace report data
	 */
	private WorkplaceReportData findWorkplaceByHierarchyCodeExportEmployee(WorkplaceReportData rootWorkplace, String hierarchyCode) {
		WorkplaceReportData workplaceReportData = null;
		
		for (Map.Entry<String, WorkplaceReportData> entry : rootWorkplace.lstChildWorkplaceReportData.entrySet()) {
			if (workplaceReportData != null) {
				break;
			}
			String key = entry.getKey();
			WorkplaceReportData childWorkplace = entry.getValue();
			if (StringUtils.equalsIgnoreCase(key, hierarchyCode)) {
				workplaceReportData = childWorkplace;
				break;
			}
			workplaceReportData = findWorkplaceByHierarchyCodeExportEmployee(childWorkplace, hierarchyCode);
		}
		
		return workplaceReportData;
	}
	
	
	/**
	 * Analyze info export by date.
	 *
	 * @param lstWorkplace the lst workplace
	 * @param parent the parent
	 * @param lstAddedWorkplace the lst added workplace
	 */
	private void analyzeInfoExportByDate(Map<String, WorkplaceInfo> lstWorkplace, DailyWorkplaceData parent, List<String> lstAddedWorkplace) {
		parent.lstChildWorkplaceData = new TreeMap<>();
		
		boolean isFoundParent;
		// Add workplace into respective parent
		for (Map.Entry<String, WorkplaceInfo> entry : lstWorkplace.entrySet()) {
			String k = entry.getKey();
			WorkplaceInfo wpi = entry.getValue();
			
			isFoundParent = false;
			
			// Init an workplace report data
			DailyWorkplaceData wrp = new DailyWorkplaceData();
			wrp.workplaceCode = wpi.getWorkplaceCode().v();
			wrp.workplaceName = wpi.getWorkplaceName().v();
			wrp.level = k.length() / 3;
			
			// Break down code
			String breakDownCode;
			for (int level = k.length(); level >= 3; level -= 3) {
				breakDownCode = k.substring(0, level);
				
				// Find workplace based on breakdown code
				DailyWorkplaceData targetWorkplace = findWorkplaceByHierarchyCodeExportDate(parent, breakDownCode);
				
				// Find parent workplace, add into its child
				if (targetWorkplace != null) {
					wrp.parent = targetWorkplace;
					targetWorkplace.lstChildWorkplaceData.put(k, wrp);
					//sortByWorkplaceCodeExportByDate(targetWorkplace.lstChildWorkplaceData);
					isFoundParent = true;
					break;
				}
			}

			// Not found, then add to root
			if (!isFoundParent) {
				wrp.parent = parent;
				parent.lstChildWorkplaceData.put(k, wrp);
				//sortByWorkplaceCodeExportByDate(parent.lstChildWorkplaceData);
			}
		}
	}
	
	/**
	 * Find workplace by hierarchy code export date.
	 *
	 * @param rootWorkplace the root workplace
	 * @param hierarchyCode the hierarchy code
	 * @return the daily workplace data
	 */
	private DailyWorkplaceData findWorkplaceByHierarchyCodeExportDate(DailyWorkplaceData rootWorkplace, String hierarchyCode) {
		DailyWorkplaceData workplaceReportData = null;
		
		for (Map.Entry<String, DailyWorkplaceData> entry : rootWorkplace.lstChildWorkplaceData.entrySet()) {
			if (workplaceReportData != null) {
				break;
			}
			String key = entry.getKey();
			DailyWorkplaceData childWorkplace = entry.getValue();
			if (StringUtils.equalsIgnoreCase(key, hierarchyCode)) {
				workplaceReportData = childWorkplace;
				break;
			}
			workplaceReportData = findWorkplaceByHierarchyCodeExportDate(childWorkplace, hierarchyCode);
		}
		
		return workplaceReportData;
	}
	
	/**
	 * Collect header data.
	 *
	 * @param headerData the header data
	 * @param condition the condition
	 */
	private void collectHeaderData(DailyPerformanceHeaderData headerData, WorkScheduleOutputCondition condition) {
		Optional<Company> optCompany = companyRepository.find(AppContexts.user().companyId());
		if (optCompany.isPresent())
			headerData.companyName = optCompany.get().getCompanyName().v();
		else
			headerData.companyName = "";
		headerData.setFixedHeaderData(new ArrayList<>());
		headerData.fixedHeaderData.add(WorkScheOutputConstants.ERAL);
		if (condition.getOutputType() == FormOutputType.BY_EMPLOYEE) {
			headerData.fixedHeaderData.add(WorkScheOutputConstants.DATE);
			headerData.fixedHeaderData.add(WorkScheOutputConstants.DAYMONTH);
			headerData.fixedHeaderData.add(WorkScheOutputConstants.DAY);
		} else {
			headerData.fixedHeaderData.add(TextResource.localize("KWR001_114"));
		}
		headerData.fixedHeaderData.add(WorkScheOutputConstants.REMARK);
	}
	
	/**
	 * Collect display map.
	 *
	 * @param headerData the header data
	 * @param condition the condition
	 * @param companyId the company id
	 * @param roleId the role id
	 */
	private List<Integer> collectDisplayMap(DailyPerformanceHeaderData headerData,
			OutputItemDailyWorkSchedule condition, String companyId, String roleId) {
		List<Integer> lstHaveNameIds = new ArrayList<>();
		List<AttendanceItemsDisplay> lstItem = condition.getLstDisplayedAttendance();
		headerData.setLstOutputItemSettingCode(new ArrayList<>());

		List<Integer> lstAttendanceId = lstItem.stream().sorted((o1, o2) -> (o1.getOrderNo() - o2.getOrderNo()))
				.map(x -> x.getAttendanceDisplay()).collect(Collectors.toList());
		List<AttItemName> lstDailyAttendanceItem = companyDailyItemService.getDailyItems(companyId, Optional.of(roleId),
				lstAttendanceId, Arrays.asList(DailyAttendanceAtr.values()));
		if (lstDailyAttendanceItem.isEmpty())
			throw new BusinessException(new RawErrorMessage("Msg_1417"));
		//condition.setLstDisplayedAttendance(lstItem.stream().filter(x -> lstAttendanceId.contains(x.getAttendanceDisplay())).collect(Collectors.toList()));
		
		lstAttendanceId.stream().forEach(x -> {
			Optional<AttItemName> opAttendanceItem = lstDailyAttendanceItem.stream()
					.filter(item -> item.getAttendanceItemId() == x).findFirst();
			OutputItemSetting setting = new OutputItemSetting();
			if (opAttendanceItem.isPresent()) {
				setting.setItemCode(opAttendanceItem.get().getAttendanceItemDisplayNumber());
				setting.setItemName(opAttendanceItem.get().getAttendanceItemName());
				lstHaveNameIds.add(x);
				headerData.lstOutputItemSettingCode.add(setting);
			} 
//			else {
//				setting.setItemCode(null);
//				setting.setItemName("");
//			}
//			headerData.lstOutputItemSettingCode.add(setting);
		});
		return lstHaveNameIds;
	}
	
	/**
	 * Write header data.
	 *
	 * @param query the query
	 * @param outputItem the output item
	 * @param sheet the sheet
	 * @param reportData the report data
	 * @param dateRow the date row
	 */
	private void writeHeaderData(WorkScheduleOutputQuery query, OutputItemDailyWorkSchedule outputItem, Worksheet sheet, DailyPerformanceReportData reportData, int dateRow) {
		// Company name
		PageSetup pageSetup = sheet.getPageSetup();
		pageSetup.setHeader(0, "&8&\"MS ゴシック\"" + reportData.getHeaderData().companyName);
		
		// Output item name
		pageSetup.setHeader(1, "&16&\"MS ゴシック,Bold\"" + outputItem.getItemName().v());
		
		// Set header date
		DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm", Locale.JAPAN);
		pageSetup.setHeader(2, "&8&\"MS ゴシック\" " + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage &P ");
		
		Cells cells = sheet.getCells();
		Cell periodCell = cells.get(dateRow,0);
		DateTimeFormatter jpFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd（E）", Locale.JAPAN);
		String periodStr = TextResource.localize("KWR001_112")
							+ query.getStartDate().toLocalDate().format(jpFormatter)
							+ WorkScheOutputConstants.PERIOD_SYMBOL
							+ query.getEndDate().toLocalDate().format(jpFormatter);
		periodCell.setValue(periodStr);

		pageSetup.setFooter(0, "&\"MS ゴシック\"&7\0" + TextResource.localize("KWR001_113"));
	}
	
	/**
	 * Sets the fixed data.
	 *
	 * @param condition the condition
	 * @param sheet the sheet
	 * @param reportData the report data
	 * @param currentRow the current row
	 */
	private void setFixedData(WorkScheduleOutputCondition condition
			, Worksheet sheet
			, DailyPerformanceReportData reportData
			, int currentRow
			, int remarkPosition) {
		DailyPerformanceHeaderData headerData = reportData.getHeaderData();
		// Set fixed value
		Cells cells = sheet.getCells();
		
		if (condition.getOutputType() == FormOutputType.BY_EMPLOYEE) {
			// A2_1
			Cell erAlCell = cells.get(currentRow, 0);
			erAlCell.setValue(headerData.getFixedHeaderData().get(0));
			
			Cell dateCell = cells.get(currentRow, 1);
			// A2_2
			dateCell.setValue(headerData.getFixedHeaderData().get(1));

			// A2_6
			Cell remarkCell = cells.get(currentRow, remarkPosition);
			remarkCell.setValue(headerData.getFixedHeaderData().get(4));
		}
		else {
			// B2_1
			Cell erAlCell = cells.get(currentRow, 0);
			erAlCell.setValue(headerData.getFixedHeaderData().get(0));
			
			// B2_2
			Cell dateCell = cells.get(currentRow, 1);
			dateCell.setValue(headerData.getFixedHeaderData().get(1));
			
			// B2_4
			Cell remarkCell = cells.get(currentRow, remarkPosition);
			remarkCell.setValue(headerData.getFixedHeaderData().get(2));
		}
		
	}
	
	/**
	 * Write display map.
	 *
	 * @param cells the cells
	 * @param reportData the report data
	 * @param currentRow the current row
	 * @param nSize the n size
	 */
	private void writeDisplayMap(Cells cells, DailyPerformanceReportData reportData, int currentRow, int nSize, int chunkSize, FontSizeEnum fontSize) {
		List<OutputItemSetting> lstItem = reportData.getHeaderData().getLstOutputItemSettingCode();
		
		// Divide list into smaller lists (max 16 items)
		int numOfChunks = (int)Math.ceil((double)lstItem.size() / chunkSize);

		int start, length;
		List<OutputItemSetting> lstItemRow;
		
        for(int i = 0; i < numOfChunks; i++) {
            start = i * chunkSize; 
            length = Math.min(lstItem.size() - start, chunkSize);

            lstItemRow = lstItem.subList(start, start + length);
            
            OutputItemSetting outputItem;
            
            for (int j = 0; j < length; j++) {
            	outputItem = lstItemRow.get(j);
            	// Column 4, 6, 8,...
            	// Row 3, 4, 5
            	Cell cell = cells.get(currentRow + i*2, DATA_COLUMN_INDEX[0] + j * 2);
            	int atdMaxLength = fontSize.equals(FontSizeEnum.BIG) ? 15 : 12;
            	cell.setValue(
	    			outputItem.getItemName().length() > atdMaxLength
    				? outputItem.getItemName().substring(0, atdMaxLength)
					: outputItem.getItemName()
    			);
            	
            	cell = cells.get(currentRow + i*2 + 1, DATA_COLUMN_INDEX[0] + j * 2);
            }
        }
	}
	
	/**
	 * Write detailed work schedule.
	 *
	 * @param currentRow the current row
	 * @param templateSheetCollection the template sheet collection
	 * @param sheetInfo the sheet
	 * @param workplaceReportData the workplace report data
	 * @param dataRowCount the data row count
	 * @param condition the condition
	 * @param rowPageTracker the row page tracker
	 * @return the int
	 * @throws Exception the exception
	 */
	private int writeDetailedWorkSchedule(int currentRow
			, WorksheetCollection templateSheetCollection
			, WorkSheetInfo sheetInfo, WorkplaceReportData workplaceReportData
			, int dataRowCount
			, WorkScheduleOutputCondition condition
			, RowPageTracker rowPageTracker
			, int chunkSize
			, FontSizeEnum fontSize) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();

		int remarkPosition = fontSize == FontSizeEnum.BIG ? 35 : 43;
		
		int contentPosition = fontSize == FontSizeEnum.BIG ? 39 : 47;
		
		WorkScheduleSettingTotalOutput totalOutput = condition.getSettingDetailTotalOutput();
		List<EmployeeReportData> lstEmployeeReportData = workplaceReportData.getLstEmployeeReportData();
		
		// Root workplace won't have employee
		List<String> departmentCode = new ArrayList<>();
		if (lstEmployeeReportData != null && lstEmployeeReportData.size() > 0) {
			Iterator<EmployeeReportData> iteratorEmployee = lstEmployeeReportData.iterator();
			while (iteratorEmployee.hasNext()) {
				EmployeeReportData employeeReportData = iteratorEmployee.next();

                if (!departmentCode.contains(workplaceReportData.getWorkplaceCode())
            		&& !condition.getSettingDetailTotalOutput().isDetails()
            		&& !condition.getSettingDetailTotalOutput().isGrossTotal()
            		&& !condition.getSettingDetailTotalOutput().isPersonalTotal()) {
	                String workplaceTitle = TextResource.localize("KWR001_90") + workplaceReportData.getWorkplaceCode() + "　" + workplaceReportData.getWorkplaceName();
	                // A3_1
	                currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
	                departmentCode.add(workplaceReportData.getWorkplaceCode());
                }
				if (!departmentCode.contains(workplaceReportData.getWorkplaceCode())
                		&& !condition.getSettingDetailTotalOutput().isDetails()
                		&& !condition.getSettingDetailTotalOutput().isWorkplaceTotal() 
                		&& !condition.getSettingDetailTotalOutput().isCumulativeWorkplace()
                		&& !condition.getSettingDetailTotalOutput().isGrossTotal()
                		&& !condition.getSettingDetailTotalOutput().isPersonalTotal()) {
	                String personalTitle = TextResource.localize("KWR001_91")	// A4_1
	                		+ employeeReportData.employeeCode + "　"				// A4_2
	                        + employeeReportData.employeeName + "　"				// A4_3
	                		+ TextResource.localize("KWR001_92")			    // A4_4
	                		+ employeeReportData.employmentCode	 + "　"		    // A4_8
	                        + employeeReportData.employmentName	 + "　"		   	// A4_5
	                		+ TextResource.localize("KWR001_93")			    // A4_6
	                		+ employeeReportData.jobTitleCode + "　"			   
	                        + employeeReportData.position;

	                currentRow = this.printPersonal(currentRow, templateSheetCollection, sheetInfo, personalTitle, contentPosition);
	                departmentCode.add(workplaceReportData.getWorkplaceCode());
                }
				if (rowPageTracker.checkRemainingRowSufficient(3 + dataRowCount) < 0) {
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
					rowPageTracker.resetRemainingRow();
				}
				if (condition.getSettingDetailTotalOutput().isPersonalTotal() || condition.getSettingDetailTotalOutput().isDetails()) {
					String workplaceTitle = TextResource.localize("KWR001_90")
							+ workplaceReportData.getWorkplaceCode() + "　" + workplaceReportData.getWorkplaceName();
					// A3_1
					currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
				}

                rowPageTracker.useRemainingRow(2);
                
                if(condition.isPrivatePerson()){
					Range employeeRangeTemp = templateSheetCollection
							.getRangeByName(WorkScheOutputConstants.RANGE_EMPLOYEE_ROW);
					Range employeeRange = cells.createRange(currentRow, 0, 1, contentPosition);
					employeeRange.copy(employeeRangeTemp);
					employeeRange.setRowHeight(employeeRangeTemp.getRowHeight());
//					if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
//						rowPageTracker.useRemainingRow(dataRowCount);
//						rowPageTracker.resetRemainingRow();
//					}
//					else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
//						rowPageTracker.resetRemainingRow();
//						rowPageTracker.useRemainingRow(dataRowCount);
//					}
//					else {
//						rowPageTracker.useRemainingRow(dataRowCount);
//					}

					Cell employeeTagCell = cells.get(currentRow, 0);
					employeeTagCell.setValue(TextResource.localize("KWR001_91")
							+ employeeReportData.employeeCode + "　"
							+ employeeReportData.employeeName + "　"
							+ TextResource.localize("KWR001_92")
							+ employeeReportData.employmentCode + "　"
							+ employeeReportData.employmentName + "　"
							+ TextResource.localize("KWR001_93")
							+ employeeReportData.jobTitleCode + "　"
							+ employeeReportData.position);
					currentRow++;
				}
                
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
					rowPageTracker.resetRemainingRow();
				}

				boolean colorWhite = true; // true = white, false = light blue, start with white row
				
				// Detail personal performance
				if (totalOutput.isDetails()) {
					List<DetailedDailyPerformanceReportData> lstDetailedDailyPerformance = employeeReportData.getLstDetailedPerformance();
					for (DetailedDailyPerformanceReportData detailedDailyPerformanceReportData: lstDetailedDailyPerformance) {
						Range dateRangeTemp;
						
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
							if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
								cells = sheetInfo.getSheet().getCells();
								currentRow = sheetInfo.getStartDataIndex();
							}
							rowPageTracker.resetRemainingRow();
					                String workplaceTitle = TextResource.localize("KWR001_90") + workplaceReportData.getWorkplaceCode() + "　" + workplaceReportData.getWorkplaceName();
					                String personalTitle = TextResource.localize("KWR001_91")
					                		+ employeeReportData.employeeCode
					                		+ "　" + employeeReportData.employeeName
					                		+ "　" + TextResource.localize("KWR001_92")
					                		+ employeeReportData.employmentCode
					                		+ "　" + employeeReportData.employmentName
					                		+ "　" + TextResource.localize("KWR001_93")
					                		+ employeeReportData.jobTitleCode
					                		+ "　" + employeeReportData.position;
					                departmentCode.add(workplaceReportData.getWorkplaceCode());
					                // A3_1
					                currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
					                // A4_1
					                currentRow = this.printPersonal(currentRow, templateSheetCollection, sheetInfo, personalTitle, contentPosition);
					                rowPageTracker.useRemainingRow(2);
						}
						if (colorWhite) // White row
							dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_WHITE_ROW + dataRowCount);
						else // Light blue row
							dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_LIGHTBLUE_ROW + dataRowCount);
						Range dateRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
						dateRange.copy(dateRangeTemp);
						dateRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
							rowPageTracker.resetRemainingRow();
							String workplaceTitle = TextResource.localize("KWR001_90") + workplaceReportData.getWorkplaceCode() + "　" + workplaceReportData.getWorkplaceName();
			                String personalTitle = TextResource.localize("KWR001_91")
			                		+ employeeReportData.employeeCode
			                		+ "　" + employeeReportData.employeeName
			                		+ "　" + TextResource.localize("KWR001_92")
			                		+ employeeReportData.employmentCode
			                		+ "　" + employeeReportData.employmentName
			                		+ "　" + TextResource.localize("KWR001_93")
			                		+ employeeReportData.jobTitleCode
			                		+ "　" + employeeReportData.position;
			                departmentCode.add(workplaceReportData.getWorkplaceCode());
			                // A3_1
			                currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
			                // A4_1
			                currentRow = this.printPersonal(currentRow, templateSheetCollection, sheetInfo, personalTitle, contentPosition);
			                rowPageTracker.useRemainingRow(2);
						}
						else {
							rowPageTracker.useRemainingRow(dataRowCount);
						}
						
						// A5_1
						Cell erAlMark = cells.get(currentRow, 0);
						erAlMark.setValue(detailedDailyPerformanceReportData.getErrorAlarmMark());
						
						// A5_2
						Cell dateCell = cells.get(currentRow, 1);
						dateCell.setValue(detailedDailyPerformanceReportData.getDate().toString("MM/dd"));
						
						// A5_3
						DateTimeFormatter jpFormatter = DateTimeFormatter.ofPattern("E", Locale.JAPAN);
						String jp = detailedDailyPerformanceReportData.getDate().toLocalDate().format(jpFormatter);
						Cell dayOfWeekCell = cells.get(currentRow, 2);
						dayOfWeekCell.setValue(jp);
						
						// A5_4
						List<ActualValue> lstItem = detailedDailyPerformanceReportData.getActualValue();
						
						// Divide list into smaller lists (max 16 items)
						int numOfChunks = (int)Math.ceil((double)lstItem.size() / chunkSize);
	
						int curRow = currentRow;
						int start, length;
						List<ActualValue> lstItemRow;

				        for(int i = 0; i < numOfChunks; i++) {
				            start = i * chunkSize;
				            length = Math.min(lstItem.size() - start, chunkSize);
	
				            lstItemRow = lstItem.subList(start, start + length);
				            
				            for (int j = 0; j < length; j++) {
				            	// Column 4, 6, 8,...
				            	ActualValue actualValue = lstItemRow.get(j);
				            	Cell cell = cells.get(curRow, DATA_COLUMN_INDEX[0] + j * 2);
				            	writeDetailValue(actualValue, cell, condition.getZeroDisplayType(), fontSize);
				            }
				            
				            curRow++;
				        }
				        
				        // A5_5
				        Cell remarkCell;
				        String errorDetail = detailedDailyPerformanceReportData.getErrorDetail();
				        String[] errorSplipData = errorDetail.split("/n");
						int curRowRemark = currentRow;
			            for (int j = 0; j < errorSplipData.length; j++) {
			            	// Column 4, 6, 8,...
			            	remarkCell = cells.get(curRowRemark, remarkPosition);
			            	Style style = remarkCell.getStyle();
			            	remarkCell.setValue(errorSplipData[j]);
							style.setHorizontalAlignment(TextAlignmentType.LEFT);
			            	setFontStyle(style, fontSize);
			            	remarkCell.setStyle(style);
				            curRowRemark++;
			            }
				        currentRow += dataRowCount;
				        colorWhite = !colorWhite; // Change to other color
					}
				}
				
				// Personal total
				if (totalOutput.isPersonalTotal()) {
					if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
						if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
							cells = sheetInfo.getSheet().getCells();
							currentRow = sheetInfo.getStartDataIndex();
						}
						rowPageTracker.resetRemainingRow();
					}
					
					Range personalTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
					Range personalTotalRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
					personalTotalRange.copy(personalTotalRangeTemp);
					personalTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
					
					// A6_1
					Cell personalTotalCellTag = cells.get(currentRow, 0);
					personalTotalCellTag.setValue(TextResource.localize("KWR001_94"));
					
					// A6_2
					Map<Integer, TotalValue> mapPersonalTotal = employeeReportData.getMapPersonalTotal();
					int numOfChunks = (int) Math.ceil( (double) mapPersonalTotal.size() / chunkSize);
					int start, length;
					List<TotalValue> lstItemRow;
					
			        for(int i = 0; i < numOfChunks; i++) {
			            start = i * chunkSize;
			            length = Math.min(mapPersonalTotal.size() - start, chunkSize);
			            
			            lstItemRow = mapPersonalTotal.values().stream().collect(Collectors.toList()).subList(start, start + length);
			            
			            for (int j = 0; j < length; j++) {
			            	TotalValue totalValue = lstItemRow.get(j);
			            	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2);
			            	writeTotalValue(totalValue, cell, condition.getZeroDisplayType(), fontSize);
			            }
			            currentRow++;
			        }
			        
			        if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
						rowPageTracker.useRemainingRow(dataRowCount);
						if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
							cells = sheetInfo.getSheet().getCells();
							currentRow = sheetInfo.getStartDataIndex();
						}
						rowPageTracker.resetRemainingRow();
					}
					else {
						rowPageTracker.useRemainingRow(dataRowCount);
					}
				}
		        
				// Total count day
		        if (condition.getSettingDetailTotalOutput().isTotalNumberDay()) {
		        	// Use fixed 2 rows
		        	if (rowPageTracker.checkRemainingRowSufficient(2) < 0) {
						if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
							cells = sheetInfo.getSheet().getCells();
							currentRow = sheetInfo.getStartDataIndex();
						}
						rowPageTracker.resetRemainingRow();
					}
			        
			        Range dayCountRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_DAYCOUNT_ROW);
					Range dayCountRange = cells.createRange(currentRow, 0, 2, contentPosition);
					dayCountRange.copy(dayCountRangeTemp);
					dayCountRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
					if (rowPageTracker.checkRemainingRowSufficient(2) == 0) {
						rowPageTracker.useRemainingRow(2);
						rowPageTracker.resetRemainingRow();
					}
					else if (rowPageTracker.checkRemainingRowSufficient(2) < 0) {
						rowPageTracker.resetRemainingRow();
						rowPageTracker.useRemainingRow(2);
					}
					else {
						rowPageTracker.useRemainingRow(2);
					}
					
					// A7_1
					Cell dayCountTag = cells.get(currentRow, 0);
					dayCountTag.setValue(WorkScheOutputConstants.DAY_COUNT);
					
					// A7_2 -> A7_10
					TotalCountDay totalCountDay = employeeReportData.getTotalCountDay();
					totalCountDay.initAllDayCount();
					for (int i = 0; i < totalCountDay.getAllDayCount().size(); i++) {
						Cell dayTypeTag = cells.get(currentRow, i*2 + 3);
						dayTypeTag.setValue(WorkScheOutputConstants.DAY_COUNT_TITLES[i]);
					}
					
					currentRow++;
				
					// A7_11 -> A7_19
					
					for (int i = 0; i < totalCountDay.getAllDayCount().size(); i++) {
						Cell dayTypeTag = cells.get(currentRow, i*2 + 3);
						String value = totalCountDay.getAllDayCount().get(i);
						boolean isZeroValue = Double.valueOf(value.substring(0, value.length() - 1)) == 0d;
						dayTypeTag.setValue((isZeroValue && condition.getZeroDisplayType() == ZeroDisplayType.NON_DISPLAY) 
								? "" : totalCountDay.getAllDayCount().get(i));
					}
					
					currentRow++;
		        }
		        
		        // Page break by employee
				if (condition.getPageBreakIndicator() == PageBreakIndicator.EMPLOYEE && iteratorEmployee.hasNext()) {
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
					rowPageTracker.resetRemainingRow();
				}
			}
		}
		
		// Skip writing total for root, use gross total instead
		if (lstEmployeeReportData != null && lstEmployeeReportData.size() > 0 && totalOutput.isWorkplaceTotal()) {
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}
			Range workplaceTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range workplaceTotalRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
				rowPageTracker.resetRemainingRow();
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			workplaceTotalRange.copy(workplaceTotalRangeTemp);
			workplaceTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			
			// A8_1
			Cell workplaceTotalCellTag = cells.get(currentRow, 0);
			workplaceTotalCellTag.setValue(WorkScheOutputConstants.WORKPLACE_TOTAL);
			
			// A8_2
			WorkplaceTotal workplaceTotal = workplaceReportData.getWorkplaceTotal();
			int numOfChunks = (int) Math.ceil( (double) workplaceTotal.getTotalWorkplaceValue().size() / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;
			
	        for(int i = 0; i < numOfChunks; i++) {
	            start = i * chunkSize;
	            length = Math.min(workplaceTotal.getTotalWorkplaceValue().size() - start, chunkSize);
	            lstItemRow = workplaceTotal.getTotalWorkplaceValue().subList(start, start + length);
	            
	            for (int j = 0; j < length; j++) {
	            	TotalValue totalValue = lstItemRow.get(j);
	            	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2);
	            	writeTotalValue(totalValue, cell, condition.getZeroDisplayType(), fontSize);
	            }
	            currentRow++;
	        }
		}
		
		if (lstEmployeeReportData != null && lstEmployeeReportData.size() > 0 && totalOutput.isCumulativeWorkplace() && currentRow == 5) {
			TotalWorkplaceHierachy totalHierarchyOption = totalOutput.getWorkplaceHierarchyTotal();
			List<Integer> lstBetweenLevel = findEnabledLevelBetweenWorkplaces(workplaceReportData, totalHierarchyOption);
			lstBetweenLevel.add(1);
			Iterator<Integer> levelIterator = null;
			if (!lstBetweenLevel.isEmpty()) {
				levelIterator = lstBetweenLevel.iterator();
			}
			
			Range wkpHierTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range wkpHierTotalRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
			wkpHierTotalRange.copy(wkpHierTotalRangeTemp);
			wkpHierTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
				rowPageTracker.resetRemainingRow();
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			// A9_1 - A13_1
			Cell workplaceTotalCellTag = cells.get(currentRow, 0);
			workplaceTotalCellTag.setValue(TextResource.localize("KWR001_116", Integer.toString(levelIterator.next())));
			
			// A9_2 - A13_2
			int numOfChunks = (int) Math.ceil( (double) workplaceReportData.getGrossTotal().size() / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;
			
	        for(int i = 0; i < numOfChunks; i++) {
	            start = i * chunkSize;
	            length = Math.min(workplaceReportData.getGrossTotal().size() - start, chunkSize);
	
	            lstItemRow = workplaceReportData.getGrossTotal().subList(start, start + length);
	            
	            for (int j = 0; j < length; j++) {
	            	TotalValue totalValue = lstItemRow.get(j);
	            	Cell cell = cells.get(currentRow + i, DATA_COLUMN_INDEX[0] + j * 2); 
	            	writeTotalValue(totalValue, cell, condition.getZeroDisplayType(), fontSize);
	            }
	            currentRow++;
	        }
	        
		}
		
		
		Map<String, WorkplaceReportData> mapChildWorkplace = workplaceReportData.getLstChildWorkplaceReportData();
		if (((condition.getPageBreakIndicator() == PageBreakIndicator.WORKPLACE || 
				condition.getPageBreakIndicator() == PageBreakIndicator.EMPLOYEE) &&
				mapChildWorkplace.size() > 0 ) && workplaceReportData.level != 0) { /* Trick to not page break between detailed data and total data */
			Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, contentPosition);
        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
				cells = sheetInfo.getSheet().getCells();
				currentRow = sheetInfo.getStartDataIndex();
			}
			rowPageTracker.resetRemainingRow();
		}
		// Process to child workplace
		Iterator<Map.Entry<String, WorkplaceReportData>> it = mapChildWorkplace.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, WorkplaceReportData> entry = it.next();
			currentRow = writeDetailedWorkSchedule(currentRow
					, templateSheetCollection
					, sheetInfo
					, entry.getValue()
					, dataRowCount
					, condition
					, rowPageTracker
					, chunkSize
					, fontSize);
			
			// Page break by workplace
			if ((condition.getPageBreakIndicator() == PageBreakIndicator.WORKPLACE || condition.getPageBreakIndicator() == PageBreakIndicator.EMPLOYEE)
					&& it.hasNext()/* && entry.getValue().getParent().level != 0*/) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}
		}
		
		/**
		 * A9 - A13
		 */
		TotalWorkplaceHierachy totalHierarchyOption = totalOutput.getWorkplaceHierarchyTotal();
		if ((totalOutput.isGrossTotal() || totalOutput.isCumulativeWorkplace() && (currentRow != 6 && totalOutput.isCumulativeWorkplace()))
				&& !(workplaceReportData.getLstChildWorkplaceReportData().isEmpty()
				&& workplaceReportData.getLstEmployeeReportData().isEmpty())) {
			
			WorkplaceReportData parentWorkplace = workplaceReportData.getParent();
			List<Integer> lstBetweenLevel = findEnabledLevelBetweenWorkplaces(workplaceReportData, totalHierarchyOption);
			int level = workplaceReportData.getLevel();
			
			if (findWorkplaceHigherEnabledLevel(workplaceReportData, totalHierarchyOption) <= level) {
				String tagStr;
				Iterator<Integer> levelIterator = null;
				if (!lstBetweenLevel.isEmpty()) {
					levelIterator = lstBetweenLevel.iterator();
				}
				do {
					//if (level != 0 && level >= totalHierarchyOption.getHighestLevelEnabled() && totalOutput.isCumulativeWorkplace() && totalHierarchyOption.checkLevelEnabled(level)) {
					if (level != 0 && level >= totalHierarchyOption.getHighestLevelEnabled() && totalOutput.isCumulativeWorkplace()) {
						// Condition: not root workplace (lvl = 0), level within picked hierarchy zone, enable cumulative workplace.
						int parentHigherEnabledLevel = findWorkplaceHigherEnabledLevel(parentWorkplace, totalHierarchyOption);
						if (parentHigherEnabledLevel <= level) {
							if (levelIterator != null && levelIterator.hasNext()) {
								tagStr = TextResource.localize("KWR001_116", Integer.toString(levelIterator.next()));
							}
							else {
								tagStr = null;
							}
						}
						else if (totalHierarchyOption.checkLevelEnabled(level)) {
							tagStr = TextResource.localize("KWR001_116", Integer.toString(workplaceReportData.getLevel()));
						}
						else {
							tagStr = null;
						}
					} else if (totalOutput.isGrossTotal() && level == 0)
						// Condition: enable gross total, also implicitly root workplace
						tagStr = WorkScheOutputConstants.GROSS_TOTAL;
					else
						tagStr = null;
					
					if (tagStr != null) {
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
							if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
								cells = sheetInfo.getSheet().getCells();
								currentRow = sheetInfo.getStartDataIndex();
							}
							rowPageTracker.resetRemainingRow();
						}

                        cells = sheetInfo.getSheet().getCells();
						Range wkpHierTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
						Range wkpHierTotalRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
						wkpHierTotalRange.copy(wkpHierTotalRangeTemp);
						wkpHierTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
							rowPageTracker.useRemainingRow(dataRowCount);
							rowPageTracker.resetRemainingRow();
						}
						else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
							rowPageTracker.resetRemainingRow();
							rowPageTracker.useRemainingRow(dataRowCount);
						}
						else {
							rowPageTracker.useRemainingRow(dataRowCount);
						}
						
						// A9_1 - A13_1
						Cell workplaceTotalCellTag = cells.get(currentRow, 0);
						workplaceTotalCellTag.setValue(tagStr);
						
						// A9_2 - A13_2
						int numOfChunks = (int) Math.ceil( (double) workplaceReportData.getGrossTotal().size() / chunkSize);
						int start, length;
						List<TotalValue> lstItemRow;
						
				        for(int i = 0; i < numOfChunks; i++) {
				            start = i * chunkSize;
				            length = Math.min(workplaceReportData.getGrossTotal().size() - start, chunkSize);
				
				            lstItemRow = workplaceReportData.getGrossTotal().subList(start, start + length);
				            
				            for (int j = 0; j < length; j++) {
				            	TotalValue totalValue = lstItemRow.get(j);
				            	Cell cell = cells.get(currentRow + i, DATA_COLUMN_INDEX[0] + j * 2);
				            	writeTotalValue(totalValue, cell, condition.getZeroDisplayType(), fontSize);
				            }
				        }
				        currentRow += dataRowCount;
					}
					else{
						if(levelIterator!=null && levelIterator.hasNext()){
							levelIterator.next();
						}
					}
				} while (levelIterator != null && levelIterator.hasNext());
			}
		}
		return currentRow;
	}

	/**
	 * Write detail value.
	 *
	 * @param actualValue the actual value
	 * @param cell the cell
	 */
	private void writeDetailValue(ActualValue actualValue, Cell cell, ZeroDisplayType zeroDisplayType, FontSizeEnum fontSize) {
		Style style = cell.getStyle();
		ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
		if (valueTypeEnum.isTime()) {
			String value = actualValue.getValue();
			if (value != null) {
				if (valueTypeEnum == ValueType.TIME) {
					cell.setValue(getTimeAttr(value, false, zeroDisplayType));
				} else {
					cell.setValue(getTimeAttr(value, true, zeroDisplayType));
				}
			}
			style.setHorizontalAlignment(TextAlignmentType.RIGHT);
		} else if (this.isNumber(actualValue.getValue())) {
			if (valueTypeEnum.isDouble()) {
				double actualDoubleValue = actualValue.getValue() != null ? Double.parseDouble(actualValue.getValue()) : 0d;
				cell.setValue((zeroDisplayType == ZeroDisplayType.NON_DISPLAY && actualDoubleValue == 0d) ? "" : actualValue.formatValue());
			} else if (valueTypeEnum.isInteger()) {
				int actualIntValue = actualValue.getValue() != null ? Integer.parseInt(actualValue.getValue()) : 0;
				cell.setValue((zeroDisplayType == ZeroDisplayType.NON_DISPLAY && actualIntValue == 0) ? "" : actualValue.formatValue());
			} else
				cell.setValue(actualValue.formatValue());
			style.setHorizontalAlignment(TextAlignmentType.RIGHT);
		} else {
			cell.setValue(actualValue.formatValue());
			style.setHorizontalAlignment(TextAlignmentType.LEFT);
		}
		setFontStyle(style, fontSize);
		cell.setStyle(style);
	}
	
	/**
	 * Write detailed daily schedule.
	 *
	 * @param currentRow the current row
	 * @param templateSheetCollection the template sheet collection
	 * @param sheetInfo the sheet
	 * @param dailyReport the daily report
	 * @param dataRowCount the data row count
	 * @param condition the condition
	 * @param rowPageTracker the row page tracker
	 * @return the int
	 * @throws Exception the exception
	 */
	private int writeDetailedDailySchedule(int currentRow, WorksheetCollection templateSheetCollection, WorkSheetInfo sheetInfo, DailyReportData dailyReport,
			int dataRowCount, WorkScheduleOutputCondition condition, RowPageTracker rowPageTracker, int chunkSize, FontSizeEnum fontSize) throws Exception {
		int contentPosition = fontSize == FontSizeEnum.BIG ? 39 : 47;
		Cells cells = sheetInfo.getSheet().getCells();
		List<WorkplaceDailyReportData> lstDailyReportData = dailyReport.getLstDailyReportData();
		
		Iterator<WorkplaceDailyReportData> iteratorWorkplaceData  = lstDailyReportData.iterator();
		while(iteratorWorkplaceData.hasNext()) {
			WorkplaceDailyReportData dailyReportData = iteratorWorkplaceData.next();
			//dateRange.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());

			DailyWorkplaceData rootWorkplace = dailyReportData.getLstWorkplaceData();
			if(condition.isDating()){

			if (rowPageTracker.checkRemainingRowSufficient(3 + dataRowCount) < 0) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}

			// B3_1
			DateTimeFormatter jpFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd（E）", Locale.JAPAN);
			String date = dailyReportData.getDate().toLocalDate().format(jpFormatter);
			String titleDate = TextResource.localize("KWR001_115") + date;
			
//			// B3_2
//			Cell dateCell = cells.get(currentRow, 2);
//			dateCell.setValue(date);

			
			currentRow = writeDailyDetailedPerformanceDataOnWorkplace(currentRow
					, sheetInfo
					, templateSheetCollection
					, rootWorkplace
					, dataRowCount
					, condition
					, rowPageTracker
					, titleDate
					, chunkSize
					, fontSize);
		
			if (iteratorWorkplaceData.hasNext()) {
				// Page break (regardless of setting, see example template sheet ★ 日別勤務表-日別3行-1)
                if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                    cells = sheetInfo.getSheet().getCells();
                    currentRow = sheetInfo.getStartDataIndex();
                }
                rowPageTracker.resetRemainingRow();
			}
		}
		}
		
		if (condition.getSettingDetailTotalOutput().isGrossTotal()) {
			// Gross total after all the rest of the data
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
                if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                    cells = sheetInfo.getSheet().getCells();
                    currentRow = sheetInfo.getStartDataIndex();
                }
                rowPageTracker.resetRemainingRow();
			}
			Range wkpGrossTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range wkpGrossTotalRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
			wkpGrossTotalRange.copy(wkpGrossTotalRangeTemp);
			wkpGrossTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
				rowPageTracker.resetRemainingRow();
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			Cell grossTotalCellTag = cells.get(currentRow, 0);
			grossTotalCellTag.setValue(WorkScheOutputConstants.GROSS_TOTAL);
			
			currentRow = writeGrossTotal(currentRow
					, dailyReport.getListTotalValue()
					, sheetInfo.getSheet()
					, dataRowCount
					, rowPageTracker
					, chunkSize
					, condition.getZeroDisplayType()
					, fontSize);
		}
		
		return currentRow;
	}
	
	/**
	 * Write daily detailed performance data on workplace.
	 *
	 * @param currentRow the current row
	 * @param sheetInfo the sheet
	 * @param templateSheetCollection the template sheet collection
	 * @param rootWorkplace the root workplace
	 * @param dataRowCount the data row count
	 * @param condition the condition
	 * @param rowPageTracker the row page tracker
	 * @return the int
	 * @throws Exception the exception
	 */
	private int writeDailyDetailedPerformanceDataOnWorkplace(int currentRow
			, WorkSheetInfo sheetInfo
			, WorksheetCollection templateSheetCollection
			, DailyWorkplaceData rootWorkplace
			, int dataRowCount
			, WorkScheduleOutputCondition condition
			, RowPageTracker rowPageTracker
			, String titleDate
			, int chunkSize
			, FontSizeEnum fontSize) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		int remarkPosition = fontSize == FontSizeEnum.BIG ? 35 : 43;
		int contentPosition = fontSize == FontSizeEnum.BIG ? 39 : 47;
		int contentPosition1 = fontSize == FontSizeEnum.BIG ? 42 : 50;
        String workplaceTitle = TextResource.localize("KWR001_90") + rootWorkplace.getWorkplaceCode() + "　" + rootWorkplace.getWorkplaceName();

        boolean colorWhite = true; // true = white, false = light blue, start with white row
		
		List<DailyPersonalPerformanceData> employeeReportData = rootWorkplace.getLstDailyPersonalData()
				.stream().sorted((o1, o2) -> o1.getEmployeeCode().compareTo(o2.getEmployeeCode()))
				.collect(Collectors.toList());

		if (employeeReportData != null && !employeeReportData.isEmpty()) {
			//rowPageTracker.useOneRowAndCheckResetRemainingRow(sheetInfo.getSheet(), currentRow);
            if (rowPageTracker.checkRemainingRowSufficient(dataRowCount + 2) < 0) {
                rowPageTracker.resetRemainingRow();
                if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                    cells = sheetInfo.getSheet().getCells();
                    currentRow = sheetInfo.getStartDataIndex();
                }
//                currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate);
//                currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle);
//                rowPageTracker.useRemainingRow(2);
            }
			// B4_1
            currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, contentPosition1);
            currentRow = this.printWorkplaceByDate(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition1);
            rowPageTracker.useRemainingRow(2);
			Iterator<DailyPersonalPerformanceData> dataIterator = employeeReportData.iterator();
			
			// Employee data
			while (dataIterator.hasNext() && condition.getSettingDetailTotalOutput().isDetails()){
				DailyPersonalPerformanceData employee = dataIterator.next();
				
				List<ActualValue> lstItem = employee.getActualValue();
				// Skip to next employee when there is no actual value
				if (lstItem == null || lstItem.isEmpty()) continue;
				
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
                    rowPageTracker.resetRemainingRow();
                    if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                        cells = sheetInfo.getSheet().getCells();
                        currentRow = sheetInfo.getStartDataIndex();
                    }
				}
				
				if (rowPageTracker.isNewPage()) {
					currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, contentPosition1);
                    currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
                    rowPageTracker.useRemainingRow(2);
				}
				
				Range dateRangeTemp;
				if (colorWhite) // White row
					dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_WHITE_ROW + dataRowCount);
				else // Light blue row
					dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_LIGHTBLUE_ROW + dataRowCount);
				Range dateRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
				dateRange.copy(dateRangeTemp);
				dateRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
					rowPageTracker.useRemainingRow(dataRowCount);
					rowPageTracker.resetRemainingRow();
				}
				else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
					rowPageTracker.resetRemainingRow();
					rowPageTracker.useRemainingRow(dataRowCount);
				}
				else {
					rowPageTracker.useRemainingRow(dataRowCount);
				}
				
				// B5_1
				Cell erAlMark = cells.get(currentRow, 0);
				erAlMark.setValue(employee.getErrorAlarmCode());
				
				// B5_2
				Cell employeeCodeCell = cells.get(currentRow, 1);
				employeeCodeCell.setValue(employee.getEmployeeCode());
				
				
				Cell employeeNameCell = cells.get(currentRow, 2);
				employeeNameCell.setValue(employee.getEmployeeName());
				
				
//				Range employeeRange = cells.createRange(currentRow, 1, dataRowCount, 2);
//				employeeRange.merge();
				
				
				if (lstItem != null) {
					// B5_3
					// Divide list into smaller lists (max 16 items)
					int size = lstItem.size();
					int numOfChunks = (int)Math.ceil((double)size / chunkSize);
					int start, length;
					List<ActualValue> lstItemRow;
					
					int curRow = currentRow;
		
			        for(int i = 0; i < numOfChunks; i++) {
			            start = i * chunkSize;
			            length = Math.min(size - start, chunkSize);
		
			            lstItemRow = lstItem.subList(start, start + length);
			            for (int j = 0; j < length; j++) {
			            	// Column 4, 6, 8,...
			            	ActualValue actualValue = lstItemRow.get(j);
			            	Cell cell = cells.get(curRow, DATA_COLUMN_INDEX[0] + j * 2); 
			            	writeDetailValue(actualValue, cell, condition.getZeroDisplayType(), fontSize);
			            }
			            
			            curRow++;
			        }
			        
			        // B5_4
			        Cell remarkCell;
			        String errorDetail = employee.getDetailedErrorData();
			        String[] errorSplipData = errorDetail.split("/n");
					int curRowRemark = currentRow;
		            for (int j = 0; j < errorSplipData.length; j++) {
		            	// Column 4, 6, 8,...
		            	remarkCell = cells.get(curRowRemark, remarkPosition);
		            	Style style = remarkCell.getStyle();
		            	remarkCell.setValue(errorSplipData[j]);
						style.setHorizontalAlignment(TextAlignmentType.LEFT);
		            	setFontStyle(style, fontSize);
		            	remarkCell.setStyle(style);
			            curRowRemark++;
		            }
				}
		        currentRow += dataRowCount;
		        colorWhite = !colorWhite; // Change to other color
		        
		        // Only break when has next iterator
		        if (dataIterator.hasNext() && condition.getPageBreakIndicator() == PageBreakIndicator.EMPLOYEE) {
		        	// Thin border for last row of page
		        	Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, contentPosition);
		        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		        	
		        	// Page break by employee
                    rowPageTracker.resetRemainingRow();
                    if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                        cells = sheetInfo.getSheet().getCells();
                        currentRow = sheetInfo.getStartDataIndex();
                    }
                    currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, contentPosition1);
                    currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
                    rowPageTracker.useRemainingRow(2);
		        }
			}
		}
		
		// Workplace total, root workplace use gross total instead
		if (condition.getSettingDetailTotalOutput().isWorkplaceTotal() && rootWorkplace.getLevel() != 0) {
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
                rowPageTracker.resetRemainingRow();
                if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                    cells = sheetInfo.getSheet().getCells();
                    currentRow = sheetInfo.getStartDataIndex();
                }
                currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, contentPosition1);
                currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
                rowPageTracker.useRemainingRow(2);
			}
			Range workplaceTotalTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range workplaceTotal = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
			workplaceTotal.copy(workplaceTotalTemp);
			workplaceTotal.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
			workplaceTotal.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
				rowPageTracker.resetRemainingRow();
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			// B6_1
			Cell workplaceTotalCellTag = cells.get(currentRow, 0);
			workplaceTotalCellTag.setValue(WorkScheOutputConstants.WORKPLACE_TOTAL);
			
			// B6_2
			currentRow = writeWorkplaceTotal(currentRow
					, rootWorkplace
					, sheetInfo.getSheet()
					, dataRowCount
					, true
					, chunkSize
					, condition.getZeroDisplayType()
					, fontSize);
		}
		
		boolean firstWorkplace = true;

		
		Map<String, DailyWorkplaceData> mapChildWorkplace = rootWorkplace.getLstChildWorkplaceData();
		if (((condition.getPageBreakIndicator() == PageBreakIndicator.WORKPLACE || 
				condition.getPageBreakIndicator() == PageBreakIndicator.EMPLOYEE) &&
				mapChildWorkplace.size() > 0) && rootWorkplace.level != 0) {
			Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, contentPosition);
        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
            if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                cells = sheetInfo.getSheet().getCells();
                currentRow = sheetInfo.getStartDataIndex();
            }
            rowPageTracker.resetRemainingRow();
		}
		// Child workplace
		for (Map.Entry<String, DailyWorkplaceData> entry: mapChildWorkplace.entrySet()) {
			// Page break by workplace
			if ((condition.getPageBreakIndicator() == PageBreakIndicator.WORKPLACE || 
					condition.getPageBreakIndicator() == PageBreakIndicator.EMPLOYEE) && !firstWorkplace) {
				Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, contentPosition);
	        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
                if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                    cells = sheetInfo.getSheet().getCells();
                    currentRow = sheetInfo.getStartDataIndex();
                }
                rowPageTracker.resetRemainingRow();
			}

			firstWorkplace = false;

			currentRow = writeDailyDetailedPerformanceDataOnWorkplace(currentRow
					, sheetInfo
					, templateSheetCollection
					, entry.getValue()
					, dataRowCount
					, condition
					, rowPageTracker
					, titleDate
					, chunkSize
					, fontSize);

		}
		
		// Workplace hierarchy total
		int level = rootWorkplace.getLevel();
		TotalWorkplaceHierachy settingTotalHierarchy = condition.getSettingDetailTotalOutput().getWorkplaceHierarchyTotal();
		
		List<Integer> lstBetweenLevel = findEnabledLevelBetweenWorkplaces(rootWorkplace, settingTotalHierarchy);
		Iterator<Integer> levelIterator = null;
		if (!lstBetweenLevel.isEmpty()) {
			levelIterator = lstBetweenLevel.iterator();
		}
		
		if (level != 0 && level >= settingTotalHierarchy.getHighestLevelEnabled() 
				&& condition.getSettingDetailTotalOutput().isCumulativeWorkplace()) {
			do {
				if (levelIterator != null && levelIterator.hasNext()) 
					level = (int) levelIterator.next();
				if (!settingTotalHierarchy.checkLevelEnabled(level)) break;
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
                    rowPageTracker.resetRemainingRow();
                    if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
                        cells = sheetInfo.getSheet().getCells();
                        currentRow = sheetInfo.getStartDataIndex();
                    }
                    currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, contentPosition1);
                    currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, contentPosition);
                    rowPageTracker.useRemainingRow(2);
				}
				Range wkpHierTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
				Range wkpHierTotalRange = cells.createRange(currentRow, 0, dataRowCount, contentPosition);
				wkpHierTotalRange.copy(wkpHierTotalRangeTemp);
				wkpHierTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
					rowPageTracker.useRemainingRow(dataRowCount);
					rowPageTracker.resetRemainingRow();
				}
				else if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) < 0) {
					rowPageTracker.resetRemainingRow();
					rowPageTracker.useRemainingRow(dataRowCount);
				}
				else {
					rowPageTracker.useRemainingRow(dataRowCount);
				}
				
				// B7_1 - B11_1
				Cell workplaceTotalCellTag = cells.get(currentRow, 0);
				workplaceTotalCellTag.setValue(TextResource.localize("KWR001_116", Integer.toString(level)));
				
				// B7_2 - B11_2
				currentRow = writeWorkplaceTotal(currentRow
						, rootWorkplace
						, sheetInfo.getSheet()
						, dataRowCount
						, false
						, chunkSize
						, condition.getZeroDisplayType()
						, fontSize);
			} while (levelIterator != null && levelIterator.hasNext());
		}
		
		return currentRow;
	}

	/**
	 * Write workplace total.
	 *
	 * @param currentRow the current row
	 * @param rootWorkplace the root workplace
	 * @param sheet the sheet
	 * @param dataRowCount the data row count
	 * @param totalType the total type
	 * @return the int
	 */
	private int writeWorkplaceTotal(int currentRow
			, DailyWorkplaceData rootWorkplace
			, Worksheet sheet
			, int dataRowCount
			, boolean totalType
			, int chunkSize
			, ZeroDisplayType zeroDisplayType
			, FontSizeEnum fontSize) {
		List<TotalValue> totalWorkplaceValue;
		if (!totalType)
			totalWorkplaceValue = rootWorkplace.getWorkplaceTotal().getTotalWorkplaceValue();
		else
			totalWorkplaceValue = rootWorkplace.getWorkplaceHierarchyTotal().getTotalWorkplaceValue();
		int size = totalWorkplaceValue.size();
		if (size == 0) {
			currentRow += dataRowCount;
		}
		else {
			
			Cells cells = sheet.getCells(); 	
			
			int numOfChunks = (int) Math.ceil( (double) size / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;
			
			for(int i = 0; i < numOfChunks; i++) {
			    start = i * chunkSize;
			    length = Math.min(size - start, chunkSize);

			    lstItemRow = totalWorkplaceValue.stream().collect(Collectors.toList()).subList(start, start + length);
			    TotalValue totalValue;
			    
			    for (int j = 0; j < length; j++) {
			    	totalValue = lstItemRow.get(j);
		        	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2); 
		        	writeTotalValue(totalValue, cell, zeroDisplayType, fontSize);
			    }
			    currentRow++;
			}
			
			if (numOfChunks == 0) currentRow++;
		}
		
		return currentRow;
	}

	/**
	 * Write total value.
	 *
	 * @param totalValue the total value
	 * @param cell the cell
	 */
	private void writeTotalValue(TotalValue totalValue, Cell cell, ZeroDisplayType zeroDisplayType, FontSizeEnum fontSize) {
		Style style = cell.getStyle();
		String value = totalValue.getValue();
    	ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValue.getValueType(), ValueType.class);
    	if (valueTypeEnum.isIntegerCountable()) {
    		if ((valueTypeEnum == ValueType.COUNT) && value != null) {
    			cell.putValue((zeroDisplayType == ZeroDisplayType.NON_DISPLAY && value.equals("0")) ? "" : totalValue.formatValue(), true);
    		} else {
    			if (!StringUtil.isNullOrEmpty(value, false))
					cell.setValue(getTimeAttr(value, false, zeroDisplayType));
				else
					cell.setValue(getTimeAttr("0", false, zeroDisplayType));
    		}
			style.setHorizontalAlignment(TextAlignmentType.RIGHT);
		} else if (this.isNumber(value) && (valueTypeEnum.isDouble() || valueTypeEnum.isInteger())) {
			if (valueTypeEnum.isDouble()) {
				double actualDoubleValue = value != null ? Double.parseDouble(value) : 0d;
				cell.setValue((zeroDisplayType == ZeroDisplayType.NON_DISPLAY && actualDoubleValue == 0d) ? "" : totalValue.formatValue());
			} else if (valueTypeEnum.isInteger()) {
				int actualIntValue = value != null ? Integer.parseInt(value) : 0;
				cell.setValue((zeroDisplayType == ZeroDisplayType.NON_DISPLAY && actualIntValue == 0) ? "" : totalValue.formatValue());
			} else
				cell.setValue((zeroDisplayType == ZeroDisplayType.NON_DISPLAY && value.equals("0")) ? "" : totalValue.formatValue());
			style.setHorizontalAlignment(TextAlignmentType.RIGHT);
		} else {
			cell.setValue((value != null && zeroDisplayType == ZeroDisplayType.NON_DISPLAY && value.equals("0")) ? "" : totalValue.formatValue());
    	}
    	setFontStyle(style, fontSize);
    	cell.setStyle(style);
	}
	
	/**
	 * Write gross total.
	 *
	 * @param currentRow the current row
	 * @param lstGrossTotal the lst gross total
	 * @param sheet the sheet
	 * @param dataRowCount the data row count
	 * @param rowPageTracker the row page tracker
	 * @return the int
	 */
	private int writeGrossTotal(int currentRow
			, List<TotalValue> lstGrossTotal
			, Worksheet sheet
			, int dataRowCount
			, RowPageTracker rowPageTracker
			, int chunkSize
			, ZeroDisplayType zeroDisplayType
			, FontSizeEnum fontSizeEnum) {
		int size = lstGrossTotal.size();
		if (size == 0) {
			currentRow += dataRowCount;
		}
		else {
			Cells cells = sheet.getCells();
			
			int numOfChunks = (int) Math.ceil( (double) size / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;
			
			for(int i = 0; i < numOfChunks; i++) {
			    start = i * chunkSize;
			    length = Math.min(lstGrossTotal.size() - start, chunkSize);

			    lstItemRow = lstGrossTotal.stream().collect(Collectors.toList()).subList(start, start + length);
			    
			    TotalValue totalValue;
			    
			    for (int j = 0; j < length; j++) {
			    	totalValue =lstItemRow.get(j);
		        	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2); 
		        	writeTotalValue(totalValue, cell, zeroDisplayType, fontSizeEnum);
			    }
			    currentRow++;
			}
			
			if (numOfChunks == 0) currentRow++;
		}
		
		return currentRow;
	}
	
	/**
	 * Hide footer.
	 *
	 * @param sheet the sheet
	 */
	private void hideFooter(Worksheet sheet) {
		PageSetup pageSetup = sheet.getPageSetup();
		pageSetup.setFooter(0, "");
	}
	
	/**
	 * Creates the print area.
	 *
	 * @param currentRow the current row
	 * @param sheet the sheet
	 */
	// fix bug 106400
//	private void createPrintArea( Worksheet sheet) {
//		// Get the last column and row name
//		Cells cells = sheet.getCells();
//		// Set max row when print
//		int maxDataRow = cells.getMaxDataRow() + 1;
//		Cell lastCell = cells.get(maxDataRow, 38);
//		String lastCellName = lastCell.getName();
//		
//		PageSetup pageSetup = sheet.getPageSetup();
//		pageSetup.setPrintArea("A1:" + lastCellName);
//	}
	
	/**
	 * Gets the time attr.
	 *
	 * @param rawValue the raw value
	 * @param isConvertAttr the is convert attr
	 * @return the time attr
	 */
	private String getTimeAttr(String rawValue, boolean isConvertAttr, ZeroDisplayType displayType) {
		int value = Integer.parseInt(rawValue);
		TimeDurationFormatExtend timeFormat = new TimeDurationFormatExtend(value);
		if (isConvertAttr) {
			//AttendanceTimeOfExistMinus time = new AttendanceTimeOfExistMinus(value);
			return timeFormat.getFullText();
		} else {
			return displayType == ZeroDisplayType.NON_DISPLAY && timeFormat.isZero() ? "" : timeFormat.getTimeText();
		}
	}
	
	/**
	 * Find workplace higher enabled level.
	 *
	 * @param workplaceReportData the workplace report data
	 * @param hierarchy the hierarchy
	 * @return the int
	 */
	private int findWorkplaceHigherEnabledLevel(WorkplaceReportData workplaceReportData, TotalWorkplaceHierachy hierarchy) {
		int currentLevel = workplaceReportData.getLevel();
		if (currentLevel != 0) {
			int lowerEnabledLevel = hierarchy.getLowerClosestSelectedHierarchyLevel(currentLevel);
			if (lowerEnabledLevel == 0) {
				WorkplaceReportData parentWorkplace = workplaceReportData.getParent();
				return findWorkplaceHigherEnabledLevel(parentWorkplace, hierarchy);
			}
			return lowerEnabledLevel;
		}
		else {
			return 0;
		}
	}
	
	/**
	 * Find enabled level between workplaces.
	 *
	 * @param workplaceReportData the workplace report data
	 * @param hierarchy the hierarchy
	 * @return the list
	 */
	private List<Integer> findEnabledLevelBetweenWorkplaces(WorkplaceReportData workplaceReportData, TotalWorkplaceHierachy hierarchy) {
		List<Integer> lstEnabledLevel = new ArrayList<>();
		
		if (workplaceReportData.getLevel() == 0)
			return lstEnabledLevel;
		
		WorkplaceReportData parentWorkplace = workplaceReportData.getParent();
		for (int i = workplaceReportData.getLevel(); i > parentWorkplace.getLevel(); i--) {
			if (hierarchy.checkLevelEnabled(i))
				lstEnabledLevel.add(i);
		}
		return lstEnabledLevel;
	}
	
	/**
	 * Find enabled level between workplaces.
	 *
	 * @param workplaceReportData the workplace report data
	 * @param hierarchy the hierarchy
	 * @return the list
	 */
	private List<Integer> findEnabledLevelBetweenWorkplaces(DailyWorkplaceData workplaceReportData, TotalWorkplaceHierachy hierarchy) {
		List<Integer> lstEnabledLevel = new ArrayList<>();
		
		if (workplaceReportData.getLevel() == 0)
			return lstEnabledLevel;
		
		DailyWorkplaceData parentWorkplace = workplaceReportData.getParent();
		for (int i = workplaceReportData.getLevel(); i > parentWorkplace.getLevel(); i--) {
			if (hierarchy.checkLevelEnabled(i))
				lstEnabledLevel.add(i);
		}
		return lstEnabledLevel;
	}
	
	
	/**
	 * Align top cotent.
	 *
	 * @param sheet the sheet
	 */
	private void alignTopCotent(Worksheet sheet) {
		PageSetup pageSetup = sheet.getPageSetup();
		pageSetup.setCenterHorizontally(true);
		pageSetup.setCenterVertically(false); 
	}
	
	/**
	 * Gets the remark content.
	 *
	 * @param employeeId 社員ID
	 * @param currentDate 期間(年月日)
	 * @param choice the choice
	 * @param lstOutputErrorCode the lst output error code
	 * @param remarkDataContainer the remark data container
	 * @return the remark content
	 */
	public Optional<PrintRemarksContent> getRemarkContent(String employeeId
			, GeneralDate currentDate
			, RemarksContentChoice choice
			, List<ErrorAlarmWorkRecordCode> lstOutputErrorCode
			, RemarkQueryDataContainer remarkDataContainer) {
		PrintRemarksContent printRemarksContent = null;
		List<EmployeeDailyPerError> errorList = remarkDataContainer.getErrorList();
		//List<String> errorCodeList = lstOutputErrorCode.stream().map(x -> x.v()).collect(Collectors.toList());
		
		// 遅刻早退
		if (errorList.size() > 0) {
			// Late come
			boolean isLateCome = false, isEarlyLeave = false;
			Optional<EmployeeDailyPerError> optErrorLateCome = errorList.stream()
					.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.LATE.value)).findFirst();
			if (optErrorLateCome.isPresent() && (choice == RemarksContentChoice.LEAVING_EARLY)) {
				isLateCome = true;
			}
			
			// Early leave
			Optional<EmployeeDailyPerError> optErrorEarlyLeave = errorList.stream()
					.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.LEAVE_EARLY.value)).findFirst();
			if (optErrorEarlyLeave.isPresent() && (choice == RemarksContentChoice.LEAVING_EARLY)) {
				isEarlyLeave = true;
			}
			
			// Both case
			if (isLateCome && isEarlyLeave) {
				printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.LATE_COME_EARLY_LEAVE.value);
			}
			// Late come
			else if (isLateCome) {
				printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.LATE_COME.value);
			}
			// Early leave
			else if (isEarlyLeave) {
				printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.LEAVING_EARLY.value);
			}
		}
		
		if (choice == RemarksContentChoice.MANUAL_INPUT || choice == RemarksContentChoice.ACKNOWLEDGMENT) {
			// 手入力
			List<EditStateOfDailyPerformanceDto> editStateDto = remarkDataContainer.getEditStateDto();
			List<GeneralDate> lstEditStateDate = new ArrayList<>();
			// Likely lstEditStateDate only has 0-1 element
			editStateDto.stream().filter(x -> (x.getEditStateSetting() == EditStateSetting.HAND_CORRECTION_MYSELF.value
										   || x.getEditStateSetting() == EditStateSetting.HAND_CORRECTION_OTHER.value)
										   && x.getYmd().compareTo(currentDate) == 0).forEach(x -> lstEditStateDate.add(x.getYmd()));
			
			if (lstEditStateDate.size() > 0 && choice == RemarksContentChoice.MANUAL_INPUT) {
				printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.MANUAL_INPUT.value);
			}
			
			// 承認反映
			Boolean isApprovalDate = editStateDto.stream().filter(x -> x.getEditStateSetting() == EditStateSetting.REFLECT_APPLICATION.value).map(x -> {return x.getYmd();}).anyMatch(y-> y.equals(currentDate));
			if (isApprovalDate && choice == RemarksContentChoice.ACKNOWLEDGMENT) {
				printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.ACKNOWLEDGMENT.value);
			}
		}
		
		// 未計算
		List<WorkInfoOfDailyPerformanceDetailDto> dailyPerformanceList = remarkDataContainer.getDailyPerformanceList();
		if (dailyPerformanceList != null && !dailyPerformanceList.isEmpty() && choice == RemarksContentChoice.NOT_CALCULATED) {
			Optional<WorkInfoOfDailyPerformanceDetailDto> optDailyPerformanceWorkInfo = dailyPerformanceList.stream().filter(info -> info.getYmd().compareTo(currentDate) == 0
					&& info.getCalculationState() == CalculationStateDto.No_Calculated
					&& StringUtils.equals(info.getEmployeeId(), employeeId)).findFirst();
			if (optDailyPerformanceWorkInfo.isPresent()) {
				printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.NOT_CALCULATED.value);
			}
		}
		
		// 事前申請超過
		Optional<EmployeeDailyPerError> optErrorExceedByApplication = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.PRE_OVERTIME_APP_EXCESS.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.PRE_HOLIDAYWORK_APP_EXCESS.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.PRE_FLEX_APP_EXCESS.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.PRE_MIDNIGHT_EXCESS.value)).findFirst();
		if (optErrorExceedByApplication.isPresent() && (choice == RemarksContentChoice.EXCEED_BY_APPLICATION)) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.EXCEED_BY_APPLICATION.value);
		}
		
		// 打刻順序不正
		Optional<EmployeeDailyPerError> optErrorIncorrectStamp = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.INCORRECT_STAMP.value)).findFirst();
		if (optErrorIncorrectStamp.isPresent() && (choice == RemarksContentChoice.IMPRINTING_ORDER_NOT_CORRECT)) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.IMPRINTING_ORDER_NOT_CORRECT.value);
		}
		
		// 打刻漏れ
		Optional<EmployeeDailyPerError> optErrorEngraving = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.TIME_LEAVING_STAMP_LEAKAGE.value)).findFirst();
		if (optErrorEngraving.isPresent() && (choice == RemarksContentChoice.ENGRAVING)) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.ENGRAVING.value);
		}
		
		// 二重打刻
		Optional<EmployeeDailyPerError> optErrorDoubleStamp = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DOUBLE_STAMP.value)).findFirst();
		if (optErrorDoubleStamp.isPresent() && (choice == RemarksContentChoice.DOUBLE_ENGRAVED)) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.DOUBLE_ENGRAVED.value);
		}
		
		// 休日打刻
		Optional<EmployeeDailyPerError> optErrorHolidayStamp = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.HOLIDAY_STAMP.value)).findFirst();
		if (optErrorHolidayStamp.isPresent() && (choice == RemarksContentChoice.HOLIDAY_STAMP)) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.HOLIDAY_STAMP.value);
		}
		
		// 乖離エラー
		Optional<EmployeeDailyPerError> deviationError = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_1.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_2.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_3.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_4.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_5.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_6.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_7.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_8.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_9.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ERROR_10.value)).findFirst();
		
		if (deviationError.isPresent() && choice == RemarksContentChoice.DEVIATION_ERROR) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.DEVIATION_ERROR.value);
		}
		
		// 乖離アラーム
		Optional<EmployeeDailyPerError> deviationAlarm = errorList.stream()
				.filter(x -> x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_1.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_2.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_3.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_4.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_5.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_6.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_7.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_8.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_9.value)
						  || x.getErrorAlarmWorkRecordCode().v().contains(SystemFixedErrorAlarm.DIVERGENCE_ALARM_10.value)).findFirst();
		
		if (deviationAlarm.isPresent() && choice == RemarksContentChoice.DEVIATION_ALARM) {
			printRemarksContent = new PrintRemarksContent(1, RemarksContentChoice.DEVIATION_ALARM.value);
		}
		
		return Optional.ofNullable(printRemarksContent);
	}
	
	/**
	 * Gets the name from code.
	 *
	 * @param attendanceId the attendance id
	 * @param code the code
	 * @param queryData the query data
	 * @param outSche the out sche
	 * @return the name from code
	 */
	private String getNameFromCode(int attendanceId
			, String code
			, WorkScheduleQueryData queryData
			, OutputItemDailyWorkSchedule outSche
			, SwitchItemDisplay displayType) {
		// Get all data
		List<WorkType> lstWorkType = queryData.getLstWorkType();
		List<WorkTimeSetting> lstWorkTime = queryData.getLstWorkTime();
		List<CodeName> lstWorkplaceInfo = queryData.getLstWorkplaceInfo();
		List<CodeName> lstWorkLocation = queryData.getLstWorkLocation();
		List<CodeName> lstReason = queryData.getLstReason();
		List<CodeName> lstClassification = queryData.getLstClassification();
		List<CodeName> lstPosition = queryData.getLstPosition();
		List<CodeName> lstEmployment = queryData.getLstEmployment();
		List<CodeName> lstBusiness = queryData.getLstBusiness();
		List<CodeName> lstAddition = queryData.getLstAddition();
		List<CodeName> lstWork = queryData.getLstWork();
		
		
		// Not set -> won't check master unregistered
		if (StringUtils.isEmpty(code)) {
			return "";
		}
		
		if (attendanceId == ATTENDANCE_ID_BUSSINESS_TYPE) {
			Optional<CodeName> optWorkplace = lstBusiness.stream()
					.filter(workplace -> workplace.getCode().equalsIgnoreCase(code)).findFirst();
			if (!optWorkplace.isPresent()) {
				return MASTER_UNREGISTERED;
			}

			CodeName workplace = optWorkplace.get();
			return workplace.getName();
		}
		
		
		if (IntStream.of(ATTENDANCE_ID_WORK_TYPE).anyMatch(id -> id == attendanceId)) {
			Optional<WorkType> optWorkType = lstWorkType.stream()
					.filter(type -> type.getWorkTypeCode().v().equalsIgnoreCase(code)).findFirst();

			if (!optWorkType.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}

			WorkType workType = optWorkType.get();
			String workTypeName = outSche.getWorkTypeNameDisplay() == NameWorkTypeOrHourZone.OFFICIAL_NAME
									? workType.getName().v()
									: workType.getAbbreviationName().v();

			return workTypeName;
		}
		
		if (IntStream.of(ATTENDANCE_ID_WORK_TIME).anyMatch(id -> id == attendanceId)) {
			Optional<WorkTimeSetting> optWorkTime = lstWorkTime.stream()
					.filter(type -> type.getWorktimeCode().v().equalsIgnoreCase(code)).findFirst();
			
			if (!optWorkTime.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}

			WorkTimeSetting workTime = optWorkTime.get();
			if (outSche.getWorkTypeNameDisplay() == NameWorkTypeOrHourZone.OFFICIAL_NAME) {
				return workTime.getWorkTimeDisplayName().getWorkTimeName().v();
			}

			return workTime.getWorkTimeDisplayName().getWorkTimeAbName().v();
		}
		
		// 勤務場所コード
		if (IntStream.of(ATTENDANCE_ID_WORK_LOCATION).anyMatch(id -> id == attendanceId)) {
			Optional<CodeName> optWorkLocation = lstWorkLocation.stream()
					.filter(location -> location.getCode().equalsIgnoreCase(code)).findFirst();
			
			if (!optWorkLocation.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}
			
			CodeName workLocation = optWorkLocation.get();
			return displayType == SwitchItemDisplay.DISPLAY_CODE ? workLocation.getCode() : workLocation.getName();
		}
		
		// 職場コード
		if (attendanceId == ATTENDANCE_ID_WORKPLACE) {
			Optional<CodeName> optWorkplace = lstWorkplaceInfo.stream()
					.filter(workplace -> workplace.getId().equalsIgnoreCase(code)).findFirst();
			if (!optWorkplace.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}

			CodeName workplace = optWorkplace.get();
			return displayType == SwitchItemDisplay.DISPLAY_CODE ? workplace.getCode() : workplace.getName();
		}

		if (IntStream.of(ATTENDANCE_ID_REASON).anyMatch(id -> id == attendanceId)) {
			String itemId = this.getIdFromAttendanceId(attendanceId);
			List<CodeName> optReason = lstReason.stream().filter(reason -> reason.getCode().equalsIgnoreCase(code))
					.filter(item -> item.getId().equalsIgnoreCase(itemId)).collect(Collectors.toList());
			if (optReason.isEmpty()) {
				return code + " " + MASTER_UNREGISTERED;
			}
			return optReason.get(0).getName();
		}
		
		if (attendanceId == ATTENDANCE_ID_CLASSIFICATION) {
			Optional<CodeName> optClassification = lstClassification.stream()
					.filter(classification -> classification.getCode().equalsIgnoreCase(code))
					.findFirst();

			if (!optClassification.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}

			CodeName classification = optClassification.get();
			return classification.getName();
		}
		
		if (attendanceId == ATTENDANCE_ID_POSITION) {
			Optional<CodeName> optPosition = lstPosition.stream()
					.filter(position -> position.getId().equalsIgnoreCase(code)).findFirst();

			if (!optPosition.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}

			CodeName position = optPosition.get();
			return position.getName();
		}
		
		if (attendanceId == ATTENDANCE_ID_EMPLOYMENT) {
			Optional<CodeName> optEmployment = lstEmployment.stream()
					.filter(employment -> employment.getCode().equalsIgnoreCase(code)).findFirst();
			if (!optEmployment.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}

			CodeName employment = optEmployment.get();
			return employment.getName();
		}

		if (IntStream.of(ATTENDANCE_ID_CALCULATION_MAP).anyMatch(id -> id == attendanceId)) {
			return EnumAdaptor.valueOf(Integer.valueOf(code), AutoCalAtrOvertime.class).description;
		}

		if (IntStream.of(ATTENDANCE_ID_OVERTIME_MAP).anyMatch(id -> id == attendanceId)) {
			return EnumAdaptor.valueOf(Integer.valueOf(code),
					TimeLimitUpperLimitSetting.class).description;
		}

		if (IntStream.of(ATTENDANCE_ID_OUTSIDE_MAP).anyMatch(id -> id == attendanceId)) {
			return TextResource.localize(
					EnumAdaptor.valueOf(Integer.valueOf(code), GoingOutReason.class).nameId);
		}

		if (IntStream.of(ATTENDANCE_ID_USE_MAP).anyMatch(id -> id == attendanceId)) {
			return EnumAdaptor.valueOf(Integer.valueOf(code), ManageAtr.class).description;
		}
		
		// 加給コード
		if (attendanceId == ATTENDANCE_ID_ADDITION) {
			Optional<CodeName> oAddtion = lstAddition.stream()
					.filter(add -> add.getCode().equalsIgnoreCase(code))
					.findFirst();
			if (!oAddtion.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}
			CodeName addition = oAddtion.get();
			return displayType == SwitchItemDisplay.DISPLAY_NAME ? addition.getName() : addition.getCode();
		}
		
		// 作業コード
		if (IntStream.of(ATTENDANCE_ID_WORKCODE).anyMatch(id -> id == attendanceId)) {
			Optional<CodeName> oWork = lstWork.stream()
					.filter(add -> add.getCode().equalsIgnoreCase(code))
					.findFirst();
			if (!oWork.isPresent()) {
				return code + " " + MASTER_UNREGISTERED;
			}
			CodeName addition = oWork.get();
			return displayType == SwitchItemDisplay.DISPLAY_NAME ? addition.getName() : addition.getCode();
		}

		return "";
	}

	//convert from attendanceId to reasonId
	private String getIdFromAttendanceId(int attendanceId) {
		int indexNumber = 0;
		for (int i = 0; i < ATTENDANCE_ID_REASON.length; i++)
			if (ATTENDANCE_ID_REASON[i] == attendanceId) {
				indexNumber = i;
			}
		//because index start from 0
		return String.valueOf(indexNumber + 1);
	}

	private Worksheet copySheet(WorksheetCollection wsc, WorkSheetInfo sheetInfo) throws Exception {
		wsc.addCopy(sheetInfo.getOriginSheetIndex());
		Worksheet ws = wsc.get(wsc.getCount() - 1);
		// sheet name
		sheetInfo.plusNewSheetIndex();
		String sheetName = sheetInfo.getSheetName();
		ws.setName(sheetName);
		return ws;
	}

	private boolean checkLimitPageBreak(WorksheetCollection wsc, WorkSheetInfo sheetInfo, int currentRow) throws Exception {
		if (sheetInfo.getSheet().getHorizontalPageBreaks().getCount() >= MAX_PAGE_PER_SHEET - 1) {
			sheetInfo.setSheet(this.copySheet(wsc, sheetInfo));
			return true;
		} else {
			// check have row is printed
			if (sheetInfo.getStartDataIndex() != currentRow) {
				sheetInfo.getSheet().getHorizontalPageBreaks().add(currentRow);
			}
			return false;
		}
	}

	private int printWorkplace(int currentRow, WorksheetCollection templateSheetCollection,
		WorkSheetInfo sheetInfo, String title, int contentPosition) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		Range workplaceRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_WORKPLACE_ROW);
		Range workplaceRange = cells.createRange(currentRow, 0, 1, contentPosition);
		workplaceRange.copy(workplaceRangeTemp);
		workplaceRange.setRowHeight(workplaceRangeTemp.getRowHeight());
		Cell workplaceTagCell = cells.get(currentRow, 0);
		workplaceTagCell.setValue(title);
		currentRow++;
		return currentRow;
	}

	private int printPersonal(int currentRow, WorksheetCollection templateSheetCollection,
		WorkSheetInfo sheetInfo, String title, int contentPosition) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		Range employeeRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_EMPLOYEE_ROW);
		Range employeeRange = cells.createRange(currentRow, 0, 1, contentPosition);
		employeeRange.copy(employeeRangeTemp);
		employeeRange.setRowHeight(employeeRangeTemp.getRowHeight());
		Cell employeeTagCell = cells.get(currentRow, 0);
		employeeTagCell.setValue(title);
		currentRow++;
		return currentRow;
	}

	private int printDateBracket(int currentRow, WorksheetCollection templateSheetCollection,
								 WorkSheetInfo sheetInfo, String titleDate, int contentPosition) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		Range dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_DATE_ROW);
		Range dateRange = cells.createRange(currentRow, 0, 1, contentPosition);
//		dateRangeTemp.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
		dateRange.copy(dateRangeTemp);
		dateRange.setRowHeight(dateRangeTemp.getRowHeight());
		Cell dateTagCell = cells.get(currentRow, 0);
		dateTagCell.setValue(titleDate);
		currentRow++;
		return currentRow;
	}

    private int printWorkplaceByDate(int currentRow, WorksheetCollection templateSheetCollection,
                               WorkSheetInfo sheetInfo, String workplaceTitle, int contentPosition) throws Exception {
        Cells cells = sheetInfo.getSheet().getCells();
        Range workplaceRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_DAILY_WORKPLACE_ROW);
        Range workplaceRange = cells.createRange(currentRow, 0, 1, contentPosition);
        workplaceRange.copy(workplaceRangeTemp);
        workplaceRange.setRowHeight(workplaceRangeTemp.getRowHeight());
        Cell workplaceTagCell = cells.get(currentRow, 0);
        workplaceTagCell.setValue(workplaceTitle);
        currentRow++;
        return currentRow;
    }
    
    private List<WorkplaceConfigInfo> convertData(List<WorkplaceInformation> wp) {
		Map<Pair<String, String>, List<WorkplaceInformation>> map =
				wp.stream().collect(Collectors.groupingBy(p -> Pair.of(p.getCompanyId(), p.getWorkplaceHistoryId())));
		List<WorkplaceConfigInfo> returnList = new ArrayList<WorkplaceConfigInfo>();
		for (Pair<String, String> key : map.keySet()) {
			returnList.add(new WorkplaceConfigInfo(key.getLeft(), key.getRight(), 
					map.get(key).stream().map(x -> WorkplaceHierarchy.newInstance(x.getWorkplaceId(), x.getHierarchyCode().v())).collect(Collectors.toList())));
		}
		return returnList;
	}

    private boolean isNumber(String value) {
    	Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        return value != null ? pattern.matcher(value).matches() :  false;
    }
    
    private String cutStringByByte(String valueStr, int start, int size) {
    	String[] valueStrArr = valueStr.split("");
    	StringBuilder result = new StringBuilder();
    	int numberOfByte = 0;
    	for (int i = 0; i < valueStrArr.length; i++) {
			numberOfByte += valueStrArr[i].getBytes().length;
    		if (numberOfByte > size) {
    			break;
    		}
    		result.append(valueStrArr[i]);
		}
    	return result.toString();
    }
}
