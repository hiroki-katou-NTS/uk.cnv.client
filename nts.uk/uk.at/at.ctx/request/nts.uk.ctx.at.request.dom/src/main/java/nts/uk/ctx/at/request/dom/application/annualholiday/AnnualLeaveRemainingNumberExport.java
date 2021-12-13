package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author phongtq
 * 年休残数Export
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnualLeaveRemainingNumberExport {
	

	/**
	 * 年休残数（付与前）日数
	 */
	// private Double annualLeaveGrantPreDay;
	
	/**
	 * 年休残数（付与後）日数
	 */
	// private Double annualLeaveGrantPostDay;
	

	/**
	 * 年休残数（付与前）時間
	 */
	private Integer annualLeaveGrantPreTime;
	
	/**
	 * 年休残数（付与後）時間
	 */
	private Integer annualLeaveGrantPostTime;
	
	/**
	 * 残明細(付与前)
	 */
	// private List<AnnualLeaveRemainingDetailExport> detailGrantPre;
	
	/**
	 * 残明細(付与後)
	 */
	// private List<AnnualLeaveRemainingDetailExport> detailGrantPost;
	
	/**
	 * 半休残数回数
	 */
	// private Integer numberOfRemainGrant;
	
	/**
	 * 半休残数（付与前）回数
	 */
	// private Integer numberOfRemainGrantPre;
	
	/**
	 * 半休残数（付与後）回数
	 */
	// private Integer numberOfRemainGrantPost;
	
	/**
	 * 時間年休上限
	 */
	// private Integer timeAnnualLeaveGrant;
	
	/**
	 * 時間年休上限（付与前）
	 */
	// private Integer timeAnnualLeaveGrantPre;
	
	/**
	 * 時間年休上限（付与後）
	 */
	// private Integer timeAnnualLeaveGrantPost;
	
	/**
	 * 使用日数
	 */	
	// private Double usedDays;
	
	/**
	 * 使用（付与前）日数
	 */	
	// private Double usedNumberBeforeGrantDay;
	
	/**
	 * 使用（付与後）日数
	 */	
	// private Double usedNumberAfterGrantDay;
	
	/**
	 * 使用時間
	 */	
	// private Integer usedTime;
	
	/**
	 * 使用（付与前）時間
	 */	
	// private Integer usedNumberBeforeGrantTime;
	
	/**
	 * 使用（付与後）時間
	 */	
	// private Integer usedNumberAfterGrantTime;
	
	/**
	 * 時間年休使用回数
	 */
	// private Integer annualLeaveUsedTimes;
	
	/**
	 * 時間年休使用日数
	 */
	// private Integer annualLeaveUsedDayTimes;
	

}
