/**
 * 
 */
package nts.uk.screen.at.app.ksu001.scheduleactualworkinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu001.displayinworkinformation.DisplayInWorkInfoParam;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleWorkInforDto;

/**
 * @author laitv
 * ScreenQuery : 予定・実績を勤務情報で取得する
 */
@Stateless
public class GetScheduleActualOfWorkInfo {
	
	@Inject
	private GetScheduleOfWorkInfo getScheduleOfWorkInfo;
	@Inject
	private GetWorkActualOfWorkInfo getWorkActualOfWorkInfo;
	
	public List<WorkScheduleWorkInforDto> getDataScheduleAndAactualOfWorkInfo(DisplayInWorkInfoParam param) {
		// lay data Schedule
		List<WorkScheduleWorkInforDto> listDataSchedule = getScheduleOfWorkInfo.getDataScheduleOfWorkInfo(param);
		
		if (param.getActualData) {
			// lay data Daily
			List<WorkScheduleWorkInforDto> listDataDaily = getWorkActualOfWorkInfo.getDataActualOfWorkInfo(param);
			// merge
			List<WorkScheduleWorkInforDto> listToRemove = new ArrayList<WorkScheduleWorkInforDto>();
			List<WorkScheduleWorkInforDto> listToAdd = new ArrayList<WorkScheduleWorkInforDto>();
			for (WorkScheduleWorkInforDto dataSchedule : listDataSchedule) {
				String sid = dataSchedule.employeeId;
				GeneralDate date = dataSchedule.date;
				Optional<WorkScheduleWorkInforDto> dataDaily = listDataDaily.stream().filter(data -> {
					if (data.employeeId.equals(sid) && data.date.equals(date) && data.date.before(GeneralDate.today()))
						return true;
					return false;
				}).findFirst();
				if (dataDaily.isPresent()) {
					listToRemove.add(dataSchedule);
					listToAdd.add(dataDaily.get());
				}
			}
			listDataSchedule.removeAll(listToRemove);
			listDataSchedule.addAll(listToAdd);
		}
		return listDataSchedule;
	}
}
