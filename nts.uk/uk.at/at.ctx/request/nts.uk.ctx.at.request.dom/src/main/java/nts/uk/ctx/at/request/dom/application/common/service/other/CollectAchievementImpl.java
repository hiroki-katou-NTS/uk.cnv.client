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
		 * ???????????????
		 */
		List<TimePlaceOutput> nursingTime = this.createTimePlace(1);
		
		/**
		 * ???????????????
		 */
		List<TimePlaceOutput> breakTime = this.createTimePlace(2);
		
		/**
		 * ???????????????
		 */
		List<TimePlaceOutput> workingTime = this.createTimePlace(3);
		
		/**
		 * ???????????????
		 */
		List<TimePlaceOutput> outingTime = this.createTimePlace(4);
		
		/**
		 * ???????????????
		 */
		List<TimePlaceOutput> supportTime;
		
		/**
		 * ???????????????
		 */
		List<TimePlaceOutput> parentingTime = this.createTimePlace(6);
		
		/**
		 * ???????????????
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
		//Imported(????????????)????????????????????????????????? - (l???y th??ng tin Imported(appAproval)???DailyPerformance???) - RQ5
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
			//???????????????0??? ( s??? data l???y ???????c  = 0)
			//??????????????????????????????????????????????????????(check c?? qu???n l?? schedule hay kh??ng) - RQ536
			if(!scheduleManagementControlAdapter.isScheduleManagementAtr(applicantID, appDate)){//???????????????
				return new ActualContentDisplay(appDate, Optional.empty());
			}
			//????????????
			//Imported(????????????)?????????????????????????????????(l???y th??ng tin imported(????????????)??????????????????)
			ScBasicScheduleImport scBasicScheduleImport = scBasicScheduleAdapter.findByIDRefactor(applicantID, appDate);
			if (scBasicScheduleImport == null) {
				//???????????????0??? ( s??? data l???y ???????c  = 0)
				return new ActualContentDisplay(appDate, Optional.empty());
			} else {
				
				if((Strings.isBlank(scBasicScheduleImport.getWorkTypeCode()) && Strings.isBlank(scBasicScheduleImport.getWorkTimeCode().orElse(null)))){
					//???????????????0??? ( s??? data l???y ???????c  = 0)
					return new ActualContentDisplay(appDate, Optional.empty());
				}				
			}
			// ??????????????????????????????????????? (Ph??n lo???i th???c t???= Schedule)
			trackRecordAtr = TrackRecordAtr.SCHEDULE;
			//??????????????????1????????????????????????OUTPUT???????????????????????????????????????
			workTypeCD = scBasicScheduleImport.getWorkTypeCode();
			//??????????????????3???????????????????????????OUTPUT???????????????????????????????????????
			workTimeCD = scBasicScheduleImport.getWorkTimeCode().orElse(null);
			//??????????????????5???????????????OUTPUT??????????????????????????????1
			opWorkTime = scBasicScheduleImport.getScheduleStartClock1().flatMap(x -> Optional.of(x.v()));
			//??????????????????6???????????????OUTPUT??????????????????????????????1
			// check null to remove exception
			opLeaveTime = scBasicScheduleImport.getScheduleEndClock1().flatMap(x -> Optional.of(x.v()));
			//??????????????????9????????????2???OUTPUT??????????????????????????????2
			opWorkTime2 = scBasicScheduleImport.getScheduleStartClock2().map(x -> x.v());
			//??????????????????10????????????2???OUTPUT??????????????????????????????2
			opDepartureTime2 = scBasicScheduleImport.getScheduleEndClock2().map(x -> x.v());
			//?????????????????????????????????????????????????????????1???OUTPUT??????????????????????????????1
			//?????????????????????????????????????????????????????????1???OUTPUT??????????????????????????????1
			//?????????????????????????????????????????????????????????2???OUTPUT??????????????????????????????2
			//?????????????????????????????????????????????????????????2???OUTPUT??????????????????????????????2
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
		} else {//???????????????1???(s??? data l???y ???????c = 1)
			// ????????????????????????????????? (Ph??n lo???i th???c t??? = Th???c t??? h??ng ng??y )
			trackRecordAtr = TrackRecordAtr.DAILY_RESULTS;
			//??????????????????1????????????????????????OUTPUT???????????????????????????????????????
			workTypeCD = recordWorkInfoImport.getWorkTypeCode() == null ? null
					: recordWorkInfoImport.getWorkTypeCode().v();
			//??????????????????3???????????????????????????OUTPUT???????????????????????????????????????
			workTimeCD = recordWorkInfoImport.getWorkTimeCode() == null ? null
					: recordWorkInfoImport.getWorkTimeCode().v();
			//??????????????????5???????????????OUTPUT??????????????????????????????1
			opWorkTime = recordWorkInfoImport.getStartTime1().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//??????????????????6???????????????OUTPUT??????????????????????????????1
			opLeaveTime = recordWorkInfoImport.getEndTime1().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//??????????????????9????????????2???OUTPUT??????????????????????????????2
			opWorkTime2 = recordWorkInfoImport.getStartTime2().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//??????????????????10????????????2???OUTPUT??????????????????????????????2
			opDepartureTime2 = recordWorkInfoImport.getEndTime2().flatMap(x -> x.getTimeWithDay()).flatMap(y -> Optional.of(y.v()));
			//?????????????????????????????????????????????????????????1???OUTPUT????????????????????????????????????1
			//?????????????????????????????????????????????????????????1???OUTPUT????????????????????????????????????1
			//?????????????????????????????????????????????????????????2???OUTPUT????????????????????????????????????2
			//?????????????????????????????????????????????????????????2???OUTPUT????????????????????????????????????2
			achievementEarly = new AchievementEarly(
					recordWorkInfoImport.getScheduledAttendence1() == null ? Optional.empty() : Optional.of(recordWorkInfoImport.getScheduledAttendence1()),
					recordWorkInfoImport.getScheduledAttendence2(),
					recordWorkInfoImport.getScheduledDeparture1() == null ? Optional.empty() : Optional.of(recordWorkInfoImport.getScheduledDeparture1()),
					recordWorkInfoImport.getScheduledDeparture2());
			//??????????????????????????????????????????????????????OUTPUT??????????????????????????????
			//??????????????????????????????????????????????????????OUTPUT??????????????????????????????
			//???????????????????????????????????????????????????2???OUTPUT??????????????????????????????2
			//???????????????????????????????????????????????????2???OUTPUT??????????????????????????????2
			timeContentOutput = new TimeContentOutput(
					recordWorkInfoImport.getEarlyLeaveTime1(),
					recordWorkInfoImport.getEarlyLeaveTime2(),
					recordWorkInfoImport.getLateTime1(),
					recordWorkInfoImport.getLateTime2());
			//??????????????????????????????????????????????????????????????????OUTPUT??????????????????????????????????????????
			shortWorkTimeLst = recordWorkInfoImport.getShortWorkingTimeSheets();
			//????????????????????????????????????OUTPUT?????????????????????????????????
			breakTimeSheets = recordWorkInfoImport.getBreakTimeSheets();
			//???????????????????????????????????????OUTPUT????????????????????????????????????
			opOvertimeMidnightTime = recordWorkInfoImport.getOverTimeMidnight().map(x -> x.getCalcTime());
			//?????????????????????????????????????????????OUTPUT??????????????????????????????????????????
			opInlawHolidayMidnightTime = recordWorkInfoImport.getMidnightOnHoliday().map(x -> x.getCalcTime());
			//?????????????????????????????????????????????OUTPUT??????????????????????????????????????????
			opOutlawHolidayMidnightTime = recordWorkInfoImport.getOutOfMidnight().map(x -> x.getCalcTime());
			//?????????????????????????????????????????????OUTPUT??????????????????????????????????????????
			opPublicHolidayMidnightTime = recordWorkInfoImport.getMidnightPublicHoliday().map(x -> x.getCalcTime());
			// ?????????????????????????????????????????????/????????????
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
			// ?????????????????????????????????????????????/????????????
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
			// ??????????????????????????????
			
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
			// ??????????????????????????????
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
			// ??????????????????????????????
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
			// ??????????????????????????????
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
		//??????????????????????????????????????????1??????????????? - (l???y 1 d??? li???u c???a domain ???WorkType???)
		opWorkTypeName = workTypeRepository.findByPK(companyID, workTypeCD).map(x -> x.getName().v());
		//?????????????????????????????????????????????1??????????????? - (l???y 1 d??? li???u c???a domain ???WorkTime???)
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
		// INPUT????????????????????????????????????????????????
		if(CollectionUtil.isEmpty(dateLst)) {
			return Collections.emptyList();
		}
		// INPUT??????????????????????????????????????????????????????????????????
		for(GeneralDate loopDate : dateLst) {
			// ???????????????
			ActualContentDisplay actualContentDisplay = this.getAchievement(companyID, employeeID, loopDate);
			// ?????????????????????Output?????????????????????????????????????????????
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
		// INPUT????????????????????????????????????????????????
		if(CollectionUtil.isEmpty(dateLst)) {
			return Collections.emptyList();
		}
		// INPUT??????????????????????????????????????????????????????????????????
		for(GeneralDate loopDate : dateLst) {
			// ??????????????????????????????????????????
			if(appType == ApplicationType.ABSENCE_APPLICATION) {
				// AppAbsence appAbsence = appAbsenceRepository.getAbsenceById(companyID, "").get();
				// result.add(appAbsence);
			}
			if(appType == ApplicationType.OVER_TIME_APPLICATION) {
				// ??????????????????????????????????????????(L???y domain[Application])
				Optional<String> opPreAppID = applicationRepository.getNewestPreAppIDByEmpDate(employeeID, loopDate, appType);
				if(opPreAppID.isPresent()) {
					// ??????????????????????????????????????????????????????(L???y domain?????????????????? )
					Optional<AppOverTime> opAppOverTime = appOverTimeRepository.find(companyID, opPreAppID.get());
					result.add(new PreAppContentDisplay(loopDate, opAppOverTime, Optional.empty()));
				} else {
					result.add(new PreAppContentDisplay(loopDate, Optional.empty(), Optional.empty()));
				}
				continue;
			}
			
			if(appType == ApplicationType.HOLIDAY_WORK_APPLICATION) {
				// ??????????????????????????????????????????(L???y domain[Application])
				Optional<String> opPreAppID = applicationRepository.getNewestPreAppIDByEmpDate(employeeID, loopDate, appType);
				if(opPreAppID.isPresent()) {
					// ????????????????????????????????????????????????????????????(l???y domain???????????????????????? )
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
