/******************************************************************
 * Copyright (c) 2016 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave;

import java.io.Serializable;
import java.util.Optional;

import lombok.Builder;
import nts.uk.ctx.at.shared.dom.vacation.setting.ManageDistinct;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.LimitedTimeHdDays;

/**
 * 時間年休の上限日数 The Class YearVacationTimeMaxDay.
 *
 */
@Builder
public class TimeAnnualMaxDay implements Serializable {

	/**
	 * Serializable
	 */
	private static final long serialVersionUID = 1L;

	/** The manage max day vacation. */
	// 管理区分
	public ManageDistinct manageType;

	/** The reference. */
	// 参照先
	public MaxDayReference reference;

	/** The max time day. */
	// 会社一律上限日数
	public MaxTimeDay maxNumberUniformCompany;

	
	public boolean isManaged(){
		return this.manageType.equals(ManageDistinct.YES);
	}
	/**
	 * [3] 時間年休上限日数を取得
	 * @param fromGrantTableDays
	 * @return
	 */
	public Optional<LimitedTimeHdDays> getLimitedTimeHdDays(Optional<LimitedTimeHdDays> fromGrantTableDays) {
		if (!this.isManaged())
			return Optional.empty();

		return this.reference.equals(MaxDayReference.CompanyUniform)
				? Optional.of(this.maxNumberUniformCompany.toLimitedTimeHdDays()) : fromGrantTableDays;
	}

}
