package nts.uk.ctx.at.record.app.command.remainingnumber.otherhdinfo;

import java.math.BigDecimal;

import nts.uk.shr.pereg.app.PeregItem;

public class AddOtherHolidayInfoCommand {

	// 代休残数
	@PeregItem("IS00366")
	private BigDecimal remainNumber;

	// 振休残数
	@PeregItem("IS00368")
	private BigDecimal remainsLeft;

	// 公休残数
	@PeregItem("IS00369")
	private BigDecimal pubHdremainNumber;

	// 60H超休管理
	@PeregItem("IS00370")
	private int useAtr;

	// 発生単位
	@PeregItem("IS00371")
	private int occurrenceUnit;

	// 精算方法
	@PeregItem("IS00372")
	private int paymentMethod;

	// 60H超休残数
	@PeregItem("IS00374")
	private int extraHours;

}
