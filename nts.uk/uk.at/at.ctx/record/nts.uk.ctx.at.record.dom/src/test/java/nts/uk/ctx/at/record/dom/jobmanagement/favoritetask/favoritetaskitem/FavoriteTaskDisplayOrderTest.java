package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

/**
 * 
 * @author tutt
 *
 */
public class FavoriteTaskDisplayOrderTest {

	String employeeId = "employeeId";

	@Test
	public void test01() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId", 1));

		NtsAssert.invokeGetters(new FavoriteTaskDisplayOrder(employeeId, displayOrders));
	}

	@Test
	public void testAdd() {
		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));

		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder(employeeId, displayOrders);

		order.add("favId");

		assertThat(order.getDisplayOrders().size()).isEqualTo(4);
		
		for (FavoriteDisplayOrder o : displayOrders) {
			if (o.getFavId() == "favId") {
				assertThat(o.getOrder()).isEqualTo(1);
			}
			
			if (o.getFavId() == "favId1") {
				assertThat(o.getOrder()).isEqualTo(2);
			}
			
			if (o.getFavId() == "favId2") {
				assertThat(o.getOrder()).isEqualTo(3);
			}
			
			if (o.getFavId() == "favId3") {
				assertThat(o.getOrder()).isEqualTo(4);
			}
		}
	}

	@Test
	public void test02() {

		FavoriteTaskDisplayOrder addOrder = FavoriteTaskDisplayOrder.addNewFavTaskDisporder(employeeId, "favId1");

		assertThat(addOrder.getDisplayOrders().size()).isEqualTo(1);
	}
	
	@Test
	public void testDelete1() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId", 1));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder(employeeId, displayOrders);
		order.delete("favId");
		
		assertThat(order.getDisplayOrders().size()).isEqualTo(0);
	}
	
	@Test
	public void testDelete2() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder("employeeId1", displayOrders);
		order.delete("favId2");
		
		assertThat(order.getDisplayOrders().size()).isEqualTo(2);
		
		for (FavoriteDisplayOrder o : displayOrders) {
			if (o.getFavId() == "favId3") {
				assertThat(o.getOrder()).isEqualTo(2);
			}
			
			if (o.getFavId() == "favId1") {
				assertThat(o.getOrder()).isEqualTo(1);
			}
			
		}
	}
	
	@Test
	public void testDelete3() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder("employeeId2", displayOrders);
		order.delete("favId4");
		
		assertThat(order.getDisplayOrders().size()).isEqualTo(3);
	}
	
	@Test
	public void testChangeOrder1() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));
		displayOrders.add(new FavoriteDisplayOrder("favId4", 4));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder("employeeId3", displayOrders);
		order.changeOrder("favId4", 4, 2);
		
		for (FavoriteDisplayOrder o : displayOrders) {
			if (o.getFavId() == "favId4") {
				assertThat(o.getOrder()).isEqualTo(2);
			}
			
			if (o.getFavId() == "favId1") {
				assertThat(o.getOrder()).isEqualTo(1);
			}
			
			if (o.getFavId() == "favId2") {
				assertThat(o.getOrder()).isEqualTo(3);
			}
			
			if (o.getFavId() == "favId3") {
				assertThat(o.getOrder()).isEqualTo(4);
			}
		}
	
	}
	
	@Test
	public void testChangeOrder2() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));
		displayOrders.add(new FavoriteDisplayOrder("favId4", 4));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder("employeeId4", displayOrders);
		order.changeOrder("favId4", 4, 4);
		
		for (FavoriteDisplayOrder o : displayOrders) {
			if (o.getFavId() == "favId4") {
				assertThat(o.getOrder()).isEqualTo(4);
			}
			
			if (o.getFavId() == "favId1") {
				assertThat(o.getOrder()).isEqualTo(1);
			}
			
			if (o.getFavId() == "favId2") {
				assertThat(o.getOrder()).isEqualTo(2);
			}
			
			if (o.getFavId() == "favId3") {
				assertThat(o.getOrder()).isEqualTo(3);
			}
		}
	}
	
	@Test
	public void testChangeOrder3() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));
		displayOrders.add(new FavoriteDisplayOrder("favId4", 4));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder("employeeId5", displayOrders);
		order.changeOrder("favId3", 3, 4);
		
		for (FavoriteDisplayOrder o : displayOrders) {
			if (o.getFavId() == "favId4") {
				assertThat(o.getOrder()).isEqualTo(3);
			}
			
			if (o.getFavId() == "favId1") {
				assertThat(o.getOrder()).isEqualTo(1);
			}
			
			if (o.getFavId() == "favId2") {
				assertThat(o.getOrder()).isEqualTo(2);
			}
			
			if (o.getFavId() == "favId3") {
				assertThat(o.getOrder()).isEqualTo(4);
			}
		}
	}
	
	@Test
	public void testChangeOrder4() {

		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		displayOrders.add(new FavoriteDisplayOrder("favId1", 1));
		displayOrders.add(new FavoriteDisplayOrder("favId2", 2));
		displayOrders.add(new FavoriteDisplayOrder("favId3", 3));
		displayOrders.add(new FavoriteDisplayOrder("favId4", 4));
		displayOrders.add(new FavoriteDisplayOrder("favId5", 5));
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder("employeeId5", displayOrders);
		order.changeOrder("favId1", 1, 4);
		
		for (FavoriteDisplayOrder o : displayOrders) {
			if (o.getFavId() == "favId4") {
				assertThat(o.getOrder()).isEqualTo(3);
			}
			
			if (o.getFavId() == "favId1") {
				assertThat(o.getOrder()).isEqualTo(4);
			}
			
			if (o.getFavId() == "favId2") {
				assertThat(o.getOrder()).isEqualTo(1);
			}
			
			if (o.getFavId() == "favId3") {
				assertThat(o.getOrder()).isEqualTo(2);
			}
			
			if (o.getFavId() == "favId5") {
				assertThat(o.getOrder()).isEqualTo(5);
			}
		}
	}
	
	@Test
	public void testBusinessException() {
		List<FavoriteDisplayOrder> displayOrders = new ArrayList<>();
		
		for (int i = 1; i <= 99; i++) {
			displayOrders.add(new FavoriteDisplayOrder("favId" + i, i));
		}
		
		FavoriteTaskDisplayOrder order = new FavoriteTaskDisplayOrder(employeeId, displayOrders);

		NtsAssert.businessException("Msg_3257",
				() -> order.add("favId"));
	}

}
