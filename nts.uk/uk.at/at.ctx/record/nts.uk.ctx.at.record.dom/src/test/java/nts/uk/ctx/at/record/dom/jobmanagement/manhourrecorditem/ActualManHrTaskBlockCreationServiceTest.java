package nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeGroup;
import nts.uk.ctx.at.record.dom.daily.timegroup.TaskTimeZone;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.time.TimeWithDayAttr;

/**
 * 
 * @author tutt
 *
 */
@RunWith(JMockit.class)
public class ActualManHrTaskBlockCreationServiceTest {

	@Injectable
	private ActualManHrTaskBlockCreationService.Require require;

	String sId = "employee";
	GeneralDate date = GeneralDate.today();
	
	@Test
	public void test1() {
		
		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		List<TaskItemValue> taskItemValues = new ArrayList<>();
		
		taskItemValues.add(new TaskItemValue(1, "1"));
		
		ManHrTaskDetail detail = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1));
		taskDetails.add(detail);
		
		List<ManHrPerformanceTaskBlock> taskBlocks = new ArrayList<>();
		
		List<ManHrTaskDetail> manHrtaskDetails = new ArrayList<>();
		
		manHrtaskDetails.add(new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1)));
		
		ManHrPerformanceTaskBlock block = new ManHrPerformanceTaskBlock(new TimeSpanForCalc (new TimeWithDayAttr(10), new TimeWithDayAttr(20)), taskDetails);
		taskBlocks.add(block);
		
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, taskBlocks);
		
		List<TaskTimeZone> timezones = new ArrayList<>();
		timezones.add(new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(10), new TimeWithDayAttr(20)), Arrays.asList(new SupportFrameNo(1)) ));
		
		//if not $作業時間帯グループ.isEmpty
		TaskTimeGroup group = new TaskTimeGroup(sId, date, timezones);

		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.of(group);

			}
		};
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				taskDetails);

		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().start()).isEqualTo(10);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().end()).isEqualTo(20);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getSupNo().v()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
	}
	
	@Test
	public void test2() {
		
		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		List<TaskItemValue> taskItemValues = new ArrayList<>();
		
		//if ($開始時刻.isEmpty OR $終了時刻.isEmpty)
		taskItemValues.add(new TaskItemValue(1, "1"));
		taskItemValues.add(new TaskItemValue(4, "2"));
		
		ManHrTaskDetail detail = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1));
		ManHrTaskDetail detail2 = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(2));
		taskDetails.add(detail);
		taskDetails.add(detail2);
		
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, new ArrayList<>());
		
		List<TaskTimeZone> timezones = new ArrayList<>();
		timezones.add(new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(10), new TimeWithDayAttr(20)), new ArrayList<>(Arrays.asList(new SupportFrameNo(1)))));
		
		//if not $作業時間帯グループ.isEmpty
		TaskTimeGroup group = new TaskTimeGroup(sId, date, timezones);

		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.of(group);

			}
		};
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				taskDetails);

		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		assertThat(actualResult.getTaskBlocks().size()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().start()).isEqualTo(10);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().end()).isEqualTo(20);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getSupNo().v()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(4);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
	}
	
	@Test
	public void test3() {
		
		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		List<TaskItemValue> taskItemValues = new ArrayList<>();
		
		//if ($開始時刻.isEmpty OR $終了時刻.isEmpty)
		taskItemValues.add(new TaskItemValue(3, "1"));
		taskItemValues.add(new TaskItemValue(2, "2"));
		
		ManHrTaskDetail detail = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1));
		ManHrTaskDetail detail2 = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(2));
		taskDetails.add(detail);
		taskDetails.add(detail2);
		
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, new ArrayList<>());
		
		List<TaskTimeZone> timezones = new ArrayList<>();
		timezones.add(new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(10), new TimeWithDayAttr(20)), new ArrayList<>(Arrays.asList(new SupportFrameNo(1)))));
		
		//if not $作業時間帯グループ.isEmpty
		TaskTimeGroup group = new TaskTimeGroup(sId, date, timezones);

		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.of(group);

			}
		};
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				taskDetails);

		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		assertThat(actualResult.getTaskBlocks().size()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().start()).isEqualTo(10);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().end()).isEqualTo(20);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getSupNo().v()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(3);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
	}
	
	@Test
	public void test4() {
		
		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		List<TaskItemValue> taskItemValues = new ArrayList<>();
		
		//if ($開始時刻.isEmpty OR $終了時刻.isEmpty)
		taskItemValues.add(new TaskItemValue(3, "1"));
		taskItemValues.add(new TaskItemValue(4, "2"));
		
		ManHrTaskDetail detail = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1));
		ManHrTaskDetail detail2 = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(2));
		taskDetails.add(detail);
		taskDetails.add(detail2);
		
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, new ArrayList<>());
		
		List<TaskTimeZone> timezones = new ArrayList<>();
		timezones.add(new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(10), new TimeWithDayAttr(20)), new ArrayList<>(Arrays.asList(new SupportFrameNo(1)))));
		
		//if not $作業時間帯グループ.isEmpty
		TaskTimeGroup group = new TaskTimeGroup(sId, date, timezones);

		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.of(group);

			}
		};
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				taskDetails);

		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		assertThat(actualResult.getTaskBlocks().size()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().start()).isEqualTo(10);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().end()).isEqualTo(20);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getSupNo().v()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(3);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(4);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
	}
	
	@Test
	public void test5() {
		
		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		List<TaskItemValue> taskItemValues = new ArrayList<>();
		
		//if ($開始時刻.isEmpty OR $終了時刻.isEmpty)
		taskItemValues.add(new TaskItemValue(1, "1"));
		taskItemValues.add(new TaskItemValue(2, "2"));
		
		ManHrTaskDetail detail = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1));
		ManHrTaskDetail detail2 = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(2));
		taskDetails.add(detail);
		taskDetails.add(detail2);
		
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, new ArrayList<>());
		
		List<TaskTimeZone> timezones = new ArrayList<>();
		timezones.add(new TaskTimeZone(new TimeSpanForCalc(new TimeWithDayAttr(10), new TimeWithDayAttr(20)), new ArrayList<>(Arrays.asList(new SupportFrameNo(1)))));
		
		//if not $作業時間帯グループ.isEmpty
		TaskTimeGroup group = new TaskTimeGroup(sId, date, timezones);

		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.of(group);

			}
		};
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				taskDetails);

		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		assertThat(actualResult.getTaskBlocks().size()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().start()).isEqualTo(10);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().end()).isEqualTo(20);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getSupNo().v()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
		
		assertThat(actualResult.getTaskBlocks().get(1).getCaltimeSpan().start()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(1).getCaltimeSpan().end()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getSupNo().v()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
	}
	
	@Test
	public void test6() {
		
		// if $作業時間帯グループ and 工数実績項目リスト isEmpty
		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.empty();

			}
		};
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, new ArrayList<>());
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				new ArrayList<>());
		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		assertThat(actualResult.getTaskBlocks().size()).isEqualTo(0);
	}
	
	@Test
	public void test7() {
		
		List<ManHrTaskDetail> taskDetails = new ArrayList<>();
		List<TaskItemValue> taskItemValues = new ArrayList<>();
		
		taskItemValues.add(new TaskItemValue(1, "1"));
		taskItemValues.add(new TaskItemValue(2, "2"));
		
		ManHrTaskDetail detail = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(1));
		ManHrTaskDetail detail2 = new ManHrTaskDetail(taskItemValues, new SupportFrameNo(2));
		taskDetails.add(detail);
		taskDetails.add(detail2);
		
		// if $作業時間帯グループ is empty and 工数実績項目リスト is not Empty
		new Expectations() {
			{
				require.get(sId, date);
				result = Optional.empty();

			}
		};
		DailyActualManHrActualTask expectedResult = new DailyActualManHrActualTask(date, new ArrayList<>());
		DailyActualManHrActualTask actualResult = ActualManHrTaskBlockCreationService.create(require, sId, date,
				taskDetails);
		
		assertThat(expectedResult.getDate()).isEqualTo(actualResult.getDate());
		assertThat(actualResult.getTaskBlocks().size()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().start()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getCaltimeSpan().end()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getSupNo().v()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(0).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
		
		assertThat(actualResult.getTaskBlocks().get(1).getCaltimeSpan().start()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(1).getCaltimeSpan().end()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getSupNo().v()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(0).getItemId()).isEqualTo(1);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(0).getValue()).isEqualTo("1");
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(1).getItemId()).isEqualTo(2);
		assertThat(actualResult.getTaskBlocks().get(1).getTaskDetails().get(0).getTaskItemValues().get(1).getValue()).isEqualTo("2");
	}

}
