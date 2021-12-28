package nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;

/**
 * 休暇付与数
 * @author masaaki_jinno
 *
 */
@Getter
@Setter
public class LeaveGrantNumber {

	/**
	 * 日数
	 */
	protected LeaveGrantDayNumber days;

	/**
	 * 時間
	 */
	protected Optional<LeaveGrantTime> minutes;

	public LeaveGrantNumber() {
		days = new LeaveGrantDayNumber(0.0);
		minutes = Optional.empty();
	}

	/**
	 * ファクトリー
	 * @param days 日数
	 * @param minutes 時間
	 * @return 休暇付与数
	 */
	public static LeaveGrantNumber of(
			LeaveGrantDayNumber days,
			Optional<LeaveGrantTime> minutes) {

		LeaveGrantNumber domain = new LeaveGrantNumber();
		domain.days = days;
		domain.minutes = minutes;
		return domain;
	}

	@Override
	public LeaveGrantNumber clone() {
		LeaveGrantNumber cloned = new LeaveGrantNumber();

		cloned.days = new LeaveGrantDayNumber(days.v());
		if ( minutes.isPresent() ){
			cloned.minutes = Optional.of(new LeaveGrantTime(minutes.get().v()));
		}

		return cloned;
	}

	protected LeaveGrantNumber(double days, Integer minutes) {
		this.days = new LeaveGrantDayNumber(days);
		this.minutes = minutes != null ? Optional.of(new LeaveGrantTime(minutes)) : Optional.empty();
	}

	public static LeaveGrantNumber createFromJavaType(double days, Integer minutes) {
		return new LeaveGrantNumber(days, minutes);
	}

	public LeaveGrantTime getMinutesOrZero() {
		if(!this.getMinutes().isPresent())return new LeaveGrantTime(0);
		return this.getMinutes().get();
	}

	public boolean isZero() {
		return this.days.v().equals(0.0) && this.getMinutesOrZero().valueAsMinutes() == 0;
	}
	
	public LeaveRemainingNumber calcDiff(String companyId, String employeeId, GeneralDate baseDate,
			LeaveRemainingNumber remain, LeaveRemainingNumber.RequireM3 require) {
		LeaveRemainingNumber differencer = new LeaveRemainingNumber(this.getDays().v(), this.getMinutesOrZero().v());
		differencer.digestLeaveUsedNumber(require, remain, companyId, employeeId, baseDate).clone();
		return differencer;
	}


}
