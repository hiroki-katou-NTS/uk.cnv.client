package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 暫定データの年休使用数
 * @author masaaki_jinno
 *
 */
@Getter
@AllArgsConstructor
public class TempAnnualLeaveUsedNumber {

	/**
	 * 使用日数
	 */
	private Optional<TmpDailyLeaveUsedDayNumber> usedDayNumber;

	/**
	 * 使用時間
	 */
	private Optional<TmpDailyLeaveUsedTime> usedTime;

	/**
	 * コンストラクタ
	 */
	public TempAnnualLeaveUsedNumber() {
		usedDayNumber = Optional.empty();
		usedTime = Optional.empty();
	}

	/**
	 * コンストラクタ
	 * @param days 使用日数
	 * @param minutes 使用時間
	 */
	public TempAnnualLeaveUsedNumber(Double days, Integer minutes) {
		this.usedDayNumber = days != null ? Optional.of(new TmpDailyLeaveUsedDayNumber(days)) : Optional.empty();
		this.usedTime = minutes != null ? Optional.of(new TmpDailyLeaveUsedTime(minutes)) : Optional.empty();
	}

	/**
	 * 日数単位で使用しているかどうかを確認する
	 * @return
	 */
	public boolean isUseDay() {
		if ( usedDayNumber.isPresent() ) {
			return this.usedDayNumber.get().greaterThan(0.0);
		} else {
			return false;
		}
	}

	public TmpDailyLeaveUsedDayNumber getUsedDayNumberOrZero() {
		if(!this.usedDayNumber.isPresent()) return new TmpDailyLeaveUsedDayNumber(0.0);
		return this.getUsedDayNumber().get();
	}

	public TmpDailyLeaveUsedTime getUsedTimeOrZero() {
		if(!this.getUsedTime().isPresent()) return new TmpDailyLeaveUsedTime(0);
		return this.getUsedTime().get();
	}
}
