package nts.uk.ctx.at.record.dom.raisesalarytime.enums;

import lombok.AllArgsConstructor;

/**
 * 
 * @author nampt
 * 早退
 *
 */
@AllArgsConstructor
public enum SpecificDateAttr {

	// 0: 使用しない
	NOT_USE(0),
	// 1: 使用する
	USE(1);
	
	public final int value;
}
