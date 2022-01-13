/******************************************************************
 * Copyright (c) 2017 Nittsu System to present.                   *
 * All right reserved.                                            *
 *****************************************************************/
package nts.uk.ctx.at.shared.infra.entity.calculation.holiday;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * The Class KshstHourPayAaddSet.
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSHMT_CALC_C_ADD_HD_HOU")
public class KshstHourPayAaddSet extends ContractUkJpaEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/** 主キー */
	@EmbeddedId
	public KshstHourPayAaddSetPK kshstHourPayAaddSetPK;
	
	/**  実働のみで計算する. */
	@Column(name = "CALC_PERMIUM_VACATION")
	public int calcPremiumVacation;
	
	/**  加算する. */
	@Column(name = "ADDITION1")
	public int addition1;
	
	/**  通常、変形の所定超過時. */
	@Column(name = "DEFORMAT_EXEC_VALUE")
	public int deformatExcValue;
	
	/**  育児・介護時間を含めて計算する. */
	@Column(name = "INC_CHILD_NURSE_CARE")
	public int incChildNursingCare;
	
	/*控除する*/
	@Column(name = "DEDUCT")
	public boolean deduct;
	
	/*インターバル免除時間を含めて計算する*/
	@Column(name = "CALC_INC_INTERVAL_EXEMP_TIME1")
	public int calculateIncludeIntervalExemptionTime1;
	
	/*実働のみで計算する*/
	@Column(name = "CALC_WORK_HOUR_VACATION")
	public int calcWorkHourVacation;
	
	/*加算する */
	@Column(name = "ADDITION2")
	public int addition2;
	
	/*育児・介護時間を含めて計算する*/
	@Column(name = "CALC_INC_CARE_TIME")
	public int calculateIncludCareTime;
	
	/*控除する*/
	@Column(name = "NOT_DEDUCT_LATE_LEAVE_EARLY")
	public int notDeductLateLeaveEarly;
	
	/*インターバル免除時間を含めて計算する*/
	@Column(name = "CALC_INC_INTERVAL_EXEMP_TIME2")
	public int calculateIncludeIntervalExemptionTime2;
	
	/*就業時間帯毎の設定を可能とする*/
	@Column(name = "ENABLE_SELECT_PER_WORK_HOUR1")
	public boolean enableSetPerWorkHour1;
	
	/*就業時間帯毎の設定を可能とする*/
	@Column(name = "ENABLE_SELECT_PER_WORK_HOUR2")
	public boolean enableSetPerWorkHour2;

	// 申請により取り消した場合も控除する
	@Column(name = "DEDUCT_BY_APPLICATION")
	public boolean deductByApplication;

	// 割増計算方法を設定する
	@Column(name = "SET_PREMIUM_CALC_METHOD")
	public boolean setPreCalcMethod;
	
	@OneToOne(optional = false)
		@JoinColumn(name = "CID", referencedColumnName="CID", insertable = false, updatable = false)
	public KshstHolidayAdditionSet holidayAddtimeSet;
	@Override
	protected Object getKey() {
		return kshstHourPayAaddSetPK;
	}
}
