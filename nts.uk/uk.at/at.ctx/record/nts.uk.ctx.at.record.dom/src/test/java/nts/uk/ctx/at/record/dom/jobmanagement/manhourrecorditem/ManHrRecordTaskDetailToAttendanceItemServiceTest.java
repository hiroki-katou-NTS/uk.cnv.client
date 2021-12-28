package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 
 * @author tutt
 *
 */
@RunWith(JMockit.class)
public class ManHrRecordTaskDetailToAttendanceItemServiceTest {

	@Injectable
	private ManHrRecordTaskDetailToAttendanceItemService.Require require;

	@Test
	public void test1() {
		List<Integer> itemLst = new ArrayList<>();
				itemLst.add(1);
				itemLst.add(2);
				List<ManHourRecordAndAttendanceItemLink> settings = new ArrayList<>();
				List<ItemValue> itemValues = new ArrayList<>();
				itemValues.add(new ItemValue(null, null, 1, "1"));
		
				List<ManHrTaskDetail> taskDetails = new ArrayList<>();
				List<TaskItemValue> taskItemValues = new ArrayList<>();
				
				itemValues.add(new ItemValue(null, null, 1, "1"));
				itemValues.add(new ItemValue(null, null, 2, "2"));
				taskItemValues.add(new TaskItemValue(1, "1"));
				taskItemValues.add(new TaskItemValue(2, "2"));
				
				taskDetails.add(new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1)));
				taskDetails.add(new ManHrTaskDetail(taskItemValues, new SupportFrameNo(2)));
				
				settings.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 1, 1));
				settings.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(2), 2, 2));
		
				new Expectations() {
					{
						require.get(itemLst);
						result = settings;
					}
				};
				List<ItemValue> actualResult = ManHrRecordTaskDetailToAttendanceItemService.convert(require, itemValues, taskDetails);
				
				for (ItemValue v : actualResult) {
					
					if (v.getItemId() == 1) {
						assertThat(v.getValue().equals("1"));
					}
					
					if (v.getItemId() == 2) {
						assertThat(v.getValue().equals("2"));
				}
				}
	}


	@Test
	public void test2() {
		List<Integer> itemLst = new ArrayList<>();
		itemLst.add(1);
		itemLst.add(2);

		List<ManHourRecordAndAttendanceItemLink> settings = new ArrayList<>();
		List<ItemValue> values = new ArrayList<>();
		List<ItemValue> expectedResult = new ArrayList<>();
		values.add(new ItemValue(null, null, 3, "3"));
		values.add(new ItemValue(null, null, 4, "4"));

		List<TaskItemValue> value1 = new ArrayList<>();
		value1.add(new TaskItemValue(1, "1"));
		value1.add(new TaskItemValue(2, "2"));

		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		taskDetails.add(new ManHrTaskDetail(value1, new SupportFrameNo(1)));
		settings.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 1, 1));

		new Expectations() {
			{
				require.get(itemLst);
				result = settings;
			}
		};
		List<ItemValue> actualResult = ManHrRecordTaskDetailToAttendanceItemService.convert(require, values,
				taskDetails);
		assertThat(expectedResult.equals(actualResult));

	}
	
	@Test
	public void test3() {
		List<Integer> itemLst = new ArrayList<>();
		itemLst.add(1);
		itemLst.add(2);

		List<ManHourRecordAndAttendanceItemLink> settings = new ArrayList<>();
		List<ItemValue> values = new ArrayList<>();
		List<ItemValue> expectedResult = new ArrayList<>();
		values.add(new ItemValue(null, null, 3, "3"));
		values.add(new ItemValue(null, null, 4, "4"));

		List<TaskItemValue> value1 = new ArrayList<>();
		value1.add(new TaskItemValue(1, "1"));
		value1.add(new TaskItemValue(2, "2"));

		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		taskDetails.add(new ManHrTaskDetail(value1, new SupportFrameNo(1)));
		settings.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 3, 3));

		new Expectations() {
			{
				require.get(itemLst);
				result = settings;
			}
		};
		List<ItemValue> actualResult = ManHrRecordTaskDetailToAttendanceItemService.convert(require, values,
				taskDetails);
		assertThat(expectedResult.equals(actualResult));

	}
	
	@Test
	public void test4() {
		List<Integer> itemLst = new ArrayList<>();
		itemLst.add(1);
		
		List<ManHourRecordAndAttendanceItemLink> settings = new ArrayList<>();
		List<ItemValue> values = new ArrayList<>();
		List<ItemValue> expectedResult = new ArrayList<>();
		values.add(new ItemValue(null, null, 1, "5"));
		
		expectedResult.add(new ItemValue(null, null, 1, "1"));

		List<TaskItemValue> value1 = new ArrayList<>();
		value1.add(new TaskItemValue(1, "1"));

		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		taskDetails.add(new ManHrTaskDetail(value1, new SupportFrameNo(1)));
		settings.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 1, 1));

		new Expectations() {
			{
				require.get(itemLst);
				result = settings;
			}
		};
		List<ItemValue> actualResult = ManHrRecordTaskDetailToAttendanceItemService.convert(require, values,
				taskDetails);
		assertThat(expectedResult.equals(actualResult));

	}
	
	@Test
	public void test5() {
		List<Integer> itemLst = new ArrayList<>();
		itemLst.add(1);
		itemLst.add(2);

		List<ManHourRecordAndAttendanceItemLink> settings = new ArrayList<>();
		List<ItemValue> values = new ArrayList<>();
		List<ItemValue> expectedResult = new ArrayList<>();
		values.add(new ItemValue(null, null, 3, "3"));
		values.add(new ItemValue(null, null, 4, "4"));
		values.add(new ItemValue(null, null, 1, "1"));

		List<TaskItemValue> value1 = new ArrayList<>();
		value1.add(new TaskItemValue(1, "1"));
		value1.add(new TaskItemValue(2, "2"));

		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		taskDetails.add(new ManHrTaskDetail(value1, new SupportFrameNo(1)));
		settings.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 4, 1));

		new Expectations() {
			{
				require.get(itemLst);
				result = settings;
			}
		};
		List<ItemValue> actualResult = ManHrRecordTaskDetailToAttendanceItemService.convert(require, values,
				taskDetails);
		assertThat(expectedResult.equals(actualResult));

	}

}
