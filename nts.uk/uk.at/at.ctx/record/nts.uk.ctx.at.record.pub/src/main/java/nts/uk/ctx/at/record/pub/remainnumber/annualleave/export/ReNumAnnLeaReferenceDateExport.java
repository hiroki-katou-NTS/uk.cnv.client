package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 基準日時点年休残数
 * @author hayata_maekawa
 *
 */

@Data
@AllArgsConstructor
public class ReNumAnnLeaReferenceDateExport {
	
	/**
	 * 年休情報
	 */
	private AnnualLeaveInfoExport annualLeaveRemainNumberExport;
	
	/**
	 * 年休管理情報Export
	 */
	private List<AnnualLeaveManageInforExport> annualLeaveManageInforExports;
	
	/**
	 * 年休付与残数データExport
	 */
	private List<AnnualLeaveGrantExport> annualLeaveGrantExports;
	
	/**
	 * 出勤率
	 */
	private Double attendanceRate;
	
	/**
	 * 労働日数
	 */
	private Double workingDays;
	
	/**
	 * 年休残日数
	 */
	private Double remainingDays;
	
	/**
	 * 年休残時間
	 */
	private Integer remainingTime;
	
	
	
	public ReNumAnnLeaReferenceDateExport(String sid){
		this.annualLeaveRemainNumberExport = new AnnualLeaveInfoExport(sid);
		this.annualLeaveManageInforExports = new ArrayList<>();
		this.annualLeaveGrantExports = new ArrayList<>();
		this.attendanceRate = 0.00;
		this.workingDays = 0.00;
		this.remainingDays = 0.00;
		this.remainingTime = 0;
	}
}
