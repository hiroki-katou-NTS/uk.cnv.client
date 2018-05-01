package nts.uk.ctx.at.request.dom.applicationreflect.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.request.dom.application.ApplicationRepository_New;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.Application_New;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflectDaily_New;
import nts.uk.ctx.at.request.dom.application.ReasonNotReflect_New;
import nts.uk.ctx.at.request.dom.application.ReflectedState_New;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsence;
import nts.uk.ctx.at.request.dom.application.appabsence.AppAbsenceRepository;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectly;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentApp;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWork;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.AttendanceType;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChange;
import nts.uk.ctx.at.request.dom.application.workchange.IAppWorkChangeRepository;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.CommonReflectPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.AppDegreeReflectionAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.AppExecutionType;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.AppReflectRecordPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.GobackAppRequestPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.GobackReflectPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.HolidayWorkReflectPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.HolidayWorktimeAppRequestPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.OvertimeAppParameter;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.OvertimeReflectPara;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.PriorStampRequestAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.ReflectRecordInfor;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.ScheAndRecordSameChangeFlg;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.ScheTimeReflectRequesAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workschedule.ApplyTimeRequestAtr;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workschedule.ExecutionType;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workschedule.ReflectScheDto;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workschedule.WorkScheduleReflectService;
import nts.uk.ctx.at.request.dom.applicationreflect.service.workrecord.WorkRecordReflectService;

@Stateless
public class AppReflectManagerImpl implements AppReflectManager {
	@Inject
	private OvertimeRepository overTimeRepo;
	@Inject
	private WorkRecordReflectService workRecordReflect;
	@Inject
	private ApplicationRepository_New appRepo;
	@Inject
	private GoBackDirectlyRepository gobackRepo;
	@Inject
	private AppAbsenceRepository absenceRepo;
	@Inject
	private AppHolidayWorkRepository holidayWorkRepo;
	@Inject
	private IAppWorkChangeRepository workChangeRepo;
	@Inject
	private WorkScheduleReflectService scheReflect;
	@Inject
	private AbsenceLeaveAppRepository absenceLeaveRepo;
	@Inject
	private RecruitmentAppRepository recruitmentRepo;
	@Override
	public void reflectEmployeeOfApp(Application_New appInfor) {
		GobackReflectPara appGobackTmp = null;
		OvertimeReflectPara overTimeTmp = null;
		CommonReflectPara workchangeData = null;
		HolidayWorkReflectPara holidayworkInfor = null;
		CommonReflectPara absenceData = null;
		ReflectScheDto reflectScheParam = new ReflectScheDto(appInfor.getEmployeeID(), 
				appInfor.getAppDate(),
				ExecutionType.NORMALECECUTION, 
				true,
				ApplyTimeRequestAtr.START,
				appInfor,
				null,
				null,
				null,
				null,
				null,
				null);
		// TODO 再実行かどうか判断する (xác nhận xem có thực hiện lại hay k)
		//申請を取得 (lấy đơn)
		if(appInfor.getAppType() == ApplicationType.OVER_TIME_APPLICATION
				&& appInfor.getPrePostAtr() == PrePostAtr.PREDICT) {
			Optional<AppOverTime> getFullAppOvertime = overTimeRepo.getFullAppOvertime(appInfor.getCompanyID(), appInfor.getAppID());
			if(!getFullAppOvertime.isPresent()) {
				return;
			}
			AppOverTime appOvertimeInfor = getFullAppOvertime.get();			
			overTimeTmp = this.getOverTimeReflect(appInfor, appOvertimeInfor);
			if(overTimeTmp == null) {
				return;
			}
		} else if (appInfor.getAppType() == ApplicationType.GO_RETURN_DIRECTLY_APPLICATION) {
			Optional<GoBackDirectly> optGobackInfo = gobackRepo.findByApplicationID(appInfor.getCompanyID(), appInfor.getAppID());
			if(!optGobackInfo.isPresent()) {
				return;
			}
			GoBackDirectly gobackInfo = optGobackInfo.get();
			reflectScheParam.setGoBackDirectly(gobackInfo);
			appGobackTmp = this.getGobackReflectPara(appInfor, gobackInfo);
			if(appGobackTmp == null) {
				return;
			}
		} else if (appInfor.getAppType() == ApplicationType.ABSENCE_APPLICATION) {
			Optional<AppAbsence> optAbsence = absenceRepo.getAbsenceByAppId(appInfor.getCompanyID(), appInfor.getAppID());
			if(!optAbsence.isPresent()) {
				return;
			}
			AppAbsence absenceAppData = optAbsence.get();
			reflectScheParam.setForLeave(absenceAppData);
			absenceData = this.getAbsence(appInfor, absenceAppData);
			if(absenceData == null) {
				return;
			}
		} else if (appInfor.getAppType() == ApplicationType.BREAK_TIME_APPLICATION
				&& appInfor.getPrePostAtr() == PrePostAtr.PREDICT) {			
			Optional<AppHolidayWork> getFullAppHolidayWork = holidayWorkRepo.getFullAppHolidayWork(appInfor.getCompanyID(), appInfor.getAppID());
			if(!getFullAppHolidayWork.isPresent()) {
				return;
			}
			AppHolidayWork holidayWorkData = getFullAppHolidayWork.get();
			reflectScheParam.setHolidayWork(holidayWorkData);
			holidayworkInfor = this.getHolidayWork(appInfor, holidayWorkData);
			if(holidayworkInfor == null) {
				return;
			}
		} else if (appInfor.getAppType() == ApplicationType.WORK_CHANGE_APPLICATION) {
			Optional<AppWorkChange> getAppworkChangeById = workChangeRepo.getAppworkChangeById(appInfor.getCompanyID(), appInfor.getAppID());
			if(!getAppworkChangeById.isPresent()) {
				return;
			}
			AppWorkChange workChange = getAppworkChangeById.get();
			reflectScheParam.setWorkChange(workChange);
			workchangeData = this.getWorkChange(appInfor, workChange);
			if(workchangeData == null) {
				return;
			}
		} else if (appInfor.getAppType() == ApplicationType.COMPLEMENT_LEAVE_APPLICATION) {
			Optional<AbsenceLeaveApp> optAbsenceLeaveData = absenceLeaveRepo.findByAppId(appInfor.getAppID());
			if(optAbsenceLeaveData.isPresent()) {
				AbsenceLeaveApp absenceLeave = optAbsenceLeaveData.get();
				reflectScheParam.setAbsenceLeave(absenceLeave);
			} 
			
			Optional<RecruitmentApp> optRecruitmentData = recruitmentRepo.findByAppId(appInfor.getAppID());
			if(optRecruitmentData.isPresent()) {
				RecruitmentApp recruitmentData = optRecruitmentData.get();
				reflectScheParam.setRecruitment(recruitmentData);
			}
			
		
		} 
		
		
		else {
			return;
		}
		//TODO 反映するかどうか判断 (Xác định để phản ánh)
		//勤務予定へ反映処理	(Xử lý phản ánh đến kế hoạch công việc)		
		if(scheReflect.workscheReflect(reflectScheParam)) {
			appInfor.getReflectionInformation().setStateReflection(ReflectedState_New.REFLECTED);
			appInfor.getReflectionInformation().setNotReason(Optional.of(ReasonNotReflect_New.WORK_CONFIRMED));
		}
		//勤務実績へ反映処理(xử lý phản ảnh thành tích thực chuyên cần)
		ReflectRecordInfor reflectRecordInfor = new ReflectRecordInfor(AppDegreeReflectionAtr.RECORD, AppExecutionType.EXCECUTION, appInfor);		
		AppReflectRecordPara appPara = new AppReflectRecordPara(reflectRecordInfor, 
				appGobackTmp, overTimeTmp, 
				workchangeData, 
				holidayworkInfor, 
				absenceData);
		if(workRecordReflect.workRecordreflect(appPara)) {
			appInfor.getReflectionInformation().setStateReflectionReal(ReflectedState_New.REFLECTED);
			appInfor.getReflectionInformation().setNotReasonReal(Optional.of(ReasonNotReflectDaily_New.ACTUAL_CONFIRMED));
		}
		appRepo.updateWithVersion(appInfor);
	}
	
	private CommonReflectPara getWorkChange(Application_New appInfor, AppWorkChange workChange) {
		CommonReflectPara workchangeInfor = null;
		
		workchangeInfor = new CommonReflectPara(appInfor.getEmployeeID(), 
				appInfor.getAppDate(),
				ScheAndRecordSameChangeFlg.ALWAY, 
				true, 
				workChange.getWorkTypeCd(), 
				workChange.getWorkTimeCd(), appInfor.getReflectionInformation().getStateReflectionReal(), 
				appInfor.getReflectionInformation().getNotReasonReal().isPresent() ? appInfor.getReflectionInformation().getNotReasonReal().get() : null,
				appInfor.getStartDate().get(),
				appInfor.getEndDate().get());
		
		 
		return workchangeInfor;		
	}
	
	private HolidayWorkReflectPara getHolidayWork(Application_New appInfor, AppHolidayWork holidayWorkData) {
		HolidayWorkReflectPara holidayPara = null;
		Map<Integer, Integer> mapOvertimeFrame =  new HashMap<>();
		if(!holidayWorkData.getHolidayWorkInputs().isEmpty()) {
			holidayWorkData.getHolidayWorkInputs().stream().forEach(x -> {
				if(x.getAttendanceType() == AttendanceType.BREAKTIME) {
					mapOvertimeFrame.put(x.getFrameNo(), x.getApplicationTime().v());
				}
			});
		}
		HolidayWorktimeAppRequestPara appPara = new HolidayWorktimeAppRequestPara(holidayWorkData.getWorkTypeCode().v(), 
				holidayWorkData.getWorkTimeCode().v(),
				mapOvertimeFrame,
				holidayWorkData.getHolidayShiftNight(),
				appInfor.getReflectionInformation().getStateReflectionReal(), 
				!appInfor.getReflectionInformation().getNotReasonReal().isPresent() ? null : appInfor.getReflectionInformation().getNotReasonReal().get(),
						holidayWorkData.getWorkClock1().getStartTime() == null ? null : holidayWorkData.getWorkClock1().getStartTime().v(),
						holidayWorkData.getWorkClock1().getEndTime() == null ? null : holidayWorkData.getWorkClock1().getEndTime().v());
		holidayPara = new HolidayWorkReflectPara(appInfor.getEmployeeID(), appInfor.getAppDate(), true, ScheAndRecordSameChangeFlg.ALWAY, true, appPara);
		return holidayPara;
		
	}
	
	private CommonReflectPara getAbsence(Application_New appInfor, AppAbsence absenceAppData) {
		CommonReflectPara absenceInfor = null;
		
		absenceInfor = new CommonReflectPara(appInfor.getEmployeeID(),
				appInfor.getAppDate(), 
				ScheAndRecordSameChangeFlg.ALWAY, 
				true, 
				absenceAppData.getWorkTypeCode().v(), 
				"",
				appInfor.getReflectionInformation().getStateReflectionReal(), 
				appInfor.getReflectionInformation().getNotReasonReal().isPresent() ? appInfor.getReflectionInformation().getNotReasonReal().get() : null,
				appInfor.getStartDate().isPresent() ? appInfor.getStartDate().get() : null,
				appInfor.getEndDate().isPresent() ? appInfor.getEndDate().get() : null);
		return absenceInfor;
	}
	
	
	private GobackReflectPara getGobackReflectPara(Application_New appInfor, GoBackDirectly gobackInfo) {
		GobackReflectPara appGobackTmp = null;		
		GobackAppRequestPara gobackReques = new GobackAppRequestPara(
				gobackInfo.getWorkChangeAtr().isPresent() ? gobackInfo.getWorkChangeAtr().get() : null, 
				gobackInfo.getSiftCD().isPresent() ? gobackInfo.getSiftCD().get().v() : null, 
				gobackInfo.getWorkTypeCD().isPresent() ? gobackInfo.getWorkTypeCD().get().v() : null, 
				gobackInfo.getWorkTimeStart1().isPresent() ? gobackInfo.getWorkTimeStart1().get().v() : null, 
				gobackInfo.getWorkTimeEnd1().isPresent() ? gobackInfo.getWorkTimeEnd1().get().v() : null, 
				gobackInfo.getWorkTimeStart2().isPresent() ? gobackInfo.getWorkTimeStart2().get().v() : null, 
				gobackInfo.getWorkTimeEnd2().isPresent() ? gobackInfo.getWorkTimeEnd2().get().v() : null, 
				appInfor.getReflectionInformation().getStateReflectionReal(),
				appInfor.getReflectionInformation().getNotReasonReal().isPresent() ? appInfor.getReflectionInformation().getNotReasonReal().get() : null);
		appGobackTmp = new GobackReflectPara(appInfor.getEmployeeID(), appInfor.getAppDate(), true, PriorStampRequestAtr.GOBACKPRIOR,
				ScheAndRecordSameChangeFlg.ALWAY,
				ScheTimeReflectRequesAtr.APPTIME,
				true,
				gobackReques);
		
		return appGobackTmp;
	}
	
	/**
	 * 残業申請
	 * @param appInfor
	 * @return
	 */
	private OvertimeReflectPara getOverTimeReflect(Application_New appInfor, AppOverTime appOvertimeInfor) {
		OvertimeReflectPara overTimeTmp = null;
		
		Map<Integer, Integer> mapOvertimeFrame =  new HashMap<>();
		if(!appOvertimeInfor.getOverTimeInput().isEmpty()) {
			appOvertimeInfor.getOverTimeInput().stream().forEach(x -> {
				if(x.getAttendanceType() == AttendanceType.NORMALOVERTIME && x.getFrameNo() <= 10) {
					mapOvertimeFrame.put(x.getFrameNo(), x.getApplicationTime().v());
				}
			});
		}
		
		OvertimeAppParameter overtimePara = new OvertimeAppParameter(appInfor.getReflectionInformation().getStateReflectionReal(),
				appInfor.getReflectionInformation().getNotReasonReal().isPresent() ? appInfor.getReflectionInformation().getNotReasonReal().get() : null,
						appOvertimeInfor.getWorkTypeCode().v(),
						appOvertimeInfor.getSiftCode().v(),
						appOvertimeInfor.getWorkClockFrom1(),
						appOvertimeInfor.getWorkClockTo1(),
						appOvertimeInfor.getWorkClockFrom2(),
						appOvertimeInfor.getWorkClockTo2(),
						mapOvertimeFrame, 
						appOvertimeInfor.getOverTimeShiftNight(),
						appOvertimeInfor.getFlexExessTime()); 
		overTimeTmp = new OvertimeReflectPara(appInfor.getEmployeeID(), 
				appInfor.getAppDate(), 
				true,
				true,
				true,
				true,
				ScheAndRecordSameChangeFlg.ALWAY,
				true, 
				overtimePara); 
		
		
		return overTimeTmp;
		
	}

}
