package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.remarks.RecordRemarks;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.remarks.RemarksOfDailyAttd;

@AllArgsConstructor
@Getter
public class RemarksOfDailyAttdCommand {
	/** 備考: 日別実績の備考 */
	private String remarks;
	
	/** 備考欄NO: int */
	private int remarkNo;
	
	
	public RemarksOfDailyAttd toDomain() {

		return new RemarksOfDailyAttd(new RecordRemarks(remarks), remarkNo);
	}




}
