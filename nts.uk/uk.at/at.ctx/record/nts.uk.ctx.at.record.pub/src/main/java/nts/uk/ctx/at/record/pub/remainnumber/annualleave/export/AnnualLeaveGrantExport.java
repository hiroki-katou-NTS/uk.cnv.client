package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * @author loivt
 * 年休付与情報(仮)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeaveGrantExport {
	/**
	 * 付与日
	 */
	private GeneralDate grantDate;
	
	/**
	 * 付与数
	 */
	private Double grantNumber;
	
	/**
	 * 使用日数
	 */
	private Double daysUsedNo;
	
	/**
	 * 使用時間
	 */
	private Integer usedMinutes;
	
	/**
	 * 残日数
	 */
	private Double remainDays;
	
	/**
	 * 残時間
	 */
	private Integer remainMinutes;
	
	/**
	 * 期限
	 */
	private GeneralDate deadline;

}
