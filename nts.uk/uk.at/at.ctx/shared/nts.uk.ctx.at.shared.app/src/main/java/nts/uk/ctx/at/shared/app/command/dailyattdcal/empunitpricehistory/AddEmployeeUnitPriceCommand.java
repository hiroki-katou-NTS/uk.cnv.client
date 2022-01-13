package nts.uk.ctx.at.shared.app.command.dailyattdcal.empunitpricehistory;

import java.math.BigDecimal;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.shr.pereg.app.PeregEmployeeId;
import nts.uk.shr.pereg.app.PeregItem;

@Getter
public class AddEmployeeUnitPriceCommand {
	
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
	private BigDecimal unitPrice1;
	
	/**
	 * 単価2
	 */
	@PeregItem("IS01081")
	private BigDecimal unitPrice2;
	
	/**
	 * 単価3
	 */
	@PeregItem("IS01082")
	private BigDecimal unitPrice3;
	
	/**
	 * 単価4
	 */
	@PeregItem("IS01083")
	private BigDecimal unitPrice4;
	
	/**
	 * 単価5
	 */
	@PeregItem("IS01084")
	private BigDecimal unitPrice5;
	
	/**
	 * 単価6
	 */
	@PeregItem("IS01085")
	private BigDecimal unitPrice6;
	
	/**
	 * 単価7
	 */
	@PeregItem("IS01086")
	private BigDecimal unitPrice7;
	
	/**
	 * 単価8
	 */
	@PeregItem("IS01087")
	private BigDecimal unitPrice8;
	
	/**
	 * 単価9
	 */
	@PeregItem("IS01088")
	private BigDecimal unitPrice9;
	
	/**
	 * 単価10
	 */
	@PeregItem("IS01089")
	private BigDecimal unitPrice10;
}
