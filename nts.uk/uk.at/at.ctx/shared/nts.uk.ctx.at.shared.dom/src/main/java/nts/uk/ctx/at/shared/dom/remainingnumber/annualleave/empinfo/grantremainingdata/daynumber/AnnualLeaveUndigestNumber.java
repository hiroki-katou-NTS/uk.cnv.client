package nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.AnnualLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUndigestTime;

/**
 * 年休未消化数
 * @author masaaki_jinno
 *
 */
@Getter
@Setter
public class AnnualLeaveUndigestNumber extends LeaveUndigestNumber {



	public AnnualLeaveUndigestNumber() {
		super();
	}

	private AnnualLeaveUndigestNumber(double days, Integer minutes) {
		// super(days, minutes);
		this.days = new AnnualLeaveUndigestDayNumber(days);
		this.minutes = minutes != null ? Optional.of(new AnnualLeaveUndigestTime(minutes)) : Optional.empty();
	}
	
	private AnnualLeaveUndigestNumber(LeaveUndigestDayNumber days, Optional<LeaveUndigestTime> minutes) {
		this.days = days;
		this.minutes = minutes;
	}

	public static AnnualLeaveUndigestNumber createFromJavaType(double days, Integer minutes) {
		return new AnnualLeaveUndigestNumber(days, minutes);
	}

	@Override
	public AnnualLeaveUndigestNumber clone() {
		AnnualLeaveUndigestNumber cloned;
		try {
			int int_minutes = 0;
			if ( this.minutes.isPresent() ){
				int_minutes = minutes.get().v();
			}

			cloned = AnnualLeaveUndigestNumber.createFromJavaType(
					days.v(), int_minutes);
		}
		catch (Exception e){
			throw new RuntimeException("AnnualLeaveUndigestNumber clone error.");
		}
		return cloned;
	}

	//[1]計算する
	public AnnualLeaveUndigestNumber calcUndigestNumber(List<AnnualLeaveGrantRemainingData> remainingDataList, GeneralDate endDay){
		List<AnnualLeaveGrantRemainingData> expiredList = remainingDataList.stream()
				.filter(x -> x.getDeadline().afterOrEquals(endDay) && 
						x.getExpirationStatus() == LeaveExpirationStatus.EXPIRED &&
						!(x.isDummyData()))
				.collect(Collectors.toList());
		
		LeaveUndigestDayNumber day = new LeaveUndigestDayNumber(expiredList.stream()
								.mapToDouble(x -> x.getDetails().getRemainingNumber().getDays().v())
								.sum());
		LeaveUndigestTime time = new LeaveUndigestTime(expiredList.stream()
								.mapToInt(x -> 
								x.getDetails().getRemainingNumber().getMinutes().isPresent()?
										x.getDetails().getRemainingNumber().getMinutes().get().v() : 0)
								.sum());
		
		return new AnnualLeaveUndigestNumber(day,Optional.of(time));
		
	}
}
