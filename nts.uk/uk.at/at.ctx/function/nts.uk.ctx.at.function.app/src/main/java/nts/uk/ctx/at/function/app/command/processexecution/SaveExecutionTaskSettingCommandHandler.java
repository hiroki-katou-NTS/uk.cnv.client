package nts.uk.ctx.at.function.app.command.processexecution;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.quartz.CronExpression;

//import org.apache.logging.log4j.core.util.CronExpression;
//import org.eclipse.persistence.jpa.jpql.parser.WhenClause;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.layer.app.command.CommandHandlerWithResult;
import nts.arc.task.schedule.cron.CronExpressionBuilder;
//import nts.arc.primitive.TimeAsMinutesPrimitiveValue;
import nts.arc.task.schedule.cron.CronSchedule;
import nts.arc.task.schedule.job.jobdata.ScheduledJobUserData;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.function.app.find.processexecution.dto.ExecutionTaskSettingDto;
import nts.uk.ctx.at.function.dom.processexecution.ProcessExecutionService;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.CurrentExecutionStatus;
import nts.uk.ctx.at.function.dom.processexecution.executionlog.ProcessExecutionLogManage;
import nts.uk.ctx.at.function.dom.processexecution.repository.ExecutionTaskSettingRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.ProcessExecutionLogManageRepository;
import nts.uk.ctx.at.function.dom.processexecution.repository.RepeatMonthDayRepository;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.ExecutionTaskSetting;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.detail.RepeatMonthDaysSelect;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.CronType;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.EndDateClassification;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.EndTimeClassification;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.OneDayRepeatClassification;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.enums.RepeatContentItem;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.primitivevalue.EndTime;
import nts.uk.ctx.at.function.dom.processexecution.tasksetting.primitivevalue.OneDayRepeatIntervalDetail;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.task.schedule.UkJobScheduleOptions;
import nts.uk.shr.com.task.schedule.UkJobScheduler;
//import nts.uk.shr.sample.task.schedule.SampleScheduledJob;

@Stateless
public class SaveExecutionTaskSettingCommandHandler
		extends CommandHandlerWithResult<SaveExecutionTaskSettingCommand, ExecutionTaskSettingDto> {

	@Inject
	private ExecutionTaskSettingRepository execTaskSettingRepo;

	@Inject
	private RepeatMonthDayRepository repMonthDayRepo;

	@Inject
	private ProcessExecutionLogManageRepository processExecutionLogManageRepository;

	@Inject
	private UkJobScheduler scheduler;

	@Inject
	private ProcessExecutionService processExecutionService;

	@Resource
	private SessionContext scContext;

	private SaveExecutionTaskSettingCommandHandler self;

	@PostConstruct
	public void init() {
		this.self = scContext.getBusinessObject(SaveExecutionTaskSettingCommandHandler.class);
	}

	@Override
	protected ExecutionTaskSettingDto handle(CommandHandlerContext<SaveExecutionTaskSettingCommand> context) {
		SaveExecutionTaskSettingCommand command = context.getCommand();
		String companyId = AppContexts.user().companyId();

		// 繰り返し詳細設定(毎月)
		List<RepeatMonthDaysSelect> days = command.getRepeatMonthDateList().stream()
				.map(x -> EnumAdaptor.valueOf(x, RepeatMonthDaysSelect.class)).collect(Collectors.toList());
		// ドメインモデル「更新処理自動実行管理」を取得し、現在の実行状態を判断する
		Optional<ProcessExecutionLogManage> optLogManage = processExecutionLogManageRepository
				.getLogByCIdAndExecCd(companyId, command.getExecItemCd());
		if (!optLogManage.isPresent()) {
			return null;
		}
		ProcessExecutionLogManage logManage = optLogManage.get();
		// 「実行中」の場合
		// エラーメッセージ「#Msg_1318」を表示する
		if (logManage.getCurrentStatus().isPresent()
				&& logManage.getCurrentStatus().get().equals(CurrentExecutionStatus.RUNNING)) {
			throw new BusinessException("Msg_1318");
		}
		// 「実行中」以外の場合
		// 画面モードチェック
		ExecutionTaskSetting taskSetting = command.toDomain();
		taskSetting.setCompanyId(companyId);
		// 登録チェック処理
		taskSetting.validate();
		/*
		 * // Calculate next execution date time taskSetting.setNextExecDateTime();
		 */
		Map<CronType, CronSchedule> lstcron = this.getCron(command);
		val scheduletimeData = new ScheduledJobUserData();
		scheduletimeData.put("companyId", companyId);
		scheduletimeData.put("execItemCd", command.getExecItemCd());
		scheduletimeData.put("companyCd", AppContexts.user().companyCode());
		
		//Revalidate all cron expression and filter out invalid ones
		Map<CronType, UkJobScheduleOptions> optionsList = lstcron.entrySet().stream()
				.filter(e -> CronExpression.isValidExpression(e.getValue().getCronExpressions().get(0)))
				.collect(Collectors.toMap(Entry::getKey,
						e -> this.processExecutionService.buildScheduleOptions(SortingProcessScheduleJob.class,
								e.getKey(), e.getValue(), scheduletimeData, taskSetting)));
		
		//Hiện tại chưa có job schedule cho endTime
		//Tạo thêm job cho endTime nếu tồn tại endTime
		String finishCron = "";
		if (command.getEndTimeCls() == 1) {
			String dateCron = " * * ?"; 
			
			finishCron = "0 " + command.getEndTime() % 60 + " " + command.getEndTime() / 60 + dateCron;
			
			Optional<GeneralDate> endDate = taskSetting.getEndDate().getEndDateCls().equals(EndDateClassification.DATE)
					? taskSetting.getEndDate().getEndDate()
					: Optional.empty();
			Optional<EndTime> endTime = taskSetting.getOneDayRepInr().getOneDayRepCls()
					.equals(OneDayRepeatClassification.YES)
					&& taskSetting.getEndTime().getEndTimeCls().equals(EndTimeClassification.YES)
							? taskSetting.getEndTime().getEndTime()
							: Optional.empty();
			UkJobScheduleOptions.Builder builder = UkJobScheduleOptions.builder(SortingProcessEndScheduleJob.class,
					"KBT002_" + taskSetting.getExecItemCd().v() + "_stop", new CronSchedule(Arrays.asList(finishCron)))
					.userData(scheduletimeData);
			endDate.ifPresent(builder::endDate);
			endTime.ifPresent(builder::endClock);
			this.scheduleOnCurrentCompany(builder.build());
		}
		
		if (!command.isNewMode()) {
			this.unscheduleOld(command, companyId);
		}

		String scheduleId = Optional.ofNullable(optionsList.get(CronType.START)).map(this::scheduleOnCurrentCompany)
				.orElse(null);
		Optional<String> endScheduleId = Optional.ofNullable(optionsList.get(CronType.END))
				.map(this::scheduleOnCurrentCompany);
		Optional<String> repScheduleId = Optional.ofNullable(optionsList.get(CronType.REPEAT))
				.map(this::scheduleOnCurrentCompany);
		taskSetting.setScheduleId(scheduleId);
		taskSetting.setRepeatScheduleId(repScheduleId);
		taskSetting.setEndScheduleId(endScheduleId);

		try {
			GeneralDateTime nextExecDateTime = this.processExecutionService.processNextExecDateTimeCreation(taskSetting);
			taskSetting.setNextExecDateTime(Optional.ofNullable(nextExecDateTime));
			self.saveTaskSetting(command, taskSetting, companyId, days, scheduleId, endScheduleId.orElse(null),
					repScheduleId.orElse(null));
			if (taskSetting.isEnabledSetting()) {
				logManage.setCurrentStatus(CurrentExecutionStatus.WAITING);
			} else {
				logManage.setCurrentStatus(CurrentExecutionStatus.INVALID);
			}
			this.processExecutionLogManageRepository.update(logManage);
		} catch (Exception e) {
			throw new BusinessException("Msg_1110");
		}

		return ExecutionTaskSettingDto.fromDomain(taskSetting);
	}

	@Transactional(value = TxType.REQUIRED, rollbackOn = Exception.class)
	public void saveTaskSetting(SaveExecutionTaskSettingCommand command, ExecutionTaskSetting taskSetting,
			String companyId, List<RepeatMonthDaysSelect> days, String scheduleId, String endScheduleId,
			String repScheduleId) throws Exception {
		if (command.isNewMode()) {
			try {
				this.execTaskSettingRepo.insert(taskSetting);
				this.repMonthDayRepo.insert(companyId, command.getExecItemCd(), days);
			} catch (Exception e) {
				this.scheduler.unscheduleOnCurrentCompany(scheduleId);

				if (endScheduleId != null) {
					this.scheduler.unscheduleOnCurrentCompany(endScheduleId);
				}

				if (repScheduleId != null) {
					this.scheduler.unscheduleOnCurrentCompany(repScheduleId);
				}

				throw new BusinessException("Msg_1110");
			}

		} else {
			try {
				this.execTaskSettingRepo.remove(companyId, command.getExecItemCd());
				this.repMonthDayRepo.removeAllByCidAndExecCd(companyId, command.getExecItemCd());
				this.execTaskSettingRepo.insert(taskSetting);
				this.repMonthDayRepo.insert(companyId, command.getExecItemCd(), days);
				// this.execTaskSettingRepo.update(taskSetting);
			} catch (Exception e) {
				this.scheduler.unscheduleOnCurrentCompany(scheduleId);

				if (endScheduleId != null) {
					this.scheduler.unscheduleOnCurrentCompany(endScheduleId);
				}

				if (repScheduleId != null) {
					this.scheduler.unscheduleOnCurrentCompany(repScheduleId);
				}

				throw new BusinessException("Msg_1110");
			}

		}
	}

	private void unscheduleOld(SaveExecutionTaskSettingCommand command, String companyId) {
		ExecutionTaskSetting executionTaskSetting = this.execTaskSettingRepo
				.getByCidAndExecCd(companyId, command.getExecItemCd()).get();
		String oldScheduleId = executionTaskSetting.getScheduleId();

		Optional<String> oldEndScheduleIdOpt = executionTaskSetting.getEndScheduleId();
		if (oldEndScheduleIdOpt.isPresent()) {
			this.scheduler.unscheduleOnCurrentCompany(oldEndScheduleIdOpt.get());
		}

		executionTaskSetting.getRepeatScheduleId().ifPresent(repeatScheduleId -> this.scheduler
				.unscheduleOnCurrentCompany(repeatScheduleId));

		this.scheduler.unscheduleOnCurrentCompany(oldScheduleId);
	}

	@Transactional(value = TxType.REQUIRED, rollbackOn = Exception.class)
	public String scheduleOnCurrentCompany(UkJobScheduleOptions options) {
		return this.scheduler.scheduleOnCurrentCompany(options).getScheduleId();
	}

	public boolean isLastDayOfMonth(int year, int month, int day) {
		GregorianCalendar calendar = new GregorianCalendar();

		// adjust the month for a zero based index
		month = month - 1;

		// set the date of the calendar to the date provided
		calendar.set(year, month, 1);

		int dayInt = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		if (day == dayInt) {
			return true;
		}
		return false;
	}

	public Map<CronType, CronSchedule> getCron(SaveExecutionTaskSettingCommand command) {
		Map<CronType, String> lstCron = new HashMap<>();

		Integer repeatMinute = null;
		Integer startTime = command.getStartTime();
		int startHours = startTime / 60;
		int startMinute = startTime % 60;

		Integer endTime = null;
		Integer endMinute = null;
		Integer endHour = null;
		if (command.getEndTimeCls() == 1) {
			endTime = command.getEndTime();
			endMinute = endTime % 60;
			endHour = endTime / 60;
		}
		Integer startTimeRun = null;

		if (command.getOneDayRepInterval() != null && command.getOneDayRepCls() == 1) {
			repeatMinute = EnumAdaptor.valueOf(command.getOneDayRepInterval(), OneDayRepeatIntervalDetail.class)
					.getMinuteValue();
		}

		if (repeatMinute != null) {
			startTimeRun = startMinute % repeatMinute;
		}

		command.getOneDayRepInterval();

		StringBuilder cronExpress = new StringBuilder();
		StringBuilder cronExpress2 = null;
		StringBuilder cronExpress3 = null;
		int repeatContent = command.getRepeatContent().intValue();
		// fixbug when not repeat day week month
//		if (!command.isRepeatCls()) {
//			repeatContent = 0;
//		}
		switch (EnumAdaptor.valueOf(repeatContent, RepeatContentItem.class)) {
		case ONCE_TIME:
			GeneralDateTime now = GeneralDateTime.now();
			Integer timeSystem = now.minutes() + now.hours() * 60;
			GeneralDate today = now.toDate();
			GeneralDate startDate = command.getStartDate();
			// →次回実行日時の日付がシステム日付で、システム日時が次回実行日時の開始時刻を過ぎていたら
			if (startDate.equals(today)) {
				if (startTime < timeSystem) {
					// 開始日をシステム日付の 次の日付にする。
					startDate = today.addDays(1);
				}
			}
			// →次回実行日時がシステム日時より前であった場合
			else if (startDate.before(today)) {
				// 開始日をシステム日付にする。
				startDate = today;
			}
			CronSchedule cron = CronExpressionBuilder.startBuildExpression()
					.seconds(0).minutes(startMinute).hours(startHours)
					.dayOfMonths(startDate.day()).months(startDate.month()).years(startDate.year())
					.build();
			return Collections.singletonMap(CronType.START, cron);
		case WEEKLY_DAYS: // day
			if (repeatMinute == null) {
				cronExpress.append("0 " + startMinute + " " + startHours + " * * ? ");
			} else {
				if (repeatMinute == 60) {
					if (command.getEndTimeCls() == 1) {
						if (command.getEndTime() % 60 > startMinute) {
							cronExpress.append("0 " + startMinute + " " + startHours + "-" + command.getEndTime() / 60
									+ "/1 * * ? ");
						} else {
							cronExpress.append("0 " + startMinute + " " + startHours + "-"
									+ (command.getEndTime() / 60 - 1) + "/1 * * ? ");
						}
					} else {
						cronExpress.append("0 " + startMinute + " " + startHours + "/1 * * ? ");
					}
				} else {
					if (command.getEndTimeCls() == 1) {
						if (endHour - startHours == 0) {
							cronExpress = this.getCron(startMinute, endMinute, repeatMinute, startHours);
							cronExpress.append(" * * ? ");
						} else if (endHour - startHours == 1) {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
							cronExpress.append(" * * ? ");
							cronExpress2.append(" * * ? ");

						} else {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCronRangeMoreTwo(startTimeRun, 60, repeatMinute, startHours,
									endHour);
							cronExpress3 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
							cronExpress.append(" * * ? ");
							cronExpress2.append(" * * ? ");
							cronExpress3.append(" * * ? ");
						}
					} else {
						cronExpress.append("0 0/" + repeatMinute + " * * * ? ");
					}
				}

			}
			break;
		case SPECIFIED_WEEK_DAYS: // week

			if (!command.isSunday() && !command.isMonday() && !command.isTuesday() && !command.isWednesday()
					&& !command.isThursday() && !command.isFriday() && !command.isSaturday()) {
				if (repeatMinute == null) {
					cronExpress.append("0 " + startMinute + " " + startHours + " * * ? ");
				} else {
					if (repeatMinute == 60) {
						if (command.getEndTimeCls() == 1) {
							if (command.getEndTime() % 60 > startMinute) {
								cronExpress.append("0 " + startMinute + " " + startHours + "-"
										+ command.getEndTime() / 60 + "/1 * * ? ");
							} else {
								cronExpress.append("0 " + startMinute + " " + startHours + "-"
										+ (command.getEndTime() / 60 - 1) + "/1 * * ? ");
							}
						} else {
							cronExpress.append("0 " + startMinute + " " + startHours + "/1 * * ? ");
						}
					} else {
						if (command.getEndTimeCls() == 1) {
							if (endHour - startHours == 0) {
								cronExpress = this.getCron(startMinute, endMinute, repeatMinute, startHours);
								cronExpress.append(" ? * * ");
							} else if (endHour - startHours == 1) {
								cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
								cronExpress2 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
								cronExpress.append(" ? * * ");
								cronExpress2.append(" ? * * ");

							} else {
								cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
								cronExpress2 = this.getCronRangeMoreTwo(startTimeRun, 60, repeatMinute, startHours,
										endHour);
								cronExpress3 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
								cronExpress.append(" ? * * ");
								cronExpress2.append(" ? * * ");
								cronExpress3.append(" ? * * ");
							}
						} else {
							cronExpress.append("0 0/" + repeatMinute + " * ? * * ");
						}
					}

				}
				break;
			}

			if (repeatMinute == null) {
				cronExpress.append("0 " + startMinute + " " + startHours + " ? ");
			} else {
				if (repeatMinute == 60) {
					if (command.getEndTimeCls() == 1) {
						if (command.getEndTime() % 60 > startMinute) {
							cronExpress.append(
									"0 " + startMinute + " " + startHours + "-" + command.getEndTime() / 60 + "/1 ? ");
						} else {
							cronExpress.append("0 " + startMinute + " " + startHours + "-"
									+ (command.getEndTime() / 60 - 1) + "/1 ? ");
						}
					} else {
						cronExpress.append("0 " + startMinute + " " + startHours + "/1 ? ");
					}

				} else {
					if (command.getEndTimeCls() == 1) {
						if (endHour - startHours == 0) {
							cronExpress = this.getCron(startMinute, endMinute, repeatMinute, startHours);
							cronExpress.append(" ? ");
						} else if (endHour - startHours == 1) {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
							cronExpress.append(" ? ");
							cronExpress2.append(" ? ");

						} else {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCronRangeMoreTwo(startTimeRun, 60, repeatMinute, startHours,
									endHour);
							cronExpress3 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
							cronExpress.append(" ? ");
							cronExpress2.append(" ? ");
							cronExpress3.append(" ? ");
						}
					} else {
						cronExpress.append("0 0/" + repeatMinute + " *	 ? ");
					}
				}

			}
			cronExpress.append(" * ");
			if (cronExpress2 != null) {
				cronExpress2.append(" * ");
			}
			if (cronExpress3 != null) {
				cronExpress3.append(" * ");
			}
			if (command.isSunday()) {
				cronExpress.append("SUN,");
				if (cronExpress2 != null) {
					cronExpress2.append("SUN,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("SUN,");
				}
			}
			if (command.isMonday()) {
				cronExpress.append("MON,");
				if (cronExpress2 != null) {
					cronExpress2.append("MON,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("MON,");
				}
			}
			if (command.isTuesday()) {
				cronExpress.append("TUE,");
				if (cronExpress2 != null) {
					cronExpress2.append("TUE,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("TUE,");
				}
			}
			if (command.isWednesday()) {
				cronExpress.append("WED,");
				if (cronExpress2 != null) {
					cronExpress2.append("WED,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("WED,");
				}
			}
			if (command.isThursday()) {
				cronExpress.append("THU,");
				if (cronExpress2 != null) {
					cronExpress2.append("THU,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("THU,");
				}
			}
			if (command.isFriday()) {
				cronExpress.append("FRI,");
				if (cronExpress2 != null) {
					cronExpress2.append("FRI,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("FRI,");
				}
			}
			if (command.isSaturday()) {
				cronExpress.append("SAT");
				if (cronExpress2 != null) {
					cronExpress2.append("SAT,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("SAT,");
				}
			}
			if (cronExpress.toString().endsWith(",")) {
				cronExpress.deleteCharAt(cronExpress.length() - 1);
				if (cronExpress2 != null) {
					cronExpress2.deleteCharAt(cronExpress2.length() - 1);
				}
				if (cronExpress3 != null) {
					cronExpress3.deleteCharAt(cronExpress3.length() - 1);
				}
			}
			break;
		case SPECIFIED_IN_MONTH: // month
			List<Integer> repeatMonthDateList = command.getRepeatMonthDateList();
			/*
			 * if(command.getRepeatMonthDateList().isEmpty()){ if(repeatMinute==null){
			 * cronExpress.append("0 "+startMinute+" "+startHours+" ? "); }else {
			 * if(command.getEndTimeCls()==1){ if(endHour-startHours ==0){ cronExpress =
			 * Test.getCron(startMinute, endMinute, repeatMinute, startHours);
			 * cronExpress.append(" ? "); }else if(endHour-startHours==1){ cronExpress =
			 * Test.getCron(startMinute, 60, repeatMinute, startHours); cronExpress2 =
			 * Test.getCron(startTimeRun, endMinute, repeatMinute, endHour);
			 * cronExpress.append(" ? "); cronExpress2.append(" ? ");
			 * 
			 * }else{ cronExpress = Test.getCron(startMinute, 60, repeatMinute, startHours);
			 * cronExpress2 = Test.getCronRangeMoreTwo(startTimeRun, 60,
			 * repeatMinute,startHours, endHour); cronExpress3 = Test.getCron(startTimeRun,
			 * endMinute, repeatMinute, endHour); cronExpress.append(" ? ");
			 * cronExpress2.append(" ? "); cronExpress3.append(" ? "); } }else{
			 * cronExpress.append("0 0/"+repeatMinute+" * ? "); } }
			 * 
			 * if(command.isJanuary()){ cronExpress.append("JAN,"); if(cronExpress2!=null){
			 * cronExpress2.append("JAN,"); } if(cronExpress3!=null){
			 * cronExpress3.append("JAN,"); } } if(command.isFebruary()){
			 * cronExpress.append("FEB,"); if(cronExpress2!=null){
			 * cronExpress2.append("FEB,"); } if(cronExpress3!=null){
			 * cronExpress3.append("FEB,"); } } if(command.isMarch()){
			 * cronExpress.append("MAR,"); if(cronExpress2!=null){
			 * cronExpress2.append("MAR,"); } if(cronExpress3!=null){
			 * cronExpress3.append("MAR,"); } } if(command.isApril()){
			 * cronExpress.append("APR,"); if(cronExpress2!=null){
			 * cronExpress2.append("APR,"); } if(cronExpress3!=null){
			 * cronExpress3.append("APR,"); } } if(command.isMay()){
			 * cronExpress.append("MAY,"); if(cronExpress2!=null){
			 * cronExpress2.append("MAY,"); } if(cronExpress3!=null){
			 * cronExpress3.append("MAY,"); } } if(command.isJune()){
			 * cronExpress.append("JUN,"); if(cronExpress2!=null){
			 * cronExpress2.append("JUN,"); } if(cronExpress3!=null){
			 * cronExpress3.append("JUN,"); } } if(command.isJuly()){
			 * cronExpress.append("JUL,"); if(cronExpress2!=null){
			 * cronExpress2.append("JUL,"); } if(cronExpress3!=null){
			 * cronExpress3.append("JUL,"); } } if(command.isAugust()){
			 * cronExpress.append("AUG,"); if(cronExpress2!=null){
			 * cronExpress2.append("AUG,"); } if(cronExpress3!=null){
			 * cronExpress3.append("AUG,"); } } if(command.isSeptember()){
			 * cronExpress.append("SEP,"); if(cronExpress2!=null){
			 * cronExpress2.append("SEP,"); } if(cronExpress3!=null){
			 * cronExpress3.append("SEP,"); } } if(command.isOctober()){
			 * cronExpress.append("OCT,"); if(cronExpress2!=null){
			 * cronExpress2.append("OCT,"); } if(cronExpress3!=null){
			 * cronExpress3.append("OCT,"); } } if(command.isNovember()){
			 * cronExpress.append("NOV,"); if(cronExpress2!=null){
			 * cronExpress2.append("NOV,"); } if(cronExpress3!=null){
			 * cronExpress3.append("NOV,"); } } if(command.isDecember()){
			 * cronExpress.append("DEC"); if(cronExpress2!=null){
			 * cronExpress2.append("DEC"); } if(cronExpress3!=null){
			 * cronExpress3.append("DEC"); } } if(cronExpress.toString().endsWith(",")){
			 * cronExpress.deleteCharAt(cronExpress.length()-1); if(cronExpress2!=null){
			 * cronExpress2.deleteCharAt(cronExpress.length()-1); } if(cronExpress3!=null){
			 * cronExpress3.deleteCharAt(cronExpress.length()-1); } }
			 * cronExpress.append(" * *"); if(cronExpress2!=null){
			 * cronExpress2.append(" * *"); } if(cronExpress3!=null){
			 * cronExpress3.append(" * *"); } break; }
			 */

			if (repeatMinute == null) {
				cronExpress.append("0 " + startMinute + " " + startHours + " ");
			} else {
				if (repeatMinute == 60) {
					if (command.getEndTimeCls() == 1) {
						if (command.getEndTime() % 60 > startMinute) {
							cronExpress.append(
									"0 " + startMinute + " " + startHours + "-" + command.getEndTime() / 60 + "/1 ");
						} else {
							cronExpress.append("0 " + startMinute + " " + startHours + "-"
									+ (command.getEndTime() / 60 - 1) + "/1 ");
						}
					} else {
						cronExpress.append("0 " + startMinute + " " + startHours + "/1 ");
					}
				} else {
					if (command.getEndTimeCls() == 1) {
						if (endHour - startHours == 0) {
							cronExpress = this.getCron(startMinute, endMinute, repeatMinute, startHours);
						} else if (endHour - startHours == 1) {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);

						} else {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCronRangeMoreTwo(startTimeRun, 60, repeatMinute, startHours,
									endHour);
							cronExpress3 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
						}
					} else {
						cronExpress.append("0 0/" + repeatMinute + " * ");
					}
				}
			}

			if (repeatMonthDateList.contains(32) && repeatMonthDateList.size() == 1) {
				cronExpress.append("L");
			} else {
				for (int dayOfMonth : repeatMonthDateList) {
					if (dayOfMonth != 32) {
						cronExpress.append(dayOfMonth + ",");
					}
				}
			}

			if (cronExpress.toString().endsWith(",")) {
				cronExpress.deleteCharAt(cronExpress.length() - 1);
			}
			cronExpress.append(" ");
			if (cronExpress2 != null) {
				for (int dayOfMonth : repeatMonthDateList) {
					cronExpress2.append(dayOfMonth + ",");
				}
				if (cronExpress2.toString().endsWith(",")) {
					cronExpress2.deleteCharAt(cronExpress2.length() - 1);
				}
				cronExpress2.append(" ");
			}
			if (cronExpress3 != null) {
				for (int dayOfMonth : repeatMonthDateList) {
					cronExpress3.append(dayOfMonth + ",");
				}
				if (cronExpress3.toString().endsWith(",")) {
					cronExpress3.deleteCharAt(cronExpress3.length() - 1);
				}
				cronExpress3.append(" ");
			}

			if (command.isJanuary()) {
				cronExpress.append("JAN,");
				if (cronExpress2 != null) {
					cronExpress2.append("JAN,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("JAN,");
				}
			}
			if (command.isFebruary()) {
				cronExpress.append("FEB,");
				if (cronExpress2 != null) {
					cronExpress2.append("FEB,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("FEB,");
				}
			}
			if (command.isMarch()) {
				cronExpress.append("MAR,");
				if (cronExpress2 != null) {
					cronExpress2.append("MAR,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("MAR,");
				}
			}
			if (command.isApril()) {
				cronExpress.append("APR,");
				if (cronExpress2 != null) {
					cronExpress2.append("APR,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("APR,");
				}
			}
			if (command.isMay()) {
				cronExpress.append("MAY,");
				if (cronExpress2 != null) {
					cronExpress2.append("MAY,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("MAY,");
				}
			}
			if (command.isJune()) {
				cronExpress.append("JUN,");
				if (cronExpress2 != null) {
					cronExpress2.append("JUN,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("JUN,");
				}
			}
			if (command.isJuly()) {
				cronExpress.append("JUL,");
				if (cronExpress2 != null) {
					cronExpress2.append("JUL,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("JUL,");
				}
			}
			if (command.isAugust()) {
				cronExpress.append("AUG,");
				if (cronExpress2 != null) {
					cronExpress2.append("AUG,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("AUG,");
				}
			}
			if (command.isSeptember()) {
				cronExpress.append("SEP,");
				if (cronExpress2 != null) {
					cronExpress2.append("SEP,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("SEP,");
				}
			}
			if (command.isOctober()) {
				cronExpress.append("OCT,");
				if (cronExpress2 != null) {
					cronExpress2.append("OCT,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("OCT,");
				}
			}
			if (command.isNovember()) {
				cronExpress.append("NOV,");
				if (cronExpress2 != null) {
					cronExpress2.append("NOV,");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("NOV,");
				}
			}
			if (command.isDecember()) {
				cronExpress.append("DEC");
				if (cronExpress2 != null) {
					cronExpress2.append("DEC");
				}
				if (cronExpress3 != null) {
					cronExpress3.append("DEC");
				}
			}
			if (cronExpress.toString().endsWith(",")) {
				cronExpress.deleteCharAt(cronExpress.length() - 1);
			}
			cronExpress.append(" ?");
			if (cronExpress2 != null) {
				if (cronExpress2.toString().endsWith(",")) {
					cronExpress2.deleteCharAt(cronExpress2.length() - 1);
				}
				cronExpress2.append(" ?");
			}
			if (cronExpress3 != null) {
				if (cronExpress3.toString().endsWith(",")) {
					cronExpress3.deleteCharAt(cronExpress3.length() - 1);
				}
				cronExpress3.append(" ?");
			}

			break;
		default:
			if (repeatMinute == null) {
				cronExpress.append("0 " + startMinute + " " + startHours + " * * ?");
			} else {
				if (repeatMinute == 60) {
					if (command.getEndTimeCls() == 1) {
						if (command.getEndTime() % 60 > startMinute) {
							cronExpress.append("0 " + startMinute + " " + startHours + "-" + command.getEndTime() / 60
									+ "/1 * * ?");
						} else {
							cronExpress.append("0 " + startMinute + " " + startHours + "-"
									+ (command.getEndTime() / 60 - 1) + "/1 * * ?");
						}
					} else {
						cronExpress.append("0 " + startMinute + " " + startHours + "/1 * * ?");
					}
				} else {
					if (command.getEndTimeCls() == 1) {

						if (endHour - startHours == 0) {
							cronExpress = this.getCron(startMinute, endMinute, repeatMinute, startHours);
							cronExpress.append(" * * ? ");
						} else if (endHour - startHours == 1) {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
							cronExpress.append(" * * ? ");
							cronExpress2.append(" * * ? ");

						} else {
							cronExpress = this.getCron(startMinute, 60, repeatMinute, startHours);
							cronExpress2 = this.getCronRangeMoreTwo(startTimeRun, 60, repeatMinute, startHours,
									endHour);
							cronExpress3 = this.getCron(startTimeRun, endMinute, repeatMinute, endHour);
							cronExpress.append(" * * ? ");
							cronExpress2.append(" * * ? ");
							cronExpress3.append(" * * ? ");
						}
					} else {
						cronExpress.append("0 0/" + repeatMinute + " * * * ? ");
					}
				}
			}
			break;
		}
		lstCron.put(CronType.START, cronExpress.toString());
		if (cronExpress2 != null) {
			if (cronExpress3 != null) {
				lstCron.put(CronType.REPEAT, cronExpress2.toString());
			} else {
				lstCron.put(CronType.END, cronExpress2.toString());
			}
		}
		if (cronExpress3 != null) {
			lstCron.put(CronType.END, cronExpress3.toString());
		}

		return lstCron.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, item -> new CronSchedule(Arrays.asList(item.getValue()))));
	}

	private StringBuilder getCron(int startTemp, int endTemp, int loopTemp, int hourTemp) {
		StringBuilder cronExpress = new StringBuilder();
		cronExpress.append("0 ");
		int startMinuteTemp = startTemp;
		while (startMinuteTemp <= endTemp) {
			if (startMinuteTemp != 60) {
				cronExpress.append(startMinuteTemp);
				cronExpress.append(",");
			}
			startMinuteTemp = startMinuteTemp + loopTemp;
		}
		if (cronExpress.toString().endsWith(",")) {
			cronExpress.deleteCharAt(cronExpress.length() - 1);
		}
		cronExpress.append(" " + hourTemp + " ");
		return cronExpress;
	}

	private StringBuilder getCronRangeMoreTwo(int startTemp, int endTemp, int loopTemp, int startHourTemp,
			int endHourTemp) {
		StringBuilder cronExpress = new StringBuilder();
		cronExpress.append("0 ");
		int startMinuteTemp = startTemp;
		while (startMinuteTemp <= endTemp) {
			if (startMinuteTemp != 60) {
				cronExpress.append(startMinuteTemp);
				cronExpress.append(",");
			}
			startMinuteTemp = startMinuteTemp + loopTemp;
		}
		if (cronExpress.toString().endsWith(",")) {
			cronExpress.deleteCharAt(cronExpress.length() - 1);
		}
		if (endHourTemp - startHourTemp == 2) {
			cronExpress.append(" " + (endHourTemp - 1) + " ");
		} else {
			cronExpress.append(" " + (startHourTemp + 1) + "-" + (endHourTemp - 1) + " ");
		}
		return cronExpress;
	}

}
