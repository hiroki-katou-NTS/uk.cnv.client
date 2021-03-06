package nts.uk.ctx.at.shared.dom.remainingnumber.base;

import lombok.AllArgsConstructor;

@AllArgsConstructor
//休暇期限切れ状態
public enum LeaveExpirationStatus {

	/**
	 *  使用可能
	 */
	AVAILABLE(1),

	/**
	 *  期限切れ
	 */
	EXPIRED(0);

	public final int value;

	public boolean IsAVAILABLE() {
		return this.value == LeaveExpirationStatus.AVAILABLE.value;
	}

}
