package nts.uk.ctx.at.shared.dom.scherec.application.common;

import lombok.AllArgsConstructor;

/**
 * 反映状態
 *
 */
@AllArgsConstructor
public enum ReflectedStateShare {

	/** 未反映 */
	NOTREFLECTED(0, "未反映"),

	/** 反映待ち */
	WAITREFLECTION(1, "反映待ち"),

	/** 反映済 */
	REFLECTED(2, "反映済"),

	/** 取消済 */
	CANCELED(3, "取消済"),

	/** 差し戻し */
	REMAND(4, "差し戻し"),

	/** 否認 */
	DENIAL(5, "否認");

	public final int value;

	public final String name;
}
