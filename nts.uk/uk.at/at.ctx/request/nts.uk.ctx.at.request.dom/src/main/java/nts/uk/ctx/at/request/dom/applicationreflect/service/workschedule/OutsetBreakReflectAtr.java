package nts.uk.ctx.at.request.dom.applicationreflect.service.workschedule;
/**
 * 振出・休出時反映する区分
 * @author dudt
 *
 */
public enum OutsetBreakReflectAtr {
	/**	反映しない */
	NOTREFLECT(0),
	/** 反映する */
	REFLECT(1);
	public int value;
	OutsetBreakReflectAtr(int type) {
		this.value = type;
	}

}
