package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.arc.time.GeneralDate;

/**
 * @author loivt
 * 年休管理情報Export
 */
@Data
@AllArgsConstructor
public class AnnualLeaveManageInforExport {
	
	/**
	 * 社員ID
	 */
	private String sID;
	
	/**
	 * 残数管理データID
	 */
	private String remainManaID;
	
	/**
	 * 年月日
	 */
	private GeneralDate ymd;
	
	/**
	 * 残数種類
	 */
	private int remainType;
	
	/**
	 * 作成元区分
	 */
	private int scheduleRecordAtr;
	
	/**
	 * 勤務種類
	 */
	private String workTypeCode;
	/**
	 * 使用日数
	 */
	private Double daysUsedNo;
	
	/**
	 * 使用時間
	 */
	private Integer usedMinutes;
	
	/**
	 * 時間消化休暇かどうか
	 */
	private boolean hourlyTimeType;
	/**
	 * 時間休暇種類
	 */
	private int appTimeType;
	
}
