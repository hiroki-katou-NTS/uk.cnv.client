package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.RegisterFavoriteTaskService.Require;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;

/**
 * 
 * @author tutt
 *
 */
@RunWith(JMockit.class)
public class RegisterFavoriteTaskServiceTest {

	@Injectable
	private Require require;

	@Test
	public void testException1() {

		List<FavoriteTaskItem> displayOrders = new ArrayList<>();
		List<TaskContent> contents = new ArrayList<>();
		contents.add(new TaskContent(1, new WorkCode("1")));

		displayOrders.add(new FavoriteTaskItem("employeeId", new FavoriteTaskName("name1"), new ArrayList<>()));
		new Expectations() {
			{
				require.getBySameSetting(anyString, contents);
				result = displayOrders;
			}
		};

		NtsAssert.businessException("Msg_2245",
				() -> RegisterFavoriteTaskService.add(require, "employeeId", 
						new FavoriteTaskName("name"), contents));
	}

	@Test
	public void testException2() {

		List<FavoriteTaskItem> displayOrders = new ArrayList<>();

		displayOrders.add(new FavoriteTaskItem("employeeId", new FavoriteTaskName("name1"), new ArrayList<>()));
		displayOrders.add(new FavoriteTaskItem("employeeId", new FavoriteTaskName("name2"), new ArrayList<>()));

		new Expectations() {
			{
				require.getBySameSetting(anyString, new ArrayList<>());
				result = displayOrders;
			}
		};

		NtsAssert.businessException("Msg_2245", 
				() -> RegisterFavoriteTaskService.add(require, "employeeId",
				new FavoriteTaskName("name"), new ArrayList<>()));
	}

	@Test
	public void test1() {
		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();

		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		Optional<FavoriteTaskDisplayOrder> optdisplayOrder = Optional
				.of(new FavoriteTaskDisplayOrder("employeeId", displayOrders));
		
		List<TaskContent> favoriteContents = new ArrayList<>();
		favoriteContents.add(new TaskContent(1, new WorkCode("a")));
		favoriteContents.add(new TaskContent(2, new WorkCode("b")));
		
		new Expectations() {
			{
				require.getBySameSetting(anyString, new ArrayList<>());
				result = new ArrayList<>();

				require.get(anyString);
				result = optdisplayOrder;
			}
		};

		AtomTask result = RegisterFavoriteTaskService.add(require, "employeeId", new FavoriteTaskName("name"),
				new ArrayList<>());

		result.run();
		
		new Verifications() {{
			require.insert(new FavoriteTaskItem(anyString, anyString, new FavoriteTaskName(anyString), new ArrayList<>()));
			times = 0;
			
			require.insert(new FavoriteTaskDisplayOrder(anyString, new ArrayList<>()));
			times = 0;
			
			require.update(optdisplayOrder.get());
			times = 1;
		}};
	}

	@Test
	public void test2() {

		Optional<FavoriteTaskDisplayOrder> optdisplayOrder = Optional.empty();

		new Expectations() {
			{
				require.getBySameSetting(anyString, new ArrayList<>());
				result = new ArrayList<>();

				require.get(anyString);
				result = optdisplayOrder;
			}
		};

		AtomTask result = RegisterFavoriteTaskService.add(require, "employeeId", new FavoriteTaskName("name"),
				new ArrayList<>());

		NtsAssert.atomTask(() -> result,
				any -> require.insert(new FavoriteTaskDisplayOrder("employeeId", new ArrayList<>())));

	}

}
