package nts.uk.ctx.at.shared.dom.employeeworkway;

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
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.GetTargetIdentifiInforService;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;

/**
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared.社員の働き方.社員の就業状態
 * 社員の就業状態
 * @author HieuLt
 *
 */
@Getter
@RequiredArgsConstructor
public class EmployeeWorkingStatus {

	/** 社員ID **/
	private final String employeeID;
	/** 年月日 **/
	private final GeneralDate date;

	/** 状態 **/
	private final WorkingStatus workingStatus;

	/** 休業枠NO **/
	private final Optional<TempAbsenceFrameNo> optTempAbsenceFrameNo;
	/** 雇用コード **/
	private final Optional<EmploymentCode> optEmploymentCd;
	/** 所属組織 **/
	private final Optional<TargetOrgIdenInfor> organization;


	/**
	 * 作成する
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param date 年月日
	 * @return 社員の就業状態
	 */
	public static EmployeeWorkingStatus create(Require require, String employeeID, GeneralDate date) {

		// 休業枠
		Optional<TempAbsenceFrameNo> frameNo = Optional.empty();
		// 雇用コード
		Optional<EmploymentCode> employmentCd = Optional.empty();
		// 所属組織
		Optional<TargetOrgIdenInfor> organization = Optional.empty();


		// 在籍中かを判定
		if (!EmployeeWorkingStatus.isEnrolled(require, employeeID, date)) {
			// 在籍中ではない
			// 就業状態 -> 在籍していない
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.NOT_ENROLLED, frameNo, employmentCd, organization);
		}


		// 雇用コード取得
		employmentCd = EmployeeWorkingStatus.getEmplomentCd(require, employeeID, date);
		if (!employmentCd.isPresent()) {
			// 雇用コードがない
			// 就業状態 -> データ不正
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.INVALID_DATA, frameNo, employmentCd, organization);
		}


		// 所属組織取得
		organization = EmployeeWorkingStatus.getAffiliatedOrganization( require, employeeID, date );
		if (!organization.isPresent()) {
			// 雇用コードがない
			// 就業状態 -> データ不正
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.INVALID_DATA, frameNo, employmentCd, organization);
		}


		// 予定管理区分取得
		val scheManegedAtr = EmployeeWorkingStatus.getScheduleManagementAtr(require, employeeID, date);
		if (!scheManegedAtr.isPresent()) {
			// 予定管理区分がない
			// 就業状態 -> データ不正
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.INVALID_DATA, frameNo, employmentCd, organization);
		} else if (scheManegedAtr.get() == ManageAtr.NOTUSE) {
			// 予定管理区分 == 管理しない
			// 就業状態 -> 予定管理しない
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.DO_NOT_MANAGE_SCHEDULE, frameNo, employmentCd, organization);
		}


		// 休職中かを判定
		if (EmployeeWorkingStatus.isOnLeave(require, employeeID, date)) {
			// 休職中である
			// 就業状態 -> 休職中
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.ON_LEAVE, frameNo, employmentCd, organization);
		}


		// 休業枠NOを取得
		frameNo = EmployeeWorkingStatus.getTempAbsenceFrameNo(require, employeeID, date);
		if (frameNo.isPresent()) {
			// 休業枠NOがある⇒休業中である
			// 就業状態 -> 休業中
			return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.CLOSED, frameNo, employmentCd, organization);

		}


		// 就業状態 -> 予定管理する
		return new EmployeeWorkingStatus(employeeID, date, WorkingStatus.SCHEDULE_MANAGEMENT, frameNo, employmentCd, organization);

	}


	/**
	 * 在籍中か
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param date 年月日
	 * @return 年月日時点で在籍しているかどうか
	 */
	private static boolean isEnrolled(Require require, String employeeID, GeneralDate date) {

		// 年月日時点の在籍期間が存在すれば true を返す
		return require.getAffCompanyHistByEmployee(employeeID, date).isPresent();

	}

	/**
	 * 所属組織を取得する
	 * @param require Require
	 * @param employeeId 社員ID
	 * @param date 年月日
	 * @return 年月日時点の所属組織
	 */
	private static Optional<TargetOrgIdenInfor> getAffiliatedOrganization(Require require, String employeeId, GeneralDate date) {

		// 年月日時点の所属組織を取得
		return Optional.of( GetTargetIdentifiInforService.get(require, date, employeeId) );

	}

	/**
	 * 雇用コードを取得する
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param date 年月日
	 * @return 年月日時点の雇用コード
	 */
	private static Optional<EmploymentCode> getEmplomentCd(Require require, String employeeID, GeneralDate date) {

		// 年月日時点の雇用履歴項目を取得⇒雇用コードを返す
		val empHistItem = require.getEmploymentHistory(employeeID, date);
		return empHistItem.map( t -> new EmploymentCode(t.getEmploymentCd()) );
	}

	/**
	 * 予定管理区分を取得する
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param date 年月日
	 * @return 年月日時点の予定管理区分
	 */
	private static Optional<ManageAtr> getScheduleManagementAtr(Require require, String employeeID, GeneralDate date) {

		// 年月日時点の労働条件項目を取得⇒予定管理区分を返す
		val zWorkingConditionIDtem = require.getBySidAndStandardDate(employeeID, date);
		return zWorkingConditionIDtem.map(c -> c.getScheduleManagementAtr());

	}

	/**
	 * 休職中か
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param date 年月日
	 * @return 年月日時点で休職中かどうか
	 */
	private static boolean isOnLeave(Require require, String employeeID, GeneralDate date) {

		// 年月日時点の休職期間が存在すれば true を返す
		return require.getByDatePeriod(employeeID, date).isPresent();

	}

	/**
	 * 休業枠NOを取得する
	 * @param require Require
	 * @param employeeID 社員ID
	 * @param date 年月日
	 * @return 年月日時点の休職休業枠NO
	 */
	private static Optional<TempAbsenceFrameNo> getTempAbsenceFrameNo(Require require, String employeeID, GeneralDate date) {

		// 年月日時点の休業期間を取得⇒休職休業枠NOを返す
		val empLeaveWkPeriod = require.specAndGetHolidayPeriod(employeeID, date);
		return empLeaveWkPeriod.map( t -> t.getTempAbsenceFrNo() );

	}


	public static interface Require extends GetTargetIdentifiInforService.Require {

		/**
		 * 在籍期間を取得する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の在籍情報
		 */
		Optional<EmpEnrollPeriodImport> getAffCompanyHistByEmployee(String employeeId, GeneralDate date);

		/**
		 * 労働条件履歴を取得する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の労働条件履歴
		 */
		Optional<WorkingConditionItem> getBySidAndStandardDate(String employeeId, GeneralDate date);

		/**
		 * 休職期間を取得する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の休職情報
		 */
		Optional<EmployeeLeaveJobPeriodImport> getByDatePeriod(String employeeId, GeneralDate date);

		/**
		 * 休業期間を取得する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の休業情報
		 */
		Optional<EmpLeaveWorkPeriodImport> specAndGetHolidayPeriod(String employeeId, GeneralDate date);

		/**
		 * 雇用履歴を取得する
		 * @param employeeId 社員ID
		 * @param date 年月日
		 * @return 年月日時点の雇用履歴
		 */
		Optional<EmploymentPeriodImported> getEmploymentHistory(String employeeId, GeneralDate date);

	}

}
