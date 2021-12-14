package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm;

import lombok.AllArgsConstructor;

/**
 * 年休エラー
 * @author shuichi_ishida
 */
@AllArgsConstructor
public enum AnnualLeaveError {
	/** 時間年休上限超過エラー（付与前） */
	EXCESS_MAX_TIMEAL_BEFORE_GRANT(0),
	/** 時間年休上限超過エラー（付与後） */
	EXCESS_MAX_TIMEAL_AFTER_GRANT(1),
	/** 半日年休上限超過エラー（付与前） */
	EXCESS_MAX_HALFDAY_BEFORE_GRANT(2),
	/** 半日年休上限超過エラー（付与後） */
	EXCESS_MAX_HALFDAY_AFTER_GRANT(3),
	/** 年休不足エラー（付与前） */
	SHORTAGE_AL_OF_UNIT_DAY_BFR_GRANT(4),
	/** 年休不足エラー（付与後） */
	SHORTAGE_AL_OF_UNIT_DAY_AFT_GRANT(5);

	public final int value;
}
