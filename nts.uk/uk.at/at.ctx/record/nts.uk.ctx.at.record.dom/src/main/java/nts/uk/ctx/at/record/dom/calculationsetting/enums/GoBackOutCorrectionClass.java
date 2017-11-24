package nts.uk.ctx.at.record.dom.calculationsetting.enums;

import lombok.AllArgsConstructor;

/**
 * 
 * @author nampt
 * 直行直帰外出補正区分
 */
@AllArgsConstructor
public enum GoBackOutCorrectionClass {

	/* 直行直帰の場合は外出の打刻漏れを補正しない */
	DO_NOT_CORRECT(0),
	/* 直行直帰の場合は外出の打刻漏れを補正する */
	CORRECT(1);
	
	public final int value;
}
