package nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.usenumber.DayNumberOver;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.usenumber.TimeOver;

/**
 * 休暇上限超過消滅数
 * @author masaaki_jinno
 *
 */
@Getter
@AllArgsConstructor
public class LeaveOverNumber {

	/**
	 * 日数
	 */
	public DayNumberOver numberOverDays;

	/**
	 * 時間
	 */
	public Optional<TimeOver> timeOver;

	/**
	 * 休暇上限超過消滅数を加算
	 * @param aLeaveRemainingNumber
	 */
	public void add(LeaveOverNumber leaveOverNumber){

		// 日付加算
		numberOverDays = new DayNumberOver(this.numberOverDays.v() + leaveOverNumber.numberOverDays.v());

		// 時間加算
		if ( leaveOverNumber.timeOver.isPresent() ){
			if ( this.timeOver.isPresent() ){
				this.timeOver =
					Optional.of(new TimeOver(
							this.timeOver.get().v() +
							leaveOverNumber.timeOver.get().v()));
			}
			else
			{
				this.timeOver =
					Optional.of(new TimeOver(
						leaveOverNumber.timeOver.get().v()));
			}
		}
	}

	public LeaveOverNumber(Double days) {
		this.numberOverDays = new DayNumberOver(days == null? 0.0d: days.doubleValue());
		this.timeOver = Optional.empty();
	}
	public LeaveOverNumber(Double days, Integer minutes) {
		this.numberOverDays = new DayNumberOver(days == null? 0.0d: days.doubleValue());
		this.timeOver = minutes != null ? Optional.of(new TimeOver(minutes)) : Optional.empty();
	}

	public static LeaveOverNumber createFromJavaType(Double days, Integer minutes) {
		return new LeaveOverNumber(days, minutes);
	}

	/**
	 * ファクトリー
	 * @param numberOverDays 日数
	 * @param timeOver　時間
	 * @return LeaveUsedNumber 休暇上限超過消滅数
	*/
	public static LeaveOverNumber of(
			DayNumberOver numberOverDays,
			Optional<TimeOver> timeOver) {

		LeaveOverNumber domain = new LeaveOverNumber(numberOverDays, timeOver);
		domain.numberOverDays = numberOverDays;
		domain.timeOver = timeOver;
		return domain;
	}

	@Override
	public LeaveOverNumber clone() {

		LeaveOverNumber cloned;

		cloned = new LeaveOverNumber(
				numberOverDays,
				timeOver.map(c -> new TimeOver(c.v())));

		return cloned;
	}
	
	public TimeOver getTimeOverOrZero() {
		return this.getTimeOver().map(c -> c).orElse(new TimeOver(0));
	}

	public LeaveRemainingNumber toLeaveRemainingNumber() {
		return new LeaveRemainingNumber(this.getNumberOverDays().v(), this.getTimeOverOrZero().v());
	}
}
