package nts.uk.ctx.at.shared.app.find.dailyattdcal.empunitpricehistory;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.EmployeeUnitPriceHistoryItem;
import nts.uk.ctx.at.shared.dom.dailyattdcal.empunitpricehistory.WorkingHoursUnitPrice;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.employeeunitpricehistory.UnitPrice;
import nts.uk.shr.com.history.DateHistoryItem;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregItem;
import nts.uk.shr.pereg.app.find.dto.PeregDomainDto;

@Setter
public class EmployeeUnitPriceDto extends PeregDomainDto {

	/**
	 * 社員ID
	 */
	@PeregEmployeeId
	private String sId;

	/**
	 * 期間
	 */
	@PeregItem("IS01077")
	private String period;

	/**
	 * 開始日
	 */
	@Getter
	@PeregItem("IS01078")
	private GeneralDate startDate;

	/**
	 * 終了日
	 */
	@PeregItem("IS01079")
	private GeneralDate endDate;
	
	/**
	 * 単価1
	 */
	@PeregItem("IS01080")
	private Integer unitPrice1;
	
	/**
	 * 単価2
	 */
	@PeregItem("IS01081")
	private Integer unitPrice2;
	
	/**
	 * 単価3
	 */
	@PeregItem("IS01082")
	private Integer unitPrice3;
	
	/**
	 * 単価4
	 */
	@PeregItem("IS01083")
	private Integer unitPrice4;
	
	/**
	 * 単価5
	 */
	@PeregItem("IS01084")
	private Integer unitPrice5;
	
	/**
	 * 単価6
	 */
	@PeregItem("IS01085")
	private Integer unitPrice6;
	
	/**
	 * 単価7
	 */
	@PeregItem("IS01086")
	private Integer unitPrice7;
	
	/**
	 * 単価8
	 */
	@PeregItem("IS01087")
	private Integer unitPrice8;
	
	/**
	 * 単価9
	 */
	@PeregItem("IS01088")
	private Integer unitPrice9;
	
	/**
	 * 単価10
	 */
	@PeregItem("IS01089")
	private Integer unitPrice10;
	
	public EmployeeUnitPriceDto(String recordId) {
		super(recordId);
	}
	
	public static EmployeeUnitPriceDto createEmployeeUnitPriceDto(String employeeId, DateHistoryItem dateHistoryItem, 
			EmployeeUnitPriceHistoryItem employeeUnitPriceItem) {
		EmployeeUnitPriceDto dto = new EmployeeUnitPriceDto(dateHistoryItem.identifier());
		dto.setSId(employeeId);
		dto.setStartDate(dateHistoryItem.start());
		dto.setEndDate(dateHistoryItem.end());
		
		dto.setUnitPrice1(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_1).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice2(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_2).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice3(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_3).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice4(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_4).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice5(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_5).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice6(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_6).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice7(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_7).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice8(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_8).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice9(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_9).orElse(new WorkingHoursUnitPrice(0)).v());
		dto.setUnitPrice10(employeeUnitPriceItem.getEmployeeHourlyUnitPrice(UnitPrice.Price_10).orElse(new WorkingHoursUnitPrice(0)).v());
		return dto;
	}
	
	public static EmployeeUnitPriceDto createEmployeeUnitPriceDtoCps013(String employeeId, DateHistoryItem dateHistoryItem, 
			EmployeeUnitPriceHistoryItem employeeUnitPriceItem, Map<String, Object> enums) {
		return null;
	}
	
}
