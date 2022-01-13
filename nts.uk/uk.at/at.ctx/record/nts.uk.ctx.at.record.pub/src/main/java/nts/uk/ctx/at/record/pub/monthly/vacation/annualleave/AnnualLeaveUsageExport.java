package nts.uk.ctx.at.record.pub.monthly.vacation.annualleave;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveRemainingDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.grantremainingdata.daynumber.AnnualLeaveUsedDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;

/**
 * 年休利用状況
 * @author shuichu_ishida
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeaveUsageExport {

	/** 年月 */
	private YearMonth yearMonth;
	/** 月度使用日数 */
	private AnnualLeaveUsedDayNumber usedDays;
	/** 月間使用時間 */
	private Optional<UsedMinutes> usedTime;
	/** 月度残日数 */
	private AnnualLeaveRemainingDayNumber remainingDays;
	/** 月度残時間 */
	private Optional<RemainingMinutes> remainingTime;
	// Update RQ255 V4
  	/** 月度使用回数 : 半日年休．使用数．回数付与前 */
	private Optional<UsedTimes> numOfuses;

	/** 月度残回数: 半日年休．残数．回数付与前*/
	private Optional<RemainingTimes> numOfRemain;

	/** 月度残時間: 上限残時間．時間付与前 */
	 private Optional<RemainingMinutes> monthlyRemainTime;

	 public AnnualLeaveUsageExport(YearMonth yearMonth,
                                   AnnualLeaveUsedDayNumber usedDays,
                                   Optional<UsedMinutes> usedTime,
                                   AnnualLeaveRemainingDayNumber remainingDays,
                                   Optional<RemainingMinutes> remainingTime
                                   ){
	     this.yearMonth = yearMonth;
         this.usedDays = usedDays;
         this.usedTime = usedTime;
	     this.remainingDays = remainingDays;
	     this.remainingTime = remainingTime;

     }

}
