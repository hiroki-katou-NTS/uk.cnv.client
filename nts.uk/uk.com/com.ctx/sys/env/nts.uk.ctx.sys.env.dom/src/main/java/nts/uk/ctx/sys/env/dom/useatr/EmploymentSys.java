package nts.uk.ctx.sys.env.dom.useatr;

import lombok.AllArgsConstructor;

/**
 * 人事システム
 * @author yennth
 *
 */
@AllArgsConstructor
public enum EmploymentSys {
	/**0:しない。*/
	NotUse(0),
	/** 1：する */
	Use(1);
	public final int value;
}
