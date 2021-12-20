package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;


/**
 * 予定管理区分に応じて勤務予定を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.予定管理区分に応じて勤務予定を取得する
 * @author HieuLt
 *
 */
public class GetWorkScheduleByScheduleManagementService {

	/**
	 * 取得する
	 * @param require Require
	 * @param employeeIds 社員IDリスト
	 * @param datePeriod 期間
	 * @return Map<社員の就業状態, Optional<勤務予定>>
	 */
	public static Map<EmployeeWorkingStatus, Optional<WorkSchedule>> getScheduleManagement(Require require, List<String> employeeIds, DatePeriod period) {

		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> map = new HashMap<>();
		employeeIds.stream().forEach( employeeId -> {

			// 社員別に取得する
			map.putAll( GetWorkScheduleByScheduleManagementService.getByEmployee(require, employeeId, period) );

		});

		return  map;
	}

	/**
	 * 	社員別に取得する
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param period 期間
	 * @return Map<社員の就業状態, Optional<勤務予定>>
	 */
	private static Map<EmployeeWorkingStatus, Optional<WorkSchedule>> getByEmployee(Require require,String employeeID, DatePeriod period) {

		Map<EmployeeWorkingStatus, Optional<WorkSchedule>> map = new HashMap<>();
		period.stream().forEach( date -> {

			// 社員の就業状態を取得する
			EmployeeWorkingStatus status =  EmployeeWorkingStatus.create(require, employeeID, date);
			if(!status.getWorkingStatus().needCreateWorkSchedule()) {
				// 勤務予定が必要ない⇒empty
				 map.put(status, Optional.empty());
				 return;
			}

			// 勤務予定を取得する
			map.put(status, require.get(employeeID, date));

		});
		return map;
	}



	public static interface Require extends EmployeeWorkingStatus.Require {
		/**
		 * 勤務予定を取得する
		 * @param employeeID 社員ID
		 * @param ymd 年月日
		 * @return 勤務予定
		 */
		Optional<WorkSchedule> get(String employeeID, GeneralDate ymd);
	}

}
