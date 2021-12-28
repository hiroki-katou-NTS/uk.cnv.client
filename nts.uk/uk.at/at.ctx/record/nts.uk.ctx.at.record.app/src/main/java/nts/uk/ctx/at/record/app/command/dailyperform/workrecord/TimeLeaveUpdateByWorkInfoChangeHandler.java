package nts.uk.ctx.at.record.app.command.dailyperform.workrecord;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.uk.ctx.at.record.dom.service.event.common.EventHandleResult;
import nts.uk.ctx.at.record.dom.service.event.common.EventHandleResult.EventHandleAction;
import nts.uk.ctx.at.record.dom.service.event.timeleave.TimeLeavingOfDailyService;
import nts.uk.ctx.at.record.dom.workinformation.WorkInfoOfDailyPerformance;
import nts.uk.ctx.at.record.dom.workinformation.repository.WorkInformationRepository;
import nts.uk.ctx.at.record.dom.worktime.TimeLeavingOfDailyPerformance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.shr.com.context.AppContexts;

/** Event：出退勤時刻を補正する */
@Stateless
public class TimeLeaveUpdateByWorkInfoChangeHandler extends CommandHandlerWithResult<TimeLeaveUpdateByWorkInfoChangeCommand, EventHandleResult<TimeLeavingOfDailyPerformance>> {

	@Inject
	private WorkInformationRepository workInfoRepo;

	@Inject
	private TimeLeavingOfDailyService eventService;
	
	@Override
	protected EventHandleResult<TimeLeavingOfDailyPerformance> handle(CommandHandlerContext<TimeLeaveUpdateByWorkInfoChangeCommand> context) {
		TimeLeaveUpdateByWorkInfoChangeCommand command = context.getCommand();

		WorkInfoOfDailyPerformance wi = getWithDefaul(command.cachedWorkInfo, () -> getDefaultWorkInfo(command));
		if(wi == null) {
			return EventHandleResult.onFail();
		}
		
		String companyId = getWithDefaul(command.companyId, () -> AppContexts.user().companyId());
		Optional<TimeLeavingOfDailyPerformance> timeLeavingOfDailyPerformance = command.cachedTimeLeave;
		Optional<TimeLeavingOfDailyAttd> timeLeavingOfDailyAttd = timeLeavingOfDailyPerformance.isPresent() && timeLeavingOfDailyPerformance.get().getAttendance() !=null?Optional.of(timeLeavingOfDailyPerformance.get().getAttendance()):Optional.empty();
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
		EventHandleResult<IntegrationOfDaily> result = eventService.correct(companyId, working, command.cachedWorkCondition, command.cachedWorkType, !command.actionOnCache);
		
		if(command.isTriggerRelatedEvent && result.getAction() != EventHandleAction.ABORT) {
			/** <<Event>> 実績の出退勤が変更されたイベントを発行する　*/
			result.getData().getAttendanceLeave().ifPresent(tl -> {
				tl.timeLeavesChanged(command.getEmployeeId(),command.getTargetDate());
			});
		}
		Optional<TimeLeavingOfDailyAttd> attendanceLeave = result.getData().getAttendanceLeave();
		TimeLeavingOfDailyPerformance timeLeavingOfDailyPer = new TimeLeavingOfDailyPerformance(command.employeeId,
				command.targetDate, 
				attendanceLeave.isPresent()?attendanceLeave.get():null);
		return EventHandleResult.withResult(result.getAction(), timeLeavingOfDailyPer);
	}

	private WorkInfoOfDailyPerformance getDefaultWorkInfo(TimeLeaveUpdateByWorkInfoChangeCommand command) {
		return this.workInfoRepo.find(command.employeeId, command.targetDate).orElse(null);
	}

	private <T> T getWithDefaul(Optional<T> target, Supplier<T> defaultVal){
		if(target.isPresent()){
			return target.get();
		}
		return defaultVal.get();
	}
}
