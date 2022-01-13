package nts.uk.screen.at.app.query.ksu.ksu002.a.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.schedule.dom.shift.management.DateInformation;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.GetDateInfoDuringThePeriodInput;
import nts.uk.shr.com.context.AppContexts;

@NoArgsConstructor
public class PlansResultsDto extends PersonalSchedulesDataDto{

	//・List<勤務予定（勤務情報）dto>
	@Setter
	@Getter
	public List<nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto> workScheduleWorkInfor;
	
	@Getter
	public List<WorkScheduleWorkInforDto> workScheduleWorkInfor2;
	
	@Getter
	public List<WorkScheduleWorkInforDto.Achievement> workScheduleWorkDaily;

	public PlansResultsDto(PersonalSchedulesDataDto personalSchedulesData,
			List<nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto> workScheduleWorkInfor) {
		super(personalSchedulesData, personalSchedulesData.weeklyData);
		this.workScheduleWorkInfor = workScheduleWorkInfor;
	}

	public void setWorkScheduleWorkInfor2(List<DateInformation> dateInformation, boolean actualData) {
		List<WorkScheduleWorkInforDto> listWorkScheduleWorkInfor = this.workScheduleWorkInfor
				.stream()
				.map(m -> {
					List<String> sids = new ArrayList<>();
					sids.add(AppContexts.user().employeeId());
					GetDateInfoDuringThePeriodInput param1 = new GetDateInfoDuringThePeriodInput();
					param1.setGeneralDate(m.getDate());
					param1.setSids(sids);
					
					EditStateOfDailyAttdDto workTypeEditStatus = null;
					if (m.workTypeEditStatus != null) {
						workTypeEditStatus = new EditStateOfDailyAttdDto(m.workTypeEditStatus.getAttendanceItemId(), m.workTypeEditStatus.getEditStateSetting());
					}
					
					EditStateOfDailyAttdDto workTimeEditStatus = null;
					if (m.workTimeEditStatus != null) {
						workTimeEditStatus = new EditStateOfDailyAttdDto(m.workTimeEditStatus.getAttendanceItemId(), m.workTimeEditStatus.getEditStateSetting());
					}
					
					EditStateOfDailyAttdDto startTimeEditState = null;
					if (m.startTimeEditState != null) {
						startTimeEditState = new EditStateOfDailyAttdDto(m.startTimeEditState.getAttendanceItemId(), m.startTimeEditState.getEditStateSetting());
					}
					
					EditStateOfDailyAttdDto endTimeEditState = null;
					if (m.endTimeEditState != null) {
						endTimeEditState = new EditStateOfDailyAttdDto(m.endTimeEditState.getAttendanceItemId(), m.endTimeEditState.getEditStateSetting());
					}
					
					// Xửa lý theo hướng của anh Lai
					Integer startTime = m.startTime;
					Integer endTime = m.endTime;
					
					if (startTime != null && endTime != null) {
						if (startTime == 0 && endTime == 0){
							startTime = null;
							endTime = null;
						}
					}
					
					
					WorkScheduleWorkInforDto dto = WorkScheduleWorkInforDto.builder()
							.employeeId(m.getEmployeeId())
							.date(m.getDate())
							.haveData(m.haveData)
							.achievements(null)
							.confirmed(m.confirmed)
							.needToWork(m.needToWork)
							.supportCategory(m.supportCategory)
							.workTypeCode(m.workTypeCode)
							.workTypeName(m.getWorkTypeNameKsu002())
							.workTypeEditStatus(workTypeEditStatus)
							.workTimeCode(m.getWorkTimeCode())
							.workTimeName(m.getWorkTimeNameKsu002())
							.workTimeEditStatus(workTimeEditStatus)
							.startTime(startTime)
							.startTimeEditState(startTimeEditState)
							.endTime(endTime)
							.endTimeEditState(endTimeEditState)
							.workHolidayCls(m.workHolidayCls)
							.dateInfoDuringThePeriod(dateInformation.stream().filter(c -> c.getYmd().equals(m.getDate())).findFirst().map(e -> new DateInfoDuringThePeriodDto(e)).orElse(new DateInfoDuringThePeriodDto()))
							.workTimeForm(m.workTimeForm)
							.build();
					return dto;
				}).collect(Collectors.toList());
		
		if (actualData) {
			
			for (WorkScheduleWorkInforDto ds : listWorkScheduleWorkInfor) {
				this.workScheduleWorkInfor.stream().filter(c -> c.employeeId.equals(ds.employeeId) && c.date.equals(ds.date) && c.achievements).findFirst()
					.ifPresent(c -> {
						WorkScheduleWorkInforDto.Achievement arch = WorkScheduleWorkInforDto
								.Achievement
								.builder()
								.workTypeCode(c.getWorkTypeCode())
								.workTypeName(c.getWorkTypeName())
								.workTimeCode(c.getWorkTimeCode())
								.workTimeName(c.getWorkTimeName())
								.startTime(c.getStartTime())
								.endTime(c.getEndTime())
								.build();
						
						ds.setAchievements(arch);
					});
			}
		}
		this.workScheduleWorkInfor2 = listWorkScheduleWorkInfor;
	}

	public void setWorkScheduleWorkDaily(List<nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto> workScheduleWorkDaily) {
		this.workScheduleWorkDaily = workScheduleWorkDaily.stream().map(m -> {
			return WorkScheduleWorkInforDto
					.Achievement
					.builder()
					.employeeId(m.getEmployeeId())
					.date(m.getDate())
					.workTypeCode(m.getWorkTypeCode())
					.workTypeName(m.getWorkTypeName())
					.workTimeCode(m.getWorkTimeCode())
					.workTimeName(m.getWorkTimeName())
					.startTime(m.getStartTime())
					.endTime(m.getEndTime())
					.build();
		}).collect(Collectors.toList());;
	}
	
	
	
}
