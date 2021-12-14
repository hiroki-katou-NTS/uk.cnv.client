/**
 * 10:16:29 AM Jun 7, 2017
 */
package nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.enums;

import lombok.AllArgsConstructor;

/**
 * @author hungnm
 *
 */
@AllArgsConstructor
public enum TimeItemTypeAtr {
	
	NORMAL_TYPE(0),

	SPECIAL_TYPE(1);

	public final int value;
	
	/**
	 * 加給か
	 * @return 加給
	 */
	public boolean isNomalType() {
		return this.equals(NORMAL_TYPE);
	}
	
	/**
	 * 特定日加給か
	 * @return 特定日加給
	 */
	public boolean isSpecialType() {
		return this.equals(SPECIAL_TYPE);
	}
}
