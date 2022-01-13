package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import nts.uk.ctx.at.record.dom.applicationcancel.ReflectApplicationHelper;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.appreflectprocess.appreflectcondition.reflectprocess.ScheduleRecordClassifi;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;

/**
 * 
 * @author tutt
 *
 */
@RunWith(JMockit.class)
public class DailyAttendenceWorkToManHrRecordItemConvertServiceTest {
 	
	@Injectable
 	private DailyAttendenceWorkToManHrRecordItemConvertService.Require require;
 	
 	@SuppressWarnings("unchecked")
	@Test
 	public void test1(@Mocked DailyRecordToAttendanceItemConverter converter) {
 				
 		List<ManHourRecordAndAttendanceItemLink> links = new ArrayList<>();
 		links.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 1, 1));
 		links.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(2), 2, 2));
 		links.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(3), 2, 3));
 		
 		List<Integer> itemIds = new ArrayList<>();
 		itemIds.add(1);
 		itemIds.add(2);
 		
 		List<ItemValue> itemValues = new ArrayList<>();
		itemValues.add(new ItemValue(null, null, 2, "2"));
		itemValues.add(new ItemValue(null, null, 4, "4"));
		itemValues.add(new ItemValue(null, null, 5, "5"));
		

		IntegrationOfDaily inteDaiy = ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.SCHEDULE,
				1);
 		
 		new Expectations() {
 			{
 				require.get(itemIds);
 				result = links;
 				
 				converter.convert((Collection<Integer>) any);
				result = itemValues;
 			}
 		};
 		
 		ManHrRecordConvertResult actualResult = DailyAttendenceWorkToManHrRecordItemConvertService.convert(require, inteDaiy, itemIds);
 				
 		for (ItemValue v : actualResult.getManHrContents()) {
 			
 			if (v.getItemId() == 2) {
 				assertThat(v.getValue().equals("2"));
 			}
 			
 			if (v.getItemId() == 4) {
 				assertThat(v.getValue().equals("4"));
 			}
 			
 			if (v.getItemId() == 5) {
 				assertThat(v.getValue().equals("5"));
 			}
 			
 		}
 		
 	}
 	
 	@SuppressWarnings("unchecked")
	@Test
 	public void test2(@Mocked DailyRecordToAttendanceItemConverter converter) {
 				
 		List<ManHourRecordAndAttendanceItemLink> links = new ArrayList<>();
 	
 		links.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 1, 1));
 		links.add(new ManHourRecordAndAttendanceItemLink(new SupportFrameNo(1), 2, 2));
 		
 		List<Integer> itemIds = new ArrayList<>();
 		itemIds.add(1);
 		itemIds.add(2);
 		
 		
 		List<ItemValue> itemValues = new ArrayList<>();
		itemValues.add(new ItemValue(null, null, 1, "1"));
		itemValues.add(new ItemValue(null, null, 2, "2"));
		itemValues.add(new ItemValue(null, null, 1, "2"));

		IntegrationOfDaily inteDaiy = ReflectApplicationHelper.createDailyRecord(ScheduleRecordClassifi.SCHEDULE,
				1);
 		
 		new Expectations() {
 			{
 				require.get(itemIds);
 				result = links;
 				
 				converter.convert((Collection<Integer>) any);
				result = itemValues;
 			}
 		};
 		
 		ManHrRecordConvertResult actualResult = DailyAttendenceWorkToManHrRecordItemConvertService.convert(require, inteDaiy, itemIds);
 				
 		for (ItemValue v : actualResult.getManHrContents()) {
 			
 			if (v.getItemId() == 1) {
 				assertThat(v.getValue().equals("1"));
 			}
 			
 			if (v.getItemId() == 2) {
 				assertThat(v.getValue().equals("2"));
 			}
 		
 		}
 		
 		assertThat(actualResult.getTaskList().get(0).getSupNo().equals(new SupportFrameNo(1)));
 		for (TaskItemValue v : actualResult.getTaskList().get(0).getTaskItemValues()) {
 			if (v.getItemId() == 1) {
 				assertThat(v.getValue().equals("1"));
 			}
 			
 			if (v.getItemId() == 2) {
 				assertThat(v.getValue().equals("2"));
 			}
 		
 		}
 	}
}
