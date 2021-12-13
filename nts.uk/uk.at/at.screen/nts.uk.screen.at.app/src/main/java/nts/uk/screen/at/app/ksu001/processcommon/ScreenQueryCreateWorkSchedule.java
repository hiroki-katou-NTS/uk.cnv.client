package nts.uk.screen.at.app.ksu001.processcommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.schedule.dom.schedule.workschedule.WorkSchedule;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
/**
 * 勤務予定（勤務情報）dtoを作成する
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU001_個人スケジュール修正(職場別).個人別と共通の処理
 * @author hoangnd
 *
 */
@Stateless
public class ScreenQueryCreateWorkSchedule {

	
	@Inject
	private CreateWorkScheduleWorkInfor createWorkScheduleWorkInfor;
	
	@Inject
	private CreateWorkScheduleWorkInforBase createWorkScheduleWorkInforBase;
	/**
	 * 
	 * @param mngStatusAndWScheMap Map<社員の予定管理状態, Optional<勤務予定>>
	 * @param map Map<社員の予定管理状態, Optional<日別勤怠(Work)>>
	 * @param isAchievement 実績も取得するか
	 * @return List<勤務予定（勤務情報）dto>
	 */
	public List<WorkScheduleWorkInforDto> get(
			Map<EmployeeWorkingStatus, Optional<WorkSchedule>> mngStatusAndWScheMap,
			Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> map,
			Boolean getActualData
			) {
		
		// 1: 作成する(Map<社員の予定管理状態, Optional<勤務予定>>)
		List<WorkScheduleWorkInforDto> workScheduleWorkInfor1 = 
					createWorkScheduleWorkInfor.getDataScheduleOfWorkInfo(mngStatusAndWScheMap);
		
		// 2実績も取得するか == true
		if (getActualData) {
			// 2.1: 作成する(Map<社員の予定管理状態, Optional<日別勤怠(Work)>>)
			List<WorkScheduleWorkInforDto> workScheduleWorkInfor2 =
						createWorkScheduleWorkInforBase.getDataScheduleOfWorkInfo(map);
			
			// 2.2 
			List<WorkScheduleWorkInforDto> list1 = 
					CollectionUtil.isEmpty(workScheduleWorkInfor2) ? new ArrayList<WorkScheduleWorkInforDto>() : workScheduleWorkInfor2;
			
			List<WorkScheduleWorkInforDto> list2 = workScheduleWorkInfor1.stream()
							   .filter(x -> !workScheduleWorkInfor2.stream()
									   .anyMatch(y -> y.getDate().equals(x.getDate()) && y.getEmployeeId().equals(x.getEmployeeId())))
							   .collect(Collectors.toList());
			list1.addAll(list2);
			return list1;
				  
		}
		return workScheduleWorkInfor1;
		
	}
	

}
