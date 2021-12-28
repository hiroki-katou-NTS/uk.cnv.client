package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.remarks.RemarksOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemarksOfDailyAttdDto {
	/** 備考: 日別実績の備考 */
	
	private String remarks;
	
	/** 備考欄NO: int */
	private int remarkNo;

	public static RemarksOfDailyAttdDto fromDomain(RemarksOfDailyAttd domain) {
		return new RemarksOfDailyAttdDto(domain.getRemarks().v(), domain.getRemarkNo());
	}
}
