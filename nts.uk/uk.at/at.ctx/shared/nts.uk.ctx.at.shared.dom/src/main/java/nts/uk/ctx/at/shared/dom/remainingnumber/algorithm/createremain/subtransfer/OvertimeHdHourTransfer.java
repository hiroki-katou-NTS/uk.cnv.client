package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;

/**
 * @author thanh_nx
 *
 *         時間外労働時間（振替用）
 */
@AllArgsConstructor
@Getter
@Setter
public class OvertimeHdHourTransfer {

	// 枠NO
	private int no;

	// 時間
	private AttendanceTime time;

	// 振替時間
	private AttendanceTime transferTime;

}
