package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.time.GeneralDate;

/**
 * @author ThanhNX 日の実績のロック状態
 */
@AllArgsConstructor
@Getter
public class DailyLock {

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
	private StatusLock lockDailyResult;

	/**
	 * 職場の就業確定
	 */
	private StatusLock lockWpl;

	/**
	 * 月別実績の承認
	 */
	private StatusLock lockApprovalMontｈ;

	/**
	 * 月別実績の確認
	 */
	private StatusLock lockConfirmMonth;

	/**
	 * 日別実績の承認
	 */
	private StatusLock lockApprovalDay;

	/**
	 * 日別実績の確認
	 */
	private StatusLock lockConfirmDay;

	/**
	 * 過去実績のロック
	 */
	private StatusLock lockPast;

	public boolean lockData() {
		return lockDailyResult.value == StatusLock.LOCK.value || lockWpl.value == StatusLock.LOCK.value
				|| lockApprovalMontｈ.value == StatusLock.LOCK.value || lockConfirmMonth.value == StatusLock.LOCK.value
				|| lockApprovalDay.value == StatusLock.LOCK.value || lockConfirmDay.value == StatusLock.LOCK.value
				|| lockPast.value == StatusLock.LOCK.value;
	}

}
