package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockArt;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;

/**
 * @author thanh_nx
 *
 *         打刻種類Output
 */
@AllArgsConstructor
@Data
public class StampTypeOutput {

	// 時刻変更区分
	private ChangeClockArt timeChangeClassifi;

	// 外出理由
	private Optional<GoingOutReason> outReason;
}
