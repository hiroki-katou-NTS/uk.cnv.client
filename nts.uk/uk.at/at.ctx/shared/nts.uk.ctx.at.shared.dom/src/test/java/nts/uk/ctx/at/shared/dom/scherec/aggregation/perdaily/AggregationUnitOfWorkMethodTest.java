package nts.uk.ctx.at.shared.dom.scherec.aggregation.perdaily;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendanceHelper;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.WorkInfoOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterHelper;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;

public class AggregationUnitOfWorkMethodTest {

	@Injectable
	AggregationUnitOfWorkMethod.Require require;

	@Test
	public void testGetWorkMethod_WorkTime_empty() {

		WorkInformation workInfo = new WorkInformation(new WorkTypeCode("work-type-code"), null );
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getData(workInfo);

		Optional<String> result = AggregationUnitOfWorkMethod.WORK_TIME.getWorkMethod(require, workInfoOfDailyAttendance);

		assertThat( result ).isEmpty();

	}

	@Test
	public void testGetWorkMethod_WorkTime_isPresent() {

		WorkInformation workInfo = new WorkInformation(new WorkTypeCode("work-type-code"), new WorkTimeCode("work-time-code") );
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getData(workInfo);

		Optional<String> result = AggregationUnitOfWorkMethod.WORK_TIME.getWorkMethod(require, workInfoOfDailyAttendance);

		assertThat( result.get() ).isEqualTo("work-time-code");

	}

	@Test
	public void testGetWorkMethod_Shift_empty() {

		WorkInformation workInfo = new WorkInformation(new WorkTypeCode("work-type-code"), null );
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getData(workInfo);

		new Expectations() {{
			require.getShiftMaster(workInfo);
			// empty
		}};

		Optional<String> result = AggregationUnitOfWorkMethod.SHIFT.getWorkMethod(require, workInfoOfDailyAttendance);

		assertThat( result ).isEmpty();

	}

	@Test
	public void testGetWorkMethod_Shift_isPresent() {

		WorkInformation workInfo = new WorkInformation(new WorkTypeCode("work-type-code"), null );
		WorkInfoOfDailyAttendance workInfoOfDailyAttendance = WorkInfoOfDailyAttendanceHelper.getData(workInfo);

		new Expectations() {{
			require.getShiftMaster(workInfo);
			result = Optional.of( ShiftMasterHelper.createDummyWithCode("shift-code") );
		}};

		Optional<String> result = AggregationUnitOfWorkMethod.SHIFT.getWorkMethod(require, workInfoOfDailyAttendance);

		assertThat( result.get() ).isEqualTo("shift-code");

	}

}
