package nts.uk.ctx.at.record.dom.daily;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.employeeworkway.EmployeeWorkingStatus;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 予定管理区分に応じて日別実績を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.勤務実績.日別実績.予定管理区分に応じて日別実績を取得する
 * @author Hieult
 *
 */
public class GetDailyRecordByScheduleManagementService {

	/**
	 * 取得する
	 * @param require Require
	 * @param employeeIds 社員IDリスト
	 * @param period 期間
	 * @return Map<社員の就業状態, Optional<日別勤怠(Work)>>
	 */
	public static Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> get(Require require, List<String> employeeIds, DatePeriod period) {

		Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> map = new HashMap<>();
		employeeIds.stream().forEach( employeeId -> {
			long startTime = System.nanoTime();

			// 社員別に取得する
			map.putAll( GetDailyRecordByScheduleManagementService.getByEmployee(require, employeeId, period) );

			long endTime = System.nanoTime();
			long duration = (endTime - startTime) / 1000000; // ms;
			System.out.println("employee: " + duration + "ms");
		});

		return map;

	}

	/**
	 * 	社員別に取得する
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param period 期間
	 * @return Map<社員の就業状態, Optional<日別勤怠(Work)>>
	 */
	private static Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> getByEmployee(Require require, String employeeID, DatePeriod period) {

		Map<EmployeeWorkingStatus, Optional<IntegrationOfDaily>> map = new HashMap<>();
		period.stream().forEach( date -> {

			// 社員の就業状態を取得する
			EmployeeWorkingStatus status = EmployeeWorkingStatus.create(require, employeeID, date);
			if(!status.getWorkingStatus().needCreateWorkSchedule()) {
				// 勤務予定が必要ない⇒empty
				map.put(status, Optional.empty());
				return;
			}

			// 日別実績を取得する
			map.put(status, require.getDailyResults(employeeID, date));

		});

		return map;

	}



	public static interface Require extends EmployeeWorkingStatus.Require {
		/**
		 * 日別実績を取得する
		 * @param empId 社員ID
		 * @param date 年月日
		 * @return 日別実績
		 */
		Optional<IntegrationOfDaily> getDailyResults(String empId, GeneralDate date);
	}

}
