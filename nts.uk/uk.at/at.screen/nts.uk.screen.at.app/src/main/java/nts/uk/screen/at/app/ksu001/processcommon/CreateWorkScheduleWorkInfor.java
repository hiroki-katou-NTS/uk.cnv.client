package nts.uk.screen.at.app.ksu001.processcommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ConfirmedATR;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.GetListWtypeWtimeUseDailyAttendRecordService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkTypeWorkTimeUseDailyAttendanceRecord;
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
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingService;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.internal.PredetermineTimeSetForCalc;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeInfor;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.EditStateOfDailyAttdDto;
import nts.uk.screen.at.app.ksu001.start.SupportCategory;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author laitv
 * ScreenQuery: 勤務予定で勤務予定（勤務情報）dtoを作成する
 * Path : UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).個人別と共通の処理.勤務予定で勤務予定（勤務情報）dtoを作成する
 */
@Stateless
public class CreateWorkScheduleWorkInfor {
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepo;
	@Inject
	private WorkTimeSettingService workTimeSettingService;
	@Inject
	private BasicScheduleService basicScheduleService;
	@Inject
	private FixedWorkSettingRepository fixedWorkSet; 
	@Inject
	private FlowWorkSettingRepository flowWorkSet;
	@Inject
	private FlexWorkSettingRepository flexWorkSet;
	@Inject
	private PredetemineTimeSettingRepository predetemineTimeSet;
	
	public List<WorkScheduleWorkInforDto> getDataScheduleOfWorkInfo(
			Map<ScheManaStatuTempo, Optional<WorkSchedule>> mngStatusAndWScheMap) {		

		String companyId = AppContexts.user().companyId();
		List<WorkInfoOfDailyAttendance>  listWorkInfo = new ArrayList<WorkInfoOfDailyAttendance>();
		mngStatusAndWScheMap.forEach((k,v)->{
			if (v.isPresent()) {
				WorkInfoOfDailyAttendance workInfo = v.get().getWorkInfo();
				listWorkInfo.add(workInfo);
			}
		});
		
		// call 日別勤怠の実績で利用する勤務種類と就業時間帯のリストを取得する
		WorkTypeWorkTimeUseDailyAttendanceRecord wTypeWTimeUseDailyAttendRecord = GetListWtypeWtimeUseDailyAttendRecordService.getdata(listWorkInfo);

		// step 3
		List<WorkTypeCode> workTypeCodes = wTypeWTimeUseDailyAttendRecord.getLstWorkTypeCode().stream().filter(wt -> wt != null).collect(Collectors.toList());
		List<String> lstWorkTypeCode     = workTypeCodes.stream().map(i -> i.toString()).collect(Collectors.toList());
		//<<Public>> 指定した勤務種類をすべて取得する
		List<WorkTypeInfor> lstWorkTypeInfor = this.workTypeRepo.getPossibleWorkTypeAndOrder(companyId, lstWorkTypeCode).stream().collect(Collectors.toList());

		// step 4
		List<WorkTimeCode> workTimeCodes   = wTypeWTimeUseDailyAttendRecord.getLstWorkTimeCode().stream().filter(wt -> wt != null).collect(Collectors.toList());
		List<String> lstWorkTimeCode       = workTimeCodes.stream().map(i -> i.toString()).collect(Collectors.toList());
		List<WorkTimeSetting> lstWorkTimeSetting =  workTimeSettingRepo.getListWorkTime(companyId, lstWorkTimeCode);

		// step 5
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = new ArrayList<>();
		mngStatusAndWScheMap.forEach((k, v) -> {
			ScheManaStatuTempo key = k;
			Optional<WorkSchedule> value = v;

			// step 5.1
			boolean needToWork = key.getScheManaStatus().needCreateWorkSchedule();
			if (!value.isPresent()) {
				// step 5.2
				WorkScheduleWorkInforDto dto = WorkScheduleWorkInforDto.builder()
						.employeeId(key.getEmployeeID())
						.date(key.getDate())
						.haveData(false)
						.achievements(false)
						.confirmed(false)
						.needToWork(needToWork)
						.supportCategory(SupportCategory.NotCheering.value)
						.workTypeCode(null)
						.workTypeName(null)
						.workTypeEditStatus(null)
						.workTimeCode(null)
						.workTimeName(null)
						.workTimeEditStatus(null)
						.startTime(null)
						.startTimeEditState(null)
						.endTime(null)
						.endTimeEditState(null)
						.workHolidayCls(null)
						.isEdit(true)   //
						.isActive(true) //
						.build();

				// ※Abc1
				boolean isEdit = true;
				if(dto.needToWork == false){
					isEdit = false;
				}

				// ※Abc2
				boolean isActive = true;
				if(dto.needToWork == false){
					isActive = false;
				}
				dto.setEdit(isEdit);
				dto.setActive(isActive);
				listWorkScheduleWorkInfor.add(dto);
			} else {
				// step 5.2.1
				WorkSchedule workSchedule = value.get();
				WorkInformation workInformation = workSchedule.getWorkInfo().getRecordInfo();

				WorkInformation.Require require2 = new RequireWorkInforImpl(workTypeRepo,workTimeSettingRepo,workTimeSettingService, basicScheduleService, fixedWorkSet, flowWorkSet, flexWorkSet, predetemineTimeSet);
				Optional<WorkStyle> workStyle = Optional.empty();
				if (workInformation.getWorkTypeCode() != null) {
					workStyle = workInformation.getWorkStyle(require2); // workHolidayCls
				}

				String workTypeCode = workInformation.getWorkTypeCode() == null  ? null : workInformation.getWorkTypeCode().toString();
				String workTypeName = null;
				boolean workTypeIsNotExit  = false;

				Optional<WorkTypeInfor> workTypeInfor = lstWorkTypeInfor.stream().filter(i -> i.getWorkTypeCode().equals(workTypeCode)).findFirst();
				if (workTypeInfor.isPresent()) {
					workTypeName = workTypeInfor.get().getAbbreviationName();
				} else if (!workTypeInfor.isPresent() && workTypeCode != null){
					workTypeIsNotExit = true;
				}

				String workTimeCode = workInformation.getWorkTimeCode() == null  ? null : workInformation.getWorkTimeCode().toString();
				Optional<WorkTimeSetting> workTimeSetting = lstWorkTimeSetting.stream().filter(i -> i.getWorktimeCode().toString().equals(workTimeCode)).findFirst();
				String workTimeName = null;

				boolean workTimeIsNotExit  = false;
				if (workTimeSetting.isPresent()) {
					if (workTimeSetting.get().getWorkTimeDisplayName() != null && workTimeSetting.get().getWorkTimeDisplayName().getWorkTimeAbName() != null ) {
						workTimeName = workTimeSetting.get().getWorkTimeDisplayName().getWorkTimeAbName().toString();
					}
				} else  if (!workTimeSetting.isPresent() && workTimeCode != null){
					workTimeIsNotExit = true;
				}

				Integer startTime = null;
				Integer endtTime = null;

				if (workTimeCode != null) {
					if (workSchedule.getOptTimeLeaving().isPresent()) {
						Optional<TimeLeavingWork> timeLeavingWork = workSchedule.getOptTimeLeaving().get().getTimeLeavingWorks().stream().filter(i -> i.getWorkNo().v() == 1).findFirst();
						if (timeLeavingWork.isPresent()) {
							if(timeLeavingWork.get().getAttendanceStamp().isPresent()){
								if(timeLeavingWork.get().getAttendanceStamp().get().getStamp().isPresent()){
									if(timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay() != null){
										if(timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()){
											startTime = timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
										}
									}
								}
							}
						}
					}

					if (workSchedule.getOptTimeLeaving().isPresent()) {
						Optional<TimeLeavingWork> timeLeavingWork = workSchedule.getOptTimeLeaving().get().getTimeLeavingWorks().stream().filter(i -> i.getWorkNo().v() == 1).findFirst();
						if (timeLeavingWork.isPresent()) {
							if(timeLeavingWork.get().getLeaveStamp().isPresent()){
								if(timeLeavingWork.get().getLeaveStamp().get().getStamp().isPresent()){
									if(timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay() != null){
										if(timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()){
											endtTime = timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
										}
									}
								}
							}
						}
					}
				}

				Optional<EditStateOfDailyAttd> workTypeEditStatus = workSchedule.getLstEditState().stream().filter(i -> i.getAttendanceItemId() == 28).findFirst();
				Optional<EditStateOfDailyAttd> workTimeEditStatus = workSchedule.getLstEditState().stream().filter(i -> i.getAttendanceItemId() == 29).findFirst();
				Optional<EditStateOfDailyAttd> startTimeEditStatus = workSchedule.getLstEditState().stream().filter(i -> i.getAttendanceItemId() == 31).findFirst();
				Optional<EditStateOfDailyAttd> endTimeEditStatus = workSchedule.getLstEditState().stream().filter(i -> i.getAttendanceItemId() == 34).findFirst();

				WorkScheduleWorkInforDto dto = WorkScheduleWorkInforDto.builder()
						.employeeId(key.getEmployeeID())
						.date(key.getDate())
						.haveData(true)
						.achievements(false)
						.confirmed(workSchedule.getConfirmedATR().value == ConfirmedATR.CONFIRMED.value)
						.needToWork(needToWork)
						.supportCategory(SupportCategory.NotCheering.value)
						.workTypeCode(workTypeCode)
						.workTypeName(workTypeName)
						.workTypeEditStatus(workTypeEditStatus.isPresent() ? new EditStateOfDailyAttdDto(workTypeEditStatus.get().getAttendanceItemId(), workTypeEditStatus.get().getEditStateSetting().value) : null)
						.workTimeCode(workTimeCode)
						.workTimeName(workTimeName)
						.workTimeEditStatus(workTimeEditStatus.isPresent() ? new EditStateOfDailyAttdDto(workTimeEditStatus.get().getAttendanceItemId(), workTimeEditStatus.get().getEditStateSetting().value) : null)
						.startTime(startTime)
						.startTimeEditState(startTimeEditStatus.isPresent() ? new EditStateOfDailyAttdDto(startTimeEditStatus.get().getAttendanceItemId(), startTimeEditStatus.get().getEditStateSetting().value) : null)
						.endTime(endtTime)
						.endTimeEditState(endTimeEditStatus.isPresent() ? new EditStateOfDailyAttdDto(endTimeEditStatus.get().getAttendanceItemId(), endTimeEditStatus.get().getEditStateSetting().value) : null)
						.workHolidayCls(workStyle.isPresent() ? workStyle.get().value : null)
						.isEdit(true)
						.isActive(true)
						.workTypeIsNotExit(workTypeIsNotExit)
						.workTimeIsNotExit(workTimeIsNotExit)
						.workTypeNameKsu002(workTypeInfor.map(m -> m.getAbbreviationName()).orElse(workTypeCode == null ? null : workTypeCode + "{#KSU002_31}"))
						.workTimeNameKsu002(workTimeSetting.map(m -> m.getWorkTimeDisplayName().getWorkTimeAbName().v()).orElse(workTimeCode == null ? null : workTimeCode + "{#KSU002_31}"))
						.build();

				// ※Abc1
				boolean isEdit = true;
				if(dto.confirmed == true || dto.needToWork == false){
					isEdit = false;
				}

				// ※Abc2
				boolean isActive = true;
				if(dto.needToWork == false){
					isActive = false;
				}

				dto.setEdit(isEdit);
				dto.setActive(isActive);

				listWorkScheduleWorkInfor.add(dto);
			}
		});

		return listWorkScheduleWorkInfor;
	}
	
	@AllArgsConstructor
	private static class RequireWorkInforImpl implements WorkInformation.Require {

		private final String companyId = AppContexts.user().companyId();

		@Inject
		private WorkTypeRepository workTypeRepo;
		@Inject
		private WorkTimeSettingRepository workTimeSettingRepository;
		@Inject
		private WorkTimeSettingService workTimeSettingService;
		@Inject
		private BasicScheduleService basicScheduleService;
		@Inject
		private FixedWorkSettingRepository fixedWorkSet;
		@Inject
		private FlowWorkSettingRepository flowWorkSet;
		@Inject
		private FlexWorkSettingRepository flexWorkSet;
		@Inject
		private PredetemineTimeSettingRepository predetemineTimeSet;

		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			 return basicScheduleService.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}

//		@Override
//		public PredetermineTimeSetForCalc getPredeterminedTimezone(String workTypeCd, String workTimeCd, Integer workNo) {
//			return workTimeSettingService .getPredeterminedTimezone(companyId, workTimeCd, workTypeCd, workNo);
//		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
			Optional<FixedWorkSetting> workSetting = fixedWorkSet.findByKey(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}
		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
			Optional<FlowWorkSetting> workSetting = flowWorkSet.find(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}
		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
			Optional<FlexWorkSetting> workSetting = flexWorkSet.find(companyId, code.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}
		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
			Optional<PredetemineTimeSetting> workSetting = predetemineTimeSet.findByWorkTimeCode(companyId, wktmCd.v());
			return workSetting.isPresent() ? workSetting.get() : null;
		}

	}


}
