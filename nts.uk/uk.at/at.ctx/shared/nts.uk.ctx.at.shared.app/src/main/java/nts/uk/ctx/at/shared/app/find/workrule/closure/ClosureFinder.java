/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.app.find.workrule.closure;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureDetailDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureFindDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureForLogDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureHistoryInDto;
import nts.uk.ctx.at.shared.app.find.workrule.closure.dto.ClosureHistoryMasterDto;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureGetMonthDay;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureHistory;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;
import nts.uk.shr.com.time.calendar.period.DatePeriod;

/**
 * The Class ClosureFinder.
 */
@Stateless
public class ClosureFinder {

	/** The repository. */
	@Inject
	private ClosureRepository repository;
	
	/** The Constant ZERO_START_DATE. */
	public static final int ZERO_START_DATE = 0;

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	
	
	public List<ClosureFindDto> findAll() {

		// get login user
		LoginUserContext loginUserContext = AppContexts.user();

		// get company id
		String companyId = loginUserContext.companyId();

		return this.repository.findAll(companyId).stream().map(closure -> {
			ClosureFindDto dto = new ClosureFindDto();
			closure.saveToMemento(dto);
			return dto;
		}).collect(Collectors.toList());
	}
	/**
	 * get listClosure for log
	 */
	
	public List<ClosureForLogDto> findAllForLog() {

		// get login user
		LoginUserContext loginUserContext = AppContexts.user();

		// get company id
		String companyId = loginUserContext.companyId();

		return this.repository.findAll(companyId)
				.stream().map(c -> ClosureForLogDto.fromDomain(c)).collect(Collectors.toList());
	}
	

	/**
	 * Find by id.
	 *
	 * @param closureId the closure id
	 * @return the closure find dto
	 */
	public ClosureFindDto findById(int closureId) {

		// get login user
		LoginUserContext loginUserContext = AppContexts.user();

		// get company id
		String companyId = loginUserContext.companyId();

		// call service
		Optional<Closure> closure = this.repository.findById(companyId, closureId);

		ClosureFindDto dto = new ClosureFindDto();

		List<ClosureHistory> closureHistories = this.repository.findByClosureId(companyId,
				closureId);

		// exist data
		if (closure.isPresent()) {

			// to data
			closure.get().setClosureHistories(closureHistories);
			closure.get().saveToMemento(dto);

			Optional<ClosureHistory> closureHisory = this.repository.findBySelectedYearMonth(
					companyId, closureId, closure.get().getClosureMonth().getProcessingYm().v());

			if (closureHisory.isPresent()) {
				ClosureHistoryMasterDto closureSelected = new ClosureHistoryMasterDto();
				closureHisory.get().saveToMemento(closureSelected);
				dto.setClosureSelected(closureSelected);
			}
		}

		return dto;
	}
	
	/**
	 * Find by id get month day.
	 *
	 * @param closureId the closure id
	 * @return the period
	 */
	public DatePeriod findByIdGetMonthDay(int closureId) {
		
		// get login user
		LoginUserContext loginUserContext = AppContexts.user();
		
		// get company id
		String companyId = loginUserContext.companyId();
		
		// call service
		Optional<Closure> closure = this.repository.findById(companyId, closureId);
		
		DatePeriod period = new DatePeriod(GeneralDate.min(), GeneralDate.min());
		
		List<ClosureHistory> closureHistories = this.repository.findByClosureId(companyId,
				closureId);
		
		// exist data
		if (closure.isPresent()) {
			
			// to data
			closure.get().setClosureHistories(closureHistories);
			
			Optional<ClosureHistory> closureHisory = this.repository.findBySelectedYearMonth(
					companyId, closureId, closure.get().getClosureMonth().getProcessingYm().v());
			
			ClosureGetMonthDay closureGetMonthDay = new ClosureGetMonthDay();
			period = closureGetMonthDay.getDayMonth(closureHisory.get().getClosureDate(),
					closure.get().getClosureMonth().getProcessingYm().v());
		}
		
		return period;
	}

	/**
	 * Find by master.
	 *
	 * @param master the master
	 * @return the closure detail dto
	 */
	public ClosureDetailDto findByMaster(ClosureHistoryInDto master) {
		// get login user
		LoginUserContext loginUserContext = AppContexts.user();

		// get company id
		String companyId = loginUserContext.companyId();

		// call service
		Optional<Closure> closure = this.repository.findById(companyId, master.getClosureId());

		ClosureDetailDto dto = new ClosureDetailDto();

		Optional<ClosureHistory> closureHistory = this.repository.findById(companyId,
				master.getClosureId(), master.getStartDate());

		// exist data
		if (closure.isPresent()) {
			closure.get().saveToMemento(dto);
		}

		if (closureHistory.isPresent()) {
			closureHistory.get().saveToMemento(dto);
		}

		return dto;
	}
	
	/**
	 * Gets the max start date closure.
	 *
	 * @return the max start date closure
	 */
	public GeneralDate getMaxStartDateClosure() {
		// get login user
		LoginUserContext loginUserContext = AppContexts.user();

		// get company id
		String companyId = loginUserContext.companyId();

		// get all closure use
		List<Closure> lstClousreUse = this.repository.findAllUse(companyId);

		GeneralDate startDate = GeneralDate.min();

		for (Closure closure : lstClousreUse) {
			DatePeriod period = this.findByIdGetMonthDay(closure.getClosureId());

			if (period.start().compareTo(startDate) > ZERO_START_DATE) {
				startDate = period.start();
			}
		}

		return startDate;
	}

}
