package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceByTimezoneDeletion.Require;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;

/**
 * 
 * @author tutt
 *
 */
@RunWith(JMockit.class)
public class AttendanceByTimezoneDeletionTest {

	@Injectable
	private Require require;

	@Test
	public void getter() {
		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.COMPLETE);

		NtsAssert.invokeGetters(deletion);
	}

	// [1] 勤怠情報を削除する 該当する応援勤務枠NOに該当する日別実績の応援作業別勤怠を削除する
	@Test
	public void deleteAttendanceInfo() {
		List<Integer> itemIds = new ArrayList<>();
		itemIds.add(1);
		itemIds.add(2);
		itemIds.add(3);

		new Expectations() {
			{
				require.deleteBySupFrameNo(anyString, GeneralDate.today(), SupportFrameNo.of(1));

				require.getAttendanceItemIds(SupportFrameNo.of(1));
				result = itemIds;

				require.deleteByListItemId(anyString, GeneralDate.today(), itemIds);
			}
		};

		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.COMPLETE);

		List<AtomTask> result = deletion.deleteAttendanceInfo(require, "sId", GeneralDate.today());

		NtsAssert.atomTask(() -> result.get(0),
				any -> require.deleteBySupFrameNo("sId", GeneralDate.today(), SupportFrameNo.of(1)));
		NtsAssert.atomTask(() -> result.get(1), any -> require.deleteByListItemId("sId", GeneralDate.today(), itemIds));
	}

	// [2] 勤怠項目一覧を取得する
	@Test
	public void getItemIds() {

		List<Integer> itemIds1 = new ArrayList<>();
		itemIds1.add(1);
		itemIds1.add(2);
		itemIds1.add(3);

		new Expectations() {
			{
				require.getAttendanceItemIds(SupportFrameNo.of(1));
				result = itemIds1;
			}
		};

		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.COMPLETE);

		List<Integer> expectedItemIds = new ArrayList<>();
		expectedItemIds.add(1);
		expectedItemIds.add(2);
		expectedItemIds.add(3);

		List<Integer> actualItemIds = deletion.getItemIds(require);
		assertThat(actualItemIds).isEqualTo(expectedItemIds);

	}

	// [3] 勤怠情報を削除するかどうか判断する
	@Test
	public void isNeedDeletingAttInfo1() {
		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.COMPLETE);

		boolean actual = deletion.isNeedDeletingAttInfo();
		assertTrue(actual);

	}

	@Test
	public void isNeedDeletingAttInfo2() {
		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.OVERWRITE);

		boolean actual = deletion.isNeedDeletingAttInfo();
		assertFalse(actual);

	}

	// [4] 勤怠情報を削除するかどうか判断する
	@Test
	public void isNeedDeletingEditedStatus1() {
		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.OVERWRITE);

		boolean actual = deletion.isNeedDeletingEditedStatus();
		assertTrue(actual);

	}

	@Test
	public void isNeedDeletingEditedStatus2() {
		AttendanceByTimezoneDeletion deletion = new AttendanceByTimezoneDeletion(SupportFrameNo.of(1),
				AttendanceDeletionStatusEnum.COMPLETE);

		boolean actual = deletion.isNeedDeletingEditedStatus();
		assertFalse(actual);

	}

}
