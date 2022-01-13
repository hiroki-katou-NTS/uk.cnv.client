package nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;

/**
 * 月次の勤怠項目の制御
 * @author tutk
 *
 */
@Getter
@Setter
public class ControlOfMonthlyItems extends AggregateRoot {
	/**会社ID*/
	private String companyId;
	/**勤怠項目ID*/
	private int itemMonthlyId;
	
	/**月別実績のヘッダ背景色*/
	private Optional<HeaderBackgroundColor> headerBgColorOfMonthlyPer;

	public ControlOfMonthlyItems(String companyId, int itemMonthlyId, HeaderBackgroundColor headerBgColorOfMonthlyPer) {
		super();
		this.companyId = companyId;
		this.itemMonthlyId = itemMonthlyId;
		this.headerBgColorOfMonthlyPer = Optional.ofNullable(headerBgColorOfMonthlyPer);
	}

}
