package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoNumItem;

@AllArgsConstructor
@Getter
public class SuppInfoNumItemDto {
	/** 補足情報NO: 作業補足情報NO */
	private Integer suppInfoNo;
	
    /** 補足数値: 作業補足数値 */
	private Integer suppNumValue;

	public static SuppInfoNumItemDto fromDomain(SuppInfoNumItem domain) {
		
		return new SuppInfoNumItemDto(domain.getSuppInfoNo().v(), domain.getSuppNumValue()== null ? null : domain.getSuppNumValue().v());
	}
	
}
