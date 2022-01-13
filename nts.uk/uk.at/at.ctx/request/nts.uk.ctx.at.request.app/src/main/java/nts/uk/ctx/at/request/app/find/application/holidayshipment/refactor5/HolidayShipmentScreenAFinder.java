package nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.app.find.application.appabsence.AppAbsenceFinder;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoStartupDto;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.DisplayInforWhenStarting;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.DisplayInformationApplication;
import nts.uk.ctx.at.request.app.find.application.holidayshipment.refactor5.dto.RemainingHolidayInforDto;
import nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetDto;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.EmploymentHistoryImported;
import nts.uk.ctx.at.request.dom.application.common.adapter.workplace.WorkplaceAdapter;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.ApplyWorkTypeOutput;
import nts.uk.ctx.at.request.dom.application.holidayshipment.BreakOutType;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.substituteapplicationsetting.SubstituteHdWorkAppSetRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSetRepository;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.TargetWorkTypeByApp;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectDto;
import nts.uk.ctx.at.shared.app.find.scherec.appreflectprocess.appreflectcondition.vacationapplication.VacationAppReflectOptionDto;
import nts.uk.ctx.at.shared.app.find.worktype.WorkTypeDto;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.OccurrenceDigClass;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.AccumulationAbsenceDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.param.LeaveOccurrDetail;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutManagementDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.PayoutSubofHDManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SubstitutionOfHDManagementData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.substituteworkapplication.SubstituteWorkAppReflectRepository;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.vacationapplication.subleaveapp.SubLeaveAppReflectRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PrescribedTimezoneSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.predset.UseSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.AttendanceHolidayAttr;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeUnit;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author ThanhPV
 */
@Stateless
public class HolidayShipmentScreenAFinder {

	@Inject
	private WorkplaceAdapter wkPlaceAdapter;
	
	@Inject
	private EmpSubstVacationRepository empSubrepo;
	
	@Inject
	private ComSubstVacationRepository comSubrepo;
	
	@Inject
	private WorkTypeRepository wkTypeRepo;
	
	@Inject
	private WorkTimeSettingRepository wkTimeRepo;
	
	@Inject
	private PredetemineTimeSettingRepository preTimeSetRepo;
	
	@Inject
	private WorkingConditionRepository wkingCondRepo;
	
	@Inject
	private WorkingConditionItemRepository wkingCondItemRepo;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private AppAbsenceFinder appAbsenceFinder;
	
	@Inject 
	private AppEmploymentSetRepository appEmploymentSetRepo;
	
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepo;
	
	@Inject
	private SubstituteHdWorkAppSetRepository substituteHdWorkAppSetRepo;
	
	@Inject
	private SubstituteWorkAppReflectRepository substituteWorkAppReflectRepo;
	
	@Inject
	private SubLeaveAppReflectRepository subLeaveAppReflectRepo;
    
    @Inject
    private ComDayOffManaDataRepository comDayOffManaDataRepo;
    
    @Inject
    private LeaveManaDataRepository leaveManaDataRepo;
    
    @Inject
    private ShareEmploymentAdapter shareEmploymentAdapter;
    
    @Inject
    private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
    
    @Inject
    private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
    
    @Inject
    private ClosureEmploymentRepository closureEmploymentRepo;
    
    @Inject
    private ClosureRepository closureRepo;
    
    @Inject
    private EmpEmployeeAdapter empEmployeeAdapter;
    
    @Inject
    private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepo;
    
    @Inject
    private PayoutSubofHDManaRepository payoutSubofHDManaRepo;
    
    @Inject
    private PayoutManagementDataRepository payoutManagementDataRepo;
    
    @Inject
    private EmpSubstVacationRepository empSubstVacationRepo;
    
    @Inject
    private ComSubstVacationRepository comSubstVacationRepo;
    
    @Inject
    private InterimRecAbasMngRepository interimRecAbasMngRepo;
    
    @Inject
    private LeaveComDayOffManaRepository leaveComDayOffManaRepo;
    
    @Inject
    private PayoutSubofHDManaRepository payoutHdManaRepo;
	
	@SuppressWarnings("incomplete-switch")
	public List<TimezoneUse> getTimeZones(String companyID, String wkTimeCode, AttendanceHolidayAttr wkTypeAttendance) {

		List<TimezoneUse> timeZones = new ArrayList<TimezoneUse>();

		// ○就業時間帯を取得
		Optional<WorkTimeSetting> workTimeOpt = wkTimeRepo.findByCode(companyID, wkTimeCode);

		if (workTimeOpt.isPresent()) {

			wkTimeCode = workTimeOpt.get().getWorktimeCode().v();
			// ○所定時間帯を取得
			Optional<PredetemineTimeSetting> preTimeSetOpt = preTimeSetRepo.findByWorkTimeCode(companyID, wkTimeCode);

			if (preTimeSetOpt.isPresent()) {

				PredetemineTimeSetting preTimeSet = preTimeSetOpt.get();

				List<TimezoneUse> timeZonesInPreTimeSet = preTimeSet.getPrescribedTimezoneSetting().getLstTimezone();

				switch (wkTypeAttendance) {
				case MORNING:
					timeZonesInPreTimeSet.stream().forEach(
							x -> x.updateEndTime(preTimeSet.getPrescribedTimezoneSetting().getMorningEndTime()));
					break;
				case AFTERNOON:
					timeZonesInPreTimeSet.stream().forEach(
							x -> x.updateStartTime(preTimeSet.getPrescribedTimezoneSetting().getAfternoonStartTime()));
					break;
				}

				timeZones = timeZonesInPreTimeSet;
			}

		}
		return timeZones;
	}
	
	// 1.振休振出申請（新規）起動処理
	public DisplayInforWhenStarting startPageARefactor(String companyId, List<String> lstEmployee, List<GeneralDate> dateLst, AppDispInfoStartupDto appDispInfoStartup) {
		DisplayInforWhenStarting result = new DisplayInforWhenStarting();
		
		RequireM11Imp requireM11Imp = new RequireM11Imp(comDayOffManaDataRepo, leaveComDayOffManaRepo, leaveManaDataRepo, shareEmploymentAdapter, compensLeaveEmSetRepo, compensLeaveComSetRepo, interimBreakDayOffMngRepo, closureEmploymentRepo, closureRepo, empEmployeeAdapter, substitutionOfHDManaDataRepo, payoutSubofHDManaRepo, payoutManagementDataRepo, empSubstVacationRepo, comSubstVacationRepo, interimRecAbasMngRepo, payoutHdManaRepo);

		
		// 起動時の申請表示情報を取得する (Lấy thông tin hiển thị Application khi  khởi động)
		result.setAppDispInfoStartup(appDispInfoStartup);
		AppDispInfoStartupOutput appDispInfoStartupOutput = appDispInfoStartup.toDomain();
		
		//振休管理チェック (Check quản lý nghỉ bù)
		this.startupErrorCheck(lstEmployee.get(0), appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate(), companyId);
		
		// 振休振出申請設定の取得(get setting đơn xin nghỉ bù làm bù)
		this.getWithDrawalReqSet(companyId, result);
		
		
		//1.振出申請（新規）起動処理(申請対象日関係あり)(Xử lý khời động Application làm bù (New )(có liên quan application ngày đối tượng )
		DisplayInformationApplication applicationForWorkingDay = this.applicationForWorkingDay(
																		companyId,
																		appDispInfoStartupOutput.getAppDispInfoNoDateOutput().getEmployeeInfoLst().get(0).getSid(),
																		appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getBaseDate(),
																		appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(),
																		appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().isPresent() ? appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpWorkTimeLst().get() : new ArrayList<>()
																		);
		result.setApplicationForWorkingDay(applicationForWorkingDay);
		
		//1.振休申請（新規）起動処理(申請対象日関係あり)(Xử lý khời động Application nghỉ bù (New )(có liên quan application ngày đối tượng )
		// chờ QA: 113043 -> Done
		DisplayInformationApplication applicationForHoliday = this.applicationForHoliday(companyId, 
																						appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getEmpHistImport().getEmploymentCode(), 
																						appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().isPresent() ? 
																								appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().isEmpty() ? 
																										Optional.empty() : 
																											appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail().isPresent() ?
																													Optional.of(appDispInfoStartupOutput.getAppDispInfoWithDateOutput().getOpActualContentDisplayLst().get().get(0).getOpAchievementDetail().get().getWorkTypeCD())
																													: Optional.empty()
																								: Optional.empty());
		result.setApplicationForHoliday(applicationForHoliday);
		
		//[No.506]振休残数を取得する ([No.506]Lấy số ngày nghỉ bù còn lại)
		LeaveRemainingDayNumber leaveRemainingDayNumber = AbsenceReruitmentMngInPeriodQuery.getAbsRecMngRemain(
				requireM11Imp, new CacheCarrier(), lstEmployee.get(0),
				GeneralDate.today());

		RemainingHolidayInforDto remainingHolidayInfor = new RemainingHolidayInforDto();
		remainingHolidayInfor.setRemainDays(leaveRemainingDayNumber.v());
		
		result.setRemainingHolidayInfor(remainingHolidayInfor);
		
		//振休の紐付け管理区分を取得する get phân loại quản lý liên kết của nghỉ bù
		Optional<ComSubstVacation> comSubstVacation = comSubrepo.findById(companyId);
		if(comSubstVacation.isPresent()) {
			result.setHolidayManage(comSubstVacation.get().getLinkingManagementATR().value);
		}else {
			result.setHolidayManage(ManageDistinct.NO.value);
		}
		
		//代休の紐付け管理区分を取得する get phân loại quản lý liên kết của nghỉ thay thế
		CompensatoryLeaveComSetting compensatoryLeaveComSetting = compensLeaveComSetRepo.find(companyId);
		if(compensatoryLeaveComSetting == null) {
			result.setSubstituteManagement(ManageDistinct.NO.value);
		}else {
			result.setSubstituteManagement(compensatoryLeaveComSetting.getLinkingManagementATR().value);
		}
		
		return result;
	}
	
	
	/**
	 * @name 振休管理チェック
	 * @param employeeID
	 * @param baseDate
	 * @param companyID
	 */
	private void startupErrorCheck(String employeeID, GeneralDate baseDate, String companyID) {
		// Imported(就業.shared.組織管理.社員情報.所属雇用履歴)「所属雇用履歴」を取得する - Lấy lịch sử employee
		Optional<EmploymentHistoryImported> empImpOpt = wkPlaceAdapter.getEmpHistBySid(companyID, employeeID, baseDate);
		if (empImpOpt.isPresent()) {
			EmploymentHistoryImported empImp = empImpOpt.get();
			String emptCD = empImp.getEmploymentCode();
			// アルゴリズム「振休管理設定の取得」を実行する -  lấy setting của loại đơn
			Optional<EmpSubstVacation> empSubOpt = empSubrepo.findById(companyID, emptCD);
			if (empSubOpt.isPresent()) {
				EmpSubstVacation empSub = empSubOpt.get();
				//boolean isNotManage = empSub.getSetting().getIsManage().equals(ManageDistinct.NO);
				boolean isNotManage = empSub.getManageDistinct().equals(ManageDistinct.NO);
				if (isNotManage) {
					throw new BusinessException("Msg_323");
				}
			} else {
				Optional<ComSubstVacation> comSubOpt = comSubrepo.findById(companyID);
				/*boolean isNoComSubOrNotManage = comSubOpt.isPresent()
						&& comSubOpt.get().getSetting().getIsManage().equals(ManageDistinct.NO);*/
				boolean isNoComSubOrNotManage = comSubOpt.isPresent()
						&& comSubOpt.get().getManageDistinct().equals(ManageDistinct.NO);
				if (isNoComSubOrNotManage) {
					throw new BusinessException("Msg_323");
				}
			}
		}
	}
	
	//1.振出申請（新規）起動処理(申請対象日関係あり)
	public DisplayInformationApplication applicationForWorkingDay(String companyId, String employeeId, GeneralDate baseDate, String employmentCode, List<WorkTimeSetting> workTimeLst) {
	
		DisplayInformationApplication result = new DisplayInformationApplication();
		
		//社員の労働条件を取得する(Lấy điều kiện lao động của employee)
		Optional<WorkingConditionItem> workingConditionItem = WorkingConditionService.findWorkConditionByEmployee(this.createImp(), employeeId, baseDate);
		
		//取得した労働条件項目．区分別勤務．平日時．就業時間帯コードは、INPUT．就業時間帯の設定の一覧に存在するかチェックする(Check xem 'Item điều kiện lao đông.Work by classification. Weekday time.WorktimeCode đã lấy' có tồn tại ở trong INPUT.WorktimeSettingList hay không?)
		Optional<String> workTime = workingConditionItem.isPresent()?workingConditionItem.get().getWorkCategory().getWorkTime().getWeekdayTime().getWorkTimeCode().map(x -> x.v()): Optional.empty();
		if(workTime.isPresent() && workTimeLst.stream().map(c->c.getWorktimeCode().v()).filter(c->c.equals(workTime.get())).findAny().isPresent()) {
			//振出申請起動時の表示情報．初期選択就業時間帯=取得した労働条件項目．区分別勤務．平日時．就業時間帯コード(DisplayInfo khi khoi dong don xin lam bu. InitialSelectionWorktime =  Item điều kiện lao đông.Work by classification. Weekday time.WorktimeCode da lay)
			result.setWorkTime(workTime.get());
		}else {
	        //振出申請起動時の表示情報．初期選択就業時間帯=INPUT．就業時間帯の設定の一覧の先頭の就業時間帯(DisplayInfo khi khơi dong don xin lam bu. InitialSelectionWorktime= INPUT. worktime dau tien cua WorktimeSettingList
	        if(!workTimeLst.isEmpty()){
	        	result.setWorkTime(workTimeLst.get(0).getWorktimeCode().v());
	        }
		}
		
		//振出用勤務種類の取得(Lấy worktype của làm bù)
		List<WorkType> workTypeForWorkingDay = this.getWorkTypeForWorkingDay(companyId, employmentCode, null);
		
		//振出申請起動時の表示情報．勤務種類リスト=取得した振出用勤務種類(List)//(DisplayInfo khi khởi động đơn xin làm bu. WorktypeList= worktype của làm bù đã lấy(List))
		result.setWorkTypeList(workTypeForWorkingDay.stream().map(c->WorkTypeDto.fromDomain(c)).collect(Collectors.toList()));
		
		if(!workTypeForWorkingDay.isEmpty()) {
			//振出申請起動時の表示情報．初期選択勤務種類=取得した振出用勤務種類(List)の先頭の勤務種類 /(DisplayInfo khi khởi động đơn xin làm bù. InitialSelectionWorkType= worktype đầu tiên của worktype làm bù(list) đã lấy)
			result.setWorkType(workTypeForWorkingDay.get(0).getWorkTypeCode().v());
		}
		//勤務時間初期値の取得(lấy giá trị khởi tạo worktime)
		PrescribedTimezoneSetting prescribedTimezoneSetting = appAbsenceFinder.initWorktimeCode(companyId, result.getWorkType(), result.getWorkTime());
		if(prescribedTimezoneSetting != null) {
			for (TimezoneUse time : prescribedTimezoneSetting.getLstTimezone()) {
				if(time.getWorkNo() == 1 && time.getUseAtr()==UseSetting.USE) {
					//振出申請起動時の表示情報．開始時刻=取得した時間帯(使用区分付き)．開始 (DisplayInfo khi khởi động đơn xin làm bù. StartTime= TimeSheet with UseAtr. StartTime đã lấy)
					result.setStartTime(time.getStart().v());
					//振出申請起動時の表示情報．終了時刻=取得した時間帯(使用区分付き)．終了(DisplayInfo khi khởi động đơn xin làm bù. EndTime= TimeSheet withUseAtr. EndTime đã lấy)
					result.setEndTime(time.getEnd().v());
				}
				if(time.getWorkNo() == 2 && time.getUseAtr()==UseSetting.USE) {
					//振出申請起動時の表示情報．開始時刻=取得した時間帯(使用区分付き)．開始 (DisplayInfo khi khởi động đơn xin làm bù. StartTime= TimeSheet with UseAtr. StartTime đã lấy)
					result.setStartTime2(time.getStart().v());
					//振出申請起動時の表示情報．終了時刻=取得した時間帯(使用区分付き)．終了(DisplayInfo khi khởi động đơn xin làm bù. EndTime= TimeSheet withUseAtr. EndTime đã lấy)
					result.setEndTime2(time.getEnd().v());
				}
			}
		}
		return result;
	}
	
	//振出用勤務種類の取得
	public List<WorkType> getWorkTypeForWorkingDay(String companyId, String employmentCode, String wkTypeCD) {
		List<WorkType> result = wkTypeRepo.findWorkTypeForShorting(companyId);
		
		// アルゴリズム「対象勤務種類の抽出」を実行する
		List<WorkType> outputWkTypes = this.extractTargetWkTypes(companyId, employmentCode, BreakOutType.WORKING_DAY.value, result);

		boolean isWkTypeCDNotNullOrEmpty = !StringUtils.isEmpty(wkTypeCD);
		if (isWkTypeCDNotNullOrEmpty ) {
			// アルゴリズム「申請済み勤務種類の存在判定と取得」を実行する
			ApplyWorkTypeOutput applyWorkType = commonAlgorithm.appliedWorkType(companyId, outputWkTypes, wkTypeCD);
			return applyWorkType.getWkTypes();
		}
		// sort by CD
		List<WorkType> disOrderList = outputWkTypes.stream().filter(w -> w.getDispOrder() != null)
				.sorted(Comparator.comparing(WorkType::getDispOrder)).collect(Collectors.toList());

		List<WorkType> wkTypeCDList = outputWkTypes.stream().filter(w -> w.getDispOrder() == null)
				.sorted(Comparator.comparing(WorkType::getWorkTypeCode)).collect(Collectors.toList());

		disOrderList.addAll(wkTypeCDList);
		return disOrderList;

	}

	//アルゴリズム「対象勤務種類の抽出」を実行する(Thực hiện thuật toán [trích xuất worktype])
	public List<WorkType> extractTargetWkTypes(String companyID, String employmentCode, int breakOutType, List<WorkType> wkTypes) {
		// ドメインモデル「申請別対象勤務種類」を取得する
		// AppEmploymentRepository change return method to Optional<AppEmploymentSetting>
		Optional<AppEmploymentSet> appEmploymentSet = appEmploymentSetRepo.findByCompanyIDAndEmploymentCD(companyID, employmentCode);
		if (appEmploymentSet.isPresent() && appEmploymentSet.get().getTargetWorkTypeByAppLst() != null) {
			Optional<TargetWorkTypeByApp> targetWorkTypeByApp = appEmploymentSet.get().getTargetWorkTypeByAppLst()
					.stream().filter(c -> 
							c.getOpBreakOrRestTime().map(d -> d.value == breakOutType).orElse(false)
							&& c.getAppType().value == ApplicationType.COMPLEMENT_LEAVE_APPLICATION.value
							&& c.isDisplayWorkType())
					.findFirst();
			if(targetWorkTypeByApp.isPresent()) {
				wkTypes.removeIf(c->!targetWorkTypeByApp.get().getWorkTypeLst().contains(c.getWorkTypeCode().v()));
			}
		} 
		return wkTypes;
	
	}

	/**
	 * @name 1.振休申請（新規）起動処理(申請対象日関係あり)
	 * @param companyId
	 * @param employmentCode
	 * @param workTypeCD
	 * @return
	 */
	public DisplayInformationApplication applicationForHoliday(String companyId, String employmentCode, Optional<String> workTypeCD) {
		DisplayInformationApplication result = new DisplayInformationApplication();
		//振休用勤務種類の取得(Lấy worktype nghỉ bù)
		List<WorkType> workTypeForHoliday = this.getWorkTypeForHoliday(companyId, employmentCode, null);
		
		//振休申請起動時の表示情報．勤務種類リスト=取得した振休用勤務種類(List) (DisplayInfo khi khởi động đơn xin nghỉ bù. WorkTypeList= worktype nghỉ bù (List) đã lấy)
		result.setWorkTypeList(workTypeForHoliday.stream().map(c->WorkTypeDto.fromDomain(c)).collect(Collectors.toList()));
		
		//振出日の休日区分により振休の勤務種類を取得する(Acquisition of the work type of the holiday based on the holiday classification of the withdrawal date)
		result.setWorkType(this.getWorkTypeOfTheHoliday(companyId, workTypeCD, workTypeForHoliday).orElse(null));
		
		return result;
	}
	
	/**
	 * @name 振出日の休日区分により振休の勤務種類を取得する
	 * @param companyId
	 * @param workTypeCD 振出日の勤務種類 (optional)
	 * @param workTypeForHoliday 振休用勤務種類(List)：1日の勤務
	 * @return
	 */
	public Optional<String> getWorkTypeOfTheHoliday(String companyId, Optional<String> workTypeCD, List<WorkType> workTypeForHoliday) {
		//INPUT．振出日の勤務種類をチェックする(INPUT. Check the type of work on the day of withdrawal)
		if(workTypeCD.isPresent()) {
			//ドメインモデル「勤務種類」を取得する(Get domain model "work type")
			Optional<WorkType> wkTypeOpt = wkTypeRepo.findByPK(companyId, workTypeCD.get());
			if(wkTypeOpt.isPresent()) {
				//INPUT．振休用勤務種類を先頭から最後へループする(INPUT. Loop from the start type to the end)
				for (WorkType workType : workTypeForHoliday.stream().filter(c->c.getDailyWork().getWorkTypeUnit() == WorkTypeUnit.OneDay).collect(Collectors.toList())) {
					if(!workType.getWorkTypeSetList().isEmpty() 
						&& !wkTypeOpt.get().getWorkTypeSetList().isEmpty() 
						&& workType.getWorkTypeSetList().get(0).getHolidayAtr() == wkTypeOpt.get().getWorkTypeSetList().get(0).getHolidayAtr()) {
						return Optional.of(workType.getWorkTypeSetList().get(0).getWorkTypeCd().toString());
					}
				}
			}
		}
		return Optional.empty();
	}
	
	//一番近い期限日を取得する
	public Optional<GeneralDate> getClosestDeadline(List<AccumulationAbsenceDetail> lstAbsRecMng) {
		GeneralDate result = GeneralDate.max();
		for (AccumulationAbsenceDetail absRecDetailPara : lstAbsRecMng) {
			if(absRecDetailPara.getOccurrentClass() == OccurrenceDigClass.DIGESTION) {
				continue;
			}
			LeaveOccurrDetail detail = (LeaveOccurrDetail)absRecDetailPara;
			if(detail.getDeadline().before(result)) {
				result = detail.getDeadline();
			}
		}
		return result.after(GeneralDate.today().addMonths(3)) ? Optional.empty() : Optional.of(result);
	}
	
	//振出用勤務種類の取得
	public List<WorkType> getWorkTypeForHoliday(String companyId, String employmentCode, String wkTypeCD) {
		List<WorkType> result = wkTypeRepo.findWorkTypeForPause(companyId);
		
		// アルゴリズム「対象勤務種類の抽出」を実行する
		List<WorkType> outputWkTypes = this.extractTargetWkTypes(companyId, employmentCode, BreakOutType.HOLIDAY.value, result);

		boolean isWkTypeCDNotNullOrEmpty = !StringUtils.isEmpty(wkTypeCD);
		if (isWkTypeCDNotNullOrEmpty ) {
			// アルゴリズム「申請済み勤務種類の存在判定と取得」を実行する
			ApplyWorkTypeOutput applyWorkType = commonAlgorithm.appliedWorkType(companyId, outputWkTypes, wkTypeCD);
			return applyWorkType.getWkTypes();
		}
		// sort by CD
		List<WorkType> disOrderList = outputWkTypes.stream().filter(w -> w.getDispOrder() != null)
				.sorted(Comparator.comparing(WorkType::getDispOrder)).collect(Collectors.toList());

		List<WorkType> wkTypeCDList = outputWkTypes.stream().filter(w -> w.getDispOrder() == null)
				.sorted(Comparator.comparing(WorkType::getWorkTypeCode)).collect(Collectors.toList());

		disOrderList.addAll(wkTypeCDList);
		return disOrderList;

	}
	
	/**
	 * 振休振出申請設定の取得
	 * @param companyID 会社ID
	 * @return
	 */
	public void getWithDrawalReqSet(String companyID, DisplayInforWhenStarting result) {
		// ドメインモデル「振休振出申請設定」を取得する
		result.setSubstituteHdWorkAppSet(SubstituteHdWorkAppSetDto.fromDomain(substituteHdWorkAppSetRepo.findSettingByCompany(companyID).get()));
		
		// ドメインモデル「振休申請の反映」を取得する
		result.setWorkInfoAttendanceReflect(VacationAppReflectOptionDto.fromDomain(subLeaveAppReflectRepo.findSubLeaveAppReflectByCompany(companyID).get().getWorkInfoAttendanceReflect()));
		// ドメインモデル「振出申請の反映」を取得する
		result.setSubstituteWorkAppReflect(SubstituteWorkAppReflectDto.fromDomain(substituteWorkAppReflectRepo.findSubWorkAppReflectByCompany(companyID).get()));
		
		
	}
	
	private WorkingConditionService.RequireM1 createImp() {
		
		return new WorkingConditionService.RequireM1() {
			
			@Override
			public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
				return wkingCondItemRepo.getByHistoryId(historyId);
			}
			
			@Override
			public Optional<WorkingCondition> workingCondition(String companyId, String employeeId, GeneralDate baseDate) {
				return wkingCondRepo.getBySidAndStandardDate(companyId, employeeId, baseDate);
			}
		};
	}
	
	@AllArgsConstructor
    public class RequireM11Imp implements BreakDayOffMngInPeriodQuery.RequireM11, AbsenceReruitmentMngInPeriodQuery.RequireM11 {
        private ComDayOffManaDataRepository comDayOffManaDataRepo;
        
        private LeaveComDayOffManaRepository leaveComDayOffManaRepo;
        
        private LeaveManaDataRepository leaveManaDataRepo;
        
        private ShareEmploymentAdapter shareEmploymentAdapter;
        
        private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
        
        private CompensLeaveComSetRepository compensLeaveComSetRepo;
        
        private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
        
        private ClosureEmploymentRepository closureEmploymentRepo;
        
        private ClosureRepository closureRepo;
        
        private EmpEmployeeAdapter empEmployeeAdapter;
        
        private SubstitutionOfHDManaDataRepository substitutionOfHDManaDataRepo;
        
        private PayoutSubofHDManaRepository payoutSubofHDManaRepo;
        
        private PayoutManagementDataRepository payoutManagementDataRepo;
        
        private EmpSubstVacationRepository empSubstVacationRepo;
        
        private ComSubstVacationRepository comSubstVacationRepo;
        
        private InterimRecAbasMngRepository interimRecAbasMngRepo;
        
        private PayoutSubofHDManaRepository payoutHdManaRepo;

        @Override
        public Optional<BsEmploymentHistoryImport> findEmploymentHistory(String companyId, String employeeId,
                GeneralDate baseDate) {
            return shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, baseDate);
        }

        @Override
        public CompensatoryLeaveEmSetting findComLeavEmpSet(String companyId, String employmentCode) {
            return compensLeaveEmSetRepo.find(companyId, employmentCode);
        }

        @Override
        public CompensatoryLeaveComSetting findComLeavComSet(String companyId) {
            return compensLeaveComSetRepo.find(companyId);
        }

        @Override
        public List<EmploymentHistShareImport> findByEmployeeIdOrderByStartDate(String employeeId) {
            return shareEmploymentAdapter.findByEmployeeIdOrderByStartDate(employeeId);
        }

        @Override
        public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
            return closureEmploymentRepo.findByEmploymentCD(companyID, employmentCD);
        }

        @Override
        public List<SharedSidPeriodDateEmploymentImport> employmentHistory(CacheCarrier cacheCarrier, List<String> sids,
                DatePeriod datePeriod) {
            return shareEmploymentAdapter.getEmpHistBySidAndPeriodRequire(cacheCarrier, sids, datePeriod);
        }

        @Override
        public List<Closure> closure(String companyId) {
            return closureRepo.findAll(companyId);
        }

        @Override
        public EmployeeImport employee(CacheCarrier cacheCarrier, String empId) {
            return empEmployeeAdapter.findByEmpIdRequire(cacheCarrier, empId);
        }

        @Override
        public List<PayoutSubofHDManagement> getPayoutSubWithDateUse(String sid, GeneralDate dateOfUse,
                GeneralDate baseDate) {
            return payoutHdManaRepo.getWithDateUse(sid, dateOfUse, baseDate);
        }

        @Override
        public List<PayoutSubofHDManagement> getPayoutSubWithOutbreakDay(String sid, GeneralDate outbreakDay,
                GeneralDate baseDate) {
            return payoutHdManaRepo.getWithOutbreakDay(sid, outbreakDay, baseDate);
        }

        @Override
        public List<LeaveComDayOffManagement> getLeaveComWithDateUse(String sid, GeneralDate dateOfUse,
                GeneralDate baseDate) {
            return leaveComDayOffManaRepo.getLeaveComWithDateUse(sid, dateOfUse, baseDate);
        }

        @Override
        public List<LeaveComDayOffManagement> getLeaveComWithOutbreakDay(String sid, GeneralDate outbreakDay,
                GeneralDate baseDate) {
            return leaveComDayOffManaRepo.getLeaveComWithOutbreakDay(sid, outbreakDay, baseDate);
        }

        @Override
        public Optional<EmpSubstVacation> findEmpById(String companyId, String contractTypeCode) {
            return empSubstVacationRepo.findById(companyId, contractTypeCode);
        }

        @Override
        public Optional<ComSubstVacation> findComById(String companyId) {
            return comSubstVacationRepo.findById(companyId);
        }

        @Override
        public List<InterimAbsMng> getAbsBySidDatePeriod(String sid, DatePeriod period) {
            return interimRecAbasMngRepo.getAbsBySidDatePeriod(sid, period);
        }

        @Override
        public List<InterimRecMng> getRecBySidDatePeriod(String sid, DatePeriod period) {
            return interimRecAbasMngRepo.getRecBySidDatePeriod(sid, period);
        }

        @Override
        public List<Closure> closureActive(String companyId, UseClassification useAtr) {
            return closureRepo.findAllActive(companyId, useAtr);
        }
        
        @Override
        public List<LeaveComDayOffManagement> getDigestOccByListComId(String sid, DatePeriod period) {
            return leaveComDayOffManaRepo.getDigestOccByListComId(sid, period);
        }

        @Override
        public List<InterimDayOffMng> getTempDayOffBySidPeriod(String sid, DatePeriod period) {
            return interimBreakDayOffMngRepo.getDayOffBySidPeriod(sid, period);
        }

        @Override
        public List<CompensatoryDayOffManaData> getFixByDayOffDatePeriod(String sid) {
            return comDayOffManaDataRepo.getBySid(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<InterimBreakMng> getTempBreakBySidPeriod(String sid, DatePeriod period) {
            return interimBreakDayOffMngRepo.getBySidPeriod(sid, period);
        }

        @Override
        public List<LeaveManagementData> getFixLeavByDayOffDatePeriod(String sid) {
            return leaveManaDataRepo.getBySid(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<PayoutSubofHDManagement> getOccDigetByListSid(String sid, DatePeriod date) {
            return payoutSubofHDManaRepo.getOccDigetByListSid(sid, date);
        }

        @Override
        public List<SubstitutionOfHDManagementData> getByYmdUnOffset(String sid) {
            return substitutionOfHDManaDataRepo.getBysiD(AppContexts.user().companyId(), sid);
        }

        @Override
        public List<PayoutManagementData> getPayoutMana(String sid) {
            return payoutManagementDataRepo.getSid(AppContexts.user().companyId(), sid);
        }
    }
}
