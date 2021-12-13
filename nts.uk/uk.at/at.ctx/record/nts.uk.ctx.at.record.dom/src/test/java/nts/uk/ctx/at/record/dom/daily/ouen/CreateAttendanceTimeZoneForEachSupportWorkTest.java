package nts.uk.ctx.at.record.dom.daily.ouen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.ReasonTimeChange;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.TimeChangeMeans;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkTimeInformation;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author chungnt
 *
 */

//@RunWith(JMockit.class)
public class CreateAttendanceTimeZoneForEachSupportWorkTest {

//	@Injectable
//	private CreateAttendanceTimeZoneForEachSupportWork.Require require;
//
//	private String empId = "empId";
//	private GeneralDate ymd = GeneralDate.today();
//
//	List<WorkDetailsParam> workDetailsParams = new ArrayList<>();
//
//	WorkDetailsParam detailsParam = new WorkDetailsParam(new SupportFrameNo(1),
//			new TimeZone(
//					new WorkTimeInformation(new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class),
//							Optional.empty()), new TimeWithDayAttr(100)),
//					new WorkTimeInformation(
//							new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class), Optional.empty()),
//							new TimeWithDayAttr(1000)),
//					Optional.empty()),
//			Optional.empty(), Optional.empty(), Optional.empty());
//
//	// if $旧の作業時間帯.isNotPresent
//	@Test
//	public void test() {
//
//		workDetailsParams.add(detailsParam);
//		workDetailsParams.add(detailsParam);
//		workDetailsParams.add(detailsParam);
//
//		new Expectations() {
//			{
//				require.find(empId, ymd);
//			}
//		};
//
//		// new MockUp<WorkGroup>() {
//		// @Mock
//		// public void checkExpirationDate(Require require, GeneralDate date) {
//		// }
//		// };
//
//		List<OuenWorkTimeSheetOfDailyAttendance> result = CreateAttendanceTimeZoneForEachSupportWork.create(require,
//				empId, ymd, workDetailsParams);
//
//		assertThat(result.isEmpty()).isFalse();
//		assertThat(result.size()).isEqualTo(3);
//		assertThat(result.get(0).getWorkNo()).isEqualTo(1);
//		assertThat(result.get(0).getTimeSheet().getStart().get().getTimeWithDay().get().v()).isEqualTo(100);
//		assertThat(result.get(0).getTimeSheet().getEnd().get().getTimeWithDay().get().v()).isEqualTo(1000);
//	}
//
//	// if $旧の作業時間帯.isPresent
//	@Test
//		public void test1() {
//			
//			workDetailsParams.add(detailsParam);
//			workDetailsParams.add(detailsParam);
//			workDetailsParams.add(detailsParam);
//			
//			List<OuenWorkTimeSheetOfDailyAttendance> ouenTimeSheets = new ArrayList<>();
//			
//			OuenWorkTimeSheetOfDailyAttendance ouenTimeSheet = OuenWorkTimeSheetOfDailyAttendance.create(
//					1,
//					WorkContent.create(
//							WorkplaceOfWorkEachOuen.create(new nts.uk.ctx.at.shared.dom.common.WorkplaceId("DUMMY"), new WorkLocationCD("DUMMY")), 
//							Optional.empty(), 
//							Optional.empty()), 
//					TimeSheetOfAttendanceEachOuenSheet.create(
//							new WorkNo(1),
//							Optional.of(new WorkTimeInformation(new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class),
//							Optional.empty()), new TimeWithDayAttr(100))),
//							Optional.of(new WorkTimeInformation(
//							new ReasonTimeChange(EnumAdaptor.valueOf(0, TimeChangeMeans.class), Optional.empty()),
//							new TimeWithDayAttr(1000)))));
//			
//			ouenTimeSheets.add(ouenTimeSheet);
//			
//			OuenWorkTimeSheetOfDaily daily = new OuenWorkTimeSheetOfDaily(empId, ymd, ouenTimeSheets);
//			
//			new Expectations() {
//				{
//					require.find(empId, ymd);
//				}
//			};
//			
//			List<OuenWorkTimeSheetOfDailyAttendance> result = CreateAttendanceTimeZoneForEachSupportWork.create(require, empId, ymd, workDetailsParams);
//			
//			assertThat(result.isEmpty()).isFalse();
//			assertThat(result.size()).isEqualTo(3);
//			assertThat(result.get(0).getWorkNo()).isEqualTo(1);
//			assertThat(result.get(0).getTimeSheet().getStart().get().getTimeWithDay().get().v()).isEqualTo(100);
//			assertThat(result.get(0).getTimeSheet().getEnd().get().getTimeWithDay().get().v()).isEqualTo(1000);
//		}

}
