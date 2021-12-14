/**
 * 
 */
package nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import nts.arc.testing.assertion.NtsAssert;

/**
 * @author laitv
 *
 */
public class EmployeeUnitPriceHistoryItemTest {
	
	@Test
	public void getters() {
		EmployeeUnitPriceHistoryItem empUnitPriceHisItem = EmployeeUnitPriceHistoryHelper.getEmployeeUnitPriceHistoryItemDefault();
		NtsAssert.invokeGetters(empUnitPriceHisItem);
	}
	
	/**
	 * Test func 社員時間単価を取得する() | getEmployeeHourlyUnitPrice()
	 * case1 
	 */
	@Test
	public void testgetEmployeeHourlyUnitPrice1() {
		EmployeeUnitPriceHistoryItem empUnitPriceHisItem = EmployeeUnitPriceHistoryHelper.getEmployeeUnitPriceHistoryItemDefault();
		
		Optional<WorkingHoursUnitPrice> result = empUnitPriceHisItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_2);
		
		assertThat(result.get().v()).isEqualTo(200);
	}
	
	/**
	 * Test func 社員時間単価を取得する() | getEmployeeHourlyUnitPrice()
	 * case1 
	 */
	@Test
	public void testgetEmployeeHourlyUnitPrice2() {
		EmployeeUnitPriceHistoryItem empUnitPriceHisItem = EmployeeUnitPriceHistoryHelper.getEmployeeUnitPriceHistoryItemDefault();
		
		Optional<WorkingHoursUnitPrice> result = empUnitPriceHisItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_10);
		
		assertThat(result).isEqualTo(Optional.empty());
	}
}
