package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.dom.schedule.workschedule.ScheManaStatuTempo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.attendancetime.TimeLeavingWork;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.GetListWtypeWtimeUseDailyAttendRecordService;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkTypeWorkTimeUseDailyAttendanceRecord;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeInfor;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.GetDateInfoDuringThePeriodInput;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).個人別と共通の処理.勤務実績で勤務予定（勤務情報）dtoを作成する
 * @author chungnt
 *
 */
@Stateless
public class CreateWorkScheduleBasedOnWorkRecord {
	
	@Inject
	private WorkTypeRepository workTypeRepo;
	@Inject
	private WorkTimeSettingRepository workTimeSettingRepo;
	@Inject
	private GetDateInfoDuringThePeriod getDateInfoDuringThePeriod;

	public List<WorkScheduleWorkInforDto> get(Map<ScheManaStatuTempo , Optional<IntegrationOfDaily>> param) {
		
		String companyId = AppContexts.user().companyId();
		List<WorkScheduleWorkInforDto> result = new ArrayList<>();
	
		// 1
		List<WorkInfoOfDailyAttendance> listWorkInfo = new ArrayList<WorkInfoOfDailyAttendance>();
		param.forEach((k, v) -> {
			if (v.isPresent()) {
				WorkInfoOfDailyAttendance workInfo = v.get().getWorkInformation();
				listWorkInfo.add(workInfo);
			}
		});
		
		WorkTypeWorkTimeUseDailyAttendanceRecord wTypeWTimeUseDailyAttendRecord = GetListWtypeWtimeUseDailyAttendRecordService
				.getdata(listWorkInfo);
		
		// 2
		List<WorkTypeCode> workTypeCodes = wTypeWTimeUseDailyAttendRecord.getLstWorkTypeCode().stream()
				.filter(wt -> wt != null).collect(Collectors.toList());
		List<String> lstWorkTypeCode = workTypeCodes.stream().map(i -> i.toString()).collect(Collectors.toList());
		// <<Public>> 指定した勤務種類をすべて取得する
		List<WorkTypeInfor> lstWorkTypeInfor = this.workTypeRepo.getPossibleWorkTypeAndOrder(companyId,
				lstWorkTypeCode);
		
		// 3
		List<WorkTimeCode> workTimeCodes = wTypeWTimeUseDailyAttendRecord.getLstWorkTimeCode().stream()
				.filter(wt -> wt != null).collect(Collectors.toList());
		List<String> lstWorkTimeCode = workTimeCodes.stream().map(i -> i.toString()).collect(Collectors.toList());
		List<WorkTimeSetting> lstWorkTimeSetting = workTimeSettingRepo.getListWorkTimeSetByListCode(companyId,
				lstWorkTimeCode);
		
		// 4
		
		param.forEach((key, value) -> {
			// step 5.1
			boolean needToWork = key.getScheManaStatus().needCreateWorkSchedule();
			
			if (value.isPresent()) {
				
//				WorkStamp check = value.get().getAttendanceLeave()
//										.map(m -> m.getTimeLeavingWorks())
//										.orElse(new ArrayList<>())
//										.stream().findFirst()
//										.map(m -> m.getAttendanceStamp())
//										.map(m -> m.get())
//										.map(m -> m.getActualStamp())
//										.map(m -> m.get())
//										.orElse(null);
				
				
				IntegrationOfDaily daily = value.get();
				if (daily.getAttendanceLeave().isPresent()) {
					if (daily.getAttendanceLeave().get().getTimeLeavingWorks() != null) {
						if (daily.getAttendanceLeave().get().getTimeLeavingWorks().stream().findFirst().isPresent()) {
							if (daily.getAttendanceLeave().get().getTimeLeavingWorks().stream().findFirst().get().getAttendanceStamp().isPresent()){
//								if (daily.getAttendanceLeave().get().getTimeLeavingWorks().stream().findFirst().get().getAttendanceStamp().get().getActualStamp().isPresent()) {
									String workTypeCode = value.get().getWorkInformation().getRecordInfo().getWorkTypeCode().v();
									Optional<WorkTypeInfor> workTypeInfor = lstWorkTypeInfor.stream().
											filter(m -> m.getWorkTypeCode().equals(workTypeCode)).findFirst();
									
									String workTimeCode = value.get().getWorkInformation().getRecordInfo().getWorkTimeCodeNotNull().map(m -> m.v()).orElse(null);
									Optional<WorkTimeSetting> workTimeSetting = lstWorkTimeSetting.stream().
											filter(m -> m.getWorktimeCode().v().equals(workTimeCode)).findFirst();
									
									Integer start = null;
									Integer end = null;
									
									List<TimeLeavingWork> tlworks = value
										.map(m -> m.getAttendanceLeave())
										.orElse(null)
										.map(m -> m.getTimeLeavingWorks())
										.orElse(null);
					
									if (tlworks != null) {
										TimeLeavingWork first = tlworks.get(0);
										TimeLeavingWork last = tlworks.get(tlworks.size() - 1);
					
										if (first != null) {
											
											start = first.getAttendanceStamp()
														.map(m -> m.getActualStamp())
														.orElse(null)
														.map(m -> m.getTimeDay())
														.map(m -> m.getTimeWithDay())
														.map(m -> m.get())
														.map(m -> m.v())
														.orElse(null);
										}
										
										if (last != null) {
											end = last.getLeaveStamp()
													.map(m -> m.getActualStamp())
													.orElse(null)
													.map(m -> m.getTimeDay())
													.map(m -> m.getTimeWithDay())
													.map(m -> m.get())
													.map(m -> m.v())
													.orElse(null);
										}
									}
									
									List<String> sids = new ArrayList<>();
									sids.add(AppContexts.user().employeeId());
									GetDateInfoDuringThePeriodInput param1 = new GetDateInfoDuringThePeriodInput();
									param1.setGeneralDate(key.getDate());
									param1.setSids(sids);
									
									WorkScheduleWorkInforDto dto = WorkScheduleWorkInforDto.builder()
											.employeeId(key.getEmployeeID())
											.date(key.getDate())
											.haveData(true)
											.achievements(null)
											.confirmed(true)
											.needToWork(needToWork)
											.supportCategory(1)
											.workTypeCode(workTypeCode)
											.workTypeName(workTypeInfor.map(m -> m.getAbbreviationName()).orElse(workTypeCode == null ? null : workTypeCode + "{#KSU002_31}"))
											.workTypeEditStatus(null)
											.workTimeCode(workTimeCode)
											.workTimeName(workTimeSetting.map(m -> m.getWorkTimeDisplayName().getWorkTimeName().v()).orElse(workTimeCode == null ? null : workTimeCode + "{#KSU002_31}"))
											.workTimeEditStatus(null)
											.startTime(start)
											.startTimeEditState(null)
											.endTime(end)
											.endTimeEditState(null)
											.dateInfoDuringThePeriod(this.getDateInfoDuringThePeriod.get(param1))
											.build();
									
									result.add(dto);
								}
							}
						}
//					}
				}
			}
		});
		
		return result;
	}	
}
