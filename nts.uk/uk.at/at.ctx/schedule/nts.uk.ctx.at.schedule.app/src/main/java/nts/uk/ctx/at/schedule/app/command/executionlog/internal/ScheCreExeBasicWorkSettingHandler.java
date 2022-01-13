/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.schedule.app.command.executionlog.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.dom.adapter.executionlog.ScWorkplaceAdapter;
import nts.uk.ctx.at.schedule.dom.adapter.executionlog.dto.AffWorkplaceHistoryItem;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.EmployeeGeneralInfoImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.classification.ExClassificationHistItemImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.classification.ExClassificationHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.workplace.ExWorkPlaceHistoryImported;
import nts.uk.ctx.at.schedule.dom.adapter.generalinfo.workplace.ExWorkplaceHistItemImported;
import nts.uk.ctx.at.schedule.dom.employeeinfo.WorkScheduleMasterReferenceAtr;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.BasicWorkSetting;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.ClassifiBasicWorkRepository;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.ClassificationBasicWork;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.CompanyBasicWork;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.CompanyBasicWorkRepository;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.WorkplaceBasicWork;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.WorkplaceBasicWorkRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarClass;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarClassRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarCompany;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarCompanyRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarWorkPlaceRepository;
import nts.uk.ctx.at.schedule.dom.shift.businesscalendar.daycalendar.CalendarWorkplace;

/**
 * The Class ScheCreExeBasicWorkSettingHandler.
 */
@Stateless
public class ScheCreExeBasicWorkSettingHandler {

	/** The sche cre exe error log handler. */
	@Inject
	private ScheCreExeErrorLogHandler scheCreExeErrorLogHandler;

	/** The sc workplace adapter. */
	@Inject
	private ScWorkplaceAdapter scWorkplaceAdapter;

	/** The work place basic work repository. */
	@Inject
	private WorkplaceBasicWorkRepository workplaceBasicWorkRepository;

	/** The calendar work place repository. */
	@Inject
	private CalendarWorkPlaceRepository calendarWorkPlaceRepository;

	/** The classification basic work repository. */
	@Inject
	private ClassifiBasicWorkRepository classificationBasicWorkRepository;

	/** The calendar class repository. */
	@Inject
	private CalendarClassRepository calendarClassRepository;

	/** The calendar company repository. */
	@Inject
	private CalendarCompanyRepository calendarCompanyRepository;

	/** The company basic work repository. */
	@Inject
	private CompanyBasicWorkRepository companyBasicWorkRepository;

	/** The Constant MUL_YEAR. */
	public static final int MUL_YEAR = 10000;

	/** The Constant MUL_MONTH. */
	public static final int MUL_MONTH = 100;

	/**
	 * Gets the workday division by wkp.
	 * 
	 * 職場の稼働日区分を取得する
	 * 
	 * @param command
	 * @return
	 */
	public Optional<Integer> getWorkdayDivisionByWkp(WorkdayAttrByWorkplaceGeterCommand command) {
		for (String workplaceId : command.getWorkplaceIds()) {

			// find calendar work place by id
			Optional<CalendarWorkplace> optionalCalendarWorkplace = this.calendarWorkPlaceRepository
					.findCalendarWorkplaceByDate(workplaceId, command.getBaseGetter().getToDate());

			// check exist data calendar work place
			if (optionalCalendarWorkplace.isPresent()) {
				return Optional.of(optionalCalendarWorkplace.get().getWorkDayDivision().value);
			}
		}

		// add error messageId Msg_588
//		this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_588");
		return Optional.empty();
	}

	/**
	 * To basic work setting.
	 * 
	 * @param domain
	 * @param workdayAtr
	 * @return
	 */
	private Optional<BasicWorkSetting> toBasicWorkSetting(WorkplaceBasicWork domain, int workdayAtr) {
		// check of basic work by work day atr
		for (BasicWorkSetting basicWorkSetting : domain.getBasicWorkSetting()) {
			if (basicWorkSetting.getWorkdayDivision().value == workdayAtr) {
				return Optional.ofNullable(basicWorkSetting);
			}
		}
		return Optional.empty();
	}

	/**
	 * Gets the basic work setting by workplace.
	 * 
	 * 職場の基本勤務設定を取得する
	 * 
	 * @param command
	 * @return
	 */
	public Optional<BasicWorkSetting> getBasicWorkSettingByWorkplace(
			BasicWorkSettingByWorkplaceGetterCommand command) {
		for (String workplaceId : command.getWorkplaceIds()) {
			// find basic work by id
			Optional<WorkplaceBasicWork> optionalWorkplaceBasicWork = this.workplaceBasicWorkRepository
					.findById(workplaceId);
			// check exist data WorkplaceBasicWork
			if (optionalWorkplaceBasicWork.isPresent()) {
				return this.toBasicWorkSetting(optionalWorkplaceBasicWork.get(), command.getWorkdayDivision());
			}
		} 

		return Optional.empty();
	}

	/**
	 * Gets the basic work setting by workday division.
	 * 
	 * 「稼働日区分」に対応する「基本勤務設定」を取得する
	 * 
	 * @param command
	 * @param mapClassificationHist
	 * @param mapWorkplaceHist
	 * @return
	 */
	private Optional<BasicWorkSetting> getBasicWorkSettingByWorkdayDivision(BasicWorkSettingGetterCommand command,
			Map<String, List<ExClassificationHistItemImported>> mapClassificationHist,
			Map<String, List<ExWorkplaceHistItemImported>> mapWorkplaceHist) {
		
		// check 基本勤務の参照先 is 職場 (referenceBusinessDayCalendar is WORKPLACE)
		if (command.getReferenceBasicWork() == WorkScheduleMasterReferenceAtr.WORKPLACE.value) {
			// EA No1683
			List<ExWorkplaceHistItemImported> listWorkplaceHistItem = mapWorkplaceHist.get(command.getEmployeeId());
			if (listWorkplaceHistItem != null) {
				Optional<ExWorkplaceHistItemImported> optWorkplaceHistItem = listWorkplaceHistItem.stream()
						.filter(workplaceHistItem -> workplaceHistItem.getPeriod()
								.contains(command.getBaseGetter().getToDate()))
						.findFirst();
				if (optWorkplaceHistItem.isPresent()) {
					// find by level work place
				//	List<String> workplaceIds = this.findWpkIdsBySid(command.getBaseGetter(), command.getEmployeeId());
					
					//[No.571]職場の上位職場を基準職場を含めて取得する
					List<String> workplaceIds = this.scWorkplaceAdapter.getWorkplaceIdAndUpper(command.getBaseGetter().getCompanyId(), command.getBaseGetter().getToDate(), optWorkplaceHistItem.get().getWorkplaceId());
					
					BasicWorkSettingByWorkplaceGetterCommand commandGetter = command.toBasicWorkplace();
					commandGetter.setWorkplaceIds(workplaceIds);
					// return basic work setting
					return this.getBasicWorkSettingByWorkplace(commandGetter);
				}
			}
			// add log error employee => 602
			this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_602",
					"#Com_Workplace");

		} else {
			// 営業日カレンダーの参照先 is 分類
			// referenceBusinessDayCalendar is CLASSIFICATION
			List<ExClassificationHistItemImported> listClassHistItem = mapClassificationHist
					.get(command.getEmployeeId());
			if (listClassHistItem != null) {
				Optional<ExClassificationHistItemImported> optClassHistItem = listClassHistItem.stream().filter(
						classHistItem -> classHistItem.getPeriod().contains(command.getBaseGetter().getToDate()))
						.findFirst();
				if (optClassHistItem.isPresent()) {
					// setup command getter by classification
					BasicWorkSettingByClassificationGetterCommand commandGetter = command.toBasicClassification();
					commandGetter.setClassificationCode(optClassHistItem.get().getClassificationCode());
					// return basic work setting by classification
					return this.getBasicWorkSettingByClassification(commandGetter);
				}
			}
			// add log error employee => 602
			this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_602",
					"#Com_Class");
		}

		// return default optional
		return Optional.empty();
	}

	/**
	 * Find wpk ids by sid.
	 * 
	 * 所属職場を含む上位職場を取得
	 * 
	 * @param command
	 * @param employeeId
	 * @return
	 */
//	private List<String> findWpkIdsBySid(ScheduleErrorLogGeterCommand command, String employeeId) {
//		return this.scWorkplaceAdapter.findWpkIdsBySid(command.getCompanyId(), employeeId, command.getToDate());
//	}

	/**
	 * Gets the basic work setting.
	 * 
	 * 基本勤務設定を取得する
	 * 
	 * @param command
	 * @param empGeneralInfo
	 * @return
	 */
	public Optional<BasicWorkSetting> getBasicWorkSetting(BasicWorkSettingGetterCommand command,
			EmployeeGeneralInfoImported empGeneralInfo) {

		Map<String, List<ExClassificationHistItemImported>> mapClassificationHist = empGeneralInfo
				.getClassificationDto().stream()
				.collect(Collectors.toMap(ExClassificationHistoryImported::getEmployeeId,
						ExClassificationHistoryImported::getClassificationItems));

		Map<String, List<ExWorkplaceHistItemImported>> mapWorkplaceHist = empGeneralInfo.getWorkplaceDto().stream()
				.collect(Collectors.toMap(ExWorkPlaceHistoryImported::getEmployeeId,
						ExWorkPlaceHistoryImported::getWorkplaceItems));

		// get work day atr by data business day calendar
		Optional<Integer> optionalBusinessDayCalendar = this.getBusinessDayCalendar(command, mapClassificationHist,
				mapWorkplaceHist);

		// check exist data
		if (optionalBusinessDayCalendar.isPresent()) {

			// setup basic work setting getter command
			command.setWorkdayDivision(optionalBusinessDayCalendar.get());
			// get basic work setting by command getter
			return this.getBasicWorkSettingByWorkdayDivision(command, mapClassificationHist, mapWorkplaceHist);
		}
		// return default optional
		return Optional.empty();
	}

	/**
	 * Gets the workday division by class.
	 * 
	 * 分類の稼働日区分を取得する
	 * 
	 * @param command
	 * @return
	 */
	public Optional<Integer> getWorkdayDivisionByClass(WorkdayAttrByClassGetterCommand command) {

		// find calendar classification by id
		Optional<CalendarClass> optionalCalendarClass = this.calendarClassRepository.findCalendarClassByDate(
				command.getBaseGetter().getCompanyId(), command.getClassificationCode(),
				command.getBaseGetter().getToDate());

		// check exist data
		if (optionalCalendarClass.isPresent()) {
			return Optional.ofNullable(optionalCalendarClass.get().getWorkDayDivision().value);
		} 
	// add error messageId Msg_588
//	this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_588");
	return Optional.empty();
	}

	/**
	 * Gets the business day calendar.
	 * 
	 * 営業日カレンダーから「稼働日区分」を取得する
	 * 
	 * @param command
	 * @param mapClassificationHist
	 * @param mapWorkplaceHist
	 * @return
	 */
	private Optional<Integer> getBusinessDayCalendar(BasicWorkSettingGetterCommand command,
			Map<String, List<ExClassificationHistItemImported>> mapClassificationHist,
			Map<String, List<ExWorkplaceHistItemImported>> mapWorkplaceHist) {
		// check 営業日カレンダーの参照先 is 職場 (referenceBusinessDayCalendar is WORKPLACE)
		if (command.getReferenceBusinessDayCalendar() == WorkScheduleMasterReferenceAtr.WORKPLACE.value) {
			// EA No1682
			// 「社員の履歴情報」から該当社員、該当日の職場情報を取得する
			List<ExWorkplaceHistItemImported> listWorkplaceHistItem = mapWorkplaceHist.get(command.getEmployeeId());
			if (listWorkplaceHistItem != null) {
				Optional<ExWorkplaceHistItemImported> optWorkplaceHistItem = listWorkplaceHistItem.stream()
						.filter(workplaceHistItem -> workplaceHistItem.getPeriod()
								.contains(command.getBaseGetter().getToDate()))
						.findFirst();
				if (optWorkplaceHistItem.isPresent()) {
					// List<String> workplaceIds =
					// this.findLevelWorkplace(command.getBaseGetter(),
					// workplaceDto.getWorkplaceCode()); FIXBUG #87217
					// List<String> workplaceIds = this.findWpkIdsBySid(command.getBaseGetter(), command.getEmployeeId());
					
					// [No.650]社員が所属している職場を取得する
					AffWorkplaceHistoryItem affWorkplaceHistoryItem = this.scWorkplaceAdapter.getAffWkpHistItemByEmpDate(command.getEmployeeId(), command.getBaseGetter().getToDate());
					
					//[No.571]職場の上位職場を基準職場を含めて取得する
					List<String> workplaceIds = this.scWorkplaceAdapter.getWorkplaceIdAndUpper(command.getBaseGetter().getCompanyId(), command.getBaseGetter().getToDate(), affWorkplaceHistoryItem.getWorkplaceId());
					
					
					// setup command getter work place

					WorkdayAttrByWorkplaceGeterCommand commandGetter = command.toCommandWorkplace();
					commandGetter.setWorkplaceIds(workplaceIds);
					// return work day atr by work place id
					return this.getWorkdayDivisionByWkp(commandGetter);
				}
			}
			// add log error employee => 602
			this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_602",
					"#Com_Workplace");

		} else {
			// 「社員の履歴情報」から該当社員、該当日の分類情報を取得する
			List<ExClassificationHistItemImported> listClassHistItem = mapClassificationHist
					.get(command.getEmployeeId());
			if (listClassHistItem != null) {
				Optional<ExClassificationHistItemImported> optClassHistItem = listClassHistItem.stream().filter(
						classHistItem -> classHistItem.getPeriod().contains(command.getBaseGetter().getToDate()))
						.findFirst();
				if (optClassHistItem.isPresent()) {

					// set command work day getter
					WorkdayAttrByClassGetterCommand commandGetter = command.toCommandClassification();
					commandGetter.setClassificationCode(optClassHistItem.get().getClassificationCode());

					// return work day atr by classification
					return this.getWorkdayDivisionByClass(commandGetter);

				}
			}
			// add log error employee => 602
			this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_602",
					"#Com_Class");
		}
		return Optional.empty();

	}

	/**
	 * To basic work setting classification.
	 * 
	 * @param domain
	 * @param workdayAtr
	 * @return
	 */
	private Optional<BasicWorkSetting> toBasicWorkSettingClassification(ClassificationBasicWork domain,
			int workdayAtr) {
		// find by work day atr
		for (BasicWorkSetting basicWorkSetting : domain.getBasicWorkSetting()) {
			if (basicWorkSetting.getWorkdayDivision().value == workdayAtr) {
				return Optional.ofNullable(basicWorkSetting);
			}
		}
		return Optional.empty();
	}

	/**
	 * To basic work setting company.
	 * 
	 * @param domain
	 * @param workdayAtr
	 * @return
	 */
	private Optional<BasicWorkSetting> toBasicWorkSettingCompany(CompanyBasicWork domain, int workdayAtr) {
		// find by work day atr
		for (BasicWorkSetting basicWorkSetting : domain.getBasicWorkSetting()) {
			if (basicWorkSetting.getWorkdayDivision().value == workdayAtr) {
				return Optional.ofNullable(basicWorkSetting);
			}
		}
		return Optional.empty();
	}

	/**
	 * Gets the basic work setting by classification.
	 * 
	 * 分類の基本勤務設定を取得する
	 * 
	 * @param command
	 * @return
	 */
	public Optional<BasicWorkSetting> getBasicWorkSettingByClassification(
			BasicWorkSettingByClassificationGetterCommand command) {
		// find classification basic work by id
		Optional<ClassificationBasicWork> optionalClassificationBasicWork = this.classificationBasicWorkRepository
				.findById(command.getBaseGetter().getCompanyId(), command.getClassificationCode(),
						command.getWorkdayDivision());

		// check exist data classification basic work
		if (optionalClassificationBasicWork.isPresent()) {

			// return basic work setting by classification
			return this.toBasicWorkSettingClassification(optionalClassificationBasicWork.get(),
					command.getWorkdayDivision());
		} else {

			// find company basic work by id
			Optional<CompanyBasicWork> optionalCompanyBasicWork = this.companyBasicWorkRepository
					.findById(command.getBaseGetter().getCompanyId(), command.getWorkdayDivision());

			// check exist data company basic work
			if (optionalCompanyBasicWork.isPresent()) {
				return this.toBasicWorkSettingCompany(optionalCompanyBasicWork.get(), command.getWorkdayDivision());
			} else {

				// add error message 589
//				this.scheCreExeErrorLogHandler.addError(command.getBaseGetter(), command.getEmployeeId(), "Msg_589");
			}
		}
		return Optional.empty();
	}
}
