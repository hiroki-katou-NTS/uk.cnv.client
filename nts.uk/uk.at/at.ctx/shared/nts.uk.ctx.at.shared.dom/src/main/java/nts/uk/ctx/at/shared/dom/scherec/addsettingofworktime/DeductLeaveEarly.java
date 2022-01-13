/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.scherec.addsettingofworktime;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.layer.dom.DomainObject;

/**
 * The Class DeductLeaveEarly.
 */
// 遅刻早退を控除する
@NoArgsConstructor
@Getter
public class DeductLeaveEarly extends DomainObject implements Serializable{

	/** Serializable */
	private static final long serialVersionUID = 1L;
	
	/** The deduct. */
	// 就業時間から控除する設定
	private EmTimezoneLateEarlyCommonSet deduct;
	
	/** The enable set per work hour. */
	// 就業時間帯毎の設定を可能とする
	private boolean enableSetPerWorkHour;

	/**
	 * @param deduct
	 * @param enableSetPerWorkHour
	 */
	public DeductLeaveEarly(int deduct, int enableSetPerWorkHour) {
		super();
		// Q&A 114126 value always False
		this.deduct = new EmTimezoneLateEarlyCommonSet(deduct == TRUE_CONST, false);
		this.enableSetPerWorkHour = enableSetPerWorkHour == TRUE_CONST ? true : false;
	}
	
	private static final int TRUE_CONST = 1;
	
	/**
	 * 全てtrueで作成する
	 * @return
	 */
	public static DeductLeaveEarly createAllTrue() {
		return new DeductLeaveEarly(TRUE_CONST, TRUE_CONST);
	}
	
	/**
	 * 控除区分を「控除する」に変更するだけ
	 * @return
	 */
	public DeductLeaveEarly changeDeduct() {
		int test = this.enableSetPerWorkHour?1:0;
		return new DeductLeaveEarly(0, test);
	}
}

