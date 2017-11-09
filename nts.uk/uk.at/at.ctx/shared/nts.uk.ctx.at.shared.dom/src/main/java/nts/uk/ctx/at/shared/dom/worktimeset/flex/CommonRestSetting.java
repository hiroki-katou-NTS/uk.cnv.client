/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.worktimeset.flex;

import lombok.Getter;
import nts.arc.layer.dom.DomainObject;
import nts.uk.ctx.at.shared.dom.worktimeset.difftime.RestTimeOfficeWorkCalcMethod;

/**
 * The Class CommonRestSetting.
 */
//共通の休憩設定
@Getter
public class CommonRestSetting extends DomainObject{
	
	/** The calculate method. */
	//休憩時間中に退勤した場合の計算方法
	private RestTimeOfficeWorkCalcMethod calculateMethod;
}
