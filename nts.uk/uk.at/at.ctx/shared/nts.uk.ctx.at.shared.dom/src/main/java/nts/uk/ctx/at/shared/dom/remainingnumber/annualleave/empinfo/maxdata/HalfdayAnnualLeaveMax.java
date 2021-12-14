package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.vacation.setting.annualpaidleave.AnnualNumberDay;

/**
 * 半日年休上限
 * @author masaaki_jinno
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HalfdayAnnualLeaveMax {

	/**
	 * 上限回数
	 */
	private AnnualNumberDay maxTimes;

	/**
	 * 使用回数
	 */
	private UsedTimes usedTimes;

	/**
	 * 残回数
	 */
	private RemainingTimes remainingTimes;
	
	/**
	 * コンストラクタ
	 * 	[C-1] 年休付与時に作成する
	 * 説明：年休付与の時に新しい＜半日年休上限＞を作る。
	 * @param maxTimes 上限回数
	 */
	public HalfdayAnnualLeaveMax(AnnualNumberDay annualNumberDayIn) {

		// 上限回数
		maxTimes = new AnnualNumberDay(annualNumberDayIn.v());
		// 0
		usedTimes = new UsedTimes(0);

		// [pvt-1]残回数の計算（上限回数，0）
		remainingTimes = calcRemainTimes(maxTimes, usedTimes);
	}

	/**
	 * コンストラクタ
	 * 	[C-2] 年休使用時に作成する
	 * 説明：年休使用の時に新しい＜半日年休上限＞を作る。
	 * @param maxTimes 上限回数
	 * @param usedTimesIn 使用回数
	 */
	public HalfdayAnnualLeaveMax(AnnualNumberDay maxTimesIn, UsedTimes usedTimesIn) {

		// 上限回数
		maxTimes = new AnnualNumberDay(maxTimesIn.v());
		// 使用回数
		usedTimes = new UsedTimes(usedTimesIn.v());

		// [pvt-1]残回数の計算（上限回数，使用回数）
		remainingTimes = calcRemainTimes(maxTimes, usedTimes);
	}

	/**
	 * 	[1]使用回数と残回数を更新
	 * @param tempAnnualLeaveMngs 暫定データ
	 * @return 	半日年休上限
	 */
	public HalfdayAnnualLeaveMax updateUsedTimesRemainingTimes(
			TempAnnualLeaveMngs tempAnnualLeaveMngs) {

		// 使用回数
		int usedTimesTmp = this.usedTimes.v();

		// 暫定データ．年休使用数.使用日数isPresent()
		if ( tempAnnualLeaveMngs.getUsedNumber().getUsedDayNumber().isPresent()) {

			// 暫定データ．年休使用数．使用日数 =＝ 0.5
			if ( tempAnnualLeaveMngs.getUsedNumber().getUsedDayNumber().get().v() == 0.5 ) {
				// ＄使用回数　+＝	1
				usedTimesTmp += 1;
			}
		}

		// 半日年休上限を作成して返す
		return new HalfdayAnnualLeaveMax(
				new AnnualNumberDay(this.maxTimes.v()),
				new UsedTimes(usedTimesTmp));
	}

	/**
	 * 半日年休上限を超過しているか
	 * @return
	 */
	public boolean IsExceed() {
		// 	@上限回数　＜　@使用回数
		return maxTimes.v() < usedTimes.v();
	}

	/**
	 * 	[pvt-1]残回数の計算
	 * @param maxTimesIn 	上限回数
	 * @param usedTimesIn 使用回数
	 * @return 残回数
	 */
	private RemainingTimes calcRemainTimes(AnnualNumberDay maxTimesIn, UsedTimes usedTimesIn) {

		return new RemainingTimes(maxTimesIn.v() - usedTimesIn.v());

	}

	public void updateMaxTimes(AnnualNumberDay maxTimes) {
		this.maxTimes = maxTimes;
		updateRemainingTimes();
	}

	public void updateUsedTimes(UsedTimes usedTimes) {
		this.usedTimes = usedTimes;
		updateRemainingTimes();
	}

	public void update(AnnualNumberDay maxTimes, UsedTimes usedTimes) {
		this.maxTimes = maxTimes;
		this.usedTimes = usedTimes;
		updateRemainingTimes();
	}

	private void updateRemainingTimes() {
		this.remainingTimes = new RemainingTimes(this.maxTimes.v() - this.usedTimes.v());
	}

	/**
	 * 残回数の計算
	 * @param maxTimes 上限回数
	 * @param usedTimes 使用回数
	 * @return 残回数
	 */
	private RemainingTimes calcRemainingTimes(MaxTimes maxTimesIn, UsedTimes usedTimesIn) {
		RemainingTimes remainingTimesOut = new RemainingTimes(maxTimesIn.v() - usedTimesIn.v());
		if ( remainingTimesOut.v() < 0 ) {
			return new RemainingTimes(0);
		} else {
			return remainingTimesOut;
		}
	}
	
	/** クローン */
	public HalfdayAnnualLeaveMax clone() {
		return new HalfdayAnnualLeaveMax(
				new AnnualNumberDay(maxTimes.v()),
				new UsedTimes(usedTimes.v()), 
				new RemainingTimes(remainingTimes.v()));
	}

	/**
	 * 半日年休上限超過チェック
	 * @param grantAtr　付与前、付与後の区分
	 * @return Optional<AnnualLeaveError> 年休エラー
	 */
	public Optional<AnnualLeaveError>  ExcessMaxErroeCheck(GrantBeforeAfterAtr grantAtr){
		if(!this.IsExceed()){
			return Optional.empty();
		}
		
		if(grantAtr == GrantBeforeAfterAtr.BEFORE_GRANT){
			return Optional.of(AnnualLeaveError.EXCESS_MAX_HALFDAY_BEFORE_GRANT);
		}else{
			return Optional.of(AnnualLeaveError.EXCESS_MAX_HALFDAY_AFTER_GRANT);
		}
	}
}
