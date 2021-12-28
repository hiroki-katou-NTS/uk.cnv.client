/**
 * 
 */
package nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory;

import java.util.ArrayList;
import java.util.List;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * @author laitv
 *
 */
public class EmployeeUnitPriceHistoryHelper {
	
	public static EmployeeUnitPriceHistory getEmployeeUnitPriceHistoryDefault() {
		List<DateHistoryItem> historyItems = new ArrayList<DateHistoryItem>();
		DateHistoryItem his1 = new DateHistoryItem("historyId1", new DatePeriod(GeneralDate.ymd(2021, 6, 1), GeneralDate.ymd(2021, 6, 30)));
		DateHistoryItem his2 = new DateHistoryItem("historyId2", new DatePeriod(GeneralDate.ymd(2021, 7, 1), GeneralDate.ymd(2021, 7, 30)));
		historyItems.add(his1);
		historyItems.add(his2);
		return new EmployeeUnitPriceHistory("sid", historyItems);
	}
	
	public static EmployeeUnitPriceHistoryItem getEmployeeUnitPriceHistoryItemDefault() {
		List<UnitPricePerNumber> unitPrices = new ArrayList<UnitPricePerNumber>();
		UnitPricePerNumber unitPrice1 = new UnitPricePerNumber(UnitPrice.Price_1, new WorkingHoursUnitPrice(100));
		UnitPricePerNumber unitPrice2 = new UnitPricePerNumber(UnitPrice.Price_2, new WorkingHoursUnitPrice(200));
		UnitPricePerNumber unitPrice3 = new UnitPricePerNumber(UnitPrice.Price_3, new WorkingHoursUnitPrice(300));
		UnitPricePerNumber unitPrice4 = new UnitPricePerNumber(UnitPrice.Price_4, new WorkingHoursUnitPrice(400));
		UnitPricePerNumber unitPrice5 = new UnitPricePerNumber(UnitPrice.Price_5, new WorkingHoursUnitPrice(500));
		UnitPricePerNumber unitPrice6 = new UnitPricePerNumber(UnitPrice.Price_6, new WorkingHoursUnitPrice(600));
		UnitPricePerNumber unitPrice7 = new UnitPricePerNumber(UnitPrice.Price_7, new WorkingHoursUnitPrice(700));
		UnitPricePerNumber unitPrice8 = new UnitPricePerNumber(UnitPrice.Price_8, new WorkingHoursUnitPrice(800));
		UnitPricePerNumber unitPrice9 = new UnitPricePerNumber(UnitPrice.Price_9, new WorkingHoursUnitPrice(900));
//		UnitPricePerNumber unitPrice10 = new UnitPricePerNumber(UnitPrice.Price_10, new WorkingHoursUnitPrice(1000));

		unitPrices.add(unitPrice1);
		unitPrices.add(unitPrice2);
		unitPrices.add(unitPrice3);
		unitPrices.add(unitPrice4);
		unitPrices.add(unitPrice5);
		unitPrices.add(unitPrice6);
		unitPrices.add(unitPrice7);
		unitPrices.add(unitPrice8);
		unitPrices.add(unitPrice9);
//		unitPrices.add(unitPrice10);

		return new EmployeeUnitPriceHistoryItem("sid","hisId", unitPrices);
	}

}
