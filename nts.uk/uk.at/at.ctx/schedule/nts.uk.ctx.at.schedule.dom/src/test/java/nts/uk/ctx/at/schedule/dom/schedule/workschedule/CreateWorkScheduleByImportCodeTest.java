package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.Test;

import mockit.Expectations;
import mockit.Injectable;
import nts.arc.error.BusinessException;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;

public class CreateWorkScheduleByImportCodeTest {

	@Injectable
	private CreateWorkScheduleByImportCode.Require require;

	@Test
	public void testCreate_Msg_2183 () {

		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.ymd(2021, 5, 1);

		new Expectations() {{

			require.isWorkScheduleExisted(employeeId, date);
			result = true;

		}};

		ResultOfRegisteringWorkSchedule result = CreateWorkScheduleByImportCode.create(
				require,
				employeeId,
				date,
				new ShiftMasterImportCode("import-001"),
				false);

		assertThat( result.isHasError() ).isTrue();
		assertThat( result.getAtomTask() ).isEmpty();
		assertThat( result.getErrorInformation() )
			.extracting(
						e -> e.getEmployeeId(),
						e -> e.getDate(),
						e -> e.getAttendanceItemId(),
						e -> e.getErrorMessage() )
			.containsOnly(
					tuple(
						employeeId,
						date,
						Optional.empty(),
						new BusinessException("Msg_2183").getMessage() ));

	}

	@Test
	public void testCreate_Msg_1705 () {

		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		ShiftMasterImportCode importCode = new ShiftMasterImportCode("import-001");

		new Expectations() {{

			require.isWorkScheduleExisted(employeeId, date);
			// result = false

			require.getShiftMaster(importCode);
			// result = Optional.empty();

		}};

		ResultOfRegisteringWorkSchedule result = CreateWorkScheduleByImportCode.create(
				require,
				employeeId,
				date,
				importCode,
				false);

		assertThat( result.isHasError() ).isTrue();
		assertThat( result.getAtomTask() ).isEmpty();
		assertThat( result.getErrorInformation() )
			.extracting(
						e -> e.getEmployeeId(),
						e -> e.getDate(),
						e -> e.getAttendanceItemId(),
						e -> e.getErrorMessage() )
			.containsOnly(
					tuple(
						employeeId,
						date,
						Optional.empty(),
						new BusinessException("Msg_1705").getMessage() ));

	}

	@Test
	public void testCreate_Exception_in_WorkSchedule_dot_create (@Injectable ShiftMaster shiftMaster) {

		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		ShiftMasterImportCode importCode = new ShiftMasterImportCode("import-001");

		new Expectations(WorkSchedule.class) {{

			require.isWorkScheduleExisted(employeeId, date);
			// result = false

			require.getShiftMaster(importCode);
			result = Optional.of(shiftMaster);

			WorkSchedule.create(require, employeeId, date, shiftMaster);
			result = new BusinessException("message_id");

		}};

		ResultOfRegisteringWorkSchedule result = CreateWorkScheduleByImportCode.create(
				require,
				employeeId,
				date,
				importCode,
				false);

		assertThat( result.isHasError() ).isTrue();
		assertThat( result.getAtomTask() ).isEmpty();
		assertThat( result.getErrorInformation() )
			.extracting(
						e -> e.getEmployeeId(),
						e -> e.getDate(),
						e -> e.getAttendanceItemId(),
						e -> e.getErrorMessage() )
			.containsOnly(
					tuple(
						employeeId,
						date,
						Optional.empty(),
						new BusinessException("message_id").getMessage() ));

	}

	@Test
	public void testCreate_insert (
			@Injectable ShiftMaster shiftMaster,
			@Injectable WorkSchedule newWorkSchedule,
			@Injectable WorkSchedule correctedResult) {

		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		ShiftMasterImportCode importCode = new ShiftMasterImportCode("import-001");

		new Expectations(WorkSchedule.class) {{

			require.isWorkScheduleExisted(employeeId, date);
			// result = false

			require.getShiftMaster(importCode);
			result = Optional.of(shiftMaster);

			WorkSchedule.create(require, employeeId, date, shiftMaster);
			result = newWorkSchedule;

			require.correctWorkSchedule(newWorkSchedule);
			result = correctedResult;

		}};

		ResultOfRegisteringWorkSchedule result = CreateWorkScheduleByImportCode.create(
				require,
				employeeId,
				date,
				importCode,
				false);

		assertThat( result.isHasError() ).isFalse();
		assertThat( result.getErrorInformation() ).isEmpty();
		NtsAssert.atomTask(
				() -> result.getAtomTask().get() ,
				any -> require.insertWorkSchedule( correctedResult ),
				any -> require.registerTemporaryData( employeeId, date )
				);

	}

	@Test
	public void testCreate_update (
			@Injectable ShiftMaster shiftMaster,
			@Injectable WorkSchedule newWorkSchedule,
			@Injectable WorkSchedule correctedResult) {

		String employeeId = "employeeId";
		GeneralDate date = GeneralDate.ymd(2021, 5, 1);
		ShiftMasterImportCode importCode = new ShiftMasterImportCode("import-001");

		new Expectations(WorkSchedule.class) {{

			require.isWorkScheduleExisted(employeeId, date);
			result = true;

			require.getShiftMaster(importCode);
			result = Optional.of(shiftMaster);

			WorkSchedule.create(require, employeeId, date, shiftMaster);
			result = newWorkSchedule;

			require.correctWorkSchedule(newWorkSchedule);
			result = correctedResult;

		}};

		ResultOfRegisteringWorkSchedule result = CreateWorkScheduleByImportCode.create(
				require,
				employeeId,
				date,
				importCode,
				true );

		assertThat( result.isHasError() ).isFalse();
		assertThat( result.getErrorInformation() ).isEmpty();
		NtsAssert.atomTask(
				() -> result.getAtomTask().get() ,
				any -> require.updateWorkSchedule( correctedResult ),
				any -> require.registerTemporaryData( employeeId, date )
				);

	}

}
