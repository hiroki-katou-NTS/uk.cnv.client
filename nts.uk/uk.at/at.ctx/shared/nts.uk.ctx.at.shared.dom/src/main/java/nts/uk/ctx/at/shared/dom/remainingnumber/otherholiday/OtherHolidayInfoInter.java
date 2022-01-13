package nts.uk.ctx.at.shared.dom.remainingnumber.otherholiday;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.employee.carryForwarddata.PublicHolidayCarryForwardData;
import nts.uk.ctx.at.shared.dom.remainingnumber.excessleave.ExcessLeaveInfo;
@Getter
@AllArgsConstructor
public class OtherHolidayInfoInter {
	private String cid;
	private PublicHolidayCarryForwardData pubHD;
	private ExcessLeaveInfo exLeav;
	private BigDecimal remainNumber;
	private BigDecimal remainLeft;
}
