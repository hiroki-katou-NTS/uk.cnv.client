package nts.uk.ctx.at.request.dom.setting.request.application.primitive;

/** 出来る 出来ない　区分 */
public enum CanAtr {
	/*出来ない*/
	NOTCAN(0),
	/*出来る*/
	CAN(1);

	public final int value;

	CanAtr(int value) {
		this.value = value;
	}
}
