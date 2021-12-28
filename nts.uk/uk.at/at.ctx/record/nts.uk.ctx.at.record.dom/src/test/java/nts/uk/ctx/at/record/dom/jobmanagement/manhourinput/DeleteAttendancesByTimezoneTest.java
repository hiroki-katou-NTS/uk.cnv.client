package nts.uk.ctx.at.record.dom.jobmanagement.manhourinput;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;
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
public class DeleteAttendancesByTimezoneTest {
	
	@Injectable
	private Require require;

	@Test
	public void getter() {
		List<AttendanceByTimezoneDeletion> attendanceDeletionLst = new ArrayList<>();
		attendanceDeletionLst.add(new AttendanceByTimezoneDeletion(SupportFrameNo.of(1), AttendanceDeletionStatusEnum.COMPLETE));
		
		DeleteAttendancesByTimezone deletion = new DeleteAttendancesByTimezone("sId", GeneralDate.today(), attendanceDeletionLst);
		
		NtsAssert.invokeGetters(deletion);
	}
	
	// [1] 応援作業別勤怠を削除する
	// List<時間帯別勤怠の削除> is not empty
	@Test
	public void deleteAttendance1() {
		
		List<AttendanceByTimezoneDeletion> attendanceDeletionLst = new ArrayList<>();
		attendanceDeletionLst.add(new AttendanceByTimezoneDeletion(SupportFrameNo.of(1), AttendanceDeletionStatusEnum.COMPLETE));
		attendanceDeletionLst.add(new AttendanceByTimezoneDeletion(SupportFrameNo.of(2), AttendanceDeletionStatusEnum.OVERWRITE));
		
		DeleteAttendancesByTimezone deletion = new DeleteAttendancesByTimezone("sId", GeneralDate.today(), attendanceDeletionLst);
		
		List<AtomTask> result = deletion.deleteAttendance(require);
		
		for (AtomTask persist : result) {
			persist.run();
		}
		
		new Verifications() {{
			require.deleteBySupFrameNo("sId", GeneralDate.today(), SupportFrameNo.of(1));
			times = 1;
			
			require.deleteBySupFrameNo("sId", GeneralDate.today(), SupportFrameNo.of(2));
			times = 0;
		}};
		
	}
	
		// [1] 応援作業別勤怠を削除する
		// List<時間帯別勤怠の削除> is empty
		@Test
		public void deleteAttendance2() {
			
			DeleteAttendancesByTimezone deletion = new DeleteAttendancesByTimezone("sId", GeneralDate.today(), new ArrayList<>());
			
			List<AtomTask> result = deletion.deleteAttendance(require);
			assertThat(result).isEqualTo(new ArrayList<>());
		}
		
		
		// [2] 勤怠項目一覧を取得する
		// List<時間帯別勤怠の削除> is not empty
		@Test
		public void getAttendanceItems1() {
			List<AttendanceByTimezoneDeletion> attendanceDeletionLst = new ArrayList<>();
			attendanceDeletionLst.add(new AttendanceByTimezoneDeletion(SupportFrameNo.of(1), AttendanceDeletionStatusEnum.COMPLETE));
			attendanceDeletionLst.add(new AttendanceByTimezoneDeletion(SupportFrameNo.of(2), AttendanceDeletionStatusEnum.OVERWRITE));
			
			DeleteAttendancesByTimezone deletion = new DeleteAttendancesByTimezone("sId", GeneralDate.today(), attendanceDeletionLst);
			
			List<Integer> itemIds1 = new ArrayList<>();
			itemIds1.add(1);
			itemIds1.add(2);
			
			new Expectations() {
				{
					require.getAttendanceItemIds(SupportFrameNo.of(2));
					result = itemIds1;
				}
			};
			
			List<Integer> result = deletion.getAttendanceItems(require);
			
			assertThat(result).isEqualTo(itemIds1);
			
		}
		
		// [2] 勤怠項目一覧を取得する
		// List<時間帯別勤怠の削除> is empty
		@Test
		public void getAttendanceItems2() {
			DeleteAttendancesByTimezone deletion = new DeleteAttendancesByTimezone("sId", GeneralDate.today(), new ArrayList<>());
			
			List<Integer> result = deletion.getAttendanceItems(require);
			
			assertThat(result).isEqualTo(new ArrayList<>());
			
		}
}
