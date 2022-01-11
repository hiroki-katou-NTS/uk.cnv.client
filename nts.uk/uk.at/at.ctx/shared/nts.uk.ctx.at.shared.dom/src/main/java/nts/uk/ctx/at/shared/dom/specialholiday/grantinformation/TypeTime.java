package nts.uk.ctx.at.shared.dom.specialholiday.grantinformation;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.specialholiday.NextSpecialHolidayGrantParameter;

/**
 * 付与するタイミングの種類
 *
 * @author tanlv
 *
 */
public enum TypeTime {

	/**
	 * 付与テーブルを参照して付与する
	 */
	REFER_GRANT_DATE_TBL(1),

	/**
	 * 指定日に付与する
	 */
	GRANT_SPECIFY_DATE(2),

	/**
	 * 期間で付与する
	 */
	GRANT_PERIOD(3);


	public int value;

	TypeTime(int type){
		this.value = type;
	}

	public static TypeTime toEnum(int value){
		return EnumAdaptor.valueOf(value, TypeTime.class);
	}

}
