package nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.layer.dom.AggregateRoot;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.primitivevalue.HeaderBackgroundColor;
/**
 * 日次の勤怠項目の制御
 * @author tutk
 *
 */
@Getter
@Setter
public class ControlOfAttendanceItems extends AggregateRoot {
	
	/**会社ID*/
	private String companyID;
	
	/**勤怠項目ID*/
	private int itemDailyID;
	
	/**日別実績のヘッダ背景色*/
	private Optional<HeaderBackgroundColor> headerBgColorOfDailyPer;

	public ControlOfAttendanceItems(String companyID, int itemDailyID, HeaderBackgroundColor headerBgColorOfDailyPer) {
		super();
		this.companyID = companyID;
		this.itemDailyID = itemDailyID;
		this.headerBgColorOfDailyPer = Optional.ofNullable(headerBgColorOfDailyPer);
	}
	
	
	


}
