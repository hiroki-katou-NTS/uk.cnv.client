package nts.uk.screen.at.app.ktgwidget.find;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.auth.app.find.employmentrole.InitDisplayPeriodSwitchSetFinder;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.DateProcessed;
import nts.uk.ctx.at.auth.app.find.employmentrole.dto.InitDisplayPeriodSwitchSetDto;
import nts.uk.ctx.at.function.dom.adapter.application.ApplicationAdapter;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.AnnualLeaveRemainingNumberImport;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.NextAnnualLeaveGrantImport;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.NumAnnLeaReferenceDateImport;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.OptionalWidgetAdapter;
import nts.uk.ctx.at.function.dom.adapter.widgetKtg.OptionalWidgetImport;
import nts.uk.ctx.at.function.dom.employmentfunction.checksdailyerror.ChecksDailyPerformanceErrorRepository;
import nts.uk.ctx.at.record.dom.require.RecordDomRequireService;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.specialholiday.SpecialHolidayRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService.RequireM3;
//import nts.uk.ctx.at.shared.pub.remainingnumber.nursingcareleavemanagement.interimdata.ChildNursingRemainExport;
//import nts.uk.ctx.at.shared.pub.remainingnumber.nursingcareleavemanagement.interimdata.NursingMode;
//import nts.uk.ctx.at.shared.pub.remainingnumber.nursingcareleavemanagement.interimdata.ShNursingLeaveSettingPub;
import nts.uk.screen.at.app.ktgwidget.find.dto.DatePeriodDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.OptionalWidgetDisplay;
import nts.uk.screen.at.app.ktgwidget.find.dto.TimeOT;
import nts.uk.screen.at.app.ktgwidget.find.dto.WidgetInitialDisplayMonthDto;
import nts.uk.screen.at.app.ktgwidget.find.dto.YearlyHoliday;
import nts.uk.screen.at.app.ktgwidget.find.dto.YearlyHolidayInfo;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class OptionalWidgetKtgFinder {

	@Inject
	private ShareEmploymentAdapter shareEmploymentAdapter;

	@Inject
	private ClosureEmploymentRepository closureEmploymentRepo;

	@Inject
	private ClosureRepository closureRepo;

	@Inject
	private RecordDomRequireService requireService;

	@Inject
	private OptionalWidgetAdapter optionalWidgetAdapter;
	

	@Inject
	private ApplicationRepository applicationRepo;

	@Inject
	private ApplicationAdapter applicationAdapter;

	@Inject
	private ChecksDailyPerformanceErrorRepository checksDailyPerformanceErrorRepo;

//	@Inject
//	private ShNursingLeaveSettingPub shNursingLeaveSettingPub;

	@Inject
	private SpecialHolidayRepository specialHolidayRepository;
	
	@Inject
	private InitDisplayPeriodSwitchSetFinder displayPeriodfinder;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public DatePeriodDto getCurrentMonth() {
		String companyId = AppContexts.user().companyId();
		Integer closureId = this.getClosureId();

		Optional<Closure> closure = closureRepo.findById(companyId, closureId);
		if(!closure.isPresent())
			return null;

		YearMonth processingDate = closure.get().getClosureMonth().getProcessingYm();

		DatePeriod currentMonth = ClosureService.getClosurePeriod(closureId, processingDate, closure);

		DatePeriod nextMonth = ClosureService.getClosurePeriod(requireService.createRequire(), closureId, processingDate.addMonths(1));

		DatePeriodDto dto = new DatePeriodDto(currentMonth.start(), currentMonth.end(), nextMonth.start(), nextMonth.end());

		return dto;
	}
	/**
	 * @return employment code
	 */
	private String getEmploymentCode() {
		String companyId = AppContexts.user().companyId();
		String employeeId = AppContexts.user().employeeId();

		Optional<BsEmploymentHistoryImport> EmploymentHistoryImport = shareEmploymentAdapter.findEmploymentHistory(companyId, employeeId, GeneralDate.today());
		if(!EmploymentHistoryImport.isPresent())
			throw new RuntimeException("Not found EmploymentHistory by closureID");
		String employmentCode = EmploymentHistoryImport.get().getEmploymentCode();
		return employmentCode;
	}

	/**
	 * @return ClosureId
	 */
	private Integer getClosureId() {
		String companyId = AppContexts.user().companyId();
		String employmentCode = this.getEmploymentCode();
		Optional<ClosureEmployment> closureEmployment = closureEmploymentRepo.findByEmploymentCD(companyId, employmentCode);
		if(!closureEmployment.isPresent())
			return null;
		return closureEmployment.get().getClosureId();
	}
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public OptionalWidgetImport findOptionalWidgetByCode(String topPagePartCode) {
		String companyId = AppContexts.user().companyId();
		Optional<OptionalWidgetImport> dto = optionalWidgetAdapter.getSelectedWidget(companyId, topPagePartCode);
		if(!dto.isPresent())
			return null;
		return dto.get();
	}
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public OptionalWidgetDisplay getOptionalWidgetDisplay(String topPagePartCode) {
		DatePeriodDto datePeriodDto = getCurrentMonth();
		OptionalWidgetImport optionalWidgetImport = findOptionalWidgetByCode(topPagePartCode);
		return new OptionalWidgetDisplay(datePeriodDto, optionalWidgetImport);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private YearlyHoliday setYearlyHoliday(String cID, String employeeId, GeneralDate date, DatePeriod datePeriod) {
		YearlyHoliday yearlyHoliday = new YearlyHoliday();
		//lấy request list 210
		List<NextAnnualLeaveGrantImport> listNextAnnualLeaveGrant = optionalWidgetAdapter.acquireNextHolidayGrantDate(cID,employeeId, date);
		if(!listNextAnnualLeaveGrant.isEmpty()) {
			NextAnnualLeaveGrantImport NextAnnualLeaveGrant = listNextAnnualLeaveGrant.get(0);
			yearlyHoliday.setNextTime(NextAnnualLeaveGrant.getGrantDate());
			yearlyHoliday.setNextGrantDate(NextAnnualLeaveGrant.getGrantDate());
			yearlyHoliday.setGrantedDaysNo(NextAnnualLeaveGrant.getGrantDays());
			if(datePeriod.contains(NextAnnualLeaveGrant.getGrantDate())) {
				yearlyHoliday.setShowGrantDate(true);
			}
		}
		//lấy request 198
		NumAnnLeaReferenceDateImport reNumAnnLeaReferenceDate = optionalWidgetAdapter.getReferDateAnnualLeaveRemainNumber(employeeId, date);

		AnnualLeaveRemainingNumberImport remainingNumber = reNumAnnLeaReferenceDate.getAnnualLeaveRemainNumberImport();
		yearlyHoliday.setNextTimeInfo(new YearlyHolidayInfo(remainingNumber.getAnnualLeaveGrantPreDay(),
															new TimeOT(remainingNumber.getAnnualLeaveGrantPreTime().intValue()/60, remainingNumber.getAnnualLeaveGrantPreTime().intValue()%60),
															remainingNumber.getNumberOfRemainGrantPre(),
															new TimeOT(remainingNumber.getTimeAnnualLeaveWithMinusGrantPre().intValue()/60,remainingNumber.getTimeAnnualLeaveWithMinusGrantPre().intValue()%60)));
		/*yearlyHoliday.setNextGrantDateInfo(new YearlyHolidayInfo(remainingNumber.getAnnualLeaveGrantPreDay(),
															new TimeOT(remainingNumber.getAnnualLeaveGrantPreTime().intValue()/60, remainingNumber.getAnnualLeaveGrantPreTime().intValue()%60),
															remainingNumber.getNumberOfRemainGrantPre(),
															new TimeOT(remainingNumber.getTimeAnnualLeaveWithMinusGrantPre().intValue()/60,remainingNumber.getTimeAnnualLeaveWithMinusGrantPre().intValue()%60)));
		yearlyHoliday.setAfterGrantDateInfo(new YearlyHolidayInfo(remainingNumber.getAnnualLeaveGrantPostDay(),
															new TimeOT(remainingNumber.getAnnualLeaveGrantPostTime().intValue()/60, remainingNumber.getAnnualLeaveGrantPostTime().intValue()%60),
															remainingNumber.getNumberOfRemainGrantPost(),
															new TimeOT(remainingNumber.getTimeAnnualLeaveWithMinusGrantPost().intValue()/60,remainingNumber.getTimeAnnualLeaveWithMinusGrantPost().intValue()%60)));*/
		/*yearlyHoliday.setAttendanceRate(remainingNumber.getAttendanceRate());
		yearlyHoliday.setWorkingDays(remainingNumber.getWorkingDays());
		yearlyHoliday.setCalculationMethod(optionalWidgetAdapter.getGrantHdTblSet(cID, employeeId));*/
		return yearlyHoliday;
	}

	/**
	 * UKDesign.UniversalK.就業.KTG_ウィジェット.共通アルゴリズム.ウィジェット初期表示月の取得.ウィジェット初期表示月の取得
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public WidgetInitialDisplayMonthDto getWidgetInitialDisplayMonth() {
		// [RQ609]ログイン社員のシステム日時点の処理対象年月を取得する
		InitDisplayPeriodSwitchSetDto rq609 = displayPeriodfinder.targetDateFromLogin();
		
		// Get closure
		RequireM3 require = ClosureService.createRequireM3(closureRepo, closureEmploymentRepo, shareEmploymentAdapter);
		Closure closure = ClosureService.getClosureDataByEmployee(
				require,
				new CacheCarrier(),
				AppContexts.user().employeeId(),
				GeneralDate.today());
		
		// 締め情報
		DateProcessed dateProcessed = rq609.getListDateProcessed().stream()
				.filter(c -> c.getClosureID() == closure.getClosureId().value)
				.collect(Collectors.toList())
				.get(0);
		
		// 指定した年月の期間を算出する
		YearMonth processYm = rq609.getCurrentOrNextMonth() == 1
				? dateProcessed.getTargetDate().addMonths(1)
				: dateProcessed.getTargetDate().addMonths(-1);
		// 締め期間
		DatePeriod datePeriod = ClosureService.getClosurePeriod(require, closure.getClosureId().value, processYm);
		
		if (rq609.getCurrentOrNextMonth() == 1) {
			return WidgetInitialDisplayMonthDto.builder()
					.currentOrNextMonth(rq609.getCurrentOrNextMonth())
					.closureId(closure.getClosureId().value)
					.currentMonth(dateProcessed.getTargetDate().v())
					.currentMonthStart(dateProcessed.getDatePeriod().start().toString())
					.currentMonthEnd(dateProcessed.getDatePeriod().end().toString())
					.nextMonth(dateProcessed.getTargetDate().addMonths(1).v())
					.nextMonthStart(datePeriod.start().toString())
					.nextMonthEnd(datePeriod.end().toString())
					.build();
		}
		
		return WidgetInitialDisplayMonthDto.builder()
				.currentOrNextMonth(rq609.getCurrentOrNextMonth())
				.closureId(closure.getClosureId().value)
				.currentMonth(dateProcessed.getTargetDate().addMonths(-1).v())
				.currentMonthStart(datePeriod.start().toString())
				.currentMonthEnd(datePeriod.end().toString())
				.nextMonth(dateProcessed.getTargetDate().v())
				.nextMonthStart(dateProcessed.getDatePeriod().start().toString())
				.nextMonthEnd(dateProcessed.getDatePeriod().end().toString())
				.build();
	}
}
