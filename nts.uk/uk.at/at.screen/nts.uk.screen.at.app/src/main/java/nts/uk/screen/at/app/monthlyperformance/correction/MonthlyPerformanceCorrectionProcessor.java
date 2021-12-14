package nts.uk.screen.at.app.monthlyperformance.correction;

import java.math.BigDecimal;
//import java.text.Format;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import nts.arc.error.BusinessException;
//import nts.arc.task.parallel.ParallelExceptions.Item;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeReferenceRange;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationAdapter;
import nts.uk.ctx.at.function.dom.adapter.annualworkschedule.EmployeeInformationQueryDtoImport;
import nts.uk.ctx.at.record.app.find.monthly.root.common.ClosureDateDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.FormatPerformanceDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.IdentityProcessDto;
import nts.uk.ctx.at.record.app.find.workrecord.operationsetting.IdentityProcessFinder;
import nts.uk.ctx.at.record.dom.adapter.company.AffCompanyHistImport;
import nts.uk.ctx.at.record.dom.adapter.company.SyCompanyRecordAdapter;
import nts.uk.ctx.at.record.dom.adapter.query.employee.RegulationInfoEmployeeQuery;
import nts.uk.ctx.at.record.dom.adapter.query.employee.RegulationInfoEmployeeQueryAdapter;
import nts.uk.ctx.at.record.dom.adapter.query.employee.RegulationInfoEmployeeQueryR;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.ApprovalStatusAdapter;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.dtos.AppRootOfEmpMonthImport;
import nts.uk.ctx.at.record.dom.adapter.workflow.service.enums.ApprovalStatusForEmployee;
import nts.uk.ctx.at.record.dom.approvalmanagement.ApprovalProcessingUseSetting;
import nts.uk.ctx.at.record.dom.approvalmanagement.repository.ApprovalProcessingUseSettingRepository;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.finddata.IFindDataDCRecord;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.organization.adapter.EmploymentAdapter;
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
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.FormatPerformance;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.FormatPerformanceRepository;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFun;
import nts.uk.ctx.at.record.dom.workrecord.operationsetting.MonPerformanceFunRepository;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.ControlOfMonthlyDto;
import nts.uk.ctx.at.shared.app.find.scherec.monthlyattditem.ControlOfMonthlyFinder;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.calc.MonthlyCalculation;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
//import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.WorkClosureQueryProcessor;
import nts.uk.ctx.at.shared.pub.workrule.closure.PresentClosingPeriodExport;
import nts.uk.ctx.at.shared.pub.workrule.closure.ShClosurePub;
import nts.uk.screen.at.app.dailyperformance.correction.DailyPerformanceScreenRepo;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeName;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.CodeNameType;
import nts.uk.screen.at.app.dailyperformance.correction.datadialog.DataDialogWithTypeProcessor;
import nts.uk.screen.at.app.dailyperformance.correction.text.DPText;
import nts.uk.screen.at.app.monthlyperformance.CheckDailyPerError;
import nts.uk.screen.at.app.monthlyperformance.CheckEmpEralOuput;
import nts.uk.screen.at.app.monthlyperformance.TypeErrorAlarm;
//import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.ActualTime;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.ClosureInfoOuput;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.ColumnSetting;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.EditStateOfMonthlyPerformanceDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPCellDataDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPCellStateDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPControlDisplayItem;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPDataDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPHeaderDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPSateCellHideControl;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPSheetDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MPText;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyAttendanceItemDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceAuthorityDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceCorrectionDto;
import nts.uk.screen.at.app.monthlyperformance.correction.dto.MonthlyPerformanceEmployeeDto;
import nts.uk.screen.at.app.monthlyperformance.correction.param.MonthlyPerformanceParam;
import nts.uk.screen.at.app.monthlyperformance.correction.param.PAttendanceItem;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyQueryProcessor;
import nts.uk.screen.at.app.monthlyperformance.correction.query.MonthlyModifyResult;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * TODO
 */
@Stateless
public class MonthlyPerformanceCorrectionProcessor {

	@Inject
	private FormatPerformanceRepository formatPerformanceRepository;
	@Inject
	private MonPerformanceFunRepository monPerformanceFunRepository;
	@Inject
	private IdentityProcessFinder identityProcessFinder;
	@Inject
	private EmploymentAdapter employmentAdapter;
	@Inject
	private ShClosurePub shClosurePub;
	@Inject
	private ClosureRepository closureRepository;
	@Inject
	private MonthlyPerformanceDisplay monthlyDisplay;
	@Inject
	private SyCompanyRecordAdapter syCompanyRecordAdapter;
	/**
	 * 実績の時系列をチェックする
	 */
	@Inject
	private MonthlyPerformanceCheck monthlyCheck;

	@Inject
	private MonthlyPerformanceScreenRepo repo;

	@Inject
	private MonthlyModifyQueryProcessor monthlyModifyQueryProcessor;

	@Inject
	private DailyPerformanceScreenRepo dailyPerformanceScreenRepo;

	@Inject
	private RegulationInfoEmployeeQueryAdapter regulationInfoEmployeePub;
	
	@Inject
	private ApprovalProcessingUseSettingRepository approvalProcessingUseSettingRepo;
	
	@Inject
	private ApprovalStatusAdapter approvalStatusAdapter;
	
	@Inject
	private AttendanceTimeOfMonthlyRepository attendanceTimeOfMonthlyRepo;
	
	@Inject
	private WorkClosureQueryProcessor workClosureQueryProcessor;

	@Inject
	private EmployeeInformationAdapter employeeInformationAdapter;
	
	/** 月次の勤怠項目の制御 */
	@Inject
	private ControlOfMonthlyFinder controlOfMonthlyFinder;
	
	@Inject
	private ApprovalStatusMonthly approvalStatusMonthly;

	@Inject
	private ConfirmStatusMonthly confirmStatusMonthly;
	
	@Inject
	private CheckDailyPerError checkDailyPerError;
	
	@Inject
	private DataDialogWithTypeProcessor dataDialogWithTypeProcessor;
	
	@Inject
	private IFindDataDCRecord iFindDataDCRecord;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	
	private static final String STATE_DISABLE = "mgrid-disable";
	private static final String HAND_CORRECTION_MYSELF = "mgrid-manual-edit-target";
	private static final String HAND_CORRECTION_OTHER = "mgrid-manual-edit-other";
//	private static final String REFLECT_APPLICATION = "ntsgrid-reflect";
	private static final String STATE_ERROR = "mgrid-error";
	private static final String STATE_ALARM = "mgrid-alarm";
	private static final String STATE_SPECIAL = "mgrid-special";
	private static final String ADD_CHARACTER = "A";
//	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	private final static List<Integer> ITEM_ID_ALL = AttendanceItemIdContainer.getIds(AttendanceItemType.MONTHLY_ITEM)
			.stream().map(x -> x.getItemId()).collect(Collectors.toList());

	public MonthlyPerformanceCorrectionDto initScreen(MonthlyPerformanceParam param) {
		String companyId = AppContexts.user().companyId();
		String employeeId = AppContexts.user().employeeId();
//		String rollId = AppContexts.user().roles().forAttendance();
		AppContexts.user().roles();
		MonthlyPerformanceCorrectionDto screenDto = new MonthlyPerformanceCorrectionDto();
		screenDto.setParam(param);
		// 1. 起動に必要な情報の取得
		// ドメインモデル「実績修正画面で利用するフォーマット」を取得する
		Optional<FormatPerformance> formatPerformance = formatPerformanceRepository.getFormatPerformanceById(companyId);
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
		if (formatPerformance.isPresent()) {
			screenDto.setFormatPerformance(FormatPerformanceDto.fromDomain(formatPerformance.get()));
		}
		// 本人確認処理の利用設定
		screenDto.setIdentityProcess(identityProcess);
		
		// 2. アルゴリズム「ログイン社員の月別実績の権限を取得する」を実行する
		// TODO 勤務実績の権限 Authority of the work record
		boolean isExistAuthorityWorkRecord = true;
		// 存在しない場合
		if (!isExistAuthorityWorkRecord) {
			// エラーメッセージ（Msg_914）を表示する
			throw new BusinessException("Msg_914");
		}
		
		Integer closureId = null;
		Integer yearMonth = null;
		
		// 3. アルゴリズム「ログイン社員の締めを取得する」を実行する   move  ログイン社員の締めを取得する  in authority 1.1
		// 基準日：システム日付
		if(param.getClosureId() == null){
			closureId = this.getClosureId(companyId, employeeId, GeneralDate.today());
			param.setClosureId(closureId);
		} else {
			closureId = param.getClosureId();
		}
		screenDto.setClosureId(closureId);
		
		// 4.アルゴリズム「処理年月の取得」を実行する   move   処理年月の取得   in authority 1.
		Optional<PresentClosingPeriodExport> presentClosingPeriodExport = shClosurePub.find(companyId, closureId);

		// アルゴリズム「締め情報の表示」を実行する move 締め情報の表示 in authority 2.
		//set A3_2
		yearMonth = 0;
		if (param.getYearMonth() == null || (param.getYearMonth() != null && param.getYearMonth() == 0)) {
			if (presentClosingPeriodExport.isPresent()) {
				yearMonth = presentClosingPeriodExport.get().getProcessingYm().v();
				param.setYearMonth(yearMonth);
				// 処理年月
				screenDto.setProcessDate(yearMonth);
			}else {
				throw new BusinessException("Msg_1143");
			}
		} else {
			yearMonth = param.getYearMonth();
			screenDto.setProcessDate(yearMonth);
		}
		// 5. アルゴリズム「締め情報の表示」を実行する  move  実績期間の表示 in authority 3.
		this.displayClosure(screenDto, companyId, closureId, yearMonth);

		// 6. どのメニューから起動したのかをチェックする (Check xem khởi động từ menu nào)
		// 「月別実績の修正」からの場合         
		if (param.getInitMenuMode() == 0 || param.getInitMenuMode() == 1) {// normal mode hoac unlock mode
			// 7. アルゴリズム「通常モードで起動する」を実行する
			// アルゴリズム「<<Public>> 就業条件で社員を検索して並び替える」を実行する
			if (param.getLstEmployees() != null && !param.getLstEmployees().isEmpty()) {
				screenDto.setLstEmployee(param.getLstEmployees());
			} else {
				List<RegulationInfoEmployeeQueryR> regulationRs = regulationInfoEmployeePub.search(
						createQueryEmployee(new ArrayList<>(), presentClosingPeriodExport.get().getClosureStartDate(),
								presentClosingPeriodExport.get().getClosureEndDate()));

				List<MonthlyPerformanceEmployeeDto> lstEmployeeDto = regulationRs.stream().map(item -> {
					return new MonthlyPerformanceEmployeeDto(item.getEmployeeId(), item.getEmployeeCode(),
							item.getEmployeeName(), item.getWorkplaceName(), item.getWorkplaceId(), "", false);
				}).collect(Collectors.toList());
				screenDto.setLstEmployee(lstEmployeeDto);
				param.setLstEmployees(lstEmployeeDto);
			}
			
			screenDto.setLoginUser(employeeId);

			List<String> employeeIds = screenDto.getLstEmployee().stream().map(e -> e.getId())
					.collect(Collectors.toList());
			
			// lay data xem thang nao co du lieu de hien thi
			List<MonthlyModifyResult> results = new GetDataMonthly(employeeIds, new YearMonth(yearMonth),
					ClosureId.valueOf(closureId), screenDto.getClosureDate().toDomain(), ITEM_ID_ALL,
					monthlyModifyQueryProcessor).call();
			
			//指定した年月の期間を算出する
			DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
					ClosureService.createRequireM1(closureRepository, closureEmploymentRepo),
					closureId.intValue(), new YearMonth(yearMonth));
			//社員ID（List）と指定期間から所属会社履歴項目を取得
			// RequestList211
			List<AffCompanyHistImport> lstAffComHist = syCompanyRecordAdapter
					.getAffCompanyHistByEmployee(employeeIds, datePeriodClosure);
			
			screenDto.setLstAffComHist(lstAffComHist);
			
			// fix bug 
			if (results.isEmpty()) {
				String mess = new String("Msg_1450");
				createFixedHeader(screenDto, yearMonth, closureId, optApprovalProcessingUseSetting.get(),mess);
				return screenDto;
			}
			
			List<MonthlyModifyResultDto> monthlyResults = results.stream()
					.map(m -> new MonthlyModifyResultDto(m.getItems(), m.getYearMonth(), m.getEmployeeId(),
							m.getClosureId(), m.getClosureDate().toDomain(), m.getWorkDatePeriod()))
					.collect(Collectors.toList());

			// lay lai employeeID cua nhung nhan vien co du lieu
			employeeIds = results.stream().map(e -> e.getEmployeeId()).collect(Collectors.toList());
			
			// アルゴリズム「表示フォーマットの取得」を実行する(Thực hiện 「Lấy format hiển thị」)
			// TODO Data null confirm??formatPerformance
			if (formatPerformance.isPresent()) {
				monthlyDisplay.getDisplayFormat(employeeIds, formatPerformance.get().getSettingUnitType(), screenDto, monthlyResults);
			} else {
				String mess = new String("Msg_1452");
				createFixedHeader(screenDto, yearMonth, closureId, optApprovalProcessingUseSetting.get(),mess);
				return screenDto;
			}
			if (screenDto.getParam().getLstAtdItemUnique().isEmpty()){
				String mess = new String("Msg_1452");
				createFixedHeader(screenDto, yearMonth, closureId, optApprovalProcessingUseSetting.get(),mess);
				return screenDto;
			}
			
			List<MonthlyPerformaceLockStatus> lstLockStatus = screenDto.getParam().getLstLockStatus();
			if (lstLockStatus.stream().allMatch(item -> item.getLockStatusString() != Strings.EMPTY)) {
				screenDto.setShowRegisterButton(false);
			} else
				screenDto.setShowRegisterButton(true);
			
			// アルゴリズム「月別実績を表示する」を実行する Hiển thị monthly result
			displayMonthlyResult(screenDto, yearMonth, closureId, optApprovalProcessingUseSetting.get(), companyId, results, employeeIds, monthlyResults);
		
		} else { // 「月別実績の承認」からの場合
			//アルゴリズム「締め情報の表示」を実行する       move 実績期間の表示
			this.displayClosureInfo(screenDto, companyId, closureId, yearMonth);
			
			//アルゴリズム「承認モードで起動する」を実行する
			this.startUpInApprovalMode(optApprovalProcessingUseSetting, formatPerformance,screenDto,yearMonth, companyId);
			if(screenDto.getMess() != null && !screenDto.getMess().isEmpty()) {
				return screenDto;
			}
		}
		
		// set data of lstControlDisplayItem
		List<Integer> attdanceIds = screenDto.getParam().getLstAtdItemUnique().keySet().stream()
				.collect(Collectors.toList());
		screenDto.getLstControlDisplayItem().setItemIds(attdanceIds);
		screenDto.getLstControlDisplayItem().getLstAttendanceItem();

		// 画面項目の非活制御をする
		// アルゴリズム「実績の時系列をチェックする」を実行する (Check actual time)
		screenDto.setActualTimeState(
				monthlyCheck.checkActualTime(closureId, yearMonth, screenDto.getSelectedActualTime()).value);

		param.setActualTime(screenDto.getSelectedActualTime());
		// author
		screenDto.setAuthorityDto(getAuthority(screenDto));
		
		screenDto.createAccessModifierCellState();
		return screenDto;
	}
	
	//締め情報の表示
	private void displayClosureInfo(MonthlyPerformanceCorrectionDto screenDto, String companyId, Integer closureId,
			Integer processYM){
		//ドメインモデル「締め」を取得する
		//List<ClosureHistory> lstClosureHistory = this.closureRepository.findByCurrentMonth(companyId, new YearMonth(processYM));
		List<Closure> lstClosure = this.closureRepository.findAllUse(companyId);
		List<ClosureHistory> lstClosureHistory = new ArrayList<>();
		lstClosure.forEach(item -> {
			lstClosureHistory.addAll(item.getClosureHistories().stream().filter(closureItem -> 
				closureItem.getStartYearMonth().v() <= processYM.intValue() && 
						closureItem.getEndYearMonth().v() >= processYM.intValue()).collect(Collectors.toList()));
			
		});
		
		
		List<ClosureInfoOuput> lstClosureInfoOuput = new ArrayList<>();
		//取得した実績期間を画面に表示する
		//set A4_8
		lstClosureInfoOuput = lstClosureHistory.stream().map(x->{
			return new ClosureInfoOuput(x.getClosureName().v(), x.getClosureId().value);
		}).collect(Collectors.toList());
		screenDto.setLstclosureInfoOuput(lstClosureInfoOuput);
		
		//set A4_7
		int closureSelected = closureId;
		Optional<ClosureHistory> checkClosureExist = lstClosureHistory.stream()
        .filter(x ->{
       return x.getClosureId().value==closureId;
        })
        .findFirst();
		
		if(!checkClosureExist.isPresent() && lstClosureHistory!=null && !lstClosureHistory.isEmpty()){
			closureSelected = lstClosureHistory.get(0).getClosureId().value;
		}	
		screenDto.setSelectedClosure(closureSelected);
		
	}
	
	
	private void startUpInApprovalMode(Optional<ApprovalProcessingUseSetting> optApprovalProcessingUseSetting,
			Optional<FormatPerformance> formatPerformance, MonthlyPerformanceCorrectionDto screenDto, Integer yearMonth,
			String companyId) {
		if (optApprovalProcessingUseSetting.isPresent()) {
			DatePeriod datePeriod = null;
			datePeriod = new DatePeriod(screenDto.getSelectedActualTime().getStartDate(), screenDto.getSelectedActualTime().getEndDate());
			// 取得している「承認処理の利用設定．月の承認者確認を利用する」をチェックする
			ApprovalProcessingUseSetting approvalProcessingUseSetting = optApprovalProcessingUseSetting.get();
			if (approvalProcessingUseSetting.getUseMonthApproverConfirm()) {
				// アルゴリズム「ログイン社員の承認対象者の取得」を実行する
//				// request list 534
				AppRootOfEmpMonthImport approvalRootOfEmloyee = this.approvalStatusAdapter
						.getApprovalEmpStatusMonth(AppContexts.user().employeeId(), new YearMonth(yearMonth),
								screenDto.getClosureId(), screenDto.getClosureDate().toDomain(),
								screenDto.getSelectedActualTime().getEndDate(), optApprovalProcessingUseSetting.get().getUseDayApproverConfirm(), datePeriod);
				//Imported（就業）「基準社員の承認対象者」を取得する request list 133
//				ApprovalRootOfEmployeeImport approvalRootOfEmloyee = this.approvalStatusAdapter
//						.getApprovalRootOfEmloyeeNew(screenDto.getSelectedActualTime().getEndDate(), 
//								screenDto.getSelectedActualTime().getEndDate(), AppContexts.user().employeeId(), companyId, Integer.valueOf(2));
				
				if (approvalRootOfEmloyee == null || approvalRootOfEmloyee.getApprovalRootSituations().isEmpty()) {
					String mess = new String("Msg_1451");
					createFixedHeader(screenDto, yearMonth, screenDto.getSelectedClosure(), approvalProcessingUseSetting,mess);
					return;
				}

				// 対象期間に対象の締めに紐付いた雇用に属しているかチェックする ↓ (Start)
				// fix bug 107128 change EAP of 日別確認のアルゴリズム
				// 2019/04/10 大竹 メモ
				// 社員(list)に対応する処理締めを取得する
				//				List<ApprovalRootSituation> approvalRootSituations = approvalRootOfEmloyee.getApprovalRootSituations();
				//				Set<String> empIds = approvalRootSituations.stream().map(a -> a.getTargetID())
				//						.collect(Collectors.toSet());
				//				List<String> employeeIds = new ArrayList<>();
				//				for (String empId : empIds) {
				//					Closure closureDataByEmployee = closureService.getClosureDataByEmployee(empId,
				//							screenDto.getSelectedActualTime().getEndDate());
				//					if (closureDataByEmployee != null  && closureDataByEmployee.getClosureId().value ==  screenDto.getClosureId()) {
				//						employeeIds.add(empId);
				//					}
				//				}
				
				List<String> employeeIds = new ArrayList<>();
				// lay thong tin nhan vien theo empID thu duoc
				employeeIds.addAll(approvalRootOfEmloyee.getApprovalRootSituations().stream().map(item -> item.getTargetID()).collect(Collectors.toList()));
				if (employeeIds.isEmpty()) {
					String mess = new String("Msg_1450");
					createFixedHeader(screenDto, yearMonth, screenDto.getSelectedClosure(),
							approvalProcessingUseSetting, mess);
					return;
				}
				// 対象期間に対象の締めに紐付いた雇用に属しているかチェックする ↑ (End)

				// lay thong tin nhan vien theo empID thu duoc
				EmployeeInformationQueryDtoImport params = new EmployeeInformationQueryDtoImport(employeeIds,
						screenDto.getSelectedActualTime().getEndDate(), true, false, false, true, false, false);
				List<MonthlyPerformanceEmployeeDto> lstEmployee = employeeInformationAdapter.getEmployeeInfo(params)
						.stream()
						.map(item -> new MonthlyPerformanceEmployeeDto(item.getEmployeeId(), item.getEmployeeCode(),
								item.getBusinessName(),
								item.getWorkplace() == null ? null : item.getWorkplace().getWorkplaceName(),
								item.getWorkplace() == null ? null : item.getWorkplace().getWorkplaceCode(), "", false))
						.collect(Collectors.toList());
				lstEmployee.sort((e1, e2) -> e1.getCode().compareTo(e2.getCode()));
				screenDto.setLstEmployee(lstEmployee);
				screenDto.setLoginUser(AppContexts.user().employeeId());

				// lay data xem thang nao co du lieu de hien thi
				List<MonthlyModifyResult> results = new GetDataMonthly(employeeIds, new YearMonth(yearMonth),
						ClosureId.valueOf(screenDto.getClosureId()), screenDto.getClosureDate().toDomain(), ITEM_ID_ALL,
						monthlyModifyQueryProcessor).call();
				
				//指定した年月の期間を算出する
				DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
						ClosureService.createRequireM1(closureRepository, closureEmploymentRepo),
						screenDto.getClosureId().intValue(), new YearMonth(yearMonth));
				//社員ID（List）と指定期間から所属会社履歴項目を取得
				// RequestList211
				List<AffCompanyHistImport> lstAffComHist = syCompanyRecordAdapter
						.getAffCompanyHistByEmployee(employeeIds, datePeriodClosure);
				
				screenDto.setLstAffComHist(lstAffComHist);
				
				List<MonthlyModifyResultDto> monthlyResults = results.stream()
						.map(m -> new MonthlyModifyResultDto(m.getItems(), m.getYearMonth(), m.getEmployeeId(),
								m.getClosureId(), m.getClosureDate().toDomain(), m.getWorkDatePeriod()))
						.collect(Collectors.toList());

				// lay lai employeeID cua nhung nhan vien co du lieu
				employeeIds = results.stream().map(e -> e.getEmployeeId()).collect(Collectors.toList());

				// アルゴリズム「表示フォーマットの取得」を実行する(Thực hiện 「Lấy format hiển thị」)
				// TODO Data null confirm??formatPerformance
				if (formatPerformance.isPresent()) {
					monthlyDisplay.getDisplayFormat(employeeIds, formatPerformance.get().getSettingUnitType(),
							screenDto, monthlyResults);
					if(screenDto.getMess() != null && screenDto.getMess().equals("Msg_1452"))
						return;
				} else {
					throw new BusinessException("Msg_1452");
				}

				List<MonthlyPerformaceLockStatus> lstLockStatus = screenDto.getParam().getLstLockStatus();
				if (lstLockStatus.stream().allMatch(item -> item.getLockStatusString() != Strings.EMPTY)) {
					screenDto.setShowRegisterButton(false);
				} else
					screenDto.setShowRegisterButton(true);

				// アルゴリズム「月別実績を表示する」を実行する Hiển thị monthly result
				displayMonthlyResult(screenDto, yearMonth, screenDto.getSelectedClosure(), approvalProcessingUseSetting,
						companyId, results, employeeIds, monthlyResults);

			} else {
				throw new BusinessException("Msg_873");
			}
		}
		// fixed
		else {
			throw new BusinessException("Msg_873");
		}
	}
	/*
	private void obtainDisplayFormat(List<String> lstEmpId, String formatCode, String companyId){
		//ドメインモデル「実績修正画面で利用するフォーマット」を取得する
		Optional<FormatPerformance> optFormatPerformance = this.formatPerformanceRepo.getFormatPerformanceById(companyId);
		if(optFormatPerformance.isPresent()){
			SettingUnitType settingUnitType = optFormatPerformance.get().getSettingUnitType();
			//権限
			if(settingUnitType.value==0){
				//アルゴリズム「社員の権限に対応する表示項目を取得する」を実行する
			}
			//勤務種別
			else{
				
			}
		}
	}
	*/
	

	private RegulationInfoEmployeeQuery createQueryEmployee(List<String> employeeCodes, GeneralDate startDate,
			GeneralDate endDate) {
		RegulationInfoEmployeeQuery query = new RegulationInfoEmployeeQuery();
		query.setBaseDate(GeneralDate.today());
		query.setReferenceRange(EmployeeReferenceRange.DEPARTMENT_ONLY.value);
		query.setFilterByEmployment(false);
		query.setEmploymentCodes(Collections.emptyList());
		// query.setFilterByDepartment(false);
		// query.setDepartmentCodes(Collections.emptyList());
		query.setFilterByWorkplace(false);
		query.setWorkplaceCodes(Collections.emptyList());
		query.setFilterByClassification(false);
		query.setClassificationCodes(Collections.emptyList());
		query.setFilterByJobTitle(false);
		query.setJobTitleCodes(Collections.emptyList());
		query.setFilterByWorktype(false);
		query.setWorktypeCodes(Collections.emptyList());
		query.setPeriodStart(startDate);
		query.setPeriodEnd(endDate);
		query.setIncludeIncumbents(true);
		query.setIncludeWorkersOnLeave(true);
		query.setIncludeOccupancy(true);
		// query.setIncludeAreOnLoan(true);
		// query.setIncludeGoingOnLoan(false);
		query.setIncludeRetirees(false);
		query.setRetireStart(GeneralDate.today());
		query.setRetireEnd(GeneralDate.today());
		query.setFilterByClosure(false);
		return query;
	}

	private List<MonthlyPerformanceAuthorityDto> getAuthority(MonthlyPerformanceCorrectionDto screenDto) {
		String roleId = AppContexts.user().roles().forAttendance();
		if (roleId != null) {
			List<MonthlyPerformanceAuthorityDto> dailyPerformans = dailyPerformanceScreenRepo.findAuthority(roleId, new BigDecimal(1));
			if (!dailyPerformans.isEmpty()) {
				return dailyPerformans;
			}
		}
		throw new BusinessException("Msg_914");
	}

	/**
	 * アルゴリズム「ログイン社員の締めを取得する」を実行する
	 * 
	 * @return 対象締め：締めID
	 */
	private Integer getClosureId(String cId, String employeeId, GeneralDate sysDate) {
		// アルゴリズム「社員IDと基準日から社員の雇用コードを取得」を実行する
		// Thực hiện thuật toán「Lấy employment từ employee ID và base date」
		// 社員IDと基準日から社員の雇用コードを取得
		Optional<EmploymentHistoryImported> empHistory = this.employmentAdapter.getEmpHistBySid(cId, employeeId,
				sysDate);
		String employmentCode = empHistory.orElseThrow(() -> new BusinessException("Msg_1143")).getEmploymentCode();
		// 雇用に紐づく締めを取得する
		Integer closureId = workClosureQueryProcessor.findClosureByEmploymentCode(employmentCode);

		// 対象締め：締めID
		return closureId;
	}

	/**
	 * Get id of employee list.
	 */
	/*
	private List<MonthlyPerformanceEmployeeDto> extractEmployeeList(List<MonthlyPerformanceEmployeeDto> lstEmployee,
			String sId, DateRange range) {
		if (!lstEmployee.isEmpty()) {
			return lstEmployee;
		} else {
			return getListEmployee(sId, range);
		}
	}
	*/

	/** アルゴリズム「対象者を抽出する」を実行する */
//	private List<MonthlyPerformanceEmployeeDto> getListEmployee(String sId, DateRange dateRange) {
//		// アルゴリズム「自職場を取得する」を実行する
//		// List<String> lstJobTitle = this.repo.getListJobTitle(dateRange);
//		// List<String> lstEmployment = this.repo.getListEmployment();
//		/// 対応するドメインモデル「所属職場」を取得する + 対応するドメインモデル「職場」を取得する
//		Map<String, String> lstWorkplace = this.repo.getListWorkplace(sId, dateRange);
//		// List<String> lstClassification = this.repo.getListClassification();
//		// 取得したドメインモデル「所属職場．社員ID」に対応するImported「（就業）社員」を取得する
//		if (lstWorkplace.isEmpty()) {
//			return new ArrayList<>();
//		}
//		return this.repo.getListEmployee(null, null, lstWorkplace, null);
//	}

	/**
	 * 締め情報の表示
	 * 
	 * @param screenDto
	 *            return result to DTO
	 * @param companyId
	 * @param closureId
	 * @param processYM
	 */
	
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
		// アルゴリズム「実績期間の取得」を実行する
		List<ActualTime> actualTimes = closure.get().getPeriodByYearMonth(YearMonth.of(processYM)).stream()
				.map(time -> {
					return new ActualTime(time.start(), time.end());
				}).collect(Collectors.toList());
		if (CollectionUtil.isEmpty(actualTimes))
			return;
		// 実績期間 → 画面項目「A4_5：実績期間選択肢」
		screenDto.setLstActualTimes(actualTimes);
		// 実績期間の件数をチェックする
		if (actualTimes.size() == 1) {
			//set A4_4
			screenDto.setSelectedActualTime(
					new ActualTime(actualTimes.get(0).getStartDate(), actualTimes.get(0).getEndDate()));
		} else if (actualTimes.size() == 2) {
			// 当月の期間を算出する
			DatePeriod datePeriod = ClosureService.getClosurePeriod(
					ClosureService.createRequireM1(closureRepository, closureEmploymentRepo),
					closureId, new YearMonth(processYM));
			// 画面項目「A4_4：実績期間選択」の選択状態を変更する
			screenDto.setSelectedActualTime(new ActualTime(datePeriod.start(), datePeriod.end()));
		}
	}

	/**
	 * 月別実績を表示する
	 */
	private void displayMonthlyResult(MonthlyPerformanceCorrectionDto screenDto, Integer yearMonth, Integer closureId,
			ApprovalProcessingUseSetting approvalProcessingUseSetting, String companyId,
			List<MonthlyModifyResult> results, List<String> employeeIds, List<MonthlyModifyResultDto> monthlyResults) {
		/**
		 * Create Grid Sheet DTO
		 */

		MPControlDisplayItem displayItem = screenDto.getLstControlDisplayItem();
		MonthlyPerformanceParam param = screenDto.getParam();
		//List<ConfirmationMonth> listConfirmationMonth = new ArrayList<>();
		List<String> listEmployeeIds = screenDto.getLstEmployee().stream().map(x->x.getId()).collect(Collectors.toList());
		List<MonthlyPerformaceLockStatus> performanceLockStatus = screenDto.getParam().getLstLockStatus();
		String loginId = AppContexts.user().employeeId();

		
		// アルゴリズム「対象年月に対応する月別実績を取得する」を実行する Lấy monthly result ứng với năm tháng
		if (param.getLstAtdItemUnique() == null || param.getLstAtdItemUnique().isEmpty()) {
			throw new BusinessException("Msg_1450");
		}
		
		iFindDataDCRecord.clearAllStateless();
		//[No.586]月の実績の確認状況を取得する
		Optional<StatusConfirmMonthDto> statusConfirmMonthDto = confirmStatusMonthly.getConfirmStatusMonthly(companyId, listEmployeeIds, YearMonth.of(yearMonth), closureId, false);

		//[No.587]月の実績の承認状況を取得する
		Optional<ApprovalStatusMonth> approvalStatusMonth =  approvalStatusMonthly.getApprovalStatusMonthly(companyId, loginId, closureId, listEmployeeIds, YearMonth.of(yearMonth), monthlyResults, false);
		iFindDataDCRecord.clearAllStateless();

		List<MPSheetDto> lstSheets = param.getSheets().stream().map(c -> {
			MPSheetDto sh = new MPSheetDto(c.getSheetNo(), c.getSheetName());
			for (PAttendanceItem attend : c.getDisplayItems()) {
				if(MPText.ITEM_CODE_LINK.contains(attend.getId())){
					sh.addColumn(mergeString(MPText.CODE, attend.getId().toString()));
					sh.addColumn(mergeString(MPText.NAME, attend.getId().toString()));
				}else{
					sh.addColumn(mergeString(ADD_CHARACTER, attend.getId().toString()));
				}
			}
			return sh;
		}).collect(Collectors.toList());
		displayItem.createSheets(lstSheets);
		
		
		List<MPHeaderDto> lstMPHeaderDto = MPHeaderDto.GenerateFixedHeader();
		
		//G7 G8 G9 hidden column identitfy, approval, dailyconfirm
		for (Iterator<MPHeaderDto> iter = lstMPHeaderDto.listIterator(); iter.hasNext(); ) {
			MPHeaderDto mpHeaderDto = iter.next();
			if ("identify".equals(mpHeaderDto.getKey())
					&& screenDto.getIdentityProcess().getUseMonthSelfCK() == 0) {
		        iter.remove();
		        continue;
		    }
			if ("approval".equals(mpHeaderDto.getKey())
					&& approvalProcessingUseSetting.getUseMonthApproverConfirm() == false) {
				iter.remove();
				continue;
			}
			if ("dailyconfirm".equals(mpHeaderDto.getKey())
					&& screenDto.getDailySelfChkDispAtr() == 0) {
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
			Map<Integer, MonthlyAttendanceItemDto> mapMP = lstAttendanceItem.stream().collect(Collectors.toMap(MonthlyAttendanceItemDto::getAttendanceItemId, x -> x));
			List<ControlOfMonthlyDto> listCtrOfMonthlyDto = controlOfMonthlyFinder.getListControlOfAttendanceItem(itemIds);
			for (Integer key : param.getLstAtdItemUnique().keySet()) {
				PAttendanceItem item = param.getLstAtdItemUnique().get(key);
				MonthlyAttendanceItemDto dto = mapMP.get(key);
				// ドメインモデル「月次の勤怠項目の制御」を取得する
				// Setting Header color & time input
				Optional<ControlOfMonthlyDto> ctrOfMonthlyDto = listCtrOfMonthlyDto.stream().filter(c -> c.getItemMonthlyId() == item.getId()).findFirst();
				lstHeader.add(MPHeaderDto.createSimpleHeader(item, ctrOfMonthlyDto.isPresent() ? ctrOfMonthlyDto.get() : null, dto));
			}
		}
		displayItem.setLstHeader(lstHeader);
		// Fixed header
		screenDto.setLstFixedHeader(lstMPHeaderDto);
		screenDto.getLstFixedHeader().forEach(column -> {
			screenDto.getLstControlDisplayItem().getColumnSettings().add(new ColumnSetting(column.getKey(), false));
		});
		//36 ->A36 (A36-> A40)
		for (MPHeaderDto key : displayItem.getLstHeader()) {
			ColumnSetting columnSetting = new ColumnSetting(key.getKey(), false);
			if (!key.getGroup().isEmpty()) {
				displayItem.getColumnSettings().add(new ColumnSetting(key.getGroup().get(0).getKey(), false));
				displayItem.getColumnSettings().add(new ColumnSetting(key.getGroup().get(1).getKey(), false));
			} else {
				if (key.getKey().contains("A")) {
					PAttendanceItem item = param.getLstAtdItemUnique()
							.get(Integer.parseInt(key.getKey().substring(1, key.getKey().length()).trim()));
					columnSetting.setTypeFormat(item.getAttendanceAtr());
				}
			}
			displayItem.getColumnSettings().add(columnSetting);
		}
		
		/**
		 * Get Data
		 */
//		List<MonthlyModifyResult> results = new ArrayList<>();
		List<Integer> attdanceIds = screenDto.getParam().getLstAtdItemUnique().keySet().stream()
				.collect(Collectors.toList());
		// loc lai list item cua tung nhan vien theo cac item lay duoc truoc do
		results.forEach(r -> {
			r.setItems(r.getItems().stream().filter(i -> attdanceIds.contains(i.getItemId())).collect(Collectors.toList()));
		});
		if (results.size() > 0) {
			screenDto.getItemValues().addAll(results.get(0).getItems());
		}
		Map<String, MonthlyModifyResult> employeeDataMap = results.stream()
				.collect(Collectors.toMap(x -> x.getEmployeeId(), Function.identity(), (x, y) -> x));

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
		
		for (int i = 0; i < screenDto.getLstEmployee().size(); i++) {
			MonthlyPerformanceEmployeeDto employee = screenDto.getLstEmployee().get(i);
			String employeeId = employee.getId();
			MonthlyModifyResult rowData = employeeDataMap.get(employeeId);
			if (rowData == null) continue; //neu khong co data cua nhan vien thi bo qua
			
			String lockStatus = lockStatusMap.isEmpty() || !lockStatusMap.containsKey(employee.getId()) || param.getInitMenuMode() == 1 ? ""
					: lockStatusMap.get(employee.getId()).getLockStatusString();
			
			// set dailyConfirm
			MonthlyPerformaceLockStatus monthlyPerformaceLockStatus = lockStatusMap.get(employeeId);
			String dailyConfirm = null;
			List<String> listCss = new ArrayList<>();
			listCss.add("daily-confirm-color");
			if (monthlyPerformaceLockStatus != null) {
				if (monthlyPerformaceLockStatus.getMonthlyResultConfirm() == LockStatus.LOCK) {
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
			//boolean identify = listConfirmationMonth.stream().filter(x->x.getEmployeeId().equals(employeeId)).findFirst().isPresent() ;
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
						performanceLockStatus.stream().filter(p -> p.getEmployeeId().equals(employee.getId()) && (p.getPastPerformaceLock() == LockStatus.LOCK || p.getMonthlyResultLock() == LockStatus.LOCK)).forEach(p -> {
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
				}
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
						performanceLockStatus.stream().filter(p -> p.getEmployeeId().equals(employee.getId()) && (p.getPastPerformaceLock() == LockStatus.LOCK || p.getMonthlyResultLock() == LockStatus.LOCK)).forEach(p -> {
							lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
						});
					}
				}
				if(!checkExist) {
					lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
				}
			}	
			   
			MPDataDto mpdata = new MPDataDto(employeeId, lockStatus, "", employee.getCode(), employee.getBusinessName(),
					employeeId, "", identify, approve, dailyConfirm, "");
			mpdata.setVersion(rowData.getVersion());
			// lock check box1 identify
//			if (!employeeIdLogin.equals(employeeId) || param.getInitMenuMode() == 2 
//					|| ((!StringUtil.isNullOrEmpty(lockStatus, true)) && (approvalProcessingUseSetting.getUseMonthApproverConfirm() && approve == true))) {
//				lstCellState.add(new MPCellStateDto(employeeId, "identify", Arrays.asList(STATE_DISABLE)));
//			}
			
			
			// Setting data for dynamic column
			List<EditStateOfMonthlyPerformanceDto> newList = editStateOfMonthlyPerformanceDtos.stream()
					.filter(item -> item.getEmployeeId().equals(employeeId)).collect(Collectors.toList());
			if (null != rowData) {
				//mpdata.setVersion(rowData.getVersion());
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
							// neu item loai thoi gian thi format lai dinh dang
							int minute = 0;
							if (item.getValue() != null) {
								Double data= Double.parseDouble(item.getValue());
								minute = data.intValue();
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
						// *3 and *6
						if (param.getInitMenuMode() == 2) { // mode approve disable neu co bat ki lock nao khac month approve lock
							//if (!StringUtil.isNullOrEmpty(lockStatus, true) && !lockStatus.equals(MonthlyPerformaceLockStatus.LOCK_MONTHLY_APPROVAL))
							if (!StringUtil.isNullOrEmpty(lockStatus, true))
								cellStatus.add(STATE_DISABLE);	
							
						} else { // mode khac cu co lock la approve
							if (!StringUtil.isNullOrEmpty(lockStatus, true))
								cellStatus.add(STATE_DISABLE);
						}
						// Cell Data
						lstCellState.add(new MPCellStateDto(employeeId, attendanceKey, cellStatus));

						Optional<EditStateOfMonthlyPerformanceDto> dto = newList.stream()
								.filter(item2 -> item2.getAttendanceItemId().equals(itemId)).findFirst();
						if (dto.isPresent()) { // set mau sua tay cho cell
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
		
		// set state hide control
		screenDto.setMPSateCellHideControl(mPSateCellHideControls);
		//get histtory into company
		List<AffCompanyHistImport> listAffCompanyHistImport = screenDto.getLstAffComHist();
		List<CheckEmpEralOuput> listCheckEmpEralOuput = checkDailyPerError.checkDailyPerError(employeeIds,
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
					&& optMonthlyPerformanceEmployeeDto.get().getLstAffComHistItem().size() > 0){
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
			}
		});
		screenDto.setLstData(listData);

	}


	private String mergeString(String... x) {
		return StringUtils.join(x);
	}
	public void createFixedHeader(MonthlyPerformanceCorrectionDto screenDto, Integer yearMonth, Integer closureId,
			ApprovalProcessingUseSetting approvalProcessingUseSetting, String mess) {
		/**
		 * Create Grid Sheet DTO
		 */

		MPControlDisplayItem displayItem = screenDto.getLstControlDisplayItem();

		List<MPHeaderDto> lstMPHeaderDto = MPHeaderDto.GenerateFixedHeader();
		
		//G7 G8 G9 hidden column identitfy, approval, dailyconfirm
		for (Iterator<MPHeaderDto> iter = lstMPHeaderDto.listIterator(); iter.hasNext(); ) {
			MPHeaderDto mpHeaderDto = iter.next();
			if ("identify".equals(mpHeaderDto.getKey())
					&& screenDto.getIdentityProcess().getUseMonthSelfCK() == 0) {
		        iter.remove();
		        continue;
		    }
			if ("approval".equals(mpHeaderDto.getKey())
					&& approvalProcessingUseSetting.getUseMonthApproverConfirm() == false) {
				iter.remove();
				continue;
			}
			if ("dailyconfirm".equals(mpHeaderDto.getKey())
					&& screenDto.getDailySelfChkDispAtr() == 0) {
		        iter.remove();
		        continue;
		    }
		}
		List<MPHeaderDto> lstHeader = new ArrayList<>();
		lstHeader.addAll(lstMPHeaderDto);
		displayItem.setLstHeader(lstHeader);
		// Fixed header
		screenDto.setLstFixedHeader(lstMPHeaderDto);
		screenDto.setMess(mess);
		}
}
