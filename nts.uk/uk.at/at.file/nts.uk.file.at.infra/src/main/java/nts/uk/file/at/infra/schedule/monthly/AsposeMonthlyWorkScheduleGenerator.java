/******************************************************************
 * Copyright (c) 2018 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.file.at.infra.schedule.monthly;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.ItemSelectionEnum;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.MonthlyAttendanceItemsDisplay;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.OutputItemMonthlyWorkSchedule;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.OutputItemMonthlyWorkScheduleRepository;
import nts.uk.ctx.at.function.dom.monthlyworkschedule.TextSizeCommonEnum;
import nts.uk.ctx.at.record.app.service.attendanceitem.value.AttendanceItemValueService;
import nts.uk.ctx.at.record.app.service.attendanceitem.value.AttendanceItemValueService.MonthlyAttendanceItemValueResult;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.employment.EmploymentHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.employment.ScEmploymentAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.SCEmployeeAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.EmployeeDto;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.monthlyattditem.aggregate.MonthlyAttItemCanAggregateRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.adapter.attendanceitemname.AttItemName;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.service.CompanyMonthlyItemService;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
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
import nts.uk.file.at.app.export.dailyschedule.ActualValue;
import nts.uk.file.at.app.export.dailyschedule.FileOutputType;
import nts.uk.file.at.app.export.dailyschedule.TotalWorkplaceHierachy;
import nts.uk.file.at.app.export.dailyschedule.WorkScheduleSettingTotalOutput;
import nts.uk.file.at.app.export.dailyschedule.data.EmployeeReportData;
import nts.uk.file.at.app.export.dailyschedule.data.OutputItemSetting;
import nts.uk.file.at.app.export.dailyschedule.data.WorkplaceReportData;
import nts.uk.file.at.app.export.dailyschedule.totalsum.TotalSumRowOfDailySchedule;
import nts.uk.file.at.app.export.dailyschedule.totalsum.TotalValue;
import nts.uk.file.at.app.export.dailyschedule.totalsum.WorkplaceTotal;
import nts.uk.file.at.app.export.employee.jobtitle.EmployeeJobHistExport;
import nts.uk.file.at.app.export.employee.jobtitle.JobTitleImportAdapter;
import nts.uk.file.at.app.export.monthlyschedule.DetailedMonthlyPerformanceReportData;
import nts.uk.file.at.app.export.monthlyschedule.DisplayTypeEnum;
import nts.uk.file.at.app.export.monthlyschedule.ItemDisplaySwitchEnum;
import nts.uk.file.at.app.export.monthlyschedule.MonthlyRecordValuesExport;
import nts.uk.file.at.app.export.monthlyschedule.MonthlyReportConstant;
import nts.uk.file.at.app.export.monthlyschedule.MonthlyWorkScheduleCondition;
import nts.uk.file.at.app.export.monthlyschedule.MonthlyWorkScheduleGenerator;
import nts.uk.file.at.app.export.monthlyschedule.MonthlyWorkScheduleQuery;
import nts.uk.file.at.app.export.schedule.FileService;
import nts.uk.file.at.infra.schedule.RowPageTracker;
import nts.uk.file.at.infra.schedule.WorkSheetInfo;
import nts.uk.file.at.infra.schedule.daily.TimeDurationFormatExtend;
import nts.uk.file.at.infra.schedule.daily.WorkScheOutputConstants;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportContext;
import nts.uk.shr.infra.file.report.aspose.cells.AsposeCellsReportGenerator;

/**
 * The Class AsposeMonthlyWorkScheduleGenerator.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AsposeMonthlyWorkScheduleGenerator extends AsposeCellsReportGenerator
		implements MonthlyWorkScheduleGenerator {

	/** The company repository. */
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AttendanceItemValueService  attendanceItemValueService;
	
	/** The workplace adapter. */
	@Inject
	private WorkplaceAdapter workplaceAdapter;

	/** The employee adapter. */
	@Inject
	private SCEmployeeAdapter employeeAdapter;
	
	/** The workplace info repository. */
	@Inject
	private WorkplaceInformationRepository workplaceInfoRepository;

	/**  The employment adapter. */
	@Inject
	private ScEmploymentAdapter employmentAdapter;

	/** The job title adapter. */
	@Inject
	private JobTitleImportAdapter jobTitleAdapter;

	/** The finder. */
	@Inject
	private EmploymentRepository employmentRepository;
	
	/** The output item repo. */
	@Inject
	private OutputItemMonthlyWorkScheduleRepository outputItemRepo;
	
	/** The company monthly item service. */
	@Inject
	private CompanyMonthlyItemService companyMonthlyItemService;
	
	@Inject
	private ManagedParallelWithContext parallel;
	
	@Inject
	private FileService service;

	/** The data processor. */
	@Inject
	private DataDialogWithTypeProcessor dataProcessor;
	@Inject
	private ClosureRepository closureRepo;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	
	/** The att item can aggregate repository. */
	@Inject
	private MonthlyAttItemCanAggregateRepository attItemCanAggregateRepository;

	/** The Constant DATA_COLUMN_INDEX. */
//	private static final int[] DATA_COLUMN_INDEX = {3, 9, 11, 15, 17, 39};
    private static final int[] DATA_COLUMN_INDEX = MonthlyReportConstant.DATA_COLUMN_INDEX;
    private static final int REMARK_COLUMN_SMALL_SIZE = 47;

	/** The font family. */
//	private final String FONT_FAMILY = "ＭＳ ゴシック";
    private final String FONT_FAMILY = MonthlyReportConstant.FONT_FAMILY;
	
	/** The Constant DATA_PREFIX_NO_WORKPLACE. */
//	private static final String DATA_PREFIX_NO_WORKPLACE = "NOWPK_";
    private static final String DATA_PREFIX_NO_WORKPLACE = MonthlyReportConstant.DATA_PREFIX_NO_WORKPLACE;
	
//	private static final int CHUNK_SIZE_ERROR = 5;
    /** The Constant CHUNK_SIZE_ERROR. */
    private static final int CHUNK_SIZE_ERROR = MonthlyReportConstant.CHUNK_SIZE_ERROR;
	
//	private static final int REMARK_CELL_WIDTH = 4;
    /** The Constant REMARK_CELL_WIDTH. */
    private static final int REMARK_CELL_WIDTH = MonthlyReportConstant.REMARK_CELL_WIDTH;

	/** The Constant MAX_PAGE_PER_SHEET. */
	private static final int MAX_PAGE_PER_SHEET = 1000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputGenerator#
	 * generate(nts.uk.file.at.app.export.dailyschedule.
	 * WorkScheduleOutputCondition,
	 * nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo,
	 * nts.uk.file.at.app.export.dailyschedule.WorkScheduleOutputQuery)
	 */
	@Override
	public void generate(FileGeneratorContext generatorContext, TaskDataSetter setter, MonthlyWorkScheduleQuery query) {
		MonthlyWorkScheduleCondition condition = query.getCondition();
		AsposeCellsReportContext reportContext = null;
		// ドメインモデル「月別勤務表の出力項目」を取得する
		Optional<OutputItemMonthlyWorkSchedule> optOutputItemMonthlyWork = outputItemRepo
				.findBySelectionAndCidAndSidAndCode(ItemSelectionEnum.valueOf(condition.getItemSettingType())
						, AppContexts.user().companyId()
						, query.getCode()
						, AppContexts.user().employeeId());
		if (!optOutputItemMonthlyWork.isPresent()) {
			// エラーメッセージ(#Msg_1141#)を表示する (Display error msg)
			throw new BusinessException(new RawErrorMessage("Msg_1141"));
		}
		OutputItemMonthlyWorkSchedule outputItemMonthlyWork = optOutputItemMonthlyWork.get();

		if (condition.getOutputType() == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE) {
			reportContext = outputItemMonthlyWork.getTextSize() == TextSizeCommonEnum.BIG 
					? this.createContext(MonthlyReportConstant.TEMPLATE_EMPLOYEE)
					: this.createContext(MonthlyReportConstant.TEMPLATE_EMPLOYEE_SMALL_SIZE);
		} else {
			reportContext = outputItemMonthlyWork.getTextSize() == TextSizeCommonEnum.BIG
					? this.createContext(MonthlyReportConstant.TEMPLATE_DATE)
					: this.createContext(MonthlyReportConstant.TEMPLATE_DATE_SMALL_SIZE);
		}
		
		Workbook workbook;
		try {
			workbook = reportContext.getWorkbook();
			
			WorkbookDesigner designer = new WorkbookDesigner();
			designer.setWorkbook(workbook);
			
			TotalSumRowOfDailySchedule sum = new TotalSumRowOfDailySchedule();
			sum.setPersonalTotal(new ArrayList<>());
			
			// Calculate row size and get sheet
			List<MonthlyAttendanceItemsDisplay> lstAttendanceItemDisplay = outputItemMonthlyWork.getLstDisplayedAttendance();
			int nListOutputCode = lstAttendanceItemDisplay.size();
			int nSize;
			int chunkSize = outputItemMonthlyWork.getTextSize() == TextSizeCommonEnum.BIG
							? MonthlyReportConstant.CHUNK_BIG_SIZE
							: MonthlyReportConstant.CHUNK_SMALL_SIZE;
			if (nListOutputCode % chunkSize == 0) {
				nSize = nListOutputCode / chunkSize;
			} else {
				nSize = nListOutputCode / chunkSize + 1;
			}
			
			// ドメインモデル「月別勤務表の出力項目」をすべて取得する
			List<Integer> lstAtdCanBeAggregate = this.attItemCanAggregateRepository
					.getMonthlyAtdItemCanAggregate(AppContexts.user().companyId())
					.stream()
					.map(t -> t.v().intValue())
					.collect(Collectors.toList());
			/**
			 * Collect data
			 */
			MonthlyPerformanceReportData reportData = new MonthlyPerformanceReportData();
			collectData(reportData, query, condition, outputItemMonthlyWork, setter, lstAtdCanBeAggregate);
			
			Worksheet sheet;
			Integer currentRow, dateRow;
			int remarkColumn = outputItemMonthlyWork.getTextSize() == TextSizeCommonEnum.BIG ? DATA_COLUMN_INDEX[5] : REMARK_COLUMN_SMALL_SIZE;
			
//			if (condition.getOutputType() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE) {
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
				
//			} else {
//				switch (nSize) {
//				case 1:
//					sheet = workbook.getWorksheets().get(3);
//					break;
//				case 2:
//					sheet = workbook.getWorksheets().get(4);
//					break;
//				case 3:
//				default:
//					sheet = workbook.getWorksheets().get(5);
//					
//				}
//			}
			currentRow = 2;
			dateRow = 0;
			
			WorksheetCollection sheetCollection = workbook.getWorksheets();
			
			// Write header data
			writeHeaderData(query, outputItemMonthlyWork, sheet, reportData, dateRow);
			
			// Copy footer
			//copyFooter(sheet, sheetCollection.get(6));
			 	
			// Set all fixed cell
			setFixedData(condition, outputItemMonthlyWork, sheet, reportData, currentRow);
			
			// Write display map
			writeDisplayMap(sheet.getCells(),reportData, currentRow, nSize, chunkSize);
			
			currentRow+=nSize*2;
			
			// Back up start row
			int startRow = currentRow;
			
			// Create row page tracker
			RowPageTracker rowPageTracker = new RowPageTracker();
			rowPageTracker.initMaxRowAllowedMonthly(nSize, condition.getOutputType());

			int sheetCount = sheetCollection.getCount();

			// Get index of temp sheet
			int indexTempSheet = sheet.getIndex();
			String itemNameSelected = outputItemMonthlyWork.getItemName().v();
			WorkSheetInfo sheetInfo = new WorkSheetInfo(sheet, currentRow, indexTempSheet, itemNameSelected);

			// Copy sheet
			sheet = this.copySheet(sheetCollection, sheetInfo);
			sheetInfo.setSheet(sheet);
			
			// Write detailed work schedule
			if (condition.getOutputType() == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE)
				currentRow = writeDetailedWorkSchedule(query
						, currentRow
						, sheetCollection
						, sheetInfo
						, reportData.getWorkplaceReportData()
						, nSize
						, condition
						, rowPageTracker
						, outputItemMonthlyWork.getTextSize()
						, lstAtdCanBeAggregate);
			else {
				MonthlyReportData dailyReportData = reportData.getMonthlyReportData();
				currentRow = writeDetailedMonthlySchedule(currentRow
						, sheetCollection
						, sheetInfo
						, dailyReportData
						, nSize, condition
						, rowPageTracker
						, outputItemMonthlyWork.getTextSize()
						, remarkColumn);
			}
			
            // Set default row height 0.45cm
            Range dataRange = sheet.getCells().createRange(startRow, 0, currentRow, remarkColumn);
            dataRange.setRowHeight(13);

			//hide footer
			hideFooter(sheet);
			// Delete remark content column if this option is disabled
			//always print remark 
			/*if (outputItemMonthlyWork.getPrintSettingRemarksColumn() == PrintSettingRemarksColumn.NOT_PRINT_REMARK) {
				Cells cells = sheet.getCells();
				cells.deleteColumns(29, 4, true);
				Range lastColumn = cells.createRange(startRow, 0, currentRow - 1 - startRow, 29);
				lastColumn.setOutlineBorder(BorderType.RIGHT_BORDER, CellBorderType.NONE, Color.getBlack());
				
				// Insert 3 columns to make the table at center area 
				// it should be 3 columns at both sides
				cells.insertColumns(0, 2);
			}*/
			
			// Delete the rest of workbook
			for (int i = 0; i < sheetCount; i++) {
				sheetCollection.removeAt(0);
			}
			
			// Set cursor
			sheet.setActiveCell("A1");
			
			// Process designer
			reportContext.processDesigner();
			
			// Get current date and format it
			DateTimeFormatter jpFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.JAPAN);
			String currentFormattedDate = LocalDateTime.now().format(jpFormatter);
			
			// Create print area
			//createPrintArea(sheet);
			
			// Save workbook
			if (query.getFileType() == FileOutputType.FILE_TYPE_EXCEL)
				reportContext.saveAsExcel(this.createNewFile(generatorContext, optOutputItemMonthlyWork.get().getItemName().v() + "_" + currentFormattedDate + ".xlsx"));
			else {
				reportContext.saveAsPdf(this.createNewFile(generatorContext, optOutputItemMonthlyWork.get().getItemName().v() + "_" + currentFormattedDate + ".pdf"));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Collect header data.
	 *
	 * @param headerData the header data
	 * @param condition the condition
	 */
	public void collectHeaderData(MonthlyPerformanceHeaderData headerData, MonthlyWorkScheduleCondition condition) {
		Optional<Company> optCompany = companyRepository.find(AppContexts.user().companyId());
		headerData.setFixedHeaderData(new ArrayList<>());
		if (optCompany.isPresent())
			headerData.companyName = optCompany.get().getCompanyName().v();
		else
			headerData.companyName = "";
		if (condition.getOutputType() == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE) {
			headerData.fixedHeaderData.add(TextResource.localize("KWR006_65"));
		} else {
			headerData.fixedHeaderData.add(TextResource.localize("KWR006_76"));
		}
		headerData.fixedHeaderData.add(TextResource.localize("KWR006_84"));
		headerData.fixedHeaderData.add(TextResource.localize("KWR006_67"));
	}
	
	/**
	 * Collect display map.
	 *
	 * @param headerData the header data
	 * @param condition the condition
	 * @param companyId the company id
	 * @param roleId the role id
	 */
	public void collectDisplayMap(MonthlyPerformanceHeaderData headerData, OutputItemMonthlyWorkSchedule condition, String companyId, String roleId) {
		List<MonthlyAttendanceItemsDisplay> lstItem = condition.getLstDisplayedAttendance();
		headerData.setLstOutputItemSettingCode(new ArrayList<>());
		
		List<Integer> lstAttendanceId = lstItem.stream().sorted((o1, o2) -> (o1.getOrderNo() - o2.getOrderNo())).map(x -> x.getAttendanceDisplay()).collect(Collectors.toList());
		List<AttItemName> lstAttendanceDto = companyMonthlyItemService.getMonthlyItems(companyId, Optional.of(roleId), lstAttendanceId, Arrays.asList(MonthlyAttendanceItemAtr.values()));
		if (lstAttendanceDto.isEmpty())
			throw new BusinessException(new RawErrorMessage("Msg_1417"));
//		List<AttdItemDto> lstMonthlyAttendanceItem = monthlyAttendanceItemFinder.findAll();
		condition.setLstDisplayedAttendance(lstItem.stream().filter(x -> lstAttendanceId.contains(x.getAttendanceDisplay())).collect(Collectors.toList()));
		boolean isBig = condition.getTextSize().equals(TextSizeCommonEnum.BIG);
		lstAttendanceId.stream().forEach(x -> {
			Optional<AttItemName> opAttendanceItem = lstAttendanceDto.stream()
					.filter(item -> item.getAttendanceItemId() == x).findFirst();
			OutputItemSetting setting = new OutputItemSetting();
			// setting.setItemCode(attendanceItem.getAttendanceItemDisplayNumber());
			if (opAttendanceItem.isPresent()) {
				setting.setItemCode(opAttendanceItem.get().getAttendanceItemId());
				setting.setItemName(this.formatAttendanceName(isBig, opAttendanceItem.get().getAttendanceItemName()));
			} else {
				setting.setItemCode(null);
				setting.setItemName("");
			}
			headerData.lstOutputItemSettingCode.add(setting);
		});
	}

	/**
	 * Collect workplace hierarchy.
	 *
	 * @param workplaceId the workplace id
	 * @param baseDate the base date
	 * @param lstWorkplaceConfigInfo the lst workplace config info
	 * @return the map
	 */
	public Map<String, WorkplaceInfo> collectWorkplaceHierarchy(String workplaceId, GeneralDate baseDate, List<WorkplaceConfigInfo> lstWorkplaceConfigInfo) {
		Map<String, WorkplaceInfo> lstWorkplace = new TreeMap<>();
		Optional<WorkplaceConfigInfo> optHierachyInfo = lstWorkplaceConfigInfo.stream().filter(x -> {
			List<String> workplaceIds = new ArrayList<>();
			if (!x.getLstWkpHierarchy().isEmpty()) {
				workplaceIds.addAll(x.getLstWkpHierarchy().stream().map(t -> t.getWorkplaceId()).collect(Collectors.toList()));
			}
			return workplaceIds.contains(workplaceId);
		}).findFirst();

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
	 * Get lowest workplace hierarchy level.
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
	 * Find workplace by hierarchy code.
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
	 * Analyze info (export by employee).
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
					isFoundParent = true;
					break;
				}
			}

			// Not found, then add to root
			if (!isFoundParent) {
				wrp.parent = parent;
				parent.lstChildWorkplaceReportData.put(k, wrp);
			}
		}
	}

	/**
	 * Find workplace (export by employee).
	 *
	 * @param employeeId the employee id
	 * @param rootWorkplace the root workplace
	 * @param baseDate the base date
	 * @param lstWorkplaceHistImport the lst workplace hist import
	 * @param lstWorkplaceConfigInfo the lst workplace config info
	 * @return the workplace report data
	 */
	public WorkplaceReportData findWorkplace(String employeeId, WorkplaceReportData rootWorkplace, GeneralDate baseDate,
			List<WkpHistImport> lstWorkplaceHistImport, List<WorkplaceConfigInfo> lstWorkplaceConfigInfo) {
		Map<String, WorkplaceReportData> mapWorkplaceInfo = rootWorkplace.getLstChildWorkplaceReportData();
		WkpHistImport workplaceImport = lstWorkplaceHistImport.stream().filter(hist -> StringUtils.equalsIgnoreCase(hist.getEmployeeId(), employeeId) ).findFirst().get();
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
		}
		else {
			for (Map.Entry<String, WorkplaceReportData> keySet : mapWorkplaceInfo.entrySet()) {
				WorkplaceReportData data = findWorkplace(employeeId, keySet.getValue(), baseDate, lstWorkplaceHistImport, lstWorkplaceConfigInfo);
				if (data != null)
					return data;
			}
		}
		return null;
	}
	
	/**
	 * Find workplace (export by date).
	 *
	 * @param employeeId the employee id
	 * @param rootWorkplace the root workplace
	 * @param baseDate the base date
	 * @param lstWkpHistImport the lst wkp hist import
	 * @param lstWorkplaceConfigInfo the lst workplace config info
	 * @return the workplace report data
	 */
	public MonthlyWorkplaceData findWorkplace(String employeeId, MonthlyWorkplaceData rootWorkplace, YearMonth baseDate, 
			List<WkpHistImport> lstWkpHistImport, List<WorkplaceConfigInfo> lstWorkplaceConfigInfo) {
		
		Map<String, MonthlyWorkplaceData> mapWorkplaceInfo = rootWorkplace.getLstChildWorkplaceData();
		WkpHistImport workplaceImport = lstWkpHistImport.stream().filter(hist -> StringUtils.equalsIgnoreCase(hist.getEmployeeId(), employeeId) ).findFirst().get();
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
		}
		else {
			for (Map.Entry<String, MonthlyWorkplaceData> keySet : mapWorkplaceInfo.entrySet()) {
				MonthlyWorkplaceData data = findWorkplace(employeeId, keySet.getValue(), baseDate, lstWkpHistImport, lstWorkplaceConfigInfo);
				if (data != null)
					return data;
			}
		}
		return null;
	}
	
	/**
	 * Collect data.
	 *
	 * @param reportData the report data
	 * @param query the query
	 * @param condition the condition
	 * @param outputItem the output item
	 * @param setter the setter
	 */
	public void collectData(MonthlyPerformanceReportData reportData
						  , MonthlyWorkScheduleQuery query
						  , MonthlyWorkScheduleCondition condition
						  , OutputItemMonthlyWorkSchedule outputItem
						  , TaskDataSetter setter
						  , List<Integer> lstAtdCanBeAggregate) {
		String companyId = AppContexts.user().companyId();
		String roleId = AppContexts.user().roles().forAttendance();
		reportData.setHeaderData(new MonthlyPerformanceHeaderData());
		collectHeaderData(reportData.getHeaderData(), condition);
		collectDisplayMap(reportData.getHeaderData(), outputItem, companyId, roleId);
		YearMonth endDate = query.getEndYearMonth();
		
		WorkScheduleMonthlyQueryData queryData = new WorkScheduleMonthlyQueryData();
		queryData.setQuery(query);
		
		List<YearMonth> lstDate = new YearMonthPeriod(query.getStartYearMonth(), endDate).yearMonthsBetween();
		queryData.setMonthPeriod(lstDate);
		
		Map<String, WorkplaceInfo> lstWorkplace = new TreeMap<>(); // Automatically sort by code, will need to check hierarchy later
		List<String> lstWorkplaceId = new ArrayList<>();
		List<String> lstEmployeeNoWorkplace = new ArrayList<>();
		// 帳票用の基準日取得
		int closureId = query.getClosureId() == 0 ? 1 : query.getClosureId();
		//マスタの取得（月次）
		DatePeriod DatePeriod = ClosureService.getClosurePeriod(
				ClosureService.createRequireM1(closureRepo, closureEmploymentRepo),
				closureId, query.getEndYearMonth());
		//締め期間．終了年月日
		GeneralDate finalDate = DatePeriod.end();
		
        List<WkpHistImport> workplaceList = new ArrayList<>();
        {
            List<WkpHistImport> workplaceListSync = Collections.synchronizedList(new ArrayList<>());
            this.parallel.forEach(query.getEmployeeId(), sid -> {
                WkpHistImport workplaceImport = workplaceAdapter.findWkpBySid(sid, finalDate);
                if(workplaceImport != null) {
                	workplaceListSync.add(workplaceImport);
                }
            });
            workplaceList = new ArrayList<>(workplaceListSync);
        }
        Map<String, WkpHistImport> workplaceAll = workplaceList.stream().collect(Collectors.toMap(WkpHistImport::getEmployeeId, x -> x));
		
		for (String employeeId: query.getEmployeeId()) {
			/*WkpHistImport workplaceImport = workplaceAdapter.findWkpBySid(employeeId, finalDate);
			if (workplaceImport == null) {
				lstEmployeeNoWorkplace.add(employeeId);
				continue;
			}
			lstWorkplaceId.add(workplaceImport.getWorkplaceId());
			queryData.getLstWorkplaceImport().add(workplaceImport);*/

			if (!workplaceAll.containsKey(employeeId)){
				lstEmployeeNoWorkplace.add(employeeId);
				continue;
			}
			WkpHistImport workplaceImport = workplaceAll.get(employeeId);
			lstWorkplaceId.add(workplaceImport.getWorkplaceId());
			queryData.getLstWorkplaceImport().add(workplaceImport);
		}
		
		if (!lstEmployeeNoWorkplace.isEmpty()) {
			List<EmployeeDto> lstEmployeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeNoWorkplace);
			int numOfChunks = (int)Math.ceil((double)lstEmployeeDto.size() / CHUNK_SIZE_ERROR);
			int start, length;
			List<EmployeeDto> lstSplitEmployeeDto;
			for(int i = 0; i < numOfChunks; i++) {
				start = i * CHUNK_SIZE_ERROR;
	            length = Math.min(lstEmployeeDto.size() - start, CHUNK_SIZE_ERROR);

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
		
		List<WorkplaceConfigInfo> lstWorkplaceConfigInfo = this.convertData(workplaceInfoRepository.findByBaseDateWkpIds2(companyId, lstWorkplaceId, finalDate));
		queryData.setLstWorkplaceConfigInfo(lstWorkplaceConfigInfo);
		
		// Collect child workplace, automatically sort into tree map
		for (String entry: lstWorkplaceId) {
			lstWorkplace.putAll(collectWorkplaceHierarchy(entry, finalDate, lstWorkplaceConfigInfo));
		}
		
		List<Integer> listAttendanceId = outputItem.getLstDisplayedAttendance().stream().map(item -> {
			return item.getAttendanceDisplay();
		}).collect(Collectors.toList());
		if (outputItem.isRemarkPrinted()) {
			listAttendanceId.add(outputItem.getRemarkInputNo().value + 1283);
		}
		
		List<MonthlyAttendanceItemValueResult> itemValues;

		Optional<GeneralDate> baseDate = service.getProcessingYM(companyId, closureId);
		if (!baseDate.isPresent()) {
			throw new BusinessException("Uchida bảo là lỗi hệ thống _ ThànhPV");
		}
		Map<String, YearMonthPeriod> employeePeriod = service.getAffiliationPeriod(query.getEmployeeId(), new YearMonthPeriod(query.getStartYearMonth(), query.getEndYearMonth()), baseDate.get());
		{
			
			List<MonthlyAttendanceItemValueResult> itemValuesSync = Collections.synchronizedList(new ArrayList<>());
			this.parallel.forEach(employeePeriod.entrySet(), subEmployee -> {
				List<MonthlyAttendanceItemValueResult> subValues = attendanceItemValueService.getMonthlyValueOf(
						subEmployee.getKey(), subEmployee.getValue(), listAttendanceId);
				itemValuesSync.addAll(subValues);
			});
			
			// synchronizedListはこの並列部分でしか必要ないので、ここで普通のArrayListに変える。
			itemValues = new ArrayList<>(itemValuesSync);
		}
		
		List<MonthlyRecordValuesExport> lstMonthlyRecordValueExport = itemValues.stream()
				.map(item -> new MonthlyRecordValuesExport(item.getYearMonth(), item.getClosureId(),
						new ClosureDate(item.getClouseDate(), item.isLastDayOfMonth()), item.getAttendanceItems(),
						item.getEmployeeId()))
				.collect(Collectors.toList());

		queryData.setLstAttendanceResultImport(lstMonthlyRecordValueExport);
		
		// Extract list employeeId from attendance result list -> List employee won't have those w/o data
		List<String> lstEmployeeWithData = lstMonthlyRecordValueExport.stream().map(attendanceData -> {
			String employeeId = attendanceData.getEmployeeId();
			return employeeId;
		}).collect(Collectors.toList());
		
		// Extract list employeeId from attendance result list -> List employee won't have those w/o data
		// From list employeeId above -> Find back their workplace hierachy code
		//Set<String> lstEmployeeWithData = new HashSet<>();
		Set<String> lstWorkplaceIdWithData = new HashSet<>();
		lstWorkplaceIdWithData = lstMonthlyRecordValueExport.stream().map(attendanceData -> {
			String employeeId = attendanceData.getEmployeeId();
			WkpHistImport workplaceImport = queryData.getLstWorkplaceImport().stream().filter(hist -> StringUtils.equalsIgnoreCase(hist.getEmployeeId(), employeeId) ).findFirst().get();
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
		
		// This employee list with data, find out all other employees who don't have data.
//		List<String> lstEmployeeIdNoData = query.getEmployeeId().stream().filter(x -> !lstEmployeeWithData.contains(x)).collect(Collectors.toList());
//		if (!lstEmployeeIdNoData.isEmpty()) {
//			List<EmployeeDto> lstEmployeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeIdNoData);
//			int numOfChunks = (int)Math.ceil((double)lstEmployeeDto.size() / CHUNK_SIZE_ERROR);
//			int start, length;
//			List<EmployeeDto> lstSplitEmployeeDto;
//			for(int i = 0; i < numOfChunks; i++) {
//				start = i * CHUNK_SIZE_ERROR;
//	            length = Math.min(lstEmployeeDto.size() - start, CHUNK_SIZE_ERROR);
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
		WorkScheduleSettingTotalOutput totalOutputSetting = condition.getTotalOutputSetting();
		TotalWorkplaceHierachy outputSetting = totalOutputSetting.getWorkplaceHierarchyTotal();
		int highestOutputLevel = outputSetting.getHighestLevelEnabled();
		if ((lowestEmployeeLevel < highestOutputLevel && !totalOutputSetting.isDetails()
				&& !totalOutputSetting.isGrossTotal() && !totalOutputSetting.isPersonalTotal() && !totalOutputSetting.isTotalNumberDay()
				&& !totalOutputSetting.isWorkplaceTotal()) || lstMonthlyRecordValueExport.isEmpty()) {
			throw new BusinessException(new RawErrorMessage("Msg_37"));
		}
		
		// Sort attendance display
		List<MonthlyAttendanceItemsDisplay> lstAttendanceItemsDisplay = outputItem.getLstDisplayedAttendance().stream().sorted((o1,o2) -> o1.getOrderNo() - o2.getOrderNo()).collect(Collectors.toList());
		queryData.setLstDisplayItem(lstAttendanceItemsDisplay);
		
		queryData.setLstWorkPlace(dataProcessor.getWorkPlace(companyId, GeneralDate.ymd(endDate.year(), endDate.month(), endDate.lastDateInMonth())).getCodeNames());
		
		queryData.setLstClassification(dataProcessor.getClassification(companyId).getCodeNames());
		
		queryData.setLstPossition(dataProcessor.getPossition(companyId, GeneralDate.ymd(endDate.year(), endDate.month(), endDate.lastDateInMonth())).getCodeNames());
		
		queryData.setLstEmployment(dataProcessor.getEmployment(companyId).getCodeNames());
		
		queryData.setLstBussinessType(dataProcessor.getBussinessType(companyId).getCodeNames());
		
		if (condition.getOutputType() == MonthlyWorkScheduleCondition.EXPORT_BY_EMPLOYEE) {
			WorkplaceReportData data = new WorkplaceReportData();
			data.workplaceCode = "";
			reportData.setWorkplaceReportData(data);
			
			analyzeInfoExportByEmployee(lstWorkplace, data);
		
			List<EmployeeDto> lstEmloyeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeWithData);
			
			lstEmloyeeDto = lstEmloyeeDto.stream().sorted((a,b)-> (a.getEmployeeCode().compareTo(b.getEmployeeCode()))).collect(Collectors.toList());
			for (EmployeeDto dto: lstEmloyeeDto) {
				collectEmployeePerformanceDataByEmployee(reportData, queryData, dto, finalDate);
			}
			
			calculateTotalExportByEmployee(data, lstAttendanceItemsDisplay, lstAtdCanBeAggregate);
		
		}
		else {
			MonthlyReportData monthlyReportData = new MonthlyReportData();
			reportData.setMonthlyReportData(monthlyReportData);
			YearMonthPeriod period = new YearMonthPeriod(query.getStartYearMonth(), query.getEndYearMonth());
			List<WorkplaceMonthlyReportData> lstReportData = period.yearMonthsBetween().stream().map(x -> {
				WorkplaceMonthlyReportData dailyWorkplaceReportData = new WorkplaceMonthlyReportData();
				dailyWorkplaceReportData.lstWorkplaceData = new MonthlyWorkplaceData();
				dailyWorkplaceReportData.lstWorkplaceData.workplaceCode = "";
				dailyWorkplaceReportData.yearMonth = x;
				return dailyWorkplaceReportData;
			}).collect(Collectors.toList());
			monthlyReportData.setLstMonthlyReportData(lstReportData);
			
			final Map<String, WorkplaceInfo> lstWorkplaceTemp = lstWorkplace;
			List<String> lstAddedCode = new ArrayList<>();
			lstReportData.forEach(x -> {
				analyzeInfoExportByDate(lstWorkplaceTemp, x.getLstWorkplaceData(), lstAddedCode);
			});
			
			collectEmployeePerformanceDataByDate(reportData, queryData, lstAtdCanBeAggregate);
			// Calculate workplace total
			lstReportData.forEach(dailyData -> {
				calculateTotalExportByDate(dailyData.getLstWorkplaceData(), lstAtdCanBeAggregate);
			});
			
			// Calculate gross total
			calculateGrossTotalByDate(monthlyReportData, lstAtdCanBeAggregate);
		}
	}

	/**
	 * Collect employee performance data by employee.
	 *
	 * @param reportData the report data
	 * @param queryData the query data
	 * @param employeeDto the employee dto
	 * @return the employee report data
	 */
	private EmployeeReportData collectEmployeePerformanceDataByEmployee(MonthlyPerformanceReportData reportData
																	  , WorkScheduleMonthlyQueryData queryData
																	  , EmployeeDto employeeDto
																	  , GeneralDate baseDate) {
		String companyId = AppContexts.user().companyId();
		MonthlyWorkScheduleQuery query = queryData.getQuery();
		MonthlyWorkScheduleCondition condition = query.getCondition();
		
		// Always has item because this has passed error check
		OutputItemMonthlyWorkSchedule outSche = outputItemRepo.findBySelectionAndCidAndSidAndCode(
				ItemSelectionEnum.valueOf(condition.getItemSettingType())
				, companyId
				, query.getCode()
				, AppContexts.user().employeeId()).get();
		
		// Get all data from query data container
		List<MonthlyRecordValuesExport> lstAttendanceResultImport = queryData.getLstAttendanceResultImport();
		List<MonthlyAttendanceItemsDisplay> lstDisplayItem = queryData.getLstDisplayItem();
		List<WkpHistImport> lstWorkplaceImport = queryData.getLstWorkplaceImport();
		List<WorkplaceConfigInfo> lstWorkplaceConfigInfo = queryData.getLstWorkplaceConfigInfo();
		String employeeId = employeeDto.getEmployeeId();
		List<CodeName> lstWorkPlace = queryData.getLstWorkPlace(); 		
		List<CodeName> lstClassification = queryData.getLstClassification();		
		List<CodeName> lstPossition = queryData.getLstPossition();		
		List<CodeName> lstEmployment = queryData.getLstEmployment();		
		List<CodeName> lstBussinessType = queryData.getLstBussinessType();
		boolean isDisplayCode = query.getCondition().getItemDisplaySwitch() == ItemDisplaySwitchEnum.CODE.indicator;
		
		WorkplaceReportData workplaceData = findWorkplace(employeeId, reportData.getWorkplaceReportData(), baseDate, lstWorkplaceImport, lstWorkplaceConfigInfo);
		EmployeeReportData employeeData = new EmployeeReportData();
		
		// Employee code and name
		employeeData.employeeName = employeeDto.getEmployeeName();
		employeeData.employeeCode = employeeDto.getEmployeeCode();
		
		// Employment
		Optional<EmploymentHistoryImported> optEmploymentImport = employmentAdapter.getEmpHistBySid(companyId, employeeId, baseDate);
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
		Optional<EmployeeJobHistExport> optJobTitle = jobTitleAdapter.findBySid(employeeId, baseDate);
		if (optJobTitle.isPresent()) {
			employeeData.position = optJobTitle.get().getJobTitleName();
			employeeData.jobTitleCode = optJobTitle.get().getJobCode();
		} else {
			employeeData.position = "";
			employeeData.jobTitleCode = "";
		}
		
		employeeData.lstDetailedMonthlyPerformance = new ArrayList<>();
		if (workplaceData != null) {
			workplaceData.lstEmployeeReportData.add(employeeData);
		}
		lstAttendanceResultImport.stream().filter(x -> x.getEmployeeId().equals(employeeId)).sorted((o1,o2) -> o1.getYearMonth().compareTo(o2.getYearMonth())).forEach(x -> {
			YearMonth workingDate = x.getYearMonth();
			
			DetailedMonthlyPerformanceReportData detailedDate = new DetailedMonthlyPerformanceReportData();
			detailedDate.setYearMonth(workingDate);
			employeeData.lstDetailedMonthlyPerformance.add(detailedDate);
			
			// Closure date
			ClosureDate closureDate = x.getClosureDate();
			if (closureDate.getLastDayOfMonth()) {
				detailedDate.closureDate = WorkScheOutputConstants.CLOSURE_DATE_LAST_DAY;
			}
			else {
				detailedDate.closureDate = String.format(WorkScheOutputConstants.CLOSURE_DATE_NON_LAST_DAY, closureDate.getClosureDay().v());
			}
			
			
			// Remark content, it's full detail remark but will be processed on printing
			detailedDate.errorDetail = "";
			if (outSche.isRemarkPrinted()) {
				Optional<ItemValue> optRemarkRecord = x.getItemValues().stream().filter(att -> att.getItemId() == outSche.getRemarkInputNo().value + 1283).findFirst();
				optRemarkRecord.ifPresent(remark -> {
					detailedDate.errorDetail += (remark.value() == null? "" : remark.value());
				});
			}
			
			detailedDate.actualValue = new ArrayList<>();
			lstDisplayItem.stream().forEach(item -> {
				Optional<ItemValue> optItemValue = x.getItemValues().stream().filter(att -> att.getItemId() == item.getAttendanceDisplay()).findFirst();
				if (optItemValue.isPresent()) {
					ItemValue itemValue = optItemValue.get();
					ActualValue value = new ActualValue();
					String codeName = "";
//					勤怠項目ID＝192、197：アルゴリズム「雇用を取得する」
					if(itemValue.getItemId() == 192 || itemValue.getItemId() == 197 ) {
						Optional<CodeName> object = lstEmployment.stream().filter(c -> c.getCode().equals(itemValue.getValue())).findFirst();
						if(object.isPresent()) {
							codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
						}
						value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
					}
//					勤怠項目ID＝193、198：アルゴリズム「職位を取得する」
					else if(itemValue.getItemId() == 193 || itemValue.getItemId() == 198 ) {
						Optional<CodeName> object = lstPossition.stream().filter(c -> c.getId().equals(itemValue.getValue())).findFirst();
						if(object.isPresent()) {
							codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
						}
						value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
					}
//					勤怠項目ID＝194、199：アルゴリズム「職場を取得する」
					else if(itemValue.getItemId() == 194 || itemValue.getItemId() == 199 ) {
						Optional<CodeName> object = lstWorkPlace.stream().filter(c -> c.getId().equals(itemValue.getValue())).findFirst();
						if(object.isPresent()) {
							codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
						}
						value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
					}
//					勤怠項目ID＝195、200：アルゴリズム「分類を取得する」
					else if(itemValue.getItemId() == 195 || itemValue.getItemId() == 200 ) {
						Optional<CodeName> object = lstClassification.stream().filter(c -> c.getCode().equals(itemValue.getValue())).findFirst();
						if(object.isPresent()) {
							codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
						}
						value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
					}
//					勤怠項目ID＝196、201：アルゴリズム「勤務種別を取得する」
					else if(itemValue.getItemId() == 196 || itemValue.getItemId() == 201 ) {
						Optional<CodeName> object = lstBussinessType.stream().filter(c -> c.getCode().equals(itemValue.getValue())).findFirst();
						if(object.isPresent()) {
							codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
						}
						value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
					}else {
						value = new ActualValue(itemValue.getItemId(), itemValue.getValue(), itemValue.getValueType().value);
					}
					detailedDate.actualValue.add(value);
				}
				else {
					detailedDate.actualValue.add(new ActualValue(item.getAttendanceDisplay(), "", ActualValue.STRING));
				}
				// Enable data presentation
				if (workplaceData != null) {
					workplaceData.setHasData(true);
				}
			});
		});
		return employeeData;
	}
	

	/**
	 * Collect employee performance data by date.
	 *
	 * @param reportData the report data
	 * @param queryData the query data
	 */
	private void collectEmployeePerformanceDataByDate(MonthlyPerformanceReportData reportData
													, WorkScheduleMonthlyQueryData queryData
													, List<Integer> lstAtdCanBeAggregate) {
		MonthlyWorkScheduleQuery query = queryData.getQuery();
		YearMonth endDate = query.getEndYearMonth();
		// Always has item because this has passed error check
		OutputItemMonthlyWorkSchedule outSche = outputItemRepo.findBySelectionAndCidAndSidAndCode(
				ItemSelectionEnum.valueOf(query.getCondition().getItemSettingType())
				, AppContexts.user().companyId()
				, query.getCode()
				, AppContexts.user().employeeId()).get();
		
		// Get all data from query data container
		List<YearMonth> datePeriod = queryData.getMonthPeriod();
		List<MonthlyRecordValuesExport> lstAttendanceResultImport = queryData.getLstAttendanceResultImport();
		List<MonthlyAttendanceItemsDisplay> lstDisplayItem = queryData.getLstDisplayItem();
		List<WkpHistImport> lstWorkplaceHistImport = queryData.getLstWorkplaceImport();		
		List<CodeName> lstWorkPlace = queryData.getLstWorkPlace(); 		
		List<CodeName> lstClassification = queryData.getLstClassification();		
		List<CodeName> lstPossition = queryData.getLstPossition();		
		List<CodeName> lstEmployment = queryData.getLstEmployment();		
		List<CodeName> lstBussinessType = queryData.getLstBussinessType();
		
		List<String> lstEmployeeId = query.getEmployeeId();
		List<EmployeeDto> lstEmployeeDto = employeeAdapter.findByEmployeeIds(lstEmployeeId)
				.stream().sorted(Comparator.comparing(EmployeeDto::getEmployeeCode))
				.collect(Collectors.toList());
		boolean isDisplayCode = query.getCondition().getItemDisplaySwitch() == ItemDisplaySwitchEnum.CODE.indicator;
		
		for (String employeeId: lstEmployeeId) {
			Optional<EmployeeDto> optEmployeeDto = lstEmployeeDto.stream().filter(employee -> StringUtils.equalsAnyIgnoreCase(employee.getEmployeeId(),employeeId)).findFirst();
			
			datePeriod.stream().forEach(date -> {
				Optional<WorkplaceMonthlyReportData> optDailyWorkplaceData = reportData.getMonthlyReportData().getLstMonthlyReportData().stream().filter(x -> x.getYearMonth().compareTo(date) == 0).findFirst();
				MonthlyWorkplaceData dailyWorkplaceData = findWorkplace(employeeId,optDailyWorkplaceData.get().getLstWorkplaceData(), endDate, lstWorkplaceHistImport, queryData.getLstWorkplaceConfigInfo());
				if (dailyWorkplaceData != null) {
					
					
					lstAttendanceResultImport.stream().filter(x -> (x.getEmployeeId().equals(employeeId) && x.getYearMonth().compareTo(date) == 0) && 
							StringUtils.equalsAnyIgnoreCase(x.getEmployeeId(), employeeId)).forEach(x -> {
						MonthlyPersonalPerformanceData personalPerformanceDate = new MonthlyPersonalPerformanceData();
								if (optEmployeeDto.isPresent()) {
									personalPerformanceDate.setEmployeeName(optEmployeeDto.get().getEmployeeName());
									personalPerformanceDate.setEmployeeCode(optEmployeeDto.get().getEmployeeCode());
								}
						dailyWorkplaceData.getLstDailyPersonalData().add(personalPerformanceDate);
						
						// Closure date
						ClosureDate closureDate = x.getClosureDate();
						if (closureDate.getLastDayOfMonth()) {
							personalPerformanceDate.closureDate = WorkScheOutputConstants.CLOSURE_DATE_LAST_DAY;
						}
						else {
							personalPerformanceDate.closureDate = String.format(WorkScheOutputConstants.CLOSURE_DATE_NON_LAST_DAY, closureDate.getClosureDay().v());
						}
						
						// Remark content, it's full detail remark but will be processed on printing
						personalPerformanceDate.detailedErrorData = "";
						if (outSche.isRemarkPrinted()) {
							Optional<ItemValue> optRemarkRecord = x.getItemValues().stream().filter(att -> att.getItemId() == outSche.getRemarkInputNo().value + 1283).findFirst();
							optRemarkRecord.ifPresent(remark -> {
								personalPerformanceDate.detailedErrorData += (remark.value() == null? "" : remark.value());
							});
						}
						
						personalPerformanceDate.actualValue = new ArrayList<>();
						lstDisplayItem.stream().forEach(item -> {
							Optional<ItemValue> optItemValue = x.getItemValues().stream().filter(att -> att.getItemId() == item.getAttendanceDisplay()).findFirst();
							if (optItemValue.isPresent()) {
								ItemValue itemValue = optItemValue.get();
								ActualValue value = new ActualValue();
								String codeName = "";
//								勤怠項目ID＝192、197：アルゴリズム「雇用を取得する」
								if(itemValue.getItemId() == 192 || itemValue.getItemId() == 197 ) {
									Optional<CodeName> object = lstEmployment.stream().filter(c -> c.getCode().equals(itemValue.getValue())).findFirst();
									if(object.isPresent()) {
										codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
									}
									value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
								}
//								勤怠項目ID＝193、198：アルゴリズム「職位を取得する」
								else if(itemValue.getItemId() == 193 || itemValue.getItemId() == 198 ) {
									Optional<CodeName> object = lstPossition.stream().filter(c -> c.getId().equals(itemValue.getValue())).findFirst();
									if(object.isPresent()) {
										codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
									}
									value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
								}
//								勤怠項目ID＝194、199：アルゴリズム「職場を取得する」
								else if(itemValue.getItemId() == 194 || itemValue.getItemId() == 199 ) {
									Optional<CodeName> object = lstWorkPlace.stream().filter(c -> c.getId().equals(itemValue.getValue())).findFirst();
									if(object.isPresent()) {
										codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
									}
									value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
								}
//								勤怠項目ID＝195、200：アルゴリズム「分類を取得する」
								else if(itemValue.getItemId() == 195 || itemValue.getItemId() == 200 ) {
									Optional<CodeName> object = lstClassification.stream().filter(c -> c.getCode().equals(itemValue.getValue())).findFirst();
									if(object.isPresent()) {
										codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
									}
									value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
								}
//								勤怠項目ID＝196、201：アルゴリズム「勤務種別を取得する」
								else if(itemValue.getItemId() == 196 || itemValue.getItemId() == 201 ) {
									Optional<CodeName> object = lstBussinessType.stream().filter(c -> c.getCode().equals(itemValue.getValue())).findFirst();
									if(object.isPresent()) {
										codeName = isDisplayCode ? object.get().getCode() : object.get().getName();
									}
									value = new ActualValue(itemValue.getItemId(), codeName, ValueType.TEXT.value);
								}else {
									value = new ActualValue(itemValue.getItemId(), itemValue.getValue(), itemValue.getValueType().value);
								}
								personalPerformanceDate.actualValue.add(value);
							}
							else {
								personalPerformanceDate.actualValue.add(new ActualValue(item.getAttendanceDisplay(), "", ActualValue.STRING));
							}
							dailyWorkplaceData.hasData = true;
						});
					});
				}
			});
		}
	}

	/**
	 * Find workplace by hierarchy code export date.
	 *
	 * @param rootWorkplace the root workplace
	 * @param hierarchyCode the hierarchy code
	 * @return the monthly workplace data
	 */
	private MonthlyWorkplaceData findWorkplaceByHierarchyCodeExportDate(MonthlyWorkplaceData rootWorkplace, String hierarchyCode) {
		MonthlyWorkplaceData workplaceReportData = null;
		
		for (Map.Entry<String, MonthlyWorkplaceData> entry : rootWorkplace.lstChildWorkplaceData.entrySet()) {
			if (workplaceReportData != null) {
				break;
			}
			String key = entry.getKey();
			MonthlyWorkplaceData childWorkplace = entry.getValue();
			if (StringUtils.equalsIgnoreCase(key, hierarchyCode)) {
				workplaceReportData = childWorkplace;
				break;
			}
			workplaceReportData = findWorkplaceByHierarchyCodeExportDate(childWorkplace, hierarchyCode);
		}
		
		return workplaceReportData;
	}

	/**
	 * Analyze info (export by date).
	 *
	 * @param lstWorkplace the lst workplace
	 * @param parent the parent
	 * @param lstAddedWorkplace the lst added workplace
	 */
	private void analyzeInfoExportByDate(Map<String, WorkplaceInfo> lstWorkplace, MonthlyWorkplaceData parent, List<String> lstAddedWorkplace) {
		parent.lstChildWorkplaceData = new TreeMap<>();
		
		boolean isFoundParent;
		// Add workplace into respective parent
		for (Map.Entry<String, WorkplaceInfo> entry : lstWorkplace.entrySet()) {
			String k = entry.getKey();
			WorkplaceInfo wpi = entry.getValue();
			
			isFoundParent = false;
			
			// Init an workplace report data
			MonthlyWorkplaceData wrp = new MonthlyWorkplaceData();
			wrp.workplaceCode = wpi.getWorkplaceCode().v();
			wrp.workplaceName = wpi.getWorkplaceName().v();
			wrp.level = k.length() / 3;
			
			// Break down code
			String breakDownCode;
			for (int level = k.length(); level >= 3; level -= 3) {
				breakDownCode = k.substring(0, level);
				
				// Find workplace based on breakdown code
				MonthlyWorkplaceData targetWorkplace = findWorkplaceByHierarchyCodeExportDate(parent, breakDownCode);
				
				// Find parent workplace, add into its child
				if (targetWorkplace != null) {
					wrp.parent = targetWorkplace;
					targetWorkplace.lstChildWorkplaceData.put(k, wrp);
					isFoundParent = true;
					break;
				}
			}

			// Not found, then add to root
			if (!isFoundParent) {
				wrp.parent = parent;
				parent.lstChildWorkplaceData.put(k, wrp);
			}
		}
	}

	/**
	 * Calculate total (export by employee).
	 *
	 * @param workplaceData the workplace data
	 * @param lstAttendanceId the lst attendance id
	 */
	public void calculateTotalExportByEmployee(WorkplaceReportData workplaceData
											 , List<MonthlyAttendanceItemsDisplay> lstAttendanceId
											 , List<Integer> lstAtdCanAggregate) {
		// Recursive
		workplaceData.getLstChildWorkplaceReportData().values().forEach(x -> {
			calculateTotalExportByEmployee(x, lstAttendanceId, lstAtdCanAggregate);
			workplaceData.setHasData(workplaceData.isHasData() || x.isHasData());
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
			totalVal.setValue(lstAtdCanAggregate.contains(item.getAttendanceDisplay()) ? "0" : "");
			totalVal.setValueType(TotalValue.STRING);
			return totalVal;
		}).collect(Collectors.toList());
		workplaceData.setGrossTotal(lstWorkplaceGrossTotal);
		workplaceData.getLstChildWorkplaceReportData().values().forEach(child -> {
			child.getGrossTotal().stream().forEach(val -> {
				if (lstAtdCanAggregate.contains(val.getAttendanceId())) {
					if (val.value() == null) return;
					int attendanceId = val.getAttendanceId();
					TotalValue totalVal = lstWorkplaceGrossTotal.stream().filter(attendance -> attendance.getAttendanceId() == attendanceId).findFirst().get();
					ValueType valueTypeEnum = EnumAdaptor.valueOf(val.getValueType(), ValueType.class);
					if (valueTypeEnum.isIntegerCountable()) {
						int currentValue = (int) val.value();
						totalVal.setValue(String.valueOf(Integer.parseInt(totalVal.getValue()) + currentValue));
						totalVal.setValueType(val.getValueType());
					}
					if (valueTypeEnum.isDoubleCountable()) {
						double currentValueDouble = (double) val.value();
						totalVal.setValue(String.valueOf(Double.parseDouble(totalVal.getValue()) + currentValueDouble));
						totalVal.setValueType(val.getValueType());
					}
					if (valueTypeEnum == ValueType.TEXT) {
						totalVal.setValue("");
						totalVal.setValueType(val.getValueType());
					}
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
						employeeData.mapPersonalTotal.put(attendanceDisplay, new TotalValue(attendanceDisplay, "0", TotalValue.STRING, ""));
						TotalValue totalVal = new TotalValue();
						totalVal.setAttendanceId(attendanceDisplay);
						totalVal.setValue(lstAtdCanAggregate.contains(attendanceId.getAttendanceDisplay()) ? "0" : "");
						totalVal.setValueType(TotalValue.STRING);
						Optional<TotalValue> optWorkplaceTotalValue = lstTotalValue.stream().filter(x -> x.getAttendanceId() == attendanceDisplay).findFirst();
						if (!optWorkplaceTotalValue.isPresent()) {
							lstTotalValue.add(totalVal);
						}
					}
				});
				
				// Add into total by attendanceId
				employeeData.getLstDetailedMonthlyPerformance().stream().forEach(detail -> {
					detail.actualValue.stream().forEach((aVal) -> {
						if (lstAtdCanAggregate.contains(aVal.getAttendanceId())) {
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
							
							if (valueTypeEnum.isIntegerCountable()) {
								int currentValue = (int) aVal.value();
								personalTotal.setValue(String.valueOf(Integer.parseInt(personalTotal.getValue()) + currentValue));
								totalVal.setValue(String.valueOf(Integer.parseInt(totalVal.getValue()) + currentValue));
								totalGrossVal.setValue(String.valueOf(Integer.parseInt(totalGrossVal.getValue()) + currentValue));
								employeeData.mapPersonalTotal.put(attdId, personalTotal);
							}
							if (valueTypeEnum.isDoubleCountable()) {
								double currentValueDouble = (double) aVal.value();
								personalTotal.setValue(String.valueOf(Double.parseDouble(personalTotal.getValue()) + currentValueDouble));
								totalVal.setValue(String.valueOf(Double.parseDouble(totalVal.getValue()) + currentValueDouble));
								totalGrossVal.setValue(String.valueOf(Double.parseDouble(totalGrossVal.getValue()) + currentValueDouble));
								employeeData.mapPersonalTotal.put(attdId, personalTotal);
							}
						} else {
							employeeData.mapPersonalTotal.put(aVal.getAttendanceId(), new TotalValue(aVal.getAttendanceId(), "", TotalValue.STRING, ""));
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
					if (lstAtdCanAggregate.contains(item.getAttendanceId())) {
						if (item.value() == null) return;
						Optional<TotalValue> optTotalVal = lstTotalVal.stream().filter(x -> x.getAttendanceId() == item.getAttendanceId()).findFirst();
						if (optTotalVal.isPresent()) {
							TotalValue totalVal = optTotalVal.get();
							int valueType = item.getValueType();
							totalVal.setValueType(valueType);
							ValueType valueTypeEnum = EnumAdaptor.valueOf(valueType, ValueType.class);
							if (valueTypeEnum.isIntegerCountable()) {
								totalVal.setValue(String.valueOf((int) totalVal.value() + Integer.parseInt(item.getValue())));
							}
							if (valueTypeEnum.isDoubleCountable()) {
								totalVal.setValue(String.valueOf((double) totalVal.value() + Double.parseDouble(item.getValue())));
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
					} else {
						lstTotalVal.add(new TotalValue(item.getAttendanceId(), "", item.getValueType(), ""));
					}
				});
			});
		}
	}
	
	/**
	 * Calculate total (export by date).
	 *
	 * @param workplaceData the workplace data
	 */
	public void calculateTotalExportByDate(MonthlyWorkplaceData workplaceData, List<Integer> lstAtdCanAggregate) {
		// Recursive
		workplaceData.getLstChildWorkplaceData().values().forEach(x -> {
			calculateTotalExportByDate(x, lstAtdCanAggregate);
			workplaceData.setHasData(workplaceData.isHasData() || x.isHasData());
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
		
		List<MonthlyPersonalPerformanceData> performanceData = workplaceData.getLstDailyPersonalData();
		
		// Employee
		if (performanceData != null && performanceData.size() > 0) {
			for (MonthlyPersonalPerformanceData data: performanceData) {
				List<ActualValue> lstActualValue = data.getActualValue();
				if (lstActualValue == null) continue;
				lstActualValue.forEach(actualValue -> {
					Optional<TotalValue> optTotalVal = lstTotalValue.stream().filter(x -> x.getAttendanceId() == actualValue.getAttendanceId()).findFirst();						
					Optional<TotalValue> optTotalWorkplaceVal = lstTotalHierarchyValue.stream().filter(x -> x.getAttendanceId() == actualValue.getAttendanceId()).findFirst();
					if (lstAtdCanAggregate.contains(actualValue.getAttendanceId())) {
						int valueType = actualValue.getValueType();
						TotalValue totalValue;
						if (optTotalVal.isPresent()) {
							if (actualValue.value() == null) return;
							totalValue = optTotalVal.get();
							int totalValueType = totalValue.getValueType();
							ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
							if (valueTypeEnum.isIntegerCountable()) {
								totalValue.setValue(String.valueOf((int) totalValue.value() + (int) actualValue.value()));
							}
							if (valueTypeEnum.isDoubleCountable()) {
								totalValue.setValue(String.valueOf((double) totalValue.value() + (double) actualValue.value()));
							}
						} else {
							totalValue = new TotalValue();
							totalValue.setAttendanceId(actualValue.getAttendanceId());
							ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
							if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
								if (actualValue.getValue() != null) {
									totalValue.setValue(actualValue.getValue());
								} else {
									totalValue.setValue("0");
								}
							} else { // This case also deals with null value
								totalValue.setValue(actualValue.getValue());
							}
							totalValue.setValueType(valueType);
							totalValue.setUnit(actualValue.getUnit());
							lstTotalValue.add(totalValue);
						}

						TotalValue totalWorkplaceValue;
						if (optTotalWorkplaceVal.isPresent()) {
							totalWorkplaceValue = optTotalWorkplaceVal.get();
							int totalValueType = totalValue.getValueType();
							ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
							if (valueTypeEnum.isIntegerCountable()) {
								totalWorkplaceValue.setValue(String.valueOf((int) totalWorkplaceValue.value() + (int) actualValue.value()));
							}
							if (valueTypeEnum.isDoubleCountable()) {
								totalWorkplaceValue.setValue(String.valueOf((double) totalWorkplaceValue.value() + (double) actualValue.value()));
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
							} else { // This case also deals with null value
								totalWorkplaceValue.setValue(actualValue.getValue());
							}
							totalWorkplaceValue.setValueType(valueType);
							totalWorkplaceValue.setUnit(actualValue.getUnit());
							lstTotalHierarchyValue.add(totalWorkplaceValue);
						}
					} else {
						if (!optTotalVal.isPresent()) {
							lstTotalValue.add(new TotalValue(actualValue.getAttendanceId(), "", TotalValue.STRING, ""));
						}
						if (!optTotalWorkplaceVal.isPresent()) {
							lstTotalHierarchyValue.add(new TotalValue(actualValue.getAttendanceId(), "", TotalValue.STRING, ""));
						}
					}
				});
			}
		}
		
		// Workplace
		Map<String, MonthlyWorkplaceData> lstReportData = workplaceData.getLstChildWorkplaceData();
		lstReportData.values().forEach((value) -> {
			List<TotalValue> lstActualValue = value.getWorkplaceTotal().getTotalWorkplaceValue();
			lstActualValue.forEach(actualValue -> {
				Optional<TotalValue> optTotalVal = lstTotalValue.stream().filter(x -> x.getAttendanceId() == actualValue.getAttendanceId()).findFirst();
				if (lstAtdCanAggregate.contains(actualValue.getAttendanceId())) {
					int valueType = actualValue.getValueType();
					TotalValue totalValue;
					if (optTotalVal.isPresent()) {
						if (actualValue.value() == null) return;
						totalValue = optTotalVal.get();
						int totalValueType = totalValue.getValueType();
						ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
						if (valueTypeEnum.isIntegerCountable()) {
							totalValue.setValue(String.valueOf((int) totalValue.value() + (int) actualValue.value()));
						}
						if (valueTypeEnum.isDoubleCountable()) {
							totalValue.setValue(String.valueOf((double) totalValue.value() + (double) actualValue.value()));
						}
						totalValue.setUnit(actualValue.getUnit());
					} else {
						totalValue = new TotalValue();
						totalValue.setAttendanceId(actualValue.getAttendanceId());
						ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							if (actualValue.getValue() != null) {
								totalValue.setValue(actualValue.getValue());
							} else {
								totalValue.setValue("0");
							}
						} else { // This case also deals with null value
							totalValue.setValue(actualValue.getValue());
						}
						totalValue.setValueType(valueType);
						totalValue.setUnit(actualValue.getUnit());
						lstTotalValue.add(totalValue);
					}
				} else {
					if (!optTotalVal.isPresent()) {
						lstTotalValue.add(new TotalValue(actualValue.getAttendanceId(), "", TotalValue.STRING, ""));
					}
				}
			});
		});
	}
	
	/**
	 * Calculate gross total by date.
	 *
	 * @param dailyReportData the daily report data
	 */
	public void calculateGrossTotalByDate(MonthlyReportData dailyReportData, List<Integer> lstAtdCanAggregate) {
		List<WorkplaceMonthlyReportData> lstWorkplaceDailyReportData = dailyReportData.getLstMonthlyReportData();
		List<TotalValue> lstGrossTotal = new ArrayList<>();
		dailyReportData.setListTotalValue(lstGrossTotal);
		
		lstWorkplaceDailyReportData.stream().forEach(workplaceDaily -> {
			WorkplaceTotal workplaceTotal = workplaceDaily.getLstWorkplaceData().getWorkplaceTotal();
			List<TotalValue> lstTotalVal = workplaceTotal.getTotalWorkplaceValue();
			lstTotalVal.stream().forEach(totalVal -> {
				// Literally 0-1 result in list
				Optional<TotalValue> optGrossTotal = lstGrossTotal.stream().filter(x -> x.getAttendanceId() == totalVal.getAttendanceId()).findFirst();
				if (lstAtdCanAggregate.contains(totalVal.getAttendanceId())) {
					TotalValue totalValue;
					if (optGrossTotal.isPresent()) {
						if (totalVal.value() == null) return;
						totalValue = optGrossTotal.get();
						int totalValueType = totalValue.getValueType();
						ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValueType, ValueType.class);
						if (valueTypeEnum.isIntegerCountable()) {
							totalValue.setValue(String.valueOf((int) totalValue.value() + (int) totalVal.value()));
						}
						if (valueTypeEnum.isDoubleCountable()) {
							totalValue.setValue(String.valueOf((double) totalValue.value() + (double) totalVal.value()));
						}
						totalValue.setUnit(totalVal.getUnit());
					} else {
						totalValue = new TotalValue();
						totalValue.setAttendanceId(totalVal.getAttendanceId());
						ValueType valueTypeEnum = EnumAdaptor.valueOf(totalVal.getValueType(), ValueType.class);
						if (valueTypeEnum.isIntegerCountable() || valueTypeEnum.isDoubleCountable()) {
							totalValue.setValue(totalVal.getValue());
						}
						totalValue.setValueType(totalVal.getValueType());
						totalValue.setUnit(totalVal.getUnit());
						lstGrossTotal.add(totalValue);
					}
				} else {
					if (!optGrossTotal.isPresent()) {
						lstGrossTotal.add(new TotalValue(totalVal.getAttendanceId(), "", TotalValue.STRING, ""));
					}
				}
			});
		});
	}

	/**
	 * Write detail work schedule.
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
	public int writeDetailedWorkSchedule(MonthlyWorkScheduleQuery query
									   , int currentRow
									   , WorksheetCollection templateSheetCollection
									   , WorkSheetInfo sheetInfo
									   , WorkplaceReportData workplaceReportData
									   , int dataRowCount
									   , MonthlyWorkScheduleCondition condition
									   , RowPageTracker rowPageTracker
									   , TextSizeCommonEnum textSizeCommonEnum
									   , List<Integer> lstAtdCanBeAggregate) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		
		WorkScheduleSettingTotalOutput totalOutput = condition.getTotalOutputSetting();
		List<EmployeeReportData> lstEmployeeReportData = workplaceReportData.getLstEmployeeReportData();
		int remarkColumn = textSizeCommonEnum == TextSizeCommonEnum.BIG ? DATA_COLUMN_INDEX[5] : REMARK_COLUMN_SMALL_SIZE;
		int chunkSize = textSizeCommonEnum == TextSizeCommonEnum.BIG
				? MonthlyReportConstant.CHUNK_BIG_SIZE
				: MonthlyReportConstant.CHUNK_SMALL_SIZE;
		
		// Root workplace won't have employee
		if (lstEmployeeReportData != null && lstEmployeeReportData.size() > 0) {
			Iterator<EmployeeReportData> iteratorEmployee = lstEmployeeReportData.iterator();
			int firstWpl = 0;
			while (iteratorEmployee.hasNext()) {
				EmployeeReportData employeeReportData = iteratorEmployee.next();
				
				// Calculate used row for workplace, employee, 1st record
				int usedRow = 0;
				//count month for get data
//				int countPeriodMonth = 12*(query.getEndYearMonth().year() - query.getStartYearMonth().year()) + query.getEndYearMonth().month() - query.getStartYearMonth().month() + 1;
				if (condition.isShowWorkplace()) usedRow++;
				if (condition.isShowPersonal()) usedRow++;
//				if (totalOutput.isPersonalTotal())  usedRow++;
//				if (totalOutput.isDetails() && !employeeReportData.getLstDetailedMonthlyPerformance().isEmpty()) usedRow += countPeriodMonth*dataRowCount;
				if (totalOutput.isDetails() && !employeeReportData.getLstDetailedMonthlyPerformance().isEmpty() && firstWpl > 0) {
					usedRow += employeeReportData.countItem(chunkSize);
				}
				if (rowPageTracker.checkRemainingRowSufficient(usedRow) <= 0) {
					sheetInfo.getSheet().getHorizontalPageBreaks().add(currentRow);
					rowPageTracker.resetRemainingRow();
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
					rowPageTracker.resetRemainingRow();
				}
				
				if ((condition.isShowWorkplace() && firstWpl == 0) || (condition.isShowWorkplace() && condition.isShowPersonal())) {
					currentRow = this.printWorkplace(cells, currentRow, templateSheetCollection, sheetInfo, workplaceReportData, condition, rowPageTracker, remarkColumn);
					firstWpl ++;
				}
				
				// A3_2
//				Cell workplaceInfo = cells.get(currentRow, DATA_COLUMN_INDEX[0]);
//				workplaceInfo.setValue(workplaceReportData.getWorkplaceCode() + " " + workplaceReportData.getWorkplaceName());

                currentRow = this.printPersonal(cells, currentRow, templateSheetCollection, sheetInfo, employeeReportData, condition, rowPageTracker, remarkColumn);
				
				// A4_2
//				Cell employeeCell = cells.get(currentRow, DATA_COLUMN_INDEX[0]);
//				employeeCell.setValue(employeeReportData.employeeCode + " " + employeeReportData.employeeName);
//				
//				// A4_3
//				Cell employmentTagCell = cells.get(currentRow, DATA_COLUMN_INDEX[1]);
//				employmentTagCell.setValue(WorkScheOutputConstants.EMPLOYMENT);
//				
//				// A4_5
//				Cell employmentCell = cells.get(currentRow, DATA_COLUMN_INDEX[2]);
//				employmentCell.setValue(employeeReportData.employmentName);
//				
//				// A4_6
//				Cell jobTitleTagCell = cells.get(currentRow, DATA_COLUMN_INDEX[3]);
//				jobTitleTagCell.setValue(WorkScheOutputConstants.POSITION);
//
//				// A4_7
//				Cell jobTitleCell = cells.get(currentRow, DATA_COLUMN_INDEX[4]);
//				jobTitleCell.setValue(employeeReportData.position);
				
				boolean colorWhite = true; // true = white, false = light blue, start with white row
				
				// Detail personal performance
				if (totalOutput.isDetails()) {
					List<DetailedMonthlyPerformanceReportData> lstDetailedDailyPerformance = employeeReportData.getLstDetailedMonthlyPerformance();
					for (DetailedMonthlyPerformanceReportData detailedDailyPerformanceReportData: lstDetailedDailyPerformance) {
						Range dateRangeTemp;
						
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
							if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
								cells = sheetInfo.getSheet().getCells();
								currentRow = sheetInfo.getStartDataIndex();
							}
							rowPageTracker.resetRemainingRow();
						}
						
						// A5_1
						Cell dateCell = cells.get(currentRow, 0);
						YearMonth yearMonth = detailedDailyPerformanceReportData.getYearMonth();
						String yearMonthStr = yearMonth.year() + "/" + (yearMonth.month() < 10? "0" + yearMonth.month() : yearMonth.month());
						Cell prevYearMonthCell = cells.get(currentRow - dataRowCount, 0);
						if (prevYearMonthCell.getValue() != null && yearMonthStr.equals(prevYearMonthCell.getValue())) {
							colorWhite = !colorWhite;
						}
						
						if (colorWhite) // White row
							dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_WHITE_ROW + dataRowCount);
						else // Light blue row
							dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_LIGHTBLUE_ROW + dataRowCount);
						Range dateRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
						dateRange.copy(dateRangeTemp);
						dateRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
							rowPageTracker.useRemainingRow(dataRowCount);
							rowPageTracker.resetRemainingRow();
						}
						else {
							rowPageTracker.useRemainingRow(dataRowCount);
						}
						
						if (prevYearMonthCell.getValue() != null && yearMonthStr.equals(prevYearMonthCell.getValue())) {
							Range yearMonthRange = cells.createRange(currentRow, 0, dataRowCount, 2);
							yearMonthRange.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.NONE, Color.getBlack());
						}
						else {
							dateCell.setValue(yearMonthStr);
						}
						
						Range dateRange2 = cells.createRange(currentRow, 0, dataRowCount, 2);
						dateRange2.merge();
						
						// A5_2
						List<ActualValue> lstItem = detailedDailyPerformanceReportData.getActualValue();
						
						// A5_4
						String closureDate = detailedDailyPerformanceReportData.getClosureDate();
						Cell closureCell = cells.get(currentRow, 2);
						closureCell.putValue(closureDate);
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
				            	Style style = cell.getStyle();
				            	ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
				            	String value = actualValue.getValue();
				            	if (valueTypeEnum.isTime()) {
									if (value != null) {
//										if (valueTypeEnum == ValueType.TIME) {
//											cell.setValue(getTimeAttr(value, false));
//										} else {
//											cell.setValue(getTimeAttr(value, true));
//										}
										cell.setValue(getTimeAttr(value, false, condition.getDisplayType()));
									}
									else{
										cell.setValue(getTimeAttr("0", true, condition.getDisplayType()));
									}	
									style.setHorizontalAlignment(TextAlignmentType.RIGHT);
				            	}
				            	else if ((valueTypeEnum.isDouble() || valueTypeEnum.isInteger()) && !StringUtil.isNullOrEmpty(value, false)) {
				            		value = condition.getDisplayType() == DisplayTypeEnum.HIDE.value && (value.equals("0") || value.equals("0.0")) ? "" : value;
									if (!StringUtil.isNullOrEmpty(value, false)
											&& (valueTypeEnum.equals(ValueType.AMOUNT_NUM)
													|| valueTypeEnum.equals(ValueType.AMOUNT))) {
				            			DecimalFormat format = new DecimalFormat("###,###,###");
				            			cell.putValue(format.format(valueTypeEnum.isInteger() ? Integer.parseInt(value) :  Double.parseDouble(value)), false);
				            		} else {
					            		cell.putValue(value, true);
				            		}
									style.setHorizontalAlignment(TextAlignmentType.RIGHT);
								}
				            	else {
				            		cell.putValue(value);
									style.setHorizontalAlignment(TextAlignmentType.LEFT);
				            	}
				            	if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
				        			cell.setValue(cell.getValue().toString().concat(actualValue.getUnit()));
				        		}
				            	setFontStyle(style, textSizeCommonEnum);
				            	cell.setStyle(style);
				            }
				            
				            curRow++;
				        }
				        
				        // A5_5
				        Cell remarkCell = cells.get(currentRow, remarkColumn-REMARK_CELL_WIDTH);
				        String errorDetail = detailedDailyPerformanceReportData.getErrorDetail();
				        
				        int numOfChunksRemark = (int)Math.ceil((double)errorDetail.length() / 15);
						int curRowRemark = currentRow;
						String remarkContentRow;
						
						int maxPossibleRow = Math.min(numOfChunksRemark, dataRowCount);
						
			        	for(int i = 0; i < maxPossibleRow; i++) {
			        		start = i * 15;
				            length = Math.min(errorDetail.length() - start, 15);
				            
				            // add ... to remark when remark length > maximum possible length
							remarkContentRow = errorDetail.length() > dataRowCount * 15 && (i == (dataRowCount - 1))
								            		? (errorDetail.substring(start, start + length) + "…")
						            				: errorDetail.substring(start, start + length);
				            
				            for (int j = 0; j < length; j++) {
				            	// Column 4, 6, 8,...
				            	remarkCell = cells.get(curRowRemark, remarkColumn-REMARK_CELL_WIDTH); 
				            	Style style = remarkCell.getStyle();
				            	remarkCell.setValue(remarkContentRow);
								style.setHorizontalAlignment(TextAlignmentType.LEFT);
				            	setFontStyle(style, textSizeCommonEnum);
				            	remarkCell.setStyle(style);
				            }
				            
				            curRowRemark++;
				        }
				        
				        //remarkCell.setValue(detailedDailyPerformanceReportData.getErrorDetail());
				        
				        currentRow += dataRowCount;
				        colorWhite = !colorWhite; // Change to other color
					}
				}
				
				// Personal total
				if (totalOutput.isPersonalTotal()) {
					if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
						if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
							cells = sheetInfo.getSheet().getCells();
							currentRow = sheetInfo.getStartDataIndex();
						}
						rowPageTracker.resetRemainingRow();
					}
					
					Range personalTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
					Range personalTotalRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
					personalTotalRange.copy(personalTotalRangeTemp);
					personalTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
					if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
						rowPageTracker.useRemainingRow(dataRowCount);
						rowPageTracker.resetRemainingRow();
					}
					else {
						rowPageTracker.useRemainingRow(dataRowCount);
					}
					
					// A6_1
					Cell personalTotalCellTag = cells.get(currentRow, 0);
					personalTotalCellTag.setValue(TextResource.localize("KWR006_72"));
					
					// A6_2
					Map<Integer, TotalValue> mapPersonalTotal = employeeReportData.getMapPersonalTotal();
					int numOfChunks = (int) Math.ceil( (double) mapPersonalTotal.size() / chunkSize);
					int start, length;
					List<TotalValue> lstItemRow;
					DecimalFormat format = new DecimalFormat("###,###,###");
			        for(int i = 0; i < numOfChunks; i++) {
			            start = i * chunkSize;
			            length = Math.min(mapPersonalTotal.size() - start, chunkSize);
			            
			            lstItemRow = mapPersonalTotal.values().stream().collect(Collectors.toList()).subList(start, start + length);
			            int valueType;
			            
			            for (int j = 0; j < length; j++) {
			            	TotalValue totalValue = lstItemRow.get(j);
			            	String value = totalValue.getValue();
			            	valueType = totalValue.getValueType();

			            	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2); 
			            	Style style = cell.getStyle();
			            	ValueType valueTypeEnum = EnumAdaptor.valueOf(valueType, ValueType.class);

			            	if (lstAtdCanBeAggregate.contains(totalValue.getAttendanceId())) {
			            		if (valueTypeEnum.isIntegerCountable()) {
			            			switch (valueTypeEnum) {
									case COUNT:
										cell.putValue(condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Integer.parseInt(value) == 0 ? "" : value);
										break;
									case AMOUNT_NUM:
										cell.putValue((condition.getDisplayType() == DisplayTypeEnum.HIDE.value
												&& Integer.parseInt(value) == 0) ? "" : format.format(Integer.parseInt(value)), false);
										break;
									default:
										if (value != null)
											cell.setValue(getTimeAttr(value,false, condition.getDisplayType()));
										else
											cell.setValue(getTimeAttr("0",false, condition.getDisplayType()));
										break;
									}
									style.setHorizontalAlignment(TextAlignmentType.RIGHT);
								}
				            	else if (valueTypeEnum.isDoubleCountable() && value != null) {
				            		value = condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Double.parseDouble(value) == 0d ? "" : value;
				            		if (!StringUtil.isNullOrEmpty(value, true)) {
				            			cell.putValue(valueTypeEnum == ValueType.AMOUNT ? format.format(Double.parseDouble(value)) : value);
				            		} else {
				            			cell.putValue("");
				            		}
				            	}
				            	if (valueTypeEnum.isDouble() || valueTypeEnum.isInteger()){
				            		style.setHorizontalAlignment(TextAlignmentType.RIGHT);
				            	}
			            	}
			            	if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
			        			cell.setValue(cell.getValue().toString().concat(totalValue.getUnit())); 
			        		}
			            	setFontStyle(style, textSizeCommonEnum);
			            	cell.setStyle(style);
			            }
			            currentRow++;
			        }
				}
		        
		        // Page break by employee
				if (condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE && iteratorEmployee.hasNext()) {
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
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}
			
			Range workplaceTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range workplaceTotalRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			workplaceTotalRange.copy(workplaceTotalRangeTemp);
			workplaceTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			
			// A8_1
			Cell workplaceTotalCellTag = cells.get(currentRow, 0);
			workplaceTotalCellTag.setValue(TextResource.localize("KWR006_73"));
			
			// A8_2
			WorkplaceTotal workplaceTotal = workplaceReportData.getWorkplaceTotal();
			int numOfChunks = (int) Math.ceil( (double) workplaceTotal.getTotalWorkplaceValue().size() / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;

			DecimalFormat format = new DecimalFormat("###,###,###");
	        for(int i = 0; i < numOfChunks; i++) {
	            start = i * chunkSize;
	            length = Math.min(workplaceTotal.getTotalWorkplaceValue().size() - start, chunkSize);
	
	            lstItemRow = workplaceTotal.getTotalWorkplaceValue().subList(start, start + length);

	            for (int j = 0; j < length; j++) {
	            	TotalValue totalValue = lstItemRow.get(j);
	            	String value = totalValue.getValue();
	            	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2); 
	            	Style style = cell.getStyle();
	            	ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValue.getValueType(), ValueType.class);
	            	if (valueTypeEnum.isIntegerCountable()) {
	            		switch (valueTypeEnum) {
						case COUNT:
							cell.putValue(condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Integer.parseInt(value) == 0 ? "" : value);
							break;
						case AMOUNT_NUM:
							cell.putValue((condition.getDisplayType() == DisplayTypeEnum.HIDE.value
									&& Integer.parseInt(value) == 0) ? "" : format.format(Integer.parseInt(value)), false);
							break;
						default:
							if (value != null)
								cell.setValue(getTimeAttr(value,false, condition.getDisplayType()));
							else
								cell.setValue(getTimeAttr("0",false, condition.getDisplayType()));
							break;
						}
						style.setHorizontalAlignment(TextAlignmentType.RIGHT);
					}
	            	else if (valueTypeEnum.isDoubleCountable() && value != null) {
	            		value = condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Double.parseDouble(value) == 0d ? "" : value;
	            		if (!StringUtil.isNullOrEmpty(value, true)) {
	            			cell.putValue(valueTypeEnum == ValueType.AMOUNT ? format.format(Double.parseDouble(value)) : value);
	            		} else {
	            			cell.putValue("");
	            		}
	            	}
	            	if (valueTypeEnum.isDouble() || valueTypeEnum.isInteger()) {
						style.setHorizontalAlignment(TextAlignmentType.RIGHT);
					}
	            	if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
	        			cell.setValue(cell.getValue().toString().concat(totalValue.getUnit())); 
	        		}
	            	setFontStyle(style, textSizeCommonEnum);
	            	cell.setStyle(style);
	            }
	            currentRow++;
	        }
		}
		
		Map<String, WorkplaceReportData> mapChildWorkplace = workplaceReportData.getLstChildWorkplaceReportData();
		// Check if any child workplace has data, page break when child workplace also has data (need to recursive into child workplace)
		int childHasDataCount = (int) mapChildWorkplace.values().stream().map(child -> child.isHasData()).filter(child -> child).count();
		if (((condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_WORKPLACE || 
				condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE) &&
				mapChildWorkplace.size() > 0) && workplaceReportData.level != 0 && childHasDataCount > 0) {
			Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, remarkColumn);
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
			
			// Remove page break when current workplace has no data
			for (int pageBreakIndex = 0; pageBreakIndex < sheetInfo.getSheet().getHorizontalPageBreaks().getCount(); pageBreakIndex++) {
				int ridx = sheetInfo.getSheet().getHorizontalPageBreaks().get(pageBreakIndex).getRow();
				if (ridx == currentRow && !entry.getValue().isHasData()) {
					sheetInfo.getSheet().getHorizontalPageBreaks().removeAt(pageBreakIndex);
					break;
				}
			}
			
			currentRow = writeDetailedWorkSchedule(query
												 , currentRow
												 , templateSheetCollection
												 , sheetInfo
												 , entry.getValue()
												 , dataRowCount
												 , condition
												 , rowPageTracker
												 , textSizeCommonEnum
												 , lstAtdCanBeAggregate);
			
			// Page break by workplace
			if ((condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_WORKPLACE || condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE)
					&& it.hasNext()) {
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
		if (totalOutput.isGrossTotal() || totalOutput.isCumulativeWorkplace()) {
			
			int level = workplaceReportData.getLevel();
			
			if (findWorkplaceHigherEnabledLevel(workplaceReportData, totalHierarchyOption) <= level && workplaceReportData.isHasData()) {
				String tagStr;
				
				WorkplaceReportData parentWorkplace = workplaceReportData.getParent();
				
				List<Integer> lstBetweenLevel = findEnabledLevelBetweenWorkplaces(workplaceReportData, totalHierarchyOption);
				Iterator<Integer> levelIterator = null;
				if (!lstBetweenLevel.isEmpty()) {
					levelIterator = lstBetweenLevel.iterator();
				}
				
				do {
					if (level != 0 && level >= totalHierarchyOption.getHighestLevelEnabled() && totalOutput.isCumulativeWorkplace()) {
						// Condition: not root workplace (lvl = 0), level within picked hierarchy zone, enable cumulative workplace.
						int parentHigherEnabledLevel = findWorkplaceHigherEnabledLevel(parentWorkplace, totalHierarchyOption);
						if (parentHigherEnabledLevel <= level) {
							if (levelIterator != null && levelIterator.hasNext()) {
								tagStr = TextResource.localize(WorkScheOutputConstants.WORKPLACE_INFO, Integer.toString(levelIterator.next()));
							}
							else {
								tagStr = null;
							}
						}
						else if (totalHierarchyOption.checkLevelEnabled(level)) {
							tagStr = TextResource.localize(WorkScheOutputConstants.WORKPLACE_INFO, Integer.toString(workplaceReportData.getLevel()));
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
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
							if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
								cells = sheetInfo.getSheet().getCells();
								currentRow = sheetInfo.getStartDataIndex();
							}
							rowPageTracker.resetRemainingRow();
						}
						
                        cells = sheetInfo.getSheet().getCells();
						Range wkpHierTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
						Range wkpHierTotalRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
						wkpHierTotalRange.copy(wkpHierTotalRangeTemp);
						wkpHierTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
						if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
							rowPageTracker.useRemainingRow(dataRowCount);
							rowPageTracker.resetRemainingRow();
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

			            DecimalFormat format = new DecimalFormat("###,###,###");
				        for(int i = 0; i < numOfChunks; i++) {
				            start = i * chunkSize;
				            length = Math.min(workplaceReportData.getGrossTotal().size() - start, chunkSize);
				
				            lstItemRow = workplaceReportData.getGrossTotal().subList(start, start + length);
				            for (int j = 0; j < length; j++) {
				            	TotalValue totalValue = lstItemRow.get(j);
				            	String value = totalValue.getValue();
				            	Cell cell = cells.get(currentRow + i, DATA_COLUMN_INDEX[0] + j * 2); 
				            	Style style = cell.getStyle();
				            	ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValue.getValueType(), ValueType.class);
				            	if (valueTypeEnum.isIntegerCountable()) {
				            		switch (valueTypeEnum) {
									case COUNT:
										cell.putValue(condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Integer.parseInt(value) == 0 ? "" : value);
										break;
									case AMOUNT_NUM:
										cell.putValue((condition.getDisplayType() == DisplayTypeEnum.HIDE.value
												&& Integer.parseInt(value) == 0) ? "" : format.format(Integer.parseInt(value)), false);
										break;
									default:
										if (value != null)
											cell.setValue(getTimeAttr(value,false, condition.getDisplayType()));
										else
											cell.setValue(getTimeAttr("0",false, condition.getDisplayType()));
										break;
									}
									style.setHorizontalAlignment(TextAlignmentType.RIGHT);
								}
				            	else if (valueTypeEnum.isDoubleCountable() && value != null) {
				            		value = condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Double.parseDouble(value) == 0d ? "" : value;
				            		if (!StringUtil.isNullOrEmpty(value, true)) {
				            			cell.putValue(valueTypeEnum == ValueType.AMOUNT ? format.format(Double.parseDouble(value)) : value);
				            		} else {
				            			cell.putValue("");
				            		}
				            	}
//				            	else if (valueTypeEnum == ValueType.TEXT && value != null) {
//				            		cell.putValue(value, false);
//				            	}
				            	if (valueTypeEnum.isDouble() || valueTypeEnum.isInteger()) {
									style.setHorizontalAlignment(TextAlignmentType.RIGHT);
								}
				            	if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
				        			cell.setValue(cell.getValue().toString().concat(totalValue.getUnit())); 
				        		}
				            	setFontStyle(style, textSizeCommonEnum);
				            	cell.setStyle(style);
				            }
				        }
				        currentRow += dataRowCount;
					} else {
						if (levelIterator != null && levelIterator.hasNext()) {
							levelIterator.next();
						}
					}
				} while (levelIterator != null && levelIterator.hasNext());
			}
		}
		
		return currentRow;
	}
	
	/**
	 * Find detailed data.
	 *
	 * @param workplaceData the workplace data
	 * @return true, if successful
	 */
	private boolean findDetailedData(MonthlyWorkplaceData workplaceData) {
		List<MonthlyPersonalPerformanceData> lstMonthlyPersonalData = workplaceData.getLstDailyPersonalData();
		if (lstMonthlyPersonalData != null && !lstMonthlyPersonalData.isEmpty())
		for (MonthlyPersonalPerformanceData personalData : lstMonthlyPersonalData) {
			List<ActualValue> lstActualValue = personalData.getActualValue();
			if (lstActualValue != null && !lstActualValue.isEmpty())
				return true;
		}
		Map<String, MonthlyWorkplaceData> mapChildWorkplaceData = workplaceData.getLstChildWorkplaceData();
		for (Map.Entry<String, MonthlyWorkplaceData> entrySet: mapChildWorkplaceData.entrySet()) {
			MonthlyWorkplaceData childWorkplace = entrySet.getValue();
			if (findDetailedData(childWorkplace))
				return true;
		}
		return false;
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
	public int writeDetailedMonthlySchedule(int currentRow, WorksheetCollection templateSheetCollection, WorkSheetInfo sheetInfo, MonthlyReportData dailyReport,
			int dataRowCount, MonthlyWorkScheduleCondition condition, RowPageTracker rowPageTracker, TextSizeCommonEnum textSizeCommonEnum, int remarkColumn) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		List<WorkplaceMonthlyReportData> lstMonthlyReportData = dailyReport.getLstMonthlyReportData();
		
		// Remove all monthly workplace data w/o any data inside it
		for (int i = lstMonthlyReportData.size() - 1; i >=0 ; i--) {
			WorkplaceMonthlyReportData monthlyReportData = lstMonthlyReportData.get(i);
			if (!findDetailedData(monthlyReportData.getLstWorkplaceData()))
				lstMonthlyReportData.remove(i);
		}
		
		Iterator<WorkplaceMonthlyReportData> iteratorWorkplaceData  = lstMonthlyReportData.iterator();
		
		while(iteratorWorkplaceData.hasNext()) {
			WorkplaceMonthlyReportData monthlyReportData = iteratorWorkplaceData.next();
			MonthlyWorkplaceData rootWorkplace = monthlyReportData.getLstWorkplaceData();
//			if (!findDetailedData(rootWorkplace))
//				continue;
			// rowPageTracker.useOneRowAndCheckResetRemainingRow(sheetInfo.getSheet(), currentRow);
			if (rowPageTracker.checkRemainingRowSufficient(3) <= 0) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}
			//dateRange.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
			
			// B3_1
			int month = monthlyReportData.getYearMonth().month();
			String date = monthlyReportData.getYearMonth().year() + "/" + (month < 10 ? "0" + month : month);
			String titleDate = TextResource.localize("KWR006_77") + date;

			currentRow = writeDailyDetailedPerformanceDataOnWorkplace(currentRow
					, sheetInfo
					, templateSheetCollection
					, rootWorkplace
					, dataRowCount
					, condition
					, rowPageTracker
					, titleDate
					, textSizeCommonEnum
					, remarkColumn);
		
			/*if (condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE
					|| condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_WORKPLACE) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}*/
		}
		
		if (condition.getTotalOutputSetting().isGrossTotal()) {
			// Remove page break after the very last workplace, previous iterator should always generate a page break after every workplace
			int ridx = 0;
			for (int pageBreakIndex = 0; pageBreakIndex < sheetInfo.getSheet().getHorizontalPageBreaks().getCount(); pageBreakIndex++) {
				ridx = sheetInfo.getSheet().getHorizontalPageBreaks().get(pageBreakIndex).getRow();
				if (ridx == currentRow && condition.getPageBreakIndicator() != MonthlyWorkScheduleCondition.PAGE_BREAK_NOT_USE) {
					sheetInfo.getSheet().getHorizontalPageBreaks().removeAt(pageBreakIndex);
					break;
				}
			}
			
			// Gross total after all the rest of the data
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}
			cells = sheetInfo.getSheet().getCells();
			Range wkpGrossTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range wkpGrossTotalRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
			wkpGrossTotalRange.copy(wkpGrossTotalRangeTemp);
			wkpGrossTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			Cell grossTotalCellTag = cells.get(currentRow, 0);
			grossTotalCellTag.setValue(WorkScheOutputConstants.GROSS_TOTAL);
			
			currentRow = writeGrossTotal(currentRow, dailyReport.getListTotalValue(), sheetInfo.getSheet(), dataRowCount, rowPageTracker, condition, textSizeCommonEnum);
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
			, MonthlyWorkplaceData rootWorkplace
			, int dataRowCount
			, MonthlyWorkScheduleCondition condition
			, RowPageTracker rowPageTracker
			, String titleDate
			, TextSizeCommonEnum textSizeCommonEnum
			, int remarkColumn) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		if (!findDetailedData(rootWorkplace))
			return currentRow;
		String workplaceTitle = TextResource.localize("KWR006_68") + rootWorkplace.getWorkplaceCode()
				+ "　" + rootWorkplace.getWorkplaceName();
		/*Range workplaceRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_DATE_ROW);
		Range workplaceRange = cells.createRange(currentRow, 0, 1, remarkColumn);
		workplaceRange.copy(workplaceRangeTemp);*/
		
		boolean colorWhite = true; // true = white, false = light blue, start with white row
		
		List<MonthlyPersonalPerformanceData> employeeReportData = rootWorkplace.getLstDailyPersonalData().stream()
				.sorted(Comparator.comparing(MonthlyPersonalPerformanceData::getEmployeeCode))
				.collect(Collectors.toList());
		if (employeeReportData != null && !employeeReportData.isEmpty()) {
			boolean isPrintWplTitle = false;
			// rowPageTracker.useOneRowAndCheckResetRemainingRow(sheetInfo.getSheet(), currentRow);
			if (rowPageTracker.checkRemainingRowSufficient(2 + dataRowCount) <= 0) {
				rowPageTracker.resetRemainingRow();
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, remarkColumn);
				currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, remarkColumn);
				rowPageTracker.useRemainingRow(2);
				isPrintWplTitle = true;
			}

			if (condition.getTotalOutputSetting().isDetails() || condition.getTotalOutputSetting().isWorkplaceTotal()
					|| condition.getTotalOutputSetting().isCumulativeWorkplace()) {
				// B4_1
				if (!isPrintWplTitle){
					currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, remarkColumn);
					currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, remarkColumn);
					rowPageTracker.useRemainingRow(2);
				}

				/*Cell workplaceTagCell = cells.get(currentRow, 0);
				workplaceTagCell.setValue(WorkScheOutputConstants.WORKPLACE + "　" + rootWorkplace.getWorkplaceCode()
						+ "　" + rootWorkplace.getWorkplaceName());
				currentRow++;*/
			}
//			// B4_2
//			Cell workplaceCell = cells.get(currentRow, 2);
//			workplaceCell.setValue(rootWorkplace.getWorkplaceCode() + " " + rootWorkplace.getWorkplaceName());
			
			Iterator<MonthlyPersonalPerformanceData> dataIterator = employeeReportData.iterator();
			
			// Employee data
			while (dataIterator.hasNext() && condition.getTotalOutputSetting().isDetails()){
				MonthlyPersonalPerformanceData employee = dataIterator.next();
				
				List<ActualValue> lstItem = employee.getActualValue();
				// Skip to next employee when there is no actual value
				if (lstItem == null || lstItem.isEmpty()) continue;
				
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
					rowPageTracker.resetRemainingRow();
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
					currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, remarkColumn);
					currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, remarkColumn);
					rowPageTracker.useRemainingRow(2);
				}
				
				// B5_1
				Cell employeeCodeCell = cells.get(currentRow, 0);
				Cell employeeNameCell = cells.get(currentRow, 1);
				
				Cell prevEmployeeCell = cells.get(currentRow - dataRowCount, 0);
				if (prevEmployeeCell.getValue() != null && prevEmployeeCell.getValue().toString().equals(employee.getEmployeeName()) && condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE) {
					colorWhite = !colorWhite;
					
					// Loop until getting the correct row, then remove page break
					for (int pageBreakIndex = 0; ; pageBreakIndex++) {
						int ridx = sheetInfo.getSheet().getHorizontalPageBreaks().get(pageBreakIndex).getRow();
						if (ridx == currentRow) {
							sheetInfo.getSheet().getHorizontalPageBreaks().removeAt(pageBreakIndex);
							break;
						}
					}
				}
				
				Range dateRangeTemp;
				if (colorWhite) // White row
					dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_WHITE_ROW + dataRowCount);
				else // Light blue row
					dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_LIGHTBLUE_ROW + dataRowCount);
				Range dateRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
				dateRange.copy(dateRangeTemp);
				dateRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
					rowPageTracker.useRemainingRow(dataRowCount);
					rowPageTracker.resetRemainingRow();
				}
				else {
					rowPageTracker.useRemainingRow(dataRowCount);
				}
				
				if (prevEmployeeCell.getValue() != null && prevEmployeeCell.getValue().toString().equals(employee.getEmployeeName())) {
					Range employeeRange = cells.createRange(currentRow, 0, dataRowCount, 2);
					employeeRange.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.NONE, Color.getBlack());
				}
				else {
					employeeCodeCell.setValue(employee.getEmployeeCode());
					employeeNameCell.setValue(employee.getEmployeeName());
				}
				
				// B5_3
				Cell closureDateCell = cells.get(currentRow, 2);
				String closureDate = employee.getClosureDate();
				closureDateCell.putValue(closureDate);
				
				if (lstItem != null) {
					// B5_3
					// Divide list into smaller lists (max 16 items)
					int size = lstItem.size();
					int chunkSize = textSizeCommonEnum == TextSizeCommonEnum.BIG
							? MonthlyReportConstant.CHUNK_BIG_SIZE
							: MonthlyReportConstant.CHUNK_SMALL_SIZE;
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
			            	Style style = cell.getStyle();
			            	ValueType valueTypeEnum = EnumAdaptor.valueOf(actualValue.getValueType(), ValueType.class);
			            	String value = actualValue.getValue();
			            	if (valueTypeEnum.isTime()) {
								if (value != null)
									cell.setValue(getTimeAttr(value,false, condition.getDisplayType()));
								else
									cell.setValue(getTimeAttr("0",false, condition.getDisplayType()));
								style.setHorizontalAlignment(TextAlignmentType.RIGHT);
			            	}
			            	else if ((valueTypeEnum.isDouble() || valueTypeEnum.isInteger()) && !StringUtil.isNullOrEmpty(value, false)) {
			            		value = condition.getDisplayType() == DisplayTypeEnum.HIDE.value && (value.equals("0") || value.equals("0.0")) ? "" : value;
			            		if (!StringUtil.isNullOrEmpty(value, false)
										&& (valueTypeEnum.equals(ValueType.AMOUNT_NUM)
												|| valueTypeEnum.equals(ValueType.AMOUNT))) {
			            			DecimalFormat format = new DecimalFormat("###,###,###");
			            			cell.putValue(format.format(valueTypeEnum.isInteger() ? Integer.parseInt(value) :  Double.parseDouble(value)), false);
			            		} else {
			            			cell.putValue(value, true);	
			            		}
			            		style.setHorizontalAlignment(TextAlignmentType.RIGHT);
			            	}
			            	else {
								cell.setValue(value);
								style.setHorizontalAlignment(TextAlignmentType.LEFT);
							}
			            	if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
			        			cell.setValue(cell.getValue().toString().concat(actualValue.getUnit())); 
			        		}
			            	setFontStyle(style, textSizeCommonEnum);
			            	cell.setStyle(style);
			            }
			            
			            curRow++;
			        }
			        
			        // B5_4
			        Cell remarkCell = cells.get(currentRow, remarkColumn);
			        String errorDetail = employee.getDetailedErrorData();
			        
			        int numOfChunksRemark = (int)Math.ceil((double)errorDetail.length() / 15);
					int curRowRemark = currentRow;
					String remarkContentRow;
					
					int maxPossibleRow = Math.min(numOfChunksRemark, dataRowCount);
					
		        	for(int i = 0; i < maxPossibleRow; i++) {
		        		start = i * 15;
			            length = Math.min(errorDetail.length() - start, 15);
			            
			            // add ... to remark when remark length > maximum possible length
						remarkContentRow = errorDetail.length() > dataRowCount * 15 && (i == (dataRowCount - 1))
							            		? (errorDetail.substring(start, start + length) + "…")
					            				: errorDetail.substring(start, start + length);
			            
			            for (int j = 0; j < length; j++) {
			            	// Column 4, 6, 8,...
			            	remarkCell = cells.get(curRowRemark, remarkColumn-REMARK_CELL_WIDTH); 
			            	Style style = remarkCell.getStyle();
			            	remarkCell.setValue(remarkContentRow);
							style.setHorizontalAlignment(TextAlignmentType.LEFT);
			            	setFontStyle(style, textSizeCommonEnum);
			            	remarkCell.setStyle(style);
			            }
			            
			            curRowRemark++;
			        }
			        //remarkCell.setValue(employee.getDetailedErrorData());
				}
		        currentRow += dataRowCount;
		        colorWhite = !colorWhite; // Change to other color
		        
		        // Only break when has next iterator
		        if (dataIterator.hasNext() && condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE) {
		        	// Thin border for last row of page
		        	Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, remarkColumn);
		        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
		        	
		        	// Page break by employee
					rowPageTracker.resetRemainingRow();
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
					currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, remarkColumn);
					currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, remarkColumn);
					rowPageTracker.useRemainingRow(2);
		        }
			}
		}
		
		// Workplace total, root workplace use gross total instead
		if (condition.getTotalOutputSetting().isWorkplaceTotal() && rootWorkplace.getLevel() != 0 && employeeReportData != null && !employeeReportData.isEmpty()) {
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
                rowPageTracker.resetRemainingRow();
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
                currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, remarkColumn);
                currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, remarkColumn);
                rowPageTracker.useRemainingRow(2);
			}
			Range workplaceTotalTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
			Range workplaceTotal = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
			workplaceTotal.copy(workplaceTotalTemp);
			workplaceTotal.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());
			workplaceTotal.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
				rowPageTracker.useRemainingRow(dataRowCount);
				rowPageTracker.resetRemainingRow();
			}
			else {
				rowPageTracker.useRemainingRow(dataRowCount);
			}
			
			// B6_1
			Cell workplaceTotalCellTag = cells.get(currentRow, 0);
			workplaceTotalCellTag.setValue(TextResource.localize("KWR006_73"));
			
			// B6_2
			if (rootWorkplace.hasData)
				currentRow = writeWorkplaceTotal(currentRow, rootWorkplace, sheetInfo.getSheet(), dataRowCount, true, textSizeCommonEnum, condition.getDisplayType());
		}
		
		Map<String, MonthlyWorkplaceData> mapChildWorkplace = rootWorkplace.getLstChildWorkplaceData();
		// Check if any child workplace has data, page break when child workplace also has data (need to recursive into child workplace)
		int childHasDataCount = (int) mapChildWorkplace.values().stream().map(child -> child.isHasData()).filter(child -> child).count();
		if (((condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_WORKPLACE || 
				condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE) &&
				mapChildWorkplace.size() > 0) && rootWorkplace.level != 0 && childHasDataCount > 0
				&& employeeReportData != null && !employeeReportData.isEmpty()) {
			Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, remarkColumn);
        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
				cells = sheetInfo.getSheet().getCells();
				currentRow = sheetInfo.getStartDataIndex();
			}
			rowPageTracker.resetRemainingRow();
		}
		// Child workplace
		Iterator<Map.Entry<String, MonthlyWorkplaceData>> workplaceIterator = mapChildWorkplace.entrySet().iterator();
		while (workplaceIterator.hasNext()) {
			Map.Entry<String, MonthlyWorkplaceData> entry = workplaceIterator.next();
			currentRow = writeDailyDetailedPerformanceDataOnWorkplace(currentRow
					, sheetInfo
					, templateSheetCollection
					, entry.getValue()
					, dataRowCount
					, condition
					, rowPageTracker
					, titleDate
					, textSizeCommonEnum
					, remarkColumn);
			
			// Page break by workplace
			if ((condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_WORKPLACE || 
					condition.getPageBreakIndicator() == MonthlyWorkScheduleCondition.PAGE_BREAK_EMPLOYEE) && rootWorkplace.hasData 
					&& (workplaceIterator.hasNext() || !workplaceIterator.hasNext() && rootWorkplace.level != 0)) {
				Range lastRowRange = cells.createRange(currentRow - 1, 0, 1, remarkColumn);
	        	lastRowRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
				if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
					cells = sheetInfo.getSheet().getCells();
					currentRow = sheetInfo.getStartDataIndex();
				}
				rowPageTracker.resetRemainingRow();
			}
		}
		
		// Workplace hierarchy total
		int level = rootWorkplace.getLevel();
		TotalWorkplaceHierachy settingTotalHierarchy = condition.getTotalOutputSetting().getWorkplaceHierarchyTotal();
		List<Integer> lstBetweenLevel = findEnabledLevelBetweenWorkplaces(rootWorkplace, settingTotalHierarchy);
		Iterator<Integer> levelIterator = null;
		if (!lstBetweenLevel.isEmpty()) {
			levelIterator = lstBetweenLevel.iterator();
		}
		if (level != 0 && level >= settingTotalHierarchy.getHighestLevelEnabled() 
				&& condition.getTotalOutputSetting().isCumulativeWorkplace()) {
			do {
				if (levelIterator != null && levelIterator.hasNext()) 
					level = (int) levelIterator.next();
				if (!settingTotalHierarchy.checkLevelEnabled(level)) break;
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) <= 0) {
                    rowPageTracker.resetRemainingRow();
					if (this.checkLimitPageBreak(templateSheetCollection, sheetInfo, currentRow)) {
						cells = sheetInfo.getSheet().getCells();
						currentRow = sheetInfo.getStartDataIndex();
					}
                    currentRow = this.printDateBracket(currentRow, templateSheetCollection, sheetInfo, titleDate, remarkColumn);
                    currentRow = this.printWorkplace(currentRow, templateSheetCollection, sheetInfo, workplaceTitle, remarkColumn);
                    rowPageTracker.useRemainingRow(2);
				}
				Range wkpHierTotalRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_TOTAL_ROW + dataRowCount);
				Range wkpHierTotalRange = cells.createRange(currentRow, 0, dataRowCount, remarkColumn);
				wkpHierTotalRange.copy(wkpHierTotalRangeTemp);
				wkpHierTotalRange.setOutlineBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
				if (rowPageTracker.checkRemainingRowSufficient(dataRowCount) == 0) {
					rowPageTracker.useRemainingRow(dataRowCount);
					rowPageTracker.resetRemainingRow();
				}
				else {
					rowPageTracker.useRemainingRow(dataRowCount);
				}
			
				// B7_1 - B11_1
				Cell workplaceTotalCellTag = cells.get(currentRow, 0);
				workplaceTotalCellTag.setValue(TextResource.localize(WorkScheOutputConstants.WORKPLACE_INFO, Integer.toString(level)));
				
				// B7_2 - B11_2
				currentRow = writeWorkplaceTotal(currentRow, rootWorkplace, sheetInfo.getSheet(), dataRowCount, false, textSizeCommonEnum, condition.getDisplayType());
			} while (levelIterator != null && levelIterator.hasNext());
		}
		
		return currentRow;
	}

	/**
	 * Write workplace total for export type by date.
	 *
	 * @param currentRow the current row
	 * @param rootWorkplace the root workplace
	 * @param sheet the sheet
	 * @param dataRowCount the data row count
	 * @param totalType the total type
	 * @return the int
	 */
	private int writeWorkplaceTotal(int currentRow
			, MonthlyWorkplaceData rootWorkplace
			, Worksheet sheet
			, int dataRowCount
			, boolean totalType
			, TextSizeCommonEnum textSizeCommonEnum
			, int zeroSetting) {
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
			int chunkSize = textSizeCommonEnum == TextSizeCommonEnum.BIG
					? MonthlyReportConstant.CHUNK_BIG_SIZE
					: MonthlyReportConstant.CHUNK_SMALL_SIZE;
			int numOfChunks = (int) Math.ceil( (double) size / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;

		    DecimalFormat format = new DecimalFormat("###,###,###");
			for(int i = 0; i < numOfChunks; i++) {
			    start = i * chunkSize;
			    length = Math.min(size - start, chunkSize);

			    lstItemRow = totalWorkplaceValue.stream().collect(Collectors.toList()).subList(start, start + length);
			    TotalValue totalValue;
			    for (int j = 0; j < length; j++) {
			    	totalValue = lstItemRow.get(j);
			    	String value = totalValue.getValue();
		        	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2); 
		        	Style style = cell.getStyle();
		        	ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValue.getValueType(), ValueType.class);
		        	if (valueTypeEnum.isIntegerCountable()) {
		        		switch (valueTypeEnum) {
						case COUNT:
							cell.putValue(zeroSetting == DisplayTypeEnum.HIDE.value && Integer.parseInt(value) == 0 ? "" : value, false);
							break;
						case AMOUNT_NUM:
							cell.putValue((zeroSetting == DisplayTypeEnum.HIDE.value && Integer.parseInt(value) == 0) ? "" : format.format(Integer.parseInt(value)), false);
							break;
						default:
							if (value != null)
								cell.setValue(getTimeAttr(value, false, zeroSetting));
							else
								cell.setValue(getTimeAttr("0", false, zeroSetting));
							break;
						}
						style.setHorizontalAlignment(TextAlignmentType.RIGHT);
					} else if (valueTypeEnum.isDoubleCountable() && value != null) {
	            		value = zeroSetting == DisplayTypeEnum.HIDE.value && Double.parseDouble(value) == 0d ? "" : value;
	            		if (!StringUtil.isNullOrEmpty(value, true)) {
	            			cell.putValue(valueTypeEnum == ValueType.AMOUNT ? format.format(Double.parseDouble(value)) : value);
	            		} else {
	            			cell.putValue("");
	            		}
	            	}
					if (valueTypeEnum.isDouble() || valueTypeEnum.isInteger()) {
						style.setHorizontalAlignment(TextAlignmentType.RIGHT);
					}
					if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
						cell.setValue(cell.getValue().toString().concat(totalValue.getUnit()));
					}
	            	setFontStyle(style, textSizeCommonEnum);
	            	cell.setStyle(style);
			    }
			    currentRow++;
			}
			
			if (numOfChunks == 0) currentRow++;
		}
		
		return currentRow;
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
			, MonthlyWorkScheduleCondition condition
			, TextSizeCommonEnum textSizeCommonEnum) {
		
		int size = lstGrossTotal.size();
		if (size == 0) {
			currentRow += dataRowCount;
		}
		else {
			Cells cells = sheet.getCells();
			int chunkSize = textSizeCommonEnum == TextSizeCommonEnum.BIG
					? MonthlyReportConstant.CHUNK_BIG_SIZE
					: MonthlyReportConstant.CHUNK_SMALL_SIZE;
			int numOfChunks = (int) Math.ceil( (double) size / chunkSize);
			int start, length;
			List<TotalValue> lstItemRow;
			
			for(int i = 0; i < numOfChunks; i++) {
			    start = i * chunkSize;
			    length = Math.min(lstGrossTotal.size() - start, chunkSize);

			    lstItemRow = lstGrossTotal.stream().collect(Collectors.toList()).subList(start, start + length);
			    
			    TotalValue totalValue;
			    DecimalFormat format = new DecimalFormat("###,###,###");
			    for (int j = 0; j < length; j++) {
			    	totalValue =lstItemRow.get(j);
			    	String value = totalValue.getValue();
		        	Cell cell = cells.get(currentRow, DATA_COLUMN_INDEX[0] + j * 2); 
		        	Style style = cell.getStyle();
		        	ValueType valueTypeEnum = EnumAdaptor.valueOf(totalValue.getValueType(), ValueType.class);
		        	if (valueTypeEnum.isIntegerCountable()) {
		        		switch (valueTypeEnum) {
						case COUNT:
							cell.putValue(condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Integer.parseInt(value) == 0 ? "" : value);
							break;
						case AMOUNT_NUM:
							cell.putValue((condition.getDisplayType() == DisplayTypeEnum.HIDE.value
									&& Integer.parseInt(value) == 0) ? "" : format.format(Integer.parseInt(value)), false);
							break;
						default:
							if (value != null)
								cell.setValue(getTimeAttr(value,false, condition.getDisplayType()));
							else
								cell.setValue(getTimeAttr("0",false, condition.getDisplayType()));
							break;
						}
						style.setHorizontalAlignment(TextAlignmentType.RIGHT);
					}
	            	else if (valueTypeEnum.isDoubleCountable() && value != null) {
	            		value = condition.getDisplayType() == DisplayTypeEnum.HIDE.value && Double.parseDouble(value) == 0d ? "" : value;
	            		if (!StringUtil.isNullOrEmpty(value, true)) {
	            			cell.putValue(valueTypeEnum == ValueType.AMOUNT ? format.format(Double.parseDouble(value)) : value);
	            		} else {
	            			cell.putValue("");
	            		}
	            	}
		        	
		        	if (valueTypeEnum.isDouble() || valueTypeEnum.isInteger()) {
						style.setHorizontalAlignment(TextAlignmentType.RIGHT);
					}
		        	if (!StringUtil.isNullOrEmpty(Objects.toString(cell.getValue(), null), true)) {
		    			cell.setValue(cell.getValue().toString().concat(totalValue.getUnit())); 
		    		}
	            	setFontStyle(style, textSizeCommonEnum);
	            	cell.setStyle(style);
			    }
			    currentRow++;
			}
			
			if (numOfChunks == 0) currentRow++;
		}
		
		return currentRow;
	}

	/**
	 * Find workplace higher enabled level.
	 *
	 * @param workplaceReportData the workplace report data
	 * @param hierarchy the hierarchy
	 * @return the int
	 */
	public int findWorkplaceHigherEnabledLevel(WorkplaceReportData workplaceReportData, TotalWorkplaceHierachy hierarchy) {
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
	 * Gets the time attr.
	 *
	 * @param rawValue the raw value
	 * @return the time attr
	 */
	public String getTimeAttr(String rawValue, boolean isConvertAttr, Integer displayType) {
		int value = Integer.parseInt(rawValue);
		TimeDurationFormatExtend timeFormat = new TimeDurationFormatExtend(value);
		if (isConvertAttr && value != 0) {
			//AttendanceTimeOfExistMinus time = new AttendanceTimeOfExistMinus(value);
			return timeFormat.getFullText();
		} else {
			return displayType == DisplayTypeEnum.HIDE.value && timeFormat.isZero() ? "" : timeFormat.getTimeText();
		}
	}

	/**
	 * Sets the font style.
	 *
	 * @param style the new font style
	 */
	private void setFontStyle(Style style, TextSizeCommonEnum textSize) {
		Font font = style.getFont();
		font.setDoubleSize(textSize == TextSizeCommonEnum.BIG
				? MonthlyReportConstant.BIG_SIZE
				: MonthlyReportConstant.SMALL_SIZE);
		font.setName(FONT_FAMILY);
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
	private void writeHeaderData(MonthlyWorkScheduleQuery query
								, OutputItemMonthlyWorkSchedule outputItem
								, Worksheet sheet
								, MonthlyPerformanceReportData reportData
								, int dateRow) {
		String settingNameFontSize = outputItem.getTextSize() == TextSizeCommonEnum.BIG
						? MonthlyReportConstant.BIG_SIZE_HEADER_SETTING
						: MonthlyReportConstant.SMALL_SIZE_HEADER_SETTING; 
		String companyDateFontSize = outputItem.getTextSize() == TextSizeCommonEnum.BIG 
						? MonthlyReportConstant.BIG_SIZE_HEADER_COMPANY_DATE
						: MonthlyReportConstant.SMALL_SIZE_HEADER_COMPANY_DATE; ; 
		// Company name
		PageSetup pageSetup = sheet.getPageSetup();
		pageSetup.setHeader(0, "&" + companyDateFontSize + "&\"MS ゴシック\" " + reportData.getHeaderData().companyName);
		
		// Output item name
		pageSetup.setHeader(1, "&" + settingNameFontSize + "&\"MS ゴシック,Bold\"" + outputItem.getItemName().v());
		
		// Set header date
		DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd  HH:mm", Locale.JAPAN);
		pageSetup.setHeader(2, "&" + companyDateFontSize + "&\"MS ゴシック\" " + LocalDateTime.now().format(fullDateTimeFormatter) + "\npage &P ");
		
		Cells cells = sheet.getCells();
		Cell periodCell = cells.get(dateRow,0);
		
		YearMonth startMonth = query.getStartYearMonth();
		YearMonth endMonth = query.getEndYearMonth();
		
		StringBuilder builder = new StringBuilder();
		
		// A1_5 - update ver20
		String printStartMonth = startMonth.month() < 10 ? ("0" + String.valueOf(startMonth.month()))
				: String.valueOf(startMonth.month());
		String printEndMonth = endMonth.month() < 10 ? ("0" + String.valueOf(endMonth.month()))
				: String.valueOf(endMonth.month());
		builder.append(TextResource.localize("KWR006_66"));
		builder.append(startMonth.year() + "/" + printStartMonth);
		builder.append("　～　");
		builder.append(endMonth.year() + "/" + printEndMonth);
		
		periodCell.setValue(builder.toString());
	}
	
	/**
	 * Sets the fixed data.
	 *
	 * @param condition the condition
	 * @param outputMonthlySchedule the output monthly schedule
	 * @param sheet the sheet
	 * @param reportData the report data
	 * @param currentRow the current row
	 */
	private void setFixedData(MonthlyWorkScheduleCondition condition
			, OutputItemMonthlyWorkSchedule outputMonthlySchedule
			, Worksheet sheet
			, MonthlyPerformanceReportData reportData
			, int currentRow) {
		MonthlyPerformanceHeaderData headerData = reportData.getHeaderData();
		// Set fixed value
		Cells cells = sheet.getCells();
		// A2_1
		Cell dateCell = cells.get(currentRow, 0);
		dateCell.setValue(headerData.getFixedHeaderData().get(0));
		
		// A2_1
		Cell closureCell = cells.get(currentRow, 2);
		closureCell.setValue(headerData.getFixedHeaderData().get(1));
		
		// A2_4
		Cell remarkCell = cells.get(currentRow, outputMonthlySchedule.getTextSize() == TextSizeCommonEnum.BIG ? 35 : 43);
		remarkCell.setValue(headerData.getFixedHeaderData().get(2));
	}
	
	/**
	 * Write display map.
	 *
	 * @param cells the cells
	 * @param reportData the report data
	 * @param currentRow the current row
	 * @param nSize the n size
	 */
	private void writeDisplayMap(Cells cells, MonthlyPerformanceReportData reportData, int currentRow, int nSize, int chunkSize) {
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
            	cell.setValue(outputItem.getItemName());

            }
        }
	}
	
	/**
	 * Get all enabled level between child and its parent workplace.
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
	private List<Integer> findEnabledLevelBetweenWorkplaces(MonthlyWorkplaceData workplaceReportData, TotalWorkplaceHierachy hierarchy) {
		List<Integer> lstEnabledLevel = new ArrayList<>();
		
		if (workplaceReportData.getLevel() == 0)
			return lstEnabledLevel;
		
		MonthlyWorkplaceData parentWorkplace = workplaceReportData.getParent();
		for (int i = workplaceReportData.getLevel(); i > parentWorkplace.getLevel(); i--) {
			if (hierarchy.checkLevelEnabled(i))
				lstEnabledLevel.add(i);
		}
		return lstEnabledLevel;
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

	private Worksheet copySheet(WorksheetCollection wsc, WorkSheetInfo sheetInfo) throws Exception {
		wsc.addCopy(sheetInfo.getOriginSheetIndex());
		Worksheet ws = wsc.get(wsc.getCount() - 1);
		// sheet name
		sheetInfo.plusNewSheetIndex();
		
		String sheetName = StringUtils.isEmpty(sheetInfo.getSheetName())
				? WorkScheOutputConstants.SHEET_NAME + sheetInfo.getNewSheetIndex()
				: sheetInfo.getSheetName();
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

	private int printWorkplace(Cells cells, int currentRow, WorksheetCollection templateSheetCollection,
        WorkSheetInfo sheetInfo, WorkplaceReportData workplaceReportData, MonthlyWorkScheduleCondition condition,
        RowPageTracker rowPageTracker, int remarkColumn) throws Exception {
        if (condition.isShowWorkplace()) {
            Range workplaceRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_WORKPLACE_ROW);
            Range workplaceRange = cells.createRange(currentRow, 0, 1, remarkColumn);
            workplaceRange.copy(workplaceRangeTemp);
            rowPageTracker.useOneRowAndCheckResetRemainingRow(sheetInfo.getSheet(), currentRow);
            // A3_1
            Cell workplaceTagCell = cells.get(currentRow, 0);
            workplaceTagCell.setValue(TextResource.localize("KWR006_68")
                    + workplaceReportData.getWorkplaceCode() + "　" + workplaceReportData.getWorkplaceName());
            Style s = workplaceTagCell.getStyle();
            s.setBackgroundColor(Color.fromArgb(155, 194, 230));
            workplaceRange.setStyle(s);
            currentRow++;
        }
        return currentRow;
    }

    private int printPersonal(Cells cells, int currentRow, WorksheetCollection templateSheetCollection,
        WorkSheetInfo sheetInfo, EmployeeReportData employeeReportData, MonthlyWorkScheduleCondition condition,
        RowPageTracker rowPageTracker, int remarkColumn) throws Exception {
        if (condition.isShowPersonal()) {
            Range employeeRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_EMPLOYEE_ROW);
            Range employeeRange = cells.createRange(currentRow, 0, 1, remarkColumn);
            employeeRange.copy(employeeRangeTemp);
            rowPageTracker.useOneRowAndCheckResetRemainingRow(sheetInfo.getSheet(), currentRow);
            // A4_1
            Cell employeeTagCell = cells.get(currentRow, 0);
            employeeTagCell.setValue(TextResource.localize("KWR006_69")
            		+ employeeReportData.employeeCode																 // A4_2
                    + "　" + employeeReportData.employeeName																 // A4_3
                    + "　" + TextResource.localize("KWR006_70")									 						 // A4_4
                    + (employeeReportData.employmentCode == null ? "" : (employeeReportData.employmentCode)) // A4_5_1
                    + "　" + (employeeReportData.employmentName == null ? "" : (employeeReportData.employmentName + "　")) // A4_5_2
                    + TextResource.localize("KWR006_71")											 					 // A4_6	
                    + employeeReportData.jobTitleCode																 // A4_7_1
                    + "　" + employeeReportData.position);																 // A4_7_2
            Style s = employeeTagCell.getStyle();
            s.setBackgroundColor(Color.fromArgb(155, 194, 230));
            employeeRange.setStyle(s);
            currentRow++;
        }
        return currentRow;
    }

	private int printDateBracket(int currentRow, WorksheetCollection templateSheetCollection,
								 WorkSheetInfo sheetInfo, String dateTitle, int remarkColumn) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		Range dateRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_DATE_ROW);
		Range dateRange = cells.createRange(currentRow, 0, 1, remarkColumn);
		dateRange.copy(dateRangeTemp);
		//dateRange.setOutlineBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());

		// B3_1
		Cell dateTagCell = cells.get(currentRow, 0);
		dateTagCell.setValue(dateTitle);

//		// B3_2
//		int month = monthlyReportData.getYearMonth().month();
//		String date = monthlyReportData.getYearMonth().year() + "/" + (month < 10 ? "0" + month : month);
//		Cell dateCell = cells.get(currentRow, 2);
//		dateCell.setValue(date);

		currentRow++;
		return currentRow;
	}

	private int printWorkplace(int currentRow, WorksheetCollection templateSheetCollection,
							   WorkSheetInfo sheetInfo, String workplaceTitle, int remarkColumn) throws Exception {
		Cells cells = sheetInfo.getSheet().getCells();
		Range workplaceRangeTemp = templateSheetCollection.getRangeByName(WorkScheOutputConstants.RANGE_DAILY_WORKPLACE_ROW);
		Range workplaceRange = cells.createRange(currentRow, 0, 1, remarkColumn);
		workplaceRange.copy(workplaceRangeTemp);
		// B4_1
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
	
	private String formatAttendanceName(boolean isBig, String input) {
		// Limit input length
		input = StringUtils.left(input, isBig ? 15 : 12);
		// Split every 5 characters for big font size, 4 for small font size
		String[] arr = input.split(isBig ? "(?<=\\G.....)" : "(?<=\\G....)");
		return String.join("\n", arr);
	}
}