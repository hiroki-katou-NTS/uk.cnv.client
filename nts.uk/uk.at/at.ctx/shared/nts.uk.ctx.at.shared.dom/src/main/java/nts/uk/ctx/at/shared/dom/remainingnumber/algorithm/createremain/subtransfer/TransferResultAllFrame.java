package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * @author thanh_nx
 *
 *振替結果(全枠)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResultAllFrame {
	
	//残りの振替可能時間
	private AttendanceTime timeRemain;
	
	//振替をした後の時間
	private List<OvertimeHdHourTransfer> timeAfterTransfer;

}
