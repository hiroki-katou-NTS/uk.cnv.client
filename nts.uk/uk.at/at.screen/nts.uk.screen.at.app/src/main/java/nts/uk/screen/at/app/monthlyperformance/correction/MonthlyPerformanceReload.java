package nts.uk.screen.at.app.monthlyperformance.correction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.IdentityProcessDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.IdentityProcessFinder;
import nts.uk.ctx.at.record.dom.adapter.company.AffCompanyHistImport;
import nts.uk.ctx.at.record.dom.adapter.company.SyCompanyRecordAdapter;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.enums.ApprovalStatusForEmployee;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffAtWorkplaceImport;
import nts.uk.ctx.at.record.dom.adapter.workplace.affiliate.AffWorkplaceAdapter;
import nts.uk.ctx.at.record.dom.approvalmanagement.ApprovalProcessingUseSetting;
import nts.uk.ctx.at.record.dom.approvalmanagement.repository.ApprovalProcessingUseSettingRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.LockStatus;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.approvalstatusmonthly.ApprovalStatusMonth;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.approvalstatusmonthly.ApprovalStatusMonthly;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.approvalstatusmonthly.ApprovalStatusResult;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.AvailabilityAtr;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.ConfirmStatusMonthly;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.ConfirmStatusResult;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.MonthlyModifyResultDto;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.MonthlyPerformaceLockStatus;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.ReleasedAtr;
import nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly.StatusConfirmMonthDto;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecord;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.ErrorAlarmWorkRecordRepository;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.Identification;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentificationRepository;
import nts.uk.ctx.at.record.dom.workrecord.managectualsituation.AcquireActualStatus;
import nts.uk.ctx.at.record.dom.workrecord.managectualsituation.ApprovalStatus;
import nts.uk.ctx.at.record.dom.workrecord.managectualsituation.EmploymentFixedStatus;
import nts.uk.ctx.at.record.dom.workrecord.managectualsituation.MonthlyActualSituationOutput;
import nts.uk.ctx.at.record.dom.workrecord.managectualsituation.MonthlyActualSituationStatus;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.ApprovalProcessRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcess;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.IdentityProcessRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFunRepository;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.ControlOfMonthlyDto;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.ControlOfMonthlyFinder;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.MonthlyItemControlByAuthDto;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.MonthlyItemControlByAuthFinder;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobTitleHisImport;
import nts.uk.ctx.at.shared.dom.adapter.jobtitle.SharedAffJobtitleHisAdapter;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemAtr;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.erroralarm.EmployeeDailyPerError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeNameType;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.screen.at.app.monthlyperformance.CheckDailyPerError;
import nts.uk.screen.at.app.monthlyperformance.CheckEmpEralOuput;
import nts.uk.screen.at.app.monthlyperformance.TypeErrorAlarm;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.ActualTime;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.ActualTimeState;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.EditStateOfMonthlyPerformanceDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPCellDataDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPCellStateDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPDataDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPHeaderDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPSateCellHideControl;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPText;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyAttendanceItemDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceCorrectionDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceEmployeeDto;
import nts.uk.screen.at.app.monthlyperformance.correction.param.MonthlyPerformanceParam;
import nts.uk.screen.at.app.monthlyperformance.correction.param.PAttendanceItem;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyQueryProcessor;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyResult;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

@Stateless
public class MonthlyPerformanceReload {

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private MonthlyModifyQueryProcessor monthlyModifyQueryProcessor;

	@Inject
	private AffWorkplaceAdapter affWorkplaceAdapter;

	@Inject
	private MonthlyActualSituationStatus monthlyActualStatus;

	@Inject
	private MonthlyPerformanceCheck monthlyCheck;

	@Inject
	private MonthlyPerformanceScreenRepo repo;
	
	@Inject
	private SyCompanyRecordAdapter syCompanyRecordAdapter;
	
	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;
	
	private static final String STATE_DISABLE = "mgrid-disable";
	private static final String HAND_CORRECTION_MYSELF = "mgrid-manual-edit-target";
	private static final String HAND_CORRECTION_OTHER = "mgrid-manual-edit-other";
//	private static final String REFLECT_APPLICATION = "ntsgrid-reflect";
	private static final String STATE_ERROR = "mgrid-error";
	private static final String STATE_ALARM = "mgrid-alarm";
	private static final String STATE_SPECIAL = "mgrid-special";
	private static final String ADD_CHARACTER = "A";
//	private static final String DATE_FORMAT = "yyyy-MM-dd";

	@Inject
	private MonPerformanceFunRepository monPerformanceFunRepository;
	
	@Inject
	private IdentityProcessFinder identityProcessFinder;
	
	@Inject
	private ApprovalProcessingUseSettingRepository approvalProcessingUseSettingRepo;
	
//	@Inject
//	private ApprovalStatusAdapter approvalStatusAdapter;
	
	@Inject
	private AttendanceTimeOfMonthlyRepository attendanceTimeOfMonthlyRepo;
	
//	@Inject
//	private ConfirmationMonthRepository confirmationMonthRepository;

	@Inject
	private ControlOfMonthlyFinder controlOfMonthlyFinder;
	
	@Inject
	private SharedAffJobtitleHisAdapter affJobTitleAdapter;
	
	@Inject
	private IdentityProcessRepository identityProcessRepo;
	
	@Inject
	private IdentificationRepository identificationRepository;
	
	@Inject 
	private EmployeeDailyPerErrorRepository employeeDailyPerErrorRepo;
	
	@Inject 
	private ApprovalProcessRepository approvalRepo;
	
	@Inject
	MonthlyItemControlByAuthFinder monthlyItemControlByAuthFinder;
	
	@Inject
	private ApprovalStatusMonthly approvalStatusMonthly;

	@Inject
	private ConfirmStatusMonthly confirmStatusMonthly;
	
	@Inject
	private ErrorAlarmWorkRecordRepository errorAlarmWorkRecordRepository;

	@Inject
	private CheckDailyPerError checkDailyPerError;
	
	@Inject
	private IFindDataDCRecord iFindDataDCRecord;
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	
	public MonthlyPerformanceCorrectionDto reloadScreen(MonthlyPerformanceParam param) {

		String companyId = AppContexts.user().companyId();

		MonthlyPerformanceCorrectionDto screenDto = new MonthlyPerformanceCorrectionDto();
		screenDto.setClosureId(param.getClosureId());
		screenDto.setProcessDate(param.getYearMonth());
		// ドメインモデル「月別実績の修正の機能」を取得する
		Optional<MonPerformanceFun> monPerformanceFun = monPerformanceFunRepository.getMonPerformanceFunById(companyId);
		// ドメインモデル「本人確認処理の利用設定」を取得する
		IdentityProcessDto identityProcess = identityProcessFinder.getAllIdentityProcessById(companyId);
		//アルゴリズム「承認処理の利用設定を取得する」を実行する
		Optional<ApprovalProcessingUseSetting> optApprovalProcessingUseSetting = this.approvalProcessingUseSettingRepo.findByCompanyId(companyId);
		
		// Comment
		if (monPerformanceFun.isPresent()) {
			screenDto.setComment(monPerformanceFun.get().getComment().v());
			screenDto.setDailySelfChkDispAtr(monPerformanceFun.get().getDailySelfChkDispAtr());
		}
		screenDto.setIdentityProcess(identityProcess);
		this.displayClosure(screenDto, companyId, param.getClosureId(), param.getYearMonth());
		screenDto.setSelectedActualTime(param.getActualTime());
		List<String> employeeIds = param.getLstEmployees().stream().map(e -> e.getId())
				.collect(Collectors.toList());
		
		List<Integer> itemIds = new ArrayList<>(param.getLstAtdItemUnique().keySet());
		MonthlyItemControlByAuthDto monthlyItemAuthDto = monthlyItemControlByAuthFinder
				.getMonthlyItemControlByToUse(AppContexts.user().companyId(), AppContexts.user().roles().forAttendance(), itemIds, 1);
		// 取得したドメインモデル「権限別月次項目制御」でパラメータ「表示する項目一覧」をしぼり込む
		// Filter param 「表示する項目一覧」 by domain 「権限別月次項目制御」
		// set quyen chinh sua item theo user
		screenDto.setAuthDto(monthlyItemAuthDto);
		
		//指定した年月の期間を算出する
		DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
				ClosureService.createRequireM1(closureRepository, closureEmploymentRepo),
				screenDto.getClosureId(), new YearMonth(screenDto.getProcessDate()));
		//社員ID（List）と指定期間から所属会社履歴項目を取得
		// RequestList211
		List<AffCompanyHistImport> lstAffComHist = syCompanyRecordAdapter
				.getAffCompanyHistByEmployee(employeeIds, datePeriodClosure);
		screenDto.setLstAffComHist(lstAffComHist);
		
		List<String> listEmployeeIds = param.getLstEmployees().stream().map(x -> x.getId())
				.collect(Collectors.toList());
		List<MonthlyModifyResult> results = new ArrayList<>();
		List<Integer> attdanceIds = param.getLstAtdItemUnique().keySet().stream()
				.collect(Collectors.toList());
		results = new GetDataMonthly(listEmployeeIds, new YearMonth(param.getYearMonth()), ClosureId.valueOf(param.getClosureId()),
				screenDto.getClosureDate().toDomain(), attdanceIds, monthlyModifyQueryProcessor).call();
		if (results.size() > 0) {
			screenDto.getItemValues().addAll(results.get(0).getItems());
		}
		Map<String, MonthlyModifyResult> employeeDataMap = results.stream()
				.collect(Collectors.toMap(x -> x.getEmployeeId(), Function.identity(), (x, y) -> x));

		List<MonthlyModifyResultDto> monthlyResults = results.stream()
				.map(m -> new MonthlyModifyResultDto(m.getItems(), m.getYearMonth(), m.getEmployeeId(),
						m.getClosureId(), m.getClosureDate().toDomain(), m.getWorkDatePeriod()))
				.collect(Collectors.toList());
		
		// アルゴリズム「ロック状態をチェックする」を実行する - set lock
		List<MonthlyPerformaceLockStatus> lstLockStatus = checkLockStatus(companyId, employeeIds, param.getYearMonth(),
				param.getClosureId(),
				new DatePeriod(param.getActualTime().getStartDate(), param.getActualTime().getEndDate()),
				param.getInitScreenMode(), lstAffComHist, monthlyResults);		
		param.setLstLockStatus(lstLockStatus);
		
		// lay lai lock status vi khong the gui tu client len duoc
		screenDto.setParam(param);
		screenDto.setLstEmployee(param.getLstEmployees());

		// アルゴリズム「月別実績を表示する」を実行する(Hiển thị monthly actual result)
		displayMonthlyResult(screenDto, param.getYearMonth(), param.getClosureId(),
				optApprovalProcessingUseSetting.get(), companyId, monthlyResults, listEmployeeIds, attdanceIds,
				employeeDataMap);
		// set trang thai disable theo quyen chinh sua item
		screenDto.createAccessModifierCellState();
		return screenDto;
	}

	private void displayClosure(MonthlyPerformanceCorrectionDto screenDto, String companyId, Integer closureId,
			Integer processYM) {
		// アルゴリズム「締めの名称を取得する」を実行する
		Optional<Closure> closure = closureRepository.findById(companyId, closureId);
		if (!closure.isPresent()) {
			return;
		}
		Optional<ClosureHistory> closureHis = closure.get().getHistoryByYearMonth(YearMonth.of(processYM));
		if (closureHis.isPresent()) {
			// 締め名称 → 画面項目「A4_2：対象締め日」
			screenDto.setClosureName(closureHis.get().getClosureName().v());
			screenDto.setClosureDate(ClosureDateDto.from(closureHis.get().getClosureDate()));
		}
	}

	/**
	 * 月別実績を表示する
	 */
	private void displayMonthlyResult(MonthlyPerformanceCorrectionDto screenDto, Integer yearMonth, Integer closureId,
			ApprovalProcessingUseSetting approvalProcessingUseSetting, String companyId,
			List<MonthlyModifyResultDto> monthlyResults, List<String> listEmployeeIds, List<Integer> attdanceIds,
			Map<String, MonthlyModifyResult> employeeDataMap) {
		/**
		 * Create Grid Sheet DTO
		 */

		MonthlyPerformanceParam param = screenDto.getParam();
		
		String loginId = AppContexts.user().employeeId();
		List<MonthlyPerformaceLockStatus> performanceLockStatus = screenDto.getParam().getLstLockStatus();
		// アルゴリズム「対象年月に対応する月別実績を取得する」を実行する Lấy monthly result ứng với năm tháng
		if (param.getLstAtdItemUnique() == null || param.getLstAtdItemUnique().isEmpty()) {
			throw new BusinessException("Msg_1261");
		}

		List<MPHeaderDto> lstMPHeaderDto = MPHeaderDto.GenerateFixedHeader();

		// G7 G8 G9 hidden column identitfy, approval, dailyconfirm
		for (Iterator<MPHeaderDto> iter = lstMPHeaderDto.listIterator(); iter.hasNext();) {
			MPHeaderDto mpHeaderDto = iter.next();
			if ("identify".equals(mpHeaderDto.getKey()) && screenDto.getIdentityProcess().getUseMonthSelfCK() == 0) {
				iter.remove();
				continue;
			}
			if ("approval".equals(mpHeaderDto.getKey())
					&& approvalProcessingUseSetting.getUseMonthApproverConfirm() == false) {
				iter.remove();
				continue;
			}
			if ("dailyconfirm".equals(mpHeaderDto.getKey()) && screenDto.getDailySelfChkDispAtr() == 0) {
				iter.remove();
				continue;
			}
		}

		/**
		 * Create Header DTO
		 */
		List<MPHeaderDto> lstHeader = new ArrayList<>();
		lstHeader.addAll(lstMPHeaderDto);
		if (param.getLstAtdItemUnique() != null) {
			List<Integer> itemIds = param.getLstAtdItemUnique().keySet().stream().collect(Collectors.toList());
			List<MonthlyAttendanceItemDto> lstAttendanceItem = repo.findByAttendanceItemId(companyId, itemIds);
			Map<Integer, MonthlyAttendanceItemDto> mapMP = lstAttendanceItem.stream().filter(x -> x.getMonthlyAttendanceAtr() != MonthlyAttendanceItemAtr.REFER_TO_MASTER.value).collect(Collectors.toMap(MonthlyAttendanceItemDto::getAttendanceItemId, x -> x));
			List<ControlOfMonthlyDto> listCtrOfMonthlyDto = controlOfMonthlyFinder
					.getListControlOfAttendanceItem(itemIds);
			for (Integer key : param.getLstAtdItemUnique().keySet()) {
				PAttendanceItem item = param.getLstAtdItemUnique().get(key);
				MonthlyAttendanceItemDto dto = mapMP.get(key);
				// ドメインモデル「月次の勤怠項目の制御」を取得する
				// Setting Header color & time input
				Optional<ControlOfMonthlyDto> ctrOfMonthlyDto = listCtrOfMonthlyDto.stream()
						.filter(c -> c.getItemMonthlyId() == item.getId()).findFirst();
				lstHeader.add(MPHeaderDto.createSimpleHeader(item,
						ctrOfMonthlyDto.isPresent() ? ctrOfMonthlyDto.get() : null, dto));
			}
		}

		/**
		 * Get Data
		 */
		iFindDataDCRecord.clearAllStateless();
		//[No.586]月の実績の確認状況を取得する
		Optional<StatusConfirmMonthDto> statusConfirmMonthDto = confirmStatusMonthly.getConfirmStatusMonthly(companyId, listEmployeeIds, YearMonth.of(yearMonth), closureId, false);

		//[No.587]月の実績の承認状況を取得する
		Optional<ApprovalStatusMonth> approvalStatusMonth =  approvalStatusMonthly.getApprovalStatusMonthly(companyId, loginId, closureId, listEmployeeIds, YearMonth.of(yearMonth),monthlyResults, false);
		iFindDataDCRecord.clearAllStateless();
		List<MPDataDto> lstData = new ArrayList<>(); // List all data
		List<MPCellStateDto> lstCellState = new ArrayList<>(); // List cell state
		screenDto.setLstData(lstData);
		screenDto.setLstCellState(lstCellState);
		Map<String, MonthlyPerformaceLockStatus> lockStatusMap = param.getLstLockStatus().stream()
				.collect(Collectors.toMap(x -> x.getEmployeeId(), Function.identity(), (x, y) -> x));
		String employeeIdLogin = AppContexts.user().employeeId();

		List<EditStateOfMonthlyPerformanceDto> editStateOfMonthlyPerformanceDtos = this.repo
				.findEditStateOfMonthlyPer(new YearMonth(screenDto.getProcessDate()), listEmployeeIds, attdanceIds);

		List<MPSateCellHideControl> mPSateCellHideControls = new ArrayList<>();
		
		// get all code-name
		CodeNameType workplaceStartCN = dataDialogWithTypeProcessor.getWorkPlace(companyId, screenDto.getSelectedActualTime().getStartDate());
		CodeNameType workplaceEndCN = dataDialogWithTypeProcessor.getWorkPlace(companyId, screenDto.getSelectedActualTime().getEndDate());
		CodeNameType positionStartCN = dataDialogWithTypeProcessor.getPossition(companyId, screenDto.getSelectedActualTime().getStartDate());
		CodeNameType positionEndCN = dataDialogWithTypeProcessor.getPossition(companyId, screenDto.getSelectedActualTime().getEndDate());
		CodeNameType classificationCN = dataDialogWithTypeProcessor.getClassification(companyId);
		CodeNameType employmentCN = dataDialogWithTypeProcessor.getEmployment(companyId);
		CodeNameType businessTypeCN = dataDialogWithTypeProcessor.getBussinessType(companyId);
		
		for (int i = 0; i < param.getLstEmployees().size(); i++) {
			MonthlyPerformanceEmployeeDto employee = param.getLstEmployees().get(i);
			String employeeId = employee.getId();
			MonthlyModifyResult rowData = employeeDataMap.get(employeeId);
			if (rowData == null) continue;
			
			String lockStatus = lockStatusMap.isEmpty() || !lockStatusMap.containsKey(employee.getId()) || param.getInitMenuMode() == 1 ? ""
					: lockStatusMap.get(employee.getId()).getLockStatusString();

			// set dailyConfirm
			MonthlyPerformaceLockStatus monthlyPerformaceLockStatus = lockStatusMap.get(employeeId);
			String dailyConfirm = null;
			List<String> listCss = new ArrayList<>();
			listCss.add("daily-confirm-color");
			if (monthlyPerformaceLockStatus != null) {
				if (monthlyPerformaceLockStatus.getMonthlyResultConfirm() == LockStatus.LOCK.value) {
					dailyConfirm = "未";
					// mau cua kiban chua dap ung duoc nen dang tu set mau
					// set color for cell dailyConfirm
					listCss.add("color-cell-un-approved");
					screenDto.setListStateCell("dailyconfirm", employeeId, listCss);
				} else {
					dailyConfirm = "済";
					// mau cua kiban chua dap ung duoc nen dang tu set mau
					// set color for cell dailyConfirm
					listCss.add("color-cell-approved");
					screenDto.setListStateCell("dailyconfirm", employeeId, listCss);
				}
			}

			// check true false identify
//			boolean identify = listConfirmationMonth.stream().filter(x -> x.getEmployeeId().equals(employeeId))
//					.findFirst().isPresent();
			boolean identify = false;
			// check true false approve
			boolean approve = false;
			
			boolean hasDataApproval = false, disabled = false;
			// set state approval
			if (param.getInitMenuMode() == 2) { // mode approve disable cot approve theo data lay duoc tu no.534
				if(approvalStatusMonth.isPresent()) {
					for (ApprovalStatusResult approvalStatusResult : approvalStatusMonth.get().getApprovalStatusResult()) {
						// *7 set value approval mode 2
						if(approvalStatusResult.getEmployeeId().equals(employee.getId())) {
							hasDataApproval = true;
							approve = approvalStatusResult.isApprovalStatus();
							// *5 check disable mode approval 
							if(!approve) {
								if(approvalStatusResult.getImplementaPropriety() == AvailabilityAtr.CAN_NOT_RELEASE) {
									lstCellState.add(new MPCellStateDto(employeeId, "approval", Arrays.asList(STATE_DISABLE)));
									disabled = true;
								}
							}else {
								if(approvalStatusResult.getWhetherToRelease() == ReleasedAtr.CAN_NOT_RELEASE) {
									lstCellState.add(new MPCellStateDto(employeeId, "approval", Arrays.asList(STATE_DISABLE)));
									disabled = true;
								}
							}
							break;
						}
						
					}
					
					if (!disabled) {
						performanceLockStatus.stream().filter(p -> p.getEmployeeId().equals(employee.getId()) && (p.getPastPerformaceLock() == LockStatus.LOCK.value || p.getMonthlyResultLock() == LockStatus.LOCK.value)).forEach(p -> {
							lstCellState.add(new MPCellStateDto(employeeId, "approval", Arrays.asList(STATE_DISABLE)));
						});
					}
				}
			}else {
				lstCellState.add(new MPCellStateDto(employeeId, "approval", Arrays.asList(STATE_DISABLE)));
				if(approvalStatusMonth.isPresent()) {
					for (ApprovalStatusResult approvalStatusResult : approvalStatusMonth.get().getApprovalStatusResult()) {
						//*6 : set value approval mode 0,1
						if(approvalStatusResult.getEmployeeId().equals(employee.getId())) {
							hasDataApproval = true;
							if(approvalStatusResult.getNormalStatus() == ApprovalStatusForEmployee.UNAPPROVED) {
								approve = false;
							}else {
								approve = true;
							}
							break;
						}
					}
				}//end for
			}
			
			if(!hasDataApproval) {
				mPSateCellHideControls.add(new MPSateCellHideControl(employee.getId(), "approval"));
				lstCellState.add(new MPCellStateDto(employeeId, "approval", Arrays.asList(STATE_ERROR)));
			}
			
			// set state identify
			if(statusConfirmMonthDto.isPresent()) {
				for (ConfirmStatusResult confirmStatusResult : statusConfirmMonthDto.get().getListConfirmStatus()) {
					if(confirmStatusResult.getEmployeeId().equals(employee.getId())) {
						identify =  confirmStatusResult.isConfirmStatus();
					}
				}
			}
			disabled = false;
			if (param.getInitMenuMode() == 2 || !employee.getId().equals(employeeIdLogin)) {
				lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
			} else {
				boolean checkExist = false;
				if(statusConfirmMonthDto.isPresent()) {
					for (ConfirmStatusResult confirmStatusResult : statusConfirmMonthDto.get().getListConfirmStatus()) {
						if(confirmStatusResult.getEmployeeId().equals(employee.getId())) {
							checkExist = true;
							if(identify) {
								//解除可否
								if(confirmStatusResult.getWhetherToRelease() == ReleasedAtr.CAN_NOT_RELEASE) {
									lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
									disabled = true;
								}
							}else {
								//実施可否
								if(confirmStatusResult.getImplementaPropriety() == AvailabilityAtr.CAN_NOT_RELEASE) {
									lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
									disabled = true;
								}
							}
							break;
						}
					}
					
					if (!disabled) {
						performanceLockStatus.stream().filter(p -> p.getEmployeeId().equals(employee.getId()) && (p.getPastPerformaceLock() == LockStatus.LOCK.value || p.getMonthlyResultLock() == LockStatus.LOCK.value)).forEach(p -> {
							lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
						});
					}
				}
				if(!checkExist) {
					lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
				}
			}
			//*7

			MPDataDto mpdata = new MPDataDto(employeeId, lockStatus, "", employee.getCode(), employee.getBusinessName(),
					employeeId, "", identify, approve, dailyConfirm, "");
			mpdata.setVersion(rowData.getVersion());
			// Setting data for dynamic column
			List<EditStateOfMonthlyPerformanceDto> newList = editStateOfMonthlyPerformanceDtos.stream()
					.filter(item -> item.getEmployeeId().equals(employeeId)).collect(Collectors.toList());
			if (null != rowData) {
				if (null != rowData.getItems()) {
					rowData.getItems().forEach(item -> {
						// Cell Data
						// TODO item.getValueType().value
						int itemId = item.getItemId();
						String attendanceAtrAsString = String.valueOf(item.getValueType());
						String attendanceKey = mergeString(ADD_CHARACTER, "" + itemId);
						PAttendanceItem pA = param.getLstAtdItemUnique().get(itemId);
						List<String> cellStatus = new ArrayList<>();

						if (pA.getAttendanceAtr() == 1) { 
							/*1:  時間 */
							// neu item la thoi gian thi format lai theo dinh dang
							int minute = 0;
							if (item.getValue() != null) {
								minute = Integer.parseInt(item.getValue());
							}
							int hours = Math.abs(minute) / 60;
							int minutes = Math.abs(minute) % 60;
							String valueConvert = (minute < 0) ? "-" + String.format("%d:%02d", hours, minutes)
									: String.format("%d:%02d", hours, minutes);

							mpdata.addCellData(
									new MPCellDataDto(attendanceKey, valueConvert, attendanceAtrAsString, "label"));
						} else if(pA.getAttendanceAtr() == 6) {
							/*6:  コード */
							String itemIdAsString = String.valueOf(itemId);
							String nameColKey = mergeString(MPText.NAME, itemIdAsString);
							String codeColKey = mergeString(MPText.CODE, itemIdAsString);
							String value = item.getValue();
							if (value.isEmpty() || value.equals("null")) {
								mpdata.addCellData(new MPCellDataDto(mergeString(DPText.CODE, itemIdAsString), "",
										attendanceAtrAsString, DPText.TYPE_LABEL));
								value = MPText.NAME_EMPTY;
							} else {
								String valueItem = value;
								switch (itemId) {
								case 192:
								case 197:
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											value, attendanceAtrAsString, MPText.TYPE_LABEL));
									Optional<CodeName> optEmploymentCN = employmentCN.getCodeNames().stream().filter(x->x.getCode().equals(valueItem)).findFirst();
									value = optEmploymentCN.isPresent() ? optEmploymentCN.get().getName() : MPText.NAME_EMPTY;
									break;
								case 195:
								case 200:
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											value, attendanceAtrAsString, MPText.TYPE_LABEL));
									Optional<CodeName> optClassificationCN = classificationCN.getCodeNames().stream().filter(x->x.getCode().equals(valueItem)).findFirst();
									value = optClassificationCN.isPresent() ? optClassificationCN.get().getName() : MPText.NAME_EMPTY;
									break;
									
								case 196:
								case 201:
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											value, attendanceAtrAsString, MPText.TYPE_LABEL));
									Optional<CodeName> optBusinessTypeCN = businessTypeCN.getCodeNames().stream().filter(x->x.getCode().equals(valueItem)).findFirst();
									value = optBusinessTypeCN.isPresent() ? optBusinessTypeCN.get().getName() : MPText.NAME_EMPTY;
									break;
									
								case 193:
									Optional<CodeName> optPositionStartCN = positionStartCN.getCodeNames().stream().filter(x->x.getId().equals(valueItem)).findFirst();
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											optPositionStartCN.isPresent() ? optPositionStartCN.get().getCode() : "", attendanceAtrAsString, MPText.TYPE_LABEL));
									value = optPositionStartCN.isPresent() ? optPositionStartCN.get().getName() : MPText.NAME_EMPTY;
									break;
								
								case 194:
									Optional<CodeName> optWorkplaceStartCN = workplaceStartCN.getCodeNames().stream().filter(x->x.getId().equals(valueItem)).findFirst();
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											optWorkplaceStartCN.isPresent() ? optWorkplaceStartCN.get().getCode() : "", attendanceAtrAsString, MPText.TYPE_LABEL));
									value = optWorkplaceStartCN.isPresent() ? optWorkplaceStartCN.get().getName() : MPText.NAME_EMPTY;
									break;
									
								case 198:
									Optional<CodeName> optPositionEndCN = positionEndCN.getCodeNames().stream().filter(x->x.getId().equals(valueItem)).findFirst();
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											optPositionEndCN.isPresent() ? optPositionEndCN.get().getCode() : "", attendanceAtrAsString, MPText.TYPE_LABEL));
									value = optPositionEndCN.isPresent() ? optPositionEndCN.get().getName() : MPText.NAME_EMPTY;
									break;
									
								case 199:
									Optional<CodeName> optWorkplaceEndCN = workplaceEndCN.getCodeNames().stream().filter(x->x.getId().equals(valueItem)).findFirst();
									mpdata.addCellData(new MPCellDataDto(codeColKey,
											optWorkplaceEndCN.isPresent() ? optWorkplaceEndCN.get().getCode() : "", attendanceAtrAsString, MPText.TYPE_LABEL));
									value = optWorkplaceEndCN.isPresent() ? optWorkplaceEndCN.get().getName() : MPText.NAME_EMPTY;
									break;
								}
							}
							mpdata.addCellData(new MPCellDataDto(nameColKey, value, attendanceAtrAsString, MPText.TYPE_LINK));
						} else {
							mpdata.addCellData(new MPCellDataDto(attendanceKey,
									item.getValue() != null ? item.getValue() : "", attendanceAtrAsString, ""));
						}
						if (param.getInitMenuMode() == 2) { // set state mode approve, bat cu lock nao ngoai lock monthly approve thi disable
							if (!StringUtil.isNullOrEmpty(lockStatus, true))
								cellStatus.add(STATE_DISABLE);
						} else { // set state cac mode khac, cu co lock la disable
							if (!StringUtil.isNullOrEmpty(lockStatus, true))
								cellStatus.add(STATE_DISABLE);
						}
						// Cell Data
						lstCellState.add(new MPCellStateDto(employeeId, attendanceKey, cellStatus));

						Optional<EditStateOfMonthlyPerformanceDto> dto = newList.stream()
								.filter(item2 -> item2.getAttendanceItemId().equals(itemId)).findFirst();
						if (dto.isPresent()) { // set mau sua tay cua cell
							// color for attendance Item 192->201
							if (MPText.ITEM_CODE_LINK.contains(itemId)) {
								if (dto.get().getStateOfEdit() == 0) {
									screenDto.setStateCell("Code"+itemId, employeeId, HAND_CORRECTION_MYSELF);
									screenDto.setStateCell("Name"+itemId, employeeId, HAND_CORRECTION_MYSELF);
								} else {
									screenDto.setStateCell("Code"+itemId, employeeId, HAND_CORRECTION_OTHER);
									screenDto.setStateCell("Name"+itemId, employeeId, HAND_CORRECTION_OTHER);
								}
							} else {
								if (dto.get().getStateOfEdit() == 0) {
									screenDto.setStateCell(attendanceKey, employeeId, HAND_CORRECTION_MYSELF);
								} else {
									screenDto.setStateCell(attendanceKey, employeeId, HAND_CORRECTION_OTHER);
								}
							}
						}
						
						// color for attendance Item 202
						if (itemId == 202) {
							// 月別実績の勤怠時間．月の計算．36協定時間．36協定時間のエラー状態
							Optional<AttendanceTimeOfMonthly> optAttendanceTimeOfMonthly = this.attendanceTimeOfMonthlyRepo
									.find(employeeId, new YearMonth(rowData.getYearMonth()),
											ClosureId.valueOf(rowData.getClosureId()),
											new ClosureDate(rowData.getClosureDate().getClosureDay(),
													rowData.getClosureDate().getLastDayOfMonth()));
							if (optAttendanceTimeOfMonthly.isPresent()) {
								MonthlyCalculation monthlyCalculation = optAttendanceTimeOfMonthly.get()
										.getMonthlyCalculation();
								if (monthlyCalculation != null) {
									/** TODO: 36協定時間対応により、コメントアウトされた */
//									AgreementTimeOfMonthly agreementTime = monthlyCalculation.getAgreementTime();
//									if (agreementTime != null) {
//										switch (agreementTime.getStatus().value) {
//										// 限度アラーム時間超過
//										case 2:
//											// 特例限度アラーム時間超過
//										case 4:
//											screenDto.setStateCell(attendanceKey, employeeId, STATE_ALARM);
//											break;
//										// 限度エラー時間超過
//										case 1:
//											// 特例限度エラー時間超過
//										case 3:
//											screenDto.setStateCell(attendanceKey, employeeId, STATE_ERROR);
//											break;
//										// 正常（特例あり）
//										case 5:
//											// 限度アラーム時間超過（特例あり）
//										case 7:
//											// 限度エラー時間超過（特例あり）
//										case 6:
//											screenDto.setStateCell(attendanceKey, employeeId, STATE_SPECIAL);
//											break;
//										default:
//											break;
//										}
//									}
								}
							}
						}

					});
				}
			}
			lstData.add(mpdata);
		}
		screenDto.setMPSateCellHideControl(mPSateCellHideControls);
		// get histtory into company
		List<AffCompanyHistImport> listAffCompanyHistImport = screenDto.getLstAffComHist();
		List<CheckEmpEralOuput> listCheckEmpEralOuput = checkDailyPerError
				.checkDailyPerError(listEmployeeIds,
						new DatePeriod(screenDto.getSelectedActualTime().getStartDate(),
								screenDto.getSelectedActualTime().getEndDate()),
						listAffCompanyHistImport, monthlyResults);
			//取得した情報を元に月別実績を画面に表示する
			//NOTE: ※取得した「会社所属履歴」をもとに、菜食していない期間の実績は表示しないでください
			List<MPDataDto> listData =  new ArrayList<>();
			screenDto.getLstData().forEach(x -> {
				Optional<AffCompanyHistImport> optMonthlyPerformanceEmployeeDto = listAffCompanyHistImport.stream()
						.filter(y -> x.getEmployeeId().equals(y.getEmployeeId())).findFirst();
				
				if (optMonthlyPerformanceEmployeeDto.isPresent()
						&& optMonthlyPerformanceEmployeeDto.get().getLstAffComHistItem().size() > 0)
					for(CheckEmpEralOuput checkEmpEralOuput: listCheckEmpEralOuput) {
						if(x.getEmployeeId().equals(checkEmpEralOuput.getEmployId())) {
							if(checkEmpEralOuput.getTypeAtr() == TypeErrorAlarm.ERROR) {
								x.setError("ER");
							}else if(checkEmpEralOuput.getTypeAtr() == TypeErrorAlarm.ALARM) {
								x.setError("AL");
							}else if(checkEmpEralOuput.getTypeAtr() == TypeErrorAlarm.ERROR_ALARM) {
								x.setError("ER/AL");
							}else {
								x.setError("");
							}
							break;
						}
					}
					listData.add(x);
			});
			screenDto.setLstData(listData);
	}

	// copy ben MonthlyPerformanceDisplay
	public List<MonthlyPerformaceLockStatus> checkLockStatus(String cid, List<String> empIds, Integer processDateYM,
			Integer closureId, DatePeriod closureTime, int intScreenMode, List<AffCompanyHistImport> lstAffComHist,
			List<MonthlyModifyResultDto> monthlyResults) {
		List<MonthlyPerformaceLockStatus> monthlyLockStatusLst = new ArrayList<MonthlyPerformaceLockStatus>();
		// ロック解除モード の場合
		if (intScreenMode == 1) {
			return monthlyLockStatusLst;
		}
		// 社員ID（List）と基準日から所属職場IDを取得
		// 基準日：パラメータ「締め期間」の終了日
		List<AffAtWorkplaceImport> affWorkplaceLst = affWorkplaceAdapter.findBySIdAndBaseDate(empIds,
				closureTime.end());
		if (CollectionUtil.isEmpty(affWorkplaceLst)) {
			return monthlyLockStatusLst;
		}
		// 「List＜所属職場履歴項目＞」の件数ループしてください
		MonthlyPerformaceLockStatus monthlyLockStatus = null;
		
		List<SharedAffJobTitleHisImport> listShareAff = affJobTitleAdapter.findAffJobTitleHisByListSid(empIds, closureTime.end());
		
		Optional<IdentityProcess> identityOp = identityProcessRepo.getIdentityProcessById(cid);
		boolean checkIdentityOp = false;
		//対応するドメインモデル「本人確認処理の利用設定」を取得する
		if(!identityOp.isPresent()) {
			checkIdentityOp = true;
		} else {
			//取得したドメインモデル「本人確認処理の利用設定．日の本人確認を利用する」チェックする
			if(identityOp.get().getUseDailySelfCk() == 0){
				checkIdentityOp = true;
			}
		}
		
		List<Identification> listIdentification = identificationRepository.findByListEmployeeID(empIds, closureTime.start(), closureTime.end());
		
		List<EmployeeDailyPerError> listEmployeeDailyPerError =  employeeDailyPerErrorRepo.finds(empIds, new DatePeriod(closureTime.start(), closureTime.end()));
		
		Optional<ApprovalProcess> approvalProcOp = approvalRepo.getApprovalProcessById(cid);
		
		for (AffAtWorkplaceImport affWorkplaceImport : affWorkplaceLst) {
			
			Optional<AffCompanyHistImport> affInHist = lstAffComHist.stream()
					.filter(x -> x.getEmployeeId().equals(affWorkplaceImport.getEmployeeId())).findFirst();

			List<DatePeriod> periodInHist = affInHist.isPresent() ? affInHist.get().getLstAffComHistItem().stream()
					.map(x -> x.getDatePeriod()).collect(Collectors.toList()) : new ArrayList<>();
			// EAP chua sua, a Tuan giai thich la:
			// lay dateperiod theo data thuc te luu trong DB, k lay theo data tu
			// man hinh truyen xuong
            Optional<MonthlyModifyResultDto> optMonthlyModifyResultDto = monthlyResults.stream().filter(x-> x.getEmployeeId().equals(affWorkplaceImport.getEmployeeId())).findFirst();
            if(!optMonthlyModifyResultDto.isPresent()){
                continue;
            }
            DatePeriod workDatePeriod = optMonthlyModifyResultDto.get().getWorkDatePeriod();
            
            List<GeneralDate> lstDateCheck = mergeDatePeriod(workDatePeriod, periodInHist);
			
			List<Identification> listIdenByEmpID = new ArrayList<>();
			for(Identification iden : listIdentification) {
				if(iden.getEmployeeId().equals(affWorkplaceImport.getEmployeeId())) {
					listIdenByEmpID.add(iden);
				}
			}
			
			boolean checkExistRecordErrorListDate = false;

			List<String> listCode = listEmployeeDailyPerError.stream()
					.filter(x -> x.getEmployeeID().equals(affWorkplaceImport.getEmployeeId()))
					.map(x -> x.getErrorAlarmWorkRecordCode().v()).collect(Collectors.toList());
			// 対応するドメインモデル「勤務実績のエラーアラーム」を取得する
			List<ErrorAlarmWorkRecord> errorAlarmWorkRecordLst = errorAlarmWorkRecordRepository
					.getListErAlByListCodeError(cid, listCode);
			if (!errorAlarmWorkRecordLst.isEmpty()) {
				checkExistRecordErrorListDate = true;
			}
			
			// 月の実績の状況を取得する
			AcquireActualStatus param = new AcquireActualStatus(cid, affWorkplaceImport.getEmployeeId(), processDateYM,
					closureId, closureTime.end(), workDatePeriod, affWorkplaceImport.getWorkplaceId());
			MonthlyActualSituationOutput monthlymonthlyActualStatusOutput = monthlyActualStatus
					.getMonthlyActualSituationStatus(param,approvalProcOp,listShareAff,checkIdentityOp,listIdenByEmpID,checkExistRecordErrorListDate, lstDateCheck);
			// Output「月の実績の状況」を元に「ロック状態一覧」をセットする
			monthlyLockStatus = new MonthlyPerformaceLockStatus(
					monthlymonthlyActualStatusOutput.getEmployeeClosingInfo().getEmployeeId(),
					// TODO
					LockStatus.UNLOCK.value,
					// 職場の就業確定状態
					monthlymonthlyActualStatusOutput.getEmploymentFixedStatus().equals(EmploymentFixedStatus.CONFIRM)
							? LockStatus.LOCK.value : LockStatus.UNLOCK.value,
					// 月の承認状況
					monthlymonthlyActualStatusOutput.getApprovalStatus().equals(ApprovalStatus.APPROVAL)
							? LockStatus.LOCK.value : LockStatus.UNLOCK.value,
					// 月別実績のロック状態
					monthlymonthlyActualStatusOutput.getMonthlyLockStatus().value,
					// 本人確認が完了している
					monthlymonthlyActualStatusOutput.getDailyActualSituation().isIdentificationCompleted()
							? LockStatus.UNLOCK.value : LockStatus.LOCK.value,
					// 日の実績が存在する
					monthlymonthlyActualStatusOutput.getDailyActualSituation().isDailyAchievementsExist()
							? LockStatus.UNLOCK.value : LockStatus.LOCK.value,
					// エラーが0件である
					monthlymonthlyActualStatusOutput.getDailyActualSituation().isDailyRecordError() ? LockStatus.LOCK.value
							: LockStatus.UNLOCK.value);
			monthlyLockStatusLst.add(monthlyLockStatus);
		}
		// 過去実績の修正ロック
		LockStatus pastLockStatus = editLockOfPastResult(processDateYM, closureId,
				new ActualTime(closureTime.start(), closureTime.end()));
		// Output「ロック状態」を「ロック状態一覧.過去実績のロック」にセットする
		monthlyLockStatusLst = monthlyLockStatusLst.stream().map(item -> {
			item.setPastPerformaceLock(pastLockStatus.value);
			return item;
		}).collect(Collectors.toList());

		return monthlyLockStatusLst;
	}

	private LockStatus editLockOfPastResult(Integer processDateYM, Integer closureId, ActualTime actualTime) {
		ActualTimeState actualTimeState = monthlyCheck.checkActualTime(closureId, processDateYM, actualTime);
		if (actualTimeState.equals(ActualTimeState.Past)) {
			return LockStatus.LOCK;
		}
		return LockStatus.UNLOCK;
	}

	private String mergeString(String... x) {
		return StringUtils.join(x);
	}
	
	public List<GeneralDate> mergeDatePeriod(DatePeriod dist, List<DatePeriod> lstSource) {
		List<DatePeriod> lstResult = new ArrayList<>();
		lstSource.stream().forEach(x -> {
			if (dist.start().beforeOrEquals(x.end()) || dist.end().afterOrEquals(x.start())) {
				lstResult.add(new DatePeriod(dist.start().beforeOrEquals(x.start()) ? x.start() : dist.start(),
						dist.end().beforeOrEquals(x.end()) ? dist.end() : x.end()));
			}
		});
		return lstResult.stream().flatMap(x -> x.datesBetween().stream()).collect(Collectors.toList());
	}
}
