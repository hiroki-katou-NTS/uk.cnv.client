package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;

/**
 * 
 * @author tutt
 *
 */
public class FavoriteTaskItemTest {

	@Test
	public void test1() {
		List<TaskContent> favoriteContents = new ArrayList<>();
		favoriteContents.add(new TaskContent(1, new WorkCode("1")));

		FavoriteTaskItem item = new FavoriteTaskItem("employeeId", "favoriteId", new FavoriteTaskName("name"),
				favoriteContents);
		NtsAssert.invokeGetters(item);

	}

	@Test
	public void test2() {
		List<TaskContent> favoriteContents = new ArrayList<>();
		favoriteContents.add(new TaskContent(1, new WorkCode("1")));
		
		FavoriteTaskItem item = new FavoriteTaskItem("employeeId", new FavoriteTaskName("name"), favoriteContents);
		item.setTaskName(new FavoriteTaskName("name1"));
		NtsAssert.invokeGetters(item);
	}

}
