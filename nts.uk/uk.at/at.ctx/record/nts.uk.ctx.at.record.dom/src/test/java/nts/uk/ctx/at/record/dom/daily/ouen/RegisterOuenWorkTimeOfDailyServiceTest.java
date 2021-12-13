package nts.uk.ctx.at.record.dom.daily.ouen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeOfDailyAttendance;

/**
 * 
 * @author chungnt
 *
 */
@RunWith(JMockit.class)
public class RegisterOuenWorkTimeOfDailyServiceTest {

	@Injectable
	private RegisterOuenWorkTimeOfDailyService.Require require;

	private String empId = "empId";
	private GeneralDate ymd = GeneralDate.today();
	private List<OuenWorkTimeOfDailyAttendance> ouenTimes = new ArrayList<>();

	OuenWorkTimeOfDailyAttendance quen = OuenWorkTimeOfDailyAttendance.create(1, null, null, null, null);

	// $実績の作業時間 notIsPresent
	@Test
	public void testInsert() {

		OuenWorkTimeOfDaily ouenWorkTimeOfDailyNew = OuenWorkTimeOfDaily.create(empId, ymd, ouenTimes);

		new Expectations() {
			{
				require.getOuenWorkTimeOfDaily(empId, ymd);
			}
		};

		AtomTask atomtask = RegisterOuenWorkTimeOfDailyService.register(require, empId, ymd, ouenTimes);
		NtsAssert.atomTask(() -> atomtask, any -> require.insert(ouenWorkTimeOfDailyNew));
	}

	// $実績の作業時間 IsPresent
	// $更新時間 = $実績の作業時間.変更する(作業時間)
	@Test
	public void testDelete() {

		OuenWorkTimeOfDaily ouenWorkTimeOfDailyNew = OuenWorkTimeOfDaily.create(empId, ymd, ouenTimes);

		new Expectations() {
			{
				require.getOuenWorkTimeOfDaily(empId, ymd);
				result = Optional.of(ouenWorkTimeOfDailyNew);
			}
		};

		AtomTask atomtask = RegisterOuenWorkTimeOfDailyService.register(require, empId, ymd, ouenTimes);
		NtsAssert.atomTask(() -> atomtask, any -> require.delete(ouenWorkTimeOfDailyNew));
	}
	
	// $実績の作業時間 IsPresent
		// $更新時間 = $実績の作業時間.変更する(作業時間)
		@Test
		public void testUpdate() {
			this.ouenTimes.add(quen);
			this.ouenTimes.add(quen);

			OuenWorkTimeOfDaily ouenWorkTimeOfDailyNew = OuenWorkTimeOfDaily.create(empId, ymd, ouenTimes);

			new Expectations() {
				{
					require.getOuenWorkTimeOfDaily(empId, ymd);
					result = Optional.of(ouenWorkTimeOfDailyNew);
				}
			};

			AtomTask atomtask = RegisterOuenWorkTimeOfDailyService.register(require, empId, ymd, ouenTimes);
			NtsAssert.atomTask(() -> atomtask, any -> require.update(ouenWorkTimeOfDailyNew));
		}

}
