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
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleErrorLogRepository;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLog;
import nts.uk.ctx.at.schedule.dom.executionlog.ScheduleExecutionLogRepository;
import nts.uk.shr.com.context.AppContexts;

// TODO: Auto-generated Javadoc
/**
 * The Class ScheduleCreateContentFinder.
 */
@Stateless
public class ScheduleCreateContentFinder {

	/** The schedule execution log repository. */
	@Inject
	ScheduleExecutionLogRepository scheduleExecutionLogRepository;

	/** The schedule create content repository. */
	@Inject
	ScheduleCreateContentRepository scheduleCreateContentRepository;

	/** The schedule creator repository. */
	@Inject
	ScheduleCreatorRepository scheduleCreatorRepository;

	/** The schedule error log repository. */
	@Inject
	ScheduleErrorLogRepository scheduleErrorLogRepository;

	/**
	 * Find by execution id.
	 *
	 * @param executionId the execution id
	 * @return the schedule create content dto
	 */
	public ScheduleCreateContentDto findByExecutionId(String executionId) {
		String companyId = AppContexts.user().companyId();

		Optional<ScheduleExecutionLog> exeLogOp = scheduleExecutionLogRepository.findById(companyId, executionId);

		Optional<ScheduleCreateContent> createContentOp = scheduleCreateContentRepository
				.findByExecutionId(executionId);
		// get count ScheduleCreator
		List<ScheduleCreator> lstCreator = scheduleCreatorRepository.findAll(executionId);

		// get count ScheduleError
		List<ScheduleErrorLog> lstError = scheduleErrorLogRepository.findByExecutionId(executionId);

		if (createContentOp.isPresent()) {
			ScheduleCreateContentDto dto = new ScheduleCreateContentDto();
			createContentOp.get().saveToMemento(dto);
			if (exeLogOp.isPresent()) {
				GeneralDateTime exeStart = exeLogOp.get().getExecutionDateTime().getExecutionStartDate();
				GeneralDateTime exeEnd = exeLogOp.get().getExecutionDateTime().getExecutionEndDate();
				GeneralDate startDate = exeLogOp.get().getPeriod().getStartDate();
				GeneralDate endDate = exeLogOp.get().getPeriod().getEndDate();
				dto.setStartDate(startDate);
				dto.setEndDate(endDate);
				dto.setExecutionStart(exeStart);
				dto.setExecutionEnd(exeEnd);
			}
			dto.setCountExecution(lstCreator == null ? BigDecimal.ZERO.intValue() : lstCreator.size());
			dto.setCountError(lstError == null ? BigDecimal.ZERO.intValue() : lstError.size());
			return dto;
		}
		return null;
	}
}
