package nts.uk.ctx.at.record.dom.dailyprocess.calc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import lombok.val;
import nts.arc.layer.app.command.AsyncCommandHandlerContext;
import nts.uk.ctx.at.record.dom.actualworkinghours.AttendanceTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.actualworkinghours.daily.workrecord.repo.AttendanceTimeByWorkOfDailyRepository;
import nts.uk.ctx.at.record.dom.actualworkinghours.repository.AttendanceTimeRepository;
import nts.uk.ctx.at.record.dom.affiliationinformation.repository.AffiliationInforOfDailyPerforRepository;
import nts.uk.ctx.at.record.dom.affiliationinformation.repository.WorkTypeOfDailyPerforRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.BreakTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.breakorgoout.repository.OutingTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.calculationattribute.repo.CalAttrOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.AttendanceLeavingGateOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.attendanceleavinggate.repo.PCLogOnInfoOfDailyRepo;
import nts.uk.ctx.at.record.dom.daily.optionalitemtime.AnyItemValueOfDailyRepo;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.CreateDailyResultDomainServiceImpl.ProcessState;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.raisesalarytime.repo.SpecificDateAttrOfDailyPerforRepo;
import nts.uk.ctx.at.record.dom.shorttimework.repo.ShortTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.statutoryworkinghours.DailyStatutoryWorkingHours;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.EmployeeDailyPerErrorRepository;
import nts.uk.ctx.at.record.dom.workrecord.erroralarm.condition.service.ErAlCheckService;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionType;
import nts.uk.ctx.at.record.dom.worktime.repository.TemporaryTimeOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.worktime.repository.TimeLeavingOfDailyPerformanceRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * ドメインサービス：日別計算　（社員の日別実績を計算）
 * @author shuichu_ishida
 */
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Stateless
public class DailyCalculationEmployeeServiceImpl implements DailyCalculationEmployeeService {

	/** ドメインサービス：日別実績計算処理　（勤務情報を取得して計算） */
	@Inject
	private CalculateDailyRecordService calculateDailtRecordService;
	
	//*****（未）　以下、日別実績の勤怠情報など、日別計算のデータ更新に必要なリポジトリを列記。
	/** リポジトリ：日別実績の勤怠時間 */
	@Inject
	private AttendanceTimeRepository attendanceTimeRepository;
	
	/** リポジトリ：日別実績の勤務情報 */
	@Inject
	private WorkInformationRepository workInformationRepository;  
	
	/** リポジトリ：日別実績の計算区分 */
	@Inject
	private CalAttrOfDailyPerformanceRepository calAttrOfDailyPerformanceRepository;
	
	/** リポジトリ：日別実績の所属情報 */
	@Inject
	private AffiliationInforOfDailyPerforRepository affiliationInforOfDailyPerforRepository;
	
	/** リポジトリ：日別実績の勤務種別 */
	@Inject
	private WorkTypeOfDailyPerforRepository workTypeOfDailyPerforRepository;
	
	/** リポジトリ：日別実績のPCログオン情報 */
	@Inject
	private PCLogOnInfoOfDailyRepo pcLogOnInfoOfDailyRepo; 
	
	/** リポジトリ:社員の日別実績エラー一覧 */
	@Inject
	private EmployeeDailyPerErrorRepository employeeDailyPerErrorRepository;
	
	/** リポジトリ：日別実績の外出時間帯 */
	@Inject
	private OutingTimeOfDailyPerformanceRepository outingTimeOfDailyPerformanceRepository;
	
	/** リポジトリ：日別実績の休憩時間帯 */
	@Inject
	private BreakTimeOfDailyPerformanceRepository breakTimeOfDailyPerformanceRepository; 
	
	/** リポジトリ：日別実績の作業別勤怠時間 */
	@Inject
	private AttendanceTimeByWorkOfDailyRepository attendanceTimeByWorkOfDailyRepository;
	
	/** リポジトリ：日別実績の出退勤 */
	@Inject
	private TimeLeavingOfDailyPerformanceRepository timeLeavingOfDailyPerformanceRepository;
	
	/** リポジトリ：日別実績の短時間勤務時間帯 */
	@Inject
	private ShortTimeOfDailyPerformanceRepository shortTimeOfDailyPerformanceRepository;
	
	/** リポジトリ：日別実績の特定日区分 */
	@Inject
	private SpecificDateAttrOfDailyPerforRepo specificDateAttrOfDailyPerforRepo;
	
	/** リポジトリ：日別実績の入退門 */
	@Inject
	private AttendanceLeavingGateOfDailyRepo attendanceLeavingGateOfDailyRepo;
	
	/** リポジトリ：日別実績の任意項目 */
	@Inject
	private AnyItemValueOfDailyRepo anyItemValueOfDailyRepo;
	
	/** リポジトリ：日別実績のの編集状態 */
	@Inject
	private EditStateOfDailyPerformanceRepository editStateOfDailyPerformanceRepository;
	
	/** リポジトリ：日別実績の臨時出退勤 */
	@Inject
	private TemporaryTimeOfDailyPerformanceRepository temporaryTimeOfDailyPerformanceRepository;
	
	@Inject 
	private ErAlCheckService determineErrorAlarmWorkRecordService;
	
	
	//リポジトリ：労働条件
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepository;

	//リポジトリ；法定労働
	@Inject
	private DailyStatutoryWorkingHours dailyStatutoryWorkingHours;
	
	
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
	public ProcessState calculate(AsyncCommandHandlerContext asyncContext, String employeeId,
			DatePeriod datePeriod, String empCalAndSumExecLogID, ExecutionType executionType,
			ManagePerCompanySet companyCommonSetting) {
		
		ProcessState status = ProcessState.SUCCESS;
		val dataSetter = asyncContext.getDataSetter();
		
		// 日別実績を取得する
		//*****（未）　期間分をまとめて取得するリポジトリメソッド等をここで使い、読み込んだデータは、最終的にIntegrationへ入れる。
		//*****（未）　データがない日も含めて、毎日ごとに処理するなら、下のループをデータ単位→日単位に変え、Integrationへの取得はループ内で行う。
		List<IntegrationOfDaily> integrationOfDailys = createIntegrationOfDaily(employeeId,datePeriod);

		/*労働条件取得*/
		Map<String,DatePeriod> id = new HashMap<>();
		id.put(employeeId, datePeriod);
		val personalInfo = workingConditionItemRepository.getBySidAndPeriodOrderByStrDWithDatePeriod(id, datePeriod.start(),datePeriod.end());
		//今の日付の労働条件
		Optional<Entry<DateHistoryItem, WorkingConditionItem>> nowCondition = personalInfo.getItemAtDate(datePeriod.start());
		if(!nowCondition.isPresent()) return status;
		companyCommonSetting.setPersonInfo(Optional.of(nowCondition.get().getValue()));
		EmploymentCode nowEmpCode = new EmploymentCode("");
		

		// 取得データ分ループ
		for (IntegrationOfDaily integrationOfDaily : integrationOfDailys) {
			
			// 中断処理　（中断依頼が出されているかチェックする）
			if (asyncContext.hasBeenRequestedToCancel()) {
				asyncContext.finishedAsCancelled();
				return ProcessState.INTERRUPTION;
			}
			
			if(!nowCondition.get().getKey().contains(integrationOfDaily.getAffiliationInfor().getYmd())) {
				//労働条件
				/*社員毎に取得するデータ(労働条件)*/
				nowCondition = personalInfo.getItemAtDate(integrationOfDaily.getAffiliationInfor().getYmd());
				if(!nowCondition.isPresent()) continue;
				//↑で取得したデータのセット
				companyCommonSetting.setPersonInfo(Optional.of(nowCondition.get().getValue()));
			}
			//社員、日付毎に取得した法定労働時間
			if(!integrationOfDaily.getAffiliationInfor().getEmploymentCode().equals(nowEmpCode)) {
				nowEmpCode = integrationOfDaily.getAffiliationInfor().getEmploymentCode(); 
				val dailyUnit = dailyStatutoryWorkingHours.getDailyUnit(AppContexts.user().companyId(),nowEmpCode.toString(), employeeId, datePeriod.start(), nowCondition.get().getValue().getLaborSystem());
				companyCommonSetting.setDailyUnit(dailyUnit);
			}
			
			// アルゴリズム「実績ロックされているか判定する」を実行する
			// ＞ロックされていれば、親に「中断」を返す
			//*****（未）　この判定が必要か、念のため確認要。
			//*****（未）　この処理は、共通処理として作る必要がある。現時点では、日別作成の中にprivateで作られているため、共有できない。
			
			// 対象個人の対象日時点の個人情報を読み込む
			//*****（未）　ここで、個人履歴の各リポジトリを使って、各コードを読み込む。（または、日別実績計算処理の中で、このコード類を取得する？）
			// 職場ID
			String placeId = "dummy";
			// 雇用コード
			String employmentCd = "dummy";
			
			// 計算処理　（勤務情報を取得して計算）
			val value = this.calculateDailtRecordService.calculate(integrationOfDaily,companyCommonSetting);
			/*
			// 状態確認
			//*****（未）　IntegrationOfDailyの中に、boolean error;を置いて、処理内でのエラー有無を返し、ここで、エラー処理につなぐ。
			//*****（未）　メッセージも必要なら、同様に中にメッセージ用メンバを置いて、そこから受け取るのもアリ。
			//*****（未）　中断ボタンの判定は、ここで無くてもよいです。上で判定しているので。人数は、親処理で確認しているので、それも不要。
			if (value.isError()) {
				//*****（未）　画面側の仕様が不明だが、画面にエラーを表示するなら、このタイミングで、セション値として入れて返す。
				dataSetter.updateData("dailyCalculateHasError", "エラーあり");
				asyncContext.finishedAsCancelled();
				return ProcessState.INTERRUPTION;
			}
			*/
			// データ更新
			//*****（未）　日別実績の勤怠情報だけを更新する場合。まとめて更新するなら、integrationOfDailyを入出できるよう調整する。
			if(value.getAttendanceTimeOfDailyPerformance().isPresent()) {
				employeeDailyPerErrorRepository.removeParam(value.getAttendanceTimeOfDailyPerformance().get().getEmployeeId(), 
						value.getAttendanceTimeOfDailyPerformance().get().getYmd());
				this.registAttendanceTime(value.getAttendanceTimeOfDailyPerformance().get());
				determineErrorAlarmWorkRecordService.createEmployeeDailyPerError(value.getEmployeeError());
			}
		}
		return status;
	}

	/**
	 * データ更新
	 * @param attendanceTime 日別実績の勤怠時間
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void registAttendanceTime(AttendanceTimeOfDailyPerformance attendanceTime){

		//*****（未）　この中で、必要なデータ更新処理を書く。下は、仮実装なので、正確な内容は別途確認する事。

		// キー値確認
		val employeeId = attendanceTime.getEmployeeId();
		val ymd = attendanceTime.getYmd();
		//if (this.attendanceTimeRepository.find(employeeId, ymd).isPresent()){
//		if(attendanceTime != null)
			
			// 更新
			this.attendanceTimeRepository.update(attendanceTime);
//		}
//		else {
//			//log.info("更新なんてされずにスルーされます");
//			// 追加
//			//*****（未）　親のフローにより、読み込めないデータは計算しないはずなので、この処理は不要かもしれない。find確認自体不要かも。
//			//this.attendanceTimeRepository.add(attendanceTime);
//		}
	}
	
	/**
	 * 日別実績(WORK)の作成
	 * @param employeeId
	 * @param datePeriod
	 * @return
	 */
	private List<IntegrationOfDaily> createIntegrationOfDaily(String employeeId, DatePeriod datePeriod) {
		val attendanceTimeList= workInformationRepository.findByPeriodOrderByYmd(employeeId, datePeriod);
		
		List<IntegrationOfDaily> returnList = new ArrayList<>();

		for(WorkInfoOfDailyPerformance attendanceTime : attendanceTimeList) {
			/** リポジトリ：日別実績の勤務情報 */
			val workInf = workInformationRepository.find(employeeId, attendanceTime.getYmd());  
			/** リポジトリ：日別実績.日別実績の計算区分 */
			val calAttr = calAttrOfDailyPerformanceRepository.find(employeeId, attendanceTime.getYmd());
			
			/** リポジトリ：日別実績の所属情報 */
			val affiInfo = affiliationInforOfDailyPerforRepository.findByKey(employeeId, attendanceTime.getYmd());

			/** リポジトリ：日別実績の勤務種別 */
			val businessType = workTypeOfDailyPerforRepository.findByKey(employeeId, attendanceTime.getYmd());
			if(!workInf.isPresent() || !affiInfo.isPresent() || !businessType.isPresent())//calAttr == null
				continue;
			returnList.add(
				new IntegrationOfDaily(
					workInf.get(),
					calAttr,
					affiInfo.get(),
					businessType,
					pcLogOnInfoOfDailyRepo.find(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績のPCログオン情報 */
					employeeDailyPerErrorRepository.findByPeriodOrderByYmd(employeeId, datePeriod),/** リポジトリ:社員の日別実績エラー一覧 */
					outingTimeOfDailyPerformanceRepository.findByEmployeeIdAndDate(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の外出時間帯 */
					breakTimeOfDailyPerformanceRepository.findByKey(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の休憩時間帯 */
					attendanceTimeRepository.find(employeeId, attendanceTime.getYmd()),
					attendanceTimeByWorkOfDailyRepository.find(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の作業別勤怠時間 */
					timeLeavingOfDailyPerformanceRepository.findByKey(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の出退勤 */
					shortTimeOfDailyPerformanceRepository.find(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の短時間勤務時間帯 */
					specificDateAttrOfDailyPerforRepo.find(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の特定日区分 */
					attendanceLeavingGateOfDailyRepo.find(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の入退門 */
					anyItemValueOfDailyRepo.find(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績の任意項目 */
					editStateOfDailyPerformanceRepository.findByKey(employeeId, attendanceTime.getYmd()),/** リポジトリ：日別実績のの編集状態 */
					temporaryTimeOfDailyPerformanceRepository.findByKey(employeeId, attendanceTime.getYmd())/** リポジトリ：日別実績の臨時出退勤 */
					));
		}
		return returnList;
	}
}
