package nts.uk.ctx.at.record.dom.workrecord.actualsituation.confirmstatusmonthly;

import org.apache.logging.log4j.util.Strings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.workrecord.actuallock.LockStatus;

/**
 * 月の実績のロック状態
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyPerformaceLockStatus {
	public static String LOCK_MONTHLY_PAST = "pastPerformaceLock";
	public static String LOCK_EMPLOYEE_CONFIRM = "EmploymentConfirmWorkplace";
	public static String LOCK_MONTHLY_APPROVAL = "monthlyResultApprova";
	public static String LOCK_MONTHLY_RESULT = "monthlyResultLock";
	public static String LOCK_MONTHLY_RESULT_CONFIRM = "monthlyResultConfirm";
	public static String LOCK_MONTHLY_LACK = "monthlyResultLack";
	public static String LOCK_MONTHLY_ERROR = "monthlyResultError";
	/**
	 * 社員: 社員ID
	 */
	String employeeId;
	/**
	 * 過去実績のロック
	 */
	Integer pastPerformaceLock;
	/**
	 * 職場の就業確定
	 */
	Integer employmentConfirmWorkplace;
	/**
	 * 月別実績の承認
	 */
	Integer monthlyResultApprova;
	/**
	 * 月別実績のロック
	 */
	Integer monthlyResultLock;
	/**
	 * 日別実績の確認
	 */
	Integer monthlyResultConfirm;
	/**
	 * 日別実績の不足
	 */
	Integer monthlyResultLack;
	/**
	 * 日別Integerのエラー
	 */
	Integer monthlyResultError;
	
	public String getLockStatusString(){
		StringBuilder sb = new StringBuilder();
		if(pastPerformaceLock == LockStatus.LOCK.value){
			sb.append(LOCK_MONTHLY_PAST);
			sb.append("|");
		}
		if(employmentConfirmWorkplace == LockStatus.LOCK.value){
			sb.append(LOCK_EMPLOYEE_CONFIRM);
			sb.append("|");
		}
		if(monthlyResultApprova == LockStatus.LOCK.value){
			sb.append(LOCK_MONTHLY_APPROVAL);
			sb.append("|");
		}
		if(monthlyResultLock == LockStatus.LOCK.value){
			sb.append(LOCK_MONTHLY_RESULT);
			sb.append("|");
		}
		if(monthlyResultConfirm == LockStatus.LOCK.value){
			sb.append(LOCK_MONTHLY_RESULT_CONFIRM);
			sb.append("|");
		}
		if(monthlyResultLack == LockStatus.LOCK.value){
			sb.append(LOCK_MONTHLY_LACK);
			sb.append("|");
		}
		if(monthlyResultError == LockStatus.LOCK.value){
			sb.append(LOCK_MONTHLY_ERROR);
			sb.append("|");
		}
		String resultStatus = Strings.EMPTY;
		if(sb.length() > 0){
			resultStatus = sb.toString().substring(0, sb.length() - 1);
		}
		return resultStatus;
	}
	
	public boolean disableState(String sid) {
		// 「過去実績のロック」「月別実績のロック」「職場の就業確定」のいずれかがロックされている場合
		if (sid.equals(employeeId)) {
			return pastPerformaceLock == LockStatus.LOCK.value || monthlyResultLock == LockStatus.LOCK.value
					|| employmentConfirmWorkplace == LockStatus.LOCK.value;
		}
		return false;
	}
}
