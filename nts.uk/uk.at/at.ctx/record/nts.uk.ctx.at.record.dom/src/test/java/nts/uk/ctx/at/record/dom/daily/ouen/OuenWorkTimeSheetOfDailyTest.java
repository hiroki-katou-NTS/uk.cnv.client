package nts.uk.ctx.at.record.dom.daily.ouen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.record.WorkplaceOfWorkEachOuen;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkGroup;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

@RunWith(JMockit.class)
public class OuenWorkTimeSheetOfDailyTest {

	private String empId = "empId";
	private GeneralDate ymd = GeneralDate.today();

	@Test
	public void testOuenWorkTimeSheetOfDaily_contructor() {

		new MockUp<AttendanceItemIdContainer>() {
			@Mock
			public List<ItemValue> getIds(AttendanceItemType type) {
				return new ArrayList<>();

			}
		};

		OuenWorkTimeSheetOfDaily rs = new OuenWorkTimeSheetOfDaily(empId, ymd, new ArrayList<>());
		NtsAssert.invokeGetters(rs);
	}

//	@Test
//	public void testMethodChange() {
//
//		List<ItemValue> itemValues = new ArrayList<>();
//
//		itemValues.add(new ItemValue());
//
//		Map<Integer, List<ItemValue>> map = new HashMap<Integer, List<ItemValue>>();
//		map.put(1, itemValues);
//
//
//		new MockUp<AttendanceItemIdContainer>() {
//			@Mock
//			public List<ItemValue> getIds(AttendanceItemType type) {
//				return itemValues;
//			}
//
//		};
//		
//		OuenWorkTimeSheetOfDaily rs = OuenWorkTimeSheetOfDailyHelper.getOuenWorkTimeSheetOfDailyDefault();
//
//		AttendanceItemToChange attendanceItemToChange = rs.change(OuenWorkTimeSheetOfDailyHelper.getlist());
//
//		assertThat(attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getOuenTimeSheet().size()).isEqualTo(3);
//		assertThat(attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getOuenTimeSheet().get(1).getWorkNo().v()).isEqualTo(2);
//	}
	
	@Test
	public void testMethodChangeUdate() {

		List<ItemValue> itemValues = new ArrayList<>();

		itemValues.add(new ItemValue());

		Map<Integer, List<ItemValue>> map = new HashMap<Integer, List<ItemValue>>();
		map.put(1, itemValues);

		new MockUp<AttendanceItemIdContainer>() {
			@Mock
			public Map<Integer, List<ItemValue>> mapWorkNoItemsValue(Collection<ItemValue> items) {
				return map;
			}
		};
		
		OuenWorkTimeSheetOfDaily rs = OuenWorkTimeSheetOfDailyHelper.getOuenWorkTimeSheetOfDailyDefault();

		AttendanceItemToChange attendanceItemToChange = rs.change(OuenWorkTimeSheetOfDailyHelper.getlist2());

		assertThat(attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getOuenTimeSheet().size()).isEqualTo(1);
		assertThat(attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getOuenTimeSheet().get(0).getWorkNo().v()).isEqualTo(2);
	}
	
	@Test
	public void testMethodChangeRemote() {

		List<ItemValue> itemValues = new ArrayList<>();

		itemValues.add(new ItemValue());

		Map<Integer, List<ItemValue>> map = new HashMap<Integer, List<ItemValue>>();
		map.put(1, itemValues);

		new MockUp<AttendanceItemIdContainer>() {
			@Mock
			public Map<Integer, List<ItemValue>> mapWorkNoItemsValue(Collection<ItemValue> items) {
				return map;
			}
		};
		
		OuenWorkTimeSheetOfDaily rs = OuenWorkTimeSheetOfDailyHelper.getOuenWorkTimeSheetOfDailyDefault();

		AttendanceItemToChange attendanceItemToChange = rs.change(new ArrayList<>());

		assertThat(attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getOuenTimeSheet().isEmpty()).isTrue();
	}
	
	@Test
	public void testMethodGetAttendanceId() {
		List<OuenWorkTimeSheetOfDailyAttendance> list = new ArrayList<>();
		list.add(
				OuenWorkTimeSheetOfDailyAttendance
				.create(new SupportFrameNo(1),
						WorkContent.create(WorkplaceOfWorkEachOuen.create(new WorkplaceId("Dummy"),new WorkLocationCD("Dummy")),
								Optional.of(new WorkGroup(new WorkCode("Dummy"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty())),
								Optional.empty()),
						TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()), Optional.empty()));
		
		OuenWorkTimeSheetOfDaily ouenWorkTimeSheetOfDaily = new OuenWorkTimeSheetOfDaily(empId, ymd, list);
		
		List<Integer> result = ouenWorkTimeSheetOfDaily.getAttendanceId();
		
		assertThat(result.isEmpty()).isFalse();
	}

}
