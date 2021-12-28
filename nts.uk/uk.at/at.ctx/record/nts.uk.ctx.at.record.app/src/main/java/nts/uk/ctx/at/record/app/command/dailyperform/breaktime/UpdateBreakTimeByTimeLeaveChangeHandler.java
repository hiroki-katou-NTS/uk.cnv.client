package nts.uk.ctx.at.record.app.command.dailyperform.breaktime;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.record.dom.breakorgoout.BreakTimeOfDailyPerformance;
import nts.uk.ctx.at.record.dom.service.event.breaktime.BreakTimeOfDailyService;
import nts.uk.ctx.at.record.dom.service.event.common.EventHandleResult;
import nts.uk.ctx.at.record.dom.service.event.common.EventHandleResult.EventHandleAction;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.shr.com.context.AppContexts;

/** Event：休憩時間帯を補正する */
@Stateless
public class UpdateBreakTimeByTimeLeaveChangeHandler extends
		CommandHandlerWithResult<UpdateBreakTimeByTimeLeaveChangeCommand, EventHandleResult<BreakTimeOfDailyPerformance>> {

	@Inject
	private WorkInformationRepository workInfoRepo;

	@Inject
	private BreakTimeOfDailyService eventService;

	@Override
	/** 休憩時間帯を補正する */
	protected EventHandleResult<BreakTimeOfDailyPerformance> handle(
			CommandHandlerContext<UpdateBreakTimeByTimeLeaveChangeCommand> context) {
		UpdateBreakTimeByTimeLeaveChangeCommand command = context.getCommand();
		WorkInfoOfDailyPerformance wi = getWithDefaul(command.cachedWorkInfo, () -> getDefaultWorkInfo(command));
		if (wi == null) {
			return EventHandleResult.withResult(EventHandleAction.ABORT, null);
		}

		String companyId = getWithDefaul(command.companyId, () -> AppContexts.user().companyId());
		Optional<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformance =command.cachedTimeLeave;
		Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyAttd = timeLeavingOfDailyPerformance.isPresent()?Optional.ofNullable(timeLeavingOfDailyPerformance.get().getAttendance()):Optional.empty();
		IntegrationOfDaily working = new IntegrationOfDaily(
				command.employeeId,
				command.targetDate,
				wi.getWorkInformation(), //workInformation
				null, //calAttr
				null, //affiliationInfor
				Optional.empty(), //pcLogOnInfo
				new ArrayList<>(), //employeeError
				Optional.empty(), //outingTime
				new BreakTimeOfDailyAttd(), //breakTime
				Optional.empty(), //attendanceTimeOfDailyPerformance
				timeLeavingOfDailyAttd, //attendanceLeave
				Optional.empty(), //shortTime
				Optional.empty(), //specDateAttr
				Optional.empty(), //attendanceLeavingGate
				Optional.empty(), //anyItemValue
				command.cachedEditState.isPresent() ? command.cachedEditState.get().stream().map(c->c.getEditState()).collect(Collectors.toList()) : new ArrayList<>(),
				Optional.empty(),//tempTime
				new ArrayList<>(),//remarks
				new ArrayList<>(),//ouenTime
				new ArrayList<>(),//ouenTimeSheet
				Optional.empty());
		command.cachedBreackTime.ifPresent(b -> {
			working.setBreakTime(b.getTimeZone());
		});

		EventHandleResult<IntegrationOfDaily> result = eventService.correct(companyId, working, command.cachedWorkType, !command.actionOnCache);

		/** 「日別実績の休憩時間帯」を削除する */
		if (result.getAction() == EventHandleAction.DELETE) {
			return EventHandleResult.withResult(EventHandleAction.DELETE, null);
		}

		/** 「日別実績の休憩時間帯」を更新する */
		if (result.getAction() == EventHandleAction.UPDATE || result.getAction() == EventHandleAction.INSERT) {
			BreakTimeOfDailyAttd breakTime = result.getData().getBreakTime();
			return EventHandleResult.withResult(EventHandleAction.UPDATE, new BreakTimeOfDailyPerformance(command.employeeId,command.targetDate, breakTime) );
		}
		return EventHandleResult.onFail();
	}

	private WorkInfoOfDailyPerformance getDefaultWorkInfo(UpdateBreakTimeByTimeLeaveChangeCommand command) {
		return this.workInfoRepo.find(command.employeeId, command.targetDate).orElse(null);
	}

	private <T> T getWithDefaul(Optional<T> target, Supplier<T> defaultVal) {
		if (target.isPresent()) {
			return target.get();
		}
		return defaultVal.get();
	}
}
