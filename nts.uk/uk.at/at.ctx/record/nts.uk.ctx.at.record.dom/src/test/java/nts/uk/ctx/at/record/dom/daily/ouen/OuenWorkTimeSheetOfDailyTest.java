package nts.uk.ctx.at.record.dom.daily.ouen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;

//@RunWith(JMockit.class)
public class OuenWorkTimeSheetOfDailyTest {

//	private String empId = "empId";
//	private GeneralDate ymd = GeneralDate.today();
//
//	@Test
//	public void testOuenWorkTimeSheetOfDaily_contructor() {
//
//		new MockUp<AttendanceItemIdContainer>() {
//			@Mock
//			public List<ItemValue> getIds(AttendanceItemType type) {
//				return new ArrayList<>();
//
//			}
//		};
//
//		OuenWorkTimeSheetOfDaily rs = new OuenWorkTimeSheetOfDaily(empId, ymd, new ArrayList<>());
//		NtsAssert.invokeGetters(rs);
//	}
//
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
//		new MockUp<AttendanceItemIdContainer>() {
//			@Mock
//			public List<ItemValue> getIds(AttendanceItemType type) {
//				return itemValues;
//			}
//
//			@Mock
//			public Map<Integer, List<ItemValue>> mapWorkNoItemsValue(Collection<ItemValue> items) {
//				return map;
//			}
//		};
//		OuenWorkTimeSheetOfDaily rs = OuenWorkTimeSheetOfDailyHelper.getOuenWorkTimeSheetOfDailyDefault();
//
//		AttendanceItemToChange attendanceItemToChange = rs.change(OuenWorkTimeSheetOfDailyHelper.getListOuenWorkTime());
//
//		assertThat(attendanceItemToChange.getOuenWorkTimeSheetOfDaily().getEmpId()).isEqualTo(empId);
//		assertThat(attendanceItemToChange.getAttendanceId().isEmpty()).isFalse();
//		assertThat(attendanceItemToChange.getAttendanceId().get(0)).isEqualTo(0);
//	}

}
