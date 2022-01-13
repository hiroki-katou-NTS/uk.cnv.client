package nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author sonnlb
 *
 */
@Value
@AllArgsConstructor
public class AnnualLeaveRemainingNumberImport {
	/**
	 * 年休残数（付与前）日数
	 */
	private Double annualLeaveGrantPreDay;
	
	/**
	 * 年休残数（付与前）時間
	 */
	private Integer annualLeaveGrantPreTime;
	
	/**
	 * 半休残数（付与前）回数
	 */
	private Integer numberOfRemainGrantPre;
	
	/**
	 * 時間年休上限（付与前）
	 */
	private Integer timeAnnualLeaveWithMinusGrantPre;
	
	/**
	 * 年休残数（付与後）日数
	 */
	private Double annualLeaveGrantPostDay;
	
	/**
	 * 年休残数（付与後）時間
	 */
	private Integer annualLeaveGrantPostTime;
	
	/**
	 * 半休残数（付与後）回数
	 */
	private Integer numberOfRemainGrantPost;
	
	/**
	 * 時間年休上限（付与後））
	 */
	private Integer timeAnnualLeaveWithMinusGrantPost;
	
	/**
	 * 年休残数日数
	 */
	//private Double annualLeaveGrantDay;
	
	/**
	 * 年休残数時間
	 */
	//private Integer annualLeaveGrantTime;
	
	
	/**
	 * 出勤率
	 */
	//private Double attendanceRate;
	
	/**
	 * 労働日数
	 */
	//private Double workingDays;
}
