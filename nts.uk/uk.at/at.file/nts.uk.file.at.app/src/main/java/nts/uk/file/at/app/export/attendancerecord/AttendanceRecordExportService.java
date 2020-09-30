package nts.uk.file.at.app.export.attendancerecord;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BundledBusinessException;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.task.data.TaskDataSetter;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExport;
import nts.uk.ctx.at.function.dom.attendancerecord.export.AttendanceRecordExportRepository;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.AttendanceRecordExportSetting;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.AttendanceRecordExportSettingRepository;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ExportFontSize;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.ItemSelectionType;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.MonthlyConfirmedDisplay;
import nts.uk.ctx.at.function.dom.attendancerecord.export.setting.NameUseAtr;
import nts.uk.ctx.at.function.dom.attendancerecord.item.CalculateAttendanceRecord;
import nts.uk.ctx.at.function.dom.attendancerecord.item.CalculateAttendanceRecordRepositoty;
import nts.uk.ctx.at.function.dom.attendancerecord.item.SingleAttendanceRecordRepository;
import nts.uk.ctx.at.function.dom.attendancetype.AttendanceType;
import nts.uk.ctx.at.function.dom.attendancetype.AttendanceTypeRepository;
import nts.uk.ctx.at.function.dom.attendancetype.ScreenUseAtr;
import nts.uk.ctx.at.function.dom.dailyworkschedule.scrA.SEmpHistExportAdapter;
import nts.uk.ctx.at.function.dom.dailyworkschedule.scrA.SEmpHistExportImported;
import nts.uk.ctx.at.record.app.service.attendanceitem.value.AttendanceItemValueService;
import nts.uk.ctx.at.record.app.service.attendanceitem.value.AttendanceItemValueService.AttendanceItemValueResult;
import nts.uk.ctx.at.record.app.service.attendanceitem.value.AttendanceItemValueService.MonthlyAttendanceItemValueResult;
import nts.uk.ctx.at.record.dom.monthly.TimeOfMonthlyRepository;
import nts.uk.ctx.at.record.dom.monthly.affiliation.AffiliationInfoOfMonthly;
import nts.uk.ctx.at.record.dom.monthly.affiliation.AffiliationInfoOfMonthlyRepository;
import nts.uk.ctx.at.record.dom.workrecord.manageactualsituation.approval.monthly.MonthlyApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.managectualsituation.ApprovalStatus;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcessRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.shared.app.service.workrule.closure.ClosureEmploymentService;
import nts.uk.ctx.at.shared.dom.attendance.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingSystem;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.bs.company.dom.company.Company;
import nts.uk.ctx.bs.company.dom.company.CompanyRepository;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceConfigInfo;
import nts.uk.ctx.bs.employee.dom.workplace.config.info.WorkplaceHierarchy;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformation;
import nts.uk.ctx.bs.employee.dom.workplace.master.WorkplaceInformationRepository;
import nts.uk.ctx.bs.employee.pub.company.StatusOfEmployee;
import nts.uk.ctx.bs.employee.pub.company.SyCompanyPub;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportColumnData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportDailyData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportEmployeeData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportWeeklyData;
import nts.uk.file.at.app.export.attendancerecord.data.AttendanceRecordReportWeeklySumaryData;
import nts.uk.file.at.app.export.schedule.FileService;
import nts.uk.query.model.employee.EmployeeInformationRepository;
import nts.uk.query.pub.employee.EmployeeInformationExport;
import nts.uk.query.pub.employee.EmployeeInformationPub;
import nts.uk.query.pub.employee.EmployeeInformationQueryDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AttendanceRecordExportService extends ExportService<AttendanceRecordRequest> {

	final static long UPPER_POSITION = 1;
	final static long LOWER_POSITION = 2;
	final static int PDF_MODE = 1;
	
	final static String ZERO = "0";
	
	/** The Constant MASTER_UNREGISTERED. */
	private static final String MASTER_UNREGISTERED = " マスタ未登録";

	@Inject
	private ClosureEmploymentService closureEmploymentService;

	@Inject
	private SingleAttendanceRecordRepository singleAttendanceRepo;

	@Inject
	private CalculateAttendanceRecordRepositoty calculateAttendanceRepo;

	@Inject
	private AttendanceItemValueService attendanceService;

	@Inject
	private AttendanceRecordExportSettingRepository attendanceRecExpSetRepo;

	@Inject
	private AttendanceRecordExportRepository attendanceRecExpRepo;

	@Inject
	private AttendanceRecordReportGenerator reportGenerator;

	@Inject
	private CompanyRepository companyRepo;

	@Inject
	private EmployeeInformationPub employeePub;

	@Inject
	private WorkplaceInformationRepository wplConfigInfoRepo;

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeRepo;

	@Inject
	private AttendanceTypeRepository attendanceRepo;

	@Inject
	private WorkplaceAdapter workplaceAdapter;
	
	@Inject
	private ManagedParallelWithContext parallel;
	
	@Inject
	private FileService service;
	
	@Inject
	private SEmpHistExportAdapter sEmpHistExportAdapter;
	
	@Inject
	private MonthlyApprovalProcess monthlyApprovalProcess;
	
	@Inject
	private ApprovalProcessRepository approvalRepo;

	@Inject
	private SyCompanyPub symCompany;
	
	@Inject
	private EmployeeInformationRepository empInfoRepo;
	
	@Inject
	private AffiliationInfoOfMonthlyRepository affiliationInfoOfMonthlyRepository;

	@Override
	protected void handle(ExportServiceContext<AttendanceRecordRequest> context) {

		// get Dto
		AttendanceRecordRequest request = context.getQuery();
		AttendanceRecordOutputConditions condition = AttendanceRecordOutputConditions.createFromJavaType(request.getCondition());
		String companyId = AppContexts.user().companyId();
		String employeeId = AppContexts.user().employeeId();
		String layoutId = condition.getSelectionType() == ItemSelectionType.FREE_SETTING
				? condition.getFreeSettingLayoutId()
				: condition.getStandardSelectionLayoutId();
		Map<String, List<AttendanceRecordReportEmployeeData>> reportData = new LinkedHashMap<>();
		List<AttendanceRecordReportEmployeeData> attendanceRecRepEmpDataList = new ArrayList<AttendanceRecordReportEmployeeData>();
		
		//	get domain model by item selection
		Optional<AttendanceRecordExportSetting> optionalAttendanceRecExpSet = this.attendanceRecExpSetRepo.findByCode(condition.getSelectionType(), 
				companyId, Optional.of(employeeId), request.getLayout());
		//	取得できなかった
		if (!optionalAttendanceRecExpSet.isPresent()) {
			//	エラーメッセージ(#Msg_1141)を表示する
			throw new BusinessException("Msg_1141");
		}
		
		int columnDailyData;	
		int columnMonthlyData;
		if (optionalAttendanceRecExpSet.get().getExportFontSize().value == ExportFontSize.CHAR_SIZE_LARGE.value) {
			columnDailyData = 9;
			columnMonthlyData = 12;
		} else if (optionalAttendanceRecExpSet.get().getExportFontSize().value == ExportFontSize.CHAR_SIZE_MEDIUM.value) {
			columnDailyData = 11;
			columnMonthlyData = 14;
		} else {
			columnDailyData = 13;
			columnMonthlyData = 16;
		}
		
		BundledBusinessException exceptions = BundledBusinessException.newInstance();
		TaskDataSetter setter = context.getDataSetter();
		// Get layout info

		List<Employee> unknownEmployeeList = new ArrayList<>();
		List<Employee> nullDataEmployeeList = new ArrayList<>();
		List<Employee> employeeListAfterSort = new ArrayList<>();
		List<Employee> distinctEmployeeListAfterSort = new ArrayList<>();
		// Get workType info
		List<WorkType> workTypeList = workTypeRepo.findByCompanyId(companyId);

		// Get workTime info
		List<WorkTimeSetting> workTimeList = workTimeRepo.findByCompanyId(companyId);

		List<String> wplIds = new ArrayList<>();
		String invidual = "";
		List<String> empIDs = request.getEmployeeList().stream().map(e -> e.getEmployeeId()).collect(Collectors.toList());
		
		GeneralDate endByClosure = GeneralDate.ymd(request.getEndDate().year(), request.getEndDate().month(), request.getEndDate().lastDateInMonth());
		GeneralDate startTime = request.getStartDate().addMonths(-1);
		GeneralDate startByClosure = GeneralDate.ymd(startTime.year(), startTime.month(), 1);
		//remove warning
		//DatePeriod period = new DatePeriod(startByClosure, endByClosure);
		//	月２回締めがある場合でも対応できる
		Comparator<WkpHistImport> compareByEmployeeAndDate = Comparator.comparing(WkpHistImport::getEmployeeId);
		List<WkpHistImport> wkps = workplaceAdapter.findWkpBySid(empIDs, startByClosure).stream().sorted(compareByEmployeeAndDate).collect(Collectors.toList());
		// Get workplace history
		for (Employee e : request.getEmployeeList()) {
			WkpHistImport hist = wkps.stream().filter(w -> w.getEmployeeId().equals(e.getEmployeeId())).findFirst().orElse(null);
			if (hist == null) {
				unknownEmployeeList.add(e);
			} else {
				e.setWorkplaceId(hist.getWorkplaceId());
				e.setWorkplaceCode(hist.getWorkplaceCode());
				wplIds.add(hist.getWorkplaceId());
			}
		}	

		if (!wplIds.isEmpty()) {

			// Get Workplace config info
			List<WorkplaceConfigInfo> wplConfigInfoList = this.convertData(wplConfigInfoRepo
					.findByBaseDateWkpIds2(AppContexts.user().companyId(), wplIds, GeneralDate.localDate(LocalDate.now())));
			List<WorkplaceHierarchy> hierarchyList = new ArrayList<>();

			// Find heirarchy for each workplace
			wplIds.forEach(item -> {

				for (WorkplaceConfigInfo info : wplConfigInfoList) {
					for (WorkplaceHierarchy hiearachy : info.getLstWkpHierarchy()) {
						if (item.equals(hiearachy.getWorkplaceId()))
							hierarchyList.add(hiearachy);
					}
				}
			});

			// Sort by heirarchy - sort by workplace
			hierarchyList.sort(Comparator.comparing(WorkplaceHierarchy::getHierarchyCode));

			wplIds = hierarchyList.stream().map(item -> item.getWorkplaceId()).distinct().collect(Collectors.toList());

			List<Employee> employeeListSortByCD = request.getEmployeeList().stream()
					.sorted(Comparator.comparing(Employee::getEmployeeCode)).collect(Collectors.toList());
			// sort employee by heirarchy
			wplIds.forEach(id -> {
				for (Employee employee : employeeListSortByCD) {
					if (id.equals(employee.getWorkplaceId()))
						employeeListAfterSort.add(employee);
				}

			});
		}
		// unknown employee put at the end of list
		if (!unknownEmployeeList.isEmpty()) {
			employeeListAfterSort.addAll(unknownEmployeeList);
		}
		distinctEmployeeListAfterSort = employeeListAfterSort.stream().distinct().collect(Collectors.toList());

		List<Integer> attendanceItemList = new ArrayList<>();
		// get upper-daily-singleItem list
		List<Integer> singleIdUpper = this.singleAttendanceRepo.getIdSingleAttendanceRecordByPosition(layoutId, UPPER_POSITION);
		attendanceItemList.addAll(singleIdUpper);
		// get upper-daily-calculateItem list

		List<CalculateAttendanceRecord> calculateUpperDaily = this.calculateAttendanceRepo
				.getIdCalculateAttendanceRecordDailyByPosition(layoutId, UPPER_POSITION, optionalAttendanceRecExpSet.get().getExportFontSize().value);

		// get lower-daily-singleItem list
		List<Integer> singleIdLower = this.singleAttendanceRepo.getIdSingleAttendanceRecordByPosition(layoutId, LOWER_POSITION);

		attendanceItemList.addAll(singleIdLower);
		// get lower-daily-CalculateItem list

		List<CalculateAttendanceRecord> calculateLowerDaily = this.calculateAttendanceRepo
				.getIdCalculateAttendanceRecordDailyByPosition(layoutId, LOWER_POSITION, optionalAttendanceRecExpSet.get().getExportFontSize().value);

		// get upper-monthly-Item list
		List<CalculateAttendanceRecord> calculateUpperMonthly = this.calculateAttendanceRepo
				.getIdCalculateAttendanceRecordMonthlyByPosition(layoutId, UPPER_POSITION, optionalAttendanceRecExpSet.get().getExportFontSize().value);

		// get lower-monthly-Item list
		List<CalculateAttendanceRecord> calculateLowerMonthly = this.calculateAttendanceRepo
				.getIdCalculateAttendanceRecordMonthlyByPosition(layoutId, LOWER_POSITION, optionalAttendanceRecExpSet.get().getExportFontSize().value);

		List<ScreenUseAtr> screenUseAtrList = Arrays.asList(ScreenUseAtr.ATTENDANCE_TYPE_OF_DERVICETYPE,
				ScreenUseAtr.EMPLOYEE_BOOKING_HOURS);

		List<AttendanceType> attendanceTypeList = new ArrayList<>();
		List<EmployeeInformationExport> employeeInfoList = new ArrayList<>();
		Set<Integer> singleId = new HashSet<>();
		Set<Integer> monthlyId = new HashSet<>();

		// Get all Daily attendance item
		singleId.addAll(singleIdUpper);
		singleId.addAll(singleIdLower);
		Stream.concat(calculateUpperDaily.stream(), calculateLowerDaily.stream()).forEach(item -> {
			if (item.getAddedItem() != null)
				singleId.addAll(item.getAddedItem());
			if (item.getSubtractedItem() != null)
				singleId.addAll(item.getSubtractedItem());
		});
		
		// Get all Monthly Attendanceitem
		Stream.concat(calculateUpperMonthly.stream(), calculateLowerMonthly.stream()).forEach(item -> {
			if (item.getAddedItem() != null)
				monthlyId.addAll(item.getAddedItem());
			if (item.getSubtractedItem() != null)
				singleId.addAll(item.getSubtractedItem());
		});
		
		YearMonthPeriod periodMonthly = new YearMonthPeriod(request.getStartDate().yearMonth(), request.getEndDate().yearMonth());
		List<MonthlyAttendanceItemValueResult> monthlyValues;
		// 帳票用の基準日取得
		int closureId = request.getClosureId() == 0 ? 1 : request.getClosureId();

		Optional<GeneralDate> baseDate = service.getProcessingYM(companyId, closureId);
		if (!baseDate.isPresent()) {
			throw new BusinessException("Uchida bảo là lỗi hệ thống _ ThànhPV");
		}
		Map<String, DatePeriod> employeePeriod = service.getAffiliationDatePeriod(empIDs, periodMonthly, baseDate.get());

		//	「日別実績」を取得する
		List<AttendanceItemValueResult> dailyValues;
		{
			List<AttendanceItemValueResult> syncResultsDaily = Collections.synchronizedList(new ArrayList<>());
            List<MonthlyAttendanceItemValueResult> syncResultsMonthly = Collections.synchronizedList(new ArrayList<>());
			this.parallel.forEach(employeePeriod.entrySet(), emp -> {
				if (!singleId.isEmpty()){				
					syncResultsDaily.addAll(attendanceService.getValueOf(Arrays.asList(emp.getKey()), emp.getValue(), singleId));
				}
				if (!monthlyId.isEmpty()) {
                    syncResultsMonthly.addAll(attendanceService.getMonthlyValueOf(Arrays.asList(emp.getKey()), periodMonthly, monthlyId));
                }
			});
			dailyValues = new ArrayList<>(syncResultsDaily);
            monthlyValues =  new ArrayList<>(syncResultsMonthly);
		}

		Map<String, List<AttendanceItemValueResult>> dailyValuesAll = dailyValues.stream()
                .collect(Collectors.groupingBy(AttendanceItemValueResult::getEmployeeId));
        Map<String, List<MonthlyAttendanceItemValueResult>> monthlyValuesAll = monthlyValues.stream()
                .collect(Collectors.groupingBy(MonthlyAttendanceItemValueResult::getEmployeeId));

		List<String> sIds = distinctEmployeeListAfterSort.stream().map(x -> x.employeeId).collect(Collectors.toList());
		// get Closure
		Map<String, Closure> closureAll = closureEmploymentService.findClosureByEmployee(sIds, request.getEndDate());
		
		//	月の承認済を取得する
		// #111688
		List<MonthlyApprovalStatusAttendanceRecord> listMonthlyApproval = new ArrayList<MonthlyApprovalStatusAttendanceRecord>();
		Optional<ApprovalProcess> approvalProcOp = approvalRepo.getApprovalProcessById(companyId);
		monthlyValues.stream().forEach(i -> {
			ApprovalStatus approvalMonth = monthlyApprovalProcess.monthlyApprovalCheck(companyId, i.getEmployeeId(),
					i.getYearMonth().v(), i.getClosureId(), baseDate.get(), approvalProcOp, null);
			if (approvalMonth.equals(ApprovalStatus.APPROVAL)) {
				MonthlyApprovalStatusAttendanceRecord monthlyApproval = new MonthlyApprovalStatusAttendanceRecord();
				monthlyApproval.setYm(i.getYearMonth());
				monthlyApproval.setClosureId(i.getClosureId());
				monthlyApproval.setApprovedFlag(true);
				monthlyApproval.setEmployeeId(i.getEmployeeId());
				monthlyApproval.setYmd(i.getYearMonth());
				listMonthlyApproval.add(monthlyApproval);
			}
		});

		
		//	社員IDでループ - Loop by employee ID
		for (Employee employee : distinctEmployeeListAfterSort) {

			// Number of real data
			Integer realDataOfEmployee = 0;

			ClosureDate closureDate = new ClosureDate(1, false);
			if (closureAll.containsKey(employee.getEmployeeId())){
				// get Closure
				Closure closure = closureAll.get(employee.getEmployeeId());

				// get closure history
				List<ClosureHistory> closureHistory = closure.getClosureHistories();

				// find closure history
				for (ClosureHistory history : closureHistory) {
					if (history.getStartYearMonth().lessThanOrEqualTo(request.getEndDate().yearMonth())
							&& history.getEndYearMonth().greaterThanOrEqualTo(request.getEndDate().yearMonth())) {
						closureDate = history.getClosureDate();

					}
				}

				// check if closure is found

				if (closureDate.getClosureDay().v() != 0 || closureDate.getLastDayOfMonth()) {

					// Get start time - end time
					DatePeriod datePeriod = employeePeriod.get(employee.employeeId);
					
					YearMonth endYearMonth = datePeriod.end().yearMonth();
					YearMonth yearMonth = datePeriod.start().yearMonth();
					
					if (attendanceTypeList.isEmpty()) {
						attendanceTypeList.addAll(attendanceRepo.getItemByAtrandType(AppContexts.user().companyId(),
								screenUseAtrList, 1));
					}

					while (yearMonth.lessThanOrEqualTo(endYearMonth)) {

						Integer realData = 0;

						GeneralDate startDateByClosure;
						GeneralDate endDateByClosure;

						// Get start - end date to export
						if (closureDate.getLastDayOfMonth()) {
							startDateByClosure = GeneralDate.ymd(yearMonth.year(), yearMonth.month(), 1);
							endDateByClosure = GeneralDate.ymd(yearMonth.year(), yearMonth.month(),
									yearMonth.lastDateInMonth());
						} else {
							startDateByClosure = GeneralDate.ymd(yearMonth.year(), yearMonth.month(),
									closureDate.getClosureDay().v() + 1);
							endDateByClosure = GeneralDate.ymd(yearMonth.addMonths(1).year(),
									yearMonth.addMonths(1).month(), closureDate.getClosureDay().v());
						}
						
						// Get start and end date of month
						DatePeriod monthPeriod = new DatePeriod(startDateByClosure, endDateByClosure);

						//	月別実績の月初と月末の雇用コードをチェックする
						// #111678
						Optional<AffiliationInfoOfMonthly> oAffiliationInfoOfMonthly = this.affiliationInfoOfMonthlyRepository
								.find(employee.getEmployeeId(), yearMonth, ClosureId.valueOf(closureId), closureDate);
						if (oAffiliationInfoOfMonthly.isPresent()) {
							MonthlyResultCheck monthResultCheck = this.checkEmployeeCodeInMonth(
									employee.getEmployeeId(), monthPeriod,
									oAffiliationInfoOfMonthly.get().getFirstInfo().getEmploymentCd().v(),
									oAffiliationInfoOfMonthly.get().getLastInfo().getEmploymentCd().v());
							//	雇用取得結果：false
							if (!monthResultCheck.isEmployeeResult()) {
								// エラーリストに「社員コード」「社員名」を書き出す
								throw new BusinessException("Msg_1724",
										oAffiliationInfoOfMonthly.get().getEmployeeId(),
										oAffiliationInfoOfMonthly.get().getEmployeeId());
							}
							if (!monthResultCheck.isCheckResult()) {
//								// 雇用コードが一致しなかったのでこの月別実績は処理しない - This monthly performance will not be processed 
																			// as the employment code did not match
//								// →次の月別実績データの処理に移行(continue) - → Move to the next monthly performance data processing (continue)
//								yearMonth = yearMonth.addMonths(1);
//								continue;
							}
//							//	月別実績１ヶ月分の期間(YMD)と所属会社履歴の重複期間(YMD)を取得する
							//	社員の指定期間中の所属期間を取得する RequestList 588
							List<StatusOfEmployee> statusEmps = this.symCompany.GetListAffComHistByListSidAndPeriod(empIDs, monthPeriod);
							//
//							// (UK2)出勤簿を出力する
							if (statusEmps.isEmpty()) {
//								yearMonth = yearMonth.addMonths(1);
//								continue;
							}
						}

						// amount day in month
						int flag = 0;

						// List DailyData
						List<AttendanceRecordReportDailyData> dailyDataList = new ArrayList<>();

						// Weekly Data
						List<AttendanceRecordReportWeeklyData> weeklyDataList = new ArrayList<>();

						// Report by Month
						//	重複期間．開始年月日～重複期間．終了年月日のループ
						while (startDateByClosure.beforeOrEquals(endDateByClosure)) {
							flag++;
							List<AttendanceRecordResponse> upperDailyRespond = new ArrayList<>();
							List<AttendanceRecordResponse> lowerDailyRespond = new ArrayList<>();
							// return result upper-daily-singleItems
							AttendanceItemValueService.AttendanceItemValueResult valueSingleUpper = null;
							GeneralDate closureDateTemp = startDateByClosure;

							//	日別項目（単一項目・上段） - Daily items (single item, upper row)
							//	勤怠項目の実績値を取得し編集する - Acquire and edit the actual value of attendance items
							AttendanceItemValueResult itemValueResult = AttendanceItemValueResult.builder()
									.employeeId(null).workingDate(null).attendanceItems(new ArrayList<ItemValue>())
									.build();
							// Get all daily result in Date
							if (!singleIdUpper.isEmpty() || !singleIdLower.isEmpty()) {
                                if (dailyValuesAll.containsKey(employee.employeeId)){
                                    List<AttendanceItemValueResult> dailyValuesByEmp = dailyValuesAll.get(employee.employeeId);
                                    Optional<AttendanceItemValueResult> itemValueOtp = dailyValuesByEmp.stream().filter(x -> x.getWorkingDate().equals(closureDateTemp)).findFirst();
                                    if (itemValueOtp.isPresent()) {
                                        itemValueResult = itemValueOtp.get();
                                    }
                                }
							}

							// Fill in upper single item
							if (!singleIdUpper.isEmpty()) {
								valueSingleUpper = AttendanceItemValueResult.builder()
										.employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
										.attendanceItems(new ArrayList<ItemValue>()).build();
								for (Integer id : singleIdUpper) {
									ItemValue value = new ItemValue();
									for (ItemValue item : itemValueResult.getAttendanceItems()) {
										if (item.getItemId() == id) {
											if (item.getValue() != null && !ZERO.equals(item.getValue())
													&& !item.getValue().isEmpty()) {
												realData++;
											}
											value = item;
											break;
										}

									}
									valueSingleUpper.getAttendanceItems().add(value);
								}

							} else {
								valueSingleUpper = AttendanceItemValueResult.builder()
										.employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
										.attendanceItems(new ArrayList<ItemValue>()).build();
								for (int i = 1; i <= 6; i++) {
									valueSingleUpper.getAttendanceItems().add(new ItemValue());

								}
							}

							// convert data to show
							if (valueSingleUpper != null) {

								valueSingleUpper.getAttendanceItems().forEach(item -> {
									if (item != null)
										upperDailyRespond.add(new AttendanceRecordResponse(employee.getEmployeeId(),
												employee.getEmployeeName(), closureDateTemp, "",
												this.convertString(item, workTypeList, workTimeList, attendanceTypeList,
														optionalAttendanceRecExpSet.get().getNameUseAtr(), condition.getZeroDisplayType())));

								});
							}
							
							//	日別項目（算出項目・上段） - Daily items (calculation items, upper row)
							//	勤怠項目の実績値を取得し集計、編集する - Acquire, aggregate, and edit the actual value of attendance items
							// return result upper-daily-calculateItems
							for (CalculateAttendanceRecord item : calculateUpperDaily) {

								AttendanceItemValueService.AttendanceItemValueResult addValueCalUpper = AttendanceItemValueResult
										.builder().employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
										.attendanceItems(new ArrayList<ItemValue>()).build();
								// Fill in upper calculate daily ADD item
								if (item.getAddedItem() != null && !item.getAddedItem().isEmpty())
									for (Integer id : item.getAddedItem()) {
										for (ItemValue e : itemValueResult.getAttendanceItems()) {
											if (e.getItemId() == id) {
												if (e.getValue() != null && !ZERO.equals(e.getValue())
														&& !e.getValue().isEmpty()) {
													realData++;
												}
												addValueCalUpper.getAttendanceItems().add(e);
												break;
											}

										}

									}

								AttendanceItemValueService.AttendanceItemValueResult subValueCalUpper = AttendanceItemValueResult
										.builder().employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
										.attendanceItems(new ArrayList<ItemValue>()).build();
								// Fill in upper calculate daily SUBTRACT item
								if (item.getSubtractedItem() != null && !item.getSubtractedItem().isEmpty())
									for (Integer id : item.getSubtractedItem()) {

										for (ItemValue e : itemValueResult.getAttendanceItems()) {
											if (e.getItemId() == id) {
												if (e.getValue() != null && !ZERO.equals(e.getValue())
														&& !e.getValue().isEmpty()) {
													realData++;
												}
												subValueCalUpper.getAttendanceItems().add(e);
												break;
											}

										}

									}

								// get result upper calculate
								String result = "";
								if (!addValueCalUpper.getAttendanceItems().isEmpty()
										|| !subValueCalUpper.getAttendanceItems().isEmpty()) {
									result = this.getSumCalculateAttendanceItem(addValueCalUpper.getAttendanceItems(),
											subValueCalUpper.getAttendanceItems());
								}
								upperDailyRespond.add(new AttendanceRecordResponse(employee.getEmployeeId(),
										employee.getEmployeeName(), closureDateTemp, "", result));

							}

							//	日別項目（単一項目・下段） - Daily items (single item / lower)
							//	勤怠項目の実績値を取得し編集する - Acquire and edit the actual value of attendance items
							// return result lower-daily-singleItems
							AttendanceItemValueService.AttendanceItemValueResult valueSingleLower = AttendanceItemValueResult
									.builder().employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
									.attendanceItems(new ArrayList<ItemValue>()).build();
							// Fill in lower single item
							if (!singleIdLower.isEmpty()) {

								valueSingleLower = AttendanceItemValueResult.builder()
										.employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
										.attendanceItems(new ArrayList<ItemValue>()).build();
								for (Integer id : singleIdLower) {
									ItemValue value = new ItemValue();
									for (ItemValue item : itemValueResult.getAttendanceItems()) {
										if (item.getItemId() == id) {
											if (item.getValue() != null && !ZERO.equals(item.getValue())
													&& !item.getValue().isEmpty()) {
												realData++;
											}
											value = item;
											break;
										}

									}
									valueSingleLower.getAttendanceItems().add(value);
								}

							} else {
								valueSingleLower = AttendanceItemValueResult.builder()
										.employeeId(employee.getEmployeeId()).workingDate(startDateByClosure)
										.attendanceItems(new ArrayList<ItemValue>()).build();
								for (int i = 1; i <= 6; i++) {
									valueSingleLower.getAttendanceItems().add(new ItemValue());

								}
							}
							// convert data to show
							if (valueSingleLower != null)
								valueSingleLower.getAttendanceItems().forEach(item -> {
									if (item != null)
										lowerDailyRespond.add(new AttendanceRecordResponse(employee.getEmployeeId(),
												employee.getEmployeeName(), closureDateTemp, "",
												this.convertString(item, workTypeList, workTimeList, attendanceTypeList,
														optionalAttendanceRecExpSet.get().getNameUseAtr(), condition.getZeroDisplayType())));

								});
							//	日別項目（算出項目・下段） - Daily items (calculation items, lower row)
							//	勤怠項目の実績値を取得し集計、編集する - Acquire, aggregate, and edit the actual value of attendance items
							for (CalculateAttendanceRecord item : calculateLowerDaily) {

								AttendanceItemValueService.AttendanceItemValueResult addValueCalUpper = AttendanceItemValueResult
										.builder().attendanceItems(new ArrayList<>()).build();
								// Fill in lower calculate daily ADD item
								if (item.getAddedItem() != null && !item.getAddedItem().isEmpty())
									for (Integer id : item.getAddedItem()) {
										for (ItemValue e : itemValueResult.getAttendanceItems()) {
											if (e.getItemId() == id) {
												if (e.getValue() != null && !ZERO.equals(e.getValue())
														&& !e.getValue().isEmpty()) {
													realData++;
												}
												addValueCalUpper.getAttendanceItems().add(e);
												break;
											}

										}
									}

								AttendanceItemValueService.AttendanceItemValueResult subValueCalUpper = AttendanceItemValueResult
										.builder().attendanceItems(new ArrayList<>()).build();
								// Fill in lower calculate daily SUBTRACT item
								if (item.getSubtractedItem() != null && !item.getSubtractedItem().isEmpty())
									for (Integer id : item.getSubtractedItem()) {
										for (ItemValue e : itemValueResult.getAttendanceItems()) {
											if (e.getItemId() == id) {
												if (e.getValue() != null && !ZERO.equals(e.getValue())
														&& !e.getValue().isEmpty()) {
													realData++;
												}
												subValueCalUpper.getAttendanceItems().add(e);
												break;
											}

										}

									}

								// get result lower calculate daily
								String result = "";
								if (!addValueCalUpper.getAttendanceItems().isEmpty()
										|| !subValueCalUpper.getAttendanceItems().isEmpty()) {
									result = this.getSumCalculateAttendanceItem(addValueCalUpper.getAttendanceItems(),
											subValueCalUpper.getAttendanceItems());
								}
								lowerDailyRespond.add(new AttendanceRecordResponse(employee.getEmployeeId(),
										employee.getEmployeeName(), closureDateTemp, "", result));
							}

							AttendanceRecordReportDailyData dailyData = new AttendanceRecordReportDailyData();
							// Set data daily
							dailyData.setDate(String.valueOf(startDateByClosure.day()));
							dailyData.setDayOfWeek(DayOfWeekJP
									.getValue(startDateByClosure.localDate().getDayOfWeek().toString()).japanese);
							AttendanceRecordReportColumnData[] columnDatasArray = new AttendanceRecordReportColumnData[columnDailyData];
							int index = 0;
							for (AttendanceRecordResponse item : upperDailyRespond) {
								columnDatasArray[index] = new AttendanceRecordReportColumnData("", "");
								if (item.getValue() != null)
									columnDatasArray[index].setUper(item.getValue());
								index++;
							}
							index = 0;
							for (AttendanceRecordResponse item : lowerDailyRespond) {
								if (item.getValue() != null)
									columnDatasArray[index].setLower(item.getValue());
								index++;

							}
							List<AttendanceRecordReportColumnData> columnDatas = new ArrayList<>();
							for (int i = 0; i < columnDatasArray.length; i++) {
								if (columnDatasArray[i] == null)
									columnDatasArray[i] = new AttendanceRecordReportColumnData("", "");
								columnDatas.add(columnDatasArray[i]);
							}
							//	日別項目（算出項目・上段下段） - Daily items (calculation items, upper and lower)
							//	週単位の集計をする - Aggregate on a weekly basis

							dailyData.setColumnDatas(columnDatas);
							dailyData.setSecondCol(flag <= 15 ? false : true);
							dailyDataList.add(dailyData);
							// Check end of week
							if (startDateByClosure.localDate().getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
								AttendanceRecordReportWeeklyData weeklyData = new AttendanceRecordReportWeeklyData();
								// Set weekly data
								weeklyData.setDailyDatas(dailyDataList);
								// Set total result in week
								AttendanceRecordReportWeeklySumaryData summaryWeeklyData = new AttendanceRecordReportWeeklySumaryData();
								summaryWeeklyData = this.getSumWeeklyValue(dailyDataList, optionalAttendanceRecExpSet.get().getExportFontSize().value);

								weeklyData.setWeeklySumaryData(summaryWeeklyData);

								weeklyDataList.add(weeklyData);
								// empty daily data list
								dailyDataList = new ArrayList<>();

							}
							// Next day
							startDateByClosure = startDateByClosure.addDays(1);

						}
						//	日別項目（算出項目・上段下段） -  Daily items (calculation items, upper and lower)
						//	週単位集計値の編集をする - Edit weekly aggregated values 
						// Day of the last week in month
						if (dailyDataList.size() > 0) {
							AttendanceRecordReportWeeklyData weeklyData = new AttendanceRecordReportWeeklyData();
							weeklyData.setDailyDatas(dailyDataList);
							AttendanceRecordReportWeeklySumaryData summaryWeeklyData = new AttendanceRecordReportWeeklySumaryData();
							summaryWeeklyData = this.getSumWeeklyValue(dailyDataList, optionalAttendanceRecExpSet.get().getExportFontSize().value);

							weeklyData.setWeeklySumaryData(summaryWeeklyData);

							weeklyDataList.add(weeklyData);
							// empty daily data list
							dailyDataList = new ArrayList<>();
						}

						//	月別項目（算出項目・上段） - Monthly items (calculation items, upper row)
						//	勤怠項目の実績値を取得し集計、編集する - Acquire, aggregate, and edit the actual value of attendance items
						List<String> upperResult = new ArrayList<>();
						List<String> lowerResult = new ArrayList<>();
						AttendanceItemValueService.MonthlyAttendanceItemValueResult itemValueResult = MonthlyAttendanceItemValueResult
								.builder().attendanceItems(new ArrayList<>()).build();

						if (!calculateUpperMonthly.isEmpty() || !calculateLowerMonthly.isEmpty()) {

							// Get montnly result
                            if (monthlyValuesAll.containsKey(employee.getEmployeeId())) {
                                List<MonthlyAttendanceItemValueResult> monthlyValuesByEmp = monthlyValuesAll.get(employee.getEmployeeId());
                                for (MonthlyAttendanceItemValueResult item : monthlyValuesByEmp) {
                                    if (item.getYearMonth()
                                            .equals(closureDate.getLastDayOfMonth() ? yearMonth : yearMonth.addMonths(1))
                                            && item.getClouseDate() == closureDate.getClosureDay().v()) {
                                        itemValueResult = item;
                                        break;
                                    }
                                }
                            }
						}

						for (CalculateAttendanceRecord item : calculateUpperMonthly) {
							AttendanceItemValueService.MonthlyAttendanceItemValueResult monthlyUpperAddResult = MonthlyAttendanceItemValueResult
									.builder().attendanceItems(new ArrayList<>()).build();
							AttendanceItemValueService.MonthlyAttendanceItemValueResult monthlyUpperSubResult = MonthlyAttendanceItemValueResult
									.builder().attendanceItems(new ArrayList<>()).build();

							// Fill in upper calculate monthly ADD item
							if (item.getAddedItem() != null && !item.getAddedItem().isEmpty()) {
								for (Integer id : item.getAddedItem()) {
									for (ItemValue e : itemValueResult.getAttendanceItems()) {
										if (id == e.getItemId()) {
											if (e.getValue() != null && !ZERO.equals(e.getValue())
													&& !e.getValue().isEmpty()) {
												realData++;
											}
											monthlyUpperAddResult.getAttendanceItems().add(e);
											break;
										}

									}

								}
							}

							// Fill in upper calculate monthly SUBTRACT item
							if (item.getSubtractedItem() != null && !item.getSubtractedItem().isEmpty()) {
								for (Integer id : item.getSubtractedItem()) {
									for (ItemValue e : itemValueResult.getAttendanceItems()) {
										if (id == e.getItemId()) {
											if (e.getValue() != null && !ZERO.equals(e.getValue())
													&& !e.getValue().isEmpty()) {
												realData++;
											}
											monthlyUpperSubResult.getAttendanceItems().add(e);
											break;
										}

									}

								}
							}

							// Get upper monthly result
							String result = "";
							if (!monthlyUpperAddResult.getAttendanceItems().isEmpty()
									|| !monthlyUpperSubResult.getAttendanceItems().isEmpty()) {
								result = this.getSumCalculateAttendanceItem(monthlyUpperAddResult.getAttendanceItems(),
										monthlyUpperSubResult.getAttendanceItems());
							}
							upperResult.add(result);

						}

						//	月別項目（算出項目・下段） -  Monthly items (calculation items, lower row)
						//	勤怠項目の実績値を取得し集計、編集する - Acquire, aggregate, and edit the actual value of attendance items
						for (CalculateAttendanceRecord item : calculateLowerMonthly) {
							AttendanceItemValueService.MonthlyAttendanceItemValueResult monthlyLowerAddResult = MonthlyAttendanceItemValueResult
									.builder().attendanceItems(new ArrayList<>()).build();

							AttendanceItemValueService.MonthlyAttendanceItemValueResult monthlyLowerSubResult = MonthlyAttendanceItemValueResult
									.builder().attendanceItems(new ArrayList<>()).build();
							// Fill in lower calculate monthly ADD item
							if (item.getAddedItem() != null && !item.getAddedItem().isEmpty()) {
								for (Integer id : item.getAddedItem()) {
									for (ItemValue e : itemValueResult.getAttendanceItems()) {
										if (id == e.getItemId()) {
											if (e.getValue() != null && !ZERO.equals(e.getValue())
													&& !e.getValue().isEmpty()) {
												realData++;
											}
											monthlyLowerAddResult.getAttendanceItems().add(e);
											break;
										}

									}

								}
							}

							// Fill in lower calculate monthly SUBTRACT item
							if (item.getSubtractedItem() != null && !item.getSubtractedItem().isEmpty()) {
								for (Integer id : item.getSubtractedItem()) {
									for (ItemValue e : itemValueResult.getAttendanceItems()) {
										if (id == e.getItemId()) {
											if (e.getValue() != null && !ZERO.equals(e.getValue())
													&& !e.getValue().isEmpty()) {
												realData++;
											}
											monthlyLowerSubResult.getAttendanceItems().add(e);
											break;
										}

									}

								}
							}

							// Get lower monthly result
							String result = new String("");
							if (!monthlyLowerAddResult.getAttendanceItems().isEmpty()
									|| !monthlyLowerSubResult.getAttendanceItems().isEmpty()) {
								result = this.getSumCalculateAttendanceItem(monthlyLowerAddResult.getAttendanceItems(),
										monthlyLowerSubResult.getAttendanceItems());
							}
							lowerResult.add(result);

						}					

						// Convert to AttendanceRecordReportColumnData
						List<AttendanceRecordReportColumnData> employeeMonthlyData = new ArrayList<>();

						AttendanceRecordReportColumnData[] columnDataMonthlyArray = new AttendanceRecordReportColumnData[columnMonthlyData];
						int index = 0;
						for (String item : upperResult) {
							columnDataMonthlyArray[index] = new AttendanceRecordReportColumnData("", "");
							if (item != null)
								columnDataMonthlyArray[index].setUper(item);
							index++;
						}
						index = 0;
						for (String item : lowerResult) {
							if (item != null) {
								if (columnDataMonthlyArray[index] == null) {
									columnDataMonthlyArray[index] = new AttendanceRecordReportColumnData("", "");
								}
								columnDataMonthlyArray[index].setLower(item);
							}
							index++;

						}

						for (int i = 0; i < columnDataMonthlyArray.length; i++) {
							if (columnDataMonthlyArray[i] == null)
								columnDataMonthlyArray[i] = new AttendanceRecordReportColumnData("", "");
							employeeMonthlyData.add(columnDataMonthlyArray[i]);
						}

						if (realData > 0) {
							// Get AttendanceRecordReportEmployeeData
							AttendanceRecordReportEmployeeData attendanceRecRepEmpData = new AttendanceRecordReportEmployeeData();

							attendanceRecRepEmpData.setEmployeeMonthlyData(employeeMonthlyData);
							attendanceRecRepEmpData.setWeeklyDatas(weeklyDataList);
							YearMonth yearMonthExport = closureDate.getLastDayOfMonth() ? yearMonth
									: yearMonth.addMonths(1);
							attendanceRecRepEmpData.setReportYearMonth(yearMonthExport.toString());

							/**
							 * Need information
							 * 
							 * The invidual. The workplace. The employment The
							 * title. The work type The year month
							 **/
							//	月次確認済表示区分をチェックする - Check the monthly confirmed display category
							if (optionalAttendanceRecExpSet.get()
									.getMonthlyConfirmedDisplay() == MonthlyConfirmedDisplay.DISPLAY) {
								//	表示  - if display 
								//	月の承認済状況を編集する - Edit the approved status of the month
								
							}

							// build param

							//	アルゴリズム「社員情報を返す」を実行する  - Execute the algorithm "Return employee information"
							// Param  出力対象社員ID（List) - Output target employee ID (List),   基準日 - Reference date
							// Get Employee information
							if(employeeInfoList.isEmpty()){
								EmployeeInformationQueryDto param = EmployeeInformationQueryDto.builder()
										.employeeIds(empIDs)
										.referenceDate(endByClosure)
										.toGetWorkplace(true)
										.toGetDepartment(false)
										.toGetPosition(true)
										.toGetEmployment(true)
										.toGetClassification(false)
										.toGetEmploymentCls(true).build();
								employeeInfoList = employeePub.find(param);
							}
							EmployeeInformationExport result = employeeInfoList.stream()
																				.filter(e -> e.getEmployeeId().equals(employee.getEmployeeId()))
																				.findFirst().get();

							attendanceRecRepEmpData
									.setEmployment(result.getEmployment().getEmploymentName().toString());
							attendanceRecRepEmpData
									.setInvidual(employee.getEmployeeCode() + " " + employee.getEmployeeName());
							attendanceRecRepEmpData.setTitle(result.getPosition() == null ? ""
									: result.getPosition().getPositionName().toString());
							attendanceRecRepEmpData.setWorkplace(result.getWorkplace() == null ? ""
									: result.getWorkplace().getWorkplaceName().toString());
							attendanceRecRepEmpData.setWorkType(result.getEmploymentCls() == null ? ""
									: TextResource.localize(EnumAdaptor.valueOf(result.getEmploymentCls(),
											WorkingSystem.class).nameId));
							attendanceRecRepEmpData
									.setYearMonth(yearMonthExport.year() + "/" + yearMonthExport.month());
							attendanceRecRepEmpDataList.add(attendanceRecRepEmpData);
							realDataOfEmployee++;
						}
						// Next monthly
						yearMonth = yearMonth.addMonths(1);

					}

				} else {
					// If closure not found
					String info = "\n " + employee.employeeCode + " " + employee.employeeName;
					if (info.length() + invidual.length() > 164) {
						if (!invidual.contains("\n..."))
							invidual = invidual.concat("\n...");
					} else {
						invidual = invidual.concat(info);
					}

				}

			} else {

				//	エラーリストに社員が入っているか判別する - Determine if an employee is in the error list
				// TODO
				
				// If closure is wrong
				String info = "\n " + employee.employeeCode + " " + employee.employeeName;
				if (info.length() + invidual.length() > 164) {
					if (!invidual.contains("\n..."))
						invidual = invidual.concat("\n...");
				} else {
					invidual = invidual.concat(info);
				}

			}

			if (realDataOfEmployee == 0) {
				nullDataEmployeeList.add(employee);
			}
			
			List<AttendanceRecordReportEmployeeData> attendanceRecRepEmpDataByMonthList = new ArrayList<>();
			for (AttendanceRecordReportEmployeeData item : attendanceRecRepEmpDataList) {

				if (this.getCodeFromInvidual(item.getInvidual()).equals(employee.getEmployeeCode().trim())) {
					attendanceRecRepEmpDataByMonthList.add(item);
				}

			}
			// Fill in export Data of employee
			reportData.put(employee.getEmployeeCode().trim(), attendanceRecRepEmpDataByMonthList);
		}

		// set invidual to client
		if (!invidual.isEmpty()) {
			setter.setData("invidual", invidual);
		}
		if (distinctEmployeeListAfterSort.size() <= nullDataEmployeeList.size()) {
			// If real data of employee isn't exist
			if (!invidual.isEmpty()) {
				exceptions.addMessage("Msg_1269", invidual);
			} else {
				exceptions.addMessage("Msg_37");
			}
			exceptions.throwExceptions();

		} else {
			distinctEmployeeListAfterSort.removeAll(nullDataEmployeeList);

		}

		// get seal stamp
		List<String> sealStamp = attendanceRecExpSetRepo.getSealStamp(companyId, layoutId);

		// Get daily header info
		List<AttendanceRecordExport> dailyRecord = attendanceRecExpRepo.getAllAttendanceRecordExportDaily(layoutId);
		List<AttendanceRecordExport> dailyRecordTotal = new ArrayList<>();

		for (int i = 1; i <= columnDailyData; i++) {
			if (this.findIndexInList(i, dailyRecord) == null) {
				AttendanceRecordExport item = new AttendanceRecordExport();
				item.setLowerPosition(null);
				item.setUpperPosition(null);
				dailyRecordTotal.add(item);
			} else {
				dailyRecordTotal.add(this.findIndexInList(i, dailyRecord));
			}
		}

		// get monthly header info
		List<AttendanceRecordExport> monthlyRecord = attendanceRecExpRepo.getAllAttendanceRecordExportMonthly(layoutId);
		List<AttendanceRecordExport> monthlyRecordTotal = new ArrayList<>();
		for (int i = 1; i <= columnMonthlyData; i++) {
			if (this.findIndexInList(i, monthlyRecord) == null) {
				AttendanceRecordExport item = new AttendanceRecordExport();
				item.setLowerPosition(null);
				item.setUpperPosition(null);
				monthlyRecordTotal.add(item);
			} else {
				monthlyRecordTotal.add(this.findIndexInList(i, monthlyRecord));
			}
		}

		// get header
		List<AttendanceRecordReportColumnData> dailyHeader = new ArrayList<AttendanceRecordReportColumnData>();
		List<AttendanceRecordReportColumnData> monthlyHeader = new ArrayList<AttendanceRecordReportColumnData>();

		for (AttendanceRecordExport item : dailyRecordTotal) {

			String upperheader = "";
			String lowerheader = "";
			if (item.getUpperPosition() != null && item.getUpperPosition().isPresent())
				upperheader = item.getUpperPosition().get().getNameDisplay();
			if (item.getLowerPosition() != null && item.getLowerPosition().isPresent())
				lowerheader = item.getLowerPosition().get().getNameDisplay();
			AttendanceRecordReportColumnData temp = (new AttendanceRecordReportColumnData(upperheader, lowerheader));
			dailyHeader.add(temp);

		}

		for (AttendanceRecordExport item : monthlyRecordTotal) {

			String upperheader = "";
			String lowerheader = "";
			if (item.getUpperPosition() != null && item.getUpperPosition().isPresent())
				upperheader = item.getUpperPosition().get().getNameDisplay();
			if (item.getLowerPosition() != null && item.getLowerPosition().isPresent())
				lowerheader = item.getLowerPosition().get().getNameDisplay();
			monthlyHeader.add(new AttendanceRecordReportColumnData(upperheader, lowerheader));
		}

		// check error List
		if (!exceptions.cloneExceptions().isEmpty()) {
			throw exceptions;
		}

		// Get info is showed on template
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		LocalDateTime presentDate = LocalDateTime.now();
		String exportDate = presentDate.format(format).toString();

		
		//	取得できた実績データがある社員の件数をチェック - Check the number of employees who have acquired performance data
		AttendanceRecordReportData recordReportData = new AttendanceRecordReportData();
		
		//	印鑑欄を生成する - Generate a seal column
		// get seal stamp
		recordReportData.setSealColName(
				optionalAttendanceRecExpSet.get().getSealUseAtr() ? sealStamp : new ArrayList<String>());
		
		//	日別項目ヘッダー部の名称を生成する - Generate the name of the daily item header part
		recordReportData.setDailyHeader(dailyHeader);
		
		//	月別項目ヘッダー部の名称を生成する - Generate the name of the monthly item header part
		recordReportData.setMonthlyHeader(monthlyHeader);
		
		//	会社名を生成する - Generate company name
		Optional<Company> optionalCompany = companyRepo.find(companyId);
		recordReportData.setCompanyName(optionalCompany.get().getCompanyName().toString());
		
		//	帳票タイトルを生成する - Generate form title
		recordReportData.setReportData(reportData);
		
		//	印刷年月日時分を生成する	- Generate print date, time, and minute
		recordReportData.setExportDateTime(exportDate);
		
		
		
		//	生成した出勤簿のPDFデータをダウンロードする OR 生成した出勤簿のEXCELデータをダウンロードする
		recordReportData.setReportName(optionalAttendanceRecExpSet.get().getName().v());
		
		recordReportData.setFontSize(optionalAttendanceRecExpSet.get().getExportFontSize().value);

		AttendanceRecordReportDatasource recordReportDataSource = new AttendanceRecordReportDatasource(recordReportData,
				request.getMode());

		// Generate file
		reportGenerator.generate(context.getGeneratorContext(), recordReportDataSource);

	}

	/**
	 * Gets the number from string.
	 *
	 * @param string
	 *            the string
	 * @return the number from string
	 */
	Integer getNumberFromString(String string) {

		String result = string.replaceAll("\\D+", "");
		if (!result.equals(""))
			return Integer.parseInt(result);
		return 0;
	}

	/**
	 * Gets the period from string.
	 *
	 * @param list
	 *            the list
	 * @return the period from string
	 */
	String getPeriodFromString(List<ItemValue> list) {
		for (ItemValue i : list) {
			if (i.value() != null) {
				String tmp = this.getNumberFromString(i.value().toString()).toString();
				int index = i.value().toString().indexOf(tmp);
				return i.value().toString().substring(index);
			}
		}
		return null;

	}

	/**
	 * Gets the name from invidual.
	 *
	 * @param invidual
	 *            the invidual
	 * @return the name from invidual
	 */
	String getNameFromInvidual(String invidual) {
		int index = invidual.indexOf(" ");
		return invidual.substring(index + 1).trim();
	}

	/**
	 * Gets the code from invidual.
	 *
	 * @param invidual
	 *            the invidual
	 * @return the code from invidual
	 */
	String getCodeFromInvidual(String invidual) {
		int index = invidual.indexOf(" ");
		return invidual.substring(0, index + 1).trim();
	}

	/**
	 * Gets the sum calculate attendance item.
	 *
	 * @param addValueCalUpper
	 *            the add value cal upper
	 * @param subValueCalUpper
	 *            the sub value cal upper
	 * @return the sum calculate attendance item
	 */
	String getSumCalculateAttendanceItem(List<ItemValue> addValueCalUpper, List<ItemValue> subValueCalUpper) {

		Double sum = new Double(0);
		if (!addValueCalUpper.isEmpty()
				&& (addValueCalUpper.get(0).getValueType().isInteger()
						|| addValueCalUpper.get(0).getValueType().isDouble())
				|| !subValueCalUpper.isEmpty() && (subValueCalUpper.get(0).getValueType().isInteger()
						|| subValueCalUpper.get(0).getValueType().isDouble())) {

			// calculate add
			if (!addValueCalUpper.isEmpty()) {
				for (ItemValue i : addValueCalUpper) {
					if (i.getValue() != null && !i.getValue().isEmpty())
						sum = Double.parseDouble(sum.toString()) + Double.parseDouble(i.value().toString());
				}
			}
			// calculate sub
			if (!subValueCalUpper.isEmpty()) {
				for (ItemValue i : subValueCalUpper) {
					if (i.getValue() != null && !i.getValue().isEmpty())
						sum = Double.parseDouble(sum.toString()) - Double.parseDouble(i.value().toString());
				}
			}
			if (sum.equals(0.0))
				return "";

			Integer sumInt;
			List<ItemValue> list = new ArrayList<>();
			if (!addValueCalUpper.isEmpty()) {
				list.addAll(addValueCalUpper);
			} else {
				list.addAll(subValueCalUpper);
			}
			switch (list.get(0).getValueType().value) {

			case 1:
			case 2:

				sumInt = sum.intValue();
				return this.convertMinutesToHours(sumInt.toString());
			case 7:
			case 8:
				sumInt = sum.intValue();
				return sumInt.toString() + " 回";
			case 13:
				sumInt = sum.intValue();
				DecimalFormat format = new DecimalFormat("###,###,###");
				return format.format(sum.intValue());
			default:
				break;

			}

		}
		return sum.toString();
	}

	/**
	 * Gets the sum weekly value.
	 *
	 * @param list
	 *            the list
	 * @return the sum weekly value
	 */
	public AttendanceRecordReportWeeklySumaryData getSumWeeklyValue(List<AttendanceRecordReportDailyData> list, int fontSize) {
		AttendanceRecordReportWeeklySumaryData result = new AttendanceRecordReportWeeklySumaryData();

		String upperValue7th = "";
		String lowerValue7th = "";
		String upperValue8th = "";
		String lowerValue8th = "";
		String upperValue9th = "";
		String lowerValue9th = "";
		String upperValue10th = "";
		String lowerValue10th = "";
		String upperValue11th = "";
		String lowerValue11th = "";
		String upperValue12th = "";
		String lowerValue12th = "";
		String upperValue13th = "";
		String lowerValue13th = "";

		for (AttendanceRecordReportDailyData item : list) {
			if (item.getColumnDatas().get(6) != null) {
				upperValue7th = this.add(upperValue7th, item.getColumnDatas().get(6).getUper());
				lowerValue7th = this.add(lowerValue7th, item.getColumnDatas().get(6).getLower());
			}
			if (item.getColumnDatas().get(7) != null) {
				upperValue8th = this.add(upperValue8th, item.getColumnDatas().get(7).getUper());
				lowerValue8th = this.add(lowerValue8th, item.getColumnDatas().get(7).getLower());
			}
			if (item.getColumnDatas().get(8) != null) {
				upperValue9th = this.add(upperValue9th, item.getColumnDatas().get(8).getUper());
				lowerValue9th = this.add(lowerValue9th, item.getColumnDatas().get(8).getLower());
			}
			if (fontSize == ExportFontSize.CHAR_SIZE_MEDIUM.value || fontSize == ExportFontSize.CHARS_SIZE_SMALL.value) { 
				if (item.getColumnDatas().get(9) != null) {
					upperValue9th = this.add(upperValue10th, item.getColumnDatas().get(9).getUper());
					lowerValue9th = this.add(lowerValue10th, item.getColumnDatas().get(9).getLower());
				}
				if (item.getColumnDatas().get(10) != null) {
					upperValue9th = this.add(upperValue11th, item.getColumnDatas().get(10).getUper());
					lowerValue9th = this.add(lowerValue11th, item.getColumnDatas().get(10).getLower());
				}
			}
			if (fontSize == ExportFontSize.CHARS_SIZE_SMALL.value) { 
				if (item.getColumnDatas().get(11) != null) {
					upperValue9th = this.add(upperValue12th, item.getColumnDatas().get(11).getUper());
					lowerValue9th = this.add(lowerValue12th, item.getColumnDatas().get(11).getLower());
				}
				if (item.getColumnDatas().get(12) != null) {
					upperValue9th = this.add(upperValue13th, item.getColumnDatas().get(12).getUper());
					lowerValue9th = this.add(lowerValue13th, item.getColumnDatas().get(12).getLower());
				}
			}

		}
		List<AttendanceRecordReportColumnData> columnDatas = new ArrayList<>();
		AttendanceRecordReportColumnData columnData7 = new AttendanceRecordReportColumnData(upperValue7th,
				lowerValue7th);
		columnDatas.add(columnData7);
		AttendanceRecordReportColumnData columnData8 = new AttendanceRecordReportColumnData(upperValue8th,
				lowerValue8th);
		columnDatas.add(columnData8);
		AttendanceRecordReportColumnData columnData9 = new AttendanceRecordReportColumnData(upperValue9th,
				lowerValue9th);
		columnDatas.add(columnData9);
		if (fontSize == ExportFontSize.CHAR_SIZE_MEDIUM.value || fontSize == ExportFontSize.CHARS_SIZE_SMALL.value) {
			AttendanceRecordReportColumnData columnData10 = new AttendanceRecordReportColumnData(upperValue10th,
					lowerValue10th);
			columnDatas.add(columnData10);
			AttendanceRecordReportColumnData columnData11 = new AttendanceRecordReportColumnData(upperValue11th,
					lowerValue11th);
			columnDatas.add(columnData11);
		}
		if (fontSize == ExportFontSize.CHARS_SIZE_SMALL.value) {
			AttendanceRecordReportColumnData columnData12 = new AttendanceRecordReportColumnData(upperValue12th,
					lowerValue12th);
			columnDatas.add(columnData12);
			AttendanceRecordReportColumnData columnData13 = new AttendanceRecordReportColumnData(upperValue13th,
					lowerValue13th);
			columnDatas.add(columnData13);
		}

		result.setDateRange(list.get(0).getDate() + "-" + list.get(list.size() - 1).getDate());
		result.setColumnDatas(columnDatas);
		result.setSecondCol(list.get(list.size() - 1).isSecondCol());

		return result;

	}

	/**
	 * Adds the.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the string
	 */
	public String add(String a, String b) {
		if (a.equals(""))
			return b;
		if (b.equals(""))
			return a;
		int indexA = a.indexOf(":");
		int indexB = b.indexOf(":");

		int subtrA = a.indexOf("-");
		int subtrB = b.indexOf("-");

		if (indexA >= 0 && indexB >= 0) {
			Integer hourA;
			Integer hourB;
			Integer minuteA;
			Integer minuteB;
			hourA = Integer.parseInt(a.substring(0, indexA));
			if (subtrA == 0) {
				minuteA = Integer.parseInt(a.substring(indexA + 1)) * (-1);
			} else {
				minuteA = Integer.parseInt(a.substring(indexA + 1));
			}
			hourB = Integer.parseInt(b.substring(0, indexB));
			if (subtrB == 0) {
				minuteB = Integer.parseInt(b.substring(indexB + 1)) * (-1);
			} else {

				minuteB = Integer.parseInt(b.substring(indexB + 1));
			}

			Integer totalMinute = (hourA * 60 + minuteA) + (hourB * 60 + minuteB);

			return this.convertMinutesToHours(totalMinute.toString());
		} else {
			indexA = a.indexOf("回");
			indexB = b.indexOf("回");

			if (indexA >= 0 && indexB >= 0) {
				Integer countA = Integer.parseInt(a.substring(0, indexA - 1));
				Integer countB = Integer.parseInt(b.substring(0, indexB - 1));

				Integer totalCount = countA + countB;

				return totalCount + " 回";
			} else {
				String stringAmountA = a.replaceAll(",", "");
				String stringAmountB = b.replaceAll(",", "");

				Integer amountA = Integer.parseInt(stringAmountA.toString());
				Integer amountB = Integer.parseInt(stringAmountB.toString());

				Integer totalAmount = amountA + amountB;
				DecimalFormat format = new DecimalFormat("###,###,###");
				return format.format(totalAmount);

			}

		}

	}

	/**
	 * Convert string.
	 *
	 * @param item
	 *            the item
	 * @param workTypeList
	 *            the work type list
	 * @param workTimeSettingList
	 *            the work time setting list
	 * @param screenUseAtrList
	 *            the screen use atr list
	 * @param nameUseAtr
	 *            the name use atr
	 * @return the string
	 */
	private String convertString(ItemValue item, List<WorkType> workTypeList, List<WorkTimeSetting> workTimeSettingList,
			List<AttendanceType> attendanceTypeList, NameUseAtr nameUseAtr, ZeroDisplayType zeroDisplayType) {
		final String value = item.getValue();
		if (item.getValueType() == null || item.getValue() == null)
			return "";
		switch (item.getValueType()) {

		case TIME:
		case CLOCK:
		case TIME_WITH_DAY:

			if (Integer.parseInt(item.getValue()) == 0 || item.getValue().isEmpty())
				return zeroDisplayType == ZeroDisplayType.DISPLAY ? item.getValue() : "";
			return this.convertMinutesToHours(value.toString());
		case COUNT:
		case COUNT_WITH_DECIMAL:
			if (Integer.parseInt(item.getValue()) == 0 || item.getValue().isEmpty())
				return zeroDisplayType == ZeroDisplayType.DISPLAY ? item.getValue() : "";
			return value.toString() + " 回";
		case AMOUNT:
			if (Integer.parseInt(item.getValue()) == 0 || item.getValue().isEmpty())
				return zeroDisplayType == ZeroDisplayType.DISPLAY ? item.getValue() : "";
			DecimalFormat format = new DecimalFormat("###,###,###");
			return format.format(Integer.parseInt(value));

		case CODE:

			if (!attendanceTypeList.isEmpty()) {
				AttendanceType attendance = attendanceTypeList.stream()
						.filter(e -> e.getAttendanceItemId() == item.getItemId()).collect(Collectors.toList()).get(0);
				if (attendance.getScreenUseAtr().equals(ScreenUseAtr.ATTENDANCE_TYPE_OF_DERVICETYPE)) {
					List<WorkType> worktype = workTypeList.stream()
							.filter(ite -> ite.getWorkTypeCode().v().equals(value)).collect(Collectors.toList());
					if (!worktype.isEmpty())

						return nameUseAtr.equals(NameUseAtr.FORMAL_NAME) ? worktype.get(0).getName().v()
								: worktype.get(0).getAbbreviationName().v();
					return value + MASTER_UNREGISTERED ;
				} else {

					List<WorkTimeSetting> workTime = workTimeSettingList.stream()
							.filter(e -> e.getWorktimeCode().v().equals(value)).collect(Collectors.toList());
					if (!workTime.isEmpty())
						return nameUseAtr.equals(NameUseAtr.FORMAL_NAME)
								? workTime.get(0).getWorkTimeDisplayName().getWorkTimeName().v()
								: workTime.get(0).getWorkTimeDisplayName().getWorkTimeAbName().v();
					return value + MASTER_UNREGISTERED;
				}
			}
			return value + MASTER_UNREGISTERED;

		default:
			return value;

		}
	}

	/**
	 * Find index in list.
	 *
	 * @param i
	 *            the i
	 * @param list
	 *            the list
	 * @return the attendance record export
	 */
	private AttendanceRecordExport findIndexInList(int i, List<AttendanceRecordExport> list) {
		for (AttendanceRecordExport item : list) {
			if (item.getColumnIndex() == i)
				return item;
		}

		return null;
	}

	/**
	 * Convert minutes to hours.
	 *
	 * @param minutes
	 *            the minutes
	 * @return the string
	 */
	private String convertMinutesToHours(String minutes) {
		if (minutes.equals("0") || minutes.equals("")) {
			return "0:00";
		}
		String FORMAT = "%d:%02d";
		Integer minuteInt = Integer.parseInt(minutes);
		if (minuteInt < 0) {
			minuteInt *= -1;
			Integer hourInt = minuteInt / 60;
			minuteInt = minuteInt % 60;
			return "-" + String.format(FORMAT, hourInt, minuteInt);
		} else {
			Integer hourInt = minuteInt / 60;
			minuteInt = minuteInt % 60;

			return String.format(FORMAT, hourInt, minuteInt);
		}
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
	
	
	/**
	 *  月別実績の月初と月末の雇用コードをチェックする
	 *
	 * @param employeeId 社員ID(SID)
	 * @param period 期間(開始年月日(START_YMD)、終了年月日(END_YMD))
	 * @param firstEmpCode 月初雇用コード(FIRST_EMP_CD)
	 * @param lastEmpCode 月末雇用コード(LAST_EMP_CD)
	 * @return the monthly result check
	 */
	private MonthlyResultCheck checkEmployeeCodeInMonth(String employeeId, DatePeriod period, String firstEmpCode, String lastEmpCode) {
		String companyId = AppContexts.user().companyId();
		MonthlyResultCheck monthlyResultCheck = new MonthlyResultCheck();

		//	雇用取得結果　←　true (OK), チェック結果 　　←　true (OK)
		monthlyResultCheck.setEmployeeResult(true);
		monthlyResultCheck.setCheckResult(true);

		// Imported「（就業）所属雇用履歴」を取得する (Lấy Imported「（就業）所属雇用履歴」)
		Optional<SEmpHistExportImported> empStartDate = sEmpHistExportAdapter.getSEmpHistExport(companyId, employeeId,
				period.start());
		//	存在しない場合
		if (!empStartDate.isPresent()) {
			// 	雇用取得結果　←　flase (NG) - チェック結果 　　←　true (OK)
			monthlyResultCheck.setEmployeeResult(false);
			monthlyResultCheck.setCheckResult(true);
			return monthlyResultCheck;
		}

		// Imported「（就業）所属雇用履歴」を取得する (Lấy Imported「（就業）所属雇用履歴」)
		Optional<SEmpHistExportImported> empEndDate = sEmpHistExportAdapter.getSEmpHistExport(companyId, employeeId,
				period.end());
		
		//	存在しない場合
		if (!empEndDate.isPresent()) {
			// 	雇用取得結果　←　flase (NG) - チェック結果 　　←　true (OK)
			monthlyResultCheck.setEmployeeResult(false);
			monthlyResultCheck.setCheckResult(true);
			return monthlyResultCheck;
		}
		// 	パラメータ「月初雇用コード」と取得した現在の月初雇用コードが同じ
		// 	パラメータ「月末雇用コード」と取得した現在の月末雇用コードが同じ
		if (!firstEmpCode.equals(empStartDate.get().getEmploymentCode())
			|| !lastEmpCode.equals(empEndDate.get().getEmploymentCode())) {
			monthlyResultCheck.setEmployeeResult(true);
			monthlyResultCheck.setCheckResult(false);
			return monthlyResultCheck;
		}
		return monthlyResultCheck;
	}
}
