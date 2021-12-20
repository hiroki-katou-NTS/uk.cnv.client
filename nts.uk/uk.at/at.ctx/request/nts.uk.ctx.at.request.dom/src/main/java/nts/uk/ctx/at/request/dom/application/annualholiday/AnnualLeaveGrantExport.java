package nts.uk.ctx.at.request.dom.application.annualholiday;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;

/**
 * @author phongtq
 * 年休付与残数データExport
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeaveGrantExport {
	
	/**
	 * ID
	 */
	private  String leaveID;
	
	/**
	 * 社員ID
	 */
	private String employeeId;
	
	/**
	 * 付与日
	 */
	private GeneralDate grantDate;
	
	/**
	 * 期限日
	 */
	private GeneralDate deadline;
	
	/**
	 * 期限切れ状態
	 */
	private int expirationStatus;
	
	/**
	 * 登録種別
	 */
	private int registerType;
	
	/**
	 * 付与日数
	 */
	private Double grantNumber;
	
	/**
	 * 付与時間
	 */
	private Integer grantNumberMinutes;
	
	/**
	 * 使用日数
	 */
	private Double daysUsedNo;
	
	/**
	 * 使用時間
	 */
	private Integer usedMinutes;
	
	/**
	 * 積み崩し日数
	 */
	private Double stowageDays;
	
	/**
	 * 上限超過消滅日数
	 */
	private Double leaveOverLimitNumberOverDays;
	
	/**
	 * 上限超過消滅時間
	 */
	private Integer leaveOverLimitNumberOverTimes;
	
	/**
	 * 残日数
	 */
	private Double remainDays;
	
	/**
	 * 残時間
	 */
	private Integer remainMinutes;


}
