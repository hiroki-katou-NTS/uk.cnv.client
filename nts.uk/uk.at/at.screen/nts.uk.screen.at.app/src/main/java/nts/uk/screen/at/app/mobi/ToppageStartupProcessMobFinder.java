/**
 *
 */
package nts.uk.screen.at.app.mobi;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.management.RuntimeErrorException;

import lombok.AllArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.arc.time.calendar.period.YearMonthPeriod;
import nts.uk.ctx.at.auth.app.find.employmentrole.InitDisplayPeriodSwitchSetFinder;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDto;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.KTGRsvLeaveInfoImport;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.NextAnnualLeaveGrantImport;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.OptionalWidgetAdapter;
import nts.uk.ctx.at.function.dom.employmentfunction.checksdailyerror.ChecksDailyPerformanceErrorRepository;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.ComplileInPeriodOfSpecialLeaveParam;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.empinfo.grantremainingdata.InPeriodOfSpecialLeaveResultInfor;
import nts.uk.ctx.at.record.dom.remainingnumber.specialleave.export.SpecialLeaveManagementService;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.record.dom.standardtime.repository.AgreementOperationSettingRepository;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.ChildCareNursePeriodImport;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.ChildCareNurseRemainingNumberImport;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberCareAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberChildCareAdapter;
import nts.uk.ctx.at.request.dom.adapter.monthly.vacation.childcarenurse.care.GetRemainingNumberNursingAdapter;
import nts.uk.ctx.at.request.dom.adapter.record.remainingnumber.holidayover60h.AggrResultOfHolidayOver60hImport;
import nts.uk.ctx.at.request.dom.adapter.record.remainingnumber.holidayover60h.GetHolidayOver60hRemNumWithinPeriodAdapter;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnLeaveRemainNumberAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnualLeaveGrantImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnualLeaveRemainingNumberImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.ReNumAnnLeaReferenceDateImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.ReNumAnnLeaveImport;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmpEmployeeAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employee.EmployeeImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.EmploymentHistShareImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.adapter.employment.SharedSidPeriodDateEmploymentImport;
import nts.uk.ctx.at.shared.dom.common.Year;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.export.query.AbsenceReruitmentMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecAbasMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.export.InterimRemainMngMode;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.BreakDayOffMngInPeriodQuery;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.export.query.numberremainrange.procwithbasedate.NumberConsecutiveVacation;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
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
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.setting.AgreementOperationSetting;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHoliday;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualPaidLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcess;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AbsenceTenProcessCommon;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.AnnualHolidaySetOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.LeaveSetOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SixtyHourSettingOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.processten.SubstitutionHolidayOutput;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingCategory;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmploymentSettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.EmptYearlyRetentionSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.retentionyearly.RetentionYearlySettingRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.ComSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacation;
import nts.uk.ctx.at.shared.dom.vacation.setting.subst.EmpSubstVacationRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.UseClassification;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureResultModel;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.WorkClosureQueryProcessor;
import nts.uk.ctx.at.shared.pub.workrule.closure.PresentClosingPeriodExport;
import nts.uk.ctx.at.shared.pub.workrule.closure.ShClosurePub;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.NotificationDetailSet;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.NotificationDisplayItem;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.NotificationType;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSet;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.SPTopPageSetRepository;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.TimeStatusDetailsSet;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.TimeStatusDisplayItem;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.TimeStatusType;
import nts.uk.ctx.sys.portal.dom.smartphonetoppageset.Type;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApprovalRootStateRepository;
import nts.uk.screen.at.app.ktgwidget.find.dto.AgreementTimeList36;
import nts.uk.screen.at.app.ktgwidget.find.dto.AgreementTimeOfMonthlyDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.DatePeriodDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.OptionalWidgetInfoMobileDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimeHours;
import nts.uk.screen.at.app.ktgwidget.find.dto.OvertimeHoursDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.RemainingNumber;
import nts.uk.screen.at.app.ktgwidget.find.dto.TimeOT;
import nts.uk.screen.at.app.ktgwidget.find.dto.YearlyHoliday;
import nts.uk.screen.at.app.ktgwidget.find.dto.YearlyHolidayInfo;
import nts.uk.screen.at.app.ktgwidget.ktg004.VacationSetting;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * @author hieult
 *
 */
@Stateless
public class ToppageStartupProcessMobFinder {
	@Inject
	private SPTopPageSetRepository sPTopPageSetRepository;
	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;
//	@Inject
//	private KTG002QueryProcessor kTG002QueryProcessor;
//	@Inject
//	private OptionalWidgetKtgFinder ktg029;
	@Inject
	private ChecksDailyPerformanceErrorRepository checksDailyPerformanceErrorRepo;
//	@Inject
//	private KTG027QueryProcessor kTG027QueryProcessor;
	@Inject
	private OptionalWidgetAdapter optionalWidgetAdapter;
	@Inject
	private SpecialHolidayRepository specialHolidayRepository;
	@Inject
	private ClosureRepository closureRepo;
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;
	@Inject
	private EmpEmployeeAdapter empEmployeeAdapter;
	@Inject
	private ShClosurePub shClosurePub;
	@Inject
	private WorkClosureQueryProcessor workClosureQueryProcessor;
//	@Inject
//	private DailyPerformanceAdapter dailyPerformanceAdapter;
	@Inject
	private ApprovalRootStateRepository approvalRootStateRepository;
	@Inject
	private ApplicationRepository applicationRepository;
	@Inject
	private AgreementOperationSettingRepository agreementOperationSettingRepository;
	@Inject
	private InitDisplayPeriodSwitchSetFinder displayPeriodfinder;
//	@Inject
//	private GetAgreementTimeOfMngPeriod getAgreementTimeOfMngPeriod;

	@Inject
	private RecordDomRequireService requireService;
	
	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepo;

	@Inject
	private PayoutSubofHDManaRepository payoutHdManaRepo;

	@Inject
    private CompensLeaveComSetRepository compensLeaveComSetRepo;
    
    @Inject
    private ComDayOffManaDataRepository comDayOffManaDataRepo;
    
    @Inject
    private LeaveManaDataRepository leaveManaDataRepo;
    
    @Inject
    private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
    
    @Inject
    private InterimBreakDayOffMngRepository interimBreakDayOffMngRepo;
    
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
    private GetHolidayOver60hRemNumWithinPeriodAdapter getHolidayOver60hRemNumWithinPeriodAdapter;

    @Inject
    private GetRemainingNumberChildCareAdapter getRemainingNumberChildCareAdapter;
    
    @Inject
    private GetRemainingNumberCareAdapter getRemainingNumberCareAdapter;
    
    @Inject
    protected AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepo;
    
    @Inject
    private EmploymentSettingRepository employmentSettingRepo;
    
    @Inject
    private NursingLeaveSettingRepository nursingLeaveSettingRepo;
    
    @Inject
    private GetRemainingNumberNursingAdapter getRemainingNumberNursingAdapter;
    
    @Inject
    private AbsenceTenProcessCommon absenceCommon;
    
    @Inject
    private RetentionYearlySettingRepository retentionYearlySettingRepo;
    
    @Inject
    private AnnLeaveRemainNumberAdapter annLeaveRemainNumberAdapter;


	public ToppageStartupDto startupProcessMob() {
		String companyID = AppContexts.user().companyId();
		ToppageStartupDto toppageStartupDto = new ToppageStartupDto();
		// ???????????????????????????????????????????????????????????????????????????????????????????????????
		// Th???c hi???n thu???t to??n "L???y type top page hi???n th??? trong c??ng ty"
		List<SPTopPageSet> listSPTopPageSet = sPTopPageSetRepository.findAll(companyID);

		for (SPTopPageSet spTopPageSet : listSPTopPageSet) {

			if (spTopPageSet.getSmartPhoneTopPageType().getType() == Type.NOTIFICATION) {
				toppageStartupDto.displayNotifiDto = new DisplayNotifiDto(false, false,
						spTopPageSet.getDisplayAtr() == NotUseAtr.USE);
			}
			if (spTopPageSet.getSmartPhoneTopPageType().getType() == Type.TIME_STATUS) {
				// -----------------------Start Date ???? End Date
				// ?????----------------------------------------------------
				toppageStartupDto.ktg029 = new ToppageOptionalWidgetInfoDto(null,
						spTopPageSet.getDisplayAtr() == NotUseAtr.USE);

			}
			if (spTopPageSet.getSmartPhoneTopPageType().getType() == Type.OVERTIME_WORK) {
				toppageStartupDto.overtimeHoursDto = new ToppageOvertimeHoursDto(null,
						spTopPageSet.getDisplayAtr() == NotUseAtr.USE);
			}
		}
		Closure closure = ClosureService.getClosureDataByEmployee(
				ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter),
				new CacheCarrier(),
				AppContexts.user().employeeId(), GeneralDate.today());
		if(closure!=null) {
			toppageStartupDto.closureID = closure.getClosureId().value;
			toppageStartupDto.closureYearMonth = closure.getClosureMonth().getProcessingYm().v();
		} else {
			toppageStartupDto.displayNotifiDto.visible = false;
			toppageStartupDto.ktg029.setVisible(false);
			toppageStartupDto.overtimeHoursDto.setVisible(false);
		}

		return toppageStartupDto;

	}

	public DisplayNotifiDto getDisplayNotif() {
		String companyID = AppContexts.user().companyId();
		DisplayNotifiDto result = displayNoti();

		SPTopPageSet setting = sPTopPageSetRepository.getTopPageSetByCompanyAndType(companyID, Type.NOTIFICATION.value);
		if (setting != null && setting.getSmartPhoneTopPageType().getType() == Type.NOTIFICATION) {
			result.setVisible(setting.getDisplayAtr() == NotUseAtr.USE);
		}
		return result;
	}

	public ToppageOptionalWidgetInfoDto getKTG029() {
		String companyID = AppContexts.user().companyId();
		// DatePeriodDto datePeriod = getCurrentMonth();
		/*
		 * GeneralDate start = datePeriod.getStrCurrentMonth(); GeneralDate end
		 * = datePeriod.getEndCurrentMonth().addMonths(1);
		 */
		boolean view = false;
		String companyId = AppContexts.user().companyId();
		DateRangeDto dateRange = getDateRangeByClsId(companyId);
		OptionalWidgetInfoMobileDto result = getKTG029(dateRange.getStart(), dateRange.getEnd());
		SPTopPageSet setting = sPTopPageSetRepository.getTopPageSetByCompanyAndType(companyID, Type.TIME_STATUS.value);

		if (setting != null && setting.getSmartPhoneTopPageType().getType() == Type.TIME_STATUS) {
			view = setting.getDisplayAtr() == NotUseAtr.USE;
		}

		return new ToppageOptionalWidgetInfoDto(result, view);
	}

	private DateRangeDto getDateRangeByClsId(String companyId) {
		String employmentCode = this.getEmploymentCode();
		Integer closureId = this.getClosureId();
		LocalDate todaydate = LocalDate.now();
		GeneralDate startDate = GeneralDate.localDate(todaydate.withDayOfMonth(1));
		GeneralDate endDate = GeneralDate.localDate(todaydate.with(TemporalAdjusters.lastDayOfMonth()));
		if (!employmentCode.isEmpty() && closureId != null) {
			Optional<Closure> closure = closureRepo.findById(companyId, closureId);
			YearMonth yearmonth = closure.get().getClosureMonth().getProcessingYm();
			// ?????????????????????????????????????????????????????????????????????
			// ?????????????????????????????????
			DatePeriod datePeriod1 = ClosureService.getClosurePeriod(closureId, yearmonth, closure);
			startDate = datePeriod1.start();
			endDate = datePeriod1.end();
		}

		return new DateRangeDto(startDate, endDate);
	}

	public ToppageOvertimeData getOvertimeToppage() {
		String companyID = AppContexts.user().companyId();
		String employeeID = AppContexts.user().employeeId();
		Integer closureId = this.getClosureId();
		List<AgreementTimeToppage> agreementTimeLst = new ArrayList<>();
		Integer dataStatus = 0;
		boolean visible = false;
		SPTopPageSet setting = sPTopPageSetRepository.getTopPageSetByCompanyAndType(companyID, Type.OVERTIME_WORK.value);

		if (setting != null) {
			visible = setting.getDisplayAtr() == NotUseAtr.USE;
		}

		Closure closure = closureRepo.findById(companyID, closureId).get();
		YearMonth targetMonth = closure.getClosureMonth().getProcessingYm();

		// [RQ609]??????????????????????????????????????????????????????????????????????????????
		InitDisplayPeriodSwitchSetDto rq609 = displayPeriodfinder.targetDateFromLogin();

		if (closureId == null) {
			throw new BusinessException("Msg_1134");
		}
		//?????????????????????????????????List????????????ID??????????????????????????????ID????????????????????????????????????????????????
		YearMonth targetMonth_A = rq609.getListDateProcessed().stream()
				.filter(c -> c.getClosureID() == closure.getClosureId().value)
				.collect(Collectors.toList()).get(0).getTargetDate();
		// ???NO.333???36?????????????????????(???NO.333???l???y th???i gian hi???p ?????nh 36)
		/** TODO: 36???????????????????????????????????????????????????????????? */
//		List<AgreementTimeDetail> listAgreementTimeDetail = GetAgreementTime.get(
//				requireService.createRequire(), new CacheCarrier(), companyID, Arrays.asList(employeeID), targetMonth_A, ClosureId.valueOf(closureId));
//
//		if (listAgreementTimeDetail.isEmpty()) {
//			throw new RuntimeException("ListAgreementTimeDetailRQ333 Empty");
//		}
//		for (AgreementTimeDetail agreementTimeDetail : listAgreementTimeDetail) {
//			if (agreementTimeDetail.getErrorMessage().isPresent()) {
//				throw new BusinessException(new RawErrorMessage(agreementTimeDetail.getErrorMessage().get()));
//			}
//		}
//		agreementTimeLst.add(new AgreementTimeToppage(String.valueOf(targetMonth_A),
//				AgreementTimeOfMonthlyDto.fromAgreementTimeOfMonthly(listAgreementTimeDetail.get(0).getConfirmed().get())));

		// ?????????????????????????????????????????????
		int currentOrNextMonth = rq609.getCurrentOrNextMonth();

		// ?????????????????????????????????????????????36??????????????????????????????????????????????????????
		AgreementOperationSetting agreeOpSet = agreementOperationSettingRepository.find(companyID).get();
		YearMonth yearMonth = closure.getClosureMonth().getProcessingYm();
		Year year = new Year(yearMonth.year());
		if (yearMonth.month() < (agreeOpSet.getStartingMonth().value + 1)) {
			year = new Year(yearMonth.year() - 1);
		}
		/** TODO: 36???????????????????????????????????????????????????????????? */
		// ????????????36??????????????????????????????
//		YearMonthPeriod yearMonthPeriod = agreeOpSet.getYearMonthPeriod(year, closure);
//		YearMonthPeriod ymPeriodPast = new YearMonthPeriod(yearMonthPeriod.start(), yearMonth.previousMonth());

		// Parameter??????????????????????????????????????????
		if(currentOrNextMonth == 1) {
//			if(yearMonthPeriod.start().lessThan(yearMonth)){
//				// [NO.612]??????????????????????????????????????????36???????????????????????????
//				List<AgreementTimeToppage> agreementTimeToppageLst =
//						getAgreementTimeOfMngPeriod.getAgreementTimeByMonths(Arrays.asList(employeeID), ymPeriodPast).stream()
//						.map(x -> {
//							AgreementTimeOfMonthlyDto agreementTimeOfMonthlyDto = AgreementTimeOfMonthlyDto
//									.fromAgreementTimeOfMonthly(x.getAgreementTime().getAgreementTime());
//							return new AgreementTimeToppage(x.getYearMonth().toString(), agreementTimeOfMonthlyDto);
//						}).collect(Collectors.toList());
//				agreementTimeLst.addAll(agreementTimeToppageLst);
//				dataStatus = AgreementPastStatus.NORMAL.value;
//			} else {
//				dataStatus = AgreementPastStatus.PRESENT.value;
//			}
		} else {//??????(NextMonth)
			/** TODO: 36???????????????????????????????????????????????????????????? */
			// ???NO.333???36?????????????????????: lay data thang hien tai
//			List<AgreementTimeDetail> listAgreementTimeCur = GetAgreementTime.get(
//					requireService.createRequire(), new CacheCarrier(), companyID, Arrays.asList(employeeID), targetMonth, closure.getClosureId());
//			if (listAgreementTimeCur.isEmpty()) {
//				throw new RuntimeException("ListAgreementTimeDetailRQ333 Empty");
//			}
//			Optional<AgreementTimeDetail> agreementTimeDetailError = listAgreementTimeCur.stream().filter(x -> x.getErrorMessage().isPresent()).findAny();
//			if(agreementTimeDetailError.isPresent()) {
//				dataStatus = AgreementPastStatus.ERROR.value;
//			} else {
//				agreementTimeLst.add(new AgreementTimeToppage(String.valueOf(targetMonth),
//						AgreementTimeOfMonthlyDto.fromAgreementTimeOfMonthly(listAgreementTimeCur.get(0).getConfirmed().get())));
//				if(yearMonthPeriod.start().lessThan(targetMonth)) {
//					// // [NO.612]??????????????????????????????????????????36???????????????????????????: lay data thang qua khu
//					List<AgreementTimeToppage> agreementTimeToppageLst =
//							getAgreementTimeOfMngPeriod.getAgreementTimeByMonths(Arrays.asList(employeeID), ymPeriodPast).stream()
//							.map(x -> {
//								AgreementTimeOfMonthlyDto agreementTimeOfMonthlyDto = AgreementTimeOfMonthlyDto
//										.fromAgreementTimeOfMonthly(x.getAgreementTime().getAgreementTime());
//								return new AgreementTimeToppage(x.getYearMonth().toString(), agreementTimeOfMonthlyDto);
//							}).collect(Collectors.toList());
//					agreementTimeLst.addAll(agreementTimeToppageLst);
//					dataStatus = AgreementPastStatus.NORMAL.value;
//				} else {
//					dataStatus = AgreementPastStatus.NORMAL.value;
//				}
//			}
		}
		/** TODO: 36???????????????????????????????????????????????????????????? */
		return new ToppageOvertimeData(convertAgreementTimeLst(agreementTimeLst, new YearMonthPeriod(targetMonth_A, targetMonth_A)),
//		return new ToppageOvertimeData(convertAgreementTimeLst(agreementTimeLst, new YearMonthPeriod(yearMonthPeriod.start(), targetMonth_A)),
				dataStatus, visible, targetMonth_A.v());
	}

	// x??? l?? output v???i c??c th??ng kh??ng c?? d??? li???u
	private List<AgreementTimeToppage> convertAgreementTimeLst(List<AgreementTimeToppage> agreementTimeLst, YearMonthPeriod ymPeriodPast) {
		List<AgreementTimeToppage> convertLst = new ArrayList<>();
		YearMonth loopYM = ymPeriodPast.start();
		while(loopYM.lessThanOrEqualTo(ymPeriodPast.end())) {
			Optional<AgreementTimeToppage> loopItem = getLoopItem(agreementTimeLst, loopYM);
			if(loopItem.isPresent()) {
				convertLst.add(loopItem.get());
			} else {
				convertLst.add(new AgreementTimeToppage(
						loopYM.toString(),
						new AgreementTimeOfMonthlyDto(0, 0, 0, 0, 0, 0)));
			}
			loopYM = loopYM.nextMonth();
		}
		convertLst.sort(Comparator.comparing(AgreementTimeToppage::getYearMonth).reversed());
		return convertLst;
	}

	private Optional<AgreementTimeToppage> getLoopItem(List<AgreementTimeToppage> agreementTimeLst, YearMonth yearMonth){
		return agreementTimeLst.stream().filter(x -> x.yearMonth.equals(yearMonth.toString())).findAny();
	}

	public ToppageOvertimeHoursDto getDisplayOvertime(int targetMonth) {
		String companyID = AppContexts.user().companyId();
		boolean view = false;
		SPTopPageSet setting = sPTopPageSetRepository.getTopPageSetByCompanyAndType(companyID, Type.TIME_STATUS.value);

		if (setting != null && setting.getSmartPhoneTopPageType().getType() == Type.TIME_STATUS) {
			view = setting.getDisplayAtr() == NotUseAtr.USE;
		}
		return new ToppageOvertimeHoursDto(displayOvertime(targetMonth), view);
	}

	private DisplayNotifiDto displayNoti() {
		// ?????????????????????????????????????????????????????????????????????????????????_
		// Th???c hi???n thu???t to??n "L???y th??ng b??o hi???n th??? t???i c??ng ty "
		String companyID = AppContexts.user().companyId();
		String employeeID = AppContexts.user().employeeId();
		DateRangeDto dateRange = getDateRangeByClsId(companyID);
		DisplayNotifiDto result = new DisplayNotifiDto();
		// OptionalWidgetInfoDto dto = new OptionalWidgetInfoDto();
		// ????????????????????????????????????????????????????????????
		// _L???y domain model"C??i ?????t chi ti???t th??ng b??o"
		// n???u type =0 th?? n?? l?? Notification
		// c??n type = 1 th?? l?? TimeStatus
		Optional<NotificationDetailSet> notiDetailSet = sPTopPageSetRepository.getNotificationDetailSet(companyID,
				Type.NOTIFICATION.value);
		if (notiDetailSet.isPresent()) {
			List<NotificationDisplayItem> listNotificationDisplayItem = notiDetailSet.get().getDetailedItem();
			for (NotificationDisplayItem notificationDisplayItem : listNotificationDisplayItem) {
				if (notificationDisplayItem.getDetailType().value == NotificationType.APPLICATION_FOR_APPROVED.value
						&& notificationDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
					result.KTG002 = existenceDataApproved();
				}
				if (notificationDisplayItem.getDetailType().value == NotificationType.DAILY_ACTUALS_ERROR.value
						&& notificationDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
					// ?????????????????????08.??????????????????????????????????????????????????????_
					// Th???c hi???n thu???t to??n "Hi???n th??? ho???c kh??ng hi???n th??? erro
					// th???c t??ch c??c ng??y 08"
					result.checkDailyErrorA2_2 = checksDailyPerformanceErrorRepo.checked(employeeID,
							dateRange.getStart(), dateRange.getEnd());
				}

			}

			return result;
		} else {
			return result;
		}

	}

	/**
	 * ????????????????????????????????????????????????
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public OptionalWidgetInfoMobileDto getKTG029(GeneralDate startDate, GeneralDate endDate) {
		GeneralDate systemDate = GeneralDate.today();
		String employeeId = AppContexts.user().employeeId();
		String companyId = AppContexts.user().companyId();
		DatePeriod datePeriod = new DatePeriod(startDate, endDate);
		OptionalWidgetInfoMobileDto dataKTG029 = new OptionalWidgetInfoMobileDto();
		
		// ????????????????????????????????????????????????
		VacationSetting vacationSetting = new VacationSetting();
		
		// set visible for 3 type
		dataKTG029.setChildRemainNo(null);
		dataKTG029.setCareLeaveNo(null);
		dataKTG029.setExtraRest(null);
		Optional<TimeStatusDetailsSet> optTimeStatusDetailsSet = sPTopPageSetRepository
				.getTimeStatusDetailsSet(companyId, Type.TIME_STATUS.value);
		
		RequireM11Imp requireM11Imp = new RequireM11Imp(
	    		comDayOffManaDataRepo,
	    		leaveComDayOffManaRepo,
	    		leaveManaDataRepo,
	    		shareEmploymentAdapter,
	    		compensLeaveEmSetRepo,
	    		compensLeaveComSetRepo,
	    		interimBreakDayOffMngRepo,
	    		closureEmploymentRepo,
	    		closureRepo,
	    		empEmployeeAdapter,
	    		substitutionOfHDManaDataRepo,
	    		payoutSubofHDManaRepo,
	    		payoutManagementDataRepo,
	    		empSubstVacationRepo,
	    		comSubstVacationRepo,
	    		interimRecAbasMngRepo,
	    		payoutHdManaRepo, 
	    		annualPaidLeaveSettingRepo, 
	    		employmentSettingRepo, 
	    		retentionYearlySettingRepo);

		if (!optTimeStatusDetailsSet.isPresent()) {
			return null;
		}

		// ????????????????????????????????????????????????????????????????????????????????????
		// _Th???c hi???n thu???t to??n "X??? l?? tr?????c khi hi???n th??? m??n h??nh Selection
		// widget"
		List<TimeStatusDisplayItem> listDetailedItem = optTimeStatusDetailsSet.get().getDetailedItem();
		for (TimeStatusDisplayItem timeStatusDisplayItem : listDetailedItem) {
			if ((timeStatusDisplayItem.getDetailType() == TimeStatusType.NUMBER_REMAINING_YEARS
					|| timeStatusDisplayItem.getDetailType() == TimeStatusType.HALF_DAY_ANNUAL_REST_COUNT
					|| timeStatusDisplayItem.getDetailType() == TimeStatusType.HOURLY_ANNUAL_HOLIDAY_AVAI_LIMIT)
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
			    // 10-1.??????????????????????????????
			    AnnualHolidaySetOutput annualHolidaySetOutput = AbsenceTenProcess.getSettingForAnnualHoliday(requireM11Imp, companyId);
			    
			    if (annualHolidaySetOutput.isYearHolidayManagerFlg()) {
			        // ????????????????????????.?????????????????????????????????true
			        vacationSetting.setAnnualManage(true);
			        
			        // ?????????????????????15.????????????????????????????????????_ TH???c hi???n thu???t to??n "15. Hi???n th???
			        // ngh??? ph??p n??m c??n l???i "
			        // X??? l?? 15
			        dataKTG029.setYearlyHoliday(setYearlyHoliday(companyId, employeeId, systemDate, datePeriod));
			    }
			} else if (timeStatusDisplayItem.getDetailType() == TimeStatusType.ACCUMULATED_ANNUAL_REST
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
			    // 10-4.????????????????????????????????????
			    boolean setForYearlyReserved = AbsenceTenProcess.getSetForYearlyReserved(requireM11Imp, new CacheCarrier(), companyId, employeeId, GeneralDate.today());
			    
			    if (setForYearlyReserved) {
			        // ????????????????????????.???????????????????????????????????????true
			        vacationSetting.setAccumAnnualManage(true);
			        
			        // ?????????????????????16.??????????????????????????????????????????_Th???c hi???n thu???t to??n "16.HI???n th???
			        // ngh??? ph??p n??m c???ng d???n"
			        // X??? l?? 16
			        KTGRsvLeaveInfoImport KTGRsvLeaveInfoImport = optionalWidgetAdapter
			                .getNumberOfReservedYearsRemain(employeeId, systemDate);
			        boolean showAfter = true;
//			        if (KTGRsvLeaveInfoImport.getGrantDay() != null) {
//			            showAfter = startDate.beforeOrEquals(KTGRsvLeaveInfoImport.getGrantDay())
//			                    && endDate.afterOrEquals(KTGRsvLeaveInfoImport.getGrantDay());
//			        }
			        dataKTG029.setReservedYearsRemainNo(new RemainingNumber("", KTGRsvLeaveInfoImport.getRemainingDays(),
			                KTGRsvLeaveInfoImport.getAftRemainDay(), KTGRsvLeaveInfoImport.getGrantDay(), showAfter));
			    }
			} else if (timeStatusDisplayItem.getDetailType() == TimeStatusType.NUMBER_DAYS_LEFT
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
			    // 10-2.??????????????????????????????
			    SubstitutionHolidayOutput substitutionHolidayOutput = AbsenceTenProcess.getSettingForSubstituteHoliday(requireM11Imp, new CacheCarrier(), companyId, employeeId, GeneralDate.today());
			    
			    if (substitutionHolidayOutput.isSubstitutionFlg()) {
			        // ????????????????????????.?????????????????????????????????true???????????????.?????????????????????????????????????????????????????????
			        vacationSetting.setSubstituteManage(true);
			        vacationSetting.setSubstituteTimeManage(substitutionHolidayOutput.isTimeOfPeriodFlg());
			        
			        // ?????????????????????18.????????????????????????????????????_Th???c hi???n thu???t to??n"18.Hi???n th??? ngh???
			        // b??"
			        // X??? l?? 18
			        NumberConsecutiveVacation numberConsecutiveVacation = BreakDayOffMngInPeriodQuery.getBreakDayOffMngRemain(
			                requireM11Imp,
			                new CacheCarrier(),
			                employeeId,
			                systemDate);
			        dataKTG029.setRemainAlternationDay(numberConsecutiveVacation.getDays() != null ? numberConsecutiveVacation.getDays().v() : 0.0);
			        dataKTG029.setRemainAlternationNoDay(numberConsecutiveVacation.getRemainTime() != null ? numberConsecutiveVacation.getRemainTime().v() : 0);
			    }
			} else if (timeStatusDisplayItem.getDetailType() == TimeStatusType.REMNANT_NUMBER
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
			    // 10-3.??????????????????????????????
			    LeaveSetOutput leaveSetOutput = AbsenceTenProcess.getSetForLeave(requireM11Imp, new CacheCarrier(), companyId, employeeId, GeneralDate.today());
			    
			    if (leaveSetOutput.isSubManageFlag()) {
			        // ????????????????????????.?????????????????????????????????true
			        vacationSetting.setAccomoManage(true);
			        
			        // ?????????????????????19.????????????????????????????????????_ TH???c hi???n thu???t to??n "19.Hi???n th???
			        // ngh??? b?? ng??y l??? kh??ng ngh???"
			        // X??? l?? 19
			        Double remainLeft = AbsenceReruitmentMngInPeriodQuery.getAbsRecMngRemain(
			                requireM11Imp,
			                new CacheCarrier(),
			                employeeId,
			                systemDate).v();
			        dataKTG029.setRemainsLeft(remainLeft != null ? remainLeft : 0.0);
			    }
			} else if (timeStatusDisplayItem.getDetailType() == TimeStatusType.CHILD_NURSING_LEAVE_REMAINING // ???????????????????????????????????????????????????
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
			    // ?????????????????????????????????
			    NursingLeaveSetting nursingLeaveSettings = nursingLeaveSettingRepo.findByCompanyIdAndNursingCategory(companyId, NursingCategory.ChildNursing.value);
			    
			    if (nursingLeaveSettings.getManageType().equals(ManageDistinct.YES)) {
			        // ????????????????????????.???????????????????????????????????????true
			        vacationSetting.setChildCaremanage(true);
			        
			        // ?????????????????????21.????????????????????????????????????????????????
			        // [NO.206]?????????????????????????????????????????????
//			        ChildCareNursePeriodImport childNursePeriod =
//			                getRemainingNumberChildCareNurseAdapter.getChildCareNurseRemNumWithinPeriod(
//			                        employeeId,
//			                        datePeriod,
//			                        InterimRemainMngMode.OTHER,
//			                        systemDate,
//			                        Optional.of(false),
//			                        Optional.empty(),
//			                        Optional.empty(),
//			                        Optional.empty(),
//			                        Optional.empty());
			        ChildCareNursePeriodImport childNursePeriod = getRemainingNumberChildCareAdapter
		                    .getRemainingNumberChildCare(companyId, employeeId, GeneralDate.today());
			        ChildCareNurseRemainingNumberImport remainingNumber = childNursePeriod.getStartdateDays().getThisYear().getRemainingNumber();
			        Double before = remainingNumber.getUsedDays();
			        Double after = Double.valueOf(remainingNumber.getUsedTime().orElse(0));
			        RemainingNumber childRemainNo = new RemainingNumber(
			                "",
			                before,
			                after,
			                GeneralDate.today(),
			                remainingNumber.getUsedTime().isPresent());
			        dataKTG029.setChildRemainNo(childRemainNo);
			    }
				
				
			} else if (timeStatusDisplayItem.getDetailType() == TimeStatusType.REMAINING_CARE_LEAVE // ?????????????????????????????????????????????
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
			    // ?????????????????????????????????
			    NursingLeaveSetting nursingLeaveSettings = nursingLeaveSettingRepo.findByCompanyIdAndNursingCategory(companyId, NursingCategory.Nursing.value);
			    
			    if (nursingLeaveSettings.getManageType().equals(ManageDistinct.YES)) {
			        // ????????????????????????.?????????????????????????????????true
			        vacationSetting.setNursingManage(true);
			        
			        // ?????????????????????22.??????????????????????????????????????????
			        // [NO.207]???????????????????????????????????????
//			        ChildCareNursePeriodImport longtermCarePeriod = getRemainingNumberCareAdapter.getCareRemNumWithinPeriod(
//			                companyId, 
//			                employeeId, 
//			                datePeriod, 
//			                InterimRemainMngMode.OTHER, 
//			                systemDate, 
//			                Optional.of(false), 
//			                new ArrayList<TempChildCareNurseManagementImport>(),
//			                Optional.empty(), 
//			                Optional.empty(), 
//			                Optional.empty());
			        ChildCareNursePeriodImport longtermCarePeriod = getRemainingNumberNursingAdapter
		                    .getRemainingNumberNursing(companyId, employeeId, GeneralDate.today());
			        ChildCareNurseRemainingNumberImport remainingNumber = longtermCarePeriod.getStartdateDays().getThisYear().getRemainingNumber();
			        Double before = remainingNumber.getUsedDays();
			        Double after = Double.valueOf(remainingNumber.getUsedTime().orElse(0));
			        RemainingNumber careLeaveNo = new RemainingNumber(
			                "",
			                before,
			                after,
			                GeneralDate.today(),
			                remainingNumber.getUsedTime().isPresent());
			        
			        dataKTG029.setCareLeaveNo(careLeaveNo);
			    }
			} else if (timeStatusDisplayItem.getDetailType() == TimeStatusType.REMAINING_HOLIDAY
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE) {
				// s??? l?? 23
				// requestList 208(??????????????????????????????????????????)
				List<RemainingNumber> sPHDRamainNos = new ArrayList<>();
				List<SpecialHoliday> specialHolidays = specialHolidayRepository.findByCompanyId(companyId);
				DatePeriodDto datePeriodDto = getCurrentMonth();
				for (SpecialHoliday specialHoliday : specialHolidays) {
					// get request list 208 r???i tr??? v???
					// ????????????????????? ??? false????????????????????????(muto)
					// ??????????????????????????????????????? ??? ??????null or Empty???????????????????????????
					ComplileInPeriodOfSpecialLeaveParam param = new ComplileInPeriodOfSpecialLeaveParam(companyId,
							employeeId,
							new DatePeriod(datePeriodDto.getStrCurrentMonth(),
									datePeriodDto.getStrCurrentMonth().addYears(1).addDays(-1)),
							false, systemDate, specialHoliday.getSpecialHolidayCode().v(), false, false,
							new ArrayList<>(),
							Optional.empty());
					InPeriodOfSpecialLeaveResultInfor inPeriodOfSpecialLeave = SpecialLeaveManagementService
							.complileInPeriodOfSpecialLeave(
									requireService.createRequire(), new CacheCarrier(), param);
					boolean showAfter = false;
					GeneralDate date = GeneralDate.today();

					// double before =  inPeriodOfSpecialLeave.getRemainDays().getGrantDetailBefore().getRemainDays();
					double before =  inPeriodOfSpecialLeave.getAsOfPeriodEnd()
							.getRemainingNumber().getSpecialLeaveWithMinus().getRemainingNumberInfo().getRemainingNumberBeforeGrant().getDayNumberOfRemain().v();

					// double after =  inPeriodOfSpecialLeave.getRemainDays().getGrantDetailAfter().isPresent()?inPeriodOfSpecialLeave.getRemainDays().getGrantDetailAfter().get().getRemainDays() : 0.0;
					double after = 0.0;
					if ( inPeriodOfSpecialLeave.getAsOfPeriodEnd()
							.getRemainingNumber().getSpecialLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().isPresent() ) {

						after = inPeriodOfSpecialLeave.getAsOfPeriodEnd()
							.getRemainingNumber().getSpecialLeaveWithMinus()
							.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().get().getDayNumberOfRemain().v();
					}
					sPHDRamainNos.add(new RemainingNumber(specialHoliday.getSpecialHolidayName().v(), before, after, date, showAfter));

				}
				dataKTG029.setSPHDRamainNo(sPHDRamainNos);
			} else if (
					timeStatusDisplayItem.getDetailType() == TimeStatusType.EXCESS_NUMBER_REST_60H // ?????????60H??????????????????????????????
					&& timeStatusDisplayItem.getDisplayAtr() == NotUseAtr.USE
					) {
			    // 10-5.60H??????????????????????????????
			    SixtyHourSettingOutput setting60H = absenceCommon.getSixtyHourSetting(companyId, employeeId, GeneralDate.today());
			    
			    if (setting60H.isSixtyHourOvertimeMngDistinction()) {
			        // ????????????????????????.60H?????????????????????????????????true
			        vacationSetting.setHoliday60HManage(true);
			        
			        // [RQ677]????????????60H???????????????????????????
			        AggrResultOfHolidayOver60hImport over60hImport = 
			                getHolidayOver60hRemNumWithinPeriodAdapter.algorithm(
			                        companyId, 
			                        employeeId, 
			                        datePeriod, 
			                        InterimRemainMngMode.OTHER, 
			                        systemDate, 
			                        Optional.of(false), 
			                        Optional.empty(), 
			                        Optional.empty());
			        
			        
			        AnnualLeaveRemainingTime over60h = over60hImport
			                .getAsOfPeriodEnd()
			                .getRemainingNumber()
			                .getRemainingTimeWithMinus();
			        
			        TimeOT extraRest = new TimeOT(over60h.hour(), over60h.minute());
			        dataKTG029.setExtraRest(extraRest);
			    }
				
				
				
			}

		}
		dataKTG029.setVacationSetting(vacationSetting);

		return dataKTG029;
	}

	public DatePeriodDto getCurrentMonth() {
		String companyId = AppContexts.user().companyId();
		Integer closureId = this.getClosureId();

		Optional<Closure> closure = closureRepo.findById(companyId, closureId);
		if (!closure.isPresent())
			return null;

		YearMonth processingDate = closure.get().getClosureMonth().getProcessingYm();

		DatePeriod currentMonth = ClosureService.getClosurePeriod(closureId, processingDate, closure);

		DatePeriod nextMonth = ClosureService.getClosurePeriod(requireService.createRequire(), closureId, processingDate.addMonths(1));

		DatePeriodDto dto = new DatePeriodDto(currentMonth.start(), currentMonth.end(), nextMonth.start(),
				nextMonth.end());

		return dto;
	}

	private Integer getClosureId() {
		String companyId = AppContexts.user().companyId();
		String employmentCode = this.getEmploymentCode();
		Optional<ClosureEmployment> closureEmployment = closureEmploymentRepo.findByEmploymentCD(companyId,
				employmentCode);
		if (!closureEmployment.isPresent())
			return null;
		return closureEmployment.get().getClosureId();
	}

	private String getEmploymentCode() {
		String companyId = AppContexts.user().companyId();
		String employeeId = AppContexts.user().employeeId();

		Optional<BsEmploymentHistoryImport> EmploymentHistoryImport = shareEmploymentAdapter
				.findEmploymentHistory(companyId, employeeId, GeneralDate.today());
		if (!EmploymentHistoryImport.isPresent())
			throw new RuntimeException("Not found EmploymentHistory by closureID");
		String employmentCode = EmploymentHistoryImport.get().getEmploymentCode();
		return employmentCode;
	}

	public OvertimeHoursDto displayOvertime(int targetMonth) {
		String empLoginCode = AppContexts.user().employeeCode();
		String companyID = AppContexts.user().companyId();
		Integer closureId = this.getClosureId();
		List<AgreementTimeList36> data = new ArrayList<>();
		// OvertimeHoursDto overtimeHoursDto =
		// kTG027QueryProcessor.initialActivationArocess(targetMonth);
		List<String> employeeId = new ArrayList<>();
		employeeId.add(AppContexts.user().employeeId());
		// RQ 333
		if (closureId == null) {
			throw new BusinessException("Msg_1134");
		}
		/** TODO: 36???????????????????????????????????????????????????????????? */
//		List<AgreementTimeDetail> listAgreementTimeDetail = GetAgreementTime.get(
//				requireService.createRequire(), new CacheCarrier(), companyID, employeeId,
//				YearMonth.of(targetMonth), ClosureId.valueOf(closureId));
//
//		if (listAgreementTimeDetail.isEmpty()) {
//			throw new RuntimeException("ListAgreementTimeDetailRQ333 Empty");
//		}
//		for (AgreementTimeDetail agreementTimeDetail : listAgreementTimeDetail) {
//			if (agreementTimeDetail.getErrorMessage().isPresent()) {
//				throw new BusinessException(new RawErrorMessage(agreementTimeDetail.getErrorMessage().get()));
//			}
//		}
		// (Set th??ng tin c??ng vi???c ngoa??i gi???? ??a?? l????y)
//		List<String> lstEmpID = listAgreementTimeDetail.stream().map(c -> c.getEmployeeId())
//				.collect(Collectors.toList());
		// Lay Request61
//		List<PersonEmpBasicInfoImport> listEmpBasicInfoImport = empEmployeeAdapter.getPerEmpBasicInfo(lstEmpID);
//		for (AgreementTimeDetail agreementTimeDetail : listAgreementTimeDetail) {
//			Optional<PersonEmpBasicInfoImport> personInfor = listEmpBasicInfoImport.stream()
//					.filter(c -> c.getEmployeeId().equals(agreementTimeDetail.getEmployeeId())).findFirst();
//			if (!personInfor.isPresent()) {
//				break;
//			}
//			AgreementTimeList36 agreementTimeList36 = new AgreementTimeList36(personInfor.get().getEmployeeCode(),
//					personInfor.get().getBusinessName(), null,
//					new AgreementTimeOfMonthlyDto(
//							!agreementTimeDetail.getConfirmed().isPresent() ? 0
//									: agreementTimeDetail.getConfirmed().get().getAgreementTime().v(),
//							!agreementTimeDetail.getConfirmed().isPresent() ? 0
//									: agreementTimeDetail.getConfirmed().get().getLimitErrorTime().v(),
//							!agreementTimeDetail.getConfirmed().isPresent() ? 0
//									: agreementTimeDetail.getConfirmed().get().getLimitAlarmTime().v(),
//							!agreementTimeDetail.getConfirmed().isPresent() ? 0
//									: (!agreementTimeDetail.getConfirmed().get().getExceptionLimitErrorTime()
//											.isPresent()
//													? agreementTimeDetail.getConfirmed().get().getLimitErrorTime().v()
//													: agreementTimeDetail.getConfirmed().get()
//															.getExceptionLimitErrorTime().get().v()),
//							!agreementTimeDetail.getConfirmed().isPresent() ? 0
//									: (!agreementTimeDetail.getConfirmed().get().getExceptionLimitAlarmTime()
//											.isPresent()
//													? 0
//													: agreementTimeDetail.getConfirmed().get()
//															.getExceptionLimitAlarmTime().get().v()),
//							!agreementTimeDetail.getConfirmed().isPresent() ? 0
//									: agreementTimeDetail.getConfirmed().get().getStatus().value),
//					new AgreementTimeOfMonthlyDto(
//							!agreementTimeDetail.getAfterAppReflect().isPresent() ? 0
//									: agreementTimeDetail.getAfterAppReflect().get().getAgreementTime().v(),
//							!agreementTimeDetail.getAfterAppReflect().isPresent() ? 0
//									: agreementTimeDetail.getAfterAppReflect().get().getLimitErrorTime().v(),
//							!agreementTimeDetail.getAfterAppReflect().isPresent() ? 0
//									: agreementTimeDetail.getAfterAppReflect().get().getLimitAlarmTime().v(),
//							!agreementTimeDetail.getAfterAppReflect().isPresent() ? 0
//									: (!agreementTimeDetail.getAfterAppReflect().get().getExceptionLimitErrorTime()
//											.isPresent()
//													? 0
//													: agreementTimeDetail.getAfterAppReflect().get()
//															.getExceptionLimitErrorTime().get().v()),
//							!agreementTimeDetail.getAfterAppReflect().isPresent() ? 0
//									: (!agreementTimeDetail.getAfterAppReflect().get().getExceptionLimitAlarmTime()
//											.isPresent()
//													? 0
//													: agreementTimeDetail.getAfterAppReflect().get()
//															.getExceptionLimitAlarmTime().get().v()),
//							!agreementTimeDetail.getAfterAppReflect().isPresent() ? 0
//									: agreementTimeDetail.getAfterAppReflect().get().getStatus().value));
//			data.add(agreementTimeList36);
//		}

		data.sort((a, b) -> {
			if (a.getAfterAppReflect().getAgreementTime() == b.getAfterAppReflect().getAgreementTime()) {
				return a.getEmployeeCD().compareTo(b.getEmployeeCD());
			} else {
				return b.getAfterAppReflect().getAgreementTime() - a.getAfterAppReflect().getAgreementTime();
			}
		});
		OvertimeHours over = new OvertimeHours(null, data);
		OvertimeHoursDto overtimeHoursDto = new OvertimeHoursDto(closureId, null, over);
		List<AgreementTimeList36> filtered = overtimeHoursDto.getOvertimeHours().getOvertimeLaborInfor().stream()
				.filter(x -> x.getEmployeeCD().equalsIgnoreCase(empLoginCode)).collect(Collectors.toList());
		overtimeHoursDto.getOvertimeHours().setOvertimeLaborInfor(filtered);

		return overtimeHoursDto;
	}

	private YearlyHoliday setYearlyHoliday(String cID, String employeeId, GeneralDate date, DatePeriod datePeriod) {
		YearlyHoliday yearlyHoliday = new YearlyHoliday();
		// l???y request list 210
		List<NextAnnualLeaveGrantImport> listNextAnnualLeaveGrant = optionalWidgetAdapter
				.acquireNextHolidayGrantDate(cID, employeeId, date);
		if (!listNextAnnualLeaveGrant.isEmpty()) {
			NextAnnualLeaveGrantImport NextAnnualLeaveGrant = listNextAnnualLeaveGrant.get(0);
			yearlyHoliday.setNextTime(NextAnnualLeaveGrant.getGrantDate());
			yearlyHoliday.setNextGrantDate(NextAnnualLeaveGrant.getGrantDate());
			yearlyHoliday.setGrantedDaysNo(NextAnnualLeaveGrant.getGrantDays());
			if (datePeriod.contains(NextAnnualLeaveGrant.getGrantDate())) {
				yearlyHoliday.setShowGrantDate(true);
			}
		}
		// l???y request 198
		ReNumAnnLeaveImport reNumAnnLeaReferenceDate = annLeaveRemainNumberAdapter
				.getReferDateAnnualLeaveRemain(employeeId, date);

		yearlyHoliday.setNextTimeInfo(new YearlyHolidayInfo(reNumAnnLeaReferenceDate.getRemainingDays(),
				new TimeOT(reNumAnnLeaReferenceDate.getRemainingTime() / 60,
				        reNumAnnLeaReferenceDate.getRemainingTime() % 60),
				0, new TimeOT(0, 0)));
		/*
		 * yearlyHoliday.setNextGrantDateInfo(new
		 * YearlyHolidayInfo(remainingNumber.getAnnualLeaveGrantPreDay(), new
		 * TimeOT(remainingNumber.getAnnualLeaveGrantPreTime().intValue()/60,
		 * remainingNumber.getAnnualLeaveGrantPreTime().intValue()%60),
		 * remainingNumber.getNumberOfRemainGrantPre(), new
		 * TimeOT(remainingNumber.getTimeAnnualLeaveWithMinusGrantPre().intValue
		 * ()/60,remainingNumber.getTimeAnnualLeaveWithMinusGrantPre().intValue(
		 * )%60))); yearlyHoliday.setAfterGrantDateInfo(new
		 * YearlyHolidayInfo(remainingNumber.getAnnualLeaveGrantPostDay(), new
		 * TimeOT(remainingNumber.getAnnualLeaveGrantPostTime().intValue()/60,
		 * remainingNumber.getAnnualLeaveGrantPostTime().intValue()%60),
		 * remainingNumber.getNumberOfRemainGrantPost(), new
		 * TimeOT(remainingNumber.getTimeAnnualLeaveWithMinusGrantPost().
		 * intValue()/60,remainingNumber.getTimeAnnualLeaveWithMinusGrantPost().
		 * intValue()%60)));
		 */
		/*
		 * yearlyHoliday.setAttendanceRate(remainingNumber.getAttendanceRate());
		 * yearlyHoliday.setWorkingDays(remainingNumber.getWorkingDays());
		 * yearlyHoliday.setCalculationMethod(optionalWidgetAdapter.
		 * getGrantHdTblSet(cID, employeeId));
		 */
		return yearlyHoliday;
	}

	public void displayPreProcessKTG029() {
		String companyId = AppContexts.user().companyId();
		String employmentCode = this.getEmploymentCode();
		Integer closureId = this.getClosureId();
		if (!employmentCode.isEmpty()) {
			Optional<Closure> closure = closureRepo.findById(companyId, closureId);
			YearMonth yearmonth = closure.get().getClosureMonth().getProcessingYm();
			// ?????????????????????????????????????????????????????????????????????
			// ?????????????????????????????????
			DatePeriod datePeriod = ClosureService.getClosurePeriod(
					ClosureService.createRequireM1(closureRepo, closureEmploymentRepo),
					closureId, yearmonth.addMonths(1));
			GeneralDate startDate = datePeriod.start();
			YearlyHoliday yearlyHoliday = new YearlyHoliday();
		}
	}

	// Get the existence of application data to be approved
	public boolean existenceDataApproved() {
		String cid = AppContexts.user().companyId();
		String employeeID = AppContexts.user().employeeId();
		List<GeneralDate> listDate = new ArrayList<>();
		// ?????????????????????????????????????????????????????????????????????
		// L???y RQ 140
		List<ClosureResultModel> rq140 = workClosureQueryProcessor.findClosureByReferenceDate(GeneralDate.today());
		List<Integer> listClosureID = rq140.stream().map(c -> c.getClosureId()).collect(Collectors.toList());
		// ??????????????????ID??????????????????????????????
		for (Integer integer : listClosureID) {
			// ?????????????????????????????????????????????????????????????????????????????????
			// (Th???c thi x??? l?? l???y th???i gian quy???t to??n v?? th??ng n??m x??? l??)
			Optional<PresentClosingPeriodExport> presentClosingPeriod = shClosurePub.find(cid, integer);
			if (presentClosingPeriod.isPresent()) {
				listDate.add(presentClosingPeriod.get().getClosureStartDate());
			}
		}
		Optional<GeneralDate> startDate = listDate.stream().min(Comparator.comparing(GeneralDate::date));
		if (!startDate.isPresent()) {
			throw new RuntimeErrorException(new Error(), "Can't get Start Date");
		} else {
			// ??????????????????????????? ?????? ???????????????????????????????????????????????????????????????
			// ??????????????????????????? ?????? ??????????????????????????????????????????????????????????????? + ?????? - ??????
			GeneralDate endDate = startDate.get().addYears(2).addDays(-1);
			List<String> listApplicationID = approvalRootStateRepository.resultKTG002Mobile(startDate.get(), endDate,
					employeeID, 0, cid);
			// ???????????????????????????ID????????????????????????????????????????????????????????????
			List<Application> listApplication = applicationRepository.findByListID(cid, listApplicationID);
			/* ??????????????????????????????Input??????????????? & ?????????????????????????????????<>???????????? ????????????????????????????????????????????????????????? */
			List<Application> listApplicationFilter = listApplication.stream()
					.filter(c -> (c.getAppType() == ApplicationType.OVER_TIME_APPLICATION)
							&& c.getAppReflectedState() != ReflectedState.REMAND)
					.collect(Collectors.toList());
			if (listApplicationFilter.isEmpty()) {
				return false;
			} else {
				return true;
			}

		}
	}
	@AllArgsConstructor
    private class RequireM11Imp implements BreakDayOffMngInPeriodQuery.RequireM11, AbsenceReruitmentMngInPeriodQuery.RequireM11, AbsenceTenProcess.RequireM0, AbsenceTenProcess.RequireM1, AbsenceTenProcess.RequireM2, AbsenceTenProcess.RequireM3, AbsenceTenProcess.RequireM4 {
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
        
        private AnnualPaidLeaveSettingRepository annualPaidLeaveSettingRepo;
        
        private EmploymentSettingRepository employmentSettingRepo;
        
        private RetentionYearlySettingRepository retentionYearlySettingRepo;

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

        @Override
        public AnnualPaidLeaveSetting annualPaidLeaveSetting(String companyId) {
            return annualPaidLeaveSettingRepo.findByCompanyId(companyId);
        }

        @Override
        public CompensatoryLeaveEmSetting compensatoryLeaveEmSetting(String companyId, String employmentCode) {
            return compensLeaveEmSetRepo.find(companyId, employmentCode);
        }

        @Override
        public CompensatoryLeaveComSetting compensatoryLeaveComSetting(String companyId) {
            return compensLeaveComSetRepo.find(companyId);
        }

        @Override
        public Optional<EmpSubstVacation> empSubstVacation(String companyId, String contractTypeCode) {
            return empSubstVacationRepo.findById(companyId, contractTypeCode);
        }

        @Override
        public Optional<ComSubstVacation> comSubstVacation(String companyId) {
            return comSubstVacationRepo.findById(companyId);
        }

        @Override
        public Optional<EmptYearlyRetentionSetting> employmentYearlyRetentionSetting(String companyId,
                String employmentCode) {
            return employmentSettingRepo.find(companyId, employmentCode);
        }

        @Override
        public Optional<RetentionYearlySetting> retentionYearlySetting(String companyId) {
            return retentionYearlySettingRepo.findByCompanyId(companyId);
        }

        @Override
        public Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
                String employeeId, GeneralDate baseDate) {
            return shareEmploymentAdapter.findEmploymentHistoryRequire(cacheCarrier, companyId, employeeId, baseDate);
        }
    }
}
