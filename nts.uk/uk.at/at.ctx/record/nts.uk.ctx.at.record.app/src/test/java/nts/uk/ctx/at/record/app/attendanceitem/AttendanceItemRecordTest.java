package nts.uk.ctx.at.record.app.attendanceitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.app.find.dailyattendance.timesheet.ouen.dto.OuenWorkTimeSheetOfDailyDto;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.dailyperform.workinfo.dto.WorkInformationOfDailyDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.ctx.at.record.dom.daily.ouen.OuenWorkTimeSheetOfDaily;
import nts.uk.ctx.at.shared.dom.attendance.util.AttendanceItemUtilRes;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemIdContainer;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.util.AttendanceItemUtil.AttendanceItemType;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.ChoiceCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.OuenWorkTimeSheetOfDailyAttendance;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoCommentItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNumItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoSelectionItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoTimeItem;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppNumValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.TimeSheetOfAttendanceEachOuenSheet;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppComment;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.WorkSuppInfo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;

public class AttendanceItemRecordTest {

//	TO DO: 医療時間（勤怠項目ID：751,753,755）が原因でテストが失敗します。
//			上記以外は正常に動作します。
//			医療時間のドメイン変更時に修正予定の為、それまでコメントアウトします。
//	@Test
//	public void test_toAttendanceItemDtoDaily() {
//		
//		List<ItemValue> items = AttendanceItemUtil.toItemValues(new DailyRecordDto(), AttendanceItemType.DAILY_ITEM);
//		
//		items.stream().forEach(c -> {
//			Logger.getLogger(this.getClass()).info(c.itemId() + ":" + c.layoutCode() + ":" + c.value());
//		});
//		
//		List<Integer> allIds = AttendanceItemIdContainer.getIds(AttendanceItemType.DAILY_ITEM).stream()
//				.map(i -> i.getItemId())
//				.collect(Collectors.toList());
//		
//		List<Integer> itemsIds = items.stream()
//				.map(i -> i.getItemId())
//				.collect(Collectors.toList());
//		
//		List<Integer> missingIds = allIds.stream()
//				.filter(a -> !itemsIds.contains(a))
//				.collect(Collectors.toList());
//		
//		Assert.assertTrue(missingIds.isEmpty());
//	}

	@Test
	/** test item: 2251 -> 2650 */
	public void test_dailyOuen() {
		
		List<OuenWorkTimeSheetOfDailyAttendance> ouenSheets = new ArrayList<>();
		IntStream.range(1, 21).forEach(c -> ouenSheets.add(getOuen(c)));
		OuenWorkTimeSheetOfDaily ouen = OuenWorkTimeSheetOfDaily.create("1", GeneralDate.today(), ouenSheets);
		OuenWorkTimeSheetOfDailyDto dto = OuenWorkTimeSheetOfDailyDto.getDto(ouen);
		List<Integer> itemIds = IntStream.range(2251, 2651).map(c -> c).boxed().collect(Collectors.toList());
		List<ItemValue> values = AttendanceItemUtilRes.collect(dto, itemIds, AttendanceItemType.DAILY_ITEM);
		values.stream().sorted((c1, c2) -> c1.itemId() - c2.itemId()).forEach(c -> {
			assertThat(c.valueAsObjet()).isNotNull();
//			System.out.println(c.path());
//			System.out.println(c.getItemId() + " - " + c.getValue() + "-" + c.getValueType());
		});
		
		OuenWorkTimeSheetOfDailyDto dto2 = OuenWorkTimeSheetOfDailyDto.getDto(null);
		AttendanceItemUtilRes.merge(dto2, values, AttendanceItemType.DAILY_ITEM);
		
//		dto2.getOuenTimeSheet().stream().forEach(c -> {
//			c.getWorkContent().getWorkSuppInfo().getComment().stream().forEach(x -> {
//				assertThat(x.getValue()).isNotNull();
//			});
//			c.getWorkContent().getWorkSuppInfo().getNum().stream().forEach(x -> {
//				assertThat(x.getValue()).isNotNull();
//			});
//			c.getWorkContent().getWorkSuppInfo().getSelection().stream().forEach(x -> {
//				assertThat(x.getValue()).isNotNull();
//			});
//			c.getWorkContent().getWorkSuppInfo().getTime().stream().forEach(x -> {
//				assertThat(x.getValue()).isNotNull();
//			});
//		});
	}
	
	private OuenWorkTimeSheetOfDailyAttendance getOuen(int idx) {
		return OuenWorkTimeSheetOfDailyAttendance.create(new SupportFrameNo(idx), 
				WorkContent.create(null, Optional.empty(), Optional.of(new WorkSuppInfo(
						IntStream.range(1, 6).boxed().map(x -> new SuppInfoTimeItem(new SuppInfoNo(x), new AttendanceTime(idx * 100 + x))).collect(Collectors.toList()),
						IntStream.range(1, 6).boxed().map(x -> new SuppInfoNumItem(new SuppInfoNo(x), new SuppNumValue(idx * 100 + x))).collect(Collectors.toList()),
						IntStream.range(1, 6).boxed().map(x -> new SuppInfoCommentItem(new SuppInfoNo(x), new WorkSuppComment("C" + (idx * 100 + x)))).collect(Collectors.toList()),
						IntStream.range(1, 6).boxed().map(x -> new SuppInfoSelectionItem(new SuppInfoNo(x), new ChoiceCode("CC" + (idx * 100 + x)))).collect(Collectors.toList())))), 
				TimeSheetOfAttendanceEachOuenSheet.create(new WorkNo(1), Optional.empty(), Optional.empty()), 
				Optional.empty());
	}
	
	@Test
	public void test_toAttendanceItemDtoMonthly() {
		
		List<ItemValue> items = AttendanceItemUtilRes.collect(new MonthlyRecordWorkDto(), AttendanceItemType.MONTHLY_ITEM);
		
		items.stream().forEach(c -> {
			Logger.getLogger(this.getClass()).info(c.itemId() + ":" + c.layoutCode() + ":" + c.value());
		});
		
		List<Integer> allIds = AttendanceItemIdContainer.getIds(AttendanceItemType.MONTHLY_ITEM).stream()
				.map(i -> i.getItemId())
				.collect(Collectors.toList());
		
		List<Integer> itemsIds = items.stream()
				.map(i -> i.getItemId())
				.collect(Collectors.toList());
		
		List<Integer> missingIds = allIds.stream()
				.filter(a -> !itemsIds.contains(a))
				.collect(Collectors.toList());
		
//		Assert.assertTrue(missingIds.isEmpty());
	}
	
	/**
	 * 調査用（日次）	勤怠項目IDを指定して実行する
	 */
	public void survey_toAttendanceItemDtoDaily() {
		
		List<Integer> itemIds = Arrays.asList(1, 2, 3); //調査したい勤怠項目IDを指定
		
		List<ItemValue> items = AttendanceItemUtilRes.collect(new DailyRecordDto(), itemIds, AttendanceItemType.DAILY_ITEM);
		
		items.stream().forEach(c -> {
			Logger.getLogger(this.getClass()).info(c.itemId() + ":" + c.layoutCode() + ":" + c.value());
		});
	}
	
	/**
	 * 調査用（月次）	勤怠項目IDを指定して実行する
	 */
	public void survey_toAttendanceItemDtoMonthly() {
		
		List<Integer> itemIds = Arrays.asList(1, 2, 3); //調査したい勤怠項目IDを指定
		
		List<ItemValue> items = AttendanceItemUtilRes.collect(new MonthlyRecordWorkDto(), itemIds, AttendanceItemType.MONTHLY_ITEM);
		
		items.stream().forEach(c -> {
			Logger.getLogger(this.getClass()).info(c.itemId() + ":" + c.layoutCode() + ":" + c.value());
		});
	}
	
//	@Test
	public void test_toAttendanceItemMultiDto() {
		List<Integer> itemIds = Arrays.asList(797);
		DailyRecordDto dto1 = new DailyRecordDto(), 
				 		dto2 = new DailyRecordDto(), 
			 			dto3 = new DailyRecordDto();
		dto1.employeeId("1");
		dto1.setWorkInfo(new WorkInformationOfDailyDto());
		dto2.employeeId("2");
		dto3.employeeId("3");
		Map<DailyRecordDto, List<ItemValue>> items = Arrays.asList(dto1, dto2, dto3).stream()
				.collect(Collectors.toMap(c -> c, c -> AttendanceItemUtilRes.collect(c, itemIds, AttendanceItemType.DAILY_ITEM)));
		items.entrySet().stream().forEach(x -> {
			x.getValue().stream().forEach(c -> {
				Logger.getLogger(this.getClass()).info(c.itemId() + ":" + c.layoutCode());
			});
		});
//		Assert.assertEquals(items.size(), 3);
	}
	

//	@Test
	public void test_FromItemValues() {
//		List<ItemValue> itemIds = Arrays.asList(new ItemValue(ValueType.CODE, "", 1, 001),
//				new ItemValue(ValueType.CODE, "", 2, 002), new ItemValue(ValueType.TIME, "", 3, 101),
//				new ItemValue(ValueType.TIME, "", 4, 102), new ItemValue(ValueType.TIME, "", 5, 103),
//				new ItemValue(ValueType.TIME, "", 6, 104), new ItemValue(ValueType.CODE, "", 28, 003),
//				new ItemValue(ValueType.CODE, "", 29, 004), new ItemValue(ValueType.CODE, "", 623, "職場ID"),
//				new ItemValue(ValueType.CODE, "", 624, "分類コード"), new ItemValue(ValueType.CODE, "", 625, "職位ID"),
//				new ItemValue(ValueType.CODE, "", 626, "雇用コード"), new ItemValue(ValueType.TIME, "", 759, 759),
//				new ItemValue(ValueType.TIME, "", 760, 760),
//				// new ItemValue(ValueType.TIME, "", 761, 761),
//				// new ItemValue(ValueType.TIME, "", 762, 762),
//				// new ItemValue(ValueType.TIME, "", 763, 763),
//				// new ItemValue(ValueType.TIME, "", 764, 764),
//				new ItemValue(ValueType.TIME, "", 765, 765), new ItemValue(ValueType.TIME, "", 766, 766),
//				new ItemValue(ValueType.TIME, "", 86, 1), new ItemValue(ValueType.TIME, "", 93, 2),
//				new ItemValue(ValueType.TIME, "", 100, 3), new ItemValue(ValueType.TIME, "", 107, 4),
//				new ItemValue(ValueType.TIME, "", 114, 5), new ItemValue(ValueType.TIME, "", 121, 6),
//				new ItemValue(ValueType.TIME, "", 128, 7), new ItemValue(ValueType.TIME, "", 135, 8),
//				new ItemValue(ValueType.TIME, "", 142, 9), new ItemValue(ValueType.TIME, "", 149, 10),
//				new ItemValue(ValueType.TIME, "", 416, 1), new ItemValue(ValueType.TIME, "", 417, 2),
//				new ItemValue(ValueType.TIME, "", 418, 3), new ItemValue(ValueType.TIME, "", 419, 4),
//				new ItemValue(ValueType.TIME, "", 420, 5), new ItemValue(ValueType.TIME, "", 421, 6),
//				new ItemValue(ValueType.TIME, "", 422, 7), new ItemValue(ValueType.TIME, "", 423, 8),
//				new ItemValue(ValueType.TIME, "", 424, 9), new ItemValue(ValueType.TIME, "", 425, 10));
//		DailyRecordDto items = AttendanceItemUtil.fromItemValues(new DailyRecordDto(), itemIds);
//		Assert.assertNotEquals(items, null);
	}
}
