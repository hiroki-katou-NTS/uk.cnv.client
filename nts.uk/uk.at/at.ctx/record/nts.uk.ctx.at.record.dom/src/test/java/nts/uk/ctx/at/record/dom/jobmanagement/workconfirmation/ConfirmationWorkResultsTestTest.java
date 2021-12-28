package nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation;

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
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.domainservice.GetWorkAvailableToEmployeesService;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskassign.taskassignworkplace.NarrowingDownTaskByWorkplace;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameName;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameNo;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskframe.TaskFrameUsageSetting;
import nts.uk.ctx.at.shared.dom.scherec.taskmanagement.taskmaster.TaskCode;

/**
 * 
 * @author chungnt
 *
 */


@RunWith(JMockit.class)
public class ConfirmationWorkResultsTestTest {

	@Injectable
	private GetWorkAvailableToEmployeesService.Require require;
	
	private String companyID = "companyID";
	private String employeeID = "employeeID";
	private GeneralDate date = GeneralDate.today();
	private TaskFrameNo taskFrameNo = new TaskFrameNo(2);
	private TaskCode taskCodes = new TaskCode("taskCodes");
	
	private TaskFrameSetting taskFrameSetting = new TaskFrameSetting(new TaskFrameNo(2),
			new TaskFrameName("TaskFrameName"),
			EnumAdaptor.valueOf(1, UseAtr.class));
	
	private List<TaskFrameSetting> frameSettingList = new ArrayList<>();
	
	
	// require.getTask() == null
//	@Test
//	public void test() {
//
//		new Expectations() {
//			{
//				require.getTask();
//			}
//		};
//		
//		assertThat((GetWorkAvailableToEmployeesService.get(require, 
//				companyID, 
//				employeeID, 
//				date, 
//				taskFrameNo, 
//				Optional.of(taskCodes)))
//				.isEmpty()).isTrue();
//	}
	
	
	//if $職場別作業の絞込.isNotPresent()	
	@Test
	public void test_1() {
		frameSettingList.add(taskFrameSetting);
		frameSettingList.add(taskFrameSetting);
		
		TaskFrameUsageSetting task = new TaskFrameUsageSetting(frameSettingList);

		new Expectations() {
			{
				require.getTask();
				result = task;
				
				require.findWpkIdsBySid(employeeID, date);
			}
		};
		
		assertThat((GetWorkAvailableToEmployeesService.get(require, 
				companyID, 
				employeeID, 
				date, 
				taskFrameNo, 
				Optional.of(taskCodes)))
				.isEmpty()).isTrue();
	}
	
	//if $職場別作業の絞込.isPresent()	
//		@Test
//		public void test_2() {
//			frameSettingList.add(taskFrameSetting);
//			frameSettingList.add(taskFrameSetting);
//			
//			List<String> listWpkIds = new ArrayList<>();
//			listWpkIds.add("WpkIds");
//			
//			TaskFrameUsageSetting task = new TaskFrameUsageSetting(frameSettingList);
//			
//			NarrowingDownTaskByWorkplace downTaskByWorkplace = new NarrowingDownTaskByWorkplace("WpkIds",
//					taskFrameNo,
//					new ArrayList<>());
//
//			new Expectations() {
//				{
//					require.getTask();
//					result = task;
//					
//					require.findWpkIdsBySid(employeeID, date);
//					result = listWpkIds;
//					
//					require.getNarrowingDownTaskByWorkplace("workPlaceId", taskFrameNo);
//					result = Optional.of(downTaskByWorkplace);
//				}
//			};
//			
//			assertThat((GetWorkAvailableToEmployeesService.get(require, 
//					companyID, 
//					employeeID, 
//					date, 
//					taskFrameNo, 
//					Optional.of(taskCodes)))
//					.isEmpty()).isTrue();
//		}

}
