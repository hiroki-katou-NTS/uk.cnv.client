package nts.uk.ctx.at.shared.app.find.scherec.dailyattendanceitem;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattendanceitem.ControlOfAttendanceItems;

@Getter
@Setter
public class ControlOfAttendanceItemsDto {
	/**会社ID*/
	private String companyID;
	
	/**勤怠項目ID*/
	private int itemDailyID;
	
	/**日別実績のヘッダ背景色*/
	private String headerBgColorOfDailyPer;

	public ControlOfAttendanceItemsDto(String companyID, int itemDailyID, String headerBgColorOfDailyPer) {
		super();
		this.companyID = companyID;
		this.itemDailyID = itemDailyID;
		this.headerBgColorOfDailyPer = headerBgColorOfDailyPer;
	}
	
	public static ControlOfAttendanceItemsDto fromDomain(ControlOfAttendanceItems domain) {
		return new ControlOfAttendanceItemsDto(
				domain.getCompanyID(),
				domain.getItemDailyID(),
				!domain.getHeaderBgColorOfDailyPer().isPresent() ?null:domain.getHeaderBgColorOfDailyPer().get().v()
		);
	}
}
