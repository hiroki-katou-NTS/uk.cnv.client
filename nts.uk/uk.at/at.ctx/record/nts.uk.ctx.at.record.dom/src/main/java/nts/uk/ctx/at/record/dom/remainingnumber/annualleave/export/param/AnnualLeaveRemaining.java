package nts.uk.ctx.at.record.dom.remainingnumber.annualleave.export.param;

import java.util.List;
import java.util.Optional;

import lombok.Getter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUndigestNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeave;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveMaxRemainingTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveRemainingDetail;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.AnnualLeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.annualleave.HalfDayAnnualLeave;

/**
 * 年休情報残数
 * @author shuichu_ishida
 */
@Getter
public class AnnualLeaveRemaining implements Cloneable {

	/** 年休（マイナスなし） */
	private AnnualLeave annualLeaveNoMinus;
	/** 年休（マイナスあり） */
	private AnnualLeave annualLeaveWithMinus;
	/** 半日年休（マイナスなし） */
	private Optional<HalfDayAnnualLeave> halfDayAnnualLeaveNoMinus;
	/** 半日年休（マイナスあり） */
	private Optional<HalfDayAnnualLeave> halfDayAnnualLeaveWithMinus;
	/** 時間年休（マイナスなし） */
	private Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveNoMinus;
	/** 時間年休（マイナスあり） */
	private Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveWithMinus;
	/** 年休未消化数 */
	private Optional<AnnualLeaveUndigestNumber> annualLeaveUndigestNumber;

	/**
	 * コンストラクタ
	 */
	public AnnualLeaveRemaining(){

		this.annualLeaveNoMinus = new AnnualLeave();
		this.annualLeaveWithMinus = new AnnualLeave();
		this.halfDayAnnualLeaveNoMinus = Optional.empty();
		this.halfDayAnnualLeaveWithMinus = Optional.empty();
		this.timeAnnualLeaveNoMinus = Optional.empty();
		this.timeAnnualLeaveWithMinus = Optional.empty();
		this.annualLeaveUndigestNumber = Optional.empty();
	}

	/**
	 * ファクトリー
	 * @param annualLeaveNoMinus 年休（マイナスなし）
	 * @param annualLeaveWithMinus 年休（マイナスあり）
	 * @param halfDayAnnualLeaveNoMinus 半日年休（マイナスなし）
	 * @param halfDayAnnualLeaveWithMinus 半日年休（マイナスあり）
	 * @param timeAnnualLeaveNoMinus 時間年休（マイナスなし）
	 * @param timeAnnualLeaveWithMinus 時間年休（マイナスあり）
	 * @param annualLeaveUndigestNumber 年休未消化数
	 * @return 年休情報残数
	 */
	public static AnnualLeaveRemaining of(
			AnnualLeave annualLeaveNoMinus,
			Optional<HalfDayAnnualLeave> halfDayAnnualLeaveNoMinus,
			Optional<HalfDayAnnualLeave> halfDayAnnualLeaveWithMinus,
			Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveNoMinus,
			Optional<AnnualLeaveMaxRemainingTime> timeAnnualLeaveWithMinus,
			Optional<AnnualLeaveUndigestNumber> annualLeaveUndigestNumber){

		AnnualLeaveRemaining domain = new AnnualLeaveRemaining();
		domain.annualLeaveNoMinus = annualLeaveNoMinus;
		domain.halfDayAnnualLeaveNoMinus = halfDayAnnualLeaveNoMinus;
		domain.halfDayAnnualLeaveWithMinus = halfDayAnnualLeaveWithMinus;
		domain.timeAnnualLeaveNoMinus = timeAnnualLeaveNoMinus;
		domain.timeAnnualLeaveWithMinus = timeAnnualLeaveWithMinus;
		domain.annualLeaveUndigestNumber = annualLeaveUndigestNumber;
		return domain;
	}

	@Override
	public AnnualLeaveRemaining clone() {
		AnnualLeaveRemaining cloned = new AnnualLeaveRemaining();
		try {
			cloned.annualLeaveNoMinus = this.annualLeaveNoMinus.clone();
			cloned.annualLeaveWithMinus = this.annualLeaveWithMinus.clone();
			if (this.halfDayAnnualLeaveNoMinus.isPresent()){
				cloned.halfDayAnnualLeaveNoMinus = Optional.of(this.halfDayAnnualLeaveNoMinus.get().clone());
			}
			if (this.halfDayAnnualLeaveWithMinus.isPresent()){
				cloned.halfDayAnnualLeaveWithMinus = Optional.of(this.halfDayAnnualLeaveWithMinus.get().clone());
			}
			if (this.timeAnnualLeaveNoMinus.isPresent()){
				cloned.timeAnnualLeaveNoMinus = Optional.of(this.timeAnnualLeaveNoMinus.get().clone());
			}
			if (this.timeAnnualLeaveWithMinus.isPresent()){
				cloned.timeAnnualLeaveWithMinus = Optional.of(this.timeAnnualLeaveWithMinus.get().clone());
			}
			if (this.annualLeaveUndigestNumber.isPresent()){
				cloned.annualLeaveUndigestNumber = Optional.of(this.annualLeaveUndigestNumber.get().clone());
			}
		}
		catch (Exception e){
			throw new RuntimeException("AnnualLeaveRemainingNumber clone error.");
		}
		return cloned;
	}

	/**
	 * 年休付与情報を更新
	 * @param remainingDataList 年休付与残数データリスト
	 * @param afterGrantAtr 付与後フラグ
	 * @param maxData 年休上限データ
	 * @param endDay 終了日
	 */
	public void updateRemainingNumber(List<AnnualLeaveGrantRemainingData> remainingDataList,
			GrantBeforeAfterAtr grantPeriodAtr, AnnualLeaveMaxData maxData, GeneralDate endDay) {

		// 年休付与残数データから年休（マイナスあり）を作成
		this.annualLeaveWithMinus.createRemainingNumberFromGrantRemaining(remainingDataList, grantPeriodAtr);

		// 年休（マイナスなし）を年休（マイナスあり）で上書き　＆　年休からマイナスを削除
		this.annualLeaveNoMinus = updateRemainingNumberNoMinus(this.annualLeaveWithMinus);
		
		//半日年休　
		if(maxData.getHalfdayAnnualLeaveMax().isPresent()){
			//更新する
			if(this.halfDayAnnualLeaveWithMinus.isPresent()){
				this.halfDayAnnualLeaveWithMinus = Optional.of(this.halfDayAnnualLeaveWithMinus.get()
						.update(maxData.getHalfdayAnnualLeaveMax().get(), grantPeriodAtr));
			}else{
				HalfDayAnnualLeave  halfDayAnnual = new HalfDayAnnualLeave();
				this.halfDayAnnualLeaveWithMinus = Optional.of(halfDayAnnual
						.update(maxData.getHalfdayAnnualLeaveMax().get(), grantPeriodAtr));
			}
			//残数超過分を補正する
			this.halfDayAnnualLeaveNoMinus = Optional.of(this.halfDayAnnualLeaveWithMinus.get().correctTheExcess());
		}else{
			//半日年休（マイナスあり）と半日年休（マイナスなし）Optional.empty()にする
			this.halfDayAnnualLeaveWithMinus = Optional.empty();
			this.halfDayAnnualLeaveNoMinus = Optional.empty();
		}
		
		//時間年休
		if(maxData.getTimeAnnualLeaveMax().isPresent()){
			//更新する
			if(this.timeAnnualLeaveWithMinus.isPresent()){
				this.timeAnnualLeaveWithMinus = Optional.of(this.timeAnnualLeaveWithMinus.get()
						.update(maxData.getTimeAnnualLeaveMax().get(), grantPeriodAtr));
			}else{
				AnnualLeaveMaxRemainingTime leaveMaxRemainingTime = new AnnualLeaveMaxRemainingTime();
					this.timeAnnualLeaveWithMinus = Optional.of(leaveMaxRemainingTime
							.update(maxData.getTimeAnnualLeaveMax().get(), grantPeriodAtr));
			}
			//残数超過分を補正する
			this.timeAnnualLeaveNoMinus = Optional.of(this.timeAnnualLeaveWithMinus.get().correctTheExcess());
			
		}else{
			//時間年休（マイナスあり）と時間年休（マイナスなし）Optional.empty()にする
			this.timeAnnualLeaveWithMinus = Optional.empty();
			this.timeAnnualLeaveNoMinus = Optional.empty();
		}
		
		//未消化数　更新する
		if(this.annualLeaveUndigestNumber.isPresent()){
			this.annualLeaveUndigestNumber = Optional.of(this.annualLeaveUndigestNumber.get().calcUndigestNumber(remainingDataList, endDay));
		}else{
			AnnualLeaveUndigestNumber UndigestNumber = new AnnualLeaveUndigestNumber();
			this.annualLeaveUndigestNumber = Optional.of(UndigestNumber.calcUndigestNumber(remainingDataList, endDay));
		}
	}

	/**
	 * 年休付与残数データから年休の年休残数を作成
	 * @param annualLeaveWithMinus 年休（マイナスあり）
	 */
	private AnnualLeave updateRemainingNumberNoMinus(AnnualLeave annualLeaveWithMinus){

		// 年休（マイナスなし）を年休（マイナスあり）で上書き
		AnnualLeave annualLeaveNoMinus = annualLeaveWithMinus.clone();

		// 年休からマイナスを削除
		// 「年休．残数付与前」「年休．残数付与後」をそれぞれ処理

		// 残数付与前
		updateRemainingNumberWithMinusToNoMinus(
				annualLeaveNoMinus.getRemainingNumberInfo().getRemainingNumberBeforeGrant(),
				annualLeaveNoMinus.getUsedNumberInfo().getUsedNumberBeforeGrant());

		// 残数付与後
		if ( annualLeaveNoMinus.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().isPresent()
				&& annualLeaveNoMinus.getUsedNumberInfo().getUsedNumberAfterGrantOpt().isPresent() ){
			updateRemainingNumberWithMinusToNoMinus(
				annualLeaveNoMinus.getRemainingNumberInfo().getRemainingNumberAfterGrantOpt().get(),
				annualLeaveNoMinus.getUsedNumberInfo().getUsedNumberAfterGrantOpt().get());
		}

		// 合計使用数を求める
		// 年休(マイナスなし)．使用数．合計 ← 年休(マイナスなし)．使用数．付与前 + 年休(マイナスなし)．使用数．付与後
		annualLeaveNoMinus.getUsedNumberInfo().setTotal();

		return annualLeaveNoMinus;
	}

	/**
	 * 年休（マイナスあり）を年休（マイナスなし）に変換
	 * @param annualLeaveRemainingNumber　年休残数
	 * @param AnnualLeaveUseNumber　年休使用数
	 */
	private void updateRemainingNumberWithMinusToNoMinus(
			AnnualLeaveRemainingNumber annualLeaveRemainingNumber,
			AnnualLeaveUsedNumber annualLeaveUsedNumber){

		// パラメータ「年休残数．合計残日数」と「年休残数．合計残時間」をチェック

		// 合計残日数<0　or 合計残時間 < 0
		double remainDays = annualLeaveRemainingNumber.getTotalRemainingDays().v();
		int remainTimes = 0;
		if ( annualLeaveRemainingNumber.getTotalRemainingTime().isPresent() ){
			remainTimes = annualLeaveRemainingNumber.getTotalRemainingTime().get().v();
		}

		if ( remainDays < 0 || remainTimes < 0 ){

			// 年休．使用数からマイナス分を引く
			if ( remainDays < 0 ){
				double useDays = annualLeaveUsedNumber.getUsedDays().map(c -> c.v()).orElse(0d) + remainDays;
				annualLeaveUsedNumber.setUsedDays(Optional.of(new AnnualLeaveUsedDayNumber(useDays)));
			}
			if ( remainTimes < 0 ){
				if ( annualLeaveUsedNumber.getUsedTime().isPresent() ){
					int useTimes = annualLeaveUsedNumber.getUsedTime().get().v() + remainTimes;
					annualLeaveUsedNumber.setUsedTime(Optional.of(new UsedMinutes(useTimes)));
				}
			}

			// 年休残数．明細を取得
			List<AnnualLeaveRemainingDetail> detailList
				= annualLeaveRemainingNumber.getDetails();

			// 取得した年休残明細でループ
			detailList.forEach(c->{
				// 年休残明細．日数←0
				c.setDays(new AnnualLeaveRemainingDayNumber(0.0));
				// 年休残明細.時間 ← 0
				c.setTime(Optional.of(new AnnualLeaveRemainingTime(0)));
			});

			// 年休．残数．合計残日数←0
			annualLeaveRemainingNumber.setTotalRemainingDays(new AnnualLeaveRemainingDayNumber(0.0));

			// 年休．残数．合計残時間←0
			annualLeaveRemainingNumber.setTotalRemainingTime(Optional.of(new AnnualLeaveRemainingTime(0)));
		}
	}

	public void addUndigestNumber(LeaveUndigestNumber undigestNumber) {
		if(!this.annualLeaveUndigestNumber.isPresent()) {
			this.annualLeaveUndigestNumber = Optional.of(new AnnualLeaveUndigestNumber());
		}
		this.annualLeaveUndigestNumber.get().add(undigestNumber);
	}

	/**
	 * 年休残数不足エラーチェック
	 * @param aggregatePeriodWork 年休集計期間WORK
	 * @return
	 */
	public Optional<AnnualLeaveError> remainShortageCheck(AggregatePeriodWork aggregatePeriodWork){

		// 年休残数がマイナスかチェック
		// 年休(マイナスあり)．残数．合計をチェック

		// 合計残日数>=0 and 合計残時間 >= 0
		if ( !annualLeaveWithMinus.getRemainingNumberInfo().getRemainingNumber().isMinus() ) {
			return Optional.empty();
		}

		// 合計残日数<0

		// 付与前付与後を判断する
		if ( aggregatePeriodWork.getGrantWork().judgeGrantPeriodAtr()
				== GrantBeforeAfterAtr.BEFORE_GRANT ) { // 付与前
			// 「年休エラー．年休不足エラー（付与前）」を返す
			return Optional.of(AnnualLeaveError.SHORTAGE_AL_OF_UNIT_DAY_BFR_GRANT);

		} else { // 付与後
			// 「年休エラー．年休不足エラー（付与後）」を返す
			return Optional.of(AnnualLeaveError.SHORTAGE_AL_OF_UNIT_DAY_AFT_GRANT);
		}
	}

}
