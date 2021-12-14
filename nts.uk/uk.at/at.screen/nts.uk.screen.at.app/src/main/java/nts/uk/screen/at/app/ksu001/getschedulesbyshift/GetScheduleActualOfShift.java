/**
 * 
 */
package nts.uk.screen.at.app.ksu001.getschedulesbyshift;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.screen.at.app.ksu001.getworkactualshift.GetActualOfShift;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.GetScheduleOfShift;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftParam;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftResult;

/**
 * @author laitv
 * <<ScreenQuery>> 予定・実績をシフトで取得する
 *
 */
@Stateless
public class GetScheduleActualOfShift {
	
	@Inject
	private GetScheduleOfShift getWorkScheduleShift;
	@Inject
	private GetActualOfShift getActualOfShift;
	
	public SchedulesbyShiftDataResult getData(SchedulesbyShiftParam param) {
		
		// step 1 call ScreenQuery 勤務予定（シフト）を取得する
		ScheduleOfShiftParam param1 = new ScheduleOfShiftParam(param.listShiftMasterNotNeedGetNew, param.listSid, param.startDate, param.endDate);
		ScheduleOfShiftResult resultStep51 = getWorkScheduleShift.getWorkScheduleShift(param1);
		List<ScheduleOfShiftDto> listWorkScheduleShift = resultStep51.listWorkScheduleShift;
		
		if (param.getActualData) {
			// lấy data thực tế
			// call ScreenQuery 勤務実績（シフト）を取得する
			ScheduleOfShiftParam param2 = new ScheduleOfShiftParam(resultStep51.shiftMasterWithWorkStyleLst, param.listSid, param.startDate, param.endDate);
			ScheduleOfShiftResult resultStep52 =  getActualOfShift.getActualOfShift(param2);
			List<ScheduleOfShiftDto> listDataDailyShift = resultStep52.listWorkScheduleShift;
			// merge
			List<ScheduleOfShiftDto> listToRemove = new ArrayList<ScheduleOfShiftDto>();
			List<ScheduleOfShiftDto> listToAdd = new ArrayList<ScheduleOfShiftDto>();
			for (ScheduleOfShiftDto dataSchedule : listWorkScheduleShift) {
				String sid = dataSchedule.employeeId;
				GeneralDate date = dataSchedule.date;
				Optional<ScheduleOfShiftDto> dataDaily = listDataDailyShift.stream().filter(data -> {
					if (data.employeeId.equals(sid) && data.date.equals(date) && data.date.before(GeneralDate.today()))
						return true;
					return false;
				}).findFirst();
				if (dataDaily.isPresent()) {
					listToRemove.add(dataSchedule);
					listToAdd.add(dataDaily.get());
				}
			}
			listWorkScheduleShift.removeAll(listToRemove);
			listWorkScheduleShift.addAll(listToAdd);
			
			return new SchedulesbyShiftDataResult(listWorkScheduleShift , resultStep52.shiftMasterWithWorkStyleLst);
		}
		return new SchedulesbyShiftDataResult(listWorkScheduleShift , resultStep51.shiftMasterWithWorkStyleLst);
	}
}
