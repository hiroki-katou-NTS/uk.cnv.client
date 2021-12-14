package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ExcessState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.AgreementOneYearTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.oneyear.OneYearTime;

/** ３６協定1年間 */
@AllArgsConstructor
@Getter
public class AgreementOneYear {

	/** 基本設定 */
	private OneYearErrorAlarmTime basic;
	/** 特例条項による上限 */
	private OneYearTime specConditionLimit;
	
	public AgreementOneYear() {
		this.basic = new OneYearErrorAlarmTime();
		this.specConditionLimit = new OneYearTime();
	}
	
	/**　３６協定年度設定を取り込む　*/
	public void updateWithEmpSet(OneYearErrorAlarmTime erAlTime) {
		
		this.specConditionLimit = OneYearTime.createWithCheck(erAlTime, this.specConditionLimit.getUpperLimit());
	}
	
	/** エラーチェック */
	public AgreementTimeStatusOfMonthly check(AgreementOneYearTime agreementTarget, AgreementOneYearTime legalUpperTarget) {
		
		/** エラーチェック */
		val legalState = this.specConditionLimit.check(legalUpperTarget);
		
		/** 法定上限対象時間の超過状態をチェック */
		if (legalState == ExcessState.ALARM_OVER) {
			
			return AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ALARM;
		}
		if (legalState == ExcessState.ERROR_OVER) {
			
			return AgreementTimeStatusOfMonthly.EXCESS_EXCEPTION_LIMIT_ERROR;
		}
		if (legalState == ExcessState.UPPER_LIMIT_OVER) {
			
			return AgreementTimeStatusOfMonthly.EXCESS_BG_GRAY;
		}
		
		/** エラーチェック */
		val agreementState = this.basic.check(agreementTarget);
		
		if (agreementState == ExcessState.ERROR_OVER) {
			return AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR;
		}
		
		if (agreementState == ExcessState.ALARM_OVER) {
			return AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM;
		}
		
		return AgreementTimeStatusOfMonthly.NORMAL;
	}

	// 	[2] 特例条項による上限のエラー時間を超えているか
	public Pair<Boolean, AgreementOneYearTime> checkErrorTimeExceeded(AgreementOneYearTime applicationTime){

		return specConditionLimit.isErrorTimeOver(applicationTime);
	}
	// 	[3] アラーム時間を計算する
	public AgreementOneYearTime calculateAlarmTime(AgreementOneYearTime applicationTime) {
		return specConditionLimit.calcAlarmTime(applicationTime);
	}
}