package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.DailyLock;

/**
 * 
 * @author sonnlb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DailyLockDto {

	/**
	 * 社員ID
	 */
	private String employeeId;

	/**
	 * 対象日
	 */
	private GeneralDate date;

	/**
	 * 日別実績のロック
	 */
	private int lockDailyResult;

	/**
	 * 職場の就業確定
	 */
	private int lockWpl;

	/**
	 * 月別実績の承認
	 */
	private int lockApprovalMontｈ;

	/**
	 * 月別実績の確認
	 */
	private int lockConfirmMonth;

	/**
	 * 日別実績の承認
	 */
	private int lockApprovalDay;

	/**
	 * 日別実績の確認
	 */
	private int lockConfirmDay;

	/**
	 * 過去実績のロック
	 */
	private int lockPast;

	public static DailyLockDto fromDomain(DailyLock domain){
		return new DailyLockDto(
				domain.getEmployeeId(), 
				domain.getDate(), 
				domain.getLockDailyResult().value, 
				domain.getLockWpl().value, 
				domain.getLockApprovalMontｈ().value, 
				domain.getLockConfirmMonth().value, 
				domain.getLockApprovalDay().value, 
				domain.getLockConfirmDay().value, 
				domain.getLockPast().value);
				
	}
}
