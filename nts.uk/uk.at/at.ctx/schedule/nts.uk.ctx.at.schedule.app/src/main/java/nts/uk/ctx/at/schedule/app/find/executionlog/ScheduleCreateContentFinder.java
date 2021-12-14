/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.find.executionlog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.schedule.app.find.executionlog.dto.ScheduleCreateContentDto;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContent;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreateContentRepository;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreator;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleCreatorRepository;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLogRepository;
import nts.uk.ctx.at.schedule.dom.shift.pattern.monthly.MonthlyPatternRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class ScheduleCreateContentFinder.
 */
@Stateless
public class ScheduleCreateContentFinder {

	/** The schedule execution log repository. */
	@Inject
	private ScheduleExecutionLogRepository scheduleExecutionLogRepository;

	/** The schedule create content repository. */
	@Inject
	private ScheduleCreateContentRepository scheduleCreateContentRepository;

	/** The schedule creator repository. */
	@Inject
	private ScheduleCreatorRepository scheduleCreatorRepository;

	/** The schedule error log repository. */
	@Inject
	private ScheduleErrorLogRepository scheduleErrorLogRepository;

	@Inject
	private MonthlyPatternRepository monthlyPatternRepo;

	/**
	 * Find by execution id.
	 *
	 * @param executionId the execution id
	 * @return the schedule create content dto
	 */
	public ScheduleCreateContentDto findByExecutionId(String executionId) {
		String companyId = AppContexts.user().companyId();

		Optional<ScheduleExecutionLog> exeLogOp = scheduleExecutionLogRepository.findById(companyId, executionId);

		if (exeLogOp.isPresent()) {

			ScheduleCreateContent createContent = scheduleCreateContentRepository.findByExecutionId(executionId).get();

			// get count ScheduleCreator
			List<ScheduleCreator> lstCreator = scheduleCreatorRepository.findAll(executionId);

			// get count ScheduleError
			Integer cntError = scheduleErrorLogRepository.distinctErrorByExecutionId(executionId);

			ScheduleCreateContentDto dto = new ScheduleCreateContentDto();
			createContent.saveToMemento(dto);

			GeneralDateTime exeStart = exeLogOp.get().getExecutionDateTime().getExecutionStartDate();
			GeneralDateTime exeEnd = exeLogOp.get().getExecutionDateTime().getExecutionEndDate();
			GeneralDate startDate = exeLogOp.get().getPeriod().start();
			GeneralDate endDate = exeLogOp.get().getPeriod().end();
			dto.setStartDate(startDate);
			dto.setEndDate(endDate);
			dto.setExecutionStart(exeStart);
			dto.setExecutionEnd(exeEnd);
			dto.setCountExecution(lstCreator == null ? BigDecimal.ZERO.intValue() : lstCreator.size());
			dto.setCountError(cntError);
			if (dto.monthlyPatternCode != null) {
				monthlyPatternRepo.findById(companyId, dto.monthlyPatternCode).ifPresent(pattern -> {
					dto.setMonthlyPatternName(pattern.getMonthlyPatternName().v());
				});
			}
			return dto;
		}

		return null;
	}
}
