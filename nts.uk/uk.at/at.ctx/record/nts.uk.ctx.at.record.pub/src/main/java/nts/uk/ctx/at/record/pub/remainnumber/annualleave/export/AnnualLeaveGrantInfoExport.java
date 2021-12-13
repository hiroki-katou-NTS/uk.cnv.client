package nts.uk.ctx.at.record.pub.remainnumber.annualleave.export;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 年休付与情報Export
 * @author hayata_maekawa
 *
 */
@Data
@AllArgsConstructor
public class AnnualLeaveGrantInfoExport {

	/**
	 * 付与日数
	 */
	private Double grantDays;

	/**
	 * 付与所定日数
	 */
	private Double grantPrescribedDays;
	
	/**
	 * 付与労働日数
	 */
	private Double grantWorkingDays;
	
	/**
	 * 付与控除日数
	 */
	private Double grantDeductedDays;
	
	/**
	 * 控除日数付与前
	 */
	private Double deductedDaysBeforeGrant;
	
	/**
	 * 控除日数付与後
	 */
	private Double deductedDaysAfterGrant;
	
	/**
	 * 出勤率
	 */
	private Double attendanceRate;
	
	public AnnualLeaveGrantInfoExport(){
		this.grantDays = 0.00;
		this.grantPrescribedDays = 0.00;
		this.grantWorkingDays = 0.00;
		this.grantDeductedDays = 0.00;
		this.deductedDaysBeforeGrant = 0.00;
		this.deductedDaysAfterGrant = 0.00;
		this.attendanceRate = 0.00;
	}
}
