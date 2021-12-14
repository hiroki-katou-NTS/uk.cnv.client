package nts.uk.ctx.at.function.dom.adapter.holidaysremaining;

import lombok.Getter;
import lombok.Setter;
import nts.arc.time.YearMonth;

@Setter
@Getter
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
