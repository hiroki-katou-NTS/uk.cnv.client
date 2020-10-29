package nts.uk.ctx.at.schedule.dom.employeeinfo.employeesort;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

public class OrderedListTest {

	@Test
	public void getters() {
		OrderedList orderedList = new OrderedList(SortType.CLASSIFY, SortOrder.SORT_ASC);
		NtsAssert.invokeGetters(orderedList);
	}

}
