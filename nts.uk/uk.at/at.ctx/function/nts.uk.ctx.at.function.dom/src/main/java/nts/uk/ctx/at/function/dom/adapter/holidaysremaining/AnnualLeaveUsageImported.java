package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingMinutes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.RemainingTimes;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.UsedTimes;

import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
public class AnnualLeaveUsageImported {
	
	/** 年月 */
	private YearMonth yearMonth;
	/** 月度使用日数 */
	private Double usedDays;
	/** 月度使用時間 */
	private Integer usedTime;
	/** 月度残日数 */
	private Double remainingDays;
	/** 月度残時間 */
	private Integer remainingTime;

	/** 月度使用回数 : 半日年休．使用数．回数付与前 */
	private Integer numOfuses;

	/** 月度残回数: 半日年休．残数．回数付与前*/
	private Integer numOfRemain;

	/** 月度残時間: 上限残時間．時間付与前 */
	private Integer monthlyRemainTime;
	
	public AnnualLeaveUsageImported(YearMonth yearMonth, Double usedDays, Integer usedTime, Double remainingDays,
			Integer remainingTime) {
		super();
		this.yearMonth = yearMonth;
		this.usedDays = usedDays;
		this.usedTime = usedTime;
		this.remainingDays = remainingDays;
		this.remainingTime = remainingTime;
	}
	
	
}
