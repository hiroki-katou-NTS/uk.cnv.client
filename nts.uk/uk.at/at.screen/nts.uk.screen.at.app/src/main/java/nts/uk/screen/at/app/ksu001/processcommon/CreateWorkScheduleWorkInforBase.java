/**
 * 
 */
package nts.uk.screen.at.app.ksu001.processcommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
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
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeInfor;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.screen.at.app.ksu001.start.SupportCategory;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author laitv
 * ScreenQuery: 勤務実績で勤務予定（勤務情報）dtoを作成する
 * Path: UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).個人別と共通の処理.勤務実績で勤務予定（勤務情報）dtoを作成する
 */

@Stateless
public class CreateWorkScheduleWorkInforBase {
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepo;
	
	public List<WorkScheduleWorkInforDto> getDataScheduleOfWorkInfo(
			Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> map) {		
		
		String companyId = AppContexts.user().companyId();
		List<WorkInfoOfDailyAttendance> listWorkInfo = new ArrayList<WorkInfoOfDailyAttendance>();
		map.forEach((k, v) -> {
			if (v.isPresent()) {
				WorkInfoOfDailyAttendance workInfo = v.get().getWorkInformation();
				if (workInfo != null) {
					listWorkInfo.add(workInfo);
				}
			}
		});

		// call 日別勤怠の実績で利用する勤務種類と就業時間帯のリストを取得する
		WorkTypeWorkTimeUseDailyAttendanceRecord wTypeWTimeUseDailyAttendRecord = GetListWtypeWtimeUseDailyAttendRecordService.getdata(listWorkInfo);

		// step 2
		List<WorkTypeCode> workTypeCodes = wTypeWTimeUseDailyAttendRecord.getLstWorkTypeCode().stream().filter(wt -> wt != null).collect(Collectors.toList());
		List<String> lstWorkTypeCode     = workTypeCodes.stream().map(i -> i.toString()).collect(Collectors.toList());
		//<<Public>> 指定した勤務種類をすべて取得する
		List<WorkTypeInfor> lstWorkTypeInfor = this.workTypeRepo.getPossibleWorkTypeAndOrder(companyId, lstWorkTypeCode).stream().collect(Collectors.toList());;

		// step 3
		List<WorkTimeCode> workTimeCodes   = wTypeWTimeUseDailyAttendRecord.getLstWorkTimeCode().stream().filter(wt -> wt != null).collect(Collectors.toList());
		List<String> lstWorkTimeCode       = workTimeCodes.stream().map(i -> i.toString()).collect(Collectors.toList());
		List<WorkTimeSetting> lstWorkTimeSetting = workTimeSettingRepo.getListWorkTime(companyId, lstWorkTimeCode);

		// step 4
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = new ArrayList<>();
		map.forEach((k, v) -> {
			EmployeeWorkingStatus key = k;
			Optional<IntegrationOfDaily> value = v;

			// step 4.1
			boolean needToWork = key.getWorkingStatus().needCreateWorkSchedule();
			if (value.isPresent()) {
				// step 4.2
				IntegrationOfDaily daily = value.get();
				if (daily.getWorkInformation() != null) {
					WorkInformation workInformation = daily.getWorkInformation().getRecordInfo();

					String workTypeCode = workInformation.getWorkTypeCode() == null ? null : workInformation.getWorkTypeCode().toString();
					String workTypeName = null;
					boolean workTypeIsNotExit  = false;

					Optional<WorkTypeInfor> workTypeInfor = lstWorkTypeInfor.stream().filter(i -> i.getWorkTypeCode().equals(workTypeCode)).findFirst();
					if (workTypeInfor.isPresent()) {
						workTypeName = workTypeInfor.get().getAbbreviationName();
					} else if (!workTypeInfor.isPresent() && workTypeCode != null){
						workTypeIsNotExit = true;
					}

					String workTimeCode = workInformation.getWorkTimeCode() == null ? null: workInformation.getWorkTimeCode().toString();
					Optional<WorkTimeSetting> workTimeSetting = lstWorkTimeSetting.stream().filter(i -> i.getWorktimeCode().toString().equals(workTimeCode)).findFirst();
					String workTimeName = null;
					boolean workTimeIsNotExit  = false;

					if (workTimeSetting.isPresent()) {
						if (workTimeSetting.get().getWorkTimeDisplayName() != null && workTimeSetting.get().getWorkTimeDisplayName().getWorkTimeAbName() != null) {
							workTimeName = workTimeSetting.get().getWorkTimeDisplayName().getWorkTimeAbName().toString();
						}
					} else if (!workTimeSetting.isPresent() && workTimeCode != null){
						workTimeIsNotExit = true;
					}

					Integer startTime = null;
					if (daily.getAttendanceLeave().isPresent()) {
						Optional<TimeLeavingWork> timeLeavingWork = daily.getAttendanceLeave().get().getTimeLeavingWorks().stream().filter(i -> i.getWorkNo().v() == 1).findFirst();
						if (timeLeavingWork.isPresent()) {
							if (timeLeavingWork.get().getAttendanceStamp().isPresent()) {
								if (timeLeavingWork.get().getAttendanceStamp().get().getStamp().isPresent()) {
									if (timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay() != null) {
										if (timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
											startTime = timeLeavingWork.get().getAttendanceStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
										}
									}
								}
							}
						}
					}

					Integer endtTime = null;
					if (daily.getAttendanceLeave().isPresent()) {
						Optional<TimeLeavingWork> timeLeavingWork = daily.getAttendanceLeave().get().getTimeLeavingWorks().stream().filter(i -> i.getWorkNo().v() == 1).findFirst();
						if (timeLeavingWork.isPresent()) {
							if (timeLeavingWork.get().getLeaveStamp().isPresent()) {
								if (timeLeavingWork.get().getLeaveStamp().get().getStamp().isPresent()) {
									if (timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay() != null) {
										if (timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().isPresent()) {
											endtTime = timeLeavingWork.get().getLeaveStamp().get().getStamp().get().getTimeDay().getTimeWithDay().get().v();
										}
									}
								}
							}
						}
					}

					WorkScheduleWorkInforDto dto = WorkScheduleWorkInforDto.builder()
							.employeeId(key.getEmployeeID())
							.date(key.getDate())
							.haveData(true)
							.achievements(true)
							.confirmed(true)
							.needToWork(needToWork)
							.supportCategory(SupportCategory.NotCheering.value)
							.workTypeCode(workTypeCode)
							.workTypeName(workTypeName)
							.workTypeEditStatus(null)
							.workTimeCode(workTimeCode)
							.workTimeName(workTimeName)
							.workTimeEditStatus(null)
							.startTime(startTime)
							.startTimeEditState(null)
							.endTime(endtTime)
							.endTimeEditState(null)
							.workHolidayCls(null)
							.workTypeIsNotExit(workTypeIsNotExit)
							.workTimeIsNotExit(workTimeIsNotExit)
							.workTypeNameKsu002(workTypeInfor.map(m -> m.getAbbreviationName()).orElse(workTypeCode == null ? null : workTypeCode + "{#KSU002_31}"))
							.workTimeNameKsu002(workTimeSetting.map(m -> m.getWorkTimeDisplayName().getWorkTimeAbName().v()).orElse(workTimeCode == null ? null : workTimeCode + "{#KSU002_31}"))
							.workTimeForm(!workTimeSetting.isPresent() ? null : workTimeSetting.get().getWorkTimeDivision().getWorkTimeForm().value)
							.conditionAbc1(false)
							.conditionAbc2(false)
							.build();

					listWorkScheduleWorkInfor.add(dto);
				}
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