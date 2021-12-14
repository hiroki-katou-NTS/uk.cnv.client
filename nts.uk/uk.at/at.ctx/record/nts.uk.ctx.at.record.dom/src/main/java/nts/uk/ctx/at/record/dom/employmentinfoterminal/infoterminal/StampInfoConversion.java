package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal;

import java.util.Optional;

import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.LeaveCategory;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;

/**
 * @author thanh_nx
 *
 *         打刻情報変換
 */
public interface StampInfoConversion {

	//NRから変換する
	public Optional<ChangeClockArt> convertFromNR(LeaveCategory leavCategory);
	
}
