package nts.uk.ctx.at.request.dom.application.annualholiday;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基準日時点年休残数
 * @author phongtq
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReNumAnnLeaReferenceDateExport {
	
	/**
	 * 年休情報
	 */
	private AnnualLeaveInfoExport annualLeaveRemainNumberExport;
	
	/**
	 * 年休管理情報Export
	 */
	// private List<AnnualLeaveManageInforExport> annualLeaveManageInforExports;
	
	/**
	 * 年休付与残数データExport
	 */
	private List<AnnualLeaveGrantExport> annualLeaveGrantExports;
	
	/**
	 * 出勤率
	 */
	// private Double attendanceRate;
	
	/**
	 * 労働日数
	 */
	// private Double workingDays;
	
	/**
	 * 年休残日数
	 */
	private Double remainingDays;
	
	/**
	 * 年休残時間
	 */
	private Integer remainingTime;
}
