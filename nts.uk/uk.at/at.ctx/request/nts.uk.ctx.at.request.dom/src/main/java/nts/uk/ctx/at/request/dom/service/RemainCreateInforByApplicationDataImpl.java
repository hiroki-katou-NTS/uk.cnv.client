package nts.uk.ctx.at.request.dom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.val;

/*import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.CompltLeaveSimMng;*/

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.adapter.schedule.WorkScheduleToIntegrationOfDailyAdapter;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ReflectedState;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeaveRepository;
import nts.uk.ctx.at.request.dom.application.appabsence.apptimedigest.TimeDigestApplication;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.reflect.GetApplicationReflectionResultAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarly;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarlyRepository;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AttendanceType_Update;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImage;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImageRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppStamp;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampRepository;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplication;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationDetail;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeRepository;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.adapter.workschedule.WorkScheWorkInforSharedAdapter;
import nts.uk.ctx.at.shared.dom.dailyattdcal.converter.DailyRecordShareFinder;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.AppRemainCreateInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.ApplicationType;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DayoffChangeAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.PrePostAtr;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.TimeDigestionUsageInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SEmpHistoryImport;
import nts.uk.ctx.at.shared.dom.remainingnumber.paymana.SysEmploymentHisAdapter;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.VacationTimeUseInfor;
import nts.uk.ctx.at.shared.dom.remainingnumber.work.service.RemainCreateInforByApplicationData;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.application.common.ApplicationShare;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.CorrectDailyAttendanceService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ChangeDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.function.algorithm.ICorrectionAttendanceRule;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveComSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensLeaveEmSetRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveEmSetting;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItemRepository;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.ctx.at.shared.dom.worktype.specialholidayframe.SpecialHdFrameNo;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

@Stateless
public class RemainCreateInforByApplicationDataImpl implements RemainCreateInforByApplicationData {
	@Inject
	private ApplicationRepository appRepository;
	@Inject
	private AppWorkChangeRepository workChangeService;
	@Inject
	private GoBackDirectlyRepository goBackRepo;
	@Inject
	private ApplyForLeaveRepository applyForLeaveRepo;
	@Inject
	private RecruitmentAppRepository recAppRepo;
	@Inject
	private AbsenceLeaveAppRepository absAppRepo;
	@Inject
	private AppOverTimeRepository overtimeRepo;
	@Inject
	private AppHolidayWorkRepository holidayWorkRepo;
	@Inject
	private TimeLeaveApplicationRepository timeLeaveRepo;
	@Inject
	private BusinessTripRepository businessTripRepo;
	@Inject
	private AppStampRepository appStampRepository;
	@Inject
	private ArrivedLateLeaveEarlyRepository arrivedLateLeaveEarlyRepository;
	@Inject
	private AppRecordImageRepository appRecordImageRepository;
	@Inject
	private OptionalItemApplicationRepository optionalItemApplicationRepository;
	@Inject
	private GetApplicationReflectionResultAdapter getApplicationReflectionResultAdapter;
	@Inject
	private DailyRecordShareFinder dailyRecordShareFinder;
	@Inject
	private ICorrectionAttendanceRule correctionAttendanceRule;
	@Inject
	private WorkTypeRepository workTypeRepository;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;
	@Inject
	private BasicScheduleService service;
	@Inject
	private FlexWorkSettingRepository flexWorkSettingRepository;
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSettingRepository;
	@Inject
	private FixedWorkSettingRepository fixedWorkSettingRepository;
	@Inject
	private FlowWorkSettingRepository flowWorkSettingRepository;
	@Inject
	private WorkScheduleToIntegrationOfDailyAdapter workScheduleAdapter;
	@Inject
	private CompensLeaveEmSetRepository compensLeaveEmSetRepo;
	@Inject
	private CompensLeaveComSetRepository compensLeaveComSetRepo;
	@Inject
	private SysEmploymentHisAdapter sysEmploymentHisAdapter;
	@Inject
	private ScBasicScheduleAdapter scBasicScheduleAdapter;
	@Inject
	private WorkScheWorkInforSharedAdapter workScheWorkInforSharedAdapter;
	@Inject
	private WorkingConditionItemRepository workingConditionItemRepo;
	
	@Override
	public List<AppRemainCreateInfor> lstRemainDataFromApp(CacheCarrier cacheCarrier, String cid, String sid,
			DatePeriod dateData) {
		List<Integer> lstReflect = new ArrayList<>();
		lstReflect.add(ReflectedState.NOTREFLECTED.value);
		lstReflect.add(ReflectedState.WAITREFLECTION.value);
		lstReflect.add(ReflectedState.REMAND.value);
		List<Integer> lstAppType = this.lstAppType();
		List<Application> lstAppData = new ArrayList<>();
		if (!lstAppType.isEmpty()) {
			lstAppData = appRepository.getByPeriodReflectType(sid, dateData, lstReflect, lstAppType);
		}
		return this.lstResult(cid, sid, lstAppData);
		
	}

	@Override
	public List<AppRemainCreateInfor> lstRemainDataFromApp(CacheCarrier cacheCarrier, String cid, String sid,
			List<GeneralDate> dates) {
		List<Integer> lstReflect = new ArrayList<>();
		lstReflect.add(ReflectedState.NOTREFLECTED.value);
		lstReflect.add(ReflectedState.WAITREFLECTION.value);
		List<Integer> lstAppType = this.lstAppType();
		List<Application> lstAppData = new ArrayList<>();
		if (!lstAppType.isEmpty()) {
			lstAppData = appRepository.getByListDateReflectType(sid, dates, lstReflect, lstAppType);
		}
		return this.lstResult(cid, sid, lstAppData);
	}

	@Override
	public List<AppRemainCreateInfor> lstRemainDataFromApp(CacheCarrier cacheCarrier, String cid, String sid,
			GeneralDate baseDate) {
		List<Integer> lstReflect = new ArrayList<>();
		lstReflect.add(ReflectedState.NOTREFLECTED.value);
		lstReflect.add(ReflectedState.WAITREFLECTION.value);
		List<Integer> lstAppType = this.lstAppType();
		List<Application> lstAppData = new ArrayList<>();
		if (!lstAppType.isEmpty()) {
			lstAppData = appRepository.getByPeriodReflectType(sid, baseDate, lstReflect, lstAppType);
		}
		return this.lstResult(cid, sid, lstAppData);
	}

	private List<Integer> lstAppType() {
		List<Integer> lstAppType = new ArrayList<>();
		// 反映する時、エラーが発生してるので、とりあえずコメントする（暫定データ処理は申請の新ドメインをまだ対応しない）
		lstAppType.add(ApplicationType.WORK_CHANGE_APPLICATION.value);
		lstAppType.add(ApplicationType.GO_RETURN_DIRECTLY_APPLICATION.value);
		lstAppType.add(ApplicationType.ABSENCE_APPLICATION.value);
		lstAppType.add(ApplicationType.COMPLEMENT_LEAVE_APPLICATION.value);
		lstAppType.add(ApplicationType.OVER_TIME_APPLICATION.value);
		lstAppType.add(ApplicationType.BREAK_TIME_APPLICATION.value);
		lstAppType.add(ApplicationType.BUSINESS_TRIP_APPLICATION.value);
		lstAppType.add(ApplicationType.ANNUAL_HOLIDAY_APPLICATION.value);
		return lstAppType;
	}

	// 申請種類に応じて残数作成元情報を作成する
	private List<AppRemainCreateInfor> lstResult(String cid, String sid, List<Application> lstAppData) {
		RequireImpl impl = new RequireImpl(cid);
		List<AppRemainCreateInfor> lstOutputData = new ArrayList<>();
		lstAppData.stream().forEach(appData -> {
			AppRemainCreateInfor outData = AppRemainCreateInfor.createDefault(sid, appData.getAppID(), 
					appData.getInputDate(), 
					appData.getAppDate().getApplicationDate(), 
					EnumAdaptor.valueOf(appData.getPrePostAtr().value, PrePostAtr.class), 
					EnumAdaptor.valueOf(appData.getAppType().value, ApplicationType.class),
					appData.getOpAppStartDate().map(x -> x.getApplicationDate()),
					appData.getOpAppEndDate().map(x -> x.getApplicationDate())
					);
			switch (outData.getAppType()) {
			case WORK_CHANGE_APPLICATION:
				Optional<AppWorkChange> workChange = workChangeService.findbyID(cid, appData.getAppID());
				workChange.ifPresent(x -> {
					outData.setWorkTimeCode(x.getOpWorkTimeCD().map(time -> time.v()));
					outData.setWorkTypeCode(x.getOpWorkTypeCD().map(type -> type.v()));
				});
				break;
			case GO_RETURN_DIRECTLY_APPLICATION:
				Optional<GoBackDirectly> goBack = goBackRepo.find(cid, appData.getAppID());
				goBack.ifPresent(x -> {
					outData.setWorkTimeCode(x.getDataWork().map(dw -> dw.getWorkTimeCode().v()));
					outData.setWorkTypeCode(x.getDataWork().map(dw -> dw.getWorkTypeCode().v()));
				});
				break;
			case ABSENCE_APPLICATION:
				Optional<ApplyForLeave> absence = applyForLeaveRepo.findApplyForLeave(cid, appData.getAppID());
				absence.ifPresent(x -> {
					outData.setWorkTypeCode(
							x.getReflectFreeTimeApp().getWorkInfo().getWorkTypeCode() == null ? Optional.empty()
									: Optional.of(x.getReflectFreeTimeApp().getWorkInfo().getWorkTypeCode().v()));
					if (x.getReflectFreeTimeApp().getWorkChangeUse().equals(NotUseAtr.USE)) {
						outData.setWorkTimeCode(
								x.getReflectFreeTimeApp().getWorkInfo().getWorkTimeCode() == null ? Optional.empty()
										: Optional.of(x.getReflectFreeTimeApp().getWorkInfo().getWorkTimeCode().v()));
					}
					outData.setTimeDigestionUsageInfor(x.getReflectFreeTimeApp().getTimeDegestion()
							.map(time -> fromTimeDegest(time)).orElse(Optional.empty()));
				});
				break;
			case COMPLEMENT_LEAVE_APPLICATION:
				Optional<AbsenceLeaveApp> optAbsApp = absAppRepo.findByAppId(appData.getAppID());
				optAbsApp.ifPresent(x -> {
					outData.setWorkTypeCode(Optional.of(x.getWorkInformation().getWorkTypeCode().v()));
					if (x.getWorkChangeUse().equals(NotUseAtr.USE)) {
						outData.setWorkTimeCode(
								x.getWorkInformation().getWorkTimeCodeNotNull().isPresent() ? Optional.empty()
										: Optional.of(x.getWorkInformation().getWorkTimeCode().v()));
					}

				});

				Optional<RecruitmentApp> recApp = recAppRepo.findByID(appData.getAppID());
				recApp.ifPresent(y -> {
					outData.setWorkTimeCode(Optional.of(y.getWorkInformation().getWorkTimeCode().v()));
					outData.setWorkTypeCode(Optional.of(y.getWorkInformation().getWorkTypeCode().v()));

				});

				break;
			case OVER_TIME_APPLICATION:
				Optional<AppOverTime> overTimeData = overtimeRepo.find(cid, appData.getAppID());
				Integer appBreakTimeTotal = 0;
				Integer appOvertimeTimeTotal = 0;
				if (overTimeData.isPresent()) {
					AppOverTime x = overTimeData.get();
					outData.setWorkTimeCode(x.getWorkInfoOp().map(wrk -> wrk.getWorkTimeCode().v()));
					outData.setWorkTypeCode(x.getWorkInfoOp().map(wrk -> wrk.getWorkTypeCode().v()));
					// 申請休出時間合計を設定する
					appBreakTimeTotal = x.getApplicationTime().getApplicationTime().stream()
							.filter(time -> time.getAttendanceType().equals(AttendanceType_Update.BREAKTIME))
							.mapToInt(time -> time.getApplicationTime().v()).sum();

					// 申請残業時間合計を設定する

					appOvertimeTimeTotal = x.getApplicationTime().getApplicationTime().stream()
							.filter(time -> time.getAttendanceType().equals(AttendanceType_Update.NORMALOVERTIME))
							.mapToInt(time -> time.getApplicationTime().v()).sum();
					
					val transferTimer = CalculationOfTransferTime.process(impl, cid, sid, appData.getAppDate().getApplicationDate(), x.getWorkInfoOp().map(y -> y.getWorkTypeCode().v()), 
							CalculationOfTransferTimeResult.create(x.getWorkInfoOp().flatMap(y -> y.getWorkTimeCodeNotNull().map(z -> z.v())), appBreakTimeTotal, appOvertimeTimeTotal), 
							DayoffChangeAtr.OVERTIME);
					if(transferTimer.isPresent()) {
						outData.setWorkTimeCode(transferTimer.get().getWorkTimeCode());
						outData.setAppBreakTimeTotal(transferTimer.get().getHolidayTransTime().map(y -> y.v()));
						outData.setAppOvertimeTimeTotal(transferTimer.get().getOverTransTime().map(y -> y.v()));
					}
				}
				break;
			case BREAK_TIME_APPLICATION:
				Optional<AppHolidayWork> holidayWork = holidayWorkRepo.find(cid, appData.getAppID());
				Integer breakTimeTotal = 0;
				Integer overtimeTimeTotal = 0;
				if (holidayWork.isPresent()) {
					AppHolidayWork x = holidayWork.get();
					outData.setWorkTimeCode(Optional.ofNullable(x.getWorkInformation().getWorkTimeCode().v()));
					outData.setWorkTypeCode(Optional.ofNullable(x.getWorkInformation().getWorkTypeCode().v()));
					// 申請休出時間合計を設定する
					breakTimeTotal = x.getApplicationTime().getApplicationTime().stream()
							.filter(time -> time.getAttendanceType().equals(AttendanceType_Update.BREAKTIME))
							.mapToInt(time -> time.getApplicationTime().v()).sum();

					overtimeTimeTotal = x.getApplicationTime().getApplicationTime().stream()
							.filter(time -> time.getAttendanceType().equals(AttendanceType_Update.NORMALOVERTIME))
							.mapToInt(time -> time.getApplicationTime().v()).sum();
					val transferTimer = CalculationOfTransferTime.process(impl, cid, sid,
							appData.getAppDate().getApplicationDate(),
							Optional.of(x.getWorkInformation().getWorkTypeCode().v()),
							CalculationOfTransferTimeResult.create(x.getWorkInformation().getWorkTimeCodeNotNull().map(z -> z.v()), breakTimeTotal, overtimeTimeTotal),
							DayoffChangeAtr.BREAKTIME);
					if(transferTimer.isPresent()) {
						outData.setWorkTimeCode(transferTimer.get().getWorkTimeCode());
						outData.setAppBreakTimeTotal(transferTimer.get().getHolidayTransTime().map(y -> y.v()));
						outData.setAppOvertimeTimeTotal(transferTimer.get().getOverTransTime().map(y -> y.v()));
					}
					
				}
				break;

			case ANNUAL_HOLIDAY_APPLICATION:
				// 時間休暇申請
				this.timeLeaveRepo.findById(cid, appData.getAppID()).ifPresent(x -> {
					List<VacationTimeUseInfor> vacationTimes = x.getLeaveApplicationDetails().stream()
							.map(time -> mapFromTimeLeave(time)).collect(Collectors.toList());
					outData.setVacationTimes(vacationTimes);
				});

				break;
			case BUSINESS_TRIP_APPLICATION:
				// 出張申請
				this.businessTripRepo.findByAppId(cid, appData.getAppID()).ifPresent(x -> {
					if (!x.getInfos().isEmpty()) {
						WorkInformation wrkInfo = x.getInfos().get(0).getWorkInformation();
						outData.setWorkTimeCode(wrkInfo.getWorkTimeCode() == null ? Optional.empty()
								: Optional.of(wrkInfo.getWorkTimeCode().v()));
						outData.setWorkTypeCode(wrkInfo.getWorkTypeCode() == null ? Optional.empty()
								: Optional.of(wrkInfo.getWorkTypeCode().v()));
					}
				});
				break;
			default:
				break;
			}
			
			//予定or実績への全ての申請反映結果を取得
//			Optional<IntegrationOfDaily> domainDailySche = ObtainAllAppReflecResultInScheduleRecord.getData(impl, cid,
//					sid, outData.getAppDate(), outData.getAppId());
//			if(domainDailySche.isPresent()) {
//				if(outData.getWorkTypeCode().isPresent()) {
//					WorkInfoOfDailyAttendance workAfter = CorrectDailyAttendanceService.correctFurikyu(impl, domainDailySche.get().getWorkInformation(),
//							new WorkInfoOfDailyAttendance(
//									new WorkInformation(outData.getWorkTypeCode().get(),
//											outData.getWorkTimeCode().orElse(null)),
//									domainDailySche.get().getWorkInformation().getCalculationState(),
//									domainDailySche.get().getWorkInformation().getGoStraightAtr(),
//									domainDailySche.get().getWorkInformation().getBackStraightAtr(),
//									domainDailySche.get().getWorkInformation().getDayOfWeek(),
//									domainDailySche.get().getWorkInformation().getScheduleTimeSheets(), Optional.empty()));
//					outData.setNumberOfDaySusp(workAfter.getNumberDaySuspension());
//				}
//			}
			lstOutputData.add(outData);
		});
		return lstOutputData;
	}

	private Optional<TimeDigestionUsageInfor> fromTimeDegest(TimeDigestApplication time) {
		return Optional.ofNullable(
				new TimeDigestionUsageInfor(time.getTimeAnnualLeave() == null ? null : time.getTimeAnnualLeave().v(),
						time.getTimeOff() == null ? null : time.getTimeOff().v(),
						time.getOvertime60H() == null ? null : time.getOvertime60H().v(),
						time.getChildTime() == null ? null : time.getChildTime().v(),
						time.getNursingTime() == null ? null : time.getNursingTime().v()));
	}

	private VacationTimeUseInfor mapFromTimeLeave(TimeLeaveApplicationDetail time) {

		TimeDigestApplication timed = time.getTimeDigestApplication();

		return VacationTimeUseInfor.builder().timeType(time.getAppTimeType()).nenkyuTime(timed.getTimeAnnualLeave())
				.kyukaTime(timed.getTimeOff()).hChoukyuTime(timed.getOvertime60H())
				.specialHolidayUseTime(timed.getTimeSpecialVacation()).timeChildCareHolidayUseTime(timed.getChildTime())
				.timeCareHolidayUseTime(timed.getNursingTime())
				.spcVacationFrameNo(Optional
						.ofNullable(timed.getSpecialVacationFrameNO().map(x -> new SpecialHdFrameNo(x)).orElse(null)))
				.build();
	}

	public static interface Require extends ObtainAllAppReflecResultInScheduleRecord.Require, CorrectDailyAttendanceService.Require, CalculationOfTransferTime.Require {
	}

	@AllArgsConstructor
	class RequireImpl implements RemainCreateInforByApplicationDataImpl.Require {
		
		private String cid;
		
		@Override
		public Optional<IntegrationOfDaily> findDailyRecord(String sid, GeneralDate date) {
			return dailyRecordShareFinder.find(sid, date);
		}

		@Override
		public Optional<IntegrationOfDaily> findSchedule(String sid, GeneralDate date) {
			return workScheduleAdapter.getWorkSchedule(sid, date);
		}

		@Override
		public Optional<IntegrationOfDaily> getAppReflectResult(String cid, ApplicationShare application,
				GeneralDate baseDate, Optional<IntegrationOfDaily> dailyData) {
			return getApplicationReflectionResultAdapter.getApp(cid, application, baseDate, dailyData);
		}

		@Override
		public List<Application> getAppForReflect(String sid, GeneralDate dateData, List<Integer> recordStatus,
				List<Integer> scheStatus, List<Integer> appType) {
			return appRepository.getAppForReflect(sid, new DatePeriod(dateData, dateData), recordStatus, scheStatus,
					appType);
		}

		@Override
		public Optional<AppWorkChange> findAppWorkCg(String companyId, String appID, Application app) {
			return workChangeService.findbyID(companyId, appID, app);
		}

		@Override
		public Optional<GoBackDirectly> findGoBack(String companyId, String appID, Application app) {
			return goBackRepo.find(companyId, appID, app);
		}

		@Override
		public Optional<AppStamp> findAppStamp(String companyId, String appID, Application app) {
			return appStampRepository.findByAppID(companyId, appID, app);
		}

		@Override
		public Optional<ArrivedLateLeaveEarly> findArrivedLateLeaveEarly(String companyId, String appID,
				Application application) {
			return Optional.ofNullable(arrivedLateLeaveEarlyRepository.getLateEarlyApp(companyId, appID, application));
		}

		@Override
		public Optional<BusinessTrip> findBusinessTripApp(String companyId, String appID, Application app) {
			return businessTripRepo.findByAppId(companyId, appID, app);
		}

		@Override
		public Optional<AppRecordImage> findAppRecordImage(String companyId, String appID, Application app) {
			return appRecordImageRepository.findByAppID(companyId, appID, app);
		}

		@Override
		public Optional<TimeLeaveApplication> findTimeLeavById(String companyId, String appId) {
			return timeLeaveRepo.findById(companyId, appId);
		}

		@Override
		public Optional<AppOverTime> findOvertime(String companyId, String appId) {
			return overtimeRepo.find(companyId, appId);
		}

		@Override
		public Optional<ApplyForLeave> findApplyForLeave(String CID, String appId) {
			return applyForLeaveRepo.findApplyForLeave(CID, appId);
		}

		@Override
		public Optional<AppHolidayWork> findAppHolidayWork(String companyId, String appId) {
			return holidayWorkRepo.find(companyId, appId);
		}

		@Override
		public Optional<AbsenceLeaveApp> findAbsenceByID(String applicationID) {
			return absAppRepo.findByAppId(applicationID);
		}

		@Override
		public Optional<RecruitmentApp> findRecruitmentByID(String applicationID) {
			return recAppRepo.findByAppId(applicationID);
		}

		@Override
		public Optional<OptionalItemApplication> getOptionalByAppId(String companyId, String appId) {
			return optionalItemApplicationRepository.getByAppId(companyId, appId);
		}

		@Override
		public IntegrationOfDaily process(IntegrationOfDaily domainDaily, ChangeDailyAttendance changeAtt) {
			return correctionAttendanceRule.process(domainDaily, changeAtt);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepository.findByPK(cid, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(cid, workTimeCode);
		}

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			return service.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			return fixedWorkSettingRepository.findByKey(cid, code.v()).get();
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			return flowWorkSettingRepository.find(cid, code.v()).get();
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			return flexWorkSettingRepository.find(cid, code.v()).get();
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			return predetemineTimeSettingRepository.findByWorkTimeCode(cid, wktmCd.v()).get();
		}

		@Override
		public Optional<SEmpHistoryImport> getEmploymentHis(String employeeId, GeneralDate baseDate) {
			return sysEmploymentHisAdapter.findSEmpHistBySid(AppContexts.user().companyId(), employeeId, baseDate);
		}
		
		@Override
		public Optional<CompensatoryLeaveComSetting> getCmpLeaveComSet(String companyId){
			return Optional.ofNullable(compensLeaveComSetRepo.find(companyId));
		}
		
		@Override
		public Optional<CompensatoryLeaveEmSetting> getCmpLeaveEmpSet(String companyId, String employmentCode){
			return Optional.ofNullable(compensLeaveEmSetRepo.find(companyId, employmentCode));
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode) {
			return workTimeSettingRepository.findByCode(cid, workTimeCode);
		}

		@Override
		public CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId) {
			return compensLeaveComSetRepo.find(companyId);
		}

		@Override
		public Optional<ScBasicScheduleImport> findByIDRefactor(String employeeID, GeneralDate date) {
			return Optional.ofNullable(scBasicScheduleAdapter.findByIDRefactor(employeeID, date));
		}

		@Override
		public Optional<WorkInfoOfDailyAttendance> getWorkInfoOfDailyAttendance(String employeeId, GeneralDate ymd) {
			return workScheWorkInforSharedAdapter.getWorkInfoOfDailyAttendance(employeeId, ymd);
		}

		@Override
		public Optional<WorkType> findByPK(String companyId, String workTypeCd) {
			return getWorkType(workTypeCd);
		}

		@Override
		public Optional<WorkingConditionItem> getWorkingConditionItemByEmpIDAndDate(String companyID, GeneralDate ymd,
				String empID) {
			return workingConditionItemRepo.getBySidAndStandardDate(empID, ymd);
		}

	}

}
