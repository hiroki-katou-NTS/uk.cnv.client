/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.ws.workrule.closure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.app.command.workrule.closure.ClosureSaveCommand;
import nts.uk.ctx.at.shared.app.command.workrule.closure.ClosureSaveCommandHandler;
import nts.uk.ctx.at.shared.app.find.workrule.closure.ClosureFinder;
import nts.uk.ctx.at.shared.app.find.workrule.closure.CurrentClosureFinder;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.CheckSaveDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureDetailDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureFindDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureForLogDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureHistoryInDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureIdNameDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.CurrentClosureDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.DayMonthChangeDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.DayMonthChangeInDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.DayMonthDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.DayMonthInDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.DayMonthOutDto;
import nts.uk.ctx.at.shared.dom.adapter.employment.BsEmploymentHistoryImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.ShareEmploymentAdapter;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureGetMonthDay;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.DayMonthChange;
import nts.uk.ctx.at.shared.dom.workrule.closure.service.ClosureService;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.com.time.calendar.date.ClosureDate;

/**
 * The Class ClosureWs.
 */
@Path("ctx/at/shared/workrule/closure")
@Produces("application/json")
public class ClosureWs {
	
	/** The finder. */
	@Inject
	private ClosureFinder finder;

	/** The save. */
	@Inject
	private ClosureSaveCommandHandler save;

	/** The current closure finder. */
	@Inject
	private CurrentClosureFinder currentClosureFinder;
	
	/** The closure repository. */
	@Inject
	private ClosureRepository closureRepository;

	@Inject
	private ClosureEmploymentRepository closureEmpRepo;
	
	@Inject
	private ShareEmploymentAdapter shrEmpAdapter;

	/** The Constant CLOSURE_ID_BEGIN. */
	public static final int CLOSURE_ID_BEGIN = 1;

	/** The Constant THREE_MONTH. */
	public static final int THREE_MONTH = 3;

	/** The Constant TOTAL_MONTH_OF_YEAR. */
	public static final int TOTAL_MONTH_OF_YEAR = 12;
	
	/** The Constant FULL_CLOSURE_ID. */
	public static final int FULL_CLOSURE_ID = 0;


	
	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@POST
	@Path("findAll")
	public List<ClosureFindDto> findAll() {
		return this.finder.findAll();
	}

	/**
	 * Find all for log
	 *
	 * @return the list
	 */
	@POST
	@Path("findallforlog")
	public List<ClosureForLogDto> findAllForLog() {
		return this.finder.findAllForLog();
	}

	/**
	 * Find by id.
	 *
	 * @param closureId
	 *            the closure id
	 * @return the closure find dto
	 */
	@POST
	@Path("findById/{closureId}")
	public ClosureFindDto findById(@PathParam("closureId") int closureId) {
		return this.finder.findById(closureId);
	}

	/**
	 * Find period by id.
	 *
	 * @param closureId
	 *            the closure id
	 * @return the period
	 */
	@POST
	@Path("findPeriodById/{closureId}")
	public DayMonthOutDto findPeriodById(@PathParam("closureId") int closureId) {
		return new DayMonthOutDto(this.finder.findByIdGetMonthDay(closureId));
	}

	/**
	 * Check three month.
	 *
	 * @param baseDate
	 *            the base date
	 * @return the boolean
	 */
	@POST
	@Path("checkThreeMonth")
	public Boolean checkThreeMonth(CheckSaveDto checksave) {
		DatePeriod period = this.finder.findByIdGetMonthDay(CLOSURE_ID_BEGIN);
		return (period.start().yearMonth().v() + THREE_MONTH < checksave.getBaseDate().yearMonth().v());
	}

	/**
	 * Check month max.
	 *
	 * @param baseDate
	 *            the base date
	 * @return the boolean
	 */
	@POST
	@Path("checkMonthMax")
	public Boolean checkMonthMax(CheckSaveDto checksave) {
		return checksave.getBaseDate().before(this.finder.getMaxStartDateClosure());
	}

	/**
	 * Find by master.
	 *
	 * @param master
	 *            the master
	 * @return the closure detail dto
	 */
	@POST
	@Path("findByMaster")
	public ClosureDetailDto findByMaster(ClosureHistoryInDto master) {
		return this.finder.findByMaster(master);
	}

	/**
	 * Save.
	 *
	 * @param command
	 *            the command
	 */
	@POST
	@Path("save")
	public void save(ClosureSaveCommand command) {
		this.save.handle(command);
	}

	/**
	 * Gets the day.
	 *
	 * @param input
	 *            the input
	 * @return the day
	 */
	@POST
	@Path("getday")
	public DayMonthDto getDay(DayMonthInDto input) {
		ClosureGetMonthDay closureGetMonthDay = new ClosureGetMonthDay();
		DatePeriod period = closureGetMonthDay
				.getDayMonth(new ClosureDate(input.getClosureDate(), input.getClosureDate() == 0), input.getMonth());
		DayMonthDto dto = new DayMonthDto();
		dto.setBeginDay(period.start().toString());
		dto.setEndDay(period.end().toString());
		return dto;
	}

	/**
	 * Gets the day.
	 *
	 * @param input
	 *            the input
	 * @return the day
	 */
	@POST
	@Path("getdaychange")
	public DayMonthChangeDto getDayChange(DayMonthChangeInDto input) {
		ClosureGetMonthDay closureGetMonthDay = new ClosureGetMonthDay();
		DayMonthChange dayMonthChange = closureGetMonthDay.getDayMonthChange(
				new ClosureDate(input.getClosureDate(), input.getClosureDate() == 0),
				new ClosureDate(input.getChangeClosureDate(), input.getChangeClosureDate() == 0), input.getMonth());
		DayMonthChangeDto dto = new DayMonthChangeDto();
		DayMonthDto beforeClosureDate = new DayMonthDto();
		DayMonthDto afterClosureDate = new DayMonthDto();

		beforeClosureDate.setBeginDay(dayMonthChange.getBeforeClosureDate().start().toString());
		beforeClosureDate.setEndDay(dayMonthChange.getBeforeClosureDate().end().toString());

		afterClosureDate.setBeginDay(dayMonthChange.getAfterClosureDate().start().toString());
		afterClosureDate.setEndDay(dayMonthChange.getAfterClosureDate().end().toString());
		dto.setBeforeClosureDate(beforeClosureDate);
		dto.setAfterClosureDate(afterClosureDate);
		return dto;
	}
	
	@POST
	@Path("find_by_empid")
	public nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureDto findClosureByEmployeeId() {
		return this.finder.findClosureByEmployeeId();
	}

	@POST
	@Path("findCurrentClosure")
	public List<CurrentClosureDto> findStartEndDate() {
		return this.currentClosureFinder.findCurrentClosure();
	}
	
	/**
	 * Gets the closure id name.
	 *
	 * @param referDate the refer date
	 * @return the closure id name
	 */
	@POST
	@Path("getClosureIdName")
	public List<ClosureIdNameDto> getClosureIdName() {
		return this.finder.getClosureIdName();
	}

	/**
	 * Find emp by closure id.
	 *
	 * @param closureId the closure id
	 * @return the list
	 */
	@POST
	@Path("findEmpByClosureId/{closureId}")
	public List<String> findEmpByClosureId(@PathParam("closureId") int closureId) {
		// Find by closure id.
		if (closureId != FULL_CLOSURE_ID) {
			return this.finder.findEmploymentCodeByClosureId(closureId);
		}
		
		// Find by All closure.
		List<Integer> ids = this.findClosureListByCurrentMonth()
				.stream().map(dto -> dto.id).collect(Collectors.toList());
		return this.finder.findEmpByClosureIds(ids);
	}
	
	/**
	 * Find emp by closure id.
	 *
	 * @param closureId the closure id
	 * @return the list
	 */
	@POST
	@Path("findEmpByClosureIdMob")
	public List<ClosureEmployment> findEmpByClosureIdMob() {
		// Find by All closure.
		List<Integer> ids = this.findClosureListByCurrentMonth()
				.stream().map(dto -> dto.id).collect(Collectors.toList());
		return this.finder.findEmpByClosureIdsMob(ids);
	}
	
	/**
	 * Find closure list by current month.
	 *
	 * @return the list
	 */
	@POST
	@Path("findClosureListByCurrentMonth")
	public List<ClosureDto> findClosureListByCurrentMonth() {
		GeneralDate now = GeneralDate.today();	
		YearMonth currentMonth = YearMonth.of(now.year(), now.month());
		return this.closureRepository.findByCurrentMonth(AppContexts.user().companyId(), currentMonth).stream().map(item -> {
			return ClosureDto.builder()
					.id(item.getClosureId().value)
					.name(item.getClosureName().v())
					.build();
		}).collect(Collectors.toList());
	}

	/**
	 * Find by current year month and used.
	 *
	 * @return the list
	 */
	@POST
	@Path("find/currentyearmonthandused")
	public List<ClosureDto> findByCurrentYearMonthAndUsed() {
		return this.closureRepository.findByCurrentYearMonthAndUsed(AppContexts.user().companyId())
				.stream().map(item -> ClosureDto.builder()
					.id(item.getClosureId().value)
					.name(item.getClosureName().v())
					.build()
		).collect(Collectors.toList());
	}
	
	/**
	 * Gets the closure tied by employment.
	 *
	 * @param employmentCode the employment code
	 * @return the closure tied by employment
	 */
	@POST
	@Path("getclosuretiedbyemployment/{employmentcode}")
	public Integer getClosureTiedByEmployment(@PathParam("employmentcode") String employmentCode) {
		return this.finder.getClosureIdByEmploymentCode(employmentCode);
	}

	/**
	 * Calculate period.
	 *
	 * @param closureId the closure id
	 * @param yearMonth the year month
	 * @return the date period dto
	 */
	@POST
	@Path("calculateperiod/{closureid}/{yearmonth}")
	public DatePeriodDto calculatePeriod(@PathParam("closureid") int closureId, @PathParam("yearmonth") int yearMonth) {
		DatePeriod period = ClosureService.getClosurePeriod(createRequireM3(), closureId, YearMonth.of(yearMonth));
		return DatePeriodDto.builder()
				.endDate(period.end().toString("yyyy-MM-dd"))
				.startDate(period.start().toString("yyyy-MM-dd")).build();
	}
	
	/**
	 * Calculate period.
	 *
	 * @param closureId the closure id
	 * @param yearMonth the year month
	 * @return the date period dto
	 */
	@POST
	@Path("calculateperiod/{closureid}")
	public DatePeriodDto calculatePeriod(@PathParam("closureid") int closureId) {
		LoginUserContext loginUserContext = AppContexts.user();
		String companyId = loginUserContext.companyId();
		Optional<Closure> closure = this.closureRepository.findById(companyId, closureId);
		DatePeriod period = ClosureService.getClosurePeriod(createRequireM3(), closureId,
															closure.get().getClosureMonth().getProcessingYm());
		return DatePeriodDto.builder().endDate(period.end().toString("yyyy-MM-dd"))
				.startDate(period.start().toString("yyyy-MM-dd")).build();
	}

	/**
	 * Gets the closures by base date.
	 *
	 * @param basedate the basedate
	 * @return the closures by base date
	 */
	@POST
	@Path("getclosuresbybasedate/{basedate}")
	public List<ClosureIdNameDto> getClosuresByBaseDate(@PathParam("basedate") String basedate) {
		return this.finder.getClosuresByBaseDate(GeneralDate.fromString(basedate, "yyyy-MM-dd"));
	}

	/**
	 * Gets the closure by current employee.
	 *
	 * @param basedate the basedate
	 * @return the closure by current employee
	 */
	@POST
	@Path("getclosurebycurrentemployee/{basedate}")
	public Integer getClosureByCurrentEmployee(@PathParam("basedate") String basedate) {
		CacheCarrier cacheCarrier = new CacheCarrier();
		
		Closure closure = ClosureService.getClosureDataByEmployee(createRequireM3(), cacheCarrier, 
				AppContexts.user().employeeId(),
				GeneralDate.fromString(basedate, "yyyy-MM-dd"));
		if (closure == null) {
			return null;
		}
		return closure.getClosureId().value;
	}

	@POST
	@Path("get-current-closure-period-by-logged-in-employee")
	public DatePeriodDto getClosurePeriod() {
		DatePeriod period = ClosureService.findClosurePeriod(
				createRequireM3(),
				new CacheCarrier(),
				AppContexts.user().employeeId(),
				GeneralDate.today()
		);
		return new DatePeriodDto(period.start().toString(), period.end().toString());
	}
	
	private ClosureService.RequireM3 createRequireM3 () {
		return new ClosureService.RequireM3() {
			
			@Override
			public Optional<Closure> closure(String companyId, int closureId) {
				return closureRepository.findById(companyId, closureId);
			}
			
			@Override
			public Optional<ClosureEmployment> employmentClosure(String companyID, String employmentCD) {
				// TODO Auto-generated method stub
				return closureEmpRepo.findByEmploymentCD(companyID, employmentCD);
			}

			@Override
			public Optional<BsEmploymentHistoryImport> employmentHistory(CacheCarrier cacheCarrier, String companyId,
					String employeeId, GeneralDate baseDate) {
				return shrEmpAdapter.findEmploymentHistoryRequire(cacheCarrier, companyId, employeeId, baseDate);
			}

			@Override
			public List<ClosureEmployment> employmentClosureClones(String companyID, List<String> employmentCD) {
				return closureEmpRepo.findListEmployment(companyID, employmentCD);
			}

			@Override
			public List<Closure> closureClones(String companyId, List<Integer> closureId) {
				return closureRepository.findByListId(companyId, closureId);
			}

			@Override
			public Map<String, BsEmploymentHistoryImport> employmentHistoryClones(String companyId, List<String> employeeId,
					GeneralDate baseDate) {
				return shrEmpAdapter.findEmpHistoryVer2(companyId, employeeId, baseDate);
			}
		};
	}
}
