package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SuppInfoTimeItem;

@AllArgsConstructor
@Getter
public class SuppInfoTimeItemDto {
	/** 補足情報NO: 作業補足情報NO */
	private Integer suppInfoNo;

	/** 補足時間 */
	private Integer attTime;

	public static SuppInfoTimeItemDto fromDomain(SuppInfoTimeItem domain) {

		return new SuppInfoTimeItemDto(domain.getSuppInfoNo().v(), domain.getAttTime()== null ? null :domain.getAttTime().v());
	}
}
