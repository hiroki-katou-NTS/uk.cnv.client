package nts.uk.screen.at.app.ksu001.processcommon.nextorderdschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.WorkStyle;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.screen.at.app.ksu001.displayinshift.ShiftMasterMapWithWorkStyle;
import nts.uk.screen.at.app.ksu001.getworkscheduleshift.ScheduleOfShiftDto;
import nts.uk.screen.at.app.ksu001.processcommon.CreateWorkScheduleShift;
import nts.uk.screen.at.app.ksu001.processcommon.CreateWorkScheduleShiftBase;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleShiftBaseResult;
import nts.uk.screen.at.app.ksu001.processcommon.WorkScheduleShiftResult;

/**
 * 勤務予定（シフト）dtoを作成する
 * @author hoangnd
 * todo
 */
@Stateless
public class ScreenQueryWorkScheduleShift {

	
	
	@Inject
	private CreateWorkScheduleShift createWorkScheduleShift;
	
	@Inject
	private CreateWorkScheduleShiftBase createWorkScheduleShiftBase;
	/**
	 * 
	 * @param listShiftMasterNotNeedGetNew 新たに取得する必要のないシフト一覧：List<シフトマスタ>
	 * @param mngStatusAndWScheMap 管理状態と勤務予定Map：Map<社員の予定管理状態, Optional<勤務予定>>
	 * @param mapDataDaily 管理状態と勤務実績Map：Map<社員の予定管理状態, Optional<日別勤怠(Work)>>
	 * @param isAchievement 実績も取得するか：boolean
	 * @return
	 */
	public WorkScheduleShiftBaseResult create(
			List<ShiftMasterMapWithWorkStyle> listShiftMasterNotNeedGetNew,
			Map<EmployeeWorkingStatus, Optional<WorkSchedule>> mngStatusAndWScheMap,
			Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> mapDataDaily,
			Boolean getActualData
			) {
		WorkScheduleShiftBaseResult output = new WorkScheduleShiftBaseResult(
				Collections.emptyList(),
				new HashMap<ShiftMaster, Optional<WorkStyle>>()
				);
		// 1: call
		WorkScheduleShiftResult workScheduleShiftResult = 
				createWorkScheduleShift.getWorkScheduleShift(
							mngStatusAndWScheMap,
							listShiftMasterNotNeedGetNew);
		output.setListWorkScheduleShift(workScheduleShiftResult.getListWorkScheduleShift());
		output.setMapShiftMasterWithWorkStyle(workScheduleShiftResult.getMapShiftMasterWithWorkStyle());
		// 2 実績も取得するか == true
		if (getActualData) {
			// 2.1: <call>
			WorkScheduleShiftBaseResult workScheduleShiftBaseResult = 
					createWorkScheduleShiftBase.getWorkScheduleShiftBase(
							mapDataDaily,
							listShiftMasterNotNeedGetNew);
			// 2.2 
			/**
			 * 予定と実績のList<勤務予定（シフト）dto>をMargeする。
				実績が存在する日（社員ID、年月日が一致するデータが存在する場合）は、
				取得した予定を実績で上書きしてListを作る。 
			 */
			
			List<ScheduleOfShiftDto> achievements = workScheduleShiftBaseResult.getListWorkScheduleShift();
			
			Map<ShiftMaster,Optional<WorkStyle>> achievementShiftMaster = workScheduleShiftBaseResult.getMapShiftMasterWithWorkStyle();
			
			List<ScheduleOfShiftDto> schedules = workScheduleShiftResult.getListWorkScheduleShift();
			
			Map<ShiftMaster,Optional<WorkStyle>> schedulesShiftMaster = workScheduleShiftResult.getMapShiftMasterWithWorkStyle();
			
			List<ScheduleOfShiftDto> list1 = CollectionUtil.isEmpty(achievements) ? new ArrayList<ScheduleOfShiftDto>() : achievements;
									
			List<ScheduleOfShiftDto> list2 = schedules.stream()
					.filter(x -> !achievements.stream()
										  .anyMatch(y -> y.getDate().equals(x.getDate()) && y.getEmployeeId().equals(x.getEmployeeId())))
					.collect(Collectors.toList());
			
			list1.addAll(list2);
			Map<ShiftMaster,Optional<WorkStyle>> map = new HashMap<ShiftMaster, Optional<WorkStyle>>();
			achievementShiftMaster.forEach((k1, v1) -> {
				schedulesShiftMaster.forEach((k2, v2) -> {
					if (k2.getShiftMasterCode().v().equals(k1.getShiftMasterCode().v())) {
						map.put(k2, v2);						
					} else {
						map.put(k1, v1);
					}
				});
			});
			output.setListWorkScheduleShift(list1);
			output.setMapShiftMasterWithWorkStyle(map);
								  
			
		}
		
		return output;
		
	}
}
