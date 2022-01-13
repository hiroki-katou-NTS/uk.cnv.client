package nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave;

import java.util.List;

import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.reserveleave.ReserveLeave;

/**
 * 積立年休情報残数
 * @author shuichu_ishida
 */
@Getter
public class ReserveLeaveRemainingNumberInfo implements Cloneable {

	/** 積立年休（マイナスなし） */
	private ReserveLeave reserveLeaveNoMinus;
	/** 積立年休（マイナスあり） */
	private ReserveLeave reserveLeaveWithMinus;
	/** 未消化数 */
	private ReserveLeaveUndigestedNumber reserveLeaveUndigestedNumber;

	/**
	 * コンストラクタ
	 */
	public ReserveLeaveRemainingNumberInfo(){

		this.reserveLeaveNoMinus = new ReserveLeave();
		this.reserveLeaveWithMinus = new ReserveLeave();
		this.reserveLeaveUndigestedNumber = new ReserveLeaveUndigestedNumber();
	}

	/**
	 * ファクトリー
	 * @param reserveLeaveNoMinus 積立年休（マイナスなし）
	 * @param reserveLeaveWithMinus 積立年休（マイナスあり）
	 * @return 積立年休情報残数
	 */
	public static ReserveLeaveRemainingNumberInfo of(
			ReserveLeave reserveLeaveNoMinus,
			ReserveLeave reserveLeaveWithMinus,
			ReserveLeaveUndigestedNumber reserveLeaveUndigestedNumber){

		ReserveLeaveRemainingNumberInfo domain = new ReserveLeaveRemainingNumberInfo();
		domain.reserveLeaveNoMinus = reserveLeaveNoMinus;
		domain.reserveLeaveWithMinus = reserveLeaveWithMinus;
		domain.reserveLeaveUndigestedNumber = reserveLeaveUndigestedNumber;
		return domain;
	}

	@Override
	public ReserveLeaveRemainingNumberInfo clone() {
		ReserveLeaveRemainingNumberInfo cloned = new ReserveLeaveRemainingNumberInfo();
		try {
			cloned.reserveLeaveNoMinus = this.reserveLeaveNoMinus.clone();
			cloned.reserveLeaveWithMinus = this.reserveLeaveWithMinus.clone();
			cloned.reserveLeaveUndigestedNumber = this.reserveLeaveUndigestedNumber.clone();
		}
		catch (Exception e){
			throw new RuntimeException("ReserveLeaveRemainingNumber clone error.");
		}
		return cloned;
	}

	/**
	 * 積立年休付与情報を更新
	 * @param remainingDataList 積立年休付与残数データリスト
	 * @param grantPeriodAtr 付与前付与後
	 */
	public void updateRemainingNumber(
			List<ReserveLeaveGrantRemainingData> remainingDataList, GrantBeforeAfterAtr grantPeriodAtr){

		// 積立年休付与残数データから積立年休（マイナスあり）を作成
		this.reserveLeaveWithMinus.createRemainingNumberFromGrantRemaining(remainingDataList, grantPeriodAtr);

		// 積立年休（マイナスなし）を積立年休（マイナスあり）で上書き　＆　積立年休からマイナスを削除
		this.reserveLeaveNoMinus.setValueFromRealReserveLeave(this.reserveLeaveWithMinus);
	}
}
