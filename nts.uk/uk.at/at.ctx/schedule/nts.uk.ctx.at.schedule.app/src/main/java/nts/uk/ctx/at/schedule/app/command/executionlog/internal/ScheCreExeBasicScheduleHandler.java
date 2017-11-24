/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.executionlog.internal;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.app.command.executionlog.ScheduleCreatorExecutionCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.basicschedule.BasicScheduleSaveCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.basicschedule.ChildCareScheduleSaveCommand;
import nts.uk.ctx.at.schedule.app.command.schedule.basicschedule.WorkScheduleTimeZoneSaveCommand;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.ScShortWorkTimeAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.ShortChildCareFrameDto;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.ShortWorkTimeDto;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.BasicSchedule;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.BasicScheduleRepository;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.ConfirmedAtr;
import nts.uk.ctx.at.schedule.dom.schedule.basicschedule.workscheduletimezone.BounceAtr;
import nts.uk.ctx.at.shared.dom.worktimeset_old.Timezone;
import nts.uk.ctx.at.shared.dom.worktimeset_old.WorkTimeSet;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSet;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeSetCheck;

/**
 * The Class ScheCreExeBasicScheduleHandler.
 */
@Stateless
public class ScheCreExeBasicScheduleHandler {
	
	/** The sc short work time adapter. */
	@Inject
	private ScShortWorkTimeAdapter scShortWorkTimeAdapter;
	
	/** The basic schedule repository. */
	@Inject
	private BasicScheduleRepository basicScheduleRepository;
	
	/** The sche cre exe error log handler. */
	@Inject
	private ScheCreExeErrorLogHandler scheCreExeErrorLogHandler;
	
	/** The sche cre exe work time handler. */
	@Inject
	private ScheCreExeWorkTimeHandler scheCreExeWorkTimeHandler;
	
	/** The work type repository. */
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	/**
	 * Update all data to command save.
	 *
	 * @param command the command
	 * @param employeeId the employee id
	 * @param worktypeCode the worktype code
	 * @param workTimeCode the work time code
	 */
	public void updateAllDataToCommandSave(ScheduleCreatorExecutionCommand command, String employeeId,
			String worktypeCode, String workTimeCode) {

		// get short work time
		Optional<ShortWorkTimeDto> optionalShortTime = this.getShortWorkTime(employeeId, command.getToDate());

		// add command save
		BasicScheduleSaveCommand commandSave = new BasicScheduleSaveCommand();
		commandSave.setWorktypeCode(worktypeCode);
		commandSave.setEmployeeId(employeeId);
		commandSave.setWorktimeCode(workTimeCode);
		commandSave.setYmd(command.getToDate());
		
		if (optionalShortTime.isPresent()) {
			commandSave
					.setChildCareSchedules(
							optionalShortTime.get().getLstTimeSlot().stream()
									.map(shortime -> this.convertShortTimeChildCareToChildCare(shortime,
											optionalShortTime.get().getChildCareAtr().value))
									.collect(Collectors.toList()));
		}

		// check not exist error 
		if (!this.scheCreExeErrorLogHandler.checkExistError(command.toBaseCommand(), employeeId)) {

			WorkTimeSetGetterCommand commandGetter = new WorkTimeSetGetterCommand();
			commandGetter.setWorktypeCode(worktypeCode);
			commandGetter.setCompanyId(command.getCompanyId());
			commandGetter.setWorkingCode(workTimeCode);
			Optional<WorkTimeSet> optionalWorkTimeSet = this.scheCreExeWorkTimeHandler
					.getScheduleWorkHour(commandGetter);
			if (optionalWorkTimeSet.isPresent()) {
				WorkTimeSet workTimeSet = optionalWorkTimeSet.get();
				commandSave.setWorkScheduleTimeZones(workTimeSet.getPrescribedTimezoneSetting().getTimezone().stream()
						.map(timezone -> this.convertTimeZoneToScheduleTimeZone(timezone))
						.collect(Collectors.toList()));
			}
		}
		// update is confirm
		commandSave.setConfirmedAtr(this.getConfirmedAtr(command.getIsConfirm(), ConfirmedAtr.CONFIRMED).value);

		// check parameter is delete before insert
		if (command.getIsDeleteBeforInsert()) {
			this.basicScheduleRepository.delete(employeeId, command.getToDate());
		}

		// save command
		this.saveBasicSchedule(commandSave);;
	}
	
	/**
	 * Gets the short work time.
	 *
	 * @param employeeId the employee id
	 * @param baseDate the base date
	 * @return the short work time
	 */
	// アルゴリズム (WorkTime)
	private Optional<ShortWorkTimeDto> getShortWorkTime(String employeeId, GeneralDate baseDate) {
		return this.scShortWorkTimeAdapter.findShortWorkTime(employeeId, baseDate);
	}
	
	
	/**
	 * Gets the confirmed atr.
	 *
	 * @param isConfirmContent the is confirm content
	 * @param confirmedAtr the confirmed atr
	 * @return the confirmed atr
	 */
	// 予定確定区分を取得
	private ConfirmedAtr getConfirmedAtr(boolean isConfirmContent, ConfirmedAtr confirmedAtr) {

		// check is confirm content
		if (isConfirmContent) {
			return ConfirmedAtr.CONFIRMED;
		} else {
			return confirmedAtr;
		}
	}
	
	/**
	 * Save basic schedule.
	 *
	 * @param command the command
	 */
	// 勤務予定情報を登録する
	private void saveBasicSchedule(BasicScheduleSaveCommand command) {

		// find basic schedule by id
		Optional<BasicSchedule> optionalBasicSchedule = this.basicScheduleRepository.find(command.getEmployeeId(),
				command.getYmd());

		// check exist data
		if (optionalBasicSchedule.isPresent()) {

			// update domain
			this.basicScheduleRepository.update(command.toDomain());
		} else {

			// insert domain
			this.basicScheduleRepository.insert(command.toDomain());
		}
	}
	
	/**
	 * Convert short tim child care to child care.
	 *
	 * @param shortChildCareFrameDto the short child care frame dto
	 * @return the child care schedule save command
	 */
	// 勤務予定育児介護時間帯
	private ChildCareScheduleSaveCommand convertShortTimeChildCareToChildCare(
			ShortChildCareFrameDto shortChildCareFrameDto, int childCareAtr) {
		ChildCareScheduleSaveCommand command = new ChildCareScheduleSaveCommand();

		// 予定育児介護回数 = 取得した短時間勤務. 時間帯. 回数
		command.setChildCareNumber(shortChildCareFrameDto.getTimeSlot());

		// 予定育児介護開始時刻 = 取得した短時間勤務. 時間帯. 開始時刻
		command.setChildCareScheduleStart(shortChildCareFrameDto.getStartTime().valueAsMinutes());

		// 予定育児介護終了時刻 = 取得した短時間勤務. 時間帯. 終了時刻
		command.setChildCareScheduleEnd(shortChildCareFrameDto.getEndTime().valueAsMinutes());

		// 育児介護区分 = 取得した短時間勤務. 育児介護区分
		command.setChildCareAtr(childCareAtr);
		return command;
	}
	
	/**
	 * Convert time zone to schedule time zone.
	 *
	 * @param timezone the timezone
	 * @return the work schedule time zone save command
	 */
	// 勤務予定時間帯
	private WorkScheduleTimeZoneSaveCommand convertTimeZoneToScheduleTimeZone(Timezone timezone){
		WorkScheduleTimeZoneSaveCommand command = new WorkScheduleTimeZoneSaveCommand();
		
		// 予定勤務回数 = 取得した勤務予定時間帯. 勤務NO
		command.setScheduleCnt(timezone.getWorkNo());
		
		// 予定開始時刻 = 取得した勤務予定時間帯. 開始
		command.setScheduleStartClock(timezone.getStart().valueAsMinutes());
		
		// 予定終了時刻 = 取得した勤務予定時間帯. 終了
		command.setScheduleEndClock(timezone.getEnd().valueAsMinutes());
		
		// TODO NOT WorktypeSet
		command.setBounceAtr(BounceAtr.NO_DIRECT_BOUNCE.value);
		return command;
	}
	
	
	/**
	 * Reset all data to command save.
	 *
	 * @param command the command
	 */
	public void resetAllDataToCommandSave(BasicScheduleResetCommand command, GeneralDate toDate) {
		// add command save
		BasicScheduleSaveCommand commandSave = new BasicScheduleSaveCommand();
		commandSave.setWorktypeCode(command.getWorkTypeCode());
		commandSave.setEmployeeId(command.getEmployeeId());
		commandSave.setWorktimeCode(command.getWorkingCode());
		commandSave.setYmd(toDate);
		commandSave = this.resetCreatedData(command, commandSave);
		// update is confirm
		commandSave.setConfirmedAtr(this.getConfirmedAtr(command.getConfirm(), ConfirmedAtr.CONFIRMED).value);

		// save command
		this.saveBasicSchedule(commandSave);;
	}
	
	/**
	 * Reset created data.
	 *
	 * @param resetAtr the reset atr
	 */
	// 作成済みのデータを再設定する
	private BasicScheduleSaveCommand resetCreatedData(BasicScheduleResetCommand command,
			BasicScheduleSaveCommand commandSave) {
		Optional<BounceAtr> optionalBounceAtr = this.resetDirectLineBounce(command);
		commandSave = this.resetWorkTime(command, commandSave);
		// check exist and not empty list
		if (optionalBounceAtr.isPresent() && !CollectionUtil.isEmpty(commandSave.getWorkScheduleTimeZones())) {
			commandSave.setWorkScheduleTimeZones(commandSave.getWorkScheduleTimeZones().stream().map(timeZone -> {
				timeZone.setBounceAtr(optionalBounceAtr.get().value);
				return timeZone;
			}).collect(Collectors.toList()));
		}
		return commandSave;
	}

	/**
	 * Reset direct line bounce.
	 */
	// 直行直帰再設定
	private Optional<BounceAtr> resetDirectLineBounce(BasicScheduleResetCommand command) {
		// check is 直行直帰再設定 TRUE
		if (command.getResetAtr().getResetDirectLineBounce()) {

			Optional<WorkType> optionalWorktype = this.workTypeRepository.findByPK(command.getCompanyId(),
					command.getWorkTypeCode());

			if (optionalWorktype.isPresent() && !CollectionUtil.isEmpty(optionalWorktype.get().getWorkTypeSetList())) {
				return optionalWorktype.get().getWorkTypeSetList().stream().findFirst()
						.map(workTypeSet -> this.resetWorkTypeSet(workTypeSet));
			}

		}
		return Optional.empty();
	}
	
	/**
	 * Reset work type set.
	 *
	 * @param workTypeSet the work type set
	 * @return the bounce atr
	 */
	private BounceAtr resetWorkTypeSet(WorkTypeSet workTypeSet) {

		// 出勤時刻を直行とする：False AND 退勤時刻を直行とする：False⇒ 直行直帰なし
		if (workTypeSet.getAttendanceTime() == WorkTypeSetCheck.NO_CHECK
				&& workTypeSet.getTimeLeaveWork() == WorkTypeSetCheck.NO_CHECK) {
			return BounceAtr.DIRECT_BOUNCE;
		}

		// 出勤時刻を直行とする：True AND 退勤時刻を直行とする：False⇒ 直行のみ
		if (workTypeSet.getAttendanceTime() == WorkTypeSetCheck.CHECK
				&& workTypeSet.getTimeLeaveWork() == WorkTypeSetCheck.NO_CHECK) {
			return BounceAtr.BOUNCE_ONLY;
		}

		// 出勤時刻を直行とする：False AND 退勤時刻を直行とする：True⇒ 直帰のみ
		if (workTypeSet.getAttendanceTime() == WorkTypeSetCheck.CHECK
				&& workTypeSet.getTimeLeaveWork() == WorkTypeSetCheck.NO_CHECK) {
			return BounceAtr.NO_DIRECT_BOUNCE;
		}

		// 出勤時刻を直行とする：True AND 退勤時刻を直行とする：True⇒ 直行直帰
		if (workTypeSet.getAttendanceTime() == WorkTypeSetCheck.CHECK
				&& workTypeSet.getTimeLeaveWork() == WorkTypeSetCheck.CHECK) {
			return BounceAtr.DIRECTLY_ONLY;
		}
		return BounceAtr.DIRECTLY_ONLY;
	}
	
	/**
	 * Reset work time.
	 *
	 * @param command the command
	 * @return the basic schedule save command
	 */
	// 就業時間帯再設定
	private BasicScheduleSaveCommand resetWorkTime(BasicScheduleResetCommand command,
			BasicScheduleSaveCommand commandSave) {

		// check 就業時間帯再設定 is TRUE
		if (command.getResetAtr().getResetWorkingHours()) {
			WorkTimeSetGetterCommand commandGetter = new WorkTimeSetGetterCommand();
			commandGetter.setWorktypeCode(command.getWorkTypeCode());
			commandGetter.setCompanyId(command.getCompanyId());
			commandGetter.setWorkingCode(command.getWorkingCode());
			Optional<WorkTimeSet> optionalWorkTimeSet = this.scheCreExeWorkTimeHandler
					.getScheduleWorkHour(commandGetter);
			if (optionalWorkTimeSet.isPresent()) {
				WorkTimeSet workTimeSet = optionalWorkTimeSet.get();
				commandSave.setWorkScheduleTimeZones(workTimeSet.getPrescribedTimezoneSetting().getTimezone().stream()
						.map(timezone -> this.convertTimeZoneToScheduleTimeZone(timezone))
						.collect(Collectors.toList()));
			}
		}
		return commandSave;
	}
}
