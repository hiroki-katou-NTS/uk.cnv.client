package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.timesetting;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeStatusOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ExcessState;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.AgreementOneMonthTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthErrorAlarmTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.management.onemonth.OneMonthTime;

/** 36協定1ヶ月 */
@AllArgsConstructor
@Getter
public class AgreementOneMonth {

	/** 基本設定 */
	private OneMonthTime basic;
	/** 特例条項による上限 */
	private OneMonthTime specConditionLimit;
	
	public AgreementOneMonth() {
		this.basic = new OneMonthTime();
		this.specConditionLimit = new OneMonthTime();
	}
	
	/** 社員の３６協定年月設定を取り込む　*/
	public void updateWithEmpSet(OneMonthErrorAlarmTime erAlTime) {
		
		this.specConditionLimit = OneMonthTime.createWithCheck(erAlTime, this.specConditionLimit.getUpperLimit());
	}
	
	/** エラーチェック */
	public AgreementTimeStatusOfMonthly check(AttendanceTimeMonth agreementTarget, AttendanceTimeMonth legalLimitTarget) {
		/** エラーチェック */
		val legalState = this.specConditionLimit.check(legalLimitTarget);
		
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
		
		/** 月別実績の36協定時間状態を判断 */
		if (agreementState == ExcessState.ALARM_OVER) {
			
			return AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ALARM;
		}
		if (agreementState == ExcessState.ERROR_OVER 
				|| agreementState == ExcessState.UPPER_LIMIT_OVER) {
			
			return AgreementTimeStatusOfMonthly.EXCESS_LIMIT_ERROR;
		}
		
		return AgreementTimeStatusOfMonthly.NORMAL;
	}


	//	[2] 特例条項による上限のエラー時間を超えているか
	public Pair<Boolean, AgreementOneMonthTime> checkErrorTimeExceeded(AgreementOneMonthTime applicationTime) {

		return specConditionLimit.isErrorTimeOver(applicationTime);
	}

	// 	[3] アラーム時間を計算する
	public AgreementOneMonthTime calculateAlarmTime(AgreementOneMonthTime applicationTime) {
		return specConditionLimit.calcAlarmTime(applicationTime);
	}
}