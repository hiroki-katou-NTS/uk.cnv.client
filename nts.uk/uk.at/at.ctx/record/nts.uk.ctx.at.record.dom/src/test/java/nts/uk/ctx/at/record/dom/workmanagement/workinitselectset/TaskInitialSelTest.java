/**
 * 
 */
package nts.uk.ctx.at.record.dom.workmanagement.workinitselectset;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

public class TaskInitialSelTest {
	
	@Test
	public void getters() {
		TaskInitialSel data = TaskInitialSelHelper.getTaskInitialSelDefault();
		NtsAssert.invokeGetters(data);
	}
	
	/*
	 * test Func changeTaskItem
	 */
	@Test
	public void changeTaskItemTest() {
		TaskInitialSel taskItem = TaskInitialSelHelper.getTaskInitialSelDefault();
		TaskItem param = TaskInitialSelHelper.getParamSetTaskItem();
		
		taskItem.changeTaskItem(param);
		
		assertThat(taskItem.getTaskItem()).isEqualToComparingFieldByField(param);

	}

}
