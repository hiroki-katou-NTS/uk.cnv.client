package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskSchedule;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.configuration.DayOfWeek;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.CalculationState;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.NotUseAttribute;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetEmpCanReferService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

/**
 *
 * @author lan_lt
 *
 */

@RunWith(JMockit.class)
public class GetWorkTogetherEmpOnDayBySpecEmpServiceTest {

	@Injectable
	GetWorkTogetherEmpOnDayBySpecEmpService.Require require;

	/**
	 * input 社員ID = "sid_0"
	 *       List<組織に出勤する社員ID> = "sid_1", "sid_2", "sid_3"
	 *       sid_1、sid_2: 出勤です。
	 *       sid_3: 休日です。
	 * output: "sid_1", "sid_2"
	 *
	 */
	@SuppressWarnings("static-access")
	@Test
	public void getWorkTogetherEmployeeOnDay(@Mocked GetTargetIdentifiInforService targetOrgService
			, @Mocked GetEmpCanReferService empSameOrgService) {
		val sid = "sid_0";
		val baseDate = GeneralDate.today();
		val empSameOrgs = Arrays.asList("sid_1", "sid_2", "sid_3");
		val targetOrg= TargetOrgIdenInfor.creatIdentifiWorkplace("wkplaceId");
		val workInfoAttd1 = Helper.createWorkInfoOfDailyAttendance(new WorkInformation("01", "01"));
		val workInfoAttd2 = Helper.createWorkInfoOfDailyAttendance(new WorkInformation("02", "02"));
		val workInfoAttd3 = Helper.createWorkInfoOfDailyAttendance(new WorkInformation("03", "03"));
		val workSchedule1 = Helper.createWorkSchedule("sid_1", baseDate, workInfoAttd1);
		val workSchedule2 = Helper.createWorkSchedule("sid_2", baseDate, workInfoAttd2);
		val workSchedule3 = Helper.createWorkSchedule("sid_3", baseDate, workInfoAttd3);

		new Expectations(workSchedule1, workSchedule2, workSchedule3) {
			{
				targetOrgService.get(require, baseDate, sid);
				result = targetOrg;

				empSameOrgService.getByOrg(require, sid, baseDate, DatePeriod.oneDay(baseDate), targetOrg);
				result = empSameOrgs;

				require.getWorkSchedule(empSameOrgs, baseDate);
				result = Arrays.asList(workSchedule1, workSchedule2, workSchedule3);

				workSchedule1.getWorkInfo().isAttendanceRate(require);
				result = true;

				workSchedule2.getWorkInfo().isAttendanceRate(require);
				result = true;

				workSchedule3.getWorkInfo().isAttendanceRate(require);
				result = false;
			}

		};

		val result = GetWorkTogetherEmpOnDayBySpecEmpService.get(require, sid, baseDate);

		assertThat(result).containsExactly("sid_1", "sid_2");

	}

	public static class Helper{
		/**
		 *　勤務予定を作る
		 * @param sid 社員ID
		 * @param date 基準日
		 * @param workInfo 勤務情報
		 * @return
		 */
		public static WorkSchedule createWorkSchedule(String sid, GeneralDate date, WorkInfoOfDailyAttendance workInfo) {
			return new WorkSchedule(sid, date, ConfirmedATR.CONFIRMED
					, workInfo, null
					, null
					, Collections.emptyList()
					, TaskSchedule.createWithEmptyList()
					, Optional.empty()
					, Optional.empty()
					, Optional.empty()
					, Optional.empty());
		}

		/**
		 * 日別勤怠の勤務情報を作る
		 * @param recordInfo 勤務情報
		 * @return
		 */
		public static WorkInfoOfDailyAttendance createWorkInfoOfDailyAttendance(WorkInformation recordInfo) {
			return new WorkInfoOfDailyAttendance(
					recordInfo
					, CalculationState.Calculated
					, NotUseAttribute.Not_use
					, NotUseAttribute.Not_use
					, DayOfWeek.FRIDAY, Collections.emptyList(), Optional.empty());
		}
	}


}