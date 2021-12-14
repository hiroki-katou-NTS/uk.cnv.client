package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.task.tran.TransactionService;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.error.ThrowableAnalyzer;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.approvalmanagement.ApprovalProcessingUseSetting;
import nts.uk.ctx.at.record.dom.approvalmanagement.repository.ApprovalProcessingUseSettingRepository;
import nts.uk.ctx.at.record.dom.daily.DailyRecordAdUpService;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDaily;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.AchievementAtr;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.GetPeriodCanProcesse;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.creationprocess.getperiodcanprocesse.IgnoreFlagDuringLock;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.checkprocessed.CheckProcessed;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.checkprocessed.OutputCheckProcessed;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.checkprocessed.StatusOutput;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyresults.ProcessState;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.organization.EmploymentHistoryImported;
import nts.uk.ctx.at.record.dom.organization.adapter.EmploymentAdapter;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLock;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.ActualLockRepository;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.DetermineActualResultLock;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.LockStatus;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.PerformanceType;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.IdentityProcessUseSet;
import nts.uk.ctx.at.record.dom.workrecord.identificationstatus.repository.IdentityProcessUseSetRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLog;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.EmpCalAndSumExeLogRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfoRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.TargetPersonRepository;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagement;
import nts.uk.ctx.at.shared.dom.scherec.closurestatus.ClosureStatusManagementRepository;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.CommonCompanySettingForCalc;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.ManagePerCompanySet;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * ドメインサービス：日別計算　（社員の日別実績を計算）
 * @author keisuke_hoshina
 */
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Stateless
public class DailyCalculationEmployeeServiceImpl implements DailyCalculationEmployeeService {

	//*****（未）　以下、日別実績の勤怠情報など、日別計算のデータ更新に必要なリポジトリを列記。
	/** リポジトリ：日別実績の勤怠時間 */
	@Inject
	private IntegrationOfDailyGetter integrationGetter;
	
	//ドメインサービス：計算用ストアド実行用
	@Inject
	private AdTimeAndAnyItemAdUpService adTimeAndAnyItemAdUpService; 
	
	/*日別計算　マネージャークラス*/
	@Inject
	private CalculateDailyRecordServiceCenter calculateDailyRecordServiceCenter;
	
	/*〆状態*/
	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepository;
	
	@Inject
	private CommonCompanySettingForCalc commonCompanySettingForCalc;
	
	/** リポジトリ：対象者ログ */
	@Inject
	private TargetPersonRepository targetPersonRepository;
	
	/** リポジトリ：就業計算と集計実行ログ */
	@Inject
	private EmpCalAndSumExeLogRepository empCalAndSumExeLogRepository;
	
	@Inject
	/*並列処理*/
	private ManagedParallelWithContext parallel;
	
	@Inject
	/*暫定データ登録*/
	private InterimRemainDataMngRegisterDateChange interimData;
	
	@Inject
	/*エラメッセージ情報登録*/
	private ErrMessageInfoRepository errMessageInfoRepository;
	
	@Inject
	//日別実績(WORK)更新処理まとめ
	private DailyRecordAdUpService dailyRecordAdUpService;
	
	@Inject
	private ApprovalProcessingUseSettingRepository approvalSettingRepo;
	
	@Inject
	private IdentityProcessUseSetRepository identityProcessUseRepository;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;
	
	@Inject
	private DetermineActualResultLock lockStatusService;
	
//	@Inject
//	private ClosureService closureService;
	
	@Inject
	private EmploymentAdapter employmentAdapter;
	
	@Inject
	private CheckProcessed checkProcessed;
	
	@Inject
	private RecordDomRequireService requireService;
	
	@Inject
	private ClosureStatusManagementRepository closureStatusManagementRepo;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ActualLockRepository actualLockRepository;
	
	@Inject
	private ShareEmploymentAdapter employmentAdapterShare;
	
	@Inject
	private TransactionService transactionService;
	/**
	 * 社員の日別実績を計算
	 * @param asyncContext 同期コマンドコンテキスト
	 * @param companyId 会社ID
	 * @param employeeId 社員ID
	 * @param datePeriod 期間
	 * @param empCalAndSumExecLogID 就業計算と集計実行ログID
	 * @param executionType 実行種別　（通常、再実行）
	 */
	@Override
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Boolean> calculate(List<String> employeeIds,DatePeriod datePeriod, Consumer<ProcessState> counter,ExecutionType reCalcAtr, String empCalAndSumExecLogID ,boolean isCalWhenLock) {
		
		String cid = AppContexts.user().companyId();
		List<Boolean> isHappendOptimistLockError = new ArrayList<>();
		
		Optional<IdentityProcessUseSet> iPUSOptTemp = identityProcessUseRepository.findByKey(cid);
		Optional<ApprovalProcessingUseSetting> approvalSetTemp = approvalSettingRepo.findByCompanyId(cid);
		
		
		this.parallel.forEach(employeeIds, employeeId -> {
			// Imported（就業）「所属雇用履歴」を取得する (Lấy dữ liệu)
			List<EmploymentHistShareImport> listEmploymentHis = this.employmentAdapterShare.findByEmployeeIdOrderByStartDate(employeeId);

			GetPeriodCanProcesseRequireImpl require = new GetPeriodCanProcesseRequireImpl(closureStatusManagementRepo,
					closureEmploymentRepo, closureRepository, actualLockRepository);
			//実績処理できる期間を取得する
			List<DatePeriod> listPeriod = GetPeriodCanProcesse.get(require, employeeId, datePeriod,
					listEmploymentHis.stream().map(c -> convert(c)).collect(Collectors.toList()),
					isCalWhenLock? IgnoreFlagDuringLock.CAN_CAL_LOCK : IgnoreFlagDuringLock.CANNOT_CAL_LOCK,
					AchievementAtr.DAILY);
			boolean optimistLock = false;
			for(DatePeriod newPeriod : listPeriod) {
				//ドメインモデル「就業計算と集計実行ログ」を取得し、実行状況を確認する
				Optional<EmpCalAndSumExeLog> log = empCalAndSumExeLogRepository.getByEmpCalAndSumExecLogID(empCalAndSumExecLogID);
				if(!log.isPresent()) {
					counter.accept(ProcessState.INTERRUPTION);
					break;
				}
				else {
					val executionStatus = log.get().getExecutionStatus();
					if(executionStatus.isPresent() && executionStatus.get().isStartInterruption()) {
						counter.accept(ProcessState.INTERRUPTION);
						break;
					}
				}
				
				Pair<Integer, ManageProcessAndCalcStateResult> result = null;
				result = runWhenOptimistLockError(cid, employeeId, newPeriod, reCalcAtr, empCalAndSumExecLogID, iPUSOptTemp, approvalSetTemp, false,isCalWhenLock);
				
				if(result.getLeft() == 1) {  //co loi haita
					result = runWhenOptimistLockError(cid, employeeId, newPeriod, reCalcAtr, empCalAndSumExecLogID, iPUSOptTemp, approvalSetTemp, true,isCalWhenLock);
					if(result.getLeft() == 1) { 
						optimistLock = true;
					}
				}
				
				//暫定データの登録
//                this.interimData.registerDateChange(cid, employeeId, newPeriod.datesBetween());
			}
			
			if (!optimistLock) {

				counter.accept(ProcessState.SUCCESS);
				targetPersonRepository.updateWithContent(employeeId, empCalAndSumExecLogID, 1, 0);
			} else {
				isHappendOptimistLockError.add(true);
			}
			
		});
		return isHappendOptimistLockError;
	}
	
	private EmploymentHistoryImported convert(EmploymentHistShareImport employmentHistShareImport) {
		return new EmploymentHistoryImported(employmentHistShareImport.getEmployeeId(), employmentHistShareImport.getEmploymentCode(), employmentHistShareImport.getPeriod());
	}
	
	private Pair<Integer, ManageProcessAndCalcStateResult> runWhenOptimistLockError(String cid, String employeeId,
			DatePeriod datePeriod, ExecutionType reCalcAtr, String empCalAndSumExecLogID,
			Optional<IdentityProcessUseSet> iPUSOptTemp,
			Optional<ApprovalProcessingUseSetting> approvalSetTemp,boolean runOptimistLock,Boolean IsCalWhenLock) {
		//if check = 0 : createListNew : null
		//if check = 1 : has error optimistic lock (lan 1)
		//if check = 2 : done
		Integer check = 2;
		
//		List<Boolean> isHappendOptimistLockError = new ArrayList<>(); 

		List<IntegrationOfDaily> createListNew = integrationGetter.getIntegrationOfDaily(employeeId, datePeriod);
		if (createListNew.isEmpty()) {
			check = 0;
			return Pair.of(check, new ManageProcessAndCalcStateResult(ProcessState.SUCCESS, new ArrayList<>()));
		}

		// 締め一覧取得
		List<ClosureStatusManagement> closureListNew = getClosureList(Arrays.asList(employeeId), datePeriod);

		val afterCalcRecord = calculateDailyRecordServiceCenter.calculateForManageState(createListNew, closureListNew,
				reCalcAtr, empCalAndSumExecLogID);
		
		List<EmploymentHistoryImported> listEmploymentHis = this.employmentAdapter.getEmpHistBySid(cid, employeeId);
		boolean checkNextEmp =false;
		// データ更新
		for (ManageCalcStateAndResult stateInfo : afterCalcRecord.getLst()) {
			if(checkNextEmp) {
				continue;
			}
			OutputCheckProcessed outputCheckProcessed = checkProcessed.getCheckProcessed(stateInfo.getIntegrationOfDaily().getYmd(), listEmploymentHis);
			if(outputCheckProcessed.getStatusOutput() == StatusOutput.NEXT_DAY) continue;
			if(outputCheckProcessed.getStatusOutput() == StatusOutput.NEXT_EMPLOYEE) {
				checkNextEmp = true;
				continue;
			}

			// 締めIDを取得する
			Optional<ClosureEmployment> closureEmploymentOptional = this.closureEmploymentRepository
					.findByEmploymentCD(cid, stateInfo.getIntegrationOfDaily().getAffiliationInfor().getEmploymentCode().v());
			
			LockStatus lockStatus = LockStatus.UNLOCK;
			if(IsCalWhenLock ==null || IsCalWhenLock == false) {
				//アルゴリズム「実績ロックされているか判定する」を実行する (Chạy xử lý)
				//実績ロックされているか判定する
				lockStatus = lockStatusService.getDetermineActualLocked(cid, 
						stateInfo.getIntegrationOfDaily().getYmd(), closureEmploymentOptional.get().getClosureId(), PerformanceType.DAILY);
			}

			if(lockStatus == LockStatus.LOCK) {
				continue;
			}
			try {
				transactionService.execute(() -> {
					val key = Pair.of(stateInfo.integrationOfDaily.getEmployeeId(), stateInfo.integrationOfDaily.getYmd());
					if (afterCalcRecord.getAtomTasks().containsKey(key)) {
						afterCalcRecord.getAtomTasks().get(key).run();
					}
					//実績登録
					updateRecord(stateInfo.integrationOfDaily); 
					//エラーで本人確認と上司承認を解除する
					clearConfirmApproval(stateInfo.integrationOfDaily);
					//計算状態の更新
					upDateCalcState(stateInfo);
					//暫定データの登録
		            this.interimData.registerDateChange(cid, employeeId, Arrays.asList(stateInfo.integrationOfDaily.getYmd()));
				});
			} catch (Exception ex) {
				boolean isOptimisticLock = new ThrowableAnalyzer(ex).findByClass(OptimisticLockException.class)
						.isPresent();
				if (!isOptimisticLock) {
					throw ex;
				}
				check = 1;
				if(runOptimistLock) {
					// create error message
					ErrMessageInfo employmentErrMes = new ErrMessageInfo(employeeId, empCalAndSumExecLogID,
							new ErrMessageResource("024"), EnumAdaptor.valueOf(1, ExecutionContent.class),
							stateInfo.getIntegrationOfDaily().getYmd(),
							new ErrMessageContent(TextResource.localize("Msg_1541")));
				}
			}
		}
		return Pair.of(check, afterCalcRecord);
	}
	
	/**
	 * #108941 
	 * 特定のエラーが発生している社員の確認、承認をクリアする
	 * @param integrationOfDaily
	 * @param iPUSOptTemp
	 * @param approvalSetTemp
	 */
	private void clearConfirmApproval(IntegrationOfDaily integrationOfDaily) {
		dailyRecordAdUpService.removeConfirmApproval(Arrays.asList(integrationOfDaily));
	}

	private void updateRecord(IntegrationOfDaily value) {
 		// データ更新
		if(value.getAttendanceTimeOfDailyPerformance().isPresent()) {
//			employeeDailyPerErrorRepository.removeParam(value.getAttendanceTimeOfDailyPerformance().get().getEmployeeId(), 
//					value.getAttendanceTimeOfDailyPerformance().get().getYmd());
//			determineErrorAlarmWorkRecordService.createEmployeeDailyPerError(value.getEmployeeError());
			AttendanceTimeOfDailyPerformance attdTimeOfDailyPer = new AttendanceTimeOfDailyPerformance(value.getEmployeeId(),value.getYmd(),
									  value.getAttendanceTimeOfDailyPerformance().get());
			Optional<AnyItemValueOfDaily> anyItem = value.getAnyItemValue().isPresent()
					? Optional.of(new AnyItemValueOfDaily(value.getEmployeeId(), value.getYmd(),
							value.getAnyItemValue().get()))
					: Optional.empty();
			this.registAttendanceTime(value.getEmployeeId(),value.getYmd(),
					attdTimeOfDailyPer,anyItem);
		}
		
		if(value.getAffiliationInfor() != null) {
			Pair<String,GeneralDate> pair = Pair.of(value.getEmployeeId(),
					value.getYmd());
			//計算から呼ぶ場合はtrueでいいらしい。保科⇒thanh
			this.dailyRecordAdUpService.adUpEmpError(value.getEmployeeError(), Arrays.asList(pair));			
		}
		
		// 編集状態更新
		if (value.getEditState().size() > 0){
			List<EditStateOfDailyPerformance> editStateList = new ArrayList<>();
			for (EditStateOfDailyAttd editState : value.getEditState()){
				editStateList.add(new EditStateOfDailyPerformance(value.getEmployeeId(), value.getYmd(), editState));
			}
			this.dailyRecordAdUpService.adUpEditState(editStateList);
			this.dailyRecordAdUpService.clearExcludeEditState(editStateList);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void upDateCalcState(ManageCalcStateAndResult stateInfo) {
		stateInfo.getIntegrationOfDaily().getWorkInformation().changeCalcState(CalculationState.Calculated);
//		workInformationRepository.updateByKeyFlush(stateInfo.getIntegrationOfDaily().getWorkInformation());
		WorkInfoOfDailyPerformance data = new WorkInfoOfDailyPerformance(
				stateInfo.getIntegrationOfDaily().getEmployeeId(), stateInfo.getIntegrationOfDaily().getYmd(),
				stateInfo.getIntegrationOfDaily().getWorkInformation());
		data.setVersion(stateInfo.getIntegrationOfDaily().getWorkInformation().getVer());
		dailyRecordAdUpService.adUpWorkInfo(data);
		
	}
	
	/**
	 * 全社員の〆状態リストの取得
	 * @param employeeId 社員ID一覧
	 * @param datePeriod　処理対象期間
	 * @return　〆状態リスト
	 */
	private List<ClosureStatusManagement> getClosureList(List<String> employeeId, DatePeriod datePeriod) {
		return closureStatusManagementRepository.getByIdListAndDatePeriod(employeeId, datePeriod);
	}

	public ProcessState calculateForOnePerson(String employeeId,DatePeriod datePeriod,Optional<Consumer<ProcessState>> counter,String executeLogId,boolean isCalWhenLock ) {
		
		
		// Imported（就業）「所属雇用履歴」を取得する (Lấy dữ liệu)
		List<EmploymentHistShareImport> listEmploymentHisShare = this.employmentAdapterShare.findByEmployeeIdOrderByStartDate(employeeId);
		//実績処理できる期間を取得する
		GetPeriodCanProcesseRequireImpl require = new GetPeriodCanProcesseRequireImpl(closureStatusManagementRepo,
				closureEmploymentRepo, closureRepository, actualLockRepository);
		
		List<DatePeriod> listPeriod = GetPeriodCanProcesse.get(require, employeeId, datePeriod,
				listEmploymentHisShare.stream().map(c -> convert(c)).collect(Collectors.toList()),
				isCalWhenLock ? IgnoreFlagDuringLock.CAN_CAL_LOCK : IgnoreFlagDuringLock.CANNOT_CAL_LOCK,
				AchievementAtr.DAILY);
		for(DatePeriod newPeriod : listPeriod) {
			//実績取得
			List<IntegrationOfDaily> createList = createIntegrationList(Arrays.asList(employeeId),newPeriod);
			//実績が無かった時用のカウントアップ
			if(createList.isEmpty()) 
				return ProcessState.SUCCESS; 
			String cid = AppContexts.user().companyId();
			
			//締め一覧取得
			List<ClosureStatusManagement> closureList = getClosureList(Arrays.asList(employeeId),newPeriod);
			
			ManagePerCompanySet companySet =  commonCompanySettingForCalc.getCompanySetting(); 
			//計算処理
			val afterCalcRecord = calculateDailyRecordServiceCenter.calculateForclosure(createList,companySet ,closureList,executeLogId);
			List<EmploymentHistoryImported> listEmploymentHis = this.employmentAdapter.getEmpHistBySid(cid, employeeId);
			boolean checkNextEmp =false;
			//データ更新
			for(ManageCalcStateAndResult stateInfo : afterCalcRecord.getLst()) {
				// 締めIDを取得する
				if(checkNextEmp) {
					continue;
				}
				OutputCheckProcessed outputCheckProcessed = checkProcessed.getCheckProcessed(stateInfo.getIntegrationOfDaily().getYmd(), listEmploymentHis);
				if(outputCheckProcessed.getStatusOutput() == StatusOutput.NEXT_DAY) continue;
				if(outputCheckProcessed.getStatusOutput() == StatusOutput.NEXT_EMPLOYEE) {
					checkNextEmp = true;
					continue;
				}
							LockStatus lockStatus = LockStatus.UNLOCK;
							if(!isCalWhenLock) {
								Closure closureData = ClosureService.getClosureDataByEmployee(
										requireService.createRequire(), new CacheCarrier(),
										employeeId, stateInfo.getIntegrationOfDaily().getYmd());
								//アルゴリズム「実績ロックされているか判定する」を実行する (Chạy xử lý)
								//実績ロックされているか判定する
								lockStatus = lockStatusService.getDetermineActualLocked(cid, 
										stateInfo.getIntegrationOfDaily().getYmd(),  closureData.getClosureId().value, PerformanceType.DAILY);
							}
							if(lockStatus == LockStatus.LOCK) {
								continue;
							}
				try {
					
					transactionService.execute(() -> {
						val key = Pair.of(stateInfo.integrationOfDaily.getEmployeeId(), stateInfo.integrationOfDaily.getYmd());
						if (afterCalcRecord.getAtomTasks().containsKey(key)) {
							afterCalcRecord.getAtomTasks().get(key).run();
						}
						//実績登録
						updateRecord(stateInfo.integrationOfDaily); 
						//エラーで本人確認と上司承認を解除する
						clearConfirmApproval(stateInfo.integrationOfDaily);
						//計算状態の更新
						upDateCalcState(stateInfo);
						//暫定データの登録
			            this.interimData.registerDateChange(cid, employeeId, Arrays.asList(stateInfo.integrationOfDaily.getYmd()));
					});
					
				} catch (Exception ex) {
					boolean isOptimisticLock = new ThrowableAnalyzer(ex).findByClass(OptimisticLockException.class).isPresent();
					if (!isOptimisticLock) {
						throw ex;
					}
					ErrMessageInfo employmentErrMes = new ErrMessageInfo(employeeId, executeLogId,
							new ErrMessageResource("024"), EnumAdaptor.valueOf(1, ExecutionContent.class), 
							stateInfo.getIntegrationOfDaily().getYmd(),
							new ErrMessageContent(TextResource.localize("Msg_1541")));
					this.errMessageInfoRepository.add(employmentErrMes);
					
				}
			}
			if(afterCalcRecord.ps == ProcessState.INTERRUPTION ) {
				return  ProcessState.INTERRUPTION;
			}
		}
		
		return ProcessState.SUCCESS; 
	}

	/**
	 * 実績データ取得
	 * @param employeeId
	 * @param datePeriod
	 * @return
	 */
	private List<IntegrationOfDaily> createIntegrationList(List<String> employeeId, DatePeriod datePeriod) {
		List<IntegrationOfDaily> returnList = new ArrayList<>();
		for(String empId:employeeId) {
			returnList.addAll(integrationGetter.getIntegrationOfDaily(empId, datePeriod));
		}
		return returnList;
	}

	/**
	 * データ更新
	 * @param attendanceTime 日別実績の勤怠時間
	 */
	private void registAttendanceTime(String empId,GeneralDate ymd,AttendanceTimeOfDailyPerformance attendanceTime, Optional<AnyItemValueOfDaily> anyItem){
		adTimeAndAnyItemAdUpService.addAndUpdate(empId,ymd,Optional.of(attendanceTime), anyItem);	
	}
	
	@AllArgsConstructor
	private class GetPeriodCanProcesseRequireImpl implements GetPeriodCanProcesse.Require {
		@Inject
		private ClosureStatusManagementRepository closureStatusManagementRepo;

		@Inject
		private ClosureEmploymentRepository closureEmploymentRepo;

		@Inject
		private ClosureRepository closureRepository;

		@Inject
		private ActualLockRepository actualLockRepository;

		@Override
		public DatePeriod getClosurePeriod(int closureId, YearMonth processYm) {
			// 指定した年月の期間を算出する
			DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
					ClosureService.createRequireM1(closureRepository, closureEmploymentRepo), closureId, processYm);
			return datePeriodClosure;
		}

		@Override
		public List<ClosureStatusManagement> getAllByEmpId(String employeeId) {
			return closureStatusManagementRepo.getAllByEmpId(employeeId);
		}

		@Override
		public Optional<ClosureEmployment> findByEmploymentCD(String employmentCode) {
			String companyId = AppContexts.user().companyId();
			return closureEmploymentRepo.findByEmploymentCD(companyId, employmentCode);
		}

		@Override
		public Optional<ActualLock> findById(int closureId) {
			String companyId = AppContexts.user().companyId();
			return actualLockRepository.findById(companyId, closureId);
		}

		@Override
		public Closure findClosureById(int closureId) {
			String companyId = AppContexts.user().companyId();
			return closureRepository.findById(companyId, closureId).get();
		}

	}
	
	
}
