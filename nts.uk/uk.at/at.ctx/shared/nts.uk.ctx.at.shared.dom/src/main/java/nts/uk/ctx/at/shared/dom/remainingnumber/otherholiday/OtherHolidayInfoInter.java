package nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.publicholiday.PublicHolidayRemain;
@Getter
@AllArgsConstructor
public class OtherHolidayInfoInter {
	private String cid;
	private PublicHolidayRemain pubHD;
	private ExcessLeaveInfo exLeav;
	private BigDecimal remainNumber;
	private BigDecimal remainLeft;
}
