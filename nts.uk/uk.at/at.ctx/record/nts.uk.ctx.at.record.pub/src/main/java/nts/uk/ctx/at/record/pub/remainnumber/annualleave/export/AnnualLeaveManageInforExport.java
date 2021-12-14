package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * @author loivt
 * 年休管理情報(仮)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeaveManageInforExport {
	/**
	 * 年月日
	 */
	private GeneralDate ymd;
	
	/**
	 * 使用日数
	 */
	private Double daysUsedNo;
	
	/**
	 * 使用時間
	 */
	private Integer usedMinutes;
	
	/**
	 * 予定実績区分
	 */
	private int scheduleRecordAtr;
	
	/**
	 * 勤務種類
	 */
	private String workTypeCode;
}
