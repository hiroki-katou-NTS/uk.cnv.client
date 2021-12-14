package nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.createremain.subtransfer;

import lombok.AllArgsConstructor;

/**
 * @author thanh_nx
 *
 *         代休振替処理モード
 */
@AllArgsConstructor
public enum SubsTransferProcessMode {

	// 日別集計
	DAILY(0),

	// 月別集計
	MONTH(1);

	public final int value;

}
