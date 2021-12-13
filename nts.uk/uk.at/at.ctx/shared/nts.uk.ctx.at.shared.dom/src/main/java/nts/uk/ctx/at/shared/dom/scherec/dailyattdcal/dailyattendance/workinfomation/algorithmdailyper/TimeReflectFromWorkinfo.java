package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.adapter.workschedule.WorkScheWorkInforSharedAdapter;
import nts.uk.ctx.at.shared.dom.adapter.workschedule.WorkScheduleWorkSharedImport;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.ScheduleTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingCondition;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemService;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionRepository;
import nts.uk.ctx.at.shared.dom.workingcondition.service.WorkingConditionService;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrorMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.ctx.at.shared.dom.worktime.common.AbolishAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.GoLeavingWorkAtr;
import nts.uk.ctx.at.shared.dom.worktime.common.StampReflectTimezone;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.TimezoneUse;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.i18n.TextResource;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 勤務情報から打刻反映時間帯を取得する
 * @author tutk
 *
 */
@Stateless
public class TimeReflectFromWorkinfo {
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	
	@Inject
	private WorkingConditionRepository workingConditionRepo;
	
	@Inject
	private BasicScheduleService basicScheduleService;
	
	@Inject
	private WorkTimeSettingService workTimeSettingService;
	
	@Inject
	private WorkScheWorkInforSharedAdapter workScheWorkInforSharedAdapter;
	
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeRepo;

	public OutputTimeReflectForWorkinfo get(String companyId, String employeeId, GeneralDate ymd,
			WorkInfoOfDailyAttendance workInformation) {
		OutputTimeReflectForWorkinfo outputTimeReflectForWorkinfo = new OutputTimeReflectForWorkinfo();

		// ドメインモデル「勤務種類」を取得する get 1 or 0
		List<WorkType> optWorkTypeData = workTypeRepo.findAllByListCode(companyId,
				Arrays.asList(workInformation.getRecordInfo().getWorkTypeCode().v()));
		if(optWorkTypeData.isEmpty()) {
			outputTimeReflectForWorkinfo.setEndStatus(EndStatus.NO_WORK_TYPE);
			return outputTimeReflectForWorkinfo;
		}
		//日別実績の勤務情報．就業時間帯コードを取得
		WorkTimeCode workTimeCode = workInformation.getRecordInfo().getWorkTimeCode();
		
		//就業時間帯コード = null の場合
		if(workTimeCode == null) {
			// 社員の労働条件を取得する
			Optional<WorkingConditionItem> workingConditionItem = WorkingConditionService
					.findWorkConditionByEmployee(new WorkingConditionService.RequireM1() {
						
						@Override
						public Optional<WorkingConditionItem> workingConditionItem(String historyId) {
							return workingConditionRepo.getWorkingConditionItem(historyId);
						}
						
						@Override
						public Optional<WorkingCondition> workingCondition(String companyId, String employeeId, GeneralDate baseDate) {
							return workingConditionRepo.getBySidAndStandardDate(companyId, employeeId, baseDate);
						}
					}, companyId, employeeId, ymd);
			if(!workingConditionItem.isPresent()) {
				outputTimeReflectForWorkinfo.setEndStatus(EndStatus.NO_WORK_CONDITION);
				return outputTimeReflectForWorkinfo;
			}
			WorkTypeClassification typeOneDay = optWorkTypeData.get(0).getDailyWork().getOneDay();
			WorkTypeClassification typeMorning = optWorkTypeData.get(0).getDailyWork().getMorning();
			WorkTypeClassification typeAfternoon = optWorkTypeData.get(0).getDailyWork().getAfternoon();
			// INPUT．「勤務種類」の勤務種類の分類をチェックする
			// 休日出勤の勤務情報を見る
			// ・勤務種類．１日の勤務．勤務区分 = 1日
			// ⇒勤務種類．１日の勤務．1日 = 休日、振休、休日出勤
			// ・勤務種類．１日の勤務．勤務区分 = 午前と午後
			// ⇒勤務種類．１日の勤務．午前 = 休日、振休、休日出勤 OR 勤務種類．１日の勤務．午後 = 休日、振休、休日出勤
			if (typeOneDay.checkHolidayNew() && (typeMorning.checkHolidayNew() || typeAfternoon.checkHolidayNew())) {
				// 出勤時の就業時間帯コードを取得する
				//Tin bảo méo thể null dc
				workTimeCode = workingConditionItem.get().getWorkCategory().getWorkTime().getWeekdayTime().getWorkTimeCode().get();
			}else {
				//休日出勤時の就業時間帯コードを取得する
				//Tin bảo méo thể null dc
				workTimeCode = workingConditionItem.get().getWorkCategory().getWorkTime().getHolidayWork().getWorkTimeCode().get();
			}
		}
		// ドメインモデル「就業時間帯の設定」を取得する
		Optional<WorkTimeSetting> workTimeOpt = this.workTimeSettingRepository.findByCodeAndAbolishCondition(companyId,
				workTimeCode.v(), AbolishAtr.NOT_ABOLISH);
		if(!workTimeOpt.isPresent()) {
			outputTimeReflectForWorkinfo.setEndStatus(EndStatus.NO_WORK_TIME);
			return outputTimeReflectForWorkinfo;
		}
		
		outputTimeReflectForWorkinfo.setEndStatus(EndStatus.NORMAL);
		
		//打刻反映時間帯を取得する (Lấy dữ liệu)
		OutputGetTimeStamReflect outputGetTimeStamReflect = this.getTimeStamReflect(
				companyId, employeeId, ymd, workInformation);
		outputTimeReflectForWorkinfo.setStampReflectRangeOutput(outputGetTimeStamReflect.getStampReflectRangeOutput());
		outputTimeReflectForWorkinfo.setError(outputGetTimeStamReflect.getError());
		
		return outputTimeReflectForWorkinfo;

	}
	
	/**
	 * 打刻反映時間帯を取得する
	 * @param companyID
	 * @param employeeID
	 * @param processingDate
	 * @param workInfoOfDaily
	 * @return
	 */
	public OutputGetTimeStamReflect getTimeStamReflect(String companyID,String employeeID, GeneralDate processingDate,
			WorkInfoOfDailyAttendance workInfoOfDaily) {
		List<ErrorMessageInfo> errMesInfos = new ArrayList<>();
		StampReflectRangeOutput stampReflectRangeOutput = new StampReflectRangeOutput();
		
		WorkTypeCode workTypeCode = workInfoOfDaily.getRecordInfo().getWorkTypeCode();
		
		WorkTimeCode workTimeCode = workInfoOfDaily.getRecordInfo().getWorkTimeCode();

		// 打刻反映時間帯を取得する - start
		// 打刻反映時の出勤休日扱いチェック
		CheckAttendanceHolidayOutPut attendanceHolidayOutPut = this.checkAttendanceHoliday(employeeID, companyID, workTypeCode.v(), processingDate);
		if (attendanceHolidayOutPut.getErrMesInfos() != null && !attendanceHolidayOutPut.getErrMesInfos().isEmpty()) {
			errMesInfos.addAll(attendanceHolidayOutPut.getErrMesInfos().stream()
					.map(c -> new ErrorMessageInfo(companyID, c.getEmployeeID(), c.getDisposalDay(),
							c.getExecutionContent(), c.getResourceID(), c.getMessageError()))
					.collect(Collectors.toList()));
		}
		boolean isAtWork = attendanceHolidayOutPut.isAtWork();

		// 終了状態：休日扱い - る
		if (isAtWork == false) {
			stampReflectRangeOutput = this.holidayStampRange(companyID, workInfoOfDaily, processingDate,
					employeeID);
		}
		// 終了状態：出勤扱い - 出勤系の打刻範囲を取得する
		else {
			stampReflectRangeOutput = this.attendanSytemStampRange(workTimeCode, companyID, workInfoOfDaily);
		}

		// 打刻取得範囲の作成
		createStampReflectRangeInaDay(stampReflectRangeOutput);

		// 外出の打刻反映範囲を取得する
		stampReflectRangeOutput.setGoOut(stampReflectRangeOutput.getStampRange());
		// 臨時の打刻反映範囲を作成する
		stampReflectRangeOutput.setTemporary(stampReflectRangeOutput.getStampRange());
		// - end
		return new OutputGetTimeStamReflect(stampReflectRangeOutput,errMesInfos);
		
	}
	
	/**
	 * 打刻反映時の出勤休日扱いチェック
	 * 
	 * @param workTypeCode
	 * @return
	 */
	private CheckAttendanceHolidayOutPut checkAttendanceHoliday(String employeeID,
			String companyID, String workTypeCode, GeneralDate processingDate) {

		CheckAttendanceHolidayOutPut outPut = new CheckAttendanceHolidayOutPut();

		List<ErrMessageInfo> errMesInfos = new ArrayList<>();

		// ドメインモデル「勤務種類」を取得する
		Optional<WorkType> workTypeOpt = this.workTypeRepo.findByPK(companyID, workTypeCode);

		if (!workTypeOpt.isPresent()) {
			ErrMessageInfo employmentErrMes = new ErrMessageInfo(employeeID, "",
					new ErrMessageResource("015"), EnumAdaptor.valueOf(0, ExecutionContent.class), processingDate,
					new ErrMessageContent(TextResource.localize("Msg_590")));
			errMesInfos.add(employmentErrMes);
			outPut.setErrMesInfos(errMesInfos);
			return outPut;
		}

		// 1日半日出勤・1日休日系の判定
		WorkStyle workStyle = basicScheduleService.checkWorkDay(companyID, workTypeCode);

		if (workStyle == WorkStyle.ONE_DAY_REST) {
			outPut.setAtWork(false);
		} else {
			outPut.setAtWork(true);
		}

		return outPut;
	}
	
	@Inject
	private WorkingConditionItemService WorkingConditionItemService;
	
	/*
	 * 休日系の打刻範囲を取得する
	 */
	private StampReflectRangeOutput holidayStampRange(String companyID,
			WorkInfoOfDailyAttendance workInfoOfDaily, GeneralDate processingDate, String employeeId) {

		StampReflectOnHolidayOutPut stampReflectOnHolidayOutPut = new StampReflectOnHolidayOutPut();

		// get workTimeCode
		WorkTimeCode workTimeCode = workInfoOfDaily.getRecordInfo().getWorkTimeCode();

		// 当日の打刻反映範囲を取得 - 当日の就業時間帯コードを取得
		// 休日出勤時の勤務情報を取得する - new wave
		if (workTimeCode == null) {
			//休日出勤時の就業時間帯コードを取得する
			workTimeCode = new WorkTimeCode( WorkingConditionItemService.getWorkTimeWorkHoliday(employeeId, processingDate));
		}

		// 当日の打刻反映範囲を取得 - end get data of this day
		StampReflectRangeOutput stampReflectRangeOutput = this.attendanSytemStampRange(workTimeCode, companyID,
				workInfoOfDaily);
		// set data of this day
		stampReflectOnHolidayOutPut.setStampReflectThisDay(stampReflectRangeOutput);

		// 前々日の打刻反映範囲を取得 - get data of two day before
		GeneralDate twoDayBeforeDate = processingDate.addDays(-2);
		StampReflectRangeOutput stampTwoDay = this.calculationStamp(workInfoOfDaily, twoDayBeforeDate,
				employeeId, companyID, 1);
		// set data of two day before
		stampReflectOnHolidayOutPut.setStampReflectTwoDayBefore(stampTwoDay);

		// 前日の打刻反映範囲を取得 - get data of previous day
		GeneralDate previousDay = processingDate.addDays(-1);
		StampReflectRangeOutput stampPreviousDay = this.calculationStamp(workInfoOfDaily, previousDay,
				employeeId, companyID, 2);
		stampReflectOnHolidayOutPut.setStampReflectPreviousDay(stampPreviousDay);

		// 翌日の打刻反映範囲を取得 - get data of next day
		GeneralDate nextDay = processingDate.addDays(1);
		StampReflectRangeOutput stampNextDay = this.calculationStamp(workInfoOfDaily, nextDay, employeeId,
				companyID, 3);
		stampReflectOnHolidayOutPut.setStampReflectNextDay(stampNextDay);

		if (stampTwoDay != null) {
			// 前々日との関係から打刻反映範囲を補正
			this.stampReflectCorrection(stampReflectOnHolidayOutPut,1);
		}

		if (stampPreviousDay != null) {
			// 前日との関係から打刻範囲範囲を補正
			this.stampReflectCorrection(stampReflectOnHolidayOutPut,2);
		}

		if (stampNextDay != null) {
			// 翌日との関係から打刻反映範囲を補正
			this.nextDayCorrection(stampReflectOnHolidayOutPut);
		}

		return stampReflectOnHolidayOutPut.getStampReflectThisDay();
	}
	
	private StampReflectRangeOutput attendanSytemStampRange(WorkTimeCode workTimeCode, String companyID,
			WorkInfoOfDailyAttendance workInfoOfDaily) {

		StampReflectRangeOutput stampReflectRangeOutput = new StampReflectRangeOutput();
		if(workTimeCode == null) {
			return stampReflectRangeOutput;
		}
		 // ドメインモデル「就業時間帯の設定」を取得
		Optional<WorkTimeSetting> workTimeSetting = workTimeSettingRepository.findByCode(companyID, workTimeCode.v());
		
		if (workTimeSetting.isPresent()) {
			
			List<ScheduleTimeSheet> scheduleTimeSheets = new ArrayList<>();
			Integer start1 = null;
			Integer start2= null;
			Integer end1= null;
			Integer end2= null;
			if(workInfoOfDaily !=null) {
				scheduleTimeSheets = workInfoOfDaily.getScheduleTimeSheets();
				start1 = (!scheduleTimeSheets.isEmpty() && scheduleTimeSheets.get(0) != null)
						? scheduleTimeSheets.get(0).getAttendance().valueAsMinutes()
						: null;
				end1 = (!scheduleTimeSheets.isEmpty() && scheduleTimeSheets.get(0) != null)
						? scheduleTimeSheets.get(0).getLeaveWork().valueAsMinutes()
						: null;
				start2 = (scheduleTimeSheets.size() > 1 && scheduleTimeSheets.get(1) != null)
						? scheduleTimeSheets.get(1).getAttendance().valueAsMinutes()
						: null;
				end2 = (scheduleTimeSheets.size() > 1 && scheduleTimeSheets.get(1) != null)
						? scheduleTimeSheets.get(1).getLeaveWork().valueAsMinutes()
						: null;
						
			}else {
				//所定時間設定を取得
				PredetemineTimeSetting predTime = this.predetemineTimeRepo.findByWorkTimeCode(companyID, workTimeCode.v()).get();
				//所定時間帯をパラメータへセット
				for(TimezoneUse timezoneUse : predTime.getPrescribedTimezoneSetting().getLstTimezone()) {
					if(timezoneUse.getWorkNo() == 1) {
						start1 = timezoneUse.getStart().v();
						end1 = timezoneUse.getEnd().v();
					}else if(timezoneUse.getWorkNo() == 2) {
						start2 = timezoneUse.getStart().v();
						end2 = timezoneUse.getEnd().v();
					}
					
				}
			}
			
			// 打刻反映時間帯を取得する
			List<StampReflectTimezone> stampReflectTimezones = this.workTimeSettingService.getStampReflectTimezone(
					companyID, workTimeCode.v(),
					start1,
					end1,
					start2,
					end2);

			if (!stampReflectTimezones.isEmpty()) {
				List<StampReflectTimezoneOutput> stampReflectRangeOutputs = new ArrayList<>();
				stampReflectTimezones.stream().forEach(timezone -> {
					StampReflectTimezoneOutput stampReflectTimezoneOutput = new StampReflectTimezoneOutput(
							timezone.getWorkNo(), timezone.getClassification(), timezone.getEndTime(),
							timezone.getStartTime());
					stampReflectRangeOutputs.add(stampReflectTimezoneOutput);
				});
				stampReflectRangeOutput.setLstStampReflectTimezone(stampReflectRangeOutputs);
			}
		}
		return stampReflectRangeOutput;
	}
	
	/*
	 * function common for calculation for two day before, previous day, next
	 * day
	 */
	private StampReflectRangeOutput calculationStamp(WorkInfoOfDailyAttendance workInfoOfDaily,
			GeneralDate processingDate, String employeeId, String companyID, int dayAttr) {

		/**
		 * dayAttr 1 : two day before , 2 : previous day 3 : next day
		 */
		Optional<WorkScheduleWorkSharedImport> scheduleWorkInfor = workScheWorkInforSharedAdapter.get(employeeId, processingDate);
		if(!scheduleWorkInfor.isPresent()) {
			return null;
		}
		// 1日半日出勤・1日休日系の判定
		// 打刻反映時の出勤休日扱いチェック
		WorkStyle workStyle = basicScheduleService
				.checkWorkDay(companyID, scheduleWorkInfor.get().getWorkType());
		StampReflectRangeOutput stampReflectRangeOutput = null;

		if (workStyle != WorkStyle.ONE_DAY_REST) {
			Optional<WorkInfoOfDailyAttendance> workInfoOfDailyNew = this.workScheWorkInforSharedAdapter
					.getWorkInfoOfDailyAttendance(employeeId, processingDate);
			// get workTimeCode of processingDate
			String worktimeCode = this.stampRangeCheckWorkRecord(processingDate, employeeId);
			if (worktimeCode != null) {
				// 1日分の打刻反映範囲を取得
				stampReflectRangeOutput = this.attendanSytemStampRange(new WorkTimeCode(worktimeCode), companyID,
						workInfoOfDailyNew.isPresent()?workInfoOfDailyNew.get():null);

				if (stampReflectRangeOutput != null
						&& !stampReflectRangeOutput.getLstStampReflectTimezone().isEmpty()) {
					// loop list 出退勤
					stampReflectRangeOutput.getLstStampReflectTimezone().stream().forEach(timezone -> {
						if (dayAttr == 1) {
							// 開始時刻をパラメータ「ズラす時間」だけズラす
							TimeWithDayAttr startTimeTwoDay = new TimeWithDayAttr(timezone.getStartTime().v() - 2880);
							if (startTimeTwoDay.v() < (-720)) {
								startTimeTwoDay = new TimeWithDayAttr(-720);
							}
							// 終了時刻をパラメータ「ズラす時間」だけズラす
							TimeWithDayAttr endTimeTwoDay = new TimeWithDayAttr(timezone.getEndTime().v() - 2880);
							if (endTimeTwoDay.v() < (-720)) {
								endTimeTwoDay = new TimeWithDayAttr(-720);
							}
							timezone.setStartTime(startTimeTwoDay);
							timezone.setEndTime(endTimeTwoDay);

						} else if (dayAttr == 2) {
							TimeWithDayAttr startTimePreviousDay = new TimeWithDayAttr(
									timezone.getStartTime().v() - 1440);
							if (startTimePreviousDay.v() < (-720)) {
								startTimePreviousDay = new TimeWithDayAttr(-720);
							}
							TimeWithDayAttr endTimePreviousDay = new TimeWithDayAttr(timezone.getEndTime().v() - 1440);
							if (endTimePreviousDay.v() < (-720)) {
								endTimePreviousDay = new TimeWithDayAttr(-720);
							}
							timezone.setStartTime(startTimePreviousDay);
							timezone.setEndTime(endTimePreviousDay);
						} else if (dayAttr == 3) {
							TimeWithDayAttr startTimeNextDay = new TimeWithDayAttr(timezone.getStartTime().v() + 1440);
							if (startTimeNextDay.v() > 4319) {
								startTimeNextDay = new TimeWithDayAttr(4319);
							}
							TimeWithDayAttr endTimeNextDay = new TimeWithDayAttr(timezone.getEndTime().v() + 1440);
							if (endTimeNextDay.v() > 4319) {
								endTimeNextDay = new TimeWithDayAttr(4319);
							}
							timezone.setStartTime(startTimeNextDay);
							timezone.setEndTime(endTimeNextDay);
						}
					});
				}

			}

		}

		return stampReflectRangeOutput;
	}
	
	/*
	 * 打刻範囲チェック用勤務実績取得
	 */
	public String stampRangeCheckWorkRecord(GeneralDate processingDate, String employeeId) {
		// ドメインモデル「日別実績の勤務情報」を取得
		Optional<WorkInfoOfDailyAttendance> workInfoOfDaily = this.workScheWorkInforSharedAdapter
				.getWorkInfoOfDailyAttendance(employeeId, processingDate);

		if (workInfoOfDaily.isPresent()) {
			if (!(workInfoOfDaily.get().getRecordInfo().getWorkTimeCode() == null 
					&& !workInfoOfDaily.get().getRecordInfo().getWorkTimeCodeNotNull().isPresent())) {
				return workInfoOfDaily.get().getRecordInfo().getWorkTimeCode().v();
			}
		}
		// Imported(就業.勤務実績)「勤務予定基本情報」を取得する
		Optional<WorkScheduleWorkSharedImport> scheduleWorkInfor = workScheWorkInforSharedAdapter.get(employeeId, processingDate);
		if (scheduleWorkInfor.isPresent()) {
			return scheduleWorkInfor.get().getWorkType();
		} 
		return null;
	}
	
	/*
	 * Stamp Reflect Correction
	 */
	private void stampReflectCorrection(StampReflectOnHolidayOutPut stampReflectOnHolidayOutPut,int dayAttr ) {

		/**
		 * dayAttr 1 : two day before , 2 : previous day 3 : next day
		 */
		StampReflectRangeOutput stampReflectRangeOutput = dayAttr == 1?stampReflectOnHolidayOutPut.getStampReflectTwoDayBefore():stampReflectOnHolidayOutPut.getStampReflectPreviousDay();
		if (!stampReflectRangeOutput.getLstStampReflectTimezone().isEmpty()) {
			// 前々日の打刻反映範囲から一番遅い時刻を取得
			TimeWithDayAttr lastestTimeGoWork = getLastestTimeFromListStampReflect(GoLeavingWorkAtr.GO_WORK,
					stampReflectRangeOutput);
			// 出勤の打刻反映範囲を補正
			deleteBeforeDesignatedTimeFromStampReflect(GoLeavingWorkAtr.GO_WORK, lastestTimeGoWork,
					stampReflectOnHolidayOutPut.getStampReflectThisDay());
			// 前日の打刻反映範囲から一番遅い時刻を取得
			TimeWithDayAttr lastestTimeLeavingWork = getLastestTimeFromListStampReflect(GoLeavingWorkAtr.LEAVING_WORK,
					stampReflectRangeOutput);
			// 退勤の打刻反映範囲を補正
			deleteBeforeDesignatedTimeFromStampReflect(GoLeavingWorkAtr.LEAVING_WORK, lastestTimeLeavingWork,
					stampReflectOnHolidayOutPut.getStampReflectThisDay());
		}
	};
	
	/*
	 * 打刻反映範囲のListから一番遅い時刻を取得
	 */
	public TimeWithDayAttr getLastestTimeFromListStampReflect(GoLeavingWorkAtr goLeavingWorkAtr,
			StampReflectRangeOutput stampReflectRangeOutput) {
		List<StampReflectTimezoneOutput> stampReflectTimezoneList = new ArrayList<>();
		if (goLeavingWorkAtr == null) {
			stampReflectTimezoneList = stampReflectRangeOutput.getLstStampReflectTimezone();
		} else {
			stampReflectTimezoneList = stampReflectRangeOutput.getLstStampReflectTimezone().stream()
					.filter(item -> item.getClassification() == goLeavingWorkAtr).collect(Collectors.toList());
		}

		TimeWithDayAttr lastestTime = null;
		for (StampReflectTimezoneOutput stampReflect : stampReflectTimezoneList) {
			if ((lastestTime == null) || stampReflect.getEndTime().greaterThan(lastestTime)) {
				lastestTime = new TimeWithDayAttr(stampReflect.getEndTime().v());
			}
		}
		;

		return lastestTime;
	}
	
	/*
	 * 打刻反映範囲から指定時刻以前を削除
	 */
	public void deleteBeforeDesignatedTimeFromStampReflect(GoLeavingWorkAtr goLeavingWorkAtr,
			TimeWithDayAttr lastestTime, StampReflectRangeOutput stampReflectRangeOutput) {

		List<StampReflectTimezoneOutput> list = stampReflectRangeOutput.getLstStampReflectTimezone().stream()
				.filter(item -> item.getClassification() == goLeavingWorkAtr).collect(Collectors.toList());

		for (StampReflectTimezoneOutput timeZone : list) {
			if (timeZone.getStartTime().lessThan(lastestTime) && timeZone.getEndTime().greaterThan(lastestTime)) {
				timeZone.setStartTime(new TimeWithDayAttr(lastestTime.v() + 1));
			} else if (timeZone.getEndTime().lessThan(lastestTime)) {
				stampReflectRangeOutput = new StampReflectRangeOutput();
			}
		}
	}
	
	/*
	 * next day correction
	 */
	private void nextDayCorrection(StampReflectOnHolidayOutPut stampReflectOnHolidayOutPut) {
		StampReflectRangeOutput stampReflectRangeOutput = stampReflectOnHolidayOutPut.getStampReflectNextDay();
		if (!stampReflectRangeOutput.getLstStampReflectTimezone().isEmpty()) {
			// 翌日の打刻反映範囲から一番早い時刻を取得
			TimeWithDayAttr earliestTimeGoWork = getEarliestTimeFromStampReflect(GoLeavingWorkAtr.GO_WORK,
					stampReflectRangeOutput);

			// 出勤の打刻反映範囲を補正
			deleteAfterDesignatedTimeFromStampReflect(GoLeavingWorkAtr.GO_WORK, earliestTimeGoWork,
					stampReflectOnHolidayOutPut.getStampReflectThisDay());

			// 翌日の打刻反映範囲から一番早い時刻を取得
			TimeWithDayAttr earliestLeavingWork = getEarliestTimeFromStampReflect(GoLeavingWorkAtr.LEAVING_WORK,
					stampReflectRangeOutput);

			// 退勤の打刻反映範囲を補正
			deleteAfterDesignatedTimeFromStampReflect(GoLeavingWorkAtr.LEAVING_WORK, earliestLeavingWork,
					stampReflectOnHolidayOutPut.getStampReflectThisDay());
		}
	}
	
	/*
	 * 打刻反映範囲のListから一番早い時刻を取得
	 */
	public TimeWithDayAttr getEarliestTimeFromStampReflect(GoLeavingWorkAtr goLeavingWorkAtr,
			StampReflectRangeOutput stampReflectRangeOutput) {

		List<StampReflectTimezoneOutput> stampReflectTimezoneList = new ArrayList<>();

		if (goLeavingWorkAtr == null) {
			stampReflectTimezoneList = stampReflectRangeOutput.getLstStampReflectTimezone();
		} else {
			stampReflectTimezoneList = stampReflectRangeOutput.getLstStampReflectTimezone().stream()
					.filter(item -> item.getClassification() == goLeavingWorkAtr).collect(Collectors.toList());
		}

		TimeWithDayAttr earliestTime = null;
		for (StampReflectTimezoneOutput stampReflect : stampReflectTimezoneList) {
			if ((earliestTime == null) || stampReflect.getStartTime().lessThan(earliestTime)) {
				earliestTime = new TimeWithDayAttr(stampReflect.getStartTime().v());
			}
		}
		;

		return earliestTime;
	}
	
	/*
	 * 打刻反映範囲から指定時刻以降を削除
	 */
	public void deleteAfterDesignatedTimeFromStampReflect(GoLeavingWorkAtr goLeavingWorkAtr,
			TimeWithDayAttr earliestTime, StampReflectRangeOutput stampReflectRangeOutput) {
		List<StampReflectTimezoneOutput> list = stampReflectRangeOutput.getLstStampReflectTimezone().stream()
				.filter(item -> item.getClassification() == goLeavingWorkAtr).collect(Collectors.toList());

		for (StampReflectTimezoneOutput timeZone : list) {
			if (timeZone.getStartTime().lessThan(earliestTime) && timeZone.getEndTime().greaterThan(earliestTime)) {
				timeZone.setEndTime(new TimeWithDayAttr(earliestTime.v() - 1));
			} else if (timeZone.getStartTime().greaterThan(earliestTime)) {
				stampReflectRangeOutput = new StampReflectRangeOutput();
			}
		}
	}
	
	/*
	 * 1日の打刻反映範囲を作成
	 */
	private void createStampReflectRangeInaDay(StampReflectRangeOutput stampReflectRangeOutput) {

		// 一番早い時刻を取得
		TimeWithDayAttr earliestTime = getEarliestTimeFromStampReflect(null, stampReflectRangeOutput);

		// 一番遅い時刻を取得
		TimeWithDayAttr lastestTime = getLastestTimeFromListStampReflect(null, stampReflectRangeOutput);

		TimeZoneOutput temporary = new TimeZoneOutput(earliestTime, lastestTime);

		stampReflectRangeOutput.setStampRange(temporary);
	}
	
}
