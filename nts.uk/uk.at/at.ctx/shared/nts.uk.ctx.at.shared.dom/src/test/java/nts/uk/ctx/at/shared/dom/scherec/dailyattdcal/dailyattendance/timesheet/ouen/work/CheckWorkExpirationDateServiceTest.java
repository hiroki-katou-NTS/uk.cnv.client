package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameName;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.Task;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * 
 * @author chungnt
 *
 */

//@RunWith(JMockit.class)
public class CheckWorkExpirationDateServiceTest {

//	@Injectable
//	private CheckWorkExpirationDateService.Require require;
//
//	GeneralDate date = GeneralDate.today();
//	TaskFrameNo taskFrameNo = new TaskFrameNo(1);
//	TaskFrameNo taskFrameNo2 = new TaskFrameNo(2);
//	WorkCode code = new WorkCode("WorkCode");
//
//	List<TaskFrameSetting> frameSettingList = new ArrayList<>();
//
//	Task task = new Task(new TaskCode("Code"), taskFrameNo, null, new ArrayList<>(), new DatePeriod(date, date), null);
//
//	// error == false
//	@Test
//	public void testCheckWorkExpiration_1() {
//
//		new Expectations() {
//			{
//				require.getTask(taskFrameNo, code);
//
//				result = Optional.of(task);
//			}
//		};
//
//		CheckWorkExpirationDateService.check(require, date, taskFrameNo, Optional.of(code));
//	}
//
//	// error == true
//	// throw 2080
//	@Test
//	public void testCheckWorkExpiration_2() {
//		frameSettingList.add(new TaskFrameSetting(taskFrameNo, new TaskFrameName("DUMMY"), null));
//		frameSettingList.add(new TaskFrameSetting(taskFrameNo2, new TaskFrameName("DUMMY1"), null));
//
//		TaskFrameUsageSetting taskFrameUsageSetting = new TaskFrameUsageSetting(frameSettingList);
//
//		new Expectations() {
//			{
//				require.getTask(taskFrameNo, code);
//
//				require.getTaskFrameUsageSetting();
//				result = taskFrameUsageSetting;
//			}
//		};
//
//		NtsAssert.businessException("Msg_2080",
//				() -> CheckWorkExpirationDateService.check(require,
//						date,
//						taskFrameNo,
//						Optional.of(code)));
//	}
//
//	// error == false
//	// task.get().checkExpirationDate(date) == true
//	@Test
//	public void testCheckWorkExpiration_3() {
//		frameSettingList.add(new TaskFrameSetting(taskFrameNo, new TaskFrameName("DUMMY"), null));
//		frameSettingList.add(new TaskFrameSetting(taskFrameNo2, new TaskFrameName("DUMMY1"), null));
//		
//		Task task = new Task(new TaskCode("Code"), taskFrameNo, null, new ArrayList<>(), new DatePeriod(date.addMonths(-5), date), null);
//
//		TaskFrameUsageSetting taskFrameUsageSetting = new TaskFrameUsageSetting(frameSettingList);
//
//		new Expectations() {
//			{
//				require.getTask(taskFrameNo, code);
//				result = Optional.of(task);
//
//				require.getTaskFrameUsageSetting();
//				result = taskFrameUsageSetting;
//			}
//		};
//
//		CheckWorkExpirationDateService.check(require, date.addMonths(-3), taskFrameNo, Optional.of(code));
//	}
}
