package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingTime;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedTime;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.erroralarm.AnnualLeaveError;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.vacation.GrantBeforeAfterAtr;

/**
 * 時間年休上限
 * @author masaaki_jinno
 *
 */
@Getter
@AllArgsConstructor
public class TimeAnnualLeaveMax {

	/**
	 * 上限時間
	 */
	private MaxMinutes maxMinutes;

	/**
	 * 使用時間
	 */
	private LeaveUsedTime usedMinutes;

	/**
	 * 残時間
	 */
	private LeaveRemainingTime remainingMinutes;
	
	/** クローン */
	public TimeAnnualLeaveMax clone() {
		return new TimeAnnualLeaveMax(
				new MaxMinutes(maxMinutes.v()),
				new LeaveUsedTime(usedMinutes.v()), 
				new LeaveRemainingTime(remainingMinutes.v()));
	}

	/**
	 * コンストラクタ
	 */
	public TimeAnnualLeaveMax() {
		// 上限時間
		maxMinutes = new MaxMinutes(0);
		// 使用時間
		usedMinutes = new LeaveUsedTime(0);
		// 残時間更新
		remainingMinutes = calcRemainMinutess(maxMinutes, usedMinutes);
	}

	/**
	 * コンストラクタ
	 * [C-1] 年休使用時に作成する
	 */
	public TimeAnnualLeaveMax(MaxMinutes maxMinutesIn) {
		// 上限時間
		maxMinutes = new MaxMinutes(maxMinutesIn.v());
		// 使用時間
		usedMinutes = new LeaveUsedTime(0);
		// 残時間更新
		remainingMinutes = calcRemainMinutess(maxMinutes, usedMinutes);
	}

	/**
	 * コンストラクタ
	 * [C-2] 年休使用時に作成する
	 */
	public TimeAnnualLeaveMax(MaxMinutes maxMinutesIn, LeaveUsedTime usedMinutesIn) {
		// 上限時間
		maxMinutes = new MaxMinutes(maxMinutesIn.v());
		// 使用時間
		usedMinutes = new LeaveUsedTime(usedMinutesIn.v());
		// 残時間更新
		remainingMinutes = calcRemainMinutess(maxMinutes, usedMinutes);
	}

	/**
	 * 	[1]使用時間と残時間を更新
	 * @param tempAnnualLeaveMngs 暫定データ
	 * @return 	時間年休上限
	 */
	public TimeAnnualLeaveMax updateUsedTimesRemainingTimes(
			TempAnnualLeaveMngs tempAnnualLeaveMngs) {

		// 使用時間
		int usedMinutesTmp = this.usedMinutes.v();

		// 暫定データ．年休使用数.使用時間isPresent()
		if ( tempAnnualLeaveMngs.getUsedNumber().getUsedTime().isPresent()) {

			// $使用時間 ＋=	暫定データ．年休使用数．使用時間
			usedMinutesTmp += tempAnnualLeaveMngs.getUsedNumber().getUsedTime().get().v();
		}

		// [C-2] 年休使用時に作成する（＠上限時間、＄使用時間）
		return new TimeAnnualLeaveMax(
				new MaxMinutes(maxMinutes.v()),
				new LeaveUsedTime(usedMinutesTmp));
	}

	/**
	 * 時間年休上限を超過しているか
	 * @return
	 */
	public boolean IsExceed() {
		// 	@上限時間　＜　@使用時間
		return maxMinutes.v() < usedMinutes.v();
	}

	/**
	 * 	[pvt-1]残時間の計算
	 * @param maxMinutesIn 上限時間
	 * @param usedMinutesIn 使用時間
	 * @return 残時間
	 */
	private LeaveRemainingTime calcRemainMinutess(MaxMinutes maxMinutesIn, LeaveUsedTime usedMinutesIn) {
		return new LeaveRemainingTime(maxMinutesIn.v() - usedMinutesIn.v());
	}
	
	
	/**
	 * 時間年休上限超過チェック
	 * @param grantAtr 付与前、付与後の区分
	 * @return Optional<AnnualLeaveError> 年休エラー
	 */
	public Optional<AnnualLeaveError>  ExcessMaxErroeCheck(GrantBeforeAfterAtr grantAtr){
		if(!this.IsExceed()){
			return Optional.empty();
		}
		
		if(grantAtr == GrantBeforeAfterAtr.BEFORE_GRANT){
			return Optional.of(AnnualLeaveError.EXCESS_MAX_TIMEAL_BEFORE_GRANT);
		}else{
			return Optional.of(AnnualLeaveError.EXCESS_MAX_TIMEAL_AFTER_GRANT);
		}
	}

}
