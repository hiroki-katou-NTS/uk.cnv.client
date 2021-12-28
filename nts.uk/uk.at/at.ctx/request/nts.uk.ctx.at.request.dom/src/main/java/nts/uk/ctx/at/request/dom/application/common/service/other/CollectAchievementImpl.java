package nts.uk.ctx.at.request.dom.application.common.service.other;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.dom.application.*;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeAppAtr;
import org.apache.logging.log4j.util.Strings;

import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.RecordWorkInfoAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.RecordWorkInfoImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedulemanagementcontrol.ScheduleManagementControlAdapter;
import nts.uk.ctx.at.request.dom.application.common.ovetimeholiday.OvertimeLeaveTime;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementEarly;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.StampRecordOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.TimeContentOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.TimePlaceOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.TrackRecordAtr;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.application.stamp.StampFrameNo;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakgoout.BreakFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.breakouting.breaking.BreakTimeSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.shortworktime.ShortWorkingTimeSheet;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 *
 * @author Doan Duy Hung
 *
 */
@Stateless
public class CollectAchievementImpl implements CollectAchievement {

	@Inject
	private RecordWorkInfoAdapter recordWorkInfoAdapter;

	@Inject
	private ScheduleManagementControlAdapter scheduleManagementControlAdapter;

	@Inject
	private ScBasicScheduleAdapter scBasicScheduleAdapter;

	@Inject
	private WorkTypeRepository workTypeRepository;

	@Inject
	private WorkTimeSettingRepository WorkTimeRepository;
	
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
	private AppOverTimeRepository appOverTimeRepository;
	
	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;
	
	public List<TimePlaceOutput> createTimePlace(int type) {
		return Collections.emptyList();
	}
	
	public StampRecordOutput createStampRecord() {
		
		/**
		 * 介護時間帯
		 */
		List<TimePlaceOutput> nursingTime = this.createTimePlace(1);
		
		/**
		 * 休憩時間帯
		 */
		List<TimePlaceOutput> breakTime = this.createTimePlace(2);
		
		/**
		 * 勤務時間帯
		 */
		List<TimePlaceOutput> workingTime = this.createTimePlace(3);
		
		/**
		 * 外出時間帯
		 */
		List<TimePlaceOutput> outingTime = this.createTimePlace(4);
		
		/**
		 * 応援時間帯
		 */
		List<TimePlaceOutput> supportTime;
		
		/**
		 * 育児時間帯
		 */
		List<TimePlaceOutput> parentingTime = this.createTimePlace(6);
		
		/**
		 * 臨時時間帯
		 */
		List<TimePlaceOutput> extraordinaryTime = this.createTimePlace(7);
		
		return new StampRecordOutput(
				nursingTime,
				breakTime,
				workingTime,
				outingTime,
				null,
				parentingTime,
				extraordinaryTime);
	}
	@Override
	public ActualContentDisplay getAchievement(String companyID, String applicantID, GeneralDate appDate) {
		//Imported(申請承認)「勤務実績」を取得する - (lấy thông tin Imported(appAproval)「DailyPerformance」) - RQ5
		RecordWorkInfoImport recordWorkInfoImport = recordWorkInfoAdapter.getRecordWorkInfoRefactor(applicantID, appDate);
		String workTypeCD = Strings.EMPTY;
		String workTimeCD = Strings.EMPTY;
		List<BreakTimeSheet> breakTimeSheets = Collections.emptyList();
		TimeContentOutput timeContentOutput = null;
		TrackRecordAtr trackRecordAtr = null;
		StampRecordOutput stampRecordOutput = this.createStampRecord();
		List<ShortWorkingTimeSheet> shortWorkTimeLst = Collections.emptyList();
		AchievementEarly achievementEarly = null;
		Optional<Integer> opDepartureTime2 = Optional.empty();
		Optional<String> opWorkTypeName = Optional.empty();
		Optional<String> opWorkTimeName = Optional.empty();
		Optional<Integer> opWorkTime = Optional.empty();
		Optional<Integer> opLeaveTime = Optional.empty();
		Optional<Integer> opAchievementStatus = Optional.empty();
		Optional<Integer> opWorkTime2 = Optional.empty();
		Optional<AttendanceTime> opOvertimeMidnightTime = Optional.empty();
		Optional<AttendanceTime> opInlawHolidayMidnightTime = Optional.empty();
		Optional<AttendanceTime> opOutlawHolidayMidnightTime = Optional.empty();
		Optional<AttendanceTime> opPublicHolidayMidnightTime = Optional.empty();
		Optional<List<OvertimeLeaveTime>> opOvertimeLeaveTimeLst = Optional.empty();
		if(recordWorkInfoImport.getWorkTypeCode()==null && recordWorkInfoImport.getWorkTimeCode()==null){
			//取得件数＝0件 ( số data lấy được  = 0)
			//スケジュールを管理するかチェックする(check có quản lý schedule hay không) - RQ536
			if(!scheduleManagementControlAdapter.isScheduleManagementAtr(applicantID, appDate)){//管理しない
				return new ActualContentDisplay(appDate, Optional.empty());
			}
			//管理する
			//Imported(申請承認)「勤務予定」を取得する(lấy thông tin imported(申請承認)「勤務予定」)
			ScBasicScheduleImport scBasicScheduleImport = scBasicScheduleAdapter.findByIDRefactor(applicantID, appDate);
			if (scBasicScheduleImport == null) {
				//取得件数＝0件 ( số data lấy được  = 0)
				return new ActualContentDisplay(appDate, Optional.empty());
			} else {
				
				if((Strings.isBlank(scBasicScheduleImport.getWorkTypeCode()) && Strings.isBlank(scBasicScheduleImport.getWorkTimeCode().orElse(null)))){
					//取得件数＝0件 ( số data lấy được  = 0)
					return new ActualContentDisplay(appDate, Optional.empty());
				}				
			}
			// 実績スケ区分＝スケジュール (Phân loại thực tế= Schedule)
			trackRecordAtr = TrackRecordAtr.SCHEDULE;
			//・実績詳細．1勤務種類コード＝OUTPUT．勤務予定．勤務種類コード
			workTypeCD = scBasicScheduleImport.getWorkTypeCode();
			//・実績詳細．3就業時間帯コード＝OUTPUT．勤務予定．勤務種類コード
			workTimeCD = scBasicScheduleImport.getWorkTimeCode().orElse(null);
			//・実績詳細．5出勤時刻＝OUTPUT．勤務予定．開始時刻1
			opWorkTime = scBasicScheduleImport.getScheduleStartClock1().flatMap(x -> Optional.of(x.v()));
			//・実績詳細．6退勤時刻＝OUTPUT．勤務予定．終了時刻1
			// check null to remove exception
			opLeaveTime = scBasicScheduleImport.getScheduleEndClock1().flatMap(x -> Optional.of(x.v()));
			//・実績詳細．9出勤時刻2＝OUTPUT．勤務予定．開始時刻2
			opWorkTime2 = scBasicScheduleImport.getScheduleStartClock2().map(x -> x.v());
			//・実績詳細．10退勤時刻2＝OUTPUT．勤務予定．終了時刻2
			opDepartureTime2 = scBasicScheduleImport.getScheduleEndClock2().map(x -> x.v());
			//・実績詳細．遅刻早退実績．予定出勤時刻1＝OUTPUT．勤務予定．開始時刻1
			//・実績詳細．遅刻早退実績．予定退勤時刻1＝OUTPUT．勤務予定．終了時刻1
			//・実績詳細．遅刻早退実績．予定出勤時刻2＝OUTPUT．勤務予定．開始時刻2
			//・実績詳細．遅刻早退実績．予定退勤時刻2＝OUTPUT．勤務予定．終了時刻2
			achievementEarly = new AchievementEarly(
					scBasicScheduleImport.getScheduleStartClock1(),
					scBasicScheduleImport.getScheduleStartClock2(),
					scBasicScheduleImport.getScheduleEndClock1(),
					scBasicScheduleImport.getScheduleEndClock2());
			breakTimeSheets = scBasicScheduleImport.getListBreakTimeSheetExports().stream().map(x -> {
			    return new BreakTimeSheet(
			            new BreakFrameNo(x.getBreakFrameNo()), 
			            new TimeWithDayAttr(x.getStartTime()), 
			            new TimeWithDayAttr(x.getEndTime()));
			}).collect(Collectors.toList());
		} else {//取得件数＝1件(số data lấy được = 1)
			// 実績スケ区分＝日別実績 (Phân loại thực tế = Thực tế hàng ngày )
			trackRecordAtr = TrackRecordAtr.DAILY_RESULTS;
			//・実績詳細．1勤務種類コード＝OUTPUT．勤務実績．勤務種類コード
			workTypeCD = recordWorkInfoImport.getWorkTypeCode() == null ? null
					: recordWorkInfoImport.getWorkTypeCode().v();
			//・実績詳細．3就業時間帯コード＝OUTPUT．勤務実績．勤務種類コード
			workTimeCD = recordWorkInfoImport.getWorkTimeCode() == null ? null
					: recordWorkInfoImport.getWorkTimeCode().v();
			//・実績詳細．5出勤時刻＝OUTPUT．勤務実績．出勤時刻1
			opWorkTime = recordWorkInfoImport.getStartTime1().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//・実績詳細．6退勤時刻＝OUTPUT．勤務実績．退勤時刻1
			opLeaveTime = recordWorkInfoImport.getEndTime1().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//・実績詳細．9出勤時刻2＝OUTPUT．勤務実績．出勤時刻2
			opWorkTime2 = recordWorkInfoImport.getStartTime2().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//・実績詳細．10退勤時刻2＝OUTPUT．勤務実績．退勤時刻2
			opDepartureTime2 = recordWorkInfoImport.getEndTime2().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//・実績詳細．遅刻早退実績．予定出勤時刻1＝OUTPUT．勤務実績．予定出勤時刻1
			//・実績詳細．遅刻早退実績．予定退勤時刻1＝OUTPUT．勤務実績．予定退勤時刻1
			//・実績詳細．遅刻早退実績．予定出勤時刻2＝OUTPUT．勤務実績．予定出勤時刻2
			//・実績詳細．遅刻早退実績．予定退勤時刻2＝OUTPUT．勤務実績．予定退勤時刻2
			achievementEarly = new AchievementEarly(
					recordWorkInfoImport.getScheduledAttendence1() == null ? Optional.empty() : Optional.of(recordWorkInfoImport.getScheduledAttendence1()),
					recordWorkInfoImport.getScheduledAttendence2(),
					recordWorkInfoImport.getScheduledDeparture1() == null ? Optional.empty() : Optional.of(recordWorkInfoImport.getScheduledDeparture1()),
					recordWorkInfoImport.getScheduledDeparture2());
			//・実績詳細．勤怠時間内容．早退時間＝OUTPUT．勤務実績．早退時間
			//・実績詳細．勤怠時間内容．遅刻時間＝OUTPUT．勤務実績．遅刻時間
			//・実績詳細．勤怠時間内容．早退時間2＝OUTPUT．勤務実績．早退時間2
			//・実績詳細．勤怠時間内容．遅刻時間2＝OUTPUT．勤務実績．遅刻時間2
			timeContentOutput = new TimeContentOutput(
					recordWorkInfoImport.getEarlyLeaveTime1(),
					recordWorkInfoImport.getEarlyLeaveTime2(),
					recordWorkInfoImport.getLateTime1(),
					recordWorkInfoImport.getLateTime2());
			//・実績詳細．勤怠時間内容．短時間勤務時間帯＝OUTPUT．勤務実績．短時間勤務時間帯
			shortWorkTimeLst = recordWorkInfoImport.getShortWorkingTimeSheets();
			//・実績詳細．休憩時間帯＝OUTPUT．勤務実績．休憩時間帯
			breakTimeSheets = recordWorkInfoImport.getBreakTimeSheets();
			//・実績詳細．残業深夜時間＝OUTPUT．勤務実績．残業深夜時間
			opOvertimeMidnightTime = recordWorkInfoImport.getOverTimeMidnight().map(x -> x.getCalcTime());
			//・実績詳細．法内休出深夜時間＝OUTPUT．勤務実績．法内休出深夜時間
			opInlawHolidayMidnightTime = recordWorkInfoImport.getMidnightOnHoliday().map(x -> x.getCalcTime());
			//・実績詳細．法外休出深夜時間＝OUTPUT．勤務実績．法外休出深夜時間
			opOutlawHolidayMidnightTime = recordWorkInfoImport.getOutOfMidnight().map(x -> x.getCalcTime());
			//・実績詳細．祝日休出深夜時間＝OUTPUT．勤務実績．祝日休出深夜時間
			opPublicHolidayMidnightTime = recordWorkInfoImport.getMidnightPublicHoliday().map(x -> x.getCalcTime());
			// 打刻実績．育児時間帯．時刻開始/時刻終了
			if(CollectionUtil.isEmpty(recordWorkInfoImport.getChildCareShortWorkingTimeList())) {
				stampRecordOutput.setParentingTime(Collections.emptyList());
			} else {
				stampRecordOutput.setParentingTime(recordWorkInfoImport.getChildCareShortWorkingTimeList().stream()
						.map(x -> new TimePlaceOutput(
								Optional.empty(), 
								Optional.empty(), 
								new StampFrameNo(x.getShortWorkTimeFrameNo().v()), 
								Optional.of(x.getEndTime()), 
								Optional.of(x.getStartTime())))
						.collect(Collectors.toList()));
			}
			// 打刻実績．介護時間帯．時刻開始/時刻終了
			if(CollectionUtil.isEmpty(recordWorkInfoImport.getCareShortWorkingTimeList())) {
				stampRecordOutput.setNursingTime(Collections.emptyList());
			} else {
				stampRecordOutput.setNursingTime(recordWorkInfoImport.getCareShortWorkingTimeList().stream()
						.map(x -> new TimePlaceOutput(
								Optional.empty(), 
								Optional.empty(), 
								new StampFrameNo(x.getShortWorkTimeFrameNo().v()), 
								Optional.of(x.getEndTime()), 
								Optional.of(x.getStartTime())))
						.collect(Collectors.toList()));
			}
			// 打刻実績．勤務時間帯
			
			TimePlaceOutput workTime1 = new TimePlaceOutput(
					Optional.empty(),
					Optional.empty(),
					new StampFrameNo(1),
					recordWorkInfoImport.getEndTime1().flatMap(x -> x.getTimeWithDay()),
					recordWorkInfoImport.getStartTime1().flatMap(x -> x.getTimeWithDay())
					);
			TimePlaceOutput workTime2 = new TimePlaceOutput(
					Optional.empty(),
					Optional.empty(),
					new StampFrameNo(2),
					recordWorkInfoImport.getEndTime2().flatMap(x -> x.getTimeWithDay()),
					recordWorkInfoImport.getStartTime2().flatMap(x -> x.getTimeWithDay())
					);		
			List<TimePlaceOutput> workTimeList = new ArrayList<TimePlaceOutput>();
			workTimeList.add(workTime1);
			workTimeList.add(workTime2);
			
			stampRecordOutput.setWorkingTime(workTimeList);
			// 打刻実績．臨時時間帯
			if(CollectionUtil.isEmpty(recordWorkInfoImport.getTimeLeavingWorks())) {
				stampRecordOutput.setExtraordinaryTime(Collections.emptyList());
			} else {
				stampRecordOutput.setExtraordinaryTime(recordWorkInfoImport.getTimeLeavingWorks().stream()
						.map(x -> new TimePlaceOutput(
								Optional.empty(), 
								Optional.empty(), 
								new StampFrameNo(x.getWorkNo().v()), 
								x.getLeaveStamp().flatMap(c -> c.getStamp()).flatMap(c -> c.getTimeDay().getTimeWithDay()), 
								x.getAttendanceStamp().flatMap(c -> c.getStamp()).flatMap(c -> c.getTimeDay().getTimeWithDay())))
						.collect(Collectors.toList()));
			}
			// 打刻実績．外出時間帯
			if(CollectionUtil.isEmpty(recordWorkInfoImport.getOutHoursLst())) {
				stampRecordOutput.setOutingTime(Collections.emptyList());
			} else {
				stampRecordOutput.setOutingTime(recordWorkInfoImport.getOutHoursLst().stream()
						.map(x -> new TimePlaceOutput(
								Optional.empty(), 
								Optional.of(x.getReasonForGoOut()), 
								new StampFrameNo(x.getOutingFrameNo().v()), 
								x.getComeBack().flatMap(c -> c.getTimeDay().getTimeWithDay()), 
								x.getGoOut().flatMap(c -> c.getTimeDay().getTimeWithDay())))
						.collect(Collectors.toList()));
			}
			// 打刻実績．休憩時間帯
			if(CollectionUtil.isEmpty(recordWorkInfoImport.getBreakTimeSheets())) {
				stampRecordOutput.setBreakTime(Collections.emptyList());
			} else {
				stampRecordOutput.setBreakTime(recordWorkInfoImport.getBreakTimeSheets().stream()
						.map(x -> new TimePlaceOutput(
								Optional.empty(), 
								Optional.empty(), 
								new StampFrameNo(x.getBreakFrameNo().v()), 
								Optional.of(x.getEndTime()), 
								Optional.of(x.getStartTime())))
						.collect(Collectors.toList()));
			}
		}
		//ドメインモデル「勤務種類」を1件取得する - (lấy 1 dữ liệu của domain 「WorkType」)
		opWorkTypeName = workTypeRepository.findByPK(companyID, workTypeCD).map(x -> x.getName().v());
		//ドメインモデル「就業時間帯」を1件取得する - (lấy 1 dữ liệu của domain 「WorkTime」)
		opWorkTimeName = WorkTimeRepository.findByCode(companyID, workTimeCD).map(x -> x.getWorkTimeDisplayName().getWorkTimeName().v());
		// #113162
		List<OvertimeLeaveTime> overtimeLeaveTimes = new ArrayList<OvertimeLeaveTime>();
		if (!(recordWorkInfoImport.getOverTimeLst() == null && recordWorkInfoImport.getCalculateHolidayLst() == null)) {
			if (recordWorkInfoImport.getOverTimeLst() != null) {
				recordWorkInfoImport.getOverTimeLst().entrySet().forEach(x -> {
				    int overTimeTransfer = recordWorkInfoImport.getCalculateTransferOverTimeLst().containsKey(x.getKey()) ? 
				            recordWorkInfoImport.getCalculateTransferOverTimeLst().get(x.getKey()).v() : 0;
					overtimeLeaveTimes.add(new OvertimeLeaveTime(x.getKey(), 0, x.getValue().v()  + overTimeTransfer, 0));
				});
			}
			
			if (recordWorkInfoImport.getCalculateHolidayLst() != null) {
				recordWorkInfoImport.getCalculateHolidayLst().entrySet().forEach(x -> {
				    int holidayTransfer = recordWorkInfoImport.getCalculateTransferLst().containsKey(x.getKey()) ? 
				            recordWorkInfoImport.getCalculateTransferLst().get(x.getKey()).v() : 0;
					overtimeLeaveTimes.add(new OvertimeLeaveTime(x.getKey(), 0, x.getValue().v() + holidayTransfer, 1));
				});
			}
			opOvertimeLeaveTimeLst = Optional.of(overtimeLeaveTimes);
		}
		Optional<AttendanceTimeOfExistMinus> flexTime = Optional.empty();
		if (recordWorkInfoImport.getCalculateFlex() != null) {
			flexTime = Optional.of(recordWorkInfoImport.getCalculateFlex());
		}
		
		AchievementDetail achievementDetail = new AchievementDetail(
				workTypeCD,
				workTimeCD,
				breakTimeSheets,
				timeContentOutput,
				trackRecordAtr,
				stampRecordOutput,
				shortWorkTimeLst,
				achievementEarly,
				opDepartureTime2,
				opWorkTypeName,
				opWorkTimeName,
				opWorkTime,
				opLeaveTime,
				opAchievementStatus,
				opWorkTime2,
				opOvertimeMidnightTime,
				opInlawHolidayMidnightTime,
				opOutlawHolidayMidnightTime,
				opPublicHolidayMidnightTime,
				opOvertimeLeaveTimeLst,
				flexTime);
		return new ActualContentDisplay(appDate, Optional.of(achievementDetail));
	}

	@Override
	public List<ActualContentDisplay> getAchievementContents(String companyID, String employeeID,
			List<GeneralDate> dateLst, ApplicationType appType) {
		List<ActualContentDisplay> result = new ArrayList<>();
		// INPUT．申請対象日リストをチェックする
		if(CollectionUtil.isEmpty(dateLst)) {
			return Collections.emptyList();
		}
		// INPUT．申請対象日リストを先頭から最後へループする
		for(GeneralDate loopDate : dateLst) {
			// 実績の取得
			ActualContentDisplay actualContentDisplay = this.getAchievement(companyID, employeeID, loopDate);
			// 取得した実績をOutput「表示する実績内容」に追加する
			result.add(actualContentDisplay);
		}
		return result.stream().sorted(Comparator.comparing(ActualContentDisplay::getDate)).collect(Collectors.toList());
	}

	@Override
	public List<PreAppContentDisplay> getPreAppContents(String companyID, String employeeID, List<GeneralDate> dateLst, ApplicationType appType, Optional<OvertimeAppAtr> opOvertimeAppAtr) {
		List<PreAppContentDisplay> result = new ArrayList<>();
		if (appType == ApplicationType.OVER_TIME_APPLICATION && opOvertimeAppAtr.isPresent() && opOvertimeAppAtr.get() == OvertimeAppAtr.MULTIPLE_OVERTIME) {
			List<Application> applications = applicationRepository.getByListDateReflectType2(
					employeeID,
					dateLst,
					Arrays.asList(ApplicationType.OVER_TIME_APPLICATION.value),
					Arrays.asList(ReflectedState.NOTREFLECTED.value, ReflectedState.WAITREFLECTION.value, ReflectedState.REFLECTED.value)
			).stream().filter(a -> a.getPrePostAtr() == PrePostAtr.PREDICT).collect(Collectors.toList());
			Map<String, AppOverTime> appOverTimes = appOverTimeRepository.getHashMapByID(companyID, applications.stream().map(Application::getAppID).collect(Collectors.toList()));
			for (int i = applications.size() - 1; i >= 0; i--) {
				AppOverTime appOverTime = appOverTimes.get(applications.get(i).getAppID());
				if (appOverTime != null && appOverTime.getOverTimeClf() == OvertimeAppAtr.MULTIPLE_OVERTIME) {
					result.add(new PreAppContentDisplay(
							applications.get(i).getAppDate().getApplicationDate(),
							Optional.of(appOverTime),
							Optional.empty()
					));
					break;
				}
			}
			return result;
		}
		// INPUT．申請対象日リストをチェックする
		if(CollectionUtil.isEmpty(dateLst)) {
			return Collections.emptyList();
		}
		// INPUT．申請対象日リストを先頭から最後へループする
		for(GeneralDate loopDate : dateLst) {
			// ドメインモデル「申請」を取得
			if(appType == ApplicationType.ABSENCE_APPLICATION) {
				// AppAbsence appAbsence = appAbsenceRepository.getAbsenceById(companyID, "").get();
				// result.add(appAbsence);
			}
			if(appType == ApplicationType.OVER_TIME_APPLICATION) {
				// ドメインモデル「申請」を取得(Lấy domain[Application])
				Optional<String> opPreAppID = applicationRepository.getNewestPreAppIDByEmpDate(employeeID, loopDate, appType);
				if(opPreAppID.isPresent()) {
					// ドメインモデル「残業申請」を取得する(Lấy domain「残業申請」 )
					Optional<AppOverTime> opAppOverTime = appOverTimeRepository.find(companyID, opPreAppID.get());
					result.add(new PreAppContentDisplay(loopDate, opAppOverTime, Optional.empty()));
				} else {
					result.add(new PreAppContentDisplay(loopDate, Optional.empty(), Optional.empty()));
				}
				continue;
			}
			
			if(appType == ApplicationType.HOLIDAY_WORK_APPLICATION) {
				// ドメインモデル「申請」を取得(Lấy domain[Application])
				Optional<String> opPreAppID = applicationRepository.getNewestPreAppIDByEmpDate(employeeID, loopDate, appType);
				if(opPreAppID.isPresent()) {
					// ドメインモデル「休日出勤申請」を取得する(lấy domain「休日出勤申請」 )
					Optional<AppHolidayWork> opAppHolidayWork = appHolidayWorkRepository.find(companyID, opPreAppID.get());
					result.add(new PreAppContentDisplay(loopDate, Optional.empty(), opAppHolidayWork));
				} else {
					result.add(new PreAppContentDisplay(loopDate, Optional.empty(), Optional.empty()));
				}
				continue;
			}
		}
		return result;
	}

}
