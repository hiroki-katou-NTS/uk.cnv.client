package nts.uk.ctx.at.schedule.dom.schedule.workschedule;

import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmpLeaveWorkPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.EmployeeLeaveJobPeriodImport;
import nts.uk.ctx.at.shared.dom.adapter.employment.employwork.leaveinfo.TempAbsenceFrameNo;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workingcondition.ManageAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkingConditionItem;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmpEnrollPeriodImport;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.employeeinfor.employmenthistory.imported.EmploymentPeriodImported;

/**
 * 社員の予定管理状態 TP
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.勤務予定.勤務予定.勤務予定
 *
 * @author HieuLt
 *
 */
@Getter
@RequiredArgsConstructor
public class ScheManaStatuTempo {

	/** 社員ID **/
	private final String employeeID;

	/** 年月日 **/
	private final GeneralDate date;

	/** 予定管理状態 **/
	private final ScheManaStatus scheManaStatus;

	/** 休業枠NO **/
	private final Optional<TempAbsenceFrameNo> optTempAbsenceFrameNo;
	/** 雇用コード **/
	private final Optional<EmploymentCode> optEmploymentCd;

	/**
	 * [C-1] 作成する
	 *
	 * @param require
	 * @param employeeID
	 * @param date
	 * @return
	 */
	public static ScheManaStatuTempo create(Require require, String employeeID, GeneralDate date) {

		// 休業枠
		Optional<TempAbsenceFrameNo> frameNo = Optional.empty();
		// 雇用コード
		Optional<EmploymentCode> employmentCd = Optional.empty();


		// 在籍中かを判定
		if (!enrolled(require, employeeID, date)) {
			// 在籍中ではない
			// 予定管理状態 -> 在籍していない
			return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.NOT_ENROLLED, frameNo, employmentCd);
		}


		// 雇用コード取得
		employmentCd = ScheManaStatuTempo.getEmplomentCd(require, employeeID, date);
		if (!employmentCd.isPresent()) {
			// 雇用コードがない
			// 予定管理状態 -> データ不正
			return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.INVALID_DATA, frameNo, employmentCd);
		}


		// 予定管理区分取得
		val scheManegedAtr = getManageAtr(require, employeeID, date);
		if (!scheManegedAtr.isPresent()) {
			// 予定管理区分がない
			// 予定管理状態 -> データ不正
			return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.INVALID_DATA, frameNo, employmentCd);
		} else if (scheManegedAtr.get() == ManageAtr.NOTUSE) {
			// 予定管理区分 == 管理しない
			// 予定管理状態 -> 予定管理しない
			return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.DO_NOT_MANAGE_SCHEDULE, frameNo, employmentCd);
		}


		// 休職中かを判定
		if (onLeave(require, employeeID, date)) {
			// 休職中である
			// 予定管理状態 -> 休職中
			return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.ON_LEAVE, frameNo, employmentCd);
		}


		// 休業枠NOを取得
		frameNo = getTempAbsenceFrameNo(require, employeeID, date);
		if (frameNo.isPresent()) {
			// 休業枠NOがある⇒休業中である
			// 予定管理状態 -> 休業中
			return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.CLOSED, frameNo, employmentCd);

		}


		// 予定管理状態 -> 予定管理する
		return new ScheManaStatuTempo(employeeID, date, ScheManaStatus.SCHEDULE_MANAGEMENT, frameNo, employmentCd);

	}

	/**
	 * [S-1] 在籍中か
	 *
	 * @param require
	 * @param employeeID
	 * @param date
	 * @return
	 */
	private static boolean enrolled(Require require, String employeeID, GeneralDate date) {

		//return require.在籍期間を取得する( 社員ID, 年月日 ).isPresent()
		return require.getAffCompanyHistByEmployee(employeeID, date).isPresent();

	}

	/**
	 * [S-2] 雇用コードを取得する
	 *
	 * @param require
	 * @param employeeID
	 * @param date
	 * @return
	 */
	private static Optional<EmploymentCode> getEmplomentCd(Require require, String employeeID, GeneralDate date) {

		// $雇用履歴項目 = require.雇用履歴を取得する( 社員ID, 年月日 )
		val empHistItem = require.getEmploymentHistory(employeeID, date);
		// return $雇用履歴項目: map $.履歴項目.雇用コード
		// 			none Optional.empty
		return empHistItem.map( t -> new EmploymentCode(t.getEmploymentCd()) );
	}

	/**
	 * [S-3] 予定管理区分を取得する
	 *
	 * @param require
	 * @param employeeID
	 * @param date
	 * @return
	 */
	private static Optional<ManageAtr> getManageAtr(Require require, String employeeID, GeneralDate date) {

		// $労働条件項目 = require.労働条件履歴を取得する( 社員ID, 年月日 )
		val zWorkingConditionIDtem = require.getBySidAndStandardDate(employeeID, date);
		// return $労働条件項目: map $.予定管理区分
		// 			none Optional.empty
		return zWorkingConditionIDtem.map(c -> c.getScheduleManagementAtr());

	}

	/**
	 * [S-4] 休職中か
	 *
	 * @param require
	 * @param employeeID
	 * @param date
	 * @return
	 */
	private static boolean onLeave(Require require, String employeeID, GeneralDate date) {

		// return require.休職期間を取得する( 社員ID, 年月日 ).isPresent()
		return require.getByDatePeriod(employeeID, date).isPresent();

	}

	/**
	 * [S-5] 休業枠NOを取得する
	 *
	 * @param require
	 * @param employeeID
	 * @param date
	 * @return Optional<休職休業枠NO>
	 */
	private static Optional<TempAbsenceFrameNo> getTempAbsenceFrameNo(Require require, String employeeID, GeneralDate date) {

		// $休業期間 = require.休業期間を取得する( 社員ID, 年月日 )
		val empLeaveWkPeriod = require.specAndGetHolidayPeriod(employeeID, date);
		// return $休業期間: map $.枠NO
		// 			none Optional.empty
		return empLeaveWkPeriod.map( t -> t.getTempAbsenceFrNo() );

	}


	public static interface Require  {

		/**
		 * 在籍期間を取得する
		 * [R-1] 在籍期間を取得する( 社員ID, 年月日 ) : Optional
		 *
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の在籍情報
		 */
		Optional<EmpEnrollPeriodImport> getAffCompanyHistByEmployee(String employeeId, GeneralDate date);

		/**
		 * 労働条件履歴を取得する
		 * [R-2] 労働条件履歴を取得する
		 *
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の労働条件履歴
		 */
		Optional<WorkingConditionItem> getBySidAndStandardDate(String employeeId, GeneralDate date);

		/**
		 * 休職期間を取得する
		 * [R-3] 休職期間を取得する( 社員ID, 年月日 ) : Optional
		 *
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の休職情報
		 */
		Optional<EmployeeLeaveJobPeriodImport> getByDatePeriod(String employeeId, GeneralDate date);

		/**
		 * 休業期間を取得する
		 * [R-4] 休業期間を取得する( 社員ID, 年月日 ) : Optional
		 *
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の休業情報
		 */
		Optional<EmpLeaveWorkPeriodImport> specAndGetHolidayPeriod(String employeeId, GeneralDate date);

		/**
		 * 雇用履歴を取得する
		 * [R-5] 雇用履歴を取得する( 社員ID, 年月日 ) : Optional
		 *
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の雇用履歴
		 */
		Optional<EmploymentPeriodImported> getEmploymentHistory(String employeeId, GeneralDate date);

	}

}
