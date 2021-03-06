package nts.uk.ctx.at.record.dom.service.event.overtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.util.value.Finally;
import nts.uk.ctx.at.record.dom.editstate.EditStateOfDailyPerformance;
import nts.uk.ctx.at.record.dom.editstate.repository.EditStateOfDailyPerformanceRepository;
import nts.uk.ctx.at.record.dom.service.event.common.CorrectEventConts;
import nts.uk.ctx.at.record.dom.workinformation.service.reflectprocess.WorkUpdateService;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeOfExistMinus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryMidNightTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.ExcessOfStatutoryTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.editstate.EditStateSetting;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.holidayworktime.HolidayWorkFrameTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.FlexTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.overtimehours.clearovertime.OverTimeOfDaily;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailycalprocess.calculation.timezone.outsideworktime.OverTimeFrameTime;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
@Stateless
public class OvertimeOfDailyService {
	@Inject
	private WorkUpdateService recordUpdate;
	@Inject
	private EditStateOfDailyPerformanceRepository editStateDaily;
	/**
	 * 申請された時間を補正する
	 * @param working
	 * @param cachedWorkType
	 * @return
	 */
	public IntegrationOfDaily correct(IntegrationOfDaily working,Optional<WorkType> cachedWorkType, boolean isSaveDirect){
		if(!cachedWorkType.isPresent() 
				|| !working.getAttendanceTimeOfDailyPerformance().isPresent()) {
			return working;
		}
		List<Integer> itemIdList = new ArrayList<>();
		ExcessOfStatutoryTimeOfDaily timeOfDaily = working.getAttendanceTimeOfDailyPerformance().get().getActualWorkingTimeOfDaily().getTotalWorkingTime().getExcessOfStatutoryTimeOfDaily();
		if(cachedWorkType.get().getDailyWork().isHolidayWork()) {
			itemIdList.addAll(recordUpdate.lstPreOvertimeItem());
			itemIdList.addAll(recordUpdate.lstAfterOvertimeItem());
			itemIdList.addAll(recordUpdate.lstTransferTimeOtItem());
			itemIdList.add(555); //フレックス時間．事前申請時間
			itemIdList.add(556); //フレックス時間．フレックス時間
		} else if(cachedWorkType.get().getDailyWork().getDecidionAttendanceHolidayAttr()){
			itemIdList.addAll(recordUpdate.lstPreOvertimeItem());
			itemIdList.addAll(recordUpdate.lstAfterOvertimeItem());
			itemIdList.add(555); //フレックス時間．事前申請時間
			itemIdList.add(556); //フレックス時間．フレックス時間
			itemIdList.addAll(recordUpdate.lstPreWorktimeFrameItem());
			itemIdList.addAll(recordUpdate.lstAfterWorktimeFrameItem());
			itemIdList.add(565); //事前所定外深夜時間
			itemIdList.add(563); //所定外深夜時間
            //休日出勤申請：休憩時間しか反映してない
            itemIdList.addAll(recordUpdate.lstScheBreakStartTime());
            itemIdList.addAll(recordUpdate.lstScheBreakEndTime());
            itemIdList.addAll(CorrectEventConts.START_BREAK_TIME_CLOCK_ITEMS);
            itemIdList.addAll(CorrectEventConts.END_BREAK_TIME_CLOCK_ITEMS);
            //替時間(休出)の反映、をクリアする
    		itemIdList.addAll(recordUpdate.lstTranfertimeFrameItem());
		} else {
			itemIdList.addAll(recordUpdate.lstPreWorktimeFrameItem());
			itemIdList.addAll(recordUpdate.lstAfterWorktimeFrameItem());
			 //休日出勤申請：休憩時間しか反映してない
			itemIdList.addAll(recordUpdate.lstScheBreakStartTime());
            itemIdList.addAll(recordUpdate.lstScheBreakEndTime());
            itemIdList.addAll(CorrectEventConts.START_BREAK_TIME_CLOCK_ITEMS);
            itemIdList.addAll(CorrectEventConts.END_BREAK_TIME_CLOCK_ITEMS);
          //替時間(休出)の反映、をクリアする
    		itemIdList.addAll(recordUpdate.lstTranfertimeFrameItem());
		}
		
		//編集状態を取得する
		List<EditStateOfDailyPerformance> editItemReflect = editStateDaily.findByEditState(working.getEmployeeId(),
				working.getYmd(),
				itemIdList,
				EditStateSetting.REFLECT_APPLICATION);
		itemIdList = editItemReflect.stream().map(x -> x.getEditState().getAttendanceItemId()).collect(Collectors.toList());
		if(itemIdList.isEmpty()) {
			return working;
		}
		//事前残業
		List<Integer> lstPreOt = new ArrayList<>();
		itemIdList.stream().forEach(a -> {
			if(recordUpdate.lstPreOvertimeItem().contains(a)) {
				lstPreOt.add(recordUpdate.lstPreOvertimeItem().indexOf(a));
			}
		});
		//事後残業
		List<Integer> lstAfterOt = new ArrayList<>();
		itemIdList.stream().forEach(a -> {
			if(recordUpdate.lstAfterOvertimeItem().contains(a)) {
				lstAfterOt.add(recordUpdate.lstAfterOvertimeItem().indexOf(a));
			}
		});
		//残業枠時間．振替時間
		List<Integer> lstTranferOT = new ArrayList<>();
		itemIdList.stream().forEach(a -> {
			if(recordUpdate.lstTransferTimeOtItem().contains(a)) {
				lstTranferOT.add(recordUpdate.lstTransferTimeOtItem().indexOf(a));
			}
		});
		
		timeOfDaily.getOverTimeWork().ifPresent(x -> {
			List<OverTimeFrameTime> lstOvertime = x.getOverTimeWorkFrameTime();
			lstOvertime.stream().forEach(y -> {
				if(lstPreOt.contains(lstOvertime.indexOf(y))) {
					y.setBeforeApplicationTime(new AttendanceTime(0));	
				}
				if(lstAfterOt.contains(lstOvertime.indexOf(y))) {
					y.getOverTimeWork().setTime(new AttendanceTime(0));
				}
				if(lstTranferOT.contains(lstOvertime.indexOf(y))) {
					y.getTransferTime().setTime(new AttendanceTime(0));
				}
			});
		});
		//事前フレックス
		if(itemIdList.contains(555)) {
			Optional<OverTimeOfDaily> optOverTimeOfDaily = timeOfDaily.getOverTimeWork();
			optOverTimeOfDaily.ifPresent(z -> {
				z.setFlexTime(new FlexTime(z.getFlexTime().getFlexTime(), new AttendanceTime(0)));
			});
		}
		//フレックス
		if(itemIdList.contains(556)) {
			Optional<OverTimeOfDaily> optOverTimeOfDaily = timeOfDaily.getOverTimeWork();
			optOverTimeOfDaily.ifPresent(z -> {
				z.getFlexTime().getFlexTime().replaceTimeAndCalcDiv(new AttendanceTimeOfExistMinus(0));
			});
		}
		
		//事前休日出勤時間
		List<Integer> lstPreHolidayWork = new ArrayList<>();
		itemIdList.stream().forEach(a -> {
			if(recordUpdate.lstPreWorktimeFrameItem().contains(a)) {
				lstPreHolidayWork.add(recordUpdate.lstPreWorktimeFrameItem().indexOf(a));
			}
		});
		//休日出勤時間
		List<Integer> lstHolidayWork = new ArrayList<>();
		itemIdList.stream().forEach(a -> {
			if(recordUpdate.lstAfterWorktimeFrameItem().contains(a)) {
				lstHolidayWork.add(recordUpdate.lstAfterWorktimeFrameItem().indexOf(a));
			}
		});
		//休出枠時間．振替時間
		List<Integer> lstTranferHw = new ArrayList<>();
		itemIdList.stream().forEach(a -> {
			if(recordUpdate.lstTranfertimeFrameItem().contains(a)) {
				lstTranferHw.add(recordUpdate.lstTranfertimeFrameItem().indexOf(a));
			}
		});
		timeOfDaily.getWorkHolidayTime().ifPresent(x -> {
			List<HolidayWorkFrameTime> lstHoliday = x.getHolidayWorkFrameTime();
			lstHoliday.stream().forEach(y -> {
				if(lstPreHolidayWork.contains(lstHoliday.indexOf(y))) {
					y.setBeforeApplicationTime(Finally.of(new AttendanceTime(0)));	
				}
				if(lstHolidayWork.contains(lstHoliday.indexOf(y))) {
					if(y.getHolidayWorkTime().isPresent()) {
						y.getHolidayWorkTime().get().setTime(new AttendanceTime(0));
					}
				}
				if(lstTranferHw.contains(lstHoliday.indexOf(y))) {
					if(y.getTransferTime().isPresent()) {
						y.getTransferTime().get().setTime(new AttendanceTime(0));
					}
				}
			});
		});
		//所定外深夜時間
		ExcessOfStatutoryMidNightTime exMidNightTime = timeOfDaily.getExcessOfStatutoryMidNightTime();
		if(itemIdList.contains(565)) {			
			ExcessOfStatutoryMidNightTime tmp = new ExcessOfStatutoryMidNightTime(exMidNightTime.getTime(), new AttendanceTime(0));
			timeOfDaily.setExcessOfStatutoryMidNightTime(tmp);
		}		
		if(itemIdList.contains(563)) {
			exMidNightTime.getTime().setTime(new AttendanceTime(0));
		}

		//attendanceTimeRepo.updateFlush(working.getAttendanceTimeOfDailyPerformance().get());
		//削除
		//editStateDaily.deleteByListItemId(working.getWorkInformation().getEmployeeId(), working.getWorkInformation().getYmd(), itemIdList);

		if(isSaveDirect){
			List<EditStateOfDailyPerformance> dailyPerformances = working.getEditState().stream().map(mapper-> new EditStateOfDailyPerformance(working.getEmployeeId(), working.getYmd(), mapper)).collect(Collectors.toList());
			List<Integer> lstDeleteItem = this.deleteItemEdit(dailyPerformances, itemIdList);
			if(!itemIdList.isEmpty()) {
				editStateDaily.deleteByListItemId(working.getEmployeeId(), working.getYmd(), lstDeleteItem);	
			}
		}
		//削除
//		List<EditStateOfDailyPerformance> editState = new ArrayList<>(working.getEditState());
//		itemIdList.stream().forEach(x -> {
//			working.getEditState().stream().forEach(y -> {
//				if(x == y.getAttendanceItemId()) {
//					editState.remove(y);
//				}
//			});
//		});
//		working.setEditState(editState);
//		if(isSaveDirect){
//			attendanceTimeRepo.updateFlush(working.getAttendanceTimeOfDailyPerformance().get());
//			editStateDaily.deleteByListItemId(working.getWorkInformation().getEmployeeId(), working.getWorkInformation().getYmd(), itemIdList);
//		}
		return working;
	}
	private List<Integer> deleteItemEdit(List<EditStateOfDailyPerformance> editState, List<Integer> deleteItem) {
		List<Integer> outputList = new ArrayList<>();
		if(editState.isEmpty() || deleteItem.isEmpty()) {
			return outputList;
		}	
		List<EditStateOfDailyPerformance> temp = new ArrayList<>(editState);
		for (EditStateOfDailyPerformance a : temp) {
			if(deleteItem.contains(a.getEditState().getAttendanceItemId())) {
				editState.remove(editState.indexOf(a));
				outputList.add(a.getEditState().getAttendanceItemId());
			}
		}
		return outputList;
	}
}
