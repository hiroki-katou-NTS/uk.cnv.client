package nts.uk.screen.at.app.query.ksu.ksu002.a;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.query.ksu.ksu002.a.dto.WorkScheduleWorkInforDto;
import nts.uk.screen.at.app.query.ksu.ksu002.a.input.DisplayInWorkInfoInput;

/**
 * @author chungnt 
 * ScreenQuery: 予定・実績を取得する
 */

@Stateless
public class GetScheduleActualOfWorkInfo002 {
	@Inject
	private GetWorkActualOfWorkInfo002 getWorkRecord;

	@Inject
	private GetScheduleOfWorkInfo002 getScheduleOfWorkInfo002;

	public List<WorkScheduleWorkInforDto> getDataScheduleAndAactualOfWorkInfo(DisplayInWorkInfoInput param) {
		// lay data Schedule
		List<WorkScheduleWorkInforDto> listDataSchedule = getScheduleOfWorkInfo002.getDataScheduleOfWorkInfo(param);

		if (param.actualData) {
			// lay data Daily
			List<WorkScheduleWorkInforDto> listDataDaily = getWorkRecord.getDataActualOfWorkInfo(param);
			
			for (WorkScheduleWorkInforDto ds : listDataSchedule) {
				listDataDaily.stream().filter(c -> c.employeeId.equals(ds.employeeId) && c.date.equals(ds.date)).findFirst()
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

		return listDataSchedule;
	}
}
