package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetEmpCanReferService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;

/**
 * 社員を指定して同日に出勤する社員を取得する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.勤務予定.勤務予定.社員を指定して同日に出勤する社員を取得する
 * @author lan_lt
 */
public class GetWorkTogetherEmpOnDayBySpecEmpService {

	/**
	 * 取得する
	 * @param require
	 * @param sid 社員ID
	 * @param baseDate 基準日
	 * @return
	 */
	public static List<String> get(Require require, String sid, GeneralDate baseDate) {
		val targetOrg = GetTargetIdentifiInforService.get(require, baseDate, sid);
		val targetEmps = GetEmpCanReferService.getByOrg(require, sid, baseDate, DatePeriod.oneDay(baseDate), targetOrg);
		val workSchedules = require.getWorkSchedule(targetEmps, baseDate);
		val workTogetherList = workSchedules.stream()
				.filter(workSchedule -> workSchedule.getWorkInfo().isAttendanceRate(require))
				.collect(Collectors.toList());
		return workTogetherList.stream()
				.map(workTogether -> workTogether.getEmployeeID())
				.filter(workTogether -> !workTogether.equals(sid))
				.collect(Collectors.toList());
	}



	public static interface Require
			extends	GetTargetIdentifiInforService.Require
				,	GetEmpCanReferService.Require
				,	WorkInfoOfDailyAttendance.Require {

		/**
		 * 勤務予定を取得する
		 * @param sids 社員IDリスト
		 * @param baseDate 年月日
		 * @return
		 */
		List<WorkSchedule> getWorkSchedule(List<String> sids, GeneralDate baseDate);

	}

}
