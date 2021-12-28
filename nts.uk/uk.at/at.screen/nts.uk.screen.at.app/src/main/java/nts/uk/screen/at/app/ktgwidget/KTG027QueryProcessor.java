package nts.uk.screen.at.app.ktgwidget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.task.parallel.ManagedParallelWithContext;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.auth.dom.adapter.workplace.AuthWorkPlaceAdapter;
import nts.uk.ctx.at.auth.dom.adapter.workplace.WorkplaceInfoImport;
import nts.uk.ctx.at.auth.dom.employmentrole.EmployeeReferenceRange;
import nts.uk.ctx.at.function.dom.adapter.employeebasic.SyEmployeeFnAdapter;
import nts.uk.ctx.at.record.app.find.monthly.root.AgreementTimeOfManagePeriodDto;
import nts.uk.ctx.at.record.dom.approvalmanagement.dailyperformance.algorithm.closure.ClosureHistPeriod;
import nts.uk.ctx.at.record.dom.approvalmanagement.dailyperformance.algorithm.closure.GetSpecifyPeriod;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetAgreementTime;
import nts.uk.ctx.at.record.dom.monthly.agreement.export.GetAgreementTimeOfMngPeriod;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.closure.CurrentClosingPeriodExport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.PersonEmpBasicInfoImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ScheRecAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureResultModel;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.WorkClosureQueryProcessor;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHist;
import nts.uk.ctx.bs.employee.dom.employee.history.AffCompanyHistByEmployee;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsHistRepository;
import nts.uk.ctx.bs.employee.dom.temporaryabsence.TempAbsenceHistory;
import nts.uk.ctx.bs.employee.pub.workplace.master.WorkplacePub;
import nts.uk.screen.at.app.ktgwidget.find.dto.AcquisitionOfOvertimeHoursOfEmployeesDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.AgreementTimeList36;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimeHours;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimeHoursDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimedDisplayForSuperiorsDto;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class KTG027QueryProcessor {
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private AuthWorkPlaceAdapter authWorkPlaceAdapter;

	@Inject
	private WorkClosureQueryProcessor workClosureQueryProcessor;

	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;

	@Inject
	private RecordDomRequireService requireService;

	@Inject
	private OvertimeHoursRepository overtimeHoursRepo;
	
	@Inject
	private TempAbsHistRepository  tempAbsHistRepository;
	
	@Inject
	private GetSpecifyPeriod getSpecifyPeriod;
	
	
	@Inject
	private EmployeeRequestAdapter employeeAdapter;
	
	@Inject
	private WorkplacePub workplacePub;
	
	@Inject
	private SyEmployeeFnAdapter syEmployeeFnAdapter;
	
	 @Inject
	private ManagedParallelWithContext parallel;

	public GeneralDate checkSysDateOrCloseEndDate() {
		// EA luôn trả v�Systemdate
		return GeneralDate.today();
	}

	public int getLoginClosure(GeneralDate referenceDate) {
		String companyID = AppContexts.user().companyId();
		String employeeID = AppContexts.user().employeeId();
		// ログイン�雔�を取得す�
		// (Lấy employeement của login) request list 31
		Optional<BsEmploymentHistoryImport> employmentHistoryImport = shareEmploymentAdapter.findEmploymentHistory(companyID, employeeID, referenceDate);
		if (!employmentHistoryImport.isPresent()) {
			// If fail return closure ID = 1
			return 1;
		} else {
			Optional<ClosureEmployment> closureEmploymentOpt = closureEmploymentRepo.findByEmploymentCD(companyID, employmentHistoryImport.get().getEmploymentCode());
			if (!closureEmploymentOpt.isPresent()) {
				throw new RuntimeException("Not found Employment history by employeeCD:" + employmentHistoryImport.get().getEmploymentCode());
			}
			return closureEmploymentOpt.get().getClosureId();
		}
	}

	public Optional<DatePeriod> getDatePeriod(int targetMonth, int closureID) {
		String companyID = AppContexts.user().companyId();
		List<DatePeriod> optDatePeriod = new ArrayList<>();
		Optional<Closure> optClosure = closureRepository.findByIdAndUseAtr(companyID, closureID, UseClassification.UseClass_Use);
		if (optClosure.isPresent()) {
			optDatePeriod = optClosure.get().getPeriodByYearMonth(new YearMonth(targetMonth));
		}

		return optDatePeriod.isEmpty() ? Optional.empty() : Optional.of(optDatePeriod.get(0));
	}

	public OvertimeHours buttonPressingProcess(int targetMonth, int closureID) {

		String employeeID = AppContexts.user().employeeId();
		String companyID = AppContexts.user().companyId();
		// (Lấy closing period) tu domain Closure
		/*
		 * ouput: Closing period start date End date
		 */
		Optional<DatePeriod> datePeriod = getDatePeriod(targetMonth, closureID);
		if (!datePeriod.isPresent()) {
			throw new BusinessException("Msg_1134");
		}
		// (Lấy workplace trong phạm vi quyền login) = Request List 478
		// referenceDate lấy theo baseDate �xử lý 1
		GeneralDate referenceDate = checkSysDateOrCloseEndDate();
		//全社員参照　＝　しない
		// referEmployee = false
		WorkplaceInfoImport workplaceInfoImport = authWorkPlaceAdapter.getWorkplaceListId(referenceDate, employeeID, false);
		List<String> listWorkPlaceID = workplaceInfoImport.getLstWorkPlaceID();
		Integer employeeRange = workplaceInfoImport.getEmployeeRange(); 
		if (listWorkPlaceID == null ) {
			throw new BusinessException("Msg_1135");
		}
		// (lấy employeement ứng với closing)
		List<ClosureEmployment> listClosureEmployment = closureEmploymentRepo.findByClosureId(companyID, closureID);
		if (listClosureEmployment == null || listClosureEmployment.isEmpty()) {
			throw new BusinessException("Msg_1136");
		}
		List<AgreementTimeList36> data = new ArrayList<>();
		
		// (Lấy target) Lấy request list 335
		List<String> listEmploymentCD = listClosureEmployment.stream().map(c -> c.getEmploymentCD()).collect(Collectors.toList());
		//Dòng code bị comment là code gốc của RQ 335
		//List<String> listEmpID = empEmployeeAdapter.getListEmpByWkpAndEmpt(listWorkPlaceID, listEmploymentCD, datePeriod.get());
		List<String> listEmpID = getListEmpByWkpAndEmpt(listWorkPlaceID, listEmploymentCD, datePeriod.get());
		
		if (listEmpID == null || listEmpID.isEmpty() ) {
			throw new BusinessException("Msg_1137");
		}
		
		if(employeeRange == EmployeeReferenceRange.ONLY_MYSELF.value){
			//取得した対象者にログイン者がい含まれているか判定する
			if(listEmpID.contains(employeeID) == false){
				//含まれていない
				throw new BusinessException("Msg_1137");
			}else{
				listEmpID = new ArrayList<>();
				listEmpID.add(employeeID);
			}
		}
		
		
		data.sort((a, b) ->
			 {
				 if(a.getAfterAppReflect().getAgreementTime()== b.getAfterAppReflect().getAgreementTime()){
					 return a.getEmployeeCD().compareTo(b.getEmployeeCD());
				 }else{
					 return  b.getAfterAppReflect().getAgreementTime()-a.getAfterAppReflect().getAgreementTime();
				 }
			}
		);

		return new OvertimeHours(null, data);
	}


	public List<ClosureResultModel> getListClosure() {
		GeneralDate referenceDate = checkSysDateOrCloseEndDate();
		List<ClosureResultModel> listClosureResultModel = workClosureQueryProcessor.findClosureByReferenceDate(referenceDate);
		return listClosureResultModel;
	}

	// get request 335
	public List<String> getListEmpByWkpAndEmpt(List<String> wkpsId, List<String> lstemptsCode, DatePeriod dateperiod) {
		// lấy List workplace history items từ dateperiod and list workplaceId
		
		List<String> lstEmpIdOfWkp = overtimeHoursRepo.getAffWkpHistItemByListWkpIdAndDatePeriod(dateperiod, wkpsId);
		if (lstEmpIdOfWkp.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<String> lstEmpIdOfEmpt = overtimeHoursRepo.getListEmptByListCodeAndDatePeriod(dateperiod, lstemptsCode);
		if (lstEmpIdOfEmpt.isEmpty()) {
			return Collections.emptyList();
		}
		// lấy list sid chung từ 2 list lstEmpIdOfWkp vs lstEmpIdOfEmpt
		List<String> lstId = lstEmpIdOfWkp.stream().filter(lstEmpIdOfEmpt::contains).collect(Collectors.toList());

		if (lstId.isEmpty()) {
			return Collections.emptyList();
		}
		// lây list Employee từ list sid v� dateperiod
		long startTime2 = System.nanoTime();
		List<AffCompanyHist> lstAffComHist = overtimeHoursRepo.getAffComHisEmpByLstSidAndPeriod(lstId, dateperiod);
		long endTime2 = System.nanoTime();	
		long duration2 = (endTime2 - startTime2) / 1000000; // ms;
		System.out.println("Time113:" + duration2);		
		if (lstAffComHist.isEmpty()) {
			return Collections.emptyList();
		}
		List<AffCompanyHistByEmployee> lstAffComHistByEmp = getAffCompanyHistByEmployee(lstAffComHist);

		// List sid from List<AffCompanyHistByEmployee>
		List<String> lstSidFromAffComHist = lstAffComHistByEmp.stream().map(m -> m.getSId())
				.collect(Collectors.toList());
		
		// (Lấy danh sách sid từ domain 「休職休業履歴」) 		
		List<String> lstSidAbsHist_NoCheckDate = tempAbsHistRepository.getByListSid(lstSidFromAffComHist);
		
		// List sid tồn tại ở lstId nhưng không tồn tại ở list sid
		List<String> result = lstSidFromAffComHist.stream().filter(i -> !lstSidAbsHist_NoCheckDate.contains(i))
				.collect(Collectors.toList());

		// lây list TempAbsenceHistory từ list sid và dateperiod
		List<TempAbsenceHistory> lstTempAbsenceHistory = tempAbsHistRepository.getByListSid(lstSidAbsHist_NoCheckDate,
				dateperiod);

		// List sid from List<TempAbsenceHistory>
		List<String> lstSidFromTempAbsHis = lstTempAbsenceHistory.stream().map(m -> m.getEmployeeId())
				.collect(Collectors.toList());

		if(!lstSidFromTempAbsHis.isEmpty()) {
			result.addAll(lstSidFromTempAbsHis);
		}
		
		if (result.isEmpty()) {
			return Collections.emptyList();
		}
		
		return result;
	}
	public List<AffCompanyHistByEmployee> getAffCompanyHistByEmployee(List<AffCompanyHist> lstAffComHist) {
		if (lstAffComHist.isEmpty()) {
			return Collections.emptyList();
		}
		List<AffCompanyHistByEmployee> result = new ArrayList<>();

		lstAffComHist.forEach(m -> {
			result.addAll(m.getLstAffCompanyHistByEmployee());
		});
		return result;
		
	}


	public OvertimeHoursDto initialActivationArocess(int targetMonth) {
		// 基準日を取得す�
		// (Lấy base date)
		GeneralDate referenceDate = checkSysDateOrCloseEndDate();
		// ログイン��めを取得す�
		// (Lấy closing của login)
		int closureIDInit = getLoginClosure(referenceDate);
		// �め�一覧を取得す�
		// (Lấy list closing) Request 142
		List<ClosureResultModel> listClosureResultModel = workClosureQueryProcessor.findClosureByReferenceDate(referenceDate);
		// 時間外労働時間を取得す�
		// (Lấy work time )
		OvertimeHours overtimeHours = buttonPressingProcess(targetMonth, closureIDInit);
		OvertimeHoursDto reusult = new OvertimeHoursDto(closureIDInit, listClosureResultModel, overtimeHours);
		return reusult;
	}
	
	/**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG027_時間外労働時間の表示(上長用).ユースケース.起動する(Khởi động).システム.起動する
	 * @return
	 * @param currentOrNextMonth
	 */
	public OvertimedDisplayForSuperiorsDto getOvertimeDisplayForSuperiorsDto(int currentOrNextMonth,
			int closingId, String startDate, String endDate, int processingYm) {
		val require = this.requireService.createRequire();
		val cacheCarrier = new CacheCarrier();
		String sID = AppContexts.user().employeeId();
		GeneralDate baseDate = GeneralDate.today();

		// 社員に対応する処理締めを取得する
		Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, sID, baseDate);
		OvertimedDisplayForSuperiorsDto result = OvertimedDisplayForSuperiorsDto.builder().build();

		// 指定した年月の締め期間を取得する
		List<ClosureHistPeriod> lstClosure = this.getSpecifyPeriod.getSpecifyPeriod(closure.getClosureMonth().getProcessingYm());

		if (lstClosure.isEmpty()) {
			return result;
		}

		CurrentClosingPeriodExport closingInformationForCurrentMonth = CurrentClosingPeriodExport.builder()
				.processingYm(processingYm)
				.closureEndDate(endDate)
				.closureStartDate(startDate)
				.build();
		result.setClosingInformationForCurrentMonth(closingInformationForCurrentMonth);
		result.setClosureId(closingId);
		// INPUT．表示年月＝当月表示
		if (currentOrNextMonth == 1) {
			// 対象年月を指定するログイン者の配下社員の時間外時間の取得
			AcquisitionOfOvertimeHoursOfEmployeesDto acquisitionOfOvertimeHoursOfEmployeesDto = this.getAcquisitionOfOvertimeHoursOfEmployeesDto(
					closure.getClosureId().value
					, closure.getClosureMonth().getProcessingYm()
					, Optional.of(lstClosure.get(0).getPeriod()));

			// 上長用の時間外時間表示．配下社員の個人情報＝取得したList＜個人社員基本情報＞
			// 上長用の時間外時間表示．配下社員の時間外時間＝取得したList＜管理期間の36協定時間＞
			result.setOvertimeOfSubordinateEmployees(acquisitionOfOvertimeHoursOfEmployeesDto.getOvertimeOfSubordinateEmployees());
			result.setPersonalInformationOfSubordinateEmployees(
				acquisitionOfOvertimeHoursOfEmployeesDto.getPersonalInformationOfSubordinateEmployees()
			);

		} else {

			// INPUT．表示年月＝翌月表示
			// 指定した年月の期間を算出する
			DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
					this.requireService.createRequire(),
					result.getClosureId(),
					closure.getClosureMonth().getProcessingYm().addMonths(1));

			// 上長用の時間外時間表示．翌月の締め情報．処理年月＝上長用の時間外時間表示．当月の締め情報．処理年月．AddMonth(1)
			// 上長用の時間外時間表示．翌月の締め情報．締め期間＝取得した締め期間
			CurrentClosingPeriodExport closingInformationForNextMonth = CurrentClosingPeriodExport.builder()
					.processingYm(closure.getClosureMonth().getProcessingYm().addMonths(1).v())
					.closureEndDate(datePeriodClosure.end().toString())
					.closureStartDate(datePeriodClosure.start().toString()).build();
			result.setClosingInformationForNextMonth(closingInformationForNextMonth);

			// /対象年月を指定するログイン者の配下社員の時間外時間の取得
			AcquisitionOfOvertimeHoursOfEmployeesDto acquisitionOfOvertimeHoursOfEmployeesDto = this
					.getAcquisitionOfOvertimeHoursOfEmployeesDto(
							closure.getClosureId().value,
							closure.getClosureMonth().getProcessingYm().addMonths(1),
							Optional.ofNullable(datePeriodClosure));

			// 上長用の時間外時間表示．配下社員の個人情報＝取得したList＜個人社員基本情報＞
			// 上長用の時間外時間表示．配下社員の時間外時間＝取得したList＜管理期間の36協定時間＞
			result.setOvertimeOfSubordinateEmployees(acquisitionOfOvertimeHoursOfEmployeesDto.getOvertimeOfSubordinateEmployees());
			result.setPersonalInformationOfSubordinateEmployees(
				acquisitionOfOvertimeHoursOfEmployeesDto.getPersonalInformationOfSubordinateEmployees()
			);
		}

		return result;
	}
	
	/**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG027_時間外労働時間の表示(上長用).アルゴリズム.起動する.対象年月を指定するログイン者の配下社員の時間外時間の取得
	 * @param closureId
	 * @param targetDate
	 * @param referencePeriod
	 * @return
	 */
	public AcquisitionOfOvertimeHoursOfEmployeesDto getAcquisitionOfOvertimeHoursOfEmployeesDto(int closureId,
			YearMonth targetDate, Optional<DatePeriod> referencePeriodParam) {
		String cID = AppContexts.user().companyId();
		String sID = AppContexts.user().employeeId();
		Optional<DatePeriod> referencePeriod;
		if (referencePeriodParam.isPresent()) {
			// 基準期間＝INPUT．基準期間
			referencePeriod = referencePeriodParam;
		} else {
			// 指定した年月の期間を算出する
			DatePeriod datePeriodClosure = ClosureService.getClosurePeriod(
					this.requireService.createRequire()
					, closureId
					, targetDate);
			// 基準期間＝取得した締め期間
			referencePeriod = Optional.ofNullable(datePeriodClosure);
		}

		// [RQ30]社員所属職場履歴を取得
		SWkpHistImport sWkpHistImport = this.employeeAdapter.getSWkpHistByEmployeeID(sID, referencePeriod.get().end());
		
		if (sWkpHistImport == null) {
			return AcquisitionOfOvertimeHoursOfEmployeesDto.builder()
					.personalInformationOfSubordinateEmployees(new ArrayList<PersonEmpBasicInfoImport>())
					.overtimeOfSubordinateEmployees(new ArrayList<AgreementTimeOfManagePeriodDto>())
					.build();
		}

		// [No.573]職場の下位職場を基準職場を含めて取得する
		List<String> lstWorkPlaceId = this.workplacePub.getWorkplaceIdAndChildren(
				cID,
				referencePeriod.get().end(),
				sWkpHistImport.getWorkplaceId());

		// 期間内に特定の職場（List）に所属している社員一覧を取得
		List<String> lstEmployeeId = this.syEmployeeFnAdapter.getListEmployeeId(lstWorkPlaceId, referencePeriod.get());

		// 社員ID(List)から個人社員基本情報を取得
		List<PersonEmpBasicInfoImport> listPersonEmp = this.empEmployeeAdapter.getPerEmpBasicInfo(lstEmployeeId);

		// 指定する年月と指定する社員の時間外時間の取得
		List<AgreementTimeOfManagePeriodDto> listAgreementTimeDetail = this.getOvertimeByEmployeeClones(
				lstEmployeeId
				, targetDate
				, referencePeriod);

		// OUTPUT：
		// List＜個人社員基本情報＞
		// List＜管理期間の36協定時間＞
		AcquisitionOfOvertimeHoursOfEmployeesDto result = AcquisitionOfOvertimeHoursOfEmployeesDto.builder()
				.personalInformationOfSubordinateEmployees(listPersonEmp)
				.overtimeOfSubordinateEmployees(listAgreementTimeDetail).build();

		return result;
	}
	 
	 /**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.KTG027_時間外労働時間の表示(上長用).アルゴリズム.対象年月を指定するログイン者の配下社員の時間外時間の取得.指定する年月と指定する社員の時間外時間の取得
	 * @param lstEmployeeId
	 * @param targetDate
	 * @param endDate
	 * @return
	 */ 
	public List<AgreementTimeOfManagePeriodDto> getOvertimeByEmployee(List<String> lstEmployeeId
																	, YearMonth targetDate
																	, Optional<DatePeriod> datePeriod) {
		val require = this.requireService.createRequire();
		val cacheCarrier = new CacheCarrier();
		GeneralDate baseDate = GeneralDate.today();
		List<AgreementTimeOfManagePeriod> listAgreementTimeDetail = new ArrayList<AgreementTimeOfManagePeriod>();
		
		this.parallel.forEach(lstEmployeeId, empCode -> {
			// 社員に対応する処理締めを取得する
			Closure closure = ClosureService.getClosureDataByEmployee(require, cacheCarrier, empCode, baseDate);
			// [取得したドメインモデル「締め」．当月<=INPUT．対象年月]がtrue
			if (closure != null) {
				if (closure.getClosureMonth().getProcessingYm().lessThanOrEqualTo(targetDate)) {
					// 【NO.333】36協定時間の取得:
					AgreementTimeOfManagePeriod AgreementTimeDetail = GetAgreementTime.get(
							require
							, empCode
							, targetDate
							, new ArrayList<>()
							, targetDate.lastGeneralDate()
							, ScheRecAtr.SCHEDULE);
					listAgreementTimeDetail.add(AgreementTimeDetail);
				} else {
					listAgreementTimeDetail.clear();
					listAgreementTimeDetail
							.addAll(GetAgreementTimeOfMngPeriod.get(require, lstEmployeeId, new YearMonthPeriod(
									datePeriod.get().start().yearMonth(), datePeriod.get().end().yearMonth())));
				}
			}
			
		});

		return listAgreementTimeDetail.stream()
				.map(item -> AgreementTimeOfManagePeriodDto.from(item))
				.collect(Collectors.toList());
	}
	
	/**
	 * clones from UKDesign.UniversalK.就業.KTG_ウィジェット.KTG027_時間外労働時間の表示(上長用).アルゴリズム.対象年月を指定するログイン者の配下社員の時間外時間の取得.指定する年月と指定する社員の時間外時間の取得
	 * for fix performance
	 * @param lstEmployeeId
	 * @param targetDate
	 * @param endDate
	 * @return
	 */ 
	public List<AgreementTimeOfManagePeriodDto> getOvertimeByEmployeeClones(List<String> lstEmployeeId, YearMonth targetDate, Optional<DatePeriod> datePeriod) {
		val require = this.requireService.createRequire();
		val cacheCarrier = new CacheCarrier();
		GeneralDate baseDate = GeneralDate.today();
		List<AgreementTimeOfManagePeriod> listAgreementTimeDetail = new ArrayList<AgreementTimeOfManagePeriod>();

		Map<String, Closure> closureByEmployees = ClosureService.getClosureByEmployees(
				ClosureService.createRequireM7(closureRepository, closureEmploymentRepo, shareEmploymentAdapter),
				cacheCarrier, lstEmployeeId, baseDate);
		List<String> employeeIdLessThanOrEqualTargetDate = new ArrayList<String>();
		List<String> employeeId2 = new ArrayList<String>();
		for (Map.Entry<String, Closure> entry : closureByEmployees.entrySet()) {
			if(entry.getValue() != null) {
				if(entry.getValue().getClosureMonth().getProcessingYm().lessThanOrEqualTo(targetDate)) {
					employeeIdLessThanOrEqualTargetDate.add(entry.getKey());
				}else {
					employeeId2.add(entry.getKey());
				}
			}	
		}

		List<AgreementTimeOfManagePeriod> AgreementTimeDetail = GetAgreementTime.getClones(require, employeeIdLessThanOrEqualTargetDate, targetDate, new ArrayList<>(), targetDate.lastGeneralDate(), ScheRecAtr.SCHEDULE);
		listAgreementTimeDetail.addAll(AgreementTimeDetail);
		
		listAgreementTimeDetail.addAll(GetAgreementTimeOfMngPeriod.get(require, employeeId2, new YearMonthPeriod(
				datePeriod.get().start().yearMonth(), datePeriod.get().end().yearMonth())));

		return listAgreementTimeDetail.stream().map(item -> AgreementTimeOfManagePeriodDto.from(item)).collect(Collectors.toList());
	}
}
